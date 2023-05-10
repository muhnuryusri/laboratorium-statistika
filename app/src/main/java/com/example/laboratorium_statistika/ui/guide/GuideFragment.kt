package com.example.laboratorium_statistika.ui.guide

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.laboratorium_statistika.R
import com.example.laboratorium_statistika.databinding.FragmentGuideBinding
import java.io.BufferedReader
import java.io.InputStreamReader

class GuideFragment : Fragment() {
    private lateinit var binding: FragmentGuideBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGuideBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButtonText.setOnClickListener {
            findNavController().navigateUp()
        }

        val inputStream = requireContext().assets.open("web_view/guide.html")
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        val content = bufferedReader.use { it.readText() }

        binding.webview.loadDataWithBaseURL(null, content, "text/html", "utf-8", null)
    }
}