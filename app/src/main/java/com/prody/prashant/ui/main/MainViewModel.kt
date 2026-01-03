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
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            // Combine the necessary flows. The splash screen will wait for the first emission.
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

                MainActivityUiState(
                    isLoading = false, // Data loaded, hide splash screen
                    startDestination = startDestination,
                    themeMode = themeMode
                )
            }.catch {
                // In case of an error loading preferences, default to a safe state
                emit(
                    MainActivityUiState(
                        isLoading = false,
                        startDestination = Screen.Onboarding.route,
                        themeMode = ThemeMode.SYSTEM
                    )
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }
}
