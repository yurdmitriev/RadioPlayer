package com.yurdm.radioplayer.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yurdm.radioplayer.model.Category
import com.yurdm.radioplayer.model.Radio
import com.yurdm.radioplayer.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response

class HomeViewModel(private val repository: Repository) : ViewModel() {
    val radioList: MutableLiveData<Response<List<Radio>>> = MutableLiveData()
    val categoryList: MutableLiveData<Response<List<Category>>> = MutableLiveData()

    fun listRadios() {
        viewModelScope.launch {
            val response = repository.listRadios()
            radioList.value = response
        }
    }

    fun listCategories() {
        viewModelScope.launch {
            val response = repository.listCategories()
            categoryList.value = response
        }
    }
}