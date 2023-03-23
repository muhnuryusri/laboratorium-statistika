package com.example.laboratorium_statistika.repository

import android.content.Context
import android.util.Log
import android.util.Xml
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.laboratorium_statistika.model.Module
import com.example.laboratorium_statistika.model.ModuleTab
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParser

class ModuleRepositoryImpl(private val context: Context) : ModuleRepository {
    private val jsonString = context.assets.open("modules.json").bufferedReader().use { it.readText() }

    override fun getModules(): LiveData<List<Module>> {
        val modulesLiveData = MutableLiveData<List<Module>>()

        val jsonObject = JSONObject(jsonString)
        val modulesObject = jsonObject.getJSONObject("modules")
        val moduleArray = modulesObject.getJSONArray("module")

        val moduleList = mutableListOf<Module>()
        val tabList = mutableListOf<ModuleTab>()

        for (i in 0 until moduleArray.length()) {
            val moduleObject = moduleArray.getJSONObject(i)

            val id = moduleObject.getInt("id")
            val title = moduleObject.getString("title")
            val description = moduleObject.getString("description")

            val module = Module(id, title, tabList, description)
            moduleList.add(module)
        }

        modulesLiveData.value = moduleList
        return modulesLiveData
    }

    override fun getModuleTab(id: Int): LiveData<List<ModuleTab>> {
        val moduleTabLiveData = MutableLiveData<List<ModuleTab>>()

        val jsonObject = JSONObject(jsonString)
        val modulesObject = jsonObject.getJSONObject("modules")
        val moduleArray = modulesObject.getJSONArray("module")

        val tabList = mutableListOf<ModuleTab>()

        for (i in 0 until moduleArray.length()) {
            val moduleObject = moduleArray.getJSONObject(i)

            if (moduleObject.getInt("id") == id) {
                val tabArray = moduleObject.getJSONArray("tab")
                for (j in 0 until tabArray.length()) {
                    val tabObject = tabArray.getJSONObject(j)
                    val tab = ModuleTab(
                        tabObject.getInt("tab_id"),
                        tabObject.getString("tab_title"),
                        tabObject.getString("tab_description")
                    )
                    tabList.add(tab)
                }
            }
        }

        moduleTabLiveData.value = tabList
        return moduleTabLiveData
    }
}