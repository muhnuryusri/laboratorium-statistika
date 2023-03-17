package com.example.laboratorium_statistika.ui.module

import androidx.lifecycle.ViewModel
import com.example.laboratorium_statistika.model.Module
import com.example.laboratorium_statistika.repository.ModuleRepository

class ModuleViewModel(private val repository: ModuleRepository) : ViewModel() {
    fun getModules(): List<Module> {
        return repository.getModules()
    }
}