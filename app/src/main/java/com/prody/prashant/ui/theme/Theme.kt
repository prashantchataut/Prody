package com.prody.prashant.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = ProdyPrimary,
    onPrimary = ProdyOnPrimary,
    primaryContainer = ProdyPrimary.copy(alpha = 0.12f),
    onPrimaryContainer = ProdyPrimaryVariant,
    secondary = ProdySecondary,
    onSecondary = ProdyOnSecondary,
    secondaryContainer = ProdySecondaryVariant,
    onSecondaryContainer = ProdyOnSecondary,
    tertiary = ProdyTertiary,
    onTertiary = ProdyOnTertiary,
    tertiaryContainer = ProdyTertiary.copy(alpha = 0.3f),
    onTertiaryContainer = ProdyOnTertiary,
    background = ProdyBackground,
    onBackground = ProdyOnBackground,
    surface = ProdySurface,
    onSurface = ProdyOnSurface,
    surfaceVariant = ProdySurfaceVariant,
    onSurfaceVariant = ProdyOnSurfaceVariant,
    error = ProdyError,
    onError = ProdyOnError,
    errorContainer = ProdyError.copy(alpha = 0.12f),
    onErrorContainer = ProdyError,
    outline = ProdyOutline,
    outlineVariant = ProdyOutlineVariant,
    inverseSurface = ProdyOnSurface,
    inverseOnSurface = ProdySurface,
    inversePrimary = ProdyPrimaryDark,
    surfaceTint = ProdyPrimary,
    scrim = Color.Black.copy(alpha = 0.32f)
)

private val DarkColorScheme = darkColorScheme(
    primary = ProdyPrimaryDark,
    onPrimary = ProdyOnPrimaryDark,
    primaryContainer = ProdyPrimaryDark.copy(alpha = 0.12f),
    onPrimaryContainer = ProdyPrimaryDark,
    secondary = ProdySecondaryDark,
    onSecondary = ProdyOnSecondaryDark,
    secondaryContainer = ProdySecondaryDark.copy(alpha = 0.3f),
    onSecondaryContainer = ProdyOnSecondaryDark,
    tertiary = ProdyTertiaryDark,
    onTertiary = ProdyOnTertiaryDark,
    tertiaryContainer = ProdyTertiaryDark.copy(alpha = 0.3f),
    onTertiaryContainer = ProdyOnTertiaryDark,
    background = ProdyBackgroundDark,
    onBackground = ProdyOnBackgroundDark,
    surface = ProdySurfaceDark,
    onSurface = ProdyOnSurfaceDark,
    surfaceVariant = ProdySurfaceVariantDark,
    onSurfaceVariant = ProdyOnSurfaceVariantDark,
    error = ProdyErrorDark,
    onError = ProdyOnErrorDark,
    errorContainer = ProdyErrorDark.copy(alpha = 0.12f),
    onErrorContainer = ProdyErrorDark,
    outline = ProdyOutlineDark,
    outlineVariant = ProdyOutlineVariantDark,
    inverseSurface = ProdyOnSurfaceDark,
    inverseOnSurface = ProdySurfaceDark,
    inversePrimary = ProdyPrimary,
    surfaceTint = ProdyPrimaryDark,
    scrim = Color.Black.copy(alpha = 0.32f)
)

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

@Composable
fun ProdyTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                @Suppress("DEPRECATION")
                window.statusBarColor = Color.Transparent.toArgb()
                @Suppress("DEPRECATION")
                window.navigationBarColor = Color.Transparent.toArgb()
            }
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ProdyTypography,
        shapes = ProdyShapes,
        content = content
    )
}
