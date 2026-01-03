package com.prody.prashant.ui.main

import com.prody.prashant.ui.theme.ThemeMode

/**
 * Represents the UI state for [MainActivity].
 *
 * @param isLoading Whether the initial data (like onboarding status) is loading.
 *                  The splash screen should be visible while this is true.
 * @param startDestination The route for the first screen to show the user (e.g., Onboarding or Home).
 *                         Null if not yet determined.
 * @param themeMode The user's selected theme preference.
 */
data class MainActivityUiState(
    val isLoading: Boolean = true,
    val startDestination: String? = null,
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)
