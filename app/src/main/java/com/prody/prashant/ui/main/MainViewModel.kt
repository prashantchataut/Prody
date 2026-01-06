package com.prody.prashant.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.ui.navigation.Screen
import com.prody.prashant.ui.theme.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    preferencesManager: PreferencesManager
) : ViewModel() {

    val uiState: StateFlow<MainActivityUiState> =
        combine(
            preferencesManager.onboardingCompleted.catch {
                if (it is IOException) emit(false) else throw it
            },
            preferencesManager.themeMode.catch {
                if (it is IOException) emit("system") else throw it
            }
        ) { isOnboardingCompleted, themeModeString ->
            val startDestination = if (isOnboardingCompleted) {
                Screen.Home.route
            } else {
                Screen.Onboarding.route
            }
            val themeMode = when (themeModeString.lowercase()) {
                "light" -> ThemeMode.LIGHT
                "dark" -> ThemeMode.DARK
                else -> ThemeMode.SYSTEM
            }
            MainActivityUiState(
                isLoading = false,
                startDestination = startDestination,
                themeMode = themeMode
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MainActivityUiState(isLoading = true)
        )
}
