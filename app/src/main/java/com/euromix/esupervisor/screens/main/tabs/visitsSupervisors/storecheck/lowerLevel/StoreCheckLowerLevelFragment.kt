package com.euromix.esupervisor.screens.main.tabs.visitsSupervisors.storecheck.lowerLevel

import android.os.Bundle
import android.view.View
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.Const
import com.euromix.esupervisor.app.model.storeCheck.entities.StoreCheckLowerLevelRow
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.ResourceManager
import com.euromix.esupervisor.app.utils.designByViewState
import com.euromix.esupervisor.app.utils.observeEvent
import com.euromix.esupervisor.app.utils.toText
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.app.utils.visibility
import com.euromix.esupervisor.databinding.StoreCheckLowerLevelFragmentBinding
import com.euromix.esupervisor.screens.main.BaseViewState
import com.euromix.esupervisor.screens.main.tabs.TitleData
import com.euromix.esupervisor.screens.viewModelCreator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StoreCheckLowerLevelFragment : BaseFragment(R.layout.store_check_lower_level_fragment) {

    @Inject
    lateinit var factory: StoreCheckLowerLevelViewModel.Factory

    private val navController: NavController by lazy { findNavController() }
    private val args by navArgs<StoreCheckLowerLevelFragmentArgs>()
    override val viewModel by viewModelCreator { factory.create(args.id, args.topLevelId) }
    private val binding by viewBinding<StoreCheckLowerLevelFragmentBinding>()

    lateinit var adapter: StoreCheckLowerLevelAdapter

    @Inject
    lateinit var resManager: ResourceManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener(Const.REQUEST_TASK_CREATION) { requestKey, bundle ->
            if (requestKey == Const.REQUEST_TASK_CREATION) {
                if (bundle.getBoolean(Const.BUNDLE_KEY_TASK_CREATION_SUCCESSFUL, false))
                    viewModel.plusTasksCount()
            }
        }

        renderState()

        setupListeners()
        setupObservers()

    }

    private fun setupListeners() {
        binding.vResult.setTryAgainAction { viewModel.reload() }
        binding.btnSave.setOnClickListener {
            viewModel.postStoreCheck()
        }
        binding.btnCancel.setOnClickListener { navController.popBackStack() }
        binding.swPOS.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onChangedPos(isChecked)
        }

        binding.ivAddTask.setOnClickListener {

            viewModel.viewState?.let { viewState ->
                navController.navigate(
                    StoreCheckLowerLevelFragmentDirections.actionStoreCheckLowerLevelFragmentToCreateTasksForStorecheckFragment(
                        titleData = TitleData(
                            getString(R.string.new_task_on_store_check, viewState.data.number),
                            null
                        ),
                        storeCheckId = viewState.extId,
                        partner = viewState.data.partner,
                        outlet = viewState.data.outlet,
                        tradeAgent = viewState.data.tradeAgent
                    )
                )
            }
        }
    }

    private fun setupObservers() {

        viewModel.navigateBackEvent.observeEvent(viewLifecycleOwner) {
            navController.popBackStack()
        }

        viewModel.viewStateEvent.observeEvent(viewLifecycleOwner) {
            renderState()
        }
    }

    private fun renderState() {

        viewModel.viewState?.let { viewState ->
            designByViewState(
                viewState as BaseViewState,
                binding.root,
                binding.vResult,
                specialViews = listOf(
                    binding.grPOS,
                    binding.tvPOS,
                    binding.swPOS,
                    binding.swPOSBasis
                )
            )

            binding.tvTasksCount.text = viewState.data.tasksCount.toText("0")

            if (!viewState.isLoading && viewState.error == null) {
                binding.grPOS.visibility(viewState.data.availabilityPos)
                binding.swPOS.isChecked = viewState.data.pos
                binding.swPOS.isEnabled = !viewState.data.isCheckOut

                binding.swPOSBasis.visibility(viewState.data.availabilityPos && !args.twoLevels && viewState.data.isBasis)
                binding.swPOSBasis.isChecked = viewState.data.posBasis

                if (viewState.data.storeCheckFormat != null && viewState.data.shelfShare != null) {
                    adapter = StoreCheckLowerLevelAdapter(
                        resManager,
                        StoreCheckLowerLevelAdapter.Settings(
                            storeCheckFormat = viewState.data.storeCheckFormat,
                            shelfShare = viewState.data.shelfShare,
                            newProducts = viewState.data.newProducts,
                            isCheckOut = viewState.data.isCheckOut,
                            isBasis = viewState.data.isBasis
                        ),
                        ::toSalesFragment
                    ) { position, changedData, value ->
                        viewModel.onChangedData(position, changedData, value)

                        adapter.submitList(viewModel.getListForSubmit())

                    }

                    binding.rvLowerLevel.adapter = adapter
                    adapter.submitList(viewModel.getListForSubmit())

                }
            }
        }

    }

    private fun toSalesFragment(item: StoreCheckLowerLevelRow) {

        navController.navigate(
            StoreCheckLowerLevelFragmentDirections.actionStoreCheckLowerLevelFragmentToVisitsSupervisorSalesFragment(
                titleData = TitleData(item.name),
                sales = item.sales.toTypedArray()
            )
        )

    }

}