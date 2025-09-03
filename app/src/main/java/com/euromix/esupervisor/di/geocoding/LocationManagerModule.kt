package com.euromix.esupervisor.di.geocoding

import android.content.Context
import com.euromix.esupervisor.app.common.geoCoding.LocationManager
import com.euromix.esupervisor.app.common.geoCoding.GoogleLocationManager
import com.euromix.esupervisor.app.common.geoCoding.MapboxLocationManager
import com.google.android.gms.common.GoogleApiAvailability
import com.mapbox.android.core.location.LocationEngine
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationManagerModule {

    @Provides
    @Singleton
    @MapboxLocationManagerQualifier
    fun provideMapBoxLocationManager(locationEngine: LocationEngine): LocationManager {
        return MapboxLocationManager(locationEngine)
    }

    @Provides
    @Singleton
    @GoogleLocationManagerQualifier
    fun provideGoogleLocationManager(@ApplicationContext context: Context): LocationManager {
        return GoogleLocationManager(context)
    }

    @Provides
    @Singleton
    fun provideGoogleApiAvailability(): GoogleApiAvailability {
        return GoogleApiAvailability.getInstance()
    }
}
