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
    preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainActivityUiState())
    val uiState: StateFlow<MainActivityUiState> = _uiState.asStateFlow()

    init {
        // Determine the start destination immediately using the synchronous method.
        // This is the most critical piece of data for the first frame.
        val startDestination = if (preferencesManager.isOnboardingCompleted()) {
            Screen.Home.route
        } else {
            Screen.Onboarding.route
        }

        // Set the initial state with isLoading=false and the determined destination.
        // This dismisses the splash screen instantly.
        _uiState.value = MainActivityUiState(
            isLoading = false,
            startDestination = startDestination
        )

        // Asynchronously collect and update non-critical UI settings like theme.
        // This happens in the background without blocking the UI.
        viewModelScope.launch {
            combine(
                preferencesManager.themeMode.catch { emit("system") },
                preferencesManager.hapticFeedbackEnabled.catch { emit(true) }
            ) { themeModeString, hapticEnabled ->
                val themeMode = when (themeModeString.lowercase()) {
                    "light" -> ThemeMode.LIGHT
                    "dark" -> ThemeMode.DARK
                    else -> ThemeMode.SYSTEM
                }
                // Create a pair of the loaded settings
                themeMode to hapticEnabled
            }.collect { (themeMode, hapticEnabled) ->
                // Update the state with the loaded settings.
                // The UI will recompose to apply the theme, but it was already visible.
                _uiState.value = _uiState.value.copy(
                    themeMode = themeMode,
                    hapticFeedbackEnabled = hapticEnabled
                )
            }
        }
    }
}
