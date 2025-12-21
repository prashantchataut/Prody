package com.prody.prashant.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.ui.theme.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.prody.prashant.ui.navigation.Screen
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MainActivityUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val startDestination: String? = null
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainActivityUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            combine(
                preferencesManager.themeMode,
                preferencesManager.onboardingCompleted
            ) { themeModeString, onboardingCompleted ->
                val themeMode = when (themeModeString.lowercase()) {
                    "light" -> ThemeMode.LIGHT
                    "dark" -> ThemeMode.DARK
                    else -> ThemeMode.SYSTEM
                }
                val startDestination = if (onboardingCompleted) Screen.Home.route else Screen.Onboarding.route
                MainActivityUiState(themeMode, startDestination)
            }.catch {
                // If preferences fail, default to system theme and show onboarding.
                emit(MainActivityUiState(ThemeMode.SYSTEM, Screen.Onboarding.route))
            }.collect {
                _uiState.value = it
            }
        }
    }
}
