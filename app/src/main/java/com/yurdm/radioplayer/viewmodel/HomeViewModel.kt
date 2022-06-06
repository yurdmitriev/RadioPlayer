package com.yurdm.radioplayer.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yurdm.radioplayer.model.Radio
import com.yurdm.radioplayer.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response

class HomeViewModel(private val repository: Repository) : ViewModel() {
    val res: MutableLiveData<Response<List<Radio>>> = MutableLiveData()

    fun listRadios() {
        viewModelScope.launch {
            val response = repository.listRadios()
            res.value = response
        }
    }
}