package com.yurdm.radioplayer.repository

import com.yurdm.radioplayer.api.RetrofitInstance
import com.yurdm.radioplayer.model.Category
import com.yurdm.radioplayer.model.Radio
import retrofit2.Response

class Repository {
    suspend fun listRadios(): Response<List<Radio>> {
        return RetrofitInstance.api.listRadios()
    }

    suspend fun listCategories(): Response<List<Category>> {
        return RetrofitInstance.api.listCategories()
    }
}