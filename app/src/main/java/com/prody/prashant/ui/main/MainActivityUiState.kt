package com.prody.prashant.ui.main

import com.prody.prashant.ui.theme.ThemeMode

/**
 * Represents the UI state for the MainActivity.
 *
 * @param isLoading Whether the initial data is loading.
 * @param startDestination The route of the first screen to display.
 * @param themeMode The current theme of the application.
 */
data class MainActivityUiState(
    val isLoading: Boolean = true,
    val startDestination: String? = null,
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)
