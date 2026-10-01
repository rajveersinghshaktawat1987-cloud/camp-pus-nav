package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.model.CampusBuilding
import com.example.data.model.NavigationHistory
import com.example.data.model.RecentSearch
import com.example.data.model.SavedLocation

@Database(
    entities = [
        CampusBuilding::class,
        SavedLocation::class,
        NavigationHistory::class,
        RecentSearch::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(CampusConverters::class)
abstract class CampusDatabase : RoomDatabase() {
    abstract fun buildingDao(): BuildingDao
    abstract fun savedLocationDao(): SavedLocationDao
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: CampusDatabase? = null

        fun getInstance(context: Context): CampusDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CampusDatabase::class.java,
                    "campus_navigation.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
