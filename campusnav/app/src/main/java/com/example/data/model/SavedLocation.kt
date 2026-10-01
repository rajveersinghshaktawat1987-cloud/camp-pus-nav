package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class LocationCategory(val label: String, val defaultColor: String) {
    CLASSROOM("Classroom", "#3B82F6"),
    STUDY_SPOT("Study Spot", "#10B981"),
    FOOD_COFFEE("Food & Cafe", "#F59E0B"),
    PARKING_BIKE("Parking / Bike", "#64748B"),
    LAB_RESEARCH("Lab / Maker", "#8B5CF6"),
    OFFICE("Faculty / Office", "#06B6D4"),
    PERSONAL("Personal Spot", "#EC4899")
}

@Entity(tableName = "saved_locations")
data class SavedLocation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val buildingId: String? = null,
    val buildingName: String = "",
    val floorNumber: Int = 1,
    val roomNumber: String = "",
    val category: LocationCategory = LocationCategory.CLASSROOM,
    val tagColorHex: String = "#3B82F6",
    val notes: String = "",
    val campusX: Float,
    val campusY: Float,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val dateCreated: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val collectionName: String = "General" // e.g. "My Classes", "Study Nooks", "Favorite Eats", "Parking"
)
