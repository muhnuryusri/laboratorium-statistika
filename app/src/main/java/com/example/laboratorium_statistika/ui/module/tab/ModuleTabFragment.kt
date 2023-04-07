package com.example.laboratorium_statistika.ui.module.tab

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.laboratorium_statistika.databinding.FragmentModuleTabBinding
import com.example.laboratorium_statistika.repository.ModuleRepositoryImpl
import com.example.laboratorium_statistika.ui.module.adapter.ModuleTabAdapter
import com.example.laboratorium_statistika.viewmodel.ModuleViewModelFactory

class ModuleTabFragment : Fragment() {
    private lateinit var binding: FragmentModuleTabBinding
    private lateinit var adapter: ModuleTabAdapter
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

        adapter = ModuleTabAdapter(this)
        binding.rvModuleTab.adapter = adapter
        binding.rvModuleTab.layoutManager = LinearLayoutManager(activity)

        val id = arguments?.getInt("modules")
        val repository = ModuleRepositoryImpl(requireContext())
        viewModel = ViewModelProvider(this, ModuleViewModelFactory(repository))[ModuleTabViewModel::class.java]
        if (id != null) {
            viewModel.getModuleTab(id).observe(viewLifecycleOwner) { modules ->
                binding.rvModuleTab.adapter?.let { adapter ->
                    if (adapter is ModuleTabAdapter) {
                        adapter.setItems(modules)
                    }
                }
            }
        }
    }
}