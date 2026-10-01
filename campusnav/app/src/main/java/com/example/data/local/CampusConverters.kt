package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.BuildingCategory
import com.example.data.model.LocationCategory

class CampusConverters {
    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return value?.joinToString(";;;") ?: ""
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value.isNullOrBlank()) return emptyList()
        return value.split(";;;").filter { it.isNotBlank() }
    }

    @TypeConverter
    fun fromBuildingCategory(category: BuildingCategory?): String {
        return category?.name ?: BuildingCategory.ACADEMIC.name
    }

    @TypeConverter
    fun toBuildingCategory(name: String?): BuildingCategory {
        return try {
            if (name != null) BuildingCategory.valueOf(name) else BuildingCategory.ACADEMIC
        } catch (e: Exception) {
            BuildingCategory.ACADEMIC
        }
    }

    @TypeConverter
    fun fromLocationCategory(category: LocationCategory?): String {
        return category?.name ?: LocationCategory.CLASSROOM.name
    }

    @TypeConverter
    fun toLocationCategory(name: String?): LocationCategory {
        return try {
            if (name != null) LocationCategory.valueOf(name) else LocationCategory.CLASSROOM
        } catch (e: Exception) {
            LocationCategory.CLASSROOM
        }
    }
}
