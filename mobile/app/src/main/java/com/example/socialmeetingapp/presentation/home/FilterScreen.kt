package com.example.socialmeetingapp.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import com.example.socialmeetingapp.R
import com.example.socialmeetingapp.domain.model.Category
import com.example.socialmeetingapp.domain.model.DateRange
import com.example.socialmeetingapp.domain.model.SortOrder
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(
    categories: List<Category>,
    filters: Filters = Filters(),
    onCloseFilters: () -> Unit,
    onApplyFilters: (DateRange?, Category?, SortOrder?) -> Unit
) {
    var showCategoryOptions by remember { mutableStateOf(false) }
    var showDateOptions by remember { mutableStateOf(false) }
    var showSortOptions by remember { mutableStateOf(false) }

    val dateRangePickerState = rememberDateRangePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= Clock.System.now().toEpochMilliseconds() - 86400000
            }
        },
        initialSelectedStartDateMillis = if (filters.dateRange is DateRange.Custom) {
            filters.dateRange.startTime.toInstant(TimeZone.currentSystemDefault())
                .toEpochMilliseconds()
        } else null,
        initialSelectedEndDateMillis = if (filters.dateRange is DateRange.Custom) {
            filters.dateRange.endTime.toInstant(TimeZone.currentSystemDefault())
                .toEpochMilliseconds()
        } else null
    )

    var selectedCategory by remember { mutableStateOf(filters.category) }
    var selectedSortOrder by remember { mutableStateOf(filters.sortOrder) }
    var selectedDateRange by remember { mutableStateOf(filters.dateRange) }

    LaunchedEffect(
        dateRangePickerState.selectedStartDateMillis,
        dateRangePickerState.selectedEndDateMillis
    ) {
        if (dateRangePickerState.selectedStartDateMillis != null && dateRangePickerState.selectedEndDateMillis != null) {
            val startTime =
                Instant.fromEpochMilliseconds(dateRangePickerState.selectedStartDateMillis!!)
                    .toLocalDateTime(TimeZone.currentSystemDefault())
            val endTime =
                Instant.fromEpochMilliseconds(dateRangePickerState.selectedEndDateMillis!!)
                    .toLocalDateTime(TimeZone.currentSystemDefault())
            selectedDateRange = DateRange.Custom(startTime, endTime)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onCloseFilters,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Close Filters")
                }
                Text(
                    text = stringResource(R.string.filter_title),
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
                        showCategoryOptions = !showCategoryOptions
                        showDateOptions = false
                        showSortOptions = false
                    })
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    stringResource(R.string.filter_categories),
                    style = MaterialTheme.typography.titleMedium
                )

                selectedCategory?.let { category ->
                    if (!showCategoryOptions) {
                        Text(
                            text = stringResource(
                                when (category.id) {
                                    "conference" -> R.string.filter_category_conference
                                    "meetup" -> R.string.filter_category_meetup
                                    "cinema" -> R.string.filter_category_cinema
                                    "concert" -> R.string.filter_category_concert
                                    "festival" -> R.string.filter_category_festival
                                    "houseparty" -> R.string.filter_category_houseparty
                                    "picnic" -> R.string.filter_category_picnic
                                    "theater" -> R.string.filter_category_theater
                                    "webinar" -> R.string.filter_category_webinar
                                    "workshop" -> R.string.filter_category_workshop
                                    else -> {
                                        R.string.filter_category_meetup
                                    }
                                }
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.background,
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.large)
                                .background(MaterialTheme.colorScheme.onBackground)
                                .padding(vertical = 4.dp, horizontal = 8.dp)
                        )
                    }
                }

                Icon(
                    if (showCategoryOptions) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Expand Categories"
                )

            }

            AnimatedVisibility(
                visible = showCategoryOptions,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    categories.chunked(2).forEach { rowCategories ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowCategories.forEach { category ->
                                FilterChip(
                                    onClick = {
                                        selectedCategory = category
                                    },
                                    label = {
                                        Text(
                                            text = stringResource(
                                                when (category.id) {
                                                    "conference" -> R.string.filter_category_conference
                                                    "meetup" -> R.string.filter_category_meetup
                                                    "cinema" -> R.string.filter_category_cinema
                                                    "concert" -> R.string.filter_category_concert
                                                    "festival" -> R.string.filter_category_festival
                                                    "houseparty" -> R.string.filter_category_houseparty
                                                    "picnic" -> R.string.filter_category_picnic
                                                    "theater" -> R.string.filter_category_theater
                                                    "webinar" -> R.string.filter_category_webinar
                                                    "workshop" -> R.string.filter_category_workshop
                                                    else -> {
                                                        R.string.filter_category_meetup
                                                    }
                                                }
                                            ),
                                            modifier = Modifier.padding(8.dp)
                                        )
                                    },
                                    selected = selectedCategory?.id == category.id,
                                    leadingIcon = {
                                        AsyncImage(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(category.iconUrl)
                                                .allowHardware(false)
                                                .build(),
                                            contentDescription = category.id,
                                            modifier = Modifier.size(24.dp)
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

            HorizontalDivider()

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable(onClick = {
                        showDateOptions = !showDateOptions
                        showCategoryOptions = false
                        showSortOptions = false
                    })
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    stringResource(R.string.filter_date),
                    style = MaterialTheme.typography.titleMedium
                )

                if (selectedDateRange != null && !showDateOptions) {
                    val dateRangeText = when (selectedDateRange!!) {
                        DateRange.Today -> stringResource(R.string.filter_date_today)
                        DateRange.Tomorrow -> stringResource(R.string.filter_date_tomorrow)
                        DateRange.ThisWeek -> stringResource(R.string.filter_date_this_week)
                        is DateRange.Custom -> {
                            val dateRange = selectedDateRange as DateRange.Custom

                            val startDate = dateRange.startTime
                            val endDate = dateRange.endTime

                            String.format(
                                Locale.getDefault(),
                                "%s %d - %s %d",
                                startDate.month.getDisplayName(
                                    TextStyle.SHORT_STANDALONE,
                                    Locale.getDefault()
                                ),
                                startDate.dayOfMonth,
                                endDate.month.getDisplayName(
                                    TextStyle.SHORT_STANDALONE,
                                    Locale.getDefault()
                                ),
                                endDate.dayOfMonth,
                            )
                        }
                    }
                    Text(
                        text = dateRangeText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.background,
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.large)
                            .background(MaterialTheme.colorScheme.onBackground)
                            .padding(vertical = 4.dp, horizontal = 8.dp)
                    )
                }

                Icon(
                    if (showDateOptions) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Expand Dates"
                )
            }



            AnimatedVisibility(
                visible = showDateOptions,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                        FilterChip(
                            selected = selectedDateRange is DateRange.Today,
                            label = { Text(stringResource(R.string.filter_date_today)) },
                            onClick = {
                                selectedDateRange = DateRange.Today
                                dateRangePickerState.setSelection(null, null)
                            },
                            modifier = Modifier.padding(start = 16.dp)
                        )

                        FilterChip(
                            selected = selectedDateRange is DateRange.Tomorrow,
                            label = { Text(stringResource(R.string.filter_date_tomorrow)) },
                            onClick = {
                                selectedDateRange = DateRange.Tomorrow
                                dateRangePickerState.setSelection(null, null)
                            },
                            modifier = Modifier.padding(start = 16.dp)
                        )

                        FilterChip(
                            selected = selectedDateRange is DateRange.ThisWeek,
                            label = { Text(stringResource(R.string.filter_date_this_week)) },
                            onClick = {
                                selectedDateRange = DateRange.ThisWeek
                                dateRangePickerState.setSelection(null, null)
                            },
                            modifier = Modifier.padding(start = 16.dp)
                        )

                    }

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
            }

            HorizontalDivider()

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable(onClick = {
                        showSortOptions = !showSortOptions
                        showDateOptions = false
                        showCategoryOptions = false
                    })
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    stringResource(R.string.filter_sort),
                    style = MaterialTheme.typography.titleMedium
                )

                selectedSortOrder?.let { sort ->
                    if (!showSortOptions) {
                        Text(
                            text = stringResource(
                                when (sort) {
                                    SortOrder.NEXT_DATE -> R.string.filter_sort_nextdate
                                    SortOrder.DISTANCE -> R.string.filter_sort_distance
                                    SortOrder.POPULARITY -> R.string.filter_sort_popularity
                                }
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.background,
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.large)
                                .background(MaterialTheme.colorScheme.onBackground)
                                .padding(vertical = 4.dp, horizontal = 8.dp)
                        )
                    }

                }

                Icon(
                    if (showSortOptions) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Expand Sort"
                )

            }

            AnimatedVisibility(
                visible = showSortOptions,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    SortOrder.entries.forEach { sort ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedSortOrder = sort
                                }
                                .padding(16.dp)
                        ) {
                            Text(
                                text = stringResource(
                                    when (sort) {
                                        SortOrder.NEXT_DATE -> R.string.filter_sort_nextdate
                                        SortOrder.DISTANCE -> R.string.filter_sort_distance
                                        SortOrder.POPULARITY -> R.string.filter_sort_popularity
                                    }
                                ),
                            )
                            if (selectedSortOrder == sort) {
                                Icon(Icons.Filled.CheckCircle, contentDescription = "Selected Sort")
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = !showSortOptions,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                HorizontalDivider()
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            AnimatedVisibility(visible = selectedDateRange != null || selectedCategory != null || selectedSortOrder != null) {
                TextButton(onClick = {
                    dateRangePickerState.setSelection(null, null)
                    selectedCategory = null
                    selectedSortOrder = null
                    selectedDateRange = null
                }, modifier = Modifier.fillMaxWidth(0.5f)) {
                    Text(text = stringResource(R.string.filter_reset))
                }
            }
            Button(
                onClick = {
                    onApplyFilters(
                        selectedDateRange,
                        selectedCategory,
                        selectedSortOrder
                    )
                },
                shape = MaterialTheme.shapes.large,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) { Text(text = stringResource(R.string.filter_apply)) }
        }
    }
}