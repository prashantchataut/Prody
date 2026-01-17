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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.domain.gamification.AchievementRarity
import com.prody.prashant.ui.theme.*

/**
 * Achievement UI Components for the Gamification System
 *
 * Components for displaying:
 * - Achievement badges with rarity-based styling
 * - Progress indicators for locked achievements
 * - Celebration animations for unlocked achievements
 * - Achievement lists and grids
 */

// =============================================================================
// ACHIEVEMENT BADGE
// =============================================================================

/**
 * Achievement badge component with rarity-based styling.
 *
 * @param name Achievement name
 * @param description Short description
 * @param rarity Achievement rarity level
 * @param isUnlocked Whether the achievement is unlocked
 * @param progress Progress toward unlock (0.0 to 1.0)
 * @param onClick Callback when badge is clicked
 */
@Composable
fun AchievementBadge(
    name: String,
    description: String,
    rarity: AchievementRarity,
    isUnlocked: Boolean,
    progress: Float = 0f,
    modifier: Modifier = Modifier,
    size: AchievementBadgeSize = AchievementBadgeSize.Medium,
    onClick: (() -> Unit)? = null
) {
    val dimensions = when (size) {
        AchievementBadgeSize.Small -> AchievementDimensions(64.dp, 48.dp, 10.sp, 8.sp)
        AchievementBadgeSize.Medium -> AchievementDimensions(80.dp, 60.dp, 12.sp, 10.sp)
        AchievementBadgeSize.Large -> AchievementDimensions(100.dp, 80.dp, 14.sp, 12.sp)
    }

    val rarityColor = rarity.color
    val glowColor = rarity.glowColor

    val infiniteTransition = rememberInfiniteTransition(label = "achievement_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = if (isUnlocked) 0.6f else 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )

    Column(
        modifier = modifier
            .width(dimensions.cardWidth)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Badge icon container
        Box(
            modifier = Modifier.size(dimensions.iconSize),
            contentAlignment = Alignment.Center
        ) {
            // Glow effect for unlocked or high-rarity items
            if (isUnlocked || rarity.sortOrder >= AchievementRarity.EPIC.sortOrder) {
                Box(
                    modifier = Modifier
                        .size(dimensions.iconSize)
                        .alpha(glowAlpha)
                        .background(glowColor, CircleShape)
                )
            }

            // Progress ring for locked achievements
            if (!isUnlocked && progress > 0f) {
                Canvas(modifier = Modifier.size(dimensions.iconSize)) {
                    val strokeWidth = 4.dp.toPx()
                    val radius = (this.size.minDimension - strokeWidth) / 2

                    // Background ring
                    drawCircle(
                        color = rarityColor.copy(alpha = 0.2f),
                        radius = radius,
                        style = Stroke(width = strokeWidth)
                    )

                    // Progress arc
                    drawArc(
                        color = rarityColor,
                        startAngle = -90f,
                        sweepAngle = progress * 360f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                        size = this.size.copy(
                            width = this.size.width - strokeWidth,
                            height = this.size.height - strokeWidth
                        )
                    )
                }
            }

            // Badge background
            Box(
                modifier = Modifier
                    .size(dimensions.iconSize - 8.dp)
                    .clip(CircleShape)
                    .background(
                        if (isUnlocked) {
                            Brush.linearGradient(
                                colors = listOf(
                                    rarityColor,
                                    rarityColor.copy(alpha = 0.7f)
                                )
                            )
                        } else {
                            Brush.linearGradient(
                                colors = listOf(
                                    Color.Gray.copy(alpha = 0.3f),
                                    Color.Gray.copy(alpha = 0.2f)
                                )
                            )
                        }
                    )
                    .then(
                        if (isUnlocked) {
                            Modifier.border(2.dp, rarityColor.copy(alpha = 0.5f), CircleShape)
                        } else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isUnlocked) ProdyIcons.EmojiEvents else ProdyIcons.Lock,
                    contentDescription = null,
                    tint = if (isUnlocked) Color.White else Color.Gray,
                    modifier = Modifier.size(dimensions.iconSize / 2)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Achievement name
        Text(
            text = name,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (isUnlocked) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            },
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            fontSize = dimensions.nameSize
        )

        // Rarity indicator
        if (isUnlocked) {
            Text(
                text = rarity.displayName,
                style = MaterialTheme.typography.labelSmall,
                color = rarityColor,
                fontSize = dimensions.raritySize
            )
        } else if (progress > 0f) {
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = rarityColor,
                fontSize = dimensions.raritySize
            )
        }
    }
}

enum class AchievementBadgeSize {
    Small, Medium, Large
}

private data class AchievementDimensions(
    val cardWidth: Dp,
    val iconSize: Dp,
    val nameSize: androidx.compose.ui.unit.TextUnit,
    val raritySize: androidx.compose.ui.unit.TextUnit
)

// =============================================================================
// ACHIEVEMENT CARD (Detailed View)
// =============================================================================

/**
 * Detailed achievement card showing full information.
 */
/**
 * Get rarity-based border color for achievement cards.
 * - Common: Grey (0xFF78909C)
 * - Uncommon: Green (0xFF66BB6A)
 * - Rare: Blue (0xFF42A5F5)
 * - Epic: Purple (0xFFAB47BC)
 * - Legendary: Gold (0xFFD4AF37)
 * - Mythic: Golden Yellow (0xFFFFD700)
 */
private fun getRarityBorderColor(rarity: AchievementRarity): Color = rarity.color

@Composable
fun AchievementCard(
    name: String,
    description: String,
    rarity: AchievementRarity,
    isUnlocked: Boolean,
    progress: Float = 0f,
    currentValue: Int = 0,
    targetValue: Int = 1,
    xpReward: Int = 0,
    celebrationMessage: String = "",
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val rarityColor = rarity.color
    val borderColor = getRarityBorderColor(rarity)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = if (isUnlocked) 2.dp else 1.dp,
                color = if (isUnlocked) borderColor else borderColor.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) {
                rarityColor.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Badge icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        if (isUnlocked) rarityColor.copy(alpha = 0.2f)
                        else Color.Gray.copy(alpha = 0.1f)
                    )
                    .then(
                        if (isUnlocked) Modifier.border(2.dp, borderColor, CircleShape)
                        else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isUnlocked) ProdyIcons.EmojiEvents else ProdyIcons.Lock,
                    contentDescription = null,
                    tint = if (isUnlocked) rarityColor else Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details column - takes remaining space
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Title row - horizontal layout with proper wrapping
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Achievement name - single line, ellipsis if too long
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isUnlocked) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Rarity badge - FIXED: ensure horizontal text with proper constraints
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = rarityColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = rarity.displayName,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = rarityColor,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            softWrap = false // Prevent wrapping - keeps text horizontal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Description - can wrap to 2 lines
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Progress bar for locked achievements
                if (!isUnlocked && targetValue > 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Column {
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = rarityColor,
                            trackColor = rarityColor.copy(alpha = 0.1f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$currentValue / $targetValue",
                            style = MaterialTheme.typography.labelSmall,
                            color = rarityColor,
                            maxLines = 1
                        )
                    }
                }
            }

            // XP reward badge - fixed width for consistency
            if (xpReward > 0) {
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GoldTier.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = ProdyIcons.Stars,
                                contentDescription = null,
                                tint = GoldTier,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "+$xpReward",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = GoldTier,
                                maxLines = 1
                            )
                        }
                    }

                    if (isUnlocked) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Icon(
                            imageVector = ProdyIcons.CheckCircle,
                            contentDescription = "Unlocked",
                            tint = SuccessGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

// =============================================================================
// ACHIEVEMENT CELEBRATION POPUP
// =============================================================================

/**
 * Achievement unlocked celebration animation.
 */
@Composable
fun AchievementCelebration(
    name: String,
    rarity: AchievementRarity,
    xpReward: Int,
    celebrationMessage: String,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {}
) {
    var animationPhase by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        animationPhase = 1
        kotlinx.coroutines.delay(3000)
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

    val rarityColor = rarity.color

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
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Achievement unlocked header
                Text(
                    text = "ACHIEVEMENT UNLOCKED",
                    style = MaterialTheme.typography.labelMedium,
                    color = GoldTier,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Badge
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(rarityColor, rarityColor.copy(alpha = 0.7f))
                            )
                        )
                        .border(3.dp, rarityColor.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = ProdyIcons.EmojiEvents,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Achievement name
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                // Rarity
                Text(
                    text = rarity.displayName,
                    style = MaterialTheme.typography.labelMedium,
                    color = rarityColor,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Celebration message
                Text(
                    text = celebrationMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // XP reward
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GoldTier.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = ProdyIcons.Stars,
                            contentDescription = null,
                            tint = GoldTier,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "+$xpReward XP",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GoldTier
                        )
                    }
                }
            }
        }
    }
}

// =============================================================================
// ACHIEVEMENT SUMMARY CARD
// =============================================================================

/**
 * Summary card showing achievement progress overview.
 */
@Composable
fun AchievementSummaryCard(
    totalAchievements: Int,
    unlockedCount: Int,
    recentUnlock: String? = null,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val progress = if (totalAchievements > 0) {
        unlockedCount.toFloat() / totalAchievements
    } else 0f

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = ProdyIcons.EmojiEvents,
                        contentDescription = null,
                        tint = GoldTier,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Achievements",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "$unlockedCount / $totalAchievements",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = GoldTier,
                trackColor = GoldTier.copy(alpha = 0.1f)
            )

            if (recentUnlock != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = ProdyIcons.NewReleases,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Latest: $recentUnlock",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
