package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color.White,
    primaryContainer = EmeraldSoft,
    onPrimaryContainer = EmeraldDark,
    secondary = AmberAccent,
    onSecondary = Color.White,
    secondaryContainer = AmberSoft,
    onSecondaryContainer = Color(0xFF78350F),
    tertiary = EmeraldBright,
    onTertiary = Color.White,
    background = SurfaceAlabaster,
    onBackground = SlateInk,
    surface = SurfaceWhite,
    onSurface = SlateInk,
    surfaceVariant = SurfaceMuted,
    onSurfaceVariant = MutedInk,
    outline = OutlineHairline,
    error = OverdueRed,
    onError = Color.White,
    errorContainer = OverdueRedSoft,
    onErrorContainer = OverdueRed
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkEmeraldPrimary,
    onPrimary = Color(0xFF042F28),
    primaryContainer = Color(0xFF0D4E42),
    onPrimaryContainer = Color(0xFFA7F3D0),
    secondary = AmberBright,
    onSecondary = Color(0xFF451A03),
    secondaryContainer = Color(0xFF78350F),
    onSecondaryContainer = AmberSoft,
    tertiary = EmeraldBright,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkOutline,
    error = Color(0xFFF87171),
    onError = Color(0xFF450A0A)
)

val RimvoShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(18.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

@Composable
fun RimvoHutTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    isBangla: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = rimvoTypography(isBangla = isBangla),
        shapes = RimvoShapes,
        content = content
    )
}
