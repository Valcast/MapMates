package com.example.socialmeetingapp.presentation.home

import androidx.compose.runtime.traceEventStart
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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import javax.inject.Inject


sealed class HomeState {
    data object Loading : HomeState()
    data class Error(val message: String) : HomeState()
    data class Content(val eventDetails: List<Event>, val location: Result<LatLng>, val categories: List<Category>) : HomeState()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    locationRepository: LocationRepository,
    eventRepository: EventRepository
) : ViewModel() {

    private val _filters = MutableStateFlow(Filters())

    val state: StateFlow<HomeState> = combine(eventRepository.events, locationRepository.latestLocation, _filters) { events, locationResult, filters ->

        val filteredEvents = events.filter { event ->
            val eventEndTimeMilis = event.startTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
            val currentTimeMilis = Clock.System.now().toEpochMilliseconds()

            eventEndTimeMilis > currentTimeMilis &&
                    (filters.startDate == null || event.startTime >= filters.startDate) &&
                    (filters.endDate == null || event.startTime <= filters.endDate) &&
                    (filters.category == null || event.category == filters.category)
        }

        val categories = eventRepository.getCategories()

        HomeState.Content(filteredEvents, locationResult, if (categories is Result.Success) categories.data else emptyList())
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        HomeState.Loading
    )

    fun applyFilters(startDate: LocalDateTime?, endDate: LocalDateTime?, category: Category?) {
        _filters.update { it.copy(startDate = startDate, endDate = endDate, category = category) }
    }

    data class Filters(
        val startDate: LocalDateTime? = null,
        val endDate: LocalDateTime? = null,
        val category: Category? = null
    )
}