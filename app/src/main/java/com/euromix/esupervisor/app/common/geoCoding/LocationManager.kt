package com.euromix.esupervisor.app.common.geoCoding

import android.location.Location
import com.euromix.esupervisor.app.model.Result
import com.mapbox.android.core.location.LocationEngineResult

interface LocationManager {
    suspend fun getLocation(): Result<LocationEngineResult>
    suspend fun getAddressFromLocation(location: Location): String
}