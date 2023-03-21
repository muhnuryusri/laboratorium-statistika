package com.example.laboratorium_statistika.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Module(
    var id: Int? = 0,
    var title: String? = null,
    var tab: List<ModuleTab>? = null,
    var description: String? = null,
) : Parcelable

@Parcelize
data class ModuleTab(
    var id: Int? = 0,
    var title: String? = null,
    var description: String? = null
) : Parcelable