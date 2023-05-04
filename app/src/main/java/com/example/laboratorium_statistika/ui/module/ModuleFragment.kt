package com.example.laboratorium_statistika.ui.module

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.laboratorium_statistika.databinding.FragmentModuleBinding
import com.example.laboratorium_statistika.repository.ModuleRepositoryImpl
import com.example.laboratorium_statistika.ui.module.adapter.ModuleAdapter
import com.example.laboratorium_statistika.viewmodel.ModuleViewModelFactory

class ModuleFragment : Fragment(){
    private lateinit var binding: FragmentModuleBinding
    private lateinit var adapter: ModuleAdapter
    private lateinit var viewModel: ModuleViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentModuleBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButtonText.setOnClickListener {
            findNavController().navigateUp()
        }

        adapter = ModuleAdapter(this)
        binding.rvModule.adapter = adapter
        binding.rvModule.layoutManager = LinearLayoutManager(activity)

        val repository = ModuleRepositoryImpl(requireContext())
        viewModel = ViewModelProvider(this, ModuleViewModelFactory(repository))[ModuleViewModel::class.java]

        val modules = viewModel.getModules()
        binding.rvModule.adapter?.let { adapter ->
            if (adapter is ModuleAdapter) {
                adapter.setItems(modules)
            }
        }
    }

}