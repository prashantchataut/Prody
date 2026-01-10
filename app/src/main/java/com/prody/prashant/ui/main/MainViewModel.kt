package com.prody.prashant.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.ui.navigation.Screen
import com.prody.prashant.ui.theme.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainActivityUiState())
    val uiState: StateFlow<MainActivityUiState> = _uiState.asStateFlow()

    init {
        // Step 1: Synchronously determine the start destination for a fast startup.
        val onboardingCompleted = preferencesManager.getOnboardingCompleted()
        val startDestination = if (onboardingCompleted) Screen.Home.route else Screen.Onboarding.route

        // Immediately update the UI state with the critical navigation info.
        _uiState.value = MainActivityUiState(
            isLoading = false,
            startDestination = startDestination,
            themeMode = ThemeMode.SYSTEM // Default theme, will be updated asynchronously.
        )

        // Step 2: Asynchronously load the theme settings without blocking the UI.
        viewModelScope.launch {
            preferencesManager.themeMode
                .catch { emit("system") }
                .map { themeModeString ->
                    when (themeModeString.lowercase()) {
                        "light" -> ThemeMode.LIGHT
                        "dark" -> ThemeMode.DARK
                        else -> ThemeMode.SYSTEM
                    }
                }
                .collect { themeMode ->
                    // Update the UI state with the theme once it's loaded.
                    _uiState.value = _uiState.value.copy(themeMode = themeMode)
                }
        }
    }
}
