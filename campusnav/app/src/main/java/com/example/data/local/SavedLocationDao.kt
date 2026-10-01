package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.SavedLocation
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedLocationDao {
    @Query("SELECT * FROM saved_locations ORDER BY isFavorite DESC, dateCreated DESC")
    fun getAllSavedLocations(): Flow<List<SavedLocation>>

    @Query("SELECT * FROM saved_locations WHERE isFavorite = 1 ORDER BY dateCreated DESC")
    fun getFavoriteLocations(): Flow<List<SavedLocation>>

    @Query("SELECT * FROM saved_locations WHERE collectionName = :collection ORDER BY dateCreated DESC")
    fun getLocationsByCollection(collection: String): Flow<List<SavedLocation>>

    @Query("SELECT DISTINCT collectionName FROM saved_locations ORDER BY collectionName ASC")
    fun getAllCollections(): Flow<List<String>>

    @Query("SELECT * FROM saved_locations WHERE id = :id")
    suspend fun getSavedLocationById(id: Long): SavedLocation?

    @Query("""
        SELECT * FROM saved_locations 
        WHERE name LIKE '%' || :query || '%' 
           OR buildingName LIKE '%' || :query || '%'
           OR roomNumber LIKE '%' || :query || '%'
           OR notes LIKE '%' || :query || '%'
        ORDER BY isFavorite DESC, dateCreated DESC
    """)
    fun searchSavedLocations(query: String): Flow<List<SavedLocation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedLocation(location: SavedLocation): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedLocations(locations: List<SavedLocation>)

    @Update
    suspend fun updateSavedLocation(location: SavedLocation)

    @Delete
    suspend fun deleteSavedLocation(location: SavedLocation)

    @Query("DELETE FROM saved_locations WHERE id = :id")
    suspend fun deleteSavedLocationById(id: Long)

    @Query("UPDATE saved_locations SET isFavorite = :isFav WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFav: Boolean)

    @Query("SELECT COUNT(*) FROM saved_locations")
    suspend fun getCount(): Int

    @Query("DELETE FROM saved_locations")
    suspend fun deleteAll()
}
