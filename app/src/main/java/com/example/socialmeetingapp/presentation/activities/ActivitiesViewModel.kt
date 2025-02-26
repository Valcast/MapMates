package com.example.socialmeetingapp.presentation.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.model.Event
import com.example.socialmeetingapp.domain.model.Result
import com.example.socialmeetingapp.domain.repository.EventRepository
import com.example.socialmeetingapp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ActivitiesState {
    data object Loading : ActivitiesState()
    data class Error(val message: String) : ActivitiesState()
    data class Content(val createdEventDetails: List<Event>, val joinedEventDetails: List<Event>) :
        ActivitiesState()
}

@HiltViewModel
class ActivitiesViewModel @Inject constructor(
    private val userRepository: UserRepository, private val eventRepository: EventRepository
) : ViewModel() {
    var _state = MutableStateFlow<ActivitiesState>(ActivitiesState.Loading)
    val state = _state.asStateFlow()

    init {
        getActivities()
    }

    fun getActivities() {
        viewModelScope.launch {
            val currentUser = userRepository.getCurrentUserPreview()

            eventRepository.events.collect {
                if (currentUser is Result.Success) {
                    val createdEvents = it.filter { it.author.id == currentUser.data.id }
                    val joinedEvents =
                        it.filter { it.participants.any { it.id == currentUser.data.id } }

                    _state.update { ActivitiesState.Content(createdEvents, joinedEvents) }
                } else {
                    _state.update {
                        ActivitiesState.Error("You are not authenticated")
                    }
                }
            }
        }
    }

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
