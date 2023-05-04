package com.example.laboratorium_statistika.ui.exercises

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.laboratorium_statistika.model.Choices
import com.example.laboratorium_statistika.model.Questions
import com.example.laboratorium_statistika.repository.ExercisesRepository
import com.example.laboratorium_statistika.repository.ModuleRepository

class ExercisesViewModel(private val repository: ExercisesRepository) : ViewModel() {
    fun getQuestions(): List<Questions> {
        return repository.getQuestions()
    }
}