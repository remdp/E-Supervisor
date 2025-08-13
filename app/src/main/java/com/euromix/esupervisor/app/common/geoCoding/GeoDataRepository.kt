package com.euromix.esupervisor.app.common.geoCoding

import android.location.Location
import com.euromix.esupervisor.app.model.Success
import com.mapbox.android.core.location.LocationEngineResult
import javax.inject.Inject

class GeoDataRepository @Inject constructor(
    private val locationManager: LocationManager
) {
    suspend fun getCoordinates(): com.euromix.esupervisor.app.model.Result<LocationEngineResult> {
        return locationManager.getLocation()
    }

    suspend fun getAddress(location: Location): String {
        return locationManager.getAddressFromLocation(location)
    }

    suspend fun getCoordinatesAndAddress(): Pair<Location?, String?> {
        return when (val locationResult = locationManager.getLocation()) {
            is Success -> {
                val location = locationResult.value.lastLocation
                val address = location?.let { locationManager.getAddressFromLocation(it) }
                location to address
            }
            else -> null to null
        }
    }
}