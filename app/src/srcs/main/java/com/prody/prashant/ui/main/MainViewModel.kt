package com.prody.prashant.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.ui.theme.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    preferencesManager: PreferencesManager
) : ViewModel() {

    private val onboardingCompletedFlow = preferencesManager.onboardingCompleted
    private val themeModeFlow = preferencesManager.themeMode

    val uiState: StateFlow<MainActivityUiState> = combine(
        onboardingCompletedFlow,
        themeModeFlow
    ) { onboardingCompleted, themeModeString ->
        val themeMode = when (themeModeString.lowercase()) {
            "light" -> ThemeMode.LIGHT
            "dark" -> ThemeMode.DARK
            else -> ThemeMode.SYSTEM
        }
        MainActivityUiState.Success(
            isOnboardingCompleted = onboardingCompleted,
            themeMode = themeMode
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MainActivityUiState.Loading
    )
}
