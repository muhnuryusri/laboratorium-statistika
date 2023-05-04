package com.example.laboratorium_statistika.repository

import androidx.lifecycle.LiveData
import com.example.laboratorium_statistika.model.Choices
import com.example.laboratorium_statistika.model.Questions

interface ExercisesRepository {
    fun getQuestions(): List<Questions>
}