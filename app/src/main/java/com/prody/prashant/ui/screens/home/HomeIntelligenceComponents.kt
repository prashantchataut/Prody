package com.prody.prashant.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.domain.progress.NextAction
import com.prody.prashant.ui.icons.ProdyIcons
import com.prody.prashant.ui.theme.*

/**
 * BuddhaThoughtCard: Displays AI-generated wisdom.
 * Optimized for performance with conditional loading states.
 */
@Composable
fun BuddhaThoughtCard(
    thought: String,
    explanation: String,
    isLoading: Boolean,
    canRefresh: Boolean,
    onRefresh: () -> Unit,
    proofInfo: AiProofModeInfo,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = ProdySurfaceLight,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(ProdyForestGreen.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = ProdyIcons.Psychology,
                            contentDescription = null,
                            tint = ProdyForestGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Buddha's Thought",
                        style = MaterialTheme.typography.titleSmall,
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.SemiBold,
                        color = ProdyTextPrimaryLight
                    )
                }

                if (canRefresh) {
                    IconButton(
                        onClick = onRefresh,
                        enabled = !isLoading,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Wisdom",
                            tint = if (isLoading) ProdyTextTertiaryLight else ProdyForestGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().height(2.dp).clip(CircleShape),
                    color = ProdyForestGreen,
                    trackColor = ProdyForestGreen.copy(alpha = 0.1f)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(
                text = thought,
                style = MaterialTheme.typography.bodyLarge,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Medium,
                lineHeight = 24.sp,
                color = ProdyTextPrimaryLight
            )

            if (explanation.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = explanation,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = PoppinsFamily,
                    color = ProdyTextSecondaryLight,
                    lineHeight = 18.sp
                )
            }

            // AI Proof Mode Info (Performance Debugging)
            if (proofInfo.isEnabled) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Source: ${proofInfo.provider} | Cache: ${proofInfo.cacheStatus}",
                    style = MaterialTheme.typography.labelSmall,
                    color = ProdyTextTertiaryLight
                )
            }
        }
    }
}

/**
 * NextActionCard: Contextual suggestion for the user.
 */
@Composable
fun NextActionCard(
    nextAction: NextAction?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = nextAction != null,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        nextAction?.let { action ->
            Surface(
                modifier = modifier
                    .fillMaxWidth()
                    .clickable { onClick() },
                shape = RoundedCornerShape(16.dp),
                color = ProdyForestGreen,
                contentColor = Color.White
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Lightbulb,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = action.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontFamily = PoppinsFamily
                        )
                        Text(
                            text = action.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = PoppinsFamily,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    Icon(
                        imageVector = ProdyIcons.ChevronRight,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }
    }
}

/**
 * AiConfigWarningBanner: Warning when AI is not configured.
 */
@Composable
fun AiConfigWarningBanner(
    status: AiConfigurationStatus,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (status == AiConfigurationStatus.MISSING) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = ProdyWarning.copy(alpha = 0.1f),
            border = androidx.compose.foundation.BorderStroke(1.dp, ProdyWarning.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "AI Not Configured",
                    tint = ProdyWarning,
                    modifier = Modifier.size(20.dp).clickable { onDismiss() }
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "AI Wisdom is limited. Configure API key in Settings.",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = PoppinsFamily,
                    color = ProdyWarning,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
