package com.example.laboratorium_statistika.ui.data_analysis

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.text.InputFilter
import android.text.TextUtils
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.marginEnd
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.laboratorium_statistika.R
import com.example.laboratorium_statistika.databinding.FragmentDataAnalysisBinding
import com.example.laboratorium_statistika.model.DataAnalysisResult
import com.example.laboratorium_statistika.repository.DataAnalysisRepositoryImpl
import com.example.laboratorium_statistika.repository.ModuleRepositoryImpl
import com.example.laboratorium_statistika.ui.data_analysis.adapter.ResultAdapter
import com.example.laboratorium_statistika.viewmodel.DataAnalysisViewModelFactory
import com.example.laboratorium_statistika.viewmodel.ModuleViewModelFactory
import org.apache.commons.math3.distribution.FDistribution
import org.apache.commons.math3.distribution.NormalDistribution
import org.apache.commons.math3.distribution.TDistribution
import org.apache.commons.math3.stat.inference.TTest
import org.apache.commons.math3.stat.regression.MultipleLinearRegression
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression
import org.apache.commons.math3.stat.regression.SimpleRegression
import java.io.FileOutputStream
import kotlin.math.*

class DataAnalysisFragment : Fragment() {
    private lateinit var binding: FragmentDataAnalysisBinding
    private lateinit var adapter: ResultAdapter
    private lateinit var resultViewModel: ResultViewModel
    private lateinit var dataAnalysisViewModel: DataAnalysisViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var alpha: String? = null
    private val alphaAnalysisList = listOf("Uji Normalitas", "Uji Homogenitas", "Uji Heteroskedastisitas", "Uji Autokorelasi", "One Sample T-Test", "Paired Sample T-Test", "Independent Sample T-Test", "One Way Anova", "Two Way Anova", "Regresi Linear Sederhana", "Regresi Linear Berganda", "Uji Binomial", "Mann Whitney", "Kruskal Wallis")
    private val oneDataUsedList = listOf("Rata-rata", "Median", "Modus", "Range", "Ragam", "Simpangan Baku", "Kuartil", "Uji Normalitas", "Uji Autokorelasi", "Uji Multikolinearitas", "One Sample T-Test")
    private val twoDataUsedList = listOf("Uji Homogenitas", "Uji Heteroskedastisitas", "Paired Sample T-Test", "Independent Sample T-Test", "Regresi Linear Sederhana", "Mann Whitney")
    private val threeDataUsedList = listOf("One Way Anova", "Regresi Linear Berganda", "Uji Binomial", "Kruskal Wallis")
    private val fourDataUsedList = listOf("Two Way Anova")
    private var runCount = 0
    private var dataToSave = ""
    private val Int.dp: Int
        get() = (this * Resources.getSystem().displayMetrics.density).toInt()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDataAnalysisBinding.inflate(layoutInflater, container, false)
        return (binding.root)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val repository = DataAnalysisRepositoryImpl(requireContext())
        resultViewModel = ViewModelProvider(this, DataAnalysisViewModelFactory(repository))[ResultViewModel::class.java]
        dataAnalysisViewModel = ViewModelProvider(this, DataAnalysisViewModelFactory(repository))[DataAnalysisViewModel::class.java]

        binding.backButtonText.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSelectData.setOnClickListener {
            val currentDestination = findNavController().currentDestination?.id
            if (currentDestination == R.id.dataAnalysisFragment) {
                val action = DataAnalysisFragmentDirections.actionDataAnalysisFragmentToDialogFragment()
                findNavController().navigate(action)
            }
        }

        sharedViewModel.analysisText.observe(viewLifecycleOwner) { text ->
            if (text.isNullOrEmpty()) {
                binding.tvSelectData.text = getString(R.string.select_analysis_method)
            } else {
                binding.tvSelectData.text = text
            }

            binding.btnRun.isEnabled = text != getString(R.string.select_analysis_method)

            showAlphaEditText(text)
            showPopulationMeanEditText(text)
            setDescriptionText(text)
            context?.let { setupDeleteButton(it, binding.tableLayout, binding.btnDeleteData) }

            for (i in 0 until binding.tableLayout.childCount) {
                val tableRow = binding.tableLayout.getChildAt(i) as TableRow
                val checkBox = tableRow.getChildAt(0) as CheckBox

                binding.btnDeleteData.isEnabled = checkBox.isChecked
            }

            binding.tvDescription.visibility = View.VISIBLE
        }

        showResultFromViewModel()

        binding.btnAddData.setOnClickListener {
            val id = View.generateViewId()
            val newRow = addData(requireContext(), id)
            binding.tableLayout.addView(newRow)
        }

        val items = listOf("0.05", "0.01", "0.10")
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_spinner_alpha, items)
        arrayAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown_alpha)
        binding.spinnerDataAlpha.adapter = arrayAdapter
        binding.spinnerDataAlpha.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                alpha = items[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        binding.btnRun.setOnClickListener {
            var hasCheckedBox = false
            val selectedCheckBoxes = mutableListOf<CheckBox>()
            runCount++

            activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

            binding.layoutResultEmpty.visibility = View.GONE
            binding.layoutResultContent.visibility = View.VISIBLE

            for (i in 0 until binding.tableLayout.childCount) {
                val tableRow = binding.tableLayout.getChildAt(i) as TableRow
                val checkBox = tableRow.getChildAt(0) as CheckBox

                if (checkBox.isChecked) {
                    selectedCheckBoxes.add(checkBox)
                    hasCheckedBox = true

                    if (selectedCheckBoxes.size == 4 && binding.tvSelectData.text in fourDataUsedList) {
                        val tableRow1 = selectedCheckBoxes[0].parent as TableRow
                        val tableRow2 = selectedCheckBoxes[1].parent as TableRow
                        val tableRow3 = selectedCheckBoxes[2].parent as TableRow
                        val tableRow4 = selectedCheckBoxes[3].parent as TableRow
                        val edtDataName1 = (tableRow1.getChildAt(1) as EditText).text.toString()
                        val edtDataValue1 = (tableRow1.getChildAt(2) as EditText).text.toString().trim()
                        val edtDataName2 = (tableRow2.getChildAt(1) as EditText).text.toString()
                        val edtDataValue2 = (tableRow2.getChildAt(2) as EditText).text.toString().trim()
                        val edtDataName3 = (tableRow3.getChildAt(1) as EditText).text.toString()
                        val edtDataValue3 = (tableRow3.getChildAt(2) as EditText).text.toString().trim()
                        val edtDataName4 = (tableRow4.getChildAt(1) as EditText).text.toString()
                        val edtDataValue4 = (tableRow4.getChildAt(2) as EditText).text.toString().trim()

                        val bindingTvSelectData = binding.tvSelectData.text
                        val tvSelectData = bindingTvSelectData.toString()

                        val pattern = Regex("[^\\d\\s.]+")
                        if (pattern.containsMatchIn(edtDataValue1) || pattern.containsMatchIn(edtDataValue2) || pattern.containsMatchIn(edtDataValue3) || pattern.containsMatchIn(edtDataValue4)) {
                            Toast.makeText(
                                requireActivity(),
                                "Gunakan spasi sebagai pemisah antar angka",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@setOnClickListener
                        } else if (!edtDataValue1.contains(" ") || !edtDataValue2.contains(" ") || !edtDataValue3.contains(" ") || !edtDataValue4.contains(" ")) {
                            Toast.makeText(
                                requireActivity(),
                                "Masukkan setidaknya dua angka yang terpisah dengan spasi",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@setOnClickListener
                        } else {
                            val screenHeight = Resources.getSystem().displayMetrics.heightPixels
                            val layoutParams = binding.layoutResultContainer.layoutParams
                            layoutParams.height = screenHeight / 2
                            binding.tvResult.setCompoundDrawablesWithIntrinsicBounds(
                                0,
                                0,
                                R.drawable.round_keyboard_arrow_down_24,
                                0
                            )

                            when (bindingTvSelectData) {
                                getString(R.string.select_analysis_method) -> Toast.makeText(
                                    requireActivity(),
                                    "Pilih data terlebih dahulu!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                "Two Way Anova" -> alpha?.let { it1 ->
                                    dataAnalysisViewModel.calculateTwoWayAnova(
                                        it1,
                                        edtDataName1,
                                        edtDataValue1,
                                        edtDataName2,
                                        edtDataValue2,
                                        edtDataName3,
                                        edtDataValue3,
                                        edtDataName4,
                                        edtDataValue4,
                                        runCount,
                                        tvSelectData
                                    )
                                }
                            }
                        }
                    } else if (selectedCheckBoxes.size == 3 && binding.tvSelectData.text in threeDataUsedList) {
                        val tableRow1 = selectedCheckBoxes[0].parent as TableRow
                        val tableRow2 = selectedCheckBoxes[1].parent as TableRow
                        val tableRow3 = selectedCheckBoxes[2].parent as TableRow
                        val edtDataName1 = (tableRow1.getChildAt(1) as EditText).text.toString()
                        val edtDataValue1 = (tableRow1.getChildAt(2) as EditText).text.toString().trim()
                        val edtDataName2 = (tableRow2.getChildAt(1) as EditText).text.toString()
                        val edtDataValue2 = (tableRow2.getChildAt(2) as EditText).text.toString().trim()
                        val edtDataName3 = (tableRow3.getChildAt(1) as EditText).text.toString()
                        val edtDataValue3 = (tableRow3.getChildAt(2) as EditText).text.toString().trim()

                        val bindingTvSelectData = binding.tvSelectData.text
                        val tvSelectData = bindingTvSelectData.toString()

                        val pattern = Regex("[^\\d\\s.]+")
                        if (tvSelectData == "Uji Binomial") {
                            if (edtDataValue1.isEmpty() || edtDataValue2.isEmpty() || edtDataValue3.isEmpty() || pattern.containsMatchIn(edtDataValue1) || pattern.containsMatchIn(edtDataValue2) || pattern.containsMatchIn(edtDataValue3)) {
                                Toast.makeText(
                                    requireActivity(),
                                    "Masukkan satu angka terlebih dahulu",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@setOnClickListener
                            } else if (edtDataValue1.contains(" ") || edtDataValue2.contains(" ") || edtDataValue3.contains(" ")){
                                Toast.makeText(
                                    requireActivity(),
                                    "Masukkan hanya satu angka untuk Uji Binomial",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@setOnClickListener
                            } else {
                                val screenHeight = Resources.getSystem().displayMetrics.heightPixels
                                val layoutParams = binding.layoutResultContainer.layoutParams
                                layoutParams.height = screenHeight / 2
                                binding.tvResult.setCompoundDrawablesWithIntrinsicBounds(
                                    0,
                                    0,
                                    R.drawable.round_keyboard_arrow_down_24,
                                    0
                                )

                                alpha?.let { it1 ->
                                    dataAnalysisViewModel.calculateUjiBinomial(
                                        it1,
                                        edtDataName1,
                                        edtDataValue1,
                                        edtDataName2,
                                        edtDataValue2,
                                        edtDataName3,
                                        edtDataValue3,
                                        runCount,
                                        tvSelectData
                                    )
                                }
                            }
                        } else if (pattern.containsMatchIn(edtDataValue1) || pattern.containsMatchIn(edtDataValue2) || pattern.containsMatchIn(edtDataValue3)) {
                            Toast.makeText(
                                requireActivity(),
                                "Gunakan spasi sebagai pemisah antar angka",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@setOnClickListener
                        } else if (!edtDataValue1.contains(" ") || !edtDataValue2.contains(" ") || !edtDataValue3.contains(" ")) {
                            Toast.makeText(
                                requireActivity(),
                                "Masukkan setidaknya dua angka yang terpisah dengan spasi",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@setOnClickListener
                        } else {
                            val screenHeight = Resources.getSystem().displayMetrics.heightPixels
                            val layoutParams = binding.layoutResultContainer.layoutParams
                            layoutParams.height = screenHeight / 2
                            binding.tvResult.setCompoundDrawablesWithIntrinsicBounds(
                                0,
                                0,
                                R.drawable.round_keyboard_arrow_down_24,
                                0
                            )

                            when (bindingTvSelectData) {
                                getString(R.string.select_analysis_method) -> Toast.makeText(
                                    requireActivity(),
                                    "Pilih data terlebih dahulu!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                "One Way Anova" -> {
                                    alpha?.let { it1 ->
                                        dataAnalysisViewModel.calculateOneWayAnova(
                                            it1,
                                            edtDataName1,
                                            edtDataValue1,
                                            edtDataName2,
                                            edtDataValue2,
                                            edtDataName3,
                                            edtDataValue3,
                                            runCount,
                                            tvSelectData
                                        )
                                    }
                                }
                                "Regresi Linear Berganda" -> {
                                    alpha?.let { it1 ->
                                        dataAnalysisViewModel.calculateRegresiLinearBerganda(
                                            it1,
                                            edtDataName1,
                                            edtDataValue1,
                                            edtDataName2,
                                            edtDataValue2,
                                            edtDataName3,
                                            edtDataValue3,
                                            runCount,
                                            tvSelectData
                                        )
                                    }
                                }
                                "Kruskal Wallis" -> {
                                    alpha?.let { it1 ->
                                        dataAnalysisViewModel.calculateKruskalWallis(
                                            it1,
                                            edtDataName1,
                                            edtDataValue1,
                                            edtDataName2,
                                            edtDataValue2,
                                            edtDataName3,
                                            edtDataValue3,
                                            runCount,
                                            tvSelectData
                                        )
                                    }
                                }
                            }
                        }
                    } else if (selectedCheckBoxes.size == 2 && binding.tvSelectData.text in twoDataUsedList) {
                        val tableRow1 = selectedCheckBoxes[0].parent as TableRow
                        val tableRow2 = selectedCheckBoxes[1].parent as TableRow
                        val edtDataName1 = (tableRow1.getChildAt(1) as EditText).text.toString()
                        val edtDataValue1 =
                            (tableRow1.getChildAt(2) as EditText).text.toString().trim()
                        val edtDataName2 = (tableRow2.getChildAt(1) as EditText).text.toString()
                        val edtDataValue2 =
                            (tableRow2.getChildAt(2) as EditText).text.toString().trim()

                        val bindingTvSelectData = binding.tvSelectData.text
                        val tvSelectData = bindingTvSelectData.toString()

                        val pattern = Regex("[^\\d\\s.]+")
                        if (pattern.containsMatchIn(edtDataValue1) || pattern.containsMatchIn(edtDataValue2)) {
                            Toast.makeText(
                                requireActivity(),
                                "Gunakan spasi sebagai pemisah antar angka",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@setOnClickListener
                        } else if (!edtDataValue1.contains(" ") || !edtDataValue2.contains(" ")) {
                            Toast.makeText(
                                requireActivity(),
                                "Masukkan setidaknya dua angka yang terpisah dengan spasi",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@setOnClickListener
                        } else {
                            val screenHeight = Resources.getSystem().displayMetrics.heightPixels
                            val layoutParams = binding.layoutResultContainer.layoutParams
                            layoutParams.height = screenHeight / 2
                            binding.tvResult.setCompoundDrawablesWithIntrinsicBounds(
                                0,
                                0,
                                R.drawable.round_keyboard_arrow_down_24,
                                0
                            )

                            when (bindingTvSelectData) {
                                getString(R.string.select_analysis_method) -> Toast.makeText(
                                    requireActivity(),
                                    "Pilih data terlebih dahulu!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                "Uji Homogenitas" -> alpha?.let { it1 ->
                                    dataAnalysisViewModel.calculateUjiHomogenitas(
                                        it1, edtDataName1, edtDataValue1, edtDataName2, edtDataValue2, runCount, tvSelectData)
                                }
                                "Uji Heteroskedastisitas" -> alpha?.let { it1 ->
                                    dataAnalysisViewModel.calculateUjiHeteroskedastisitas(
                                        it1, edtDataName1, edtDataValue1, edtDataName2, edtDataValue2, runCount, tvSelectData)
                                }
                                "Paired Sample T-Test" -> alpha?.let { it1 ->
                                    dataAnalysisViewModel.calculatePairedSampleTTest(
                                        it1, edtDataName1, edtDataValue1, edtDataName2, edtDataValue2, runCount, tvSelectData)
                                }
                                "Independent Sample T-Test" -> alpha?.let { it1 ->
                                    dataAnalysisViewModel.calculateIndependentSampleTTest(
                                        it1, edtDataName1, edtDataValue1, edtDataName2, edtDataValue2, runCount, tvSelectData)
                                }
                                "Regresi Linear Sederhana" -> alpha?.let { it1 ->
                                    dataAnalysisViewModel.calculateRegresiLinearSederhana(
                                        it1,
                                        edtDataName1,
                                        edtDataValue1,
                                        edtDataName2,
                                        edtDataValue2,
                                        runCount,
                                        tvSelectData
                                    )
                                }
                                "Mann Whitney" -> alpha?.let { it1 ->
                                    dataAnalysisViewModel.calculateMannWhitney(
                                        it1, edtDataName1, edtDataValue1, edtDataName2, edtDataValue2, runCount, tvSelectData)
                                }
                            }
                        }
                    } else if (selectedCheckBoxes.size == 1 && binding.tvSelectData.text in oneDataUsedList) {
                        val tableRowSingle = selectedCheckBoxes[0].parent as TableRow
                        val edtDataName = (tableRowSingle.getChildAt(1) as EditText).text.toString()
                        val edtDataValue =
                            (tableRowSingle.getChildAt(2) as EditText).text.toString().trim()
                        val edtDataPopulationMean = binding.edtDataPopulationMean.text.toString()

                        val bindingTvSelectData = binding.tvSelectData.text
                        val tvSelectData = bindingTvSelectData.toString()

                        val pattern = Regex("[^\\d\\s.]+")
                        if (pattern.containsMatchIn(edtDataValue)) {
                            Toast.makeText(
                                requireActivity(),
                                "Gunakan spasi sebagai pemisah antar angka",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@setOnClickListener
                        } else if (!edtDataValue.contains(" ")) {
                            Toast.makeText(
                                requireActivity(),
                                "Masukkan setidaknya dua angka yang terpisah dengan spasi",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@setOnClickListener
                        } else {
                            val screenHeight = Resources.getSystem().displayMetrics.heightPixels
                            val layoutParams = binding.layoutResultContainer.layoutParams
                            layoutParams.height = screenHeight / 2
                            binding.tvResult.setCompoundDrawablesWithIntrinsicBounds(
                                0,
                                0,
                                R.drawable.round_keyboard_arrow_down_24,
                                0
                            )

                            when (bindingTvSelectData) {
                                getString(R.string.select_analysis_method) -> Toast.makeText(
                                    requireActivity(),
                                    "Pilih data terlebih dahulu!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                "Rata-rata" -> dataAnalysisViewModel.calculateDeskriptifData("Rata-rata", edtDataName, edtDataValue, runCount, tvSelectData)
                                "Median" -> dataAnalysisViewModel.calculateDeskriptifData("Median", edtDataName, edtDataValue, runCount, tvSelectData)
                                "Modus" -> dataAnalysisViewModel.calculateDeskriptifData("Modus", edtDataName, edtDataValue, runCount, tvSelectData)
                                "Range" -> dataAnalysisViewModel.calculateDeskriptifData("Range", edtDataName, edtDataValue, runCount, tvSelectData)
                                "Ragam" -> dataAnalysisViewModel.calculateDeskriptifData("Ragam", edtDataName, edtDataValue, runCount, tvSelectData)
                                "Simpangan Baku" -> dataAnalysisViewModel.calculateDeskriptifData("Simpangan Baku", edtDataName, edtDataValue, runCount, tvSelectData)
                                "Kuartil" -> dataAnalysisViewModel.calculateDeskriptifData("Kuartil", edtDataName, edtDataValue, runCount, tvSelectData)

                                "Uji Normalitas" -> alpha?.let { it1 ->
                                    dataAnalysisViewModel.calculateUjiNormalitas(
                                        it1, edtDataName, edtDataValue, runCount, tvSelectData)
                                }
                                "Uji Autokorelasi" -> alpha?.let { it1 ->
                                    dataAnalysisViewModel.calculateAutokorelasi(
                                        it1, edtDataName, edtDataValue, runCount, tvSelectData)
                                }
                                "Uji Multikolinieritas" -> dataAnalysisViewModel.calculateUjiMultikolinieritas(edtDataName, edtDataValue, runCount, tvSelectData)
                                "One Sample T-Test" -> alpha?.let { it1 ->
                                    dataAnalysisViewModel.calculateOneSampleTTest(
                                        it1, edtDataName, edtDataValue, edtDataPopulationMean, runCount, tvSelectData)
                                }
                            }
                        }
                    }
                }
            }

            if (!hasCheckedBox) {
                Toast.makeText(requireActivity(), "Harap pilih kotak centang data terlebih dahulu", Toast.LENGTH_SHORT).show()
            } else if (binding.tvSelectData.text in twoDataUsedList  && selectedCheckBoxes.size != 2) {
                Toast.makeText(requireActivity(), "Harap pilih 2 kotak centang data terlebih dahulu", Toast.LENGTH_SHORT).show()
            } else if (binding.tvSelectData.text in threeDataUsedList  && selectedCheckBoxes.size != 3) {
                Toast.makeText(requireActivity(), "Harap pilih 3 kotak centang data terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }

        var resultLayoutHeight = 0
        binding.layoutResult.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.layoutResult.viewTreeObserver.removeOnGlobalLayoutListener(this)
                resultLayoutHeight = binding.layoutResult.height
            }
        })

        binding.tvResult.post {
            val layoutParams = binding.layoutResultContainer.layoutParams
            layoutParams.height = binding.tvResult.height
            binding.layoutResultContainer.layoutParams = layoutParams
        }

        binding.tvResult.setOnClickListener {
            val screenHeight = Resources.getSystem().displayMetrics.heightPixels
            val layoutParams = binding.layoutResultContainer.layoutParams
            val measureSpec = View.MeasureSpec.makeMeasureSpec(screenHeight, View.MeasureSpec.AT_MOST)
            binding.layoutResult.measure(measureSpec, measureSpec)

            val animator = if (layoutParams.height == binding.tvResult.height) {
                ValueAnimator.ofInt(binding.tvResult.height, binding.layoutResult.measuredHeight)
            } else {
                ValueAnimator.ofInt(binding.layoutResultContainer.measuredHeight, binding.tvResult.height)
            }

            animator.addUpdateListener {
                val value = it.animatedValue as Int
                layoutParams.height = value
                binding.layoutResultContainer.layoutParams = layoutParams
                binding.layoutResultContainer.invalidate() // <-- call invalidate() instead of requestLayout()
            }

            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    if (layoutParams.height == binding.tvResult.height) {
                        binding.tvResult.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.round_keyboard_arrow_up_24, 0)
                    } else {
                        binding.tvResult.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.round_keyboard_arrow_down_24, 0)
                    }
                }
            })

            animator.duration = 300
            animator.start()
        }

        binding.btnDeleteResult.setOnClickListener {
            runCount = 0
            deleteAllDataAnalysisResultsFromViewModel()
            binding.layoutResultContent.visibility = View.GONE
            binding.layoutResultEmpty.visibility = View.VISIBLE
        }

        binding.btnSaveResult.setOnClickListener {
            val results = resultViewModel.getResultList().value
            if (results != null) {
                dataToSave = ""
                for (result in results) {
                    val resultTitle = result.resultTitle
                    val resultData = result.resultData
                    val descriptiveTitle = result.descriptiveTitle
                    val descriptiveContent = result.descriptiveContent
                    val secondDescriptiveTitle = result.secondDescriptiveTitle
                    val secondDescriptiveContent = result.secondDescriptiveContent
                    val thirdDescriptiveTitle = result.thirdDescriptiveTitle
                    val thirdDescriptiveContent = result.thirdDescriptiveContent

                    val testValuesContent = result.testValuesContent
                    val resultConclusion = result.resultConclusion

                    dataToSave += "$resultTitle\n"
                    dataToSave += "$resultData\n"
                    dataToSave += "$descriptiveTitle\n"
                    dataToSave += "$descriptiveContent\n"
                    when (result.amountOfData) {
                        3 -> {
                            dataToSave += "$secondDescriptiveTitle\n"
                            dataToSave += "$secondDescriptiveContent\n"
                        }
                        2 -> {
                            dataToSave += "$thirdDescriptiveTitle\n"
                            dataToSave += "$thirdDescriptiveContent\n"
                        }
                    }
                    if (result.hideTestValues != true) {
                        dataToSave += "Test value:\n$testValuesContent\n"
                    }
                    if (resultConclusion != null) {
                        dataToSave += "$resultConclusion\n"
                    }
                    dataToSave += "\n--------------------------------------------\n"
                }

                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TITLE, "my_analysis.txt")
                }
                startActivityForResult(intent, 2)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        sharedViewModel.analysisText.value = null
        binding.btnRun.isEnabled = false
        binding.tvDescription.visibility = View.GONE
        binding.spinnerDataAlpha.visibility = View.GONE
    }

    private fun calculateTwoWayAnova(edtDataAlpha: String, edtDataName1: String, edtDataValue1: String, edtDataName2: String, edtDataValue2: String, edtDataName3: String, edtDataValue3: String) {
        // Mengambil nilai input dari EditText
        val dataValue1 = edtDataValue1.split(" ").map { it.toDouble() }.toDoubleArray()
        val dataValue2 = edtDataValue2.split(" ").map { it.toDouble() }.toDoubleArray()
        val dataValue3 = edtDataValue3.split(" ").map { it.toDouble() }.toDoubleArray()
        val alpha = edtDataAlpha.toDoubleOrNull()

        if (alpha == null) {
            runCount
            Toast.makeText(requireActivity(), "Masukkan nilai signifikansi", Toast.LENGTH_SHORT)
                .show()
            return
        }
    }

    private fun addData(context: Context, id: Int): TableRow {
        // create TableRow
        val tableRow = TableRow(context)
        val tableRowLayoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.WRAP_CONTENT
        )
        tableRow.layoutParams = tableRowLayoutParams
        tableRow.setPadding(0, 0, 0, 12.dp)
        tableRow.gravity = Gravity.START or Gravity.CENTER

        // create CheckBox
        val checkBox = CheckBox(context)
        checkBox.id = id
        checkBox.layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.WRAP_CONTENT,
            TableRow.LayoutParams.WRAP_CONTENT
        )
        checkBox.setButtonDrawable(android.R.color.transparent)
        checkBox.setBackgroundResource(R.drawable.custom_checkbox)
        (checkBox.layoutParams as TableRow.LayoutParams).column = 1
        tableRow.addView(checkBox)

        // create EditText for name
        val nameEditText = EditText(context)
        nameEditText.id = View.generateViewId() // generate a unique id
        val nameEditTextLayoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.WRAP_CONTENT,
            TableRow.LayoutParams.WRAP_CONTENT
        )
        nameEditTextLayoutParams.marginStart = 16.dp
        nameEditTextLayoutParams.marginEnd = 6.dp
        nameEditText.layoutParams = nameEditTextLayoutParams
        (nameEditText.layoutParams as TableRow.LayoutParams).column = 2
        nameEditText.marginEnd
        nameEditText.setPadding(12.dp, 12.dp, 12.dp, 12.dp)
        nameEditText.setBackgroundResource(R.drawable.custom_round_background)
        nameEditText.hint = "Name"
        nameEditText.setTextColor(ContextCompat.getColor(context, R.color.black))
        nameEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        nameEditText.typeface = ResourcesCompat.getFont(context, R.font.inter)
        nameEditText.ellipsize = TextUtils.TruncateAt.END
        tableRow.addView(nameEditText)

        // create EditText for data value
        val valueEditText = EditText(context)
        valueEditText.id = View.generateViewId() // generate a unique id
        valueEditText.layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.WRAP_CONTENT,
            TableRow.LayoutParams.MATCH_PARENT
        )
        (valueEditText.layoutParams as TableRow.LayoutParams).column = 3
        valueEditText.setPadding(12.dp, 0, 0, 0)
        valueEditText.setBackgroundResource(R.drawable.custom_round_background)
        valueEditText.hint = "Data"
        valueEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.toFloat())
        valueEditText.typeface = ResourcesCompat.getFont(context, R.font.inter)
        tableRow.addView(valueEditText)

        return tableRow
    }

    private fun deleteCheckedRows(tableLayout: TableLayout) {
        val rowsToDelete = mutableListOf<TableRow>()
        for (i in 0 until tableLayout.childCount) {
            val tableRow = tableLayout.getChildAt(i) as? TableRow ?: continue
            val checkBox = tableRow.getChildAt(0) as? CheckBox ?: continue
            if (checkBox.isChecked) {
                rowsToDelete.add(tableRow)
            }
        }
        rowsToDelete.forEach {
            tableLayout.removeView(it)
        }
    }

    private fun setupDeleteButton(context: Context, tableLayout: TableLayout, deleteButton: Button) {
        // Disable the delete button by default
        deleteButton.isEnabled = false

        // Add an OnCheckedChangeListener to all the CheckBoxes in the TableLayout
        for (i in 0 until tableLayout.childCount) {
            val tableRow = tableLayout.getChildAt(i) as? TableRow ?: continue
            val checkBox = tableRow.getChildAt(0) as? CheckBox ?: continue

            checkBox.setOnCheckedChangeListener { _, _ ->
                // Check if any CheckBox is checked
                for (j in 0 until tableLayout.childCount) {
                    val row = tableLayout.getChildAt(j) as? TableRow ?: continue
                    val cb = row.getChildAt(0) as? CheckBox ?: continue
                    if (cb.isChecked) {
                        deleteButton.isEnabled = true
                        return@setOnCheckedChangeListener
                    }
                }
                // Disable the delete button if no CheckBox is checked
                deleteButton.isEnabled = false
            }
        }

        // Set the click listener for the delete button
        deleteButton.setOnClickListener {
            deleteCheckedRows(tableLayout)
        }
    }

    private fun showAlphaEditText(text: String?) {
        binding.spinnerDataAlpha.visibility =
            if (text in alphaAnalysisList)
                View.VISIBLE
            else
                View.GONE
    }

    private fun showPopulationMeanEditText(text: String?) {
        binding.edtDataPopulationMean.visibility =
            if (text == "One Sample T-Test")
                View.VISIBLE
            else
                View.GONE
    }

    private fun setDescriptionText(text: String?) {
        binding.tvDescription.text =
            when (text) {
                in twoDataUsedList -> "Metode ini menggunakan 2 data."
                in threeDataUsedList -> {
                    if (text == "Uji Binomial") {
                        "Metode ini menggunakan 3 data. Dimana setiap data hanya berupa 1 angka untuk nilai Successes, Trials, dan Expectation."
                    } else {
                        "Metode ini menggunakan 3 data."
                    }
                }
                in fourDataUsedList -> "Metode ini menggunakan 4 data.\n2 faktor, dan 2 data di setiap faktor. Urutkan data seperti ini:\n- Faktor 1, data 1\n- Faktor 1, data 2\n- Faktor 2, data 1\n- Faktor 2, data 2"
                else -> "Metode ini menggunakan 1 data."
            }
    }

    private fun showResultFromViewModel() {
        adapter = ResultAdapter()
        binding.rvResult.adapter = adapter
        binding.rvResult.layoutManager = LinearLayoutManager(activity)

        resultViewModel.getResultList().observe(viewLifecycleOwner) { results ->
            binding.rvResult.adapter?.let { adapter ->
                if (adapter is ResultAdapter) {
                    adapter.setItems(results)
                }
            }
        }
    }

    private fun deleteAllDataAnalysisResultsFromViewModel() {
        resultViewModel.clearResults()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            if (uri != null) {
                requireContext().contentResolver.openFileDescriptor(uri, "w")?.use {
                    FileOutputStream(it.fileDescriptor).use { fileOutputStream ->
                        fileOutputStream.write(dataToSave.toByteArray())
                    }
                }
            }
        }
    }
}