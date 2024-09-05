package com.euromix.esupervisor.app.model.odometers

import com.euromix.esupervisor.app.utils.async.serverCallbackFlowFetcher
import com.euromix.esupervisor.sources.odometers.entities.OdometersReadingRequestEntity
import com.euromix.esupervisor.sources.odometers.entities.TodayOdometersReadingRequestEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OdometersRepository @Inject constructor(private val odometersSource: OdometersSource) {

    fun getTodayOdometersReading() =
        serverCallbackFlowFetcher { odometersSource.getTodayOdometersReading() }

    fun sendTodayOdometersReading(request: TodayOdometersReadingRequestEntity) =
        serverCallbackFlowFetcher { odometersSource.sendTodayOdometersReading(request) }

    fun getOdometersReadingList(request: OdometersReadingRequestEntity) =
        serverCallbackFlowFetcher { odometersSource.getOdometersReading(request) }
}