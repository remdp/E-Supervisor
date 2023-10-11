package com.euromix.esupervisor.sources.salesRate

import com.euromix.esupervisor.app.model.rates.RatesSource
import com.euromix.esupervisor.sources.base.BaseRetrofitSource
import com.euromix.esupervisor.sources.base.RetrofitConfig
import com.euromix.esupervisor.app.model.*
import com.euromix.esupervisor.app.model.rates.entities.RateData
import com.euromix.esupervisor.app.model.rates.entities.RateStructure
import com.euromix.esupervisor.sources.salesRate.entities.*
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import retrofit2.HttpException
import retrofit2.create
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitRatesSource @Inject constructor(config: RetrofitConfig) : RatesSource {

    private val moshi = config.moshi
    private val errorAdapter = moshi.adapter(BaseRetrofitSource.ErrorResponseBody::class.java)
    private val rateApi = config.retrofit.create<RateApi>()

    override suspend fun getRate(rateRequestEntity: RateRequestEntity): RateData =
        wrapRetrofitException {
            rateApi.getRate(rateRequestEntity).toRateData()
        }

    override suspend fun getRates(): List<RateStructure> =
        wrapRetrofitException {
            rateApi.getRates().map { it.toRateStructure() }
        }

    private suspend fun <T> wrapRetrofitException(block: suspend () -> T): T {

        return try {
            block()
        }
        //moshi
        catch (e: JsonDataException) {
            throw ParseBackendResponseException(e)
        } catch (e: JsonEncodingException) {
            throw ParseBackendResponseException(e)
        }
        //retrofit
        catch (e: HttpException) {

            if (e.code() == 403) throw AuthException(e)

            throw createBackendException(e)
        }
        //mostly retrofit
        catch (e: IOException) {
            throw ConnectionException(e)
        } catch (e: AppException) {
            throw e
        }
    }

    private fun createBackendException(e: HttpException): Exception {

        val jsonFromServer = e.response()?.errorBody()?.string()

        val stringError = try {
            errorAdapter.fromJson(jsonFromServer)?.error
        } catch (e: Exception) {
            jsonFromServer
        }

        return BackendException(e.code(), e, stringError ?: e.message)
    }
}