package com.example.laboratorium_statistika.model

data class Module(
    var id: Int,
    var title: String,
    var tab: List<ModuleTab>,
    var description: String,
)

data class ModuleTab(
    var id: Int,
    var title: String,
    var description: String
)