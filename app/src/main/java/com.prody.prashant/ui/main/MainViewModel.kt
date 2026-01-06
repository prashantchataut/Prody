package com.prody.prashant.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    preferencesManager: PreferencesManager
) : ViewModel() {

    val uiState: StateFlow<MainActivityUiState> =
        preferencesManager.onboardingCompleted
            .catch {
                // In case of an IO exception (e.g., file corruption),
                // default to showing onboarding.
                if (it is IOException) {
                    emit(false)
                } else {
                    throw it
                }
            }
            .map { isOnboardingCompleted ->
                val startDestination = if (isOnboardingCompleted) {
                    Screen.Home.route
                } else {
                    Screen.Onboarding.route
                }
                MainActivityUiState(
                    isLoading = false,
                    startDestination = startDestination
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = MainActivityUiState(isLoading = true)
            )
}
