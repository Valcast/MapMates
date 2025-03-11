package com.example.socialmeetingapp.presentation.notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.socialmeetingapp.R
import com.example.socialmeetingapp.domain.model.NotificationType
import com.example.socialmeetingapp.presentation.common.NavigationManager
import com.example.socialmeetingapp.presentation.common.Routes

@Composable
fun NotificationScreen(notification: NotificationUI, onMarkAsRead: (String) -> Unit) {
    Card(
        onClick = {
            onMarkAsRead(notification.id)
            when (notification.type) {
                NotificationType.EVENT_CREATED -> NavigationManager.navigateTo(
                    Routes.Event(
                        (notification.data as NotificationData.EventCreated).eventId
                    )
                )

                NotificationType.JOIN_REQUEST -> NavigationManager.navigateTo(
                    Routes.Profile(
                        (notification.data as NotificationData.JoinRequest).userId
                    )
                )
            }
        },
        colors = CardDefaults.cardColors()
            .copy(containerColor = if (notification.isRead) Color.Transparent else MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(8.dp)
        ) {
            when (val data = notification.data) {
                is NotificationData.EventCreated -> EventCreatedContent(data, notification)
                is NotificationData.JoinRequest -> JoinRequestContent(data, notification)
            }

            IconButton(onClick = {}, modifier = Modifier.padding(8.dp)) {
                Icon(Icons.Default.Menu, contentDescription = "Close")
            }
        }

        if (notification.data is NotificationData.JoinRequest) {
            Row {
                Button(
                    onClick = {},
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.decline),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Button(
                    onClick = {},
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.accept),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }

}

@Composable
fun EventCreatedContent(data: NotificationData.EventCreated, notification: NotificationUI) {
    AsyncImage(
        model = data.authorProfilePictureUrl,
        contentDescription = "Profile picture",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(100f))
    )

    Column(modifier = Modifier.padding(start = 16.dp)) {
        Text(
            text = stringResource(
                R.string.notification_event_created,
                data.authorName,
                data.eventTitle
            ),
            style = MaterialTheme.typography.bodySmall,
        )

        Text(
            text = formatTimeAgo(notification.timestamp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.8f)
        )
    }
}

@Composable
fun JoinRequestContent(data: NotificationData.JoinRequest, notification: NotificationUI) {
    AsyncImage(
        model = data.userPictureUrl,
        contentDescription = "Profile picture",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(100f))
    )

    Column(modifier = Modifier.padding(start = 16.dp)) {
        Text(
            text = stringResource(
                R.string.notification_join_request,
                data.userName,
                data.eventTitle
            ),
            style = MaterialTheme.typography.bodySmall,
        )

        Text(
            text = formatTimeAgo(notification.timestamp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.8f)
        )
    }
}