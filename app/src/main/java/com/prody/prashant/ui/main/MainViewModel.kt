package com.prody.prashant.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.ui.navigation.Screen
import com.prody.prashant.ui.theme.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainActivityUiState())
    val uiState: StateFlow<MainActivityUiState> = _uiState.asStateFlow()

    init {
        // Performance Optimization: Use a two-phase loading strategy.
        viewModelScope.launch {
            // Phase 1: Load only the critical data needed for navigation.
            val onboardingCompleted = preferencesManager.onboardingCompleted
                .catch { emit(false) }
                .first() // We only need the initial value to decide the route.

            val startDestination = if (onboardingCompleted) {
                Screen.Home.route
            } else {
                Screen.Onboarding.route
            }

            // Dismiss the splash screen as soon as we have the correct start destination.
            // The theme and other settings will load shortly after.
            _uiState.value = MainActivityUiState(
                isLoading = false,
                startDestination = startDestination
            )

            // Phase 2: Asynchronously load the remaining non-critical user preferences.
            combine(
                preferencesManager.themeMode.catch { emit("system") },
                preferencesManager.hapticFeedbackEnabled.catch { emit(true) }
            ) { themeModeString, hapticEnabled ->
                val themeMode = when (themeModeString.lowercase()) {
                    "light" -> ThemeMode.LIGHT
                    "dark" -> ThemeMode.DARK
                    else -> ThemeMode.SYSTEM
                }
                // Update the state with the loaded non-critical preferences.
                _uiState.value.copy(
                    themeMode = themeMode,
                    hapticFeedbackEnabled = hapticEnabled
                )
            }.collect { newState ->
                // Apply the final state with all preferences loaded.
                _uiState.value = newState
            }
        }
    }
}
