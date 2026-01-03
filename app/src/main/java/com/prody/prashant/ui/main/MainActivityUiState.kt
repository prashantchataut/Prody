package com.prody.prashant.ui.main

import com.prody.prashant.ui.theme.ThemeMode

/**
 * A sealed interface representing the UI state for MainActivity.
 * This is used to drive the UI, ensuring that the splash screen waits for
 * essential data to be loaded before dismissing.
 */
sealed interface MainActivityUiState {
    /**
     * The initial state while waiting for essential data (like onboarding status) to load.
     * The splash screen will remain visible during this state.
     */
    data object Loading : MainActivityUiState

    /**
     * The state representing that all essential data has been loaded successfully.
     *
     * @param startDestination The route to navigate to after the splash screen.
     * @param themeMode The theme to apply to the application.
     */
    data class Success(
        val startDestination: String,
        val themeMode: ThemeMode
    ) : MainActivityUiState
}
