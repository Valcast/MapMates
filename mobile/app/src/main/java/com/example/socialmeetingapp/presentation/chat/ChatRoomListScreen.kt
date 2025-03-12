package com.example.socialmeetingapp.presentation.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ChatRoomListScreen(chatRooms: List<ChatRoomSummary>, onChatRoomClick: (String) -> Unit) {
    Column {
        Text(
            text = "Chat Rooms",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        chatRooms.forEach { chatRoom ->
            ChatRoomListItem(chatRoom = chatRoom, onChatRoomClick = onChatRoomClick)
        }
    }

}

@Composable
fun ChatRoomListItem(chatRoom: ChatRoomSummary, onChatRoomClick: (String) -> Unit) {

    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable { onChatRoomClick(chatRoom.chatRoom.id) }) {
        Text(
            text = chatRoom.chatRoom.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        Text(
            text = chatRoom.eventTitle,
            style = MaterialTheme.typography.bodyMedium,
        )

        Text(
            text = "Members: ${chatRoom.chatRoom.members.size}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(16.dp)
        )

        chatRoom.chatRoom.lastMessage?.let { lastMessage ->
            Text(
                text = "Last Message: ${lastMessage.text}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}