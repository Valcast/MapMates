package mapmates.feature.home.impl.ui

import android.Manifest
import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.launch
import mapmates.feature.event.api.Event
import mapmates.feature.event.api.filters.Filter
import mapmates.feature.event.api.filters.get
import mapmates.feature.event.ui.EventCard
import mapmates.feature.home.impl.ui.filters.CategoryFilterChip
import mapmates.feature.home.impl.ui.filters.DateRangeFilterChip
import mapmates.feature.home.impl.ui.filters.SortOrderFilterChip
import kotlin.reflect.KClass


@Composable
internal fun HomeScreen(
    locationCoordinates: LatLng? = null,
    viewModel: HomeViewModel = hiltViewModel<HomeViewModel, HomeViewModel.Factory>(
        creationCallback = { factory ->
            factory.create(locationCoordinates)
        }
    ),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    HomeScreenUi(
        state = state,
        onListViewToggle = viewModel::onListViewToggle,
        onFilterScreenOpen = viewModel::onFilterScreenOpen,
        onFilterDelete = viewModel::onFilterReset,
        onEventCardClick = viewModel::onEventCardClick,
        onMapClick = viewModel::onMapClick,
        onClusterClick = viewModel::onClusterClick,
        onClusterItemClick = viewModel::onClusterItemClick,
        onCenterToCurrentLocation = viewModel::onCenterToCurrentLocation
    )
}

@OptIn(
    ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class,
    MapsComposeExperimentalApi::class
)
@Composable
private fun HomeScreenUi(
    state: HomeState,
    onListViewToggle: () -> Unit = {},
    onFilterScreenOpen: () -> Unit = {},
    onFilterDelete: (KClass<out Filter>) -> Unit = {},
    onEventCardClick: (String) -> Unit = {},
    onMapClick: (LatLng?) -> Unit = {},
    onClusterItemClick: (EventMarker) -> Unit = {},
    onClusterClick: (List<EventMarker>) -> Unit = {},
    onCenterToCurrentLocation: () -> Unit = {},
) {
    var isRequestPermissionDialogVisible by remember { mutableStateOf(false) }

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

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            state.currentLocation, 10f
        )
    }

    LaunchedEffect(state.currentLocation) {
        if (state.currentLocation != LatLng(0.0, 0.0)) {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newCameraPosition(
                    CameraPosition.fromLatLngZoom(state.currentLocation, 10f)
                )
            )
        }
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
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "MapMates",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )

                TextButton(
                    onClick = onListViewToggle
                ) {
                    Text(
                        text = if (state.isListView) "Switch to map" else "Switch to list",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.animateContentSize()
                    )
                }
            }



            if (state.isListView) {
                Row(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    CategoryFilterChip(
                        onClick = onFilterScreenOpen,
                        onCategoryReset = { onFilterDelete(Filter.ByCategory::class) },
                        selectedCategory = state.filters.get<Filter.ByCategory>()?.category
                    )

                    DateRangeFilterChip(
                        onClick = onFilterScreenOpen,
                        onDateRangeReset = { onFilterDelete(Filter.ByDateRange::class) },
                        selectedDateRange = state.filters.get<Filter.ByDateRange>()?.dateRange
                    )

                    SortOrderFilterChip(
                        onClick = onFilterScreenOpen,
                        onSortOrderReset = { onFilterDelete(Filter.BySortOrder::class) },
                        selectedSortOrder = state.filters.get<Filter.BySortOrder>()?.sortOrder
                    )
                }
                LazyColumn {
                    items(state.events) { event ->
                        EventCard(
                            event = event,
                            onEventCardClick = onEventCardClick,
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
                        onMapClick = onMapClick,
                    ) {
                        Clustering(
                            items = state.events.mapNotNull { event ->
                                event.locationCoordinates?.let {
                                    EventMarker(
                                        LatLng(
                                            it.first,
                                            it.second
                                        ),
                                        event.title,
                                        event.description,
                                        event.category
                                    )
                                }
                            },
                            onClusterItemClick = { eventMarker ->
                                onClusterItemClick(eventMarker)
                                true
                            },
                            onClusterClick = { eventMarkers ->
                                onClusterClick(eventMarkers.items.toList())
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
                                Icon(
                                    painter = painterResource(event.getCategoryIcon()),
                                    contentDescription = event.title,
                                    modifier = Modifier.size(24.dp),
                                    tint = Color.Unspecified
                                )
                            }
                        )
                    }

                    Column {
                        Row(
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                        ) {
                            CategoryFilterChip(
                                onClick = onFilterScreenOpen,
                                onCategoryReset = { onFilterDelete(Filter.ByCategory::class) },
                                selectedCategory = state.filters.get<Filter.ByCategory>()?.category
                            )

                            DateRangeFilterChip(
                                onClick = onFilterScreenOpen,
                                onDateRangeReset = { onFilterDelete(Filter.ByDateRange::class) },
                                selectedDateRange = state.filters.get<Filter.ByDateRange>()?.dateRange
                            )

                            SortOrderFilterChip(
                                onClick = onFilterScreenOpen,
                                onSortOrderReset = { onFilterDelete(Filter.BySortOrder::class) },
                                selectedSortOrder = state.filters.get<Filter.BySortOrder>()?.sortOrder
                            )
                        }

                        AnimatedVisibility(
                            visible = state.isLoading,
                            enter = fadeIn(),
                            exit = fadeOut(),
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .align(Alignment.CenterHorizontally)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(48.dp)
                                    .shadow(1.dp, shape = RoundedCornerShape(100))
                                    .clip(RoundedCornerShape(100))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(8.dp)
                            )
                        }

                        AnimatedVisibility(
                            visible = state.selectedEvent != null,
                            enter = slideInHorizontally() + fadeIn(),
                            exit = fadeOut()
                        ) {
                            state.selectedEvent?.let { event ->
                                EventCard(
                                    event,
                                    onEventCardClick = onEventCardClick,
                                    modifier = Modifier.padding(
                                        top = 16.dp,
                                        start = 8.dp,
                                        end = 8.dp
                                    )
                                )
                            }
                        }

                        AnimatedVisibility(
                            visible = state.eventCluster.isNotEmpty(),
                            enter = expandVertically(),
                            exit = fadeOut()
                        ) {
                            LazyRow {
                                items(state.eventCluster) { event ->
                                    EventCard(
                                        event = event,
                                        onEventCardClick = onEventCardClick,
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

        if (!state.isListView) {
            SmallFloatingActionButton(
                onClick = {
                    if (locationPermissions.permissions.all { !it.status.isGranted }) {
                        isRequestPermissionDialogVisible = true
                        return@SmallFloatingActionButton
                    }

                    onCenterToCurrentLocation()
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
            }, modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add Event")
        }


        AnimatedVisibility(
            visible = state.isFilterScreenVisible,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            // TODO: Generate filter screen
        }
    }


}
