package com.euromix.esupervisor.screens.main.tabs.rates

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.rates.entities.RateData
import com.euromix.esupervisor.app.model.rates.entities.RateDataRow
import com.euromix.esupervisor.app.model.rates.entities.RateStructure
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.clear
import com.euromix.esupervisor.app.utils.designByResult
import com.euromix.esupervisor.app.utils.draw
import com.euromix.esupervisor.app.utils.gone
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

    private lateinit var ratesDetailingAdapter: RatesDetailingAdapter

    private var adapter = RateAdapter(lifecycleScope) {

        val rate = it?.tag as RateDataRow

        val dimensionsArray = viewModel.decipherDimensions()

        if (dimensionsArray.isNotEmpty()) AlertDialog.Builder(requireContext())
            .setTitle(R.string.decipher_to)
            .setSingleChoiceItems(dimensionsArray, 0) { dialog, index ->
                viewModel.updateRate(
                    dimensionsArray[index], rate.serverObject
                )
                dialog.dismiss()
            }.create().show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvList.adapter = adapter

        ratesDetailingAdapter =
            RatesDetailingAdapter(requireContext(), R.layout.item_spinner, mutableListOf())
        binding.spDetailing.adapter = ratesDetailingAdapter

        setupObservers()
        setupListeners()
        setPeriodSelection(
            binding.etPeriodSelection, viewModel.period, parentFragmentManager
        ) {
            viewModel.updateRate(it)
        }
    }

    private fun setupObservers() {

        viewModel.rates.observe(viewLifecycleOwner) { result ->

            if (result is Success) {
                setupSpinner(result.value)
                binding.vResult.setTryAgainAction { viewModel.reloadRate() }
            }
            designByResult(result, binding.root, binding.vResult, binding.srl)
        }

        viewModel.rate.observe(viewLifecycleOwner) { result ->

            if (result is Success) {
                adapter.rates = result.value.rows
                renderTotalViews(result.value)

                visibilityViews()
                setDetailPath()

            }
            designByResult(result, binding.root, binding.vResult, binding.srl)

        }

        viewModel.planType.observe(viewLifecycleOwner) {
            binding.swPlanType.text =
                getString(if (it == 0) R.string.month_plan else R.string.daily_plan)
            ratesDetailingAdapter.setList(viewModel.getDimensions())
            visibilityViews()
        }
    }

    private fun setupListeners() {
        binding.srl.setOnRefreshListener { viewModel.reloadRate() }
        binding.vResult.setTryAgainAction { viewModel.reloadRates() }
        binding.tvDetailPath.setOnClickListener { viewModel.updateRate() }
        binding.swPlanType.setOnCheckedChangeListener { _, isChecked -> viewModel.changePlanType(if (isChecked) 1 else 0) }
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
                viewModel.updateRate(currentRate)
                ratesDetailingAdapter.setList(viewModel.getDimensions())

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.spDetailing.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                viewModel.updateRate(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun visibilityViews() {

        with(binding) {
            if (viewModel.selectionEmpty()) {
                spRates.visible()

                etPeriodSelection.visible()
                tvDetailPath.gone()
                swPlanType.visible()

                if ((viewModel.planType.value ?: 0) == 0) {
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

    private fun setDetailPath() {
        binding.tvDetailPath.text = viewModel.backStackPath()
    }
}