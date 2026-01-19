package com.prody.prashant.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.data.local.entity.FutureMessageEntity
import com.prody.prashant.ui.icons.ProdyIcons
import com.prody.prashant.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.cos
import kotlin.math.sin

/**
 * PodGrid - THE SEALED PODS
 *
 * Visual grid of sealed pods for future messages (time capsules).
 * Each pod is a mysterious, sealed container that holds a message to the future self.
 *
 * Pod States:
 * - SEALED: Default state, locked and waiting
 * - GLOWING: Close to delivery (within 7 days)
 * - READY: Delivery date has passed, ready to open
 * - HAS_PROPHECY: Contains a prediction about the future
 */

// Pod visual states
enum class PodState {
    SEALED,      // Default locked state
    GLOWING,     // Close to delivery (within 7 days)
    READY,       // Ready to open
    HAS_PROPHECY // Contains a prediction
}

/**
 * Grid of sealed pods
 */
@Composable
fun PodGrid(
    pods: List<FutureMessageEntity>,
    onPodClick: (FutureMessageEntity) -> Unit,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    if (pods.isEmpty()) {
        EmptyPodState(isDarkTheme = isDarkTheme)
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = modifier
        ) {
            items(
                items = pods.sortedBy { it.deliveryDate },
                key = { it.id }
            ) { message ->
                val daysRemaining = remember(message.deliveryDate) {
                    val diff = message.deliveryDate - System.currentTimeMillis()
                    TimeUnit.MILLISECONDS.toDays(diff).coerceAtLeast(0)
                }

                val podState = when {
                    daysRemaining <= 0 -> PodState.READY
                    daysRemaining <= 7 -> PodState.GLOWING
                    message.prediction != null -> PodState.HAS_PROPHECY
                    else -> PodState.SEALED
                }

                SealedPod(
                    message = message,
                    podState = podState,
                    daysRemaining = daysRemaining,
                    onClick = { onPodClick(message) },
                    isDarkTheme = isDarkTheme
                )
            }
        }
    }
}

/**
 * Individual sealed pod component
 */
@Composable
fun SealedPod(
    message: FutureMessageEntity,
    podState: PodState,
    daysRemaining: Long,
    onClick: () -> Unit,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("MMM d", Locale.getDefault()) }

    // Animations for glowing pods
    val infiniteTransition = rememberInfiniteTransition(label = "pod_glow")

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Colors based on state
    val podColor = when (podState) {
        PodState.READY -> PodReadyColor
        PodState.GLOWING -> PodGlowingColor
        PodState.HAS_PROPHECY -> PodProphecyColor
        PodState.SEALED -> if (isDarkTheme) PodSealedDark else PodSealedLight
    }

    val backgroundColor = if (isDarkTheme) {
        TimeCapsuleEmptyCircleBgDark
    } else {
        TimeCapsuleTabContainerLight
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .scale(if (podState == PodState.GLOWING) pulseScale else 1f)
            .clickable(onClick = onClick)
    ) {
        // Outer glow effect for glowing pods
        if (podState == PodState.GLOWING || podState == PodState.READY) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(1.1f)
                    .alpha(glowAlpha)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                podColor.copy(alpha = 0.4f),
                                podColor.copy(alpha = 0.1f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        // Main pod container
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(16.dp),
            color = backgroundColor,
            tonalElevation = 0.dp
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Rotating ring for ready pods
                if (podState == PodState.READY) {
                    Canvas(
                        modifier = Modifier
                            .size(100.dp)
                            .alpha(0.6f)
                    ) {
                        val radius = size.minDimension / 2 - 4.dp.toPx()
                        val center = Offset(size.width / 2, size.height / 2)

                        // Rotating dashed circle
                        val dashPathEffect = PathEffect.dashPathEffect(
                            floatArrayOf(10f, 15f),
                            phase = rotationAngle
                        )
                        drawCircle(
                            color = podColor,
                            radius = radius,
                            center = center,
                            style = Stroke(
                                width = 2.dp.toPx(),
                                pathEffect = dashPathEffect
                            )
                        )
                    }
                }

                // Pod content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Pod icon
                    PodIcon(
                        podState = podState,
                        color = podColor,
                        rotationAngle = if (podState == PodState.GLOWING) rotationAngle * 0.1f else 0f,
                        modifier = Modifier.size(48.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Time indicator
                    Text(
                        text = when {
                            daysRemaining <= 0 -> "READY"
                            daysRemaining == 1L -> "TOMORROW"
                            daysRemaining <= 7 -> "${daysRemaining}d"
                            else -> dateFormat.format(Date(message.deliveryDate))
                        },
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (podState == PodState.READY || podState == PodState.GLOWING)
                            FontWeight.Bold else FontWeight.Medium,
                        color = podColor,
                        textAlign = TextAlign.Center
                    )

                    // Message title preview (blurred for sealed)
                    if (podState == PodState.SEALED || podState == PodState.HAS_PROPHECY) {
                        Text(
                            text = message.title.take(15) + if (message.title.length > 15) "..." else "",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isDarkTheme) TimeCapsuleTextSecondaryDark else TimeCapsuleTextSecondaryLight,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .blur(if (podState == PodState.SEALED) 3.dp else 0.dp)
                        )
                    }

                    // Prophecy indicator
                    if (podState == PodState.HAS_PROPHECY || message.prediction != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = PodProphecyColor.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "PROPHECY",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = PodProphecyColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 8.sp
                            )
                        }
                    }
                }

                // Lock icon overlay for sealed pods
                if (podState == PodState.SEALED) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = ProdyIcons.Lock,
                            contentDescription = "Sealed",
                            tint = if (isDarkTheme) TimeCapsuleTextSecondaryDark else TimeCapsuleTextSecondaryLight,
                            modifier = Modifier
                                .size(16.dp)
                                .alpha(0.5f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Custom pod icon based on state
 */
@Composable
private fun PodIcon(
    podState: PodState,
    color: Color,
    rotationAngle: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Outer orbital ring for glowing/ready states
        if (podState == PodState.GLOWING || podState == PodState.READY) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val radius = size.minDimension / 2 - 2.dp.toPx()
                val center = Offset(size.width / 2, size.height / 2)

                // Draw small orbiting dots
                val dotAngles = listOf(0f, 90f, 180f, 270f)
                dotAngles.forEach { angle ->
                    val adjustedAngle = angle + rotationAngle * 3
                    val radians = Math.toRadians(adjustedAngle.toDouble())
                    val dotX = center.x + radius * cos(radians).toFloat()
                    val dotY = center.y + radius * sin(radians).toFloat()
                    drawCircle(
                        color = color.copy(alpha = 0.7f),
                        radius = 2.dp.toPx(),
                        center = Offset(dotX, dotY)
                    )
                }
            }
        }

        // Inner pod shape
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when (podState) {
                    PodState.READY -> ProdyIcons.LockOpen
                    PodState.HAS_PROPHECY -> ProdyIcons.AutoAwesome
                    else -> ProdyIcons.HourglassBottom
                },
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Empty state when no pods exist
 */
@Composable
private fun EmptyPodState(isDarkTheme: Boolean) {
    val primaryTextColor = if (isDarkTheme) TimeCapsuleTextPrimaryDark else TimeCapsuleTextPrimaryLight
    val secondaryTextColor = if (isDarkTheme) TimeCapsuleTextSecondaryDark else TimeCapsuleTextSecondaryLight

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Empty pod illustration
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(
                    if (isDarkTheme) TimeCapsuleEmptyCircleBgDark
                    else TimeCapsuleTabContainerLight
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = ProdyIcons.HourglassEmpty,
                contentDescription = null,
                tint = TimeCapsuleAccent.copy(alpha = 0.5f),
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "No sealed pods yet",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = primaryTextColor,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Write a message to your future self and seal it in a time pod.",
            style = MaterialTheme.typography.bodyMedium,
            color = secondaryTextColor,
            textAlign = TextAlign.Center
        )
    }
}

// Pod colors
val PodSealedDark = Color(0xFF3A3A4C)
val PodSealedLight = Color(0xFFE8E8F0)
val PodGlowingColor = Color(0xFFC9FF3B) // Neon green
val PodReadyColor = Color(0xFF00E5FF) // Cyan
val PodProphecyColor = Color(0xFFAA66FF) // Purple for prophecy
