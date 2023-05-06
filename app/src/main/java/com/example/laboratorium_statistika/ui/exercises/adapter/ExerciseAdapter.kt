package com.example.laboratorium_statistika.ui.exercises.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.laboratorium_statistika.R
import com.example.laboratorium_statistika.databinding.ItemQuestionBinding
import com.example.laboratorium_statistika.model.Questions
import com.example.laboratorium_statistika.ui.exercises.ExercisesFragment

class ExerciseAdapter(private val fragment: Fragment) : RecyclerView.Adapter<ExerciseAdapter.ViewHolder>() {
    val items = mutableListOf<Questions>()
    private var isDoneClicked = false
    private var isAnswered = false

    fun setItems(newItems: List<Questions>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun setDoneClicked(isDoneClicked: Boolean) {
        this.isDoneClicked = isDoneClicked
        notifyDataSetChanged()
    }

    fun showAnswers() {
        isAnswered = true
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemQuestionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        items[position].let { holder.bind(it) }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(val binding: ItemQuestionBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: Questions) {
            binding.apply {
                if (adapterPosition == itemCount - 1) {
                    viewLine.visibility = View.GONE
                } else {
                    viewLine.visibility = View.VISIBLE
                }

                tvQuestionNumber.text = "Pertanyaan ${item.id}"
                tvQuestion.text = item.question
                radioChoiceA.text = item.choices[0].choice
                radioChoiceB.text = item.choices[1].choice
                radioChoiceC.text = item.choices[2].choice
                radioChoiceD.text = item.choices[3].choice
                radioChoiceE.text = item.choices[4].choice

                radioGroupChoices.setOnCheckedChangeListener { _, checkedId ->
                    (fragment as ExercisesFragment).onAnswerSelected(item.id, checkedId)
                }

                if (isAnswered) {
                    val selectedChoiceIndex = radioGroupChoices.checkedRadioButtonId.let {
                        radioGroupChoices.indexOfChild(radioGroupChoices.findViewById(it))
                    }

                    if (selectedChoiceIndex == item.answerIndex) {
                        when (selectedChoiceIndex) {
                            0 -> radioChoiceA.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                            1 -> radioChoiceB.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                            2 -> radioChoiceC.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                            3 -> radioChoiceD.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                            4 -> radioChoiceE.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                        }
                        radioGroupChoices.getChildAt(selectedChoiceIndex).background = ContextCompat.getDrawable(root.context, R.drawable.custom_question_correct_choice_background)
                    } else if (selectedChoiceIndex != -1) {
                        when (item.answerIndex) {
                            0 -> radioChoiceA.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                            1 -> radioChoiceB.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                            2 -> radioChoiceC.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                            3 -> radioChoiceD.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                            4 -> radioChoiceE.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                        }
                        radioGroupChoices.getChildAt(selectedChoiceIndex).background = ContextCompat.getDrawable(root.context, R.drawable.custom_question_wrong_choice_background)
                        radioGroupChoices.getChildAt(item.answerIndex).background = ContextCompat.getDrawable(root.context, R.drawable.custom_question_correct_choice_background)
                    }
                } else {
                    for (i in 0 until radioGroupChoices.childCount) {
                        radioGroupChoices.getChildAt(i).isEnabled = true
                    }
                }

                if (isDoneClicked) {
                    for (i in 0 until radioGroupChoices.childCount) {
                        radioGroupChoices.getChildAt(i).isEnabled = false
                    }
                } else {
                    for (i in 0 until radioGroupChoices.childCount) {
                        radioGroupChoices.getChildAt(i).isEnabled = true
                    }
                }
            }
        }
    }
}