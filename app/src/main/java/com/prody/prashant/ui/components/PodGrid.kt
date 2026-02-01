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

enum class PodState {
    SEALED,
    GLOWING,
    READY,
    HAS_PROPHECY
}

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
    val infiniteTransition = rememberInfiniteTransition(label = "pod_glow")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val podColor = when (podState) {
        PodState.READY -> Color(0xFF00E5FF)
        PodState.GLOWING -> Color(0xFFC9FF3B)
        PodState.HAS_PROPHECY -> Color(0xFFAA66FF)
        PodState.SEALED -> if (isDarkTheme) Color(0xFF3A3A4C) else Color(0xFFE8E8F0)
    }

    // Using MaterialTheme colors to avoid unresolved references
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .scale(if (podState == PodState.GLOWING) pulseScale else 1f)
            .clickable(onClick = onClick)
    ) {
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(podColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (podState) {
                                PodState.READY -> ProdyIcons.LockOpen
                                PodState.HAS_PROPHECY -> ProdyIcons.AutoAwesome
                                else -> ProdyIcons.HourglassBottom
                            },
                            contentDescription = null,
                            tint = podColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = when {
                            daysRemaining <= 0 -> "READY"
                            daysRemaining == 1L -> "TOMORROW"
                            daysRemaining <= 7 -> "${daysRemaining}d"
                            else -> dateFormat.format(Date(message.deliveryDate))
                        },
                        style = MaterialTheme.typography.labelLarge,
                        color = podColor,
                        textAlign = TextAlign.Center
                    )

                    if (podState == PodState.SEALED || podState == PodState.HAS_PROPHECY) {
                        Text(
                            text = message.title.take(15) + if (message.title.length > 15) "..." else "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .blur(if (podState == PodState.SEALED) 3.dp else 0.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyPodState(isDarkTheme: Boolean) {
    val primaryTextColor = MaterialTheme.colorScheme.onSurface
    val secondaryTextColor = MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = ProdyIcons.HourglassEmpty,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
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