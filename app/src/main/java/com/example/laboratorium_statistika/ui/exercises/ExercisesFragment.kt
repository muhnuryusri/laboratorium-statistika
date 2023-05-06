package com.example.laboratorium_statistika.ui.exercises

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.laboratorium_statistika.R
import com.example.laboratorium_statistika.databinding.FragmentExercisesBinding
import com.example.laboratorium_statistika.repository.ExercisesRepositoryImpl
import com.example.laboratorium_statistika.repository.ModuleRepositoryImpl
import com.example.laboratorium_statistika.ui.exercises.adapter.ExerciseAdapter
import com.example.laboratorium_statistika.ui.module.ModuleViewModel
import com.example.laboratorium_statistika.ui.module.adapter.ModuleAdapter
import com.example.laboratorium_statistika.viewmodel.ExercisesViewModelFactory
import com.example.laboratorium_statistika.viewmodel.ModuleViewModelFactory

class ExercisesFragment : Fragment() {
    private lateinit var binding: FragmentExercisesBinding
    private lateinit var viewModel: ExercisesViewModel
    private lateinit var adapter: ExerciseAdapter
    private val answers = mutableMapOf<Int, Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExercisesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButtonText.setOnClickListener {
            findNavController().navigateUp()
        }

        adapter = ExerciseAdapter(this)
        binding.rvQuestion.adapter = adapter
        binding.rvQuestion.layoutManager = LinearLayoutManager(activity)

        val repository = ExercisesRepositoryImpl(requireContext())
        viewModel = ViewModelProvider(this, ExercisesViewModelFactory(repository))[ExercisesViewModel::class.java]

        val questions = viewModel.getQuestions()
        binding.rvQuestion.adapter?.let { adapter ->
            if (adapter is ExerciseAdapter) {
                adapter.setItems(questions)
            }
        }

        binding.btnDone.setOnClickListener {
            binding.apply {
                btnDone.visibility = View.GONE
                layoutResultContainer.visibility = View.VISIBLE
                layoutBtnRepeatContainer.visibility = View.VISIBLE
                scrollView.smoothScrollTo(0, 0)
            }
            showScore()

            adapter.setDoneClicked(true)
            adapter.showAnswers()
        }

        binding.btnRepeat.setOnClickListener {
            binding.apply {
                btnDone.visibility = View.VISIBLE
                btnDone.isEnabled = false
                layoutResultContainer.visibility = View.GONE
                layoutBtnRepeatContainer.visibility = View.GONE
            }

            adapter.setDoneClicked(false)
        }
    }

    fun onAnswerSelected(questionId: Int, choice: Int) {
        answers[questionId] = choice
        binding.btnDone.isEnabled = answers.size == adapter.itemCount
    }

    private fun calculateScore(): Pair<Int, Int> {
        var score = 0
        var correctAnswers = 0
        for (i in 0 until adapter.itemCount) {
            val viewHolder = binding.rvQuestion.findViewHolderForAdapterPosition(i) as? ExerciseAdapter.ViewHolder
            val selectedChoiceIndex = viewHolder?.binding?.radioGroupChoices?.checkedRadioButtonId?.let {
                viewHolder.binding.radioGroupChoices.indexOfChild(viewHolder.itemView.findViewById(it))
            }
            if (selectedChoiceIndex != null && selectedChoiceIndex == adapter.items[i].answerIndex) {
                score += 10
                correctAnswers++
            }
        }
        return Pair(score, correctAnswers)
    }

    @SuppressLint("SetTextI18n")
    private fun showScore() {
        val (score, correctAnswers) = calculateScore()
        binding.tvScore.text = score.toString()
        binding.tvCorrectAnswer.text = "Jawaban benar: $correctAnswers"

        if (score == 100) {
            binding.tvMessage.text = "Selamat! Anda menyelesaikan latihan soal dengan hasil yang sempurna!"
            binding.tvScore.textSize = 50F
            binding.tvScore.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_main))
        } else if (score >= 80) {
            binding.tvMessage.text = "Anda telah menyelesaikan latihan soal dengan hasil yang memuaskan!"
            binding.tvScore.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_main))
        } else if (score >= 40) {
            binding.tvMessage.text = "Pelajari kembali materi yang telah disediakan, kamu pasti bisa!"
            binding.tvScore.setTextColor(ContextCompat.getColor(requireContext(), R.color.yellow))
        } else {
            binding.tvMessage.text = "Pelajari kembali materi yang telah disediakan, kamu pasti bisa!"
            binding.tvScore.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_main))
        }
    }
}