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
    preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainActivityUiState())
    val uiState: StateFlow<MainActivityUiState> = _uiState.asStateFlow()

    init {
        // Step 1: Determine the start destination synchronously.
        // This is now possible because `onboardingCompleted` is read from SharedPreferences.
        val startDestination = if (preferencesManager.onboardingCompleted) {
            Screen.Home.route
        } else {
            Screen.Onboarding.route
        }

        // Step 2: Set the initial state immediately.
        // The splash screen can now be dismissed as soon as the ViewModel is created.
        _uiState.value = MainActivityUiState(
            isLoading = false,
            startDestination = startDestination
        )

        // Step 3: Asynchronously load the theme and update the state.
        // This no longer blocks the initial UI from showing.
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
