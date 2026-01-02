package com.prody.prashant.ui.main

import android.util.Log
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainActivityUiState())
    val uiState: StateFlow<MainActivityUiState> = _uiState.asStateFlow()

    init {
        loadInitialAppState()
    }

    private fun loadInitialAppState() {
        viewModelScope.launch {
            // Combine the flows for onboarding status and theme mode.
            // This ensures we get updates from both and create a consistent UI state.
            combine(
                preferencesManager.onboardingCompleted,
                preferencesManager.themeMode
            ) { onboardingCompleted, themeModeString ->
                // Determine the start destination based on onboarding status.
                val startDestination = if (onboardingCompleted) {
                    Screen.Home.route
                } else {
                    Screen.Onboarding.route
                }

                // Map the theme string to the ThemeMode enum.
                val themeMode = when (themeModeString.lowercase()) {
                    "light" -> ThemeMode.LIGHT
                    "dark" -> ThemeMode.DARK
                    else -> ThemeMode.SYSTEM
                }

                // Return a pair of the determined values.
                startDestination to themeMode
            }
            .catch { exception ->
                // IMPORTANT: Handle potential IOExceptions from DataStore.
                // If we fail to read preferences, default to a safe state (show onboarding).
                Log.e("MainViewModel", "Failed to load initial app state from preferences", exception)
                emit(Screen.Onboarding.route to ThemeMode.SYSTEM) // Fallback value
            }
            .collect { (startDestination, themeMode) ->
                // Update the UI state with the loaded data.
                // isLoading is set to false because we have successfully loaded the data.
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        startDestination = startDestination,
                        themeMode = themeMode
                    )
                }
            }
        }
    }
}
