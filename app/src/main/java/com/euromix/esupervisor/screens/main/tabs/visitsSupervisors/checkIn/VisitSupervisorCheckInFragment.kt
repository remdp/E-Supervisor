package com.euromix.esupervisor.screens.main.tabs.visitsSupervisors.checkIn

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.NewPermissionManager
import com.euromix.esupervisor.app.utils.designByViewState
import com.euromix.esupervisor.app.utils.newPermissionManager
import com.euromix.esupervisor.app.utils.observeEvent
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.databinding.VisitSupervisorCheckInFragmentBinding
import com.euromix.esupervisor.screens.main.BaseViewState
import com.euromix.esupervisor.screens.viewModelCreator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class VisitSupervisorCheckInFragment : BaseFragment(R.layout.visit_supervisor_check_in_fragment) {

    @Inject
    lateinit var factory: VisitSupervisorCheckInViewModel.Factory

    private val args by navArgs<VisitSupervisorCheckInFragmentArgs>()
    override val viewModel by viewModelCreator { factory.create(args.id) }
    private val binding by viewBinding<VisitSupervisorCheckInFragmentBinding>()

    private lateinit var permissionManager: NewPermissionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        permissionManager =
            newPermissionManager(onAccepted = { viewModel.getLocation() })


        viewModel.requestForGetLocation(permissionManager)

        binding.tvOutlet.text = args.outlet

        setupListeners()
        setupObservers()

    }

    private fun setupListeners() {

        binding.ibUpdate.setOnClickListener {
            viewModel.requestForGetLocation(
                permissionManager
            )
        }

        binding.btnCheckIn.setOnClickListener { viewModel.checkIn() }

    }

    private fun setupObservers() {

        viewModel.viewStateEvent.observeEvent(viewLifecycleOwner) {
            renderState(it)
        }

    }

    private fun renderState(viewState: VisitSupervisorCheckInViewModel.ViewState) {
        designByViewState(
            viewState as BaseViewState, binding.root, binding.vResult
        )

        if (!viewState.isLoading && viewState.error == null) {
            //  binding.tvOutlet.text = viewState.detailData?.outlet

            with(binding){
                tvCurrentLatitude.text = viewState.latitude.toString()
                tvCurrentLongitude.text = viewState.longitude.toString()
                tvCurrentLocation.text = viewState.address
            }
        }

    }

}