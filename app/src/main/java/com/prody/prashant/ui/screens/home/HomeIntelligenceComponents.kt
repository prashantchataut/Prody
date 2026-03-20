package com.prody.prashant.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.domain.progress.NextAction
import com.prody.prashant.domain.progress.NextActionType
import com.prody.prashant.ui.components.AiProofModeDebugInfo
import com.prody.prashant.ui.components.ProdyCard
import com.prody.prashant.ui.components.ProdyClickableCard
import com.prody.prashant.ui.icons.ProdyIcons
import com.prody.prashant.ui.theme.*

/**
 * AI Configuration Warning Banner
 * Shows when the Gemini API key is missing.
 */
@Composable
fun AiConfigWarningBanner(
    status: AiConfigurationStatus,
    onConfigureClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (status == AiConfigurationStatus.CONFIGURED) return

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        color = ProdyWarning.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, ProdyWarning.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = ProdyIcons.Warning,
                contentDescription = null,
                tint = ProdyWarning,
                modifier = Modifier.size(24.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "AI Not Configured",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = ProdyWarning
                )
                Text(
                    text = "Buddha's wisdom is currently limited. Add an API key in settings.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            TextButton(
                onClick = onConfigureClick,
                colors = ButtonDefaults.textButtonColors(contentColor = ProdyWarning)
            ) {
                Text("Fix", fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * Buddha Thought Card
 * Displays AI-generated or fallback daily wisdom.
 */
@Composable
fun BuddhaThoughtCard(
    thought: String,
    explanation: String,
    isLoading: Boolean,
    isAiGenerated: Boolean,
    canRefresh: Boolean,
    proofInfo: AiProofModeInfo,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    ProdyCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
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
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = ProdyForestGreen,
                        letterSpacing = 1.sp
                    )
                }

                if (canRefresh && !isLoading) {
                    IconButton(onClick = onRefresh, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = ProdyIcons.Refresh,
                            contentDescription = "Refresh",
                            tint = ProdyTextSecondaryLight,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = ProdyForestGreen,
                    strokeWidth = 3.dp
                )
            } else {
                Text(
                    text = "\"$thought\"",
                    style = MaterialTheme.typography.titleLarge,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontStyle = FontStyle.Italic,
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

            // AI Proof Mode debug info
            if (proofInfo.isEnabled) {
                Spacer(modifier = Modifier.height(16.dp))
                AiProofModeDebugInfo(proofInfo = proofInfo)
            }
        }
    }
}

/**
 * Next Action Card
 * Contextual suggestion for the user's next step.
 */
@Composable
fun NextActionCard(
    nextAction: NextAction?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (nextAction == null) return

    val (icon, color) = when (nextAction.type) {
        NextActionType.START_JOURNAL, NextActionType.FOLLOW_UP_JOURNAL -> ProdyIcons.Book to ProdyForestGreen
        NextActionType.LEARN_WORD, NextActionType.REVIEW_WORDS -> ProdyIcons.School to ProdyWarmAmber
        NextActionType.WRITE_FUTURE_MESSAGE -> ProdyIcons.Send to Color(0xFF9C27B0)
        NextActionType.REFLECT_ON_QUOTE -> ProdyIcons.Lightbulb to ProdyInfo
        NextActionType.COMPLETE_CHALLENGE -> ProdyIcons.EmojiEvents to ProdyAccent
    }

    ProdyClickableCard(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        backgroundColor = color.copy(alpha = 0.05f)
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
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nextAction.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ProdyTextPrimaryLight
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
                tint = ProdyTextTertiaryLight,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
