package com.example.ui.screens.settings

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessible
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CampusProfile
import com.example.data.model.DistanceUnit
import com.example.data.model.MapThemeStyle
import com.example.data.model.RoutePreference
import com.example.data.model.WalkingSpeed
import com.example.ui.theme.CampusEmerald
import com.example.ui.viewmodel.CampusViewModel

@Composable
fun SettingsScreen(
    viewModel: CampusViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val navigationHistory by viewModel.navigationHistory.collectAsStateWithLifecycle()

    var showResetDialog by remember { mutableStateOf(false) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.updateArrivalNotifications(isGranted)
        if (isGranted) {
            viewModel.showMessage("Notification permission granted")
        } else {
            viewModel.showMessage("Notifications disabled in system settings")
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("settings_screen")
    ) {
        Text(
            text = "Settings & Preferences",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Tailor campus navigation to your routine and accessibility needs",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campus Profile Section
        SettingsCard(
            icon = Icons.Default.Person,
            title = "Campus Profile",
            subtitle = settings.profile.label
        ) {
            CampusProfile.values().forEach { profile ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.completeOnboarding(profile) }
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = settings.profile == profile,
                        onClick = { viewModel.completeOnboarding(profile) },
                        modifier = Modifier.testTag("profile_radio_${profile.name.lowercase()}")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = profile.label,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = profile.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Walking Speed Preference
        SettingsCard(
            icon = Icons.Default.DirectionsWalk,
            title = "Walking Speed",
            subtitle = settings.walkingSpeed.label
        ) {
            WalkingSpeed.values().forEach { speed ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.updateWalkingSpeed(speed) }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = settings.walkingSpeed == speed,
                        onClick = { viewModel.updateWalkingSpeed(speed) },
                        modifier = Modifier.testTag("speed_radio_${speed.name.lowercase()}")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = speed.label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Route Mode Preference (Accessible, Quickest, Covered)
        SettingsCard(
            icon = Icons.Default.Accessible,
            title = "Routing Preference",
            subtitle = settings.routePreference.label
        ) {
            RoutePreference.values().forEach { pref ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.updateRoutePreference(pref) }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = settings.routePreference == pref,
                        onClick = { viewModel.updateRoutePreference(pref) },
                        modifier = Modifier.testTag("route_pref_radio_${pref.name.lowercase()}")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = pref.label,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (settings.routePreference == pref) FontWeight.Bold else FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Units of Measurement
        SettingsCard(
            icon = Icons.Default.Straighten,
            title = "Distance Units",
            subtitle = settings.distanceUnit.label
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                DistanceUnit.values().forEach { unit ->
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.updateDistanceUnit(unit) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = settings.distanceUnit == unit,
                            onClick = { viewModel.updateDistanceUnit(unit) },
                            modifier = Modifier.testTag("unit_radio_${unit.name.lowercase()}")
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(unit.label, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Map Visual Style
        SettingsCard(
            icon = Icons.Default.Map,
            title = "Campus Map Style",
            subtitle = settings.mapTheme.label
        ) {
            MapThemeStyle.values().forEach { style ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.updateMapTheme(style) }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = settings.mapTheme == style,
                        onClick = { viewModel.updateMapTheme(style) },
                        modifier = Modifier.testTag("theme_radio_${style.name.lowercase()}")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(style.label, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Notifications & Haptics
        SettingsCard(
            icon = Icons.Default.Notifications,
            title = "Arrival Alerts & Turn Cues",
            subtitle = "Notifications and haptic cues when approaching turns and destination"
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Arrival Notifications",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Banner notification when reaching your destination",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = settings.arrivalNotificationsEnabled,
                    onCheckedChange = { checked ->
                        if (checked && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                return@Switch
                            }
                        }
                        viewModel.updateArrivalNotifications(checked)
                    },
                    modifier = Modifier.testTag("switch_arrival_notifications")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Haptic Vibration Cues",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Subtle vibration pulse on turns and arrival",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = settings.hapticVibrationEnabled,
                    onCheckedChange = { viewModel.updateHaptics(it) },
                    modifier = Modifier.testTag("switch_haptics")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Test Notification Button
            Button(
                onClick = { viewModel.testArrivalNotification() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("send_test_notification_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Send Test Arrival Notification",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Navigation History Section
        if (navigationHistory.isNotEmpty()) {
            SettingsCard(
                icon = Icons.Default.History,
                title = "Recent Trip History",
                subtitle = "${navigationHistory.size} completed trips"
            ) {
                navigationHistory.take(4).forEach { trip ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${trip.startTitle} → ${trip.destinationTitle}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${trip.distanceMeters}m • ${trip.durationSeconds / 60} min ${if (trip.isAccessible) "• Step-Free" else ""}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Divider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                }

                TextButton(
                    onClick = { viewModel.clearNavHistory() },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Clear Trip History")
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Data Management / Reset
        SettingsCard(
            icon = Icons.Default.Refresh,
            title = "Data Management",
            subtitle = "Reset campus database to defaults or restore initial bookmarks"
        ) {
            OutlinedButton(
                onClick = { showResetDialog = true },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reset_campus_data_btn")
            ) {
                Icon(Icons.Default.DeleteSweep, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reset to Default Campus Data")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Campus Data?") },
            text = { Text("This will restore default campus buildings, pathways, and sample saved places. Custom places will be reset.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetCampusData()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SettingsCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            content()
        }
    }
}
