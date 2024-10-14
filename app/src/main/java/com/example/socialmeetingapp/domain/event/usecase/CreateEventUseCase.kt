package com.example.socialmeetingapp.domain.event.usecase

import com.example.socialmeetingapp.domain.event.model.Event
import com.example.socialmeetingapp.domain.event.repository.EventRepository
import com.example.socialmeetingapp.domain.user.usecase.GetCurrentUserUseCase
import java.util.Date
import javax.inject.Inject

class CreateEventUseCase @Inject constructor(
    private val eventRepository: EventRepository,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) {
    suspend operator fun invoke(event: Event): EventResult {
        val currentUserResult = getCurrentUserUseCase()

        if (currentUserResult is UserResult.SuccessSingle) {
            val event = event.copy(author = currentUserResult.user, createdAt = Date(System.currentTimeMillis()), updatedAt = Date(System.currentTimeMillis()))

            return eventRepository.createEvent(event)
        } else {
            return EventResult.Error("Failed to get current user")
        }

    }
}