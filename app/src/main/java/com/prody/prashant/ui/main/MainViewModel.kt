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
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    preferencesManager: PreferencesManager
) : ViewModel() {

    val uiState: StateFlow<MainActivityUiState> = combine(
        preferencesManager.onboardingCompleted,
        preferencesManager.themeMode
    ) { onboardingCompleted, themeModeString ->
        val themeMode = when (themeModeString.lowercase()) {
            "light" -> ThemeMode.LIGHT
            "dark" -> ThemeMode.DARK
            else -> ThemeMode.SYSTEM
        }
        val startDestination = if (onboardingCompleted) Screen.Home.route else Screen.Onboarding.route

        MainActivityUiState(
            isLoading = false,
            startDestination = startDestination,
            themeMode = themeMode
        )
    }.catch {
        // If there's an error reading preferences, default to a safe state (show onboarding)
        emit(
            MainActivityUiState(
                isLoading = false,
                startDestination = Screen.Onboarding.route,
                themeMode = ThemeMode.SYSTEM
            )
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MainActivityUiState(isLoading = true)
    )
}
