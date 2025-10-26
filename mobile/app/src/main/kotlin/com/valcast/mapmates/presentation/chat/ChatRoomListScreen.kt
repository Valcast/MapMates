package com.valcast.mapmates.presentation.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.valcast.mapmates.R
import com.valcast.mapmates.domain.model.Category
import com.valcast.mapmates.presentation.notifications.formatTimeAgo

@Composable
fun ChatRoomListScreen(chatRooms: List<RoomPreview>, onChatRoomClick: (String) -> Unit) {
    Column {
        Text(
            text = "Chat Rooms",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        chatRooms.forEach { chatRoom ->
            ChatRoomListItem(roomPreview = chatRoom, onChatRoomClick = onChatRoomClick)
        }
    }

}

@Composable
fun ChatRoomListItem(roomPreview: RoomPreview, onChatRoomClick: (String) -> Unit) {

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .clickable { onChatRoomClick(roomPreview.chatRoom.id) }
            .padding(32.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = roomPreview.chatRoom.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(
                    when (roomPreview.event.category) {
                        Category.CINEMA -> R.drawable.cinema
                        Category.CONCERT -> R.drawable.concert
                        Category.CONFERENCE -> R.drawable.conference
                        Category.HOUSEPARTY -> R.drawable.houseparty
                        Category.MEETUP -> R.drawable.meetup
                        Category.THEATER -> R.drawable.theater
                        Category.WEBINAR -> R.drawable.webinar
                    }
                ),
                tint = Color.Unspecified,
                contentDescription = "Event Icon",
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = roomPreview.event.title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }



        Text(
            text = "Members: ${roomPreview.chatRoom.members.size}",
            style = MaterialTheme.typography.bodyMedium,
        )

        roomPreview.chatRoom.lastMessage?.let { lastMessage ->
            Row {
                Text(
                    text = "${roomPreview.lastMessageUserPreview?.username}: ${lastMessage.text}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.8f),
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = formatTimeAgo(lastMessage.createdAt),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.8f),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}