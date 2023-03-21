package com.example.laboratorium_statistika.ui.module

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.laboratorium_statistika.model.Module
import com.example.laboratorium_statistika.repository.ModuleRepository

class ModuleViewModel(private val repository: ModuleRepository) : ViewModel() {
    fun getModules(): LiveData<List<Module>> {
        return repository.getModules()
    }
}