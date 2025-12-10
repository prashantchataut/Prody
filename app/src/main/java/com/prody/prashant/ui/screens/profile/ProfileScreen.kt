package com.prody.prashant.ui.screens.profile

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.R
import com.prody.prashant.data.local.entity.AchievementEntity
import com.prody.prashant.domain.model.AchievementRarity
import com.prody.prashant.domain.model.Achievements
import com.prody.prashant.ui.components.ProdyCard
import com.prody.prashant.ui.components.StreakBadge
import com.prody.prashant.ui.theme.*

@Composable
fun ProfileScreen(
    onNavigateToSettings: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Profile header
        item {
            ProfileHeader(
                displayName = uiState.displayName,
                title = uiState.title,
                totalPoints = uiState.totalPoints,
                currentStreak = uiState.currentStreak,
                longestStreak = uiState.longestStreak,
                onSettingsClick = onNavigateToSettings
            )
        }

        // Stats summary
        item {
            ProfileStats(
                wordsLearned = uiState.wordsLearned,
                journalEntries = uiState.journalEntries,
                achievementsUnlocked = uiState.achievementsUnlocked,
                daysOnPrody = uiState.daysOnPrody
            )
        }

        // Achievements section
        item {
            Text(
                text = stringResource(R.string.achievements),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }

        // Unlocked achievements
        if (uiState.unlockedAchievements.isNotEmpty()) {
            item {
                Text(
                    text = "${stringResource(R.string.unlocked)} (${uiState.unlockedAchievements.size})",
                    style = MaterialTheme.typography.labelLarge,
                    color = AchievementUnlocked,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.unlockedAchievements) { achievement ->
                        AchievementCard(
                            achievement = achievement,
                            isUnlocked = true
                        )
                    }
                }
            }
        }

        // Locked achievements (in progress)
        if (uiState.lockedAchievements.isNotEmpty()) {
            item {
                Text(
                    text = "${stringResource(R.string.locked)} (${uiState.lockedAchievements.size})",
                    style = MaterialTheme.typography.labelLarge,
                    color = AchievementLocked,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.lockedAchievements.take(5)) { achievement ->
                        AchievementCard(
                            achievement = achievement,
                            isUnlocked = false
                        )
                    }
                }
            }
        }

        // Activity timeline hint
        item {
            ProdyCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Timeline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column {
                        Text(
                            text = "Your Growth Journey",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Keep engaging with Prody to unlock more achievements and watch your progress unfold.",
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
private fun ProfileHeader(
    displayName: String,
    title: String,
    totalPoints: Int,
    currentStreak: Int,
    longestStreak: Int,
    onSettingsClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
                    )
                )
            )
            .statusBarsPadding()
            .padding(20.dp)
    ) {
        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = stringResource(R.string.settings),
                tint = Color.White
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
                    .border(3.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = displayName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = GoldTier
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.Stars,
                        contentDescription = null,
                        tint = GoldTier,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = totalPoints.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Points",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                StreakBadge(streakDays = currentStreak)

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = null,
                        tint = GoldTier,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = longestStreak.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Best",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileStats(
    wordsLearned: Int,
    journalEntries: Int,
    achievementsUnlocked: Int,
    daysOnPrody: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ProfileStatItem(value = wordsLearned.toString(), label = "Words")
        ProfileStatItem(value = journalEntries.toString(), label = "Entries")
        ProfileStatItem(value = achievementsUnlocked.toString(), label = "Badges")
        ProfileStatItem(value = daysOnPrody.toString(), label = "Days")
    }
}

@Composable
private fun ProfileStatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AchievementCard(
    achievement: AchievementEntity,
    isUnlocked: Boolean
) {
    val rarity = try {
        AchievementRarity.valueOf(achievement.rarity.uppercase())
    } catch (e: Exception) {
        AchievementRarity.COMMON
    }

    val achievementData = Achievements.getAchievementById(achievement.id)

    ProdyCard(
        modifier = Modifier
            .width(140.dp)
            .clickable { },
        backgroundColor = if (isUnlocked)
            rarity.color.copy(alpha = 0.1f)
        else
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (isUnlocked) rarity.color.copy(alpha = 0.2f)
                        else AchievementLocked.copy(alpha = 0.2f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = achievementData?.icon ?: Icons.Filled.EmojiEvents,
                    contentDescription = null,
                    tint = if (isUnlocked) rarity.color else AchievementLocked,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = achievement.name,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = if (isUnlocked) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (!isUnlocked && achievement.requirement > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { achievement.currentProgress.toFloat() / achievement.requirement },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(CardShape),
                    color = rarity.color,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
                Text(
                    text = "${achievement.currentProgress}/${achievement.requirement}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = rarity.displayName,
                style = MaterialTheme.typography.labelSmall,
                color = rarity.color
            )
        }
    }
}
