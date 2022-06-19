package com.yurdm.radioplayer.api

import android.content.Context
import com.yurdm.radioplayer.util.Constants.Companion.BASE_URL
import com.yurdm.radioplayer.util.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    var token: String? = null

    private fun builder(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(OkHttpClient.Builder().apply {
                if (token != null) addInterceptor(Interceptor(token))
                println(this.interceptors())
            }.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: RadioPlayerApi by lazy {
        builder().create(RadioPlayerApi::class.java)
    }
}