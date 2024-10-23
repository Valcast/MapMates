package com.example.socialmeetingapp.domain.event.usecase

import android.util.Log
import com.example.socialmeetingapp.domain.common.model.Result
import com.example.socialmeetingapp.domain.event.repository.EventRepository
import com.example.socialmeetingapp.domain.user.usecase.GetCurrentUserUseCase
import javax.inject.Inject

class JoinEventUseCase @Inject constructor(private val eventRepository: EventRepository, private val getCurrentUserUseCase: GetCurrentUserUseCase, private val getEventUseCase: GetEventUseCase) {
    suspend operator fun invoke(id: String): Result<Unit> {
        val currentUserResult = getCurrentUserUseCase()
        val eventResult = getEventUseCase(id)

        when {
            currentUserResult is Result.Success && eventResult is Result.Success -> {
                val event = eventResult.data
                val currentUser = currentUserResult.data

                if (event.author.id == currentUser.id) {
                    return Result.Error("You cannot join your own event")
                }


                Log.d("JoinEventUseCase", "event.participants: ${event.participants}")

                event.participants.forEach { participant ->

                    if (participant.id == currentUser.id) {
                        return Result.Error("You already joined this event")
                    }
                }
            }

            currentUserResult is Result.Error -> return currentUserResult
            eventResult is Result.Error -> return eventResult
        }

        return eventRepository.joinEvent(id)
    }
}