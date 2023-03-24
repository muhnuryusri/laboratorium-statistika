package com.example.laboratorium_statistika.ui.data_analysis

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Spinner
import com.example.laboratorium_statistika.R
import com.example.laboratorium_statistika.databinding.FragmentDataAnalysisBinding
import com.example.laboratorium_statistika.ui.data_analysis.adapter.SpinnerAdapter

class DataAnalysisFragment : Fragment() {
    private lateinit var binding: FragmentDataAnalysisBinding
    private lateinit var adapter: SpinnerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDataAnalysisBinding.inflate(layoutInflater, container, false)
        return (binding.root)
    }

    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val category = listOf("Deskriptif", "Uji Asumsi", "Uji Parametrik", "Uji Non Parametrik", "Uji Instrumen")

        adapter = context?.let { SpinnerAdapter(it, category) }!!
        binding.dataAnalysisSpinner.adapter = adapter
    }
}