package com.yurdm.radioplayer.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yurdm.radioplayer.MainActivity
import com.yurdm.radioplayer.model.Profile
import com.yurdm.radioplayer.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response

class ProfileViewModel(val repository: Repository) : ViewModel() {
    var res: MutableLiveData<Response<Profile>> = MutableLiveData()

    fun receiveToken(token: String) {
        viewModelScope.launch {
            val response = repository.receiveToken(token)
            res.value = response
        }
    }

    fun getUserInfo(token: String) {
        viewModelScope.launch {
            val response = repository.getUserInfo(token)
            res.value = response
//            val token = MainActivity.token
//            if (!token.isNullOrEmpty()) {
//            }
        }
    }
}