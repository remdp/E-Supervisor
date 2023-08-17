package com.euromix.esupervisor.screens.main.tabs.tasks.selection

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.euromix.esupervisor.App
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.Const.MIN_LENGTH_SEARCH_STRING
import com.euromix.esupervisor.app.model.tasks.entities.TasksSelection
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.observeResults
import com.euromix.esupervisor.app.utils.popupWindowForSelections
import com.euromix.esupervisor.app.utils.setEtOnEditorActionListener
import com.euromix.esupervisor.app.utils.setOnClickListenerLocalSelection
import com.euromix.esupervisor.app.utils.setOnClickListenerServerSelection
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.databinding.TasksSelectionFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksSelectionFragment : BaseFragment(R.layout.tasks_selection_fragment) {

    override val viewModel by viewModels<TasksSelectionViewModel>()

    private val binding by viewBinding<TasksSelectionFragmentBinding>()
    private val args by navArgs<TasksSelectionFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.updateSelection(args.selection)

        setupListeners()
        setupObservers(view)

    }

    private fun setupListeners() {

        binding.btnOk.setOnClickListener {
            val direction =
                TasksSelectionFragmentDirections.actionTasksSelectionFragmentToTasksFragment(
                    selection = viewModel.selection.value
                )
            findNavController().navigate(direction)
        }

        binding.btnCancel.setOnClickListener { findNavController().popBackStack() }

        binding.etPartner.setOnClickListener { binding.tiPartner.error = null }
        binding.etPartner.setOnFocusChangeListener { _, _ -> binding.tiPartner.error = null }
        binding.etPartner.setEtOnEditorActionListener(viewModel::findPartners)
        binding.etPartner.setOnClickListenerServerSelection(viewModel::updatePartnerSelection)

        binding.tvTaskState.setOnClickListenerLocalSelection(
            viewModel.getTasksStateForChoose(requireContext()),
            viewModel::updateTaskStateSelection,
            viewModel::handleViewClick,
            viewModel::checkTaskStateEmpty
        )

        binding.etExecutor.setOnClickListener { binding.tiExecutor.error = null }
        binding.etExecutor.setOnFocusChangeListener { _, _ -> binding.tiExecutor.error = null }
        binding.etExecutor.setEtOnEditorActionListener(viewModel::findExecutors)
        binding.etExecutor.setOnClickListenerServerSelection(viewModel::updateExecutorsSelection)

        binding.tvOnlyMyTasks.setOnClickListener {
            val onlyMy = !(viewModel.selection.value?.onlyMy ?: false)
            viewModel.updateOnlyMy(onlyMy)
        }

        binding.tvOnlyOverdueTasks.setOnClickListener {
            val onlyOverdue = !(viewModel.selection.value?.onlyOverdue ?: false)
            viewModel.updateOnlyOverdue(onlyOverdue)
        }

        binding.ivArrowBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvClear.setOnClickListener {
            viewModel.clearSelection()
        }

    }

    private fun setupObservers(view: View) {

        viewModel.selection.observe(viewLifecycleOwner) {

            binding.etPartner.setText(it?.partner?.presentation)
            binding.etPartner.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                if (it?.partner == null) R.drawable.ic_arrow_drop_down_gray_400 else R.drawable.ic_cross_gray_300,
                0
            )

            binding.tvTaskType.text = it?.taskType?.presentation
            binding.tvTaskType.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                if (it?.taskType == null) R.drawable.ic_arrow_drop_down_gray_400 else R.drawable.ic_cross_gray_300,
                0
            )

            binding.tvTaskState.text = it?.taskState?.presentation
            binding.tvTaskState.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                if (it?.taskState == null) R.drawable.ic_arrow_drop_down_gray_400 else R.drawable.ic_cross_gray_300,
                0
            )

            binding.etExecutor.setText(it?.executor?.presentation)
            binding.etExecutor.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                if (it?.executor == null) R.drawable.ic_arrow_drop_down_gray_400 else R.drawable.ic_cross_gray_300,
                0
            )

            binding.tvOnlyMyTasks.setCompoundDrawablesWithIntrinsicBounds(
                if (it?.onlyMy == true) R.drawable.ic_checkbox_white_on else R.drawable.ic_checkbox_white_off,
                0,
                0,
                0
            )

            binding.tvOnlyOverdueTasks.setCompoundDrawablesWithIntrinsicBounds(
                if (it?.onlyOverdue == true) R.drawable.ic_checkbox_white_on else R.drawable.ic_checkbox_white_off,
                0,
                0,
                0
            )

            if (TasksSelection.isEmpty(it)) {
                binding.tvClear.setTextColor(App.getColor(requireContext(), R.color.gray_500))
                binding.tvClear.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_gray_basket,
                    0,
                    0,
                    0
                )
            } else {
                binding.tvClear.setTextColor(App.getColor(requireContext(), R.color.blue))
                binding.tvClear.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_blue_basket,
                    0,
                    0,
                    0
                )
            }
        }

        viewModel.foundPartners.observeResults(this, view, binding.vResult) {
            if (it.isNotEmpty())
                popupWindowForSelections(requireContext(), it, viewModel::updatePartnerSelection)
                    .showAsDropDown(binding.etPartner)
        }

        viewModel.foundTasksType.observeResults(this, view, binding.vResult) {
            binding.tvTaskType.setOnClickListenerLocalSelection(
                it,
                viewModel::updateTasksTypeSelection,
                viewModel::handleViewClick,
                viewModel::checkTaskTypeEmpty
            )

        }

        viewModel.foundExecutors.observeResults(this, view, binding.vResult) {
            if (it.isNotEmpty())
                popupWindowForSelections(requireContext(), it, viewModel::updateExecutorsSelection)
                    .showAsDropDown(binding.etExecutor)
        }

        viewModel.errorsMinLength.observe(viewLifecycleOwner) {
            binding.tiPartner.error = if (it.minLengthPartnerError) getString(
                R.string.error_min_length, MIN_LENGTH_SEARCH_STRING
            ) else null
            binding.tiExecutor.error = if (it.minLengthExecutorError) getString(
                R.string.error_min_length, MIN_LENGTH_SEARCH_STRING
            ) else null
        }
    }

}