package com.prody.prashant.ui.main

import androidx.compose.runtime.Stable
import com.prody.prashant.ui.theme.ThemeMode

/**
 * Represents the UI state for [MainActivity].
 *
 * @param isLoading Whether the initial data is loading (e.g., preferences).
 * @param startDestination The route of the first screen to show (e.g., Onboarding or Home).
 * @param themeMode The current theme mode for the application.
 */
@Stable
data class MainActivityUiState(
    val isLoading: Boolean = true,
    val startDestination: String? = null,
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)
