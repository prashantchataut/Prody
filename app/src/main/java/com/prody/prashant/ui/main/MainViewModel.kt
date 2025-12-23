package com.prody.prashant.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.ui.theme.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

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
                preferencesManager.onboardingCompleted,
                preferencesManager.themeMode
            ) { onboardingCompleted, themeModeString ->
                val themeMode = when (themeModeString.lowercase()) {
                    "light" -> ThemeMode.LIGHT
                    "dark" -> ThemeMode.DARK
                    else -> ThemeMode.SYSTEM
                }
                MainActivityUiState(
                    isLoading = false,
                    isOnboardingCompleted = onboardingCompleted,
                    themeMode = themeMode
                )
            }.catch {
                // In case of an error loading preferences, default to a safe state
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isOnboardingCompleted = false
                    )
                }
            }.collect { newState ->
                _uiState.update { newState }
            }
        }
    }
}
