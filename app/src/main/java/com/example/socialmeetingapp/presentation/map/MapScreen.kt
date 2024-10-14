package com.example.socialmeetingapp.presentation.map

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.socialmeetingapp.domain.event.model.Event
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerInfoWindowContent
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun MapScreen(innerPadding: PaddingValues, goToCreateEventScreen: (latitute: Double, longtitude: Double) -> Unit) {
    val viewModel = hiltViewModel<MapViewModel>()
    val locationState = viewModel.locationState.collectAsState().value
    val eventsState = viewModel.eventsState.collectAsState().value

    val defaultPosition = LatLng(52.237049, 21.017532)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultPosition, 10f)
    }
    val markerPositionState = rememberMarkerState()



    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        Button(onClick = { goToCreateEventScreen(defaultPosition.latitude, defaultPosition.longitude) }) {
            Text(text = "Create Event")
        }
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraPositionState,
            googleMapOptionsFactory = {
                GoogleMapOptions().mapToolbarEnabled(false).zoomControlsEnabled(false)
            },
            onMapLongClick = {
                goToCreateEventScreen(it.latitude, it.longitude)
            }
        ) {

            if (locationState is MapState.LocationAvailable) {
                val currentPosition = LatLng(locationState.location.latitude, locationState.location.longitude)
                LaunchedEffect(currentPosition) {
                    markerPositionState.position = currentPosition
                }

            }

            if (eventsState is MapState.EventsAvailable) {
                for (event in eventsState.events) {
                    CustomMarker(
                        event = event
                    )

                }
            }
        }

        when (locationState) {
            is MapState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is MapState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = locationState.message, color = Color.Red)
                }
            }

            else -> {
                return
            }
        }
    }
}

@Composable
fun CustomMarker(event: Event) {

    val markerState = rememberMarkerState(position = event.location)

    MarkerInfoWindowContent(
        state = markerState,
        title = event.title,
        snippet = "Your current location"
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = event.title, style = MaterialTheme.typography.titleLarge)
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Default.AccountBox,
                        contentDescription = "Creator Info"
                    )
                }
            }
            Text(text = event.description, style = MaterialTheme.typography.bodyMedium)

            Row {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Default.AccountBox,
                        contentDescription = "Creator Info"
                    )
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Default.AccountBox,
                        contentDescription = "Creator Info"
                    )
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Default.AccountBox,
                        contentDescription = "Creator Info"
                    )
                }
            }
            Button(onClick = { /*TODO*/ }) {
                Text(text = "JOIN")
            }
        }
    }
}