package com.example.laboratorium_statistika.ui.data_analysis

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laboratorium_statistika.model.DataAnalysisResult
import com.example.laboratorium_statistika.repository.DataAnalysisRepository
import com.example.laboratorium_statistika.repository.ModuleRepository
import kotlinx.coroutines.launch

class ResultViewModel(private val repository: DataAnalysisRepository) : ViewModel() {

    fun addResult(result: DataAnalysisResult) {
        repository.addResult(result)
    }

    fun getResultList(): LiveData<List<DataAnalysisResult>> {
        return repository.getResultList()
    }

    fun clearResults() {
        return repository.clearResults()
    }
}