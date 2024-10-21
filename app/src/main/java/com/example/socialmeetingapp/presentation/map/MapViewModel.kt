package com.example.socialmeetingapp.presentation.map

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.common.model.Result
import com.example.socialmeetingapp.domain.event.model.Event
import com.example.socialmeetingapp.domain.event.usecase.GetAllEventsUseCase
import com.example.socialmeetingapp.domain.location.repository.LocationRepository
import com.example.socialmeetingapp.domain.location.usecase.GetCurrentLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getAllEventsUseCase: GetAllEventsUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase
) : ViewModel() {
    private var _locationState = MutableStateFlow<Result<Location>>(Result.Loading)
    val locationState = _locationState.onStart {
        viewModelScope.launch {
            getCurrentLocationUseCase().collect { _locationState.value = it }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        Result.Loading
    )

    private var _eventsState = MutableStateFlow<Result<List<Event>>>(Result.Loading)
    val eventsState = _eventsState.onStart {
        viewModelScope.launch {
            _eventsState.value = getAllEventsUseCase()
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        Result.Loading
    )
}