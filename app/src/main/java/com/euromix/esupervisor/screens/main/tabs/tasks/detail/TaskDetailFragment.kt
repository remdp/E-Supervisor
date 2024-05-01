package com.euromix.esupervisor.screens.main.tabs.tasks.detail

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.enums.TaskState
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.designByViewState
import com.euromix.esupervisor.app.utils.observeEvent
import com.euromix.esupervisor.app.utils.textDate
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.databinding.TaskDetailFragmentBinding
import com.euromix.esupervisor.screens.main.BaseViewState
import com.euromix.esupervisor.screens.main.tabs.TitleData
import com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.viewPager.imagesPage.ImagesFragment
import com.euromix.esupervisor.screens.viewModelCreator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TaskDetailFragment : BaseFragment(R.layout.task_detail_fragment) {

    @Inject
    lateinit var factory: TaskDetailViewModel.Factory

    override val viewModel by viewModelCreator { factory.create(args.id) }
    private val binding by viewBinding<TaskDetailFragmentBinding>()
    private val args by navArgs<TaskDetailFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupListeners()
        setupImagesFragment(args.id, args.titleData)

    }

    private fun setupObservers() {

        viewModel.viewStateEvent.observeEvent(viewLifecycleOwner) {
            renderState()
        }
    }

    private fun renderState() {

        designByViewState(
            viewModel.viewState as BaseViewState, binding.root, binding.vResult
        )
        viewModel.viewState.taskDetail?.let { taskDetail ->

            with(binding) {

                tvTaskType.text = taskDetail.taskType
                TaskState.designTV(tvTaskState, taskDetail.taskState)
                tvDeadline.text = textDate(taskDetail.deadline)
                tvExecutor.text = taskDetail.executor
                tvDescription.text = taskDetail.description
                tvPartner.text = taskDetail.partner
                tvOutlet.text = taskDetail.outlet
                tvAttachPhoto.setCompoundDrawablesWithIntrinsicBounds(
                    if (taskDetail.attachPhoto) R.drawable.ic_checkbox_on else R.drawable.ic_checkbox_off,
                    0,
                    0,
                    0
                )
            }
        }
    }

    private fun setupListeners() {
        binding.vResult.setTryAgainAction { viewModel.reload() }
    }

    private fun setupImagesFragment(id: String, titleData: TitleData) {

        if (titleData.startText != null && titleData.endText != null) {

            childFragmentManager.beginTransaction().apply {
                replace(
                    R.id.fragmentContainerView,
                    ImagesFragment.newInstance(
                        extId = id,
                        abilityCreateTask = true
                    ) { imageUri ->
                        val direction =
                            TaskDetailFragmentDirections.actionTaskDetailFragmentToImageFragment(
                                imageUri = imageUri,
                                titleData = titleData
                            )

                        findNavController().navigate(direction)
                    }
                )
                commit()
            }
        }
    }
}