package com.yurdm.radioplayer.api

import com.yurdm.radioplayer.util.Constants.Companion.BASE_URL
import com.yurdm.radioplayer.util.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    var token: String? = null

    private val client = OkHttpClient.Builder().apply {
        if (token != null) addInterceptor(Interceptor(token))
    }.build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: RadioPlayerApi by lazy {
        retrofit.create(RadioPlayerApi::class.java)
    }
}