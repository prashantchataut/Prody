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
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainActivityUiState())
    val uiState: StateFlow<MainActivityUiState> = _uiState.asStateFlow()

    init {
        // Step 1: Immediately determine the start destination synchronously.
        val onboardingCompleted = preferencesManager.getOnboardingCompletedSync()
        val startDestination = if (onboardingCompleted) Screen.Home.route else Screen.Onboarding.route

        // Instantly update the UI state to dismiss the splash screen.
        _uiState.value = MainActivityUiState(
            isLoading = false,
            startDestination = startDestination
        )

        // Step 2: Asynchronously load the remaining non-critical preferences.
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
                // Return a pair of the loaded preferences
                themeMode to hapticEnabled
            }.collect { (themeMode, hapticEnabled) ->
                // Update the state with the async-loaded values without re-setting the start destination.
                _uiState.value = _uiState.value.copy(
                    themeMode = themeMode,
                    hapticFeedbackEnabled = hapticEnabled
                )
            }
        }
    }
}
