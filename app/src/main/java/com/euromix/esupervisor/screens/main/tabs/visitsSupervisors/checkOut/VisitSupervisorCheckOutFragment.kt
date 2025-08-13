package com.euromix.esupervisor.screens.main.tabs.visitsSupervisors.checkOut

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.PermissionManager
import com.euromix.esupervisor.app.utils.designByViewState
import com.euromix.esupervisor.app.utils.formatTimeFromSeconds
import com.euromix.esupervisor.app.utils.permissionManager
import com.euromix.esupervisor.app.utils.observeEvent
import com.euromix.esupervisor.app.utils.toTextHM
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.app.utils.visibility
import com.euromix.esupervisor.databinding.VisitSupervisorCheckOutFragmentBinding
import com.euromix.esupervisor.screens.main.BaseViewState
import com.euromix.esupervisor.screens.viewModelCreator
import com.mapbox.android.core.location.LocationEngine
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import javax.inject.Inject

@AndroidEntryPoint
class VisitSupervisorCheckOutFragment :
    BaseFragment(R.layout.visit_supervisor_check_out_fragment) {

    @Inject
    lateinit var factory: VisitSupervisorCheckOutViewModel.Factory

    @Inject
    lateinit var locationEngine: LocationEngine

    private val args by navArgs<VisitSupervisorCheckOutFragmentArgs>()
    override val viewModel by viewModelCreator { factory.create(args.id) }
    private val binding by viewBinding<VisitSupervisorCheckOutFragmentBinding>()

    private lateinit var permissionManager: PermissionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        permissionManager = permissionManager()

        checkOutGetAndGetGeoData()

        setupListeners()
        setupObservers()

    }

    private fun checkOutGetAndGetGeoData() {
        viewModel.checkOutGetAndGetGeoData {
            permissionManager.requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                onAccepted = { viewModel.getGeoData() },
                onDenied = {}
            )
        }
    }

    private fun setupListeners() {

        binding.ibUpdate.setOnClickListener {
            checkOutGetAndGetGeoData()
        }

        binding.btnSaveCheckIn.setOnClickListener {
            viewModel.checkOutPost()
        }

        binding.vResult.setTryAgainAction {
            if (viewModel.viewState.address == null)
                checkOutGetAndGetGeoData()
            else
                viewModel.checkOutPost()
        }
    }

    private fun setupObservers() {

        viewModel.viewStateEvent.observeEvent(viewLifecycleOwner) {
            renderState(viewState = viewModel.viewState)
        }
    }

    private fun renderState(viewState: VisitSupervisorCheckOutViewModel.ViewState) {
        designByViewState(
            viewState as BaseViewState,
            binding.root,
            binding.vResult,
            specialViews = listOf(binding.clAppbarBottom)
        )

        if (!viewState.isLoading && viewState.error == null) {
            with(binding) {
                tvOutlet.text = viewState.outlet

                tvOutletCoordinates.text =
                    formatCoordinates(viewState.latitude, viewState.longitude)
                tvOutletCoordinates.setCompoundDrawablesWithIntrinsicBounds(
                    getCoordinateIcon(viewState.latitude, viewState.longitude),
                    0,
                    0,
                    0
                )

                tvCurrentCoordinates.text =
                    formatCoordinates(viewState.currentLatitude, viewState.currentLongitude)
                tvCurrentCoordinates.setCompoundDrawablesWithIntrinsicBounds(
                    getCoordinateIcon(viewState.currentLatitude, viewState.currentLongitude),
                    0,
                    0,
                    0
                )

                tvDateCoordinates.text = LocalDateTime.now().toTextHM()

                tvCurrentLocation.text = viewState.address

                val isCoordinatesDefined =
                    viewState.latitude != 0.0 && viewState.longitude != 0.0 &&
                            viewState.currentLatitude != 0.0 && viewState.currentLongitude != 0.0

                if (isCoordinatesDefined) {
                    tvCoordinateDeviation.text =
                        getString(R.string.deviation_m, viewState.deviation)
                    tvCoordinateDeviation.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_location_mark,
                        0,
                        0,
                        0
                    )
                } else {
                    tvCoordinateDeviation.text = getString(R.string.undefined)
                    tvCoordinateDeviation.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_location_mark_red,
                        0,
                        0,
                        0
                    )
                }

                tvOutletTime.text = formatTimeFromSeconds(viewState.outletTime)

                clAppbarBottom.visibility(!viewState.isCheckOut)

            }
        }
    }

    private fun formatCoordinates(latitude: Double, longitude: Double) =
        if (latitude == 0.0 && longitude == 0.0)
            getString(R.string.coordinates_not_defined)
        else
            getString(R.string.two_double_parameters, latitude, longitude)

    private fun getCoordinateIcon(latitude: Double, longitude: Double) =
        if (latitude != 0.0 && longitude != 0.0) R.drawable.ic_coordinate else R.drawable.ic_coordinate_red
}