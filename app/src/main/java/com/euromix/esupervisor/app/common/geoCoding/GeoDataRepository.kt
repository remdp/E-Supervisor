package com.euromix.esupervisor.app.common.geoCoding

import android.location.Location
import com.euromix.esupervisor.app.model.Success
import com.euromix.esupervisor.di.geocoding.LocationServiceProvider
import javax.inject.Inject

class GeoDataRepository @Inject constructor(
    private val serviceProvider: LocationServiceProvider
) {

    private val locationManager: LocationManager by lazy { serviceProvider.get() }

    suspend fun getCoordinates(): com.euromix.esupervisor.app.model.Result<Location> {
        return locationManager.getLocation()
    }

    suspend fun getAddress(location: Location): String {
        return locationManager.getAddressFromLocation(location)
    }

    suspend fun getCoordinatesAndAddress(): Pair<Location?, String?> {
        return when (val locationResult = locationManager.getLocation()) {
            is Success -> {
                val location = locationResult.value
                val address = location.let { locationManager.getAddressFromLocation(it) }
                location to address
            }
            else -> null to null
        }
    }
}