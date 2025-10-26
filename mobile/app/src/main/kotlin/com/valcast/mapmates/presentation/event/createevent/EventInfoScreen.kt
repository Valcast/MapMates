package com.valcast.mapmates.presentation.event.createevent

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.valcast.mapmates.domain.model.Event


@Composable
fun EventInfoScreen(
    event: Event,
    onUpdateTitle: (String) -> Unit,
    onUpdateDescription: (String) -> Unit,
    onUpdateIsPrivate: (Boolean) -> Unit,
    onUpdateMaxParticipants: (Int) -> Unit,
) {
    val participantLimits = listOf(2, 3, 5, 10, 15, 20, 30, Int.MAX_VALUE)
    var sliderPosition by remember {
        mutableFloatStateOf(
            participantLimits.indexOf(event.maxParticipants).toFloat()
        )
    }


    Column {
        Text(
            text = "What's your event about?",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Title",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        OutlinedTextField(
            value = event.title,
            onValueChange = {
                if (it.length <= 30) {
                    onUpdateTitle(it)
                }
            },
            singleLine = true,
            trailingIcon = {
                Text(
                    text = "${event.title.length}/30",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(.5F)
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Description",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 4.dp, top = 32.dp)
        )
        OutlinedTextField(
            value = event.description,
            onValueChange = {
                if (it.length <= 250) {
                    onUpdateDescription(it)
                }
            },
            minLines = 3,
            maxLines = 3,
            placeholder = {
                Text(
                    text = "Add a description to encourage people to join your event.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(.5F)
                )
            },
            trailingIcon = {
                Text(
                    text = "${event.description.length}/250",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(.5F)
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "How many people can attend?",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 32.dp, bottom = 4.dp)
        )

        Slider(
            value = sliderPosition,
            onValueChange = {
                sliderPosition = it
                val index = it.toInt().coerceIn(0, participantLimits.size - 1)
                onUpdateMaxParticipants(participantLimits[index])
            },
            valueRange = 0f..participantLimits.size - 1f,
            steps = participantLimits.size - 2
        )

        Text(
            text = if (
                event.maxParticipants == Int.MAX_VALUE
            ) "Unlimited" else "${event.maxParticipants} people",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

    }
}