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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainActivityUiState())
    val uiState: StateFlow<MainActivityUiState> = _uiState.asStateFlow()

    init {
        // Determine the start destination synchronously for a fast startup.
        val startDestination = if (preferencesManager.isOnboardingCompleted()) {
            Screen.Home.route
        } else {
            Screen.Onboarding.route
        }

        // Immediately update the UI state with the determined start destination.
        // The splash screen can now be dismissed.
        _uiState.value = MainActivityUiState(
            isLoading = false,
            startDestination = startDestination
        )

        // Asynchronously load the theme and update the UI state when it's ready.
        // This does not block the initial UI render.
        viewModelScope.launch {
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
