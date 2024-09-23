package com.example.socialmeetingapp.presentation.map

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.model.LocationResult
import com.example.socialmeetingapp.domain.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MapState {
    data object Loading : MapState()
    data class LocationAvailable(val location: Location) : MapState()
    data class Error(val message: String) : MapState()
}

@HiltViewModel
class MapViewModel @Inject constructor(
    private val locationRepository: LocationRepository
): ViewModel()
 {
    private var _state = MutableStateFlow<MapState>(MapState.Loading)
    val state = _state.onStart {
        viewModelScope.launch {
            while (true) {
                when (val locationResult = locationRepository.getCurrentLocation()) {
                    is LocationResult.Success -> {
                        _state.value = MapState.LocationAvailable(locationResult.location)
                    }
                    is LocationResult.Error -> {
                        _state.value = MapState.Error(locationResult.message)
                    }
                }

                delay(2000L)
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        MapState.Loading
    )
}