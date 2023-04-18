package com.example.laboratorium_statistika.ui.data_analysis.adapter

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.laboratorium_statistika.R
import com.example.laboratorium_statistika.databinding.ItemCategoryBinding
import com.example.laboratorium_statistika.model.Module
import com.example.laboratorium_statistika.model.ModuleTab
import com.example.laboratorium_statistika.ui.data_analysis.SharedViewModel

class SecondCategoryAdapter(
    private val listener: OnModuleTabClickListener,
    private val sharedViewModel: SharedViewModel
) : RecyclerView.Adapter<SecondCategoryAdapter.ViewHolder>() {
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
            binding.apply {
                tvCategory.text = item.title

                if (sharedViewModel.analysisText.value == item.title) {
                    tvCategory.setTextColor(ContextCompat.getColor(itemView.context, R.color.blue))
                    root.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.blue_surface))
                    val drawable = ContextCompat.getDrawable(itemView.context, R.drawable.round_check_24)
                    drawable?.let {
                        val tint = ContextCompat.getColor(itemView.context, R.color.blue)
                        it.setColorFilter(tint, PorterDuff.Mode.SRC_IN)
                        tvCategory.setCompoundDrawablesWithIntrinsicBounds(null, null, it, null)
                    }
                } else {
                    tvCategory.setTextColor(ContextCompat.getColor(itemView.context, R.color.neutral_black))
                    root.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.white))
                    tvCategory.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }

                tvCategory.setOnClickListener {
                    item.id?.let { it1 -> item.title?.let { it2 ->
                        listener.onModuleTabClicked(moduleId, it1,
                            it2
                        )
                    } }
                }
            }
        }
    }
}