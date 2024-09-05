package com.euromix.esupervisor.sources.odometers

import com.euromix.esupervisor.app.model.odometers.OdometersSource
import com.euromix.esupervisor.app.model.odometers.entities.OdometersReading
import com.euromix.esupervisor.app.model.odometers.entities.TodayOdometersReading
import com.euromix.esupervisor.sources.base.BaseRetrofitSource
import com.euromix.esupervisor.sources.base.RetrofitConfig
import com.euromix.esupervisor.sources.odometers.entities.OdometersReadingRequestEntity
import com.euromix.esupervisor.sources.odometers.entities.TodayOdometersReadingRequestEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitOdometersSource @Inject constructor(private val config: RetrofitConfig) :
    BaseRetrofitSource(config), OdometersSource {

    private val odometersApi = retrofit.create(OdometersApi::class.java)
    override suspend fun getTodayOdometersReading(): TodayOdometersReading {
        return wrapRetrofitException {
            odometersApi.getTodayOdometersReading().toOdometersReading()
        }
    }

    override suspend fun sendTodayOdometersReading(request: TodayOdometersReadingRequestEntity): TodayOdometersReading {
        return wrapRetrofitException {
            odometersApi.sendTodayOdometersReading(request).toOdometersReading()
        }
    }

    override suspend fun getOdometersReading(request: OdometersReadingRequestEntity): List<OdometersReading> {
        return wrapRetrofitException {
            val jsonAdapter = config.moshi.adapter(OdometersReadingRequestEntity::class.java)
            odometersApi.getOdometersReadingList(jsonAdapter.toJson(request)).map {
                it.toOdometersReading()
            }
        }
    }
}