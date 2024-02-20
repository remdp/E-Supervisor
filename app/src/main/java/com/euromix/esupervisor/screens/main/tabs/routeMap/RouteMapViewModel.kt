package com.euromix.esupervisor.screens.main.tabs.routeMap

import android.content.Context
import android.graphics.Bitmap
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.routes.RoutesRepository
import com.euromix.esupervisor.app.model.routes.entities.MapPoint
import com.euromix.esupervisor.app.model.routes.entities.OutletData
import com.euromix.esupervisor.app.model.routes.entities.MapPointSigns
import com.euromix.esupervisor.app.model.routes.entities.RouteMapSelection
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.Event
import com.euromix.esupervisor.app.utils.MutableLiveEvent
import com.euromix.esupervisor.app.utils.dateToJsonString
import com.euromix.esupervisor.app.utils.publishEvent
import com.euromix.esupervisor.app.utils.share
import com.euromix.esupervisor.app.utils.toLocalDate
import com.euromix.esupervisor.sources.routes.entities.MapPointsRequestEntity.Companion.mapPointsRequestEntity
import com.euromix.esupervisor.sources.routes.entities.OutletDataRequestEntity
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.toCameraOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class RouteMapViewModel @Inject constructor(
    private val routesRepository: RoutesRepository
) : BaseViewModel() {

    private val cameraOptionsBuilder = CameraOptions.Builder()
    private var _isInitialized = false
    val isInitialized
        get() = _isInitialized

    private val _currentVisibleMarkers = mutableListOf<MapPoint>()
    private val _currentVisibleMarkersEvent = MutableLiveEvent<List<MapPoint>>()
    val currentVisibleMarkersEvent = _currentVisibleMarkersEvent.share()

    private val _options: MutableList<PointAnnotationOptions> = mutableListOf()
    val options: List<PointAnnotationOptions>
        get() = _options

    private val _mapPoints: MutableList<MapPoint> = mutableListOf()
    private val mapPoints: List<MapPoint>
        get() = _mapPoints
    private val _mapPointsEvent = MutableLiveEvent<Result<List<MapPoint>>>()
    val mapPointsEvent = _mapPointsEvent.share()

    private var _selection: RouteMapSelection = RouteMapSelection(LocalDate.now())
    val selection: RouteMapSelection
        get() = _selection

    private val _selectionEvent = MutableLiveEvent<RouteMapSelection>()
    val selectionEvent = _selectionEvent.share()

    private val _outletData = MutableLiveEvent<Result<OutletData>>()
    val outletData = _outletData.share()

    private var currentJob: Job? = null

    private var bitmapCache: Map<MapPointSigns?, Bitmap?>? = null

    init {
        updateSelection()
        _isInitialized = true
    }

    private fun getMapPoints() {

        _mapPoints.clear()
        _options.clear()

        currentJob?.cancel()

        currentJob = safeLaunch {

            routesRepository.getMapPoints(mapPointsRequestEntity(_selection)).collect { result ->

                if (result !is Pending)
                    currentJob = null

                if (result is Success) {

                    _mapPoints.addAll(result.value)

                    result.value.forEach {
                        _options.add(it.toPointsAnnotationOptions())
                    }
                }
                _mapPointsEvent.value = Event(result)
            }
        }
    }

    private fun visibleMarkers(mapboxMap: MapboxMap): List<MapPoint> {
        val visibleBounds = mapboxMap.cameraState.toCameraOptions()
            .let { cameraOptions -> mapboxMap.coordinateBoundsForCamera(cameraOptions) }

        val filteredOptions = options.asSequence().filter { paOptions ->
            paOptions.getPoint()
                ?.let { point -> visibleBounds.contains(point, false) } == true
        }.take(MAX_COUNT_POINTS).toList()

        return filteredOptions
            .mapNotNull { paOptions ->
                val point = paOptions.getPoint()
                mapPoints.find {
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

        if (currentJob == null) {
            val visibleMarkers = visibleMarkers(mapboxMap)

            if ((_currentVisibleMarkers != visibleMarkers && _mapPoints.isNotEmpty()) || force) {
                _currentVisibleMarkers.clear()
                _currentVisibleMarkers.addAll(visibleMarkers)
                _currentVisibleMarkersEvent.publishEvent(_currentVisibleMarkers)
            }
        }
    }

    fun publishCurrentVisibleMarkers() {
        _currentVisibleMarkersEvent.publishEvent(_currentVisibleMarkers)
    }

    fun getOutletData(outletId: String) {

        currentJob?.cancel()
        currentJob = safeLaunch {

            routesRepository.getOutletData(_selection.day.dateToJsonString().let { day ->
                OutletDataRequestEntity(day, day, outletId)
            }).collect {

                if (it !is Pending)
                    currentJob = null

                _outletData.publishEvent(it)
            }
        }
    }

    fun reload() {
        getMapPoints()
    }

    fun setBitmapCache(context: Context) {

        if (bitmapCache == null)
            bitmapCache = MapPointSigns.bitmapCache(context)
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

        if (!autoPos && _mapPoints.isNotEmpty()) {
            longitude = _mapPoints[0].longitude
            latitude = _mapPoints[0].latitude
        }

        mapboxMap.setCamera(
            cameraOptionsBuilder
                .center(Point.fromLngLat(longitude, latitude))
                .zoom(6.0)
                .build()
        )
    }
    companion object {
        private const val MAX_COUNT_POINTS = 100
    }

}