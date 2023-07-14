package com.euromix.esupervisor.screens.main.tabs.tasks.selection

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.euromix.esupervisor.App
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.Const
import com.euromix.esupervisor.app.model.tasks.entities.TasksSelection
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.*
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

//        binding.etTaskProducer.setOnClickListener { binding.tiTaskProducer.error = null }
//        binding.etTaskProducer.setOnFocusChangeListener { _, _ ->
//            binding.etTaskProducer.error = null
//        }
//        binding.etTaskProducer.setEtOnEditorActionListener(viewModel::findTradingAgents)
//        binding.etTaskProducer.setOnClickListenerServerSelection(viewModel::updateTaskProducerSelection)

        binding.etPartner.setOnClickListener { binding.tiPartner.error = null }
        binding.etPartner.setOnFocusChangeListener { _, _ -> binding.tiPartner.error = null }
        binding.etPartner.setEtOnEditorActionListener(viewModel::findPartners)
        binding.etPartner.setOnClickListenerServerSelection(viewModel::updatePartnerSelection)
//
//        binding.etTaskType.setOnClickListener { binding.tiTaskType.error = null }
//        binding.etTaskType.setOnFocusChangeListener { _, _ -> binding.tiTaskType.error = null }
//        binding.etTaskType.setEtOnEditorActionListener(viewModel::findTasksType)
//        binding.etTaskType.setOnClickListenerServerSelection(viewModel::updateTasksTypeSelection)

//        binding.tvTaskType.setOnClickListenerLocalSelection(
//            viewModel.taskTypesForChoose,
//            viewModel::updateTasksTypeSelection,
//            viewModel::handleViewClick,
//            viewModel::checkTaskTypeEmpty
//        )

        binding.tvTaskType.setOnClickListener {


            //   val ff = viewModel.taskTypesForChoose
        }

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
//            binding.etTaskProducer.setText(it?.taskProducer?.presentation)
//            binding.etTaskProducer.setCompoundDrawablesWithIntrinsicBounds(
//                0,
//                0,
//                if (it?.taskProducer == null) R.drawable.ic_arrow_drop_down_gray_400 else R.drawable.ic_cross_gray_300,
//                0
//            )

            binding.etPartner.setText(it?.partner?.presentation)
            binding.etPartner.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                if (it?.partner == null) R.drawable.ic_arrow_drop_down_gray_400 else R.drawable.ic_cross_gray_300,
                0
            )

//            binding.etTaskType.setText(it?.taskType?.presentation)
//            binding.etTaskType.setCompoundDrawablesWithIntrinsicBounds(
//                0,
//                0,
//                if (it?.taskType == null) R.drawable.ic_arrow_drop_down_gray_400 else R.drawable.ic_cross_gray_300,
//                0
//            )

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

//        viewModel.foundTradingAgents.observeResults(this, view, binding.vResult) {
//            if (it.isNotEmpty())
//                popupWindow(requireContext(), it, viewModel::updateTaskProducerSelection)
//                    .showAsDropDown(binding.etTaskProducer)
//        }

        viewModel.foundPartners.observeResults(this, view, binding.vResult) {
            if (it.isNotEmpty())
                popupWindow(requireContext(), it, viewModel::updatePartnerSelection)
                    .showAsDropDown(binding.etPartner)
        }

        viewModel.foundTasksType.observeResults(this, view, binding.vResult) {
            binding.tvTaskType.setOnClickListenerLocalSelection(
                it,
                viewModel::updateTasksTypeSelection,
                viewModel::handleViewClick,
                viewModel::checkTaskTypeEmpty
            )
//            if (it.isNotEmpty())
//                popupWindow(requireContext(), it, viewModel::updateTasksTypeSelection)
//                    .showAsDropDown(binding.tvTaskType)

        }

        viewModel.foundExecutors.observeResults(this, view, binding.vResult) {
            if (it.isNotEmpty())
                popupWindow(requireContext(), it, viewModel::updateExecutorsSelection)
                    .showAsDropDown(binding.etExecutor)
        }


        viewModel.errorsMinLength.observe(viewLifecycleOwner) {

//            binding.tiTaskProducer.error = if (it.minLengthTradingAgentError) getString(
//                R.string.error_min_length
//            ) else null

            binding.tiPartner.error = if (it.minLengthPartnerError) getString(
                R.string.error_min_length
            ) else null

//            binding.tiTaskType.error = if (it.minLengthTradingAgentError) getString(
//                R.string.error_min_length,
//                Const.MIN_LENGTH_SEARCH_STRING
//            ) else null

            binding.tiExecutor.error = if (it.minLengthExecutorError) getString(
                R.string.error_min_length
            ) else null
        }
    }

}