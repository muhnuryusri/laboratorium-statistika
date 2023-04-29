package com.example.laboratorium_statistika.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Module(
    var id: Int? = 0,
    var title: String? = null,
    var tab: List<ModuleTab>? = null,
    var module: String? = null,
) : Parcelable

@Parcelize
data class ModuleTab(
    var id: Int? = 0,
    var title: String? = null,
    var module: String? = null,
    var analysis: List<AnalysisTab>? = null
) : Parcelable

@Parcelize
data class AnalysisTab(
    var id: Int? = 0,
    var title: String? = null
) : Parcelable

@Parcelize
data class DataAnalysisResult(
    var id: Int? = 0,
    var resultTitle: String? = null,
    var resultData: String? = null,
    var descriptiveTitle: String? = null,
    var descriptiveContent: String? = null,
    var secondDescriptiveTitle: String? = null,
    var secondDescriptiveContent: String? = null,
    var thirdDescriptiveTitle: String? = null,
    var thirdDescriptiveContent: String? = null,
    var testValuesContent: String? = null,
    var resultConclusion: String? = null,
    var amountOfData: Int? = 0,
    var hideTestValues: Boolean? = false
) : Parcelable