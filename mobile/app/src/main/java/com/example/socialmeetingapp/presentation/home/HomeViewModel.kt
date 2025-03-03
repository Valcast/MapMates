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
import com.google.maps.android.ktx.utils.sphericalDistance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    eventRepository: EventRepository
) : ViewModel() {

    private val _sortType = MutableStateFlow<SortType?>(null)

    private val _filters = MutableStateFlow(Filters())

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

    val events = combine(eventRepository.events, _filters, _sortType) { events, filters, sortType ->
        val filteredEvents = events.filter { event ->
            val eventEndTimeMilis =
                event.startTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
            val currentTimeMilis = Clock.System.now().toEpochMilliseconds()

            eventEndTimeMilis > currentTimeMilis &&
                    (filters.startDate == null || event.startTime >= filters.startDate) &&
                    (filters.endDate == null || event.startTime <= filters.endDate) &&
                    (filters.category == null || event.category == filters.category)
        }

        if (sortType != null) {
            sortEvents(filteredEvents, sortType)
        } else {
            filteredEvents
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
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

    fun changeSortType(sortType: SortType?) {
        _sortType.value = sortType
    }

    private fun sortEvents(events: List<Event>, sortType: SortType): List<Event> {
        return when (sortType) {
            SortType.DATE -> events.sortedBy { it.startTime }
            SortType.DISTANCE -> events.sortedBy { currentLocation.value?.let { it1 ->
                it.locationCoordinates.sphericalDistance(
                    it1
                )
            } }
        }
    }

    data class Filters(
        val startDate: LocalDateTime? = null,
        val endDate: LocalDateTime? = null,
        val category: Category? = null
    )

    sealed class SortType {
        data object DATE : SortType()
        data object DISTANCE : SortType()
    }
}