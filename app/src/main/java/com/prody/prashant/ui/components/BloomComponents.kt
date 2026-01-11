package com.prody.prashant.ui.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.ui.draw.rotate
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.ui.theme.*

/**
 * Bloom UI Components for the Gamification System
 *
 * The Bloom System represents the growth of vocabulary knowledge into active use.
 * Components visualize:
 * - Seeds (learned words) waiting to bloom
 * - Blooming progress when words are used in context
 * - Bloom celebrations when mastery is achieved
 * - Garden overview showing bloom stats
 */

// =============================================================================
// SEED CARD
// =============================================================================

/**
 * Card displaying a vocabulary seed ready to bloom.
 *
 * @param word The vocabulary word
 * @param definition Short definition
 * @param daysUntilBloom Days until this seed can bloom
 * @param isReady Whether the seed is ready to bloom
 * @param onClick Callback when card is clicked
 */
@Composable
fun SeedCard(
    word: String,
    definition: String,
    daysUntilBloom: Int,
    isReady: Boolean,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "seed_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isReady) 1.05f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        colors = CardDefaults.cardColors(
            containerColor = if (isReady) {
                BloomReady.copy(alpha = 0.1f)
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
            // Seed icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(
                        if (isReady) BloomReady.copy(alpha = 0.2f)
                        else SeedDormant.copy(alpha = 0.2f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isReady) Icons.Filled.LocalFlorist else Icons.Filled.Grass,
                    contentDescription = null,
                    tint = if (isReady) BloomReady else SeedDormant,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Word info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = word,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = definition,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Status indicator
            Column(
                horizontalAlignment = Alignment.End
            ) {
                if (isReady) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = BloomReady.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "READY",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = BloomReady,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Text(
                        text = "$daysUntilBloom days",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "until ready",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

// =============================================================================
// BLOOM PROGRESS INDICATOR
// =============================================================================

/**
 * Circular progress indicator for bloom progress.
 */
@Composable
fun BloomProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    size: BloomIndicatorSize = BloomIndicatorSize.Medium
) {
    val dimensions = when (size) {
        BloomIndicatorSize.Small -> 48.dp
        BloomIndicatorSize.Medium -> 64.dp
        BloomIndicatorSize.Large -> 80.dp
    }

    val strokeWidth = when (size) {
        BloomIndicatorSize.Small -> 4.dp
        BloomIndicatorSize.Medium -> 6.dp
        BloomIndicatorSize.Large -> 8.dp
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000, easing = EaseOutCubic),
        label = "bloom_progress"
    )

    Box(
        modifier = modifier.size(dimensions),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(dimensions)) {
            val stroke = strokeWidth.toPx()
            val radius = (this.size.minDimension - stroke) / 2

            // Background ring
            drawCircle(
                color = BloomReady.copy(alpha = 0.1f),
                radius = radius,
                style = Stroke(width = stroke)
            )

            // Progress arc
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        SeedDormant,
                        BloomGrowing,
                        BloomReady
                    )
                ),
                startAngle = -90f,
                sweepAngle = animatedProgress * 360f,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
                topLeft = Offset(stroke / 2, stroke / 2),
                size = this.size.copy(
                    width = this.size.width - stroke,
                    height = this.size.height - stroke
                )
            )
        }

        // Center icon
        val iconColor = when {
            progress >= 1f -> BloomReady
            progress >= 0.5f -> BloomGrowing
            else -> SeedDormant
        }

        Icon(
            imageVector = when {
                progress >= 1f -> Icons.Filled.LocalFlorist
                progress >= 0.5f -> Icons.Filled.Grass
                else -> Icons.Filled.Grain
            },
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(dimensions / 2.5f)
        )
    }
}

enum class BloomIndicatorSize {
    Small, Medium, Large
}

// =============================================================================
// BLOOM CELEBRATION
// =============================================================================

/**
 * Bloom celebration animation when a word is fully mastered.
 */
@Composable
fun BloomCelebration(
    word: String,
    xpEarned: Int,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {}
) {
    var animationPhase by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        animationPhase = 1
        kotlinx.coroutines.delay(2500)
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
        label = "bloom_scale"
    )

    val alpha by animateFloatAsState(
        targetValue = when (animationPhase) {
            0 -> 0f
            1 -> 1f
            else -> 0f
        },
        animationSpec = tween(300),
        label = "bloom_alpha"
    )

    // Petal rotation animation
    val infiniteTransition = rememberInfiniteTransition(label = "bloom_rotate")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
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
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Text(
                    text = "WORD BLOOMED!",
                    style = MaterialTheme.typography.labelMedium,
                    color = BloomReady,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Animated flower
                Box(
                    modifier = Modifier.size(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Rotating petals background
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .rotate(rotation)
                            .alpha(0.3f)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        BloomReady,
                                        BloomReady.copy(alpha = 0f)
                                    )
                                ),
                                CircleShape
                            )
                    )

                    // Main flower icon
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(BloomReady),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocalFlorist,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Word
                Text(
                    text = word,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Message
                Text(
                    text = "You've mastered this word through active use!",
                    style = MaterialTheme.typography.bodySmall,
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
                            imageVector = Icons.Filled.Stars,
                            contentDescription = null,
                            tint = GoldTier,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "+$xpEarned XP",
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
// GARDEN SUMMARY CARD
// =============================================================================

/**
 * Summary card showing bloom garden statistics.
 */
@Composable
fun GardenSummaryCard(
    totalSeeds: Int,
    readyToBloom: Int,
    bloomedTotal: Int,
    currentStreak: Int,
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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Park,
                        contentDescription = null,
                        tint = BloomReady,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Your Garden",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (readyToBloom > 0) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = BloomReady.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "$readyToBloom ready!",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = BloomReady,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                GardenStat(
                    icon = Icons.Filled.Grass,
                    value = totalSeeds,
                    label = "Seeds",
                    color = SeedDormant
                )
                GardenStat(
                    icon = Icons.Filled.LocalFlorist,
                    value = bloomedTotal,
                    label = "Bloomed",
                    color = BloomReady
                )
                GardenStat(
                    icon = Icons.Filled.LocalFireDepartment,
                    value = currentStreak,
                    label = "Streak",
                    color = StreakFire
                )
            }
        }
    }
}

@Composable
private fun GardenStat(
    icon: ImageVector,
    value: Int,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
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
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "$value",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// =============================================================================
// BLOOM STREAK CARD
// =============================================================================

/**
 * Card displaying bloom streak with encouragement.
 */
@Composable
fun BloomStreakCard(
    currentStreak: Int,
    longestStreak: Int,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "streak_fire")
    val fireAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fire_pulse"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        colors = CardDefaults.cardColors(
            containerColor = if (currentStreak > 0) {
                StreakFire.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Fire icon with animation
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        if (currentStreak > 0) StreakFire.copy(alpha = 0.2f)
                        else Color.Gray.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.LocalFireDepartment,
                    contentDescription = null,
                    tint = if (currentStreak > 0) {
                        StreakFire.copy(alpha = fireAlpha)
                    } else {
                        Color.Gray
                    },
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Streak info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Bloom Streak",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                if (currentStreak > 0) {
                    Text(
                        text = "$currentStreak day${if (currentStreak != 1) "s" else ""} of blooming!",
                        style = MaterialTheme.typography.bodySmall,
                        color = StreakFire
                    )
                } else {
                    Text(
                        text = "Bloom a word today to start your streak",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Best streak
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Best",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$longestStreak",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (currentStreak >= longestStreak && currentStreak > 0) {
                        GoldTier
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}

// =============================================================================
// COMPACT BLOOM CARD
// =============================================================================

/**
 * Compact bloom card for lists.
 */
@Composable
fun CompactBloomCard(
    word: String,
    stage: BloomStage,
    progress: Float,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(12.dp),
        color = stage.color.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Stage icon
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(stage.color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = stage.icon,
                    contentDescription = null,
                    tint = stage.color,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Word and progress
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = word,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = stage.color,
                    trackColor = stage.color.copy(alpha = 0.1f)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Stage label
            Text(
                text = stage.displayName,
                style = MaterialTheme.typography.labelSmall,
                color = stage.color
            )
        }
    }
}

// =============================================================================
// BLOOM STAGE ENUM
// =============================================================================

enum class BloomStage(
    val displayName: String,
    val icon: ImageVector,
    val color: Color
) {
    SEED("Seed", Icons.Filled.Grain, SeedDormant),
    SPROUTING("Sprouting", Icons.Filled.Grass, BloomGrowing),
    GROWING("Growing", Icons.Filled.Nature, BloomGrowing),
    BLOOMING("Blooming", Icons.Filled.LocalFlorist, BloomReady),
    FLOURISHING("Flourishing", Icons.Filled.Park, MoodGrateful)
}

