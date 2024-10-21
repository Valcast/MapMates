package com.example.socialmeetingapp.presentation.event.createevent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle


@Composable
fun EventInfoScreen() {

    val viewModel = hiltViewModel<CreateEventViewModel>()
    val eventData by viewModel.eventData.collectAsStateWithLifecycle()

    Column {
        Text(
            text = "What are you organizing?",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )


        OutlinedTextField(
            value = eventData.title,
            onValueChange = {
                if (it.length <= 30) {
                    viewModel.updateTitle(it)
                }
            },
            label = { Text(text = "Title") },
            singleLine = true,
            placeholder = { Text(text = "Enter event title") },
            trailingIcon = {
                Text(
                    text = "${eventData.title.length}/30",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(.5F)
                )
            }
        )
        OutlinedTextField(
            value = eventData.description,
            onValueChange = {
                if (it.length <= 100) {
                    viewModel.updateDescription(it)
                }
            },
            label = { Text(text = "Description") },
            minLines = 3,
            maxLines = 3,
            placeholder = { Text(text = "Enter event description") },
            trailingIcon = {
                Text(
                    text = "${eventData.description.length}/100",
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
                checked = eventData.isPrivate,
                onCheckedChange = { viewModel.updateIsPrivate(it) }
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(text = "Online")
            Checkbox(
                checked = eventData.isOnline,
                onCheckedChange = { viewModel.updateIsOnline(it) })

        }

        Text(
            text = "How many people can attend?",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Slider(
            value = eventData.maxParticipants.toFloat(),
            onValueChange = {
                viewModel.updateMaxParticipants(it.toInt())
            },
            valueRange = 3f..20f
        )

        Text(
            text = "${eventData.maxParticipants} people",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

    }
}