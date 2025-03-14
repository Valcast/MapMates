package com.example.socialmeetingapp.presentation.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import coil3.compose.AsyncImage
import com.example.socialmeetingapp.domain.model.ChatRoom
import com.example.socialmeetingapp.domain.model.Message
import com.example.socialmeetingapp.presentation.common.NavigationManager
import com.example.socialmeetingapp.presentation.common.Routes
import java.util.Locale

@Composable
fun ChatRoomScreen(
    messages: LazyPagingItems<MessageWithUserData>,
    chatRoom: ChatRoom?,
    newMessage: Message?,
    onSendMessage: (String) -> Unit,
) {
    var message by remember { mutableStateOf("") }
    var visibleMessageData by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(newMessage) {
        newMessage?.let {
            messages
        }
    }

    Column(verticalArrangement = Arrangement.Top) {
        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(
                onClick = { NavigationManager.navigateTo(Routes.Chat) },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
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

        LazyColumn(
            reverseLayout = true,
        ) {
            items(messages.itemCount) { messageIndex ->
                if (messages[messageIndex] != null) {
                    val message = messages[messageIndex]!!
                    val prevMessage =
                        if (messageIndex < messages.itemCount - 1) messages[messageIndex + 1] else null

                    val nextMessage =
                        if (messageIndex > 0) messages[messageIndex - 1] else null
                    Row(
                        horizontalArrangement = if (message.isCurrentUser) Arrangement.End else Arrangement.Start,
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        if (nextMessage?.isCurrentUser == message.isCurrentUser && !message.isCurrentUser) {
                            AsyncImage(
                                model = message.senderProfileImageUrl,
                                contentDescription = "Profile image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(36.dp)
                                    .clip(
                                        RoundedCornerShape(100)
                                    )
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.End, modifier = Modifier.padding(
                                start = if (message.isCurrentUser) 0.dp else 52.dp,
                                end = if (message.isCurrentUser && nextMessage?.isCurrentUser == true) 52.dp else 0.dp
                            )
                        ) {
                            if (prevMessage?.isCurrentUser != message.isCurrentUser) {
                                Text(
                                    text = message.senderNick,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Card(
                                onClick = {
                                    visibleMessageData = if (visibleMessageData == messageIndex) {
                                        null
                                    } else {
                                        messageIndex
                                    }
                                },
                            ) {
                                Text(
                                    text = message.message.text,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }

                            AnimatedVisibility(
                                visible = visibleMessageData == messageIndex,
                                modifier = Modifier.padding(start = 8.dp)
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
                        if (nextMessage?.isCurrentUser != message.isCurrentUser && message.isCurrentUser) {
                            AsyncImage(
                                model = message.senderProfileImageUrl,
                                contentDescription = "Profile image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(36.dp)
                                    .clip(
                                        RoundedCornerShape(100)
                                    )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))


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
                    },
                    modifier = Modifier.padding(start = 4.dp)
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
