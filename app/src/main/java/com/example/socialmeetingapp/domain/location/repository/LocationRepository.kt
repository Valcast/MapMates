package com.example.socialmeetingapp.domain.location.repository

import com.example.socialmeetingapp.domain.location.model.LocationResult
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    val latestLocation: Flow<LocationResult>

    fun hasLocationPermission(): Boolean
}

