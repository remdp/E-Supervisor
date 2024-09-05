package com.euromix.esupervisor.screens.main.tabs.odometers.reading

import android.net.Uri
import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.odometers.OdometersRepository
import com.euromix.esupervisor.app.model.odometers.entities.TodayOdometersReading
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.MutableLiveEvent
import com.euromix.esupervisor.app.utils.publishEvent
import com.euromix.esupervisor.app.utils.share
import com.euromix.esupervisor.screens.main.BaseViewState
import com.euromix.esupervisor.sources.odometers.entities.TodayOdometersReadingRequestEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class DialogOdometersViewModel @Inject constructor(private val odometersRepository: OdometersRepository) :
    BaseViewModel() {

    private var _viewState: ViewState = ViewState()
    val viewState: ViewState
        get() = _viewState

    private val _viewStateEvent = MutableLiveEvent<Unit>()
    val viewStateEvent = _viewStateEvent.share()

    init {
        getTodayOdometersReading()
    }

    private fun <T> updateViewState(result: Result<T>) {

        when (result) {
            is Pending -> handlePendingState()
            is Success -> {
                if (result.value is TodayOdometersReading)
                    handleSuccess(result.value)
            }

            is Error -> handleError(result.error)
            else -> {}
        }
        _viewStateEvent.publishEvent()
    }

    private fun handlePendingState() {
        _viewState = _viewState.copy(isLoading = true, error = null)
    }

    private fun handleError(error: Throwable) {
        _viewState = _viewState.copy(isLoading = false, error = error)
    }

    private fun handleSuccess(value: TodayOdometersReading) {
        _viewState = _viewState.copy(
            isLoading = false,
            error = null,
            startKm = value.startKm,
            startUri = null,
            startTime = value.startTime,
            startPhoto = value.startPhoto,
            stopKm = value.stopKm,
            stopUri = null,
            stopTime = value.stopTime,
            stopPhoto = value.stopPhoto,
            isStart = value.startKm != null,
            isStop = value.stopKm != null
        )
    }

    private fun getTodayOdometersReading() {

        safeLaunch {
            odometersRepository.getTodayOdometersReading().collect {
                updateViewState(it)
            }
        }
    }

    fun reload() {
        getTodayOdometersReading()
    }

    fun selectPicture(uri: Uri) {
        _viewState =
            if (_viewState.startUri == null && _viewState.startPhoto == null)
                _viewState.copy(startUri = uri)
            else
                _viewState.copy(stopUri = uri)

        _viewStateEvent.publishEvent()
    }

    fun setStartKm(km: Int?) {
        _viewState = _viewState.copy(startKm = km)
        _viewStateEvent.publishEvent()
    }

    fun setStopKm(km: Int?) {
        _viewState = _viewState.copy(stopKm = km)
        _viewStateEvent.publishEvent()
    }

    fun sendTodayOdometersReading(pictureBase64: String) {

        safeLaunch {
            odometersRepository.sendTodayOdometersReading(
                TodayOdometersReadingRequestEntity(
                    start = !_viewState.isStart,
                    km = if (!_viewState.isStart) {
                        _viewState.startKm ?: 0
                    } else if (!_viewState.isStop) {
                        _viewState.stopKm ?: 0
                    } else {
                        0
                    },
                    photo = pictureBase64
                )
            )
                .collect {
                    updateViewState(it)
                }
        }
    }

    data class ViewState(
        override val isLoading: Boolean = false,
        override val error: Throwable? = null,
        val startKm: Int? = null,
        val startUri: Uri? = null,
        val startTime: LocalDateTime? = null,
        val startPhoto: String? = null,
        val stopKm: Int? = null,
        val stopUri: Uri? = null,
        val stopTime: LocalDateTime? = null,
        val stopPhoto: String? = null,
        val isStart: Boolean = false,
        val isStop: Boolean = false
    ) : BaseViewState()
}