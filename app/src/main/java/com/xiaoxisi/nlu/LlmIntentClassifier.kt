package com.xiaoxisi.nlu

import android.util.Log
import com.google.gson.Gson
import com.xiaoxisi.core.config.ApiConfig
import com.xiaoxisi.core.config.PromptTemplates
import com.xiaoxisi.data.remote.api.LlmApi
import com.xiaoxisi.data.remote.dto.ClassificationResult as LlmClassificationResult
import com.xiaoxisi.data.remote.dto.LlmMessage
import com.xiaoxisi.data.remote.dto.LlmRequest
import com.xiaoxisi.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LlmIntentClassifier @Inject constructor(
    private val llmApi: LlmApi,
    private val gson: Gson
) {
    suspend fun classify(userText: String, context: String = ""): LcResult {
        Log.d("Xiaoxisi", "LLM classify: text=$userText provider=${ApiConfig.llmProvider} baseUrl=${ApiConfig.llmBaseUrl}")
        val userPrompt = PromptTemplates.buildUserPrompt(userText, context)

        val request = LlmRequest(
            model = if (ApiConfig.llmProvider == "deepseek") "deepseek-chat" else "qwen-turbo",
            messages = listOf(
                LlmMessage("system", PromptTemplates.systemPrompt),
                LlmMessage("user", userPrompt)
            ),
            temperature = 0.3,
            maxTokens = 500
        )

        return try {
            val response = llmApi.chatCompletion(
                authorization = "Bearer ${ApiConfig.llmApiKey}",
                request = request
            )
            Log.d("Xiaoxisi", "LLM response: code=${response.code()} success=${response.isSuccessful}")

            if (!response.isSuccessful) {
                val errBody = response.errorBody()?.string() ?: ""
                Log.e("Xiaoxisi", "LLM error: code=${response.code()} body=$errBody")
                return LcResult(
                    replyText = if (context.isBlank()) "你好，我是小希司，有什么可以帮你的？"
                        else "抱歉，我没太听清，能再说一遍吗？"
                )
            }

            val body = response.body()
            val content = body?.choices?.firstOrNull()?.message?.content ?: ""
            Log.d("Xiaoxisi", "LLM content: $content")

            parseResponse(content, userText)
        } catch (e: Exception) {
            Log.e("Xiaoxisi", "LLM exception: ${e.message}", e)
            LcResult(
                replyText = if (context.isBlank()) "你好，我是小希司，有什么可以帮你的？"
                    else "抱歉，我没太听清，能再说一遍吗？"
            )
        }
    }

    private fun parseResponse(jsonContent: String, fallbackText: String): LcResult {
        return try {
            val cleanJson = extractJson(jsonContent)
            val parsed = gson.fromJson(cleanJson, LlmClassificationResult::class.java)
            val conf = parsed.confidence.toFloat()
            val entities = parsed.entities ?: emptyMap()

            val userIntent = when (parsed.intent) {
                "system_setting" -> {
                    val setting = when (parsed.subIntent) {
                        "wifi" -> SettingType.WIFI
                        "brightness" -> SettingType.BRIGHTNESS
                        "volume" -> SettingType.VOLUME
                        "font_size" -> SettingType.FONT_SIZE
                        else -> SettingType.WIFI
                    }
                    Intent.SystemSetting(setting, entities["action"] ?: "open", conf)
                }
                "phone" -> {
                    val action = when (parsed.subIntent) {
                        "dial" -> PhoneAction.DIAL
                        "hangup" -> PhoneAction.HANGUP
                        "answer" -> PhoneAction.ANSWER
                        else -> PhoneAction.DIAL
                    }
                    Intent.Phone(action, entities["contact"], conf)
                }
                "app" -> {
                    val action = when (parsed.subIntent) {
                        "open" -> AppAction.OPEN
                        "search" -> AppAction.SEARCH
                        "install" -> AppAction.INSTALL
                        else -> AppAction.OPEN
                    }
                    Intent.App(action, entities["app"] ?: parsed.subIntent, conf)
                }
                "chat" -> Intent.Chat(conf)
                else -> Intent.Unknown(conf)
            }

            LcResult(
                recognizedText = fallbackText,
                intent = userIntent,
                replyText = parsed.reply,
                entities = entities
            )
        } catch (e: Exception) {
            Log.e("Xiaoxisi", "Parse error: ${e.message}", e)
            LcResult(
                replyText = "好嘞，我来帮你看看",
                intent = Intent.Unknown(0.5f)
            )
        }
    }

    private fun extractJson(text: String): String {
        val start = text.indexOf('{')
        val end = text.lastIndexOf('}')
        return if (start >= 0 && end > start) {
            text.substring(start, end + 1)
        } else text
    }

    data class LcResult(
        val recognizedText: String = "",
        val intent: Intent = Intent.Unknown(0f),
        val replyText: String = "",
        val entities: Map<String, String> = emptyMap()
    )
}
