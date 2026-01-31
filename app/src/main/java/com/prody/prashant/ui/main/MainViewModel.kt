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
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    /**
     * The UI state for [MainActivity], managed declaratively using [stateIn].
     * This approach ensures that:
     * 1. All necessary data is loaded before dismissing the splash screen (isLoading = false).
     * 2. There are no race conditions between different preference updates.
     * 3. The startup logic is clean and follows modern Architectural Patterns.
     */
    val uiState: StateFlow<MainActivityUiState> = combine(
        preferencesManager.onboardingCompleted.catch { emit(false) },
        preferencesManager.themeMode.catch { emit("system") },
        preferencesManager.hapticFeedbackEnabled.catch { emit(true) }
    ) { onboardingCompleted, themeModeString, hapticEnabled ->
        MainActivityUiState(
            isLoading = false,
            startDestination = if (onboardingCompleted) Screen.Home.route else Screen.Onboarding.route,
            themeMode = when (themeModeString.lowercase()) {
                "light" -> ThemeMode.LIGHT
                "dark" -> ThemeMode.DARK
                else -> ThemeMode.SYSTEM
            },
            hapticFeedbackEnabled = hapticEnabled
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainActivityUiState(isLoading = true)
    )
}
