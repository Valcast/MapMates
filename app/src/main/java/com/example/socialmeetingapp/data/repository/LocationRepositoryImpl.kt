package com.example.socialmeetingapp.data.repository

import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.content.ContextCompat
import com.example.socialmeetingapp.domain.location.model.LocationResult
import com.example.socialmeetingapp.domain.location.repository.LocationRepository
import com.example.socialmeetingapp.data.utils.PermissionManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class LocationRepositoryImpl(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val context: Context
): LocationRepository {
    override val latestLocation: Flow<LocationResult> = callbackFlow {
        if (hasLocationPermission()) {
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                    val lastLocation = locationResult.lastLocation
                    if (lastLocation != null) {
                        trySend(LocationResult.Success(lastLocation))
                    } else {
                        trySend(LocationResult.Error("No location available"))
                    }
                }
            }

            try {
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )

                awaitClose {
                    fusedLocationProviderClient.removeLocationUpdates(locationCallback)
                }
            } catch (e: SecurityException) {
                trySend(LocationResult.Error("Location permission denied"))
                close(e)
            }
        } else {
            trySend(LocationResult.Error("Location permission not granted"))
            close()
        }
    }

    private val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L).build()

    override fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(context, PermissionManager.FINE_LOCATION_PERMISSION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, PermissionManager.COARSE_LOCATION_PERMISSION) == PackageManager.PERMISSION_GRANTED
    }
}
