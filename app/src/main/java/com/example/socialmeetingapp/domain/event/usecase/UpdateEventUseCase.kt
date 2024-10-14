package com.example.socialmeetingapp.domain.event.usecase

import com.example.socialmeetingapp.domain.event.model.Event
import com.example.socialmeetingapp.domain.event.repository.EventRepository
import javax.inject.Inject

class UpdateEventUseCase @Inject constructor(private val eventRepository: EventRepository) {
    suspend operator fun invoke(event: Event): EventResult {
        return eventRepository.updateEvent(event)
    }

}