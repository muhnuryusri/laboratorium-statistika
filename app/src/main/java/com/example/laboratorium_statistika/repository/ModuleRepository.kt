package com.example.laboratorium_statistika.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.laboratorium_statistika.model.Module
import com.example.laboratorium_statistika.model.ModuleTab

interface ModuleRepository {
    fun getModules(): LiveData<List<Module>>
    fun getModuleTab(id: Int): LiveData<List<ModuleTab>>
    fun getDetailModule(id: Int): Module?
    fun getDetailModuleTab(id: Int): MutableLiveData<ModuleTab?>
}