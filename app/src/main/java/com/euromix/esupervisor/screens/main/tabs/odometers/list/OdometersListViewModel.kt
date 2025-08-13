package com.euromix.esupervisor.screens.main.tabs.odometers.list

import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.odometers.OdometersRepository
import com.euromix.esupervisor.app.model.odometers.entities.OdometersReading
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.MutableLiveEvent
import com.euromix.esupervisor.app.utils.toJsonString
import com.euromix.esupervisor.app.utils.publishEvent
import com.euromix.esupervisor.app.utils.share
import com.euromix.esupervisor.screens.main.BaseViewState
import com.euromix.esupervisor.sources.odometers.entities.OdometersReadingRequestEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class OdometersListViewModel @Inject constructor(private val odometersRepository: OdometersRepository) :
    BaseViewModel() {

    private var _viewState: ViewState =
        ViewState(odometersReadingList = listOf())
    val viewState: ViewState
        get() = _viewState

    private val _viewStateEvent = MutableLiveEvent<Unit>()
    val viewStateEvent = _viewStateEvent.share()

    init {
        getOdometersReadingList()
    }

    private fun getOdometersReadingList() {

        safeLaunch {
            odometersRepository.getOdometersReadingList(requestFromSelection()).collect {
                updateViewState(it)
            }
        }
    }

    private fun <T> updateViewState(result: Result<T>) {

        when (result) {
            is Pending -> handlePendingState()
            is Success -> {
                if (result.value is List<*>)
                    handleSuccess(result.value as List<OdometersReading>)
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

    private fun handleSuccess(value: List<OdometersReading>) {
        _viewState = _viewState.copy(
            isLoading = false,
            error = null,
            odometersReadingList = value
        )
    }

    private fun requestFromSelection() = OdometersReadingRequestEntity(
        startDate = _viewState.period?.first?.toJsonString(),
        endDate = _viewState.period?.second?.toJsonString()
    )

    fun reload() {
        getOdometersReadingList()
    }

    fun changePeriod(period: Pair<Date, Date>?) {
        _viewState = _viewState.copy(period = period)
        getOdometersReadingList()
    }

    fun getListForSubmit(): List<OdometersReading> = _viewState.odometersReadingList

    data class ViewState(
        override val isLoading: Boolean = false,
        override val error: Throwable? = null,
        val period: Pair<Date, Date>? = null,
        val odometersReadingList: List<OdometersReading>
    ) : BaseViewState()
}