package com.prody.prashant.ui.components.quietmode
import com.prody.prashant.ui.icons.ProdyIcons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.prody.prashant.ui.theme.*

/**
 * QuietModeSuggestionDialog - Auto-suggest dialog for Quiet Mode
 *
 * This dialog appears when the app detects stress patterns in recent journal entries.
 * It's designed to be:
 * - Warm and non-judgmental
 * - Clearly explain what Quiet Mode does
 * - Easy to accept or dismiss
 * - Never forced or pushy
 *
 * Philosophy:
 * "It looks like things have been heavy lately. Want to simplify the app for a bit?"
 */
@Composable
fun QuietModeSuggestionDialog(
    onAccept: () -> Unit,
    onDismiss: () -> Unit,
    analysisReason: String? = null
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = if (isDarkTheme()) ProdySurfaceDark else ProdySurfaceLight,
            tonalElevation = 0.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            color = if (isDarkTheme()) {
                                QuietModeTheme.QuietSurfaceVariantDark
                            } else {
                                QuietModeTheme.QuietSurfaceVariantLight
                            },
                            shape = RoundedCornerShape(32.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = ProdyIcons.SelfImprovement,
                        contentDescription = null,
                        modifier = Modifier.size(36.dp),
                        tint = if (isDarkTheme()) {
                            QuietModeTheme.QuietAccentDark
                        } else {
                            QuietModeTheme.QuietAccentLight
                        }
                    )
                }

                // Title
                Text(
                    text = "It looks like things have been heavy lately",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    lineHeight = 28.sp
                )

                // Message
                Text(
                    text = "Would you like to simplify the app for a bit? We can hide all the achievements, streaks, and stats, so you can just focus on yourself.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = if (isDarkTheme()) {
                        ProdyTextSecondaryDark
                    } else {
                        ProdyTextSecondaryLight
                    },
                    lineHeight = 22.sp
                )

                // What changes section
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = if (isDarkTheme()) {
                        QuietModeTheme.QuietSurfaceVariantDark
                    } else {
                        QuietModeTheme.QuietSurfaceVariantLight
                    },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Hidden items
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Hidden temporarily:",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = if (isDarkTheme()) {
                                    QuietModeTheme.QuietTextPrimaryDark
                                } else {
                                    QuietModeTheme.QuietTextPrimaryLight
                                }
                            )
                            Text(
                                text = "• Streaks & XP\n• Leaderboard & rankings\n• Achievements & badges\n• Celebration animations",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 13.sp,
                                lineHeight = 19.sp,
                                color = if (isDarkTheme()) {
                                    QuietModeTheme.QuietTextSecondaryDark
                                } else {
                                    QuietModeTheme.QuietTextSecondaryLight
                                }
                            )
                        }

                        HorizontalDivider(
                            color = if (isDarkTheme()) {
                                QuietModeTheme.QuietDividerDark
                            } else {
                                QuietModeTheme.QuietDividerLight
                            }
                        )

                        // Kept items
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Still here for you:",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = if (isDarkTheme()) {
                                    QuietModeTheme.QuietTextPrimaryDark
                                } else {
                                    QuietModeTheme.QuietTextPrimaryLight
                                }
                            )
                            Text(
                                text = "• Your journal (all entries safe)\n• Daily wisdom\n• Future messages\n• AI support",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 13.sp,
                                lineHeight = 19.sp,
                                color = if (isDarkTheme()) {
                                    QuietModeTheme.QuietTextSecondaryDark
                                } else {
                                    QuietModeTheme.QuietTextSecondaryLight
                                }
                            )
                        }
                    }
                }

                // Note about reversibility
                Text(
                    text = "You can turn it back on anytime from settings or the home screen.",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = if (isDarkTheme()) {
                        ProdyTextTertiaryDark
                    } else {
                        ProdyTextTertiaryLight
                    },
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Action buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Accept button
                    Button(
                        onClick = onAccept,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDarkTheme()) {
                                QuietModeTheme.QuietAccentDark
                            } else {
                                QuietModeTheme.QuietAccentLight
                            },
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "Yes, simplify",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Dismiss button
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (isDarkTheme()) {
                                ProdyTextSecondaryDark
                            } else {
                                ProdyTextSecondaryLight
                            }
                        ),
                        border = ButtonDefaults.outlinedButtonBorder(
                            enabled = true
                        ).copy(
                            width = 1.dp
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "I'm okay, thanks",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

/**
 * Exit check-in dialog - shown after 7 days in Quiet Mode.
 * Gently asks if user wants to bring features back.
 */
@Composable
fun QuietModeExitCheckInDialog(
    daysInQuietMode: Int,
    onKeepQuietMode: () -> Unit,
    onExitQuietMode: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = if (isDarkTheme()) ProdySurfaceDark else ProdySurfaceLight,
            tonalElevation = 0.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            color = if (isDarkTheme()) {
                                QuietModeTheme.QuietSurfaceVariantDark
                            } else {
                                QuietModeTheme.QuietSurfaceVariantLight
                            },
                            shape = RoundedCornerShape(32.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = ProdyIcons.SelfImprovement,
                        contentDescription = null,
                        modifier = Modifier.size(36.dp),
                        tint = if (isDarkTheme()) {
                            QuietModeTheme.QuietAccentDark
                        } else {
                            QuietModeTheme.QuietAccentLight
                        }
                    )
                }

                // Title
                Text(
                    text = "How are you feeling?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )

                // Message
                Text(
                    text = "You've been in Quiet Mode for $daysInQuietMode ${if (daysInQuietMode == 1) "day" else "days"}. Want to bring everything back, or keep things simple a bit longer?",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = if (isDarkTheme()) {
                        ProdyTextSecondaryDark
                    } else {
                        ProdyTextSecondaryLight
                    },
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Action buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Bring back button
                    Button(
                        onClick = onExitQuietMode,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ProdyAccentGreen,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "Bring everything back",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Keep quiet mode button
                    OutlinedButton(
                        onClick = onKeepQuietMode,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (isDarkTheme()) {
                                QuietModeTheme.QuietTextSecondaryDark
                            } else {
                                QuietModeTheme.QuietTextSecondaryLight
                            }
                        ),
                        border = ButtonDefaults.outlinedButtonBorder(
                            enabled = true
                        ).copy(
                            width = 1.dp
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "Keep it simple",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
