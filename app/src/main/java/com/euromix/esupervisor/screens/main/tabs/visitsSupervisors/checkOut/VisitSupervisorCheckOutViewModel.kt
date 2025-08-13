package com.euromix.esupervisor.screens.main.tabs.visitsSupervisors.checkOut

import android.location.Location
import com.euromix.esupervisor.app.common.geoCoding.GeoDataRepository
import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.visitsSupervisors.VisitsSupervisorsRepository
import com.euromix.esupervisor.app.model.visitsSupervisors.entities.CheckOut
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.MutableLiveEvent
import com.euromix.esupervisor.app.utils.distanceBetweenPoints
import com.euromix.esupervisor.app.utils.publishEvent
import com.euromix.esupervisor.app.utils.share
import com.euromix.esupervisor.screens.main.BaseViewState
import com.euromix.esupervisor.sources.visitsSupervisors.entities.CheckOutRequestEntity
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class VisitSupervisorCheckOutViewModel @AssistedInject constructor(
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
            is CheckOut -> {
                _viewState = _viewState.copy(
                    isLoading = false,
                    error = null,
                    isCheckOut = value.isCheckOut,
                    outlet = value.outlet,
                    latitude = value.latitude,
                    longitude = value.longitude,
                    deviation = distanceBetweenPoints(
                        value.longitude,
                        value.latitude,
                        _viewState.currentLongitude,
                        _viewState.currentLatitude
                    ),
                    outletTime = value.outletTime
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

    fun checkOutPost() {
        safeLaunch {
            visitsSupervisorsRepository.checkOutPost(
                id,
                CheckOutRequestEntity(
                    latitude = _viewState.currentLatitude,
                    longitude = _viewState.currentLongitude
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

    fun checkOutGetAndGetGeoData(permissionCallback: suspend () -> Unit) {
        safeLaunch {
            visitsSupervisorsRepository.checkOutGet(id).collect {
                updateViewState(it)
            }
            permissionCallback()
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(id: String): VisitSupervisorCheckOutViewModel
    }

    data class ViewState(
        override val isLoading: Boolean = false,
        override val error: Throwable? = null,
        val isCheckOut: Boolean = false,
        val outlet: String = "",
        val latitude: Double = 0.0,
        val longitude: Double = 0.0,
        val currentLatitude: Double = 0.0,
        val currentLongitude: Double = 0.0,
        val address: String? = null,
        val deviation: Int = 0,
        val outletTime: Int = 0
    ) : BaseViewState()
}