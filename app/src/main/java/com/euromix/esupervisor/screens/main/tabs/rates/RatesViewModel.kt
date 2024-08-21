package com.euromix.esupervisor.screens.main.tabs.rates

import com.euromix.esupervisor.App.Companion.beginCurrentMonth
import com.euromix.esupervisor.App.Companion.endCurrentMonth
import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.common.entities.ServerObject
import com.euromix.esupervisor.app.model.common.entities.ServerSelectionItem
import com.euromix.esupervisor.app.model.rates.RatesRepository
import com.euromix.esupervisor.app.model.rates.entities.RateData
import com.euromix.esupervisor.app.model.rates.entities.RateStructure
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.MutableLiveEvent
import com.euromix.esupervisor.app.utils.dateToJsonString
import com.euromix.esupervisor.app.utils.publishEvent
import com.euromix.esupervisor.app.utils.share
import com.euromix.esupervisor.screens.main.BaseViewState
import com.euromix.esupervisor.sources.salesRate.entities.RateRequestEntity
import com.squareup.moshi.Json
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class RatesViewModel @Inject constructor(
    private val ratesRepository: RatesRepository
) : BaseViewModel() {

    private var _viewState: ViewState =
        ViewState(period = Pair(beginCurrentMonth(), endCurrentMonth()))
    val viewState: ViewState
        get() = _viewState

    private val _viewStateEvent = MutableLiveEvent<Unit>()
    val viewStateEvent = _viewStateEvent.share()

    init {
        getRates()
    }

    private fun <T> updateViewState(result: Result<T>) {

        when (result) {
            is Pending -> handlePendingState()
            is Success -> {

                if (result.value is List<*>) {
                    handleSuccessRates(result.value as List<RateStructure>)
                } else if (result.value is RateData) {
                    handleSuccessRate(result.value)
                }
            }

            is Error -> handleError(result.error)
            else -> {}
        }
        _viewStateEvent.publishEvent()
    }

    private fun handlePendingState() {
        _viewState = _viewState.copy(isLoading = true, error = null)
    }

    private fun handleSuccessRates(value: List<RateStructure>) {
        _viewState = _viewState.copy(
            isLoading = false,
            error = null,
            rates = value,
            currentRate = if (value.isNotEmpty()) value[0] else null,
            currentDimensions = if (value.isNotEmpty()) getCurrentDimensions(value[0]) else listOf()
        )
    }

    private fun handleSuccessRate(value: RateData) {
        _viewState = _viewState.copy(
            isLoading = false,
            error = null,
            rateData = value
        )
    }

    private fun handleError(error: Throwable) {
        _viewState = _viewState.copy(isLoading = false, error = error)
    }

    private fun getRates() {
        safeLaunch {
            ratesRepository.getRates().collect { result -> updateViewState(result) }
        }
    }

    private fun getRate() {
        safeLaunch {
            requestForResult()?.let { request ->
                ratesRepository.getRate(request).collect { result -> updateViewState(result) }
            }
        }
    }

    private fun requestForResult(): RateRequestEntity? {

        return _viewState.currentRate?.let { rate ->
            RateRequestEntity(
                rateId = rate.rate.id,
                startDate = _viewState.period.first.dateToJsonString(),
                endDate = _viewState.period.second.dateToJsonString(),
                detailLevel = _viewState.rateSelection.lastOrNull()?.detailLevel
                    ?: _viewState.detailLevel,
                planType = _viewState.planType,
                selection = _viewState.rateSelection.map {
                    it.serverObject.let { serverObj ->
                        ServerSelectionItem(serverObj.serverPair.id, serverObj.serverType)
                    }
                }
            )
        }
    }

    private fun getCurrentDimensions(rate: RateStructure) =
        if (_viewState.planType == 1) rate.dayDimensions else rate.dimensions

    fun restoreViewState() {
        _viewStateEvent.publishEvent()
    }

    fun reloadRates() {
        getRates()
    }

    fun reloadRate() {
        getRate()
    }

    fun changeRate(rate: RateStructure) {
        _viewState = _viewState.copy(
            currentRate = rate,
            detailLevel = 0,
            currentDimensions = getCurrentDimensions(rate)
        )
        getRate()
    }

    fun changePeriod(period: Pair<Date, Date>) {
        _viewState = _viewState.copy(period = period)
        getRate()
    }

    fun changeDetailLevel(detailLevel: Int) {
        _viewState = _viewState.copy(detailLevel = detailLevel)
        getRate()
    }

    fun decipher(dimension: String? = null, serverObject: ServerObject? = null) {
        if (serverObject == null) {
            _viewState.rateSelection.removeLast()
        } else {
            _viewState.rateSelection.add(
                RateSelection(
                    serverObject,
                    _viewState.currentDimensions.indexOf(dimension)
                )
            )
        }
        getRate()
    }

    fun changePlanType(planType: Int) {

        val currentRate = _viewState.currentRate

        _viewState = _viewState.copy(
            planType = planType,
            detailLevel = 0,
            currentDimensions = if (currentRate != null) getCurrentDimensions(currentRate) else listOf()
        )
        getRate()
    }

    fun decipherDimensions() = with(_viewState) {
        if (currentDimensions.isNotEmpty()) {

            val excludedDimensions = mutableSetOf(currentDimensions[detailLevel])
            rateSelection.forEach { excludedDimensions.add(currentDimensions[it.detailLevel]) }
            currentDimensions.filter { it !in excludedDimensions }.toTypedArray()
        } else arrayOf()
    }

    fun backStackPath(): String {
        var path = ""

        _viewState.currentRate?.let { rateStructure ->
            path = rateStructure.rate.presentation

            _viewState.rateSelection.forEach { rateSetting ->
                path = path + if (path.isBlank()) "" else {
                    " / "
                } + rateSetting.serverObject.serverPair.presentation
            }
        }
        return path
    }

    data class ViewState(
        override val isLoading: Boolean = false,
        override val error: Throwable? = null,
        val period: Pair<Date, Date>,
        val rateData: RateData? = null,
        val rates: List<RateStructure>? = null,
        val currentRate: RateStructure? = null,
        var currentDimensions: List<String> = listOf(),
        val planType: Int = 0,
        val rateSelection: MutableList<RateSelection> = mutableListOf(),
        val detailLevel: Int = 0

    ) : BaseViewState()

    data class RateSelection(
        @field:Json(name = "server_object") val serverObject: ServerObject,
        @field:Json(name = "detail_level") val detailLevel: Int
    )
}