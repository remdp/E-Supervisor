package com.euromix.esupervisor.app.model.rates

import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.rates.entities.RateData
import com.euromix.esupervisor.app.utils.async.LazyFlowSubject
import com.euromix.esupervisor.sources.salesRate.entities.RateRequestEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RatesRepository @Inject constructor(private val ratesSource: RatesSource) {

    private val rateLazyFlowSubject =
        LazyFlowSubject<RateRequestEntity, List<RateData>> {
            ratesSource.getRate(it)
        }

    fun getRate(rateRequest: RateRequestEntity): Flow<Result<List<RateData>>> {
        return rateLazyFlowSubject.listen(rateRequest)
    }

//    fun reloadRate(rateRequest: RateRequestEntity) {
//        return rateLazyFlowSubject.reloadArgument(rateRequest)
//    }
}