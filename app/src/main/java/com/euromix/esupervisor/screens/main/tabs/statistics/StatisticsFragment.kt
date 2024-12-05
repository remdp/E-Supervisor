package com.euromix.esupervisor.screens.main.tabs.statistics

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.Const
import com.euromix.esupervisor.app.model.routes.entities.StatisticsSelection
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.ResourceManager
import com.euromix.esupervisor.app.utils.designByViewState
import com.euromix.esupervisor.app.utils.observeEvent
import com.euromix.esupervisor.app.utils.setPeriodSelection
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.databinding.StatisticsFragmentBinding
import com.euromix.esupervisor.screens.main.BaseViewState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StatisticsFragment : BaseFragment(R.layout.statistics_fragment) {

    override val viewModel by viewModels<StatisticsViewModel>()
    private val binding by viewBinding<StatisticsFragmentBinding>()
    private lateinit var adapter: StatisticsAdapter

    @Inject
    lateinit var resManager: ResourceManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.init()

        adapter = StatisticsAdapter(
            resManager = resManager,
            onDetailClick = viewModel::onItemClick,
            onWatchAllClick = viewModel::changeWatchAllManufacturersLogo,
            onDecipherClick = viewModel::decipher
        )

        binding.rvStatistic.adapter = adapter

        setupListeners()
        setupObservers()
        renderState()
    }

    private fun setupObservers() {
        viewModel.viewStateEvent.observeEvent(viewLifecycleOwner) {
            renderState()
        }

        setFragmentResultListener(Const.SELECTION_KEY) { requestKey, bundle ->

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bundle.getParcelable(requestKey, StatisticsSelection::class.java)
            } else {
                bundle.getParcelable(requestKey)
            }?.let { selection ->
                viewModel.changeSelection(selection)
            }
        }
    }

    private fun setupListeners() {

        binding.srl.setOnRefreshListener { viewModel.reload() }
        binding.vResult.setTryAgainAction {
            if (viewModel.viewState.detailSelection == null)
                viewModel.reload()
            else
                viewModel.reloadDetail()
        }

        binding.tvAnalysisVisits.setOnClickListener {
            viewModel.back()
        }

        setPeriodSelection(
            binding.etPeriod, viewModel.viewState.selection.period, parentFragmentManager
        ) { period ->
            period?.let { viewModel.changePeriod(it) }

        }

        binding.ivFunnel.setOnClickListener {
            findNavController().navigate(
                StatisticsFragmentDirections.actionStatisticsFragmentToStatisticsSelectionFragment(
                    viewModel.viewState.selection,
                    viewModel.viewState.jumpCount
                )
            )
        }
    }

    private fun renderState() {

        with(viewModel.viewState) {
            designByViewState(
                this as BaseViewState, binding.root, binding.vResult, binding.srl
            )

            if (!isLoading && error == null) {
                with(binding) {

                    visitsData.apply {
                        tvAnalysisVisits.text = backStackPath()
                        tvAnalysisVisits.setCompoundDrawablesWithIntrinsicBounds(
                            if (viewModel.canBack()) R.drawable.ic_arrow_back else 0,
                            0,
                            0,
                            0
                        )
                        adapter.submitList(visitsData)
                    }
                }
            }
        }
    }

    private fun backStackPath(): String {

        var path = ""

        if (!viewModel.canBack())
            path = getString(R.string.analysis_of_visits)
        else {
            viewModel.viewState.backStackItems.forEach {

                path += if (path == "") "" else " / "
                path += it
            }
        }
        return path
    }
}