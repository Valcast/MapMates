package com.example.socialmeetingapp.presentation.event.createevent

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.common.model.Result
import com.example.socialmeetingapp.domain.event.model.Event
import com.example.socialmeetingapp.domain.event.usecase.CreateEventUseCase
import com.example.socialmeetingapp.domain.location.usecase.GetAddressFromLatLngUseCase
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class CreateEventViewModel @Inject constructor(
    private val createEventUseCase: CreateEventUseCase,
    private val getAddressFromLatLngUseCase: GetAddressFromLatLngUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<Result<Unit>>(Result.Initial)
    val state = _state.asStateFlow()

    private val _uiState = MutableStateFlow<CreateEventFlow>(CreateEventFlow.Info)
    val uiState = _uiState.asStateFlow()

    private val _isNextButtonEnabled = MutableStateFlow(false)
    val isNextButtonEnabled = _isNextButtonEnabled.asStateFlow()

    private val _isRulesAccepted = MutableStateFlow(false)
    val isRulesAccepted = _isRulesAccepted.asStateFlow()

    private val _eventData = MutableStateFlow(Event.EMPTY)
    val eventData = _eventData.asStateFlow()

    fun nextStep() {
        if (uiState.value == CreateEventFlow.Rules) {
            viewModelScope.launch { _state.value = createEventUseCase(eventData.value) }
            return
        }

        _uiState.update { it.inc() }
        validateNextButton()
    }

    fun previousStep() {
        if (uiState.value == CreateEventFlow.Info) return

        _uiState.update { it.dec() }
        validateNextButton()
    }

    private fun validateNextButton() {
        _isNextButtonEnabled.value = when (uiState.value) {
            CreateEventFlow.Info -> _eventData.value.title.length > 3 && eventData.value.description.length > 15 && eventData.value.maxParticipants > 2
            CreateEventFlow.Time -> (eventData.value.startTime.date == eventData.value.endTime.date && eventData.value.startTime.time < eventData.value.endTime.time) || eventData.value.startTime.date < eventData.value.endTime.date
            CreateEventFlow.Location -> true
            CreateEventFlow.Rules -> isRulesAccepted.value
        }
    }


    fun updateTitle(title: String) {
        _eventData.value = eventData.value.copy(title = title)
        validateNextButton()
    }

    fun updateDescription(description: String) {
        _eventData.value = eventData.value.copy(description = description)
        validateNextButton()
    }

    fun updateIsPrivate(isPrivate: Boolean) {
        _eventData.value = eventData.value.copy(isPrivate = isPrivate)
        validateNextButton()
    }

    fun updateIsOnline(isOnline: Boolean) {
        _eventData.value = eventData.value.copy(isOnline = isOnline)
        validateNextButton()
    }

    fun updateMaxParticipants(maxParticipants: Int) {
        _eventData.value = eventData.value.copy(maxParticipants = maxParticipants)
        validateNextButton()
    }

    fun updateLocation(location: LatLng) {
        viewModelScope.launch {
            val addressResult = getAddressFromLatLngUseCase(location)
            if (addressResult is Result.Success) {
                _eventData.value = eventData.value.copy(
                    locationAddress = addressResult.data,
                    locationCoordinates = location
                )
            }
            validateNextButton()
        }
    }

    fun setStartTime(date: LocalDateTime) {
        _eventData.value = eventData.value.copy(startTime = date)
        validateNextButton()
    }

    fun setEndTime(date: LocalDateTime) {
        _eventData.value = eventData.value.copy(endTime = date)
        validateNextButton()
    }

    fun updateRulesAccepted() {
        _isRulesAccepted.value = !_isRulesAccepted.value
        validateNextButton()
    }
}

sealed class CreateEventFlow() {
    data object Info : CreateEventFlow()
    data object Time : CreateEventFlow()
    data object Location : CreateEventFlow()
    data object Rules : CreateEventFlow()

    operator fun inc(): CreateEventFlow {
        return when (this) {
            Info -> Time
            Time -> Location
            Location -> Rules
            Rules -> Rules
        }
    }

    operator fun dec(): CreateEventFlow {
        return when (this) {
            Info -> Info
            Time -> Info
            Location -> Time
            Rules -> Location
        }
    }

    fun toInt(): Int {
        return when (this) {
            Info -> 0
            Time -> 1
            Location -> 2
            Rules -> 3
        }
    }
}


