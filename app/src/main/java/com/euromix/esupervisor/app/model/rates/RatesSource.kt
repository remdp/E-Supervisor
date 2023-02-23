package com.euromix.esupervisor.app.model.rates

import com.euromix.esupervisor.app.model.rates.entities.*
import com.euromix.esupervisor.sources.salesRate.entities.RateRequestEntity
import java.util.*

interface RatesSource {
    suspend fun getRate(rateRequestEntity: RateRequestEntity): List<RateData>
}