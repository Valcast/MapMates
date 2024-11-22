package com.example.socialmeetingapp.presentation.activities

import android.inputmethodservice.Keyboard.Row
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import com.example.socialmeetingapp.domain.model.UserEvents
import com.example.socialmeetingapp.presentation.home.EventCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitiesScreen(
    events: UserEvents,
    onCardClick: (String) -> Unit,
    onCreateEventClick: () -> Unit,
    onExploreEventClick: () -> Unit,
    onAcceptJoinRequest: (String, String) -> Unit,
    onDeclineJoinRequest: (String, String) -> Unit
) {
    var state by remember { mutableIntStateOf(0) }
    val titles = listOf("Joined", "Created")

    Column {
        Text(
            text = "Your Activities",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
        PrimaryTabRow(selectedTabIndex = state) {
            titles.forEachIndexed { index, title ->
                Tab(
                    selected = state == index,
                    onClick = { state = index },
                    text = { Text(text = title, maxLines = 2, overflow = TextOverflow.Ellipsis) }
                )
            }
        }

        when (state) {
            0 -> {
                if (events.joinedEvents.isEmpty()) {
                    Text(
                        text = "You have not joined any events yet",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally)
                    )

                    Button(
                        onClick = onExploreEventClick,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.align(
                            Alignment.CenterHorizontally
                        )
                    ) {
                        Text(text = "Explore Events")
                    }
                } else {
                    LazyColumn {
                        items(events.joinedEvents.size) { event ->
                            EventCard(
                                event = events.joinedEvents[event],
                                onCardClick = onCardClick,
                                modifier = Modifier.padding(top = 16.dp, start = 8.dp, end = 8.dp)
                            )
                        }
                    }
                }
            }

            1 -> {
                if (events.createdEvents.isEmpty()) {
                    Text(
                        text = "You have not created any events yet",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally)
                    )

                    Button(
                        onClick = onCreateEventClick,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.align(
                            Alignment.CenterHorizontally
                        )
                    ) {
                        Text(text = "Create Event")
                    }
                } else {
                    LazyColumn {
                        items(events.createdEvents.size) { event ->
                            EventCard(
                                event = events.createdEvents[event],
                                onCardClick = onCardClick,
                                modifier = Modifier
                                    .zIndex(1f)
                                    .padding(top = 16.dp, start = 8.dp, end = 8.dp)
                            )

                            if (events.createdEvents[event].isPrivate && events.createdEvents[event].joinRequests.isNotEmpty()) {
                                LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                                    items(events.createdEvents[event].joinRequests.size) { joinRequest ->
                                        ElevatedCard(
                                            elevation = CardDefaults.elevatedCardElevation(
                                                defaultElevation = 4.dp
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 24.dp, vertical = 8.dp)
                                        ) {
                                            Column(modifier = Modifier.padding(16.dp)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    AsyncImage(
                                                        model = events.createdEvents[event].joinRequests[joinRequest].profilePictureUri,
                                                        contentDescription = "Participant Profile Picture",
                                                        modifier = Modifier
                                                            .size(32.dp)
                                                            .clip(
                                                                RoundedCornerShape(16.dp)
                                                            ),
                                                        contentScale = ContentScale.Crop
                                                    )
                                                    Text(
                                                        text = events.createdEvents[event].joinRequests[joinRequest].username,
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
                                                            events.createdEvents[event].id,
                                                            events.createdEvents[event].joinRequests[joinRequest].id
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
                                                                events.createdEvents[event].id,
                                                                events.createdEvents[event].joinRequests[joinRequest].id
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