package com.example.socialmeetingapp.domain.event.usecase

import com.example.socialmeetingapp.domain.common.model.Result
import com.example.socialmeetingapp.domain.event.repository.EventRepository
import com.example.socialmeetingapp.domain.user.usecase.GetCurrentUserUseCase
import javax.inject.Inject

class LeaveEventUseCase @Inject constructor(
    private val eventRepository: EventRepository,
    private val getEventUseCase: GetEventUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) {
    suspend operator fun invoke(id: String): Result<Unit> {

        val eventResult = getEventUseCase(id)
        val currentUserResult = getCurrentUserUseCase()

        when {
            eventResult is Result.Success && currentUserResult is Result.Success -> {
                val event = eventResult.data
                val currentUser = currentUserResult.data

                for (participant in event.participants) {
                    if (participant.id == currentUser.id) {
                        return eventRepository.leaveEvent(id)
                    }
                }
            }

            eventResult is Result.Error -> return eventResult
            currentUserResult is Result.Error -> return currentUserResult

        }

        return Result.Error("You are not a participant of the event")
    }
}