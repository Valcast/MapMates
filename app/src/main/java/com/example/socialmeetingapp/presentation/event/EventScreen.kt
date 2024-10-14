package com.example.socialmeetingapp.presentation.event

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun EventScreen(eventID: String, navigateToMap: () -> Unit, innerPadding: PaddingValues) {
    val viewModel = hiltViewModel<EventViewModel>()

    Column(
        modifier = Modifier
            .padding(innerPadding)
            .padding(16.dp)
    ) {
        Button(
            onClick = { navigateToMap() },
            shape = RoundedCornerShape(10.dp),
        ) {
            Text(text = "Back")
        }

        Row(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Event Title",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )

            Box {
                Text(
                    text = "25 / 50",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Row {

            Column {
                Text(text = "Sunday, September 2024", color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.titleMedium)
                Text(text = "⏱\uFE0E 7.30am - 9.30am", color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.titleMedium)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Hosted By",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Button(
                onClick = {}, colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.background,
                    disabledContentColor = MaterialTheme.colorScheme.background
                ), shape = RoundedCornerShape(10.dp), contentPadding = PaddingValues(0.dp)
            ) {
                Icon(imageVector = Icons.Default.Person, contentDescription = null)
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "People Going",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "(25 / 50)",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    modifier = Modifier.padding(start = 4.dp)
                )

            }
            Button(
                onClick = {}, colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.background,
                    disabledContentColor = MaterialTheme.colorScheme.background
                ), shape = RoundedCornerShape(10.dp), contentPadding = PaddingValues(0.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null)
                    Icon(imageVector = Icons.Default.Person, contentDescription = null)
                    Icon(imageVector = Icons.Default.Person, contentDescription = null)
                }
            }
        }

        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()).weight(1f),
        ) {
            Text(
                text = "About Event",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras nec elit vitae mauris volutpat tempor. Nunc rhoncus mi vel sem lacinia, quis aliquet ex molestie. Cras nec elit vitae mauris volutpat tempor. Nunc rhoncus mi vel sem lacinia, quis aliquet ex molestie. Cras in lectus ultrices, auctor mi sed, dapibus metus. Aenean volutpat erat justo, quis sodales velit pulvinar ac. Donec velit urna, aliquet vel lacus vitae, luctus fermentum felis. Sed tempor tellus maximus sem congue, ac sollicitudin purus viverra. Vestibulum tempor, quam eu efficitur molestie, tellus justo consequat neque, at molestie risus felis et sem. Nam euismod tempor eleifend. Phasellus pharetra ligula arcu, non eleifend lacus interdum non. Fusce iaculis ligula diam, eu egestas ante porttitor non. In tincidunt eros ac sapien molestie sagittis. Mauris id tellus eros. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; ",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,

            )
        }



        ExtendedFloatingActionButton(
            onClick = {},
            modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth(),

        ) {
            Text(text = "Check In Event")
        }

    }


}