package com.prody.prashant.ui.screens.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.BuildConfig
import com.prody.prashant.R
import com.prody.prashant.data.ai.GeminiModel
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

            // Buddha AI (Gemini) section
            GeminiAiSettingsSection(
                apiKey = uiState.geminiApiKey,
                selectedModel = uiState.geminiModel,
                buddhaEnabled = uiState.buddhaAiEnabled,
                isTestingConnection = uiState.isTestingConnection,
                connectionTestResult = uiState.connectionTestResult,
                onApiKeyChange = { viewModel.setGeminiApiKey(it) },
                onModelChange = { viewModel.setGeminiModel(it) },
                onBuddhaEnabledChange = { viewModel.setBuddhaAiEnabled(it) },
                onTestConnection = { viewModel.testGeminiConnection() },
                onClearTestResult = { viewModel.clearConnectionTestResult() }
            )

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

@Composable
private fun GeminiAiSettingsSection(
    apiKey: String,
    selectedModel: String,
    buddhaEnabled: Boolean,
    isTestingConnection: Boolean,
    connectionTestResult: String?,
    onApiKeyChange: (String) -> Unit,
    onModelChange: (String) -> Unit,
    onBuddhaEnabledChange: (Boolean) -> Unit,
    onTestConnection: () -> Unit,
    onClearTestResult: () -> Unit
) {
    var showApiKey by remember { mutableStateOf(false) }
    var apiKeyInput by remember(apiKey) { mutableStateOf(apiKey) }
    var modelDropdownExpanded by remember { mutableStateOf(false) }

    // Show snackbar for connection test result
    LaunchedEffect(connectionTestResult) {
        if (connectionTestResult != null) {
            kotlinx.coroutines.delay(3000)
            onClearTestResult()
        }
    }

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = "Buddha AI (Gemini)",
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
            Column(modifier = Modifier.padding(16.dp)) {
                // Enable Buddha AI Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.SelfImprovement,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Enable Buddha AI",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "AI-powered stoic wisdom in journals",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = buddhaEnabled,
                        onCheckedChange = onBuddhaEnabledChange
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                // API Key Input
                Text(
                    text = "Gemini API Key",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = apiKeyInput,
                    onValueChange = { apiKeyInput = it },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = buddhaEnabled,
                    placeholder = { Text("Enter your Gemini API key") },
                    visualTransformation = if (showApiKey) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    trailingIcon = {
                        Row {
                            IconButton(onClick = { showApiKey = !showApiKey }) {
                                Icon(
                                    imageVector = if (showApiKey) Icons.Filled.VisibilityOff
                                    else Icons.Filled.Visibility,
                                    contentDescription = if (showApiKey) "Hide" else "Show"
                                )
                            }
                            if (apiKeyInput != apiKey && apiKeyInput.isNotBlank()) {
                                IconButton(onClick = { onApiKeyChange(apiKeyInput) }) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = "Save",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    },
                    shape = MaterialTheme.shapes.medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Get your free API key from Google AI Studio",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Model Selection
                Text(
                    text = "AI Model",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Box {
                    OutlinedButton(
                        onClick = { modelDropdownExpanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = buddhaEnabled && apiKey.isNotBlank()
                    ) {
                        val currentModel = GeminiModel.entries.find { it.modelId == selectedModel }
                            ?: GeminiModel.GEMINI_1_5_FLASH
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = currentModel.displayName,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = currentModel.description,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(
                                imageVector = Icons.Filled.ArrowDropDown,
                                contentDescription = null
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = modelDropdownExpanded,
                        onDismissRequest = { modelDropdownExpanded = false }
                    ) {
                        GeminiModel.entries.forEach { model ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = model.displayName,
                                            fontWeight = if (model.modelId == selectedModel)
                                                FontWeight.Bold else FontWeight.Normal
                                        )
                                        Text(
                                            text = model.description,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                onClick = {
                                    onModelChange(model.modelId)
                                    modelDropdownExpanded = false
                                },
                                leadingIcon = {
                                    if (model.modelId == selectedModel) {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Test Connection Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onTestConnection,
                        enabled = buddhaEnabled && apiKey.isNotBlank() && !isTestingConnection
                    ) {
                        if (isTestingConnection) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(if (isTestingConnection) "Testing..." else "Test Connection")
                    }

                    AnimatedVisibility(
                        visible = connectionTestResult != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        val isSuccess = connectionTestResult?.contains("Connected") == true
                        Surface(
                            color = if (isSuccess) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.errorContainer,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = if (isSuccess) Icons.Filled.CheckCircle
                                    else Icons.Filled.Error,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (isSuccess) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = if (isSuccess) "Connected" else "Failed",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSuccess) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }

                if (!apiKey.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "API key saved securely on device",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
