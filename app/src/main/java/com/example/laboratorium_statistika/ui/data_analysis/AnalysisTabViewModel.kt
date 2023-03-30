package com.example.laboratorium_statistika.ui.data_analysis

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.laboratorium_statistika.model.AnalysisTab
import com.example.laboratorium_statistika.model.Module
import com.example.laboratorium_statistika.model.ModuleTab
import com.example.laboratorium_statistika.repository.ModuleRepository

class AnalysisTabViewModel(private val repository: ModuleRepository) : ViewModel() {
    fun getModules(): LiveData<List<Module>> {
        return repository.getModules()
    }

    fun getModuleTab(moduleId: Int): LiveData<List<ModuleTab>> {
        return repository.getModuleTab(moduleId)
    }

    fun getAnalysisTab(moduleId: Int, tabId: Int): LiveData<List<AnalysisTab>> {
        return repository.getAnalysisTab(moduleId, tabId)
    }
}