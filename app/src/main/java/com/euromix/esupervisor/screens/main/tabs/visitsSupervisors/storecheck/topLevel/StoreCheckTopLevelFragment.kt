package com.euromix.esupervisor.screens.main.tabs.visitsSupervisors.storecheck.topLevel

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.storeCheck.entities.StoreCheckTopLevelRow
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.designByViewState
import com.euromix.esupervisor.app.utils.observeEvent
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.app.utils.visibility
import com.euromix.esupervisor.app.utils.visible
import com.euromix.esupervisor.databinding.StoreCheckTopLevelFragmentBinding
import com.euromix.esupervisor.screens.main.BaseViewState
import com.euromix.esupervisor.screens.main.tabs.TitleData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StoreCheckTopLevelFragment : BaseFragment(R.layout.store_check_top_level_fragment) {

    private val navController: NavController by lazy { findNavController() }
    private val args by navArgs<StoreCheckTopLevelFragmentArgs>()
    override val viewModel by viewModels<StoreCheckTopLevelViewModel>()
    private val binding by viewBinding<StoreCheckTopLevelFragmentBinding>()

    val adapter = StoreCheckTopLevelAdapter(::toSalesFragment) { item ->

        navController.navigate(
            StoreCheckTopLevelFragmentDirections.actionStoreCheckTopLevelFragmentToStoreCheckLowerLevelFragment(
                id = viewModel.viewState?.data?.extId ?: "",
                titleData = TitleData(item.serverObject.serverPair.presentation),
                topLevelId = item.serverObject.serverPair.id,
                topLevelServerType = item.serverObject.serverType,
                twoLevels = true
//                storeCheckFormat = viewModel.viewState?.data?.storeCheckFormat?.getIndex() ?: -1,
//                shelfShare = viewModel.viewState?.data?.shelfShare?.getIndex() ?: -1,
//                newProducts = viewModel.viewState?.data?.newProducts ?: false
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.viewState == null) {
            viewModel.initViewState(args.id)
            viewModel.reload()
        } else {
            renderState()
        }

        binding.rvTopLevel.adapter = adapter

        setupListeners()
        setupObservers()

    }


    private fun setupListeners() {
        binding.vResult.setTryAgainAction { viewModel.reload() }
    }

    private fun setupObservers() {

        viewModel.viewStateEvent.observeEvent(viewLifecycleOwner) {
            renderState()
        }

    }

    private fun renderState() {
        val viewState = viewModel.viewState
        designByViewState(
            viewState as BaseViewState,
            binding.root,
            binding.vResult,
            specialViews = listOf(binding.grPOS, binding.tvPOS, binding.swPOS, binding.swPOSBasis)
        )
        if (!viewState.isLoading && viewState.error == null) {
            binding.grPOS.visibility(viewState.data.availabilityPos && args.twoLevels)

//            binding.tvPOS.visibility(viewState.data.availabilityPos)
//            binding.swPOS.visibility(viewState.data.availabilityPos)
            binding.swPOS.isChecked = viewState.data.pos
            binding.swPOS.isEnabled = !viewState.data.isCheckOut

            binding.swPOSBasis.visibility(viewState.data.availabilityPos && args.twoLevels && viewState.data.isBasis)
            binding.swPOSBasis.isChecked = viewState.data.posBasis

            adapter.submitList(viewModel.getListForSubmit())
        }
    }

    private fun toSalesFragment(item: StoreCheckTopLevelRow) {

        navController.navigate(
            StoreCheckTopLevelFragmentDirections.actionStoreCheckTopLevelFragmentToVisitsSupervisorSalesFragment(
                titleData = TitleData(item.serverObject.serverPair.presentation),
                sales = item.sales.toTypedArray()
            )
        )
    }
}