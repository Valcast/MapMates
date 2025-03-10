package com.example.socialmeetingapp.presentation.home

import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import com.example.socialmeetingapp.R
import com.example.socialmeetingapp.domain.model.Category
import com.example.socialmeetingapp.domain.model.Event
import com.example.socialmeetingapp.domain.model.Sort
import com.example.socialmeetingapp.presentation.components.EventCard
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.clustering.Clustering
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import java.time.format.TextStyle
import java.util.Locale

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalPermissionsApi::class,
    MapsComposeExperimentalApi::class
)
@Composable
fun HomeScreen(
    events: List<Event>,
    categories: List<Category>,
    currentLocation: LatLng?,
    locationCoordinates: LatLng? = null,
    onMapLongClick: (LatLng) -> Unit,
    onEventClick: (String) -> Unit,
    onLocationRequested: suspend () -> Unit,
    onFiltersApplied: (LocalDateTime?, LocalDateTime?, Category?, Sort?) -> Unit,
) {
    val lifecycleScope = LocalLifecycleOwner.current.lifecycleScope
    var isListView by rememberSaveable { mutableStateOf(false) }
    var isRequestPermissionDialogVisible by remember { mutableStateOf(false) }
    var selectedEventIndex by remember { mutableStateOf<Int?>(null) }
    var eventCluster by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedCreateEventPosition by remember { mutableStateOf<LatLng?>(null) }
    var shouldShowEventDialog by rememberSaveable { mutableStateOf(true) }
    var areFiltersShown by remember { mutableStateOf(false) }
    var selectedStartDate by remember { mutableStateOf<LocalDateTime?>(null) }
    var selectedEndDate by remember { mutableStateOf<LocalDateTime?>(null) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedSortType by remember { mutableStateOf<Sort?>(null) }

    var updateLocationJob by remember { mutableStateOf<Job?>(null) }

    val locationPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    LaunchedEffect(locationPermissions) {
        if (locationPermissions.allPermissionsGranted) {
            isRequestPermissionDialogVisible = false
        }
    }


    val startPosition = locationCoordinates
        ?: (currentLocation ?: LatLng(
            52.237049, 21.017532
        ))

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            startPosition, if (locationCoordinates != null) 15f else 10f
        )
    }

    Box {
        Column(
            Modifier.fillMaxSize()
        ) {

            Text(
                text = "MapMates",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(16.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .fillMaxWidth()

            )

            if (isListView) {
                LazyColumn {
                    items(events.size) { index ->
                        EventCard(
                            events[index],
                            onCardClick = onEventClick,
                            modifier = Modifier.padding(
                                top = 16.dp,
                                start = 8.dp,
                                end = 8.dp
                            )
                        )
                    }
                }
            } else {
                Box {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        googleMapOptionsFactory = {
                            GoogleMapOptions().mapToolbarEnabled(false)
                                .zoomControlsEnabled(false)
                        },
                        properties = MapProperties(
                            isMyLocationEnabled = locationPermissions.permissions.any { it.status.isGranted },
                            isBuildingEnabled = true,
                            mapType = MapType.NORMAL
                        ),
                        uiSettings = MapUiSettings(
                            compassEnabled = false,
                            zoomControlsEnabled = false,
                            mapToolbarEnabled = false,
                            myLocationButtonEnabled = false
                        ),
                        onMapLongClick = {
                            selectedCreateEventPosition = it
                        },
                        onMapClick = {
                            selectedEventIndex = null
                            selectedCreateEventPosition = null
                            eventCluster = emptyList()
                        },
                    ) {
                        Clustering(
                            items = events.map { event ->
                                EventMarker(
                                    event.locationCoordinates,
                                    event.title,
                                    event.description,
                                    event.category.iconUrl
                                )
                            },
                            onClusterItemClick = { event ->
                                eventCluster = emptyList()
                                selectedEventIndex =
                                    events.indexOfFirst { it.title == event.title }
                                true
                            },
                            onClusterClick = { events ->
                                selectedEventIndex = null
                                eventCluster = events.items.map { it.title.toString() }
                                true
                            },
                            clusterContent = { cluster ->
                                Box(
                                    modifier = Modifier
                                        .clip(MaterialTheme.shapes.large)
                                        .background(MaterialTheme.colorScheme.primary)
                                        .size(40.dp)
                                ) {
                                    Text(
                                        text = cluster.size.toString(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.align(
                                            Alignment.Center
                                        )
                                    )
                                }

                            }, clusterItemContent = { event ->
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(event.getEventIconUrl())
                                        .allowHardware(false)
                                        .build(),
                                    contentDescription = event.title,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .animateContentSize()
                                )
                            }
                        )
                    }

                    Column {
                        Row(
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                        ) {
                            FilterChip(
                                selected = selectedStartDate != null && selectedEndDate != null,
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
                                        text = if (selectedStartDate != null && selectedEndDate != null) {
                                            String.format(
                                                Locale.getDefault(),
                                                "%s %d %d - %s %d %d",
                                                selectedStartDate!!.month.getDisplayName(
                                                    TextStyle.SHORT_STANDALONE,
                                                    Locale.getDefault()
                                                ),
                                                selectedStartDate!!.dayOfMonth,
                                                selectedStartDate!!.year,
                                                selectedEndDate!!.month.getDisplayName(
                                                    TextStyle.SHORT_STANDALONE,
                                                    Locale.getDefault()
                                                ),
                                                selectedEndDate!!.dayOfMonth,
                                                selectedEndDate!!.year
                                            )
                                        } else {
                                            "Date"
                                        },
                                        style = MaterialTheme.typography.labelMedium,
                                        modifier = Modifier.animateContentSize()
                                    )
                                },
                                trailingIcon = {
                                    AnimatedVisibility(
                                        visible = selectedStartDate != null && selectedEndDate != null,
                                        enter = expandHorizontally(),
                                        exit = shrinkHorizontally()
                                    ) {
                                        Icon(
                                            Icons.Filled.Clear,
                                            contentDescription = "Date Range",
                                            modifier = Modifier
                                                .clickable(onClick = {
                                                    selectedStartDate = null
                                                    selectedEndDate = null
                                                    onFiltersApplied(
                                                        null,
                                                        null,
                                                        selectedCategory,
                                                        selectedSortType
                                                    )
                                                })
                                        )
                                    }
                                },
                                onClick = {
                                    areFiltersShown = true
                                },
                                colors = FilterChipDefaults.filterChipColors()
                                    .copy(containerColor = MaterialTheme.colorScheme.surface),
                                border = null,
                                shape = MaterialTheme.shapes.extraLarge,
                                elevation = FilterChipDefaults.filterChipElevation(
                                    elevation = 4.dp
                                ),
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .animateContentSize()
                            )

                            FilterChip(
                                selected = selectedCategory != null,
                                leadingIcon = {
                                    Icon(
                                        Icons.Filled.Menu,
                                        contentDescription = "Category",
                                        modifier = Modifier
                                            .padding(end = 4.dp)
                                            .size(20.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = stringResource(
                                            when (selectedCategory?.id) {
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
                                                    R.string.filter_category
                                                }
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
                                                .clickable(onClick = {
                                                    selectedCategory = null
                                                    onFiltersApplied(
                                                        selectedStartDate,
                                                        selectedEndDate,
                                                        null,
                                                        selectedSortType
                                                    )
                                                })
                                        )
                                    }
                                },
                                onClick = {
                                    areFiltersShown = true
                                },
                                colors = FilterChipDefaults.filterChipColors()
                                    .copy(containerColor = MaterialTheme.colorScheme.surface),
                                border = null,
                                elevation = FilterChipDefaults.filterChipElevation(
                                    elevation = 4.dp
                                ),
                                shape = MaterialTheme.shapes.extraLarge
                            )

                            FilterChip(
                                selected = selectedSortType != null,
                                leadingIcon = {
                                    Icon(
                                        Icons.Filled.Settings,
                                        contentDescription = "SortType",
                                        modifier = Modifier
                                            .padding(end = 4.dp)
                                            .size(20.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = when (selectedSortType) {
                                            Sort.NEXT_DATE -> stringResource(R.string.filter_sort_nextdate)
                                            Sort.DISTANCE -> stringResource(R.string.filter_sort_distance)
                                            Sort.POPULARITY -> stringResource(R.string.filter_sort_popularity)
                                            else -> stringResource(R.string.filter_sort)
                                        },
                                        style = MaterialTheme.typography.labelMedium,
                                    )
                                },
                                trailingIcon = {
                                    AnimatedVisibility(
                                        visible = selectedSortType != null,
                                        enter = expandHorizontally(),
                                        exit = shrinkHorizontally()
                                    ) {
                                        Icon(
                                            Icons.Filled.Clear,
                                            contentDescription = "Reset Sort Type",
                                            modifier = Modifier
                                                .padding(start = 4.dp)
                                                .clickable(onClick = {
                                                    selectedSortType = null
                                                    onFiltersApplied(
                                                        selectedStartDate,
                                                        selectedEndDate,
                                                        selectedCategory,
                                                        null
                                                    )
                                                })
                                        )
                                    }
                                },
                                onClick = {
                                    areFiltersShown = true
                                },
                                colors = FilterChipDefaults.filterChipColors()
                                    .copy(containerColor = MaterialTheme.colorScheme.surface),
                                border = null,
                                elevation = FilterChipDefaults.filterChipElevation(
                                    elevation = 4.dp
                                ),
                                shape = MaterialTheme.shapes.extraLarge,
                                modifier = Modifier
                                    .padding(start = 16.dp)
                                    .animateContentSize()
                            )
                        }

                        selectedEventIndex?.let {
                            EventCard(
                                events[it],
                                onCardClick = onEventClick,
                                modifier = Modifier.padding(
                                    top = 16.dp,
                                    start = 8.dp,
                                    end = 8.dp
                                )
                            )
                        }

                        AnimatedVisibility(
                            visible = eventCluster.isNotEmpty(),
                            enter = expandVertically(),
                            exit = fadeOut()
                        ) {
                            LazyRow {
                                items(eventCluster.size) { index ->
                                    EventCard(
                                        events.first { it.title == eventCluster[index] },
                                        onCardClick = onEventClick,
                                        modifier = Modifier
                                            .padding(top = 16.dp, start = 8.dp, end = 8.dp)
                                            .height(
                                                IntrinsicSize.Min
                                            )
                                    )
                                }
                            }
                        }
                    }

                    if (selectedCreateEventPosition != null) {
                        shouldShowEventDialog = false
                        Card(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(16.dp)
                                .clickable {
                                    onMapLongClick(selectedCreateEventPosition!!)
                                    selectedCreateEventPosition = null
                                }) {
                            Row(modifier = Modifier.padding(16.dp)) {

                                Icon(
                                    Icons.Filled.Add,
                                    contentDescription = "Create Event",
                                    modifier = Modifier.padding(end = 4.dp)
                                )
                                Text(
                                    text = "Create Event",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }

                        }
                    }
                }
            }



            if (isRequestPermissionDialogVisible) {
                BasicAlertDialog(onDismissRequest = {
                    isRequestPermissionDialogVisible = false

                }) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.background)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "MapMates needs your location permission",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "We need your location to show you events near you",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(top = 16.dp),
                            textAlign = TextAlign.Center
                        )
                        Row(
                            horizontalArrangement = Arrangement.SpaceAround,
                            modifier = Modifier
                                .padding(top = 32.dp)
                                .fillMaxWidth()
                        ) {
                            OutlinedButton(onClick = {
                                isRequestPermissionDialogVisible = false
                            }) {
                                Text(
                                    text = "Skip",
                                    color = MaterialTheme.colorScheme.onBackground,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            Button(onClick = {
                                locationPermissions.launchMultiplePermissionRequest()
                                isRequestPermissionDialogVisible = false
                            }) {
                                Text(
                                    text = "Allow",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                        }
                    }


                }
            }
        }

        SmallFloatingActionButton(
            onClick = { isListView = !isListView },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
        ) {
            Icon(
                if (isListView) Icons.Filled.Place else Icons.AutoMirrored.Filled.List,
                "Map View"
            )
        }

        if (!isListView) {
            SmallFloatingActionButton(
                onClick = {
                    updateLocationJob?.cancel()

                    if (locationPermissions.permissions.all { !it.status.isGranted }) {
                        isRequestPermissionDialogVisible = true
                        return@SmallFloatingActionButton
                    }

                    updateLocationJob = lifecycleScope.launch {
                        onLocationRequested()

                        currentLocation?.let {
                            try {
                                cameraPositionState.animate(
                                    update = CameraUpdateFactory.newCameraPosition(
                                        CameraPosition.fromLatLngZoom(
                                            it, 15f
                                        )
                                    ),
                                    durationMs = 1000
                                )
                            } catch (_: Exception) {
                            }
                        }


                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
            ) {
                if (locationPermissions.permissions.any { it.status.isGranted }) {
                    Icon(
                        imageVector = Icons.Filled.Place,
                        contentDescription = "My Location",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(
                        bitmap = LocalContext.current.assets.open("icons/location-disabled.webp")
                            .use(
                                BitmapFactory::decodeStream
                            ).asImageBitmap(),
                        contentDescription = "My Location",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = areFiltersShown,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            FilterScreen(
                categories = categories,
                initialStartDate = selectedStartDate,
                initialEndDate = selectedEndDate,
                initialSelectedCategory = selectedCategory,
                onCloseFilters = { areFiltersShown = false },
                onApplyFilters = { startDate, endDate, category, sortType ->
                    selectedStartDate = startDate
                    selectedEndDate = endDate
                    selectedCategory = category
                    selectedSortType = sortType
                    areFiltersShown = false
                    onFiltersApplied(startDate, endDate, category, sortType)
                })
        }
    }


}
