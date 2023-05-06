package com.example.laboratorium_statistika.ui.data_analysis.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.laboratorium_statistika.databinding.ItemResultBinding
import com.example.laboratorium_statistika.model.DataAnalysisResult
import com.example.laboratorium_statistika.model.Module
import com.example.laboratorium_statistika.model.ModuleTab

class ResultAdapter() : RecyclerView.Adapter<ResultAdapter.ViewHolder>() {
    private val items = mutableListOf<DataAnalysisResult>()

    fun setItems(newItems: List<DataAnalysisResult>) {
        items.clear()
        items.addAll(newItems.reversed())
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


                if (item.hideTestValues == true) {
                    layoutTestValuesContainer.visibility = View.GONE
                } else {
                    layoutTestValuesContainer.visibility = View.VISIBLE
                    tvTestValuesContent.text = item.testValuesContent
                    tvResultConclusion.text = item.resultConclusion
                }

                when (item.amountOfData) {
                    1 -> {
                        layoutSecondDescriptiveContainer.visibility = View.GONE
                        layoutThirdDescriptiveContainer.visibility = View.GONE
                    }
                    2 -> {
                        layoutSecondDescriptiveContainer.visibility = View.VISIBLE
                        tvSecondDescriptiveTitle.text = item.secondDescriptiveTitle
                        tvSecondDescriptiveContent.text = item.secondDescriptiveContent
                        layoutThirdDescriptiveContainer.visibility = View.GONE
                    }
                    3 -> {
                        layoutSecondDescriptiveContainer.visibility = View.VISIBLE
                        tvSecondDescriptiveTitle.text = item.secondDescriptiveTitle
                        tvSecondDescriptiveContent.text = item.secondDescriptiveContent
                        layoutThirdDescriptiveContainer.visibility = View.VISIBLE
                        tvThirdDescriptiveTitle.text = item.thirdDescriptiveTitle
                        tvThirdDescriptiveContent.text = item.thirdDescriptiveContent
                    }
                    else -> {
                        layoutSecondDescriptiveContainer.visibility = View.GONE
                        layoutThirdDescriptiveContainer.visibility = View.GONE
                        layoutTestValuesContainer.visibility = View.GONE
                    }
                }

                if (adapterPosition == itemCount - 1) {
                    viewLine.visibility = View.GONE
                } else {
                    viewLine.visibility = View.VISIBLE
                }
            }
        }
    }
}