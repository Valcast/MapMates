package mapmates.feature.home.impl.ui

import com.google.android.gms.maps.model.LatLng
import mapmates.feature.event.api.Event
import mapmates.feature.event.api.filters.Filter

data class HomeState(
    val isLoading: Boolean = false,
    val currentLocation: LatLng,
    val events: List<Event> = emptyList(),
    val selectedEvent: Event? = null,
    val eventCluster: List<Event> = emptyList(),
    val filters: Set<Filter> = setOf(),
    val isFilterScreenVisible: Boolean = false,
    val isListView: Boolean = false,
)
