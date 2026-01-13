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
        // [PERFORMANCE] Load critical startup information synchronously.
        // This determines the start destination immediately, unblocking the splash screen.
        val onboardingCompleted = preferencesManager.getOnboardingCompleted()
        val startDestination = if (onboardingCompleted) Screen.Home.route else Screen.Onboarding.route

        _uiState.value = MainActivityUiState(
            isLoading = false,
            startDestination = startDestination
        )

        // [PERFORMANCE] Load non-critical UI settings asynchronously.
        // Theme and haptics can be updated moments after the initial UI is shown.
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
                // Return a pair or a small data class for clarity
                themeMode to hapticEnabled
            }.collect { (themeMode, hapticEnabled) ->
                _uiState.value = _uiState.value.copy(
                    themeMode = themeMode,
                    hapticFeedbackEnabled = hapticEnabled
                )
            }
        }
    }
}
