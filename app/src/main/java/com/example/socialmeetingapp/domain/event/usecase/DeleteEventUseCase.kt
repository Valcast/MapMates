package com.example.socialmeetingapp.domain.event.usecase

import com.example.socialmeetingapp.domain.common.model.Result
import com.example.socialmeetingapp.domain.event.repository.EventRepository
import javax.inject.Inject

class DeleteEventUseCase @Inject constructor(private val eventRepository: EventRepository) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return eventRepository.deleteEvent(id)
    }
}