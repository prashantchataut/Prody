package com.prody.prashant.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.data.local.preferences.StartupPreferences
import com.prody.prashant.ui.navigation.Screen
import com.prody.prashant.ui.theme.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val startupPreferences: StartupPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainActivityUiState())
    val uiState: StateFlow<MainActivityUiState> = _uiState.asStateFlow()

    init {
        // Step 1: Synchronously determine the start destination.
        // This is the critical optimization. We read from SharedPreferences,
        // which is fast and doesn't block the UI thread waiting for DataStore's file I/O.
        val onboardingCompleted = startupPreferences.getOnboardingCompleted()
        val startDestination = if (onboardingCompleted) Screen.Home.route else Screen.Onboarding.route

        // Immediately update the UI state with the critical information.
        // isLoading is set to false right away, unblocking the splash screen.
        _uiState.value = MainActivityUiState(
            isLoading = false,
            startDestination = startDestination
        )

        // Step 2: Asynchronously load the non-critical theme preference.
        // This runs in the background and updates the state a few moments later
        // without affecting the perceived startup time.
        viewModelScope.launch {
            preferencesManager.themeMode
                .catch { emit("system") }
                .collect { themeModeString ->
                    val themeMode = when (themeModeString.lowercase()) {
                        "light" -> ThemeMode.LIGHT
                        "dark" -> ThemeMode.DARK
                        else -> ThemeMode.SYSTEM
                    }
                    // Update the state with the theme, keeping other values the same.
                    _uiState.value = _uiState.value.copy(themeMode = themeMode)
                }
        }
    }
}
