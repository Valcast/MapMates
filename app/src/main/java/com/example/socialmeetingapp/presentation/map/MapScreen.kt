package com.example.socialmeetingapp.presentation.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
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

    Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
        GoogleMap(modifier = Modifier.matchParentSize(), cameraPositionState = cameraPositionState) {
            if (state is MapState.LocationAvailable) {
                val currentPosition = LatLng(state.location.latitude, state.location.longitude)
                LaunchedEffect(currentPosition) {
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(currentPosition, 15f)
                    markerPositionState.position = currentPosition
                }
                Marker(
                    state = markerPositionState,
                    title = "Current Location",
                    snippet = "Your current location"
                )
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