package com.example.socialmeetingapp.presentation.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.example.socialmeetingapp.domain.model.ChatRoom
import java.util.Locale

@Composable
fun ChatRoomScreen(
    messages: LazyPagingItems<UserMessage>,
    chatRoom: ChatRoom?,
    latestMessages: List<UserMessage>,
    onSendMessage: (String) -> Unit,
    onBackClick: () -> Unit
) {
    var message by remember { mutableStateOf("") }
    var messageIndexWithVisibleDate by remember { mutableStateOf<Int?>(null) }

    Column(verticalArrangement = Arrangement.Top) {
        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back"
                )
            }

            Text(
                text = chatRoom?.name ?: "Chat",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .align(Alignment.Center)
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                reverseLayout = true,
            ) {
                items(latestMessages.size) { messageIndex ->
                    val message = latestMessages[messageIndex]
                    MessageItem(
                        message = message,
                        onShowDate = {
                            messageIndexWithVisibleDate =
                                if (messageIndexWithVisibleDate == messageIndex) {
                                    null
                                } else {
                                    messageIndex
                                }
                        }, isDateVisible = messageIndexWithVisibleDate == messageIndex
                    )
                }

                items(messages.itemCount) { messageIndex ->
                    if (messages[messageIndex] != null) {
                        val message = messages[messageIndex]!!

                        MessageItem(
                            message = message,
                            onShowDate = {
                                messageIndexWithVisibleDate =
                                    if (messageIndexWithVisibleDate == messageIndex) {
                                        null
                                    } else {
                                        messageIndex
                                    }
                            }, isDateVisible = messageIndexWithVisibleDate == messageIndex
                        )
                    }
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                placeholder = { Text("Message") },
                shape = MaterialTheme.shapes.extraLarge,
                modifier = Modifier.weight(1f)
            )

            AnimatedVisibility(
                visible = message.isNotEmpty()
            ) {
                IconButton(
                    onClick = {
                        onSendMessage(message)
                        message = ""
                    }, modifier = Modifier.padding(start = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send message"
                    )
                }
            }
        }
    }
}

@Composable
fun MessageItem(message: UserMessage, onShowDate: () -> Unit, isDateVisible: Boolean) {
    Row(
        horizontalArrangement = if (message.isCurrentUser) Arrangement.End else Arrangement.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.End,
        ) {
            if (!message.isCurrentUser) {
                Text(
                    text = message.senderName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Card(
                onClick = onShowDate,
            ) {
                Text(
                    text = message.message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(8.dp)
                )
            }

            AnimatedVisibility(
                visible = isDateVisible, modifier = Modifier.padding(start = 8.dp)
            ) {

                Text(
                    text = String.format(
                        Locale.getDefault(),
                        "%02d:%02d",
                        message.message.createdAt.hour,
                        message.message.createdAt.minute
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 8.dp)
                )

            }
        }
    }
}
