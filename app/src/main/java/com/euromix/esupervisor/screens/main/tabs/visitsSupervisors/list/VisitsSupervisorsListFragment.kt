package com.euromix.esupervisor.screens.main.tabs.visitsSupervisors.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.designByViewState
import com.euromix.esupervisor.app.utils.observeEvent
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.databinding.VisitsSupervisorsListFragmentBinding
import com.euromix.esupervisor.screens.main.BaseViewState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VisitsSupervisorsListFragment : BaseFragment(R.layout.visits_supervisors_list_fragment) {

    private val navController: NavController by lazy { findNavController() }
    override val viewModel by viewModels<VisitsSupervisorsListViewModel>()

    private val binding by viewBinding<VisitsSupervisorsListFragmentBinding>()

    val adapter: VisitsSupervisorsAdapter = VisitsSupervisorsAdapter{
       navController.navigate(it)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvList.adapter = adapter

        setupListeners()
        setupObservers()

    }

    private fun setupListeners() {
        binding.srl.setOnRefreshListener { viewModel.reload() }
    }

    private fun setupObservers() {

        viewModel.viewStateEvent.observeEvent(viewLifecycleOwner) {
            renderState(it)
        }

    }

    private fun renderState(viewState: VisitsSupervisorsListViewModel.ViewState) {
        designByViewState(
            viewState as BaseViewState, binding.root, binding.vResult, binding.srl
        )

        if (!viewState.isLoading && viewState.error == null) adapter.submitList(viewModel.getListForSubmit())

    }

}