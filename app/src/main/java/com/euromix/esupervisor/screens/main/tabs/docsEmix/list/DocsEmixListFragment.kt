package com.euromix.esupervisor.screens.main.tabs.docsEmix.list

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.Const
import com.euromix.esupervisor.app.Const.NEED_RELOAD
import com.euromix.esupervisor.app.Const.SELECTION_KEY
import com.euromix.esupervisor.app.model.docsEmix.entities.DocsEmixSelection
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.designByViewState
import com.euromix.esupervisor.app.utils.observeEvent
import com.euromix.esupervisor.app.utils.setPeriodSelection
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.databinding.DocEmixListFragmentBinding
import com.euromix.esupervisor.screens.main.BaseViewState
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

        adapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.rvList.adapter = adapter

        setPeriodSelection(
            binding.iSelection.etPeriod, viewModel.selection.period, parentFragmentManager
        ) {
            viewModel.changePeriod(it)
        }

        setupListeners()
        setupObservers()
        renderState(viewModel.viewState)
    }

    private fun setupListeners() {
        binding.srl.setOnRefreshListener { viewModel.reload() }
        binding.vResult.setTryAgainAction { viewModel.reload() }

        binding.iSelection.ivFunnel.setOnClickListener {
            val direction =
                DocsEmixListFragmentDirections.actionDocsEmixListFragmentToDocsEmixSelectionFragment(
                    selection = viewModel.selection
                )
            navController.navigate(direction)
        }
    }

    private fun setupObservers() {

        viewModel.viewStateEvent.observeEvent(viewLifecycleOwner) {
            renderState(it)
        }

        viewModel.selectionEvent.observeEvent(viewLifecycleOwner) {
            viewModel.reload()
        }

        setFragmentResultListener(SELECTION_KEY) { requestKey, bundle ->

            val selection: DocsEmixSelection?
            val cancelSelection: Boolean
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                selection = bundle.getParcelable(requestKey, DocsEmixSelection::class.java)
                cancelSelection = bundle.getBoolean(Const.CANCEL)
            } else {
                selection = bundle.getParcelable(requestKey)
                cancelSelection = bundle.getBoolean(Const.CANCEL)
            }

            if (!cancelSelection)
                viewModel.updateSelection(selection)
        }

        setFragmentResultListener(NEED_RELOAD){ _, _ ->
            viewModel.reload()
        }

        viewModel.scrollToTopEvent.observeEvent(viewLifecycleOwner) {
            binding.rvList.post { binding.rvList.scrollToPosition(0) }
        }
    }

    private fun renderState(viewState: DocsEmixListViewModel.ViewState) {

        designByViewState(
            viewState as BaseViewState, binding.root, binding.vResult, binding.srl
        )

        with(viewState) {
            if (!isLoading && error == null) {
                adapter.submitList(docsEmix)
            }
        }
    }

}