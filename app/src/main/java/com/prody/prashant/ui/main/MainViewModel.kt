package com.prody.prashant.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.ui.navigation.Screen
import com.prody.prashant.ui.theme.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    preferencesManager: PreferencesManager
) : ViewModel() {

    /**
     * The UI state for the main activity, derived from user preferences.
     * This flow combines multiple preference flows to create a single, cohesive
     * state object that the UI can observe.
     */
    val uiState: StateFlow<MainActivityUiState> = combine(
        preferencesManager.onboardingCompleted.catch { emit(false) }, // Default to onboarding if error
        preferencesManager.themeMode.catch { emit("system") } // Default to system theme if error
    ) { onboardingCompleted, themeModeString ->
        val themeMode = when (themeModeString.lowercase()) {
            "light" -> ThemeMode.LIGHT
            "dark" -> ThemeMode.DARK
            else -> ThemeMode.SYSTEM
        }
        val startDestination = if (onboardingCompleted) Screen.Home.route else Screen.Onboarding.route

        MainActivityUiState(
            isLoading = false, // Data is loaded
            startDestination = startDestination,
            themeMode = themeMode
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MainActivityUiState() // Initial state is loading
    )
}
