package com.euromix.esupervisor.screens.main.tabs.rates

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.rates.entities.RateData
import com.euromix.esupervisor.app.model.rates.entities.RateDataRow
import com.euromix.esupervisor.app.model.rates.entities.RateStructure
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.clear
import com.euromix.esupervisor.app.utils.designByViewState
import com.euromix.esupervisor.app.utils.draw
import com.euromix.esupervisor.app.utils.gone
import com.euromix.esupervisor.app.utils.observeEvent
import com.euromix.esupervisor.app.utils.setPeriodSelection
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.app.utils.visible
import com.euromix.esupervisor.databinding.RatesFragmentBinding
import com.euromix.esupervisor.screens.main.BaseViewState
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat

@AndroidEntryPoint
class RatesFragment : BaseFragment(R.layout.rates_fragment) {

    override val viewModel by viewModels<RatesViewModel>()
    private val binding by viewBinding<RatesFragmentBinding>()

    private var adapter = RateAdapter(lifecycleScope) {

        val rate = it?.tag as RateDataRow

        val dimensionsArray = viewModel.decipherDimensions()

        if (dimensionsArray.isNotEmpty()) AlertDialog.Builder(requireContext())
            .setTitle(R.string.decipher_to)
            .setSingleChoiceItems(dimensionsArray, 0) { dialog, index ->
                viewModel.decipher(
                    dimensionsArray[index], rate.serverObject
                )
                dialog.dismiss()
            }.create().show()
    }

    private var userChangingDetailRate = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvList.adapter = adapter

        setupObservers()
        setupListeners()
        setPeriodSelection(
            binding.etPeriodSelection, viewModel.viewState.period, parentFragmentManager
        ) { period -> period?.let { viewModel.changePeriod(it) } }
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

            rateData?.let { renderTotalViews(it) }
            binding.swPlanType.text =
                getString(if (planType == 0) R.string.month_plan else R.string.daily_plan)

            if (initialLoad)
                rates?.let {
                    setupSpinner(it)
                }
            rateData?.let { adapter.rates = it.rows }

            visibilityViews()
            binding.tvDetailPath.text = viewModel.backStackPath()
        }
    }

    private fun setupListeners() {
        binding.srl.setOnRefreshListener { viewModel.reloadRate() }
        binding.vResult.setTryAgainAction {

            if (viewModel.viewState.rates.isNullOrEmpty())
                viewModel.reloadRates()
            else
                viewModel.reloadRate()
        }
        binding.tvDetailPath.setOnClickListener { viewModel.decipher() }
        binding.swPlanType.setOnCheckedChangeListener { _, isChecked ->
            viewModel.changePlanType(if (isChecked) 1 else 0)
            setRatesDetailingAdapter()
        }
    }

    private fun renderTotalViews(rate: RateData) {
        if (rate.totalPlan != 0f && rate.totalFact != 0f) {
            binding.piTotal.draw(rate.totalPlan.toDouble(), rate.totalFact.toDouble())
        } else {
            binding.piTotal.clear()
        }
        binding.tvTotalFact.text = DecimalFormat("###,###.##").format(rate.totalFact)
        binding.tvTotalPlan.text = DecimalFormat("###,###.##").format(rate.totalPlan)
    }

    private fun setupSpinner(rates: List<RateStructure>) {

        binding.spRates.adapter = SpinnerRatesAdapter(
            requireContext(), R.layout.item_spinner, rates
        )

        binding.spRates.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {

                val currentRate = parent?.getItemAtPosition(position) as RateStructure
                viewModel.changeRate(currentRate)
                setRatesDetailingAdapter()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setRatesDetailingAdapter() {
        with(viewModel.viewState) {
            currentRate?.let {
                userChangingDetailRate = false
                binding.spDetailing.adapter = RatesDetailingAdapter(
                    requireContext(), R.layout.item_spinner, (if (planType == 0)
                        currentRate.dimensions
                    else
                        currentRate.dayDimensions).toMutableList()
                )

                binding.spDetailing.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?, view: View?, position: Int, id: Long
                        ) {

                            if (userChangingDetailRate)
                                viewModel.changeDetailLevel(position)
                            else
                                userChangingDetailRate = true
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }
            }
        }
    }

    private fun visibilityViews() {

        with(binding) {
            if (viewModel.viewState.rateSelection.isEmpty()) {
                spRates.visible()

                etPeriodSelection.visible()
                tvDetailPath.gone()
                swPlanType.visible()

                if (viewModel.viewState.planType == 0) {
                    spDetailing.visible()
                    clTotal.visible()
                } else {
                    spDetailing.gone()
                    clTotal.gone()
                }
            } else {
                spRates.gone()

                etPeriodSelection.gone()
                tvDetailPath.visible()
                swPlanType.gone()
                spDetailing.gone()
                clTotal.visible()
            }
        }
    }
}