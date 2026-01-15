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
        // Asynchronously load all necessary preferences to unblock the splash screen.
        // By setting isLoading=true initially and combining all flows, we ensure the
        // main thread is not blocked by I/O during startup.
        viewModelScope.launch {
            combine(
                preferencesManager.onboardingCompleted.catch { emit(false) }, // Default to onboarding if error
                preferencesManager.themeMode.catch { emit("system") },
                preferencesManager.hapticFeedbackEnabled.catch { emit(true) }
            ) { onboardingCompleted, themeModeString, hapticEnabled ->
                // Determine the start destination based on onboarding status
                val startDestination = if (onboardingCompleted) Screen.Home.route else Screen.Onboarding.route

                // Determine the theme mode
                val themeMode = when (themeModeString.lowercase()) {
                    "light" -> ThemeMode.LIGHT
                    "dark" -> ThemeMode.DARK
                    else -> ThemeMode.SYSTEM
                }

                // Create a complete, final state object
                MainActivityUiState(
                    isLoading = false,
                    startDestination = startDestination,
                    themeMode = themeMode,
                    hapticFeedbackEnabled = hapticEnabled
                )
            }.collect { loadedState ->
                // Update the UI state in a single, atomic operation.
                _uiState.value = loadedState
            }
        }
    }
}
