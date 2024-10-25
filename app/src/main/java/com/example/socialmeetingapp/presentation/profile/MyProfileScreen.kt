package com.example.socialmeetingapp.presentation.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import com.example.socialmeetingapp.domain.common.model.Result
import com.example.socialmeetingapp.domain.user.model.User
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


@Composable
fun MyProfileScreen(user: Result<User>, onLogout: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        when (user) {
            is Result.Loading -> {
                CircularProgressIndicator()
            }

            is Result.Success -> {
                val user = user.data

                AsyncImage(
                    model = user.profilePictureUri,
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(50.dp))
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(
                        text = user.username.uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Text(
                    text = user.bio,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Text(
                    text = "Information",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(top = 16.dp, bottom = 8.dp)
                        .align(Alignment.Start)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DateRange,
                            contentDescription = "Age",
                            modifier = Modifier.padding(end = 4.dp),
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                        Text(
                            text = "Age",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }

                    Text(text = "${Clock.System.now().toLocalDateTime(TimeZone.UTC).year - user.dateOfBirth.year}", style = MaterialTheme.typography.bodyMedium)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Icon(
                            imageVector = Icons.Filled.DateRange,
                            contentDescription = "Gender",
                            modifier = Modifier.padding(end = 4.dp),
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                        Text(
                            text = "Gender",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }

                    Text(text = user.gender, style = MaterialTheme.typography.bodyMedium)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.DateRange,
                            contentDescription = "Joined Date",
                            modifier = Modifier.padding(end = 4.dp),
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                        Text(
                            text = "Joined",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(end = 4.dp),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }

                    Text(
                        text = "${user.createdAt.dayOfMonth} ${
                            user.createdAt.month.name.lowercase().replaceFirstChar {
                                it.uppercase()
                            }
                        }, ${user.createdAt.year}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Button(
                    onClick = onLogout,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(text = "Logout")
                }

            }

            is Result.Error -> {
                val error = user.message
                Text(text = "Error: $error")
            }

            else -> {}
        }
    }
}