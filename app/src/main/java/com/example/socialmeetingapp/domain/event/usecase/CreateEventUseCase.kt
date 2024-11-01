package com.example.socialmeetingapp.domain.event.usecase

import com.example.socialmeetingapp.domain.common.model.Result
import com.example.socialmeetingapp.domain.event.model.Event
import com.example.socialmeetingapp.domain.event.repository.EventRepository
import com.example.socialmeetingapp.domain.location.usecase.GetAddressFromLatLngUseCase
import javax.inject.Inject

class CreateEventUseCase @Inject constructor(
    private val eventRepository: EventRepository,
    private val getAddressFromLatLngUseCase: GetAddressFromLatLngUseCase
) {
    suspend operator fun invoke(event: Event): Result<String> {
        when (val result = getAddressFromLatLngUseCase(event.locationCoordinates)) {
            is Result.Success -> event.locationAddress = result.data
            is Result.Error -> return Result.Error(result.message)
            else -> Unit
        }

        return eventRepository.createEvent(event)
    }
}