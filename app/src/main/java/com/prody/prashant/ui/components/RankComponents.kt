package com.prody.prashant.ui.components
import com.prody.prashant.ui.icons.ProdyIcons

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.domain.gamification.Rank
import com.prody.prashant.domain.gamification.RankMessages
import com.prody.prashant.domain.gamification.RankState
import com.prody.prashant.domain.gamification.Skill
import com.prody.prashant.ui.theme.*

/**
 * Rank UI Components for the Gamification System
 *
 * Components for displaying:
 * - User rank badges with tier-based styling
 * - Rank progress indicators
 * - Rank advancement celebrations
 * - Skill balance visualization
 */

// =============================================================================
// RANK BADGE
// =============================================================================

/**
 * Rank badge component with tier-based styling and glow effects.
 *
 * @param rank The user's current rank
 * @param size Badge size variant
 * @param showLabel Whether to show rank name below badge
 * @param onClick Optional click handler
 */
@Composable
fun RankBadgeDisplay(
    rank: Rank,
    modifier: Modifier = Modifier,
    size: RankBadgeSize = RankBadgeSize.Medium,
    showLabel: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val dimensions = when (size) {
        RankBadgeSize.Small -> RankDimensions(40.dp, 12.sp, 10.sp)
        RankBadgeSize.Medium -> RankDimensions(56.dp, 14.sp, 12.sp)
        RankBadgeSize.Large -> RankDimensions(72.dp, 16.sp, 14.sp)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "rank_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )

    Column(
        modifier = modifier
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(dimensions.badgeSize),
            contentAlignment = Alignment.Center
        ) {
            // Glow effect for higher ranks
            if (rank.ordinal >= Rank.PHILOSOPHER.ordinal) {
                Box(
                    modifier = Modifier
                        .size(dimensions.badgeSize)
                        .alpha(glowAlpha)
                        .background(rank.color.copy(alpha = 0.3f), CircleShape)
                )
            }

            // Badge background
            Box(
                modifier = Modifier
                    .size(dimensions.badgeSize - 4.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                rank.color,
                                rank.color.copy(alpha = 0.7f)
                            )
                        )
                    )
                    .border(2.dp, rank.color.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = rank.icon,
                    contentDescription = rank.displayName,
                    tint = Color.White,
                    modifier = Modifier.size(dimensions.badgeSize / 2)
                )
            }
        }

        if (showLabel) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = rank.displayName,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = rank.color,
                fontSize = dimensions.nameSize
            )
        }
    }
}

enum class RankBadgeSize {
    Small, Medium, Large
}

private data class RankDimensions(
    val badgeSize: Dp,
    val nameSize: androidx.compose.ui.unit.TextUnit,
    val descSize: androidx.compose.ui.unit.TextUnit
)

// =============================================================================
// RANK PROGRESS CARD
// =============================================================================

/**
 * Card showing rank progress with skill breakdown.
 */
@Composable
fun RankProgressCard(
    rankState: RankState,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with rank badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RankBadgeDisplay(
                    rank = rankState.currentRank,
                    size = RankBadgeSize.Medium,
                    showLabel = false
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = rankState.currentRank.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = rankState.currentRank.color
                    )
                    Text(
                        text = rankState.currentRank.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Combined level
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "${rankState.combinedLevel}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = rankState.currentRank.color
                    )
                    Text(
                        text = "/ ${Rank.MAX_COMBINED_LEVEL}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Progress to next rank
            if (!rankState.isMaxRank && rankState.nextRank != null) {
                Spacer(modifier = Modifier.height(16.dp))

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Next: ${rankState.nextRank.displayName}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${rankState.levelsToNextRank} levels to go",
                            style = MaterialTheme.typography.labelSmall,
                            color = rankState.nextRank.color
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    LinearProgressIndicator(
                        progress = { rankState.progressToNextRank },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = rankState.nextRank.color,
                        trackColor = rankState.nextRank.color.copy(alpha = 0.1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Skill breakdown
            SkillBreakdownRow(
                clarityLevel = rankState.clarityLevel,
                disciplineLevel = rankState.disciplineLevel,
                courageLevel = rankState.courageLevel
            )

            // Suggestion for balance
            if (!rankState.isFullyMastered) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = rankState.lowestSkill.color.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = ProdyIcons.Lightbulb,
                            contentDescription = null,
                            tint = rankState.lowestSkill.color,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = rankState.suggestedFocus,
                            style = MaterialTheme.typography.bodySmall,
                            color = rankState.lowestSkill.color
                        )
                    }
                }
            }
        }
    }
}

/**
 * Row showing skill level breakdown.
 */
@Composable
private fun SkillBreakdownRow(
    clarityLevel: Int,
    disciplineLevel: Int,
    courageLevel: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        SkillLevelIndicator(
            skill = Skill.CLARITY,
            level = clarityLevel,
            maxLevel = 20
        )
        SkillLevelIndicator(
            skill = Skill.DISCIPLINE,
            level = disciplineLevel,
            maxLevel = 20
        )
        SkillLevelIndicator(
            skill = Skill.COURAGE,
            level = courageLevel,
            maxLevel = 20
        )
    }
}

/**
 * Individual skill level indicator.
 */
@Composable
private fun SkillLevelIndicator(
    skill: Skill,
    level: Int,
    maxLevel: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(skill.color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$level",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = skill.color
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = skill.displayName,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// =============================================================================
// RANK ADVANCEMENT CELEBRATION
// =============================================================================

/**
 * Rank advancement celebration animation.
 */
@Composable
fun RankAdvancementCelebration(
    previousRank: Rank,
    newRank: Rank,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {}
) {
    var animationPhase by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        animationPhase = 1
        kotlinx.coroutines.delay(3500)
        animationPhase = 2
        kotlinx.coroutines.delay(500)
        onDismiss()
    }

    val scale by animateFloatAsState(
        targetValue = when (animationPhase) {
            0 -> 0f
            1 -> 1f
            else -> 0.8f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "celebration_scale"
    )

    val alpha by animateFloatAsState(
        targetValue = when (animationPhase) {
            0 -> 0f
            1 -> 1f
            else -> 0f
        },
        animationSpec = tween(300),
        label = "celebration_alpha"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .alpha(alpha),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Text(
                    text = "RANK ACHIEVED",
                    style = MaterialTheme.typography.labelLarge,
                    color = GoldTier,
                    letterSpacing = 3.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Rank badge with glow
                Box(
                    modifier = Modifier.size(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Animated glow
                    val infiniteTransition = rememberInfiniteTransition(label = "rank_celebration")
                    val glowScale by infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 1.2f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000, easing = EaseInOutCubic),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "glow_scale"
                    )

                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .scale(glowScale)
                            .alpha(0.3f)
                            .background(newRank.color, CircleShape)
                    )

                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(newRank.color, newRank.color.copy(alpha = 0.7f))
                                )
                            )
                            .border(3.dp, newRank.color.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = newRank.icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Rank name
                Text(
                    text = newRank.displayName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = newRank.color
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description
                Text(
                    text = newRank.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Advancement message
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = newRank.color.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = RankMessages.advancementMessage(newRank),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = newRank.color,
                        textAlign = TextAlign.Center,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Philosophical origin
                Text(
                    text = newRank.philosophicalOrigin,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// =============================================================================
// COMPACT RANK DISPLAY
// =============================================================================

/**
 * Compact rank display for headers and lists.
 */
@Composable
fun CompactRankDisplay(
    rank: Rank,
    combinedLevel: Int,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(12.dp),
        color = rank.color.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(rank.color),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = rank.icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }

            Column {
                Text(
                    text = rank.displayName,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = rank.color
                )
                Text(
                    text = "Level $combinedLevel",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// =============================================================================
// RANK LIST ITEM
// =============================================================================

/**
 * Rank list item showing all ranks with current highlighted.
 */
@Composable
fun RankListItem(
    rank: Rank,
    isCurrentRank: Boolean,
    isUnlocked: Boolean,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val backgroundColor = when {
        isCurrentRank -> rank.color.copy(alpha = 0.15f)
        isUnlocked -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank badge
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (isUnlocked) rank.color.copy(alpha = 0.2f)
                        else Color.Gray.copy(alpha = 0.1f)
                    )
                    .then(
                        if (isCurrentRank) Modifier.border(2.dp, rank.color, CircleShape)
                        else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isUnlocked) rank.icon else ProdyIcons.Lock,
                    contentDescription = null,
                    tint = if (isUnlocked) rank.color else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Rank info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = rank.displayName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isUnlocked) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        }
                    )

                    if (isCurrentRank) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = rank.color.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "CURRENT",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = rank.color,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Text(
                    text = rank.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                        alpha = if (isUnlocked) 1f else 0.5f
                    )
                )
            }

            // Level requirement
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Lv ${rank.minCombinedLevel}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isUnlocked) rank.color else Color.Gray
                )
            }
        }
    }
}

// =============================================================================
// HELPER EXTENSIONS
// =============================================================================

/**
 * Get icon for rank.
 */
val Rank.icon: androidx.compose.ui.graphics.vector.ImageVector
    get() = when (this) {
        Rank.SEEKER -> ProdyIcons.Search
        Rank.LEARNER -> ProdyIcons.MenuBook
        Rank.INITIATE -> ProdyIcons.Flare
        Rank.STUDENT -> ProdyIcons.School
        Rank.PRACTITIONER -> ProdyIcons.SelfImprovement
        Rank.CONTEMPLATIVE -> ProdyIcons.Spa
        Rank.PHILOSOPHER -> ProdyIcons.Psychology
        Rank.SAGE -> ProdyIcons.Elderly
        Rank.LUMINARY -> ProdyIcons.WbSunny
        Rank.AWAKENED -> ProdyIcons.AutoAwesome
    }

/**
 * Get color for skill.
 */
val Skill.color: Color
    get() = when (this) {
        Skill.CLARITY -> ClaritySkillColor
        Skill.DISCIPLINE -> DisciplineSkillColor
        Skill.COURAGE -> CourageSkillColor
    }
