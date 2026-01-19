package com.prody.prashant.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.prody.prashant.domain.intelligence.*
import com.prody.prashant.domain.model.Mood
import com.prody.prashant.ui.icons.ProdyIcons
import com.prody.prashant.ui.theme.ProdyTokens
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * ================================================================================================
 * SOUL LAYER UI COMPONENTS
 * ================================================================================================
 *
 * These components display intelligent, context-aware content from the Soul Layer system.
 * They follow Prody's flat design principles:
 * - No shadows/elevation
 * - Scale animations for interactions
 * - Warm, encouraging visual language
 * - Accessibility-first design
 */

// ================================================================================================
// SURFACED MEMORY CARD
// ================================================================================================

/**
 * Displays a surfaced memory with context-aware styling.
 * Shows memories that the Soul Layer has intelligently selected to surface.
 */
@Composable
fun SurfacedMemoryCard(
    memory: SurfacedMemory,
    onExpand: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "memory_card_scale"
    )

    val memoryColor = getMemoryTypeColor(memory.memory.type)
    val memoryIcon = getMemoryTypeIcon(memory.memory.type)

    ProdyCard(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .semantics {
                contentDescription = "Memory from ${memory.memory.date}: ${memory.memory.preview}"
            }
    ) {
        Column(
            modifier = Modifier.padding(ProdyTokens.Spacing.cardPadding)
        ) {
            // Header with icon and surface reason
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(ProdyTokens.Spacing.sm)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(memoryColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = memoryIcon,
                            contentDescription = null,
                            tint = memoryColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = memory.surfaceReason,
                        style = MaterialTheme.typography.labelMedium,
                        color = memoryColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Dismiss button
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = ProdyIcons.Rounded.Close,
                        contentDescription = "Dismiss memory",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(ProdyTokens.Spacing.md))

            // Memory preview content
            Text(
                text = "\"${memory.memory.preview}\"",
                style = MaterialTheme.typography.bodyLarge,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(ProdyTokens.Spacing.sm))

            // Date
            Text(
                text = memory.memory.date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Mood indicator if present
            memory.memory.mood?.let { mood ->
                Spacer(modifier = Modifier.height(ProdyTokens.Spacing.xs))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(ProdyTokens.Spacing.xs)
                ) {
                    Text(
                        text = getMoodEmoji(mood),
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = mood.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(ProdyTokens.Spacing.md))

            // Action button
            ProdyOutlinedButton(
                onClick = onExpand,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = memory.actionText)
                Spacer(modifier = Modifier.width(ProdyTokens.Spacing.xs))
                Icon(
                    imageVector = ProdyIcons.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// ================================================================================================
// ANNIVERSARY MEMORY CARD
// ================================================================================================

/**
 * Displays "On this day X years ago..." anniversary memories.
 * Has a special nostalgic visual treatment.
 */
@Composable
fun AnniversaryMemoryCard(
    anniversary: AnniversaryMemory,
    onView: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "anniversary_shimmer")
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer_alpha"
    )

    val timeAgoText = when {
        anniversary.yearsAgo != null && anniversary.yearsAgo > 0 -> {
            if (anniversary.yearsAgo == 1) "1 year ago" else "${anniversary.yearsAgo} years ago"
        }
        anniversary.monthsAgo != null && anniversary.monthsAgo > 0 -> {
            if (anniversary.monthsAgo == 1) "1 month ago" else "${anniversary.monthsAgo} months ago"
        }
        else -> "A while back"
    }

    ProdyCard(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "Anniversary memory from $timeAgoText"
            }
    ) {
        Box {
            // Subtle gradient overlay for nostalgic feel
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFE8B4B8).copy(alpha = shimmerAlpha),
                                Color(0xFFB4C8E8).copy(alpha = shimmerAlpha),
                                Color(0xFFE8D4B4).copy(alpha = shimmerAlpha)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier.padding(ProdyTokens.Spacing.cardPadding)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Anniversary label with sparkle
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(ProdyTokens.Spacing.sm)
                    ) {
                        Icon(
                            imageVector = ProdyIcons.Rounded.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFFD4A574),
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = "On this day",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = timeAgoText,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = ProdyIcons.Rounded.Close,
                            contentDescription = "Dismiss",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(ProdyTokens.Spacing.md))

                // Memory preview with decorative quotes
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "\u201C",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    )
                    Text(
                        text = anniversary.memory.preview,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = ProdyTokens.Spacing.xs),
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(ProdyTokens.Spacing.md))

                // Action row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(ProdyTokens.Spacing.sm)
                ) {
                    ProdyOutlinedButton(
                        onClick = onView,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Revisit this moment")
                    }
                }
            }
        }
    }
}

// ================================================================================================
// FIRST WEEK PROGRESS CARD
// ================================================================================================

/**
 * Displays the user's first week journey progress.
 * Shows current day, completed milestones, and next steps.
 */
@Composable
fun FirstWeekProgressCard(
    dayNumber: Int,
    progress: FirstWeekProgress?,
    dayContent: FirstWeekDayContent?,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progressPercent = progress?.let {
        (it.completedMilestones.size.toFloat() / 7f).coerceIn(0f, 1f)
    } ?: (dayNumber.toFloat() / 7f)

    ProdyGradientCard(
        gradientColors = listOf(
            Color(0xFF6366F1),
            Color(0xFF8B5CF6)
        ),
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "First week journey, day $dayNumber of 7"
            }
    ) {
        Column(
            modifier = Modifier.padding(ProdyTokens.Spacing.cardPadding)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Your First Week",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Day $dayNumber of 7",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                // Day indicator circles
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(7) { index ->
                        val dayIndex = index + 1
                        val isCompleted = dayIndex < dayNumber
                        val isCurrent = dayIndex == dayNumber

                        Box(
                            modifier = Modifier
                                .size(if (isCurrent) 12.dp else 8.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isCompleted -> Color.White
                                        isCurrent -> Color.White.copy(alpha = 0.8f)
                                        else -> Color.White.copy(alpha = 0.3f)
                                    }
                                )
                                .then(
                                    if (isCurrent) Modifier.border(
                                        width = 2.dp,
                                        color = Color.White,
                                        shape = CircleShape
                                    ) else Modifier
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(ProdyTokens.Spacing.md))

            // Progress bar
            LinearProgressIndicator(
                progress = { progressPercent },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f),
            )

            Spacer(modifier = Modifier.height(ProdyTokens.Spacing.md))

            // Day content
            dayContent?.let { content ->
                Text(
                    text = content.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(ProdyTokens.Spacing.xs))
                Text(
                    text = content.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(ProdyTokens.Spacing.md))
            }

            // Continue button
            Button(
                onClick = onContinue,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF6366F1)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = dayContent?.primaryAction?.title ?: "Continue your journey",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// ================================================================================================
// CELEBRATION DIALOG
// ================================================================================================

/**
 * Full-screen celebration dialog for milestones and achievements.
 * Shows animated content with rewards.
 */
@Composable
fun CelebrationDialog(
    celebration: CelebrationContent,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        showContent = true
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        AnimatedVisibility(
            visible = showContent,
            enter = scaleIn(
                initialScale = 0.8f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            ) + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Card(
                modifier = modifier
                    .fillMaxWidth(0.9f)
                    .padding(ProdyTokens.Spacing.lg),
                shape = RoundedCornerShape(ProdyTokens.Radius.xl),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(ProdyTokens.Spacing.xl),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Animated celebration icon
                    val infiniteTransition = rememberInfiniteTransition(label = "celebration")
                    val iconScale by infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 1.1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(600, easing = EaseInOutSine),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "icon_scale"
                    )

                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .graphicsLayer {
                                scaleX = iconScale
                                scaleY = iconScale
                            }
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFFFFD700),
                                        Color(0xFFFFA500)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getCelebrationIcon(celebration.animation),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(ProdyTokens.Spacing.lg))

                    // Title
                    Text(
                        text = celebration.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(ProdyTokens.Spacing.sm))

                    // Message
                    Text(
                        text = celebration.message,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(ProdyTokens.Spacing.lg))

                    // Rewards row
                    if (celebration.xpReward > 0 || celebration.tokensReward > 0) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(ProdyTokens.Radius.md))
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                                .padding(ProdyTokens.Spacing.md),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            if (celebration.xpReward > 0) {
                                RewardItem(
                                    icon = ProdyIcons.Rounded.Star,
                                    value = "+${celebration.xpReward}",
                                    label = "XP",
                                    color = Color(0xFF6366F1)
                                )
                            }
                            if (celebration.tokensReward > 0) {
                                RewardItem(
                                    icon = ProdyIcons.Rounded.Token,
                                    value = "+${celebration.tokensReward}",
                                    label = "Tokens",
                                    color = Color(0xFFD4A574)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(ProdyTokens.Spacing.lg))

                    // Next hint
                    if (celebration.nextHint.isNotEmpty()) {
                        Text(
                            text = celebration.nextHint,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            fontStyle = FontStyle.Italic
                        )
                        Spacer(modifier = Modifier.height(ProdyTokens.Spacing.md))
                    }

                    // Continue button
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = "Continue",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RewardItem(
    icon: ImageVector,
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = value,
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

// ================================================================================================
// INTELLIGENT GREETING HEADER
// ================================================================================================

/**
 * Context-aware greeting header that displays personalized greetings
 * based on time of day, user state, and Soul Layer intelligence.
 */
@Composable
fun IntelligentGreetingHeader(
    greeting: String,
    subtext: String,
    userName: String?,
    isStruggling: Boolean = false,
    isThriving: Boolean = false,
    modifier: Modifier = Modifier
) {
    val greetingColor = when {
        isStruggling -> MaterialTheme.colorScheme.tertiary
        isThriving -> Color(0xFF10B981)
        else -> MaterialTheme.colorScheme.onSurface
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ProdyTokens.Spacing.lg)
    ) {
        // Main greeting
        Text(
            text = if (userName.isNullOrBlank()) greeting else "$greeting, $userName",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = greetingColor
        )

        if (subtext.isNotEmpty()) {
            Spacer(modifier = Modifier.height(ProdyTokens.Spacing.xs))
            Text(
                text = subtext,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Supportive message for struggling users
        if (isStruggling) {
            Spacer(modifier = Modifier.height(ProdyTokens.Spacing.sm))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(ProdyTokens.Spacing.xs)
            ) {
                Icon(
                    imageVector = ProdyIcons.Rounded.Favorite,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "We're here for you",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

// ================================================================================================
// GROWTH CONTRAST MEMORY CARD
// ================================================================================================

/**
 * Shows a comparison between past and present, highlighting personal growth.
 */
@Composable
fun GrowthContrastCard(
    contrast: GrowthContrastMemory,
    onExplore: () -> Unit,
    modifier: Modifier = Modifier
) {
    ProdyCard(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "Growth reflection: ${contrast.growthMessage}"
            }
    ) {
        Column(
            modifier = Modifier.padding(ProdyTokens.Spacing.cardPadding)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(ProdyTokens.Spacing.sm)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = ProdyIcons.Rounded.TrendingUp,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(18.dp)
                    )
                }
                Column {
                    Text(
                        text = "Look how you've grown",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF10B981)
                    )
                    contrast.sharedTheme?.let { theme ->
                        Text(
                            text = "About: $theme",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(ProdyTokens.Spacing.md))

            // Then vs Now comparison
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(ProdyTokens.Spacing.md)
            ) {
                // Then
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(ProdyTokens.Radius.md))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(ProdyTokens.Spacing.md)
                ) {
                    Text(
                        text = "Then",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(ProdyTokens.Spacing.xs))
                    Text(
                        text = "\"${contrast.oldMemory.preview}\"",
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Now
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(ProdyTokens.Radius.md))
                        .background(Color(0xFF10B981).copy(alpha = 0.1f))
                        .padding(ProdyTokens.Spacing.md)
                ) {
                    Text(
                        text = "Now",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF10B981),
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(ProdyTokens.Spacing.xs))
                    Text(
                        text = contrast.currentState,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(ProdyTokens.Spacing.md))

            // Growth message
            Text(
                text = contrast.growthMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )

            // Days between indicator
            Text(
                text = "${contrast.daysBetween} days of growth",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(ProdyTokens.Spacing.md))

            ProdyOutlinedButton(
                onClick = onExplore,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Explore your journey")
            }
        }
    }
}

// ================================================================================================
// MILESTONE CELEBRATION CARD
// ================================================================================================

/**
 * Compact celebration card for milestones shown in-line.
 */
@Composable
fun MilestoneCard(
    milestone: MilestoneMemory,
    onCelebrate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val milestoneColor = when (milestone.celebrationType) {
        CelebrationType.CONFETTI -> Color(0xFFFFD700)
        CelebrationType.SPARKLE, CelebrationType.SPARKLES -> Color(0xFF8B5CF6)
        CelebrationType.GENTLE_GLOW -> Color(0xFF6366F1)
        CelebrationType.WARM_FADE -> Color(0xFFE8B4B8)
        CelebrationType.FIREWORKS -> Color(0xFFFF6B6B)
        CelebrationType.FIRST_ENTRY -> Color(0xFF10B981)
        CelebrationType.STREAK_MILESTONE -> Color(0xFFF59E0B)
        CelebrationType.WEEK_COMPLETE -> Color(0xFF3B82F6)
        CelebrationType.FEATURE_DISCOVERED -> Color(0xFF8B5CF6)
        CelebrationType.GROWTH_MOMENT -> Color(0xFF22C55E)
        CelebrationType.VULNERABILITY_SHARED -> Color(0xFFEC4899)
        CelebrationType.PATTERN_BROKEN -> Color(0xFF14B8A6)
        CelebrationType.ACHIEVEMENT -> Color(0xFFF97316)
    }

    ProdyClickableCard(
        onClick = onCelebrate,
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "Milestone: ${milestone.title}"
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(ProdyTokens.Spacing.cardPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(ProdyTokens.Spacing.md)
        ) {
            // Milestone icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(milestoneColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getMilestoneIcon(milestone.type),
                    contentDescription = null,
                    tint = milestoneColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = milestone.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = milestone.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Number badge
            if (milestone.number > 0) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(ProdyTokens.Radius.sm))
                        .background(milestoneColor)
                        .padding(horizontal = ProdyTokens.Spacing.sm, vertical = ProdyTokens.Spacing.xs),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "#${milestone.number}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// ================================================================================================
// HELPER FUNCTIONS
// ================================================================================================

@Composable
private fun getMemoryTypeColor(type: MemoryType): Color {
    return when (type) {
        MemoryType.JOURNAL_ENTRY -> MaterialTheme.colorScheme.primary
        MemoryType.FUTURE_MESSAGE_SENT -> Color(0xFF8B5CF6)
        MemoryType.FUTURE_MESSAGE_RECEIVED -> Color(0xFFD4A574)
        MemoryType.MILESTONE_ACHIEVED -> Color(0xFFFFD700)
        MemoryType.HAVEN_BREAKTHROUGH -> Color(0xFF10B981)
        MemoryType.FIRST_OF_KIND -> Color(0xFF6366F1)
        MemoryType.STREAK_ACHIEVEMENT -> Color(0xFFFF6B6B)
    }
}

private fun getMemoryTypeIcon(type: MemoryType): ImageVector {
    return when (type) {
        MemoryType.JOURNAL_ENTRY -> ProdyIcons.Rounded.Book
        MemoryType.FUTURE_MESSAGE_SENT -> ProdyIcons.Rounded.Send
        MemoryType.FUTURE_MESSAGE_RECEIVED -> ProdyIcons.Rounded.Mail
        MemoryType.MILESTONE_ACHIEVED -> ProdyIcons.Rounded.EmojiEvents
        MemoryType.HAVEN_BREAKTHROUGH -> ProdyIcons.Rounded.Psychology
        MemoryType.FIRST_OF_KIND -> ProdyIcons.Rounded.Stars
        MemoryType.STREAK_ACHIEVEMENT -> ProdyIcons.Rounded.LocalFireDepartment
    }
}

private fun getMoodEmoji(mood: Mood): String {
    return mood.emoji
}

private fun getCelebrationIcon(animation: CelebrationAnimation): ImageVector {
    return when (animation) {
        CelebrationAnimation.CONFETTI -> ProdyIcons.Rounded.Celebration
        CelebrationAnimation.SPARKLE -> ProdyIcons.Rounded.AutoAwesome
        CelebrationAnimation.GLOW -> ProdyIcons.Rounded.Favorite
        CelebrationAnimation.FIREWORKS -> ProdyIcons.Rounded.Stars
    }
}

private fun getMilestoneIcon(type: MemoryMilestoneType): ImageVector {
    return when (type) {
        MemoryMilestoneType.ENTRY_COUNT -> ProdyIcons.Rounded.EditNote
        MemoryMilestoneType.DAYS_WITH_PRODY -> ProdyIcons.Rounded.CalendarMonth
        MemoryMilestoneType.WISDOM_STREAK -> ProdyIcons.Rounded.AutoAwesome
        MemoryMilestoneType.REFLECTION_STREAK -> ProdyIcons.Rounded.LocalFireDepartment
    }
}

// ================================================================================================
// PRODY BUTTON VARIANTS (used above)
// ================================================================================================

/**
 * Outlined button following Prody's flat design with custom content.
 * Use [ProdyOutlinedButton] from ProdyButton.kt for more options.
 */
@Composable
fun SoulOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(ProdyTokens.Radius.md),
        border = ButtonDefaults.outlinedButtonBorder(enabled),
        content = content
    )
}

/**
 * Gradient card used for special content like First Week progress.
 * Use [ProdyGradientCard] from ProdyCard.kt for more options.
 */
@Composable
fun SoulGradientCard(
    gradientColors: List<Color>,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(ProdyTokens.Radius.lg))
            .background(Brush.linearGradient(gradientColors)),
        content = content
    )
}
