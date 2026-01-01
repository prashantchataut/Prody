package com.prody.prashant.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.ui.navigation.Screen
import com.prody.prashant.ui.theme.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainActivityUiState())
    val uiState: StateFlow<MainActivityUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            // Combine onboarding and theme flows to process them together
            combine(
                preferencesManager.onboardingCompleted,
                preferencesManager.themeMode
            ) { onboardingCompleted, themeModeString ->
                val themeMode = when (themeModeString.lowercase()) {
                    "light" -> ThemeMode.LIGHT
                    "dark" -> ThemeMode.DARK
                    else -> ThemeMode.SYSTEM
                }
                val startDestination = if (onboardingCompleted) Screen.Home.route else Screen.Onboarding.route

                // Return a pair of the determined start destination and theme mode
                startDestination to themeMode
            }
                .catch {
                    // In case of an error (e.g., I/O exception), emit a default state
                    emit(Screen.Onboarding.route to ThemeMode.SYSTEM)
                }
                .collect { (startDestination, themeMode) ->
                    // Update the UI state once data is loaded and processed
                    _uiState.value = MainActivityUiState(
                        isLoading = false,
                        startDestination = startDestination,
                        themeMode = themeMode
                    )
                }
        }
    }
}
