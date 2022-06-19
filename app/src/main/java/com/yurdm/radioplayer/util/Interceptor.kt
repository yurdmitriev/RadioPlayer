package com.yurdm.radioplayer.util

import okhttp3.Interceptor
import okhttp3.Response

class Interceptor(private val bearer: String?): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("Accept", "application/json")
            .addHeader("Authorization", "Bearer $bearer")
            .addHeader("Referrer", Constants.BASE_URL)
            .build()

        return chain.proceed(request)
    }
}