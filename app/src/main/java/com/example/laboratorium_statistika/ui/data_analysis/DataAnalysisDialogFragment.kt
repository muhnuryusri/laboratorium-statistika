package com.example.laboratorium_statistika.ui.data_analysis

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.laboratorium_statistika.databinding.FragmentDataAnalysisDialogBinding
import com.example.laboratorium_statistika.repository.ModuleRepositoryImpl
import com.example.laboratorium_statistika.ui.data_analysis.adapter.FirstCategoryAdapter
import com.example.laboratorium_statistika.ui.data_analysis.adapter.SecondCategoryAdapter
import com.example.laboratorium_statistika.ui.data_analysis.adapter.ThirdCategoryAdapter
import com.example.laboratorium_statistika.viewmodel.ModuleViewModelFactory
import java.util.*

class DataAnalysisDialogFragment : DialogFragment(),
    FirstCategoryAdapter.OnModuleClickListener,
    SecondCategoryAdapter.OnModuleTabClickListener,
    ThirdCategoryAdapter.OnAnalysisTabClickListener
{
    private lateinit var binding: FragmentDataAnalysisDialogBinding
    private lateinit var adapter: RecyclerView.Adapter<*>
    private lateinit var currentAdapter: RecyclerView.Adapter<*>
    private lateinit var previousAdapter: RecyclerView.Adapter<*>
    private lateinit var viewModel: AnalysisTabViewModel
    private val adapterStack = Stack<RecyclerView.Adapter<*>>()
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
        currentAdapter = adapter

        val repository = ModuleRepositoryImpl(requireContext())

        viewModel = ViewModelProvider(this, ModuleViewModelFactory(repository))[AnalysisTabViewModel::class.java]
        viewModel.getModules().observe(viewLifecycleOwner) { modules ->
            binding.rvDataAnalysis.adapter?.let { adapter ->
                if (adapter is FirstCategoryAdapter) {
                    adapter.setItems(modules)
                }
            }
        }

        binding.imgBackButton.setOnClickListener {
            if (adapterStack.isNotEmpty()) {
                currentAdapter = adapterStack.pop()
                binding.rvDataAnalysis.adapter = currentAdapter

                // Hide the back button if we are back to the first adapter
                if (adapterStack.isEmpty()) {
                    binding.imgBackButton.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)

        val window = dialog.window
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onModuleClicked(moduleId: Int) {
        binding.imgBackButton.visibility = View.VISIBLE

        previousAdapter = currentAdapter
        adapterStack.push(currentAdapter)
        adapter = SecondCategoryAdapter(this, sharedViewModel)
        binding.rvDataAnalysis.adapter = adapter
        currentAdapter = adapter

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
        binding.imgBackButton.visibility = View.VISIBLE

        previousAdapter = currentAdapter
        adapterStack.push(currentAdapter)
        adapter = ThirdCategoryAdapter(this, sharedViewModel)
        binding.rvDataAnalysis.adapter = adapter
        currentAdapter = adapter

        val repository = ModuleRepositoryImpl(requireContext())
        viewModel = ViewModelProvider(this, ModuleViewModelFactory(repository))[AnalysisTabViewModel::class.java]
        viewModel.getAnalysisTab(moduleId, tabId).observe(viewLifecycleOwner) { analysisTab ->
            if (analysisTab.isNullOrEmpty()) {
                sharedViewModel.analysisText.value = text
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