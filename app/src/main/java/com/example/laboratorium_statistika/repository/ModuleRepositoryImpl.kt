package com.example.laboratorium_statistika.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.laboratorium_statistika.model.AnalysisTab
import com.example.laboratorium_statistika.model.DataAnalysisResult
import com.example.laboratorium_statistika.model.Module
import com.example.laboratorium_statistika.model.ModuleTab
import com.google.gson.JsonParser

class ModuleRepositoryImpl(private val context: Context) : ModuleRepository {
    private val jsonString = context.assets.open("modules.json").bufferedReader().use { it.readText() }
    private val resultList = MutableLiveData<List<DataAnalysisResult>>(emptyList())

    override fun getModules(): LiveData<List<Module>> {
        val modulesLiveData = MutableLiveData<List<Module>>()

        val jsonObject = JsonParser.parseString(jsonString).asJsonObject
        val modulesObject = jsonObject.get("modules").asJsonObject
        val moduleArray = modulesObject.getAsJsonArray("module")

        val moduleList = mutableListOf<Module>()
        val tabList = mutableListOf<ModuleTab>()

        for (i in 0 until moduleArray.size()) {
            val moduleObject = moduleArray.get(i).asJsonObject

            val id = moduleObject.get("id").asInt
            val title = moduleObject.get("title").asString
            val description = moduleObject.get("description").asString

            val module = Module(id, title, tabList, description)
            moduleList.add(module)
            Log.d("Test", "Value: $module")
        }

        modulesLiveData.value = moduleList
        return modulesLiveData
    }

    override fun getModuleTab(moduleId: Int): LiveData<List<ModuleTab>> {
        val moduleTabLiveData = MutableLiveData<List<ModuleTab>>()

        val jsonObject = JsonParser.parseString(jsonString).asJsonObject
        val modulesObject = jsonObject.get("modules").asJsonObject
        val moduleArray = modulesObject.getAsJsonArray("module")

        val tabList = mutableListOf<ModuleTab>()

        for (i in 0 until moduleArray.size()) {
            val moduleObject = moduleArray.get(i).asJsonObject

            if (moduleObject.get("id").asInt ==  moduleId) {
                val tabArray = moduleObject.getAsJsonArray("tab")
                for (j in 0 until tabArray.size()) {
                    val tabObject = tabArray.get(j).asJsonObject
                    val tab = ModuleTab(
                        tabObject.get("tab_id").asInt,
                        tabObject.get("tab_title").asString,
                        tabObject.get("tab_description").asString
                    )
                    tabList.add(tab)
                    Log.d("Test", "Value: $tab")
                }
            }
        }


        moduleTabLiveData.value = tabList
        return moduleTabLiveData
    }

    override fun getAnalysisTab(moduleId: Int, tabId: Int): LiveData<List<AnalysisTab>> {
        val analysisTabLiveData = MutableLiveData<List<AnalysisTab>>()

        val jsonObject = JsonParser.parseString(jsonString).asJsonObject
        val modulesObject = jsonObject.get("modules").asJsonObject
        val moduleArray = modulesObject.getAsJsonArray("module")

        val analysisTabList = mutableListOf<AnalysisTab>()

        for (i in 0 until moduleArray.size()) {
            val moduleObject = moduleArray.get(i).asJsonObject
            if (moduleObject.get("id").asInt ==  moduleId) {
                val tabArray = moduleObject.getAsJsonArray("tab")
                for (j in 0 until tabArray.size()) {
                    val tabObject = tabArray.get(j).asJsonObject
                    if (tabObject.get("tab_id").asInt == tabId) {
                        val analysisArray = tabObject.getAsJsonArray("analysis_tab")
                        if (analysisArray != null && analysisArray.size() > 0) {
                            for (k in 0 until analysisArray.size()) {
                                val analysisObject = analysisArray.get(k).asJsonObject
                                val analysisTab = AnalysisTab(
                                    analysisObject.get("id").asInt,
                                    analysisObject.get("title").asString
                                )
                                analysisTabList.add(analysisTab)
                                Log.d("Test", "Value: $analysisTab")
                            }
                        }
                    }
                }
            }
        }

        analysisTabLiveData.value = analysisTabList
        return analysisTabLiveData
    }

    override fun addResult(result: DataAnalysisResult) {
        val currentList = resultList.value ?: emptyList()
        resultList.value = currentList + result
    }

    override fun getResultList(): LiveData<List<DataAnalysisResult>> {
        return resultList
    }
}