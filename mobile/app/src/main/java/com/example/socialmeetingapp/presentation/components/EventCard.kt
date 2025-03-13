package com.example.socialmeetingapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.socialmeetingapp.R
import com.example.socialmeetingapp.domain.model.Category
import com.example.socialmeetingapp.domain.model.Event
import com.example.socialmeetingapp.domain.model.UserPreview
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun EventCard(event: Event, onCardClick: (String) -> Unit, modifier: Modifier = Modifier) {
    ElevatedCard(
        onClick = { onCardClick(event.id) }, modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    painter = painterResource(
                        when (event.category) {
                            Category.CINEMA -> R.drawable.cinema
                            Category.CONCERT -> R.drawable.concert
                            Category.CONFERENCE -> R.drawable.conference
                            Category.HOUSEPARTY -> R.drawable.houseparty
                            Category.MEETUP -> R.drawable.meetup
                            Category.THEATER -> R.drawable.theater
                            Category.WEBINAR -> R.drawable.webinar
                        }
                    ),
                    contentDescription = "Event Category Icon",
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(36.dp),
                    tint = Color.Unspecified
                )
            }
            if (event.isOnline) {
                Text(
                    text = "Online",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            } else {
                Row {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(16.dp),
                        tint = MaterialTheme.colorScheme.onBackground,

                        )

                    Text(
                        text = event.locationAddress!!,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Event Date",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onBackground,
                )

                val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

                Text(
                    text = when (event.startTime.date) {
                        now.date -> {
                            "Today"
                        }

                        now.date.plus(1, DateTimeUnit.DAY) -> {
                            "Tomorrow"
                        }

                        else -> {
                            String.format(
                                Locale.getDefault(),
                                "%d %s",
                                event.startTime.dayOfMonth,
                                event.startTime.month.getDisplayName(
                                    TextStyle.FULL_STANDALONE, Locale.getDefault()
                                ).replaceFirstChar {
                                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                                }
                            )
                        }
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(start = 4.dp)
                )

                Icon(
                    painter = painterResource(R.drawable.clock),
                    contentDescription = "Event Time",
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(16.dp),
                    tint = MaterialTheme.colorScheme.onBackground,
                )

                Text(
                    text = event.startTime.time.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                val allParticipants = mutableListOf<UserPreview>()
                allParticipants.add(event.author)
                allParticipants.addAll(event.participants)

                allParticipants.forEachIndexed { index, participant ->
                    AsyncImage(
                        model = participant.profilePictureUri,
                        contentDescription = "Participant Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(32.dp)
                            .let {
                                if (index < 3) {
                                    it.offset(
                                        if (index == 0) 0.dp else (-8).dp * index,
                                        0.dp
                                    )
                                } else it
                            }
                            .clip(RoundedCornerShape(16.dp))
                    )
                }
                Box(
                    modifier = Modifier
                        .offset(
                            when {
                                event.participants.isEmpty() -> (-8).dp
                                event.participants.size == 1 -> (-16).dp
                                event.participants.size > 1 -> (-24).dp
                                else -> 0.dp
                            }, 0.dp
                        )
                        .size(32.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "+${
                            when {
                                event.participants.size < 3 -> "0"
                                else -> event.participants.size - 2
                            }
                        }",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.background,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Text(
                    text = event.author.username,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .offset(
                            when {
                                event.participants.isEmpty() -> (-8).dp
                                event.participants.size == 1 -> (-16).dp
                                event.participants.size > 1 -> (-24).dp
                                else -> 0.dp
                            }, 0.dp
                        )
                )
            }


        }
    }

}