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


    fun getEvent(id: String) {
        viewModelScope.launch {
            val eventResult = async { eventRepository.getEvent(id) }.await()
            val currentUserResult = async { userRepository.getCurrentUser() }.await()

            if (eventResult is Result.Success && currentUserResult is Result.Success) {
                _state.value = EventState.Content(eventResult.data, currentUserResult.data)
            } else if (eventResult is Result.Error) {
                _state.value = EventState.Error(eventResult.message)
            } else if (currentUserResult is Result.Error) {
                _state.value = EventState.Error(currentUserResult.message)
            }

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
                    NavigationManager.navigateTo(Routes.Map)
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

