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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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
        // This is the critical path to unblock the splash screen.
        // It determines the initial destination.
        viewModelScope.launch {
            val onboardingCompleted = try {
                preferencesManager.onboardingCompleted.first()
            } catch (e: Exception) {
                // Default to false if there's an error reading the preference
                false
            }
            _uiState.update {
                it.copy(
                    isLoading = false,
                    startDestination = if (onboardingCompleted) Screen.Home.route else Screen.Onboarding.route
                )
            }
        }

        // This coroutine runs in parallel to collect theme changes.
        // It's not on the critical path for the splash screen.
        viewModelScope.launch {
            preferencesManager.themeMode.map { themeString ->
                when (themeString.lowercase()) {
                    "light" -> ThemeMode.LIGHT
                    "dark" -> ThemeMode.DARK
                    else -> ThemeMode.SYSTEM
                }
            }.collect { themeMode ->
                _uiState.update { it.copy(themeMode = themeMode) }
            }
        }
    }
}
