package com.example.socialmeetingapp.presentation.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.common.model.Result
import com.example.socialmeetingapp.domain.event.model.Event
import com.example.socialmeetingapp.domain.event.usecase.DeleteEventUseCase
import com.example.socialmeetingapp.domain.event.usecase.GetEventUseCase
import com.example.socialmeetingapp.domain.event.usecase.JoinEventUseCase
import com.example.socialmeetingapp.domain.event.usecase.LeaveEventUseCase
import com.example.socialmeetingapp.domain.user.model.User
import com.example.socialmeetingapp.domain.user.usecase.GetCurrentUserUseCase
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
    data class Content(val eventResult: Result<Event>, val currentUser: Result<User>) : EventState()
}

@HiltViewModel
class EventViewModel @Inject constructor(
    private val getEventUseCase: GetEventUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val joinEventUseCase: JoinEventUseCase,
    private val leaveEventUseCase: LeaveEventUseCase,
    private val deleteEventUseCase: DeleteEventUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<EventState>(EventState.Loading)
    val state = _state.asStateFlow()


    fun getEvent(id: String) {
        viewModelScope.launch {
            val eventResult = async { getEventUseCase(id) }
            val currentUser = async { getCurrentUserUseCase() }

            _state.value = EventState.Content(eventResult.await(), currentUser.await())
        }
    }

    fun joinEvent(eventID: String) {
        viewModelScope.launch {
            when(val joinResult = joinEventUseCase(eventID)) {
                is Result.Success -> {
                    SnackbarManager.showMessage("You have joined the event")
                }
                is Result.Error -> {
                    SnackbarManager.showMessage(joinResult.message)
                }
                else -> {}
            }
        }
    }

    fun leaveEvent(eventID: String) {
        viewModelScope.launch {
            when(val leaveResult = leaveEventUseCase(eventID)) {
                is Result.Success -> {
                    SnackbarManager.showMessage("You have left the event")
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
            when(val deleteResult = deleteEventUseCase(eventID)) {
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
}

