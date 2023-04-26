package com.example.laboratorium_statistika.ui.data_analysis.adapter

import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.laboratorium_statistika.R
import com.example.laboratorium_statistika.databinding.ItemCategoryBinding
import com.example.laboratorium_statistika.model.AnalysisTab
import com.example.laboratorium_statistika.model.Module
import com.example.laboratorium_statistika.model.ModuleTab
import com.example.laboratorium_statistika.ui.data_analysis.SharedViewModel

class ThirdCategoryAdapter(
    private val listener: OnAnalysisTabClickListener,
    private val sharedViewModel: SharedViewModel
) : RecyclerView.Adapter<ThirdCategoryAdapter.ViewHolder>() {

    private val items = mutableListOf<AnalysisTab>()

    fun setItems(newItems: List<AnalysisTab>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    interface OnAnalysisTabClickListener {
        fun onAnalysisTabClicked(text: String, tabId: Int)
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
            binding.apply {
                tvCategory.text = item.title

                if (sharedViewModel.analysisText.value == item.title) {
                    tvCategory.setTextColor(ContextCompat.getColor(itemView.context, R.color.blue))
                    tvCategory.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.blue_surface))
                    val drawable = ContextCompat.getDrawable(itemView.context, R.drawable.round_check_24)
                    drawable?.let {
                        val tint = ContextCompat.getColor(itemView.context, R.color.blue)
                        it.setColorFilter(tint, PorterDuff.Mode.SRC_IN)
                        tvCategory.setCompoundDrawablesWithIntrinsicBounds(null, null, it, null)
                    }
                }

                tvCategory.setOnClickListener {
                    item.title?.let { it1 -> item.id?.let { it2 ->
                        listener.onAnalysisTabClicked(it1,
                            it2
                        )
                    } }
                }
            }
        }
    }
}
