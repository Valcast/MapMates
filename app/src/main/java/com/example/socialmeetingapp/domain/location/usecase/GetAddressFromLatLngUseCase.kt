package com.example.socialmeetingapp.domain.location.usecase

import com.example.socialmeetingapp.domain.location.repository.LocationRepository
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

class GetAddressFromLatLngUseCase @Inject constructor(private val locationRepository: LocationRepository) {
    suspend operator fun invoke(location: LatLng) = locationRepository.getAddressFromLatLng(location)
}