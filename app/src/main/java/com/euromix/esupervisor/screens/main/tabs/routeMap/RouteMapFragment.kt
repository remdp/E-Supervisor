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
import com.euromix.esupervisor.App
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.Const
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.routes.entities.MapPoint
import com.euromix.esupervisor.app.model.routes.entities.OutletData
import com.euromix.esupervisor.app.model.routes.entities.RouteMapSelection
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.utils.dateToString
import com.euromix.esupervisor.app.utils.designByResult
import com.euromix.esupervisor.app.utils.observeEvent
import com.euromix.esupervisor.app.utils.toLong
import com.euromix.esupervisor.app.utils.viewBinding
import com.euromix.esupervisor.databinding.RouteMapFragmentBinding
import com.euromix.esupervisor.databinding.StatisticPopupBinding
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

class RouteMapFragment : BaseFragment(R.layout.route_map_fragment) {

    override val viewModel by viewModels<RouteMapViewModel>()
    private val binding by viewBinding<RouteMapFragmentBinding>()

    private lateinit var mapboxMap: MapboxMap

    private lateinit var pointAnnotationManager: PointAnnotationManager
    private lateinit var clusterAnnotationManager: PointAnnotationManager

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

        viewModel.setBitmapCache(requireContext())
        if (viewModel.isInitialized) {
            //todo need refactor
            binding.tvDay.text = viewModel.selection.day.dateToString()
            viewModel.setCamera(mapboxMap, false)
            viewModel.publishCurrentVisibleMarkers()
        }
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
                it.getData()?.asJsonObject?.getAsJsonPrimitive("outletId")?.asString?.let { outletId ->
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

        viewModel.mapPointsEvent.observeEvent(viewLifecycleOwner) { result ->

            if (result is Success) {
                viewModel.setCamera(mapboxMap, false)
                viewModel.changeCurrentVisibleMarkers(mapboxMap, force = true)
            }
            designByResult(result, binding.root, binding.vResult)
        }
        viewModel.selectionEvent.observeEvent(viewLifecycleOwner) {
            binding.tvDay.text = it.day.dateToString()
            viewModel.reload()
        }

        viewModel.currentVisibleMarkersEvent.observeEvent(viewLifecycleOwner) {
            if (it.isNotEmpty())
                showPoints(it)
            else
                showClusters()
        }

        viewModel.outletData.observeEvent(viewLifecycleOwner) { result ->

            if (result is Success) {
                showPopup(result.value)

            }
            designByResult(result, binding.root, binding.vResult, null, listOf(binding.mapView))
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

    private fun showClusters() {
        pointAnnotationManager.deleteAll()
        viewModel.options.let { options ->
            clusterAnnotationManager.create(options)
        }
    }

    private fun showPoints(pointsList: List<MapPoint>) {
        clusterAnnotationManager.deleteAll()
        pointAnnotationManager.deleteAll()
        if (pointsList.isNotEmpty()) {

            val po = createPointAnnotationOptions(pointsList)

            pointAnnotationManager.create(po)
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
                    .withData(JsonObject().apply { addProperty("outletId", it.outletId) })
            }
        }
    }

    private fun showPopup(outletData: OutletData) {

        val backgroundView = View(context)
        backgroundView.setBackgroundColor(Color.parseColor("#80000000")) // Устанавливаем цвет и прозрачность фона
        backgroundView.isClickable = true

        binding.root.addView(
            backgroundView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        // Создаем объект View Binding
        val popupBinding = StatisticPopupBinding.inflate(LayoutInflater.from(requireContext()))

        with(popupBinding) {

            tvPartner.text = outletData.partners.firstOrNull()
            tvOutletName.text = outletData.name
            tvOutletAddress.text = outletData.address

            tvVisitTime.text = outletData.checkIn
            tvVisitDuration.text = getString(R.string.time_at_the_outlet, outletData.outletTime)
            tvOrderSum.text = getString(R.string.sum_hryvnia, outletData.orderSum)
            tvCashReceiptOrderSum.text =
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
            App.getDrawable(
                requireContext(),
                R.drawable.bg_8dp_white_border
            )
        )
        popupWindow.showAtLocation(binding.root, Gravity.CENTER, 0, -300)
    }
}