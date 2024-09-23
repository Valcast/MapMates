package com.example.socialmeetingapp.data.repository

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.example.socialmeetingapp.domain.model.LocationResult
import com.example.socialmeetingapp.domain.repository.LocationRepository
import com.example.socialmeetingapp.presentation.PermissionManager
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.tasks.asDeferred
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val context: Context
): LocationRepository {

    override suspend fun getCurrentLocation(): LocationResult {
        if (hasLocationPermission()) {
            try {
                val location: Location? = fusedLocationProviderClient.lastLocation.asDeferred().await()

                return if (location != null) {
                    LocationResult.Success(location)
                } else {
                    //Location not found in cache
                    LocationResult.Error("Location not found")
                }

            } catch (e: SecurityException) {
                //Permissions denied during runtime
                return LocationResult.Error("Location permission denied")
            }
        } else {
            //Permissions not granted by user
            return LocationResult.Error("Location permission not granted")
        }
    }

    override fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(context, PermissionManager.FINE_LOCATION_PERMISSION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, PermissionManager.COARSE_LOCATION_PERMISSION) == PackageManager.PERMISSION_GRANTED
    }

}
