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
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerInfoWindowContent
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun MapScreen(innerPadding: PaddingValues) {
    val viewModel = hiltViewModel<MapViewModel>()
    val state = viewModel.state.collectAsState().value

    val defaultPosition = LatLng(1.3521, 103.8198)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultPosition, 10f)
    }
    val markerPositionState = rememberMarkerState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraPositionState,
            googleMapOptionsFactory = {
                GoogleMapOptions().mapToolbarEnabled(false).zoomControlsEnabled(false)
            }
        ) {
            if (state is MapState.LocationAvailable) {
                val currentPosition = LatLng(state.location.latitude, state.location.longitude)
                LaunchedEffect(currentPosition) {
                    cameraPositionState.position =
                        CameraPosition.fromLatLngZoom(currentPosition, 15f)
                    markerPositionState.position = currentPosition
                }
                MarkerInfoWindowContent(
                    state = markerPositionState,
                    title = "Current Location",
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
                            Text(text = "Event Title", style = MaterialTheme.typography.titleLarge)
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(
                                    imageVector = Icons.Default.AccountBox,
                                    contentDescription = "Creator Info"
                                )
                            }
                        }
                        Text(text = "Description", style = MaterialTheme.typography.bodyMedium)

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
        }

        when (state) {
            is MapState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is MapState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.message, color = Color.Red)
                }
            }

            else -> {
                return
            }
        }
    }
}