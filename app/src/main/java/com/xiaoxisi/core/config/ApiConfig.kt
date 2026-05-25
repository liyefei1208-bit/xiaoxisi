package com.xiaoxisi.core.config

import com.xiaoxisi.BuildConfig

object ApiConfig {
    val asrApiKey: String get() = BuildConfig.ASR_API_KEY
    val asrApiSecret: String get() = BuildConfig.ASR_API_SECRET
    val asrAppId: String get() = BuildConfig.ASR_APP_ID

    val llmProvider: String get() = BuildConfig.LLM_PROVIDER
    val llmApiKey: String get() = BuildConfig.LLM_API_KEY
    val llmBaseUrl: String get() = BuildConfig.LLM_BASE_URL

    fun isLlmConfigured(): Boolean = llmApiKey.isNotBlank()
    fun isAsrConfigured(): Boolean = asrApiKey.isNotBlank() && asrApiSecret.isNotBlank()
    fun isFullyConfigured(): Boolean = isLlmConfigured() && isAsrConfigured()
}
