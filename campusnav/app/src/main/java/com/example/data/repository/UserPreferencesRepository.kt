package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.CampusProfile
import com.example.data.model.DistanceUnit
import com.example.data.model.MapThemeStyle
import com.example.data.model.RoutePreference
import com.example.data.model.UserSettings
import com.example.data.model.WalkingSpeed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserPreferencesRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("campus_nav_prefs", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<UserSettings> = _settings.asStateFlow()

    private fun loadSettings(): UserSettings {
        return UserSettings(
            hasCompletedOnboarding = prefs.getBoolean("onboarding_done", false),
            profile = try {
                CampusProfile.valueOf(prefs.getString("profile", CampusProfile.UNDERGRADUATE.name)!!)
            } catch (e: Exception) {
                CampusProfile.UNDERGRADUATE
            },
            walkingSpeed = try {
                WalkingSpeed.valueOf(prefs.getString("walking_speed", WalkingSpeed.NORMAL.name)!!)
            } catch (e: Exception) {
                WalkingSpeed.NORMAL
            },
            routePreference = try {
                RoutePreference.valueOf(prefs.getString("route_pref", RoutePreference.QUICKEST.name)!!)
            } catch (e: Exception) {
                RoutePreference.QUICKEST
            },
            distanceUnit = try {
                DistanceUnit.valueOf(prefs.getString("unit", DistanceUnit.METERS.name)!!)
            } catch (e: Exception) {
                DistanceUnit.METERS
            },
            mapTheme = try {
                MapThemeStyle.valueOf(prefs.getString("map_theme", MapThemeStyle.COLLEGIATE_EMERALD.name)!!)
            } catch (e: Exception) {
                MapThemeStyle.COLLEGIATE_EMERALD
            },
            arrivalNotificationsEnabled = prefs.getBoolean("arrival_notifs", true),
            hapticVibrationEnabled = prefs.getBoolean("haptics", true),
            showAmenitiesLayer = prefs.getBoolean("layer_amenities", true),
            showParkingLayer = prefs.getBoolean("layer_parking", true),
            showTransitLayer = prefs.getBoolean("layer_transit", true),
            simulatedLocationEnabled = prefs.getBoolean("sim_location", true)
        )
    }

    fun setOnboardingCompleted(completed: Boolean, profile: CampusProfile) {
        prefs.edit()
            .putBoolean("onboarding_done", completed)
            .putString("profile", profile.name)
            .apply()
        _settings.value = loadSettings()
    }

    fun updateWalkingSpeed(speed: WalkingSpeed) {
        prefs.edit().putString("walking_speed", speed.name).apply()
        _settings.value = loadSettings()
    }

    fun updateRoutePreference(pref: RoutePreference) {
        prefs.edit().putString("route_pref", pref.name).apply()
        _settings.value = loadSettings()
    }

    fun updateDistanceUnit(unit: DistanceUnit) {
        prefs.edit().putString("unit", unit.name).apply()
        _settings.value = loadSettings()
    }

    fun updateMapTheme(theme: MapThemeStyle) {
        prefs.edit().putString("map_theme", theme.name).apply()
        _settings.value = loadSettings()
    }

    fun updateArrivalNotifications(enabled: Boolean) {
        prefs.edit().putBoolean("arrival_notifs", enabled).apply()
        _settings.value = loadSettings()
    }

    fun updateHaptics(enabled: Boolean) {
        prefs.edit().putBoolean("haptics", enabled).apply()
        _settings.value = loadSettings()
    }

    fun toggleLayer(layerKey: String, enabled: Boolean) {
        when (layerKey) {
            "amenities" -> prefs.edit().putBoolean("layer_amenities", enabled).apply()
            "parking" -> prefs.edit().putBoolean("layer_parking", enabled).apply()
            "transit" -> prefs.edit().putBoolean("layer_transit", enabled).apply()
        }
        _settings.value = loadSettings()
    }

    fun setSimulatedLocation(enabled: Boolean) {
        prefs.edit().putBoolean("sim_location", enabled).apply()
        _settings.value = loadSettings()
    }

    fun resetPreferences() {
        prefs.edit().clear().apply()
        _settings.value = loadSettings()
    }
}
