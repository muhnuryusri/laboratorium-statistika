package com.example.laboratorium_statistika.ui.data_analysis

import android.annotation.SuppressLint
import android.app.ActionBar.LayoutParams
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.laboratorium_statistika.R
import com.example.laboratorium_statistika.databinding.FragmentDataAnalysisBinding
import org.apache.commons.math3.stat.StatUtils
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression
import org.apache.commons.math3.stat.regression.SimpleRegression
import kotlin.math.*

class DataAnalysisFragment : Fragment() {
    private lateinit var binding: FragmentDataAnalysisBinding
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDataAnalysisBinding.inflate(layoutInflater, container, false)
        return (binding.root)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSelectData.setOnClickListener {
            val action = DataAnalysisFragmentDirections.actionDataAnalysisFragmentToDialogFragment()
            findNavController().navigate(action)
        }

        sharedViewModel.analysisText.observe(viewLifecycleOwner, Observer { text ->
            if (text.isNullOrEmpty()) {
                binding.tvSelectData.text = "Pilih metode analisis data"
            } else {
                binding.apply {
                    tvSelectData.text = text
                    tvSelectData.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                }
            }

            binding.btnRun.isEnabled = text != "Pilih metode analisis data"

            if (text == "Kolmogorov-Smirnov Test" || text == "Levene Test" || text == "Glesjer Test") {
                binding.edtDataAlpha.visibility = View.VISIBLE
            } else {
                binding.edtDataAlpha.visibility = View.GONE
            }
        })

        binding.btnAddData.setOnClickListener {
            val id = View.generateViewId()
            val newRow = addData(requireContext(), id)
            binding.tableLayout.addView(newRow)
        }

        binding.btnRun.setOnClickListener {
            var hasCheckedBox = false
            val selectedCheckBoxes = mutableListOf<CheckBox>()

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

                        // Pindahkan di luar fungsi onClickListener jika hanya ingin hasilnya diupdate (Bukan menambah hasil baru)
                        val textViewName = TextView(requireActivity())
                        val textViewValue = TextView(requireActivity())

                        when (binding.tvSelectData.text) {
                            "Pilih metode analisis data" -> Toast.makeText(requireActivity(), "Pilih data terlebih dahulu!", Toast.LENGTH_SHORT).show()

                            "Levene Test" -> calculateLeveneTest(edtDataAlpha, edtDataName1, edtDataName2, edtDataValue1, edtDataValue2, textViewName, textViewValue)
                            }
                    } else if (selectedCheckBoxes.size == 1) {
                        val tableRowSingle = selectedCheckBoxes[0].parent as TableRow
                        val edtDataName = (tableRowSingle.getChildAt(1) as EditText).text.toString()
                        val edtDataValue = (tableRowSingle.getChildAt(2) as EditText).text.toString().trim()
                        val edtDataAlpha = binding.edtDataAlpha.text.toString()

                        val pattern = Regex("[^\\d\\s.]+")
                        if (pattern.containsMatchIn(edtDataValue)) {
                            Toast.makeText(requireActivity(), "Gunakan spasi sebagai pemisah antar angka", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }

                        if (!edtDataValue.contains(" ")) {
                            Toast.makeText(requireActivity(), "Masukkan setidaknya dua angka yang terpisah dengan spasi", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }

                        val textViewName = TextView(requireActivity())
                        val textViewValue = TextView(requireActivity())

                        when (binding.tvSelectData.text) {
                            "Pilih metode analisis data" -> Toast.makeText(requireActivity(), "Pilih data terlebih dahulu!", Toast.LENGTH_SHORT).show()

                            "Rata-rata" -> calculateDeskriptifData("Rata-rata", edtDataName, edtDataValue, textViewName, textViewValue)
                            "Median" -> calculateDeskriptifData("Median", edtDataName, edtDataValue, textViewName, textViewValue)
                            "Modus" -> calculateDeskriptifData("Modus", edtDataName, edtDataValue, textViewName, textViewValue)
                            "Range" -> calculateDeskriptifData("Range", edtDataName, edtDataValue, textViewName, textViewValue)
                            "Ragam" -> calculateDeskriptifData("Ragam", edtDataName, edtDataValue, textViewName, textViewValue)
                            "Simpangan Baku" -> calculateDeskriptifData("Simpangan Baku", edtDataName, edtDataValue, textViewName, textViewValue)
                            "Kuartil" -> calculateDeskriptifData("Kuartil", edtDataName, edtDataValue, textViewName, textViewValue)

                            "Kolmogorov-Smirnov Test" -> calculateKolmogorovSmirnovTest(edtDataAlpha, edtDataName, edtDataValue, textViewName, textViewValue)
                            "Glesjer Test" -> calculateGlesjerTest(edtDataAlpha, edtDataName, edtDataValue, textViewName, textViewValue)
                        }
                    }
                }
            }

            if (!hasCheckedBox) {
                Toast.makeText(requireActivity(), "Harap pilih setidaknya satu kotak centang", Toast.LENGTH_SHORT).show()
            }
        }

        binding.resultFrameLayout.setOnClickListener(object : View.OnClickListener {
            var isExpanded = false
            val displayMetrics = resources.displayMetrics
            val screenHeight = displayMetrics.heightPixels
            val halfScreenHeight = screenHeight / 2

            override fun onClick(v: View?) {
                if (isExpanded) {
                    // shrink the layout
                    binding.resultFrameLayout.layoutParams.height = 200 // or any desired collapsed height
                    isExpanded = false
                } else {
                    // expand the layout
                    binding.resultFrameLayout.layoutParams.height = 500 // or any desired expanded height
                    isExpanded = true
                }
                binding.resultFrameLayout.requestLayout()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sharedViewModel.analysisText.value = null
    }

    @SuppressLint("SetTextI18n")
    private fun calculateDeskriptifData(analysis: String, edtDataName: String, edtDataValue: String, textViewName: TextView, textViewValue: TextView) {
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

        textViewName.text = "Data : $edtDataName"
        when (analysis) {
            "Rata-rata" -> textViewValue.text = "Mean : $mean"
            "Median" -> textViewValue.text = "Median : $median"
            "Modus" -> textViewValue.text = "Modus : $modus"
            "Range" -> textViewValue.text = "Range : $range"
            "Ragam" -> textViewValue.text = "Ragam : $variance"
            "Simpangan Baku" -> textViewValue.text = "Simpangan Baku Populasi (Deviation): $deviation\nSimpangan Baku Sample (Standard Deviation): $standardDeviation"
            "Kuartil" -> textViewValue.text = "Kuartil Bawah (Q1) : $quartile1\nKuartil Tengah (Q2) : $quartile2\nKuartil Atas (Q3) : $quartile3\nInterkuartil Range (IQR) : $iqr"
        }

        if (textViewValue.parent == null) {
            binding.layoutResult.addView(textViewName)
            binding.layoutResult.addView(textViewValue)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun calculateKolmogorovSmirnovTest(edtDataAlpha: String, edtDataName: String, edtDataValue: String, textViewName: TextView, textViewValue: TextView) {
        val dataValue = edtDataValue.split(" ").map { it.toDouble() }.toDoubleArray()
        val alpha = edtDataAlpha.toDoubleOrNull()

        if (alpha == null) {
            Toast.makeText(requireActivity(), "Masukkan nilai alpha", Toast.LENGTH_SHORT).show()
            return
        }

        // Mengurutkan array data
        dataValue.sort()

        // Menghitung jumlah data dan nilai rata-rata
        val n = dataValue.size
        val mean = dataValue.average()

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

        textViewName.text = "Data : $edtDataName"

        // Menampilkan hasil uji statistik dan nilai kritis
        // Menentukan apakah hipotesis nol diterima atau ditolak
        textViewValue.text =
            "Uji statistik (D-Value): $dValue\nNilai kritis (p-Value): $pValue\n" +
                    if (dValue <= pValue) {
                        "Hipotesis nol diterima.\nData terdistribusi dengan normal (α = $alpha)"
                    } else {
                        "Hipotesis nol ditolak.\nData tidak terdistribusi dengan normal (α = $alpha)"
                    }

        if (textViewValue.parent == null) {
            binding.layoutResult.addView(textViewName)
            binding.layoutResult.addView(textViewValue)
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
    private fun calculateLeveneTest(edtDataAlpha: String, edtDataName1: String, edtDataName2: String, edtDataValue1: String, edtDataValue2: String, textViewName: TextView, textViewValue: TextView) {
        val dataValue1 = edtDataValue1.split(" ").map { it.toDouble() }.toDoubleArray()
        val dataValue2 = edtDataValue2.split(" ").map { it.toDouble() }.toDoubleArray()
        val alpha = edtDataAlpha.toDoubleOrNull()

        val mean1 = dataValue1.average()
        val mean2 = dataValue2.average()

        // Calculate deviations from the mean for each sample
        val deviations1 = dataValue1.map { it - mean1 }
        val deviations2 = dataValue2.map { it - mean2 }

        // Calculate the absolute deviations
        val absDeviations1 = deviations1.map { it.absoluteValue }
        val absDeviations2 = deviations2.map { it.absoluteValue }

        // Calculate the median of the absolute deviations
        val median1 = absDeviations1.sorted()[dataValue1.size / 2]
        val median2 = absDeviations2.sorted()[dataValue2.size / 2]

        // Calculate the Levene test statistic
        val numerator = ((deviations1.map { it.pow(2) }.sum() / (dataValue1.size - 1)) + (deviations2.map { it.pow(2) }.sum() / (dataValue2.size - 1)))
        val denominator = ((median1.pow(2) * (dataValue1.size - 1) / dataValue1.size) + (median2.pow(2) * (dataValue2.size - 1) / dataValue2.size))
        val leveneStat = numerator / denominator

        // Calculate the degrees of freedom
        val df1 = dataValue1.size - 1
        val df2 = dataValue2.size - 1

        // Calculate the p-value using an F distribution
        val pValue = fDist(leveneStat, df1, df2)

        textViewName.text = "Data 1 : $edtDataName1\nData 2 : $edtDataName2"
        textViewValue.text =
            "Levene Test : $leveneStat\n" +
                    if (pValue < alpha!!) {
                        "Terdapat perbedaan signifikan antara varian kedua kelompok"
                    } else {
                        "Tidak terdapat perbedaan signifikan antara varian kedua kelompok"
                    }

        if (textViewValue.parent == null) {
            binding.layoutResult.addView(textViewName)
            binding.layoutResult.addView(textViewValue)
        }
    }

    fun fDist(fValue: Double, df1: Int, df2: Int): Double {
        val numerator = ((df1 * fValue).pow(df1.toDouble())) * ((df2.toDouble()).pow(df2.toDouble()))
        val denominator = ((df1 * fValue) + df2).pow((df1 + df2).toDouble())
        val beta = betaInc(df1 / 2.0, df2 / 2.0, df1 / (df1 + df2 * fValue))
        return (numerator / denominator) * beta
    }

    fun betaInc(a: Double, b: Double, x: Double): Double {
        val epsilon = 1e-15 // konstanta kecil untuk toleransi kesalahan
        var h = 1.0
        var d = 1.0
        var aa = a
        var bb = b
        var i = 1

        while (true) {
            val a1 = aa + i.toDouble()
            val b1 = bb + i.toDouble()
            val c1 = i.toDouble()
            val d1 = x * (aa + bb + c1 - 1.0)

            val del = d1 / (a1 * b1 * c1)

            h *= 1.0 + del
            d = if (del.absoluteValue < epsilon) {
                1.0 / (1.0 + del) * h
            } else {
                d / (1.0 + del)
            }

            aa += 1.0
            bb += 1.0

            val delta = aa * bb
            val frac = d * delta
            val g = frac / h

            if ((g - 1.0).absoluteValue < epsilon) {
                return h * x.pow(a) * (1.0 - x).pow(b) / (a * delta)
            }

            i += 1
        }
    }

    @SuppressLint("SetTextI18n")
    private fun calculateGlesjerTest(edtDataAlpha: String, edtDataName: String, edtDataValue: String, textViewName: TextView, textViewValue: TextView) {
        val dataValue = edtDataValue.split(" ").map { it.toDouble() }.toDoubleArray()
        val alpha = edtDataAlpha.toDoubleOrNull()

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

        textViewName.text = "Data : $edtDataName"
        textViewValue.text = "Glesjer Test : $tValues\n " +
            if (outlierIndexes.isNotEmpty()) {
            "Terjadi heteroskedastisitas. Outliers pada indeks ${outlierIndexes.joinToString()}"
        } else {
            "Tidak terjadi heteroskedastisitas"
        }

        if (textViewValue.parent == null) {
            binding.layoutResult.addView(textViewName)
            binding.layoutResult.addView(textViewValue)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun calculateGlesjerTes(edtDataAlpha: String, edtDataName1: String, edtDataName2: String, edtDataValue1: String, edtDataValue2: String, textViewName: TextView, textViewValue: TextView) {
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
        textViewName.text = "Data 1 : $edtDataName1\nData 2 : $edtDataName2"
        textViewValue.text = "Glesjer Test : $pValue\n" +
                if (pValue < alpha!!) {
                    "Data menunjukkan heteroskedastisitas, menunjukkan bahwa varian residual tidak konstan pada semua level variabel independen."
                } else {
                    "Data menunjukkan homoskedastisitas, menunjukkan bahwa varian residual konstan pada semua level variabel independen."
                }

        if (textViewValue.parent == null) {
            binding.layoutResult.addView(textViewName)
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

    private fun isHomoscedastic(predictedVariance: DoubleArray, residuals: DoubleArray, alpha: Double = 0.01): Boolean {
        val n = residuals.size
        val threshold = org.apache.commons.math3.distribution.FDistribution(1.0, (n - 2).toDouble()).inverseCumulativeProbability(1 - alpha / 2)
        for (i in 0 until n) {
            if (residuals[i] * residuals[i] > predictedVariance[i] * threshold) {
                return false
            }
        }
        return true
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
        valueEditText.inputType = InputType.TYPE_CLASS_NUMBER
        valueEditText.typeface = ResourcesCompat.getFont(context, R.font.inter)
        tableRow.addView(valueEditText)

        return tableRow
    }

    private val Int.dp: Int
        get() = (this * Resources.getSystem().displayMetrics.density).toInt()

    fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp.toFloat() * density).roundToInt()
    }
}