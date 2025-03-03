package com.example.socialmeetingapp.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import com.example.socialmeetingapp.BuildConfig
import com.example.socialmeetingapp.data.api.GeocodingApi
import com.example.socialmeetingapp.domain.model.Result
import com.example.socialmeetingapp.domain.repository.LocationRepository
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.tasks.await
import retrofit2.HttpException

class LocationRepositoryImpl(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    private val context: Context,
    private val geocodingApi: GeocodingApi
) : LocationRepository {

    override suspend fun getLocation(): Result<LatLng> {
        if (!checkLocationPermission()) {
            return Result.Error("Location permission not granted")
        }

        val location = fusedLocationProviderClient.getCurrentLocation(locationRequest, null).await()
                ?: return Result.Error("Cannot get location")

        return Result.Success(LatLng(location.latitude, location.longitude))
    }

    private val locationRequest = CurrentLocationRequest.Builder()
        .setPriority(Priority.PRIORITY_LOW_POWER)
        .setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
        .build()

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

    private fun checkLocationPermission(): Boolean {
        return context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
}
