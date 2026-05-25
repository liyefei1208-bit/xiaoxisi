package com.xiaoxisi.nlu

import android.util.Log
import com.xiaoxisi.core.config.ApiConfig
import com.xiaoxisi.core.config.PromptTemplates
import com.xiaoxisi.data.remote.api.LlmApi
import com.xiaoxisi.data.remote.dto.LlmMessage
import com.xiaoxisi.data.remote.dto.LlmRequest
import com.xiaoxisi.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LlmIntentClassifier @Inject constructor(
    private val llmApi: LlmApi,
    private val entityExtractor: EntityExtractor
) {
    suspend fun classify(userText: String, context: String = ""): LcResult {
        Log.d("Xiaoxisi", "LLM classify: text=$userText provider=${ApiConfig.llmProvider} baseUrl=${ApiConfig.llmBaseUrl}")
        val userPrompt = PromptTemplates.buildUserPrompt(userText, context)

        val request = LlmRequest(
            model = when (ApiConfig.llmProvider) {
                "deepseek" -> "deepseek-v4-pro"
                else -> "qwen-turbo"
            },
            messages = listOf(
                LlmMessage("system", PromptTemplates.systemPrompt),
                LlmMessage("user", userPrompt)
            ),
            temperature = 0.7,
            maxTokens = 1024
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

    private fun parseResponse(content: String, fallbackText: String): LcResult {
        Log.d("Xiaoxisi", "LLM raw response: $content")

        val actionRegex = Regex("""\[ACTION:(\w+):([^\]]*)]""")
        val actionMatch = actionRegex.find(content)

        if (actionMatch != null) {
            val actionType = actionMatch.groupValues[1]
            val actionParam = actionMatch.groupValues[2].trim()

            val replyText = content.substringBeforeLast("[ACTION:")
                .trimEnd('。', '！', '，', ' ', '\n')

            Log.d("Xiaoxisi", "LLM action detected: type=$actionType, param=$actionParam, reply=$replyText")

            val intent = when (actionType) {
                "open_app" -> {
                    val appName = actionParam.ifBlank { null }
                        ?: entityExtractor.extractAppName(fallbackText)
                    Intent.App(AppAction.OPEN, appName, 0.9f)
                }
                "wifi" -> Intent.SystemSetting(SettingType.WIFI, "open", 0.9f)
                "brightness" -> {
                    val action = if (actionParam.contains("down")) "down" else "up"
                    Intent.SystemSetting(SettingType.BRIGHTNESS, action, 0.9f)
                }
                "volume" -> {
                    val action = if (actionParam.contains("down")) "down" else "up"
                    Intent.SystemSetting(SettingType.VOLUME, action, 0.9f)
                }
                "font_size" -> Intent.SystemSetting(SettingType.FONT_SIZE, "open", 0.9f)
                "dial" -> {
                    val contact = actionParam.ifBlank { null }
                    Intent.Phone(PhoneAction.DIAL, contact, 0.9f)
                }
                "hangup" -> Intent.Phone(PhoneAction.HANGUP, null, 0.9f)
                else -> Intent.Chat(0.9f)
            }

            return LcResult(
                recognizedText = fallbackText,
                intent = intent,
                replyText = replyText.ifBlank { "好嘞" }
            )
        }

        return LcResult(
            recognizedText = fallbackText,
            intent = Intent.Chat(0.8f),
            replyText = content.trim()
        )
    }

    data class LcResult(
        val recognizedText: String = "",
        val intent: Intent = Intent.Unknown(0f),
        val replyText: String = "",
        val entities: Map<String, String> = emptyMap()
    )
}
