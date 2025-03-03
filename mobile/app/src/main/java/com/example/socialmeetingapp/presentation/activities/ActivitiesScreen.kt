package com.example.socialmeetingapp.presentation.activities

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import com.example.socialmeetingapp.presentation.common.NavigationManager
import com.example.socialmeetingapp.presentation.common.Routes
import com.example.socialmeetingapp.presentation.components.EventCard
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitiesScreen(
    state: ActivitiesState,
    onCardClick: (String) -> Unit,
    onCreateEventClick: () -> Unit,
    onExploreEventClick: () -> Unit,
    onAcceptJoinRequest: (String, String) -> Unit,
    onDeclineJoinRequest: (String, String) -> Unit
) {
    var tabState by rememberSaveable { mutableIntStateOf(0) }
    val titles = listOf("Joined", "Created")

    when (state) {
        is ActivitiesState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

        }

        is ActivitiesState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = state.message,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        is ActivitiesState.Content -> {
            Column {
                Text(
                    text = "Your Activities",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp),
                    letterSpacing = 1.sp
                )
                PrimaryTabRow(selectedTabIndex = tabState) {
                    titles.forEachIndexed { index, title ->
                        Tab(
                            selected = tabState == index,
                            onClick = { tabState = index },
                            text = {
                                Text(
                                    text = title,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground,
                                )
                            }
                        )
                    }
                }

                when (tabState) {
                    0 -> {
                        if (state.joinedEventDetails.isEmpty() || state.joinedEventDetails.all { it.endTime < Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()) }) {

                            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "You haven't joined any events yet. Discover exciting events and join the fun!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 40.dp),
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                )

                                Button(
                                    onClick = onExploreEventClick,
                                    shape = MaterialTheme.shapes.medium,
                                    modifier = Modifier.padding(top = 16.dp)
                                ) {
                                    Text(text = "Explore Events")
                                }
                            }

                        } else {
                            LazyColumn {
                                items(state.joinedEventDetails.size) { event ->

                                    if (state.joinedEventDetails[event].endTime < Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())) {
                                        return@items
                                    }

                                    EventCard(
                                        event = state.joinedEventDetails[event],
                                        onCardClick = onCardClick,
                                        modifier = Modifier.padding(
                                            top = 16.dp,
                                            start = 8.dp,
                                            end = 8.dp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    1 -> {
                        if (state.createdEventDetails.isEmpty() || state.createdEventDetails.all { it.endTime < Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()) }) {
                            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "You haven't created any events yet. Start creating events and invite your friends!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 40.dp),
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                )

                                Button(
                                    onClick = onCreateEventClick,
                                    shape = MaterialTheme.shapes.medium,
                                    modifier = Modifier.padding(top = 16.dp)
                                ) {
                                    Text(text = "Create Event")
                                }
                            }
                        } else {
                            LazyColumn {
                                items(state.createdEventDetails.size) { event ->
                                    if (state.createdEventDetails[event].endTime < Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())) {
                                        return@items
                                    }

                                    EventCard(
                                        event = state.createdEventDetails[event],
                                        onCardClick = onCardClick,
                                        modifier = Modifier
                                            .zIndex(1f)
                                            .padding(top = 16.dp, start = 8.dp, end = 8.dp)
                                    )

                                    if (state.createdEventDetails[event].isPrivate && state.createdEventDetails[event].joinRequests.isNotEmpty()) {
                                        LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                                            items(state.createdEventDetails[event].joinRequests.size) { joinRequest ->
                                                ElevatedCard(
                                                    elevation = CardDefaults.elevatedCardElevation(
                                                        defaultElevation = 1.dp
                                                    ),
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(
                                                            horizontal = 24.dp,
                                                            vertical = 8.dp
                                                        )
                                                ) {
                                                    Column(modifier = Modifier.padding(16.dp)) {
                                                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clip(MaterialTheme.shapes.small).clickable {
                                                            NavigationManager.navigateTo(Routes.Profile(state.createdEventDetails[event].joinRequests[joinRequest].id))
                                                        }.padding(4.dp)) {
                                                            AsyncImage(
                                                                model = state.createdEventDetails[event].joinRequests[joinRequest].profilePictureUri,
                                                                contentDescription = "Participant Profile Picture",
                                                                modifier = Modifier
                                                                    .size(32.dp)
                                                                    .clip(
                                                                        RoundedCornerShape(16.dp)
                                                                    ),
                                                                contentScale = ContentScale.Crop
                                                            )
                                                            Text(
                                                                text = state.createdEventDetails[event].joinRequests[joinRequest].username,
                                                                style = MaterialTheme.typography.bodyLarge,
                                                                fontWeight = FontWeight.Bold,
                                                                modifier = Modifier.padding(start = 8.dp)
                                                            )

                                                            Text(
                                                                text = " wants to join your event",
                                                                style = MaterialTheme.typography.bodyMedium,
                                                            )
                                                        }


                                                        Row(
                                                            horizontalArrangement = Arrangement.End,
                                                            modifier = Modifier.fillMaxWidth()
                                                        ) {
                                                            Button(onClick = {
                                                                onDeclineJoinRequest(
                                                                    state.createdEventDetails[event].id,
                                                                    state.createdEventDetails[event].joinRequests[joinRequest].id
                                                                )
                                                            }) {
                                                                Text(
                                                                    text = "Decline",
                                                                    style = MaterialTheme.typography.bodyMedium
                                                                )
                                                            }

                                                            Button(
                                                                onClick = {
                                                                    onAcceptJoinRequest(
                                                                        state.createdEventDetails[event].id,
                                                                        state.createdEventDetails[event].joinRequests[joinRequest].id
                                                                    )
                                                                },
                                                                modifier = Modifier.padding(start = 16.dp)
                                                            ) {
                                                                Text(
                                                                    text = "Accept",
                                                                    style = MaterialTheme.typography.bodyMedium
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                        }
                                    }
                                }

                            }
                        }

                    }
                }


            }

        }
    }


}