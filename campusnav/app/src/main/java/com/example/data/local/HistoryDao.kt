package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.NavigationHistory
import com.example.data.model.RecentSearch
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM recent_searches ORDER BY timestamp DESC LIMIT 15")
    fun getRecentSearches(): Flow<List<RecentSearch>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearch(search: RecentSearch)

    @Query("DELETE FROM recent_searches")
    suspend fun clearRecentSearches()

    @Query("SELECT * FROM navigation_history ORDER BY completedAt DESC LIMIT 25")
    fun getNavigationHistory(): Flow<List<NavigationHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: NavigationHistory)

    @Query("DELETE FROM navigation_history")
    suspend fun clearHistory()
}
