package com.example.laboratorium_statistika.ui.exercises.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.laboratorium_statistika.databinding.ItemModuleBinding
import com.example.laboratorium_statistika.databinding.ItemQuestionBinding
import com.example.laboratorium_statistika.model.Module
import com.example.laboratorium_statistika.model.Questions
import com.example.laboratorium_statistika.repository.ModuleRepositoryImpl
import com.example.laboratorium_statistika.ui.module.ModuleFragmentDirections
import com.example.laboratorium_statistika.ui.module.ModuleViewModel
import com.example.laboratorium_statistika.viewmodel.ModuleViewModelFactory

class ExerciseAdapter(private val fragment: Fragment) : RecyclerView.Adapter<ExerciseAdapter.ViewHolder>() {
    private val items = mutableListOf<Questions>()

    fun setItems(newItems: List<Questions>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemQuestionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        items[position].let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(private val binding: ItemQuestionBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: Questions) {
            binding.apply {
                tvQuestionNumber.text = "Pertanyaan ${item.id}"
                tvQuestion.text = item.question
                tvChoiceA.text = item.choices[0].choice
                tvChoiceB.text = item.choices[1].choice
                tvChoiceC.text = item.choices[2].choice
                tvChoiceD.text = item.choices[3].choice
                tvChoiceE.text = item.choices[4].choice
            }
        }
    }
}