package com.euromix.esupervisor.screens.main.tabs.docsEmix.detail.changeCoordinates

import android.content.Context
import android.location.Geocoder
import android.os.Build
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.docEmix.DocEmixDetailRepository
import com.euromix.esupervisor.app.model.docEmix.entities.ChangeCoordinates
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.MutableLiveEvent
import com.euromix.esupervisor.app.utils.bitmapFromDrawableRes
import com.euromix.esupervisor.app.utils.publishEvent
import com.euromix.esupervisor.app.utils.share
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mapbox.geojson.Point
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class ChangeOutletCoordinatesViewModel @AssistedInject constructor(
    @Assisted private val extId: String,
    private val docEmixDetailRepository: DocEmixDetailRepository
) : BaseViewModel() {

    private var _viewState: ViewState = ViewState()
    private val _viewStateEvent = MutableLiveEvent<ViewState>()
    val viewStateEvent = _viewStateEvent.share()

    init {
        reload()
    }
    private fun <T> updateViewState(result: Result<T>) {
        when (result) {
            is Pending -> {handlePendingState()}
            is Success -> handleSuccess(result.value)
            is Error -> handleError(result.error)
            else -> {}
        }
        _viewStateEvent.publishEvent(_viewState)
    }

    private fun handlePendingState() {
        _viewState = _viewState.copy(isLoading = true, canBeAgreed = false)
    }

    private fun <T> handleSuccess(value: T) {
        when (value) {
            is ChangeCoordinates -> {
                with(value) {
                    _viewState = _viewState.copy(
                        date = date,
                        distance = distance,
                        outletPresentation = outletPresentation,
                        latitudeOld = latitudeOld,
                        longitudeOld = longitudeOld,
                        latitudeNew = latitudeNew,
                        longitudeNew = longitudeNew,
                        isLoading = false,
                        canBeAgreed = canBeAgreed
                    )
                }
            }
            is Unit -> _viewState = _viewState.copy(isLoading = false, canBeAgreed = false)
        }
    }

    private fun handleError(error: Throwable) {
        _viewState = _viewState.copy(isLoading = false, error = error)
    }

    private fun getChangeCoordinates() {
        safeLaunch {
            docEmixDetailRepository.getChangeCoordinates(extId).collect { result ->
                updateViewState(result)
            }
        }
    }

    /**
     * @param new need for render label NEW or OLD
     * @param up need for render popup when user clicked on point, popup will be shown at the top or bottom of the screen
     */
    private fun createPointAnnotationOptions(
        context: Context,
        icon: Int,
        latitude: Double,
        longitude: Double,
        new: Boolean,
        up: Boolean
    ): PointAnnotationOptions {
        val bitmap = context.bitmapFromDrawableRes(icon) ?: return PointAnnotationOptions()

        val options = PointAnnotationOptions()
            .withPoint(Point.fromLngLat(longitude, latitude))
            .withIconImage(bitmap)
            .withData(JsonObject().apply {
                addProperty(PA_DATA, PAOptionsData.jsonString(new, up, "$latitude, $longitude"))
            })
        return options
    }

    private fun setGeoAddressToViewState(new: Boolean, address: String) {
        _viewState =
            if (new)
                _viewState.copy(newGeoAddress = address)
            else
                _viewState.copy(oldGeoAddress = address)
    }

    fun setGeoAddress(
        geocoder: Geocoder,
        new: Boolean
    ) {

        val latitude = if (new) _viewState.latitudeNew else _viewState.latitudeOld
        val longitude = if (new) _viewState.longitudeNew else _viewState.longitudeOld

        safeLaunch {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    geocoder.getFromLocation(latitude, longitude, 1) {
                        if (it.isNotEmpty())
                            setGeoAddressToViewState(new, it[0].getAddressLine(0))
                    }
                } else {
                    geocoder.getFromLocation(latitude, longitude, 1)?.let {
                        if (it.isNotEmpty())
                            setGeoAddressToViewState(new, it[0].getAddressLine(0))
                    }
                }
            } catch (_: Exception) {
            }
        }
    }

    fun reload() {
        getChangeCoordinates()
    }

    fun getAddressFromViewState(new: Boolean) =
        if (new) _viewState.newGeoAddress else _viewState.oldGeoAddress

    fun createPointsAnnotationOptions(context: Context): List<PointAnnotationOptions> {
        val oldPointUp = _viewState.latitudeOld > _viewState.latitudeNew

        val oldOptions = createPointAnnotationOptions(
            context,
            R.drawable.ic_person_grey,
            _viewState.latitudeOld,
            _viewState.longitudeOld,
            new = false,
            up = oldPointUp
        )

        val newOptions = createPointAnnotationOptions(
            context,
            R.drawable.ic_person_blue,
            _viewState.latitudeNew,
            _viewState.longitudeNew,
            new = true,
            up = !oldPointUp
        )

        return listOf(oldOptions, newOptions)
    }

    fun acceptChangeCoordinates(approve: Boolean, reason: String) {
        safeLaunch {
            docEmixDetailRepository.acceptChangeCoordinates(extId, approve, reason)
                .collect { result ->
                    updateViewState(result)
                }
        }
    }

    fun calculateMidpoint(
        longitudeOld: Double, latitudeOld: Double, longitudeNew: Double, latitudeNew: Double
    ) = Pair((longitudeOld + longitudeNew) / 2, (latitudeOld + latitudeNew) / 2)

    fun calculateZoom(distance: Int) = when {
        distance <= 3 -> 22.0
        distance <= 6 -> 21.0
        distance <= 12 -> 20.0
        distance <= 30 -> 19.0
        distance <= 60 -> 18.0
        distance <= 120 -> 17.0
        distance <= 250 -> 16.0
        distance <= 550 -> 15.0
        distance <= 1100 -> 14.0
        distance <= 2000 -> 13.0
        distance <= 4000 -> 12.0
        distance <= 8000 -> 11.0
        distance <= 16000 -> 10.0
        distance <= 32000 -> 9.0
        distance <= 64000 -> 8.0
        distance <= 128000 -> 7.0
        distance <= 256000 -> 6.0
        distance <= 512000 -> 5.0
        distance <= 1024000 -> 4.0
        distance <= 2048000 -> 3.0
        distance <= 4096000 -> 2.0
        else -> 1.0
    }

    @AssistedFactory
    interface Factory {
        fun create(extId: String): ChangeOutletCoordinatesViewModel
    }

    companion object {
        const val PA_DATA = "PA_DATA"
    }
}

data class ViewState(
    val date: String = "",
    val distance: Int = 0,
    val outletPresentation: String = "",
    val latitudeOld: Double = 0.0,
    val longitudeOld: Double = 0.0,
    val latitudeNew: Double = 0.0,
    val longitudeNew: Double = 0.0,
    val isLoading: Boolean = false,
    val canBeAgreed: Boolean = false,
    val error: Throwable? = null,
    val oldGeoAddress: String = "",
    val newGeoAddress: String = "",
    val showPoints: Boolean = false
)

data class PAOptionsData(
    val new: Boolean = false,
    val up: Boolean = false,
    val coordinates: String
) {
    companion object {
        fun jsonString(
            new: Boolean,
            up: Boolean,
            coordinates: String
        ): String =
            Gson().toJson(PAOptionsData(new, up, coordinates))

        fun newInstance(jsonString: String): PAOptionsData =
            with(JsonParser.parseString(jsonString)) {
                PAOptionsData(
                    new = asJsonObject.get("new").asBoolean,
                    up = asJsonObject.get("up").asBoolean,
                    coordinates = asJsonObject.get("coordinates").asString
                )
            }
    }
}