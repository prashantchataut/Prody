package com.prody.prashant.ui.main

import androidx.lifecycle.ViewModel
import android.content.Intent
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

    // Security: A hardcoded allow-list of routes that can be navigated to from an external Intent.
    // This prevents malicious apps from forcing navigation to arbitrary or sensitive screens.
    private val safeExternalRoutes = setOf(
        Screen.NewJournalEntry.route,
        "journal/new", // Alias for deep linking
        Screen.CollaborativeHome.route,
        Screen.Missions.route
    )

    init {
        loadInitialState()
    }

    /**
     * Processes an incoming Intent to check for a valid "navigate_to" extra.
     * If a valid, safe route is found, it overrides the default start destination.
     */
    fun processIntent(intent: Intent?) {
        val navigateTo = intent?.getStringExtra("navigate_to")
        if (navigateTo != null && navigateTo in safeExternalRoutes) {
            // If a valid deep link is found, update the start destination
            // and keep other state properties as they are.
            _uiState.value = _uiState.value.copy(startDestination = navigateTo)
        }
    }

    private fun loadInitialState() {
        // Performance Optimization: All startup data is loaded asynchronously.
        // The splash screen (`installSplashScreen`) waits for `uiState.isLoading` to be false.
        // By combining all necessary flows, we ensure the main thread is never blocked
        // and the UI is only updated once all critical data is loaded.
        viewModelScope.launch {
            combine(
                // Critical: Catch potential IOExceptions from DataStore to prevent startup crashes.
                preferencesManager.onboardingCompleted.catch { emit(false) },
                preferencesManager.themeMode.catch { emit("system") },
                preferencesManager.hapticFeedbackEnabled.catch { emit(true) }
            ) { onboardingCompleted, themeModeString, hapticEnabled ->
                // Determine the default starting screen based on onboarding status.
                val defaultStartDestination = if (onboardingCompleted) {
                    Screen.Home.route
                } else {
                    Screen.Onboarding.route
                }

                // Parse the theme mode from the string preference.
                val themeMode = when (themeModeString.lowercase()) {
                    "light" -> ThemeMode.LIGHT
                    "dark" -> ThemeMode.DARK
                    else -> ThemeMode.SYSTEM
                }

                // Create the fully-loaded UI state.
                MainActivityUiState(
                    isLoading = false, // Data is now loaded.
                    startDestination = defaultStartDestination,
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
