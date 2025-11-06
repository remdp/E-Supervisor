package com.euromix.esupervisor.screens.main.tabs.rates

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.enums.FilterSource
import com.euromix.esupervisor.app.model.rates.entities.RateData
import com.euromix.esupervisor.app.model.rates.entities.RateDataRow
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.clear
import com.euromix.esupervisor.app.utils.designByViewState
import com.euromix.esupervisor.app.utils.draw
import com.euromix.esupervisor.app.utils.gone
import com.euromix.esupervisor.app.utils.observeEvent
import com.euromix.esupervisor.app.utils.setPeriodSelection
import com.euromix.esupervisor.app.utils.toStringOrDefault
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.app.utils.visible
import com.euromix.esupervisor.databinding.RatesFragmentBinding
import com.euromix.esupervisor.screens.main.BaseViewState
import com.euromix.esupervisor.screens.main.tabs.filter.FilterFragmentArgs
import com.euromix.esupervisor.screens.main.tabs.filter.SharedFilterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RatesFragment : BaseFragment(R.layout.rates_fragment) {

    private val navController: NavController by lazy { findNavController() }
    override val viewModel by viewModels<RatesViewModel>()
    private val sharedViewModel: SharedFilterViewModel by navGraphViewModels(R.id.rates_graph)
    private val binding by viewBinding<RatesFragmentBinding>()

    private val rateAdapter = RateAdapter(lifecycleScope) {

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

    private lateinit var ratesDetailingAdapter: RatesDetailingAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvList.adapter = rateAdapter

        ratesDetailingAdapter = RatesDetailingAdapter(requireContext(), R.layout.item_spinner)

        binding.spDetailing.adapter = ratesDetailingAdapter

        setupObservers()
        setupListeners()
        setPeriodSelection(
            binding.etPeriodSelection, viewModel.viewState.period, parentFragmentManager
        ) { period -> period?.let { viewModel.changePeriod(it) } }

        viewModel.restoreViewState()
    }

    private fun setupObservers() {
        viewModel.viewStateEvent.observeEvent(viewLifecycleOwner) {
            renderState()
        }

        sharedViewModel.filterResult.observe(viewLifecycleOwner) { filter ->
            viewModel.changeRate(filter)
        }
    }

    private fun renderState() {

        with(viewModel.viewState) {
            designByViewState(
                this as BaseViewState,
                binding.root,
                binding.vResult,
                binding.srl
            )

            if (!isLoading) {
                binding.tvCurrentRate.text = currentRate?.rate?.presentation
                binding.swPlanType.text =
                    getString(if (planType == 0) R.string.month_plan else R.string.daily_plan)
                binding.swPlanType.isChecked = planType == 1

                binding.tvDetailPath.text = viewModel.backStackPath()

                rateData?.let {
                    renderTotalViews(it)
                    rateAdapter.rates = it.rows
                }

                ratesDetailingAdapter.setList(currentDimensions)

                if (binding.spDetailing.selectedItemPosition != detailLevel) {
                    binding.spDetailing.setSelection(detailLevel)
                }

                visibilityViews()
                setViewStateListeners()
            }
        }
    }

    private fun setupListeners() {

        binding.tvCurrentRate.setOnClickListener {
            navController.navigate(
                R.id.action_rates_to_filter_graph,
                FilterFragmentArgs(
                    filterSource = FilterSource.FROM_RATES_FRAGMENT,
                    filterTitles = arrayOf(getString(R.string.rates)),
                    graphId = R.id.rates_graph,
                    initialData = viewModel.ratesForFilter(),
                    singleChoice = true
                ).toBundle()
            )
        }

        binding.srl.setOnRefreshListener { viewModel.reloadRate() }
        binding.vResult.setTryAgainAction {

            if (viewModel.viewState.rates.isNullOrEmpty())
                viewModel.reloadRates()
            else
                viewModel.reloadRate()
        }
        binding.tvDetailPath.setOnClickListener { viewModel.decipher() }
        binding.btnOverallRates.setOnClickListener {
            viewModel.changeRateGroup(true)
        }
        binding.btnTeamRates.setOnClickListener {
            viewModel.changeRateGroup(false)
        }

        binding.spDetailing.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    if (viewModel.viewState.detailLevel != position) {
                        viewModel.changeDetailLevel(position)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

    }

    private fun renderTotalViews(rate: RateData) {
        if (rate.totalPlan != 0f && rate.totalFact != 0f) {
            binding.piTotal.draw(rate.totalPlan.toDouble(), rate.totalFact.toDouble())
        } else {
            binding.piTotal.clear()
        }
        binding.tvTotalFact.text = rate.totalFact.toStringOrDefault()
        binding.tvTotalPlan.text = rate.totalPlan.toStringOrDefault()
    }

    private fun setViewStateListeners() {

        binding.swPlanType.setOnCheckedChangeListener { _, isChecked ->
            viewModel.changePlanType(if (isChecked) 1 else 0)
        }
    }

    private fun visibilityViews() {

        with(binding) {
            if (viewModel.viewState.rateSelection.isEmpty()) {
                rgRates.visible()
                tvCurrentRate.visible()

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
                rgRates.gone()
                tvCurrentRate.gone()

                etPeriodSelection.gone()
                tvDetailPath.visible()
                swPlanType.gone()
                spDetailing.gone()
                clTotal.visible()
            }
        }
    }
}