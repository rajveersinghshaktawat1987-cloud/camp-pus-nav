package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TurnType {
    START,
    STRAIGHT,
    TURN_LEFT,
    TURN_RIGHT,
    SLIGHT_LEFT,
    SLIGHT_RIGHT,
    ENTER_BUILDING,
    TAKE_ELEVATOR,
    TAKE_STAIRS,
    DESTINATION
}

data class RouteStep(
    val stepIndex: Int,
    val instruction: String,
    val detail: String,
    val turnType: TurnType,
    val distanceMeters: Int,
    val targetCampusX: Float,
    val targetCampusY: Float,
    val isAccessible: Boolean = true
)

data class CampusRoute(
    val routeId: String,
    val startTitle: String,
    val destinationTitle: String,
    val totalDistanceMeters: Int,
    val estimatedDurationSeconds: Int,
    val isAccessibleMode: Boolean,
    val steps: List<RouteStep>,
    val pathPoints: List<Pair<Float, Float>>
)

@Entity(tableName = "navigation_history")
data class NavigationHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startTitle: String,
    val destinationTitle: String,
    val distanceMeters: Int,
    val durationSeconds: Int,
    val isAccessible: Boolean,
    val completedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "recent_searches")
data class RecentSearch(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val query: String,
    val targetId: String? = null,
    val targetName: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
