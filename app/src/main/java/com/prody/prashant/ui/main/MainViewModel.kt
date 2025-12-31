package com.prody.prashant.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.ui.navigation.Screen
import com.prody.prashant.ui.theme.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainActivityUiState())
    val uiState: StateFlow<MainActivityUiState> = _uiState.asStateFlow()

    init {
        // Combine the necessary flows from preferences to create the initial UI state.
        // This ensures that we react to changes in either onboarding status or theme.
        combine(
            preferencesManager.onboardingCompleted,
            preferencesManager.themeMode
        ) { onboardingCompleted, themeModeString ->
            val themeMode = when (themeModeString.lowercase()) {
                "light" -> ThemeMode.LIGHT
                "dark" -> ThemeMode.DARK
                else -> ThemeMode.SYSTEM
            }
            val startDestination = if (onboardingCompleted) Screen.Home.route else Screen.Onboarding.route

            MainActivityUiState(
                isLoading = false, // Data is loaded
                startDestination = startDestination,
                themeMode = themeMode
            )
        }.catch { exception ->
            // Handle exceptions from the flows, e.g., DataStore read errors.
            // Log the error and emit a default state to prevent a crash.
            Log.e("MainViewModel", "Failed to load user preferences", exception)
            emit(
                MainActivityUiState(
                    isLoading = false,
                    startDestination = Screen.Onboarding.route, // Default to onboarding on error
                    themeMode = ThemeMode.SYSTEM
                )
            )
        }.onEach { newState ->
            // Update the state flow with the combined result.
            _uiState.value = newState
        }.launchIn(viewModelScope) // Launch the collection in the viewModelScope.
    }
}
