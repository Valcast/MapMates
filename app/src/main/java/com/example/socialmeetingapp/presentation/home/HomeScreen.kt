package com.example.socialmeetingapp.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.socialmeetingapp.domain.common.model.Result
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun HomeScreen(
    goToCreateEventScreen: (latitute: Double, longtitude: Double) -> Unit,
    navigateToEvent: (eventId: String) -> Unit
) {
    val viewModel = hiltViewModel<HomeViewModel>()
    val eventsResult by viewModel.eventsState.collectAsStateWithLifecycle()
    val currentLocationResult by viewModel.locationState.collectAsStateWithLifecycle()

    var isListView by rememberSaveable { mutableStateOf(false) }

    val defaultPosition = LatLng(0.0, 0.0)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultPosition, 10f)
    }

    LaunchedEffect(Unit) {

        if (currentLocationResult is Result.Success<LatLng>) {
            val currentLocation = (currentLocationResult as Result.Success<LatLng>).data
            cameraPositionState.position = CameraPosition.fromLatLngZoom(currentLocation, 12f)
        }
    }



    when (eventsResult) {
        is Result.Success -> {
            val events = (eventsResult as Result.Success).data

            Box(
                Modifier
                    .fillMaxSize()) {

                if (isListView) {
                    LazyColumn {
                        items(events.size) { index ->
                            EventCard(events[index], navigateToEvent)
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
                        onMapLongClick = {}
                    ) {
                        if (currentLocationResult is Result.Success) {
                            Marker(
                                state = rememberMarkerState(position = (currentLocationResult as Result.Success<LatLng>).data)
                            )
                        }

                        for (event in events) {
                            EventMarker(event, navigateToEvent)
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
                                    (currentLocationResult as Result.Success<LatLng>).data,
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
            }

        }

        is Result.Error -> TODO()
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

