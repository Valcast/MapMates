package com.example.socialmeetingapp.domain.repository

import com.example.socialmeetingapp.domain.model.Result
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    val latestLocation: Flow<Result<LatLng>>

    suspend fun getAddressFromLatLng(location: LatLng): Result<String>
}

