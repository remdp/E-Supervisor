package com.euromix.esupervisor.di.geocoding

import android.content.Context
import android.location.Geocoder
import com.mapbox.android.core.location.LocationEngineProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.Locale
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LocationModule {

    @Provides
    @Singleton
    fun provideLocationEngine(@ApplicationContext context: Context) = LocationEngineProvider.getBestLocationEngine(context)

    @Provides
    @Singleton
    fun provideGeocoder(@ApplicationContext context: Context) = Geocoder(context, Locale.getDefault())
}
