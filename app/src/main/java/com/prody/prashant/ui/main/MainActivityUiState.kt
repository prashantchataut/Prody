package com.prody.prashant.ui.main

import com.prody.prashant.ui.theme.ThemeMode

/**
 * Represents the UI state for the [MainActivity].
 *
 * @property isLoading Indicates if essential data (like onboarding status) is being loaded.
 * @property startDestination The route for the initial screen to be displayed.
 * @property themeMode The current theme mode of the application.
 */
data class MainActivityUiState(
    val isLoading: Boolean = true,
    val startDestination: String? = null,
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)
