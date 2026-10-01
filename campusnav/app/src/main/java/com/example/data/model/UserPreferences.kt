package com.example.data.model

enum class WalkingSpeed(val label: String, val speedMps: Float) {
    ACCESSIBLE("Accessible / Leisurely (0.8 m/s)", 0.8f),
    NORMAL("Standard Walk (1.2 m/s)", 1.2f),
    BRISK("Brisk / Fast (1.5 m/s)", 1.5f)
}

enum class RoutePreference(val label: String) {
    QUICKEST("Quickest Path"),
    ACCESSIBLE_ELEVATORS("Accessible & Step-Free"),
    COVERED_PASSAGES("Covered / Weather-Protected")
}

enum class DistanceUnit(val label: String) {
    METERS("Meters (m)"),
    FEET("Feet (ft)")
}

enum class MapThemeStyle(val label: String) {
    MODERN_SLATE("Modern Slate"),
    COLLEGIATE_EMERALD("Collegiate Emerald"),
    HIGH_CONTRAST("High Contrast")
}

enum class CampusProfile(val label: String, val description: String) {
    UNDERGRADUATE("Undergraduate Student", "Focus on classrooms, dining, dorms & student center"),
    GRADUATE("Graduate & Researcher", "Focus on labs, specialized libraries & departmental offices"),
    FACULTY_STAFF("Faculty & Staff", "Focus on admin, faculty parking, meeting halls & offices"),
    VISITOR("Campus Visitor & Guest", "Focus on visitor centers, parking, landmarks & public amenities")
}

data class UserSettings(
    val hasCompletedOnboarding: Boolean = false,
    val profile: CampusProfile = CampusProfile.UNDERGRADUATE,
    val walkingSpeed: WalkingSpeed = WalkingSpeed.NORMAL,
    val routePreference: RoutePreference = RoutePreference.QUICKEST,
    val distanceUnit: DistanceUnit = DistanceUnit.METERS,
    val mapTheme: MapThemeStyle = MapThemeStyle.COLLEGIATE_EMERALD,
    val arrivalNotificationsEnabled: Boolean = true,
    val hapticVibrationEnabled: Boolean = true,
    val showAmenitiesLayer: Boolean = true,
    val showParkingLayer: Boolean = true,
    val showTransitLayer: Boolean = true,
    val simulatedLocationEnabled: Boolean = true // Seamless on emulator or when GPS is unavailable
)
