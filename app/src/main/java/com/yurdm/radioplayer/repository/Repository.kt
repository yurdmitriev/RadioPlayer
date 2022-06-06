package com.yurdm.radioplayer.repository

import com.yurdm.radioplayer.api.RetrofitInstance
import com.yurdm.radioplayer.model.Radio

class Repository {
    suspend fun listRadios(): List<Radio> {
        return RetrofitInstance.api.listRadios()
    }
}