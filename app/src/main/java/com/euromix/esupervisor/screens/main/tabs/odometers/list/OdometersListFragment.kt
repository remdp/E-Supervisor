package com.euromix.esupervisor.screens.main.tabs.odometers.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.designByViewState
import com.euromix.esupervisor.app.utils.gone
import com.euromix.esupervisor.app.utils.observeEvent
import com.euromix.esupervisor.app.utils.setPeriodSelection
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.app.utils.visible
import com.euromix.esupervisor.databinding.OdometersListFragmentBinding
import com.euromix.esupervisor.screens.main.BaseViewState
import com.euromix.esupervisor.screens.main.tabs.odometers.reading.DialogOdometersReading

class OdometersListFragment : BaseFragment(R.layout.odometers_list_fragment) {

    override val viewModel by viewModels<OdometersListViewModel>()

    private val binding by viewBinding<OdometersListFragmentBinding>()

    private val adapter = OdometersReadingAdapter()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.iSelection.ivAdditionalAction.visible()
        binding.iSelection.ivFunnel.gone()
        binding.rvList.adapter = adapter

        setPeriodSelection(
            binding.iSelection.etPeriod, viewModel.viewState.period, parentFragmentManager
        ) {
            viewModel.changePeriod(it)
        }

        setupListeners()
        setupObservers()
        renderState()
    }

    fun setupListeners() {

        binding.srl.setOnRefreshListener { viewModel.reload() }
        binding.vResult.setTryAgainAction { viewModel.reload() }
        binding.iSelection.ivAdditionalAction.setOnClickListener {
            DialogOdometersReading.newInstance(viewModel::reload).show(parentFragmentManager, null)
        }
    }

    private fun setupObservers() {
        viewModel.viewStateEvent.observeEvent(viewLifecycleOwner) {
            renderState()
        }
    }

    private fun renderState() {

        with(viewModel.viewState) {
            designByViewState(
                this as BaseViewState, binding.root, binding.vResult, binding.srl
            )

            if (!isLoading && error == null) adapter.submitList(viewModel.getListForSubmit())
            binding.rvList.post { binding.rvList.scrollToPosition(0) }
        }
    }

}