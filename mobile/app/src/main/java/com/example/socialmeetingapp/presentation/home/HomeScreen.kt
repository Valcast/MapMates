package com.example.socialmeetingapp.presentation.home

import android.Manifest
import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import com.example.socialmeetingapp.domain.model.Category
import com.example.socialmeetingapp.domain.model.DateRange
import com.example.socialmeetingapp.domain.model.Event
import com.example.socialmeetingapp.domain.model.SortOrder
import com.example.socialmeetingapp.presentation.common.NavigationManager
import com.example.socialmeetingapp.presentation.common.Routes
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
    filters: Filters = Filters(),
    onEventClick: (String) -> Unit,
    onLocationRequested: suspend () -> Unit,
    onFiltersApplied: (DateRange?, Category?, SortOrder?) -> Unit,
) {
    val lifecycleScope = LocalLifecycleOwner.current.lifecycleScope
    var isListView by rememberSaveable { mutableStateOf(false) }
    var isRequestPermissionDialogVisible by remember { mutableStateOf(false) }
    var selectedEventIndex by remember { mutableStateOf<Int?>(null) }
    var eventCluster by remember { mutableStateOf<List<String>>(emptyList()) }
    var isFilterScreenVisible by remember { mutableStateOf(false) }

    val locationPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    LaunchedEffect(locationPermissions) {
        if (locationPermissions.allPermissionsGranted) {
            isRequestPermissionDialogVisible = false
        }
    }

    var updateLocationJob by remember { mutableStateOf<Job?>(null) }


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

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                Text(
                    text = "MapMates",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )

                TextButton(
                    onClick = { isListView = !isListView }
                ) {
                    Text(
                        text = if (isListView) "Switch to map" else "Switch to list",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.animateContentSize()
                    )
                }
            }



            if (isListView) {
                Row(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    CategoryFilterChip(
                        onClick = {
                            isFilterScreenVisible = true
                        },
                        onFiltersApplied = onFiltersApplied,
                        filters = filters
                    )

                    DateRangeFilterChip(
                        onClick = {
                            isFilterScreenVisible = true
                        },
                        onFiltersApplied = onFiltersApplied,
                        filters = filters
                    )

                    SortOrderFilterChip(
                        onClick = {
                            isFilterScreenVisible = true
                        },
                        onFiltersApplied = onFiltersApplied,
                        filters = filters
                    )
                }
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
                        onMapClick = {
                            selectedEventIndex = null
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
                            CategoryFilterChip(
                                onClick = {
                                    isFilterScreenVisible = true
                                },
                                onFiltersApplied = onFiltersApplied,
                                filters = filters
                            )

                            DateRangeFilterChip(
                                onClick = {
                                    isFilterScreenVisible = true
                                },
                                onFiltersApplied = onFiltersApplied,
                                filters = filters
                            )

                            SortOrderFilterChip(
                                onClick = {
                                    isFilterScreenVisible = true
                                },
                                onFiltersApplied = onFiltersApplied,
                                filters = filters
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
                    .align(Alignment.BottomStart)
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

        FloatingActionButton(
            onClick = {
                NavigationManager.navigateTo(
                    Routes.CreateEvent(
                        currentLocation?.latitude ?: 52.237049,
                        currentLocation?.longitude ?: 21.017532
                    )
                )
            }, modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add Event")
        }


        AnimatedVisibility(
            visible = isFilterScreenVisible,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            FilterScreen(
                categories = categories,
                filters = filters,
                onCloseFilters = { isFilterScreenVisible = false },
                onApplyFilters = { dateRange, category, sortType ->
                    isFilterScreenVisible = false
                    onFiltersApplied(dateRange, category, sortType)
                })
        }
    }


}
