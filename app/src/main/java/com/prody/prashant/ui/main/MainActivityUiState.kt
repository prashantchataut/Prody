package com.prody.prashant.ui.main

import com.prody.prashant.ui.navigation.Screen
import com.prody.prashant.ui.theme.ThemeMode

/**
 * Represents the UI state for the [MainActivity].
 *
 * @param isLoading True if the initial data (onboarding status, theme) is loading.
 *                  The splash screen will be visible while this is true.
 * @param startDestination The route to the first screen to display (e.g., Onboarding or Home).
 *                         Null if not yet determined.
 * @param themeMode The current theme mode for the application.
 */
data class MainActivityUiState(
    val isLoading: Boolean = true,
    val startDestination: String? = null,
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)
