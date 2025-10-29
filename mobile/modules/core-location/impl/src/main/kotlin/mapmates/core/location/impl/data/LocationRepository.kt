package mapmates.core.location.impl.data

import android.Manifest
import android.location.LocationManager
import androidx.annotation.RequiresPermission
import androidx.core.location.LocationManagerCompat
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import mapmates.core.location.api.LocationError
import mapmates.core.location.api.LocationResult
import javax.inject.Inject

internal class LocationRepository @Inject constructor(
    private val locationClient: FusedLocationProviderClient,
    private val locationManager: LocationManager
) {

    private val locationRequest = CurrentLocationRequest.Builder()
        .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
        .build()

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    suspend fun getCurrentLocation(): LocationResult = withContext(Dispatchers.IO) {
        if (!isGpsEnabled()) {
            return@withContext LocationResult.Failure(LocationError.GpsDisabled)
        }

        try {
            val location = locationClient.getCurrentLocation(locationRequest, null).await()
                ?: return@withContext LocationResult.Failure(LocationError.Unknown)


            LocationResult.Success(location.latitude, location.longitude)
        } catch (_: SecurityException) {
            LocationResult.Failure(LocationError.LocationPermissionDisabled)
        }
    }

    private fun isGpsEnabled() = LocationManagerCompat.isLocationEnabled(locationManager)


}