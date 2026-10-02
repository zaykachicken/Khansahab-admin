package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFD0DB),
    onPrimaryContainer = Color(0xFF5B0018),
    secondary = SecondaryColor,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE0E6),
    onSecondaryContainer = Color(0xFF480011),
    tertiary = SuccessColor,
    onTertiary = Color.White,
    background = BackgroundColor,
    onBackground = TextPrimary,
    surface = SurfaceColor,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceElevatedColor,
    onSurfaceVariant = TextSecondary,
    outline = SurfaceBorderColor,
    error = ErrorColor,
    onError = Color.White
)

@Composable
fun ZaykaAdminTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
