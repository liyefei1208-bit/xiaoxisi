package com.xiaoxisi.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AsrRequest(
    val common: AsrCommon,
    val business: AsrBusiness,
    val data: AsrData
)

data class AsrCommon(
    @SerializedName("app_id") val appId: String
)

data class AsrBusiness(
    val language: String = "zh-cn",
    val domain: String = "iat",
    val accent: String = "mandarin",
    val ptt: Int = 0
)

data class AsrData(
    val status: Int,
    val format: String = "audio/L16;rate=16000",
    val encoding: String = "raw",
    val audio: String
)

data class AsrResponse(
    val code: Int,
    val message: String,
    @SerializedName("sid") val sessionId: String? = null,
    val data: AsrResultData? = null
)

data class AsrResultData(
    val result: AsrTextResult? = null,
    val status: Int = 0
)

data class AsrTextResult(
    val wst: List<AsrWordSegment>? = null,
    val ws: List<AsrWord>? = null
)

data class AsrWordSegment(
    val bg: Int,
    val ed: Int,
    val cw: List<AsrCharWord>? = null
)

data class AsrWord(
    val bg: Int,
    val cw: List<AsrCharWord>? = null
)

data class AsrCharWord(
    val w: String? = null,
    val sc: Int? = null
)
