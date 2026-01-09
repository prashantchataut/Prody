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
        // Performance Optimization: Decouple startup path from theme loading.
        // The splash screen was waiting for BOTH onboarding status AND theme to load.
        // Now, we synchronously check the onboarding status to decide the navigation
        // path immediately, allowing the UI to render much faster. The theme is then
        // loaded asynchronously and applied once it's available.

        // Step 1: Synchronously determine the start destination for instant navigation.
        val onboardingCompleted = preferencesManager.getOnboardingCompletedSync()
        val startDestination = if (onboardingCompleted) Screen.Home.route else Screen.Onboarding.route

        // Step 2: Immediately update the UI state to dismiss the splash screen.
        // The theme will use the default value from the UiState initially.
        _uiState.value = MainActivityUiState(
            isLoading = false,
            startDestination = startDestination
        )

        // Step 3: Asynchronously collect the theme preference and update the UI state.
        // This will cause a recomposition to apply the theme, but it happens after
        // the splash screen is gone and doesn't block initial rendering.
        viewModelScope.launch {
            preferencesManager.themeMode
                .catch {
                    // In case of an error reading the theme, default to system theme
                    // and log the exception. The user can still use the app.
                    emit("system")
                }
                .collect { themeModeString ->
                    val themeMode = when (themeModeString.lowercase()) {
                        "light" -> ThemeMode.LIGHT
                        "dark" -> ThemeMode.DARK
                        else -> ThemeMode.SYSTEM
                    }
                    _uiState.value = _uiState.value.copy(themeMode = themeMode)
                }
        }
    }
}
