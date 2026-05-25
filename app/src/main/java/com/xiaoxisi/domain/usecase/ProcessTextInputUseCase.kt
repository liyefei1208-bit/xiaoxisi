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
        val llmConfigured = ApiConfig.isLlmConfigured()
        Log.d("Xiaoxisi", "ProcessTextInput: llmConfigured=$llmConfigured, keyLen=${ApiConfig.llmApiKey.length}")
        if (!llmConfigured) {
            Log.d("Xiaoxisi", "ProcessTextInput: using localMatcher")
            return localMatch(text)
        }
        Log.d("Xiaoxisi", "ProcessTextInput: calling LLM classifier")
        return classifier.classify(text, buildContextString(context))
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
