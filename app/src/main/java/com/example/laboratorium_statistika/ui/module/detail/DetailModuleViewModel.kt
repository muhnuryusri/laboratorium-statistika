package com.example.laboratorium_statistika.ui.module.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.laboratorium_statistika.model.Module
import com.example.laboratorium_statistika.model.ModuleTab
import com.example.laboratorium_statistika.repository.ModuleRepository

class DetailModuleViewModel(private val repository: ModuleRepository) : ViewModel() {
    fun getDetailModuleTab(id: Int): MutableLiveData<ModuleTab?> {
        return repository.getDetailModuleTab(id)
    }
}