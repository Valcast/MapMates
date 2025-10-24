package com.example.socialmeetingapp.presentation.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.socialmeetingapp.R
import com.example.socialmeetingapp.domain.model.Category
import com.example.socialmeetingapp.domain.model.DateRange
import com.example.socialmeetingapp.domain.model.SortOrder
import kotlinx.datetime.LocalDateTime
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun DateRangeFilterChip(
    onClick: () -> Unit,
    onFiltersApplied: (DateRange?, Category?, SortOrder?) -> Unit,
    filters: Filters = Filters()
) {
    FilterChip(
        selected = filters.dateRange != null,
        leadingIcon = {
            Icon(
                Icons.Filled.DateRange,
                contentDescription = "Date Range",
                modifier = Modifier
                    .size(20.dp)
            )
        },
        label = {
            Text(
                text = when (filters.dateRange) {
                    DateRange.Today -> stringResource(R.string.filter_date_today)
                    DateRange.Tomorrow -> stringResource(R.string.filter_date_tomorrow)
                    DateRange.ThisWeek -> stringResource(R.string.filter_date_this_week)
                    is DateRange.Custom -> {
                        val startDate = filters.dateRange.startTime
                        val endDate = filters.dateRange.endTime

                        String.format(
                            Locale.getDefault(),
                            "%s - %d",
                            startDate.day,
                            endDate.day,
                        )
                    }

                    null -> stringResource(R.string.filter_date)
                },
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.animateContentSize()
            )
        },
        trailingIcon = {
            AnimatedVisibility(
                visible = filters.dateRange != null,
                enter = expandHorizontally(),
                exit = shrinkHorizontally()
            ) {
                Icon(
                    Icons.Filled.Clear,
                    contentDescription = "Date Range",
                    modifier = Modifier
                        .clickable(onClick = {
                            onFiltersApplied(
                                null,
                                filters.category,
                                filters.sortOrder
                            )
                        })
                )
            }
        },
        onClick = onClick,
        colors = FilterChipDefaults.filterChipColors()
            .copy(containerColor = MaterialTheme.colorScheme.surface),
        border = null,
        shape = MaterialTheme.shapes.extraLarge,
        elevation = FilterChipDefaults.filterChipElevation(
            elevation = 2.dp
        ),
        modifier = Modifier
            .padding(start = 16.dp)
            .animateContentSize()
    )

}