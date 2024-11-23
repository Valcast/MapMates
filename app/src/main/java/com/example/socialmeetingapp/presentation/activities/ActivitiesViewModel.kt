package com.example.socialmeetingapp.presentation.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.model.Event
import com.example.socialmeetingapp.domain.model.Result
import com.example.socialmeetingapp.domain.repository.EventRepository
import com.example.socialmeetingapp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ActivitiesState {
    data object Loading : ActivitiesState()
    data class Error(val message: String) : ActivitiesState()
    data class Content(val createdEvents: List<Event>, val joinedEvents: List<Event>) : ActivitiesState()
}

@HiltViewModel
class ActivitiesViewModel @Inject constructor(
    userRepository: UserRepository,
    private val eventRepository: EventRepository
) : ViewModel() {
    val state = combine(userRepository.currentUser, eventRepository.eventsStateFlow) { userResult, events ->
        if (userResult is Result.Success && userResult.data != null) {
            val createdEvents = events.filter { it.author.id == userResult.data.id }
            val joinedEvents = events.filter { it.participants.any { it.id == userResult.data.id } }

            ActivitiesState.Content(createdEvents, joinedEvents)
        } else {
            ActivitiesState.Loading
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ActivitiesState.Loading
    )

    fun acceptJoinRequest(eventID: String, userID: String) {
        viewModelScope.launch {
            eventRepository.acceptJoinRequest(eventID, userID)
        }
    }

    fun declineJoinRequest(eventID: String, userID: String) {
        viewModelScope.launch {
            eventRepository.declineJoinRequest(eventID, userID)
        }
    }

}