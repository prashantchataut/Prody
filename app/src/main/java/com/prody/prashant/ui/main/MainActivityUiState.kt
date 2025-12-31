package com.prody.prashant.ui.main

import com.prody.prashant.ui.theme.ThemeMode

/**
 * Represents the UI state for the MainActivity.
 *
 * @param isLoading Indicates if the initial data (onboarding status, theme) is loading.
 * @param startDestination The route for the initial screen to display.
 * @param themeMode The current theme mode of the application.
 */
data class MainActivityUiState(
    val isLoading: Boolean = true,
    val startDestination: String? = null,
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)
