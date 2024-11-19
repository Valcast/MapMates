package com.example.socialmeetingapp.presentation.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.model.Result
import com.example.socialmeetingapp.domain.model.User
import com.example.socialmeetingapp.domain.model.UserEvents
import com.example.socialmeetingapp.domain.repository.EventRepository
import com.example.socialmeetingapp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ActivitiesViewModel @Inject constructor(
    userRepository: UserRepository,
    eventRepository: EventRepository
) : ViewModel() {

    private var _events = MutableStateFlow(UserEvents(emptyList(), emptyList()))
    val events = _events.asStateFlow().onStart {
        val user = userRepository.getCurrentUser()

        if (user is Result.Error) {
            return@onStart
        }

        val eventsResult = eventRepository.getUserEvents((user as Result.Success<User>).data.id)

        if (eventsResult is Result.Success) {
            _events.value = eventsResult.data
        }


    }.stateIn(viewModelScope, SharingStarted.Eagerly, UserEvents(emptyList(), emptyList()))


}