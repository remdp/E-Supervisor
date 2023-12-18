package com.euromix.esupervisor.app.model.rates

import com.euromix.esupervisor.app.utils.async.serverCallbackFlowFetcher
import com.euromix.esupervisor.sources.salesRate.entities.RateRequestEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RatesRepository @Inject constructor(private val ratesSource: RatesSource) {

    fun getRate(rateRequest: RateRequestEntity) = serverCallbackFlowFetcher {
        ratesSource.getRate(rateRequest)
    }

    fun getRates() = serverCallbackFlowFetcher { ratesSource.getRates() }
}