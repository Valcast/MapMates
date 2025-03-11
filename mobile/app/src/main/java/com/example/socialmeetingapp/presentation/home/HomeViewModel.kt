package com.example.socialmeetingapp.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmeetingapp.domain.model.Category
import com.example.socialmeetingapp.domain.model.DateRange
import com.example.socialmeetingapp.domain.model.Event
import com.example.socialmeetingapp.domain.model.Result
import com.example.socialmeetingapp.domain.model.SortOrder
import com.example.socialmeetingapp.domain.repository.EventRepository
import com.example.socialmeetingapp.domain.repository.LocationRepository
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.ktx.utils.sphericalDistance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val locationRepository: LocationRepository, eventRepository: EventRepository
) : ViewModel() {
    private var _filters = MutableStateFlow(Filters())
    val filters = _filters.asStateFlow()

    private var _currentLocation = MutableStateFlow<LatLng?>(null)
    val currentLocation: StateFlow<LatLng?> = _currentLocation.onStart {
        val locationResult = locationRepository.getLocation()

        if (locationResult is Result.Success) {
            _currentLocation.update { locationResult.data }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private var _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories = _categories.onStart {
        val categoriesResult = eventRepository.getCategories()

        if (categoriesResult is Result.Success) {
            _categories.update { categoriesResult.data }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val events = combine(eventRepository.events, _filters) { events, filters ->
        events.filter { event ->
            val eventStartTime = event.startTime.toInstant(TimeZone.UTC)
                .toLocalDateTime(TimeZone.currentSystemDefault())
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

            when (filters.dateRange) {
                DateRange.Today -> {
                    eventStartTime.date == today
                }

                DateRange.Tomorrow -> {
                    eventStartTime.date == today.plus(DatePeriod(days = 1))
                }

                DateRange.ThisWeek -> {
                    val endOfWeek = today.plus(DatePeriod(days = 7))
                    eventStartTime.date in today..endOfWeek
                }

                is DateRange.Custom -> {
                    val customStart = filters.dateRange.startTime.toInstant(TimeZone.UTC)
                        .toLocalDateTime(TimeZone.currentSystemDefault()).date
                    val customEnd = filters.dateRange.endTime.toInstant(TimeZone.UTC)
                        .toLocalDateTime(TimeZone.currentSystemDefault()).date

                    eventStartTime.date in customStart..customEnd
                }

                null -> eventStartTime >= Clock.System.now()
                    .toLocalDateTime(TimeZone.currentSystemDefault())
            } && (filters.category == null || event.category == filters.category)
        }.let { filteredEvents ->
            if (filters.sortOrder != null) {
                sortEvents(filteredEvents, filters.sortOrder)
            } else {
                filteredEvents
            }
        }
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    fun applyFilters(
        dateRange: DateRange?, category: Category?, sortOrderType: SortOrder?
    ) {
        _filters.update {
            it.copy(
                dateRange = dateRange, category = category, sortOrder = sortOrderType
            )
        }
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

    private fun sortEvents(events: List<Event>, sortOrderType: SortOrder): List<Event> {
        return when (sortOrderType) {
            SortOrder.NEXT_DATE -> events.sortedBy { it.startTime }
            SortOrder.DISTANCE -> events.sortedBy {
                currentLocation.value?.let { it1 ->
                    it.locationCoordinates.sphericalDistance(
                        it1
                    )
                }
            }

            SortOrder.POPULARITY -> events.sortedByDescending { it.participants.size }
        }
    }
}

data class Filters(
    val dateRange: DateRange? = null,
    val category: Category? = null,
    val sortOrder: SortOrder? = null
)