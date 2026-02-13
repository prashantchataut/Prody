package com.prody.prashant.ui.screens.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

data class SettingsScreenState(
    val themeModeLabel: String,
    val dynamicColors: Boolean,
    val notificationsEnabled: Boolean,
    val wisdomNotificationsEnabled: Boolean,
    val journalRemindersEnabled: Boolean
)

@Composable
fun rememberSettingsScreenState(uiState: SettingsUiState): SettingsScreenState = remember(uiState) {
    SettingsScreenState(
        themeModeLabel = uiState.themeMode.replaceFirstChar { it.uppercase() },
        dynamicColors = uiState.dynamicColors,
        notificationsEnabled = uiState.notificationsEnabled,
        wisdomNotificationsEnabled = uiState.wisdomNotificationsEnabled,
        journalRemindersEnabled = uiState.journalRemindersEnabled
    )
}
