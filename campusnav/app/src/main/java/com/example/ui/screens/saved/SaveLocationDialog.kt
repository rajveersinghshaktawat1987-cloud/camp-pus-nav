package com.example.ui.screens.saved

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.model.CampusBuilding
import com.example.data.model.LocationCategory
import com.example.data.model.SavedLocation

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SaveLocationDialog(
    initialLocation: SavedLocation,
    buildings: List<CampusBuilding>,
    onSave: (SavedLocation) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(initialLocation.name) }
    var selectedBuilding by remember {
        mutableStateOf(buildings.find { it.id == initialLocation.buildingId } ?: buildings.firstOrNull())
    }
    var floorNumber by remember { mutableIntStateOf(initialLocation.floorNumber) }
    var roomNumber by remember { mutableStateOf(initialLocation.roomNumber) }
    var category by remember { mutableStateOf(initialLocation.category) }
    var tagColorHex by remember { mutableStateOf(initialLocation.tagColorHex) }
    var collectionName by remember { mutableStateOf(initialLocation.collectionName) }
    var notes by remember { mutableStateOf(initialLocation.notes) }

    var buildingDropdownExpanded by remember { mutableStateOf(false) }

    val availableColors = listOf(
        "#3B82F6", // Blue
        "#10B981", // Emerald
        "#F59E0B", // Amber
        "#8B5CF6", // Purple
        "#EC4899", // Pink
        "#06B6D4", // Cyan
        "#64748B"  // Slate
    )

    val collections = listOf("My Classes", "Study Nooks", "Favorite Eats", "Parking & Transit", "Personal")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("save_location_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (initialLocation.id == 0L) "Save Campus Place" else "Edit Saved Place",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_save_dialog_btn")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Place Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Place or Spot Name") },
                    placeholder = { Text("e.g. Chemistry 101 Lab, Window Alcove") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_location_name")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Category Chips
                Text(
                    text = "Category:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    LocationCategory.values().forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = {
                                category = cat
                                tagColorHex = cat.defaultColor
                            },
                            label = { Text(cat.label, style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.testTag("chip_cat_${cat.name.lowercase()}")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Building Dropdown
                ExposedDropdownMenuBox(
                    expanded = buildingDropdownExpanded,
                    onExpandedChange = { buildingDropdownExpanded = !buildingDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedBuilding?.name ?: "Select Building",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Building") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = buildingDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("building_selector_dropdown")
                    )

                    ExposedDropdownMenu(
                        expanded = buildingDropdownExpanded,
                        onDismissRequest = { buildingDropdownExpanded = false }
                    ) {
                        buildings.forEach { b ->
                            DropdownMenuItem(
                                text = { Text("${b.code} • ${b.name}") },
                                onClick = {
                                    selectedBuilding = b
                                    buildingDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Floor & Room row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = if (floorNumber > 0) "Floor $floorNumber" else "Ground",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Floor") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_floor")
                    )

                    OutlinedTextField(
                        value = roomNumber,
                        onValueChange = { roomNumber = it },
                        label = { Text("Room / Spot #") },
                        placeholder = { Text("e.g. 204B") },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("input_room")
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Collection selector
                Text(
                    text = "Collection / Folder:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    collections.forEach { col ->
                        FilterChip(
                            selected = collectionName == col,
                            onClick = { collectionName = col },
                            label = { Text(col, style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.testTag("chip_col_${col.lowercase().replace(" ", "_")}")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Color Tag Picker
                Text(
                    text = "Color Tag:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    availableColors.forEach { hex ->
                        val color = Color(android.graphics.Color.parseColor(hex))
                        val isSelected = tagColorHex == hex
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(color, CircleShape)
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White,
                                    shape = CircleShape
                                )
                                .clickable { tagColorHex = hex }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Helpful Notes (optional)") },
                    placeholder = { Text("e.g. Quietest table near back window, outlet under desk") },
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_notes")
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("cancel_save_btn")
                    ) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                val b = selectedBuilding
                                onSave(
                                    initialLocation.copy(
                                        name = name.trim(),
                                        buildingId = b?.id,
                                        buildingName = b?.name ?: "",
                                        floorNumber = floorNumber,
                                        roomNumber = roomNumber.trim(),
                                        category = category,
                                        tagColorHex = tagColorHex,
                                        collectionName = collectionName,
                                        notes = notes.trim(),
                                        campusX = b?.let { it.campusX + it.width / 2f } ?: initialLocation.campusX,
                                        campusY = b?.let { it.campusY + it.height / 2f } ?: initialLocation.campusY
                                    )
                                )
                            }
                        },
                        enabled = name.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("confirm_save_btn")
                    ) {
                        Text("Save Location")
                    }
                }
            }
        }
    }
}
