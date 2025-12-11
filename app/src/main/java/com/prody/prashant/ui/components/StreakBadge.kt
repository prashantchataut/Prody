package com.prody.prashant.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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

@Composable
fun StreakBadge(
    streakDays: Int,
    modifier: Modifier = Modifier,
    showAnimation: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "streak_animation")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (showAnimation && streakDays > 0) 1.05f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "streak_scale"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    Row(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.horizontalGradient(
                    colors = if (streakDays > 0) {
                        listOf(
                            StreakFire.copy(alpha = glowAlpha),
                            StreakGlow.copy(alpha = glowAlpha * 0.8f)
                        )
                    } else {
                        listOf(
                            Color.Gray.copy(alpha = 0.3f),
                            Color.Gray.copy(alpha = 0.2f)
                        )
                    }
                )
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.LocalFireDepartment,
            contentDescription = null,
            tint = if (streakDays > 0) StreakFire else Color.Gray,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = streakDays.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}
