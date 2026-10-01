package com.example.navigation

sealed class NavDestination(val route: String, val title: String) {
    data object Onboarding : NavDestination("onboarding", "Welcome")
    data object Map : NavDestination("map", "Campus Map")
    data object Saved : NavDestination("saved", "Saved Places")
    data object Search : NavDestination("search", "Directory")
    data object Settings : NavDestination("settings", "Settings")
    data object NavigationActive : NavDestination("navigation_active", "Turn Guidance")
}
