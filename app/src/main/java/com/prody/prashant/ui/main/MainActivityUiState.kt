package com.prody.prashant.ui.main

import com.prody.prashant.ui.theme.ThemeMode

/**
 * Represents the UI state for the [MainActivity].
 *
 * @property isLoading Indicates if the initial data is being loaded. While true, the splash screen is shown.
 * @property isOnboardingCompleted Determines if the user has completed the onboarding flow.
 * @property themeMode The current theme mode of the application (Light, Dark, or System).
 */
data class MainActivityUiState(
    val isLoading: Boolean = true,
    val isOnboardingCompleted: Boolean = false,
   val themeMode: ThemeMode = ThemeMode.SYSTEM
)
