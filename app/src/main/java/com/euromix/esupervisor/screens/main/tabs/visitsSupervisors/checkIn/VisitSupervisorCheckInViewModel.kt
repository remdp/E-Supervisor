package com.euromix.esupervisor.screens.main.tabs.visitsSupervisors.checkIn

import android.location.Location
import com.euromix.esupervisor.app.common.geoCoding.GeoDataRepository
import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.visitsSupervisors.VisitsSupervisorsRepository
import com.euromix.esupervisor.app.model.visitsSupervisors.entities.CheckIn
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.MutableLiveEvent
import com.euromix.esupervisor.app.utils.distanceBetweenPoints
import com.euromix.esupervisor.app.utils.publishEvent
import com.euromix.esupervisor.app.utils.share
import com.euromix.esupervisor.screens.main.BaseViewState
import com.euromix.esupervisor.sources.visitsSupervisors.entities.CheckInRequestEntity
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class VisitSupervisorCheckInViewModel @AssistedInject constructor(
    @Assisted private val id: String,
    private val visitsSupervisorsRepository: VisitsSupervisorsRepository,
    private val geoDataRepository: GeoDataRepository
) : BaseViewModel() {

    private var _viewState = ViewState()
    val viewState: ViewState
        get() = _viewState

    private val _viewStateEvent = MutableLiveEvent<Unit>()
    val viewStateEvent = _viewStateEvent.share()

    private fun <T> updateViewState(result: Result<T>) {

        when (result) {
            is Pending -> handlePendingState()
            is Success -> handleSuccess(result.value)
            is Error -> handleError(result.error)
            else -> {}
        }
        _viewStateEvent.publishEvent()
    }

    private fun handlePendingState() {
        _viewState = _viewState.copy(isLoading = true, error = null)

    }

    private fun <T> handleSuccess(value: T) {

        when (value) {
            is CheckIn -> {
                _viewState = _viewState.copy(
                    isLoading = false,
                    error = null,
                    isCheckIn = value.isCheckIn,
                    outlet = value.outlet,
                    base64CheckInPhoto = value.checkInPhoto,
                    checkInByPhoto = value.checkInByPhoto,
                    checkInDeviation = value.checkInDeviation,
                    latitude = value.latitude,
                    longitude = value.longitude,
                    deviation = distanceBetweenPoints(
                        value.longitude,
                        value.latitude,
                        _viewState.currentLongitude,
                        _viewState.currentLatitude
                    )
                )
            }

            is Pair<*, *> -> {

                _viewState = _viewState.copy(
                    isLoading = false,
                    error = null,
                    address = ""
                )
                value.first?.let {
                    with(it as Location) {
                        _viewState = _viewState.copy(
                            currentLatitude = latitude,
                            currentLongitude = longitude,
                            deviation = distanceBetweenPoints(
                                _viewState.longitude,
                                _viewState.latitude,
                                longitude,
                                latitude
                            )
                        )
                    }
                }
                value.second?.let {
                    _viewState = _viewState.copy(
                        address = it as String
                    )
                }
            }

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

    fun checkInPost() {
        safeLaunch {
            visitsSupervisorsRepository.checkInPost(
                id,
                CheckInRequestEntity(
                    latitude = _viewState.currentLatitude,
                    longitude = _viewState.currentLongitude,
                    photoBase64 = _viewState.base64CheckInPhoto
                )
            ).collect {
                updateViewState(it)
            }
        }
    }

    fun getGeoData() {
        safeLaunch {
            updateViewState(Pending<Unit>())
            updateViewState(Success(geoDataRepository.getCoordinatesAndAddress()))
        }
    }

    fun checkInGetAndGetGeoData(permissionCallback: suspend () -> Unit) {
        safeLaunch {
            visitsSupervisorsRepository.checkInGet(id).collect {
                updateViewState(it)
            }
            permissionCallback()
        }
    }

    fun selectPicture(photoBase64String: String) {
        _viewState = _viewState.copy(base64CheckInPhoto = photoBase64String)
        _viewStateEvent.publishEvent()
    }

    @AssistedFactory
    interface Factory {
        fun create(id: String): VisitSupervisorCheckInViewModel
    }

    data class ViewState(
        override val isLoading: Boolean = false,
        override val error: Throwable? = null,
        val isCheckIn: Boolean = false,
        val outlet: String = "",
        val latitude: Double = 0.0,
        val longitude: Double = 0.0,
        val currentLatitude: Double = 0.0,
        val currentLongitude: Double = 0.0,
        val address: String? = null,
        val deviation: Int = 0,
        val base64CheckInPhoto: String = "",
        val checkInDeviation: Int = 0,
        val checkInByPhoto: Boolean = false,
    ) : BaseViewState()


}