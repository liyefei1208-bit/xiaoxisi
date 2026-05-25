package com.xiaoxisi.data.remote.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Streaming
import retrofit2.http.Url

interface AsrApi {
    @POST("v2/iat")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    suspend fun recognize(
        @Body body: okhttp3.RequestBody
    ): Response<ResponseBody>

    @GET
    @Streaming
    suspend fun downloadAudio(@Url url: String): Response<ResponseBody>
}
