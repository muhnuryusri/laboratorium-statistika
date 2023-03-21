package com.example.laboratorium_statistika.ui.module.adapter

import com.example.laboratorium_statistika.model.Module
import com.example.laboratorium_statistika.model.ModuleTab

interface MyAdapterCallback {
    fun onModuleClick(id: Int)
    fun onModuleTabClick(moduleTab: ModuleTab)
}