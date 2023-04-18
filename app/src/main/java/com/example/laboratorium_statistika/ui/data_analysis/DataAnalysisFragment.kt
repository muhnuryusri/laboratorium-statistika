package com.example.laboratorium_statistika.ui.data_analysis

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.TypedValue
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.marginEnd
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.laboratorium_statistika.R
import com.example.laboratorium_statistika.databinding.FragmentDataAnalysisBinding
import com.example.laboratorium_statistika.model.DataAnalysisResult
import com.example.laboratorium_statistika.repository.ModuleRepositoryImpl
import com.example.laboratorium_statistika.ui.data_analysis.adapter.ResultAdapter
import com.example.laboratorium_statistika.ui.module.adapter.ModuleTabAdapter
import com.example.laboratorium_statistika.ui.module.tab.ModuleTabViewModel
import com.example.laboratorium_statistika.viewmodel.ModuleViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.apache.commons.math3.distribution.FDistribution
import org.apache.commons.math3.distribution.TDistribution
import org.apache.commons.math3.special.Gamma.gamma
import org.apache.commons.math3.stat.inference.TTest
import org.apache.commons.math3.stat.regression.SimpleRegression
import kotlin.math.*

class DataAnalysisFragment : Fragment() {
    private lateinit var binding: FragmentDataAnalysisBinding
    private lateinit var adapter: ResultAdapter
    private lateinit var viewModel: ResultViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var hideTestValues = false
    private var runCount = 0
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
        binding.layoutDescriptionContainer.visibility = View.GONE
        binding.btnRun.isEnabled = false

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

        sharedViewModel.analysisText.observe(viewLifecycleOwner, Observer { text ->
            if (text.isNullOrEmpty()) {
                binding.tvSelectData.text = getString(R.string.select_analysis_method)
            } else {
                binding.apply {
                    tvSelectData.text = text
                    tvSelectData.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                }
            }

            binding.btnRun.isEnabled = text != getString(R.string.select_analysis_method)

            // Masih perlu di perbaiki
            if (text == "Uji Normalitas" || text == "Uji Homogenitas" || text == "Uji Heterokedasitas" || text == "One Sample T-Test" || text == "Paired Sample T-Test" || text == "Independent Sample T-Test") {
                binding.edtDataAlpha.visibility = View.VISIBLE
            } else {
                binding.edtDataAlpha.visibility = View.GONE
            }

            // Masih perlu di perbaiki
            if (text == "One Sample T-Test") {
                binding.edtDataPopulationMean.visibility = View.VISIBLE
            } else {
                binding.edtDataPopulationMean.visibility = View.GONE
            }

            binding.layoutDescriptionContainer.visibility = View.VISIBLE
            // Masih perlu di perbaiki
            if (text == "Uji Homogenitas") {
                binding.tvDescription.text = "2 data."
            } else {
                binding.tvDescription.text = "1 data."
            }
        })

        adapter = ResultAdapter(hideTestValues)
        binding.rvResult.adapter = adapter
        binding.rvResult.layoutManager = LinearLayoutManager(activity)

        binding.btnAddData.setOnClickListener {
            val id = View.generateViewId()
            val newRow = addData(requireContext(), id)
            binding.tableLayout.addView(newRow)
        }

        binding.btnRun.setOnClickListener {
            var hasCheckedBox = false
            val selectedCheckBoxes = mutableListOf<CheckBox>()

            binding.layoutResultEmpty.visibility = View.GONE
            binding.layoutResultContent.visibility = View.VISIBLE

            for (i in 0 until binding.tableLayout.childCount) {
                val tableRow = binding.tableLayout.getChildAt(i) as TableRow
                val checkBox = tableRow.getChildAt(0) as CheckBox

                if (checkBox.isChecked) {
                    selectedCheckBoxes.add(checkBox)
                    hasCheckedBox = true

                    if (selectedCheckBoxes.size == 2) {
                        val tableRow1 = selectedCheckBoxes[0].parent as TableRow
                        val tableRow2 = selectedCheckBoxes[1].parent as TableRow
                        val edtDataName1 = (tableRow1.getChildAt(1) as EditText).text.toString()
                        val edtDataValue1 = (tableRow1.getChildAt(2) as EditText).text.toString().trim()
                        val edtDataName2 = (tableRow2.getChildAt(1) as EditText).text.toString()
                        val edtDataValue2 = (tableRow2.getChildAt(2) as EditText).text.toString().trim()
                        val edtDataAlpha = binding.edtDataAlpha.text.toString()

                        val screenHeight = Resources.getSystem().displayMetrics.heightPixels
                        val layoutParams = binding.layoutResultContainer.layoutParams
                        layoutParams.height = screenHeight / 2
                        binding.tvResult.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.round_keyboard_arrow_down_24, 0)

                        when (binding.tvSelectData.text) {
                            getString(R.string.select_analysis_method) -> Toast.makeText(requireActivity(), "Pilih data terlebih dahulu!", Toast.LENGTH_SHORT).show()


                            "Uji Homogenitas" -> calculateLeveneTest(edtDataAlpha, edtDataName1, edtDataValue1, edtDataName2, edtDataValue2)
                            "Paired Sample T-Test" -> calculatePairedTTest(edtDataAlpha, edtDataName1, edtDataValue1, edtDataName2, edtDataValue2)
                            "Independent Sample T-Test" -> calculateIndependentSampleTTest(edtDataAlpha, edtDataName1, edtDataValue1, edtDataName2, edtDataValue2)
                        }
                    } else if (selectedCheckBoxes.size == 1) {
                        val tableRowSingle = selectedCheckBoxes[0].parent as TableRow
                        val edtDataName = (tableRowSingle.getChildAt(1) as EditText).text.toString()
                        val edtDataValue = (tableRowSingle.getChildAt(2) as EditText).text.toString().trim()
                        val edtDataAlpha = binding.edtDataAlpha.text.toString()
                        val edtDataPopulationMean = binding.edtDataPopulationMean.text.toString()

                        val pattern = Regex("[^\\d\\s.]+")
                        if (pattern.containsMatchIn(edtDataValue)) {
                            Toast.makeText(requireActivity(), "Gunakan spasi sebagai pemisah antar angka", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }

                        if (!edtDataValue.contains(" ")) {
                            Toast.makeText(requireActivity(), "Masukkan setidaknya dua angka yang terpisah dengan spasi", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }

                        val screenHeight = Resources.getSystem().displayMetrics.heightPixels
                        val layoutParams = binding.layoutResultContainer.layoutParams
                        layoutParams.height = screenHeight / 2
                        binding.tvResult.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.round_keyboard_arrow_down_24, 0)

                        when (binding.tvSelectData.text) {
                            getString(R.string.select_analysis_method) -> Toast.makeText(requireActivity(), "Pilih data terlebih dahulu!", Toast.LENGTH_SHORT).show()

                            "Rata-rata" -> calculateDeskriptifData("Rata-rata", edtDataName, edtDataValue)
                            "Median" -> calculateDeskriptifData("Median", edtDataName, edtDataValue)
                            "Modus" -> calculateDeskriptifData("Modus", edtDataName, edtDataValue)
                            "Range" -> calculateDeskriptifData("Range", edtDataName, edtDataValue)
                            "Ragam" -> calculateDeskriptifData("Ragam", edtDataName, edtDataValue)
                            "Simpangan Baku" -> calculateDeskriptifData("Simpangan Baku", edtDataName, edtDataValue)
                            "Kuartil" -> calculateDeskriptifData("Kuartil", edtDataName, edtDataValue)

                            "Uji Normalitas" -> calculateKolmogorovSmirnovTest(edtDataAlpha, edtDataName, edtDataValue)
                            "Uji Heterokedasitas" -> calculateGlesjerTest(edtDataAlpha, edtDataName, edtDataValue)
                            "Uji Multikolinieritas" -> calculateVIF(edtDataName, edtDataValue)
                            "One Sample T-Test" -> calculateOneSampleTTest(edtDataAlpha, edtDataPopulationMean, edtDataName, edtDataValue)
                        }
                    }
                }
            }

            if (!hasCheckedBox) {
                Toast.makeText(requireActivity(), "Harap pilih setidaknya satu kotak centang", Toast.LENGTH_SHORT).show()
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
    }

    override fun onResume() {
        super.onResume()

        sharedViewModel.analysisText.value = null
        binding.btnRun.isEnabled = false
        binding.layoutDescriptionContainer.visibility = View.GONE
        binding.edtDataAlpha.visibility = View.GONE
    }

    @SuppressLint("SetTextI18n")
    private fun calculateDeskriptifData(
        analysis: String,
        edtDataName: String,
        edtDataValue: String
    ) {
        runCount++
        val dataValue = edtDataValue.split(" ").map { it.toDouble() }.toDoubleArray()

        // Mean
        val mean = dataValue.average()

        // Median
        val sortedNumbers = dataValue.sorted()
        val middle = dataValue.size / 2
        val median = if (dataValue.size % 2 == 0) {
            (sortedNumbers[middle - 1] + sortedNumbers[middle]) / 2.0
        } else {
            sortedNumbers[middle]
        }

        // Modus
        val modus = dataValue.toTypedArray().groupBy { it }.maxByOrNull { it.value.size }?.key

        // Menghitung range dari data
        val range = dataValue.maxOrNull()!! - dataValue.minOrNull()!!

        // Menghitung deviation, variance/ragam, dan standard deviation dari data
        val deviation = sqrt(dataValue.sumOf { (it - dataValue.average()).pow(2.0) } / (dataValue.size))
        val variance = dataValue.sumOf { (it - dataValue.average()).pow(2.0) } / (dataValue.size - 1)
        val standardDeviation = sqrt(variance)

        // Menghitung quartile 1, 2, dan 3 serta interquartile range (IQR) dari data
        val quartile1 = dataValue.sorted()[dataValue.size / 4]
        val quartile2 = dataValue.sorted()[dataValue.size / 2]
        val quartile3 = dataValue.sorted()[3 * dataValue.size / 4]
        val iqr = quartile3 - quartile1

        val result = DataAnalysisResult(
            resultTitle = "Run #$runCount - ${binding.tvSelectData.text}",
            resultData = "Data yang digunakan: $edtDataName",
            descriptiveTitle = "$edtDataName descriptive:",
            descriptiveContent = "n: ${dataValue.size}; " + when (analysis) {
                "Rata-rata" -> "Mean: $mean"
                "Median" -> "Median: $median"
                "Modus" -> "Modus: $modus"
                "Range" -> "Range: $range"
                "Ragam" -> "Ragam: $variance"
                "Simpangan Baku" -> "Simpangan Baku Populasi (Deviation): $deviation\nSimpangan Baku Sample (Standard Deviation): $standardDeviation"
                "Kuartil" -> "Kuartil Bawah (Q1)\t\t\t\t\t\t: $quartile1\nKuartil Tengah (Q2)\t\t\t\t\t: $quartile2\nKuartil Atas (Q3)\t\t\t\t\t\t\t: $quartile3\nInterkuartil Range (IQR)\t: $iqr"
                else -> ""
            }
        )

        val repository = ModuleRepositoryImpl(requireContext())
        viewModel = ViewModelProvider(this, ModuleViewModelFactory(repository))[ResultViewModel::class.java]
        viewModel.addResult(result)
        viewModel.getResultList().observe(viewLifecycleOwner) { results ->
            binding.rvResult.adapter?.let { adapter ->
                if (adapter is ResultAdapter) {
                    adapter.updateHideTestValues(true)
                    adapter.setItems(results)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun calculateKolmogorovSmirnovTest(edtDataAlpha: String, edtDataName: String, edtDataValue: String) {
        runCount++
        val dataValue = edtDataValue.split(" ").map { it.toDouble() }.toDoubleArray()
        val alpha = edtDataAlpha.toDoubleOrNull()

        if (alpha == null) {
            runCount
            Toast.makeText(requireActivity(), "Masukkan nilai alpha", Toast.LENGTH_SHORT).show()
            return
        }

        // Mengurutkan array data
        dataValue.sort()

        // Menghitung jumlah data dan nilai rata-rata
        val n = dataValue.size
        val mean = dataValue.average()

        // Menghitung standard deviation dari data
        val deviation = sqrt(dataValue.sumOf { (it - dataValue.average()).pow(2.0) } / (dataValue.size))

        // Menghitung nilai CDF dari setiap data point
        val cdf = DoubleArray(n)
        for (i in dataValue.indices) {
            cdf[i] = (i + 1.0) / n
        }

        // Menghitung deviasi maksimum antara CDF dan PDF teoretis (normal)
        var dPlus = Double.NEGATIVE_INFINITY
        var dMinus = Double.NEGATIVE_INFINITY
        for (i in dataValue.indices) {
            val dPlusCurr = cdf[i] - normalCDF((dataValue[i] - mean) / sqrt(n.toDouble()))
            val dMinusCurr = normalCDF((dataValue[i] - mean) / sqrt(n.toDouble())) - (i.toDouble() / n)
            if (dPlusCurr > dPlus) {
                dPlus = dPlusCurr
            }
            if (dMinusCurr > dMinus) {
                dMinus = dMinusCurr
            }
        }

        // Menghitung nilai D yang terbesar dari deviasi maksimum
        val dValue = max(dPlus, dMinus)

        // Menentukan nilai kritis dengan alpha = 0.05 dan menghitung apakah D melebihi nilai kritis
        val pValue = sqrt(-0.5 * ln(alpha / 2)) / sqrt(n.toDouble())

        val result = DataAnalysisResult(
            resultTitle = "Run #$runCount - ${binding.tvSelectData.text}",
            resultData = "Data yang digunakan: $edtDataName",
            descriptiveTitle = "$edtDataName descriptive:",
            descriptiveContent = "n: $n; Mean : $mean; SD: $deviation",
            testValuesContent = "D-Value: $dValue\np-Value: $pValue\n",
            resultConclusion = if (dValue <= pValue) {
                "Hipotesis nol diterima.\nData terdistribusi dengan normal (α = $alpha)"
            } else {
                "Hipotesis nol ditolak.\nData tidak terdistribusi dengan normal (α = $alpha)"
            }
        )

        val repository = ModuleRepositoryImpl(requireContext())
        viewModel = ViewModelProvider(this, ModuleViewModelFactory(repository))[ResultViewModel::class.java]
        viewModel.addResult(result)
        viewModel.getResultList().observe(viewLifecycleOwner) { results ->
            binding.rvResult.adapter?.let { adapter ->
                if (adapter is ResultAdapter) {
                    adapter.updateHideTestValues(false)
                    adapter.setItems(results)
                }
            }
        }
    }


    // Fungsi untuk menghitung CDF teoretis dari distribusi normal
    fun normalCDF(x: Double): Double {
        return 0.5 * (1.0 + erf(x / sqrt(2.0)))
    }

    // Fungsi error function (erf) dari Apache Commons Math library
    fun erf(x: Double): Double {
        val t = 1.0 / (1.0 + 0.5 * abs(x))
        val tau = t * exp(-x * x - 1.26551223 + t * (1.00002368 + t * (0.37409196 +
                t * (0.09678418 + t * (-0.18628806 + t * (0.27886807 +
                t * (-1.13520398 + t * (1.48851587 + t * (-0.82215223 +
                t * 0.17087277)))))))))
        return if (x >= 0) 1 - tau else tau - 1
    }

    @SuppressLint("SetTextI18n")
    private fun calculateLeveneTest(edtDataAlpha: String, edtDataName1: String, edtDataValue1: String, edtDataName2: String, edtDataValue2: String) {
        runCount++
        val dataValue1 = edtDataValue1.split(" ").map { it.toDouble() }.toDoubleArray()
        val dataValue2 = edtDataValue2.split(" ").map { it.toDouble() }.toDoubleArray()
        val alpha = edtDataAlpha.toDoubleOrNull()

        if (alpha == null) {
            runCount
            Toast.makeText(requireActivity(), "Masukkan nilai alpha", Toast.LENGTH_SHORT).show()
            return
        }

        // Combine two input data arrays into a list of lists
        val groupList = listOf(dataValue1.toList(), dataValue2.toList())

        // Calculate the number of values in each group and the overall number of values
        val m = dataValue1.size
        val f = dataValue2.size
        val n = m + f

        // Calculate overall mean
        val overallMean = (dataValue1 + dataValue2).average()

        // Calculate numerator and denominator for test statistic
        var numerator = 0.0
        var denominator = 0.0

        for (i in 0 until 2) {
            val groupMean = groupList[i].average()
            val diff = groupList[i].map { it - groupMean }
            val groupN = groupList[i].size
            numerator += groupN * ((groupMean - overallMean).pow(2))
            val groupDenominator = (diff.map { it.pow(2) }.sum()) / (groupN - 1)
            denominator += if (groupDenominator == 0.0) 0.0 else groupDenominator
        }

        val w = ((n - 2) / 1.0) * (numerator / denominator)

        // Calculate p-value using F-distribution
        val pValue = if (denominator == 0.0) 1.0 else 1.0 - FDistribution(1.0, ((n - 2).toDouble())).cumulativeProbability(w)

        val result = DataAnalysisResult(
            resultTitle = "Run #$runCount - ${binding.tvSelectData.text}",
            resultData = "Data yang digunakan: $edtDataName1, $edtDataName2",
            descriptiveTitle = "$edtDataName1 descriptive:",
            descriptiveContent = "n: $n",
            testValuesContent = "p-Value: $pValue",
            resultConclusion = if (pValue <= alpha) {
                "Terdapat perbedaan variansi antar sampel data (α = $alpha)."
            } else {
                "Tidak terdapat perbedaan variansi antar sampel data (α = $alpha)."
            }
        )

        val repository = ModuleRepositoryImpl(requireContext())
        viewModel = ViewModelProvider(this, ModuleViewModelFactory(repository))[ResultViewModel::class.java]
        viewModel.addResult(result)
        viewModel.getResultList().observe(viewLifecycleOwner) { results ->
            binding.rvResult.adapter?.let { adapter ->
                if (adapter is ResultAdapter) {
                    adapter.updateHideTestValues(false)
                    adapter.setItems(results)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun calculateGlesjerTest(edtDataAlpha: String, edtDataName: String, edtDataValue: String) {
        runCount++
        val dataValue = edtDataValue.split(" ").map { it.toDouble() }.toDoubleArray()
        val alpha = edtDataAlpha.toDoubleOrNull()

        // Menghitung jumlah data dan nilai rata-rata
        val n = dataValue.size
        val mean = dataValue.average()

        // Menghitung standard deviation dari data
        val deviation = sqrt(dataValue.sumOf { (it - dataValue.average()).pow(2.0) } / (dataValue.size))

        val xBar = dataValue.average()
        val sSquared = dataValue.sumOf { (it - xBar).pow(2) } / (dataValue.size - 1)
        val s = sqrt(sSquared)
        val residuals = dataValue.map { it - xBar }
        val residualSlopes = residuals.mapIndexed { index, residual -> residual * (alpha?.minus(
            dataValue[index]
        )!!) }
        val tValues = residualSlopes.map { it -> it / (s * sqrt(1 - (it / residuals.sumOf { it.pow(2) }))) }

        val outlierIndexes = tValues.mapIndexedNotNull { index, tValue ->
            if (tValue.absoluteValue > 2) index else null
        }

        val result = DataAnalysisResult(
            resultTitle = "Run #$runCount - ${binding.tvSelectData.text}",
            resultData = "Data yang digunakan: $edtDataName",
            descriptiveTitle = "$edtDataName descriptive:",
            descriptiveContent = "n: $n; Mean : $mean; SD: $deviation",
            testValuesContent = "t-Value: $tValues",
            resultConclusion = if (outlierIndexes.isNotEmpty()) {
                "Terjadi heteroskedastisitas. Outliers pada indeks ${outlierIndexes.joinToString()}"
            } else {
                "Tidak terjadi heteroskedastisitas"
            }
        )

        val repository = ModuleRepositoryImpl(requireContext())
        viewModel = ViewModelProvider(this, ModuleViewModelFactory(repository))[ResultViewModel::class.java]
        viewModel.addResult(result)
        viewModel.getResultList().observe(viewLifecycleOwner) { results ->
            binding.rvResult.adapter?.let { adapter ->
                if (adapter is ResultAdapter) {
                    adapter.updateHideTestValues(false)
                    adapter.setItems(results)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun calculateGlesjerTes(edtDataAlpha: String, edtDataName1: String, edtDataName2: String, edtDataValue1: String, edtDataValue2: String, textDataName: TextView, textViewValue: TextView) {
        val dataValue1 = edtDataValue1.split(" ").map { it.toDouble() }.toDoubleArray()
        val dataValue2 = edtDataValue2.split(" ").map { it.toDouble() }.toDoubleArray()
        val alpha = edtDataAlpha.toDoubleOrNull()

        // 2. Calculate the residuals
        val predictedYValues = predictYValues(dataValue1, dataValue2)
        val residuals = mutableListOf<Double>()
        for (i in dataValue2.indices) {
            residuals.add(dataValue2[i] - predictedYValues[i])
        }

        // 3. Square the residuals
        val squaredResiduals = mutableListOf<Double>()
        for (residual in residuals) {
            squaredResiduals.add(residual * residual)
        }

        // 4. Regress squared residuals on the independent variable
        val regressionModel = SimpleRegression()
        for (i in dataValue1.indices) {
            regressionModel.addData(dataValue1[i], squaredResiduals[i])
        }

        // 5. Test for significance
        val tScore = regressionModel.slope / regressionModel.slopeStdErr
        val degreesOfFreedom = dataValue1.size - 2
        val pValue = 2.0 * (1.0 - org.apache.commons.math3.distribution.TDistribution(degreesOfFreedom.toDouble()).cumulativeProbability(
            abs(tScore)
        ))

        // 6. Interpret the results
        textDataName.text = "Data 1 : $edtDataName1\nData 2 : $edtDataName2"
        textViewValue.text = "Glesjer Test : $pValue\n" +
                if (pValue < alpha!!) {
                    "Data menunjukkan heteroskedastisitas, menunjukkan bahwa varian residual tidak konstan pada semua level variabel independen."
                } else {
                    "Data menunjukkan homoskedastisitas, menunjukkan bahwa varian residual konstan pada semua level variabel independen."
                }

        if (textViewValue.parent == null) {
            binding.layoutResult.addView(textDataName)
            binding.layoutResult.addView(textViewValue)
        }
    }

    // Function to predict Y values based on a linear regression model
    fun predictYValues(xValues: DoubleArray, yValues: DoubleArray): List<Double> {
        val regressionModel = SimpleRegression()
        for (i in xValues.indices) {
            regressionModel.addData(xValues[i], yValues[i])
        }
        val predictedYValues = mutableListOf<Double>()
        for (x in xValues) {
            predictedYValues.add(regressionModel.predict(x))
        }
        return predictedYValues
    }

    private fun calculateVIF(edtDataName: String, edtDataValue: String) {
        runCount++
        val dataValue = edtDataValue.split(" ").map { it.toDouble() }.toDoubleArray()

        // Menghitung jumlah data dan nilai rata-rata
        val n = dataValue.size
        val mean = dataValue.average()

        // Menghitung standard deviation dari data
        val deviation = sqrt(dataValue.sumOf { (it - dataValue.average()).pow(2.0) } / (dataValue.size))

        // Then, calculate the correlation matrix of the input data
        val corMatrix = Array(dataValue.size) { DoubleArray(dataValue.size) }
        for (i in dataValue.indices) {
            for (j in dataValue.indices) {
                val xi = dataValue[i]
                val xj = dataValue[j]
                val xiMean = dataValue.average()
                val xjMean = dataValue.average()
                val numerator = (xi - xiMean) * (xj - xjMean)
                val denominator = (dataValue.size - 1) * dataValue.standardDeviation()
                corMatrix[i][j] = numerator / denominator
            }
        }

        // Next, calculate the Variance Inflation Factor (VIF) for each variable
        val vifArray = DoubleArray(dataValue.size)
        for (i in dataValue.indices) {
            val sum = (dataValue.indices).sumOf { j -> corMatrix[j][i] * corMatrix[j][i] }
            vifArray[i] = 1 / (1 - sum)
        }

        // Finally, calculate the average VIF and check if any VIF is greater than 10
        val avgVif = vifArray.average()
        val isMulticollinear = vifArray.any { it > 10 }

        val result = DataAnalysisResult(
            resultTitle = "Run #$runCount - ${binding.tvSelectData.text}",
            resultData = "Data yang digunakan: $edtDataName",
            descriptiveTitle = "$edtDataName descriptive:",
            descriptiveContent = "n: $n; Mean : $mean; SD: $deviation",
            testValuesContent = "VIF values:\n" + vifArray.indices.joinToString("\n") { index -> "Variable ${index+1}: ${vifArray[index]}" },
            resultConclusion = if (isMulticollinear) {
                "Data menunjukkan multikolinieritas dengan rata-rata VIF: $avgVif"
            } else {
                "Data tidak menunjukkan multikolinieritas dengan rata-rata VIF: $avgVif"
            }
        )

        val repository = ModuleRepositoryImpl(requireContext())
        viewModel = ViewModelProvider(this, ModuleViewModelFactory(repository))[ResultViewModel::class.java]
        viewModel.addResult(result)
        viewModel.getResultList().observe(viewLifecycleOwner) { results ->
            binding.rvResult.adapter?.let { adapter ->
                if (adapter is ResultAdapter) {
                    adapter.updateHideTestValues(false)
                    adapter.setItems(results)
                }
            }
        }
    }

    fun calculateOneSampleTTest(edtDataAlpha: String, edtDataPopulationMean: String, edtDataName: String, edtDataValue: String) {
        runCount++
        val dataValue = edtDataValue.split(" ").map { it.toDouble() }.toDoubleArray()
        val alpha = edtDataAlpha.toDoubleOrNull()
        val populationMean = edtDataPopulationMean.toDoubleOrNull()

        if (alpha == null) {
            runCount
            Toast.makeText(requireActivity(), "Masukkan nilai signifikansi", Toast.LENGTH_SHORT).show()
            return
        }

        if (populationMean == null) {
            runCount
            Toast.makeText(requireActivity(), "Masukkan nilai rata-rata populasi", Toast.LENGTH_SHORT).show()
            return
        }

        // Menghitung jumlah data dan nilai rata-rata
        val n = dataValue.size
        val mean = dataValue.average()

        // Menghitung standard deviation dari data
        val variance = dataValue.sumOf { (it - dataValue.average()).pow(2.0) } / (dataValue.size - 1)
        val standardDeviation = sqrt(variance)

        val xBar = dataValue.sum() / n
        val s = sqrt(dataValue.sumOf { (it - xBar).pow(2) } / (n - 1))
        val t = (xBar - populationMean) / (s / sqrt(n.toDouble()))
        val df = n - 1
        val tCritical = abs(TDistribution(df.toDouble()).inverseCumulativeProbability((alpha.div(2))))

        val result = DataAnalysisResult(
            resultTitle = "Run #$runCount - ${binding.tvSelectData.text}",
            resultData = "Data yang digunakan: $edtDataName",
            descriptiveTitle = "$edtDataName descriptive:",
            descriptiveContent = "n: $n; Mean : $mean; SD: $standardDeviation",
            testValuesContent = "t-Value: $t; t-Critical: $tCritical",
            resultConclusion = if (abs(t) > tCritical) "H0 ditolak" else "H0 gagal ditolak"
        )

        val repository = ModuleRepositoryImpl(requireContext())
        viewModel = ViewModelProvider(this, ModuleViewModelFactory(repository))[ResultViewModel::class.java]
        viewModel.addResult(result)
        viewModel.getResultList().observe(viewLifecycleOwner) { results ->
            binding.rvResult.adapter?.let { adapter ->
                if (adapter is ResultAdapter) {
                    adapter.updateHideTestValues(false)
                    adapter.setItems(results)
                }
            }
        }
    }

    private fun calculatePairedTTest(edtDataAlpha: String, edtDataName1: String, edtDataValue1: String, edtDataName2: String, edtDataValue2: String) {
        val dataValue1 = edtDataValue1.split(" ").map { it.toDouble() }.toDoubleArray()
        val dataValue2 = edtDataValue2.split(" ").map { it.toDouble() }.toDoubleArray()
        val alpha = edtDataAlpha.toDoubleOrNull()

        if (alpha == null) {
            runCount
            Toast.makeText(requireActivity(), "Masukkan nilai signifikansi", Toast.LENGTH_SHORT).show()
            return
        }

        val n = dataValue1.size
        var diffSum = 0.0
        for (i in 0 until n) {
            diffSum += dataValue2[i] - dataValue1[i]
        }
        val meanDiff = diffSum / n

        var sqDiffSum = 0.0
        for (i in 0 until n) {
            sqDiffSum += (dataValue2[i] - dataValue1[i] - meanDiff).pow(2)
        }
        val stdDevDiff = sqrt(sqDiffSum / (n - 1))

        val tScore = meanDiff / (stdDevDiff / sqrt(n.toDouble()))
        val df = n - 1
        val tDist = TDistribution(df.toDouble())
        val tCritical = abs(tDist.inverseCumulativeProbability(alpha / 2))

        val result = DataAnalysisResult(
            resultTitle = "Run #$runCount - ${binding.tvSelectData.text}",
            resultData = "Data yang digunakan: $edtDataName1, $edtDataName2",
            descriptiveTitle = "$edtDataName1 descriptive:",
            descriptiveContent = "n: $n; Mean : $meanDiff; SD: $stdDevDiff",
            testValuesContent = "t-Value: $tScore; t-Critical: $tCritical",
            resultConclusion = if (abs(tScore) > tCritical) {
                "H0 ditolak pada tingkat signifikansi $alpha. Terdapat perbedaan yang signifikan antara data1 dan data2."
            } else {
                "H0 gagal ditolak pada tingkat signifikansi $alpha. Tidak terdapat perbedaan yang signifikan antara data1 dan data2."
            }
        )

        val repository = ModuleRepositoryImpl(requireContext())
        viewModel = ViewModelProvider(this, ModuleViewModelFactory(repository))[ResultViewModel::class.java]
        viewModel.addResult(result)
        viewModel.getResultList().observe(viewLifecycleOwner) { results ->
            binding.rvResult.adapter?.let { adapter ->
                if (adapter is ResultAdapter) {
                    adapter.updateHideTestValues(false)
                    adapter.setItems(results)
                }
            }
        }
    }

    private fun calculateIndependentSampleTTest(edtDataAlpha: String, edtDataName1: String, edtDataValue1: String, edtDataName2: String, edtDataValue2: String) {
        val dataValue1 = edtDataValue1.split(" ").map { it.toDouble() }.toDoubleArray()
        val dataValue2 = edtDataValue2.split(" ").map { it.toDouble() }.toDoubleArray()
        val alpha = edtDataAlpha.toDoubleOrNull()

        if (alpha == null) {
            runCount
            Toast.makeText(requireActivity(), "Masukkan nilai signifikansi", Toast.LENGTH_SHORT).show()
            return
        }

        // Menghitung mean, std deviasi, dan jumlah sampel
        val meanA = dataValue1.average()
        val meanB = dataValue2.average()
        val sdA = sqrt(dataValue1.map { (it - meanA).pow(2.0) }.sum() / (dataValue1.size - 1))
        val sdB = sqrt(dataValue2.map { (it - meanB).pow(2.0) }.sum() / (dataValue2.size - 1))
        val nA = dataValue1.size
        val nB = dataValue2.size

        val s = sqrt(((nA - 1) * sdA.pow(2.0) + (nB - 1) * sdB.pow(2.0)) / (nA + nB - 2))
        val t = (meanA - meanB) / (s * sqrt(1.0/nA + 1.0/nB))

        val df = nA + nB - 2
        val tDist = TDistribution(df.toDouble())
//        val pValue = 2.0 * (1.0 - tDist.cumulativeProbability(abs(t)))

        // Melakukan uji Independent Sample T-Test
        val tTest = TTest()
        val pValue = tTest.tTest(dataValue1, dataValue2)

        val result = DataAnalysisResult(
            resultTitle = "Run #$runCount - ${binding.tvSelectData.text}",
            resultData = "Data yang digunakan: $edtDataName1, $edtDataName2",
            descriptiveTitle = "$edtDataName1 descriptive:",
            descriptiveContent = "n: $nA; Mean : $meanA; SD: $sdA",
            testValuesContent = "t-Value: $t; p-Value: $pValue",
            resultConclusion = if (pValue < alpha) {
                if (meanA > meanB) {
                    "Terdapat perbedaan signifikan antara data A dan data B. Rata-rata data A lebih tinggi dari rata-rata data B."
                } else {
                    "Terdapat perbedaan signifikan antara data A dan data B. Rata-rata data B lebih tinggi dari rata-rata data A."
                }
            } else {
                "Tidak terdapat perbedaan signifikan antara data A dan data B."
            }
        )

        val repository = ModuleRepositoryImpl(requireContext())
        viewModel = ViewModelProvider(this, ModuleViewModelFactory(repository))[ResultViewModel::class.java]
        viewModel.addResult(result)
        viewModel.getResultList().observe(viewLifecycleOwner) { results ->
            binding.rvResult.adapter?.let { adapter ->
                if (adapter is ResultAdapter) {
                    adapter.updateHideTestValues(false)
                    adapter.setItems(results)
                }
            }
        }
    }

    // Extension function to calculate the standard deviation of an array of doubles
    fun DoubleArray.standardDeviation(): Double {
        val mean = average()
        var sum = 0.0
        for (value in this) {
            sum += (value - mean).pow(2.0)
        }
        return sqrt(sum / (size - 1))
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
        nameEditText.setPadding(12.dp, 12.dp, 6.dp, 12.dp)
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
}