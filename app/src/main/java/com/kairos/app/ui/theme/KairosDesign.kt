package com.kairos.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Focused design tokens for the Kairos product surface (Paper & Ink).
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

/**
 * Flat paper tokens. The "glass" name is kept for compatibility; the material
 * is now a solid paper panel with a 1px hairline — no translucency, no shadow.
 */
internal val LightKairosGlassColors = KairosGlassColors(
    fill = KairosSurfaceLight,
    fillStrong = KairosSurfaceContainerLight,
    border = KairosOutlineLight,
    highlight = Color.Transparent,
    shadow = Color.Transparent,
    coolWash = Color.Transparent,
    warmWash = Color.Transparent,
    success = KairosVerdigris
)

internal val DarkKairosGlassColors = KairosGlassColors(
    fill = KairosSurfaceDark,
    fillStrong = KairosSurfaceContainerDark,
    border = KairosOutlineDark,
    highlight = Color.Transparent,
    shadow = Color.Transparent,
    coolWash = Color.Transparent,
    warmWash = Color.Transparent,
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
    val control: Dp = 12.dp
    val controlLarge: Dp = 16.dp
    val readingSurface: Dp = 16.dp
    val floating: Dp = 16.dp
    val navigation: Dp = 16.dp
}

object KairosElevation {
    val glass: Dp = 0.dp
    val floating: Dp = 0.dp
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
