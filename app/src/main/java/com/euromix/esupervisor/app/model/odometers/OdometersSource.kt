package com.euromix.esupervisor.app.model.odometers

import com.euromix.esupervisor.app.model.odometers.entities.OdometersReading
import com.euromix.esupervisor.app.model.odometers.entities.TodayOdometersReading
import com.euromix.esupervisor.sources.odometers.entities.OdometersReadingRequestEntity
import com.euromix.esupervisor.sources.odometers.entities.TodayOdometersReadingRequestEntity

interface OdometersSource {
    suspend fun getTodayOdometersReading(): TodayOdometersReading

    suspend fun sendTodayOdometersReading(request: TodayOdometersReadingRequestEntity): TodayOdometersReading

    suspend fun getOdometersReading(request: OdometersReadingRequestEntity): OdometersReading

}