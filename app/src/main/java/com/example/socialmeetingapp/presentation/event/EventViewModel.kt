package com.example.socialmeetingapp.presentation.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.model.Event
import com.example.socialmeetingapp.domain.model.Result
import com.example.socialmeetingapp.domain.model.User
import com.example.socialmeetingapp.domain.repository.EventRepository
import com.example.socialmeetingapp.domain.repository.UserRepository
import com.example.socialmeetingapp.presentation.common.NavigationManager
import com.example.socialmeetingapp.presentation.common.Routes
import com.example.socialmeetingapp.presentation.common.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class EventState {
    data object Loading : EventState()
    data class Error(val message: String) : EventState()
    data class Content(val event: Event, val currentUser: User) : EventState()
}

@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _state = MutableStateFlow<EventState>(EventState.Loading)
    val state = _state.asStateFlow()


    fun getEvent(eventID: String) {
        viewModelScope.launch {
            combine(eventRepository.eventsStateFlow, userRepository.currentUser) { events, userResult ->
                if (events.isEmpty()) return@combine EventState.Loading

                val event = events.find { it.id == eventID } ?: return@combine EventState.Error("Event not found")


                if (userResult is Result.Success && userResult.data != null) {
                    EventState.Content(event, userResult.data)
                } else {
                    EventState.Error("User not found")
                }
            }.collectLatest { _state.value = it }
        }
    }

    fun joinEvent(eventID: String) {
        if (state.value is EventState.Content) {
            if ((state.value as EventState.Content).currentUser.id == (state.value as EventState.Content).event.author.id) {
                SnackbarManager.showMessage("You are the author of this event")
                return
            } else if ((state.value as EventState.Content).event.participants.any { it.id == (state.value as EventState.Content).currentUser.id }) {
                SnackbarManager.showMessage("You have already joined this event")
                return
            } else if ((state.value as EventState.Content).event.participants.size >= (state.value as EventState.Content).event.maxParticipants) {
                SnackbarManager.showMessage("Event is full")
                return
            }
        }

        viewModelScope.launch {
            when (val joinResult = eventRepository.joinEvent(eventID)) {
                is Result.Success -> {
                    SnackbarManager.showMessage("You have joined the event")
                    getEvent(eventID)
                }
                is Result.Error -> { SnackbarManager.showMessage(joinResult.message) }

                else -> {}
            }
        }
    }

    fun leaveEvent(eventID: String) {
        if (state.value is EventState.Content) {
            if ((state.value as EventState.Content).currentUser.id == (state.value as EventState.Content).event.author.id) {
                SnackbarManager.showMessage("You are the author of this event")
                return
            } else if (!(state.value as EventState.Content).event.participants.any { it.id == (state.value as EventState.Content).currentUser.id }) {
                SnackbarManager.showMessage("You have not joined this event")
                return
            }
        }
        viewModelScope.launch {
            when (val leaveResult = eventRepository.leaveEvent(eventID)) {
                is Result.Success -> {
                    SnackbarManager.showMessage("You have left the event")
                    getEvent(eventID)
                }

                is Result.Error -> {
                    SnackbarManager.showMessage(leaveResult.message)
                }

                else -> {}
            }
        }
    }

    fun deleteEvent(eventID: String) {
        viewModelScope.launch {
            when (val deleteResult = eventRepository.deleteEvent(eventID)) {
                is Result.Success -> {
                    SnackbarManager.showMessage("Event deleted")
                    NavigationManager.navigateTo(Routes.Map())
                }

                is Result.Error -> {
                    SnackbarManager.showMessage(deleteResult.message)
                }

                else -> {}
            }
        }
    }

    fun removeParticipant(eventID: String, userID: String) {
        viewModelScope.launch {
            when (val removeResult = eventRepository.removeParticipant(eventID, userID)) {
                is Result.Success -> {
                    SnackbarManager.showMessage("Participant removed")
                    getEvent(eventID)
                }

                is Result.Error -> {
                    SnackbarManager.showMessage(removeResult.message)
                }

                else -> {}
            }
        }
    }

    fun sendJoinRequest(eventID: String) {
        if (state.value is EventState.Content) {
            if ((state.value as EventState.Content).currentUser.id == (state.value as EventState.Content).event.author.id) {
                SnackbarManager.showMessage("You are the author of this event")
                return
            } else if ((state.value as EventState.Content).event.participants.any { it.id == (state.value as EventState.Content).currentUser.id }) {
                SnackbarManager.showMessage("You have already joined this event")
                return
            } else if ((state.value as EventState.Content).event.participants.size >= (state.value as EventState.Content).event.maxParticipants) {
                SnackbarManager.showMessage("Event is full")
                return
            } else if ((state.value as EventState.Content).event.joinRequests.any { it.id == (state.value as EventState.Content).currentUser.id }) {
                SnackbarManager.showMessage("You have already sent a join request")
                return
            }
        }

        viewModelScope.launch {
            when (val result = eventRepository.sendJoinRequest(eventID)) {
                is Result.Success -> {
                    SnackbarManager.showMessage("Join request sent")
                    getEvent(eventID)
                }
                is Result.Error -> {
                    SnackbarManager.showMessage(result.message)
                }
                else -> {}
            }
        }
    }
}

