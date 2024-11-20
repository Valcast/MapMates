package com.example.socialmeetingapp.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.socialmeetingapp.domain.model.Result
import com.example.socialmeetingapp.domain.model.Event
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.ComposeMapColorScheme
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun HomeScreen(eventsResult: Result<List<Event>>, currentLocationResult: Result<LatLng>, onMapLongClick: (LatLng) -> Unit, onEventClick: (String) -> Unit) {

    var isListView by rememberSaveable { mutableStateOf(false) }

    val defaultPosition = LatLng(52.237049, 21.017532)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultPosition, 10f)
    }

    var selectedEventIndex by remember { mutableStateOf<Int?>(null) }
    var selectedCreateEventPosition by remember { mutableStateOf<LatLng?>(null) }


    when (eventsResult) {
        is Result.Success -> {
            val events = eventsResult.data

            Box(
                Modifier
                    .fillMaxSize()) {

                if (isListView) {
                    LazyColumn {
                        items(events.size) { index ->
                            EventCard(events[index], onCardClick = onEventClick)
                        }
                    }
                } else {

                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        googleMapOptionsFactory = {
                            GoogleMapOptions().mapToolbarEnabled(false).zoomControlsEnabled(false)
                        },
                        uiSettings = MapUiSettings(
                            compassEnabled = false,
                            zoomControlsEnabled = false,
                            mapToolbarEnabled = false
                        ),
                        onMapLongClick = {
                            selectedCreateEventPosition = it
                        },
                        onMapClick = {
                            selectedEventIndex = null
                            selectedCreateEventPosition = null
                        },
                        mapColorScheme = ComposeMapColorScheme.FOLLOW_SYSTEM
                    ) {
                        if (currentLocationResult is Result.Success) {
                            Marker(
                                state = rememberMarkerState(position = currentLocationResult.data),

                            )
                        }

                        for (event in events) {
                            Marker(
                                state = rememberMarkerState(position = event.locationCoordinates),
                                onClick = {
                                    selectedEventIndex = events.indexOf(event)
                                    true
                                }
                            )
                        }

                        selectedCreateEventPosition?.let {
                            Marker(
                                state = rememberMarkerState(position = it)
                            )
                        }
                    }
                }

                selectedCreateEventPosition?.let {
                    Card(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .clickable {
                                onMapLongClick(it)
                                selectedCreateEventPosition = null
                            }
                    ) {
                        Row(modifier = Modifier.padding(16.dp)) {

                            Icon(
                                Icons.Filled.Add,
                                contentDescription = "Create Event",
                                modifier = Modifier.padding(end = 4.dp)
                            )
                            Text(text = "Create Event",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                    }
                }


                SmallFloatingActionButton(
                    onClick = { isListView = !isListView },
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                ) {
                    Icon(
                        if (isListView) Icons.Filled.Place else Icons.AutoMirrored.Filled.List,
                        "Map View"
                    )
                }

                if (!isListView) {
                    SmallFloatingActionButton(
                        onClick = {
                            if (currentLocationResult is Result.Success) {
                                cameraPositionState.position = CameraPosition.fromLatLngZoom(
                                    currentLocationResult.data,
                                    15f
                                )
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp),
                    ) {
                        Icon(Icons.Filled.LocationOn, "Move to current position")
                    }
                }

                if (selectedEventIndex != null) {
                    EventCard(events[selectedEventIndex!!], onCardClick = onEventClick)
                }
            }

        }

        is Result.Error -> {
            Text(text = eventsResult.message)
        }
        else -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

