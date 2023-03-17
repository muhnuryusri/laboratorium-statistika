package com.example.laboratorium_statistika.repository

import android.content.Context
import android.util.Xml
import com.example.laboratorium_statistika.model.Module
import com.example.laboratorium_statistika.model.ModuleTab
import org.xmlpull.v1.XmlPullParser

class ModuleRepositoryImpl(private val context: Context) : ModuleRepository {
    override fun getModules(): List<Module> {
        val parser = Xml.newPullParser()
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
        val inputStream = context.assets.open("modules.xml")
        parser.setInput(inputStream, null)

        var eventType = parser.eventType
        var currentItem: Module? = null
        val itemList = mutableListOf<Module>()

        while (eventType != XmlPullParser.END_DOCUMENT) {
            val tagName = parser.name
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    when (tagName) {
                        "module" -> currentItem = Module(0, "", emptyList(), "")
                        "id" -> currentItem?.id = parser.nextText().toInt()
                        "title" -> currentItem?.title = parser.nextText()
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (tagName == "module") {
                        currentItem?.let { itemList.add(it) }
                    }
                }
            }
            eventType = parser.next()
        }
        return itemList
    }

    override fun getModuleTab(id: Int): List<ModuleTab> {
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
                        "tab" -> currentItem = ModuleTab(0, "", "")
                        "tab_title" -> currentItem?.title = parser.nextText()
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
        return itemList
    }
}