package com.euromix.esupervisor.screens.main.tabs.odometers.list

import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.odometers.OdometersRepository
import com.euromix.esupervisor.app.model.odometers.entities.OdometersReading
import com.euromix.esupervisor.app.model.odometers.entities.OdometersReadingItem
import com.euromix.esupervisor.app.screens.Scrollable
import com.euromix.esupervisor.app.screens.ScrollableDelegate
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
    BaseViewModel(), Scrollable by ScrollableDelegate() {

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
                if (result.value is OdometersReading)
                    handleSuccess(result.value as OdometersReading)
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

    private fun handleSuccess(value: OdometersReading) {
        _viewState = _viewState.copy(
            isLoading = false,
            error = null,
            todayReadings = value.todayReadings,
            odometersReadingList = value.readings
        )
        triggerScrollToTop()
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

    fun getListForSubmit(): List<OdometersReadingItem> = _viewState.odometersReadingList

    data class ViewState(
        override val isLoading: Boolean = false,
        override val error: Throwable? = null,
        val period: Pair<Date, Date>? = null,
        val todayReadings: Int = 0,
        val odometersReadingList: List<OdometersReadingItem>
    ) : BaseViewState()
}