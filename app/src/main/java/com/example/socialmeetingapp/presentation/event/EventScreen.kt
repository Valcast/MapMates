package com.example.socialmeetingapp.presentation.event

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.socialmeetingapp.domain.model.Result
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventScreen(
    state: EventState,
    onJoinEvent: () -> Unit,
    onBack: () -> Unit,
    onGoToAuthor: (authorId: String) -> Unit,
    onDeleteEvent: () -> Unit,
    onLeaveEvent: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }

    when (state) {
        is EventState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is EventState.Content -> {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {

                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .weight(1f)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { onBack() },
                            shape = RoundedCornerShape(10.dp),
                        ) {
                            Text(text = "Back")
                        }


                        Box {
                            IconButton(
                                onClick = { expanded = !expanded },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.MoreVert,
                                    contentDescription = "Edit Profile",
                                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                )
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }) {
                                if (state.currentUser.id == state.event.author.id) {
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                "Delete Event",
                                                style = MaterialTheme.typography.titleSmall,
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        },
                                        onClick = onDeleteEvent,
                                    )
                                } else {
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                "Leave Event",
                                                style = MaterialTheme.typography.titleSmall,
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        },
                                        onClick = onLeaveEvent,
                                    )

                                }
                            }

                        }
                    }

                    Text(
                        text = state.event.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "${state.event.startTime.dayOfMonth} ${
                            state.event.startTime.month.name
                                .lowercase(Locale.ROOT)
                                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                        } ${state.event.startTime.year}",
                        color = MaterialTheme.colorScheme.onBackground.copy(0.5f),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .background(
                                    MaterialTheme.colorScheme.secondaryContainer,
                                    RoundedCornerShape(10.dp)
                                )
                                .padding(10.dp)
                        )
                        Column {
                            Text(
                                text = state.event.startTime.dayOfWeek.name.lowercase(Locale.ROOT)
                                    .replaceFirstChar {
                                        if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
                                    },
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = String.format(
                                    Locale.ROOT,
                                    "%02d:%02d - %02d:%02d",
                                    state.event.startTime.hour,
                                    state.event.startTime.minute,
                                    state.event.endTime.hour,
                                    state.event.endTime.minute
                                ),
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.titleMedium
                            )

                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .background(
                                    MaterialTheme.colorScheme.secondaryContainer,
                                    RoundedCornerShape(10.dp)
                                )
                                .padding(10.dp)
                        )
                        Text(
                            text = state.event.locationAddress!!,
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleMedium,
                            minLines = 2,
                            maxLines = 2,
                            modifier = Modifier.fillMaxWidth(0.9f)
                        )

                    }



                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Hosted By",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )


                        Button(
                            onClick = { onGoToAuthor(state.event.author.id) },
                            colors = ButtonColors(
                                containerColor = MaterialTheme.colorScheme.background,
                                contentColor = MaterialTheme.colorScheme.onBackground.copy(0.5f),
                                disabledContainerColor = MaterialTheme.colorScheme.background,
                                disabledContentColor = MaterialTheme.colorScheme.background
                            ),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = state.event.author.username,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(end = 8.dp)
                                )

                                AsyncImage(
                                    model = state.event.author.profilePictureUri,
                                    contentDescription = "Author Profile Picture",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                )
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "People Going",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "(${state.event.participants.size} / ${state.event.maxParticipants})",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                modifier = Modifier.padding(start = 4.dp)
                            )

                        }
                        Button(
                            onClick = {
                                if (state.event.participants.isNotEmpty()) showBottomSheet = true
                            },
                            colors = ButtonColors(
                                containerColor = MaterialTheme.colorScheme.background,
                                contentColor = MaterialTheme.colorScheme.primary,
                                disabledContainerColor = MaterialTheme.colorScheme.background,
                                disabledContentColor = MaterialTheme.colorScheme.background
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End
                            ) {
                                state.event.participants.forEachIndexed { index, participant ->
                                    if (index < 3) {
                                        AsyncImage(
                                            model = participant.profilePictureUri,
                                            contentDescription = "Participant Profile Picture",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Text(
                        text = "About Event",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = state.event.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )


                    GoogleMap(
                        cameraPositionState = rememberCameraPositionState {
                            position =
                                CameraPosition.fromLatLngZoom(state.event.locationCoordinates, 15f)
                        },
                        onMapClick = {
                        },
                        uiSettings = MapUiSettings(
                            zoomControlsEnabled = false,
                            zoomGesturesEnabled = false,
                            scrollGesturesEnabled = false,
                            scrollGesturesEnabledDuringRotateOrZoom = false,
                            mapToolbarEnabled = false,
                            rotationGesturesEnabled = false,
                            tiltGesturesEnabled = false,
                            compassEnabled = false,
                            indoorLevelPickerEnabled = false,
                            myLocationButtonEnabled = false,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp)
                            .height(200.dp)
                            .clip(RoundedCornerShape(10.dp))
                    ) {
                        Marker(
                            state = rememberMarkerState(
                                position = state.event.locationCoordinates
                            ),
                            onClick = {
                                true
                            }
                        )
                    }
                }


                ExtendedFloatingActionButton(
                    onClick = onJoinEvent,
                    modifier = Modifier
                        .align(Alignment.End)
                        .fillMaxWidth(),


                    ) {
                    Text(text = if (state.currentUser.id == state.event.author.id) "You joined this event" else "Join Event")
                }
            }

            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showBottomSheet = false
                    },
                    sheetState = sheetState
                ) {
                    state.event.participants.forEach { participant ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = participant.profilePictureUri,
                                contentDescription = "Participant Profile Picture",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(16.dp))
                            )
                            Text(
                                text = participant.username,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(start = 8.dp)
                            )

                            Text(
                                text = (Clock.System.now()
                                    .toLocalDateTime(TimeZone.currentSystemDefault()).year - participant.dateOfBirth.year).toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }


        }

        is EventState.Error -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
           Text(text = state.message)
        }
    }
}

