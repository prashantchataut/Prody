package com.prody.prashant.ui.main

import com.prody.prashant.ui.navigation.Screen
import com.prody.prashant.ui.theme.ThemeMode

data class MainActivityUiState(
    val isLoading: Boolean = true,
    val startDestination: String = Screen.Onboarding.route,
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)
