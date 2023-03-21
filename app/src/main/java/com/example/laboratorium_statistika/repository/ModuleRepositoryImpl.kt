package com.example.laboratorium_statistika.repository

import android.content.Context
import android.util.Log
import android.util.Xml
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.laboratorium_statistika.model.Module
import com.example.laboratorium_statistika.model.ModuleTab
import com.google.gson.Gson
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParser

class ModuleRepositoryImpl(private val context: Context) : ModuleRepository {
/*    override fun getModules(): List<Module> {
        val parser = Xml.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        val inputStream = context.assets.open("modules.xml")
        parser.setInput(inputStream, null)

        var eventType = parser.eventType
        var currentModule: Module? = null
        var currentModuleTab: ModuleTab? = null
        val moduleList = mutableListOf<Module>()
        val moduleTabList = mutableListOf<ModuleTab>()

        while (eventType != XmlPullParser.END_DOCUMENT) {
            val tagName = parser.name
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when (tagName) {
                        "module" -> currentModule = Module()
                        "tab" -> currentModuleTab = ModuleTab()
                        "id" -> currentModule?.id = parser.nextText().toInt()
                        "title" -> currentModule?.title = parser.nextText()
                        "description" -> currentModule?.description = parser.nextText()
                        "tab_id" -> currentModuleTab?.id = parser.nextText().toInt()
                        "tab_title" -> currentModuleTab?.title = parser.nextText()
                        "tab_description" -> currentModuleTab?.description = parser.nextText()
                    }
                }
                XmlPullParser.END_TAG -> {
                    when (tagName) {
                        "module" -> currentModule?.let {
                            moduleList.add(it)
                            Log.d("XMLParser", "Parsed module: $currentModule")
                        }
                        "tab" -> currentModuleTab.let {
                            if (it != null) {
                                moduleTabList.add(it)
                            }
                            Log.d("XMLParser", "Parsed tab: $currentModuleTab")
                        }
                    }
                }
            }
            eventType = parser.next()
        }
        return moduleList
    }*/

    private val modulesLiveData = MutableLiveData<List<Module>>()
    private val moduleTabLiveData = MutableLiveData<List<ModuleTab>>()
    private val jsonString = context.assets.open("modules.json").bufferedReader().use { it.readText() }

    override fun getModules(): LiveData<List<Module>> {
        val jsonObject = JSONObject(jsonString)
        val modulesObject = jsonObject.getJSONObject("modules")
        val moduleArray = modulesObject.getJSONArray("module")

        val moduleList = mutableListOf<Module>()
        val tabList = mutableListOf<ModuleTab>()

        for (i in 0 until moduleArray.length()) {
            val moduleObject = moduleArray.getJSONObject(i)
            val tabArray = moduleObject.getJSONArray("tab")

            for (j in 0 until tabArray.length()) {
                val tabObject = tabArray.getJSONObject(j)
                val tabId = tabObject.getInt("tab_id")
                val tabTitle = tabObject.getString("tab_title")
                val tabDescription = tabObject.getString("tab_description")

                val tab = ModuleTab(tabId, tabTitle, tabDescription)
                tabList.add(tab)
            }

            val id = moduleObject.getInt("id")
            val title = moduleObject.getString("title")
            val description = moduleObject.getString("description")

            val module = Module(id, title, tabList, description, )
            moduleList.add(module)
        }

        modulesLiveData.value = moduleList
        return modulesLiveData
    }

    override fun getModuleTab(id: Int): LiveData<List<ModuleTab>> {
        val jsonObject = JSONObject(jsonString)
        val moduleObject = jsonObject.getJSONObject("module")

        val tabList = mutableListOf<ModuleTab>()

        val tabArray = moduleObject.getJSONArray("tab")

        for (j in 0 until tabArray.length()) {
            val tabObject = tabArray.getJSONObject(j)
            val tabId = tabObject.getInt("tab_id")
            val tabTitle = tabObject.getString("tab_title")
            val tabDescription = tabObject.getString("tab_description")

            val tab = ModuleTab(tabId, tabTitle, tabDescription)
            tabList.add(tab)

            Log.d("ModuleAdapter", "Added module tab: $tab")
        }

        moduleTabLiveData.value = tabList
        return moduleTabLiveData
    }

    override fun getDetailModule(id: Int): Module? {
        val parser = Xml.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        val inputStream = context.assets.open("modules.xml")
        parser.setInput(inputStream, null)

        var eventType = parser.eventType
        var currentItem: Module? = null

        while (eventType != XmlPullParser.END_DOCUMENT) {
            val tagName = parser.name
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when (tagName) {
                        "module" -> currentItem = Module()
                    }
                }
                XmlPullParser.END_TAG -> {
                    when (tagName) {
                        "description" -> currentItem?.description = parser.nextText()
                    }
                }
            }
            eventType = parser.next()
        }
        return currentItem
    }

    override fun getDetailModuleTab(id: Int): MutableLiveData<ModuleTab?> {
        val parser = Xml.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        val inputStream = context.assets.open("modules.xml")
        parser.setInput(inputStream, null)

        var eventType = parser.eventType
        var currentItem: ModuleTab? = null
        val itemList = mutableListOf<ModuleTab>()

        while (eventType != XmlPullParser.END_DOCUMENT) {
            val tagName = parser.name
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when (tagName) {
                        "tab" -> currentItem = ModuleTab()
                        "tab_id" -> currentItem?.id = parser.nextText().toInt()
                        "tab_title" -> currentItem?.title = parser.nextText()
                        "description" -> currentItem?.description = parser.nextText()
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (tagName == "tab") {
                        currentItem?.let { itemList.add(it) }
                    }
                }
            }
            eventType = parser.next()
        }

        val liveData = MutableLiveData<ModuleTab?>()
        liveData.value = currentItem
        return liveData
    }

    private fun loadJsonFromAssets(fileName: String): String {
        val inputStream = context.assets.open(fileName)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        return String(buffer, Charsets.UTF_8)
    }
}