package com.xiaoxisi.data.remote.api

import com.xiaoxisi.data.remote.dto.LlmRequest
import com.xiaoxisi.data.remote.dto.LlmResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface LlmApi {
    @POST("chat/completions")
    suspend fun chatCompletion(
        @Header("Authorization") authorization: String,
        @Body request: LlmRequest
    ): Response<LlmResponse>
}
