package com.example.laboratorium_statistika.ui.table

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.laboratorium_statistika.databinding.FragmentTableBinding
import com.github.barteksc.pdfviewer.PDFView

class TableFragment : Fragment() {
    private lateinit var binding: FragmentTableBinding
    private lateinit var pdfView: PDFView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTableBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButtonText.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnTTable.setOnClickListener {
            launchDetailTableFragment("table_pdf/t_table.pdf")
        }

        binding.btnZTable.setOnClickListener {
            launchDetailTableFragment("table_pdf/z_table.pdf")
        }

        binding.btnFTable.setOnClickListener {
            launchDetailTableFragment("table_pdf/f_table.pdf")
        }

        binding.btnRTable.setOnClickListener {
            launchDetailTableFragment("table_pdf/r_table.pdf")
        }

        binding.btnRandomTable.setOnClickListener {
            launchDetailTableFragment("table_pdf/durbin_watson_table.pdf")
        }
    }

    private fun launchDetailTableFragment(pdfFileName: String) {
        val action = TableFragmentDirections.actionTableFragmentToDetailTableFragment(pdfFileName)
        findNavController().navigate(action)
    }
}