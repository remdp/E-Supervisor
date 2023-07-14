package com.euromix.esupervisor.screens.main.tabs.tasks.list

//import com.euromix.esupervisor.app.utils.designedPeriodView
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.*
import com.euromix.esupervisor.databinding.TasksFragmentBinding
import com.euromix.esupervisor.screens.main.tabs.TitleData
import com.euromix.esupervisor.screens.main.tabs.docsEmix.list.DocsEmixListFragmentDirections
import com.euromix.esupervisor.screens.main.tabs.tasks.selection.TasksSelectionFragmentArgs
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
            viewModel.getTasks()
        }
    }

    private fun setupListeners() {
        binding.srl.setOnRefreshListener { viewModel.getTasks() }
        binding.vResult.setTryAgainAction { viewModel.getTasks() }

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