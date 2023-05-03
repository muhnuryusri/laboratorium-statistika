package com.example.laboratorium_statistika.ui.table

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.laboratorium_statistika.databinding.FragmentDetailTableBinding
import com.github.barteksc.pdfviewer.PDFView

class DetailTableFragment : Fragment() {
    private lateinit var binding: FragmentDetailTableBinding
    private lateinit var pdfView: PDFView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailTableBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pdfFileName = arguments?.getString("pdf_file_name")

        pdfView = binding.pdfView
        pdfFileName?.let { loadPDF(it) }
    }

    private fun loadPDF(pdfFileName: String) {
        try {
            val inputStream = requireContext().assets.open(pdfFileName)

            pdfView.fromStream(inputStream)
                .defaultPage(0)
                .enableDoubletap(true)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .onError { t -> Toast.makeText(context, t.localizedMessage, Toast.LENGTH_SHORT).show() }
                .load()

        } catch (e: Exception) {
            Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }
}