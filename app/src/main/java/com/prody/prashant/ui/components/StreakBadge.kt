package com.prody.prashant.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.prody.prashant.ui.theme.StreakFire
import com.prody.prashant.ui.theme.StreakGlow
import com.prody.prashant.ui.theme.GoldTier

@Composable
fun StreakBadge(
    streakDays: Int,
    modifier: Modifier = Modifier,
    showAnimation: Boolean = true,
    compact: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "streak_animation")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (showAnimation && streakDays > 0) 1.03f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "streak_scale"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    val shape = RoundedCornerShape(if (compact) 8.dp else 12.dp)
    val isActive = streakDays > 0
    val isMilestone = streakDays >= 7

    Row(
        modifier = modifier
            .scale(scale)
            .clip(shape)
            .then(
                if (isMilestone && isActive) {
                    Modifier.border(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                GoldTier.copy(alpha = glowAlpha),
                                StreakFire.copy(alpha = glowAlpha * 0.8f)
                            )
                        ),
                        shape = shape
                    )
                } else Modifier
            )
            .background(
                Brush.horizontalGradient(
                    colors = if (isActive) {
                        listOf(
                            StreakFire.copy(alpha = glowAlpha * 0.9f),
                            StreakGlow.copy(alpha = glowAlpha * 0.6f)
                        )
                    } else {
                        listOf(
                            Color.Gray.copy(alpha = 0.25f),
                            Color.Gray.copy(alpha = 0.15f)
                        )
                    }
                )
            )
            .padding(
                horizontal = if (compact) 10.dp else 12.dp,
                vertical = if (compact) 6.dp else 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(if (compact) 4.dp else 6.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.LocalFireDepartment,
            contentDescription = "Streak",
            tint = if (isActive) {
                if (isMilestone) GoldTier else StreakFire
            } else Color.Gray,
            modifier = Modifier.size(if (compact) 16.dp else 20.dp)
        )
        Text(
            text = streakDays.toString(),
            style = if (compact) MaterialTheme.typography.labelLarge
                   else MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}
