package com.example.laboratorium_statistika.ui.data_analysis.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.laboratorium_statistika.databinding.ItemCategoryBinding
import com.example.laboratorium_statistika.databinding.ItemResultBinding
import com.example.laboratorium_statistika.model.AnalysisTab
import com.example.laboratorium_statistika.model.DataAnalysisResult
import com.example.laboratorium_statistika.model.Module
import com.example.laboratorium_statistika.model.ModuleTab

class ResultAdapter(private var hideTestValues: Boolean) : RecyclerView.Adapter<ResultAdapter.ViewHolder>() {
    private val items = mutableListOf<DataAnalysisResult>()

    fun setItems(newItems: List<DataAnalysisResult>) {
        items.clear()
        items.addAll(newItems.reversed())
        notifyDataSetChanged()
    }

    fun updateHideTestValues(hideTestValues: Boolean) {
        this.hideTestValues = hideTestValues
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        items[position].let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(private val binding: ItemResultBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DataAnalysisResult) {
            binding.apply {
                tvResultTitle.text = item.resultTitle
                tvResultData.text = item.resultData
                tvDescriptiveTitle.text = item.descriptiveTitle
                tvDescriptiveContent.text = item.descriptiveContent

                if (hideTestValues) {
                    tvTestValuesTitle.visibility = View.GONE
                    tvTestValuesContent.visibility = View.GONE
                    tvResultConclusion.visibility = View.GONE
                } else {
                    tvTestValuesContent.text = item.testValuesContent
                    tvResultConclusion.text = item.resultConclusion
                }
            }
        }
    }
}