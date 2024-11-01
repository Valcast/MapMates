package com.example.socialmeetingapp.domain.event.usecase

import com.example.socialmeetingapp.domain.common.model.Result
import com.example.socialmeetingapp.domain.event.model.UserEvents
import com.example.socialmeetingapp.domain.event.repository.EventRepository
import javax.inject.Inject

class GetUserEventsUseCase @Inject constructor(
    private val eventRepository: EventRepository
) {
    suspend operator fun invoke(userId: String): Result<UserEvents> {
        return eventRepository.getUserEvents(userId)
    }
}