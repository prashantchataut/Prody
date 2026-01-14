package com.prody.prashant.ui.components.quietmode
import com.prody.prashant.ui.icons.ProdyIcons

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.ui.theme.*

/**
 * QuietModeIndicator - Shows that Quiet Mode is currently active
 *
 * This appears at the top of the home screen when Quiet Mode is on.
 * It provides:
 * - Visual confirmation that Quiet Mode is active
 * - Quick exit option
 * - Reassurance that everything is still there
 */
@Composable
fun QuietModeIndicator(
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showExitDialog by remember { mutableStateOf(false) }

    if (showExitDialog) {
        QuietModeExitDialog(
            onConfirm = {
                onExit()
                showExitDialog = false
            },
            onDismiss = {
                showExitDialog = false
            }
        )
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        color = if (isDarkTheme()) {
            QuietModeTheme.QuietSurfaceVariantDark
        } else {
            QuietModeTheme.QuietSurfaceVariantLight
        },
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon with subtle pulse
            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.6f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "alpha"
            )

            Icon(
                imageVector = ProdyIcons.SelfImprovement,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = QuietModeTheme.getAccent().copy(alpha = alpha)
            )

            // Text content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "Quiet Mode Active",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = QuietModeTheme.getTextPrimary()
                )

                Text(
                    text = "Simplified view • All your data is safe",
                    style = MaterialTheme.typography.bodySmall,
                    color = QuietModeTheme.getTextSecondary(),
                    fontSize = 12.sp
                )
            }

            // Exit button
            Surface(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { showExitDialog = true },
                color = if (isDarkTheme()) {
                    QuietModeTheme.QuietSurfaceDark
                } else {
                    QuietModeTheme.QuietSurfaceLight
                },
                tonalElevation = 0.dp
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = ProdyIcons.Close,
                        contentDescription = "Exit Quiet Mode",
                        modifier = Modifier.size(18.dp),
                        tint = QuietModeTheme.getTextSecondary()
                    )
                }
            }
        }
    }
}

/**
 * Compact indicator for use in the top bar or other constrained spaces.
 */
@Composable
fun CompactQuietModeIndicator(
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onExit() },
        color = if (isDarkTheme()) {
            QuietModeTheme.QuietSurfaceVariantDark
        } else {
            QuietModeTheme.QuietSurfaceVariantLight
        },
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = ProdyIcons.SelfImprovement,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = QuietModeTheme.getAccent()
            )

            Text(
                text = "Quiet Mode",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = QuietModeTheme.getTextPrimary(),
                fontSize = 12.sp
            )
        }
    }
}

/**
 * Badge indicator - minimal design for top right corner
 */
@Composable
fun QuietModeBadge(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = QuietModeTheme.getAccent(),
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = ProdyIcons.SelfImprovement,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = Color.White
            )

            Text(
                text = "Quiet",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                fontSize = 11.sp
            )
        }
    }
}

/**
 * Exit confirmation dialog.
 */
@Composable
private fun QuietModeExitDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = ProdyIcons.SelfImprovement,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = QuietModeTheme.getAccent()
            )
        },
        title = {
            Text(
                text = "Exit Quiet Mode?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "This will bring back all features:",
                    style = MaterialTheme.typography.bodyLarge
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = if (isDarkTheme()) {
                        ProdySurfaceVariantDark
                    } else {
                        ProdySurfaceVariantLight
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "• Streaks & XP\n• Leaderboard & rankings\n• Achievements & badges\n• Celebrations",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = if (isDarkTheme()) {
                                ProdyTextSecondaryDark
                            } else {
                                ProdyTextSecondaryLight
                            }
                        )
                    }
                }

                Text(
                    text = "You can always turn it back on when you need simplicity.",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDarkTheme()) {
                        ProdyTextTertiaryDark
                    } else {
                        ProdyTextTertiaryLight
                    },
                    fontSize = 12.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ProdyAccentGreen,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Bring everything back",
                    fontWeight = FontWeight.Medium
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = if (isDarkTheme()) {
                        ProdyTextSecondaryDark
                    } else {
                        ProdyTextSecondaryLight
                    }
                )
            ) {
                Text(
                    text = "Keep Quiet Mode",
                    fontWeight = FontWeight.Medium
                )
            }
        },
        containerColor = if (isDarkTheme()) ProdySurfaceDark else ProdySurfaceLight,
        shape = RoundedCornerShape(20.dp)
    )
}

/**
 * Animated entrance for the indicator (slides down from top).
 */
@Composable
fun AnimatedQuietModeIndicator(
    visible: Boolean,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
        ) + fadeIn(animationSpec = tween(400)),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(durationMillis = 300, easing = FastOutLinearInEasing)
        ) + fadeOut(animationSpec = tween(300))
    ) {
        QuietModeIndicator(
            onExit = onExit,
            modifier = modifier
        )
    }
}
