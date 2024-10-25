package com.example.socialmeetingapp.presentation.activities

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.example.socialmeetingapp.domain.event.model.Event
import com.example.socialmeetingapp.presentation.home.EventCard

@Composable
fun ActivitiesScreen(events: List<Event>, onCardClick: (String) -> Unit) {

    Column {
        Text(
            text = "Your Activities",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
        )

        LazyColumn {
            items(events.size) { event ->
                EventCard(event = events[event], onCardClick = onCardClick)
            }
        }


    }

}