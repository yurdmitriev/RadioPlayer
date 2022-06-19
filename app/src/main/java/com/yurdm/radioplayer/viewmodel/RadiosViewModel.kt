package com.yurdm.radioplayer.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yurdm.radioplayer.MainActivity.Companion.token
import com.yurdm.radioplayer.model.Category
import com.yurdm.radioplayer.model.Radio
import com.yurdm.radioplayer.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response

class RadiosViewModel(private val repository: Repository) : ViewModel() {
    val radioList: MutableLiveData<Response<List<Radio>>> = MutableLiveData()

    fun listRadios() {
        viewModelScope.launch {
            val response = repository.listRadios()
            radioList.value = response
        }
    }

    fun listFavourites(token: String) {
        viewModelScope.launch {
            val response = repository.getFavourites(token)
            radioList.value = response
        }
    }

    fun search(query: String) {
        viewModelScope.launch {
            val response = repository.search(query)
            radioList.value = response
        }
    }
}