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
import com.example.socialmeetingapp.domain.model.Date
import com.example.socialmeetingapp.domain.model.Sort
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
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
    initialSelectedSort: Sort? = null,
    initialSelectedDate: Date? = null,
    onCloseFilters: () -> Unit,
    onApplyFilters: (LocalDateTime?, LocalDateTime?, Category?, Sort?) -> Unit
) {
    var isCategoriesExpanded by remember { mutableStateOf(false) }
    var isDateRangePickerExpanded by remember { mutableStateOf(false) }
    var isSortExpanded by remember { mutableStateOf(false) }

    val dateRangePickerState = rememberDateRangePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= Clock.System.now().toEpochMilliseconds() - 86400000
            }
        },
        initialSelectedStartDateMillis = initialStartDate?.toInstant(TimeZone.currentSystemDefault())
            ?.toEpochMilliseconds(),
        initialSelectedEndDateMillis = initialEndDate?.toInstant(TimeZone.currentSystemDefault())
            ?.toEpochMilliseconds()
    )

    val selectedStartDate = dateRangePickerState.selectedStartDateMillis?.let {
        Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.currentSystemDefault())
    }

    val selectedEndDate = dateRangePickerState.selectedEndDateMillis?.let {
        Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.currentSystemDefault())
    }

    var selectedDate by remember { mutableStateOf(initialSelectedDate) }

    var selectedCategory by remember { mutableStateOf(initialSelectedCategory) }
    var selectedSort by remember { mutableStateOf(initialSelectedSort) }

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
                        isCategoriesExpanded = !isCategoriesExpanded
                        isDateRangePickerExpanded = false
                        isSortExpanded = false
                    })
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    stringResource(R.string.filter_categories),
                    style = MaterialTheme.typography.titleMedium
                )

                selectedCategory?.let { category ->
                    if (!isCategoriesExpanded) {
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
                    if (isCategoriesExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Expand Categories"
                )

            }

            AnimatedVisibility(
                visible = isCategoriesExpanded,
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
                        isDateRangePickerExpanded = !isDateRangePickerExpanded
                        isCategoriesExpanded = false
                        isSortExpanded = false
                    })
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    stringResource(R.string.filter_date),
                    style = MaterialTheme.typography.titleMedium
                )

                if (selectedStartDate != null && selectedEndDate != null && !isDateRangePickerExpanded) {
                    val dateText = when (selectedDate) {
                        Date.TODAY -> stringResource(R.string.filter_date_today)
                        Date.TOMORROW -> stringResource(R.string.filter_date_tomorrow)
                        Date.THIS_WEEK -> stringResource(R.string.filter_date_this_week)
                        else -> "${
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
                                } ${selectedEndDate.dayOfMonth} ${selectedEndDate.year}"
                    }
                    Text(
                        text = dateText,
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
                Column {
                    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                        Date.entries.forEach { date ->
                            FilterChip(
                                selected = selectedDate == date,
                                label = {
                                    Text(
                                        stringResource(
                                            when (date) {
                                                Date.TODAY -> R.string.filter_date_today
                                                Date.TOMORROW -> R.string.filter_date_tomorrow
                                                Date.THIS_WEEK -> R.string.filter_date_this_week
                                            }
                                        )
                                    )
                                },
                                onClick = {
                                    val timeZone = TimeZone.currentSystemDefault()
                                    val today = Clock.System.now().toLocalDateTime(timeZone).date
                                    when (date) {
                                        Date.TODAY -> {
                                            val start =
                                                today.atStartOfDayIn(timeZone).toEpochMilliseconds()
                                            dateRangePickerState.setSelection(start, start)
                                            selectedDate = Date.TODAY
                                        }

                                        Date.TOMORROW -> {
                                            val tomorrow = today.plus(1, DateTimeUnit.DAY)
                                            val start = tomorrow.atStartOfDayIn(timeZone)
                                                .toEpochMilliseconds()
                                            dateRangePickerState.setSelection(start, start)
                                            selectedDate = Date.TOMORROW
                                        }

                                        Date.THIS_WEEK -> {
                                            val endOfWeek = today.plus(7, DateTimeUnit.DAY)
                                            val start =
                                                today.atStartOfDayIn(timeZone).toEpochMilliseconds()
                                            val end = endOfWeek.atStartOfDayIn(timeZone)
                                                .toEpochMilliseconds()
                                            dateRangePickerState.setSelection(start, end)
                                            selectedDate = Date.THIS_WEEK
                                        }
                                    }
                                },
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
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
                        isSortExpanded = !isSortExpanded
                        isDateRangePickerExpanded = false
                        isCategoriesExpanded = false
                    })
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    stringResource(R.string.filter_sort),
                    style = MaterialTheme.typography.titleMedium
                )

                selectedSort?.let { sort ->
                    if (!isSortExpanded) {
                        Text(
                            text = stringResource(
                                when (sort) {
                                    Sort.NEXT_DATE -> R.string.filter_sort_nextdate
                                    Sort.DISTANCE -> R.string.filter_sort_distance
                                    Sort.POPULARITY -> R.string.filter_sort_popularity
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
                    if (isSortExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Expand Sort"
                )

            }

            AnimatedVisibility(
                visible = isSortExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    Sort.entries.forEach { sort ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedSort = sort
                                }
                                .padding(16.dp)
                        ) {
                            Text(
                                text = stringResource(
                                    when (sort) {
                                        Sort.NEXT_DATE -> R.string.filter_sort_nextdate
                                        Sort.DISTANCE -> R.string.filter_sort_distance
                                        Sort.POPULARITY -> R.string.filter_sort_popularity
                                    }
                                ),
                            )
                            if (selectedSort == sort) {
                                Icon(Icons.Filled.CheckCircle, contentDescription = "Selected Sort")
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = !isSortExpanded,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                HorizontalDivider()
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            AnimatedVisibility(visible = (selectedStartDate != null && selectedEndDate != null) || selectedCategory != null || selectedSort != null) {
                TextButton(onClick = {
                    dateRangePickerState.setSelection(null, null)
                    selectedCategory = null
                    selectedSort = null
                }, modifier = Modifier.fillMaxWidth(0.5f)) {
                    Text(text = stringResource(R.string.filter_reset))
                }
            }
            Button(
                onClick = {
                    onApplyFilters(
                        selectedStartDate,
                        selectedEndDate,
                        selectedCategory,
                        selectedSort
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