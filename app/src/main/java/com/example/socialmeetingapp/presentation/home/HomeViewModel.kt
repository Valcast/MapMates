package com.example.socialmeetingapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.model.Event
import com.example.socialmeetingapp.domain.model.Result
import com.example.socialmeetingapp.domain.repository.EventRepository
import com.example.socialmeetingapp.domain.repository.LocationRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import javax.inject.Inject


sealed class HomeState {
    data object Loading : HomeState()
    data class Error(val message: String) : HomeState()
    data class Content(val eventDetails: List<Event>, val location: Result<LatLng>) : HomeState()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    locationRepository: LocationRepository,
    eventRepository: EventRepository
) : ViewModel() {
    val state: StateFlow<HomeState> = combine(eventRepository.events, locationRepository.latestLocation) { events, locationResult ->

        val filteredEvents = events.filter { event ->
            event.endTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds() > System.currentTimeMillis()
        }

        HomeState.Content(filteredEvents, locationResult)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        HomeState.Loading
    )
}