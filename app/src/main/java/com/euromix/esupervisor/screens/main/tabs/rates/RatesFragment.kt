package com.euromix.esupervisor.screens.main.tabs.rates

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.enums.Rate
import com.euromix.esupervisor.app.enums.RatesDetailing
import com.euromix.esupervisor.app.model.common.entities.ServerPair
import com.euromix.esupervisor.app.model.rates.entities.RateData
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.clear
import com.euromix.esupervisor.app.utils.draw
import com.euromix.esupervisor.app.utils.gone
import com.euromix.esupervisor.app.utils.observeResults
import com.euromix.esupervisor.app.utils.popupWindow
import com.euromix.esupervisor.app.utils.setPeriodSelection
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.app.utils.visible
import com.euromix.esupervisor.databinding.RatesFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat

@AndroidEntryPoint
class RatesFragment : BaseFragment(R.layout.rates_fragment) {

    override val viewModel by viewModels<RatesViewModel>()

    private val binding by viewBinding<RatesFragmentBinding>()

    private var adapter = RateAdapter(lifecycleScope) {

        val rate = it?.tag as RateData
        it.let {

            popupWindow(it, viewModel.detailsList()) { rateDetail ->
                viewModel.updateState(rateDetail, ServerPair(rate.rateObjectId, rate.rateObject))

            }.showAsDropDown(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvList.adapter = adapter

        setupObservers(view)
        setupListeners()
        setPeriodSelection(
            binding.etPeriodSelection, viewModel.state.value?.ratePeriod, parentFragmentManager
        ) {
            it?.let { viewModel.updateState(ratePeriod = it) }
        }

        setupSpinner()

    }

    private fun setupObservers(view: View) {

        viewModel.rate.observeResults(
            this,
            view,
            binding.vResult,
            binding.srl,
            listOf(
                binding.tvDetailPath,
                binding.spRates,
                binding.spDetailing,
                binding.etPeriodSelection
            )
        ) {
            adapter.rates = it
            issueTotalViews(it)
            setDetailPath()
            visibilityViews()
        }

        viewModel.state.observe(viewLifecycleOwner) { viewModel.fetchRate() }
    }

    private fun setupListeners() {
        binding.srl.setOnRefreshListener { viewModel.fetchRate(true) }
        binding.vResult.setTryAgainAction { viewModel.fetchRate(true) }
        binding.tvDetailPath.setOnClickListener { viewModel.updateStateBackStack() }
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

        binding.tvTotalFact.text = DecimalFormat("###,###.##").format(totalFact)
        binding.tvTotalPlan.text = DecimalFormat("###,###.##").format(totalPlan)

    }

    private fun setupSpinner() {

        binding.spRates.adapter = SpinnerRatesAdapter(
            requireContext(), R.layout.item_spinner, Rate.allRates()
        )

        binding.spRates.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {

                val currentRate = Rate.getByIndex(position)

                binding.spDetailing.adapter = RatesDetailingAdapter(
                    requireContext(), R.layout.item_spinner, RatesDetailing.allLevels(currentRate)
                )
                viewModel.updateState(rate = currentRate)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.spDetailing.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {

                viewModel.updateState(RatesDetailing.getByIndex(position))

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setDetailPath() {
        binding.tvDetailPath.text = viewModel.backStackPath()
    }

    private fun visibilityViews() {

        val stateValue = viewModel.state.value
        if (stateValue?.detailLevelsBackStack == null
        ) {
            binding.spRates.visible()
            binding.spDetailing.visible()
            binding.etPeriodSelection.visible()
            binding.tvDetailPath.gone()
        } else {
            binding.spRates.gone()
            binding.spDetailing.gone()
            binding.etPeriodSelection.gone()
            binding.tvDetailPath.visible()
        }

    }

}