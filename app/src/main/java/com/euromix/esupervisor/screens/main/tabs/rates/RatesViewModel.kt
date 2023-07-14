package com.euromix.esupervisor.screens.main.tabs.rates

import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.euromix.esupervisor.App.Companion.beginCurrentMonth
import com.euromix.esupervisor.App.Companion.endCurrentMonth
import com.euromix.esupervisor.App.Companion.formattedDate
import com.euromix.esupervisor.app.enums.Rate
import com.euromix.esupervisor.app.enums.Role
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.account.AccountRepository
import com.euromix.esupervisor.app.model.rates.RatesRepository
import com.euromix.esupervisor.app.model.rates.entities.*
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.share
import com.euromix.esupervisor.sources.salesRate.entities.RateRequestEntity
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.parcelize.Parcelize
import java.util.*
import javax.inject.Inject
import kotlin.math.max

@HiltViewModel
class RatesViewModel @Inject constructor(
    private val ratesRepository: RatesRepository,
    accountRepository: AccountRepository
) : BaseViewModel(accountRepository) {

    private val _state = MutableLiveData<ViewState>()
    val state = _state.share()

    private val _rate = MutableLiveData<Result<List<RateData>>>()
    val rate = _rate.share()

    private val minDetailLevel = Role.getMinDetailLevel(accountRepository.getCurrentRole())

    fun updateState(ratePeriod: Pair<Date, Date>) {
        _state.value = _state.value?.copy(ratePeriod = ratePeriod)
    }

    fun updateState(ratePosition: Int) {

        if (_state.value == null) {

            _state.value = ViewState(
                detailLevel = minDetailLevel,
                ratePeriod = Pair(beginCurrentMonth(), endCurrentMonth()),
                levelIds = mutableMapOf(Pair(1, null), Pair(2, null), Pair(3, null))
            )

        } else
            _state.value = _state.value?.copy(ratePosition = ratePosition, detailLevel = 0)
    }

    fun updateState(detailId: String) {

        _state.value?.let {

            val newLevel = it.detailLevel + 1
            val newState = it.copy(detailLevel = newLevel)
            newState.levelIds[newLevel] = detailId

            _state.value = newState
        }
    }

    fun updateState() {
        _state.value?.let {
            _state.value = it.copy(detailLevel = it.detailLevel - 1)
        }
    }

    fun currentDetailLevel(): Int = state.value?.detailLevel ?: 0

    fun possibleReduceDetailLevel() = currentDetailLevel() > minDetailLevel

    private fun getRate(rateRequest: RateRequestEntity) {
        viewModelScope.safeLaunch {
            ratesRepository.getRate(rateRequest)
                .collect { result ->
                    _rate.value = result
                }
        }
    }

    private fun reloadRate(rateRequest: RateRequestEntity) {
        ratesRepository.getRate(rateRequest)
    }

    fun fetchRate(reload: Boolean = false) {

        state.value?.let { state ->

            val completed = Rate.allRates()[state.ratePosition] == Rate.VisitsEfficiencyFact

            getRate(
                RateRequestEntity(
                    rate = Rate.stringRepresentation(state.ratePosition),
                    startDate = formattedDate(state.ratePeriod.first),
                    endDate = formattedDate(state.ratePeriod.second),
                    detailId = state.levelIds[state.detailLevel],
                    completed = completed,
                    detailLevel = state.detailLevel
                )
            )

//            if (reload) reloadRate(
//                RateRequestEntity(
//                    rate = Rate.stringRepresentation(state.ratePosition),
//                    tradingAgentId = state.tradingAgentId,
//                    startDate = formattedDate(state.ratePeriod.first),
//                    endDate = formattedDate(state.ratePeriod.second),
//                    completed = completed
//                )
//            ) else getRate(
//                RateRequestEntity(
//                    rate = Rate.stringRepresentation(state.ratePosition),
//                    startDate = formattedDate(state.ratePeriod.first),
//                    endDate = formattedDate(state.ratePeriod.second),
//                    tradingAgentId = state.tradingAgentId,
//                    completed = completed
//                )
//            )
        }
    }

    @Parcelize
    data class ViewState(
        val detailLevel: Int = 0,
        val ratePeriod: Pair<Date, Date>,
        val ratePosition: Int = 0,
        val levelIds: MutableMap<Int, String?>
    ) : Parcelable
}