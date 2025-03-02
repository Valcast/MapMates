package com.example.socialmeetingapp.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.model.Category
import com.example.socialmeetingapp.domain.model.Event
import com.example.socialmeetingapp.domain.model.Result
import com.example.socialmeetingapp.domain.repository.EventRepository
import com.example.socialmeetingapp.domain.repository.LocationRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import javax.inject.Inject


sealed class HomeState {
    data object Loading : HomeState()
    data class Error(val message: String) : HomeState()
    data class Content(val eventDetails: List<Event>, val categories: List<Category>) : HomeState()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    eventRepository: EventRepository
) : ViewModel() {

    private val _filters = MutableStateFlow(Filters())

    private val _currentLocation = MutableStateFlow<LatLng?>(null)
    val currentLocation: StateFlow<LatLng?> = _currentLocation.onStart {
        val locationResult = locationRepository.getLocation()

        if (locationResult is Result.Success) {
            _currentLocation.update { locationResult.data }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val state: StateFlow<HomeState> = combine(eventRepository.events, _filters) { events, filters ->

        val filteredEvents = events.filter { event ->
            val eventEndTimeMilis =
                event.startTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
            val currentTimeMilis = Clock.System.now().toEpochMilliseconds()

            eventEndTimeMilis > currentTimeMilis &&
                    (filters.startDate == null || event.startTime >= filters.startDate) &&
                    (filters.endDate == null || event.startTime <= filters.endDate) &&
                    (filters.category == null || event.category == filters.category)
        }

        val categories = eventRepository.getCategories()

        HomeState.Content(
            filteredEvents,
            if (categories is Result.Success) categories.data else emptyList()
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        HomeState.Loading
    )

    fun applyFilters(startDate: LocalDateTime?, endDate: LocalDateTime?, category: Category?) {
        _filters.update { it.copy(startDate = startDate, endDate = endDate, category = category) }
    }

    suspend fun getLocation() {
        when (val locationResult = locationRepository.getLocation()) {
            is Result.Error -> _currentLocation.value = null
            is Result.Success -> {
                Log.d("HomeViewModel", "Location: ${locationResult.data}")
                _currentLocation.value = locationResult.data
            }

            else -> {}
        }

    }

    data class Filters(
        val startDate: LocalDateTime? = null,
        val endDate: LocalDateTime? = null,
        val category: Category? = null
    )
}