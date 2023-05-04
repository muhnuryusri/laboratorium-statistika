package com.example.laboratorium_statistika.repository

import androidx.lifecycle.LiveData
import com.example.laboratorium_statistika.model.AnalysisTab
import com.example.laboratorium_statistika.model.DataAnalysisResult
import com.example.laboratorium_statistika.model.Module
import com.example.laboratorium_statistika.model.ModuleTab

interface ModuleRepository {
    fun getModules(): List<Module>
    fun getModuleTab(moduleId: Int): List<ModuleTab>
    fun getAnalysisTab(moduleId: Int, tabId: Int): List<AnalysisTab>
}