package com.prody.prashant.ui.screens.profile

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
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
import com.prody.prashant.ui.components.ProdyCard
import com.prody.prashant.ui.theme.*

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

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
            // Buddha AI (Gemini) section - Featured prominently
            BuddhaAiSection(
                geminiApiKey = uiState.geminiApiKey,
                geminiModel = uiState.geminiModel,
                geminiEnabled = uiState.geminiEnabled,
                availableModels = viewModel.availableGeminiModels,
                isTestingApiKey = uiState.isTestingApiKey,
                apiKeyTestResult = uiState.apiKeyTestResult,
                onApiKeyChange = { viewModel.setGeminiApiKey(it) },
                onModelChange = { viewModel.setGeminiModel(it) },
                onEnabledChange = { viewModel.setGeminiEnabled(it) },
                onTestApiKey = { viewModel.testGeminiApiKey() },
                onClearTestResult = { viewModel.clearApiKeyTestResult() },
                onGetApiKey = { uriHandler.openUri("https://aistudio.google.com/app/apikey") }
            )

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
private fun BuddhaAiSection(
    geminiApiKey: String,
    geminiModel: String,
    geminiEnabled: Boolean,
    availableModels: List<com.prody.prashant.util.GeminiManager.GeminiModel>,
    isTestingApiKey: Boolean,
    apiKeyTestResult: ApiKeyTestResult?,
    onApiKeyChange: (String) -> Unit,
    onModelChange: (String) -> Unit,
    onEnabledChange: (Boolean) -> Unit,
    onTestApiKey: () -> Unit,
    onClearTestResult: () -> Unit,
    onGetApiKey: () -> Unit
) {
    var showApiKey by remember { mutableStateOf(false) }
    var showModelSelector by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(ProdyPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.SelfImprovement,
                    contentDescription = null,
                    tint = ProdyPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Column {
                Text(
                    text = "Buddha AI",
                    style = MaterialTheme.typography.labelLarge,
                    color = ProdyPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Powered by Google Gemini",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        ProdyCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            backgroundColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Enable/Disable toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Enable AI Responses",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = if (geminiApiKey.isBlank()) "Set API key to enable" else "Buddha will use Gemini AI",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = geminiEnabled && geminiApiKey.isNotBlank(),
                        onCheckedChange = onEnabledChange,
                        enabled = geminiApiKey.isNotBlank()
                    )
                }

                HorizontalDivider()

                // API Key input
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "API Key",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium
                    )
                    OutlinedTextField(
                        value = geminiApiKey,
                        onValueChange = onApiKeyChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter your Gemini API key") },
                        visualTransformation = if (showApiKey) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        trailingIcon = {
                            Row {
                                IconButton(onClick = { showApiKey = !showApiKey }) {
                                    Icon(
                                        imageVector = if (showApiKey) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                        contentDescription = if (showApiKey) "Hide" else "Show"
                                    )
                                }
                            }
                        },
                        shape = CardShape
                    )

                    // API Key test result
                    AnimatedVisibility(
                        visible = apiKeyTestResult != null,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        apiKeyTestResult?.let { result ->
                            Surface(
                                shape = ChipShape,
                                color = when (result) {
                                    is ApiKeyTestResult.Success -> AchievementUnlocked.copy(alpha = 0.15f)
                                    is ApiKeyTestResult.Failed -> ProdyError.copy(alpha = 0.15f)
                                    is ApiKeyTestResult.Testing -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    when (result) {
                                        is ApiKeyTestResult.Success -> {
                                            Icon(
                                                imageVector = Icons.Filled.CheckCircle,
                                                contentDescription = null,
                                                tint = AchievementUnlocked,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = "Connection successful!",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = AchievementUnlocked
                                            )
                                        }
                                        is ApiKeyTestResult.Failed -> {
                                            Icon(
                                                imageVector = Icons.Filled.Error,
                                                contentDescription = null,
                                                tint = ProdyError,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Text(
                                                text = "Connection failed. Check your API key.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = ProdyError
                                            )
                                        }
                                        is ApiKeyTestResult.Testing -> {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(16.dp),
                                                strokeWidth = 2.dp
                                            )
                                            Text(
                                                text = "Testing connection...",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onGetApiKey,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Get API Key")
                        }

                        Button(
                            onClick = onTestApiKey,
                            modifier = Modifier.weight(1f),
                            enabled = geminiApiKey.isNotBlank() && !isTestingApiKey
                        ) {
                            if (isTestingApiKey) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.NetworkCheck,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Test")
                        }
                    }
                }

                HorizontalDivider()

                // Model selector
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "AI Model",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium
                    )

                    val selectedModel = availableModels.find { it.id == geminiModel }
                        ?: availableModels.first()

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showModelSelector = true },
                        shape = CardShape,
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = selectedModel.displayName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = selectedModel.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(
                                imageVector = Icons.Filled.ExpandMore,
                                contentDescription = "Select model",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showModelSelector,
                        onDismissRequest = { showModelSelector = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        availableModels.forEach { model ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = model.displayName,
                                            fontWeight = if (model.id == geminiModel) FontWeight.SemiBold else FontWeight.Normal
                                        )
                                        Text(
                                            text = model.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                onClick = {
                                    onModelChange(model.id)
                                    showModelSelector = false
                                },
                                leadingIcon = {
                                    if (model.id == geminiModel) {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = null,
                                            tint = ProdyPrimary
                                        )
                                    }
                                }
                            )
                        }
                    }
                }

                // Info text
                Surface(
                    shape = ChipShape,
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Your API key is stored locally on your device. Without an API key, Buddha will use built-in wisdom responses.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
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
