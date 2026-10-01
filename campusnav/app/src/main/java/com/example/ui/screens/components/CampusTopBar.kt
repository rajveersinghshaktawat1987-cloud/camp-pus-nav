package com.example.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.CampusProfile
import com.example.navigation.NavDestination
import com.example.ui.theme.CampusEmerald
import com.example.ui.theme.CollegiateNavy

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampusTopBar(
    currentRoute: String,
    profile: CampusProfile,
    isNavigatingActive: Boolean,
    onNavigateToSearch: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CompassCalibration,
                        contentDescription = "CampusNav",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "CampusNav",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = profile.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        actions = {
            if (isNavigatingActive) {
                Surface(
                    color = CampusEmerald.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(CampusEmerald, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Navigating",
                            style = MaterialTheme.typography.labelSmall,
                            color = CampusEmerald,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            IconButton(
                onClick = onNavigateToSearch,
                modifier = Modifier.testTag("topbar_search_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search campus directory"
                )
            }

            IconButton(
                onClick = onNavigateToSettings,
                modifier = Modifier.testTag("topbar_settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Open Settings"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = modifier.testTag("campus_top_bar")
    )
}

@Composable
fun CampusBottomBar(
    currentRoute: String,
    savedLocationsCount: Int,
    isNavigatingActive: Boolean,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        modifier = modifier.testTag("campus_bottom_bar")
    ) {
        NavigationBarItem(
            selected = currentRoute == NavDestination.Map.route && !isNavigatingActive,
            onClick = { onNavigate(NavDestination.Map.route) },
            icon = {
                Icon(Icons.Default.Map, contentDescription = "Campus Map")
            },
            label = { Text("Map") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.testTag("nav_tab_map")
        )

        if (isNavigatingActive) {
            NavigationBarItem(
                selected = currentRoute == NavDestination.NavigationActive.route,
                onClick = { onNavigate(NavDestination.NavigationActive.route) },
                icon = {
                    Icon(
                        Icons.Default.DirectionsWalk,
                        contentDescription = "Active Turn Guidance",
                        tint = CampusEmerald
                    )
                },
                label = { Text("Guidance", color = CampusEmerald, fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("nav_tab_guidance")
            )
        }

        NavigationBarItem(
            selected = currentRoute == NavDestination.Saved.route,
            onClick = { onNavigate(NavDestination.Saved.route) },
            icon = {
                BadgedBox(
                    badge = {
                        if (savedLocationsCount > 0) {
                            Badge { Text("$savedLocationsCount") }
                        }
                    }
                ) {
                    Icon(Icons.Default.Bookmark, contentDescription = "Saved Locations")
                }
            },
            label = { Text("Saved") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.testTag("nav_tab_saved")
        )

        NavigationBarItem(
            selected = currentRoute == NavDestination.Search.route,
            onClick = { onNavigate(NavDestination.Search.route) },
            icon = {
                Icon(Icons.Default.Search, contentDescription = "Directory Search")
            },
            label = { Text("Directory") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.testTag("nav_tab_directory")
        )

        NavigationBarItem(
            selected = currentRoute == NavDestination.Settings.route,
            onClick = { onNavigate(NavDestination.Settings.route) },
            icon = {
                Icon(Icons.Default.Settings, contentDescription = "Settings")
            },
            label = { Text("Settings") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.testTag("nav_tab_settings")
        )
    }
}
