package com.euromix.esupervisor.screens.main.tabs.rates

import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.euromix.esupervisor.App.Companion.beginCurrentMonth
import com.euromix.esupervisor.App.Companion.endCurrentMonth
import com.euromix.esupervisor.App.Companion.formattedDate
import com.euromix.esupervisor.app.enums.Rate
import com.euromix.esupervisor.app.model.Result
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

@HiltViewModel
class RatesViewModel @Inject constructor(
    private val ratesRepository: RatesRepository
) : BaseViewModel() {

    private val _state = MutableLiveData<ViewState>()
    val state = _state.share()

    private val _rate = MutableLiveData<Result<List<RateData>>>()
    val rate = _rate.share()

    fun updateState(
        argsState: ViewState?,
        ratePeriod: Pair<Date, Date>? = null,
        currentRate: Int? = null,
        initialState: Boolean = false
    ) {

        _state.value = if (initialState) {

            if (argsState?.tradingAgentId == null)
                ViewState(Pair(beginCurrentMonth(), endCurrentMonth()))
            else ViewState(
                ratePeriod = argsState.ratePeriod,
                ratePosition = currentRate!!,
                tradingAgentId = argsState.tradingAgentId
            )

        } else {
            ViewState(
                ratePeriod = ratePeriod ?: _state.value!!.ratePeriod,
                ratePosition = currentRate ?: _state.value!!.ratePosition,
                tradingAgentId = argsState?.tradingAgentId ?: _state.value?.tradingAgentId
            )
        }
    }

    private fun getRate(rateRequest: RateRequestEntity) {
        viewModelScope.safeLaunch {
            ratesRepository.getRate(rateRequest)
                .collect { result -> _rate.value = result }
        }
    }

    private fun reloadRate(rateRequest: RateRequestEntity) {
        ratesRepository.reloadRate(rateRequest)
    }

    fun fetchRate(reload: Boolean = false) {

        state.value?.let { state ->

            val completed = Rate.allRates()[state.ratePosition] == Rate.VisitsEfficiencyFact

            if (reload) reloadRate(
                RateRequestEntity(
                    rate = Rate.stringRepresentation(state.ratePosition),
                    tradingAgentId = state.tradingAgentId,
                    startDate = formattedDate(state.ratePeriod.first),
                    endDate = formattedDate(state.ratePeriod.second),
                    completed = completed
                )
            ) else getRate(
                RateRequestEntity(
                    rate = Rate.stringRepresentation(state.ratePosition),
                    startDate = formattedDate(state.ratePeriod.first),
                    endDate = formattedDate(state.ratePeriod.second),
                    tradingAgentId = state.tradingAgentId,
                    completed = completed
                )
            )
        }
    }

    @Parcelize
    data class ViewState(
        var ratePeriod: Pair<Date, Date>,
        var ratePosition: Int = 0,
        val tradingAgentId: String? = null
    ) : Parcelable

}