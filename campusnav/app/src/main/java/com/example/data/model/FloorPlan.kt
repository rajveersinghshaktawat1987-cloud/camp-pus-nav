package com.example.data.model

data class FloorRoom(
    val roomId: String,
    val name: String,
    val type: RoomType,
    val floorNumber: Int,
    val relX: Float, // 0..1 inside building box
    val relY: Float,
    val isAccessible: Boolean = true,
    val capacity: Int? = null
)

enum class RoomType(val label: String, val icon: String) {
    CLASSROOM("Classroom", "meeting_room"),
    LAB("Lab", "science"),
    ELEVATOR("Elevator", "elevator"),
    STAIRS("Stairs", "stairs"),
    RESTROOM("Restroom", "wc"),
    STUDY_AREA("Study Zone", "menu_book"),
    OFFICE("Faculty Office", "badge"),
    CAFE("Cafe/Vending", "local_cafe")
}

data class BuildingFloorPlan(
    val buildingId: String,
    val floorNumber: Int,
    val floorName: String,
    val rooms: List<FloorRoom>
)
