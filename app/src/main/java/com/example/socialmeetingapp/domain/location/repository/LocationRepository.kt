package com.example.socialmeetingapp.domain.location.repository

import android.location.Location
import com.example.socialmeetingapp.domain.common.model.Result
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    val latestLocation: Flow<Result<LatLng>>

    fun hasLocationPermission(): Boolean

    suspend fun getAddressFromLatLng(location: LatLng): Result<String>
}

