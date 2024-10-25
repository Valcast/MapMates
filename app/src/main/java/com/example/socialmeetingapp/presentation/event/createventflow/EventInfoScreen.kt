package com.example.socialmeetingapp.presentation.event.createventflow

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.socialmeetingapp.domain.event.model.Event


@Composable
fun EventInfoScreen(event: Event, onUpdateTitle: (String) -> Unit, onUpdateDescription: (String) -> Unit, onUpdateIsPrivate: (Boolean) -> Unit, onUpdateIsOnline: (Boolean) -> Unit, onUpdateMaxParticipants: (Int) -> Unit) {

    Column {
        Text(
            text = "What are you organizing?",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )


        OutlinedTextField(
            value = event.title,
            onValueChange = {
                if (it.length <= 30) {
                    onUpdateTitle(it)
                }
            },
            label = { Text(text = "Title") },
            singleLine = true,
            placeholder = { Text(text = "Enter event title") },
            trailingIcon = {
                Text(
                    text = "${event.title.length}/30",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(.5F)
                )
            }
        )
        OutlinedTextField(
            value = event.description,
            onValueChange = {
                if (it.length <= 250) {
                    onUpdateDescription(it)
                }
            },
            label = { Text(text = "Description") },
            minLines = 3,
            maxLines = 3,
            placeholder = { Text(text = "Enter event description") },
            trailingIcon = {
                Text(
                    text = "${event.description.length}/250",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(.5F)
                )
            }
        )


        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Private")
            Checkbox(
                checked = event.isPrivate,
                onCheckedChange = { onUpdateIsPrivate(it) }
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(text = "Online")
            Checkbox(
                checked = event.isOnline,
                onCheckedChange = { onUpdateIsOnline(it) })

        }

        Text(
            text = "How many people can attend?",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Slider(
            value = event.maxParticipants.toFloat(),
            onValueChange = { onUpdateMaxParticipants(it.toInt()) },
            valueRange = 3f..20f
        )

        Text(
            text = "${event.maxParticipants} people",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

    }
}