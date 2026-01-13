package com.prody.prashant.ui.components.quietmode
import com.prody.prashant.ui.icons.ProdyIcons

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SelfImprovement
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
import com.prody.prashant.ui.theme.*

/**
 * QuietModeToggle - A warm, inviting button to enable Quiet Mode
 *
 * This component appears on the home screen as a quick action tile.
 * It presents Quiet Mode as a caring feature, not a punishment.
 *
 * Design:
 * - Soft, calming colors
 * - Gentle, non-judgmental messaging
 * - Shows confirmation dialog before enabling
 * - Easy to dismiss
 */
@Composable
fun QuietModeToggle(
    isQuietModeActive: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showConfirmDialog by remember { mutableStateOf(false) }

    // Show confirmation dialog when user wants to enable Quiet Mode
    if (showConfirmDialog) {
        QuietModeEnableDialog(
            onConfirm = {
                onToggle()
                showConfirmDialog = false
            },
            onDismiss = {
                showConfirmDialog = false
            }
        )
    }

    // The toggle card
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                if (isQuietModeActive) {
                    // If already active, toggle off immediately
                    onToggle()
                } else {
                    // If not active, show confirmation
                    showConfirmDialog = true
                }
            },
        color = if (isQuietModeActive) {
            if (isDarkTheme()) QuietModeTheme.QuietSurfaceDark else QuietModeTheme.QuietSurfaceLight
        } else {
            if (isDarkTheme()) ProdySurfaceDark else ProdySurfaceLight
        },
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon
            Icon(
                imageVector = ProdyIcons.SelfImprovement,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = if (isQuietModeActive) {
                    QuietModeTheme.getAccent()
                } else {
                    if (isDarkTheme()) ProdyTextSecondaryDark else ProdyTextSecondaryLight
                }
            )

            // Text content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = if (isQuietModeActive) "Quiet Mode Active" else "Need Things Simple?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isQuietModeActive) {
                        QuietModeTheme.getTextPrimary()
                    } else {
                        if (isDarkTheme()) ProdyTextPrimaryDark else ProdyTextPrimaryLight
                    }
                )

                Text(
                    text = if (isQuietModeActive) {
                        "Tap to bring everything back"
                    } else {
                        "Simplify the app for a bit"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isQuietModeActive) {
                        QuietModeTheme.getTextSecondary()
                    } else {
                        if (isDarkTheme()) ProdyTextSecondaryDark else ProdyTextSecondaryLight
                    },
                    fontSize = 14.sp
                )
            }

            // Status indicator
            if (isQuietModeActive) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = QuietModeTheme.getAccent(),
                            shape = RoundedCornerShape(4.dp)
                        )
                )
            }
        }
    }
}

/**
 * Confirmation dialog shown before enabling Quiet Mode.
 * Explains what will happen in a warm, caring way.
 */
@Composable
private fun QuietModeEnableDialog(
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
                tint = if (isDarkTheme()) QuietModeTheme.QuietAccentDark else QuietModeTheme.QuietAccentLight
            )
        },
        title = {
            Text(
                text = "I need things simple right now",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "We'll simplify the app to give you space to breathe.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = if (isDarkTheme()) ProdyTextSecondaryDark else ProdyTextSecondaryLight
                )

                Spacer(modifier = Modifier.height(4.dp))

                // What will be hidden
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = if (isDarkTheme()) {
                        QuietModeTheme.QuietSurfaceVariantDark
                    } else {
                        QuietModeTheme.QuietSurfaceVariantLight
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Hidden:",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "• Streaks, XP, achievements\n• Leaderboard & comparisons\n• Celebrations & animations",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = if (isDarkTheme()) {
                                QuietModeTheme.QuietTextSecondaryDark
                            } else {
                                QuietModeTheme.QuietTextSecondaryLight
                            }
                        )
                    }
                }

                // What will be kept
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = if (isDarkTheme()) {
                        QuietModeTheme.QuietSurfaceVariantDark
                    } else {
                        QuietModeTheme.QuietSurfaceVariantLight
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Kept:",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "• Your journal & all entries\n• Daily wisdom\n• AI support (if enabled)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = if (isDarkTheme()) {
                                QuietModeTheme.QuietTextSecondaryDark
                            } else {
                                QuietModeTheme.QuietTextSecondaryLight
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDarkTheme()) {
                        QuietModeTheme.QuietAccentDark
                    } else {
                        QuietModeTheme.QuietAccentLight
                    },
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Yes, simplify",
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
                    text = "I'm okay",
                    fontWeight = FontWeight.Medium
                )
            }
        },
        containerColor = if (isDarkTheme()) ProdySurfaceDark else ProdySurfaceLight,
        shape = RoundedCornerShape(20.dp)
    )
}

/**
 * Compact version of the toggle for use in settings or other locations.
 */
@Composable
fun CompactQuietModeToggle(
    isQuietModeActive: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = ProdyIcons.SelfImprovement,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = if (isQuietModeActive) {
                    QuietModeTheme.getAccent()
                } else {
                    if (isDarkTheme()) ProdyTextSecondaryDark else ProdyTextSecondaryLight
                }
            )

            Column {
                Text(
                    text = "Quiet Mode",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                if (isQuietModeActive) {
                    Text(
                        text = "Active",
                        style = MaterialTheme.typography.bodySmall,
                        color = QuietModeTheme.getTextSecondary(),
                        fontSize = 12.sp
                    )
                }
            }
        }

        Switch(
            checked = isQuietModeActive,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = QuietModeTheme.getAccent(),
                uncheckedThumbColor = if (isDarkTheme()) {
                    ProdyTextSecondaryDark
                } else {
                    ProdyTextSecondaryLight
                },
                uncheckedTrackColor = if (isDarkTheme()) {
                    ProdySurfaceVariantDark
                } else {
                    ProdySurfaceVariantLight
                }
            )
        )
    }
}
