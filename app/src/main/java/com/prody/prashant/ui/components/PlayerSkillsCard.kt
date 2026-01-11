package com.prody.prashant.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import com.prody.prashant.domain.gamification.GameSkillSystem
import com.prody.prashant.ui.theme.ClaritySkillColor
import com.prody.prashant.ui.theme.DisciplineSkillColor
import com.prody.prashant.ui.theme.CourageSkillColor

/**
 * Player Skills Card - Displays the 3 core skill stats.
 *
 * Shows:
 * - Clarity (Reflect): Purple theme
 * - Discipline (Sharpen): Cyan theme
 * - Courage (Commit): Orange theme
 *
 * Each skill shows:
 * - Current level
 * - XP progress to next level
 * - Daily cap remaining (optional)
 */
@Composable
fun PlayerSkillsCard(
    clarityXp: Int,
    disciplineXp: Int,
    courageXp: Int,
    dailyClarityXp: Int = 0,
    dailyDisciplineXp: Int = 0,
    dailyCourageXp: Int = 0,
    showDailyCaps: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Text(
                text = "Player Skills",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Skill bars
            SkillBar(
                skillName = "Clarity",
                verb = "Reflect",
                xp = clarityXp,
                dailyXp = dailyClarityXp,
                dailyCap = GameSkillSystem.DAILY_CLARITY_CAP,
                showDailyCap = showDailyCaps,
                icon = Icons.Filled.Lightbulb,
                color = SkillColors.Clarity
            )

            SkillBar(
                skillName = "Discipline",
                verb = "Sharpen",
                xp = disciplineXp,
                dailyXp = dailyDisciplineXp,
                dailyCap = GameSkillSystem.DAILY_DISCIPLINE_CAP,
                showDailyCap = showDailyCaps,
                icon = Icons.Filled.School,
                color = SkillColors.Discipline
            )

            SkillBar(
                skillName = "Courage",
                verb = "Commit",
                xp = courageXp,
                dailyXp = dailyCourageXp,
                dailyCap = GameSkillSystem.DAILY_COURAGE_CAP,
                showDailyCap = showDailyCaps,
                icon = Icons.Filled.Favorite,
                color = SkillColors.Courage
            )
        }
    }
}

@Composable
private fun SkillBar(
    skillName: String,
    verb: String,
    xp: Int,
    dailyXp: Int,
    dailyCap: Int,
    showDailyCap: Boolean,
    icon: ImageVector,
    color: Color
) {
    val level = calculateLevel(xp)
    val (currentXp, xpToNext) = getXpProgress(xp)
    val progress = if (xpToNext > 0) currentXp.toFloat() / xpToNext else 1f

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(500),
        label = "skill_progress"
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Skill name with icon
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = color
                    )
                }

                Column {
                    Text(
                        text = skillName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = verb,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Level badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = color.copy(alpha = 0.15f)
            ) {
                Text(
                    text = "Lv.$level",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
        }

        // Progress bar
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = color,
                trackColor = color.copy(alpha = 0.1f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$currentXp / $xpToNext XP",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (showDailyCap) {
                    val remaining = (dailyCap - dailyXp).coerceAtLeast(0)
                    Text(
                        text = "Daily: $remaining left",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (remaining > 0) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            color
                        }
                    )
                }
            }
        }
    }
}

/**
 * Compact skill display for smaller spaces.
 */
@Composable
fun CompactSkillDisplay(
    clarityXp: Int,
    disciplineXp: Int,
    courageXp: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        CompactSkillChip(
            name = "Clarity",
            level = calculateLevel(clarityXp),
            color = SkillColors.Clarity
        )
        CompactSkillChip(
            name = "Discipline",
            level = calculateLevel(disciplineXp),
            color = SkillColors.Discipline
        )
        CompactSkillChip(
            name = "Courage",
            level = calculateLevel(courageXp),
            color = SkillColors.Courage
        )
    }
}

@Composable
private fun CompactSkillChip(
    name: String,
    level: Int,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name.take(3).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = color
            )
            Text(
                text = level.toString(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

/**
 * Skill colors matching the game's identity system.
 * Uses the canonical colors defined in Color.kt for consistency.
 */
object SkillColors {
    val Clarity = ClaritySkillColor    // Serene sky blue (#6CB4D4)
    val Discipline = DisciplineSkillColor // Creative purple (#9B6DD4)
    val Courage = CourageSkillColor    // Vibrant coral (#FF7B5A)
}

// ==================== LOCAL SKILL CALCULATION HELPERS ====================

/**
 * XP thresholds for each level (cumulative) - 20-level system.
 * Matches the Skill.kt domain model.
 *
 * Level 1 starts at 0 XP, Level 20 (True Mastery) requires 15,300 total XP.
 */
private val LEVEL_THRESHOLDS = listOf(
    0,       // Level 1  - Starting point
    50,      // Level 2  - Getting started
    120,     // Level 3  - Building momentum
    220,     // Level 4  - Finding rhythm
    360,     // Level 5  - First milestone (unlocks: Advanced templates)
    550,     // Level 6  - Developing habits
    800,     // Level 7  - Growing stronger
    1150,    // Level 8  - Gaining mastery
    1600,    // Level 9  - Nearly there
    2200,    // Level 10 - Mastery I (unlocks: Weekly insight summaries)
    2900,    // Level 11 - Beyond mastery
    3700,    // Level 12 - Expert
    4600,    // Level 13 - Advanced expert
    5600,    // Level 14 - Nearly legendary
    6700,    // Level 15 - Legendary I (unlocks: Premium features)
    8000,    // Level 16 - Transcendent
    9500,    // Level 17 - Enlightened
    11200,   // Level 18 - Awakened
    13100,   // Level 19 - Nearly perfect
    15300    // Level 20 - True Mastery (unlocks: Ultimate badge + banner)
)

private const val MAX_SKILL_LEVEL = 20

/**
 * Calculate level from XP amount.
 */
private fun calculateLevel(xp: Int): Int {
    for (i in LEVEL_THRESHOLDS.indices.reversed()) {
        if (xp >= LEVEL_THRESHOLDS[i]) {
            return (i + 1).coerceAtMost(MAX_SKILL_LEVEL)
        }
    }
    return 1
}

/**
 * Get XP progress: returns Pair(currentXpInLevel, xpNeededForNextLevel)
 */
private fun getXpProgress(xp: Int): Pair<Int, Int> {
    val currentLevel = calculateLevel(xp)
    if (currentLevel >= MAX_SKILL_LEVEL) return Pair(0, 0)

    val currentThreshold = LEVEL_THRESHOLDS.getOrElse(currentLevel - 1) { 0 }
    val nextThreshold = LEVEL_THRESHOLDS.getOrElse(currentLevel) { currentThreshold + 50 }
    val xpInLevel = xp - currentThreshold
    val xpNeeded = nextThreshold - currentThreshold

    return Pair(xpInLevel, xpNeeded)
}
