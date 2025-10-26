@file:OptIn(ExperimentalTime::class)

package com.valcast.mapmates.presentation.notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.valcast.mapmates.R
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun NotificationScreen(
    notifications: List<NotificationUI>,
    onMarkAllAsRead: () -> Unit,
    onMarkAsRead: (String) -> Unit
) {

    LazyColumn {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.notification_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )

                TextButton(onClick = onMarkAllAsRead) {
                    Text(
                        text = stringResource(R.string.notification_mark_all_as_read),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

        }

        if (notifications.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.notification_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            items(notifications.size) { index ->
                val notification = notifications[index]

                NotificationScreen(notification, onMarkAsRead)
            }
        }
    }
}


@Composable
fun formatTimeAgo(localDateTime: LocalDateTime): String {
    val currentDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val diff = currentDateTime.toInstant(TimeZone.currentSystemDefault()) - localDateTime.toInstant(
        TimeZone.currentSystemDefault()
    )

    return when {
        diff.inWholeMinutes < 1 -> stringResource(R.string.just_now)
        diff.inWholeMinutes == 1L -> stringResource(R.string.one_minute_ago_short)
        diff.inWholeMinutes < 60 -> stringResource(R.string.minutes_ago_short, diff.inWholeMinutes)
        diff.inWholeHours == 1L -> stringResource(R.string.one_hour_ago_short)
        diff.inWholeHours < 24 -> stringResource(R.string.hours_ago_short, diff.inWholeHours)
        diff.inWholeDays == 1L -> stringResource(R.string.one_day_ago)
        diff.inWholeDays < 30 -> stringResource(R.string.days_ago, diff.inWholeDays)
        else -> {
            val months = diff.inWholeDays / 30
            stringResource(R.string.months_ago, months)
        }
    }
}