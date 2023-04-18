package com.example.laboratorium_statistika.repository

import androidx.lifecycle.LiveData
import com.example.laboratorium_statistika.model.AnalysisTab
import com.example.laboratorium_statistika.model.DataAnalysisResult
import com.example.laboratorium_statistika.model.Module
import com.example.laboratorium_statistika.model.ModuleTab

interface ModuleRepository {
    fun getModules(): LiveData<List<Module>>
    fun getModuleTab(moduleId: Int): LiveData<List<ModuleTab>>
    fun getAnalysisTab(moduleId: Int, tabId: Int): LiveData<List<AnalysisTab>>
    fun addResult(result: DataAnalysisResult)
    fun getResultList(): LiveData<List<DataAnalysisResult>>
}