package com.example.ui.screens.map

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Elevator
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Wc
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.window.Dialog
import com.example.data.local.CampusSeedData
import com.example.data.model.BuildingFloorPlan
import com.example.data.model.CampusBuilding
import com.example.data.model.FloorRoom
import com.example.data.model.RoomType
import com.example.ui.theme.CampusEmerald
import com.example.ui.theme.CollegiateNavy

@Composable
fun FloorPlanDialog(
    building: CampusBuilding,
    currentFloorNumber: Int,
    onSelectFloor: (Int) -> Unit,
    onSaveRoomClick: (FloorRoom) -> Unit,
    onDismiss: () -> Unit
) {
    val floorPlans = CampusSeedData.floorPlans.filter { it.buildingId == building.id }
    val currentPlan = floorPlans.find { it.floorNumber == currentFloorNumber }
        ?: floorPlans.firstOrNull()

    var selectedRoom by remember { mutableStateOf<FloorRoom?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("floor_plan_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = building.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Interactive Indoor Floor Directory",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_floor_plan_btn")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Floor Level Selector Tabs
                Text(
                    text = "Select Floor Level:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(floorPlans) { plan ->
                        FilterChip(
                            selected = plan.floorNumber == currentPlan?.floorNumber,
                            onClick = {
                                onSelectFloor(plan.floorNumber)
                                selectedRoom = null
                            },
                            label = { Text("Level ${plan.floorNumber}") },
                            modifier = Modifier.testTag("floor_level_chip_${plan.floorNumber}")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Current Floor Name
                currentPlan?.let { plan ->
                    Text(
                        text = plan.floorName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Indoor Floor Blueprint Box (Canvas-like relative positioning)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.5f)
                            .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        // Corridor background guides
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                                .align(Alignment.Center)
                                .background(Color(0xFFE2E8F0))
                        )

                        // Rooms positioned by relX and relY
                        for (room in plan.rooms) {
                            val isSelected = selectedRoom?.roomId == room.roomId
                            val roomColor = when (room.type) {
                                RoomType.CLASSROOM -> Color(0xFF3B82F6)
                                RoomType.LAB -> Color(0xFF8B5CF6)
                                RoomType.ELEVATOR -> Color(0xFF0284C7)
                                RoomType.RESTROOM -> Color(0xFF10B981)
                                RoomType.STUDY_AREA -> Color(0xFFF59E0B)
                                RoomType.CAFE -> Color(0xFFD97706)
                                else -> Color(0xFF64748B)
                            }

                            // Calculate alignment / placement in the box
                            Box(
                                modifier = Modifier
                                    .align(
                                        when {
                                            room.relX < 0.4f && room.relY < 0.5f -> Alignment.TopStart
                                            room.relX > 0.6f && room.relY < 0.5f -> Alignment.TopEnd
                                            room.relX < 0.4f && room.relY >= 0.5f -> Alignment.BottomStart
                                            room.relX > 0.6f && room.relY >= 0.5f -> Alignment.BottomEnd
                                            room.relY < 0.3f -> Alignment.TopCenter
                                            room.relY > 0.7f -> Alignment.BottomCenter
                                            else -> Alignment.Center
                                        }
                                    )
                                    .padding(4.dp)
                                    .clickable { selectedRoom = room }
                            ) {
                                Surface(
                                    color = if (isSelected) roomColor else roomColor.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = roomColor
                                    ),
                                    modifier = Modifier.padding(2.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = when (room.type) {
                                                RoomType.CLASSROOM -> Icons.Default.MeetingRoom
                                                RoomType.LAB -> Icons.Default.Science
                                                RoomType.ELEVATOR -> Icons.Default.Elevator
                                                RoomType.RESTROOM -> Icons.Default.Wc
                                                RoomType.STUDY_AREA -> Icons.Default.MenuBook
                                                RoomType.CAFE -> Icons.Default.LocalCafe
                                                else -> Icons.Default.MeetingRoom
                                            },
                                            contentDescription = null,
                                            tint = if (isSelected) Color.White else roomColor,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = room.name.take(14),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else roomColor
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Selected Room Card with "Save as Favorite"
                selectedRoom?.let { room ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = room.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "${room.type.label} • Floor ${room.floorNumber} ${if (room.capacity != null) "• Capacity: ${room.capacity}" else ""}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.clickable { onSaveRoomClick(room) }
                            ) {
                                Text(
                                    text = "Bookmark",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                } ?: run {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Tap any room or elevator to view details and bookmark it",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
