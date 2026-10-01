package com.example.ui.screens.map

import android.graphics.Paint
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import com.example.data.local.CampusSeedData
import com.example.data.model.BuildingCategory
import com.example.data.model.CampusBuilding
import com.example.data.model.CampusRoute
import com.example.data.model.MapThemeStyle
import com.example.data.model.SavedLocation
import com.example.ui.theme.CollegiateNavy
import com.example.ui.theme.MapBuilding
import com.example.ui.theme.MapBuildingOutline
import com.example.ui.theme.MapGrass
import com.example.ui.theme.MapGrassDark
import com.example.ui.theme.MapPathway
import com.example.ui.theme.MapPathwayBorder
import com.example.ui.theme.MapRouteLine
import com.example.ui.theme.MapUserPin
import com.example.ui.theme.MapUserPulse

@Composable
fun CampusMapCanvas(
    buildings: List<CampusBuilding>,
    savedLocations: List<SavedLocation>,
    selectedBuilding: CampusBuilding?,
    activeRoute: CampusRoute?,
    userCampusX: Float,
    userCampusY: Float,
    mapTheme: MapThemeStyle,
    showParkingLayer: Boolean,
    showTransitLayer: Boolean,
    scale: Float,
    offsetX: Float,
    offsetY: Float,
    onTransformChange: (Float, Float, Float) -> Unit,
    onBuildingClick: (CampusBuilding) -> Unit,
    onMapTap: (Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 12f,
        targetValue = 32f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseRadius"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    // Palette depending on map theme
    val bgColor = when (mapTheme) {
        MapThemeStyle.COLLEGIATE_EMERALD -> MapGrass
        MapThemeStyle.MODERN_SLATE -> Color(0xFFE2E8F0)
        MapThemeStyle.HIGH_CONTRAST -> Color(0xFF1E293B)
    }

    val pathwayColor = when (mapTheme) {
        MapThemeStyle.COLLEGIATE_EMERALD -> MapPathway
        MapThemeStyle.MODERN_SLATE -> Color(0xFFFFFFFF)
        MapThemeStyle.HIGH_CONTRAST -> Color(0xFF334155)
    }

    val buildingColor = when (mapTheme) {
        MapThemeStyle.COLLEGIATE_EMERALD -> MapBuilding
        MapThemeStyle.MODERN_SLATE -> Color(0xFFCBD5E1)
        MapThemeStyle.HIGH_CONTRAST -> Color(0xFF0F172A)
    }

    val textColor = when (mapTheme) {
        MapThemeStyle.HIGH_CONTRAST -> Color.White
        else -> CollegiateNavy
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
            .testTag("campus_map_canvas")
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    val newScale = (scale * zoom).coerceIn(0.5f, 3.5f)
                    val newOffsetX = offsetX + pan.x
                    val newOffsetY = offsetY + pan.y
                    onTransformChange(newScale, newOffsetX, newOffsetY)
                }
            }
            .pointerInput(buildings, scale, offsetX, offsetY) {
                detectTapGestures { tapOffset ->
                    // Convert screen pixel coordinates to campus normalized coordinates
                    val campusX = (tapOffset.x - offsetX) / scale
                    val campusY = (tapOffset.y - offsetY) / scale

                    // Check if a building was tapped
                    val clicked = buildings.find { b ->
                        campusX >= b.campusX && campusX <= b.campusX + b.width &&
                                campusY >= b.campusY && campusY <= b.campusY + b.height
                    }

                    if (clicked != null) {
                        onBuildingClick(clicked)
                    } else {
                        onMapTap(campusX, campusY)
                    }
                }
            }
    ) {
        // Draw Grand Oval Green Lawn
        val ovalLeft = 380f * scale + offsetX
        val ovalTop = 410f * scale + offsetY
        val ovalWidth = 340f * scale
        val ovalHeight = 240f * scale
        drawOval(
            color = if (mapTheme == MapThemeStyle.HIGH_CONTRAST) Color(0xFF064E3B) else Color(0xFFC7E3C4),
            topLeft = Offset(ovalLeft, ovalTop),
            size = Size(ovalWidth, ovalHeight)
        )

        // Draw Pathway Network
        for (edge in CampusSeedData.pathEdges) {
            val n1 = CampusSeedData.pathNodes.find { it.id == edge.fromId }
            val n2 = CampusSeedData.pathNodes.find { it.id == edge.toId }
            if (n1 != null && n2 != null) {
                val p1 = Offset(n1.x * scale + offsetX, n1.y * scale + offsetY)
                val p2 = Offset(n2.x * scale + offsetX, n2.y * scale + offsetY)

                // Border casing
                drawLine(
                    color = MapPathwayBorder,
                    start = p1,
                    end = p2,
                    strokeWidth = 14f * scale,
                    cap = StrokeCap.Round
                )
                // Path center fill
                drawLine(
                    color = pathwayColor,
                    start = p1,
                    end = p2,
                    strokeWidth = 10f * scale,
                    cap = StrokeCap.Round
                )
            }
        }

        // Draw Active Route if exists
        if (activeRoute != null && activeRoute.pathPoints.size >= 2) {
            val routePath = Path()
            val firstPt = activeRoute.pathPoints.first()
            routePath.moveTo(firstPt.first * scale + offsetX, firstPt.second * scale + offsetY)

            for (i in 1 until activeRoute.pathPoints.size) {
                val pt = activeRoute.pathPoints[i]
                routePath.lineTo(pt.first * scale + offsetX, pt.second * scale + offsetY)
            }

            // Outer glow
            drawPath(
                path = routePath,
                color = MapRouteLine.copy(alpha = 0.35f),
                style = Stroke(
                    width = 16f * scale,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // Inner solid route line
            drawPath(
                path = routePath,
                color = MapRouteLine,
                style = Stroke(
                    width = 8f * scale,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // Draw waypoint dots along the steps
            for (step in activeRoute.steps) {
                val pt = Offset(step.targetCampusX * scale + offsetX, step.targetCampusY * scale + offsetY)
                drawCircle(
                    color = Color.White,
                    radius = 6f * scale,
                    center = pt
                )
                drawCircle(
                    color = MapRouteLine,
                    radius = 4f * scale,
                    center = pt
                )
            }
        }

        // Draw Buildings
        val paint = Paint().apply {
            isAntiAlias = true
            textSize = 11f * scale
            color = textColor.toArgb()
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
        }

        val codePaint = Paint().apply {
            isAntiAlias = true
            textSize = 9f * scale
            color = Color(0xFF64748B).toArgb()
            textAlign = Paint.Align.CENTER
        }

        for (building in buildings) {
            val bx = building.campusX * scale + offsetX
            val by = building.campusY * scale + offsetY
            val bw = building.width * scale
            val bh = building.height * scale

            val isSelected = selectedBuilding?.id == building.id

            // Subtle drop shadow for building
            drawRoundRect(
                color = Color.Black.copy(alpha = 0.12f),
                topLeft = Offset(bx + 3f * scale, by + 4f * scale),
                size = Size(bw, bh),
                cornerRadius = CornerRadius(8f * scale, 8f * scale)
            )

            // Building base fill
            drawRoundRect(
                color = if (isSelected) Color(0xFFE0E7FF) else buildingColor,
                topLeft = Offset(bx, by),
                size = Size(bw, bh),
                cornerRadius = CornerRadius(8f * scale, 8f * scale)
            )

            // Category accent header strip
            val categoryColor = Color(android.graphics.Color.parseColor(building.category.colorHex))
            drawRoundRect(
                color = categoryColor,
                topLeft = Offset(bx, by),
                size = Size(bw, 6f * scale),
                cornerRadius = CornerRadius(8f * scale, 8f * scale)
            )

            // Building stroke
            drawRoundRect(
                color = if (isSelected) MapRouteLine else MapBuildingOutline,
                topLeft = Offset(bx, by),
                size = Size(bw, bh),
                cornerRadius = CornerRadius(8f * scale, 8f * scale),
                style = Stroke(width = if (isSelected) 3f * scale else 1.2f * scale)
            )

            // Building text (Only render when zoomed enough)
            if (scale >= 0.7f) {
                val label = if (bw < 100f * scale) building.code else building.name.take(18)
                drawContext.canvas.nativeCanvas.drawText(
                    building.code,
                    bx + bw / 2f,
                    by + 20f * scale,
                    codePaint
                )
                drawContext.canvas.nativeCanvas.drawText(
                    label,
                    bx + bw / 2f,
                    by + 36f * scale,
                    paint
                )

                // Multi-floor badge
                if (building.numFloors > 1 && bw > 70f * scale) {
                    val badgeX = bx + bw - 18f * scale
                    val badgeY = by + bh - 10f * scale
                    val floorBadgePaint = Paint().apply {
                        isAntiAlias = true
                        textSize = 8f * scale
                        color = Color(0xFF475569).toArgb()
                        textAlign = Paint.Align.RIGHT
                    }
                    drawContext.canvas.nativeCanvas.drawText(
                        "${building.numFloors}F",
                        badgeX,
                        badgeY,
                        floorBadgePaint
                    )
                }
            }
        }

        // Draw Saved Location Pins
        for (saved in savedLocations) {
            val sx = saved.campusX * scale + offsetX
            val sy = saved.campusY * scale + offsetY

            val tagColor = try {
                Color(android.graphics.Color.parseColor(saved.tagColorHex))
            } catch (e: Exception) {
                Color(0xFF3B82F6)
            }

            // Pin shadow
            drawCircle(
                color = Color.Black.copy(alpha = 0.2f),
                radius = 8f * scale,
                center = Offset(sx + 1f, sy + 2f)
            )
            // Pin outer ring
            drawCircle(
                color = Color.White,
                radius = 7f * scale,
                center = Offset(sx, sy)
            )
            // Pin inner fill
            drawCircle(
                color = tagColor,
                radius = 5f * scale,
                center = Offset(sx, sy)
            )

            // Favorite star badge
            if (saved.isFavorite) {
                drawCircle(
                    color = Color(0xFFF59E0B),
                    radius = 2.5f * scale,
                    center = Offset(sx + 5f * scale, sy - 5f * scale)
                )
            }
        }

        // Draw User Current Location Pin with Pulse
        val ux = userCampusX * scale + offsetX
        val uy = userCampusY * scale + offsetY

        // Pulse wave
        drawCircle(
            color = MapUserPulse.copy(alpha = pulseAlpha),
            radius = pulseRadius * scale,
            center = Offset(ux, uy)
        )
        // Outer halo
        drawCircle(
            color = Color.White,
            radius = 10f * scale,
            center = Offset(ux, uy)
        )
        // Core dot
        drawCircle(
            color = MapUserPin,
            radius = 7f * scale,
            center = Offset(ux, uy)
        )
    }
}
