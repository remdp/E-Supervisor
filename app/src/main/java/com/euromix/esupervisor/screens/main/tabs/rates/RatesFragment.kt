package com.euromix.esupervisor.screens.main.tabs.rates

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.enums.Rate
import com.euromix.esupervisor.app.model.rates.entities.*
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.observeResults
import com.euromix.esupervisor.databinding.RatesFragmentBinding
import com.euromix.esupervisor.screens.viewModelCreator
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class RatesFragment : BaseFragment(R.layout.rates_fragment) {

    override val viewModel by viewModels<RatesViewModel>()

    private lateinit var binding: RatesFragmentBinding
    private val args by navArgs<RatesFragmentArgs>()

    private val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()

    private var initialState = true

    private var adapter = RateAdapter {

        if (args.state == null && Rate.manufacturerDetailRates()
                .contains(binding.spinner.selectedItem)
        ) {

            val direction = RatesFragmentDirections.actionRatesToManufacturersRates(
                screenTitle = " ${it.rateObject}",
                state = viewModel.state.value?.copy(tradingAgentId = it.tradingAgentId)
            )

            findNavController().navigate(direction)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = RatesFragmentBinding.bind(view)

        setupObservers(view)
        binding.etPeriodSelection.setOnClickListener {

            val picker = dateRangePicker.build()
            picker.addOnPositiveButtonClickListener {

                viewModel.updateState(
                    argsState = args.state,
                    ratePeriod = Pair(Date(it.first), Date(it.second))
                )

            }
            picker.show(parentFragmentManager, picker.toString())
        }


        binding.spinner.adapter = SpinnerRatesAdapter(
            requireContext(),
            R.layout.spinner_rates_drop_down_item,
            if (args.state == null) Rate.allRates() else Rate.manufacturerDetailRates()
        )

        if (args.state != null) binding.spinner.setSelection((args.state as RatesViewModel.ViewState).ratePosition)

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                viewModel.updateState(
                    argsState = args.state,
                    currentRate = position,
                    initialState = initialState
                )

                initialState = false

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        binding.srl.setOnRefreshListener { viewModel.fetchRate(true) }
        binding.vResult.setTryAgainAction { viewModel.fetchRate(true) }

    }

    private fun setupObservers(view: View) {

        viewModel.rate.observeResults(this, view, binding.vResult, binding.srl) {
            binding.rvList.adapter = adapter
            adapter.rates = it

            issueTotalViews(it)
        }

        viewModel.state.observe(viewLifecycleOwner) {
            viewModel.fetchRate()
            setPeriodText()
        }
    }

    private fun issueTotalViews(rateList: List<RateData>) {

        if (rateList.isNotEmpty()) {

            var totalPlan = 0.0
            var totalFact = 0.0
            rateList.forEach { listItem ->

                totalPlan += listItem.plan
                totalFact += listItem.fact

            }

            binding.tvTotalFact.text = getString(R.string.total_fact, totalFact.toInt())
            binding.tvPlan.text = getString(R.string.total_plan, totalPlan.toInt())

        }
    }

    private fun setPeriodText() {
        viewModel.state.value?.let {
            binding.etPeriodSelection.setText(
                formatDateRange(
                    Pair(
                        it.ratePeriod.first,
                        it.ratePeriod.second
                    )
                ) ?: ""
            )
        }
    }

    private fun formatDateRange(
        range: Pair<Date, Date>,
        pattern: String = "dd.MM.yyyy",
        locale: Locale = Locale.getDefault()
    ): String? {
        val localDateFormat = SimpleDateFormat(pattern, locale)
        return try {
            "${localDateFormat.format(range.first)} - ${localDateFormat.format(range.second)}"
        } catch (ex: Throwable) {
            null
        }
    }
}