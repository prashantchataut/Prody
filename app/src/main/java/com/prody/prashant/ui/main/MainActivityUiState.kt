package com.prody.prashant.ui.main

import com.prody.prashant.ui.theme.ThemeMode

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState
    data class Ready(
        val themeMode: ThemeMode,
        val isOnboardingCompleted: Boolean
    ) : MainActivityUiState
}
