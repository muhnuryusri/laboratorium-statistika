package com.example.laboratorium_statistika.ui.data_analysis.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.laboratorium_statistika.databinding.ItemCategoryBinding
import com.example.laboratorium_statistika.model.Module
import com.example.laboratorium_statistika.model.ModuleTab

class SecondCategoryAdapter(private val listener: OnModuleTabClickListener) : RecyclerView.Adapter<SecondCategoryAdapter.ViewHolder>() {
    private val items = mutableListOf<ModuleTab>()
    private var moduleId: Int = 0

    interface OnModuleTabClickListener {
        fun onModuleTabClicked(moduleId: Int, tabId: Int, text: String)
    }

    fun setModuleId(moduleId: Int) {
        this.moduleId = moduleId
    }

    fun setItems(newItems: List<ModuleTab>) {
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
        fun bind(item: ModuleTab) {
            binding.tvCategory.text = item.title
            binding.tvCategory.setOnClickListener {
                item.id?.let { it1 -> item.title?.let { it2 ->
                    listener.onModuleTabClicked(moduleId, it1,
                        it2
                    )
                } }
            }
        }
    }
}