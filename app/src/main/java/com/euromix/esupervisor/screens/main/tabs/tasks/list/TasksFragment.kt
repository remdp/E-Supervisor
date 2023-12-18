package com.euromix.esupervisor.screens.main.tabs.tasks.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.observeResults
import com.euromix.esupervisor.app.utils.setPeriodSelection
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.app.utils.visible
import com.euromix.esupervisor.databinding.TasksFragmentBinding
import com.euromix.esupervisor.screens.main.tabs.TitleData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksFragment : BaseFragment(R.layout.tasks_fragment) {

    private val binding by viewBinding<TasksFragmentBinding>()
    private val args by navArgs<TasksFragmentArgs>()

    private val adapter = TasksAdapter()

    override val viewModel by viewModels<TasksViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvList.adapter = adapter

        setupObservers(view)
        setupListeners()

        viewModel.updateSelection(args.selection)

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
            adapter.tasks = it
        }

        viewModel.selection.observe(viewLifecycleOwner) {
            viewModel.reload()
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
                TitleData(getString(R.string.new_task), null)
            )
            findNavController().navigate(direction)
        }
    }
}