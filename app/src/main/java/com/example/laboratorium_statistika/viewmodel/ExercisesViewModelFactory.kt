package com.example.laboratorium_statistika.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.laboratorium_statistika.repository.DataAnalysisRepository
import com.example.laboratorium_statistika.repository.ExercisesRepository
import com.example.laboratorium_statistika.repository.ModuleRepository
import com.example.laboratorium_statistika.ui.data_analysis.AnalysisTabViewModel
import com.example.laboratorium_statistika.ui.data_analysis.DataAnalysisViewModel
import com.example.laboratorium_statistika.ui.data_analysis.ResultViewModel
import com.example.laboratorium_statistika.ui.exercises.ExercisesViewModel
import com.example.laboratorium_statistika.ui.module.ModuleViewModel
import com.example.laboratorium_statistika.ui.module.tab.ModuleTabViewModel

class ExercisesViewModelFactory(private val repository: ExercisesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExercisesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExercisesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}