package com.example.laboratorium_statistika.ui.data_analysis

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.laboratorium_statistika.R
import com.example.laboratorium_statistika.databinding.FragmentDataAnalysisDialogBinding
import com.example.laboratorium_statistika.model.Module
import com.example.laboratorium_statistika.model.ModuleTab
import com.example.laboratorium_statistika.repository.ModuleRepositoryImpl
import com.example.laboratorium_statistika.ui.data_analysis.adapter.FirstCategoryAdapter
import com.example.laboratorium_statistika.ui.data_analysis.adapter.SecondCategoryAdapter
import com.example.laboratorium_statistika.ui.data_analysis.adapter.ThirdCategoryAdapter
import com.example.laboratorium_statistika.ui.module.ModuleViewModel
import com.example.laboratorium_statistika.ui.module.tab.ModuleTabFragmentDirections
import com.example.laboratorium_statistika.ui.module.tab.ModuleTabViewModel
import com.example.laboratorium_statistika.viewmodel.ModuleViewModelFactory

class DataAnalysisDialogFragment : DialogFragment(),
    FirstCategoryAdapter.OnModuleClickListener,
    SecondCategoryAdapter.OnModuleTabClickListener,
    ThirdCategoryAdapter.OnAnalysisTabClickListener
{
    private lateinit var binding: FragmentDataAnalysisDialogBinding
    private lateinit var adapter: RecyclerView.Adapter<*>
    private lateinit var viewModel: AnalysisTabViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDataAnalysisDialogBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = FirstCategoryAdapter(this)
        binding.rvDataAnalysis.adapter = adapter
        binding.rvDataAnalysis.layoutManager = LinearLayoutManager(activity)

        val repository = ModuleRepositoryImpl(requireContext())

        viewModel = ViewModelProvider(this, ModuleViewModelFactory(repository))[AnalysisTabViewModel::class.java]
        viewModel.getModules().observe(viewLifecycleOwner) { modules ->
            binding.rvDataAnalysis.adapter?.let { adapter ->
                if (adapter is FirstCategoryAdapter) {
                    adapter.setItems(modules)
                }
            }
        }
    }

    override fun onModuleClicked(moduleId: Int) {
        adapter = SecondCategoryAdapter(this)
        binding.rvDataAnalysis.adapter = adapter

        val repository = ModuleRepositoryImpl(requireContext())
        viewModel = ViewModelProvider(this, ModuleViewModelFactory(repository))[AnalysisTabViewModel::class.java]
        viewModel.getModuleTab(moduleId).observe(viewLifecycleOwner) { moduleTab ->
            binding.rvDataAnalysis.adapter?.let { adapter ->
                if (adapter is SecondCategoryAdapter) {
                    adapter.setItems(moduleTab)
                    adapter.setModuleId(moduleId)
                    Log.d("onModuleClicked", "Data: $moduleTab")
                }
            }
        }
    }

    override fun onModuleTabClicked(moduleId: Int, tabId: Int, text: String) {
        adapter = ThirdCategoryAdapter(this)
        binding.rvDataAnalysis.adapter = adapter

        val repository = ModuleRepositoryImpl(requireContext())
        viewModel = ViewModelProvider(this, ModuleViewModelFactory(repository))[AnalysisTabViewModel::class.java]
        viewModel.getAnalysisTab(moduleId, tabId).observe(viewLifecycleOwner) { analysisTab ->
            if (analysisTab.isNullOrEmpty()) {
                sharedViewModel.analysisText.value = when (text) {
                    "Uji Normalitas" -> "Kolmogorov-Smirnov Test"
                    "Uji Homogenitas" -> "Levene Test"
                    "Uji Heterokedasitas" -> "Glesjer Test"
                    "Uji Autokorelasi" -> "Durbin Watson"
                    "Uji Multikolinearitas" -> "Variance Inflation Factor (VIF)"
                    else -> text
                }
                dismiss()
            } else {
                binding.rvDataAnalysis.adapter?.let { adapter ->
                    if (adapter is ThirdCategoryAdapter) {
                        adapter.setItems(analysisTab)
                    }
                }
            }
        }
    }

    override fun onAnalysisTabClicked(text: String) {
        sharedViewModel.analysisText.value = text
        dismiss()
    }
}