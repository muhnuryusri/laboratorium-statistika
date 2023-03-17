package com.example.laboratorium_statistika.ui.module.adapter

import android.os.Bundle
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

class ModuleAdapter<T>(private val callback: MyAdapterCallback, private val items: List<T>?) : RecyclerView.Adapter<ModuleAdapter.ViewHolder<T>>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<T> {
        val binding = ItemModuleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return when (items?.firstOrNull()) {
            is Module -> ModuleViewHolder(binding)
            is ModuleTab -> TabViewHolder(binding)
            else -> throw IllegalArgumentException("Invalid item type")
        } as ViewHolder<T>
    }

    override fun onBindViewHolder(holder: ViewHolder<T>, position: Int) {
        items?.get(position)?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    abstract class ViewHolder<T>(private val binding: ItemModuleBinding) : RecyclerView.ViewHolder(binding.root) {
        abstract fun bind(item: T)
    }

    inner class ModuleViewHolder(private val binding: ItemModuleBinding) : ViewHolder<Module>(binding) {
        override fun bind(item: Module) {
            binding.btnModul.text = item.title
            binding.btnModul.setOnClickListener {
                callback.onButtonClick(item.id)
            }
        }
    }

    inner class TabViewHolder(private val binding: ItemModuleBinding) : ViewHolder<ModuleTab>(binding) {
        override fun bind(item: ModuleTab) {
            binding.btnModul.text = item.title
        }
    }
}