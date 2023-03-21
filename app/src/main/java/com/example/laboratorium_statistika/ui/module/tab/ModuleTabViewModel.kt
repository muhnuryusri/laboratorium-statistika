package com.example.laboratorium_statistika.ui.module.tab

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.laboratorium_statistika.model.Module
import com.example.laboratorium_statistika.model.ModuleTab
import com.example.laboratorium_statistika.repository.ModuleRepository

class ModuleTabViewModel(private val repository: ModuleRepository) : ViewModel() {
    fun getModules(id: Int): LiveData<List<ModuleTab>> {
        return repository.getModuleTab(id)
    }
}