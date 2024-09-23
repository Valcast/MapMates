package com.example.socialmeetingapp.domain.repository

import com.example.socialmeetingapp.domain.model.LocationResult


interface LocationRepository {
    suspend fun getCurrentLocation(): LocationResult

    fun hasLocationPermission(): Boolean
}

