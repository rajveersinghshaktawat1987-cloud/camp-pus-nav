package com.example.ui.screens.routing

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessible
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Elevator
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Stairs
import androidx.compose.material.icons.filled.TurnLeft
import androidx.compose.material.icons.filled.TurnRight
import androidx.compose.material.icons.filled.TurnSlightLeft
import androidx.compose.material.icons.filled.TurnSlightRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.DistanceUnit
import com.example.data.model.RouteStep
import com.example.data.model.TurnType
import com.example.ui.screens.components.EmptyStateView
import com.example.ui.theme.CampusEmerald
import com.example.ui.theme.CollegiateNavy
import com.example.ui.viewmodel.CampusViewModel

@Composable
fun ActiveNavigationScreen(
    viewModel: CampusViewModel,
    onBackToMap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeRoute by viewModel.activeRoute.collectAsStateWithLifecycle()
    val currentStepIndex by viewModel.currentStepIndex.collectAsStateWithLifecycle()
    val isSimulatingWalk by viewModel.isSimulatingWalk.collectAsStateWithLifecycle()
    val hasArrived by viewModel.hasArrived.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    BackHandler {
        onBackToMap()
    }

    val route = activeRoute
    if (route == null) {
        EmptyStateView(
            icon = Icons.Default.DirectionsWalk,
            title = "No Active Route",
            description = "Select a building, room, or saved place on the campus map to start turn-by-turn guidance.",
            actionLabel = "Open Campus Map",
            onActionClick = onBackToMap,
            modifier = modifier
        )
        return
    }

    val currentStep = if (currentStepIndex < route.steps.size) route.steps[currentStepIndex] else route.steps.last()
    val progress = (currentStepIndex + 1).toFloat() / route.steps.size.toFloat()

    val distStr = if (settings.distanceUnit == DistanceUnit.FEET) {
        "${(currentStep.distanceMeters * 3.28f).toInt()} ft"
    } else {
        "${currentStep.distanceMeters} m"
    }

    val totalDistStr = if (settings.distanceUnit == DistanceUnit.FEET) {
        "${(route.totalDistanceMeters * 3.28f).toInt()} ft"
    } else {
        "${route.totalDistanceMeters} m"
    }

    val totalDurationMin = (route.estimatedDurationSeconds / 60).coerceAtLeast(1)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("active_navigation_screen")
    ) {
        // High-Visibility Guidance Header Banner
        Surface(
            color = CollegiateNavy,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Top Row: Back to Map + Close Guidance
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = CampusEmerald,
                            shape = CircleShape,
                            modifier = Modifier.size(10.dp)
                        ) {}
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "LIVE GUIDANCE",
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    IconButton(
                        onClick = {
                            viewModel.stopNavigation()
                            onBackToMap()
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("stop_navigation_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Stop Navigation",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Big Turn Icon + Distance + Instruction
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getTurnIcon(currentStep.turnType),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (hasArrived) "Destination Reached" else "In $distStr",
                            color = Color.White.copy(alpha = 0.85f),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (hasArrived) "You have arrived at ${route.destinationTitle}" else currentStep.instruction,
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = currentStep.detail,
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // Progress bar and Route Summary Card
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = CampusEmerald,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "$totalDurationMin min ($totalDistStr)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Destination: ${route.destinationTitle}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (route.isAccessibleMode) {
                        Surface(
                            color = CampusEmerald.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Accessible,
                                    contentDescription = "Accessible",
                                    tint = CampusEmerald,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Step-Free",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CampusEmerald,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Arrival Card (when reached destination)
        if (hasArrived) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CampusEmerald.copy(alpha = 0.1f)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, CampusEmerald),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("arrival_card")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Arrived",
                        tint = CampusEmerald,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "You've Arrived!",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = CampusEmerald
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Welcome to ${route.destinationTitle}. Navigation complete.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            viewModel.stopNavigation()
                            onBackToMap()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CampusEmerald),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("finish_navigation_btn")
                    ) {
                        Text("Finish & Return to Map")
                    }
                }
            }
        }

        // Upcoming Step-by-Step Directions List
        Text(
            text = "Upcoming Directions (${route.steps.size} steps):",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("steps_list")
        ) {
            itemsIndexed(route.steps) { index, step ->
                val isPast = index < currentStepIndex
                val isCurrent = index == currentStepIndex

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            isCurrent -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            isPast -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            else -> MaterialTheme.colorScheme.surface
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .testTag("step_card_$index")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = getTurnIcon(step.turnType),
                                contentDescription = null,
                                tint = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = step.instruction,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                color = if (isPast) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${step.distanceMeters}m • ${step.detail}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Bottom Controls Bar (Simulation, Step, Reroute)
        Surface(
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Play/Pause Walk Simulation
                Button(
                    onClick = { viewModel.toggleSimulatedWalk() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSimulatingWalk) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("simulate_walk_btn")
                ) {
                    Icon(
                        imageVector = if (isSimulatingWalk) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isSimulatingWalk) "Pause" else "Simulate")
                }

                // Advance Step
                OutlinedButton(
                    onClick = { viewModel.advanceStep() },
                    enabled = currentStepIndex < route.steps.size - 1 && !hasArrived,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("advance_step_btn")
                ) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Next Step")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Next")
                }

                // Reroute
                OutlinedButton(
                    onClick = { viewModel.reroute() },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("reroute_btn")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reroute")
                }
            }
        }
    }
}

fun getTurnIcon(turnType: TurnType): ImageVector {
    return when (turnType) {
        TurnType.START -> Icons.Default.DirectionsWalk
        TurnType.STRAIGHT -> Icons.Default.ArrowForward
        TurnType.TURN_LEFT -> Icons.Default.TurnLeft
        TurnType.TURN_RIGHT -> Icons.Default.TurnRight
        TurnType.SLIGHT_LEFT -> Icons.Default.TurnSlightLeft
        TurnType.SLIGHT_RIGHT -> Icons.Default.TurnSlightRight
        TurnType.ENTER_BUILDING -> Icons.Default.LocationOn
        TurnType.TAKE_ELEVATOR -> Icons.Default.Elevator
        TurnType.TAKE_STAIRS -> Icons.Default.Stairs
        TurnType.DESTINATION -> Icons.Default.CheckCircle
    }
}
