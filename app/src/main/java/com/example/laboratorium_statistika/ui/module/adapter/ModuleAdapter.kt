package com.example.laboratorium_statistika.ui.module.adapter

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
import com.example.laboratorium_statistika.model.Module
import com.example.laboratorium_statistika.model.ModuleTab

class ModuleAdapter(private val callback: MyAdapterCallback) : RecyclerView.Adapter<ModuleAdapter.ViewHolder>() {
    private val items = mutableListOf<Module>()

    fun setItems(newItems: List<Module>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemModuleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        items[position].let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(private val binding: ItemModuleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Module) {
            binding.btnModul.text = item.title
            binding.btnModul.setOnClickListener {
                item.id?.let { it1 -> callback.onModuleClick(it1) }
            }
        }
    }
}