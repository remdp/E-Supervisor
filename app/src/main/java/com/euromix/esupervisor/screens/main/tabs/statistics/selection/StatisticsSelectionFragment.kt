package com.euromix.esupervisor.screens.main.tabs.statistics.selection

import android.os.Bundle
import android.view.View
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.Const
import com.euromix.esupervisor.app.model.common.entities.ServerPair
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.ResourceManager
import com.euromix.esupervisor.app.utils.observeResults
import com.euromix.esupervisor.app.utils.popupWindowForSelections
import com.euromix.esupervisor.app.utils.setEtOnEditorActionListener
import com.euromix.esupervisor.app.utils.setOnClickListenerLocalSelection
import com.euromix.esupervisor.app.utils.setOnClickListenerServerSelection
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.app.utils.visibility
import com.euromix.esupervisor.databinding.StatisticsSelectionFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StatisticsSelectionFragment : BaseFragment(R.layout.statistics_selection_fragment) {

    override val viewModel by viewModels<StatisticsSelectionViewModel>()

    private val binding by viewBinding<StatisticsSelectionFragmentBinding>()
    private val args by navArgs<StatisticsSelectionFragmentArgs>()

    @Inject
    lateinit var resManager: ResourceManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.initSelection(args.selection, args.jumpCount)

        setupVisibility()
        setupListeners()
        setupObservers()

    }

    private fun setupListeners() {

        binding.btnOk.setOnClickListener {
            setFragmentResult(
                Const.SELECTION_KEY,
                Bundle().apply {
                    putParcelable(Const.SELECTION_KEY, viewModel.selection.value)
                })
            findNavController().popBackStack()
        }

        binding.btnCancel.setOnClickListener { findNavController().popBackStack() }

        binding.etTradingAgent.setOnClickListener { binding.tiTradingAgent.error = null }
        binding.etTradingAgent.setOnFocusChangeListener { _, _ ->
            binding.tiTradingAgent.error = null
        }
        binding.etTradingAgent.setEtOnEditorActionListener(viewModel::findTradingAgents)
        binding.etTradingAgent.setOnClickListenerServerSelection(viewModel::updateTradingAgentSelection)

        binding.ivArrowBack.setOnClickListener { findNavController().popBackStack() }

        binding.tvClear.setOnClickListener {
            viewModel.clearSelection()
        }

    }

    private fun setupObservers() {

        viewModel.selection.observe(viewLifecycleOwner) { selection ->

            binding.tvBalanceUnit.text = selection.balanceUnit?.presentation
            binding.tvBalanceUnit.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                if (selection.balanceUnit == null) R.drawable.ic_arrow_drop_down_gray_400
                else R.drawable.ic_cross_gray_300,
                0
            )

            binding.tvTradingTeam.text = selection.tradingTeam?.presentation
            binding.tvTradingTeam.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                if (selection.tradingTeam == null) R.drawable.ic_arrow_drop_down_gray_400
                else R.drawable.ic_cross_gray_300,
                0
            )

            binding.etTradingAgent.setText(selection.tradingAgent?.presentation)
            binding.etTradingAgent.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                if (selection.tradingAgent == null) R.drawable.ic_arrow_drop_down_gray_400 else R.drawable.ic_cross_gray_300,
                0
            )

            if (selection.isEmpty(viewModel.jumpCount)) {
                binding.tvClear.setTextColor(resManager.getColor(R.color.gray_500))
                binding.tvClear.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_gray_basket,
                    0,
                    0,
                    0
                )
            } else {
                binding.tvClear.setTextColor(resManager.getColor(R.color.blue))
                binding.tvClear.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_blue_basket,
                    0,
                    0,
                    0
                )
            }
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
                            it.id,
                            it.presentation
                        )
                    },
                    viewModel::updateTradingAgentSelection
                )
                    .showAsDropDown(binding.etTradingAgent)
        }

        viewModel.foundBUAndTradingTeams.observeResults(this, binding.root, binding.vResult) {

            it.getOrNull(0)?.let { balanceUnits ->

                binding.tvBalanceUnit.setOnClickListenerLocalSelection(
                    balanceUnits,
                    viewModel::updateBalanceUnitsSelection,
                    ::handleViewClick,
                    viewModel::checkBalanceUnitEmpty
                )

            }

            it.getOrNull(1)?.let { tradingTeams ->

                binding.tvTradingTeam.setOnClickListenerLocalSelection(
                    tradingTeams,
                    viewModel::updateTradingTeamSelection,
                    ::handleViewClick,
                    viewModel::checkTradingTeamEmpty
                )
            }
        }

        viewModel.errorsMinLength.observe(viewLifecycleOwner) {
            binding.tiTradingAgent.error = if (it.minLengthTradingAgentError) getString(
                R.string.error_min_length, Const.MIN_LENGTH_SEARCH_STRING
            ) else null
        }
    }

    private fun setupVisibility() {

        binding.tvBalanceUnit.visibility(args.jumpCount == 0)
        binding.tvTradingTeam.visibility(args.jumpCount < 2)

    }

    // 0-common click
    //1-right drawable click
    fun handleViewClick(
        itemsList: List<ServerPair>,
        updaterSelection: (ServerPair?) -> Unit,
        anchor: View,
        click: Int,
        emptyChecker: () -> Boolean
    ) {

        if (click == 0 || emptyChecker())
            popupWindowForSelections(
                anchor.context,
                itemsList,
                updaterSelection
            ).showAsDropDown(anchor)
        else updaterSelection(null)

    }
}