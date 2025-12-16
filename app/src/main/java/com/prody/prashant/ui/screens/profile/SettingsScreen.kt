package com.prody.prashant.ui.screens.profile

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.BuildConfig
import com.prody.prashant.R
import com.prody.prashant.ui.components.ProdyCard
import com.prody.prashant.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Entry animations
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    Scaffold(
        topBar = {
            SettingsTopAppBar(onNavigateBack = onNavigateBack)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Appearance section
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(400)) + slideInVertically(
                    initialOffsetY = { it / 4 },
                    animationSpec = tween(400, easing = EaseOutCubic)
                )
            ) {
                EnhancedSettingsSection(
                    title = "Appearance",
                    icon = Icons.Filled.Palette
                ) {
                    SettingsItem(
                        icon = Icons.Filled.DarkMode,
                        title = stringResource(R.string.theme),
                        subtitle = "Choose your preferred theme",
                        iconBackground = MoodCalm.copy(alpha = 0.15f),
                        iconTint = MoodCalm
                    ) {
                        var expanded by remember { mutableStateOf(false) }
                        ThemeSelector(
                            currentTheme = uiState.themeMode,
                            expanded = expanded,
                            onExpandChange = { expanded = it },
                            onThemeSelected = {
                                viewModel.setThemeMode(it)
                                expanded = false
                            }
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )

                    EnhancedSettingsToggle(
                        icon = Icons.Filled.AutoAwesome,
                        title = "Dynamic Colors",
                        subtitle = "Use Material You colors (Android 12+)",
                        checked = uiState.dynamicColors,
                        onCheckedChange = { viewModel.setDynamicColors(it) },
                        iconBackground = MoodExcited.copy(alpha = 0.15f),
                        iconTint = MoodExcited
                    )
                }
            }

            // Notifications section
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(400, delayMillis = 100)) + slideInVertically(
                    initialOffsetY = { it / 4 },
                    animationSpec = tween(400, delayMillis = 100, easing = EaseOutCubic)
                )
            ) {
                EnhancedSettingsSection(
                    title = stringResource(R.string.notifications),
                    icon = Icons.Filled.Notifications
                ) {
                    EnhancedSettingsToggle(
                        icon = Icons.Filled.NotificationsActive,
                        title = "All Notifications",
                        subtitle = "Enable or disable all notifications",
                        checked = uiState.notificationsEnabled,
                        onCheckedChange = { viewModel.setNotificationsEnabled(it) },
                        iconBackground = MoodMotivated.copy(alpha = 0.15f),
                        iconTint = MoodMotivated
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )

                    EnhancedSettingsToggle(
                        icon = Icons.Filled.Lightbulb,
                        title = "Daily Wisdom",
                        subtitle = "Get daily vocabulary and quotes",
                        checked = uiState.wisdomNotificationsEnabled,
                        onCheckedChange = { viewModel.setWisdomNotifications(it) },
                        enabled = uiState.notificationsEnabled,
                        iconBackground = GoldTier.copy(alpha = 0.15f),
                        iconTint = GoldTier
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )

                    EnhancedSettingsToggle(
                        icon = Icons.Filled.Book,
                        title = "Journal Reminders",
                        subtitle = "Gentle reminders to reflect",
                        checked = uiState.journalRemindersEnabled,
                        onCheckedChange = { viewModel.setJournalReminders(it) },
                        enabled = uiState.notificationsEnabled,
                        iconBackground = MoodCalm.copy(alpha = 0.15f),
                        iconTint = MoodCalm
                    )
                }
            }

            // Preferences section
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(400, delayMillis = 200)) + slideInVertically(
                    initialOffsetY = { it / 4 },
                    animationSpec = tween(400, delayMillis = 200, easing = EaseOutCubic)
                )
            ) {
                EnhancedSettingsSection(
                    title = "Preferences",
                    icon = Icons.Filled.Tune
                ) {
                    EnhancedSettingsToggle(
                        icon = Icons.Filled.Vibration,
                        title = "Haptic Feedback",
                        subtitle = "Vibration feedback for actions",
                        checked = uiState.hapticFeedbackEnabled,
                        onCheckedChange = { viewModel.setHapticFeedback(it) },
                        iconBackground = MoodGrateful.copy(alpha = 0.15f),
                        iconTint = MoodGrateful
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )

                    EnhancedSettingsToggle(
                        icon = Icons.Filled.ViewCompact,
                        title = "Compact View",
                        subtitle = "Smaller cards for more content",
                        checked = uiState.compactView,
                        onCheckedChange = { viewModel.setCompactView(it) },
                        iconBackground = MoodConfused.copy(alpha = 0.15f),
                        iconTint = MoodConfused
                    )
                }
            }

            // Buddha AI section (expanded with per-feature toggles)
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(400, delayMillis = 300)) + slideInVertically(
                    initialOffsetY = { it / 4 },
                    animationSpec = tween(400, delayMillis = 300, easing = EaseOutCubic)
                )
            ) {
                EnhancedSettingsSection(
                    title = "Buddha AI",
                    icon = Icons.Filled.SelfImprovement
                ) {
                    // Master toggle
                    EnhancedSettingsToggle(
                        icon = Icons.Filled.Psychology,
                        title = "Enable Buddha AI",
                        subtitle = "AI-powered stoic wisdom across the app",
                        checked = uiState.buddhaAiEnabled,
                        onCheckedChange = { viewModel.setBuddhaAiEnabled(it) },
                        iconBackground = ProdyPrimary.copy(alpha = 0.15f),
                        iconTint = ProdyPrimary
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )

                    // Per-feature toggles (only when master is enabled)
                    AnimatedVisibility(visible = uiState.buddhaAiEnabled) {
                        Column {
                            EnhancedSettingsToggle(
                                icon = Icons.Filled.WbSunny,
                                title = "Daily Wisdom",
                                subtitle = "AI-generated wisdom on home screen",
                                checked = uiState.buddhaDailyWisdomEnabled,
                                onCheckedChange = { viewModel.setBuddhaDailyWisdomEnabled(it) },
                                iconBackground = GoldTier.copy(alpha = 0.15f),
                                iconTint = GoldTier
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            )

                            EnhancedSettingsToggle(
                                icon = Icons.Filled.FormatQuote,
                                title = "Quote Insights",
                                subtitle = "Meaning and daily action for quotes",
                                checked = uiState.buddhaQuoteExplanationEnabled,
                                onCheckedChange = { viewModel.setBuddhaQuoteExplanationEnabled(it) },
                                iconBackground = MoodCalm.copy(alpha = 0.15f),
                                iconTint = MoodCalm
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            )

                            EnhancedSettingsToggle(
                                icon = Icons.Filled.Edit,
                                title = "Journal Insights",
                                subtitle = "Emotion and theme analysis after journaling",
                                checked = uiState.buddhaJournalInsightsEnabled,
                                onCheckedChange = { viewModel.setBuddhaJournalInsightsEnabled(it) },
                                iconBackground = MoodGrateful.copy(alpha = 0.15f),
                                iconTint = MoodGrateful
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            )

                            EnhancedSettingsToggle(
                                icon = Icons.Filled.TrendingUp,
                                title = "Weekly Patterns",
                                subtitle = "Track mood trends and themes",
                                checked = uiState.buddhaPatternTrackingEnabled,
                                onCheckedChange = { viewModel.setBuddhaPatternTrackingEnabled(it) },
                                iconBackground = MoodMotivated.copy(alpha = 0.15f),
                                iconTint = MoodMotivated
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            )

                            EnhancedSettingsToggle(
                                icon = Icons.Filled.Spellcheck,
                                title = "Vocabulary Help",
                                subtitle = "Example sentences and memory hooks",
                                checked = uiState.buddhaVocabularyContextEnabled,
                                onCheckedChange = { viewModel.setBuddhaVocabularyContextEnabled(it) },
                                iconBackground = MoodExcited.copy(alpha = 0.15f),
                                iconTint = MoodExcited
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            )

                            EnhancedSettingsToggle(
                                icon = Icons.Filled.EmojiEmotions,
                                title = "Playful Buddha",
                                subtitle = "Occasional lighthearted responses",
                                checked = uiState.buddhaPlayfulMode,
                                onCheckedChange = { viewModel.setBuddhaPlayfulMode(it) },
                                iconBackground = MoodJoyful.copy(alpha = 0.15f),
                                iconTint = MoodJoyful
                            )

                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            )

                            EnhancedSettingsToggle(
                                icon = Icons.Filled.BatteryChargingFull,
                                title = "Reduce AI Usage",
                                subtitle = "Prefer cached responses to save data",
                                checked = uiState.buddhaReduceAiUsage,
                                onCheckedChange = { viewModel.setBuddhaReduceAiUsage(it) },
                                iconBackground = MoodAnxious.copy(alpha = 0.15f),
                                iconTint = MoodAnxious
                            )
                        }
                    }

                    // Info card about Buddha AI
                    BuddhaAiInfoCard()

                    // Privacy note
                    BuddhaPrivacyNote()
                }
            }

            // About section
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(400, delayMillis = 400)) + slideInVertically(
                    initialOffsetY = { it / 4 },
                    animationSpec = tween(400, delayMillis = 400, easing = EaseOutCubic)
                )
            ) {
                EnhancedSettingsSection(
                    title = stringResource(R.string.about),
                    icon = Icons.Filled.Info
                ) {
                    AboutAppCard()
                }
            }

            // Send Feedback section
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(400, delayMillis = 500)) + slideInVertically(
                    initialOffsetY = { it / 4 },
                    animationSpec = tween(400, delayMillis = 500, easing = EaseOutCubic)
                )
            ) {
                SendFeedbackSection()
            }

            // Developer section - About Me (at the bottom, expandable)
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(400, delayMillis = 600)) + slideInVertically(
                    initialOffsetY = { it / 4 },
                    animationSpec = tween(400, delayMillis = 600, easing = EaseOutCubic)
                )
            ) {
                DeveloperAboutMeSection()
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsTopAppBar(onNavigateBack: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.settings),
                fontWeight = FontWeight.SemiBold
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@Composable
private fun EnhancedSettingsSection(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }

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
    iconBackground: Color = MaterialTheme.colorScheme.primaryContainer,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    trailing: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon with background
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconBackground),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
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
private fun EnhancedSettingsToggle(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    iconBackground: Color = MaterialTheme.colorScheme.primaryContainer,
    iconTint: Color = MaterialTheme.colorScheme.primary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon with background
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (enabled) iconBackground
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (enabled) iconTint else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
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
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}

@Composable
private fun ThemeSelector(
    currentTheme: String,
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    onThemeSelected: (String) -> Unit
) {
    Box {
        Surface(
            modifier = Modifier.clickable { onExpandChange(true) },
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = currentTheme.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandChange(false) }
        ) {
            listOf(
                "system" to Icons.Filled.BrightnessAuto,
                "light" to Icons.Filled.LightMode,
                "dark" to Icons.Filled.DarkMode
            ).forEach { (theme, icon) ->
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = theme.replaceFirstChar { it.uppercase() },
                                fontWeight = if (theme == currentTheme) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    },
                    onClick = { onThemeSelected(theme) },
                    trailingIcon = if (theme == currentTheme) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else null
                )
            }
        }
    }
}

@Composable
private fun BuddhaAiInfoCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        ProdyPrimary.copy(alpha = 0.08f),
                        ProdyTertiary.copy(alpha = 0.08f)
                    )
                )
            )
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Column {
                Text(
                    text = "About Buddha AI",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Buddha provides thoughtful, stoic-inspired reflections on your journal entries. " +
                            "Drawing from ancient wisdom and modern psychology, it offers personalized guidance " +
                            "for your self-improvement journey.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun BuddhaPrivacyNote() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Shield,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
            Column {
                Text(
                    text = "Privacy",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Your journal content is processed securely for AI features. " +
                            "Responses are cached locally to reduce data usage. " +
                            "No data is stored on external servers beyond what's needed to generate responses.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun AboutAppCard() {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        // App info row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App icon placeholder
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.SelfImprovement,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Prody",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Your Growth Companion",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "v${BuildConfig.VERSION_NAME}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tagline
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = null,
                    tint = MoodExcited,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Made with love for your personal growth journey",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SendFeedbackSection() {
    val context = LocalContext.current

    Column(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Feedback",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }

        ProdyCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:prashantchataut@gmail.com")
                        putExtra(Intent.EXTRA_SUBJECT, "Prody App Feedback - v${BuildConfig.VERSION_NAME}")
                        putExtra(Intent.EXTRA_TEXT, "Hi Prashant,\n\nI wanted to share my feedback about Prody:\n\n")
                    }
                    context.startActivity(Intent.createChooser(intent, "Send Feedback"))
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    MoodExcited.copy(alpha = 0.2f),
                                    MoodMotivated.copy(alpha = 0.2f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Feedback,
                        contentDescription = null,
                        tint = MoodExcited,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Send Feedback",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Help us improve Prody with your suggestions",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = "Open",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun DeveloperAboutMeSection() {
    val context = LocalContext.current
    var isExpanded by remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(300, easing = EaseInOutCubic),
        label = "rotation"
    )

    Column(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Code,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Developer",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }

        ProdyCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column {
                // Header - always visible, clickable to expand
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isExpanded = !isExpanded }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Developer avatar
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        ProdyPrimary,
                                        ProdyTertiary
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "PC",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Prashant Chataut",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Creator of Prody",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Icon(
                        imageVector = Icons.Filled.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .size(24.dp)
                            .rotate(rotationAngle)
                    )
                }

                // Expandable content
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = expandVertically(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.padding(bottom = 16.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )

                        // About text
                        Text(
                            text = "Hey there! I'm Prashant, a passionate developer who believes in the power of self-improvement. Prody is my way of helping people grow, reflect, and become their best selves. Thanks for being part of this journey!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Social links
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // Instagram
                            SocialLinkButton(
                                icon = Icons.Filled.CameraAlt,
                                label = "Instagram",
                                color = Color(0xFFE4405F),
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/prashantchataut_/"))
                                    context.startActivity(intent)
                                }
                            )

                            // Website
                            SocialLinkButton(
                                icon = Icons.Filled.Language,
                                label = "Website",
                                color = MoodMotivated,
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://knowprashant.vercel.app"))
                                    context.startActivity(intent)
                                }
                            )

                            // GitHub
                            SocialLinkButton(
                                icon = Icons.Filled.Code,
                                label = "GitHub",
                                color = MaterialTheme.colorScheme.onSurface,
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/prashantchataut"))
                                    context.startActivity(intent)
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Thank you message
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            ProdyPrimary.copy(alpha = 0.1f),
                                            ProdyTertiary.copy(alpha = 0.1f)
                                        )
                                    )
                                )
                                .padding(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Favorite,
                                    contentDescription = null,
                                    tint = MoodExcited,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Thank you for using Prody!",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SocialLinkButton(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
