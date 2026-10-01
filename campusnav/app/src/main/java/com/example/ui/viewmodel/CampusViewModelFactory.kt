package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.local.CampusDatabase
import com.example.data.repository.CampusRepository
import com.example.data.repository.UserPreferencesRepository
import com.example.service.NotificationHelper

class CampusViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CampusViewModel::class.java)) {
            val db = CampusDatabase.getInstance(context)
            val campusRepo = CampusRepository(
                buildingDao = db.buildingDao(),
                savedLocationDao = db.savedLocationDao(),
                historyDao = db.historyDao()
            )
            val prefsRepo = UserPreferencesRepository(context)
            val notifHelper = NotificationHelper(context)
            return CampusViewModel(campusRepo, prefsRepo, notifHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
