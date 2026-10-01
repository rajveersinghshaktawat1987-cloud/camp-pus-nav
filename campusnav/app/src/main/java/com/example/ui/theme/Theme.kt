package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = CollegiateNavy,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6E4FF),
    onPrimaryContainer = Color(0xFF001A40),
    secondary = CampusEmerald,
    onSecondary = Color.White,
    secondaryContainer = CampusEmeraldLight,
    onSecondaryContainer = CampusEmeraldDark,
    tertiary = CampusAmber,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFECCF),
    onTertiaryContainer = Color(0xFF5A2C00),
    background = SurfaceLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceContainerLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceContainerHighestLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = BorderLight,
    outlineVariant = Color(0xFFCBD5E1)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFA5C8FF),
    onPrimary = CollegiateNavyDark,
    primaryContainer = CollegiateBlue,
    onPrimaryContainer = Color(0xFFD6E4FF),
    secondary = Color(0xFF4EDE9F),
    onSecondary = Color(0xFF003822),
    secondaryContainer = Color(0xFF005233),
    onSecondaryContainer = Color(0xFF8CFBBE),
    tertiary = CampusGold,
    onTertiary = Color(0xFF452200),
    tertiaryContainer = Color(0xFF653400),
    onTertiaryContainer = Color(0xFFFFDDB8),
    background = SurfaceDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceContainerDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceContainerHighestDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = BorderDark,
    outlineVariant = Color(0xFF334155)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent collegiate branding
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
