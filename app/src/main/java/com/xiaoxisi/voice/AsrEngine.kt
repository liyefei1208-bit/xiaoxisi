package com.xiaoxisi.voice

import android.util.Base64
import com.xiaoxisi.core.config.ApiConfig
import com.xiaoxisi.data.remote.api.AsrApi
import com.xiaoxisi.data.remote.dto.AsrResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.net.URL
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton
import java.nio.charset.StandardCharsets

@Singleton
class AsrEngine @Inject constructor(
    private val asrApi: AsrApi
) {
    companion object {
        private const val HOST_URL = "https://iat-api.xfyun.cn/v2/iat"
        private const val HOST = "iat-api.xfyun.cn"
        private const val PATH = "/v2/iat"
        private const val METHOD = "POST"
    }

    suspend fun recognize(audioData: ByteArray, sampleRate: Int = 16000): Result<String> {
        val apiKey = ApiConfig.asrApiKey
        val apiSecret = ApiConfig.asrApiSecret
        val appId = ApiConfig.asrAppId

        if (apiKey.isBlank() || apiSecret.isBlank()) {
            return Result.failure(IllegalStateException("讯飞ASR API Key 未配置"))
        }

        return try {
            val date = java.text.SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", java.util.Locale.US)
                .apply { timeZone = java.util.TimeZone.getTimeZone("GMT") }
                .format(java.util.Date())

            val signatureOrigin = "host: $HOST\ndate: $date\n$METHOD $PATH HTTP/1.1"
            val signature = hmacSha256(signatureOrigin, apiSecret)
            val authorizationOrigin = "api_key=\"$apiKey\", algorithm=\"hmac-sha256\", headers=\"host date request-line\", signature=\"$signature\""
            val authorization = Base64.encodeToString(authorizationOrigin.toByteArray(StandardCharsets.UTF_8), Base64.NO_WRAP)

            val audioBase64 = Base64.encodeToString(audioData, Base64.NO_WRAP)

            val params = JSONObject().apply {
                put("common", JSONObject().apply {
                    put("app_id", appId)
                })
                put("business", JSONObject().apply {
                    put("language", "zh-cn")
                    put("domain", "iat")
                    put("accent", "mandarin")
                    put("ptt", 0)
                    put("vinfo", 1)
                })
                put("data", JSONObject().apply {
                    put("status", 2)
                    put("format", "audio/L16;rate=$sampleRate")
                    put("encoding", "raw")
                    put("audio", audioBase64)
                })
            }

            val requestBody = params.toString().toRequestBody("application/json".toMediaType())

            val response = asrApi.recognize(requestBody)
            if (response.isSuccessful) {
                val body = response.body()?.string() ?: ""
                parseAsrResult(body)
            } else {
                Result.failure(Exception("ASR请求失败: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseAsrResult(responseJson: String): Result<String> {
        return try {
            val json = JSONObject(responseJson)
            val code = json.optInt("code", -1)
            if (code != 0) {
                val message = json.optString("message", "未知错误")
                return Result.failure(Exception("ASR识别失败: $message"))
            }

            val data = json.optJSONObject("data")
            val result = data?.optJSONObject("result")
            val text = result?.optString("text", "") ?: ""

            if (text.isBlank()) {
                Result.failure(Exception("未识别到语音内容"))
            } else {
                Result.success(text.trimEnd { it == '.' })
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun hmacSha256(data: String, key: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        val secretKeySpec = SecretKeySpec(key.toByteArray(StandardCharsets.UTF_8), "HmacSHA256")
        mac.init(secretKeySpec)
        val bytes = mac.doFinal(data.toByteArray(StandardCharsets.UTF_8))
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }
}
