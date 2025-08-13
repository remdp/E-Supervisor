//package com.euromix.esupervisor.screens.main.tabs.visitsSupervisors.storecheck.list
//
//import android.os.Bundle
//import android.view.View
//import androidx.core.widget.doAfterTextChanged
//import androidx.fragment.app.viewModels
//import androidx.navigation.NavController
//import androidx.navigation.fragment.findNavController
//import androidx.navigation.fragment.navArgs
//import com.euromix.esupervisor.R
//import com.euromix.esupervisor.app.screens.base.BaseFragment
//import com.euromix.esupervisor.app.utils.designByViewState
//import com.euromix.esupervisor.app.utils.observeEvent
//import com.euromix.esupervisor.app.utils.toText
//import com.euromix.esupervisor.app.utils.viewBinding
//import com.euromix.esupervisor.databinding.StorecheckListFragmentBinding
//import com.euromix.esupervisor.databinding.VisitsSupervisorsListFragmentBinding
//import com.euromix.esupervisor.screens.main.BaseViewState
//import com.euromix.esupervisor.screens.main.tabs.TitleData
//import com.euromix.esupervisor.screens.main.tabs.visitsSupervisors.detail.VisitSupervisorDetailFragmentArgs
//import com.euromix.esupervisor.screens.main.tabs.visitsSupervisors.list.VisitsSupervisorListViewModel
//import com.euromix.esupervisor.screens.viewModelCreator
//import dagger.hilt.android.AndroidEntryPoint
//import javax.inject.Inject
//
//@AndroidEntryPoint
//class StoreCheckListFragment : BaseFragment(R.layout.storecheck_list_fragment) {
//
//    @Inject
//    lateinit var factory: StoreCheckListViewModel.Factory
//
//    private val navController: NavController by lazy { findNavController() }
//    //private val args by navArgs<StoreCheckListFragmentArgs>()
//    //override val viewModel by viewModelCreator { factory.create(args.id) }
//    //verride val viewModel by viewModels<StoreCheckListViewModel>()
//
//    private val binding by viewBinding<StorecheckListFragmentBinding>()
//
//    //val adapter = StoreCheckListAdapter(::toStoreCheckDetail, ::toNewTaskNavigate)
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        viewModel.reload()
////        if (viewModel.viewState == null) {
////            //  viewModel.initViewState()
////            // viewModel.reload()
////        } else {
////            renderState()
////        }
//
//        binding.rvList.adapter = adapter
//
//        setupListeners()
//        setupObservers()
//    }
//
//    private fun setupListeners() {
//        //   binding.srl.setOnRefreshListener { viewModel.reload() }
//        binding.vResult.setTryAgainAction { viewModel.reload() }
//    }
//
//    private fun setupObservers() {
//        viewModel.viewStateEvent.observeEvent(viewLifecycleOwner) {
//            renderState()
//        }
//    }
//
//    private fun renderState() {
//        val viewState = viewModel.viewState
//        designByViewState(
//            viewState as BaseViewState, binding.root, binding.vResult
//        )
//
//        if (!viewState.isLoading && viewState.error == null) adapter.submitList(viewModel.getListForSubmit())
//    }
//
//    fun toNewTaskNavigate(
//        storeCheckId: String,
//        storeCheckNumber: String,
//        partner: String,
//        outlet: String,
//        tradeAgent: String
//    ) {
////        navController.navigate(
////            StoreCheckListFragmentDirections.actionStoreCheckListFragmentToCreateTasksFragment(
////                titleData = TitleData(
////                    getString(R.string.new_task_on_store_check, storeCheckNumber),
////                    null
////                ),
////                storeCheckId = storeCheckId,
////                partner = partner,
////                outlet = outlet,
////                tradeAgent = tradeAgent
////            )
////        )
//    }
//
//    fun toStoreCheckDetail(storeCheckId: String, storeCheckNumber: String, twoLevels: Boolean) {
//
////        val direction =
////            if (twoLevels) StoreCheckListFragmentDirections.actionStoreCheckListFragmentToStoreCheckLowerLevelFragment(
////                titleData = TitleData(
////                    storeCheckNumber,
////                    null
////                ), id = storeCheckId
////            ) else StoreCheckListFragmentDirections.actionStoreCheckListFragmentToStoreCheckTopLevelFragment(
////                titleData = TitleData(
////                    storeCheckNumber,
////                    null
////                ), id = storeCheckId
////            )
////
////        navController.navigate(direction)
//
//
//        val titleData = TitleData(storeCheckNumber, null)
//
////        val direction = when {
////            twoLevels ->
////                StoreCheckListFragmentDirections
////                    .actionStoreCheckListFragmentToStoreCheckTopLevelFragment(
////                        id = storeCheckId,
////                        titleData = titleData,
////                        twoLevels = true
////                    )
////
////            else -> StoreCheckListFragmentDirections
////                .actionStoreCheckListFragmentToStoreCheckLowerLevelFragment(
////                    id = storeCheckId,
////                    titleData = titleData,
////                    topLevelId = storeCheckId,
////                    twoLevels = false
////                )
////        }
////
////        navController.navigate(direction)
//    }
//}