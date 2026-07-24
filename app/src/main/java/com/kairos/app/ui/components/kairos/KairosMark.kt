package com.kairos.app.ui.components.kairos

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kairos.app.ui.animation.KairosDurations
import com.kairos.app.ui.animation.KairosEasing
import com.kairos.app.ui.animation.rememberKairosReducedMotion

/**
 * Kairos aperture mark.
 *
 * The open arc suggests an attentive mind without copying a literal head
 * silhouette. The asymmetric six-ray aperture represents a thought arriving at
 * the right moment. It remains recognizable in monochrome and at launcher size.
 */
@Composable
fun KairosMark(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    accent: Color = MaterialTheme.colorScheme.primary,
    revealed: Boolean = true
) {
    val reducedMotion = rememberKairosReducedMotion()
    val reveal by animateFloatAsState(
        targetValue = if (revealed) 1f else 0f,
        animationSpec = tween(
            durationMillis = if (reducedMotion) KairosDurations.Micro else 520,
            easing = KairosEasing.EaseOutExpo
        ),
        label = "kairos-mark-reveal"
    )

    Canvas(
        modifier = modifier.graphicsLayer {
            alpha = reveal
            scaleX = 0.86f + (0.14f * reveal)
            scaleY = 0.86f + (0.14f * reveal)
        }
    ) {
        val min = size.minDimension
        val stroke = min * 0.14f
        val inset = stroke * 0.65f

        drawArc(
            color = tint,
            startAngle = 132f,
            sweepAngle = 286f * reveal,
            useCenter = false,
            topLeft = Offset(inset, inset),
            size = Size(min - inset * 2f, min - inset * 2f),
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )

        drawLine(
            color = tint,
            start = Offset(min * 0.51f, min * 0.66f),
            end = Offset(min * 0.51f, min * (0.90f * reveal + 0.66f * (1f - reveal))),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = tint,
            start = Offset(min * 0.51f, min * 0.88f),
            end = Offset(min * 0.76f, min * 0.88f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )

        val center = Offset(min * 0.53f, min * 0.45f)
        val longRay = min * 0.18f * reveal
        val shortRay = min * 0.13f * reveal
        val rayStroke = min * 0.065f
        listOf(0f, 60f, 120f).forEachIndexed { index, degrees ->
            val radians = Math.toRadians(degrees.toDouble())
            val dx = kotlin.math.cos(radians).toFloat() * if (index == 0) longRay else shortRay
            val dy = kotlin.math.sin(radians).toFloat() * if (index == 0) longRay else shortRay
            drawLine(
                color = accent,
                start = Offset(center.x - dx, center.y - dy),
                end = Offset(center.x + dx, center.y + dy),
                strokeWidth = rayStroke,
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
fun KairosWordmark(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurface,
    accent: Color = MaterialTheme.colorScheme.primary
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        KairosMark(
            modifier = Modifier.size(34.dp),
            tint = tint,
            accent = accent
        )
        Text(
            text = "Kairos",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = tint
        )
    }
}
