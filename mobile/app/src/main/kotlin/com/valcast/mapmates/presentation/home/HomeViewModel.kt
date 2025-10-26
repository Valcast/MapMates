package com.valcast.mapmates.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valcast.mapmates.domain.model.Category
import com.valcast.mapmates.domain.model.DateRange
import com.valcast.mapmates.domain.model.Event
import com.valcast.mapmates.domain.model.SortOrder
import com.valcast.mapmates.domain.model.onFailure
import com.valcast.mapmates.domain.model.onSuccess
import com.valcast.mapmates.domain.repository.EventRepository
import com.valcast.mapmates.domain.repository.LocationRepository
import com.valcast.mapmates.presentation.common.SnackbarManager
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.ktx.utils.sphericalDistance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import javax.inject.Inject
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val eventRepository: EventRepository
) : ViewModel() {
    private var _filters = MutableStateFlow(Filters())
    val filters = _filters.asStateFlow()

    private var _isLoadingEvents = MutableStateFlow(false)
    val isLoadingEvents = _isLoadingEvents.asStateFlow()

    private var _currentLocation = MutableStateFlow<LatLng?>(null)
    val currentLocation: StateFlow<LatLng?> = _currentLocation.onStart {
        locationRepository.getLocation()
            .onSuccess { locationData ->
                _currentLocation.update { locationData }
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private var _events = MutableStateFlow<List<Event>>(emptyList())
    val events = _events.onStart {
        fetchEvents()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private fun fetchEvents() {
        _isLoadingEvents.value = true
        _events.value = emptyList()
        viewModelScope.launch {
            eventRepository.getEvents()
                .onSuccess { fetchedEvents ->
                    Log.i("HomeViewModel", "Fetched events: $fetchedEvents")
                    val filters = _filters.value

                    val filteredEvents = fetchedEvents.filter { event ->
                        val eventStartTime = event.startTime.toInstant(TimeZone.UTC)
                            .toLocalDateTime(TimeZone.currentSystemDefault())
                        val today =
                            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

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
                                val customStart =
                                    filters.dateRange.startTime.toInstant(TimeZone.UTC)
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

                    _isLoadingEvents.value = false
                    _events.value = filteredEvents
                }
                .onFailure { _ ->
                    _isLoadingEvents.value = false
                    _events.value = emptyList()
                    SnackbarManager.showMessage("Failed to fetch events")
                }
                .also {
                    _isLoadingEvents.value = false
                }
        }
    }


    fun applyFilters(
        dateRange: DateRange?, category: Category?, sortOrderType: SortOrder?
    ) {
        _filters.update {
            it.copy(
                dateRange = dateRange, category = category, sortOrder = sortOrderType
            )
        }
        fetchEvents()
    }

    suspend fun getLocation() {
        locationRepository.getLocation()
            .onSuccess { locationData ->
                Log.d("HomeViewModel", "Location: $locationData")
                _currentLocation.value = locationData
            }
            .onFailure { _ ->
                _currentLocation.value = null
            }
    }

    private fun sortEvents(events: List<Event>, sortOrderType: SortOrder): List<Event> {
        return when (sortOrderType) {
            SortOrder.NEXT_DATE -> events.sortedBy { it.startTime }
            SortOrder.DISTANCE -> events.sortedBy {
                currentLocation.value?.let { it1 ->
                    it.locationCoordinates?.sphericalDistance(
                        it1
                    ) ?: Double.MAX_VALUE
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