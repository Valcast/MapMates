package com.example.socialmeetingapp.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.socialmeetingapp.domain.event.model.Event
import com.example.socialmeetingapp.presentation.common.NavigationManager
import com.example.socialmeetingapp.presentation.common.Routes
import java.util.Locale

@Composable
fun EventCard(event: Event) {
    ElevatedCard(
        onClick = {  },
        modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Creator Info",
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    modifier = Modifier.padding(end = 8.dp),
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),

                    )

                Text(
                    text = event.locationAddress!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Event Date",
                    modifier = Modifier.padding(end = 8.dp),
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                )

                Text(
                    text = String.format(
                        Locale.ROOT,
                        "%02d.%02d.%d %02d:%02d - %02d.%02d.%d %02d:%02d",
                        event.startTime.dayOfMonth,
                        event.startTime.monthNumber,
                        event.startTime.year,
                        event.startTime.hour,
                        event.startTime.minute,
                        event.endTime.dayOfMonth,
                        event.endTime.monthNumber,
                        event.endTime.year,
                        event.endTime.hour,
                        event.endTime.minute
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                )
            }

            Text(text = "${event.participants.size} people attending",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(vertical = 8.dp))



            Row( verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(0.5f)) {
                    Text(
                        text = "About Event",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = event.description,
                        style = MaterialTheme.typography.bodyMedium,
                        minLines = 2,
                        maxLines = 2,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }


                FilledIconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Creator Info",
                        tint = MaterialTheme.colorScheme.onPrimary,

                        )
                }
            }
        }
    }

}