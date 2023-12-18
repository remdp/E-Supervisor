package com.euromix.esupervisor.app.model.rates

import com.euromix.esupervisor.app.model.rates.entities.*
import com.euromix.esupervisor.sources.salesRate.entities.RateRequestEntity

interface RatesSource {
    suspend fun getRate(rateRequestEntity: RateRequestEntity): RateData
    suspend fun getRates(): List<RateStructure>
}