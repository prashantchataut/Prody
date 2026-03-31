package com.prody.prashant.ui.screens.profile
import com.prody.prashant.ui.icons.ProdyIcons

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import com.prody.prashant.domain.gamification.AchievementRarity
import com.prody.prashant.ui.theme.UiAchievements
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.ui.theme.isDarkTheme
import com.prody.prashant.R
import com.prody.prashant.data.ai.WeeklyPatternResult
import com.prody.prashant.data.local.entity.AchievementEntity
import com.prody.prashant.ui.components.AmbientBackground
import com.prody.prashant.ui.components.FloatingParticles
import com.prody.prashant.ui.components.GrowthGarden
import com.prody.prashant.ui.components.MoodBreathingHalo
import com.prody.prashant.ui.components.PlayerSkillsCard
import com.prody.prashant.ui.components.getCurrentTimeOfDay
import com.prody.prashant.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Profile Screen - Identity & Trophy Room - Premium Phase 2 Redesign
 *
 * A completely redesigned gamified profile experience featuring:
 *
 * Design Philosophy:
 * - Extreme minimalism, flat design - NO shadows, gradients, or hi-fi elements
 * - Deep teal dark (#0D2826), clean off-white light (#F0F4F3)
 * - Vibrant neon green accent (#36F97F) for interactive elements
 * - Poppins typography throughout
 * - 8dp grid spacing system
 *
 * Features:
 * - Avatar with animated neon green progress ring
 * - DEV and BETA PIONEER badges
 * - Key metrics (Level, Streak, Words) with clean cards
 * - Story of Growth narrative section
 * - Trophy Room achievement showcase
 * - Weekly AI Insights card
 */

// ============================================================================
// DESIGN SYSTEM COLORS - Identity Room Theme (Using Theme Colors)
// ============================================================================

private object IdentityRoomColors {
    // Dark Mode - Using theme colors
    val BackgroundDark = ProdyBackgroundDark
    val CardBackgroundDark = ProdySurfaceVariantDark
    val CardBackgroundElevatedDark = ProdySurfaceContainerDark
    val AccentGreen = ProdyAccentGreen
    val AccentGreenDim = ProdyAccentGreenDark
    val TextPrimaryDark = ProdyTextPrimaryDark
    val TextSecondaryDark = ProdyTextSecondaryDark
    val TextTertiaryDark = ProdyTextTertiaryDark
    val BorderDark = ProdyOutlineDark

    // Light Mode - Using theme colors
    val BackgroundLight = ProdyBackgroundLight
    val CardBackgroundLight = ProdySurfaceLight
    val CardBackgroundElevatedLight = ProdySurfaceContainerLight
    val AccentGreenLight = ProdyAccentGreen
    val TextPrimaryLight = ProdyTextPrimaryLight
    val TextSecondaryLight = ProdyTextSecondaryLight
    val TextTertiaryLight = ProdyTextTertiaryLight
    val BorderLight = ProdyOutlineLight

    // Badge Colors - Using theme colors
    val DevBadgeBackground = ProdySurfaceVariantDark
    val DevBadgeText = ProdyAccentGreen
    val BetaBadgeBackground = ProdyPremiumVioletContainer
    val BetaBadgeText = ProdyPremiumViolet

    // Achievement Rarity - Using theme colors
    val RarityCommon = com.prody.prashant.ui.theme.RarityCommon
    val RarityUncommon = com.prody.prashant.ui.theme.RarityUncommon
    val RarityRare = com.prody.prashant.ui.theme.RarityRare
    val RarityEpic = com.prody.prashant.ui.theme.RarityEpic
    val RarityLegendary = com.prody.prashant.ui.theme.RarityLegendary
}

fun ProfileScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToAchievements: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val screenState = rememberProfileScreenState(uiState)
    val isDarkMode = isDarkTheme()

    // Premium theme colors using MaterialTheme
    val backgroundColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val surfaceElevated = MaterialTheme.colorScheme.surfaceVariant
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant
    val textTertiary = if (isDarkMode) ProdyTextTertiaryDark else ProdyTextTertiaryLight
    val accentColor = MaterialTheme.colorScheme.primary
    val dividerColor = MaterialTheme.colorScheme.outlineVariant

    // Soul Layer Context for Identity Card


    // Entry animation
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(50)
        isVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {

        HorizontalDivider(
            modifier = Modifier.align(Alignment.TopCenter).statusBarsPadding().padding(top = 72.dp),
            color = textPrimary.copy(alpha = 0.05f)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item {
                PremiumHeader(
                    title = "Identity",
                    subtitle = "Soul Layer",
                    onSettingsClick = onNavigateToSettings
                )
            }
        }
    }
}

private fun PremiumHeader(
    title: String,
    subtitle: String,
    onSettingsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        IconButton(onClick = onSettingsClick) {
            Icon(Icons.Default.Settings, contentDescription = "Settings")
        }
    }
}

fun AchievementShelf(
    unlockedCount: Int,
    totalCount: Int,
    achievements: List<AchievementEntity>,
    onViewAllClick: () -> Unit,
    isDarkMode: Boolean
) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(end = 24.dp)
        ) {
            items(achievements) { achievement ->
                AchievementBadge(achievement = achievement, isDarkMode = isDarkMode)
            }
        }
    }
}

fun AchievementBadge(
    achievement: AchievementEntity,
    isDarkMode: Boolean
) {
    val cardBg = if (isDarkMode) IdentityRoomColors.CardBackgroundElevatedDark else IdentityRoomColors.CardBackgroundElevatedLight
    val textPrimary = if (isDarkMode) IdentityRoomColors.TextPrimaryDark else IdentityRoomColors.TextPrimaryLight

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        Surface(
            modifier = Modifier.size(64.dp),
            shape = RoundedCornerShape(16.dp),
            color = cardBg,
            border = BorderStroke(1.dp, textPrimary.copy(alpha = 0.05f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = IdentityRoomColors.AccentGreen,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = achievement.name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                color = textPrimary
            )
        )
    }
}

data class ProfileScreenState(val uiState: ProfileUiState)
fun rememberProfileScreenState(uiState: ProfileUiState) = remember(uiState) { ProfileScreenState(uiState) }

private fun formatCompactNumber(number: Int): String = number.toString()
