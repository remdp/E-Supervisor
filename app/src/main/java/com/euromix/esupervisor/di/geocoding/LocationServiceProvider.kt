package com.euromix.esupervisor.di.geocoding

import android.content.Context
import com.euromix.esupervisor.app.common.geoCoding.LocationManager
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationServiceProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    private val googleApiAvailability: GoogleApiAvailability,
    @GoogleLocationManagerQualifier private val googleLocationManager: LocationManager,
    @MapboxLocationManagerQualifier private val mapboxLocationManager: LocationManager
) {

    fun get() = if (isGooglePlayServicesAvailable())
        googleLocationManager
    else
        mapboxLocationManager

    private fun isGooglePlayServicesAvailable() =
        googleApiAvailability.isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
}