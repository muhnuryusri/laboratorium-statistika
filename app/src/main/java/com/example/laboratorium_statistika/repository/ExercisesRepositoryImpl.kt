package com.example.laboratorium_statistika.repository

import android.content.Context
import android.util.Log
import com.example.laboratorium_statistika.model.Choices
import com.example.laboratorium_statistika.model.Questions
import com.google.gson.JsonParser

class ExercisesRepositoryImpl(private val context: Context): ExercisesRepository {
    private val jsonString = context.assets.open("exercises.json").bufferedReader().use { it.readText() }

    override fun getQuestions(): List<Questions> {
        val jsonObject = JsonParser.parseString(jsonString).asJsonObject
        val questionsObject = jsonObject.get("exercises").asJsonObject
        val questionArray = questionsObject.getAsJsonArray("question")

        val questionList = mutableListOf<Questions>()

        for (i in 0 until questionArray.size()) {
            val questionObject = questionArray.get(i).asJsonObject

            val id = questionObject.get("id").asInt
            val question = questionObject.get("question").asString
            val answerIndex = questionObject.get("answer_index").asInt

            val choiceList = mutableListOf<Choices>()

            val questionModel = Questions(id, question, choiceList, answerIndex)
            questionList.add(questionModel)

            val choiceArray = questionObject.getAsJsonArray("choices")
            for (j in 0 until choiceArray.size()) {
                val choiceObject = choiceArray.get(j).asJsonObject
                val choiceText = choiceObject.get("choice").asString

                val choiceModel = Choices(choiceText)
                choiceList.add(choiceModel)
            }
        }

        return questionList
    }
}