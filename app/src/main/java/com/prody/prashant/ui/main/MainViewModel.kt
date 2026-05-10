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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainActivityUiState())
    val uiState: StateFlow<MainActivityUiState> = _uiState.asStateFlow()

    init {
        // Optimized Startup Path: Consolidate critical and non-critical preferences
        // into a single flow to ensure atomic UI state updates and prevent flickers.
        viewModelScope.launch {
            combine(
                preferencesManager.onboardingCompleted.catch { emit(false) },
                preferencesManager.themeMode.catch { emit("system") },
                preferencesManager.hapticFeedbackEnabled.catch { emit(true) }
            ) { onboardingCompleted, themeModeString, hapticEnabled ->
                val startDestination = if (onboardingCompleted) {
                    Screen.Home.route
                } else {
                    Screen.Onboarding.route
                }

                val themeMode = when (themeModeString.lowercase()) {
                    "light" -> ThemeMode.LIGHT
                    "dark" -> ThemeMode.DARK
                    else -> ThemeMode.SYSTEM
                }

                // Return all configuration details together
                Triple(startDestination, themeMode, hapticEnabled)
            }.collectLatest { (startDestination, themeMode, hapticEnabled) ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        startDestination = startDestination,
                        themeMode = themeMode,
                        hapticFeedbackEnabled = hapticEnabled
                    )
                }
            }
        }
    }
}
