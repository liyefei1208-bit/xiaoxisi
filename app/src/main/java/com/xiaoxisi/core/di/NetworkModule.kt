package com.xiaoxisi.core.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.xiaoxisi.data.remote.api.AsrApi
import com.xiaoxisi.data.remote.api.LlmApi
import com.xiaoxisi.core.config.ApiConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val ASR_BASE_URL = "https://iat-api.xfyun.cn/"

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().create()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Provides
    @Singleton
    @Named("asr")
    fun provideAsrRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ASR_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("llm")
    fun provideLlmRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiConfig.llmBaseUrl.ifBlank { "https://dashscope.aliyuncs.com/compatible-mode/v1/" }
                .let { if (it.endsWith("/")) it else "$it/" })
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAsrApi(@Named("asr") retrofit: Retrofit): AsrApi {
        return retrofit.create(AsrApi::class.java)
    }

    @Provides
    @Singleton
    fun provideLlmApi(@Named("llm") retrofit: Retrofit): LlmApi {
        return retrofit.create(LlmApi::class.java)
    }
}
