package com.example.socialmeetingapp.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.paging.compose.LazyPagingItems
import coil3.compose.AsyncImage
import com.example.socialmeetingapp.R
import com.example.socialmeetingapp.domain.model.Relationship
import com.example.socialmeetingapp.presentation.components.EventCard
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun ProfileScreen(
    state: ProfileState,
    following: LazyPagingItems<Relationship>,
    followers: LazyPagingItems<Relationship>,
    onFollowUser: (String) -> Unit,
    onUnfollowUser: (String) -> Unit,
    onDeleteFollower: (String) -> Unit,
    onEditProfile: () -> Unit,
    onGoToSettings: () -> Unit,
    onCardClick: (String) -> Unit
) {
    var isProfilePictureDialogVisible by remember { mutableStateOf(false) }
    var isFollowersDialogVisible by remember { mutableStateOf(false) }
    var isFollowingDialogVisible by remember { mutableStateOf(false) }

    when (state) {
        is ProfileState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is ProfileState.Content -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
                    )

                    if (!state.isMyProfile) {
                        IconButton(
                            onClick = {
                                if (state.isObservedUser) {
                                    onUnfollowUser(state.user.id)
                                } else {
                                    onFollowUser(state.user.id)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (state.isObservedUser) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Observe User"
                            )
                        }
                    }
                }


                HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp))

                AsyncImage(
                    model = state.user.profilePictureUri,
                    contentDescription = stringResource(R.string.profile_picture),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .clickable { isProfilePictureDialogVisible = true },
                )

                if (isProfilePictureDialogVisible) {
                    Dialog(onDismissRequest = { isProfilePictureDialogVisible = false }) {
                        AsyncImage(
                            model = state.user.profilePictureUri,
                            contentDescription = stringResource(R.string.profile_picture),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(300.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(
                        text = state.user.username.uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Text(
                    text = state.user.bio,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(end = 8.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .clickable { isFollowersDialogVisible = true }) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "Followers",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                            Text(
                                text = "${state.user.followersCount}",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    if (state.isMyProfile && isFollowersDialogVisible) {
                        Dialog(onDismissRequest = { isFollowersDialogVisible = false }) {
                            LazyColumn(
                                modifier = Modifier
                                    .clip(MaterialTheme.shapes.medium)
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(16.dp)
                            ) {
                                item {
                                    Text(
                                        text = "Followers",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .padding(
                                                top = 16.dp,
                                                bottom = 8.dp,
                                                start = 16.dp,
                                                end = 16.dp
                                            )
                                    )
                                }

                                if (followers.itemCount == 0) {
                                    item {
                                        Text(
                                            text = "You are not following anyone",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onBackground.copy(
                                                alpha = 0.5f
                                            ),
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier
                                                .padding(16.dp)
                                        )
                                    }
                                }
                                items(followers.itemCount) { followerIndex ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .fillMaxWidth()
                                    ) {
                                        AsyncImage(
                                            model = followers[followerIndex]?.userPreview?.profilePictureUri,
                                            contentDescription = "Follower Avatar",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(50.dp)
                                                .clip(RoundedCornerShape(25.dp))
                                        )

                                        Text(
                                            text = followers[followerIndex]?.userPreview?.username
                                                ?: "",
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.padding(start = 8.dp)
                                        )

                                        Spacer(modifier = Modifier.weight(1f))

                                        IconButton(onClick = {
                                            onDeleteFollower(
                                                followers[followerIndex]?.userPreview?.id ?: ""
                                            )
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Unfollow"
                                            )
                                        }
                                    }

                                }
                            }
                        }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .clickable { isFollowingDialogVisible = true },
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = "Following",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                            Text(
                                text = "${state.user.followingCount}",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                if (state.isMyProfile && isFollowingDialogVisible) {
                    Dialog(onDismissRequest = { isFollowingDialogVisible = false }) {
                        LazyColumn(
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(16.dp)
                        ) {
                            item {
                                Text(
                                    text = "Following",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .padding(
                                            top = 16.dp,
                                            bottom = 8.dp,
                                            start = 16.dp,
                                            end = 16.dp
                                        )
                                )
                            }

                            if (following.itemCount == 0) {
                                item {
                                    Text(
                                        text = "You are not following anyone",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .align(Alignment.CenterHorizontally)
                                    )
                                }
                            }

                            items(following.itemCount) { followingIndex ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth()
                                ) {
                                    AsyncImage(
                                        model = following[followingIndex]?.userPreview?.profilePictureUri,
                                        contentDescription = "Follower Avatar",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(RoundedCornerShape(25.dp))
                                    )

                                    Text(
                                        text = following[followingIndex]?.userPreview?.username
                                            ?: "",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )

                                    Spacer(modifier = Modifier.weight(1f))

                                    IconButton(onClick = {
                                        onUnfollowUser(
                                            following[followingIndex]?.userPreview?.id ?: ""
                                        )
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Unfollow"
                                        )
                                    }
                                }

                            }
                        }
                    }
                }

                Text(
                    text = "Information",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(top = 16.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
                        .align(Alignment.Start)
                )
                Card(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {

                        Text(
                            text = "Age",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )


                        Text(
                            text = "${
                                Clock.System.now()
                                    .toLocalDateTime(TimeZone.UTC).year - state.user.dateOfBirth.year
                            }", style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Card(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {

                        Text(
                            text = "Gender",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )


                        Text(text = state.user.gender, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Card(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {

                        Text(
                            text = "Joined",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(end = 4.dp),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )


                        Text(
                            text = "${state.user.createdAt.dayOfMonth} ${
                                state.user.createdAt.month.name.lowercase().replaceFirstChar {
                                    it.uppercase()
                                }
                            }, ${state.user.createdAt.year}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                if (state.isMyProfile) {
                    OutlinedButton(
                        onClick = onEditProfile,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Edit Profile",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    OutlinedButton(
                        onClick = onGoToSettings,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Settings",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }


                }

                Text(
                    text = "History",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(start = 16.dp, top = 16.dp)
                        .align(Alignment.Start)
                )

                if (state.userEventDetails.isEmpty()) {
                    Text(
                        text = if (state.isMyProfile) "You don't have any activity in history" else "This user doesn't have any activity in history",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                } else {
                    state.userEventDetails.forEach { event ->
                        EventCard(
                            event = event,
                            onCardClick = { onCardClick(event.id) },
                            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
                        )
                    }
                }
            }
        }

        is ProfileState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}