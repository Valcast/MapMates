package com.example.socialmeetingapp.presentation.event.createventflow

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.socialmeetingapp.domain.model.Event
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun EventLocationScreen(event: Event, onUpdateLocation: (LatLng) -> Unit) {

    var isLocationPickerVisible by remember { mutableStateOf(false) }

    Column {
        Text(
            text = "Where will it happen?",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = event.locationAddress ?: "Select a location",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Button(onClick = { isLocationPickerVisible = true }) {
            Text(text = "Pick a location")
        }

        if (isLocationPickerVisible) {
            Dialog(
                onDismissRequest = { isLocationPickerVisible = false },
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                ),

                ) {

                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp)
                        .height(200.dp)
                ) {
                    GoogleMap(
                        modifier = Modifier.fillMaxWidth(),
                        onMapClick = {
                            onUpdateLocation(it)
                            isLocationPickerVisible = false
                        },
                        cameraPositionState = rememberCameraPositionState {
                            position = CameraPosition.fromLatLngZoom(event.locationCoordinates, 10f)
                        }
                    )
                }


            }
        }


    }
}