package mapmates.feature.home.impl.ui.filters

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
import mapmates.feature.event.api.filters.DateRange
import java.util.Locale
import mapmates.feature.home.impl.R as HomeR

@Composable
fun DateRangeFilterChip(
    onClick: () -> Unit,
    onDateRangeReset: () -> Unit,
    selectedDateRange: DateRange? = null,
) {
    FilterChip(
        selected = selectedDateRange != null,
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
                text = when (selectedDateRange) {
                    DateRange.Today -> stringResource(HomeR.string.filter_date_today)
                    DateRange.Tomorrow -> stringResource(HomeR.string.filter_date_tomorrow)
                    DateRange.ThisWeek -> stringResource(HomeR.string.filter_date_this_week)
                    is DateRange.Custom -> {
                        val startDate = selectedDateRange.startTime
                        val endDate = selectedDateRange.endTime

                        String.format(
                            Locale.getDefault(),
                            "%s - %d",
                            startDate.day,
                            endDate.day,
                        )
                    }

                    null -> stringResource(HomeR.string.filter_date)
                },
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.animateContentSize()
            )
        },
        trailingIcon = {
            AnimatedVisibility(
                visible = selectedDateRange != null,
                enter = expandHorizontally(),
                exit = shrinkHorizontally()
            ) {
                Icon(
                    Icons.Filled.Clear,
                    contentDescription = "Date Range",
                    modifier = Modifier
                        .clickable(onClick = onDateRangeReset)
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