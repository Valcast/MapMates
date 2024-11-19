package com.example.socialmeetingapp.presentation.activities

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.socialmeetingapp.domain.model.UserEvents
import com.example.socialmeetingapp.presentation.home.EventCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitiesScreen(events: UserEvents, onCardClick: (String) -> Unit, onCreateEventClick: () -> Unit, onExploreEventClick: () -> Unit) {
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
                if(events.joinedEvents.isEmpty()){
                    Text(
                        text = "You have not joined any events yet",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
                    )

                    Button(onClick = onExploreEventClick, shape = MaterialTheme.shapes.medium, modifier = Modifier.align(
                        Alignment.CenterHorizontally)) {
                        Text(text = "Explore Events")
                    }
                } else{
                LazyColumn {
                    items(events.joinedEvents.size) { event ->
                        EventCard(event = events.joinedEvents[event], onCardClick = onCardClick)
                    }
                }}
            }
            1 -> {
                if(events.createdEvents.isEmpty()){
                    Text(
                        text = "You have not created any events yet",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
                    )

                    Button(onClick = onCreateEventClick,shape = MaterialTheme.shapes.medium, modifier = Modifier.align(
                        Alignment.CenterHorizontally)) {
                        Text(text = "Create Event")
                    }
                } else {LazyColumn {
                    items(events.createdEvents.size) { event ->
                        EventCard(event = events.createdEvents[event], onCardClick = onCardClick)
                    }
                }}

            }
        }





    }

}