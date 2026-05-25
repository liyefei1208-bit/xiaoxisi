package com.xiaoxisi.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LlmRequest(
    val model: String,
    val messages: List<LlmMessage>,
    val temperature: Double = 0.3,
    @SerializedName("max_tokens") val maxTokens: Int = 500
)

data class LlmMessage(
    val role: String,
    val content: String
)

data class LlmResponse(
    val id: String? = null,
    val choices: List<LlmChoice>? = null,
    val error: LlmError? = null
)

data class LlmChoice(
    val index: Int,
    val message: LlmMessage? = null,
    @SerializedName("finish_reason") val finishReason: String? = null
)

data class LlmError(
    val message: String? = null,
    val type: String? = null,
    val code: String? = null
)

data class ClassificationResult(
    val intent: String,
    @SerializedName("sub_intent") val subIntent: String? = null,
    val entities: Map<String, String>? = null,
    val reply: String,
    val confidence: Double
)
