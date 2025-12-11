package com.prody.prashant.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.BuildConfig
import com.prody.prashant.R
import com.prody.prashant.ui.components.ProdyCard

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Appearance section
            SettingsSection(title = "Appearance") {
                SettingsItem(
                    icon = Icons.Filled.DarkMode,
                    title = stringResource(R.string.theme),
                    subtitle = uiState.themeMode.replaceFirstChar { it.uppercase() }
                ) {
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        TextButton(onClick = { expanded = true }) {
                            Text(uiState.themeMode.replaceFirstChar { it.uppercase() })
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            listOf("system", "light", "dark").forEach { theme ->
                                DropdownMenuItem(
                                    text = { Text(theme.replaceFirstChar { it.uppercase() }) },
                                    onClick = {
                                        viewModel.setThemeMode(theme)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                SettingsToggle(
                    icon = Icons.Filled.Palette,
                    title = "Dynamic Colors",
                    subtitle = "Use Material You colors (Android 12+)",
                    checked = uiState.dynamicColors,
                    onCheckedChange = { viewModel.setDynamicColors(it) }
                )
            }

            // Notifications section
            SettingsSection(title = stringResource(R.string.notifications)) {
                SettingsToggle(
                    icon = Icons.Filled.Notifications,
                    title = "All Notifications",
                    subtitle = "Enable or disable all notifications",
                    checked = uiState.notificationsEnabled,
                    onCheckedChange = { viewModel.setNotificationsEnabled(it) }
                )

                SettingsToggle(
                    icon = Icons.Filled.Lightbulb,
                    title = "Daily Wisdom",
                    subtitle = "Get daily vocabulary and quotes",
                    checked = uiState.wisdomNotificationsEnabled,
                    onCheckedChange = { viewModel.setWisdomNotifications(it) },
                    enabled = uiState.notificationsEnabled
                )

                SettingsToggle(
                    icon = Icons.Filled.Book,
                    title = "Journal Reminders",
                    subtitle = "Gentle reminders to reflect",
                    checked = uiState.journalRemindersEnabled,
                    onCheckedChange = { viewModel.setJournalReminders(it) },
                    enabled = uiState.notificationsEnabled
                )
            }

            // Preferences section
            SettingsSection(title = "Preferences") {
                SettingsToggle(
                    icon = Icons.Filled.Vibration,
                    title = "Haptic Feedback",
                    subtitle = "Vibration feedback for actions",
                    checked = uiState.hapticFeedbackEnabled,
                    onCheckedChange = { viewModel.setHapticFeedback(it) }
                )

                SettingsToggle(
                    icon = Icons.Filled.ViewCompact,
                    title = "Compact View",
                    subtitle = "Smaller cards for more content",
                    checked = uiState.compactView,
                    onCheckedChange = { viewModel.setCompactView(it) }
                )
            }

            // About section
            SettingsSection(title = stringResource(R.string.about)) {
                SettingsItem(
                    icon = Icons.Filled.Info,
                    title = "Prody",
                    subtitle = "Your Growth Companion"
                ) {
                    Text(
                        text = "v${BuildConfig.VERSION_NAME}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                SettingsItem(
                    icon = Icons.Filled.Code,
                    title = "Made with love",
                    subtitle = "Open source self-improvement app"
                ) {}
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        ProdyCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(vertical = 8.dp),
                content = content
            )
        }
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    trailing: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        trailing()
    }
}

@Composable
private fun SettingsToggle(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (enabled) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (enabled) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )
    }
}
