package com.prody.prashant.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.ui.navigation.Screen
import com.prody.prashant.ui.theme.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainActivityUiState())
    val uiState: StateFlow<MainActivityUiState> = _uiState.asStateFlow()

    private val _navigationChannel = Channel<String>()
    val navigationChannel = _navigationChannel.receiveAsFlow()

    // Security: A whitelist of routes that can be navigated to from an external Intent.
    // This prevents malicious apps from forcing navigation to arbitrary screens.
    private val externalNavigationAllowList = setOf("journal/new")

    init {
        viewModelScope.launch {
            combine(
                preferencesManager.onboardingCompleted.catch { emit(false) },
                preferencesManager.themeMode.catch { emit("system") }
            ) { onboardingCompleted, themeModeString ->
                val themeMode = when (themeModeString.lowercase()) {
                    "light" -> ThemeMode.LIGHT
                    "dark" -> ThemeMode.DARK
                    else -> ThemeMode.SYSTEM
                }
                val startDestination = if (onboardingCompleted) Screen.Home.route else Screen.Onboarding.route

                MainActivityUiState(
                    isLoading = false,
                    startDestination = startDestination,
                    themeMode = themeMode
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun onNavigationIntent(route: String?) {
        // Security: Only process the route if it's in the allow-list.
        if (route != null && route in externalNavigationAllowList) {
            viewModelScope.launch {
                _navigationChannel.send(route)
            }
        }
    }
}
