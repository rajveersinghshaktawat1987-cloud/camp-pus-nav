package com.example.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessible
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CampusProfile
import com.example.ui.theme.CampusEmerald
import com.example.ui.theme.CollegiateNavy
import com.example.ui.viewmodel.CampusViewModel

data class OnboardingSlide(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val highlightBadge: String
)

@Composable
fun OnboardingScreen(
    viewModel: CampusViewModel,
    onFinishOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentSlideIndex by remember { mutableIntStateOf(0) }
    var selectedProfile by remember { mutableStateOf(CampusProfile.UNDERGRADUATE) }

    val slides = listOf(
        OnboardingSlide(
            icon = Icons.Default.CompassCalibration,
            title = "Navigate Campus with Confidence",
            description = "Offline-first campus maps with accurate walking times, shortcuts through arcades, and step-free accessible routes.",
            highlightBadge = "Offline Ready • Zero Data Required"
        ),
        OnboardingSlide(
            icon = Icons.Default.Layers,
            title = "Multi-Floor Building Blueprints",
            description = "Explore classrooms, research labs, elevators, quiet study nooks, printers, and restrooms on any floor level.",
            highlightBadge = "Interactive Floor Directory"
        ),
        OnboardingSlide(
            icon = Icons.Default.Bookmark,
            title = "Save Your Spots & Parked Bike",
            description = "One-tap pin where you parked your bike or car, organize class lecture halls into folders, and star favorite study desks.",
            highlightBadge = "Personal Campus Collections"
        ),
        OnboardingSlide(
            icon = Icons.Default.School,
            title = "Personalize Your Experience",
            description = "Select your campus role to prioritize relevant departments, buildings, and walking preferences.",
            highlightBadge = "Custom Tailored Profiles"
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .testTag("onboarding_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Skip Button Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            if (currentSlideIndex < slides.size - 1) {
                TextButton(
                    onClick = {
                        viewModel.completeOnboarding(selectedProfile)
                        onFinishOnboarding()
                    },
                    modifier = Modifier.testTag("onboarding_skip_btn")
                ) {
                    Text("Skip", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                Spacer(modifier = Modifier.height(48.dp))
            }
        }

        // Center Content Carousel Slide
        val currentSlide = slides[currentSlideIndex]

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Icon Emblem
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = currentSlide.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(56.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Highlight Badge
            Surface(
                color = CampusEmerald.copy(alpha = 0.15f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = currentSlide.highlightBadge,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = CampusEmerald,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = currentSlide.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = currentSlide.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // If on last slide: show Profile Selection
            if (currentSlideIndex == 3) {
                Spacer(modifier = Modifier.height(20.dp))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        CampusProfile.values().forEach { profile ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedProfile = profile }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedProfile == profile,
                                    onClick = { selectedProfile = profile },
                                    modifier = Modifier.testTag("onboarding_radio_${profile.name.lowercase()}")
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = profile.label,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = profile.description,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Controls: Page Dots + Next / Get Started Button
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Indicator Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                slides.indices.forEach { index ->
                    val isCurrent = index == currentSlideIndex
                    Box(
                        modifier = Modifier
                            .size(if (isCurrent) 24.dp else 8.dp, 8.dp)
                            .background(
                                if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                RoundedCornerShape(4.dp)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Button
            Button(
                onClick = {
                    if (currentSlideIndex < slides.size - 1) {
                        currentSlideIndex++
                    } else {
                        viewModel.completeOnboarding(selectedProfile)
                        onFinishOnboarding()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("onboarding_action_btn")
            ) {
                Text(
                    text = if (currentSlideIndex < slides.size - 1) "Continue" else "Get Started",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}
