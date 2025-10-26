package com.valcast.mapmates.presentation.event.createevent

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconToggleButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.valcast.mapmates.domain.model.ChatRoom

@Composable
fun EventChatScreen(
    chatRoom: ChatRoom?,
    onChatRoomShouldCreate: (Boolean) -> Unit,
    onChatRoomNameChange: (String) -> Unit,
    onChatRoomAuthorOnlyWriteChange: (Boolean) -> Unit
) {

    Column {
        Text(
            text = "Do you want to create chat for this event?",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onChatRoomShouldCreate(chatRoom == null)

                }
                .padding(vertical = 16.dp)
        ) {
            Text(
                text = "Create Chat Room?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            OutlinedIconToggleButton(
                checked = chatRoom != null,
                onCheckedChange = { onChatRoomShouldCreate(it) },
                modifier = Modifier.size(24.dp)
            ) {
                if (chatRoom != null) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = "Selected Sort",
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = chatRoom != null, modifier = Modifier.padding(top = 16.dp)
        ) {
            chatRoom?.let {
                Column {
                    Text(
                        text = "Chat Room",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = chatRoom.name, onValueChange = {
                            if (it.length <= 30) {
                                onChatRoomNameChange(it)
                            }
                        }, singleLine = true, trailingIcon = {
                            Text(
                                text = "${chatRoom.name.length}/30",
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
                                onChatRoomAuthorOnlyWriteChange(!chatRoom.authorOnlyWrite)

                            }
                            .padding(vertical = 16.dp)) {
                        Text(
                            text = "Should only author write?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        OutlinedIconToggleButton(
                            checked = chatRoom.authorOnlyWrite,
                            onCheckedChange = { onChatRoomAuthorOnlyWriteChange(it) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            if (chatRoom.authorOnlyWrite) {
                                Icon(
                                    Icons.Filled.Check,
                                    contentDescription = "Selected Sort",
                                    modifier = Modifier.padding(4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
