package com.xiaoxisi.domain.usecase

import com.xiaoxisi.core.config.ApiConfig
import com.xiaoxisi.domain.model.ConversationContext
import com.xiaoxisi.domain.model.Intent
import com.xiaoxisi.domain.model.IntentResult
import com.xiaoxisi.domain.model.Message
import com.xiaoxisi.domain.model.MessageRole
import com.xiaoxisi.nlu.LlmIntentClassifier
import com.xiaoxisi.nlu.LocalIntentMatcher
import com.xiaoxisi.nlu.EntityExtractor
import com.xiaoxisi.voice.AsrEngine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProcessVoiceInputUseCase @Inject constructor(
    private val asrEngine: AsrEngine,
    private val classifier: LlmIntentClassifier,
    private val localMatcher: LocalIntentMatcher,
    private val entityExtractor: EntityExtractor
) {
    suspend operator fun invoke(
        audioData: ByteArray,
        context: ConversationContext
    ): Result<VoiceProcessResult> {
        if (!ApiConfig.isAsrConfigured()) {
            return Result.failure(
                IllegalStateException("语音识别API未配置，请填写local.properties中的Key，或使用键盘输入。")
            )
        }

        val recognizedText = asrEngine.recognize(audioData)
            .getOrElse { return Result.failure(it) }

        val classification = try {
            val llmResult = classifier.classify(
                recognizedText,
                buildContextString(context)
            )
            if (llmResult.intent is Intent.Unknown) {
                localMatch(recognizedText)
            } else {
                llmResult
            }
        } catch (e: Exception) {
            localMatch(recognizedText)
        }

        return Result.success(
            VoiceProcessResult(
                recognizedText = recognizedText,
                intentResult = IntentResult(
                    intent = classification.intent,
                    replyText = classification.replyText,
                    entities = classification.entities
                )
            )
        )
    }

    private fun buildContextString(context: ConversationContext): String {
        if (context.messages.isEmpty()) return ""
        return context.messages.takeLast(4).joinToString("\n") { msg ->
            "${if (msg.role == MessageRole.USER) "用户" else "助手"}: ${msg.content}"
        }
    }

    data class VoiceProcessResult(
        val recognizedText: String,
        val intentResult: IntentResult
    )

    private fun localMatch(text: String): LlmIntentClassifier.LcResult {
        val result = localMatcher.match(text)
        return LlmIntentClassifier.LcResult(
            recognizedText = text,
            intent = result.intent,
            replyText = result.replyText,
            entities = result.entities
        )
    }
}
