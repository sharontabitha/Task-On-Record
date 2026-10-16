package com.taskonrecord.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val VillageDarkColorScheme = darkColorScheme(
    primary = SkyBlueDark,
    onPrimary = PixelWhite,
    primaryContainer = DarkNavy,
    onPrimaryContainer = CollegeAccent,
    secondary = GardenGreen,
    onSecondary = PixelWhite,
    secondaryContainer = DarkGrass,
    onSecondaryContainer = GardenAccent,
    tertiary = SunOrange,
    onTertiary = DarkNight,
    background = DarkNight,
    onBackground = PixelWhite,
    surface = MidnightBlue,
    onSurface = StarWhite,
    surfaceVariant = CardBackground,
    onSurfaceVariant = PixelGray,
    error = StatusOverdue,
    onError = PixelWhite,
    outline = CardBorder,
    outlineVariant = PixelDarkGray,
    scrim = Color.Black.copy(alpha = 0.6f)
)

@Composable
fun TaskOnRecordTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = VillageDarkColorScheme,
        typography = Typography,
        content = content
    )
}
