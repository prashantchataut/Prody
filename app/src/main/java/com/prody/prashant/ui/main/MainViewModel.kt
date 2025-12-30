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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainActivityUiState())
    val uiState: StateFlow<MainActivityUiState> = _uiState.asStateFlow()

    init {
        // Combine the flows for onboarding status and theme mode.
        // This ensures that we react to changes in either preference.
        combine(
            preferencesManager.onboardingCompleted,
            preferencesManager.themeMode
        ) { onboardingCompleted, themeModeString ->
            val themeMode = when (themeModeString.lowercase()) {
                "light" -> ThemeMode.LIGHT
                "dark" -> ThemeMode.DARK
                else -> ThemeMode.SYSTEM
            }
            val startDestination = if (onboardingCompleted) {
                Screen.Home.route
            } else {
                Screen.Onboarding.route
            }

            MainActivityUiState(
                isLoading = false, // Data loaded, splash screen can be dismissed
                startDestination = startDestination,
                themeMode = themeMode
            )
        }.onEach { newState ->
            _uiState.value = newState
        }.launchIn(viewModelScope)
    }
}
