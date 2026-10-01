package com.example.ui.screens.saved

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.LocationCategory
import com.example.data.model.SavedLocation
import com.example.ui.screens.components.EmptyStateView
import com.example.ui.theme.CampusEmerald
import com.example.ui.viewmodel.CampusViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedLocationsScreen(
    viewModel: CampusViewModel,
    onNavigateToRoute: () -> Unit,
    modifier: Modifier = Modifier
) {
    val savedLocations by viewModel.savedLocations.collectAsStateWithLifecycle()
    val buildings by viewModel.buildings.collectAsStateWithLifecycle()
    val showSaveDialog by viewModel.showSaveDialog.collectAsStateWithLifecycle()
    val locationToEdit by viewModel.locationToEdit.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCollection by remember { mutableStateOf<String?>(null) }
    var showOnlyFavorites by remember { mutableStateOf(false) }

    val collections = listOf("All", "My Classes", "Study Nooks", "Favorite Eats", "Parking & Transit", "Personal")

    val filteredLocations = savedLocations.filter { loc ->
        val matchesQuery = searchQuery.isBlank() ||
                loc.name.contains(searchQuery, ignoreCase = true) ||
                loc.buildingName.contains(searchQuery, ignoreCase = true) ||
                loc.roomNumber.contains(searchQuery, ignoreCase = true) ||
                loc.notes.contains(searchQuery, ignoreCase = true)

        val matchesCollection = selectedCollection == null || selectedCollection == "All" ||
                loc.collectionName.equals(selectedCollection, ignoreCase = true)

        val matchesFav = !showOnlyFavorites || loc.isFavorite

        matchesQuery && matchesCollection && matchesFav
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openCreateSavedLocationDialog(null) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_saved_location_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Saved Location")
            }
        },
        modifier = modifier
            .fillMaxSize()
            .testTag("saved_locations_screen")
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search your saved places, rooms, notes...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("search_saved_locations_input")
            )

            // Collection Tabs & Favorites Filter
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    ElevatedFilterChip(
                        selected = showOnlyFavorites,
                        onClick = { showOnlyFavorites = !showOnlyFavorites },
                        label = { Text("★ Starred") },
                        leadingIcon = {
                            Icon(
                                imageVector = if (showOnlyFavorites) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = null,
                                tint = if (showOnlyFavorites) Color(0xFFF59E0B) else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.elevatedFilterChipColors(
                            selectedContainerColor = Color(0xFFFFFBEB)
                        ),
                        modifier = Modifier.testTag("filter_chip_favorites")
                    )
                }

                items(collections) { col ->
                    val isSelected = (selectedCollection == null && col == "All") || selectedCollection == col
                    ElevatedFilterChip(
                        selected = isSelected,
                        onClick = { selectedCollection = if (col == "All") null else col },
                        label = { Text(col) },
                        modifier = Modifier.testTag("collection_chip_${col.lowercase().replace(" ", "_")}")
                    )
                }
            }

            // Quick "Save Current Location" Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("quick_save_current_pos_card")
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(CampusEmerald.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PinDrop,
                            contentDescription = null,
                            tint = CampusEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Save Current Location",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Pin where you parked your bike or your current study desk",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.quickSaveCurrentPosition("My Saved Spot", LocationCategory.PERSONAL, "Personal")
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.testTag("quick_save_btn")
                    ) {
                        Text("Pin Here", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            // Locations List or Empty State
            if (filteredLocations.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.BookmarkBorder,
                    title = if (searchQuery.isNotEmpty()) "No Matching Places" else "No Saved Places Yet",
                    description = if (searchQuery.isNotEmpty()) {
                        "Try a different search query or collection filter."
                    } else {
                        "Bookmark your classrooms, favorite study spots, cafeterias, and parking locations to navigate there with one tap."
                    },
                    actionLabel = "Add a Campus Place",
                    onActionClick = { viewModel.openCreateSavedLocationDialog(null) },
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .testTag("saved_locations_list")
                ) {
                    items(filteredLocations, key = { it.id }) { item ->
                        SavedLocationItemCard(
                            location = item,
                            onNavigate = {
                                viewModel.startNavigationToSaved(item)
                                onNavigateToRoute()
                            },
                            onToggleFavorite = { viewModel.toggleLocationFavorite(item) },
                            onEdit = { viewModel.openEditSavedLocationDialog(item) },
                            onDelete = { viewModel.deleteLocationItem(item) }
                        )
                    }
                }
            }
        }

        // Add / Edit Dialog
        if (showSaveDialog && locationToEdit != null) {
            SaveLocationDialog(
                initialLocation = locationToEdit!!,
                buildings = buildings,
                onSave = { updated -> viewModel.saveLocationItem(updated) },
                onDismiss = { viewModel.dismissSaveDialog() }
            )
        }
    }
}

@Composable
fun SavedLocationItemCard(
    location: SavedLocation,
    onNavigate: () -> Unit,
    onToggleFavorite: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tagColor = try {
        Color(android.graphics.Color.parseColor(location.tagColorHex))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("saved_location_item_${location.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Color bar + Title + Favorite Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Color Tag Bar
                Box(
                    modifier = Modifier
                        .size(width = 6.dp, height = 36.dp)
                        .background(tagColor, RoundedCornerShape(3.dp))
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = location.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (location.roomNumber.isNotBlank()) {
                                "${location.buildingName} • ${location.roomNumber}"
                            } else {
                                location.buildingName
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Favorite Star
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.testTag("fav_btn_${location.id}")
                ) {
                    Icon(
                        imageVector = if (location.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Toggle favorite",
                        tint = if (location.isFavorite) Color(0xFFF59E0B) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Collection & Category Badges
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = tagColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = location.collectionName,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = tagColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = location.category.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                if (location.floorNumber > 0) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Floor ${location.floorNumber}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Notes if any
            if (location.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "📝 ${location.notes}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("edit_location_btn_${location.id}")
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit location",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("delete_location_btn_${location.id}")
                    ) {
                        Icon(
                            Icons.Default.DeleteOutline,
                            contentDescription = "Delete location",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Button(
                    onClick = onNavigate,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("navigate_saved_btn_${location.id}")
                ) {
                    Icon(
                        Icons.Default.DirectionsWalk,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Navigate", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}
