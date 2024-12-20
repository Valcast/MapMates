package com.example.socialmeetingapp.presentation.notifications

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.socialmeetingapp.domain.model.Notification
import com.example.socialmeetingapp.domain.model.NotificationData
import com.example.socialmeetingapp.domain.model.NotificationType
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.periodUntil
import kotlinx.datetime.toInstant

@Composable
fun NotificationsScreen(state: NotificationsState, onNotificationAvatarClick: (String) -> Unit, onJoinEventNotificationClick: (String) -> Unit) {

    when (state) {
        is NotificationsState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        }

        is NotificationsState.Content -> {
            if (state.notifications.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No notifications")
                }
            }
            LazyColumn {
                item {
                    Column {
                        Text(
                            text = "Notifications",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(16.dp)
                        )

                        HorizontalDivider()
                    }


                }

                items(state.notifications.size) { id ->
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = state.notifications[id].senderAvatar,
                            contentDescription = "Sender Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(25.dp))
                                .clickable { onNotificationAvatarClick(state.notifications[id].senderId) }
                        )

                        Column(verticalArrangement = Arrangement.SpaceAround) {
                            when (state.notifications[id].type) {
                                NotificationType.NewFollower -> {
                                    Text(
                                        text = buildAnnotatedString {
                                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                append(state.notifications[id].senderName)
                                            }
                                            append(" started following you")
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }

                                NotificationType.JoinEvent -> {
                                    Text(
                                        text = buildAnnotatedString {
                                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                append(state.notifications[id].senderName)
                                            }
                                            append(" joined your ")
                                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                append((state.notifications[id].data as NotificationData.EventNotificationData).eventName)
                                            }
                                            append(" event")
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }

                                NotificationType.FriendCreatedNewEvent -> {
                                    Text(
                                        text = buildAnnotatedString {
                                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                append(state.notifications[id].senderName)
                                            }
                                            append(" created a new event ")
                                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                append((state.notifications[id].data as NotificationData.EventNotificationData).eventName)
                                            }
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(start = 8.dp).clickable {
                                            onJoinEventNotificationClick((state.notifications[id].data as NotificationData.EventNotificationData).eventId)
                                        }
                                    )
                                }

                                NotificationType.RemovedFromEvent -> {
                                    Text(
                                        text = buildAnnotatedString {
                                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                                append(state.notifications[id].senderName)
                                            }
                                            append(" removed you from an event")
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }

                            val notificationCreatedTime = state.notifications[id].createdAt.toInstant(
                                TimeZone.currentSystemDefault()
                            ).periodUntil(
                                Clock.System.now(),
                                TimeZone.currentSystemDefault()
                            )


                            Text(
                                text = when {
                                    notificationCreatedTime.days > 0 -> "${notificationCreatedTime.days} days ago"
                                    notificationCreatedTime.hours > 0 -> "${notificationCreatedTime.hours} hours ago"
                                    notificationCreatedTime.minutes > 0 -> "${notificationCreatedTime.minutes} minutes ago"
                                    else -> "Just now"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        is NotificationsState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.message)
            }
        }
    }
}