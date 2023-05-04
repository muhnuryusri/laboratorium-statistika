package com.example.laboratorium_statistika.ui.exercises

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.laboratorium_statistika.R
import com.example.laboratorium_statistika.databinding.FragmentExercisesBinding
import com.example.laboratorium_statistika.repository.ExercisesRepositoryImpl
import com.example.laboratorium_statistika.repository.ModuleRepositoryImpl
import com.example.laboratorium_statistika.ui.exercises.adapter.ExerciseAdapter
import com.example.laboratorium_statistika.ui.module.ModuleViewModel
import com.example.laboratorium_statistika.ui.module.adapter.ModuleAdapter
import com.example.laboratorium_statistika.viewmodel.ExercisesViewModelFactory
import com.example.laboratorium_statistika.viewmodel.ModuleViewModelFactory

class ExercisesFragment : Fragment() {
    private lateinit var binding: FragmentExercisesBinding
    private lateinit var viewModel: ExercisesViewModel
    private lateinit var adapter: ExerciseAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExercisesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ExerciseAdapter(this)
        binding.rvQuestion.adapter = adapter
        binding.rvQuestion.layoutManager = LinearLayoutManager(activity)

        val repository = ExercisesRepositoryImpl(requireContext())
        viewModel = ViewModelProvider(this, ExercisesViewModelFactory(repository))[ExercisesViewModel::class.java]

        val questions = viewModel.getQuestions()
        binding.rvQuestion.adapter?.let { adapter ->
            if (adapter is ExerciseAdapter) {
                adapter.setItems(questions)
            }
        }
    }
}