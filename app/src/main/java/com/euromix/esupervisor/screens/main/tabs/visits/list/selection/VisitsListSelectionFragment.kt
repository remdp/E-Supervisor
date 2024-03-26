package com.euromix.esupervisor.screens.main.tabs.visits.list.selection

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
import com.euromix.esupervisor.app.model.visits.entities.VisitsListSelection
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.observeResults
import com.euromix.esupervisor.app.utils.popupWindowForSelections
import com.euromix.esupervisor.app.utils.setEtOnEditorActionListener
import com.euromix.esupervisor.app.utils.setOnClickListenerServerSelection
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.databinding.VisitsListSelectionFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VisitsListSelectionFragment: BaseFragment(R.layout.visits_list_selection_fragment) {

    override val viewModel by viewModels<VisitsListSelectionViewModel>()
    private val binding by viewBinding<VisitsListSelectionFragmentBinding>()
    private val args by navArgs<VisitsListSelectionFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.initSelection(args.selection)
        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {

        binding.btnOk.setOnClickListener { setFragmentResult() }

        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack(R.id.visitsListFragment, false)
        }

        binding.etTradingAgent.setOnClickListener { binding.tiTradingAgent.error = null }
        binding.etTradingAgent.setOnFocusChangeListener { _, _ ->
            binding.tiTradingAgent.error = null
        }
        binding.etTradingAgent.setEtOnEditorActionListener(viewModel::findTradingAgents)
        binding.etTradingAgent.setOnClickListenerServerSelection(viewModel::updateTradingAgentSelection)

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

            if (VisitsListSelection.isEmpty(it)) {
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

        viewModel.errorsMinLength.observe(viewLifecycleOwner) {
            binding.tiTradingAgent.error = if (it.minLengthTradingAgentError) getString(
                R.string.error_min_length, Const.MIN_LENGTH_SEARCH_STRING
            ) else null
        }
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