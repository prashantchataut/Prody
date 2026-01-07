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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainActivityUiState())
    val uiState: StateFlow<MainActivityUiState> = _uiState.asStateFlow()

    init {
        // Sequentially load data:
        // 1. Critical data (onboarding status) for initial navigation.
        //    This is now read from SharedPreferences and is very fast.
        // 2. Non-critical data (theme) is loaded asynchronously.
        loadInitialDestination()
        loadTheme()
    }

    private fun loadInitialDestination() {
        viewModelScope.launch {
            // .first() is now fast and safe because it's backed by SharedPreferences.
            val onboardingCompleted = try {
                preferencesManager.onboardingCompleted.first()
            } catch (e: Exception) {
                // In case of any error reading the preference, default to showing onboarding.
                false
            }

            val startDestination = if (onboardingCompleted) {
                Screen.Home.route
            } else {
                Screen.Onboarding.route
            }

            _uiState.value = _uiState.value.copy(
                isLoading = false, // Unlock the splash screen
                startDestination = startDestination
            )
        }
    }

    private fun loadTheme() {
        viewModelScope.launch {
            // Asynchronously collect theme changes from DataStore.
            // This does not block the splash screen.
            preferencesManager.themeMode
                .catch { emit("system") }
                .collect { themeModeString ->
                    val themeMode = when (themeModeString.lowercase()) {
                        "light" -> ThemeMode.LIGHT
                        "dark" -> ThemeMode.DARK
                        else -> ThemeMode.SYSTEM
                    }
                    _uiState.value = _uiState.value.copy(themeMode = themeMode)
                }
        }
    }
}
