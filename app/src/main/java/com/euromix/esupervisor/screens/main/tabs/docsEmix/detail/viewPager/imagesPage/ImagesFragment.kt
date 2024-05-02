package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.imagesPage

import android.os.Bundle
import android.view.View
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.designByViewState
import com.euromix.esupervisor.app.utils.observeEvent
import com.euromix.esupervisor.app.utils.simplyMessageDialog
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.databinding.ImagesFragmentBinding
import com.euromix.esupervisor.dialogs.dialogReactDislike.DialogReactDislikeFragment
import com.euromix.esupervisor.screens.main.BaseViewState
import com.euromix.esupervisor.screens.viewModelCreator
import com.euromix.esupervisor.sources.docsEmixDetail.entities.ImageReactionRequestEntity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ImagesFragment : BaseFragment(R.layout.images_fragment) {

    @Inject
    lateinit var factory: ImagesViewModel.Factory
    override val viewModel by viewModelCreator {
        factory.create(extId)
    }
    private val binding by viewBinding<ImagesFragmentBinding>()

    private lateinit var openerImageFragment: (imageUri: String) -> Unit

    private val adapter =
        ImagesAdapter(::imageOnClickListener, ::dislikeOnClickListener, ::likeOnClickListener)

    private lateinit var extId: String
    private var abilityCreateTask: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvImages.adapter = adapter
        setupListeners()
        setupObservers()
    }

    private fun setupObservers() {

        viewModel.viewStateEvent.observeEvent(viewLifecycleOwner) {
            renderState()
        }
    }

    private fun setupListeners() {
        binding.vResult.setTryAgainAction { viewModel.reload() }
    }

    private fun renderState() {
        val viewState = viewModel.viewState
        designByViewState(
            viewState as BaseViewState, binding.root, binding.vResult
        )

        viewState.imagesReactions?.let {imagesReactions->

            adapter.setImages(imagesReactions.rows)

            if (imagesReactions.creationDislikeTaskMessage.isNotBlank()) {
                simplyMessageDialog(
                    requireContext(),
                    imagesReactions.creationDislikeTaskMessage,
                    getString(R.string.create_task_next_visit_result)
                )
                viewModel.clearCreationDislikeTaskMessage()
            }
        }
    }

    private fun imageOnClickListener(imageUri: String) {
        openerImageFragment(imageUri)
    }

    private fun dislikeOnClickListener(reaction: ImageReactionRequestEntity) {

        val dialog = DialogReactDislikeFragment.newInstance(
            abilityCreateTask,
            extId
        ) { reason, createDislikeTask, deadline ->

            viewModel.react(
                reaction.copy(
                    comment = reason,
                    createDislikeTask = createDislikeTask,
                    deadline = deadline
                )
            )

        }
        dialog.show(parentFragmentManager, null)
    }

    private fun likeOnClickListener(reaction: ImageReactionRequestEntity) {
        viewModel.react(reaction)
    }

    companion object {

        fun newInstance(
            extId: String,
            abilityCreateTask: Boolean,
            openerImageFragment: (imageUri: String) -> Unit
        ): ImagesFragment = ImagesFragment().apply {

            this.extId = extId
            this.abilityCreateTask = abilityCreateTask
            this.openerImageFragment = openerImageFragment

        }
    }
}