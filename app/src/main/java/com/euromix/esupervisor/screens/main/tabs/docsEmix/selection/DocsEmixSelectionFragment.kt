package com.euromix.esupervisor.screens.main.tabs.docsEmix.selection

import android.os.Bundle
import android.view.View
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.Const
import com.euromix.esupervisor.app.Const.MIN_LENGTH_SEARCH_STRING
import com.euromix.esupervisor.app.Const.SELECTION_KEY
import com.euromix.esupervisor.app.model.common.entities.ServerPair
import com.euromix.esupervisor.app.model.docsEmix.entities.DocsEmixSelection
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.ResourceManager
import com.euromix.esupervisor.app.utils.observeResults
import com.euromix.esupervisor.app.utils.popupWindowForSelections
import com.euromix.esupervisor.app.utils.setEtOnEditorActionListener
import com.euromix.esupervisor.app.utils.setOnClickListenerLocalSelection
import com.euromix.esupervisor.app.utils.setOnClickListenerServerSelection
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.databinding.DocsEmixSelectionFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DocsEmixSelectionFragment : BaseFragment(R.layout.docs_emix_selection_fragment) {

    override val viewModel by viewModels<DocsSelectionViewModel>()

    private val binding by viewBinding<DocsEmixSelectionFragmentBinding>()
    private val args by navArgs<DocsEmixSelectionFragmentArgs>()

    @Inject
    lateinit var resManager: ResourceManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.updateSelection(args.selection)

        setupListeners()
        setupObservers()

    }

    private fun setupListeners() {

        binding.btnOk.setOnClickListener { setFragmentResult() }

        binding.btnCancel.setOnClickListener { findNavController().popBackStack() }

        binding.etTradingAgent.setOnClickListener { binding.tiTradingAgent.error = null }
        binding.etTradingAgent.setOnFocusChangeListener { _, _ ->
            binding.tiTradingAgent.error = null
        }
        binding.etTradingAgent.setEtOnEditorActionListener(viewModel::findTradingAgents)
        binding.etTradingAgent.setOnClickListenerServerSelection(viewModel::updateTradingAgentSelection)

        binding.etPartner.setOnClickListener { binding.tiPartner.error = null }
        binding.etPartner.setOnFocusChangeListener { _, _ -> binding.tiPartner.error = null }
        binding.etPartner.setEtOnEditorActionListener(viewModel::findPartners)
        binding.etPartner.setOnClickListenerServerSelection(viewModel::updatePartnerSelection)

        binding.tvOperationType.setOnClickListenerLocalSelection(
            viewModel.getOperationTypesForChoose(),
            viewModel::updateOperationTypeSelection,
            ::handleViewClick,
            viewModel::checkOperationTypeEmpty
        )

        binding.tvStatus.setOnClickListenerLocalSelection(
            viewModel.getStatusesForChoose(),
            viewModel::updateStatusSelection,
            ::handleViewClick,
            viewModel::checkStatusEmpty
        )

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

        viewModel.foundTradingAgents.observeResults(this, binding.root, binding.vResult) {
            if (it.isNotEmpty())
                popupWindowForSelections(
                    requireContext(),
                    it,
                    viewModel::updateTradingAgentSelection
                )
                    .showAsDropDown(binding.etTradingAgent)
        }

        viewModel.foundPartners.observeResults(this, binding.root, binding.vResult) {
            if (it.isNotEmpty())
                popupWindowForSelections(requireContext(), it, viewModel::updatePartnerSelection)
                    .showAsDropDown(binding.etPartner)
        }

        viewModel.errorsMinLength.observe(viewLifecycleOwner) {

            binding.tiTradingAgent.error = if (it.minLengthTradingAgentError) getString(
                R.string.error_min_length, MIN_LENGTH_SEARCH_STRING
            ) else null

            binding.tiPartner.error = if (it.minLengthPartnerError) getString(
                R.string.error_min_length, MIN_LENGTH_SEARCH_STRING
            ) else null
        }
    }

    private fun setFragmentResult(cancel: Boolean = false) {
        setFragmentResult(
            SELECTION_KEY,
            Bundle().apply {
                putParcelable(SELECTION_KEY, viewModel.selection.value)
                if (cancel)
                    putBoolean(Const.CANCEL, true)
            })
        findNavController().popBackStack()
    }

    //click
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