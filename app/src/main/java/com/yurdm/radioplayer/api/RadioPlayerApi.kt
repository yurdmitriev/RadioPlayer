package com.yurdm.radioplayer.api

import com.yurdm.radioplayer.model.Radio
import retrofit2.http.GET

interface RadioPlayerApi {
    @GET("api/radio")
    suspend fun listRadios(): List<Radio>
}