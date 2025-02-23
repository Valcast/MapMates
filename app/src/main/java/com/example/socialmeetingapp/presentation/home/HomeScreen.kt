package com.example.socialmeetingapp.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.socialmeetingapp.domain.model.Result
import com.example.socialmeetingapp.domain.model.Theme
import com.example.socialmeetingapp.presentation.components.EventCard
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapColorScheme
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    state: HomeState,
    theme: Theme,
    locationCoordinates: LatLng? = null,
    onMapLongClick: (LatLng) -> Unit,
    onEventClick: (String) -> Unit,
) {
    var isListView by rememberSaveable { mutableStateOf(false) }
    var isRequestPermissionDialogVisible by remember { mutableStateOf(false) }
    var selectedEventIndex by remember { mutableStateOf<Int?>(null) }
    var selectedCreateEventPosition by remember { mutableStateOf<LatLng?>(null) }
    var shouldShowEventDialog by rememberSaveable { mutableStateOf(true) }

    val locationPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    LaunchedEffect(locationPermissions) {
        if (locationPermissions.allPermissionsGranted) {
            isRequestPermissionDialogVisible = false
        }
    }


    when (state) {
        is HomeState.Content -> {
            val startPosition = locationCoordinates
                ?: if (state.location is Result.Success) state.location.data else LatLng(
                    52.237049,
                    21.017532
                )

            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(
                    startPosition,
                    if (locationCoordinates != null) 15f else 10f
                )
            }

            Box(
                Modifier
                    .fillMaxSize()
            ) {

                if (isListView) {
                    LazyColumn {
                        items(state.events.size) { index ->
                            EventCard(
                                state.events[index],
                                onCardClick = onEventClick,
                                modifier = Modifier.padding(top = 16.dp, start = 8.dp, end = 8.dp)
                            )
                        }
                    }
                } else {

                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        googleMapOptionsFactory = {
                            GoogleMapOptions().mapToolbarEnabled(false).zoomControlsEnabled(false).mapColorScheme(
                                when (theme) {
                                    Theme.LIGHT -> MapColorScheme.LIGHT
                                    Theme.DARK -> MapColorScheme.DARK
                                    Theme.SYSTEM -> MapColorScheme.FOLLOW_SYSTEM
                                }
                            )
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
                    ) {
                        if (state.location is Result.Success) {
                            Marker(
                                state = rememberMarkerState(position = state.location.data),

                                )
                        }

                        for (event in state.events) {
                            Marker(
                                state = rememberMarkerState(position = event.locationCoordinates),
                                onClick = {
                                    selectedEventIndex = state.events.indexOf(event)
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

                if (selectedCreateEventPosition != null) {
                    shouldShowEventDialog = false
                    Card(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .clickable {
                                onMapLongClick(selectedCreateEventPosition!!)
                                selectedCreateEventPosition = null
                            }
                    ) {
                        Row(modifier = Modifier.padding(16.dp)) {

                            Icon(
                                Icons.Filled.Add,
                                contentDescription = "Create Event",
                                modifier = Modifier.padding(end = 4.dp)
                            )
                            Text(
                                text = "Create Event",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                    }
                } else {

                    if (shouldShowEventDialog) {
                        Card(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(16.dp)
                        ) {
                            Row (modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Long press on the map to create an event",
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(end = 8.dp)
                                )

                                IconButton(
                                    onClick = {
                                        shouldShowEventDialog = false
                                    }
                                ) {
                                    Icon(
                                        Icons.Filled.Close,
                                        contentDescription = "Close"
                                    )
                                }
                            }

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
                            if (state.location is Result.Success) {
                                cameraPositionState.position = CameraPosition.fromLatLngZoom(
                                    state.location.data,
                                    15f
                                )
                            } else {
                                isRequestPermissionDialogVisible = true
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
                    EventCard(
                        state.events[selectedEventIndex!!],
                        onCardClick = onEventClick,
                        modifier = Modifier.padding(top = 16.dp, start = 8.dp, end = 8.dp)
                    )
                }

                if (isRequestPermissionDialogVisible) {
                    BasicAlertDialog(onDismissRequest = {
                        isRequestPermissionDialogVisible = false

                    }) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.background)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "MapMates needs your location permission",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "We need your location to show you events near you",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(top = 16.dp),
                                textAlign = TextAlign.Center
                            )
                            Row(
                                horizontalArrangement = Arrangement.SpaceAround,
                                modifier = Modifier
                                    .padding(top = 32.dp)
                                    .fillMaxWidth()
                            ) {
                                OutlinedButton(onClick = {
                                    isRequestPermissionDialogVisible = false
                                }) {
                                    Text(
                                        text = "Skip",
                                        color = MaterialTheme.colorScheme.onBackground,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

                                Button(onClick = {locationPermissions.launchMultiplePermissionRequest()}) {
                                    Text(

                                        text = "Allow",
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

                            }
                        }


                    }
                }
            }

        }

        is HomeState.Error -> {
            Text(text = state.message)
        }

        is HomeState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}