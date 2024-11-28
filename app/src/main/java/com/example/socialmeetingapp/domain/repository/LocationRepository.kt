package com.example.socialmeetingapp.domain.repository

import com.example.socialmeetingapp.domain.model.Result
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface LocationRepository {
    val latestLocation: Flow<Result<LatLng>>

    fun hasLocationPermission(): Boolean

    suspend fun getAddressFromLatLng(location: LatLng): Result<String>
}

