package com.euromix.esupervisor.screens.main.tabs.tasks.list

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.Const
import com.euromix.esupervisor.app.model.tasks.entities.TasksSelection
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.observeResults
import com.euromix.esupervisor.app.utils.setPeriodSelection
import com.euromix.esupervisor.app.utils.toText
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.app.utils.visible
import com.euromix.esupervisor.databinding.TasksFragmentBinding
import com.euromix.esupervisor.screens.main.tabs.TitleData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksFragment : BaseFragment(R.layout.tasks_fragment) {

    private val binding by viewBinding<TasksFragmentBinding>()
    private val navController: NavController by lazy { findNavController() }

    private val adapter = TasksAdapter { task ->
        navController.navigate(
            TasksFragmentDirections.actionTasksFragmentToTaskDetailFragment(
                task.extId,
                TitleData(task.number, task.date.toText())
            )
        )
    }

    override val viewModel by viewModels<TasksViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvList.adapter = adapter

        setupObservers(view)
        setupListeners()

        setPeriodSelection(
            binding.iSelection.etPeriod,
            viewModel.selection.value?.period,
            parentFragmentManager
        ) {
            viewModel.updatePeriod(it)
        }
        binding.iSelection.ivAdditionalAction.visible()
    }

    private fun setupObservers(view: View) {

        viewModel.tasks.observeResults(this, view, binding.vResult, binding.srl) {
            adapter.submitList(it)
        }

        viewModel.selection.observe(viewLifecycleOwner) {
            viewModel.reload()
        }

        setFragmentResultListener(TASKS_FRAGMENT_SELECTION_KEY) { requestKey, bundle ->

            if (bundle.containsKey(UPDATE_DETAIL_TASK))
                viewModel.reload()
            else {
                val selection: TasksSelection?
                val cancelSelection: Boolean
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    selection = bundle.getParcelable(requestKey, TasksSelection::class.java)
                    cancelSelection = bundle.getBoolean(Const.CANCEL)
                } else {
                    selection = bundle.getParcelable(requestKey)
                    cancelSelection = bundle.getBoolean(Const.CANCEL)
                }

                if (!cancelSelection)
                    viewModel.updateSelection(selection)
            }
        }
    }

    private fun setupListeners() {
        binding.srl.setOnRefreshListener { viewModel.reload() }
        binding.vResult.setTryAgainAction { viewModel.reload() }

        binding.iSelection.ivFunnel.setOnClickListener {

            val direction =
                TasksFragmentDirections.actionTasksFragmentToTasksSelectionFragment(
                    selection = viewModel.selection.value
                )
            findNavController().navigate(direction)
        }

        binding.iSelection.ivAdditionalAction.setOnClickListener {

            val direction = TasksFragmentDirections.actionTasksFragmentToCreateTaskFragment(
                TitleData(getString(R.string.new_task), null),
                graphId = R.id.tasks_graph
            )
            findNavController().navigate(direction)
        }
    }

    companion object {
        const val TASKS_FRAGMENT_SELECTION_KEY = "TASKS_FRAGMENT_SELECTION_KEY"
        const val UPDATE_DETAIL_TASK = "UPDATE_DETAIL_TASK"
    }
}