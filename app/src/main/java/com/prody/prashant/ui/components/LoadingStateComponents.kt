package com.prody.prashant.ui.components
import com.prody.prashant.ui.icons.ProdyIcons

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prody.prashant.ui.theme.*

/**
 * Loading State UI Components for Prody
 *
 * Comprehensive loading indicators and skeleton loaders that maintain
 * the premium feel while clearly communicating loading states.
 *
 * Design Philosophy:
 * - Smooth, calming animations (not jarring)
 * - Consistent with brand colors
 * - Accessible and performant
 */

// =============================================================================
// PRODY LOADING SPINNER
// =============================================================================

/**
 * Premium Prody loading spinner with accent color.
 */
@Composable
fun ProdyLoadingSpinner(
    modifier: Modifier = Modifier,
    size: LoadingSpinnerSize = LoadingSpinnerSize.Medium,
    color: Color = MaterialTheme.colorScheme.primary
) {
    val dimensions = when (size) {
        LoadingSpinnerSize.Small -> 24.dp
        LoadingSpinnerSize.Medium -> 40.dp
        LoadingSpinnerSize.Large -> 56.dp
    }

    val strokeWidth = when (size) {
        LoadingSpinnerSize.Small -> 2.dp
        LoadingSpinnerSize.Medium -> 3.dp
        LoadingSpinnerSize.Large -> 4.dp
    }

    val infiniteTransition = rememberInfiniteTransition(label = "spinner")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spinner_rotation"
    )

    androidx.compose.foundation.Canvas(
        modifier = modifier
            .size(dimensions)
            .rotate(rotation)
    ) {
        // Background track
        drawArc(
            color = color.copy(alpha = 0.2f),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth.toPx())
        )
        // Active arc
        drawArc(
            color = color,
            startAngle = 0f,
            sweepAngle = 90f,
            useCenter = false,
            style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
        )
    }
}

enum class LoadingSpinnerSize {
    Small, Medium, Large
}

// =============================================================================
// FULL SCREEN LOADING
// =============================================================================

/**
 * Full screen loading overlay with optional message.
 */
@Composable
fun ProdyFullScreenLoading(
    modifier: Modifier = Modifier,
    message: String? = null
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProdyLoadingSpinner(size = LoadingSpinnerSize.Large)

            if (message != null) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// =============================================================================
// BUTTON LOADING STATE
// =============================================================================

/**
 * Loading state for buttons - replaces button content.
 */
@Composable
fun ButtonLoadingContent(
    isLoading: Boolean,
    loadingText: String = "Loading...",
    content: @Composable () -> Unit
) {
    if (isLoading) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProdyLoadingSpinner(
                size = LoadingSpinnerSize.Small,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = loadingText,
                style = MaterialTheme.typography.labelLarge
            )
        }
    } else {
        content()
    }
}

// =============================================================================
// SKELETON LOADERS
// =============================================================================

/**
 * Skeleton card loader for list items.
 */
@Composable
fun SkeletonCard(
    modifier: Modifier = Modifier,
    height: Dp = 80.dp
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar skeleton
            ShimmerEffect(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Text skeletons
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ShimmerEffect(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(14.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
                ShimmerEffect(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(12.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }
        }
    }
}

/**
 * Skeleton list loader.
 */
@Composable
fun SkeletonList(
    modifier: Modifier = Modifier,
    itemCount: Int = 5,
    itemHeight: Dp = 80.dp
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(itemCount) {
            SkeletonCard(height = itemHeight)
        }
    }
}

/**
 * Skeleton for stat cards.
 */
@Composable
fun SkeletonStatCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.size(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ShimmerEffect(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(8.dp))
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(12.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
        }
    }
}

/**
 * Skeleton for profile header.
 */
@Composable
fun SkeletonProfileHeader(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar
        ShimmerEffect(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Name
        ShimmerEffect(
            modifier = Modifier
                .width(120.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(4.dp))
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Rank
        ShimmerEffect(
            modifier = Modifier
                .width(80.dp)
                .height(14.dp)
                .clip(RoundedCornerShape(4.dp))
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Stats row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            repeat(3) {
                SkeletonStatCard()
            }
        }
    }
}

/**
 * Skeleton for challenge card.
 */
@Composable
fun SkeletonChallengeCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ShimmerEffect(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    ShimmerEffect(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(16.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ShimmerEffect(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(12.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                }
                ShimmerEffect(
                    modifier = Modifier
                        .width(50.dp)
                        .height(24.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Progress bar skeleton
            ShimmerEffect(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            )
        }
    }
}

// =============================================================================
// PULSING DOT LOADER
// =============================================================================

/**
 * Three dots pulsing loading indicator.
 */
@Composable
fun PulsingDotsLoader(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    dotSize: Dp = 8.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")

    val dot1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )

    val dot2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, delayMillis = 150, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )

    val dot3Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, delayMillis = 300, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(dotSize)
                .alpha(dot1Alpha)
                .background(color, CircleShape)
        )
        Box(
            modifier = Modifier
                .size(dotSize)
                .alpha(dot2Alpha)
                .background(color, CircleShape)
        )
        Box(
            modifier = Modifier
                .size(dotSize)
                .alpha(dot3Alpha)
                .background(color, CircleShape)
        )
    }
}

// =============================================================================
// LOADING OVERLAY
// =============================================================================

/**
 * Semi-transparent loading overlay for content that's refreshing.
 */
@Composable
fun LoadingOverlay(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        content()

        if (isLoading) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                ProdyLoadingSpinner(size = LoadingSpinnerSize.Medium)
            }
        }
    }
}

// =============================================================================
// PULL TO REFRESH INDICATOR
// =============================================================================

/**
 * Custom pull to refresh indicator.
 */
@Composable
fun ProdyRefreshIndicator(
    isRefreshing: Boolean,
    pullProgress: Float,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isRefreshing) 1f else pullProgress.coerceIn(0f, 1f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "refresh_scale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        if (isRefreshing) {
            ProdyLoadingSpinner(
                size = LoadingSpinnerSize.Small,
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            androidx.compose.material.icons.ProdyIcons.let { icons ->
                androidx.compose.material3.Icon(
                    imageVector = icons.ArrowDownward,
                    contentDescription = "Pull to refresh",
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = pullProgress),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// =============================================================================
// CONTENT LOADING STATE WRAPPER
// =============================================================================

/**
 * Generic content state wrapper that handles loading, error, and empty states.
 */
@Composable
fun <T> ContentLoadingState(
    data: T?,
    isLoading: Boolean,
    error: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    loadingContent: @Composable () -> Unit = { ProdyFullScreenLoading() },
    emptyContent: @Composable () -> Unit = {},
    errorContent: @Composable (String) -> Unit = { errorMsg ->
        ProdyErrorEmptyState(
            icon = androidx.compose.material.icons.ProdyIcons.ErrorOutline,
            title = "Something went wrong",
            message = errorMsg,
            retryLabel = "Try Again",
            onRetryClick = onRetry
        )
    },
    content: @Composable (T) -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        when {
            isLoading && data == null -> loadingContent()
            error != null && data == null -> errorContent(error)
            data != null -> content(data)
            else -> emptyContent()
        }
    }
}

// =============================================================================
// INLINE LOADING TEXT
// =============================================================================

/**
 * Loading text with animated ellipsis.
 */
@Composable
fun LoadingText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading_text")
    val dotCount by infiniteTransition.animateValue(
        initialValue = 0,
        targetValue = 4,
        typeConverter = Int.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Restart
        ),
        label = "dots"
    )

    val dots = ".".repeat(dotCount % 4)

    Text(
        text = "$text$dots",
        modifier = modifier,
        style = MaterialTheme.typography.bodyMedium,
        color = color
    )
}
