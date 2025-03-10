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
    val data = when (notification.type) {
        NotificationType.EVENT_CREATED -> notification.data as NotificationData.EventCreated
    }

    Card(
        onClick = {
            onMarkAsRead(notification.id)
            NavigationManager.navigateTo(Routes.Event(data.eventId))
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
                    text = when (notification.type) {
                        NotificationType.EVENT_CREATED -> stringResource(
                            R.string.notification_event_created,
                            data.authorName,
                            data.eventTitle
                        )
                    },
                    style = MaterialTheme.typography.bodySmall,
                )

                Text(
                    text = formatTimeAgo(notification.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.8f)
                )
            }

            IconButton(onClick = {}, modifier = Modifier.padding(8.dp)) {
                Icon(Icons.Default.Menu, contentDescription = "Close")
            }
        }
    }

}