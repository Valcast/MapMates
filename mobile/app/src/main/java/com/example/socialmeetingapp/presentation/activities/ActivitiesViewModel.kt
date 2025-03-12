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
    private var _state = MutableStateFlow<ActivitiesState>(ActivitiesState.Loading)
    val state = _state.asStateFlow()

    init {
        getActivities()
    }

    private fun getActivities() {
        viewModelScope.launch {
            when (val currentUserResult = userRepository.getCurrentUserPreview()) {
                is Result.Error -> {
                    _state.update { ActivitiesState.Error(currentUserResult.message) }
                }

                is Result.Success -> {
                    val currentUser = currentUserResult.data
                    val createdEventsResult = eventRepository.getEventsByAuthor(currentUser.id)
                    val joinedEventsResult = eventRepository.getEventsByParticipant(currentUser.id)

                    if (createdEventsResult is Result.Error || joinedEventsResult is Result.Error) {
                        _state.update { ActivitiesState.Error("Error occurred while loading activities") }
                    } else if (createdEventsResult is Result.Success && joinedEventsResult is Result.Success) {
                        _state.update {
                            ActivitiesState.Content(
                                createdEventsResult.data,
                                joinedEventsResult.data
                            )
                        }
                    } else {
                        _state.update { ActivitiesState.Loading }
                    }
                }

                else -> {
                    _state.update { ActivitiesState.Loading }
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
