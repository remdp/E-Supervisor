package com.euromix.esupervisor.screens.main.tabs.rates

import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.euromix.esupervisor.App.Companion.beginCurrentMonth
import com.euromix.esupervisor.App.Companion.dateToJsonString
import com.euromix.esupervisor.App.Companion.endCurrentMonth
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.account.AccountRepository
import com.euromix.esupervisor.app.model.common.entities.ServerObject
import com.euromix.esupervisor.app.model.rates.RatesRepository
import com.euromix.esupervisor.app.model.rates.entities.RateData
import com.euromix.esupervisor.app.model.rates.entities.RateStructure
import com.euromix.esupervisor.app.screens.base.BaseViewModel
import com.euromix.esupervisor.app.utils.share
import com.euromix.esupervisor.sources.salesRate.entities.RateRequestEntity
import com.euromix.esupervisor.sources.salesRate.entities.RateSelectionItem
import com.squareup.moshi.Json
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.parcelize.Parcelize
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class RatesViewModel @Inject constructor(
    private val ratesRepository: RatesRepository, accountRepository: AccountRepository
) : BaseViewModel(accountRepository) {

    private var _rateId: String = ""
    private var _dimensions: List<String> = listOf()
    private var _detailLevel: Int = 0
    private val rateSettings: MutableList<RateSetting> = mutableListOf()

    private var _period: Pair<Date, Date> = Pair(beginCurrentMonth(), endCurrentMonth())
    val period
        get() = _period

    private val _rates = MutableLiveData<Result<List<RateStructure>>>()
    val rates = _rates.share()

    private val _rate = MutableLiveData<Result<RateData>>()
    val rate = _rate.share()

    init {
        getRates()
    }

    private fun requestForResult(): RateRequestEntity {

        return RateRequestEntity(
            rateId = _rateId,
            startDate = dateToJsonString(period.first),
            endDate = dateToJsonString(period.second),
            detailLevel = if (rateSettings.isEmpty()) _detailLevel else rateSettings.last().detailLevel,
            selection = rateSettings.map {
                RateSelectionItem(
                    it.serverObject.serverPair.id,
                    it.serverObject.serverType
                )
            }
        )
    }

    private fun getRate() {
        viewModelScope.safeLaunch {
            val request = requestForResult()
            ratesRepository.getRate(request).collect { result ->
                _rate.value = result
            }
        }
    }

    private fun getRates() {
        viewModelScope.safeLaunch {
            ratesRepository.getRates().collect { result ->
                _rates.value = result
            }
        }
    }

    fun updateRate(rateId: String, dimensions: List<String>) {
        _rateId = rateId
        _dimensions = dimensions
        getRate()
    }

    fun updateRate(period: Pair<Date, Date>?) {
        _period = period ?: Pair(beginCurrentMonth(), endCurrentMonth())
        getRate()
    }

    fun updateRate(detailLevel: Int) {
        _detailLevel = detailLevel
        getRate()
    }

    fun updateRate(dimension: String? = null, serverObject: ServerObject? = null) {
        if (serverObject == null) {
            rateSettings.removeLast()
        } else {
            rateSettings.add(RateSetting(serverObject, _dimensions.indexOf(dimension)))
        }
        getRate()
    }

    fun decipherDimensions(): Array<String> {
        val excludedDimensions = mutableSetOf(_dimensions[_detailLevel])

        rateSettings.forEach { excludedDimensions.add(_dimensions[it.detailLevel]) }

        return _dimensions.filter { it !in excludedDimensions }.toTypedArray()
    }

    fun selectionEmpty() = rateSettings.isEmpty()

    fun backStackPath(): String {

        var path = ""

        rateSettings.forEach {
            path = path + if (path.isBlank()) "" else {
                " / "
            } + it.serverObject.serverPair.presentation
        }

        return path
    }

    fun reloadRates() = getRates()
    fun reloadRate() = getRate()

    @Parcelize
    data class RateSetting(
        @field:Json(name = "server_object") val serverObject: ServerObject,
        @field:Json(name = "detail_level") val detailLevel: Int
    ) : Parcelable

}