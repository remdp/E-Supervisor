package com.euromix.esupervisor.screens.main.tabs.visitsSupervisors.checkIn

import android.Manifest
import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.visitsSupervisors.VisitsSupervisorsRepository
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.MutableLiveEvent
import com.euromix.esupervisor.app.utils.NewPermissionManager
import com.euromix.esupervisor.app.utils.publishEvent
import com.euromix.esupervisor.app.utils.share
import com.euromix.esupervisor.screens.main.BaseViewState
import com.euromix.esupervisor.sources.visitsSupervisors.entities.CheckInRequestEntity
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineRequest
import com.mapbox.android.core.location.LocationEngineResult
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Inject


//todo return the implementation of the LocationEngineCallback interface to the fragment
class VisitSupervisorCheckInViewModel @AssistedInject constructor(
    @Assisted private val id: String,
    private val locationEngine: LocationEngine,
    private val visitsSupervisorsRepository: VisitsSupervisorsRepository
) : BaseViewModel(), LocationEngineCallback<LocationEngineResult> {

    private var _viewState = ViewState()
    val viewState: ViewState
        get() = _viewState

    private val _viewStateEvent = MutableLiveEvent<ViewState>()
    val viewStateEvent = _viewStateEvent.share()

    @Inject
    lateinit var geocoder: Geocoder

    private fun <T> updateViewState(result: Result<T>) {

        when (result) {
            is Pending -> handlePendingState()
            is Success -> handleSuccess(result.value)
            is Error -> handleError(result.error)
            else -> {}
        }
        _viewStateEvent.publishEvent(_viewState)
    }

    private fun handlePendingState() {
        _viewState = _viewState.copy(isLoading = true, error = null)

    }

    private fun <T> handleSuccess(value: T) {

        when (value) {
            is LocationEngineResult -> {
                value.lastLocation?.let { location ->
                    _viewState = _viewState.copy(
                        isLoading = false,
                        error = null,
                        latitude = location.latitude,
                        longitude = location.longitude
                    )
                }
            }

            is String -> _viewState = _viewState.copy(
                isLoading = false,
                error = null,
                address = value

            )

            else ->
                _viewState = _viewState.copy(
                    isLoading = false,
                    error = null
                )
        }
    }

    private fun handleError(error: Throwable) {
        _viewState = _viewState.copy(isLoading = false, error = error)
    }

    fun requestForGetLocation(
        permissionManager: NewPermissionManager
        //locationEngine: LocationEngine
    ) {
        permissionManager.requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) { getLocation() }
    }

    @SuppressLint("MissingPermission")
    fun getLocation() {

        updateViewState(Pending<LocationEngineResult>())
        val request = LocationEngineRequest.Builder(1000L)
            .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
            .setMaxWaitTime(5000L).build()

        locationEngine.requestLocationUpdates(request, this, null)
        locationEngine.getLastLocation(this)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onSuccess(result: LocationEngineResult?) {
        updateViewState(Success(result))
        locationEngine.removeLocationUpdates(this)

        result?.lastLocation?.let { getAddressFromLocation(it) }
    }

    override fun onFailure(exception: java.lang.Exception) {
        updateViewState(Error<LocationEngineResult>(exception))
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun getAddressFromLocation(location: Location) {

        updateViewState(Pending<String>())
        geocoder.getFromLocation(location.latitude, location.longitude, 1, object :
            Geocoder.GeocodeListener {
            override fun onGeocode(addresses: List<Address>) {
                if (addresses.isNotEmpty()) {
                    val address = addresses[0]
                    val fullAddress = address.getAddressLine(0)
                    updateViewState(Success(fullAddress))
                }
            }

            override fun onError(errorMessage: String?) {
                updateViewState(Error<String>(Error(errorMessage)))
            }
        })

    }

    fun checkIn() {
        safeLaunch {
            visitsSupervisorsRepository.checkIn(
                id,
                CheckInRequestEntity(
                    latitude = _viewState.latitude,
                    longitude = _viewState.longitude
                )
            ).collect {
                updateViewState(it)
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(id: String): VisitSupervisorCheckInViewModel
    }

    data class ViewState(
        override val isLoading: Boolean = false,
        override val error: Throwable? = null,
        val latitude: Double = 0.0,
        val longitude: Double = 0.0,
        val address: String = ""
    ) : BaseViewState()


}