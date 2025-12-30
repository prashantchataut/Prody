package com.prody.prashant.ui.main

import com.prody.prashant.ui.theme.ThemeMode

/**
 * Represents the UI state for [com.prody.prashant.MainActivity].
 *
 * @param isLoading Indicates if essential data is being loaded. While true, the splash screen is shown.
 * @param startDestination The route for the first screen to display (e.g., Onboarding or Home).
 * @param themeMode The visual theme to apply to the application.
 */
data class MainActivityUiState(
    val isLoading: Boolean = true,
    val startDestination: String? = null,
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)
