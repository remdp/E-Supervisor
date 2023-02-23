package com.euromix.esupervisor.app.model.rates

import com.euromix.esupervisor.App
import com.euromix.esupervisor.App.Companion.dateFromString
import com.euromix.esupervisor.App.Companion.formattedDate
import com.euromix.esupervisor.app.enums.Rate
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.rates.entities.*
import com.euromix.esupervisor.app.utils.async.LazyFlowSubject
import com.euromix.esupervisor.sources.salesRate.entities.*
import kotlinx.coroutines.flow.Flow
import java.util.*
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

    fun reloadRate(rateRequest: RateRequestEntity) {
        return rateLazyFlowSubject.reloadArgument(rateRequest)
    }
}