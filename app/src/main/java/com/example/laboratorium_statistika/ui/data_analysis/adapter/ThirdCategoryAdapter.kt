package com.example.laboratorium_statistika.ui.data_analysis.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.laboratorium_statistika.databinding.ItemCategoryBinding
import com.example.laboratorium_statistika.model.AnalysisTab
import com.example.laboratorium_statistika.model.Module
import com.example.laboratorium_statistika.model.ModuleTab

class ThirdCategoryAdapter(private val listener: OnAnalysisTabClickListener) : RecyclerView.Adapter<ThirdCategoryAdapter.ViewHolder>() {
    private val items = mutableListOf<AnalysisTab>()

    fun setItems(newItems: List<AnalysisTab>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    interface OnAnalysisTabClickListener {
        fun onAnalysisTabClicked(text: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        items[position].let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(private val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AnalysisTab) {
            binding.tvCategory.text = item.title
            binding.tvCategory.setOnClickListener {
                item.title?.let { it1 -> listener.onAnalysisTabClicked(it1) }
            }
        }
    }
}