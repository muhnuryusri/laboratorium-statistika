package com.example.laboratorium_statistika.ui.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.laboratorium_statistika.R
import com.example.laboratorium_statistika.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {
    private lateinit var binding: FragmentDashboardBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnModule.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_moduleFragment)
        }

        binding.btnDataAnalysis.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_dataAnalysisFragment)
        }
    }
}