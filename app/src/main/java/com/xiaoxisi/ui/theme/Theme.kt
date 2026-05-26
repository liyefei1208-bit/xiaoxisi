package com.xiaoxisi.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ============================================================
// Brand colors - 小希司 mascot color scheme
// ============================================================

// Orange brand palette (primary)
val Orange = Color(0xFFFF6B2B)
val OrangeLight = Color(0xFFFF9F60)
val OrangeDark = Color(0xFFC44A10)
val OrangeDeep = Color(0xFFE55A1A)
val OrangeSurface = Color(0xFFFFF0E5)
val OrangeDim = Color(0x10FF6B2B)

// Green accent palette (secondary, send button, dialect)
val GreenAccent = Color(0xFF00B898)
val GreenLight = Color(0xFF00D4AF)
val GreenSurface = Color(0xFFE5F9F5)

// Warm neutral palette
val WarmWhite = Color(0xFFFFF8F0)
val Cream = Color(0xFFF5EDE4)
val BrownDark = Color(0xFF1C0E05)
val BrownMedium = Color(0xFF8C6A52)
val BrownBorder = Color(0x26B46E3C)

// Semantic
val Red = Color(0xFFE53935)
val RedSurface = Color(0xFFFDECEC)
val Gold = Color(0xFFFFB347)
val White = Color(0xFFFFFFFF)

// Text
val TextPrimary = Color(0xFF1C0E05)
val TextSecondary = Color(0xFF8C6A52)
val TextHint = Color(0xFF999999)

// Backward compatibility aliases
val Teal = GreenAccent
val TealLight = GreenLight
val TealDark = Color(0xFF006B56)
val TealSurface = GreenSurface
val Beige = WarmWhite
val BeigeDark = Cream
val GoldLight = Color(0xFFE8D48B)

// ============================================================
// Gradient utilities
// ============================================================

fun orangeGradient(): Brush = Brush.linearGradient(
    colors = listOf(Orange, OrangeLight),
    start = Offset.Zero,
    end = Offset.Infinite
)

fun orangeRecordingGradient(): Brush = Brush.linearGradient(
    colors = listOf(OrangeDeep, Orange),
    start = Offset.Zero,
    end = Offset.Infinite
)

fun orangeGreetingGradient(): Brush = Brush.linearGradient(
    colors = listOf(Orange, Color(0xFFFF9F60), Gold),
    start = Offset.Zero,
    end = Offset.Infinite
)

fun greenGradient(): Brush = Brush.linearGradient(
    colors = listOf(GreenAccent, GreenLight),
    start = Offset.Zero,
    end = Offset.Infinite
)

// ============================================================
// Material3 Color Schemes
// ============================================================

private val LightColors = lightColorScheme(
    primary = Orange,
    onPrimary = Color.White,
    primaryContainer = OrangeSurface,
    onPrimaryContainer = OrangeDark,
    secondary = GreenAccent,
    onSecondary = Color.White,
    secondaryContainer = GreenSurface,
    onSecondaryContainer = Color(0xFF003D33),
    surface = Color.White,
    onSurface = BrownDark,
    surfaceVariant = Cream,
    onSurfaceVariant = BrownMedium,
    background = WarmWhite,
    onBackground = BrownDark,
    outline = BrownBorder,
    outlineVariant = Cream,
    error = Red,
    onError = Color.White,
    errorContainer = RedSurface,
    onErrorContainer = Color(0xFF410002)
)

private val DarkColors = darkColorScheme(
    primary = OrangeLight,
    onPrimary = Color(0xFF3E0500),
    primaryContainer = OrangeDark,
    onPrimaryContainer = OrangeSurface,
    secondary = GreenLight,
    onSecondary = Color(0xFF003D33),
    secondaryContainer = Color(0xFF005142),
    onSecondaryContainer = GreenSurface,
    surface = Color(0xFF1C1B18),
    onSurface = Color(0xFFE5E2DA),
    surfaceVariant = Color(0xFF2D2B26),
    onSurfaceVariant = Color(0xFFC5C0B6),
    background = Color(0xFF1C1B18),
    onBackground = Color(0xFFE5E2DA),
    outline = Color(0xFF5A5550),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

@Composable
fun XiaoxisiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
