package com.euromix.esupervisor.screens.main.tabs.rates

import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.euromix.esupervisor.App.Companion.beginCurrentMonth
import com.euromix.esupervisor.App.Companion.endCurrentMonth
import com.euromix.esupervisor.App.Companion.formattedDate
import com.euromix.esupervisor.app.enums.Rate
import com.euromix.esupervisor.app.enums.RatesDetailing
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.account.AccountRepository
import com.euromix.esupervisor.app.model.common.entities.ServerPair
import com.euromix.esupervisor.app.model.rates.RatesRepository
import com.euromix.esupervisor.app.model.rates.entities.RateData
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.share
import com.euromix.esupervisor.sources.salesRate.entities.RateRequestEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.parcelize.Parcelize
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class RatesViewModel @Inject constructor(
    private val ratesRepository: RatesRepository, accountRepository: AccountRepository
) : BaseViewModel(accountRepository) {

    private val _state = MutableLiveData<ViewState>()
    val state = _state.share()

    private val _rate = MutableLiveData<Result<List<RateData>>>()
    val rate = _rate.share()

    fun updateState(ratePeriod: Pair<Date, Date>) {
        _state.value = _state.value?.copy(ratePeriod = ratePeriod)
    }

    fun updateState(rate: Rate) {

        if (_state.value == null) {
            _state.value = ViewState(ratePeriod = Pair(beginCurrentMonth(), endCurrentMonth()))
        } else _state.value = _state.value?.copy(rate = rate)

    }

    fun updateStateBackStack() {

        _state.value?.let {
            var newDetailBackStack = it.detailLevelsBackStack?.toMutableList()
            var newDetailLevel: RatesDetailing? = newDetailBackStack?.last()?.ratesDetail

            if (newDetailBackStack?.size == 1) newDetailBackStack = null
            else {
                newDetailBackStack?.removeLast()
            }

            _state.value = it.copy(
                detailLevel = newDetailLevel ?: RatesDetailing.BalanceUnit,
                detailLevelsBackStack = newDetailBackStack
            )
        }
    }

    fun updateState(
        rateDetail: RatesDetailing,
        selectionObject: ServerPair? = null
    ) {
        _state.value?.let { currentState ->

            val newDetailBackStack = if (selectionObject != null) {
                val newBackStackItem =
                    BackStackItem(currentState.detailLevel, selectionObject)

                if (currentState.detailLevelsBackStack == null) mutableListOf(newBackStackItem)
                else {
                    val newBackStack = currentState.detailLevelsBackStack.toMutableList()
                    newBackStack.add(newBackStackItem)
                    newBackStack
                }

            } else {
                null
            }

            _state.value = currentState.copy(
                detailLevel = rateDetail,
                detailLevelsBackStack = newDetailBackStack
            )
        }
    }

    private fun getRate(rateRequest: RateRequestEntity) {
        viewModelScope.safeLaunch {
            ratesRepository.getRate(rateRequest).collect { result ->
                _rate.value = result
            }
        }
    }

    fun fetchRate(reload: Boolean = false) {

        state.value?.let { state ->

            val completed = state.rate == Rate.VisitsEfficiencyFact

            getRate(
                RateRequestEntity(
                    rate = Rate.allRates().indexOf(state.rate),
                    detailLevel = RatesDetailing.allLevels().indexOf(state.detailLevel),
                    startDate = formattedDate(state.ratePeriod.first),
                    endDate = formattedDate(state.ratePeriod.second),
                    buId = state.detailLevelsBackStack?.find { it.ratesDetail == RatesDetailing.BalanceUnit }?.selectionObject?.id,
                    ttId = state.detailLevelsBackStack?.find { it.ratesDetail == RatesDetailing.TradingTeam }?.selectionObject?.id,
                    routeId = state.detailLevelsBackStack?.find { it.ratesDetail == RatesDetailing.Route }?.selectionObject?.id,
                    manufacturerId = state.detailLevelsBackStack?.find { it.ratesDetail == RatesDetailing.Manufacturer }?.selectionObject?.id,
                    completed = completed
                )
            )
        }
    }

    fun detailsList(): List<RatesDetailing> {

        val backStackDetails = state.value?.detailLevelsBackStack?.map { it.ratesDetail }
        val ratesDetailing = RatesDetailing.allLevels(state.value?.rate).toMutableList()
        ratesDetailing.removeIf { it == state.value?.detailLevel || backStackDetails?.contains(it) == true }
        return ratesDetailing
    }

    fun backStackPath(): String {

        var path = ""

        state.value?.detailLevelsBackStack?.forEach {
            path = path + if (path.isBlank()) "" else {
                " / "
            } + it.selectionObject.presentation
        }

        return path
    }

    @Parcelize
    data class ViewState(
        val rate: Rate = Rate.SalesPlanFact,
        val detailLevel: RatesDetailing = RatesDetailing.BalanceUnit,
        val detailLevelsBackStack: List<BackStackItem>? = null,
        val ratePeriod: Pair<Date, Date>
    ) : Parcelable

    @Parcelize
    data class BackStackItem(
        val ratesDetail: RatesDetailing, val selectionObject: ServerPair
    ) : Parcelable
}