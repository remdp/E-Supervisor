package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.imagesPage

import android.os.Bundle
import android.view.View
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.designByResult
import com.euromix.esupervisor.app.utils.simplyMessageDialog
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.databinding.ImagesFragmentBinding
import com.euromix.esupervisor.dialogs.dialogReactDislike.DialogReactDislikeFragment
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

        viewModel.viewState.observe(viewLifecycleOwner) { state ->
            viewModel.afterUpdateState()

            if (state.imagesResult is Success && !state.needLoading) {

                //todo move list comparison to adapter
                // if (adapter.getImagesRvList() != state.imagesResult.value) adapter.setImages(state.imagesResult.value)
                val imageReaction = state.imagesResult.value
                adapter.setImages(imageReaction.rows)

                if (imageReaction.creationDislikeTaskMessage.isNotBlank())
                    simplyMessageDialog(
                        requireContext(),
                        imageReaction.creationDislikeTaskMessage,
                        getString(R.string.create_task_next_visit_result)
                    )
            }

            designByResult(
                state.imagesResult, binding.root, binding.vResult
            )
        }
    }

    private fun setupListeners() {
        binding.vResult.setTryAgainAction { viewModel.reload() }
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
        //dialog.show(parentFragmentManager, App.getString(requireContext(), R.string.dislike_reason))

//        dialogReactDislike(
//            requireContext(),
//            R.string.dislike_reason,
//            R.string.create_task_next_visit,
//            parentFragmentManager,
//            abilityCreateTask = abilityCreateTask
//        ) { enteredText, additionalFlag, deadline ->
//            viewModel.react(
//                reaction.copy(
//                    comment = enteredText,
//                    createDislikeTask = additionalFlag,
//                    deadline = formattedDate(deadline),
//                )
//            )
//        }


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