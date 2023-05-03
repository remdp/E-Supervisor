package com.euromix.esupervisor.screens.main.tabs.docsEmix.selection

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.euromix.esupervisor.App.Companion.getColor
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.docsEmix.entities.DocsEmixSelection
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.observeResults
import com.euromix.esupervisor.app.utils.setOnClickListenerLocalSelection
import com.euromix.esupervisor.app.utils.setOnClickListenerServerSelection
import com.euromix.esupervisor.app.utils.setSelectionOnEditorActionListener
import com.euromix.esupervisor.databinding.DocsEmixSelectionFragmentBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DocsEmixSelectionFragment : BaseFragment(R.layout.docs_emix_selection_fragment) {

    override val viewModel by viewModels<DocsSelectionViewModel>()

    private lateinit var binding: DocsEmixSelectionFragmentBinding
    private val args by navArgs<DocsEmixSelectionFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = DocsEmixSelectionFragmentBinding.bind(view)

        viewModel.updateSelection(args.selection)

        setupListeners()
        setupObservers(view)

    }

    private fun setupListeners() {

        binding.btnOk.setOnClickListener {
            val direction =
                DocsEmixSelectionFragmentDirections.actionDocsEmixSelectionFragmentToDocsEmixListFragment(
                    selection = viewModel.selection.value
                )
            findNavController().navigate(direction)
        }

        binding.btnCancel.setOnClickListener { findNavController().popBackStack() }

        binding.etTradingAgent.setOnClickListener { binding.tiTradingAgent.error = null }
        binding.etTradingAgent.setOnFocusChangeListener { _, _ ->  binding.tiTradingAgent.error = null}
        binding.etTradingAgent.setSelectionOnEditorActionListener(viewModel::findTradingAgents)
        binding.etTradingAgent.setOnClickListenerServerSelection(viewModel::updateTradingAgentSelection)

        binding.etPartner.setOnClickListener { binding.tiPartner.error = null }
        binding.etPartner.setOnFocusChangeListener { _, _ ->  binding.tiPartner.error = null}
        binding.etPartner.setSelectionOnEditorActionListener(viewModel::findPartners)
        binding.etPartner.setOnClickListenerServerSelection(viewModel::updatePartnerSelection)

        binding.tvOperationType.setOnClickListenerLocalSelection(
            viewModel.getOperationTypesForChoose(requireContext()),
            viewModel::updateOperationTypeSelection,
            viewModel::handleViewClick,
            viewModel::checkOperationTypeEmpty
        )

        binding.tvStatus.setOnClickListenerLocalSelection(
            viewModel.getStatusesForChoose(requireContext()),
            viewModel::updateStatusSelection,
            viewModel::handleViewClick,
            viewModel::checkStatusEmpty
        )

        binding.ivArrowBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvClear.setOnClickListener {
            viewModel.clearSelection()
        }

    }

    private fun setupObservers(view: View) {

        viewModel.selection.observe(viewLifecycleOwner) {
            binding.etTradingAgent.setText(it?.tradingAgent?.presentation)
            binding.etTradingAgent.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                if (it?.tradingAgent == null) R.drawable.ic_arrow_drop_down_gray_400 else R.drawable.ic_cross_gray_300,
                0
            )

            binding.etPartner.setText(it?.partner?.presentation)
            binding.etPartner.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                if (it?.partner == null) R.drawable.ic_arrow_drop_down_gray_400 else R.drawable.ic_cross_gray_300,
                0
            )

            binding.tvOperationType.text = it?.operationType?.presentation
            binding.tvOperationType.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                if (it?.operationType == null) R.drawable.ic_arrow_drop_down_gray_400 else R.drawable.ic_cross_gray_300,
                0
            )

            binding.tvStatus.text = it?.status?.presentation
            binding.tvStatus.setCompoundDrawablesWithIntrinsicBounds(
                0,
                0,
                if (it?.status == null) R.drawable.ic_arrow_drop_down_gray_400 else R.drawable.ic_cross_gray_300,
                0
            )

            if (DocsEmixSelection.isEmpty(it)) {
                binding.tvClear.setTextColor(getColor(requireContext(), R.color.gray_500))
                binding.tvClear.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_gray_basket,
                    0,
                    0,
                    0
                )
            } else {
                binding.tvClear.setTextColor(getColor(requireContext(), R.color.blue))
                binding.tvClear.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_blue_basket,
                    0,
                    0,
                    0
                )
            }
        }

        viewModel.foundTradingAgents.observeResults(this, view, binding.vResult) {
            if (it.isNotEmpty())
                viewModel.popupWindow(requireContext(), it, viewModel::updateTradingAgentSelection)
                    .showAsDropDown(binding.etTradingAgent)
        }

        viewModel.foundPartners.observeResults(this, view, binding.vResult) {
            if (it.isNotEmpty())
                viewModel.popupWindow(requireContext(), it, viewModel::updatePartnerSelection)
                    .showAsDropDown(binding.etPartner)
        }

        viewModel.errorsMinLength.observe(viewLifecycleOwner) {

                binding.tiTradingAgent.error = if (it.minLengthTradingAgentError) getString(
                    R.string.error_min_length,
                    DocsSelectionViewModel.MIN_LENGTH_SEARCH_STRING
                ) else null

                binding.tiPartner.error = if (it.minLengthPartnerError) getString(
                    R.string.error_min_length,
                    DocsSelectionViewModel.MIN_LENGTH_SEARCH_STRING
                ) else null
        }
    }

}