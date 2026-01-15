package com.prody.prashant.ui.screens.profile
import com.prody.prashant.ui.icons.ProdyIcons

import androidx.compose.animation.core.animateColorAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import com.prody.prashant.ui.theme.isDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.R
import com.prody.prashant.data.local.entity.AchievementEntity
import com.prody.prashant.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Achievements Collection Screen
 *
 * Full achievements view with:
 * - Category filtering
 * - Progress tracking
 * - Unlocked/locked achievements
 * - Rarity indicators
 */

// Design System Colors for Achievements
private object AchievementColors {
    // Dark Mode
    val BackgroundDark = Color(0xFF0D2826)
    val CardBackgroundDark = Color(0xFF1A3331)
    val CardBackgroundElevatedDark = Color(0xFF223D3A)
    val AccentGreen = Color(0xFF36F97F)
    val TextPrimaryDark = Color(0xFFFFFFFF)
    val TextSecondaryDark = Color(0xFFB8C5C3)
    val TextTertiaryDark = Color(0xFF6B7F7C)
    val BorderDark = Color(0xFF2A4744)

    // Light Mode
    val BackgroundLight = Color(0xFFF5F8F7)
    val CardBackgroundLight = Color(0xFFFFFFFF)
    val CardBackgroundElevatedLight = Color(0xFFF0F5F4)
    val AccentGreenLight = Color(0xFF2ECC71)
    val TextPrimaryLight = Color(0xFF1A2B23)
    val TextSecondaryLight = Color(0xFF5A6B63)
    val TextTertiaryLight = Color(0xFF8A9B93)
    val BorderLight = Color(0xFFE0E8E4)

    // Rarity Colors (6 tiers)
    val RarityCommon = Color(0xFF78909C)      // Slate gray
    val RarityUncommon = Color(0xFF66BB6A)    // Fresh green
    val RarityRare = Color(0xFF42A5F5)        // Bright blue
    val RarityEpic = Color(0xFFAB47BC)        // Rich purple
    val RarityLegendary = Color(0xFFD4AF37)   // Gold
    val RarityMythic = Color(0xFFFFD700)      // Brilliant gold - rarest tier

    // Locked state
    val LockedOverlay = Color(0xFF3A4F4D)
}

enum class AchievementFilter(val displayName: String) {
    ALL("All"),
    UNLOCKED("Unlocked"),
    LOCKED("Locked"),
    WISDOM("Wisdom"),
    REFLECTION("Reflection"),
    CONSISTENCY("Consistency"),
    PRESENCE("Presence"),
    TEMPORAL("Temporal"),
    MASTERY("Mastery")
}

@Composable
fun AchievementsCollectionScreen(
    onNavigateBack: () -> Unit,
    viewModel: AchievementsCollectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDarkMode = isDarkTheme()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (isDarkMode) AchievementColors.BackgroundDark
                else AchievementColors.BackgroundLight
            )
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = if (isDarkMode) AchievementColors.AccentGreen
                        else AchievementColors.AccentGreenLight
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // Header
                item {
                    AchievementsHeader(
                        onBackClick = onNavigateBack,
                        unlockedCount = uiState.unlockedCount,
                        totalCount = uiState.totalCount,
                        isDarkMode = isDarkMode
                    )
                }

                // Progress Overview
                item {
                    ProgressOverviewCard(
                        unlockedCount = uiState.unlockedCount,
                        totalCount = uiState.totalCount,
                        totalPoints = uiState.totalPointsFromAchievements,
                        isDarkMode = isDarkMode
                    )
                }

                // Filter Tabs
                item {
                    FilterTabs(
                        selectedFilter = uiState.selectedFilter,
                        onFilterSelected = { viewModel.selectFilter(it) },
                        isDarkMode = isDarkMode
                    )
                }

                // Achievements List
                items(uiState.filteredAchievements) { achievement ->
                    AchievementCard(
                        achievement = achievement,
                        isDarkMode = isDarkMode,
                        onClick = { viewModel.selectAchievement(achievement) }
                    )
                }

                // Empty State
                if (uiState.filteredAchievements.isEmpty()) {
                    item {
                        EmptyAchievementsState(
                            filter = uiState.selectedFilter,
                            isDarkMode = isDarkMode
                        )
                    }
                }
            }
        }

        // Achievement Detail Dialog
        uiState.selectedAchievement?.let { achievement ->
            AchievementDetailDialog(
                achievement = achievement,
                onDismiss = { viewModel.clearSelectedAchievement() },
                isDarkMode = isDarkMode
            )
        }
    }
}

@Composable
private fun AchievementsHeader(
    onBackClick: () -> Unit,
    unlockedCount: Int,
    totalCount: Int,
    isDarkMode: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = ProdyIcons.ArrowBack,
                contentDescription = stringResource(R.string.back),
                tint = if (isDarkMode) AchievementColors.TextPrimaryDark
                       else AchievementColors.TextPrimaryLight,
                modifier = Modifier.size(24.dp)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Trophy Room",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = if (isDarkMode) AchievementColors.TextPrimaryDark
                        else AchievementColors.TextPrimaryLight
            )
            Text(
                text = "$unlockedCount / $totalCount Unlocked",
                fontFamily = PoppinsFamily,
                fontSize = 12.sp,
                color = if (isDarkMode) AchievementColors.TextTertiaryDark
                        else AchievementColors.TextTertiaryLight
            )
        }

        // Spacer for alignment
        Spacer(modifier = Modifier.size(48.dp))
    }
}

@Composable
private fun ProgressOverviewCard(
    unlockedCount: Int,
    totalCount: Int,
    totalPoints: Int,
    isDarkMode: Boolean
) {
    val progress = if (totalCount > 0) unlockedCount.toFloat() / totalCount else 0f

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        color = if (isDarkMode) AchievementColors.CardBackgroundDark
                else AchievementColors.CardBackgroundLight,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Collection Progress",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = if (isDarkMode) AchievementColors.TextSecondaryDark
                                else AchievementColors.TextSecondaryLight
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = if (isDarkMode) AchievementColors.AccentGreen
                                else AchievementColors.AccentGreenLight
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Points Earned",
                        fontFamily = PoppinsFamily,
                        fontSize = 12.sp,
                        color = if (isDarkMode) AchievementColors.TextTertiaryDark
                                else AchievementColors.TextTertiaryLight
                    )
                    Text(
                        text = "$totalPoints XP",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = if (isDarkMode) AchievementColors.TextPrimaryDark
                                else AchievementColors.TextPrimaryLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        if (isDarkMode) AchievementColors.BorderDark
                        else AchievementColors.BorderLight
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (isDarkMode) AchievementColors.AccentGreen
                            else AchievementColors.AccentGreenLight
                        )
                )
            }
        }
    }
}

@Composable
private fun FilterTabs(
    selectedFilter: AchievementFilter,
    onFilterSelected: (AchievementFilter) -> Unit,
    isDarkMode: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AchievementFilter.entries.forEach { filter ->
            FilterChip(
                filter = filter,
                isSelected = filter == selectedFilter,
                onClick = { onFilterSelected(filter) },
                isDarkMode = isDarkMode
            )
        }
    }
}

@Composable
private fun FilterChip(
    filter: AchievementFilter,
    isSelected: Boolean,
    onClick: () -> Unit,
    isDarkMode: Boolean
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            if (isDarkMode) AchievementColors.AccentGreen
            else AchievementColors.AccentGreenLight
        } else {
            if (isDarkMode) AchievementColors.CardBackgroundDark
            else AchievementColors.CardBackgroundLight
        },
        animationSpec = tween(200),
        label = "filter_bg"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) {
            Color.Black
        } else {
            if (isDarkMode) AchievementColors.TextSecondaryDark
            else AchievementColors.TextSecondaryLight
        },
        animationSpec = tween(200),
        label = "filter_text"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = filter.displayName,
            fontFamily = PoppinsFamily,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            fontSize = 13.sp,
            color = textColor
        )
    }
}

@Composable
private fun AchievementCard(
    achievement: AchievementEntity,
    isDarkMode: Boolean,
    onClick: () -> Unit
) {
    val rarity = getRarityColor(achievement.rarity)
    val isUnlocked = achievement.isUnlocked

    // Hidden achievements show "???" until unlocked
    // Secret achievements are filtered out in ViewModel (won't reach here unless unlocked)
    val showMystery = achievement.isHidden && !isUnlocked
    val displayName = if (showMystery) "???" else achievement.name
    val displayDescription = if (showMystery) "This achievement is hidden. Keep exploring to discover it!" else achievement.description

    var isAnimated by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        isAnimated = true
    }

    val scale by animateFloatAsState(
        targetValue = if (isAnimated) 1f else 0.95f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "achievement_scale"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .scale(scale)
            .clickable { onClick() },
        color = if (isDarkMode) AchievementColors.CardBackgroundDark
                else AchievementColors.CardBackgroundLight,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Achievement Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        if (isUnlocked) rarity.copy(alpha = 0.15f)
                        else AchievementColors.LockedOverlay.copy(alpha = 0.3f)
                    )
                    .border(
                        width = 2.dp,
                        color = if (isUnlocked) rarity else AchievementColors.LockedOverlay,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isUnlocked) {
                    Icon(
                        imageVector = getAchievementIcon(achievement.iconId),
                        contentDescription = null,
                        tint = rarity,
                        modifier = Modifier.size(28.dp)
                    )
                } else if (showMystery) {
                    // Mystery icon for hidden achievements
                    Icon(
                        imageVector = ProdyIcons.Help,
                        contentDescription = "Hidden achievement",
                        tint = AchievementColors.LockedOverlay,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(
                        imageVector = ProdyIcons.Lock,
                        contentDescription = "Locked",
                        tint = AchievementColors.LockedOverlay,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Achievement Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = displayName,
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = if (isUnlocked) {
                            if (isDarkMode) AchievementColors.TextPrimaryDark
                            else AchievementColors.TextPrimaryLight
                        } else {
                            if (isDarkMode) AchievementColors.TextTertiaryDark
                            else AchievementColors.TextTertiaryLight
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Rarity Badge (hide for mystery achievements)
                    if (!showMystery) {
                        RarityBadge(
                            rarity = achievement.rarity,
                            isDarkMode = isDarkMode
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = displayDescription,
                    fontFamily = PoppinsFamily,
                    fontSize = 12.sp,
                    color = if (isDarkMode) AchievementColors.TextTertiaryDark
                            else AchievementColors.TextTertiaryLight,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Progress Bar for locked achievements
                if (!isUnlocked && achievement.requirement > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(
                                    if (isDarkMode) AchievementColors.BorderDark
                                    else AchievementColors.BorderLight
                                )
                        ) {
                            val progress = (achievement.currentProgress.toFloat() / achievement.requirement).coerceIn(0f, 1f)
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(progress)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(rarity)
                            )
                        }
                        Text(
                            text = "${achievement.currentProgress}/${achievement.requirement}",
                            fontFamily = PoppinsFamily,
                            fontSize = 10.sp,
                            color = if (isDarkMode) AchievementColors.TextTertiaryDark
                                    else AchievementColors.TextTertiaryLight
                        )
                    }
                }
            }

            // Points/Status
            Column(
                horizontalAlignment = Alignment.End
            ) {
                if (isUnlocked) {
                    Icon(
                        imageVector = ProdyIcons.CheckCircle,
                        contentDescription = "Unlocked",
                        tint = if (isDarkMode) AchievementColors.AccentGreen
                               else AchievementColors.AccentGreenLight,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = "+${achievement.rewardValue} XP",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = rarity
                    )
                }
            }
        }
    }
}

@Composable
private fun RarityBadge(
    rarity: String,
    isDarkMode: Boolean
) {
    val rarityColor = getRarityColor(rarity)
    val rarityName = rarity.replaceFirstChar { it.uppercase() }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(rarityColor.copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = rarityName,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 9.sp,
            color = rarityColor
        )
    }
}

@Composable
private fun EmptyAchievementsState(
    filter: AchievementFilter,
    isDarkMode: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = ProdyIcons.EmojiEvents,
            contentDescription = null,
            tint = if (isDarkMode) AchievementColors.TextTertiaryDark
                   else AchievementColors.TextTertiaryLight,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = when (filter) {
                AchievementFilter.UNLOCKED -> "No achievements unlocked yet"
                AchievementFilter.LOCKED -> "All achievements unlocked!"
                else -> "No achievements in this category"
            },
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            color = if (isDarkMode) AchievementColors.TextSecondaryDark
                    else AchievementColors.TextSecondaryLight,
            textAlign = TextAlign.Center
        )

        if (filter == AchievementFilter.UNLOCKED) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Keep using Prody to earn achievements!",
                fontFamily = PoppinsFamily,
                fontSize = 14.sp,
                color = if (isDarkMode) AchievementColors.TextTertiaryDark
                        else AchievementColors.TextTertiaryLight,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun AchievementDetailDialog(
    achievement: AchievementEntity,
    onDismiss: () -> Unit,
    isDarkMode: Boolean
) {
    val rarity = getRarityColor(achievement.rarity)
    val isUnlocked = achievement.isUnlocked

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = if (isDarkMode) AchievementColors.CardBackgroundDark
                         else AchievementColors.CardBackgroundLight,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(rarity.copy(alpha = 0.15f))
                        .border(2.dp, rarity, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isUnlocked) getAchievementIcon(achievement.iconId)
                                      else ProdyIcons.Lock,
                        contentDescription = null,
                        tint = if (isUnlocked) rarity else AchievementColors.LockedOverlay,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column {
                    Text(
                        text = achievement.name,
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    RarityBadge(rarity = achievement.rarity, isDarkMode = isDarkMode)
                }
            }
        },
        text = {
            Column {
                Text(
                    text = achievement.description,
                    fontFamily = PoppinsFamily,
                    fontSize = 14.sp,
                    color = if (isDarkMode) AchievementColors.TextSecondaryDark
                            else AchievementColors.TextSecondaryLight
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (isUnlocked) {
                    // Show celebration message
                    if (achievement.celebrationMessage.isNotEmpty()) {
                        Text(
                            text = "\"${achievement.celebrationMessage}\"",
                            fontFamily = PoppinsFamily,
                            fontSize = 13.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = rarity
                        )
                    }
                } else {
                    // Show progress
                    Text(
                        text = "Progress: ${achievement.currentProgress}/${achievement.requirement}",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = if (isDarkMode) AchievementColors.TextPrimaryDark
                                else AchievementColors.TextPrimaryLight
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (isDarkMode) AchievementColors.BorderDark
                                else AchievementColors.BorderLight
                            )
                    ) {
                        val progress = (achievement.currentProgress.toFloat() / achievement.requirement).coerceIn(0f, 1f)
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(progress)
                                .clip(RoundedCornerShape(4.dp))
                                .background(rarity)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Category: ${achievement.category.replaceFirstChar { it.uppercase() }}",
                        fontFamily = PoppinsFamily,
                        fontSize = 12.sp,
                        color = if (isDarkMode) AchievementColors.TextTertiaryDark
                                else AchievementColors.TextTertiaryLight
                    )
                    Text(
                        text = "+${achievement.rewardValue} XP",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = rarity
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Close",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    color = if (isDarkMode) AchievementColors.AccentGreen
                            else AchievementColors.AccentGreenLight
                )
            }
        }
    )
}

private fun getRarityColor(rarity: String): Color {
    return when (rarity.lowercase()) {
        "common" -> AchievementColors.RarityCommon
        "uncommon" -> AchievementColors.RarityUncommon
        "rare" -> AchievementColors.RarityRare
        "epic" -> AchievementColors.RarityEpic
        "legendary" -> AchievementColors.RarityLegendary
        "mythic" -> AchievementColors.RarityMythic
        else -> AchievementColors.RarityCommon
    }
}

private fun getAchievementIcon(iconId: String): ImageVector {
    return when (iconId.lowercase()) {
        "journal" -> ProdyIcons.Book
        "streak" -> ProdyIcons.LocalFireDepartment
        "wisdom" -> ProdyIcons.Psychology
        "meditation" -> ProdyIcons.SelfImprovement
        "vocabulary" -> ProdyIcons.School
        "future" -> ProdyIcons.Schedule
        "star" -> ProdyIcons.Star
        "trophy" -> ProdyIcons.EmojiEvents
        "heart" -> ProdyIcons.Favorite
        "sparkle" -> ProdyIcons.AutoAwesome
        "sunrise" -> ProdyIcons.WbSunny
        "moon" -> ProdyIcons.DarkMode
        else -> ProdyIcons.EmojiEvents
    }
}
