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
import kotlinx.coroutines.flow.distinctUntilChanged
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
        // Performance Optimization: Consolidate all startup logic into a single, efficient flow.
        // This avoids multiple staggered UI state emissions and ensures a smoother splash-to-home transition.
        viewModelScope.launch {
            combine(
                preferencesManager.onboardingCompleted.distinctUntilChanged().catch { emit(false) },
                preferencesManager.themeMode.distinctUntilChanged().catch { emit("system") },
                preferencesManager.hapticFeedbackEnabled.distinctUntilChanged().catch { emit(true) }
            ) { onboardingCompleted, themeModeString, hapticEnabled ->
                // 1. Determine Start Destination
                val startDestination = if (onboardingCompleted) {
                    Screen.Home.route
                } else {
                    Screen.Onboarding.route
                }

                // 2. Map Theme Mode
                val themeMode = when (themeModeString.lowercase()) {
                    "light" -> ThemeMode.LIGHT
                    "dark" -> ThemeMode.DARK
                    else -> ThemeMode.SYSTEM
                }

                Triple(startDestination, themeMode, hapticEnabled)
            }.collectLatest { (startDestination, themeMode, hapticEnabled) ->
                // Atomic update to unblock UI and apply preferences simultaneously
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
