package com.euromix.esupervisor.screens.main.tabs.visitsSupervisors.checkIn

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.PermissionManager
import com.euromix.esupervisor.app.utils.base64StringFromUri
import com.euromix.esupervisor.app.utils.designByViewState
import com.euromix.esupervisor.app.utils.permissionManager
import com.euromix.esupervisor.app.utils.observeEvent
import com.euromix.esupervisor.app.utils.setBitmapFromBase64String
import com.euromix.esupervisor.app.utils.toTextHM
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.app.utils.visibility
import com.euromix.esupervisor.databinding.VisitSupervisorCheckInFragmentBinding
import com.euromix.esupervisor.dialogs.selectPictureDialog.SelectPictureDialog
import com.euromix.esupervisor.dialogs.selectPictureDialog.SelectPictureViewModel
import com.euromix.esupervisor.screens.main.BaseViewState
import com.euromix.esupervisor.screens.viewModelCreator
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import javax.inject.Inject

@AndroidEntryPoint
class VisitSupervisorCheckInFragment : BaseFragment(R.layout.visit_supervisor_check_in_fragment) {

    @Inject
    lateinit var factory: VisitSupervisorCheckInViewModel.Factory

    private val args by navArgs<VisitSupervisorCheckInFragmentArgs>()
    override val viewModel by viewModelCreator { factory.create(args.id) }
    private val binding by viewBinding<VisitSupervisorCheckInFragmentBinding>()

    private lateinit var permissionManager: PermissionManager

    private val selectPictureViewModel by activityViewModels<SelectPictureViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        permissionManager =
            permissionManager()

        checkInGetAndGetGeoData()

        setupListeners()
        setupObservers()

    }

    private fun checkInGetAndGetGeoData() {
        viewModel.checkInGetAndGetGeoData {
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
            checkInGetAndGetGeoData()
        }

        binding.btnPhotoCheckIn.setOnClickListener {
            SelectPictureDialog.newInstance().show(parentFragmentManager, null)
        }

        binding.btnSaveCheckIn.setOnClickListener { viewModel.checkInPost() }

        binding.vResult.setTryAgainAction {
            if (viewModel.viewState.address == null)
                checkInGetAndGetGeoData()
            else
                viewModel.checkInPost()
        }
    }

    private fun setupObservers() {

        selectPictureViewModel.uriEvent.observeEvent(viewLifecycleOwner) {
            viewModel.selectPicture(base64StringFromUri(requireContext(), it))
        }

        viewModel.viewStateEvent.observeEvent(viewLifecycleOwner) {
            renderState(viewState = viewModel.viewState)
        }
    }

    private fun renderState(viewState: VisitSupervisorCheckInViewModel.ViewState) {
        designByViewState(
            viewState as BaseViewState,
            binding.root,
            binding.vResult,
            specialViews = listOf(binding.clAppbarBottom, binding.btnPhotoCheckIn, binding.ivPhoto)
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

                ivPhoto.setBitmapFromBase64String(viewState.base64CheckInPhoto)
                ivPhoto.visibility(
                    viewState.base64CheckInPhoto.isNotEmpty()
                )

                clAppbarBottom.visibility(
                    !viewState.isCheckIn &&
                            (viewState.deviation <= viewState.checkInDeviation || viewState.base64CheckInPhoto.isNotEmpty())
                )

                btnPhotoCheckIn.visibility(
                    !viewState.isCheckIn &&
                            viewState.deviation > viewState.checkInDeviation &&
                            viewState.base64CheckInPhoto.isEmpty()
                )
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