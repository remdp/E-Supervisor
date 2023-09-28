package com.euromix.esupervisor.app.model.rates

import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Pending
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.app.model.common.entities.ServerPair
import com.euromix.esupervisor.app.model.rates.entities.RateData
import com.euromix.esupervisor.app.model.rates.entities.RateStructure
import com.euromix.esupervisor.sources.salesRate.entities.RateRequestEntity
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RatesRepository @Inject constructor(private val ratesSource: RatesSource) {
    fun getRate(rateRequest: RateRequestEntity): Flow<Result<RateData>> {

        return callbackFlow {
            try {
                trySend(Pending())
                val res = ratesSource.getRate(rateRequest)
                trySend(Success(res))
            } catch (e: Exception) {
                trySend(Error(e))
            }
            awaitClose { }
        }
    }
}