package com.euromix.esupervisor.screens.main.tabs.routeMap

import android.graphics.Bitmap
import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.routes.RoutesRepository
import com.euromix.esupervisor.app.model.routes.entities.MapPoint
import com.euromix.esupervisor.app.model.routes.entities.MapPointSigns
import com.euromix.esupervisor.app.model.routes.entities.OutletData
import com.euromix.esupervisor.app.model.routes.entities.RouteMapSelection
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.MutableLiveEvent
import com.euromix.esupervisor.app.utils.ResourceManager
import com.euromix.esupervisor.app.utils.toJsonString
import com.euromix.esupervisor.app.utils.publishEvent
import com.euromix.esupervisor.app.utils.share
import com.euromix.esupervisor.app.utils.toLocalDate
import com.euromix.esupervisor.screens.main.BaseViewState
import com.euromix.esupervisor.sources.routes.entities.MapPointsRequestEntity.Companion.mapPointsRequestEntity
import com.euromix.esupervisor.sources.routes.entities.OutletDataRequestEntity
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.toCameraOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class RouteMapViewModel @Inject constructor(
    private val resManager: ResourceManager,
    private val routesRepository: RoutesRepository
) : BaseViewModel() {

    private val cameraOptionsBuilder = CameraOptions.Builder()

    private var _viewState = ViewState()
    val viewState: ViewState
        get() = _viewState

    private val _viewStateEvent = MutableLiveEvent<Unit>()
    val viewStateEvent = _viewStateEvent.share()

    private var _selection: RouteMapSelection = RouteMapSelection(LocalDate.now())
    val selection: RouteMapSelection
        get() = _selection

    private val _selectionEvent = MutableLiveEvent<RouteMapSelection>()
    val selectionEvent = _selectionEvent.share()

    private var bitmapCache: Map<MapPointSigns?, Bitmap?>? = null

    init {
        updateSelection()
    }

    private fun <T> updateViewState(result: Result<T>) {

        when (result) {
            is Pending -> handlePendingState()
            is Success -> {

                if (result.value is List<*>) {
                    handleSuccessMapPoints(result.value as List<MapPoint>)
                } else if (result.value is OutletData) {
                    handleSuccessOutletData(result.value)
                }
            }

            is Error -> handleError(result.error)
            else -> {}
        }

        _viewStateEvent.publishEvent()
    }

    private fun updateViewState(value: List<MapPoint>, force: Boolean) {

        with(viewState) {
            if ((currentVisibleMarkers != value && mapPoints.isNotEmpty()) || force) {
                _viewState = _viewState.copy(currentVisibleMarkers = value)
                _viewStateEvent.publishEvent()
            }
        }
    }

    private fun handlePendingState() {
        _viewState = _viewState.copy(isLoading = true, error = null)
    }

    private fun handleSuccessMapPoints(value: List<MapPoint>) {
        _viewState = _viewState.copy(
            isLoading = false,
            error = null,
            mapPoints = value,
            options = value.map { it.toPointsAnnotationOptions() },
            currentVisibleMarkers = listOf(),
            outletData = null,
            posCamera = true
        )
    }

    private fun handleSuccessOutletData(value: OutletData) {
        _viewState = _viewState.copy(isLoading = false, error = null, outletData = value)
    }

    private fun handleError(error: Throwable) {
        _viewState = _viewState.copy(isLoading = false, error = error)
    }

    private fun getMapPoints() {
        safeLaunch {
            routesRepository.getMapPoints(mapPointsRequestEntity(_selection)).collect { result ->
                updateViewState(result)
            }
        }
    }

    private fun visibleMarkers(mapboxMap: MapboxMap): List<MapPoint> {
        val visibleBounds = mapboxMap.cameraState.toCameraOptions()
            .let { cameraOptions -> mapboxMap.coordinateBoundsForCamera(cameraOptions) }

        val filteredOptions = _viewState.options.asSequence().filter { paOptions ->
            paOptions.getPoint()
                ?.let { point -> visibleBounds.contains(point, false) } == true
        }.take(MAX_COUNT_POINTS).toList()

        return filteredOptions
            .mapNotNull { paOptions ->
                val point = paOptions.getPoint()
                _viewState.mapPoints.find {
                    it.latitude == point?.latitude() && it.longitude == point.longitude()
                }
            }
            .takeIf { it.size < MAX_COUNT_POINTS }
            ?: emptyList()
    }

    fun updateSelection(selection: RouteMapSelection? = null) {

        _selection = selection ?: _selection
        _selectionEvent.publishEvent(_selection)
    }

    fun changeDay(increase: Boolean = true) {
        _selection = _selection.copy(day = _selection.day.plusDays(if (increase) 1 else -1))
        _selectionEvent.publishEvent(_selection)
    }

    fun changeDay(day: Long) {
        _selection = _selection.copy(day = day.toLocalDate())
        _selectionEvent.publishEvent(_selection)
    }

    fun changeCurrentVisibleMarkers(
        mapboxMap: MapboxMap,
        force: Boolean = false
    ) {
        val visibleMarkers = visibleMarkers(mapboxMap)
        updateViewState(visibleMarkers, force)
    }

    fun publishViewStateEvent() {
        _viewStateEvent.publishEvent()
    }

    fun clearPosCamera() {
        _viewState = _viewState.copy(posCamera = false)
    }

    fun clearOutletData() {
        _viewState = _viewState.copy(outletData = null)
    }

    fun getOutletData(outletId: String) {

        safeLaunch {
            routesRepository.getOutletData(_selection.day.toJsonString().let { day ->
                OutletDataRequestEntity(day, day, outletId)
            }).collect {
                updateViewState(it)
            }
        }
    }

    fun reload() {
        getMapPoints()
    }

    fun setBitmapCache() {

        if (bitmapCache == null)
            bitmapCache = MapPointSigns.bitmapCache(resManager)
    }

    fun getBitmap(mapPoint: MapPoint) = bitmapCache?.let {
        it[mapPoint.signs] ?: it[null]
    }

    fun setCamera(
        mapboxMap: MapboxMap,
        autoPos: Boolean = true
    ) {

        var longitude = 31.41933250
        var latitude = 49.02459717

        with(viewState) {
            if (!autoPos && mapPoints.isNotEmpty()) {
                longitude = mapPoints[0].longitude
                latitude = mapPoints[0].latitude
            }
        }

        mapboxMap.setCamera(
            cameraOptionsBuilder
                .center(Point.fromLngLat(longitude, latitude))
                .zoom(6.0)
                .build()
        )
    }

    data class ViewState(
        override val isLoading: Boolean = false,
        override val error: Throwable? = null,
        val currentVisibleMarkers: List<MapPoint> = listOf(),
        val options: List<PointAnnotationOptions> = listOf(),
        val mapPoints: List<MapPoint> = listOf(),
        val outletData: OutletData? = null,
        val posCamera: Boolean = false
    ) : BaseViewState()

    companion object {
        private const val MAX_COUNT_POINTS = 100
    }
}