package com.euromix.esupervisor.screens.main.tabs.rates

import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.euromix.esupervisor.App.Companion.dateToJsonString
import com.euromix.esupervisor.app.enums.Rate
import com.euromix.esupervisor.app.enums.RatesDetailing
import com.euromix.esupervisor.app.model.Empty
import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.account.AccountRepository
import com.euromix.esupervisor.app.model.common.entities.ServerPair
import com.euromix.esupervisor.app.model.rates.RatesRepository
import com.euromix.esupervisor.app.model.rates.entities.RateData
import com.euromix.esupervisor.app.screens.base.BaseFragment
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.designByResult
import com.euromix.esupervisor.app.utils.share
import com.euromix.esupervisor.databinding.RatesFragmentBinding
import com.euromix.esupervisor.sources.salesRate.entities.RateRequestEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.parcelize.Parcelize
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class RatesViewModel @Inject constructor(
    private val ratesRepository: RatesRepository, accountRepository: AccountRepository
) : BaseViewModel(accountRepository) {

    private val _viewState = MutableLiveData(ViewState(needLoading = true))
    val viewState = _viewState.share()

    fun reload() {
        getRate()
    }

    fun updatePeriod(period: Pair<Date, Date>?) {
        _viewState.value = _viewState.value?.copy(period = period, needLoading = true)
    }

    fun updateRate(rate: Rate) {
        if (_viewState.value?.result !is Pending) _viewState.value =
            _viewState.value?.copy(rate = rate, needLoading = true)
    }

    fun updateDetailLevel(
        rateDetail: RatesDetailing, selectionObject: ServerPair? = null
    ) {
        _viewState.value?.let { currentState ->

            val newDetailBackStack = if (selectionObject != null) {
                val newBackStackItem = BackStackItem(currentState.detailLevel, selectionObject)

                if (currentState.detailLevelsBackStack == null) mutableListOf(newBackStackItem)
                else {
                    val newBackStack = currentState.detailLevelsBackStack.toMutableList()
                    newBackStack.add(newBackStackItem)
                    newBackStack
                }
            } else {
                null
            }

            _viewState.value = currentState.copy(
                detailLevel = rateDetail,
                detailLevelsBackStack = newDetailBackStack,
                needLoading = true
            )
        }
    }

    fun updateStateBackStack() {

        _viewState.value?.let {
            var newDetailBackStack = it.detailLevelsBackStack?.toMutableList()
            val newDetailLevel: RatesDetailing? = newDetailBackStack?.last()?.ratesDetail

            if (newDetailBackStack?.size == 1) newDetailBackStack = null
            else {
                newDetailBackStack?.removeLast()
            }

            _viewState.value = it.copy(
                detailLevel = newDetailLevel ?: RatesDetailing.BalanceUnit,
                detailLevelsBackStack = newDetailBackStack,
                needLoading = true
            )
        }
    }

    fun afterUpdateState(
        adapter: RateAdapter, binding: RatesFragmentBinding, fragment: BaseFragment
    ) {

        viewState.value?.let { stateValue ->

            if (stateValue.needLoading) {
                getRate()
            } else {
                if (stateValue.result is Success) adapter.rates = stateValue.result.value.rows
            }

            fragment.designByResult(
                stateValue.result, binding.root, binding.vResult, binding.srl
            )

        }
    }

    private fun getRate() {
        viewModelScope.safeLaunch {
            val request = requestForResult()
            ratesRepository.getRate(request).collect { result ->
                updateResult(result)
            }
        }
    }

    private fun updateResult(result: Result<*>) {

        viewState.value?.let { stateValue ->
            when (result) {
                is Pending -> _viewState.value =
                    stateValue.copy(needLoading = false, result = Pending())

                is Success -> _viewState.value =
                    stateValue.copy(result = Success(result.value as RateData))

                is Error -> _viewState.value = stateValue.copy(
                    needLoading = false, result = Error(result.error)
                )

                else -> _viewState.value = stateValue.copy(needLoading = false)
            }
        }
    }

    private fun requestForResult(): RateRequestEntity {
        val state = viewState.value

        return RateRequestEntity(
            rate = Rate.allRates().indexOf(state?.rate),
            detailLevel = RatesDetailing.allLevels().indexOf(state?.detailLevel),
            startDate = state?.period?.let { dateToJsonString(it.first) },
            endDate = state?.period?.let { dateToJsonString(it.second) },
            buId = state?.detailLevelsBackStack?.find { it.ratesDetail == RatesDetailing.BalanceUnit }?.selectionObject?.id,
            ttId = state?.detailLevelsBackStack?.find { it.ratesDetail == RatesDetailing.TradingTeam }?.selectionObject?.id,
            routeId = state?.detailLevelsBackStack?.find { it.ratesDetail == RatesDetailing.Route }?.selectionObject?.id,
            manufacturerId = state?.detailLevelsBackStack?.find { it.ratesDetail == RatesDetailing.Manufacturer }?.selectionObject?.id,
            completed = state?.rate == Rate.VisitsEfficiencyFact
        )
    }

    fun detailsList(): List<RatesDetailing> {

        val backStackDetails = viewState.value?.detailLevelsBackStack?.map { it.ratesDetail }
        val ratesDetailing = RatesDetailing.allLevels(viewState.value?.rate).toMutableList()
        ratesDetailing.removeIf {
            it == viewState.value?.detailLevel || backStackDetails?.contains(it) == true
        }
        return ratesDetailing
    }

    fun backStackPath(): String {

        var path = ""

        viewState.value?.detailLevelsBackStack?.forEach {
            path = path + if (path.isBlank()) "" else {
                " / "
            } + it.selectionObject.presentation
        }

        return path
    }

    data class ViewState(
        val period: Pair<Date, Date>? = null,
        val rate: Rate = Rate.SalesPlanFact,
        val detailLevel: RatesDetailing = RatesDetailing.BalanceUnit,
        val detailLevelsBackStack: List<BackStackItem>? = null,
        val needLoading: Boolean = false,
        val result: Result<RateData> = Empty()
    )

    @Parcelize
    data class BackStackItem(
        val ratesDetail: RatesDetailing, val selectionObject: ServerPair
    ) : Parcelable
}