package com.example.laboratorium_statistika.ui.module.tab

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.activity.addCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.laboratorium_statistika.R
import com.example.laboratorium_statistika.databinding.FragmentModuleTabBinding
import com.example.laboratorium_statistika.model.Module
import com.example.laboratorium_statistika.model.ModuleTab
import com.example.laboratorium_statistika.repository.ModuleRepositoryImpl
import com.example.laboratorium_statistika.ui.module.adapter.ModuleTabAdapter
import com.example.laboratorium_statistika.viewmodel.ModuleViewModelFactory
import java.io.BufferedReader
import java.io.InputStreamReader

class ModuleTabFragment : Fragment() {
    private lateinit var binding: FragmentModuleTabBinding
    private lateinit var adapter: ModuleTabAdapter
    private lateinit var viewModel: ModuleTabViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentModuleTabBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButtonText.setOnClickListener {
            findNavController().navigateUp()
        }

        showModuleTabList()

        val module = arguments?.getParcelable<Module>("module")

        // Mengambil konten Markdown dari file di dalam folder assets
        val inputStreamModule = module?.module?.let { requireContext().assets.open(it) }
        val bufferedReaderModule = BufferedReader(InputStreamReader(inputStreamModule))
        val contentModule = bufferedReaderModule.use { it.readText() }

        binding.moduleWebview.loadDataWithBaseURL(null, contentModule, "text/html", "utf-8", null)

        adapter.onItemClick { moduleTab ->
            binding.layoutRecyclerViewContainer.visibility = View.GONE
            binding.layoutWebviewContainer.visibility = View.VISIBLE

            val inputStreamModuleTab = moduleTab.module?.let { requireContext().assets.open(it) }
            val bufferedReaderModuleTab = BufferedReader(InputStreamReader(inputStreamModuleTab))
            val contentModuleTab = bufferedReaderModuleTab.use { it.readText() }

            binding.moduleTabWebView.loadDataWithBaseURL(null, contentModuleTab, "text/html", "utf-8", null)
            binding.moduleTabWebView.settings.textZoom = 70

            binding.btnBack.setOnClickListener {
                binding.moduleTabWebView.loadData("", "text/html", "utf-8")
                binding.layoutRecyclerViewContainer.visibility = View.VISIBLE
                binding.layoutWebviewContainer.visibility = View.GONE
            }
        }

        var resultLayoutHeight = 0
        binding.layoutResult.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.layoutResult.viewTreeObserver.removeOnGlobalLayoutListener(this)
                resultLayoutHeight = binding.layoutResult.height
            }
        })

        binding.tvSubmodule.post {
            val layoutParams = binding.layoutSubmoduleContainer.layoutParams
            layoutParams.height = binding.tvSubmodule.height
            binding.layoutSubmoduleContainer.layoutParams = layoutParams
        }

        binding.tvSubmodule.setOnClickListener {
            val screenHeight = Resources.getSystem().displayMetrics.heightPixels
            val desiredHeight = (screenHeight * 0.75).toInt()
            val layoutParams = binding.layoutSubmoduleContainer.layoutParams
            val measureSpec = View.MeasureSpec.makeMeasureSpec(screenHeight, View.MeasureSpec.AT_MOST)
            binding.layoutResult.measure(measureSpec, measureSpec)

            val animator = if (layoutParams.height == binding.tvSubmodule.height) {
                ValueAnimator.ofInt(binding.tvSubmodule.height, desiredHeight)
            } else {
                ValueAnimator.ofInt(binding.layoutSubmoduleContainer.measuredHeight, binding.tvSubmodule.height)
            }

            animator.addUpdateListener {
                val value = it.animatedValue as Int
                layoutParams.height = value
                binding.layoutSubmoduleContainer.layoutParams = layoutParams
                binding.layoutSubmoduleContainer.invalidate() // <-- call invalidate() instead of requestLayout()
            }

            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    if (layoutParams.height == binding.tvSubmodule.height) {
                        binding.tvSubmodule.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.round_keyboard_arrow_up_24, 0)
                    } else {
                        binding.tvSubmodule.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.round_keyboard_arrow_down_24, 0)
                    }
                }
            })

            animator.duration = 300
            animator.start()

            val overlay = binding.overlay

            val overlayAnimator = if (layoutParams.height == binding.tvSubmodule.height) {
                overlay.visibility = View.VISIBLE
                ObjectAnimator.ofFloat(overlay, "alpha", 0f, 1f)
            } else {
                ObjectAnimator.ofFloat(overlay, "alpha", 1f, 0f).apply {
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            overlay.visibility = View.GONE
                        }
                    })
                }
            }

            overlayAnimator.duration = 300
            overlayAnimator.start()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            onBackButtonPressed()
        }
    }

    private fun onBackButtonPressed() {
        if (binding.layoutWebviewContainer.visibility == View.VISIBLE) {
            binding.moduleTabWebView.loadData("", "text/html", "utf-8")
            binding.layoutRecyclerViewContainer.visibility = View.VISIBLE
            binding.layoutWebviewContainer.visibility = View.GONE
        } else if (binding.layoutSubmoduleContainer.height > binding.tvSubmodule.height) {
            binding.overlay.visibility = View.GONE
            val animator = ValueAnimator.ofInt(binding.layoutSubmoduleContainer.measuredHeight, binding.tvSubmodule.height)
            animator.addUpdateListener {
                val value = it.animatedValue as Int
                val layoutParams = binding.layoutSubmoduleContainer.layoutParams
                layoutParams.height = value
                binding.layoutSubmoduleContainer.layoutParams = layoutParams
                binding.layoutSubmoduleContainer.invalidate()
            }
            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    binding.tvSubmodule.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.round_keyboard_arrow_down_24, 0)
                }
            })
            animator.duration = 300
            animator.start()
        } else {
            if (findNavController().currentBackStackEntry != null) {
                findNavController().popBackStack()
            } else {
                requireActivity().finish()
            }
        }
    }

    private fun showModuleTabList() {
        adapter = ModuleTabAdapter()
        binding.rvModuleTab.adapter = adapter
        binding.rvModuleTab.layoutManager = LinearLayoutManager(activity)

        val id = arguments?.getInt("modules")
        val repository = ModuleRepositoryImpl(requireContext())
        viewModel = ViewModelProvider(this, ModuleViewModelFactory(repository))[ModuleTabViewModel::class.java]

        val modules = id?.let { viewModel.getModuleTab(it) }
        if (id != null) {
            binding.rvModuleTab.adapter?.let { adapter ->
                if (adapter is ModuleTabAdapter) {
                    if (modules != null) {
                        adapter.setItems(modules)
                    }
                }
            }
        }
    }
}