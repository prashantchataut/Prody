package com.prody.prashant.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.ui.navigation.Screen
import com.prody.prashant.ui.theme.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.io.IOException
import javax.inject.Inject

/**
 * ViewModel for MainActivity, responsible for loading initial app data
 * and providing it to the UI as a state.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    preferencesManager: PreferencesManager
) : ViewModel() {

    /**
     * The UI state for MainActivity, exposed as a StateFlow.
     * This state determines whether to show the splash screen, the main content,
     * what the start destination is, and what theme to use.
     */
    val uiState: StateFlow<MainActivityUiState> = combine(
        preferencesManager.onboardingCompleted,
        preferencesManager.themeMode
    ) { onboardingCompleted, themeModeString ->
        val startDestination = if (onboardingCompleted) Screen.Home.route else Screen.Onboarding.route
        val themeMode = when (themeModeString.lowercase()) {
            "light" -> ThemeMode.LIGHT
            "dark" -> ThemeMode.DARK
            else -> ThemeMode.SYSTEM
        }
        MainActivityUiState.Success(startDestination, themeMode)
    }.catch { exception ->
        // DataStore can throw IOException on read failures.
        // If it fails, default to a safe state (show onboarding).
        if (exception is IOException) {
            emit(
                MainActivityUiState.Success(
                    startDestination = Screen.Onboarding.route,
                    themeMode = ThemeMode.SYSTEM
                )
            )
        } else {
            // Re-throw other exceptions we don't expect.
            throw exception
        }
    }.stateIn(
        scope = viewModelScope,
        // Start the flow immediately and share the last value with all collectors.
        // This is appropriate for app-level state that's needed right away.
        started = SharingStarted.Eagerly,
        // The initial state while we wait for the preferences to load.
        initialValue = MainActivityUiState.Loading
    )
}
