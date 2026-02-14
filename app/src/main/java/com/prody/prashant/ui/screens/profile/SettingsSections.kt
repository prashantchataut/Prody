package com.prody.prashant.ui.screens.profile

import androidx.compose.runtime.Composable
import com.prody.prashant.ui.icons.ProdyIcons

@Composable
internal fun AppearanceSettingsSection(
    state: SettingsScreenState,
    isDark: Boolean,
    onThemeModeChange: (String) -> Unit,
    onDynamicColorsChange: (Boolean) -> Unit
) {
    SettingsSection(title = "APPEARANCE", isDark = isDark) {
        SettingsRowWithDropdown(
            icon = ProdyIcons.DarkMode,
            title = "App Theme",
            currentValue = state.themeModeLabel,
            isDark = isDark,
            onValueChange = onThemeModeChange
        )

        SettingsDivider(isDark)

        SettingsRowWithToggle(
            icon = ProdyIcons.Palette,
            title = "Dynamic Colors",
            checked = state.dynamicColors,
            onCheckedChange = onDynamicColorsChange,
            isDark = isDark
        )
    }
}

@Composable
internal fun NotificationSettingsSection(
    state: SettingsScreenState,
    isDark: Boolean,
    onNotificationsChange: (Boolean) -> Unit,
    onWisdomNotificationsChange: (Boolean) -> Unit,
    onJournalReminderChange: (Boolean) -> Unit
) {
    SettingsSection(title = "NOTIFICATIONS", isDark = isDark) {
        SettingsRowWithToggle(
            icon = ProdyIcons.Notifications,
            title = "Push Notifications",
            checked = state.notificationsEnabled,
            onCheckedChange = onNotificationsChange,
            isDark = isDark
        )

        SettingsDivider(isDark)

        SettingsRowWithToggle(
            icon = ProdyIcons.Lightbulb,
            title = "Daily Wisdom",
            subtitle = "Morning quotes, inspiration",
            checked = state.wisdomNotificationsEnabled,
            onCheckedChange = onWisdomNotificationsChange,
            isDark = isDark
        )

        SettingsDivider(isDark)

        SettingsRowWithToggle(
            icon = ProdyIcons.Edit,
            title = "Journal Reminder",
            subtitle = "Evening reflection prompt",
            checked = state.journalRemindersEnabled,
            onCheckedChange = onJournalReminderChange,
            isDark = isDark
        )
    }
}
