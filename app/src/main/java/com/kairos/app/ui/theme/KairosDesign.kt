package com.kairos.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Focused design tokens for the Kairos product surface.
 *
 * The legacy token catalog is intentionally left intact while screens migrate.
 * New product-facing screens depend on this small semantic set instead of the
 * historical feature-specific color and dimension inventory.
 */
@Immutable
data class KairosGlassColors(
    val fill: Color,
    val fillStrong: Color,
    val border: Color,
    val highlight: Color,
    val shadow: Color,
    val coolWash: Color,
    val warmWash: Color,
    val success: Color
)

internal val LightKairosGlassColors = KairosGlassColors(
    fill = Color(0xB8FCFBF8),
    fillStrong = Color(0xE8FCFBF8),
    border = Color(0x8FD6D8E2),
    highlight = Color(0xD9FFFFFF),
    shadow = Color(0x261B1E2A),
    coolWash = Color(0x24495CC7),
    warmWash = Color(0x1FC86F4E),
    success = KairosVerdigris
)

internal val DarkKairosGlassColors = KairosGlassColors(
    fill = Color(0xB51B1E26),
    fillStrong = Color(0xE621252E),
    border = Color(0x734C5361),
    highlight = Color(0x4DFFFFFF),
    shadow = Color(0x73000000),
    coolWash = Color(0x2EAEB8FF),
    warmWash = Color(0x24E39A7D),
    success = KairosSeaGlass
)

internal val LocalKairosGlassColors = staticCompositionLocalOf { LightKairosGlassColors }

object KairosSpacing {
    val xxs: Dp = 4.dp
    val xs: Dp = 8.dp
    val sm: Dp = 12.dp
    val md: Dp = 16.dp
    val lg: Dp = 20.dp
    val xl: Dp = 24.dp
    val xxl: Dp = 32.dp
    val section: Dp = 40.dp
    val pageHorizontal: Dp = 20.dp
    val screen: Dp = pageHorizontal
    val tabletMaxWidth: Dp = 760.dp
}

object KairosRadius {
    val control: Dp = 16.dp
    val controlLarge: Dp = 20.dp
    val readingSurface: Dp = 28.dp
    val floating: Dp = 30.dp
    val navigation: Dp = floating
}

object KairosElevation {
    val glass: Dp = 14.dp
    val floating: Dp = 22.dp
}

object KairosMotion {
    const val instant = 120
    const val quick = instant
    const val state = 220
    const val navigation = 300
}

object KairosTheme {
    val glass: KairosGlassColors
        @Composable
        @ReadOnlyComposable
        get() = LocalKairosGlassColors.current
}
