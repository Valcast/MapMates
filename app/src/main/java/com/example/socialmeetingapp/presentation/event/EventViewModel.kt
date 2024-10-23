package com.example.socialmeetingapp.presentation.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.common.model.Result
import com.example.socialmeetingapp.domain.event.model.Event
import com.example.socialmeetingapp.domain.event.usecase.GetEventUseCase
import com.example.socialmeetingapp.domain.event.usecase.JoinEventUseCase
import com.example.socialmeetingapp.domain.event.usecase.LeaveEventUseCase
import com.example.socialmeetingapp.presentation.common.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val getEventUseCase: GetEventUseCase,
    private val joinEventUseCase: JoinEventUseCase,
    private val leaveEventUseCase: LeaveEventUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<Result<Event>>(Result.Loading)
    val state = _state.asStateFlow()


    fun getEvent(id: String) {
        viewModelScope.launch {
            when (val eventResult = getEventUseCase(id)) {
                is Result.Success -> {
                    _state.value = eventResult
                }
                is Result.Error -> {
                    SnackbarManager.showMessage(eventResult.message)
                }

                else -> {}
            }
        }
    }

    fun joinEvent(eventID: String) {
        viewModelScope.launch {
            val result = joinEventUseCase(eventID)

            if (result is Result.Error) {
                SnackbarManager.showMessage(result.message)
            }

            SnackbarManager.showMessage("You have joined the event")
        }
    }
}