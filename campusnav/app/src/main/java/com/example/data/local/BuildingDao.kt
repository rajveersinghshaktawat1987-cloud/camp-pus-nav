package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.BuildingCategory
import com.example.data.model.CampusBuilding
import kotlinx.coroutines.flow.Flow

@Dao
interface BuildingDao {
    @Query("SELECT * FROM buildings ORDER BY name ASC")
    fun getAllBuildings(): Flow<List<CampusBuilding>>

    @Query("SELECT * FROM buildings WHERE id = :id")
    suspend fun getBuildingById(id: String): CampusBuilding?

    @Query("SELECT * FROM buildings WHERE category = :category ORDER BY name ASC")
    fun getBuildingsByCategory(category: BuildingCategory): Flow<List<CampusBuilding>>

    @Query("SELECT * FROM buildings WHERE isAccessible = 1 ORDER BY name ASC")
    fun getAccessibleBuildings(): Flow<List<CampusBuilding>>

    @Query("""
        SELECT * FROM buildings 
        WHERE name LIKE '%' || :query || '%' 
           OR code LIKE '%' || :query || '%' 
           OR description LIKE '%' || :query || '%'
           OR highlights LIKE '%' || :query || '%'
           OR amenities LIKE '%' || :query || '%'
        ORDER BY name ASC
    """)
    fun searchBuildings(query: String): Flow<List<CampusBuilding>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBuildings(buildings: List<CampusBuilding>)

    @Query("SELECT COUNT(*) FROM buildings")
    suspend fun getBuildingCount(): Int
}
