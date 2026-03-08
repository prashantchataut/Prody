package com.prody.prashant.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.domain.progress.NextAction
import com.prody.prashant.ui.icons.ProdyIcons
import com.prody.prashant.ui.theme.*

/**
 * AI Configuration Warning Banner
 * Shown when the API key is missing or there's a configuration error.
 */
@Composable
fun AiConfigWarningBanner(
    onConfigureClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        color = ProdyError.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ProdyError.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = ProdyError,
                modifier = Modifier.size(24.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "AI Not Configured",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = ProdyError
                )
                Text(
                    text = "Add your Gemini API key in Settings to enable Buddha AI features.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ProdyTextSecondaryLight
                )
            }
            TextButton(onClick = onConfigureClick) {
                Text("Fix", fontWeight = FontWeight.Bold, color = ProdyError)
            }
        }
    }
}

/**
 * Buddha Thought Card
 * Displays the daily AI-generated (or fallback) wisdom.
 */
@Composable
fun BuddhaThoughtCard(
    thought: String,
    explanation: String,
    isLoading: Boolean,
    isAiGenerated: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    ProdyCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = ProdyIcons.Psychology,
                        contentDescription = null,
                        tint = ProdyForestGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "BUDDHA'S THOUGHT",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = ProdyForestGreen,
                        letterSpacing = 1.sp
                    )
                }

                if (isAiGenerated) {
                    Surface(
                        color = ProdyInfo.copy(alpha = 0.1f),
                        shape = CircleShape
                    ) {
                        Text(
                            text = "AI",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = ProdyInfo
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = ProdyForestGreen,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "\"$thought\"",
                    style = WisdomLargeStyle,
                    textAlign = TextAlign.Center,
                    color = ProdyTextPrimaryLight
                )

                if (explanation.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = explanation,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = ProdyTextSecondaryLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ProdyOutlinedButton(
                text = "Reflect & Journal",
                onClick = onRefresh, // In a real app, this might navigate to journal
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = ProdyIcons.Edit
            )
        }
    }
}

/**
 * Next Action Card
 * Suggests the most relevant next step for the user.
 */
@Composable
fun NextActionCard(
    nextAction: NextAction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ProdyClickableCard(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(ProdyInfo.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Lightbulb,
                    contentDescription = null,
                    tint = ProdyInfo,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nextAction.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = nextAction.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = ProdyTextSecondaryLight
                )
            }

            Icon(
                imageVector = ProdyIcons.ArrowForward,
                contentDescription = null,
                tint = ProdyTextTertiaryLight
            )
        }
    }
}
