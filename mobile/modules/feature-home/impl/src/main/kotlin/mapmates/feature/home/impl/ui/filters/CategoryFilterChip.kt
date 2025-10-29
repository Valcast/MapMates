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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import mapmates.feature.event.api.filters.Category
import mapmates.feature.home.impl.R as HomeR


@Composable
fun CategoryFilterChip(
    onClick: () -> Unit,
    onCategoryReset: () -> Unit,
    selectedCategory: Category? = null,
) {
    FilterChip(
        selected = selectedCategory != null,
        leadingIcon = {
            Icon(
                Icons.Filled.Menu,
                contentDescription = "Category",
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(20.dp)
            )
        },
        label = {
            Text(
                text = stringResource(
                    when (selectedCategory) {
                        Category.CINEMA -> HomeR.string.filter_category_cinema
                        Category.CONCERT -> HomeR.string.filter_category_concert
                        Category.CONFERENCE -> HomeR.string.filter_category_conference
                        Category.HOUSEPARTY -> HomeR.string.filter_category_houseparty
                        Category.MEETUP -> HomeR.string.filter_category_meetup
                        Category.THEATER -> HomeR.string.filter_category_theater
                        Category.WEBINAR -> HomeR.string.filter_category_webinar
                        else -> HomeR.string.filter_category
                    }
                ),
                style = MaterialTheme.typography.labelMedium,
            )
        },
        trailingIcon = {
            AnimatedVisibility(
                visible = selectedCategory != null,
                enter = expandHorizontally(),
                exit = shrinkHorizontally()
            ) {
                Icon(
                    Icons.Filled.Clear,
                    contentDescription = "Category",
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .clickable(onClick = onCategoryReset)
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