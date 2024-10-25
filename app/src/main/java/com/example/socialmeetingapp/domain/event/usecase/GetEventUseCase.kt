package com.example.socialmeetingapp.domain.event.usecase

import com.example.socialmeetingapp.domain.common.model.Result
import com.example.socialmeetingapp.domain.event.model.Event
import com.example.socialmeetingapp.domain.event.repository.EventRepository
import com.example.socialmeetingapp.domain.location.usecase.GetAddressFromLatLngUseCase
import javax.inject.Inject

class GetEventUseCase @Inject constructor(
    private val eventRepository: EventRepository,
) {
    suspend operator fun invoke(id: String): Result<Event> {
        return eventRepository.getEvent(id)
    }
}