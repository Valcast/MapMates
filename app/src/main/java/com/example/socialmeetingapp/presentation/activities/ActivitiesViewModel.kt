package com.example.socialmeetingapp.presentation.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.common.model.Result
import com.example.socialmeetingapp.domain.event.model.Event
import com.example.socialmeetingapp.domain.event.usecase.GetUserEventsUseCase
import com.example.socialmeetingapp.domain.user.model.User
import com.example.socialmeetingapp.domain.user.usecase.GetCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ActivitiesViewModel @Inject constructor(
    private val getUserEventsUseCase: GetUserEventsUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private var _events = MutableStateFlow<List<Event>>(emptyList())
    val events = _events.asStateFlow().onStart {
        val user = getCurrentUserUseCase()

        if (user is Result.Error) {
            return@onStart
        }

        val eventsResult = getUserEventsUseCase((user as Result.Success<User>).data.id)

        if (eventsResult is Result.Success) {
            _events.value = eventsResult.data
        }


    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())


}