package com.prody.prashant.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.domain.intelligence.IntelligenceInsight
import com.prody.prashant.domain.progress.NextAction
import com.prody.prashant.domain.progress.TodayProgress
import com.prody.prashant.ui.components.ProdyClickableCard
import com.prody.prashant.ui.components.ProdyPremiumCard
import com.prody.prashant.ui.icons.ProdyIcons
import com.prody.prashant.ui.theme.*

/**
 * NextActionCard - Suggests the most relevant next step for the user.
 */
@Composable
fun NextActionCard(
    action: NextAction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ProdyClickableCard(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        backgroundColor = ProdyForestGreen.copy(alpha = 0.05f),
        contentDescription = "Next suggested action: ${action.title}"
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(ProdyForestGreen.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = ProdyIcons.Bolt,
                    contentDescription = null,
                    tint = ProdyForestGreen,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "UP NEXT",
                    style = MaterialTheme.typography.labelSmall,
                    color = ProdyForestGreen,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = action.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = action.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = ProdyForestGreen.copy(alpha = 0.5f)
            )
        }
    }
}

/**
 * TodayProgressCard - Displays a summary of today's activities.
 */
@Composable
fun TodayProgressCard(
    progress: TodayProgress,
    modifier: Modifier = Modifier
) {
    ProdyPremiumCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        backgroundColor = MaterialTheme.colorScheme.surface,
        contentDescription = "Today's progress summary"
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "TODAY'S MOMENTUM",
                style = MaterialTheme.typography.labelSmall,
                color = ProdyTextSecondaryLight,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ProgressStatItem(
                    label = "Entries",
                    value = progress.journalEntries.toString(),
                    color = ProdyForestGreen,
                    modifier = Modifier.weight(1f)
                )

                // Vertical Divider
                Box(modifier = Modifier.width(1.dp).height(40.dp).background(MaterialTheme.colorScheme.outlineVariant))

                ProgressStatItem(
                    label = "Words",
                    value = progress.wordsWritten.toString(),
                    color = ProdyWarmAmber,
                    modifier = Modifier.weight(1f)
                )

                // Vertical Divider
                Box(modifier = Modifier.width(1.dp).height(40.dp).background(MaterialTheme.colorScheme.outlineVariant))

                ProgressStatItem(
                    label = "XP Gained",
                    value = "+${progress.pointsEarned}",
                    color = ProdyInfo,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ProgressStatItem(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = ProdyTextSecondaryLight
        )
    }
}

/**
 * IntelligenceInsightCard - Displays AI-powered insights about the user.
 */
@Composable
fun IntelligenceInsightCard(
    insight: IntelligenceInsight,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, ProdyForestGreen.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            ProdyForestGreen.copy(alpha = 0.03f),
                            Color.Transparent
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(ProdyForestGreen.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = ProdyForestGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = "IDENTITY INSIGHT",
                        style = TextStyle(
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            letterSpacing = 1.sp,
                            color = ProdyForestGreen
                        )
                    )
                    Text(
                        text = insight.title,
                        style = TextStyle(
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = insight.description,
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            if (insight.actionable != null) {
                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onActionClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ProdyForestGreen,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Text(
                        text = insight.actionable!!,
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
