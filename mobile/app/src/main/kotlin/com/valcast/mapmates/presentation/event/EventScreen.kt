package com.valcast.mapmates.presentation.event

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import com.valcast.mapmates.R
import com.valcast.mapmates.domain.model.Category
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.Locale
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun EventScreen(
    state: EventState,
    isRefresh: Boolean,
    onJoinEvent: () -> Unit,
    onBack: () -> Unit,
    onGoToAuthor: (authorId: String) -> Unit,
    onGoToEditEvent: () -> Unit,
    onLeaveEvent: () -> Unit,
    onSendJoinRequest: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var eventActionsExpanded by remember { mutableStateOf(false) }

    val context = LocalContext.current


    when (state) {
        is EventState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is EventState.Content -> {
            val isEventEnded = state.event.endTime < Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
            val isEventHappeningNow = state.event.startTime < Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())

            val navigateInMapIntent = remember {
                Intent().apply {
                    action = Intent.ACTION_VIEW
                    data = state.event.locationCoordinates?.let {
                        "google.navigation:q=${it.latitude},${it.longitude}"
                    }?.toUri()
                }
            }

            val goToMeetingLink = remember {
                Intent(
                    Intent.ACTION_VIEW, state.event.meetingLink?.toUri()
                )
            }

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .weight(1f)
                        .padding(bottom = 40.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth(),
                    ) {
                        FilledTonalButton(
                            onClick = onBack,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.align(Alignment.CenterStart)
                        ) {
                            Text(text = "Back")
                        }

                        this@Column.AnimatedVisibility(
                            visible = isRefresh,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically(),
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(100))
                                    .padding(8.dp)
                            )
                        }


                        Box(
                            modifier = Modifier.align(Alignment.CenterEnd)
                        )
                        {
                            IconButton(
                                onClick = { eventActionsExpanded = !eventActionsExpanded },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.MoreVert,
                                    contentDescription = "Edit Profile",
                                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                )
                            }
                            DropdownMenu(
                                expanded = eventActionsExpanded,
                                onDismissRequest = { eventActionsExpanded = false }) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            "Leave Event",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    },
                                    onClick = {
                                        onLeaveEvent()
                                        eventActionsExpanded = false
                                    },
                                    enabled = state.event.participants.any { it.id == state.currentUser.id } && !isEventEnded && !isEventHappeningNow
                                )
                                if (state.currentUser.id == state.event.author.id) {
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                "Edit Event",
                                                style = MaterialTheme.typography.titleSmall,
                                            )
                                        },
                                        onClick = onGoToEditEvent
                                    )
                                }
                            }
                        }
                    }

                    if (isEventEnded) {
                        Text(
                            text = "Event has ended",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    } else if (isEventHappeningNow) {
                        Text(
                            text = "This event is happening right now! You cannot join anymore.",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(
                                when (state.event.category) {
                                    Category.CINEMA -> R.drawable.cinema
                                    Category.CONCERT -> R.drawable.concert
                                    Category.CONFERENCE -> R.drawable.conference
                                    Category.HOUSEPARTY -> R.drawable.houseparty
                                    Category.MEETUP -> R.drawable.meetup
                                    Category.THEATER -> R.drawable.theater
                                    Category.WEBINAR -> R.drawable.webinar
                                }
                            ),
                            contentDescription = "Event Category Icon",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(48.dp),
                        )
                        Text(
                            text = "${state.event.title} ${if (state.event.isPrivate) "(Private)" else ""}",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Max)
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Card {
                            Text(
                                text = "Date & Time",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(
                                    start = 16.dp, top = 22.dp, end = 16.dp, bottom = 8.dp
                                )
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Text(
                                text = "${state.event.startTime.day}  ${state.event.startTime.year}",
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                    0.5f
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(
                                    start = 16.dp, end = 16.dp
                                )
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
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                    0.5f
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(
                                    start = 16.dp, bottom = 16.dp, end = 16.dp
                                )
                            )


                        }

                        Card(
                            modifier = Modifier.padding(start = 16.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(
                                        start = 16.dp, top = 16.dp, bottom = 8.dp, end = 16.dp
                                    )
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = "Location",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onBackground
                                )

                                FilledIconButton(
                                    onClick = {
                                        context.startActivity(navigateInMapIntent)
                                    },
                                    modifier = Modifier.size(32.dp),
                                    enabled = !state.event.isOnline
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.navigate),
                                        contentDescription = "Location",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            if (state.event.isOnline) {
                                Text(
                                    text = "Online",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.5f),
                                    style = MaterialTheme.typography.bodySmall,
                                    minLines = 2,
                                    maxLines = 2,
                                    modifier = Modifier.padding(
                                        start = 16.dp, bottom = 16.dp, end = 16.dp
                                    )
                                )
                            } else {
                                Text(
                                    text = state.event.locationAddress!!,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.5f),
                                    style = MaterialTheme.typography.bodySmall,
                                    minLines = 2,
                                    maxLines = 2,
                                    modifier = Modifier.padding(
                                        start = 16.dp, bottom = 16.dp, end = 16.dp
                                    )
                                )
                            }
                        }
                    }

                    Text(
                        text = "About ${state.event.title}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = if (state.event.description.isEmpty()) "No description" else state.event.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(0.5f),
                    )

                    if (state.event.isOnline) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text(
                                text = "Meeting Link:",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                            TextButton(
                                onClick = {
                                    context.startActivity(goToMeetingLink)
                                }, modifier = Modifier.padding(start = 16.dp)
                            ) {
                                Text(
                                    text = state.event.meetingLink!!,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1
                                )
                            }
                        }

                    }

                    OutlinedCard(
                        onClick = {
                            onGoToAuthor(state.event.author.id)
                        }, modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            AsyncImage(
                                model = state.event.author.profilePictureUri,
                                contentDescription = "Author Profile Picture",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(16.dp))
                            )

                            Column(
                                modifier = Modifier
                                    .padding(start = 16.dp)
                                    .weight(1f)
                            ) {
                                Text(
                                    text = state.event.author.username,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                )
                                Text(
                                    text = (Clock.System.now()
                                        .toLocalDateTime(TimeZone.currentSystemDefault()).year - state.event.author.dateOfBirth.year).toString() + " years old",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onBackground.copy(0.8f),
                                )
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = "People Going",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = if (state.event.maxParticipants == Int.MAX_VALUE) "" else "(${state.event.participants.size}/${state.event.maxParticipants})",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            modifier = Modifier.padding(start = 4.dp)
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.clickable {
                                if (state.event.participants.isNotEmpty()) showBottomSheet =
                                    true
                            }) {
                            state.event.participants.forEachIndexed { index, participant ->
                                if (index < 3) {
                                    AsyncImage(
                                        model = participant.profilePictureUri,
                                        contentDescription = "Participant Profile Picture",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(32.dp)
                                            .offset(
                                                if (index == 0) 0.dp else (-8).dp * index, 0.dp
                                            )
                                            .clip(RoundedCornerShape(16.dp))
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .offset(
                                        when {
                                            state.event.participants.isEmpty() -> (0).dp
                                            state.event.participants.size == 1 -> (-8).dp
                                            state.event.participants.size > 1 -> (-16).dp
                                            else -> 0.dp
                                        }, 0.dp
                                    )
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.primary)
                            ) {
                                Text(
                                    text = "+${
                                        when {
                                            state.event.participants.size < 3 -> "0"
                                            else -> state.event.participants.size - 2
                                        }
                                    }",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.background,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    }




                    if (!state.event.isOnline) {
                        GoogleMap(
                            cameraPositionState = rememberCameraPositionState {
                                position = CameraPosition.fromLatLngZoom(
                                    state.event.locationCoordinates!!, 15f
                                )
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
                                state = rememberUpdatedMarkerState(
                                    position = state.event.locationCoordinates!!
                                )
                            )
                        }
                    }
                }


                ElevatedButton(
                    onClick = {
                        if (state.event.isPrivate) {
                            onSendJoinRequest()
                        } else {
                            onJoinEvent()
                        }
                    },
                    enabled = when {
                        state.event.participants.any { it.id == state.currentUser.id } -> false
                        state.event.joinRequests.any { it.id == state.currentUser.id } -> false
                        state.event.participants.size >= state.event.maxParticipants -> false
                        state.currentUser.id == state.event.author.id -> false
                        isEventEnded -> false
                        else -> true
                    },
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .align(Alignment.End)
                        .fillMaxWidth(),

                    ) {
                    Text(
                        text = when {
                            isEventEnded -> "Event has ended"
                            isEventHappeningNow -> "Event is happening right now"
                            state.event.participants.any { it.id == state.currentUser.id } -> stringResource(
                                R.string.event_joined
                            )

                            state.event.joinRequests.any { it.id == state.currentUser.id } -> stringResource(
                                R.string.event_join_request_sent
                            )

                            state.event.participants.size >= state.event.maxParticipants -> stringResource(
                                R.string.event_full
                            )

                            state.currentUser.id == state.event.author.id -> stringResource(R.string.event_host)
                            state.event.isPrivate -> stringResource(R.string.event_join_request)

                            else -> stringResource(R.string.event_join)
                        }, modifier = Modifier.padding(vertical = 8.dp))
                }
            }

            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showBottomSheet = false
                    }, sheetState = sheetState
                ) {
                    state.event.participants.forEach { participant ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(MaterialTheme.shapes.small)
                                    .clickable {
                                    }
                                    .padding(4.dp)) {
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

                            var participantActionsExpanded by remember { mutableStateOf(false) }

                            if (state.currentUser.id == state.event.author.id) {
                                Box {
                                    IconButton(
                                        onClick = {
                                            participantActionsExpanded = !participantActionsExpanded
                                        }, modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.MoreVert,
                                            contentDescription = "Edit Profile",
                                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                        )
                                    }
                                }

                            }


                        }
                    }
                }
            }


        }

        is EventState.Error -> Box(
            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            Text(text = state.message)
        }
    }
}

