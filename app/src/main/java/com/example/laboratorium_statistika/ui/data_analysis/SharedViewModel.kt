package com.example.laboratorium_statistika.ui.data_analysis

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val analysisText = MutableLiveData<String>()
}
