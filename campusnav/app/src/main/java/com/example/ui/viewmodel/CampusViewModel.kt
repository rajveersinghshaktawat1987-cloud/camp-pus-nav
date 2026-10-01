package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CampusSeedData
import com.example.data.model.BuildingCategory
import com.example.data.model.BuildingFloorPlan
import com.example.data.model.CampusBuilding
import com.example.data.model.CampusProfile
import com.example.data.model.CampusRoute
import com.example.data.model.DistanceUnit
import com.example.data.model.LocationCategory
import com.example.data.model.MapThemeStyle
import com.example.data.model.NavigationHistory
import com.example.data.model.RecentSearch
import com.example.data.model.RoutePreference
import com.example.data.model.SavedLocation
import com.example.data.model.UserSettings
import com.example.data.model.WalkingSpeed
import com.example.data.repository.CampusRepository
import com.example.data.repository.UserPreferencesRepository
import com.example.service.NotificationHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.hypot

class CampusViewModel(
    private val campusRepository: CampusRepository,
    private val preferencesRepository: UserPreferencesRepository,
    private val notificationHelper: NotificationHelper
) : ViewModel() {

    val settings: StateFlow<UserSettings> = preferencesRepository.settings

    val buildings: StateFlow<List<CampusBuilding>> = campusRepository.allBuildings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedLocations: StateFlow<List<SavedLocation>> = campusRepository.allSavedLocations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentSearches: StateFlow<List<RecentSearch>> = campusRepository.recentSearches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val navigationHistory: StateFlow<List<NavigationHistory>> = campusRepository.navigationHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Map selection & UI
    private val _selectedBuilding = MutableStateFlow<CampusBuilding?>(null)
    val selectedBuilding: StateFlow<CampusBuilding?> = _selectedBuilding.asStateFlow()

    private val _selectedFloorNumber = MutableStateFlow(1)
    val selectedFloorNumber: StateFlow<Int> = _selectedFloorNumber.asStateFlow()

    private val _showFloorPlanDialog = MutableStateFlow(false)
    val showFloorPlanDialog: StateFlow<Boolean> = _showFloorPlanDialog.asStateFlow()

    // User position on campus (Default: Entrance near Student Union)
    private val _userCampusX = MutableStateFlow(275f)
    val userCampusX: StateFlow<Float> = _userCampusX.asStateFlow()

    private val _userCampusY = MutableStateFlow(590f)
    val userCampusY: StateFlow<Float> = _userCampusY.asStateFlow()

    private val _userHeading = MutableStateFlow(0f)
    val userHeading: StateFlow<Float> = _userHeading.asStateFlow()

    // Navigation & Turn Guidance
    private val _activeRoute = MutableStateFlow<CampusRoute?>(null)
    val activeRoute: StateFlow<CampusRoute?> = _activeRoute.asStateFlow()

    private val _currentStepIndex = MutableStateFlow(0)
    val currentStepIndex: StateFlow<Int> = _currentStepIndex.asStateFlow()

    private val _isSimulatingWalk = MutableStateFlow(false)
    val isSimulatingWalk: StateFlow<Boolean> = _isSimulatingWalk.asStateFlow()

    private val _hasArrived = MutableStateFlow(false)
    val hasArrived: StateFlow<Boolean> = _hasArrived.asStateFlow()

    private var walkSimulationJob: Job? = null

    // Search & Filtering
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow<BuildingCategory?>(null)
    val selectedCategoryFilter: StateFlow<BuildingCategory?> = _selectedCategoryFilter.asStateFlow()

    private val _onlyAccessibleFilter = MutableStateFlow(false)
    val onlyAccessibleFilter: StateFlow<Boolean> = _onlyAccessibleFilter.asStateFlow()

    // Combined filtered buildings
    val filteredBuildings: StateFlow<List<CampusBuilding>> = combine(
        buildings,
        searchQuery,
        selectedCategoryFilter,
        onlyAccessibleFilter
    ) { list, query, category, accessibleOnly ->
        list.filter { building ->
            val matchesQuery = query.isBlank() ||
                    building.name.contains(query, ignoreCase = true) ||
                    building.code.contains(query, ignoreCase = true) ||
                    building.description.contains(query, ignoreCase = true) ||
                    building.highlights.any { it.contains(query, ignoreCase = true) } ||
                    building.amenities.any { it.contains(query, ignoreCase = true) }

            val matchesCategory = category == null || building.category == category
            val matchesAccessible = !accessibleOnly || building.isAccessible

            matchesQuery && matchesCategory && matchesAccessible
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Saved places editing dialog
    private val _showSaveDialog = MutableStateFlow(false)
    val showSaveDialog: StateFlow<Boolean> = _showSaveDialog.asStateFlow()

    private val _locationToEdit = MutableStateFlow<SavedLocation?>(null)
    val locationToEdit: StateFlow<SavedLocation?> = _locationToEdit.asStateFlow()

    // Snackbars / Feedback
    private val _snackBarMessage = MutableStateFlow<String?>(null)
    val snackBarMessage: StateFlow<String?> = _snackBarMessage.asStateFlow()

    init {
        viewModelScope.launch {
            campusRepository.initializeDatabaseIfNeeded()
        }
    }

    fun clearSnackBar() {
        _snackBarMessage.value = null
    }

    fun showMessage(msg: String) {
        _snackBarMessage.value = msg
    }

    fun selectBuilding(building: CampusBuilding?) {
        _selectedBuilding.value = building
        _selectedFloorNumber.value = 1
    }

    fun selectFloor(floor: Int) {
        _selectedFloorNumber.value = floor
    }

    fun openFloorPlanDialog(show: Boolean) {
        _showFloorPlanDialog.value = show
    }

    fun getCurrentFloorPlan(): BuildingFloorPlan? {
        val building = _selectedBuilding.value ?: return null
        return campusRepository.getFloorPlan(building.id, _selectedFloorNumber.value)
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.isNotBlank()) {
            viewModelScope.launch {
                campusRepository.addRecentSearch(query)
            }
        }
    }

    fun setCategoryFilter(category: BuildingCategory?) {
        _selectedCategoryFilter.value = if (_selectedCategoryFilter.value == category) null else category
    }

    fun toggleAccessibleFilter() {
        _onlyAccessibleFilter.value = !_onlyAccessibleFilter.value
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _selectedCategoryFilter.value = null
        _onlyAccessibleFilter.value = false
    }

    fun clearRecentSearches() {
        viewModelScope.launch {
            campusRepository.clearRecentSearches()
            showMessage("Recent searches cleared")
        }
    }

    // Navigation flows
    fun startNavigationTo(
        destX: Float,
        destY: Float,
        destTitle: String,
        buildingName: String = ""
    ) {
        viewModelScope.launch {
            val userX = _userCampusX.value
            val userY = _userCampusY.value
            val currentSettings = settings.value

            val route = campusRepository.calculateRoute(
                startX = userX,
                startY = userY,
                startTitle = "Current Location",
                destX = destX,
                destY = destY,
                destTitle = destTitle,
                routePreference = currentSettings.routePreference,
                walkingSpeed = currentSettings.walkingSpeed
            )

            _activeRoute.value = route
            _currentStepIndex.value = 0
            _hasArrived.value = false
            _isSimulatingWalk.value = false

            // Initial notification if enabled
            if (currentSettings.arrivalNotificationsEnabled && route.steps.isNotEmpty()) {
                val firstStep = route.steps.first()
                notificationHelper.sendTurnNotification(firstStep.instruction, "${firstStep.distanceMeters}m")
            }

            if (currentSettings.hapticVibrationEnabled) {
                notificationHelper.triggerTurnHaptic()
            }
        }
    }

    fun startNavigationToBuilding(building: CampusBuilding) {
        startNavigationTo(
            destX = building.campusX + building.width / 2f,
            destY = building.campusY + building.height / 2f,
            destTitle = building.name,
            buildingName = building.name
        )
    }

    fun startNavigationToSaved(saved: SavedLocation) {
        startNavigationTo(
            destX = saved.campusX,
            destY = saved.campusY,
            destTitle = saved.name,
            buildingName = saved.buildingName
        )
    }

    fun advanceStep() {
        val route = _activeRoute.value ?: return
        val nextIndex = _currentStepIndex.value + 1
        if (nextIndex < route.steps.size) {
            _currentStepIndex.value = nextIndex
            val step = route.steps[nextIndex]
            _userCampusX.value = step.targetCampusX
            _userCampusY.value = step.targetCampusY

            if (settings.value.hapticVibrationEnabled) {
                notificationHelper.triggerTurnHaptic()
            }
            if (settings.value.arrivalNotificationsEnabled) {
                notificationHelper.sendTurnNotification(step.instruction, "${step.distanceMeters}m")
            }
        } else {
            completeArrival()
        }
    }

    fun completeArrival() {
        val route = _activeRoute.value ?: return
        _hasArrived.value = true
        _isSimulatingWalk.value = false
        walkSimulationJob?.cancel()

        if (settings.value.hapticVibrationEnabled) {
            notificationHelper.triggerArrivalHaptic()
        }
        if (settings.value.arrivalNotificationsEnabled) {
            notificationHelper.sendArrivalNotification(route.destinationTitle)
        }

        viewModelScope.launch {
            campusRepository.recordCompletedNavigation(
                NavigationHistory(
                    startTitle = route.startTitle,
                    destinationTitle = route.destinationTitle,
                    distanceMeters = route.totalDistanceMeters,
                    durationSeconds = route.estimatedDurationSeconds,
                    isAccessible = route.isAccessibleMode
                )
            )
        }
        showMessage("You have arrived at ${route.destinationTitle}!")
    }

    fun toggleSimulatedWalk() {
        val isSimulating = !_isSimulatingWalk.value
        _isSimulatingWalk.value = isSimulating

        if (isSimulating) {
            startWalkSimulation()
        } else {
            walkSimulationJob?.cancel()
        }
    }

    private fun startWalkSimulation() {
        walkSimulationJob?.cancel()
        walkSimulationJob = viewModelScope.launch {
            val route = _activeRoute.value ?: return@launch
            while (_currentStepIndex.value < route.steps.size - 1) {
                delay(3000) // 3 seconds per step simulated
                advanceStep()
            }
            delay(2000)
            completeArrival()
        }
    }

    fun stopNavigation() {
        walkSimulationJob?.cancel()
        _isSimulatingWalk.value = false
        _activeRoute.value = null
        _hasArrived.value = false
        _currentStepIndex.value = 0
    }

    fun reroute() {
        val route = _activeRoute.value ?: return
        val lastStep = route.steps.last()
        startNavigationTo(
            destX = lastStep.targetCampusX,
            destY = lastStep.targetCampusY,
            destTitle = route.destinationTitle
        )
        showMessage("Rerouting from current position...")
    }

    // Saved places management
    fun openCreateSavedLocationDialog(prefillBuilding: CampusBuilding? = null) {
        val defaultX = prefillBuilding?.let { it.campusX + it.width / 2f } ?: _userCampusX.value
        val defaultY = prefillBuilding?.let { it.campusY + it.height / 2f } ?: _userCampusY.value

        _locationToEdit.value = SavedLocation(
            name = "",
            buildingId = prefillBuilding?.id,
            buildingName = prefillBuilding?.name ?: "",
            floorNumber = 1,
            roomNumber = "",
            category = LocationCategory.CLASSROOM,
            tagColorHex = "#3B82F6",
            notes = "",
            campusX = defaultX,
            campusY = defaultY,
            collectionName = "My Classes"
        )
        _showSaveDialog.value = true
    }

    fun openEditSavedLocationDialog(saved: SavedLocation) {
        _locationToEdit.value = saved
        _showSaveDialog.value = true
    }

    fun dismissSaveDialog() {
        _showSaveDialog.value = false
        _locationToEdit.value = null
    }

    fun saveLocationItem(location: SavedLocation) {
        viewModelScope.launch {
            if (location.id == 0L) {
                campusRepository.saveLocation(location)
                showMessage("Saved \"${location.name}\" to ${location.collectionName}")
            } else {
                campusRepository.updateLocation(location)
                showMessage("Updated \"${location.name}\"")
            }
            dismissSaveDialog()
        }
    }

    fun deleteLocationItem(location: SavedLocation) {
        viewModelScope.launch {
            campusRepository.deleteLocation(location)
            showMessage("Removed \"${location.name}\"")
        }
    }

    fun toggleLocationFavorite(location: SavedLocation) {
        viewModelScope.launch {
            campusRepository.toggleFavorite(location.id, !location.isFavorite)
        }
    }

    fun quickSaveCurrentPosition(name: String, category: LocationCategory, collection: String) {
        viewModelScope.launch {
            val nearestBuilding = buildings.value.minByOrNull {
                hypot(it.campusX + it.width / 2f - _userCampusX.value, it.campusY + it.height / 2f - _userCampusY.value)
            }
            val newLocation = SavedLocation(
                name = name,
                buildingId = nearestBuilding?.id,
                buildingName = nearestBuilding?.name ?: "Campus Quad",
                floorNumber = 1,
                roomNumber = "",
                category = category,
                tagColorHex = category.defaultColor,
                notes = "Saved at current spot",
                campusX = _userCampusX.value,
                campusY = _userCampusY.value,
                collectionName = collection
            )
            campusRepository.saveLocation(newLocation)
            showMessage("Saved \"$name\" at current location!")
        }
    }

    fun setUserPosition(x: Float, y: Float) {
        _userCampusX.value = x.coerceIn(50f, 950f)
        _userCampusY.value = y.coerceIn(50f, 950f)
    }

    // Settings actions
    fun completeOnboarding(profile: CampusProfile) {
        preferencesRepository.setOnboardingCompleted(true, profile)
        showMessage("Welcome to CampusNav, ${profile.label}!")
    }

    fun updateWalkingSpeed(speed: WalkingSpeed) {
        preferencesRepository.updateWalkingSpeed(speed)
    }

    fun updateRoutePreference(pref: RoutePreference) {
        preferencesRepository.updateRoutePreference(pref)
    }

    fun updateDistanceUnit(unit: DistanceUnit) {
        preferencesRepository.updateDistanceUnit(unit)
    }

    fun updateMapTheme(theme: MapThemeStyle) {
        preferencesRepository.updateMapTheme(theme)
    }

    fun updateArrivalNotifications(enabled: Boolean) {
        preferencesRepository.updateArrivalNotifications(enabled)
    }

    fun updateHaptics(enabled: Boolean) {
        preferencesRepository.updateHaptics(enabled)
    }

    fun toggleMapLayer(layer: String, enabled: Boolean) {
        preferencesRepository.toggleLayer(layer, enabled)
    }

    fun toggleSimulatedLocation(enabled: Boolean) {
        preferencesRepository.setSimulatedLocation(enabled)
    }

    fun testArrivalNotification() {
        notificationHelper.sendArrivalNotification("Langdell Memorial Library", "Langdell Hall")
        if (settings.value.hapticVibrationEnabled) {
            notificationHelper.triggerArrivalHaptic()
        }
        showMessage("Test notification sent!")
    }

    fun resetCampusData() {
        viewModelScope.launch {
            campusRepository.resetToDefaultCampusData()
            preferencesRepository.resetPreferences()
            _userCampusX.value = 275f
            _userCampusY.value = 590f
            showMessage("Reset to initial campus data and preferences")
        }
    }

    fun clearNavHistory() {
        viewModelScope.launch {
            campusRepository.clearNavigationHistory()
            showMessage("Navigation history cleared")
        }
    }
}
