package com.euromix.esupervisor.app.common.geoCoding

import android.annotation.SuppressLint
import android.location.Location
import com.euromix.esupervisor.BuildConfig
import com.euromix.esupervisor.app.model.Empty
import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineRequest
import com.mapbox.android.core.location.LocationEngineResult
import com.mapbox.api.geocoding.v5.GeocodingCriteria
import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.geojson.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class MapboxLocationManager @Inject constructor(private val locationEngine: LocationEngine) : LocationManager {

    @SuppressLint("MissingPermission")
    override suspend fun getLocation(): Result<LocationEngineResult> = withContext(Dispatchers.IO) {
        try {
            val request = LocationEngineRequest.Builder(1000L)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(5000L)
                .build()

            val locationResult = suspendCancellableCoroutine { continuation ->
                locationEngine.requestLocationUpdates(
                    request,
                    object : LocationEngineCallback<LocationEngineResult> {
                        override fun onSuccess(locationResult: LocationEngineResult?) {
                            locationEngine.removeLocationUpdates(this)
                            continuation.resume(locationResult)
                        }

                        override fun onFailure(exception: Exception) {
                            locationEngine.removeLocationUpdates(this)
                            continuation.resumeWithException(exception)
                        }
                    },
                    null
                )
            }

            locationResult?.let { Success(it) } ?: Empty()
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun getAddressFromLocation(location: Location): String =
        withContext(Dispatchers.IO) {
            try {
                val geocodingClient = MapboxGeocoding.builder()
                    .accessToken(BuildConfig.MAPBOX_ACCESS_TOKEN)
                    .query(Point.fromLngLat(location.longitude, location.latitude))
                    .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
                    .build()

                val response = geocodingClient.executeCall()
                response.body()?.features()?.firstOrNull()?.placeName() ?: ""
            } catch (e: Exception) {
                e.localizedMessage ?: ""
            }
        }
}