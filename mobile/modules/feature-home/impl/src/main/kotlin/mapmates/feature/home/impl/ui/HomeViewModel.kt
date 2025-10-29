package mapmates.feature.home.impl.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mapmates.core.location.api.LocationResult
import mapmates.core.location.api.interactor.GetCurrentLocationInteractor
import mapmates.feature.event.api.GetEventsResult
import mapmates.feature.event.api.filters.Filter
import mapmates.feature.event.api.interactor.GetAllEventsPreviewInteractor
import kotlin.reflect.KClass
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@HiltViewModel(assistedFactory = HomeViewModel.Factory::class)
internal class HomeViewModel @AssistedInject constructor(
    @Assisted val initialLocation: LatLng?,
    private val getCurrentLocationInteractor: GetCurrentLocationInteractor,
    private val getAllEventsPreviewInteractor: GetAllEventsPreviewInteractor,
) : ViewModel() {

    private val _state = MutableStateFlow(
        HomeState(
            currentLocation = initialLocation ?: LatLng(0.0, 0.0)
        )
    )
    val state = _state.asStateFlow()

    init {
        updateCurrentLocation()
        fetchEvents()
    }

    fun onListViewToggle() = _state.update { state -> state.copy(isListView = !state.isListView) }

    fun onFilterScreenOpen() = _state.update { state -> state.copy(isFilterScreenVisible = true) }

    fun onFilterReset(filter: KClass<out Filter>) = _state.update { state ->
        val newFilters = state.filters.filterNot { it::class == filter }.toSet()
        state.copy(filters = newFilters)
    }

    fun onEventCardClick(eventId: String) {
        TODO("Handle event card click")
    }

    fun onMapClick(coordinates: LatLng?) = _state.update { state ->
        state.copy(eventCluster = emptyList())
    }

    fun onClusterClick(eventMarkers: List<EventMarker>) = _state.update { state ->
        val eventsInCluster = state.events.filter { event ->
            eventMarkers.any { eventMarker -> eventMarker.title == event.title }
        }
        state.copy(eventCluster = eventsInCluster, selectedEvent = null)
    }

    fun onClusterItemClick(eventMarker: EventMarker) = _state.update { state ->
        val event = state.events.first { event -> eventMarker.title == event.title }
        state.copy(selectedEvent = event, eventCluster = emptyList())
    }

    fun onCenterToCurrentLocation() = updateCurrentLocation()

    private fun updateCurrentLocation() = viewModelScope.launch {
        when (val result = getCurrentLocationInteractor()) {
            is LocationResult.Success -> {
                val latLng = LatLng(result.latitude, result.longitude)
                _state.update { currentState ->
                    currentState.copy(currentLocation = latLng)
                }
            }

            is LocationResult.Failure -> {
                // SnackbarManager will handle showing error messages
            }
        }
    }

    private fun fetchEvents() {
        _state.update { state -> state.copy(isLoading = true) }
        viewModelScope.launch {
            when (val result = getAllEventsPreviewInteractor(state.value.filters)) {
                is GetEventsResult.Failure -> _state.update { state ->
                    state.copy(isLoading = false)
                    // SnackbarManager will handle showing error messages
                }

                is GetEventsResult.Success -> {
                    _state.update { state ->
                        state.copy(
                            isLoading = false,
                            events = result.events
                        )
                    }
                }
            }
        }
    }

    @AssistedFactory
    internal interface Factory {
        fun create(locationCoordinates: LatLng?): HomeViewModel
    }
}