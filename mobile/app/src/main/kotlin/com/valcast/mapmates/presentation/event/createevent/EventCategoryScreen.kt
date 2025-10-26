package com.valcast.mapmates.presentation.event.createevent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.valcast.mapmates.R
import com.valcast.mapmates.domain.model.Category

@Composable
fun EventCategoryScreen(
    selectedCategory: Category,
    onUpdateCategory: (Category) -> Unit
) {
    Column() {
        Text(
            text = "What are you organizing?",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Category.entries.chunked(2).forEach { rowCategories ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowCategories.forEach { category ->
                    FilterChip(
                        onClick = {
                            onUpdateCategory(category)
                        },
                        label = {
                            Text(
                                text = stringResource(
                                    when (category) {
                                        Category.CINEMA -> R.string.filter_category_cinema
                                        Category.CONCERT -> R.string.filter_category_concert
                                        Category.CONFERENCE -> R.string.filter_category_conference
                                        Category.HOUSEPARTY -> R.string.filter_category_houseparty
                                        Category.MEETUP -> R.string.filter_category_meetup
                                        Category.THEATER -> R.string.filter_category_theater
                                        Category.WEBINAR -> R.string.filter_category_webinar
                                    }
                                ),
                                modifier = Modifier.padding(8.dp)
                            )
                        },
                        selected = selectedCategory == category,
                        leadingIcon = {
                            Icon(
                                painter = painterResource(
                                    when (category) {
                                        Category.CINEMA -> R.drawable.cinema
                                        Category.CONCERT -> R.drawable.concert
                                        Category.CONFERENCE -> R.drawable.conference
                                        Category.HOUSEPARTY -> R.drawable.houseparty
                                        Category.MEETUP -> R.drawable.meetup
                                        Category.THEATER -> R.drawable.theater
                                        Category.WEBINAR -> R.drawable.webinar
                                    }
                                ),
                                contentDescription = "Category Icon",
                                modifier = Modifier.size(24.dp),
                                tint = Color.Unspecified
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                if (rowCategories.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

}