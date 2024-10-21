package com.example.socialmeetingapp.domain.location.usecase

import android.location.Location
import com.example.socialmeetingapp.domain.common.model.Result
import com.example.socialmeetingapp.domain.location.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentLocationUseCase @Inject constructor(private val locationRepository: LocationRepository) {
    operator fun invoke(): Flow<Result<Location>> {
        return locationRepository.latestLocation
    }

}