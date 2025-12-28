package com.prody.prashant.ui.main

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState
    data class Success(val onboardingCompleted: Boolean) : MainActivityUiState
}
