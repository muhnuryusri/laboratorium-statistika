package com.example.laboratorium_statistika.repository

import androidx.lifecycle.LiveData
import com.example.laboratorium_statistika.model.DataAnalysisResult

interface DataAnalysisRepository {
    fun calculateDeskriptifData(analysis: String, edtDataName: String, edtDataValue: String, runCount: Int, tvSelectData: String): DataAnalysisResult
    fun calculateUjiNormalitas(dataAlpha: String, edtDataName: String, edtDataValue: String, runCount: Int, tvSelectData: String): DataAnalysisResult
    fun calculateUjiHomogenitas(dataAlpha: String, edtDataName1: String, edtDataValue1: String, edtDataName2: String, edtDataValue2: String, runCount: Int, tvSelectData: String): DataAnalysisResult
    fun calculateUjiHeteroskedastisitas(dataAlpha: String, edtDataName1: String, edtDataValue1: String, edtDataName2: String, edtDataValue2: String, runCount: Int, tvSelectData: String): DataAnalysisResult
    fun calculateAutokorelasi(dataAlpha: String, edtDataName: String, edtDataValue: String, runCount: Int, tvSelectData: String): DataAnalysisResult
    fun calculateUjiMultikolinieritas(edtDataName: String, edtDataValue: String, runCount: Int, tvSelectData: String): DataAnalysisResult
    fun calculateOneSampleTTest(dataAlpha: String, edtDataName: String, edtDataValue: String, edtDataPopulationMean: String, runCount: Int, tvSelectData: String): DataAnalysisResult
    fun calculateIndependentSampleTTest(dataAlpha: String, edtDataName1: String, edtDataValue1: String, edtDataName2: String, edtDataValue2: String, runCount: Int, tvSelectData: String): DataAnalysisResult
    fun calculatePairedSampleTTest(dataAlpha: String, edtDataName1: String, edtDataValue1: String, edtDataName2: String, edtDataValue2: String, runCount: Int, tvSelectData: String): DataAnalysisResult
    fun calculateOneWayAnova(dataAlpha: String, edtDataName1: String, edtDataValue1: String, edtDataName2: String, edtDataValue2: String, edtDataName3: String, edtDataValue3: String, runCount: Int, tvSelectData: String): DataAnalysisResult
    fun calculateTwoWayAnova(dataAlpha: String, edtDataName1: String, edtDataValue1: String, edtDataName2: String, edtDataValue2: String, edtDataName3: String, edtDataValue3: String, edtDataName4: String, edtDataValue4: String, runCount: Int, tvSelectData: String): DataAnalysisResult
    fun calculateRegresiLinearSederhana(dataAlpha: String, edtDataName1: String, edtDataValue1: String, edtDataName2: String, edtDataValue2: String, runCount: Int, tvSelectData: String): DataAnalysisResult
    fun calculateRegresiLinearBerganda(dataAlpha: String, edtDataName1: String, edtDataValue1: String, edtDataName2: String, edtDataValue2: String, edtDataName3: String, edtDataValue3: String, runCount: Int, tvSelectData: String): DataAnalysisResult
    fun calculateUjiBinomial(dataAlpha: String, edtDataSuccessesName: String, edtDataSuccesses: String, edtDataTrialsName: String, edtDataTrials: String, edtDataExpectiationName: String, edtDataExpectation: String, runCount: Int, tvSelectData: String): DataAnalysisResult
    fun calculateMannWhitney(dataAlpha: String, edtDataName1: String, edtDataValue1: String, edtDataName2: String, edtDataValue2: String, runCount: Int, tvSelectData: String): DataAnalysisResult
    fun calculateKruskalWallis(dataAlpha: String, edtDataName1: String, edtDataValue1: String, edtDataName2: String, edtDataValue2: String, edtDataName3: String, edtDataValue3: String, runCount: Int, tvSelectData: String): DataAnalysisResult
    fun calculateUjiValiditas(dataAlpha: String, edtDataName1: String, edtDataValue1: String, edtDataName2: String, edtDataValue2: String, runCount: Int, tvSelectData: String): DataAnalysisResult

    fun addResult(result: DataAnalysisResult)
    fun getResultList(): LiveData<List<DataAnalysisResult>>
    fun clearResults()
}