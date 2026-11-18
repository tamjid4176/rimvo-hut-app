package com.example.data.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class JwtAuthInterceptor(
    private val tokenProvider: () -> String?
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val original = chain.request()
        val token = tokenProvider()
        val request = if (!token.isNullOrBlank()) {
            original.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Accept", "application/json")
                .build()
        } else {
            original.newBuilder()
                .addHeader("Accept", "application/json")
                .build()
        }
        return chain.proceed(request)
    }
}

object NetworkModule {
    const val WOOCOMMERCE_BASE_URL = "https://rimvohut.com/"
    const val RIMVO_FINANCE_BASE_URL = "https://finance-api.rimvohut.com/"

    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    fun provideOkHttpClient(tokenProvider: () -> String?): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        return OkHttpClient.Builder()
            .addInterceptor(JwtAuthInterceptor(tokenProvider))
            .addInterceptor(logging)
            .connectTimeout(12, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    fun provideWooCommerceApi(client: OkHttpClient): WooCommerceApiService =
        Retrofit.Builder()
            .baseUrl(WOOCOMMERCE_BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(WooCommerceApiService::class.java)

    fun provideRimvoFinanceApi(client: OkHttpClient): RimvoFinanceApiService =
        Retrofit.Builder()
            .baseUrl(RIMVO_FINANCE_BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(RimvoFinanceApiService::class.java)
}
