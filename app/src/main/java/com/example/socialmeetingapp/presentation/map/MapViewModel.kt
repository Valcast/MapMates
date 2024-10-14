package com.example.socialmeetingapp.presentation.map

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.common.model.Resource
import com.example.socialmeetingapp.domain.event.model.Event
import com.example.socialmeetingapp.domain.location.model.LocationResult
import com.example.socialmeetingapp.domain.event.repository.EventRepository
import com.example.socialmeetingapp.domain.event.usecase.GetAllEventsUseCase
import com.example.socialmeetingapp.domain.location.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val getAllEventsUseCase: GetAllEventsUseCase
) : ViewModel() {
    private var _locationState = MutableStateFlow<Resource<Location>>(Resource.Loading)
    val locationState = _locationState.onStart {
        viewModelScope.launch {
            locationRepository.latestLocation.collect { locationResult ->
                when (locationResult) {
                    is LocationResult.Error -> _locationState.value =
                        MapState.Error(locationResult.message)

                    is LocationResult.Success -> _locationState.value =
                        MapState.LocationAvailable(locationResult.location)
                }
            }

        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        MapState.Loading
    )

    private var _eventsState = MutableStateFlow<MapState>(MapState.Loading)
    val eventsState = _eventsState.onStart {
        viewModelScope.launch {
            val eventsResult = getAllEventsUseCase()

            when (eventsResult) {
                is EventResult.SuccessMultiple -> _eventsState.value =
                    MapState.EventsAvailable(eventsResult.events)

                is EventResult.Error -> _eventsState.value = MapState.Error(eventsResult.message)
                else -> {
                    return@launch
                }
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        MapState.Loading
    )
}