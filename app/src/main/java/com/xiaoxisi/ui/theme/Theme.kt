package com.xiaoxisi.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Teal = Color(0xFF417B7E)
val TealLight = Color(0xFF5C9B9E)
val TealDark = Color(0xFF2E5C5F)
val TealSurface = Color(0xFFE8F0F0)

val Beige = Color(0xFFF5F1EA)
val BeigeDark = Color(0xFFE8E2D8)
val BeigeWarm = Color(0xFFFAF7F2)

val TextPrimary = Color(0xFF333333)
val TextSecondary = Color(0xFF666666)
val TextHint = Color(0xFF999999)

val White = Color(0xFFFFFFFF)
val OffWhite = Color(0xFFF5F1EA)

val Gold = Color(0xFFC9A84C)
val GoldLight = Color(0xFFE8D48B)

private val LightColors = lightColorScheme(
    primary = Teal,
    onPrimary = Color.White,
    primaryContainer = TealSurface,
    onPrimaryContainer = TealDark,
    secondary = Gold,
    onSecondary = Color.White,
    secondaryContainer = GoldLight.copy(alpha = 0.3f),
    onSecondaryContainer = Color(0xFF3A2E0A),
    surface = Beige,
    onSurface = TextPrimary,
    surfaceVariant = BeigeDark,
    onSurfaceVariant = TextSecondary,
    background = Beige,
    onBackground = TextPrimary,
    outline = Color(0xFFD0CCC4),
    outlineVariant = BeigeDark,
    error = Color(0xFFBA1A1A),
    onError = Color.White
)

private val DarkColors = darkColorScheme(
    primary = TealLight,
    onPrimary = Color(0xFF0A2022),
    primaryContainer = TealDark,
    onPrimaryContainer = TealSurface,
    secondary = GoldLight,
    onSecondary = Color(0xFF3A2E0A),
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
