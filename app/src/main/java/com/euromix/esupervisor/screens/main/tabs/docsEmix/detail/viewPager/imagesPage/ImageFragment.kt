package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.imagesPage

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.designByResult
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.databinding.ImageFragmentBinding
import com.euromix.esupervisor.screens.viewModelCreator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ImageFragment : BaseFragment(R.layout.image_fragment) {

    @Inject
    lateinit var factory: ImageViewModel.Factory
    override val viewModel by viewModelCreator { factory.create(navArgs.imageUri) }

    private val binding by viewBinding<ImageFragmentBinding>()
    private val navArgs by navArgs<ImageFragmentArgs>()

    private val adapter = ImageAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide.with(view).load(navArgs.imageUri).into(binding.iv)
        binding.rvReactions.adapter = adapter

        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        binding.ivClose.setOnClickListener { findNavController().popBackStack() }
        binding.vResult.setTryAgainAction { viewModel.reload() }
    }

    private fun setupObservers() {
        viewModel.viewState.observe(viewLifecycleOwner) {
            renderState(it)
            designByResult(it.resultReactions, binding.root, binding.vResult)
        }
    }

    private fun renderState(viewState: ImageViewModel.ViewState) {

        val resultReactions = viewState.resultReactions
        if (resultReactions is Success)
            adapter.submitList(resultReactions.value)
    }

}