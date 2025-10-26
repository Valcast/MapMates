package com.valcast.mapmates.presentation.event.createevent

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.valcast.mapmates.domain.model.UserPreview

@Composable
fun EventInviteScreen(
    followersAndFollowing: Pair<List<UserPreview>, List<UserPreview>>,
    onUserSelected: (String) -> Unit
) {
    val allUsers = followersAndFollowing.first + followersAndFollowing.second

    LazyColumn {
        item {
            Text(
                text = "Invite people to your event",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        items(allUsers.size) { user ->
            val userPreview = allUsers[user]
            UserListItem(
                user = userPreview,
                onUserSelected = onUserSelected
            )
        }
    }
}

@Composable
fun UserListItem(
    user: UserPreview,
    onUserSelected: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clickable {
                onUserSelected(user.id)
            }
    ) {
        AsyncImage(
            model = user.profilePictureUri,
            contentDescription = "Follower Avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(25.dp))
        )

        Text(
            text = user.username,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 8.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Checkbox(
            checked = false,
            onCheckedChange = {
                onUserSelected(user.id)
            }
        )
    }
}