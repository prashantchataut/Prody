package com.prody.prashant.ui.main

import com.prody.prashant.ui.navigation.Screen
import com.prody.prashant.ui.theme.ThemeMode

/**
 * Represents the UI state for MainActivity, covering loading,
 * navigation, and theming.
 *
 * @property isLoading True if essential data is still being loaded, false otherwise.
 * @property startDestination The route for the first screen to display.
 * @property themeMode The calculated theme mode for the application.
 */
data class MainActivityUiState(
    val isLoading: Boolean = true,
    val startDestination: String = Screen.Onboarding.route,
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)
