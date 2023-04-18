package com.example.laboratorium_statistika.ui.data_analysis.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.laboratorium_statistika.databinding.ItemCategoryBinding
import com.example.laboratorium_statistika.model.Module

class FirstCategoryAdapter(private val listener: OnModuleClickListener) : RecyclerView.Adapter<FirstCategoryAdapter.ViewHolder>() {
    private val items = mutableListOf<Module>()

    interface OnModuleClickListener {
        fun onModuleClicked(moduleId: Int)
    }

    fun setItems(newItems: List<Module>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
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
        fun bind(item: Module) {
            binding.apply {
                tvCategory.text = item.title
                tvCategory.setOnClickListener {
                    item.id?.let { it1 -> listener.onModuleClicked(it1) }
                }
            }
        }
    }
}