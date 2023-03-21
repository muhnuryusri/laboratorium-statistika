package com.example.laboratorium_statistika.model

data class Modules(
    var modules: List<Module>? = null
)

data class Module(
    var id: Int? = 0,
    var title: String? = null,
    var tab: List<ModuleTab>? = null,
    var description: String? = null,
)

data class ModuleTab(
    var id: Int? = 0,
    var title: String? = null,
    var description: String? = null
)