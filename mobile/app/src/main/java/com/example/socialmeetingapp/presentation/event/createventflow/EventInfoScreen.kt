package com.example.socialmeetingapp.presentation.event.createventflow

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.socialmeetingapp.domain.model.Category
import com.example.socialmeetingapp.domain.model.Event


@Composable
fun EventInfoScreen(
    event: Event,
    categories: List<Category>,
    onUpdateTitle: (String) -> Unit,
    onUpdateDescription: (String) -> Unit,
    onUpdateIsPrivate: (Boolean) -> Unit,
    onUpdateIsOnline: (Boolean) -> Unit,
    onUpdateMaxParticipants: (Int) -> Unit,
    onUpdateCategory: (Category) -> Unit
) {
    var selectedIndex by remember { mutableIntStateOf(0) }

    val topCategories = categories.take(categories.size / 2)
    val bottomCategories = categories.drop(categories.size / 2)

    val scrollState = rememberScrollState()

    Column {
        Text(
            text = "What are you organizing?",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Column {
            CategorySegmentedRow(
                topCategories,
                0f,
                scrollState,
                {
                    selectedIndex = it
                    onUpdateCategory(categories[it])
                },
                selectedIndex)
            Spacer(modifier = Modifier.height(8.dp))
            CategorySegmentedRow(
                bottomCategories,
                topCategories.size.toFloat(),
                scrollState,
                {
                    selectedIndex = it
                    onUpdateCategory(categories[it])
                },
                selectedIndex
            )
        }


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

@Composable
fun CategorySegmentedRow(
    categories: List<Category>,
    offset: Float,
    scrollState: ScrollState,
    selectItem: (Int) -> Unit,
    selectedIndex: Int
) {
    Row(modifier = Modifier.horizontalScroll(scrollState)) {
        SingleChoiceSegmentedButtonRow {
            categories.forEachIndexed { index, label ->
                val absoluteIndex = categories.indexOf(label) + offset.toInt()

                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index, categories.size),
                    onClick = { selectItem(absoluteIndex) },
                    selected = absoluteIndex == selectedIndex,
                    icon = { null },
                    label = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .width(110.dp)
                                .padding(vertical = 4.dp)
                        ) {
                            AsyncImage(
                                model = label.iconUrl,
                                contentDescription = label.id,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = label.id,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    },
                )
            }
        }
    }
}

