package com.example.socialmeetingapp.presentation.home

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.common.model.Result
import com.example.socialmeetingapp.domain.event.model.Event
import com.example.socialmeetingapp.domain.event.usecase.GetAllEventsUseCase
import com.example.socialmeetingapp.domain.location.usecase.GetCurrentLocationUseCase
import com.example.socialmeetingapp.presentation.common.NavigationManager
import com.example.socialmeetingapp.presentation.common.Routes
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllEventsUseCase: GetAllEventsUseCase,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase
) : ViewModel() {
    private var _locationData = MutableStateFlow<Result<LatLng>>(Result.Loading)
    val locationData = _locationData.onStart {
        viewModelScope.launch {
            getCurrentLocationUseCase().collect { _locationData.value = it }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        Result.Loading
    )

    private var _eventsData = MutableStateFlow<Result<List<Event>>>(Result.Loading)
    val eventsData = _eventsData.onStart {
        viewModelScope.launch {
            _eventsData.value = getAllEventsUseCase()
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        Result.Loading
    )
}