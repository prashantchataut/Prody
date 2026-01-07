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
    val level = GameSkillSystem.calculateLevel(xp)
    val (currentXp, xpToNext) = GameSkillSystem.getXpProgress(xp)
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
            level = GameSkillSystem.calculateLevel(clarityXp),
            color = SkillColors.Clarity
        )
        CompactSkillChip(
            name = "Discipline",
            level = GameSkillSystem.calculateLevel(disciplineXp),
            color = SkillColors.Discipline
        )
        CompactSkillChip(
            name = "Courage",
            level = GameSkillSystem.calculateLevel(courageXp),
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
 */
object SkillColors {
    val Clarity = Color(0xFF7C4DFF)    // Purple
    val Discipline = Color(0xFF00BCD4) // Cyan
    val Courage = Color(0xFFFF9800)    // Orange
}
