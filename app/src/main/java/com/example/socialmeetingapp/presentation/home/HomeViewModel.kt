package com.example.socialmeetingapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.model.Event
import com.example.socialmeetingapp.domain.model.Result
import com.example.socialmeetingapp.domain.repository.EventRepository
import com.example.socialmeetingapp.domain.repository.LocationRepository
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
    private val locationRepository: LocationRepository,
    private val eventRepository: EventRepository
) : ViewModel() {
    private var _locationData = MutableStateFlow<Result<LatLng>>(Result.Loading)
    val locationData = _locationData.onStart {
        viewModelScope.launch {
            locationRepository.latestLocation.collect { _locationData.value = it }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        Result.Loading
    )

    private var _eventsData = MutableStateFlow<Result<List<Event>>>(Result.Loading)
    val eventsData = _eventsData.onStart {
        viewModelScope.launch { _eventsData.value = eventRepository.getEvents() }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        Result.Loading
    )
}