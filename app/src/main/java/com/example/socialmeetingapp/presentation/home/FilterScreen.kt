package com.example.socialmeetingapp.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import com.example.socialmeetingapp.domain.model.Category
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(
    categories: List<Category>,
    initialStartDate: LocalDateTime? = null,
    initialEndDate: LocalDateTime? = null,
    initialSelectedCategory: Category? = null,
    onCloseFilters: () -> Unit,
    onApplyFilters: (LocalDateTime?, LocalDateTime?, Category?) -> Unit
) {
    var isCategoriesExpanded by remember { mutableStateOf(false) }
    var isDateRangePickerExpanded by remember { mutableStateOf(false) }

    val dateRangePickerState = rememberDateRangePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= Clock.System.now().toEpochMilliseconds() - 86400000
            }
        },
        initialSelectedStartDateMillis = initialStartDate?.toInstant(TimeZone.currentSystemDefault())?.toEpochMilliseconds(),
        initialSelectedEndDateMillis = initialEndDate?.toInstant(TimeZone.currentSystemDefault())?.toEpochMilliseconds()
    )

    val selectedStartDate = dateRangePickerState.selectedStartDateMillis?.let {
        Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.currentSystemDefault())
    }

    val selectedEndDate = dateRangePickerState.selectedEndDateMillis?.let {
        Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.currentSystemDefault())
    }

    var selectedCategory by remember { mutableStateOf<Category?>(initialSelectedCategory) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = onCloseFilters, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(Icons.Filled.Close, contentDescription = "Close Filters")
            }
            Text(
                text = "Filters",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        }
        HorizontalDivider()
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable(onClick = {
                    isCategoriesExpanded = !isCategoriesExpanded
                    isDateRangePickerExpanded = false
                })
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text("Categories", style = MaterialTheme.typography.titleMedium)

            selectedCategory?.let { category ->
                Text(
                    text = category.id,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.large)
                        .background(MaterialTheme.colorScheme.onBackground)
                        .padding(vertical = 4.dp, horizontal = 8.dp)
                )
            }

            Icon(
                if (isCategoriesExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = "Expand Categories"
            )

        }

        AnimatedVisibility(
            visible = isCategoriesExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                items(categories.size) {
                    FilterChip(
                        onClick = {
                            selectedCategory = categories[it]
                        },
                        label = {
                            Text(
                                text = categories[it].id,
                                modifier = Modifier.padding(8.dp)
                            )
                        },
                        selected = selectedCategory?.id == categories[it].id,
                        leadingIcon = {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(categories[it].iconUrl)
                                    .allowHardware(false)
                                    .build(),
                                contentDescription = categories[it].id,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }

        HorizontalDivider()

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable(onClick = {
                    isDateRangePickerExpanded = !isDateRangePickerExpanded
                    isCategoriesExpanded = false
                })
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text("Date", style = MaterialTheme.typography.titleMedium)

            if (selectedStartDate != null && selectedEndDate != null) {
                Text(
                    text = "${
                        selectedStartDate.month.getDisplayName(
                            TextStyle.SHORT_STANDALONE,
                            Locale.getDefault()
                        )
                    } ${selectedStartDate.dayOfMonth} ${selectedStartDate.year} - " +
                            "${
                                selectedEndDate.month.getDisplayName(
                                    TextStyle.SHORT_STANDALONE,
                                    Locale.getDefault()
                                )
                            } ${selectedEndDate.dayOfMonth} ${selectedEndDate.year}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.large)
                        .background(MaterialTheme.colorScheme.onBackground)
                        .padding(vertical = 4.dp, horizontal = 8.dp)
                )
            }


            Icon(
                if (isDateRangePickerExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = "Expand Dates"
            )
        }

        AnimatedVisibility(
            visible = isDateRangePickerExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {

            BoxWithConstraints {
                val scale =
                    remember(this.maxWidth) { if (this.maxWidth > 360.dp) 1f else (this.maxWidth / 360.dp) }
                Box(
                    modifier = Modifier
                        .requiredWidthIn(min = 360.dp)
                        .height(360.dp)
                ) {
                    DateRangePicker(
                        modifier = Modifier.scale(scale),
                        state = dateRangePickerState,
                        title = null,
                        headline = null,
                        showModeToggle = false,
                        colors = DatePickerDefaults.colors().copy(
                            containerColor = MaterialTheme.colorScheme.background,
                        )
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = !isDateRangePickerExpanded,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            HorizontalDivider()
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(verticalAlignment = Alignment.CenterVertically) {
            AnimatedVisibility(visible = (selectedStartDate != null && selectedEndDate != null) || selectedCategory != null) {
                TextButton(onClick = {
                    dateRangePickerState.setSelection(null, null)
                    selectedCategory = null
                }, modifier = Modifier.fillMaxWidth(0.5f)) {
                    Text(text = "Clear Filters")
                }
            }
            Button(
                onClick = {
                    onApplyFilters(selectedStartDate, selectedEndDate, selectedCategory)
                },
                shape = MaterialTheme.shapes.large,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) { Text(text = "Apply Filters") }
        }
    }
}