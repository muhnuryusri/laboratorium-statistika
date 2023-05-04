package com.example.laboratorium_statistika.ui.module.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.laboratorium_statistika.databinding.FragmentDetailModuleBinding
import com.example.laboratorium_statistika.model.Module
import com.example.laboratorium_statistika.model.ModuleTab
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class DetailModuleFragment : Fragment() {
    private lateinit var binding: FragmentDetailModuleBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailModuleBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButtonText.setOnClickListener {
            findNavController().navigateUp()
        }

        val module = arguments?.getParcelable<Module>("module")
        val moduleTab = arguments?.getParcelable<ModuleTab>("tab")

        if (module != null) {
            showModuleWebView(module)
        } else if (moduleTab != null) {
            showModuleTabWebView(moduleTab)
        }
    }

    private fun showModuleWebView(module: Module?) {
        // Mengambil konten Markdown dari file di dalam folder assets
        val inputStream = module?.module?.let { requireContext().assets.open(it) }
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        val content = bufferedReader.use { it.readText() }

        binding.webview.loadDataWithBaseURL(null, content, "text/html", "utf-8", null)
    }

    private fun showModuleTabWebView(moduleTab: ModuleTab?) {
        // Mengambil konten Markdown dari file di dalam folder assets
        val inputStream = moduleTab?.module?.let { requireContext().assets.open(it) }
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        val content = bufferedReader.use { it.readText() }

        binding.webview.loadDataWithBaseURL(null, content, "text/html", "utf-8", null)
    }

/*    private fun showModuleDetail(module: Module?) {
        binding.apply {
            tvTitle.text = module?.title
            tvDescription.text = module?.description
        }
    }

    private fun showModuleTabDetail(moduleTab: ModuleTab?) {
        binding.apply {
            tvTitle.text = moduleTab?.title
            tvDescription.text = moduleTab?.description
        }
    }*/
}