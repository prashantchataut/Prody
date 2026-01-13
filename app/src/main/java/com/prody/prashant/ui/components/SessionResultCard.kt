package com.prody.prashant.ui.components
import com.prody.prashant.ui.icons.ProdyIcons

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.prody.prashant.domain.gamification.*

/**
 * Session Result Card - Clean, non-spammy feedback after completing a game mode.
 *
 * Shows:
 * - What you did (summary)
 * - What you gained (XP per skill, tokens)
 * - Level ups (if any)
 * - Mission progress
 * - Seed bloom (if applicable)
 * - Next suggested action
 *
 * Replaces spam toasts like "+25 XP" with a single, satisfying summary.
 */
@Composable
fun SessionResultCard(
    sessionResult: SessionResult,
    onDismiss: () -> Unit,
    onNextAction: ((SuggestionType) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(true) }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + slideInVertically { -it },
        exit = fadeOut() + slideOutVertically { -it }
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with session type icon
                SessionHeader(sessionResult.sessionType, sessionResult.summary.headline)

                Spacer(modifier = Modifier.height(16.dp))

                // Summary details
                if (sessionResult.summary.details.isNotEmpty()) {
                    SessionDetails(sessionResult.summary.details)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Rewards section
                if (sessionResult.hasRewards()) {
                    RewardsSection(sessionResult.rewards)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Level ups
                if (sessionResult.hasLevelUp()) {
                    LevelUpSection(sessionResult.rewards.skillLevelUps)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Mission progress
                sessionResult.missionProgress?.let { progress ->
                    MissionProgressSection(progress)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Seed bloom
                sessionResult.seedBloom?.let { bloom ->
                    SeedBloomSection(bloom)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Divider before next action
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                // Next suggestion
                sessionResult.nextSuggestion?.let { suggestion ->
                    NextSuggestionSection(
                        suggestion = suggestion,
                        onTap = {
                            onNextAction?.invoke(suggestion.type)
                            isVisible = false
                            onDismiss()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Dismiss button
                TextButton(
                    onClick = {
                        isVisible = false
                        onDismiss()
                    }
                ) {
                    Text("Done")
                }
            }
        }
    }
}

@Composable
private fun SessionHeader(
    sessionType: GameSessionType,
    headline: String
) {
    val icon = when (sessionType) {
        GameSessionType.REFLECT -> ProdyIcons.Edit
        GameSessionType.SHARPEN -> ProdyIcons.Psychology
        GameSessionType.COMMIT -> ProdyIcons.Schedule
    }

    val color = when (sessionType) {
        GameSessionType.REFLECT -> MaterialTheme.colorScheme.primary
        GameSessionType.SHARPEN -> MaterialTheme.colorScheme.secondary
        GameSessionType.COMMIT -> MaterialTheme.colorScheme.tertiary
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = headline,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun SessionDetails(details: List<String>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        details.forEachIndexed { index, detail ->
            Text(
                text = detail,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (index < details.size - 1) {
                Text(
                    text = " • ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
private fun RewardsSection(rewards: SessionRewards) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Skill XP gains
        rewards.skillXpGains.forEach { (skill, xp) ->
            if (xp > 0) {
                SkillXpGain(skill, xp)
            }
        }

        // Tokens
        if (rewards.tokens > 0) {
            TokenGain(rewards.tokens)
        }
    }
}

@Composable
private fun SkillXpGain(
    skill: GameSkillSystem.SkillType,
    xp: Int
) {
    val (icon, color) = when (skill) {
        GameSkillSystem.SkillType.CLARITY -> ProdyIcons.Visibility to MaterialTheme.colorScheme.primary
        GameSkillSystem.SkillType.DISCIPLINE -> ProdyIcons.FitnessCenter to MaterialTheme.colorScheme.secondary
        GameSkillSystem.SkillType.COURAGE -> ProdyIcons.EmojiEvents to MaterialTheme.colorScheme.tertiary
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "+$xp",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = skill.displayName,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TokenGain(tokens: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = ProdyIcons.Token,
            contentDescription = null,
            tint = Color(0xFFFFD700),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "+$tokens",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFD700)
        )
        Text(
            text = "Tokens",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun LevelUpSection(levelUps: List<SkillLevelUp>) {
    levelUps.forEach { levelUp ->
        val (icon, color) = when (levelUp.skill) {
            GameSkillSystem.SkillType.CLARITY -> ProdyIcons.Visibility to MaterialTheme.colorScheme.primary
            GameSkillSystem.SkillType.DISCIPLINE -> ProdyIcons.FitnessCenter to MaterialTheme.colorScheme.secondary
            GameSkillSystem.SkillType.COURAGE -> ProdyIcons.EmojiEvents to MaterialTheme.colorScheme.tertiary
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = color.copy(alpha = 0.15f)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = ProdyIcons.TrendingUp,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${levelUp.skill.displayName} Level Up!",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = color
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Lv ${levelUp.previousLevel} → ${levelUp.newLevel}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
        }
    }
}

@Composable
private fun MissionProgressSection(progress: MissionProgress) {
    val progressPercent = (progress.newProgress.toFloat() / progress.targetValue).coerceIn(0f, 1f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (progress.justCompleted)
                MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
            else
                MaterialTheme.colorScheme.surfaceContainer
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (progress.justCompleted) ProdyIcons.CheckCircle else ProdyIcons.Flag,
                    contentDescription = null,
                    tint = if (progress.justCompleted)
                        MaterialTheme.colorScheme.tertiary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = progress.missionTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = if (progress.justCompleted) "Complete!" else "${progress.newProgress}/${progress.targetValue}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = if (progress.justCompleted) FontWeight.Bold else FontWeight.Normal,
                    color = if (progress.justCompleted)
                        MaterialTheme.colorScheme.tertiary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (!progress.justCompleted) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progressPercent },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun SeedBloomSection(bloom: SeedBloomInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF4CAF50).copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = ProdyIcons.LocalFlorist,
                contentDescription = null,
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Seed Bloomed!",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF4CAF50)
                )
                Text(
                    text = "\"${bloom.seedContent}\"",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "+${bloom.bonusTokens}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFD700)
            )
        }
    }
}

@Composable
private fun NextSuggestionSection(
    suggestion: NextSuggestion,
    onTap: () -> Unit
) {
    FilledTonalButton(
        onClick = onTap,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        val icon = when (suggestion.type) {
            SuggestionType.COMPLETE_DAILY_MISSION -> ProdyIcons.Flag
            SuggestionType.TRY_DIFFERENT_MODE -> ProdyIcons.SwapHoriz
            SuggestionType.BLOOM_SEED -> ProdyIcons.LocalFlorist
            SuggestionType.CHECK_WEEKLY_TRIAL -> ProdyIcons.EmojiEvents
            SuggestionType.VIEW_PROGRESS -> ProdyIcons.Analytics
        }

        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = suggestion.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            suggestion.reason?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                )
            }
        }
        Icon(
            imageVector = ProdyIcons.ChevronRight,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
    }
}
