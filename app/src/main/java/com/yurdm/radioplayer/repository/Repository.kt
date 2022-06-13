package com.yurdm.radioplayer.repository

import com.yurdm.radioplayer.api.RetrofitInstance
import com.yurdm.radioplayer.model.Category
import com.yurdm.radioplayer.model.Profile
import com.yurdm.radioplayer.model.Radio
import retrofit2.Response
import retrofit2.Retrofit

class Repository {
    suspend fun listRadios(): Response<List<Radio>> {
        return RetrofitInstance.api.listRadios()
    }

    suspend fun listCategories(): Response<List<Category>> {
        return RetrofitInstance.api.listCategories()
    }

    suspend fun receiveToken(
        token: String
    ): Response<Profile> {
        return RetrofitInstance.api.receiveToken(token)
    }

    suspend fun getUserInfo(token: String): Response<Profile> {
        RetrofitInstance.token = token
        return RetrofitInstance.api.getUserInfo()
    }
}