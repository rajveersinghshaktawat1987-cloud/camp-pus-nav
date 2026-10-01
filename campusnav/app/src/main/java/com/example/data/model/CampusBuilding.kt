package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class BuildingCategory(val displayName: String, val iconName: String, val colorHex: String) {
    ACADEMIC("Academic", "school", "#1E40AF"),
    LIBRARY("Library & Study", "local_library", "#047857"),
    DINING("Dining & Cafes", "restaurant", "#D97706"),
    STUDENT_CENTER("Student Center", "groups", "#7C3AED"),
    DORMITORY("Housing & Dorms", "bed", "#4338CA"),
    SPORTS("Sports & Gym", "fitness_center", "#DC2626"),
    PARKING("Parking & Transit", "local_parking", "#475569"),
    ADMINISTRATION("Services & Admin", "account_balance", "#0284C7"),
    HEALTH("Health & Wellness", "medical_services", "#E11D48"),
    AMENITY("Campus Amenity", "place", "#059669")
}

@Entity(tableName = "buildings")
data class CampusBuilding(
    @PrimaryKey
    val id: String,
    val code: String,
    val name: String,
    val category: BuildingCategory,
    val description: String,
    val numFloors: Int,
    val isAccessible: Boolean,
    val openHours: String,
    val amenities: List<String>,
    // Normalized campus coordinates (0..1000) for vector map rendering
    val campusX: Float,
    val campusY: Float,
    val width: Float,
    val height: Float,
    // Real world GPS coordinates for Ivy/Stanford style campus
    val latitude: Double,
    val longitude: Double,
    // List of major departments/labs/rooms in this building
    val highlights: List<String>
)
