package org.ambatujudge.legaloo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class LegalooExtraColors(
    val primaryEdge: Color,
    val secondaryEdge: Color,
    val activeRing: Color,
    val lockedFill: Color,
    val lockedEdge: Color,
    val fieldFill: Color
)

private val LightExtra = LegalooExtraColors(
    primaryEdge = LegalooBlueEdge,
    secondaryEdge = LegalooYellowEdge,
    activeRing = LegalooBlueRing,
    lockedFill = LegalooTrack,
    lockedEdge = LegalooLockedEdge,
    fieldFill = LegalooFieldFill
)

private val DarkExtra = LegalooExtraColors(
    primaryEdge = DarkPrimaryEdge,
    secondaryEdge = LegalooYellowEdge,
    activeRing = DarkRing,
    lockedFill = DarkTrack,
    lockedEdge = DarkLockedEdge,
    fieldFill = DarkFieldFill
)

val LocalExtraColors = staticCompositionLocalOf { LightExtra }

private val LightColors = lightColorScheme(
    primary = LegalooBlue,
    onPrimary = Color.White,
    primaryContainer = LegalooBlueSoft,
    onPrimaryContainer = LegalooBlue,
    secondary = LegalooYellow,
    onSecondary = LegalooNavy,
    background = Color.White,
    onBackground = LegalooNavy,
    surface = Color.White,
    onSurface = LegalooNavy,
    surfaceVariant = LegalooTrack,
    onSurfaceVariant = LegalooSlate,
    outline = LegalooBorder,
    error = LegalooRed
)

private val DarkColors = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkBackground,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkPrimary,
    secondary = LegalooYellow,
    onSecondary = LegalooNavy,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkTrack,
    onSurfaceVariant = DarkSlate,
    outline = DarkBorder,
    error = DarkRed
)

@Composable
fun LegalooTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    val extra = if (darkTheme) DarkExtra else LightExtra
    CompositionLocalProvider(LocalExtraColors provides extra) {
        MaterialTheme(
            colorScheme = colors,
            typography = LegalooTypography,
            shapes = LegalooShapes,
            content = content
        )
    }
}
