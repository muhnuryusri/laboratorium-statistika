package com.example.laboratorium_statistika.ui.data_analysis

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laboratorium_statistika.model.DataAnalysisResult
import com.example.laboratorium_statistika.repository.DataAnalysisRepository
import com.example.laboratorium_statistika.repository.ModuleRepository
import kotlinx.coroutines.launch

class DataAnalysisViewModel(private val repository: DataAnalysisRepository) : ViewModel() {

    fun calculateDeskriptifData(analysis: String, edtDataName: String, edtDataValue: String, runCount: Int, tvSelectData: String) {
        val result = repository.calculateDeskriptifData(analysis, edtDataName, edtDataValue, runCount, tvSelectData)
        repository.addResult(result)
    }

    fun calculateUjiNormalitas(dataAlpha: String, edtDataName: String, edtDataValue: String, runCount: Int, tvSelectData: String) {
        val result = repository.calculateUjiNormalitas(dataAlpha, edtDataName, edtDataValue, runCount, tvSelectData)
        repository.addResult(result)
    }

    fun calculateUjiHomogenitas(dataAlpha: String, edtDataName1: String, edtDataValue1: String, edtDataName2: String, edtDataValue2: String, runCount: Int, tvSelectData: String) {
        val result = repository.calculateUjiHomogenitas(dataAlpha, edtDataName1, edtDataValue1, edtDataName2, edtDataValue2, runCount, tvSelectData)
        repository.addResult(result)
    }

    fun calculateUjiHeteroskedastisitas(dataAlpha: String, edtDataName1: String, edtDataValue1: String, edtDataName2: String, edtDataValue2: String, runCount: Int, tvSelectData: String) {
        val result = repository.calculateUjiHeteroskedastisitas(dataAlpha, edtDataName1, edtDataValue1, edtDataName2, edtDataValue2, runCount, tvSelectData)
        repository.addResult(result)
    }

    fun calculateAutokorelasi(dataAlpha: String, edtDataName: String, edtDataValue: String, runCount: Int, tvSelectData: String) {
        val result = repository.calculateAutokorelasi(dataAlpha, edtDataName, edtDataValue, runCount, tvSelectData)
        repository.addResult(result)
    }

    fun calculateUjiMultikolinieritas(edtDataName: String, edtDataValue: String, runCount: Int, tvSelectData: String) {
        val result = repository.calculateUjiMultikolinieritas(edtDataName, edtDataValue, runCount, tvSelectData)
        repository.addResult(result)
    }

    fun calculateOneSampleTTest(dataAlpha: String, edtDataName: String, edtDataValue: String, edtDataPopulationMean: String, runCount: Int, tvSelectData: String) {
        val result = repository.calculateOneSampleTTest(dataAlpha, edtDataName, edtDataValue, edtDataPopulationMean, runCount, tvSelectData)
        repository.addResult(result)
    }

    fun calculateIndependentSampleTTest(dataAlpha: String, edtDataName1: String, edtDataValue1: String, edtDataName2: String, edtDataValue2: String, runCount: Int, tvSelectData: String) {
        val result = repository.calculateIndependentSampleTTest(dataAlpha, edtDataName1, edtDataValue1, edtDataName2, edtDataValue2, runCount, tvSelectData)
        repository.addResult(result)
    }

    fun calculatePairedSampleTTest(dataAlpha: String, edtDataName1: String, edtDataValue1: String, edtDataName2: String, edtDataValue2: String, runCount: Int, tvSelectData: String) {
        val result = repository.calculatePairedSampleTTest(dataAlpha, edtDataName1, edtDataValue1, edtDataName2, edtDataValue2, runCount, tvSelectData)
        repository.addResult(result)
    }

    fun calculateOneWayAnova(dataAlpha: String, edtDataName1: String, edtDataValue1: String, edtDataName2: String, edtDataValue2: String, edtDataName3: String, edtDataValue3: String, runCount: Int, tvSelectData: String) {
        val result = repository.calculateOneWayAnova(dataAlpha, edtDataName1, edtDataValue1, edtDataName2, edtDataValue2, edtDataName3, edtDataValue3, runCount, tvSelectData)
        repository.addResult(result)
    }

    fun calculateTwoWayAnova(dataAlpha: String, edtDataName1: String, edtDataValue1: String, edtDataName2: String, edtDataValue2: String, edtDataName3: String, edtDataValue3: String, edtDataName4: String, edtDataValue4: String, runCount: Int, tvSelectData: String) {
        val result = repository.calculateTwoWayAnova(dataAlpha, edtDataName1, edtDataValue1, edtDataName2, edtDataValue2, edtDataName3, edtDataValue3, edtDataName4, edtDataValue4, runCount, tvSelectData)
        repository.addResult(result)
    }

    fun calculateRegresiLinearSederhana(dataAlpha: String, edtDataName1: String, edtDataValue1: String, edtDataName2: String, edtDataValue2: String, runCount: Int, tvSelectData: String) {
        val result = repository.calculateRegresiLinearSederhana(dataAlpha, edtDataName1, edtDataValue1, edtDataName2, edtDataValue2, runCount, tvSelectData)
        repository.addResult(result)
    }

    fun calculateRegresiLinearBerganda(dataAlpha: String, edtDataName1: String, edtDataValue1: String, edtDataName2: String, edtDataValue2: String, edtDataName3: String, edtDataValue3: String, runCount: Int, tvSelectData: String) {
        val result = repository.calculateRegresiLinearBerganda(dataAlpha, edtDataName1, edtDataValue1, edtDataName2, edtDataValue2, edtDataName3, edtDataValue3, runCount, tvSelectData)
        repository.addResult(result)
    }

    fun calculateUjiBinomial(dataAlpha: String, edtDataSuccessesName: String, edtDataSuccesses: String, edtDataTrialsName: String, edtDataTrials: String, edtDataExpectiationName: String, edtDataExpectation: String, runCount: Int, tvSelectData: String) {
        val result = repository.calculateUjiBinomial(dataAlpha, edtDataSuccessesName, edtDataSuccesses, edtDataTrialsName, edtDataTrials, edtDataExpectiationName, edtDataExpectation, runCount, tvSelectData)
        repository.addResult(result)
    }

    fun calculateMannWhitney(dataAlpha: String, edtDataName1: String, edtDataValue1: String, edtDataName2: String, edtDataValue2: String, runCount: Int, tvSelectData: String) {
        val result = repository.calculateMannWhitney(dataAlpha, edtDataName1, edtDataValue1, edtDataName2, edtDataValue2, runCount, tvSelectData)
        repository.addResult(result)
    }

    fun calculateKruskalWallis(dataAlpha: String, edtDataName1: String, edtDataValue1: String, edtDataName2: String, edtDataValue2: String, edtDataName3: String, edtDataValue3: String, runCount: Int, tvSelectData: String) {
        val result = repository.calculateKruskalWallis(dataAlpha, edtDataName1, edtDataValue1, edtDataName2, edtDataValue2, edtDataName3, edtDataValue3, runCount, tvSelectData)
        repository.addResult(result)
    }
}