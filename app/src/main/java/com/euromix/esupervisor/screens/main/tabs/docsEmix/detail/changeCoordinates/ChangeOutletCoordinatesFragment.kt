package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.changeCoordinates

import android.location.Geocoder
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.euromix.esupervisor.App
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.ConnectionException
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.designByViewState
import com.euromix.esupervisor.app.utils.dialogPositiveButton
import com.euromix.esupervisor.app.utils.observeEvent
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.databinding.ChangeCoordinatesPopupBinding
import com.euromix.esupervisor.databinding.ChangeOutletCoordinatesFragmentBinding
import com.euromix.esupervisor.screens.viewModelCreator
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.gestures
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject


@AndroidEntryPoint
class ChangeOutletCoordinatesFragment : BaseFragment(R.layout.change_outlet_coordinates_fragment) {

    @Inject
    lateinit var factory: ChangeOutletCoordinatesViewModel.Factory
    override val viewModel by viewModelCreator { factory.create(args.extId) }
    private val args by navArgs<ChangeOutletCoordinatesFragmentArgs>()

    private val binding by viewBinding<ChangeOutletCoordinatesFragmentBinding>()

    private lateinit var mapboxMap: MapboxMap
    private lateinit var pointAnnotationManager: PointAnnotationManager
    private lateinit var geocoder: Geocoder

    private var x = 0f
    private var y = 0f

    private val visiblePopups = HashMap<String, PopupWindow>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapboxMap = binding.mapView.getMapboxMap()
        viewModel.setCamera(mapboxMap)

        pointAnnotationManager =
            binding.mapView.annotations.createPointAnnotationManager()

        geocoder = Geocoder(requireContext(), Locale.getDefault())

        setupObservers()
        setupListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        visiblePopups.forEach { it.value.dismiss() }
    }

    private fun setupObservers() {

        viewModel.viewStateEvent.observeEvent(viewLifecycleOwner) {viewState->
            renderState(viewState)
            if (!viewState.isLoading) {
                showPoints(viewModel.createPointsAnnotationOptions(requireContext()))

                viewModel.calculateMidpoint(
                    viewState.longitudeOld,
                    viewState.latitudeOld,
                    viewState.longitudeNew,
                    viewState.latitudeNew
                ).apply {
                    viewModel.setCamera(
                        mapboxMap,
                        viewModel.calculateZoom(viewState.distance),
                        first,
                        second
                    )
                }
                viewModel.setGeoAddress(geocoder, false)
                viewModel.setGeoAddress(geocoder, true)
            }
        }
    }

    private fun setupListeners() {

        pointAnnotationManager.addClickListener { pointAnnotation ->

            val paData = pointAnnotation.getData()?.asJsonObject?.getAsJsonPrimitive(
                ChangeOutletCoordinatesViewModel.PA_DATA
            )
                ?.let { PAOptionsData.newInstance(it.asString) }

            paData?.let {
                if (visiblePopups[it.coordinates] == null)
                    showPopup(it)
            }
            true
        }

        binding.btnApprove.setOnClickListener { viewModel.acceptChangeCoordinates(true, "") }
        binding.btnReject.setOnClickListener {
            dialogPositiveButton(requireContext(), R.string.reject_reason) { enteredText, _, _ ->
                viewModel.acceptChangeCoordinates(false, enteredText)
            }
        }

        binding.vResult.setTryAgainAction { viewModel.reload() }

        binding.mapView.setOnTouchListener { v, event ->
            x = event.x
            y = event.y
            false
        }
    }

    private fun showPoints(paOptions: List<PointAnnotationOptions>) {
        paOptions.takeIf { it.isNotEmpty() }?.let { pointAnnotationManager.create(it) }
    }

    private fun renderState(state: ViewState) {

        if (state.error is ConnectionException || state.error == null)
            binding.vResult.setTryAgainAction { viewModel.reload() }
        else
            binding.vResult.setTryAgainAction(null)

        designByViewState(
            state,
            binding.root,
            binding.vResult,
            null,
            listOf(binding.clAppbarBottom)
        )

        with(binding) {
            tvOutlet.text = state.outletPresentation
            tvDistance.text = getString(R.string.distance_meters, state.distance.toString())

            clAppbarBottom.isVisible = state.canBeAgreed
        }
    }

    private fun availabilityGestures() {

        val available = visiblePopups.isEmpty()

        binding.mapView.gestures.apply {
            pitchEnabled = available
            scrollEnabled = available
            pinchToZoomEnabled = available
            rotateEnabled = available
            doubleTapToZoomInEnabled = available
            doubleTouchToZoomOutEnabled = available
            pinchScrollEnabled = available
            pinchToZoomDecelerationEnabled = available
            quickZoomEnabled = available
            simultaneousRotateAndPinchToZoomEnabled = available
            rotateDecelerationEnabled = available
            scrollDecelerationEnabled = available
        }
    }

    private fun showPopup(paData: PAOptionsData) {

        val popupBinding =
            ChangeCoordinatesPopupBinding.inflate(LayoutInflater.from(requireContext()))

        with(popupBinding) {

            tvCoordinates.text = paData.coordinates
            tvNewOld.text = if (paData.new) "NEW" else "OLD"
            tvNewOld.background = if (paData.new) App.getDrawable(
                requireContext(),
                R.drawable.bg_4dp_green_light
            ) else App.getDrawable(requireContext(), R.drawable.bg_4dp_red_light)
            tvAddress.text = viewModel.getAddressFromViewState(paData.new)

        }

        val popupWindow = PopupWindow(
            popupBinding.root,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        popupBinding.ivClose.setOnClickListener {
            popupWindow.dismiss()
            visiblePopups.remove(paData.coordinates)
            availabilityGestures()
        }

        popupWindow.setBackgroundDrawable(
            App.getDrawable(
                requireContext(),
                R.drawable.bg_8dp_white_border
            )
        )
        popupWindow.showAtLocation(
            binding.root,
            Gravity.NO_GRAVITY,
            x.toInt(),
            y.toInt()
        )
        visiblePopups[paData.coordinates] = popupWindow
        availabilityGestures()
    }

}