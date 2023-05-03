package com.euromix.esupervisor.screens.main.tabs.rates

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.enums.Rate
import com.euromix.esupervisor.app.model.rates.entities.*
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.*
import com.euromix.esupervisor.databinding.RatesFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat
import java.util.*


@AndroidEntryPoint
class RatesFragment : BaseFragment(R.layout.rates_fragment) {

    override val viewModel by viewModels<RatesViewModel>()

    private lateinit var binding: RatesFragmentBinding

    private var adapter = RateAdapter(lifecycleScope) {

        viewModel.state.value?.let { viewState ->

            if (viewModel.currentDetailLevel() < Rate.maxDetailLevel(viewState.ratePosition)) {

                it.rateObjectId?.let { rateObjectId ->
                    viewModel.updateState(rateObjectId)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = RatesFragmentBinding.bind(view)
        binding.rvList.adapter = adapter

        setupObservers(view)
        setupListeners()
        setPeriodSelection(binding.etPeriodSelection, parentFragmentManager) {
            it?.let { viewModel.updateState(ratePeriod = it) }
        }

        setupSpinner()
        overrideOnBackPressed()
    }

    private fun setupObservers(view: View) {

        viewModel.rate.observeResults(this, view, binding.vResult, binding.srl) {
            adapter.rates = it
            issueTotalViews(it)
        }

        viewModel.state.observe(viewLifecycleOwner) {

            designedPeriodView(
                binding.etPeriodSelection,
                Pair(it.ratePeriod.first, it.ratePeriod.second),
                false
            )
            viewModel.fetchRate()
        }
    }

    private fun setupListeners() {
        binding.srl.setOnRefreshListener { viewModel.fetchRate(true) }
        binding.vResult.setTryAgainAction { viewModel.fetchRate(true) }
    }

    private fun issueTotalViews(rateList: List<RateData>) {

        var totalPlan = 0.0
        var totalFact = 0.0

        if (rateList.isNotEmpty()) {

            rateList.forEach { listItem ->

                totalPlan += listItem.plan
                totalFact += listItem.fact
            }

            if (totalPlan != 0.0 && totalFact != 0.0) {
                binding.piTotal.draw(totalPlan, totalFact)
            } else {
                binding.piTotal.clear()
            }
        }

        binding.tvTotalFact.text = DecimalFormat("###,###").format(totalFact)
        binding.tvTotalPlan.text = DecimalFormat("###,###").format(totalPlan)

    }

    private fun setupSpinner() {

        binding.spinner.adapter = SpinnerRatesAdapter(
            requireContext(),
            R.layout.spinner_rates_drop_down_item,
            Rate.allRates()
        )

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.updateState(ratePosition = position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

    }

    private fun overrideOnBackPressed() {

        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                    if (viewModel.possibleReduceDetailLevel()) viewModel.updateState()
                    else {
                        activity?.moveTaskToBack(true)
                    }
                }
            })
    }

}