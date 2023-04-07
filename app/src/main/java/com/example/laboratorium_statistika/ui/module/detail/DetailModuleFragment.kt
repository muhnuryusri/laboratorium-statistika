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
import com.example.laboratorium_statistika.model.Module
import com.example.laboratorium_statistika.model.ModuleTab
import com.example.laboratorium_statistika.repository.ModuleRepositoryImpl
import com.example.laboratorium_statistika.ui.module.adapter.ModuleAdapter
import com.example.laboratorium_statistika.ui.module.adapter.ModuleTabAdapter
import com.example.laboratorium_statistika.ui.module.tab.ModuleTabViewModel
import com.example.laboratorium_statistika.viewmodel.ModuleViewModelFactory

class DetailModuleFragment : Fragment() {
    private lateinit var binding: FragmentDetailModuleBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailModuleBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val module = arguments?.getParcelable<Module>("module")
        val moduleTab = arguments?.getParcelable<ModuleTab>("tab")

        if (module != null) {
            showModuleDetail(module)
        } else if (moduleTab != null) {
            showModuleTabDetail(moduleTab)
        }
    }

    private fun showModuleDetail(module: Module?) {
        binding.apply {
            tvTitle.text = module?.title
            tvDescription.text = module?.description
        }
    }

    private fun showModuleTabDetail(moduleTab: ModuleTab?) {
        binding.apply {
            tvTitle.text = moduleTab?.title
            tvDescription.text = moduleTab?.description
        }
    }
}