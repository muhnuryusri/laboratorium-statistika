package com.example.laboratorium_statistika.repository

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.laboratorium_statistika.model.DataAnalysisResult
import org.apache.commons.math3.distribution.*
import org.apache.commons.math3.special.Erf.erfc
import org.apache.commons.math3.stat.inference.AlternativeHypothesis
import org.apache.commons.math3.stat.inference.BinomialTest
import org.apache.commons.math3.stat.inference.MannWhitneyUTest
import org.apache.commons.math3.stat.inference.TTest
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression
import org.apache.commons.math3.stat.regression.SimpleRegression
import kotlin.math.*

class DataAnalysisRepositoryImpl(private val context: Context): DataAnalysisRepository {
    private val resultList = MutableLiveData<List<DataAnalysisResult>>(emptyList())

    override fun calculateDeskriptifData(
        analysis: String,
        edtDataName: String,
        edtDataValue: String,
        runCount: Int,
        tvSelectData: String
    ): DataAnalysisResult {
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

        return DataAnalysisResult(
            id = runCount,
            resultTitle = "Run #$runCount - $tvSelectData",
            resultData = "Data yang digunakan: $edtDataName",
            descriptiveTitle = "$edtDataName descriptive:",
            descriptiveContent = "n: ${dataValue.size};\n" + when (analysis) {
                "Rata-rata" -> "Mean: $mean"
                "Median" -> "Median: $median"
                "Modus" -> "Modus: $modus"
                "Range" -> "Range: $range"
                "Ragam" -> "Ragam: $variance"
                "Simpangan Baku" -> "Simpangan Baku Populasi (Deviation): $deviation\nSimpangan Baku Sample (Standard Deviation): $standardDeviation"
                "Kuartil" -> "Kuartil Bawah (Q1)\t\t\t\t\t\t: $quartile1\n" +
                        "Kuartil Tengah (Q2)\t\t\t\t\t: $quartile2\n" +
                        "Kuartil Atas (Q3)\t\t\t\t\t\t\t: $quartile3\n" +
                        "Interkuartil Range (IQR)\t: $iqr"
                else -> ""
            },
            amountOfData = 1,
            hideTestValues = true
        )
    }

    // Uji Kolmogorov-Smirnoff
    override fun calculateUjiNormalitas(
        dataAlpha: String,
        edtDataName: String,
        edtDataValue: String,
        runCount: Int,
        tvSelectData: String
    ): DataAnalysisResult {
        val dataValue = edtDataValue.split(" ").map { it.toDouble() }.toDoubleArray()
        val alpha = dataAlpha.toDouble()

        dataValue.sort()

        // Menghitung jumlah data dan nilai rata-rata
        val n = dataValue.size
        val mean = dataValue.average()

        // Menghitung standard deviation dari data
        val standardDeviation = dataValue.standardDeviation()

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

        val conclusion = if (dValue <= pValue) {
            "Hipotesis nol diterima.\nData terdistribusi dengan normal (α = $alpha)"
        } else {
            "Hipotesis nol ditolak.\nData tidak terdistribusi dengan normal (α = $alpha)"
        }

        return DataAnalysisResult(
            id = runCount,
            resultTitle = "Run #$runCount - $tvSelectData",
            resultData = "Data yang digunakan: $edtDataName",
            descriptiveTitle = "$edtDataName descriptive:",
            descriptiveContent = "n: $n; Mean: $mean; SD: $standardDeviation",
            testValuesContent = "D-Value: $dValue\np-Value: $pValue",
            resultConclusion = conclusion,
            amountOfData = 1,
            hideTestValues = false
        )
    }

    override fun calculateUjiHomogenitas(
        dataAlpha: String,
        edtDataName1: String,
        edtDataValue1: String,
        edtDataName2: String,
        edtDataValue2: String,
        runCount: Int,
        tvSelectData: String
    ): DataAnalysisResult {
        val dataValue1 = edtDataValue1.split(" ").map { it.toDouble() }.toDoubleArray()
        val dataValue2 = edtDataValue2.split(" ").map { it.toDouble() }.toDoubleArray()
        val alpha = dataAlpha.toDouble()

        // Combine two input data arrays into a list of lists
        val groupList = listOf(dataValue1.toList(), dataValue2.toList())

        // Calculate the number of values in each group and the overall number of values
        val m = dataValue1.size
        val f = dataValue2.size
        val n = m + f

        val standardDeviation1 = dataValue1.standardDeviation()
        val standardDeviation2 = dataValue2.standardDeviation()

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
            val groupDenominator = (diff.sumOf { it.pow(2) }) / (groupN - 1)
            denominator += if (groupDenominator == 0.0) 0.0 else groupDenominator
        }

        val w = ((n - 2) / 1.0) * (numerator / denominator)

        // Calculate p-value using F-distribution
        val pValue = if (denominator == 0.0) 1.0 else 1.0 - FDistribution(1.0, ((n - 2).toDouble())).cumulativeProbability(w)

        val conclusion = if (pValue <= alpha) {
            "Terdapat perbedaan variansi antar sampel data (α = $alpha)."
        } else {
            "Tidak terdapat perbedaan variansi antar sampel data (α = $alpha)."
        }

        return DataAnalysisResult(
            id = runCount,
            resultTitle = "Run #$runCount - $tvSelectData",
            resultData = "Data yang digunakan: $edtDataName1, $edtDataName2",
            descriptiveTitle = "$edtDataName1 descriptive:",
            descriptiveContent = "n: $m; SD: $standardDeviation1",
            secondDescriptiveTitle = "$edtDataName2 descriptive:",
            secondDescriptiveContent = "n: $f; SD: $standardDeviation2",
            testValuesContent = "p-Value: $pValue",
            resultConclusion = conclusion,
            amountOfData = 2,
            hideTestValues = false
        )
    }

    override fun calculateUjiHeteroskedastisitas(
        dataAlpha: String,
        edtDataName1: String,
        edtDataValue1: String,
        edtDataName2: String,
        edtDataValue2: String,
        runCount: Int,
        tvSelectData: String
    ): DataAnalysisResult {
        val dataValue1 = edtDataValue1.split(" ").map { it.toDouble() }.toDoubleArray()
        val dataValue2 = edtDataValue2.split(" ").map { it.toDouble() }.toDoubleArray()
        val alpha = dataAlpha.toDouble()

        val n1 = dataValue1.size
        val n2 = dataValue2.size
        val n = min(n1, n2)
        val sumX = dataValue1.sum()
        val sumY = dataValue2.sum()
        val sumXY = (dataValue1 zip dataValue2).sumOf { it.first * it.second }
        val sumXX = dataValue1.sumOf { it * it }

        val standardDeviation1 = dataValue1.standardDeviation()
        val standardDeviation2 = dataValue2.standardDeviation()

        val b = (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX)
        val a = (sumY - b * sumX) / n

        val yPredict = DoubleArray(n) { i -> a + b * dataValue1[i] }
        val residual = DoubleArray(n) { i -> dataValue2[i] - yPredict[i] }
        val absResidual = DoubleArray(n) { i -> abs(residual[i]) }

        val sse = residual.sumOf { it * it }
        val sst = (dataValue2.sumOf { it * it } - sumY * sumY / n).coerceAtLeast(0.0)

        val k = 1 // Jumlah variabel independent
        val df = n - k - 1
        val tCritical = abs(TDistribution(df.toDouble()).inverseCumulativeProbability((alpha.div(2))))
        val tStat = absResidual.sum() / sqrt((n - 2).toDouble() * sse / sst)

        val conclusion = if (tStat > tCritical) {
            "Terdapat bukti signifikan untuk menyatakan adanya heteroskedastisitas pada data, karena T-Statistic > T-Critical (α = $alpha)."
        } else {
            "Tidak terdapat bukti signifikan untuk menyatakan adanya heteroskedastisitas pada data, karena T-Statistic < T-Critical (α = $alpha)."
        }

        return DataAnalysisResult(
            id = runCount,
            resultTitle = "Run #$runCount - $tvSelectData",
            resultData = "Data yang digunakan: $edtDataName1, $edtDataName2",
            descriptiveTitle = "$edtDataName1 descriptive:",
            descriptiveContent = "n: $n1; SD: $standardDeviation1",
            secondDescriptiveTitle = "$edtDataName2 descriptive:",
            secondDescriptiveContent = "n: $n2; SD: $standardDeviation2",
            testValuesContent = "t-Statistik: $tStat; t-Critical: $tCritical",
            resultConclusion = conclusion,
            amountOfData = 2,
            hideTestValues = false
        )
    }

    override fun calculateAutokorelasi(
        dataAlpha: String,
        edtDataName: String,
        edtDataValue: String,
        runCount: Int,
        tvSelectData: String
    ): DataAnalysisResult {
        val dataValue = edtDataValue.split(" ").map { it.toDouble() }.toDoubleArray()
        val alpha = dataAlpha.toDouble()

        // Menghitung nilai rata-rata
        val mean = dataValue.average()

        // Menghitung standard deviation dari data
        val standardDeviation = dataValue.standardDeviation()

        // Hitung nilai d
        var et: Double
        var etMinus1: Double
        var etMinus1Square: Double
        var etSquare = 0.0
        var d = 0.0
        for (i in 1 until dataValue.size) {
            et = dataValue[i] - dataValue[i-1]
            etMinus1 = etSquare
            etMinus1Square = et * etMinus1
            etSquare = et * et
            d += etMinus1Square / etSquare
        }
        d /= dataValue.size - 2

        val n = dataValue.size
        val zAlpha = abs(NormalDistribution().inverseCumulativeProbability(alpha / 2))
        val criticalLow = 2 - (3 / (n - 1)) - zAlpha / sqrt(n.toDouble())
        val criticalHigh = 2 + (3 / (n - 1)) - zAlpha / sqrt(n.toDouble())

        // Ambil keputusan
        val conclusion = if (d <= criticalLow || d >= criticalHigh) {
            "Terdapat autokorelasi pada data."
        } else {
            "Tidak terdapat autokorelasi pada data."
        }

        return DataAnalysisResult(
            id = runCount,
            resultTitle = "Run #$runCount - $tvSelectData",
            resultData = "Data yang digunakan: $edtDataName",
            descriptiveTitle = "$edtDataName descriptive:",
            descriptiveContent = "n: $n; Mean: $mean; SD: $standardDeviation",
            testValuesContent = "d-Value: $d; critical Low: $criticalLow; critical High: $criticalHigh",
            resultConclusion = conclusion,
            amountOfData = 1,
            hideTestValues = false
        )
    }

    override fun calculateUjiMultikolinieritas(
        edtDataName: String,
        edtDataValue: String,
        runCount: Int,
        tvSelectData: String
    ): DataAnalysisResult {
        val dataValue = edtDataValue.split(" ").map { it.toDouble() }.toDoubleArray()

        // Menghitung jumlah data dan nilai rata-rata
        val n = dataValue.size
        val mean = dataValue.average()

        // Menghitung standard deviation dari data
        val standardDeviation = dataValue.standardDeviation()

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

        val conclusion = if (isMulticollinear) {
            "Data menunjukkan multikolinieritas dengan rata-rata VIF: $avgVif"
        } else {
            "Data tidak menunjukkan multikolinieritas dengan rata-rata VIF: $avgVif"
        }

        return DataAnalysisResult(
            id = runCount,
            resultTitle = "Run #$runCount - tvSelectData",
            resultData = "Data yang digunakan: $edtDataName",
            descriptiveTitle = "$edtDataName descriptive:",
            descriptiveContent = "n: $n; Mean: $mean; SD: $standardDeviation",
            testValuesContent = "VIF values:\n" + vifArray.indices.joinToString("\n") { index -> "Variable ${index+1}: ${vifArray[index]}" },
            resultConclusion = conclusion,
            amountOfData = 1,
            hideTestValues = false
        )
    }

    override fun calculateOneSampleTTest(
        dataAlpha: String,
        edtDataName: String,
        edtDataValue: String,
        edtDataPopulationMean: String,
        runCount: Int,
        tvSelectData: String
    ): DataAnalysisResult {
        val dataValue = edtDataValue.split(" ").map { it.toDouble() }.toDoubleArray()
        val alpha = dataAlpha.toDouble()
        val populationMean = edtDataPopulationMean.toDoubleOrNull()

        if (populationMean == null) {
            Toast.makeText(context, "Masukkan nilai rata-rata populasi", Toast.LENGTH_SHORT).show()
        }

        // Menghitung jumlah data dan nilai rata-rata
        val n = dataValue.size
        val mean = dataValue.average()

        // Menghitung standard deviation dari data
        val standardDeviation = dataValue.standardDeviation()

        val xBar = dataValue.sum() / n
        val s = sqrt(dataValue.sumOf { (it - xBar).pow(2) } / (n - 1))
        val t = (xBar - populationMean!!) / (s / sqrt(n.toDouble()))
        val df = n - 1
        val tCritical = abs(TDistribution(df.toDouble()).inverseCumulativeProbability((alpha.div(2))))

        val conclusion = if (abs(t) > tCritical) {
            "H0 ditolak. Terdapat perbedaan yang signifikan."
        } else {
            "H0 gagal ditolak. Tidak terdapat perbedaan yang signifikan."
        }

        return DataAnalysisResult(
            id = runCount,
            resultTitle = "Run #$runCount - $tvSelectData",
            resultData = "Data yang digunakan: $edtDataName",
            descriptiveTitle = "$edtDataName descriptive:",
            descriptiveContent = "n: $n; Mean : $mean; SD: $standardDeviation",
            testValuesContent = "t-Value: $t; t-Critical: $tCritical",
            resultConclusion = conclusion,
            amountOfData = 1,
            hideTestValues = false
        )
    }

    override fun calculateIndependentSampleTTest(
        dataAlpha: String,
        edtDataName1: String,
        edtDataValue1: String,
        edtDataName2: String,
        edtDataValue2: String,
        runCount: Int,
        tvSelectData: String
    ): DataAnalysisResult {
        val dataValue1 = edtDataValue1.split(" ").map { it.toDouble() }.toDoubleArray()
        val dataValue2 = edtDataValue2.split(" ").map { it.toDouble() }.toDoubleArray()
        val alpha = dataAlpha.toDouble()

        val meanA = dataValue1.average()
        val meanB = dataValue2.average()
        val sdA = dataValue1.standardDeviation()
        val sdB = dataValue2.standardDeviation()
        val nA = dataValue1.size
        val nB = dataValue2.size

        val s = sqrt(((nA - 1) * sdA.pow(2.0) + (nB - 1) * sdB.pow(2.0)) / (nA + nB - 2))
        val t = (meanA - meanB) / (s * sqrt(1.0/nA + 1.0/nB))

        // Melakukan uji Independent Sample T-Test
        val tTest = TTest()
        val pValue = tTest.tTest(dataValue1, dataValue2)

        val conclusion = if (pValue < alpha) {
            if (meanA > meanB) {
                "Terdapat perbedaan signifikan antara data A dan data B. Rata-rata data A lebih tinggi dari rata-rata data B."
            } else {
                "Terdapat perbedaan signifikan antara data A dan data B. Rata-rata data B lebih tinggi dari rata-rata data A."
            }
        } else {
            "Tidak terdapat perbedaan signifikan antara data A dan data B."
        }

        return DataAnalysisResult(
            id = runCount,
            resultTitle = "Run #$runCount - $tvSelectData",
            resultData = "Data yang digunakan: $edtDataName1, $edtDataName2",
            descriptiveTitle = "$edtDataName1 descriptive:",
            descriptiveContent = "n: $nA; Mean: $meanA; SD: $sdA",
            secondDescriptiveTitle = "$edtDataName2 descriptive:",
            secondDescriptiveContent = "n: $nB; Mean: $meanB; SD: $sdB",
            testValuesContent = "t-Value: $t; p-Value: $pValue",
            resultConclusion = conclusion,
            amountOfData = 2,
            hideTestValues = false
        )
    }

    override fun calculatePairedSampleTTest(
        dataAlpha: String,
        edtDataName1: String,
        edtDataValue1: String,
        edtDataName2: String,
        edtDataValue2: String,
        runCount: Int,
        tvSelectData: String
    ): DataAnalysisResult {
        val dataValue1 = edtDataValue1.split(" ").map { it.toDouble() }.toDoubleArray()
        val dataValue2 = edtDataValue2.split(" ").map { it.toDouble() }.toDoubleArray()
        val alpha = dataAlpha.toDouble()

        val n1 = dataValue1.size
        val n2 = dataValue2.size
        val n = min(n1, n2)

        val meanA = dataValue1.average()
        val meanB = dataValue2.average()
        val sdA = dataValue1.standardDeviation()
        val sdB = dataValue2.standardDeviation()

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

        val conclusion = if (abs(tScore) > tCritical) {
            "H0 ditolak pada tingkat signifikansi $alpha. Terdapat perbedaan yang signifikan antara data1 dan data2."
        } else {
            "H0 gagal ditolak pada tingkat signifikansi $alpha. Tidak terdapat perbedaan yang signifikan antara data1 dan data2."
        }

        return DataAnalysisResult(
            id = runCount,
            resultTitle = "Run #$runCount - $tvSelectData",
            resultData = "Data yang digunakan: $edtDataName1, $edtDataName2",
            descriptiveTitle = "$edtDataName1 descriptive:",
            descriptiveContent = "n: $n1; Mean : $meanA; SD: $sdA",
            secondDescriptiveTitle = "$edtDataName2 descriptive:",
            secondDescriptiveContent = "n: $n2; Mean : $meanB; SD: $sdB",
            testValuesContent = "t-Value: $tScore; t-Critical: $tCritical",
            resultConclusion = conclusion,
            amountOfData = 2,
            hideTestValues = false
        )
    }

    override fun calculateOneWayAnova(
        dataAlpha: String,
        edtDataName1: String,
        edtDataValue1: String,
        edtDataName2: String,
        edtDataValue2: String,
        edtDataName3: String,
        edtDataValue3: String,
        runCount: Int,
        tvSelectData: String
    ): DataAnalysisResult {
        // Mengambil nilai input dari EditText
        val dataValue1 = edtDataValue1.split(" ").map { it.toDouble() }.toDoubleArray()
        val dataValue2 = edtDataValue2.split(" ").map { it.toDouble() }.toDoubleArray()
        val dataValue3 = edtDataValue3.split(" ").map { it.toDouble() }.toDoubleArray()
        val alpha = dataAlpha.toDouble()

        val n1 = dataValue1.size
        val n2 = dataValue2.size
        val n3 = dataValue3.size

        val mean1 = dataValue1.average()
        val mean2 = dataValue2.average()
        val mean3 = dataValue3.average()

        val sd1 = dataValue1.standardDeviation()
        val sd2 = dataValue2.standardDeviation()
        val sd3 = dataValue3.standardDeviation()

        // Hitung total mean
        val meanTotal = (mean1 + mean2 + mean3) / 3

        // Hitung SSB dan SSW
        val ssb = ((mean1 - meanTotal).pow(2) + (mean2 - meanTotal).pow(2) + (mean3 - meanTotal).pow(2))
        val ssw = (dataValue1.sumOf { (it - mean1).pow(2) } + dataValue2.sumOf { (it - mean2).pow(2) } + dataValue3.sumOf {
            (it - mean3).pow(2) }) / (dataValue1.size + dataValue2.size + dataValue3.size - 3)

        // Hitung dfB dan dfW
        val k = 3 // Jumlah kelompok
        val n = n1 + n2 + n3 // Total jumlah sampel
        val dfB = k - 1
        val dfW = n - k

        // Hitung MSB dan MSW
        val msb = ssb / dfB
        val msw = ssw / dfW

        // Hitung F-hitung
        val fHitung = msb / msw

        // Hitung F-tabel
        val fDist = FDistribution(dfB.toDouble(), dfW.toDouble())
        val fTabel = fDist.inverseCumulativeProbability(1 - alpha)

        // Tentukan kesimpulan
        val conclusion = if (fHitung > fTabel) {
            "Terdapat perbedaan signifikan antara rata-rata produktivitas di tiga divisi."
        } else {
            "Tidak terdapat perbedaan signifikan antara rata-rata produktivitas di tiga divisi."
        }

        return DataAnalysisResult(
            id = runCount,
            resultTitle = "Run #$runCount - $tvSelectData",
            resultData = "Data yang digunakan: $edtDataName1, $edtDataName2, $edtDataName3",
            descriptiveTitle = "$edtDataName1 descriptive:",
            descriptiveContent = "n: $n1; Mean: $mean1; SD: $sd1",
            secondDescriptiveTitle = "$edtDataName2 descriptive:",
            secondDescriptiveContent = "n: $n2; Mean: $mean2; SD: $sd2",
            thirdDescriptiveTitle = "$edtDataName3 descriptive:",
            thirdDescriptiveContent = "n: $n3; Mean: $mean3; SD: $sd3",
            testValuesContent = "F-Hitung: $fHitung; F-Tabel: $fTabel",
            resultConclusion = conclusion,
            amountOfData = 3,
            hideTestValues = false
        )
    }

    override fun calculateTwoWayAnova(
        dataAlpha: String,
        edtDataName1: String,
        edtDataValue1: String,
        edtDataName2: String,
        edtDataValue2: String,
        edtDataName3: String,
        edtDataValue3: String,
        edtDataName4: String,
        edtDataValue4: String,
        runCount: Int,
        tvSelectData: String
    ): DataAnalysisResult {
        val factor1dataValue1 = edtDataValue1.split(" ").map { it.toDouble() }.toDoubleArray()
        val factor1dataValue2 = edtDataValue2.split(" ").map { it.toDouble() }.toDoubleArray()
        val factor2dataValue1 = edtDataValue3.split(" ").map { it.toDouble() }.toDoubleArray()
        val factor2dataValue2 = edtDataValue4.split(" ").map { it.toDouble() }.toDoubleArray()
        val alpha = dataAlpha.toDouble()

        val n1 = factor1dataValue1.size
        val n2 = factor1dataValue2.size
        val n3 = factor2dataValue1.size
        val n4 = factor2dataValue2.size

        val meanFactor1Group1 = factor1dataValue1.average()
        val meanFactor1Group2 = factor1dataValue2.average()
        val meanFactor2Group1 = factor2dataValue1.average()
        val meanFactor2Group2 = factor2dataValue2.average()

        val sd1 = factor1dataValue1.standardDeviation()
        val sd2 = factor1dataValue2.standardDeviation()
        val sd3 = factor2dataValue1.standardDeviation()
        val sd4 = factor2dataValue2.standardDeviation()

        val meanTotal = (meanFactor1Group1 + meanFactor1Group2 + meanFactor2Group1 + meanFactor2Group2) / 4

        val ssbFactor1 = ((meanFactor1Group1 - meanTotal).pow(2) + (meanFactor1Group2 - meanTotal).pow(2))
        val sswFactor1 = (factor1dataValue1.sumOf { (it - meanFactor1Group1).pow(2) } + factor1dataValue2.sumOf { (it - meanFactor1Group2).pow(2) } / (factor1dataValue1.size + factor1dataValue2.size - 2))

        val ssbFactor2 = ((meanFactor2Group1 - meanTotal).pow(2) + (meanFactor2Group2 - meanTotal).pow(2))
        val sswFactor2 = (factor2dataValue1.sumOf { (it - meanFactor2Group1).pow(2) } + factor2dataValue2.sumOf { (it - meanFactor2Group2).pow(2) } / (factor2dataValue1.size + factor2dataValue2.size - 2))

        val k = 2 // Jumlah kelompok
        val nFactor1 = n1 + n2
        val nFactor2 = n3 + n4
        val dfBFactor1 = k - 1
        val dfBFactor2 = k - 1
        val dfWFactor1 = nFactor1 - k
        val dfWFactor2 = nFactor2 - k

        val msbFactor1 = ssbFactor1 / dfBFactor1
        val mswFactor1 = sswFactor1 / dfWFactor1
        val msbFactor2 = ssbFactor2 / dfBFactor2
        val mswFactor2 = sswFactor2 / dfWFactor2

        val fHitungFactor1 = msbFactor1 / mswFactor1
        val fHitungFactor2 = msbFactor2 / mswFactor2

        val fTabelFactor1 = FDistribution(dfBFactor1.toDouble(), dfWFactor1.toDouble()).inverseCumulativeProbability(1 - alpha)
        val fTabelFactor2 = FDistribution(dfBFactor2.toDouble(), dfWFactor2.toDouble()).inverseCumulativeProbability(1 - alpha)

/*        val grandMean = (meanFactor1Group1 + meanFactor1Group2 + meanFactor2Group1 + meanFactor2Group2) / 4

        val varianceFactor1Group1 = factor1dataValue1.sumOf { (it - meanFactor1Group1).pow(2) } / (factor1dataValue1.size - 1)
        val varianceFactor1Group2 = factor1dataValue2.sumOf { (it - meanFactor1Group2).pow(2) } / (factor1dataValue2.size - 1)
        val varianceFactor2Group1 = factor2dataValue1.sumOf { (it - meanFactor2Group1).pow(2) } / (factor2dataValue1.size - 1)
        val varianceFactor2Group2 = factor2dataValue2.sumOf { (it - meanFactor2Group2).pow(2) } / (factor2dataValue2.size - 1)

        val sd1 = factor1dataValue1.standardDeviation()
        val sd2 = factor1dataValue2.standardDeviation()
        val sd3 = factor2dataValue1.standardDeviation()
        val sd4 = factor2dataValue2.standardDeviation()

        val betweenGroupSumOfSquaresFactor1 = (meanFactor1Group1 - grandMean).pow(2) + (meanFactor1Group2 - grandMean).pow(2)
        val betweenGroupSumOfSquaresFactor2 = (meanFactor2Group1 - grandMean).pow(2) + (meanFactor2Group2 - grandMean).pow(2)

        val withinGroupSumOfSquares = (varianceFactor1Group1 + varianceFactor1Group2 + varianceFactor2Group1 + varianceFactor2Group2) / 2

        val dfBetweenGroupsFactor1 = 2 - 1
        val dfBetweenGroupsFactor2 = 2 - 1
        val dfBetweenGroupsInteraction = dfBetweenGroupsFactor1 * dfBetweenGroupsFactor2
        val dfWithinGroups = factor1dataValue1.size + factor1dataValue2.size + factor2dataValue1.size + factor2dataValue2.size - 4

        val fStatisticFactor1 = betweenGroupSumOfSquaresFactor1 / dfBetweenGroupsFactor1 / (withinGroupSumOfSquares / dfWithinGroups)
        val fStatisticFactor2 = betweenGroupSumOfSquaresFactor2 / dfBetweenGroupsFactor2 / (withinGroupSumOfSquares / dfWithinGroups)
        val fStatisticInteraction = fStatisticFactor1 * fStatisticFactor2

        val pValueFactor1 = 1 - FDistribution(dfBetweenGroupsFactor1.toDouble(), dfWithinGroups.toDouble()).cumulativeProbability(fStatisticFactor1)
        val pValueFactor2 = 1 - FDistribution(dfBetweenGroupsFactor2.toDouble(), dfWithinGroups.toDouble()).cumulativeProbability(fStatisticFactor2)
        val pValueInteraction = 1 - FDistribution(dfBetweenGroupsInteraction.toDouble(), dfWithinGroups.toDouble()).cumulativeProbability(fStatisticInteraction)*/

        val conclusionFactor1 = if (fHitungFactor1 > fTabelFactor1) {
            "Faktor 1 memiliki perbedaan yang signifikan"
        } else {
            "Faktor 1 tidak memiliki perbedaan yang signifikan"
        }

        val conclusionFactor2 = if (fHitungFactor2 > fTabelFactor2) {
            "Faktor 2 memiliki perbedaan yang signifikan"
        } else {
            "Faktor 2 tidak memiliki perbedaan yang signifikan"
        }

        return DataAnalysisResult(
            id = runCount,
            resultTitle = "Run #$runCount - $tvSelectData",
            resultData = "Data yang digunakan:\nFaktor 1: $edtDataName1, $edtDataName2\nFaktor 2: $edtDataName3, $edtDataName4",
            descriptiveTitle = "Faktor 1 ($edtDataName1, $edtDataName2) descriptive:",
            descriptiveContent = "Mean ($edtDataName1): $meanFactor1Group1; Mean ($edtDataName2): $meanFactor1Group2;\nSD ($edtDataName1): $sd1; SD ($edtDataName2): $sd2",
            secondDescriptiveTitle = "Faktor 2 ($edtDataName3, $edtDataName4) descriptive:",
            secondDescriptiveContent = "Mean ($edtDataName3): $meanFactor2Group1; Mean ($edtDataName4): $meanFactor2Group2;\nSD ($edtDataName3): $sd3; SD ($edtDataName4): $sd4",
            testValuesContent = "F-Hitung Faktor 1: $fHitungFactor1; F-Tabel Faktor 1: $fTabelFactor1;\nF-Hitung Faktor 2: $fHitungFactor2; F-Tabel Faktor 2: $fTabelFactor2;",
            resultConclusion = "- $conclusionFactor1\n- $conclusionFactor2",
            amountOfData = 2,
            hideTestValues = false
        )
    }

    override fun calculateRegresiLinearSederhana(
        dataAlpha: String,
        edtDataName1: String,
        edtDataValue1: String,
        edtDataName2: String,
        edtDataValue2: String,
        runCount: Int,
        tvSelectData: String
    ): DataAnalysisResult {
        val x = edtDataValue1.split(" ").map { it.toDouble() }.toDoubleArray()
        val y = edtDataValue2.split(" ").map { it.toDouble() }.toDoubleArray()

        val alpha = dataAlpha.toDouble()

        val n1 = x.size
        val n2 = y.size

        val mean1 = x.average()
        val mean2 = y.average()

        val sd1 = x.standardDeviation()
        val sd2 = y.standardDeviation()

        // membuat objek SimpleRegression dari library Apache Commons Math
        val regression = SimpleRegression()
        for (i in x.indices) {
            regression.addData(x[i], y[i])
        }

        // menghitung nilai koefisien slope (b1), koefisien intercept (b0), dan R-squared
        val b1 = regression.slope
        val b0 = regression.intercept
        val rSquared = regression.rSquare

        // uji signifikansi koefisien slope
        val tValue = b1 / regression.slopeStdErr
        val degreesOfFreedom = x.size - 2
        val pValue = TDistribution(degreesOfFreedom.toDouble()).cumulativeProbability(-abs(tValue)) * 2

        // Check if slope coefficient is significant
        val isB1Significant = pValue < alpha

        // uji kecocokan model dengan R-squared
        val rSquaredThreshold = 0.5 // set threshold to 0.5 as an example
        val isRSquaredSignificant = rSquared > rSquaredThreshold

        // menampilkan kesimpulan hipotesis regresi linear sederhana ke layar
        val conclusion =
            if (isB1Significant) {
                "Terdapat hubungan linier yang signifikan (alpha = $alpha). "
            } else {
                "Tidak terdapat hubungan linier yang signifikan (alpha = $alpha). "
            } +
                    if (isRSquaredSignificant) {
                        "R² signifikan dengan nilai = $rSquared"
                    } else {
                        "R² tidak signifikan dengan nilai = $rSquared"
                    }

        return DataAnalysisResult(
            id = runCount,
            resultTitle = "Run #$runCount - $tvSelectData",
            resultData = "Data yang digunakan: $edtDataName1, $edtDataName2",
            descriptiveTitle = "$edtDataName1 descriptive:",
            descriptiveContent = "n: $n1; Mean: $mean1; SD: $sd1",
            secondDescriptiveTitle = "$edtDataName2 descriptive:",
            secondDescriptiveContent = "n: $n2; Mean: $mean2; SD: $sd2",
            testValuesContent = "R\u00B2: $rSquared\nT-value: $tValue\nP-value: $pValue",
            resultConclusion = conclusion,
            amountOfData = 2,
            hideTestValues = false
        )
    }

    override fun calculateRegresiLinearBerganda(
        dataAlpha: String,
        edtDataName1: String,
        edtDataValue1: String,
        edtDataName2: String,
        edtDataValue2: String,
        edtDataName3: String,
        edtDataValue3: String,
        runCount: Int,
        tvSelectData: String
    ): DataAnalysisResult {
        // Mengambil nilai input dari EditText
        val x1 = edtDataValue1.split(" ").map { it.toDouble() }.toDoubleArray()
        val x2 = edtDataValue2.split(" ").map { it.toDouble() }.toDoubleArray()
        val y = edtDataValue3.split(" ").map { it.toDouble() }.toDoubleArray()
        val alpha = dataAlpha.toDouble()

        val n1 = x1.size
        val n2 = x2.size
        val n3 = y.size

        val mean1 = x1.average()
        val mean2 = x2.average()
        val mean3 = y.average()

        val sd1 = x1.standardDeviation()
        val sd2 = x2.standardDeviation()
        val sd3 = y.standardDeviation()

        val n = y.size
        val k = 2
        val xm = arrayOf(x1, x2).transpose()
        val regression = OLSMultipleLinearRegression()
        regression.newSampleData(y, xm)
        val b = regression.estimateRegressionParameters()
        val yMean = y.average()
        var ssTotal = 0.0
        var ssResidual = 0.0
        for (i in y.indices) {
            ssTotal += (y[i] - yMean).pow(2)
            ssResidual += (y[i] - (b[0] + b[1] * x1[i] + b[2] * x2[i])).pow(2)
        }
        val rSquared = 1 - ssResidual / ssTotal
        val adjustedRSquared = 1 - ((1 - rSquared) * (n - 1)) / (n - k - 1)

        // Hitung nilai fStatistic dan pValue
        val mse = ssResidual / (n - k - 1)
        val msr = (ssTotal - ssResidual) / (k - 1)
        val fStatistic = msr / mse
        val fDist = FDistribution((k - 1).toDouble(), (n - k).toDouble())
        val fTable = fDist.inverseCumulativeProbability(1 - alpha)
        val pValue = 1 - fDist.cumulativeProbability(fStatistic)

        // buat kondisi untuk menentukan apakah hipotesis nol diterima atau ditolak
        val conclusion = if (pValue < alpha) "Terjadi hubungan yang signifikan antara variabel independen dan variabel dependen." else "Tidak terjadi hubungan yang signifikan antara variabel independen dan variabel dependen."

        return DataAnalysisResult(
            id = runCount,
            resultTitle = "Run #$runCount - $tvSelectData",
            resultData = "Data yang digunakan: $edtDataName1, $edtDataName2",
            descriptiveTitle = "$edtDataName1 descriptive:",
            descriptiveContent = "n: $n1; Mean: $mean1; SD: $sd1",
            secondDescriptiveTitle = "$edtDataName2 descriptive:",
            secondDescriptiveContent = "n: $n2; Mean: $mean2; SD: $sd2",
            thirdDescriptiveTitle = "$edtDataName3 descriptive:",
            thirdDescriptiveContent = "n: $n3; Mean: $mean3; SD: $sd3",
            testValuesContent = "R\u00B2: $rSquared\nF-stat: $fStatistic\nF-table: $fTable\nP-value: $pValue",
            resultConclusion = conclusion,
            amountOfData = 3,
            hideTestValues = false
        )
    }

    override fun calculateUjiBinomial(
        dataAlpha: String,
        edtDataSuccessesName: String,
        edtDataSuccesses: String,
        edtDataTrialsName: String,
        edtDataTrials: String,
        edtDataExpectiationName: String,
        edtDataExpectation: String,
        runCount: Int,
        tvSelectData: String
    ): DataAnalysisResult {
        val trials = edtDataTrials.toInt()
        val successes = edtDataSuccesses.toInt()
        val expectation = edtDataExpectation.toDouble()
        val alpha = dataAlpha.toDouble()
        val alternativeHypothesis = AlternativeHypothesis.LESS_THAN

        val binomial = BinomialDistribution(trials, expectation)
        val pValue = 1.0 - binomial.cumulativeProbability(successes - 1)
        val conclusion = if (pValue < alpha) {
            "H0 ditolak, terdapat perbedaan yang signifikan antara nilai observasi dan nilai ekspektasi"
        } else {
            "H0 diterima, tidak terdapat perbedaan yang signifikan antara nilai observasi dan nilai ekspektasi"
        }

        return DataAnalysisResult(
            id = runCount,
            resultTitle = "Run #$runCount - $tvSelectData",
            resultData = "Data yang digunakan: $edtDataTrialsName, $edtDataSuccessesName, $edtDataExpectiationName",
            descriptiveTitle = "$edtDataTrialsName descriptive:",
            descriptiveContent = null,
            secondDescriptiveTitle = "$edtDataSuccessesName descriptive:",
            secondDescriptiveContent = null,
            thirdDescriptiveTitle = "$edtDataExpectiationName descriptive:",
            thirdDescriptiveContent = null,
            testValuesContent = "p-Value: $pValue",
            resultConclusion = conclusion,
            amountOfData = 3,
            hideTestValues = false
        )
    }

    override fun calculateMannWhitney(
        dataAlpha: String,
        edtDataName1: String,
        edtDataValue1: String,
        edtDataName2: String,
        edtDataValue2: String,
        runCount: Int,
        tvSelectData: String
    ): DataAnalysisResult {
        val dataValue1 = edtDataValue1.split(" ").map { it.toDouble() }.toDoubleArray()
        val dataValue2 = edtDataValue2.split(" ").map { it.toDouble() }.toDoubleArray()
        val alpha = dataAlpha.toDouble()

        val n1 = dataValue1.size
        val n2 = dataValue2.size

        val mean1 = dataValue1.average()
        val mean2 = dataValue2.average()

        val sd1 = dataValue1.standardDeviation()
        val sd2 = dataValue2.standardDeviation()

        val test = MannWhitneyUTest()
        val uValue = test.mannWhitneyU(dataValue1, dataValue2)
        val pValue = test.mannWhitneyUTest(dataValue1, dataValue2)
        val conclusion = if (pValue < alpha) {
            "Terdapat perbedaan yang signifikan antara kedua sampel dengan nilai p=$pValue (α=$alpha)"
        } else {
            "Tidak terdapat perbedaan yang signifikan antara kedua sampel dengan nilai p=$pValue (α=$alpha)"
        }

        return DataAnalysisResult(
            id = runCount,
            resultTitle = "Run #$runCount - $tvSelectData",
            resultData = "Data yang digunakan: $edtDataName1, $edtDataName2",
            descriptiveTitle = "$edtDataName1 descriptive:",
            descriptiveContent = "n: $n1; Mean: $mean1; SD: $sd1",
            secondDescriptiveTitle = "$edtDataName2 descriptive:",
            secondDescriptiveContent = "n: $n2; Mean: $mean2; SD: $sd2",
            testValuesContent = "U-value: $uValue; p-Value: $pValue",
            resultConclusion = conclusion,
            amountOfData = 2,
            hideTestValues = false
        )
    }

    override fun calculateKruskalWallis(
        dataAlpha: String,
        edtDataName1: String,
        edtDataValue1: String,
        edtDataName2: String,
        edtDataValue2: String,
        edtDataName3: String,
        edtDataValue3: String,
        runCount: Int,
        tvSelectData: String
    ): DataAnalysisResult {
        val dataValue1 = edtDataValue1.split(" ").map { it.toDouble() }.toDoubleArray()
        val dataValue2 = edtDataValue2.split(" ").map { it.toDouble() }.toDoubleArray()
        val dataValue3 = edtDataValue3.split(" ").map { it.toDouble() }.toDoubleArray()
        val alpha = dataAlpha.toDouble()

        val n1 = dataValue1.size
        val n2 = dataValue2.size
        val n3 = dataValue3.size

        val mean1 = dataValue1.average()
        val mean2 = dataValue2.average()
        val mean3 = dataValue3.average()

        val sd1 = dataValue1.standardDeviation()
        val sd2 = dataValue2.standardDeviation()
        val sd3 = dataValue3.standardDeviation()

        // Combine the data from all three groups
        val allData = dataValue1 + dataValue2 + dataValue3

        // Calculate the rank sum for each group
        val rankSum1 = rankSum(dataValue1)
        val rankSum2 = rankSum(dataValue2)
        val rankSum3 = rankSum(dataValue3)

        // Calculate the overall rank sum
        val allRankSum = rankSum(allData)

        // Calculate the test statistic H
        val n = allData.size
        val k = 3 // number of groups
        val H = 12.0 / (n * (n + 1)) * ((allRankSum.pow(2)) / k) - 3 * (n + 1)

        // Calculate the critical value using the chi-squared distribution
        val df = k - 1
        val criticalValue = chiSquaredDistributionInverse(df, alpha)

        // Compare the test statistic to the critical value
        val conclusion = if (H > criticalValue) {
            "Reject the null hypothesis. There is a significant difference between the groups."
        } else {
            "Fail to reject the null hypothesis. There is not enough evidence to conclude that there is a significant difference between the groups."
        }

        return DataAnalysisResult(
            id = runCount,
            resultTitle = "Run #$runCount - $tvSelectData",
            resultData = "Data yang digunakan: $edtDataName1, $edtDataName2, $edtDataName3",
            descriptiveTitle = "$edtDataName1 descriptive:",
            descriptiveContent = "n: $n1; Mean: $mean1; SD: $sd1",
            secondDescriptiveTitle = "$edtDataName2 descriptive:",
            secondDescriptiveContent = "n: $n2; Mean: $mean2; SD: $sd2",
            thirdDescriptiveTitle = "$edtDataName3 descriptive:",
            thirdDescriptiveContent = "n: $n3; Mean: $mean3; SD: $sd3",
            testValuesContent = "H: $H; Critical Value: $criticalValue",
            resultConclusion = conclusion,
            amountOfData = 3,
            hideTestValues = false
        )
    }

    // Function to calculate the rank sum of a given array
    private fun rankSum(data: DoubleArray): Double {
        val sortedData = data.sorted()
        val rank = sortedData.indices.map { i -> (2 * i + 1).toDouble() }.toDoubleArray()
        return rank.sum()
    }

    // Function to calculate the inverse of the chi-squared distribution
    private fun chiSquaredDistributionInverse(df: Int, alpha: Double): Double {
        val chi2 = ChiSquaredDistribution(df.toDouble())
        return chi2.inverseCumulativeProbability(1 - alpha)
    }

    private fun DoubleArray.standardDeviation(): Double {
        val mean = average()
        var sum = 0.0
        for (value in this) {
            sum += (value - mean).pow(2.0)
        }
        return sqrt(sum / (size - 1))
    }

    private fun Array<DoubleArray>.transpose(): Array<DoubleArray> {
        val n = this.size
        val m = this[0].size
        val transposed = Array(m) { DoubleArray(n) }
        for (i in 0 until n) {
            for (j in 0 until m) {
                transposed[j][i] = this[i][j]
            }
        }
        return transposed
    }

    private fun normalCDF(z: Double): Double {
        return 0.5 * (1 + erfc(-z / sqrt(2.0)))
    }

    private fun rankData(data: DoubleArray): DoubleArray {
        val sortedData = data.sortedArrayDescending()
        val rank = DoubleArray(sortedData.size)
        var i = 0
        while (i < sortedData.size) {
            var j = i
            while (j < sortedData.size && sortedData[j] == sortedData[i]) {
                j++
            }
            val avgRank = (i + j - 1) / 2.0 + 1
            for (k in i until j) {
                rank[k] = avgRank
            }
            i = j
        }
        return rank
    }

    override fun addResult(result: DataAnalysisResult) {
        val currentList = resultList.value ?: emptyList()
        resultList.value = currentList + result
    }

    override fun getResultList(): LiveData<List<DataAnalysisResult>> {
        return resultList
    }

    override fun clearResults() {
        resultList.value = emptyList()
    }
}