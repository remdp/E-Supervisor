package com.euromix.esupervisor.screens.main.tabs.docsEmix.list

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.Const.SELECTION_KEY
import com.euromix.esupervisor.app.Const.START_LOAD
import com.euromix.esupervisor.app.model.docsEmix.entities.DocsEmixSelection
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.setPeriodSelection
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.databinding.DocEmixListFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DocsEmixListFragment : BaseFragment(R.layout.doc_emix_list_fragment) {

    private val navController: NavController by lazy { findNavController() }
    override val viewModel by viewModels<DocsEmixListViewModel>()
    private val binding by viewBinding<DocEmixListFragmentBinding>()

    private val adapter = DocsEmixAdapter { direction ->
        navController.navigate(direction)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvList.adapter = adapter

        setupObservers()
        setupListeners()

        setPeriodSelection(
            binding.iSelection.etPeriod,
            viewModel.viewState.value?.period,
            parentFragmentManager
        ) {
            viewModel.updatePeriod(it)
        }

        (navController.currentBackStackEntry?.savedStateHandle?.get<Boolean>(START_LOAD)
            ?: true).also { startLoad ->
            if (startLoad) {
                viewModel.updateSelection()
            }
        }
    }

    private fun setupObservers() {
        viewModel.viewState.observe(viewLifecycleOwner) {
            viewModel.afterUpdateState(adapter, binding, this as BaseFragment)
        }

        setFragmentResultListener(SELECTION_KEY) { requestKey, bundle ->

            val selection: DocsEmixSelection? =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    bundle.getParcelable(requestKey, DocsEmixSelection::class.java)
                } else {
                    bundle.getParcelable(requestKey)
                }
            if (selection != null) {
                viewModel.updateSelection(selection)
            }

            navController.currentBackStackEntry?.savedStateHandle?.set(
                START_LOAD,
                true
            )
        }
    }

    private fun setupListeners() {
        binding.srl.setOnRefreshListener { viewModel.reload() }
        binding.vResult.setTryAgainAction { viewModel.reload() }

        binding.iSelection.ivFunnel.setOnClickListener {
            val direction =
                DocsEmixListFragmentDirections.actionDocsEmixListFragmentToDocsEmixSelectionFragment(
                    selection = viewModel.viewState.value?.selection
                )
            navController.navigate(direction)
        }
    }
}