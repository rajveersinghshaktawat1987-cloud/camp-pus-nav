package com.example.data.repository

import com.example.data.local.BuildingDao
import com.example.data.local.CampusSeedData
import com.example.data.local.HistoryDao
import com.example.data.local.SavedLocationDao
import com.example.data.model.BuildingCategory
import com.example.data.model.CampusBuilding
import com.example.data.model.CampusRoute
import com.example.data.model.FloorRoom
import com.example.data.model.NavigationHistory
import com.example.data.model.RecentSearch
import com.example.data.model.RoutePreference
import com.example.data.model.RouteStep
import com.example.data.model.SavedLocation
import com.example.data.model.TurnType
import com.example.data.model.WalkingSpeed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.roundToInt

class CampusRepository(
    private val buildingDao: BuildingDao,
    private val savedLocationDao: SavedLocationDao,
    private val historyDao: HistoryDao
) {

    val allBuildings: Flow<List<CampusBuilding>> = buildingDao.getAllBuildings()
    val allSavedLocations: Flow<List<SavedLocation>> = savedLocationDao.getAllSavedLocations()
    val favoriteLocations: Flow<List<SavedLocation>> = savedLocationDao.getFavoriteLocations()
    val allCollections: Flow<List<String>> = savedLocationDao.getAllCollections()
    val recentSearches: Flow<List<RecentSearch>> = historyDao.getRecentSearches()
    val navigationHistory: Flow<List<NavigationHistory>> = historyDao.getNavigationHistory()

    suspend fun initializeDatabaseIfNeeded() = withContext(Dispatchers.IO) {
        if (buildingDao.getBuildingCount() == 0) {
            buildingDao.insertBuildings(CampusSeedData.buildings)
        }
        if (savedLocationDao.getCount() == 0) {
            savedLocationDao.insertSavedLocations(CampusSeedData.defaultSavedLocations)
        }
    }

    suspend fun resetToDefaultCampusData() = withContext(Dispatchers.IO) {
        buildingDao.insertBuildings(CampusSeedData.buildings)
        savedLocationDao.deleteAll()
        savedLocationDao.insertSavedLocations(CampusSeedData.defaultSavedLocations)
        historyDao.clearRecentSearches()
        historyDao.clearHistory()
    }

    suspend fun getBuildingById(id: String): CampusBuilding? = withContext(Dispatchers.IO) {
        buildingDao.getBuildingById(id)
    }

    fun getBuildingsByCategory(category: BuildingCategory): Flow<List<CampusBuilding>> {
        return buildingDao.getBuildingsByCategory(category)
    }

    fun searchBuildings(query: String): Flow<List<CampusBuilding>> {
        return buildingDao.searchBuildings(query)
    }

    suspend fun saveLocation(location: SavedLocation): Long = withContext(Dispatchers.IO) {
        savedLocationDao.insertSavedLocation(location)
    }

    suspend fun updateLocation(location: SavedLocation) = withContext(Dispatchers.IO) {
        savedLocationDao.updateSavedLocation(location)
    }

    suspend fun deleteLocation(location: SavedLocation) = withContext(Dispatchers.IO) {
        savedLocationDao.deleteSavedLocation(location)
    }

    suspend fun deleteLocationById(id: Long) = withContext(Dispatchers.IO) {
        savedLocationDao.deleteSavedLocationById(id)
    }

    suspend fun toggleFavorite(id: Long, isFav: Boolean) = withContext(Dispatchers.IO) {
        savedLocationDao.updateFavorite(id, isFav)
    }

    suspend fun addRecentSearch(query: String, targetId: String? = null, targetName: String = "") = withContext(Dispatchers.IO) {
        if (query.isNotBlank()) {
            historyDao.insertSearch(RecentSearch(query = query.trim(), targetId = targetId, targetName = targetName))
        }
    }

    suspend fun clearRecentSearches() = withContext(Dispatchers.IO) {
        historyDao.clearRecentSearches()
    }

    suspend fun recordCompletedNavigation(history: NavigationHistory) = withContext(Dispatchers.IO) {
        historyDao.insertHistory(history)
    }

    suspend fun clearNavigationHistory() = withContext(Dispatchers.IO) {
        historyDao.clearHistory()
    }

    fun getFloorPlan(buildingId: String, floorNumber: Int) =
        CampusSeedData.floorPlans.find { it.buildingId == buildingId && it.floorNumber == floorNumber }

    fun getAllFloorsForBuilding(buildingId: String) =
        CampusSeedData.floorPlans.filter { it.buildingId == buildingId }.sortedBy { it.floorNumber }

    // Routing Engine with Dijkstra algorithm
    suspend fun calculateRoute(
        startX: Float,
        startY: Float,
        startTitle: String,
        destX: Float,
        destY: Float,
        destTitle: String,
        routePreference: RoutePreference = RoutePreference.QUICKEST,
        walkingSpeed: WalkingSpeed = WalkingSpeed.NORMAL
    ): CampusRoute = withContext(Dispatchers.Default) {
        val isAccessibleMode = routePreference == RoutePreference.ACCESSIBLE_ELEVATORS
        val nodes = CampusSeedData.pathNodes
        val edges = CampusSeedData.pathEdges

        // Find nearest path nodes to start and destination
        val startNode = nodes.minByOrNull { hypot(it.x - startX, it.y - startY) } ?: nodes.first()
        val destNode = nodes.minByOrNull { hypot(it.x - destX, it.y - destY) } ?: nodes.last()

        // Adjacency map
        val adj = mutableMapOf<String, MutableList<Pair<CampusSeedData.PathNode, Float>>>()
        for (node in nodes) {
            adj[node.id] = mutableListOf()
        }

        for (edge in edges) {
            // In accessible mode, skip stairs or heavily penalize
            if (isAccessibleMode && edge.hasStairs) continue

            val nodeA = nodes.find { it.id == edge.fromId }
            val nodeB = nodes.find { it.id == edge.toId }
            if (nodeA != null && nodeB != null) {
                val dist = hypot(nodeA.x - nodeB.x, nodeA.y - nodeB.y)
                adj[nodeA.id]?.add(Pair(nodeB, dist))
                adj[nodeB.id]?.add(Pair(nodeA, dist))
            }
        }

        // Dijkstra algorithm
        val distMap = mutableMapOf<String, Float>()
        val prevMap = mutableMapOf<String, String>()
        val unvisited = nodes.map { it.id }.toMutableSet()

        for (n in nodes) {
            distMap[n.id] = Float.MAX_VALUE
        }
        distMap[startNode.id] = 0f

        while (unvisited.isNotEmpty()) {
            val currentId = unvisited.minByOrNull { distMap[it] ?: Float.MAX_VALUE } ?: break
            val currentDist = distMap[currentId] ?: Float.MAX_VALUE
            if (currentDist == Float.MAX_VALUE || currentId == destNode.id) break
            unvisited.remove(currentId)

            val neighbors = adj[currentId] ?: emptyList()
            for ((neighbor, weight) in neighbors) {
                if (neighbor.id in unvisited) {
                    val alt = currentDist + weight
                    if (alt < (distMap[neighbor.id] ?: Float.MAX_VALUE)) {
                        distMap[neighbor.id] = alt
                        prevMap[neighbor.id] = currentId
                    }
                }
            }
        }

        // Reconstruct path
        val pathNodeIds = mutableListOf<String>()
        var curr: String? = destNode.id
        while (curr != null) {
            pathNodeIds.add(0, curr)
            curr = prevMap[curr]
            if (curr == startNode.id) {
                pathNodeIds.add(0, startNode.id)
                break
            }
        }

        // If no graph path, fallback to straight line
        val resolvedNodes = if (pathNodeIds.size >= 2) {
            pathNodeIds.mapNotNull { id -> nodes.find { it.id == id } }
        } else {
            listOf(startNode, destNode)
        }

        // Build continuous points from (startX, startY) -> path points -> (destX, destY)
        val allPoints = mutableListOf<Pair<Float, Float>>()
        allPoints.add(Pair(startX, startY))
        for (node in resolvedNodes) {
            allPoints.add(Pair(node.x, node.y))
        }
        allPoints.add(Pair(destX, destY))

        // Calculate total distance in meters (1 map canvas unit ~ 0.95 meters)
        var totalCanvasDistance = 0f
        for (i in 0 until allPoints.size - 1) {
            val p1 = allPoints[i]
            val p2 = allPoints[i + 1]
            totalCanvasDistance += hypot(p1.first - p2.first, p1.second - p2.second)
        }
        val distanceMeters = (totalCanvasDistance * 0.95f).roundToInt().coerceAtLeast(30)
        val durationSeconds = (distanceMeters / walkingSpeed.speedMps).roundToInt().coerceAtLeast(30)

        // Generate Turn-by-Turn Steps
        val steps = mutableListOf<RouteStep>()
        steps.add(
            RouteStep(
                stepIndex = 1,
                instruction = "Start from $startTitle",
                detail = "Head towards ${resolvedNodes.first().label}",
                turnType = TurnType.START,
                distanceMeters = (hypot(startX - resolvedNodes.first().x, startY - resolvedNodes.first().y) * 0.95f).roundToInt(),
                targetCampusX = resolvedNodes.first().x,
                targetCampusY = resolvedNodes.first().y,
                isAccessible = true
            )
        )

        for (i in 0 until resolvedNodes.size - 1) {
            val currNode = resolvedNodes[i]
            val nextNode = resolvedNodes[i + 1]
            val stepDist = (hypot(currNode.x - nextNode.x, currNode.y - nextNode.y) * 0.95f).roundToInt()

            // Calculate bearing angle to determine Turn Left / Turn Right
            val turn = if (i == 0) {
                TurnType.STRAIGHT
            } else {
                val prevNode = resolvedNodes[i - 1]
                val anglePrev = atan2((currNode.y - prevNode.y).toDouble(), (currNode.x - prevNode.x).toDouble())
                val angleNext = atan2((nextNode.y - currNode.y).toDouble(), (nextNode.x - currNode.x).toDouble())
                var diff = Math.toDegrees(angleNext - anglePrev)
                while (diff < -180) diff += 360
                while (diff > 180) diff -= 360

                when {
                    diff in -45.0..45.0 -> TurnType.STRAIGHT
                    diff in 45.0..135.0 -> TurnType.TURN_RIGHT
                    diff in -135.0..-45.0 -> TurnType.TURN_LEFT
                    diff > 135.0 -> TurnType.TURN_RIGHT
                    else -> TurnType.TURN_LEFT
                }
            }

            val instruction = when (turn) {
                TurnType.STRAIGHT -> "Continue along walkway towards ${nextNode.label}"
                TurnType.TURN_RIGHT -> "Turn right onto ${nextNode.label}"
                TurnType.TURN_LEFT -> "Turn left towards ${nextNode.label}"
                else -> "Walk along path to ${nextNode.label}"
            }

            steps.add(
                RouteStep(
                    stepIndex = steps.size + 1,
                    instruction = instruction,
                    detail = if (isAccessibleMode) "Step-free ramp / level paving" else "Paved walkway",
                    turnType = turn,
                    distanceMeters = stepDist.coerceAtLeast(15),
                    targetCampusX = nextNode.x,
                    targetCampusY = nextNode.y,
                    isAccessible = true
                )
            )
        }

        // Final step: Destination
        steps.add(
            RouteStep(
                stepIndex = steps.size + 1,
                instruction = "Arrive at $destTitle",
                detail = if (isAccessibleMode) "Use automatic door entrance & elevator" else "Enter through main entrance",
                turnType = TurnType.DESTINATION,
                distanceMeters = (hypot(resolvedNodes.last().x - destX, resolvedNodes.last().y - destY) * 0.95f).roundToInt().coerceAtLeast(10),
                targetCampusX = destX,
                targetCampusY = destY,
                isAccessible = true
            )
        )

        CampusRoute(
            routeId = "route_${System.currentTimeMillis()}",
            startTitle = startTitle,
            destinationTitle = destTitle,
            totalDistanceMeters = distanceMeters,
            estimatedDurationSeconds = durationSeconds,
            isAccessibleMode = isAccessibleMode,
            steps = steps,
            pathPoints = allPoints
        )
    }
}
