package com.example.socialmeetingapp.presentation.event.createevent

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconToggleButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
fun EventLocationScreen(
    event: Event, onUpdateLocation: (LatLng) -> Unit,
    onUpdateIsOnline: (Boolean) -> Unit,
    onUpdateMeetingLink: (String) -> Unit,
) {
    var isLocationPickerVisible by remember { mutableStateOf(false) }

    Column {
        Text(
            text = "Where will it happen?",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        AnimatedVisibility(
            visible = !event.isOnline,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Column {
                Text(
                    text = "Location",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = event.locationAddress,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Button(
                    onClick = { isLocationPickerVisible = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = "Pick a location",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onUpdateIsOnline(!event.isOnline)

                }
                .padding(vertical = 16.dp)) {
            Text(
                text = "Is it online?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            OutlinedIconToggleButton(
                checked = event.isOnline,
                onCheckedChange = onUpdateIsOnline,
                modifier = Modifier.size(24.dp)
            ) {
                if (event.isOnline) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = "Selected Sort",
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = event.isOnline, modifier = Modifier.padding(top = 16.dp)
        ) {

            Column {
                Text(
                    text = "Link to the meeting",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = event.meetingLink ?: "",
                    onValueChange = onUpdateMeetingLink,
                    placeholder = {
                        Text(
                            text = "https://meet.google.com/abc-xyz",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(.5F)
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                Text(
                    text = "We accept Google Meet, Teams and other popular video conferencing links.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(.5F)
                )
            }
        }


        if (isLocationPickerVisible) {
            Dialog(
                onDismissRequest = { isLocationPickerVisible = false },
                properties = DialogProperties(
                    dismissOnBackPress = true, dismissOnClickOutside = true
                ),

                ) {

                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp)
                        .height(200.dp)
                ) {
                    GoogleMap(modifier = Modifier.fillMaxWidth(), onMapClick = {
                        onUpdateLocation(it)
                        isLocationPickerVisible = false
                    }, cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(event.locationCoordinates, 10f)
                    })
                }


            }
        }
    }
}