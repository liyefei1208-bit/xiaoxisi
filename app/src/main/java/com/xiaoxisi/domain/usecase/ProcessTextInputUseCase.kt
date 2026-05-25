package com.xiaoxisi.domain.usecase

import android.util.Log
import com.xiaoxisi.core.config.ApiConfig
import com.xiaoxisi.domain.model.ConversationContext
import com.xiaoxisi.domain.model.Intent
import com.xiaoxisi.domain.model.Message
import com.xiaoxisi.domain.model.MessageRole
import com.xiaoxisi.nlu.LlmIntentClassifier
import com.xiaoxisi.nlu.LocalIntentMatcher
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProcessTextInputUseCase @Inject constructor(
    private val classifier: LlmIntentClassifier,
    private val localMatcher: LocalIntentMatcher
) {
    suspend operator fun invoke(
        text: String,
        context: ConversationContext
    ): LlmIntentClassifier.LcResult {
        val localResult = localMatch(text)
        val hasLocalAction = localResult.intent !is Intent.Chat && localResult.intent !is Intent.Unknown

        if (!ApiConfig.isLlmConfigured()) {
            Log.d("Xiaoxisi", "ProcessTextInput: using localMatcher (LLM not configured)")
            return localResult
        }

        return try {
            val llmResult = classifier.classify(text, buildContextString(context))
            if (llmResult.intent is Intent.Unknown || (llmResult.intent is Intent.Chat && hasLocalAction)) {
                Log.d("Xiaoxisi", "ProcessTextInput: LLM returned ${llmResult.intent::class.simpleName}, local has action=$hasLocalAction, using local")
                localResult
            } else {
                llmResult
            }
        } catch (e: Exception) {
            Log.e("Xiaoxisi", "ProcessTextInput: LLM failed, using local", e)
            localResult
        }
    }

    private fun localMatch(text: String): LlmIntentClassifier.LcResult {
        val result = localMatcher.match(text)
        return LlmIntentClassifier.LcResult(
            recognizedText = text,
            intent = result.intent,
            replyText = result.replyText,
            entities = result.entities
        )
    }

    private fun buildContextString(context: ConversationContext): String {
        if (context.messages.isEmpty()) return ""
        return context.messages.takeLast(4).joinToString("\n") { msg ->
            "${if (msg.role == MessageRole.USER) "用户" else "助手"}: ${msg.content}"
        }
    }
}
