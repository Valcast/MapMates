package com.example.socialmeetingapp.presentation.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import com.example.socialmeetingapp.R
import com.example.socialmeetingapp.presentation.components.EventCard
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun ProfileScreen(
    state: ProfileState,
    onAddFriend: (String) -> Unit,
    onDeleteFriend: (String) -> Unit,
    onEditProfile: () -> Unit,
    onGoToSettings: () -> Unit
) {

    var isProfilePictureDialogVisible by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {

        when (state) {
            is ProfileState.Loading -> {
                CircularProgressIndicator()
            }

            is ProfileState.Content -> {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    if (!state.isMyProfile) {
                        IconButton(
                            onClick = {
                                if (state.isObservedUser) {
                                    onDeleteFriend(state.user.id)
                                } else {
                                    onAddFriend(state.user.id)
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


                HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp, top = 8.dp))

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

                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Card(modifier = Modifier.fillMaxWidth(0.5f).padding(end = 8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.padding(16.dp).fillMaxWidth()
                        ) {
                            Text(
                                text = "Followers",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                            Text(
                                text = state.user.followers.size.toString(),
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Card(modifier = Modifier.fillMaxWidth().padding(start = 8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.padding(16.dp).fillMaxWidth()
                        ) {
                            Text(
                                text = "Following",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                            Text(
                                text = state.user.following.size.toString(),
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
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
                        modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp).fillMaxWidth()
                    ) {
                        Text(
                            text = "Edit Profile",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    OutlinedButton(
                        onClick = onGoToSettings,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp).fillMaxWidth()
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

                if (state.userEvents.isEmpty()) {
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
                    state.userEvents.forEach { event ->
                        EventCard(
                            event = event,
                            onCardClick = {},
                            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
                        )
                    }
                }


            }

            is ProfileState.Error -> {
                val error = state.message
                Text(text = "Error: $error")
            }
        }
    }

}