package com.prody.prashant.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.ui.icons.ProdyIcons
import com.prody.prashant.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * THE RECEIPT - Side-by-side journal entry comparison card.
 *
 * Design Philosophy:
 * - Split-screen layout showing THEN vs NOW
 * - Brutal honesty about patterns and contradictions
 * - Visual timeline showing the gap between entries
 * - Footer with provocative question
 * - Clean, premium design matching Prody's aesthetic
 *
 * Usage:
 * Used after journal entry submission to show the user a comparison
 * with their past self, highlighting patterns or contradictions.
 */

/**
 * Data class representing the receipt comparison context.
 */
data class ReceiptData(
    val thenDate: String,
    val thenContent: String,
    val thenMood: String,
    val nowDate: String,
    val nowContent: String,
    val nowMood: String,
    val daysApart: Int,
    val mirrorResponse: String,
    val isFirstTimeWritingAboutTopic: Boolean = false
)

@Composable
fun ReceiptCard(
    receiptData: ReceiptData,
    onDismiss: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isDark = isDarkTheme()
    val surfaceColor = MaterialTheme.colorScheme.surface
    val primaryTextColor = MaterialTheme.colorScheme.onSurface
    val secondaryTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    val accentColor = MaterialTheme.colorScheme.primary

    // Entry animation
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(400)) + slideInVertically(
            initialOffsetY = { it / 4 },
            animationSpec = tween(400, easing = FastOutSlowInEasing)
        )
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            color = surfaceColor,
            tonalElevation = 0.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Header - "THE RECEIPT"
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
                            imageVector = ProdyIcons.Visibility,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "THE RECEIPT",
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = accentColor,
                            letterSpacing = 2.sp
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = ProdyIcons.Close,
                            contentDescription = "Close",
                            tint = secondaryTextColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (!receiptData.isFirstTimeWritingAboutTopic) {
                    // Side-by-side comparison
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // THEN (Past Entry)
                        EntryColumn(
                            label = "THEN",
                            date = receiptData.thenDate,
                            content = receiptData.thenContent,
                            mood = receiptData.thenMood,
                            isPast = true,
                            primaryTextColor = primaryTextColor,
                            secondaryTextColor = secondaryTextColor,
                            modifier = Modifier.weight(1f)
                        )

                        // Vertical Divider with Timeline
                        TimelineDivider(
                            daysApart = receiptData.daysApart,
                            accentColor = accentColor,
                            secondaryTextColor = secondaryTextColor
                        )

                        // NOW (Current Entry)
                        EntryColumn(
                            label = "NOW",
                            date = receiptData.nowDate,
                            content = receiptData.nowContent,
                            mood = receiptData.nowMood,
                            isPast = false,
                            primaryTextColor = primaryTextColor,
                            secondaryTextColor = secondaryTextColor,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Time gap indicator
                    TimeGapIndicator(
                        daysApart = receiptData.daysApart,
                        accentColor = accentColor,
                        secondaryTextColor = secondaryTextColor
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mirror Response - The brutal reflection
                MirrorResponseSection(
                    response = receiptData.mirrorResponse,
                    primaryTextColor = primaryTextColor,
                    secondaryTextColor = secondaryTextColor,
                    accentColor = accentColor
                )
            }
        }
    }
}

/**
 * Single entry column showing date, content preview, and mood.
 */
@Composable
private fun EntryColumn(
    label: String,
    date: String,
    content: String,
    mood: String,
    isPast: Boolean,
    primaryTextColor: Color,
    secondaryTextColor: Color,
    modifier: Modifier = Modifier
) {
    val labelColor = if (isPast) {
        secondaryTextColor.copy(alpha = 0.7f)
    } else {
        MaterialTheme.colorScheme.primary
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        // Label (THEN/NOW)
        Text(
            text = label,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            color = labelColor,
            letterSpacing = 1.5.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Date
        Text(
            text = date,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            color = if (isPast) secondaryTextColor else primaryTextColor
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Content preview
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (isPast)
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    else
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
                .padding(12.dp)
        ) {
            Text(
                text = "\"$content\"",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = if (isPast) secondaryTextColor else primaryTextColor,
                fontStyle = FontStyle.Italic,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Mood indicator
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(getMoodColor(mood))
            )
            Text(
                text = mood,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp,
                color = secondaryTextColor
            )
        }
    }
}

/**
 * Vertical timeline divider between THEN and NOW.
 */
@Composable
private fun TimelineDivider(
    daysApart: Int,
    accentColor: Color,
    secondaryTextColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        // Top dot
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(secondaryTextColor.copy(alpha = 0.4f))
        )

        // Dashed line
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            repeat(8) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(6.dp)
                        .background(secondaryTextColor.copy(alpha = 0.3f))
                )
            }
        }

        // Arrow indicator
        Icon(
            imageVector = ProdyIcons.ArrowForward,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier
                .size(16.dp)
                .padding(2.dp)
        )

        // Bottom dot
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(accentColor)
        )
    }
}

/**
 * Time gap indicator showing how long between entries.
 */
@Composable
private fun TimeGapIndicator(
    daysApart: Int,
    accentColor: Color,
    secondaryTextColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(accentColor.copy(alpha = 0.1f))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        val timeText = when {
            daysApart == 0 -> "Same day"
            daysApart == 1 -> "1 day"
            daysApart < 7 -> "$daysApart days"
            daysApart < 14 -> "1 week"
            daysApart < 30 -> "${daysApart / 7} weeks"
            daysApart < 60 -> "1 month"
            else -> "${daysApart / 30} months"
        }

        Text(
            text = "$timeText. Same problem. What changed?",
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            color = accentColor,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * The Mirror Response section - shows the AI's brutal reflection.
 */
@Composable
private fun MirrorResponseSection(
    response: String,
    primaryTextColor: Color,
    secondaryTextColor: Color,
    accentColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(16.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = ProdyIcons.Psychology,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = "THE MIRROR SPEAKS",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp,
                color = accentColor,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Mirror response text
        Text(
            text = response,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 13.sp,
            color = primaryTextColor,
            lineHeight = 20.sp
        )
    }
}

/**
 * Get color for mood indicator.
 */
@Composable
private fun getMoodColor(mood: String): Color {
    return when (mood.lowercase()) {
        "happy", "joyful", "excited", "grateful" -> Color(0xFF4CAF50)
        "sad", "melancholy", "depressed" -> Color(0xFF2196F3)
        "angry", "frustrated", "annoyed" -> Color(0xFFF44336)
        "anxious", "worried", "nervous" -> Color(0xFFFF9800)
        "calm", "peaceful", "relaxed" -> Color(0xFF00BCD4)
        "confused", "lost" -> Color(0xFF9C27B0)
        "neutral", "okay" -> Color(0xFF9E9E9E)
        else -> MaterialTheme.colorScheme.primary
    }
}

/**
 * Compact version of ReceiptCard for inline display.
 */
@Composable
fun CompactReceiptCard(
    thenDate: String,
    thenContentPreview: String,
    nowDate: String,
    daysApart: Int,
    onTapToExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = MaterialTheme.colorScheme.primary
    val secondaryTextColor = MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        onClick = onTapToExpand
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = ProdyIcons.Visibility,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(20.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Similar entry found from $thenDate",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "\"${thenContentPreview.take(50)}...\"",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp,
                    color = secondaryTextColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = "${daysApart}d ago",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                color = accentColor
            )

            Icon(
                imageVector = ProdyIcons.ChevronRight,
                contentDescription = "View receipt",
                tint = secondaryTextColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
