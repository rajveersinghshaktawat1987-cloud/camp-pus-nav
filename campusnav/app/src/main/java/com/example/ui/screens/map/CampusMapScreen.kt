package com.example.ui.screens.map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.BuildingCategory
import com.example.data.model.CampusBuilding
import com.example.data.model.FloorRoom
import com.example.data.model.LocationCategory
import com.example.ui.screens.components.OfflineNoticeBanner
import com.example.ui.viewmodel.CampusViewModel

@Composable
fun CampusMapScreen(
    viewModel: CampusViewModel,
    onNavigateToRoute: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buildings by viewModel.filteredBuildings.collectAsStateWithLifecycle()
    val savedLocations by viewModel.savedLocations.collectAsStateWithLifecycle()
    val selectedBuilding by viewModel.selectedBuilding.collectAsStateWithLifecycle()
    val selectedFloorNumber by viewModel.selectedFloorNumber.collectAsStateWithLifecycle()
    val showFloorPlanDialog by viewModel.showFloorPlanDialog.collectAsStateWithLifecycle()
    val activeRoute by viewModel.activeRoute.collectAsStateWithLifecycle()
    val userCampusX by viewModel.userCampusX.collectAsStateWithLifecycle()
    val userCampusY by viewModel.userCampusY.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategoryFilter.collectAsStateWithLifecycle()

    var scale by remember { mutableFloatStateOf(1.0f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    val categories = listOf(
        null to "All",
        BuildingCategory.ACADEMIC to "Academic",
        BuildingCategory.LIBRARY to "Libraries",
        BuildingCategory.DINING to "Dining",
        BuildingCategory.STUDENT_CENTER to "Student Center",
        BuildingCategory.DORMITORY to "Dorms",
        BuildingCategory.SPORTS to "Sports",
        BuildingCategory.PARKING to "Parking & Transit"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("campus_map_screen")
    ) {
        // Interactive Canvas Vector Map
        CampusMapCanvas(
            buildings = buildings,
            savedLocations = savedLocations,
            selectedBuilding = selectedBuilding,
            activeRoute = activeRoute,
            userCampusX = userCampusX,
            userCampusY = userCampusY,
            mapTheme = settings.mapTheme,
            showParkingLayer = settings.showParkingLayer,
            showTransitLayer = settings.showTransitLayer,
            scale = scale,
            offsetX = offsetX,
            offsetY = offsetY,
            onTransformChange = { newScale, newX, newY ->
                scale = newScale
                offsetX = newX
                offsetY = newY
            },
            onBuildingClick = { building ->
                viewModel.selectBuilding(building)
            },
            onMapTap = { campusX, campusY ->
                // Deselect building if tapping empty campus ground
                if (selectedBuilding != null) {
                    viewModel.selectBuilding(null)
                }
            }
        )

        // Top Category Quick Filters Overlay
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        ) {
            OfflineNoticeBanner()

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("map_category_chips_row")
            ) {
                items(categories) { (cat, label) ->
                    ElevatedFilterChip(
                        selected = selectedCategory == cat,
                        onClick = { viewModel.setCategoryFilter(cat) },
                        label = { Text(label, style = MaterialTheme.typography.labelMedium) },
                        colors = FilterChipDefaults.elevatedFilterChipColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("chip_category_${label.lowercase()}")
                    )
                }
            }
        }

        // Floating Action Controls (Right side)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Recenter on User
            SmallFloatingActionButton(
                onClick = {
                    // Center screen on user location
                    offsetX = -userCampusX * scale + 500f
                    offsetY = -userCampusY * scale + 500f
                    viewModel.showMessage("Centered on your location")
                },
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.testTag("recenter_on_me_btn")
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "Center on me")
            }

            // Zoom In
            SmallFloatingActionButton(
                onClick = { scale = (scale * 1.25f).coerceAtMost(3.5f) },
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.testTag("zoom_in_btn")
            ) {
                Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In")
            }

            // Zoom Out
            SmallFloatingActionButton(
                onClick = { scale = (scale * 0.8f).coerceAtLeast(0.5f) },
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.testTag("zoom_out_btn")
            ) {
                Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out")
            }

            // Quick Save Location Pin
            FloatingActionButton(
                onClick = { viewModel.openCreateSavedLocationDialog(selectedBuilding) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier.testTag("quick_save_location_fab")
            ) {
                Icon(Icons.Default.AddLocationAlt, contentDescription = "Save Custom Location")
            }
        }

        // Building Detail Bottom Sheet (when building is tapped)
        AnimatedVisibility(
            visible = selectedBuilding != null,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            selectedBuilding?.let { building ->
                BuildingDetailSheet(
                    building = building,
                    onNavigateClick = {
                        viewModel.startNavigationToBuilding(building)
                        onNavigateToRoute()
                    },
                    onSaveLocationClick = {
                        viewModel.openCreateSavedLocationDialog(building)
                    },
                    onViewFloorsClick = {
                        viewModel.openFloorPlanDialog(true)
                    },
                    onDismiss = {
                        viewModel.selectBuilding(null)
                    }
                )
            }
        }

        // Multi-Floor Plan Dialog
        if (showFloorPlanDialog && selectedBuilding != null) {
            FloorPlanDialog(
                building = selectedBuilding!!,
                currentFloorNumber = selectedFloorNumber,
                onSelectFloor = { floor -> viewModel.selectFloor(floor) },
                onSaveRoomClick = { room ->
                    viewModel.openCreateSavedLocationDialog(selectedBuilding)
                    viewModel.openFloorPlanDialog(false)
                },
                onDismiss = { viewModel.openFloorPlanDialog(false) }
            )
        }
    }
}
