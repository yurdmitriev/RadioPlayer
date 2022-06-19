package com.yurdm.radioplayer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yurdm.radioplayer.repository.Repository

class RadiosViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RadiosViewModel(repository) as T
    }
}