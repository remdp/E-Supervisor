package com.euromix.esupervisor.screens.main.tabs.routeMap.selection

import android.os.Bundle
import android.view.View
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.euromix.esupervisor.App
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.Const
import com.euromix.esupervisor.app.model.common.entities.ServerPair
import com.euromix.esupervisor.app.model.routes.entities.RouteMapSelection
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.gone
import com.euromix.esupervisor.app.utils.observeResults
import com.euromix.esupervisor.app.utils.popupWindowForSelections
import com.euromix.esupervisor.app.utils.setEtOnEditorActionListener
import com.euromix.esupervisor.app.utils.setOnClickListenerLocalSelection
import com.euromix.esupervisor.app.utils.setOnClickListenerServerSelection
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.app.utils.visible
import com.euromix.esupervisor.databinding.RouteMapSelectionFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RouteMapSelectionFragment : BaseFragment(R.layout.route_map_selection_fragment) {

    override val viewModel by viewModels<RouteMapSelectionViewModel>()
    private val binding by viewBinding<RouteMapSelectionFragmentBinding>()
    private val args by navArgs<RouteMapSelectionFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.initSelection(args.selection)
        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {

        binding.btnOk.setOnClickListener { setFragmentResult() }

        binding.btnCancel.setOnClickListener { setFragmentResult(true) }

        binding.etTradingAgent.setOnClickListener { binding.tiTradingAgent.error = null }
        binding.etTradingAgent.setOnFocusChangeListener { _, _ ->
            binding.tiTradingAgent.error = null
        }
        binding.etTradingAgent.setEtOnEditorActionListener(viewModel::findTradingAgentsAndTeams)
        binding.etTradingAgent.setOnClickListenerServerSelection(viewModel::updateTradingAgentSelection)

        binding.tvOutletsInRouteTA.setOnClickListener { viewModel.changeOutletsInRouteTASelection() }
        binding.tvOutletsTANotRoute.setOnClickListener { viewModel.changeOutletsTANotRouteSelection() }
        binding.tvOutletsTTNotTA.setOnClickListener { viewModel.changeOutletsTTNotTASelection() }
        binding.tvOutletsNotRouteTTButInOtherTT.setOnClickListener { viewModel.changeOutletsNotRouteTTButInOtherTTSelection() }
        binding.tvPromisingOutlets.setOnClickListener { viewModel.changePromisingOutletsSelection() }

        binding.ivArrowBack.setOnClickListener { setFragmentResult(true) }

        binding.tvClear.setOnClickListener {
            viewModel.clearSelection()
        }
    }

    private fun setupObservers() {

        viewModel.selection.observe(viewLifecycleOwner) {
            binding.etTradingAgent.setText(it?.tradingAgent?.presentation)
            binding.etTradingAgent.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                if (it?.tradingAgent == null) R.drawable.ic_arrow_drop_down_gray_400 else R.drawable.ic_cross_gray_300,
                0
            )

            binding.tvTradingTeam.text = it?.tradingTeam?.presentation
            binding.tvTradingTeam.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                if (it?.tradingAgent != null) 0
                else if (it?.tradingTeam == null) R.drawable.ic_arrow_drop_down_gray_400
                else R.drawable.ic_cross_gray_300,
                0
            )

            binding.tvOutletsInRouteTA.setCompoundDrawablesWithIntrinsicBounds(
                if (it?.outletsWithVisits == true) R.drawable.ic_checkbox_white_on else R.drawable.ic_checkbox_white_off,
                0,
                0,
                0
            )

            binding.tvOutletsTANotRoute.setCompoundDrawablesWithIntrinsicBounds(
                if (it?.outletsWithoutVisits == true) R.drawable.ic_checkbox_white_on else R.drawable.ic_checkbox_white_off,
                0,
                0,
                0
            )

            binding.tvOutletsTTNotTA.setCompoundDrawablesWithIntrinsicBounds(
                if (it?.outletsTTNotTA == true) R.drawable.ic_checkbox_white_on else R.drawable.ic_checkbox_white_off,
                0,
                0,
                0
            )

            binding.tvOutletsNotRouteTTButInOtherTT.setCompoundDrawablesWithIntrinsicBounds(
                if (it?.outletsNotRouteTTButInOtherTT == true) R.drawable.ic_checkbox_white_on else R.drawable.ic_checkbox_white_off,
                0,
                0,
                0
            )

            binding.tvPromisingOutlets.setCompoundDrawablesWithIntrinsicBounds(
                if (it?.promisingOutlets == true) R.drawable.ic_checkbox_white_on else R.drawable.ic_checkbox_white_off,
                0,
                0,
                0
            )

            if (RouteMapSelection.isEmpty(it)) {
                binding.tvClear.setTextColor(App.getColor(requireContext(), R.color.gray_500))
                binding.tvClear.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_gray_basket,
                    0,
                    0,
                    0
                )
            } else {
                binding.tvClear.setTextColor(App.getColor(requireContext(), R.color.blue))
                binding.tvClear.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_blue_basket,
                    0,
                    0,
                    0
                )
            }

            setVisibility(it)
        }

        viewModel.foundTradingAgents.observeResults(
            this,
            binding.root,
            binding.vResult
        ) { serverPairList ->
            if (serverPairList.isNotEmpty())
                popupWindowForSelections(
                    requireContext(),
                    serverPairList.map {
                        ServerPair(
                            it.tradingAgent.id,
                            it.tradingAgent.presentation
                        )
                    },
                    viewModel::updateTradingAgentSelection
                )
                    .showAsDropDown(binding.etTradingAgent)
        }

        viewModel.foundTradingTeams.observeResults(this, binding.root, binding.vResult) {
            binding.tvTradingTeam.setOnClickListenerLocalSelection(
                it,
                viewModel::updateTradingTeamSelection,
                viewModel::handleViewClick,
                viewModel::checkTradingTeamEmpty
            )

        }

        viewModel.errorsMinLength.observe(viewLifecycleOwner) {
            binding.tiTradingAgent.error = if (it.minLengthTradingAgentError) getString(
                R.string.error_min_length, Const.MIN_LENGTH_SEARCH_STRING
            ) else null
        }
    }

    private fun setVisibility(selection: RouteMapSelection) {

        if (selection.tradingAgent == null)
            binding.tvOutletsTTNotTA.gone()
        else
            binding.tvOutletsTTNotTA.visible()

        if (selection.tradingTeam == null)
            binding.tvOutletsNotRouteTTButInOtherTT.gone()
        else
            binding.tvOutletsNotRouteTTButInOtherTT.visible()
    }

    private fun setFragmentResult(cancel: Boolean = false) {
        setFragmentResult(
            Const.SELECTION_KEY,
            Bundle().apply {
                putParcelable(Const.SELECTION_KEY, viewModel.selection.value)
                if (cancel)
                    putBoolean(Const.CANCEL, true)
            })
        findNavController().popBackStack()
    }
}