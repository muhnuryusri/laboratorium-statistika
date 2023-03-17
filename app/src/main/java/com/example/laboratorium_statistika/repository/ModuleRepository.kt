package com.example.laboratorium_statistika.repository

import com.example.laboratorium_statistika.model.Module
import com.example.laboratorium_statistika.model.ModuleTab

interface ModuleRepository {
    fun getModules(): List<Module>
    fun getModuleTab(id: Int): List<ModuleTab>
}