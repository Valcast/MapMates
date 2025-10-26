package com.valcast.mapmates.domain.repository

import com.valcast.mapmates.domain.model.Result
import com.google.android.gms.maps.model.LatLng

interface LocationRepository {
    suspend fun getLocation(): Result<LatLng>
    suspend fun getAddressFromLatLng(location: LatLng): Result<String>
}

