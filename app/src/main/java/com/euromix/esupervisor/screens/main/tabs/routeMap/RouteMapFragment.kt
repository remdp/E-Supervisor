package com.euromix.esupervisor.screens.main.tabs.routeMap

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.Const
import com.euromix.esupervisor.app.model.routes.entities.MapPoint
import com.euromix.esupervisor.app.model.routes.entities.OutletData
import com.euromix.esupervisor.app.model.routes.entities.RouteMapSelection
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.ResourceManager
import com.euromix.esupervisor.app.utils.dateToString
import com.euromix.esupervisor.app.utils.designByViewState
import com.euromix.esupervisor.app.utils.observeEvent
import com.euromix.esupervisor.app.utils.toLong
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.databinding.RouteMapFragmentBinding
import com.euromix.esupervisor.databinding.StatisticPopupBinding
import com.euromix.esupervisor.screens.main.BaseViewState
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.gson.JsonObject
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.extension.style.expressions.dsl.generated.literal
import com.mapbox.maps.extension.style.expressions.generated.Expression
import com.mapbox.maps.plugin.annotation.AnnotationConfig
import com.mapbox.maps.plugin.annotation.AnnotationSourceOptions
import com.mapbox.maps.plugin.annotation.ClusterOptions
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RouteMapFragment : BaseFragment(R.layout.route_map_fragment) {

    override val viewModel by viewModels<RouteMapViewModel>()
    private val binding by viewBinding<RouteMapFragmentBinding>()

    private lateinit var mapboxMap: MapboxMap

    private lateinit var pointAnnotationManager: PointAnnotationManager
    private lateinit var clusterAnnotationManager: PointAnnotationManager

    @Inject
    lateinit var resManager: ResourceManager

    private val annotationConfig = AnnotationConfig(
        annotationSourceOptions = AnnotationSourceOptions(
            clusterOptions = ClusterOptions(
                textColorExpression = Expression.color(Color.WHITE),
                textColor = Color.BLACK, // Will not be applied as textColorExpression has been set
                textSize = 20.0,
                circleRadiusExpression = literal(25.0),
                clusterMaxZoom = 16,
                colorLevels = listOf(
                    Pair(100, Color.argb(100, 255, 0, 0)),
                    Pair(50, Color.argb(100, 0, 0, 255)),
                    Pair(0, Color.argb(100, 0, 255, 0))
                )
            )
        )
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapboxMap = binding.mapView.getMapboxMap()

        pointAnnotationManager =
            binding.mapView.annotations.createPointAnnotationManager()
        clusterAnnotationManager =
            binding.mapView.annotations.createPointAnnotationManager(annotationConfig)

        viewModel.setCamera(mapboxMap)
        setupListeners()
        setupObservers()

        viewModel.setBitmapCache()
        binding.tvDay.text = viewModel.selection.day.dateToString()
        viewModel.publishViewStateEvent()
    }

    private fun setupListeners() {

        binding.ivArrowLeft.setOnClickListener { viewModel.changeDay(false) }
        binding.ivArrowRight.setOnClickListener { viewModel.changeDay() }

        binding.ivFunnel.setOnClickListener {
            clusterAnnotationManager.deleteAll()
            pointAnnotationManager.deleteAll()
            val direction =
                RouteMapFragmentDirections.actionRouteMapFragmentToRouteMapSelectionFragment(
                    selection = viewModel.selection
                )
            findNavController().navigate(direction)
        }

        mapboxMap.addOnMapIdleListener {
            viewModel.changeCurrentVisibleMarkers(mapboxMap)
        }

        pointAnnotationManager.addClickListener(
            OnPointAnnotationClickListener {
                it.getData()?.asJsonObject?.getAsJsonPrimitive(OUTLET_ID)?.asString?.let { outletId ->
                    viewModel.getOutletData(outletId)
                }
                true
            }
        )

        binding.tvDay.setOnClickListener {

            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setSelection(viewModel.selection.day.toLong())
                    .build()

            datePicker.addOnPositiveButtonClickListener {
                viewModel.changeDay(it)
            }

            datePicker.show(parentFragmentManager, null)

        }

        binding.vResult.setTryAgainAction { viewModel.reload() }
    }

    private fun setupObservers() {

        viewModel.viewStateEvent.observeEvent(viewLifecycleOwner) {
            renderState()
        }

        viewModel.selectionEvent.observeEvent(viewLifecycleOwner) {
            binding.tvDay.text = it.day.dateToString()
            viewModel.reload()
        }

        setFragmentResultListener(Const.SELECTION_KEY) { requestKey, bundle ->

            val selection: RouteMapSelection?
            val cancelSelection: Boolean
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                selection = bundle.getParcelable(requestKey, RouteMapSelection::class.java)
                cancelSelection = bundle.getBoolean(Const.CANCEL)
            } else {
                selection = bundle.getParcelable(requestKey)
                cancelSelection = bundle.getBoolean(Const.CANCEL)
            }

            if (!cancelSelection)
                viewModel.updateSelection(selection)
            else {
                binding.tvDay.text = viewModel.selection.day.dateToString()
            }
        }
    }

    private fun renderState() {

        val viewState = viewModel.viewState

        designByViewState(
            viewState as BaseViewState, binding.root, binding.vResult
        )

        if (viewState.posCamera)
            viewModel.setCamera(mapboxMap, false)

        viewState.outletData?.let {
            showPopup(it)
            viewModel.clearOutletData()
        } ?: run {
            if (viewState.currentVisibleMarkers.isNotEmpty())
                showPoints(viewState.currentVisibleMarkers)
            else
                showClusters()

            viewModel.clearPosCamera()
        }
    }

    private fun showClusters() {
        pointAnnotationManager.deleteAll()
        viewModel.viewState.options.let { options ->
            clusterAnnotationManager.create(options)
        }
    }

    private fun showPoints(pointsList: List<MapPoint>) {
        clusterAnnotationManager.deleteAll()
        pointAnnotationManager.deleteAll()
        if (pointsList.isNotEmpty()) {
            pointAnnotationManager.create(createPointAnnotationOptions(pointsList))
        }
    }

    private fun createPointAnnotationOptions(
        pointsList: List<MapPoint>
    ): List<PointAnnotationOptions> {

        return pointsList.mapNotNull {
            viewModel.getBitmap(it)?.let { bitmap ->
                PointAnnotationOptions()
                    .withPoint(Point.fromLngLat(it.longitude, it.latitude))
                    .withIconImage(bitmap)
                    .withData(JsonObject().apply { addProperty(OUTLET_ID, it.outletId) })
            }
        }
    }

    private fun showPopup(outletData: OutletData) {

        val backgroundView = View(context)
        backgroundView.setBackgroundColor(Color.parseColor("#80000000"))
        backgroundView.isClickable = true

        binding.root.addView(
            backgroundView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val popupBinding = StatisticPopupBinding.inflate(LayoutInflater.from(requireContext()))

        with(popupBinding) {

            tvPartner.text = outletData.partners.firstOrNull()
            tvOutletName.text = outletData.name
            tvOutletAddress.text = outletData.address

            iVisitStatistic.tvVisitTime.text = outletData.checkIn
            iVisitStatistic.tvVisitDuration.text =
                getString(R.string.time_at_the_outlet, outletData.outletTime)
            iVisitStatistic.tvOrderSum.text = getString(R.string.sum_hryvnia, outletData.orderSum)
            iVisitStatistic.tvCashReceiptOrderSum.text =
                getString(R.string.sum_hryvnia, outletData.cashReceiptOrderSum)
        }

        val popupWindow = PopupWindow(
            popupBinding.root,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        popupBinding.ivClose.setOnClickListener {
            popupWindow.dismiss()
            binding.root.removeView(backgroundView)
        }

        popupWindow.setBackgroundDrawable(
            resManager.getDrawable(R.drawable.bg_8dp_white_border_gray_200)
        )
        popupWindow.showAtLocation(binding.root, Gravity.CENTER, 0, -300)
    }

    companion object {
        private const val OUTLET_ID = "OUTLET_ID"
    }
}