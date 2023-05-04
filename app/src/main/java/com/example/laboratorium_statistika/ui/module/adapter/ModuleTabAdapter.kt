package com.example.laboratorium_statistika.ui.module.adapter

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.laboratorium_statistika.R
import com.example.laboratorium_statistika.databinding.ItemModuleBinding
import com.example.laboratorium_statistika.databinding.ItemModuleTabBinding
import com.example.laboratorium_statistika.model.Module
import com.example.laboratorium_statistika.model.ModuleTab
import com.example.laboratorium_statistika.ui.data_analysis.adapter.SecondCategoryAdapter
import com.example.laboratorium_statistika.ui.module.ModuleFragmentDirections
import com.example.laboratorium_statistika.ui.module.tab.ModuleTabFragmentDirections

class ModuleTabAdapter() : RecyclerView.Adapter<ModuleTabAdapter.ViewHolder>() {
    private val items = mutableListOf<ModuleTab>()
    private var onItemClickListener: ((ModuleTab) -> Unit)? = null

    fun onItemClick(listener: (ModuleTab) -> Unit) {
        this.onItemClickListener = listener
    }

    fun setItems(newItems: List<ModuleTab>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemModuleTabBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(item)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(private val binding: ItemModuleTabBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: ModuleTab) {
            binding.apply {
                tvModuleTab.text = item.title
            }
        }
    }
}