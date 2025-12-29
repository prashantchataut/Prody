package com.prody.prashant.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.ui.navigation.Screen
import com.prody.prashant.ui.theme.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ViewModel for the [MainActivity], responsible for loading initial app data
 * and providing the main UI state.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    preferencesManager: PreferencesManager
) : ViewModel() {

    /**
     * The UI state for the [MainActivity], observed by the UI to determine
     * the start destination and theme.
     */
    val uiState: StateFlow<MainActivityUiState> =
        combine(
            preferencesManager.onboardingCompleted,
            preferencesManager.themeMode
        ) { onboardingCompleted, themeModeString ->
            val themeMode = when (themeModeString.lowercase()) {
                "light" -> ThemeMode.LIGHT
                "dark" -> ThemeMode.DARK
                else -> ThemeMode.SYSTEM
            }

            MainActivityUiState(
                isLoading = false, // Data is loaded, no longer loading
                startDestination = if (onboardingCompleted) Screen.Home.route else Screen.Onboarding.route,
                themeMode = themeMode
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MainActivityUiState() // Initial state is loading
        )
}
