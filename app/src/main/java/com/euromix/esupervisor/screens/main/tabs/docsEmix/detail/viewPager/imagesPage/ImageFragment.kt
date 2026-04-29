package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.imagesPage

import android.os.Bundle
import android.view.View
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.designByViewState
import com.euromix.esupervisor.app.utils.gone
import com.euromix.esupervisor.app.utils.observeEvent
import com.euromix.esupervisor.app.utils.setBitmapFromBase64String
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.app.utils.visible
import com.euromix.esupervisor.databinding.ImageFragmentBinding
import com.euromix.esupervisor.screens.main.BaseViewState
import com.euromix.esupervisor.screens.viewModelCreator
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ImageFragment : BaseFragment(R.layout.image_fragment) {

    @Inject
    lateinit var factory: ImageViewModel.Factory
    override val viewModel by viewModelCreator { factory.create(if (navArgs.imageBase64.isNullOrEmpty()) navArgs.imageUri else null) }

    private val binding by viewBinding<ImageFragmentBinding>()
    private val navArgs by navArgs<ImageFragmentArgs>()

    private val adapter = ImageAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvReactions.adapter = adapter

        setupContent()
        setupListeners()
        setupObservers()
    }

    private fun setupContent() {
        val base64 = navArgs.imageBase64

        if (!base64.isNullOrEmpty()) {
            binding.iv.setBitmapFromBase64String(base64)

            binding.rvReactions.gone()
            binding.clAppbarBottom.visible()

        } else {
            Glide.with(this).load(navArgs.imageUri).into(binding.iv)

            binding.rvReactions.visible()
            binding.clAppbarBottom.gone()
        }
    }

    private fun setupListeners() {
        binding.vResult.setTryAgainAction { viewModel.reload() }
        binding.btnDel.setOnClickListener { showDeleteConfirmation() }
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
    }

    private fun showDeleteConfirmation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.photo_deletion)
            .setMessage(R.string.are_you_sure)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                deletePhotoAndExit()
            }
            .show()
    }

    private fun deletePhotoAndExit() {
        val result = Bundle().apply {
            putInt(DELETED_POSITION, navArgs.imagePosition)
        }
        setFragmentResult(PHOTO_DELETION_REQUEST, result)

        findNavController().popBackStack()
    }

    private fun setupObservers() {
        viewModel.viewStateEvent.observeEvent(viewLifecycleOwner) {
            renderState()
        }
    }

    private fun renderState() {
        val viewState = viewModel.viewState
        designByViewState(
            viewState as BaseViewState, binding.root, binding.vResult,
            specialViews = listOf(binding.iv, binding.clAppbarBottom)
        )
        adapter.submitList(viewState.reactions)
    }

    companion object{
        const val PHOTO_DELETION_REQUEST = "photo_deletion_request"
        const val DELETED_POSITION = "deleted_position"
    }
}