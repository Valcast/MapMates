package com.example.socialmeetingapp.presentation.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.model.Event
import com.example.socialmeetingapp.domain.model.UserPreview
import com.example.socialmeetingapp.domain.model.onFailure
import com.example.socialmeetingapp.domain.model.onSuccess
import com.example.socialmeetingapp.domain.repository.EventRepository
import com.example.socialmeetingapp.domain.repository.UserRepository
import com.example.socialmeetingapp.presentation.common.SnackbarManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class EventState {
    data object Loading : EventState()
    data class Error(val message: String) : EventState()
    data class Content(val event: Event, val currentUser: UserPreview) : EventState()
}

@HiltViewModel(assistedFactory = EventViewModel.Factory::class)
class EventViewModel @AssistedInject constructor(
    @Assisted private val eventId: String,
    private val eventRepository: EventRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(eventId: String): EventViewModel
    }

    private val _state = MutableStateFlow<EventState>(EventState.Loading)
    val state = _state.asStateFlow()

    private val _isRefresh = MutableStateFlow(false)
    val isRefresh = _isRefresh.asStateFlow()

    init {
        loadEvent()
    }

    fun refresh() {
        _isRefresh.value = true
        loadEvent()
    }

    private fun loadEvent() {
        viewModelScope.launch {
            eventRepository.getEvent(eventId)
                .onSuccess { eventData ->
                    _isRefresh.value = false
                    userRepository.getCurrentUserPreview()
                        .onSuccess { userData ->
                            _state.value = EventState.Content(eventData, userData)
                        }
                        .onFailure { userError ->
                            _state.value =
                                EventState.Error("Failed to get current user: $userError")
                        }
                }
                .onFailure { eventError ->
                    _state.value = EventState.Error(eventError)
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
            eventRepository.joinEvent(eventID)
                .onSuccess {
                    SnackbarManager.showMessage("You have joined the event")
                    loadEvent()
                }
                .onFailure { errorMessage ->
                    SnackbarManager.showMessage(errorMessage)
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
            eventRepository.leaveEvent(eventID)
                .onSuccess {
                    SnackbarManager.showMessage("You have left the event")
                    loadEvent()
                }
                .onFailure { errorMessage ->
                    SnackbarManager.showMessage(errorMessage)
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
            }
        }

        viewModelScope.launch {
            eventRepository.sendJoinRequest(eventID)
                .onSuccess {
                    SnackbarManager.showMessage("Join request sent")
                }
                .onFailure { errorMessage ->
                    SnackbarManager.showMessage(errorMessage)
                }
        }
    }
}

