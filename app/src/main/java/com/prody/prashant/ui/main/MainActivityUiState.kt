package com.prody.prashant.ui.main

import com.prody.prashant.ui.theme.ThemeMode

/**
 * Represents the UI state for the [MainActivity].
 *
 * @param isLoading Whether the initial data is loading (e.g., preferences).
 * @param startDestination The route to the first screen to be displayed.
 * @param themeMode The current theme mode for the application.
 */
data class MainActivityUiState(
    val isLoading: Boolean = true,
    val startDestination: String? = null,
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)
