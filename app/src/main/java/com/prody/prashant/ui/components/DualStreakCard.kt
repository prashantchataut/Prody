package com.prody.prashant.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.domain.streak.DualStreakStatus
import com.prody.prashant.domain.streak.StreakInfo
import com.prody.prashant.domain.streak.StreakType
import com.prody.prashant.ui.theme.*

/**
 * Dual Streak Card - Shows both Wisdom and Reflection streaks.
 *
 * Design Philosophy:
 * - Two distinct streaks side by side
 * - Clear visual hierarchy
 * - Wisdom streak: Flame icon 🔥 (quick engagement)
 * - Reflection streak: Pen icon ✍️ (deep engagement)
 * - Grace period visibility (encouraging, not punishing)
 * - Tap for detailed breakdown
 * - Clean, premium design matching Prody's aesthetic
 */
@Composable
fun DualStreakCard(
    dualStreakStatus: DualStreakStatus,
    onTapForDetails: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isDark = isDarkTheme()
    val surfaceColor = MaterialTheme.colorScheme.surface
    val primaryTextColor = MaterialTheme.colorScheme.onSurface
    val secondaryTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    val accentColor = MaterialTheme.colorScheme.primary

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onTapForDetails),
        shape = RoundedCornerShape(20.dp),
        color = surfaceColor,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "YOUR STREAKS",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    color = accentColor,
                    letterSpacing = 1.5.sp
                )

                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "Tap for details",
                    tint = secondaryTextColor,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Two streaks side by side
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Wisdom Streak (Left)
                StreakColumn(
                    streakInfo = dualStreakStatus.wisdomStreak,
                    icon = Icons.Outlined.LocalFireDepartment,
                    iconColor = StreakFire,
                    title = "Wisdom",
                    subtitle = "Daily learning",
                    primaryTextColor = primaryTextColor,
                    secondaryTextColor = secondaryTextColor,
                    accentColor = accentColor,
                    modifier = Modifier.weight(1f)
                )

                // Divider
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(100.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )

                // Reflection Streak (Right)
                StreakColumn(
                    streakInfo = dualStreakStatus.reflectionStreak,
                    icon = Icons.Outlined.Edit,
                    iconColor = ProdyAccent,
                    title = "Reflection",
                    subtitle = "Deep work",
                    primaryTextColor = primaryTextColor,
                    secondaryTextColor = secondaryTextColor,
                    accentColor = accentColor,
                    modifier = Modifier.weight(1f)
                )
            }

            // Grace period indicators (if relevant)
            val showGraceIndicator = !dualStreakStatus.wisdomStreak.gracePeriodAvailable ||
                    !dualStreakStatus.reflectionStreak.gracePeriodAvailable

            if (showGraceIndicator) {
                Spacer(modifier = Modifier.height(16.dp))
                GracePeriodIndicator(
                    dualStreakStatus = dualStreakStatus,
                    secondaryTextColor = secondaryTextColor,
                    accentColor = accentColor
                )
            }

            // Tap for details hint
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tap for detailed breakdown",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp,
                    color = secondaryTextColor.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Single streak column showing icon, count, and status.
 */
@Composable
private fun StreakColumn(
    streakInfo: StreakInfo,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    primaryTextColor: Color,
    secondaryTextColor: Color,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    // Animation for the flame when maintained today
    val infiniteTransition = rememberInfiniteTransition(label = "flame")
    val flameScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flameScale"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icon container
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Streak count
        Text(
            text = streakInfo.current.toString(),
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            color = if (streakInfo.maintainedToday) accentColor else primaryTextColor
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Title
        Text(
            text = title,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = primaryTextColor
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Subtitle
        Text(
            text = subtitle,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 11.sp,
            color = secondaryTextColor
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Status indicator
        StreakStatusIndicator(
            streakInfo = streakInfo,
            secondaryTextColor = secondaryTextColor,
            accentColor = accentColor,
            iconColor = iconColor
        )
    }
}

/**
 * Status indicator for a streak (maintained today, at risk, etc.).
 */
@Composable
private fun StreakStatusIndicator(
    streakInfo: StreakInfo,
    secondaryTextColor: Color,
    accentColor: Color,
    iconColor: Color
) {
    when {
        streakInfo.maintainedToday -> {
            // Maintained today - show checkmark
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "Done today",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp,
                    color = accentColor
                )
            }
        }
        streakInfo.isAtRisk && streakInfo.gracePeriodAvailable -> {
            // At risk but grace available
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Shield,
                    contentDescription = null,
                    tint = LeaderboardGold,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "Grace ready",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp,
                    color = LeaderboardGold
                )
            }
        }
        streakInfo.isAtRisk -> {
            // At risk, no grace
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Warning,
                    contentDescription = null,
                    tint = ProdyError,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "At risk",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp,
                    color = ProdyError
                )
            }
        }
        streakInfo.current == 0 -> {
            // No streak yet
            Text(
                text = "Start today!",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp,
                color = secondaryTextColor
            )
        }
        else -> {
            // Active, not maintained today
            Text(
                text = "Keep going!",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp,
                color = secondaryTextColor
            )
        }
    }
}

/**
 * Grace period indicator showing cooldown for grace days.
 */
@Composable
private fun GracePeriodIndicator(
    dualStreakStatus: DualStreakStatus,
    secondaryTextColor: Color,
    accentColor: Color
) {
    val wisdomDaysUntilReset = dualStreakStatus.wisdomStreak.daysUntilGracePeriodReset
    val reflectionDaysUntilReset = dualStreakStatus.reflectionStreak.daysUntilGracePeriodReset

    if (wisdomDaysUntilReset > 0 || reflectionDaysUntilReset > 0) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(12.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Shield,
                        contentDescription = null,
                        tint = LeaderboardGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "GRACE PERIOD COOLDOWN",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp,
                        color = secondaryTextColor,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    if (wisdomDaysUntilReset > 0) {
                        GraceCooldownItem(
                            label = "Wisdom",
                            daysRemaining = wisdomDaysUntilReset,
                            secondaryTextColor = secondaryTextColor
                        )
                    }
                    if (reflectionDaysUntilReset > 0) {
                        GraceCooldownItem(
                            label = "Reflection",
                            daysRemaining = reflectionDaysUntilReset,
                            secondaryTextColor = secondaryTextColor
                        )
                    }
                }
            }
        }
    }
}

/**
 * Single grace cooldown item.
 */
@Composable
private fun GraceCooldownItem(
    label: String,
    daysRemaining: Int,
    secondaryTextColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            color = secondaryTextColor
        )
        Text(
            text = "$daysRemaining day${if (daysRemaining > 1) "s" else ""}",
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = LeaderboardGold
        )
    }
}

/**
 * Detailed dual streak dialog (for tap to expand).
 */
@Composable
fun DualStreakDetailDialog(
    dualStreakStatus: DualStreakStatus,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Your Streak Journey",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Wisdom Streak Details
                StreakDetailSection(
                    streakInfo = dualStreakStatus.wisdomStreak,
                    icon = Icons.Outlined.LocalFireDepartment,
                    iconColor = StreakFire,
                    title = "Wisdom Streak"
                )

                HorizontalDivider()

                // Reflection Streak Details
                StreakDetailSection(
                    streakInfo = dualStreakStatus.reflectionStreak,
                    icon = Icons.Outlined.Edit,
                    iconColor = ProdyAccent,
                    title = "Reflection Streak"
                )

                // Grace period explanation
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Shield,
                                contentDescription = null,
                                tint = LeaderboardGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Grace Period",
                                fontFamily = PoppinsFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Each streak gets one skip per 14 days. Life happens, and we understand that. Your grace day will protect your streak when you need it most.",
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Got it!",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

/**
 * Detailed section for a single streak in the dialog.
 */
@Composable
private fun StreakDetailSection(
    streakInfo: StreakInfo,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    title: String
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = title,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        DetailRow(label = "Current Streak", value = "${streakInfo.current} days")
        DetailRow(label = "Longest Streak", value = "${streakInfo.longest} days")
        DetailRow(
            label = "Grace Available",
            value = if (streakInfo.gracePeriodAvailable) "Yes ✓" else "In ${streakInfo.daysUntilGracePeriodReset} days"
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = streakInfo.getEncouragementMessage(),
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
        )

        streakInfo.getMilestone()?.let { milestone ->
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = iconColor.copy(alpha = 0.15f)
            ) {
                Text(
                    text = "🏆 $milestone",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = iconColor,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

/**
 * Detail row for dialog.
 */
@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp
        )
    }
}
