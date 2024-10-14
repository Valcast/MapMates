package com.example.socialmeetingapp.domain.event.usecase

import com.example.socialmeetingapp.domain.event.repository.EventRepository
import javax.inject.Inject

class GetAllEventsUseCase @Inject constructor(private val eventRepository: EventRepository) {
    suspend operator fun invoke(): EventResult {
        return eventRepository.getEvents()
    }
}