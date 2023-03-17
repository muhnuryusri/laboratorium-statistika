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
import com.example.laboratorium_statistika.model.Module
import com.example.laboratorium_statistika.repository.ModuleRepositoryImpl
import com.example.laboratorium_statistika.ui.module.adapter.ModuleAdapter
import com.example.laboratorium_statistika.ui.module.adapter.MyAdapterCallback
import com.example.laboratorium_statistika.viewmodel.ModuleViewModelFactory

class ModuleFragment : Fragment(), MyAdapterCallback {
    private lateinit var binding: FragmentModuleBinding
    private lateinit var adapter: ModuleAdapter<Module>
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

        val repository = ModuleRepositoryImpl(requireContext())
        viewModel = ViewModelProvider(this, ModuleViewModelFactory(repository))[ModuleViewModel::class.java]

        adapter = ModuleAdapter(this, viewModel.getModules())
        binding.rvModule.adapter = adapter
        binding.rvModule.layoutManager = LinearLayoutManager(activity)
    }

    override fun onButtonClick(id: Int) {
        val bundle = Bundle()
        bundle.putInt("moduleId", id)
        val action = ModuleFragmentDirections.actionModuleFragmentToModuleTabFragment(id)
        findNavController().navigate(action)
    }
}