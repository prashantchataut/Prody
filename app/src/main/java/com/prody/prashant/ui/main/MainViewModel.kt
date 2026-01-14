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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainActivityUiState())
    val uiState: StateFlow<MainActivityUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Combine all essential preferences flows into one.
            // This ensures we have all the necessary data before updating the UI state.
            combine(
                preferencesManager.onboardingCompleted.catch { emit(false) },
                preferencesManager.themeMode.catch { emit("system") },
                preferencesManager.hapticFeedbackEnabled.catch { emit(true) }
            ) { onboardingCompleted, themeModeString, hapticEnabled ->
                // Determine the starting destination of the app
                val startDestination = if (onboardingCompleted) Screen.Home.route else Screen.Onboarding.route

                // Map the theme mode string to the ThemeMode enum
                val themeMode = when (themeModeString.lowercase()) {
                    "light" -> ThemeMode.LIGHT
                    "dark" -> ThemeMode.DARK
                    else -> ThemeMode.SYSTEM
                }

                // Create a temporary data holder for the loaded preferences
                InitialPreferences(startDestination, themeMode, hapticEnabled)
            }.collect { initialPrefs ->
                // Update the UI state in one atomic operation.
                // This unblocks the splash screen and makes all data available simultaneously.
                _uiState.value = MainActivityUiState(
                    isLoading = false,
                    startDestination = initialPrefs.startDestination,
                    themeMode = initialPrefs.themeMode,
                    hapticFeedbackEnabled = initialPrefs.hapticFeedbackEnabled
                )
            }
        }
    }
}

/**
 * A helper data class to hold the initial preferences loaded asynchronously.
 * This makes the combine operator's logic cleaner and more readable.
 */
private data class InitialPreferences(
    val startDestination: String,
    val themeMode: ThemeMode,
    val hapticFeedbackEnabled: Boolean
)
}
