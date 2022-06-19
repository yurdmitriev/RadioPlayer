package com.yurdm.radioplayer.api

import com.yurdm.radioplayer.model.Category
import com.yurdm.radioplayer.model.Profile
import com.yurdm.radioplayer.model.Radio
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RadioPlayerApi {
    @GET("api/radio")
    suspend fun listRadios(): Response<List<Radio>>

    @GET("api/category")
    suspend fun listCategories(): Response<List<Category>>

    @GET("api/login/google/callback")
    suspend fun receiveToken(
        @Query("code") code: String
    ): Response<Profile>

    @GET("api/user")
    suspend fun getUserInfo(): Response<Profile>

    @GET("api/user/favourites")
    suspend fun getFavourites(): Response<List<Radio>>

    @GET("api/radio/search")
    suspend fun search(@Query("q") query: String): Response<List<Radio>>
}