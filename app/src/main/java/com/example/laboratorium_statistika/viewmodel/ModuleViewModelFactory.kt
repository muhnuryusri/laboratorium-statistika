package com.example.laboratorium_statistika.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.laboratorium_statistika.repository.ModuleRepository
import com.example.laboratorium_statistika.ui.module.ModuleViewModel
import com.example.laboratorium_statistika.ui.module.detail.DetailModuleViewModel
import com.example.laboratorium_statistika.ui.module.tab.ModuleTabViewModel

class ModuleViewModelFactory(private val repository: ModuleRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ModuleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ModuleViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(ModuleTabViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ModuleTabViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(DetailModuleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailModuleViewModel(repository) as T
    }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}