package com.example.socialmeetingapp.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import com.example.socialmeetingapp.BuildConfig
import com.example.socialmeetingapp.data.api.GeocodingApi
import com.example.socialmeetingapp.domain.model.Result
import com.example.socialmeetingapp.domain.repository.LocationRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import retrofit2.HttpException

class LocationRepositoryImpl(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val context: Context,
    private val geocodingApi: GeocodingApi
) : LocationRepository {

    override val latestLocation: Flow<Result<LatLng>> = callbackFlow {
        if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                    val lastLocation = locationResult.lastLocation
                    if (lastLocation != null) {
                        trySend(Result.Success(LatLng(lastLocation.latitude, lastLocation.longitude)))
                    } else {
                        trySend(Result.Error("No location available"))
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
                trySend(Result.Error("Location permission denied"))
                close(e)
            }
        } else {
            trySend(Result.Error("Location permission not granted"))
            close()
        }
    }

    private val locationRequest =
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L).build()

    override suspend fun getAddressFromLatLng(location: LatLng): Result<String> {
        return try {
            val response = geocodingApi.getAddressFromLatLng(
                latLng = "${location.latitude},${location.longitude}",
                apiKey = BuildConfig.MAPS_API_KEY
            )

            if (response.isSuccessful) {
                val status = response.body()?.status
                val address = response.body()?.results?.firstOrNull()?.formatted_address
                println("address: $address")
                println("status: $status")
                if (address != null) {
                    Result.Success(address)
                } else {
                    Result.Error("No address found")
                }
            } else {
                Result.Error("Error fetching address")
            }
        } catch (e: HttpException) {
            Result.Error(e.message())
        }
    }
}
