package com.euromix.esupervisor.di

import com.euromix.esupervisor.app.common.geoCoding.LocationManager
import com.euromix.esupervisor.app.common.geoCoding.MapboxLocationManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationManagerModule {
    @Binds
    abstract fun bindLocationManager(mapboxLocationManager: MapboxLocationManager): LocationManager
}
