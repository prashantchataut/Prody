package com.prody.prashant.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.preferences.PreferencesManager
import com.prody.prashant.ui.navigation.Screen
import com.prody.prashant.ui.theme.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    preferencesManager: PreferencesManager
) : ViewModel() {

    /**
     * Exposes the UI state for the MainActivity.
     *
     * This StateFlow is created using a robust, declarative pattern:
     * 1.  `combine` merges the latest values from both the onboarding status and theme mode flows.
     * 2.  The result is transformed into a `MainActivityUiState` object.
     * 3.  `catch` gracefully handles any exceptions during data loading (e.g., I/O errors
     *     from DataStore). It logs the error and emits a safe default state, preventing crashes
     *     and ensuring the app can always start (by showing onboarding).
     * 4.  `stateIn` converts this cold flow into a hot `StateFlow`, caching the last emitted value.
     *     - `scope`: `viewModelScope` ensures the flow is active only as long as the ViewModel is alive.
     *     - `started`: `SharingStarted.WhileSubscribed(5_000)` is a lifecycle-aware strategy.
     *       It starts the flow when the UI is visible and stops it 5 seconds after the last
     *       subscriber disappears, saving resources.
     *     - `initialValue`: The UI starts in a `isLoading = true` state, which is used to
     *       keep the splash screen visible until the actual data is loaded.
     */
    val uiState: StateFlow<MainActivityUiState> = combine(
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
            isLoading = false, // Data is now loaded
            startDestination = startDestination,
            themeMode = themeMode
        )
    }.catch { exception ->
        // Log the error for debugging purposes.
        Log.e("MainViewModel", "Failed to load user preferences", exception)
        // Emit a safe default state to ensure the app can still start.
        emit(
            MainActivityUiState(
                isLoading = false,
                startDestination = Screen.Onboarding.route,
                themeMode = ThemeMode.SYSTEM
            )
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MainActivityUiState(isLoading = true) // Default to loading state
    )
}
