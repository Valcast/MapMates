package com.example.socialmeetingapp.presentation.map

import android.util.Log
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.socialmeetingapp.domain.common.model.Result
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerInfoWindowContent
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.datetime.format
import java.util.Locale

@Composable
fun MapScreen(
    innerPadding: PaddingValues,
    goToCreateEventScreen: (latitute: Double, longtitude: Double) -> Unit,
    navigateToEvent: (eventId: String) -> Unit
) {
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

            if (locationState is Result.Success) {
                val currentPosition =
                    locationState.data?.let { LatLng(it.latitude, locationState.data.longitude) }
                LaunchedEffect(currentPosition) {
                    if (currentPosition != null) {
                        markerPositionState.position = currentPosition
                    }
                }

                Marker(markerPositionState, snippet = "Current Location")

            }

            if (eventsState is Result.Error) {
                Log.e("MapScreen", "Error: ${eventsState.message}")
            }

            if (eventsState is Result.Success) {
                for (event in eventsState.data!!) EventMarker(event, navigateToEvent)
            }
        }

        when (locationState) {
            is Result.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is Result.Error -> {
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

