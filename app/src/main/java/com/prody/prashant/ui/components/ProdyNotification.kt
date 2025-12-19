package com.prody.prashant.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.EaseOutCubic
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
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
// blur import removed - flat design with no blur effects
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
// shadow import removed - flat design with no shadows
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.ui.theme.GoldTier
import com.prody.prashant.ui.theme.NotificationAchievement
import com.prody.prashant.ui.theme.NotificationCelebration
import com.prody.prashant.ui.theme.NotificationMotivation
import com.prody.prashant.ui.theme.NotificationPrimary
import com.prody.prashant.ui.theme.NotificationReminder
import com.prody.prashant.ui.theme.NotificationStreak
import com.prody.prashant.ui.theme.NotificationSuccess
import com.prody.prashant.ui.theme.ProdyGradients
import com.prody.prashant.ui.theme.ProdyTokens
import com.prody.prashant.ui.theme.StreakFire
import kotlinx.coroutines.delay

/**
 * Prody Design System - Lively Notification Components
 *
 * Fun, engaging notification components inspired by Duolingo's playful style
 * but adapted to fit Prody's calming, mature aesthetic.
 *
 * Design Philosophy:
 * - Celebratory without being overwhelming
 * - Playful animations that feel earned
 * - Encouraging language, not pushy
 * - Premium feel with subtle effects
 */

// =============================================================================
// NOTIFICATION TYPES
// =============================================================================

/**
 * Types of notifications with their associated visual styling.
 */
enum class ProdyNotificationType(
    val icon: ImageVector,
    val primaryColor: Color,
    val gradientColors: List<Color>
) {
    STREAK_REMINDER(
        icon = Icons.Filled.LocalFireDepartment,
        primaryColor = NotificationStreak,
        gradientColors = ProdyGradients.streakNotificationGradient
    ),
    STREAK_MILESTONE(
        icon = Icons.Filled.LocalFireDepartment,
        primaryColor = GoldTier,
        gradientColors = ProdyGradients.celebrationGradient
    ),
    ACHIEVEMENT_UNLOCKED(
        icon = Icons.Filled.EmojiEvents,
        primaryColor = NotificationAchievement,
        gradientColors = ProdyGradients.achievementGradient
    ),
    DAILY_MOTIVATION(
        icon = Icons.Filled.TipsAndUpdates,
        primaryColor = NotificationMotivation,
        gradientColors = ProdyGradients.motivationGradient
    ),
    GENTLE_REMINDER(
        icon = Icons.Filled.Notifications,
        primaryColor = NotificationReminder,
        gradientColors = ProdyGradients.oceanGradient
    ),
    CELEBRATION(
        icon = Icons.Filled.Celebration,
        primaryColor = NotificationCelebration,
        gradientColors = ProdyGradients.celebrationGradient
    ),
    SUCCESS(
        icon = Icons.Filled.Verified,
        primaryColor = NotificationSuccess,
        gradientColors = ProdyGradients.growthGradient
    ),
    ENCOURAGEMENT(
        icon = Icons.Filled.Favorite,
        primaryColor = NotificationPrimary,
        gradientColors = ProdyGradients.serenityGradient
    )
}

// =============================================================================
// MAIN NOTIFICATION BANNER
// =============================================================================

/**
 * Lively notification banner with animated entrance.
 *
 * @param type Type of notification determining visual style
 * @param title Main notification headline
 * @param message Supporting message text
 * @param isVisible Whether the notification is visible
 * @param onDismiss Callback when notification is dismissed
 * @param modifier Modifier for the component
 * @param actionLabel Optional action button label
 * @param onActionClick Optional action button callback
 */
@Composable
fun ProdyNotificationBanner(
    type: ProdyNotificationType,
    title: String,
    message: String,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "notification_animation")

    val iconScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "icon_scale"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn(animationSpec = tween(300)),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(200, easing = EaseInOutCubic)
        ) + fadeOut(animationSpec = tween(200))
    ) {
        // Flat design - no shadow, just clean surface with border accent
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(ProdyTokens.Spacing.lg)
                .clip(RoundedCornerShape(ProdyTokens.Radius.xl))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, type.primaryColor.copy(alpha = 0.2f), RoundedCornerShape(ProdyTokens.Radius.xl))
                .semantics { contentDescription = "$title. $message" }
        ) {
            // Subtle gradient accent at top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(Brush.horizontalGradient(type.gradientColors))
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(ProdyTokens.Spacing.lg),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(ProdyTokens.Spacing.md)
            ) {
                // Animated icon with subtle pulse
                Box(contentAlignment = Alignment.Center) {
                    // Flat design - subtle alpha pulse instead of blur glow
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .scale(iconScale)
                            .alpha(glowAlpha * 0.2f)
                            .background(type.primaryColor, CircleShape)
                    )

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                brush = Brush.linearGradient(type.gradientColors),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = type.icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .size(28.dp)
                                .scale(iconScale)
                        )
                    }
                }

                // Content
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (actionLabel != null && onActionClick != null) {
                        Spacer(modifier = Modifier.height(ProdyTokens.Spacing.sm))

                        Button(
                            onClick = onActionClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = type.primaryColor,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text(
                                text = actionLabel,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Dismiss button
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Dismiss notification",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// =============================================================================
// STREAK NOTIFICATION
// =============================================================================

/**
 * Special streak notification with fire animation.
 *
 * @param streakDays Current streak count
 * @param title Notification title
 * @param message Encouraging message
 * @param isVisible Whether visible
 * @param onDismiss Dismiss callback
 * @param onContinueStreak Action to continue streak
 * @param modifier Modifier for the component
 */
@Composable
fun StreakNotification(
    streakDays: Int,
    title: String,
    message: String,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onContinueStreak: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "streak_notification")

    val fireScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fire_scale"
    )

    val fireOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -4f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fire_offset"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    AnimatedVisibility(
        visible = isVisible,
        enter = scaleIn(
            initialScale = 0.8f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn(),
        exit = scaleOut(targetScale = 0.8f) + fadeOut()
    ) {
        Box(
            // Flat design - no shadow, just clean surface with border accent
            modifier = modifier
                .fillMaxWidth()
                .padding(ProdyTokens.Spacing.lg)
                .clip(RoundedCornerShape(ProdyTokens.Radius.xxl))
                .border(1.dp, StreakFire.copy(alpha = 0.3f), RoundedCornerShape(ProdyTokens.Radius.xxl))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                        )
                    )
                )
                .semantics { contentDescription = "$title. $message. $streakDays day streak" }
        ) {
            // Fire gradient accent
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(Brush.horizontalGradient(ProdyGradients.streakGradient))
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(ProdyTokens.Spacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Animated fire icon with subtle pulse
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(bottom = ProdyTokens.Spacing.md)
                ) {
                    // Flat design - subtle layered alpha instead of blur glow
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .scale(fireScale * 1.1f)
                            .alpha(glowAlpha * 0.15f)
                            .background(StreakFire.copy(alpha = 0.5f), CircleShape)
                    )

                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .scale(fireScale)
                            .alpha(glowAlpha * 0.25f)
                            .background(StreakFire, CircleShape)
                    )

                    Icon(
                        imageVector = Icons.Filled.LocalFireDepartment,
                        contentDescription = null,
                        tint = StreakFire,
                        modifier = Modifier
                            .size(48.dp)
                            .scale(fireScale)
                            .offset(y = fireOffset.dp)
                    )
                }

                // Streak count with emphasis
                Text(
                    text = "$streakDays",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = StreakFire
                )

                Text(
                    text = if (streakDays == 1) "Day Streak" else "Day Streak",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(ProdyTokens.Spacing.md))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(ProdyTokens.Spacing.xs))

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(ProdyTokens.Spacing.xl))

                // Action button
                Button(
                    onClick = onContinueStreak,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StreakFire,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ProdyTokens.Touch.comfortable)
                ) {
                    Text(
                        text = "Continue Streak",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(ProdyTokens.Spacing.sm))

                TextButton(onClick = onDismiss) {
                    Text(
                        text = "Maybe Later",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// =============================================================================
// ACHIEVEMENT NOTIFICATION
// =============================================================================

/**
 * Achievement unlocked notification with celebratory animation.
 *
 * @param achievementTitle Name of the achievement
 * @param achievementDescription Description of what was accomplished
 * @param isVisible Whether visible
 * @param onDismiss Dismiss callback
 * @param onViewAchievement View achievement details callback
 * @param modifier Modifier for the component
 */
@Composable
fun AchievementNotification(
    achievementTitle: String,
    achievementDescription: String,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onViewAchievement: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "achievement_notification")

    val trophyRotation by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "trophy_rotation"
    )

    val sparkleScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparkle_scale"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    AnimatedVisibility(
        visible = isVisible,
        enter = scaleIn(
            initialScale = 0.5f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn(),
        exit = scaleOut(targetScale = 0.8f) + fadeOut()
    ) {
        Box(
            // Flat design - no shadow, just clean surface with border accent
            modifier = modifier
                .fillMaxWidth()
                .padding(ProdyTokens.Spacing.lg)
                .clip(RoundedCornerShape(ProdyTokens.Radius.xxl))
                .border(1.dp, GoldTier.copy(alpha = 0.3f), RoundedCornerShape(ProdyTokens.Radius.xxl))
                .background(MaterialTheme.colorScheme.surface)
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(ProdyGradients.goldGradient),
                    shape = RoundedCornerShape(ProdyTokens.Radius.xxl)
                )
                .semantics {
                    contentDescription = "Achievement unlocked: $achievementTitle. $achievementDescription"
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(ProdyTokens.Spacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // "Achievement Unlocked" header
                Text(
                    text = "Achievement Unlocked",
                    style = MaterialTheme.typography.labelLarge,
                    color = GoldTier,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(ProdyTokens.Spacing.lg))

                // Animated trophy with sparkles
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(bottom = ProdyTokens.Spacing.md)
                ) {
                    // Flat design - subtle alpha effect instead of blur glow
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .alpha(glowAlpha * 0.2f)
                            .background(GoldTier, CircleShape)
                    )

                    // Sparkle effects
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        tint = GoldTier,
                        modifier = Modifier
                            .size(24.dp)
                            .offset(x = (-35).dp, y = (-20).dp)
                            .scale(sparkleScale)
                            .alpha(glowAlpha)
                    )

                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = GoldTier,
                        modifier = Modifier
                            .size(20.dp)
                            .offset(x = 35.dp, y = (-25).dp)
                            .scale(sparkleScale * 0.9f)
                            .alpha(glowAlpha * 0.8f)
                    )

                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        tint = GoldTier,
                        modifier = Modifier
                            .size(18.dp)
                            .offset(x = 40.dp, y = 15.dp)
                            .scale(sparkleScale * 0.85f)
                            .alpha(glowAlpha * 0.7f)
                    )

                    // Trophy icon
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(
                                brush = Brush.linearGradient(ProdyGradients.goldGradient),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.EmojiEvents,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .size(40.dp)
                                .rotate(trophyRotation)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(ProdyTokens.Spacing.md))

                // Achievement title
                Text(
                    text = achievementTitle,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(ProdyTokens.Spacing.xs))

                // Description
                Text(
                    text = achievementDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(ProdyTokens.Spacing.xl))

                // Action button
                Button(
                    onClick = onViewAchievement,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldTier,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ProdyTokens.Touch.comfortable)
                ) {
                    Text(
                        text = "View Achievement",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(ProdyTokens.Spacing.sm))

                TextButton(onClick = onDismiss) {
                    Text(
                        text = "Dismiss",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// =============================================================================
// MOTIVATIONAL NOTIFICATION
// =============================================================================

/**
 * Gentle motivational notification with calming animation.
 *
 * @param quote Motivational quote or message
 * @param isVisible Whether visible
 * @param onDismiss Dismiss callback
 * @param modifier Modifier for the component
 */
@Composable
fun MotivationalNotification(
    quote: String,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "motivation_notification")

    val iconAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "icon_alpha"
    )

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(400, easing = EaseOutCubic)
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(300)
        ) + fadeOut()
    ) {
        Box(
            // Flat design - no shadow, just clean surface with border accent
            modifier = modifier
                .fillMaxWidth()
                .padding(ProdyTokens.Spacing.lg)
                .clip(RoundedCornerShape(ProdyTokens.Radius.xl))
                .border(1.dp, NotificationMotivation.copy(alpha = 0.2f), RoundedCornerShape(ProdyTokens.Radius.xl))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        )
                    )
                )
                .clickable { onDismiss() }
                .semantics { contentDescription = "Daily inspiration: $quote" }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(ProdyTokens.Spacing.lg),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(ProdyTokens.Spacing.md)
            ) {
                Icon(
                    imageVector = Icons.Filled.TipsAndUpdates,
                    contentDescription = null,
                    tint = NotificationMotivation,
                    modifier = Modifier
                        .size(32.dp)
                        .alpha(iconAlpha)
                )

                Text(
                    text = quote,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// =============================================================================
// TOAST NOTIFICATION
// =============================================================================

/**
 * Brief toast-style notification for quick feedback.
 *
 * @param message Toast message
 * @param type Type determining visual style
 * @param isVisible Whether visible
 * @param durationMs How long to show (auto-dismisses)
 * @param onDismiss Dismiss callback
 * @param modifier Modifier for the component
 */
@Composable
fun ProdyToast(
    message: String,
    type: ProdyNotificationType = ProdyNotificationType.SUCCESS,
    isVisible: Boolean,
    durationMs: Long = 3000,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Auto-dismiss after duration
    LaunchedEffect(isVisible) {
        if (isVisible) {
            delay(durationMs)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(200)
        ) + fadeOut()
    ) {
        // Flat design - no shadow, just clean surface
        Box(
            modifier = modifier
                .padding(ProdyTokens.Spacing.lg)
                .clip(RoundedCornerShape(ProdyTokens.Radius.full))
                .background(MaterialTheme.colorScheme.inverseSurface)
                .clickable { onDismiss() }
                .padding(horizontal = ProdyTokens.Spacing.lg, vertical = ProdyTokens.Spacing.md)
                .semantics { contentDescription = message }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(ProdyTokens.Spacing.sm)
            ) {
                Icon(
                    imageVector = type.icon,
                    contentDescription = null,
                    tint = type.primaryColor,
                    modifier = Modifier.size(20.dp)
                )

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.inverseOnSurface
                )
            }
        }
    }
}

// =============================================================================
// NOTIFICATION MESSAGES (Sample content)
// =============================================================================

/**
 * Sample notification messages for various scenarios.
 * Use these as templates for consistent, encouraging language.
 */
object ProdyNotificationMessages {

    // Streak reminders
    val streakReminders = listOf(
        "Your streak is waiting for you today",
        "A moment of growth keeps the streak going",
        "Your consistency inspires progress",
        "Today's small step continues your journey"
    )

    // Streak milestones
    fun streakMilestone(days: Int): Pair<String, String> = when {
        days == 7 -> "Weekly Warrior" to "Seven days of dedication. You are building something meaningful."
        days == 14 -> "Fortnight Focus" to "Two weeks of commitment. Your habits are taking root."
        days == 30 -> "Monthly Master" to "A full month of growth. This is who you are becoming."
        days == 60 -> "Double Month Champion" to "Sixty days of dedication. Extraordinary commitment."
        days == 90 -> "Quarter Year Legend" to "Ninety days. You have proven your dedication beyond doubt."
        days == 180 -> "Half Year Hero" to "Six months of unwavering commitment. Truly remarkable."
        days == 365 -> "Annual Achiever" to "One full year. You have transformed yourself through consistency."
        else -> "Streak Milestone" to "$days days of continuous growth. Well done."
    }

    // Achievement messages
    val achievementMessages = listOf(
        "Your dedication has been recognized",
        "This milestone marks your growth",
        "A reflection of your consistent effort",
        "You have earned this through persistence"
    )

    // Motivational quotes (literary, thoughtful)
    val motivationalQuotes = listOf(
        "The journey of a thousand miles begins with a single step.",
        "What lies behind us and what lies before us are tiny matters compared to what lies within us.",
        "The only way to do great work is to love what you do.",
        "In the middle of difficulty lies opportunity.",
        "Growth is never by mere chance; it is the result of forces working together.",
        "The best time to plant a tree was twenty years ago. The second best time is now.",
        "What we think, we become.",
        "Small daily improvements over time lead to stunning results."
    )

    // Encouragement messages
    val encouragementMessages = listOf(
        "Every step forward matters",
        "Progress, not perfection",
        "You are exactly where you need to be",
        "Trust the process of growth"
    )
}
