package com.prody.prashant.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prody.prashant.ui.theme.ProdyGradients
import com.prody.prashant.ui.theme.ProdyTokens
import kotlin.math.min

/**
 * Prody Design System - Premium Progress Indicators
 *
 * A collection of animated progress indicators with gradient support
 * and smooth animations for a premium user experience.
 *
 * Features:
 * - Smooth entrance animations
 * - Gradient color support
 * - Accessibility labels
 * - 60fps performance
 */

// =============================================================================
// CIRCULAR PROGRESS INDICATOR
// =============================================================================

/**
 * Circular progress indicator with gradient and smooth animation.
 *
 * @param progress Progress value from 0.0 to 1.0
 * @param modifier Modifier for the indicator
 * @param size Size of the circular indicator
 * @param strokeWidth Width of the progress stroke
 * @param backgroundColor Background track color
 * @param progressColors List of colors for gradient progress arc
 * @param animationDuration Duration of the entrance animation in milliseconds
 * @param showPercentage Whether to display percentage text in center
 * @param content Optional content to display in the center
 */
@Composable
fun ProdyCircularProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = ProdyTokens.Progress.circularSizeLarge,
    strokeWidth: Dp = ProdyTokens.Progress.circularStrokeWidthLarge,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    progressColors: List<Color> = ProdyGradients.primaryGradient,
    animationDuration: Int = ProdyTokens.Animation.slow,
    showPercentage: Boolean = true,
    content: @Composable BoxScope.() -> Unit = {}
) {
    var animationPlayed by remember { mutableStateOf(false) }

    val animatedProgress = animateFloatAsState(
        targetValue = if (animationPlayed) progress.coerceIn(0f, 1f) else 0f,
        animationSpec = tween(
            durationMillis = animationDuration,
            easing = FastOutSlowInEasing
        ),
        label = "circular_progress"
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    Box(
        modifier = modifier
            .size(size)
            .semantics {
                // Defer state read to semantics phase
                val percentage = (animatedProgress.value * 100).toInt()
                contentDescription = "Progress: $percentage percent"
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidthPx = strokeWidth.toPx()
            val radius = (min(this.size.width, this.size.height) - strokeWidthPx) / 2
            val center = Offset(this.size.width / 2, this.size.height / 2)

            // Background circle
            drawCircle(
                color = backgroundColor,
                radius = radius,
                center = center,
                style = Stroke(width = strokeWidthPx)
            )

            // Progress arc with gradient
            // Defer state read to drawing phase
            val currentProgress = animatedProgress.value
            val sweepAngle = currentProgress * 360f
            if (sweepAngle > 0f) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = progressColors + progressColors.first()
                    ),
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(
                        center.x - radius,
                        center.y - radius
                    ),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(
                        width = strokeWidthPx,
                        cap = StrokeCap.Round
                    )
                )
            }
        }

        if (showPercentage) {
            // Note: Text still recomposes as it must read the state to display it
            // but the parent Box and Canvas won't recompose thanks to deferred reads above
            Text(
                text = "${(animatedProgress.value * 100).toInt()}%",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        content()
    }
}

// =============================================================================
// PRODY PROGRESS INDICATOR - CANVAS OPTIMIZED
// =============================================================================

/**
 * High-performance, Canvas-based progress indicator for pagers.
 *
 * Performance features:
 * - Uses animateFloatAsState for smooth index transitions
 * - Draws directly on Canvas to avoid recomposition of multiple dots
 * - Optimized for 60fps+ animations during page swipes
 *
 * @param currentPage Current active page index
 * @param totalPages Total number of pages
 * @param modifier Modifier for the indicator
 * @param activeColor Color for the active dot
 * @param inactiveColor Color for inactive dots
 * @param dotHeight Height of the dots
 * @param spacing Spacing between dots
 */
@Composable
fun ProdyProgressIndicator(
    currentPage: Int,
    totalPages: Int,
    modifier: Modifier = Modifier,
    activeColor: Color = com.prody.prashant.ui.theme.ProdyForestGreen,
    inactiveColor: Color = com.prody.prashant.ui.theme.ProdyOutlineLight,
    dotHeight: Dp = 4.dp,
    spacing: Dp = 4.dp
) {
    val animatedIndex = animateFloatAsState(
        targetValue = currentPage.toFloat(),
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "progress_indicator_index"
    )

    val dotHeightPx = with(androidx.compose.ui.platform.LocalDensity.current) { dotHeight.toPx() }
    val spacingPx = with(androidx.compose.ui.platform.LocalDensity.current) { spacing.toPx() }

    // Calculate total width based on dots (1 active dot is wider)
    // Inactive dots: 8dp, Active dot: 24dp
    val inactiveDotWidth = 8.dp
    val activeDotWidth = 24.dp

    val inactiveDotWidthPx = with(androidx.compose.ui.platform.LocalDensity.current) { inactiveDotWidth.toPx() }
    val activeDotWidthPx = with(androidx.compose.ui.platform.LocalDensity.current) { activeDotWidth.toPx() }

    Canvas(
        modifier = modifier
            .height(dotHeight)
            .fillMaxWidth()
    ) {
        val yOffset = size.height / 2
        val currentIndex = animatedIndex.value // Defer read to drawing phase

        var currentX = 0f

        for (i in 0 until totalPages) {
            // Calculate distance from animated index to determine width and color
            val distance = kotlin.math.abs(i - currentIndex)
            val selectionProgress = (1f - distance).coerceIn(0f, 1f)

            val dotWidth = inactiveDotWidthPx + (activeDotWidthPx - inactiveDotWidthPx) * selectionProgress
            val dotColor = androidx.compose.ui.graphics.lerp(inactiveColor, activeColor, selectionProgress)

            drawRoundRect(
                color = dotColor,
                topLeft = Offset(currentX, yOffset - dotHeightPx / 2),
                size = Size(dotWidth, dotHeightPx),
                cornerRadius = CornerRadius(dotHeightPx / 2, dotHeightPx / 2)
            )

            currentX += dotWidth + spacingPx
        }
    }
}

// =============================================================================
// SMALL CIRCULAR PROGRESS
// =============================================================================

/**
 * Compact circular progress indicator for inline use.
 *
 * @param progress Progress value from 0.0 to 1.0
 * @param modifier Modifier for the indicator
 * @param size Size of the indicator
 * @param strokeWidth Width of the progress stroke
 * @param progressColor Single color for progress
 * @param backgroundColor Background track color
 */
@Composable
fun ProdySmallCircularProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = ProdyTokens.Progress.circularSizeSmall,
    strokeWidth: Dp = ProdyTokens.Progress.circularStrokeWidth,
    progressColor: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    var animationPlayed by remember { mutableStateOf(false) }

    val animatedProgress = animateFloatAsState(
        targetValue = if (animationPlayed) progress.coerceIn(0f, 1f) else 0f,
        animationSpec = tween(
            durationMillis = ProdyTokens.Animation.normal,
            easing = FastOutSlowInEasing
        ),
        label = "small_circular_progress"
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    Canvas(
        modifier = modifier
            .size(size)
            .semantics {
                val percentage = (progress * 100).toInt()
                contentDescription = "Progress: $percentage percent"
            }
    ) {
        val strokeWidthPx = strokeWidth.toPx()
        val radius = (min(this.size.width, this.size.height) - strokeWidthPx) / 2
        val center = Offset(this.size.width / 2, this.size.height / 2)

        // Background
        drawCircle(
            color = backgroundColor,
            radius = radius,
            center = center,
            style = Stroke(width = strokeWidthPx)
        )

        // Progress
        val sweepAngle = animatedProgress.value * 360f // Defer read to drawing phase
        if (sweepAngle > 0f) {
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
            )
        }
    }
}

// =============================================================================
// LINEAR PROGRESS BAR
// =============================================================================

/**
 * Linear progress bar with gradient and smooth animation.
 *
 * @param progress Progress value from 0.0 to 1.0
 * @param modifier Modifier for the progress bar
 * @param height Height of the progress bar
 * @param backgroundColor Background track color
 * @param progressColors List of colors for gradient progress
 * @param animationDuration Duration of the entrance animation in milliseconds
 * @param cornerRadius Corner radius of the progress bar
 */
@Composable
fun ProdyLinearProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = ProdyTokens.Progress.linearHeight,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    progressColors: List<Color> = ProdyGradients.primaryGradient,
    animationDuration: Int = ProdyTokens.Animation.slow,
    cornerRadius: Dp = height / 2
) {
    var animationPlayed by remember { mutableStateOf(false) }

    val animatedProgress = animateFloatAsState(
        targetValue = if (animationPlayed) progress.coerceIn(0f, 1f) else 0f,
        animationSpec = tween(
            durationMillis = animationDuration,
            easing = FastOutSlowInEasing
        ),
        label = "linear_progress"
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .semantics {
                val percentage = (progress * 100).toInt()
                contentDescription = "Progress: $percentage percent"
            }
    ) {
        val cornerRadiusPx = cornerRadius.toPx()

        // Background track
        drawRoundRect(
            color = backgroundColor,
            cornerRadius = CornerRadius(cornerRadiusPx)
        )

        // Progress fill
        val currentProgress = animatedProgress.value // Defer read to drawing phase
        if (currentProgress > 0f) {
            drawRoundRect(
                brush = Brush.horizontalGradient(progressColors),
                size = Size(this.size.width * currentProgress, this.size.height),
                cornerRadius = CornerRadius(cornerRadiusPx)
            )
        }
    }
}

// =============================================================================
// SEGMENTED PROGRESS BAR
// =============================================================================

/**
 * Segmented progress bar showing discrete steps.
 *
 * @param currentStep Current step (1-indexed)
 * @param totalSteps Total number of steps
 * @param modifier Modifier for the progress bar
 * @param height Height of each segment
 * @param segmentSpacing Space between segments
 * @param completedColor Color for completed segments
 * @param uncompletedColor Color for incomplete segments
 * @param currentColor Color for the current step
 */
@Composable
fun ProdySegmentedProgress(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier,
    height: Dp = ProdyTokens.Progress.linearHeight,
    segmentSpacing: Dp = 4.dp,
    completedColor: Color = MaterialTheme.colorScheme.primary,
    uncompletedColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    currentColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
) {
    val accessibilityDescription = "Step $currentStep of $totalSteps"

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .semantics { contentDescription = accessibilityDescription }
    ) {
        val segmentSpacingPx = segmentSpacing.toPx()
        val totalSpacing = segmentSpacingPx * (totalSteps - 1)
        val segmentWidth = (this.size.width - totalSpacing) / totalSteps
        val cornerRadiusPx = (height / 2).toPx()

        for (i in 0 until totalSteps) {
            val segmentColor = when {
                i < currentStep - 1 -> completedColor
                i == currentStep - 1 -> currentColor
                else -> uncompletedColor
            }

            val startX = i * (segmentWidth + segmentSpacingPx)

            drawRoundRect(
                color = segmentColor,
                topLeft = Offset(startX, 0f),
                size = Size(segmentWidth, this.size.height),
                cornerRadius = CornerRadius(cornerRadiusPx)
            )
        }
    }
}

// =============================================================================
// LEVEL PROGRESS BAR
// =============================================================================

/**
 * Progress bar styled for level/XP display with label support.
 *
 * @param progress Progress value from 0.0 to 1.0
 * @param modifier Modifier for the component
 * @param height Height of the progress bar
 * @param progressColors Gradient colors for the progress fill
 * @param backgroundColor Background track color
 * @param showGlow Whether to show a subtle glow effect
 * @param glowColor Color for the glow effect
 */
@Composable
fun ProdyLevelProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = ProdyTokens.Progress.linearHeightLarge,
    progressColors: List<Color> = ProdyGradients.goldGradient,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    showGlow: Boolean = false,
    glowColor: Color = progressColors.firstOrNull() ?: MaterialTheme.colorScheme.primary
) {
    var animationPlayed by remember { mutableStateOf(false) }

    val animatedProgress = animateFloatAsState(
        targetValue = if (animationPlayed) progress.coerceIn(0f, 1f) else 0f,
        animationSpec = tween(
            durationMillis = ProdyTokens.Animation.slow,
            easing = FastOutSlowInEasing
        ),
        label = "level_progress"
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .semantics {
                val percentage = (progress * 100).toInt()
                contentDescription = "Level progress: $percentage percent"
            }
    ) {
        val cornerRadiusPx = (height / 2).toPx()
        val currentProgress = animatedProgress.value // Defer read to drawing phase

        // Optional glow effect
        if (showGlow && currentProgress > 0f) {
            val glowWidth = this.size.width * currentProgress
            drawRoundRect(
                color = glowColor.copy(alpha = 0.3f),
                size = Size(glowWidth + 4.dp.toPx(), this.size.height + 4.dp.toPx()),
                topLeft = Offset(-2.dp.toPx(), -2.dp.toPx()),
                cornerRadius = CornerRadius(cornerRadiusPx + 2.dp.toPx())
            )
        }

        // Background track
        drawRoundRect(
            color = backgroundColor,
            cornerRadius = CornerRadius(cornerRadiusPx)
        )

        // Progress fill with gradient
        if (currentProgress > 0f) {
            drawRoundRect(
                brush = Brush.horizontalGradient(progressColors),
                size = Size(this.size.width * currentProgress, this.size.height),
                cornerRadius = CornerRadius(cornerRadiusPx)
            )
        }
    }
}

// =============================================================================
// STREAK PROGRESS RING
// =============================================================================

/**
 * Circular progress specifically styled for streak displays.
 *
 * @param progress Progress value from 0.0 to 1.0
 * @param modifier Modifier for the indicator
 * @param size Size of the ring
 * @param strokeWidth Width of the ring stroke
 * @param progressColors Gradient colors for the progress (defaults to streak colors)
 * @param backgroundColor Background ring color
 * @param content Content to display in the center
 */
@Composable
fun ProdyStreakProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    strokeWidth: Dp = 8.dp,
    progressColors: List<Color> = ProdyGradients.streakGradient,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
    content: @Composable BoxScope.() -> Unit = {}
) {
    var animationPlayed by remember { mutableStateOf(false) }

    val animatedProgress = animateFloatAsState(
        targetValue = if (animationPlayed) progress.coerceIn(0f, 1f) else 0f,
        animationSpec = tween(
            durationMillis = ProdyTokens.Animation.slow,
            easing = FastOutSlowInEasing
        ),
        label = "streak_progress_ring"
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    Box(
        modifier = modifier
            .size(size)
            .semantics {
                val percentage = (progress * 100).toInt()
                contentDescription = "Streak progress: $percentage percent"
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidthPx = strokeWidth.toPx()
            val radius = (min(this.size.width, this.size.height) - strokeWidthPx) / 2
            val center = Offset(this.size.width / 2, this.size.height / 2)

            // Background ring
            drawCircle(
                color = backgroundColor,
                radius = radius,
                center = center,
                style = Stroke(width = strokeWidthPx)
            )

            // Progress arc
            val currentProgress = animatedProgress.value // Defer read to drawing phase
            val sweepAngle = currentProgress * 360f
            if (sweepAngle > 0f) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = progressColors + progressColors.first()
                    ),
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                )
            }
        }

        content()
    }
}
