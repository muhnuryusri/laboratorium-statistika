package com.example.laboratorium_statistika.ui.module.tab

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.laboratorium_statistika.databinding.FragmentModuleTabBinding
import com.example.laboratorium_statistika.model.ModuleTab
import com.example.laboratorium_statistika.repository.ModuleRepositoryImpl
import com.example.laboratorium_statistika.ui.module.adapter.ModuleAdapter
import com.example.laboratorium_statistika.ui.module.adapter.MyAdapterCallback
import com.example.laboratorium_statistika.viewmodel.ModuleViewModelFactory

class ModuleTabFragment : Fragment(), MyAdapterCallback {
    private lateinit var binding: FragmentModuleTabBinding
    private lateinit var adapter: ModuleAdapter<ModuleTab>
    private lateinit var viewModel: ModuleTabViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentModuleTabBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = ModuleRepositoryImpl(requireContext())
        viewModel = ViewModelProvider(this, ModuleViewModelFactory(repository))[ModuleTabViewModel::class.java]

        val moduleId = arguments?.getInt("moduleId", 0) ?: 0
        Log.d("ModuleTabFragment", "Module ID: $moduleId")
        adapter = ModuleAdapter(this, moduleId.let { viewModel.getModuleTab(it) })
        binding.rvModule.adapter = adapter
        binding.rvModule.layoutManager = LinearLayoutManager(activity)
    }

    override fun onButtonClick(id: Int) {
        
    }
}