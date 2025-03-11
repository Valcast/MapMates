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
import androidx.compose.material.icons.filled.Settings
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

@Composable
fun SortOrderFilterChip(
    onClick: () -> Unit,
    onFiltersApplied: (DateRange?, Category?, SortOrder?) -> Unit,
    filters: Filters = Filters()
) {
    FilterChip(
        selected = filters.sortOrder != null,
        leadingIcon = {
            Icon(
                Icons.Filled.Settings,
                contentDescription = "SortType",
                modifier = Modifier
                    .size(20.dp)
            )
        },
        label = {
            Text(
                text = when (filters.sortOrder) {
                    SortOrder.NEXT_DATE -> stringResource(R.string.filter_sort_nextdate)
                    SortOrder.DISTANCE -> stringResource(R.string.filter_sort_distance)
                    SortOrder.POPULARITY -> stringResource(R.string.filter_sort_popularity)
                    else -> stringResource(R.string.filter_sort)
                },
                style = MaterialTheme.typography.labelMedium,
            )
        },
        trailingIcon = {
            AnimatedVisibility(
                visible = filters.sortOrder != null,
                enter = expandHorizontally(),
                exit = shrinkHorizontally()
            ) {
                Icon(
                    Icons.Filled.Clear,
                    contentDescription = "Reset Sort Type",
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .clickable(onClick = {
                            onFiltersApplied(
                                filters.dateRange,
                                filters.category,
                                null
                            )
                        })
                )
            }
        },
        onClick = onClick,
        colors = FilterChipDefaults.filterChipColors()
            .copy(containerColor = MaterialTheme.colorScheme.surface),
        border = null,
        elevation = FilterChipDefaults.filterChipElevation(
            elevation = 2.dp
        ),
        shape = MaterialTheme.shapes.extraLarge,
        modifier = Modifier
            .padding(start = 16.dp)
            .animateContentSize()
    )
}
