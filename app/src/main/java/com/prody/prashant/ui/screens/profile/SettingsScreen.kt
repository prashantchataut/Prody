package com.prody.prashant.ui.screens.profile

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.prody.prashant.util.AccessibilityUtils
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.BuildConfig
import com.prody.prashant.R
import com.prody.prashant.ui.theme.isDarkTheme
import com.prody.prashant.ui.theme.MoodCalm
import com.prody.prashant.ui.theme.MoodGrateful
import com.prody.prashant.ui.components.ProdyCard
import androidx.compose.ui.graphics.Brush
import kotlinx.coroutines.delay

// =============================================================================
// COLOR DEFINITIONS - Exact colors from design specs
// =============================================================================

// Light Mode Colors
private val LightBackground = Color(0xFFF9FAFB)
private val LightCardBackground = Color(0xFFFFFFFF)
private val LightSectionHeader = Color(0xFF6C757D)
private val LightIconBackground = Color(0xFFE0E7E6)
private val LightIconColor = Color(0xFF212529)
private val LightPrimaryText = Color(0xFF212529)
private val LightSecondaryText = Color(0xFFADB5BD)
private val LightToggleActive = Color(0xFF36F97F)
private val LightToggleInactive = Color(0xFFDEE2E6)
private val LightDropdownBackground = Color(0xFFE0E7E6)
private val LightOnlineGreen = Color(0xFF36F97F)
private val LightFollowButtonBg = Color(0xFFE0E7E6)
private val LightFeedbackCardBg = Color(0xFFFFF8F0)
private val LightFeedbackIconBg = Color(0xFFFFD8A3)
private val LightFeedbackIconColor = Color(0xFFFA8800)
private val LightProdyIdText = Color(0xFFDEE2E6)

// Dark Mode Colors
private val DarkBackground = Color(0xFF0D2826)
private val DarkCardBackground = Color(0xFF1A3331)
private val DarkSectionHeader = Color(0xFFFFFFFF)
private val DarkIconBackground = Color(0xFF2A4240)
private val DarkIconColor = Color(0xFFFFFFFF)
private val DarkPrimaryText = Color(0xFFFFFFFF)
private val DarkSecondaryText = Color(0xFFD3D8D7)
private val DarkToggleActive = Color(0xFF36F97F)
private val DarkToggleInactive = Color(0xFF404B4A)
private val DarkDropdownBackground = Color(0xFF2A4240)
private val DarkOnlineGreen = Color(0xFF36F97F)
private val DarkFollowButtonBg = Color(0xFF2A4240)
private val DarkFeedbackCardBg = Color(0xFF3F2B1A)
private val DarkFeedbackIconBg = Color(0xFF5A3B27)
private val DarkFeedbackIconColor = Color(0xFFFFD8A3)
private val DarkProdyIdText = Color(0xFF404B4A)

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDark = isDarkTheme()

    // Entry animations
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    val backgroundColor = if (isDark) DarkBackground else LightBackground

    Scaffold(
        topBar = {
            SettingsTopBar(
                onNavigateBack = onNavigateBack,
                isDark = isDark
            )
        },
        containerColor = backgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .background(backgroundColor)
        ) {
            // APPEARANCE Section
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(400)) + slideInVertically(
                    initialOffsetY = { it / 4 },
                    animationSpec = tween(400, easing = EaseOutCubic)
                )
            ) {
                SettingsSection(
                    title = "APPEARANCE",
                    isDark = isDark
                ) {
                    // App Theme Row
                    SettingsRowWithDropdown(
                        icon = Icons.Filled.DarkMode,
                        title = "App Theme",
                        currentValue = uiState.themeMode.replaceFirstChar { it.uppercase() },
                        isDark = isDark,
                        onValueChange = { viewModel.setThemeMode(it) }
                    )

                    SettingsDivider(isDark)

                    // Dynamic Colors Row
                    SettingsRowWithToggle(
                        icon = Icons.Filled.Palette,
                        title = "Dynamic Colors",
                        checked = uiState.dynamicColors,
                        onCheckedChange = { viewModel.setDynamicColors(it) },
                        isDark = isDark
                    )
                }
            }

            // NOTIFICATIONS Section
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(400, delayMillis = 100)) + slideInVertically(
                    initialOffsetY = { it / 4 },
                    animationSpec = tween(400, delayMillis = 100, easing = EaseOutCubic)
                )
            ) {
                SettingsSection(
                    title = "NOTIFICATIONS",
                    isDark = isDark
                ) {
                    // Push Notifications
                    SettingsRowWithToggle(
                        icon = Icons.Filled.Notifications,
                        title = "Push Notifications",
                        checked = uiState.notificationsEnabled,
                        onCheckedChange = { viewModel.setNotificationsEnabled(it) },
                        isDark = isDark
                    )

                    SettingsDivider(isDark)

                    // Daily Wisdom
                    SettingsRowWithToggle(
                        icon = Icons.Filled.Lightbulb,
                        title = "Daily Wisdom",
                        subtitle = "Morning quotes, inspiration",
                        checked = uiState.wisdomNotificationsEnabled,
                        onCheckedChange = { viewModel.setWisdomNotifications(it) },
                        enabled = uiState.notificationsEnabled,
                        isDark = isDark
                    )

                    SettingsDivider(isDark)

                    // Journal Reminders
                    SettingsRowWithToggle(
                        icon = Icons.Filled.Edit,
                        title = "Journal Reminders",
                        subtitle = "Evening reflection",
                        checked = uiState.journalRemindersEnabled,
                        onCheckedChange = { viewModel.setJournalReminders(it) },
                        enabled = uiState.notificationsEnabled,
                        isDark = isDark
                    )
                }
            }

            // PREFERENCES Section
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(400, delayMillis = 200)) + slideInVertically(
                    initialOffsetY = { it / 4 },
                    animationSpec = tween(400, delayMillis = 200, easing = EaseOutCubic)
                )
            ) {
                SettingsSection(
                    title = "PREFERENCES",
                    isDark = isDark
                ) {
                    // Haptics
                    SettingsRowWithToggle(
                        icon = Icons.Filled.Vibration,
                        title = "Haptics",
                        checked = uiState.hapticFeedbackEnabled,
                        onCheckedChange = { viewModel.setHapticFeedback(it) },
                        isDark = isDark
                    )

                    SettingsDivider(isDark)

                    // Compact View
                    SettingsRowWithToggle(
                        icon = Icons.Filled.GridView,
                        title = "Compact View",
                        checked = uiState.compactView,
                        onCheckedChange = { viewModel.setCompactView(it) },
                        isDark = isDark
                    )
                }
            }

            // BUDDHA AI Section
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(400, delayMillis = 300)) + slideInVertically(
                    initialOffsetY = { it / 4 },
                    animationSpec = tween(400, delayMillis = 300, easing = EaseOutCubic)
                )
            ) {
                SettingsSection(
                    title = "BUDDHA AI",
                    isDark = isDark,
                    showLeafIcon = true
                ) {
                    // Enable AI (Master Toggle)
                    SettingsRowWithToggle(
                        icon = Icons.Filled.Psychology,
                        title = "Enable AI",
                        checked = uiState.buddhaAiEnabled,
                        onCheckedChange = { viewModel.setBuddhaAiEnabled(it) },
                        isDark = isDark,
                        useAccentIcon = true
                    )

                    SettingsDivider(isDark)

                    // Daily Wisdom
                    SettingsRowWithToggle(
                        icon = Icons.Filled.WbSunny,
                        title = "Daily Wisdom",
                        subtitle = "AI-generated wisdom on home screen",
                        checked = uiState.buddhaDailyWisdomEnabled,
                        onCheckedChange = { viewModel.setBuddhaDailyWisdomEnabled(it) },
                        enabled = uiState.buddhaAiEnabled,
                        isDark = isDark,
                        useAccentIcon = true
                    )

                    SettingsDivider(isDark)

                    // Quote Insights
                    SettingsRowWithToggle(
                        icon = Icons.Filled.FormatQuote,
                        title = "Quote Insights",
                        subtitle = "Meaning and daily action for quotes",
                        checked = uiState.buddhaQuoteExplanationEnabled,
                        onCheckedChange = { viewModel.setBuddhaQuoteExplanationEnabled(it) },
                        enabled = uiState.buddhaAiEnabled,
                        isDark = isDark,
                        useAccentIcon = true
                    )

                    SettingsDivider(isDark)

                    // Journal Insights
                    SettingsRowWithToggle(
                        icon = Icons.Filled.AutoAwesome,
                        title = "Journal Insights",
                        subtitle = "Emotion and theme analysis after journaling",
                        checked = uiState.buddhaJournalInsightsEnabled,
                        onCheckedChange = { viewModel.setBuddhaJournalInsightsEnabled(it) },
                        enabled = uiState.buddhaAiEnabled,
                        isDark = isDark,
                        useAccentIcon = true
                    )

                    SettingsDivider(isDark)

                    // Weekly Patterns
                    SettingsRowWithToggle(
                        icon = Icons.Filled.TrendingUp,
                        title = "Weekly Patterns",
                        subtitle = "Track mood trends and themes",
                        checked = uiState.buddhaPatternTrackingEnabled,
                        onCheckedChange = { viewModel.setBuddhaPatternTrackingEnabled(it) },
                        enabled = uiState.buddhaAiEnabled,
                        isDark = isDark,
                        useAccentIcon = true
                    )
                }
            }

            // PRIVACY & DATA Section
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(400, delayMillis = 350)) + slideInVertically(
                    initialOffsetY = { it / 4 },
                    animationSpec = tween(400, delayMillis = 350, easing = EaseOutCubic)
                )
            ) {
                PrivacyDataPolicySection()
            }

            // SYSTEM INFO Section (Enhanced "Cooler" About Section)
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(400, delayMillis = 450)) + slideInVertically(
                    initialOffsetY = { it / 4 },
                    animationSpec = tween(400, delayMillis = 450, easing = EaseOutCubic)
                )
            ) {
                SettingsSection(
                    title = "SYSTEM INFO",
                    isDark = isDark
                ) {
                    EnhancedSystemInfoCard(isDark = isDark)
                }
            }

            // DEBUG Section (only in debug builds)
            if (uiState.isDebugBuild) {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(400, delayMillis = 500)) + slideInVertically(
                        initialOffsetY = { it / 4 },
                        animationSpec = tween(400, delayMillis = 500, easing = EaseOutCubic)
                    )
                ) {
                    DebugSection(
                        isDark = isDark,
                        onTestNotification = { type -> viewModel.sendTestNotification(type) },
                        debugNotificationSent = uiState.debugNotificationSent,
                        aiProofModeEnabled = uiState.debugAiProofMode,
                        onAiProofModeChange = { viewModel.setDebugAiProofMode(it) }
                    )
                }
            }

            // FEEDBACK Section
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(400, delayMillis = 550)) + slideInVertically(
                    initialOffsetY = { it / 4 },
                    animationSpec = tween(400, delayMillis = 550, easing = EaseOutCubic)
                )
            ) {
                SettingsSection(
                    title = "FEEDBACK",
                    isDark = isDark
                ) {
                    FeedbackCard(isDark = isDark)
                }
            }

            // PRODY ID Footer
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(400, delayMillis = 650)) + slideInVertically(
                    initialOffsetY = { it / 4 },
                    animationSpec = tween(400, delayMillis = 650, easing = EaseOutCubic)
                )
            ) {
                ProdyIdFooter(isDark = isDark)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// =============================================================================
// TOP APP BAR
// =============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsTopBar(
    onNavigateBack: () -> Unit,
    isDark: Boolean
) {
    val backgroundColor = if (isDark) DarkBackground else LightBackground
    val textColor = if (isDark) DarkPrimaryText else LightPrimaryText

    TopAppBar(
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.settings),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = textColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        actions = {
            // Spacer to balance the back button
            Spacer(modifier = Modifier.size(48.dp))
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = backgroundColor
        )
    )
}

// =============================================================================
// SETTINGS SECTION
// =============================================================================

@Composable
private fun SettingsSection(
    title: String,
    isDark: Boolean,
    showLeafIcon: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val sectionHeaderColor = if (isDark) DarkSectionHeader else LightSectionHeader
    val cardBackground = if (isDark) DarkCardBackground else LightCardBackground

    Column(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (showLeafIcon) {
                Icon(
                    imageVector = Icons.Filled.Eco,
                    contentDescription = null,
                    tint = LightOnlineGreen,
                    modifier = Modifier.size(14.dp)
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Normal,
                color = sectionHeaderColor,
                letterSpacing = 1.sp
            )
        }

        // Card Container
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            color = cardBackground
        ) {
            Column(
                modifier = Modifier.padding(vertical = 4.dp),
                content = content
            )
        }
    }
}

// =============================================================================
// SETTINGS ROW WITH TOGGLE
// =============================================================================

@Composable
private fun SettingsRowWithToggle(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    isDark: Boolean,
    subtitle: String? = null,
    enabled: Boolean = true,
    useAccentIcon: Boolean = false
) {
    val iconBackground = if (isDark) DarkIconBackground else LightIconBackground
    val iconColor = when {
        useAccentIcon -> LightOnlineGreen
        isDark -> DarkIconColor
        else -> LightIconColor
    }
    val primaryText = if (isDark) DarkPrimaryText else LightPrimaryText
    val secondaryText = if (isDark) DarkSecondaryText else LightSecondaryText
    val toggleActiveColor = if (isDark) DarkToggleActive else LightToggleActive
    val toggleInactiveColor = if (isDark) DarkToggleInactive else LightToggleInactive

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon with circular background
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (enabled) iconBackground else iconBackground.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (enabled) iconColor else iconColor.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Title and Subtitle
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = if (enabled) primaryText else primaryText.copy(alpha = 0.5f)
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = secondaryText
                )
            }
        }

        // Custom Toggle Switch (no tick marks, pill-shaped)
        CustomToggleSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            activeTrackColor = toggleActiveColor,
            inactiveTrackColor = toggleInactiveColor,
            isDark = isDark
        )
    }
}

// =============================================================================
// CUSTOM TOGGLE SWITCH (No tick marks, pill-shaped)
// =============================================================================

@Composable
private fun CustomToggleSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean,
    activeTrackColor: Color,
    inactiveTrackColor: Color,
    isDark: Boolean
) {
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 20.dp else 0.dp,
        animationSpec = tween(200, easing = EaseOutCubic),
        label = "thumbOffset"
    )

    val trackColor by animateColorAsState(
        targetValue = if (checked) activeTrackColor else inactiveTrackColor,
        animationSpec = tween(200),
        label = "trackColor"
    )

    val thumbBorderColor = if (isDark) Color(0xFF404B4A) else Color(0xFF6C757D)

    Box(
        modifier = Modifier
            .width(52.dp)
            .height(32.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (enabled) trackColor else trackColor.copy(alpha = 0.5f))
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onCheckedChange(!checked) },
        contentAlignment = Alignment.CenterStart
    ) {
        // Thumb (white knob with thin dark border)
        Box(
            modifier = Modifier
                .padding(start = 4.dp)
                .offset(x = thumbOffset)
                .size(24.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(1.dp, thumbBorderColor.copy(alpha = 0.3f), CircleShape)
        )
    }
}

// =============================================================================
// SETTINGS ROW WITH DROPDOWN
// =============================================================================

@Composable
private fun SettingsRowWithDropdown(
    icon: ImageVector,
    title: String,
    currentValue: String,
    isDark: Boolean,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val iconBackground = if (isDark) DarkIconBackground else LightIconBackground
    val iconColor = if (isDark) DarkIconColor else LightIconColor
    val primaryText = if (isDark) DarkPrimaryText else LightPrimaryText
    val dropdownBg = if (isDark) DarkDropdownBackground else LightDropdownBackground

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon with circular background
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
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = primaryText,
            modifier = Modifier.weight(1f)
        )

        // Dropdown
        Box {
            Surface(
                modifier = Modifier.clickable { expanded = true },
                shape = RoundedCornerShape(8.dp),
                color = dropdownBg
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = currentValue,
                        style = MaterialTheme.typography.bodyMedium,
                        color = primaryText
                    )
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = null,
                        tint = primaryText,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                listOf("System", "Light", "Dark").forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = when (option) {
                                        "System" -> Icons.Filled.BrightnessAuto
                                        "Light" -> Icons.Filled.LightMode
                                        else -> Icons.Filled.DarkMode
                                    },
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = option,
                                    fontWeight = if (option.lowercase() == currentValue.lowercase())
                                        FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                        },
                        onClick = {
                            onValueChange(option.lowercase())
                            expanded = false
                        },
                        trailingIcon = if (option.lowercase() == currentValue.lowercase()) {
                            {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = LightOnlineGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        } else null
                    )
                }
            }
        }
    }
}

// =============================================================================
// SETTINGS DIVIDER
// =============================================================================

@Composable
private fun SettingsDivider(isDark: Boolean) {
    val dividerColor = if (isDark) DarkIconBackground.copy(alpha = 0.5f)
                       else LightIconBackground.copy(alpha = 0.5f)
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = dividerColor
    )
}

// =============================================================================
// ENHANCED SYSTEM INFO CARD (Cooler About Section)
// =============================================================================

@Composable
private fun EnhancedSystemInfoCard(isDark: Boolean) {
    val context = LocalContext.current
    val primaryText = if (isDark) DarkPrimaryText else LightPrimaryText
    val secondaryText = if (isDark) DarkSecondaryText else LightSecondaryText
    val onlineColor = if (isDark) DarkOnlineGreen else LightOnlineGreen
    val iconBackground = if (isDark) DarkIconBackground else LightIconBackground
    val followButtonBg = if (isDark) DarkFollowButtonBg else LightFollowButtonBg

    // Subtle pulsing animation for the status dot
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Top Row: Status and Meditation Icon
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                // ONLINE Status with pulsing dot
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(onlineColor.copy(alpha = pulseAlpha))
                    )
                    Text(
                        text = "ONLINE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Normal,
                        color = primaryText,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Prody Logo Text
                Text(
                    text = "Prody.",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = primaryText
                )

                // Tagline
                Text(
                    text = "Growth Companion OS",
                    style = MaterialTheme.typography.bodySmall,
                    color = secondaryText
                )
            }

            // Meditation Icon (circular with dark background)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0xFF212121) else LightPrimaryText),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.SelfImprovement,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Version and Build ID in a sleek row with subtle visual treatment
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Version Info
            Column {
                Text(
                    text = "VERSION",
                    style = MaterialTheme.typography.labelSmall,
                    color = secondaryText,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${BuildConfig.VERSION_NAME} (RC)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = primaryText
                )
            }

            // Build ID Info
            Column {
                Text(
                    text = "BUILD ID",
                    style = MaterialTheme.typography.labelSmall,
                    color = secondaryText,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = BuildConfig.VERSION_CODE.toString() + "-XJ",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = primaryText
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Minimalist Progress/Status visualization (Cooler element)
        EnhancedStatusVisualization(isDark = isDark, primaryText = primaryText, secondaryText = secondaryText)

        Spacer(modifier = Modifier.height(20.dp))

        // Developer Info Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Developer Avatar
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0xFF212121) else LightPrimaryText),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "PC",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Developer Name and Role
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Prashant Chataut",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = primaryText
                )
                Text(
                    text = "Lead Developer",
                    style = MaterialTheme.typography.bodySmall,
                    color = secondaryText
                )
            }

            // Follow Button
            Surface(
                modifier = Modifier
                    .clickable {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://github.com/prashantchataut")
                        )
                        context.startActivity(intent)
                    },
                shape = RoundedCornerShape(8.dp),
                color = followButtonBg
            ) {
                Text(
                    text = "Follow",
                    style = MaterialTheme.typography.bodyMedium,
                    color = primaryText,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

// =============================================================================
// ENHANCED STATUS VISUALIZATION (Cooler Element for About Section)
// =============================================================================

@Composable
private fun EnhancedStatusVisualization(
    isDark: Boolean,
    primaryText: Color,
    secondaryText: Color
) {
    val accentColor = LightOnlineGreen
    val trackColor = if (isDark) DarkIconBackground else LightIconBackground

    // Animated progress for visual interest
    val infiniteTransition = rememberInfiniteTransition(label = "statusAnim")
    val progressOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progressOffset"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(trackColor.copy(alpha = 0.5f))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "System Health",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = primaryText
            )
            Text(
                text = "Optimal",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = accentColor
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Progress bar representing system health
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(trackColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accentColor)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Mini stats row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MiniStatItem(
                label = "Uptime",
                value = "99.9%",
                primaryText = primaryText,
                secondaryText = secondaryText
            )
            MiniStatItem(
                label = "Response",
                value = "12ms",
                primaryText = primaryText,
                secondaryText = secondaryText
            )
            MiniStatItem(
                label = "Sync",
                value = "Active",
                primaryText = primaryText,
                secondaryText = secondaryText,
                isActive = true
            )
        }
    }
}

@Composable
private fun MiniStatItem(
    label: String,
    value: String,
    primaryText: Color,
    secondaryText: Color,
    isActive: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = if (isActive) LightOnlineGreen else primaryText
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = secondaryText
        )
    }
}

// =============================================================================
// FEEDBACK CARD
// =============================================================================

@Composable
private fun FeedbackCard(isDark: Boolean) {
    val context = LocalContext.current
    val cardBg = if (isDark) DarkFeedbackCardBg else LightFeedbackCardBg
    val iconBg = if (isDark) DarkFeedbackIconBg else LightFeedbackIconBg
    val iconColor = if (isDark) DarkFeedbackIconColor else LightFeedbackIconColor
    val primaryText = if (isDark) DarkPrimaryText else LightPrimaryText
    val secondaryText = if (isDark) DarkSecondaryText else LightSecondaryText

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:prashantchataut@gmail.com")
                    putExtra(Intent.EXTRA_SUBJECT, "Prody App Feedback - v${BuildConfig.VERSION_NAME}")
                    putExtra(Intent.EXTRA_TEXT, "Hi Prashant,\n\nI wanted to share my feedback about Prody:\n\n")
                }
                context.startActivity(Intent.createChooser(intent, "Send Feedback"))
            },
        shape = RoundedCornerShape(0.dp),
        color = cardBg
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Feedback Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.ChatBubble,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Text content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Send Feedback",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = primaryText
                )
                Text(
                    text = "Help us improve Prody",
                    style = MaterialTheme.typography.bodySmall,
                    color = secondaryText
                )
            }

            // Arrow Icon
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = primaryText,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

// =============================================================================
// PRODY ID FOOTER
// =============================================================================

@Composable
private fun ProdyIdFooter(isDark: Boolean) {
    val textColor = if (isDark) DarkProdyIdText else LightProdyIdText

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "PRODY ID: 882-991-X",
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            letterSpacing = 0.5.sp
        )
    }
}

// =============================================================================
// DEBUG SECTION (DEBUG builds only)
// =============================================================================

@Composable
private fun DebugSection(
    isDark: Boolean,
    onTestNotification: (String) -> Unit,
    debugNotificationSent: String?,
    aiProofModeEnabled: Boolean,
    onAiProofModeChange: (Boolean) -> Unit
) {
    val cardBackground = if (isDark) DarkCardBackground else LightCardBackground
    val primaryText = if (isDark) DarkPrimaryText else LightPrimaryText
    val secondaryText = if (isDark) DarkSecondaryText else LightSecondaryText
    val iconBackground = if (isDark) DarkIconBackground else LightIconBackground
    val warningColor = Color(0xFFFF9800)
    val toggleActiveColor = if (isDark) DarkToggleActive else LightToggleActive
    val toggleInactiveColor = if (isDark) DarkToggleInactive else LightToggleInactive

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        // Section Header
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.BugReport,
                contentDescription = null,
                tint = warningColor,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = "DEBUG",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Normal,
                color = warningColor,
                letterSpacing = 1.sp
            )
        }

        // Card Container
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            color = cardBackground
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // AI Proof Mode Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onAiProofModeChange(!aiProofModeEnabled) }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(iconBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Science,
                            contentDescription = null,
                            tint = LightOnlineGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "AI Proof Mode",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = primaryText
                        )
                        Text(
                            text = "Show AI generation metadata on cards",
                            style = MaterialTheme.typography.bodySmall,
                            color = secondaryText
                        )
                    }

                    CustomToggleSwitch(
                        checked = aiProofModeEnabled,
                        onCheckedChange = onAiProofModeChange,
                        enabled = true,
                        activeTrackColor = toggleActiveColor,
                        inactiveTrackColor = toggleInactiveColor,
                        isDark = isDark
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = iconBackground.copy(alpha = 0.5f)
                )

                // Title
                Text(
                    text = "Test Notifications",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = primaryText
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Tap a button to send a test notification immediately",
                    style = MaterialTheme.typography.bodySmall,
                    color = secondaryText
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Success message
                AnimatedVisibility(
                    visible = debugNotificationSent != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = LightOnlineGreen.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = LightOnlineGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Sent: ${debugNotificationSent ?: ""}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = primaryText
                            )
                        }
                    }
                }

                // Notification type buttons in a flow layout
                val notificationTypes = listOf(
                    "morning" to "Morning Wisdom",
                    "evening" to "Evening Reflection",
                    "word" to "Word of Day",
                    "streak" to "Streak Reminder",
                    "journal" to "Journal Reminder",
                    "future" to "Future Message"
                )

                // Two rows of buttons
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        notificationTypes.take(3).forEach { (type, label) ->
                            DebugNotificationButton(
                                label = label,
                                onClick = { onTestNotification(type) },
                                isDark = isDark,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        notificationTypes.drop(3).forEach { (type, label) ->
                            DebugNotificationButton(
                                label = label,
                                onClick = { onTestNotification(type) },
                                isDark = isDark,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DebugNotificationButton(
    label: String,
    onClick: () -> Unit,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val buttonBg = if (isDark) DarkIconBackground else LightIconBackground
    val buttonText = if (isDark) DarkPrimaryText else LightPrimaryText

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        color = buttonBg
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = buttonText,
                maxLines = 1
            )
        }
    }
}

// ============================================================================
// PRIVACY & DATA POLICY SECTION
// ============================================================================

@Composable
private fun PrivacyDataPolicySection() {
    var showPolicyDialog by remember { mutableStateOf(false) }

    // Privacy Policy Dialog
    if (showPolicyDialog) {
        PrivacyPolicyDialog(onDismiss = { showPolicyDialog = false })
    }

    Column(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Security,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Privacy & Data Policy",
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
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                // View Data Policy
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showPolicyDialog = true }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MoodCalm.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Policy,
                            contentDescription = null,
                            tint = MoodCalm,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "View Data Policy",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "See what data we collect and why",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Privacy Summary Card
                PrivacySummaryCard()
            }
        }
    }
}

@Composable
private fun PrivacySummaryCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        MoodCalm.copy(alpha = 0.08f),
                        MoodGrateful.copy(alpha = 0.08f)
                    )
                )
            )
            .padding(16.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.VerifiedUser,
                    contentDescription = null,
                    tint = MoodCalm,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Your Privacy at a Glance",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Privacy bullets
            PrivacyBullet(
                icon = Icons.Filled.Lock,
                text = "Journal entries encrypted on device"
            )
            PrivacyBullet(
                icon = Icons.Filled.PhoneAndroid,
                text = "All data stored locally on your device"
            )
            PrivacyBullet(
                icon = Icons.Filled.VisibilityOff,
                text = "No personal data shared with third parties"
            )
            PrivacyBullet(
                icon = Icons.Filled.Cloud,
                text = "AI features use secure API connections"
            )
        }
    }
}

@Composable
private fun PrivacyBullet(
    icon: ImageVector,
    text: String
) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MoodCalm,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PrivacyPolicyDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Filled.Policy,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                text = "Prody Data Policy",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                PolicySection(
                    title = "What We Collect",
                    content = """
                        • Journal entries (stored locally, encrypted)
                        • Mood selections and tags
                        • Gamification progress (XP, badges, streaks)
                        • App preferences and settings
                        • Anonymous performance metrics
                    """.trimIndent()
                )

                PolicySection(
                    title = "How We Use Your Data",
                    content = """
                        • To provide personalized AI insights
                        • To track your self-improvement journey
                        • To generate mood analytics and patterns
                        • To improve app performance (anonymized only)
                    """.trimIndent()
                )

                PolicySection(
                    title = "Data Storage",
                    content = """
                        • All personal data is stored locally on your device
                        • Journal entries are encrypted using industry-standard AES-256 encryption
                        • You can export or delete all data at any time
                        • No data is uploaded to servers without your explicit consent
                    """.trimIndent()
                )

                PolicySection(
                    title = "AI Features",
                    content = """
                        • Journal content may be sent to AI services for analysis when AI features are enabled
                        • AI providers do not store your personal data
                        • You can disable AI features at any time in Settings
                        • Cached AI responses are stored locally to reduce data usage
                    """.trimIndent()
                )

                PolicySection(
                    title = "Your Rights",
                    content = """
                        • Export all your data at any time
                        • Delete all data permanently
                        • Disable any data collection features
                        • Opt out of AI processing
                    """.trimIndent()
                )

                Text(
                    text = "Last updated: December 2024",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun PolicySection(
    title: String,
    content: String
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 18.sp
        )
    }
}
