package com.euromix.esupervisor.app.common.geoCoding

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import com.euromix.esupervisor.R
import com.euromix.esupervisor.app.model.Error
import com.euromix.esupervisor.app.model.Result
import com.euromix.esupervisor.app.model.Success
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class GoogleLocationManager @Inject constructor(@ApplicationContext private val context: Context): LocationManager {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override suspend fun getLocation(): Result<Location>  = withContext(Dispatchers.IO){
        try {
            val location = fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).await()
            Success(location)
        }catch (e: Exception){
            Error(e)
        }
    }

    override suspend fun getAddressFromLocation(location: Location): String = withContext(Dispatchers.IO){
        if (!Geocoder.isPresent()) {
            return@withContext "Geocoder not available"
        }
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val addresses = suspendCancellableCoroutine<List<Address>> { continuation ->
                    geocoder.getFromLocation(location.latitude, location.longitude, 1) { addresses ->
                        continuation.resume(addresses)
                    }
                }
                addresses.firstOrNull()?.toReadableString() ?: context.getString(R.string.address_not_found)
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                addresses?.firstOrNull()?.toReadableString() ?: context.getString(R.string.address_not_found)
            }
        } catch (e: Exception) {
            e.localizedMessage ?: context.getString(R.string.error_getting_address)
        }
    }

    private fun Address.toReadableString(): String {
        return (0..maxAddressLineIndex).joinToString(separator = "\n") { getAddressLine(it) }
    }
}