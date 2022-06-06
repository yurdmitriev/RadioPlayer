package com.yurdm.radioplayer.api

import com.yurdm.radioplayer.util.Constants.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: RadioPlayerApi by lazy {
        retrofit.create(RadioPlayerApi::class.java)
    }
}