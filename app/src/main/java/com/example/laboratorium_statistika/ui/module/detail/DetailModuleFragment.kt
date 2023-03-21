package com.example.laboratorium_statistika.ui.module.detail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.laboratorium_statistika.R
import com.example.laboratorium_statistika.databinding.FragmentDetailModuleBinding
import com.example.laboratorium_statistika.repository.ModuleRepositoryImpl
import com.example.laboratorium_statistika.ui.module.adapter.ModuleAdapter
import com.example.laboratorium_statistika.ui.module.tab.ModuleTabViewModel
import com.example.laboratorium_statistika.viewmodel.ModuleViewModelFactory

class DetailModuleFragment : Fragment() {
    private lateinit var binding: FragmentDetailModuleBinding
    private lateinit var viewModel: DetailModuleViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailModuleBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = ModuleRepositoryImpl(requireContext())
        viewModel = ViewModelProvider(this, ModuleViewModelFactory(repository))[DetailModuleViewModel::class.java]

        val tabId = arguments?.getInt("moduleId", 0) ?: 0
        Log.d("DetailModuleFragment", "Tab ID: $tabId")
        viewModel.getDetailModuleTab(tabId).observe(viewLifecycleOwner) { module ->
            binding.tvTitle.text = module?.title
            binding.tvDescription.text = module?.description
        }
    }
}