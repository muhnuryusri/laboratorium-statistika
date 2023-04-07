package com.example.laboratorium_statistika.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.laboratorium_statistika.model.AnalysisTab
import com.example.laboratorium_statistika.model.Module
import com.example.laboratorium_statistika.model.ModuleTab

interface ModuleRepository {
    fun getModules(): LiveData<List<Module>>
    fun getModuleTab(moduleId: Int): LiveData<List<ModuleTab>>
    fun getAnalysisTab(moduleId: Int, tabId: Int): LiveData<List<AnalysisTab>>
}