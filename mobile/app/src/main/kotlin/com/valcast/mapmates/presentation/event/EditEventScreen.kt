package com.valcast.mapmates.presentation.event

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedIconToggleButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import com.valcast.mapmates.R
import com.valcast.mapmates.domain.model.Category
import com.valcast.mapmates.domain.model.ChatRoom
import com.valcast.mapmates.domain.model.Event
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun EditEventScreen(
    event: Event?,
    chatRoom: ChatRoom?,
    newEventDescription: String,
    newChatRoom: ChatRoom = ChatRoom.EMPTY,
    newMeetingLink: String,
    onBack: () -> Unit,
    onUpdateEventDescription: (String) -> Unit,
    onSaveEventDescription: () -> Unit,
    onUpdateMeetingLink: (String) -> Unit,
    onSaveMeetingLink: () -> Unit,
    onRemoveParticipant: (String) -> Unit,
    onNavigateToChatRoom: (String) -> Unit,
    onUpdateNewChatRoomName: (String) -> Unit,
    onUpdateNewChatRoomAuthorOnlyWrite: (Boolean) -> Unit,
    onCreateNewChatRoom: () -> Unit,
    onDeleteEvent: () -> Unit,
) {
    var showDeleteEventDialog by remember { mutableStateOf(false) }
    var showDescriptionEditDialog by remember { mutableStateOf(false) }
    var showMeetingLinkEditDialog by remember { mutableStateOf(false) }
    var showCreateChatRoomDialog by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    var showPeopleGoingBottomSheet by remember { mutableStateOf(false) }

    if (event == null) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }

        return
    }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {

            FilledTonalButton(
                onClick = onBack,
                shape = RoundedCornerShape(10.dp),
            ) {
                Text(
                    text = "Back", style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = "Edit Event",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }


        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 16.dp)
        ) {
            Icon(
                painter = painterResource(
                    when (event.category) {
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
                text = "${event.title} ${if (event.isPrivate) "(Private)" else ""}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Button(
            onClick = { showDescriptionEditDialog = true },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(
                text = "Edit description", style = MaterialTheme.typography.bodyMedium
            )
        }

        if (event.isOnline) {
            Button(
                onClick = { showMeetingLinkEditDialog = true },
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text(
                    text = "Edit meeting link", style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Row(
            modifier = Modifier
                .height(IntrinsicSize.Max)
                .padding(top = 16.dp)
        ) {
            Card(onClick = {
                if (event.participants.isNotEmpty()) showPeopleGoingBottomSheet = true
            }) {
                Text(
                    text = "People Going",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(
                        top = 8.dp,
                        start = 16.dp,
                        bottom = 16.dp,
                        end = 16.dp
                    )
                ) {
                    event.participants.forEachIndexed { index, participant ->
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
                                    event.participants.isEmpty() -> (0).dp
                                    event.participants.size == 1 -> (-8).dp
                                    event.participants.size > 1 -> (-16).dp
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
                                    event.participants.size < 3 -> "0"
                                    else -> event.participants.size - 2
                                }
                            }",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.background,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }

            Card(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Chat Room",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = chatRoom?.name ?: "No chat room",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(.5F),
                    modifier = Modifier.padding(
                        start = 16.dp,
                        top = 8.dp,
                        end = 16.dp,
                    )
                )

                if (chatRoom != null) {

                    Text(
                        text = "Open Chat Room",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(
                                start = 16.dp,
                                top = 8.dp,
                                end = 16.dp,
                                bottom = 16.dp
                            )
                            .clickable(onClickLabel = "Open Chat Room") {
                                onNavigateToChatRoom(chatRoom.id)
                            }
                    )

                } else {
                    Text(
                        text = " + Create Chat Room",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(
                                start = 16.dp,
                                top = 8.dp,
                                end = 16.dp,
                                bottom = 16.dp
                            )
                            .clickable {
                                showCreateChatRoomDialog = true
                            }
                    )

                }


            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { showDeleteEventDialog = true },
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Delete Event", style = MaterialTheme.typography.bodyMedium
            )

        }
    }

    if (showDescriptionEditDialog) {
        Dialog(
            onDismissRequest = { showDescriptionEditDialog = false }) {
            Column(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Edit description",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                OutlinedTextField(
                    value = newEventDescription, onValueChange = {
                        if (it.length <= 400) {
                            onUpdateEventDescription(it)
                        }
                    }, minLines = 3, textStyle = MaterialTheme.typography.bodySmall, placeholder = {
                        Text(
                            text = "Add a description to encourage people to join your event.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(.5F)
                        )
                    }, trailingIcon = {
                        Text(
                            text = "${newEventDescription.length}/400",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(.5F)
                        )
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )

                Button(
                    onClick = {
                        onSaveEventDescription()
                        showDescriptionEditDialog = false
                    },
                    enabled = newEventDescription.trim() != event.description,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text(
                        text = "Save Description",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }

    if (showMeetingLinkEditDialog) {
        Dialog(
            onDismissRequest = { showMeetingLinkEditDialog = false }) {
            Column(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Link to the meeting",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = newMeetingLink,
                    onValueChange = onUpdateMeetingLink,
                    placeholder = {
                        Text(
                            text = "https://meet.google.com/abc-xyz",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(.5F)
                        )
                    },
                    textStyle = MaterialTheme.typography.bodySmall,
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

                Button(
                    onClick = {
                        onSaveMeetingLink()
                        showMeetingLinkEditDialog = false
                    },
                    enabled = newEventDescription.trim() != event.meetingLink && isValidLink(
                        newMeetingLink
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text(
                        text = "Save meeting link",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }

    if (showPeopleGoingBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showPeopleGoingBottomSheet = false
            }, sheetState = sheetState
        ) {
            event.participants.forEach { participant ->
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


                        DropdownMenu(
                            expanded = participantActionsExpanded,
                            onDismissRequest = { participantActionsExpanded = false }) {

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "Remove from event",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                },
                                onClick = {
                                    onRemoveParticipant(participant.id)
                                    showPeopleGoingBottomSheet = false
                                },
                            )
                        }


                    }


                }
            }
        }
    }

    if (showCreateChatRoomDialog) {

        Dialog(
            onDismissRequest = { showCreateChatRoomDialog = false }) {
            Column(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Create new chat room",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Title",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = newChatRoom.name, onValueChange = {
                        if (it.length <= 30) {
                            onUpdateNewChatRoomName(it)
                        }
                    }, singleLine = true, trailingIcon = {
                        Text(
                            text = "${newChatRoom.name.length}/30",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(.5F)
                        )
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onUpdateNewChatRoomAuthorOnlyWrite(!newChatRoom.authorOnlyWrite)
                        }
                        .padding(vertical = 16.dp)) {
                    Text(
                        text = "Should only author write?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    OutlinedIconToggleButton(
                        checked = newChatRoom.authorOnlyWrite,
                        onCheckedChange = {
                            onUpdateNewChatRoomAuthorOnlyWrite(it)
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        if (newChatRoom.authorOnlyWrite) {
                            Icon(
                                Icons.Filled.Check,
                                contentDescription = "Selected Sort",
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        onCreateNewChatRoom()
                        showCreateChatRoomDialog = false
                    },
                    enabled = newChatRoom.name.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text(
                        text = "Create Chat Room",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }

    if (showDeleteEventDialog) {
        Dialog(
            onDismissRequest = { showDeleteEventDialog = false }) {
            Column(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Are you sure you want to delete this event?",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "This action cannot be undone.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = buildAnnotatedString {
                        append("To confirm, type the event title '")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(event.title)
                        }
                        append("' below.")
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                var deleteConfirmation by remember { mutableStateOf("") }

                OutlinedTextField(
                    value = deleteConfirmation,
                    onValueChange = { deleteConfirmation = it },
                    textStyle = MaterialTheme.typography.bodySmall,
                    placeholder = {
                        Text(
                            text = event.title,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = onDeleteEvent,
                    enabled = deleteConfirmation == event.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text(
                        text = "Delete Event",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

private fun isValidLink(link: String): Boolean {
    val googleMeetRegex = "^(http(s)://)?meet\\.google\\.com/\\S*$"
    val teamsRegex = "^(http(s)://)?teams\\.microsoft\\.com/\\S*$"
    val zoomRegex = "^(http(s)://)?(us0[2-9]web\\.zoom\\.us|zoom\\.us|\\w+\\.zoom\\.us)/j/\\S*$"
    val skypeRegex = "^(http(s)://)?join\\.skype\\.com/\\S*$"
    val discordRegex = "^(http(s)://)?discord\\.com/\\S*$"

    return link.matches(googleMeetRegex.toRegex()) || link.matches(teamsRegex.toRegex()) || link.matches(
        zoomRegex.toRegex()
    ) || link.matches(skypeRegex.toRegex()) || link.matches(discordRegex.toRegex())
}