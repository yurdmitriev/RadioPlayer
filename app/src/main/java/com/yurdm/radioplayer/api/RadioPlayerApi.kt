package com.yurdm.radioplayer.api

import com.yurdm.radioplayer.model.Category
import com.yurdm.radioplayer.model.Radio
import retrofit2.Response
import retrofit2.http.GET

interface RadioPlayerApi {
    @GET("api/radio")
    suspend fun listRadios(): Response<List<Radio>>

    @GET("api/category")
    suspend fun listCategories(): Response<List<Category>>
}