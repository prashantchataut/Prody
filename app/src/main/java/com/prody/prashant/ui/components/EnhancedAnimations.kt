package com.prody.prashant.ui.components
import com.prody.prashant.ui.icons.ProdyIcons

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prody.prashant.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.*
import kotlin.random.Random

/**
 * Prody Enhanced Animations Library
 *
 * Additional premium animation components for next-level UI/UX:
 * - Contextual header micro-animations
 * - Enhanced scroll-to-top indicators
 * - Input field fluidity effects
 * - Premium ripple and touch feedback
 * - Delivery countdown visualizations
 * - Intelligent mood suggestions
 */

// =============================================================================
// CONTEXTUAL HEADER MICRO-ANIMATIONS
// =============================================================================

/**
 * Enhanced header that shrinks and transforms with scroll.
 * Features a scroll-to-top indicator that appears elegantly.
 *
 * @param title The header title
 * @param scrollOffset Current scroll position
 * @param onScrollToTop Callback when scroll-to-top is tapped
 * @param modifier Modifier for the header
 */
@Composable
fun MagicalHeader(
    title: String,
    scrollOffset: Float,
    onScrollToTop: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null
) {
    val collapseProgress = (scrollOffset / 150f).coerceIn(0f, 1f)

    // Animated title scale
    val titleScale by animateFloatAsState(
        targetValue = 1f - (collapseProgress * 0.15f),
        animationSpec = tween(200, easing = EaseOutCubic),
        label = "title_scale"
    )

    // Show scroll-to-top button when scrolled
    val showScrollToTop by remember(scrollOffset) {
        derivedStateOf { scrollOffset > 300f }
    }

    val scrollToTopAlpha by animateFloatAsState(
        targetValue = if (showScrollToTop) 1f else 0f,
        animationSpec = tween(300),
        label = "scroll_top_alpha"
    )

    val scrollToTopScale by animateFloatAsState(
        targetValue = if (showScrollToTop) 1f else 0.5f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f),
        label = "scroll_top_scale"
    )

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = titleScale
                        scaleY = titleScale
                        transformOrigin = TransformOrigin(0f, 0.5f)
                    }
            )

            // Subtitle with fade
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.alpha(1f - collapseProgress)
                )
            }
        }

        // Scroll-to-top button with premium animation
        if (scrollToTopAlpha > 0f) {
            ScrollToTopButton(
                onClick = onScrollToTop,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
                    .alpha(scrollToTopAlpha)
                    .scale(scrollToTopScale)
            )
        }
    }
}

/**
 * Premium scroll-to-top button with breathing glow.
 */
@Composable
private fun ScrollToTopButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "scroll_top_glow")

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    val bounceOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -4f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce_offset"
    )

    Box(
        modifier = modifier
            .size(40.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        // Glow layer
        Box(
            modifier = Modifier
                .size(36.dp)
                .alpha(glowAlpha)
                .blur(8.dp)
                .background(ProdyPrimary, CircleShape)
        )

        // Button
        Box(
            modifier = Modifier
                .size(32.dp)
                .offset(y = bounceOffset.dp)
                .clip(CircleShape)
                .background(ProdyPrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = ProdyIcons.KeyboardArrowUp,
                contentDescription = "Scroll to top",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// =============================================================================
// TIME CAPSULE DELIVERY COUNTDOWN VISUALIZATION
// =============================================================================

/**
 * Subtle countdown visualization for pending time capsule messages.
 * Shows a diminishing aura when close to delivery.
 *
 * @param daysUntilDelivery Days remaining until message delivery
 * @param modifier Modifier for the countdown
 */
@Composable
fun DeliveryCountdownAura(
    daysUntilDelivery: Int,
    modifier: Modifier = Modifier
) {
    // Only show for messages close to delivery (within 7 days)
    if (daysUntilDelivery > 7) return

    val infiniteTransition = rememberInfiniteTransition(label = "countdown_aura")

    // Pulse intensity increases as delivery approaches
    val intensity = 1f - (daysUntilDelivery / 7f)

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.1f * intensity,
        targetValue = 0.4f * intensity,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (2000 - (intensity * 1000).toInt()).coerceAtLeast(800),
                easing = EaseInOutSine
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1f + (0.1f * intensity),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (2000 - (intensity * 1000).toInt()).coerceAtLeast(800),
                easing = EaseInOutSine
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Color shifts from teal to gold as delivery approaches
    val auraColor = androidx.compose.ui.graphics.lerp(
        TimeCapsuleAccent,
        FutureMessageArrived,
        intensity
    )

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Outer glow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(pulseScale)
                .alpha(pulseAlpha)
                .blur(16.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            auraColor.copy(alpha = 0.5f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
        )
    }
}

/**
 * Animated mini clock for countdown visualization.
 * Shows a tiny, barely perceptible clock hand moving.
 */
@Composable
fun MiniCountdownClock(
    hoursRemaining: Int,
    modifier: Modifier = Modifier,
    size: Dp = 16.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mini_clock")

    // Slow rotation representing time passage
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(60000, easing = LinearEasing), // 1 minute per rotation
            repeatMode = RepeatMode.Restart
        ),
        label = "clock_rotation"
    )

    val density = LocalDensity.current
    val sizePx = with(density) { size.toPx() }

    Canvas(modifier = modifier.size(size)) {
        val center = Offset(sizePx / 2, sizePx / 2)
        val radius = sizePx / 2 - 2

        // Clock face outline
        drawCircle(
            color = TimeCapsuleAccent.copy(alpha = 0.3f),
            radius = radius,
            center = center,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1f)
        )

        // Hour hand (static, based on remaining hours)
        val hourAngle = ((hoursRemaining % 12) * 30f - 90f) * (PI / 180f)
        val hourHandLength = radius * 0.5f
        drawLine(
            color = TimeCapsuleAccent.copy(alpha = 0.6f),
            start = center,
            end = Offset(
                center.x + hourHandLength * cos(hourAngle).toFloat(),
                center.y + hourHandLength * sin(hourAngle).toFloat()
            ),
            strokeWidth = 2f
        )

        // Minute hand (animated)
        val minuteAngle = (rotation - 90f) * (PI / 180f)
        val minuteHandLength = radius * 0.7f
        drawLine(
            color = TimeCapsuleAccent,
            start = center,
            end = Offset(
                center.x + minuteHandLength * cos(minuteAngle).toFloat(),
                center.y + minuteHandLength * sin(minuteAngle).toFloat()
            ),
            strokeWidth = 1.5f
        )

        // Center dot
        drawCircle(
            color = TimeCapsuleAccent,
            radius = 2f,
            center = center
        )
    }
}

// =============================================================================
// INTELLIGENT INPUT FEEDBACK - Journaling Flow
// =============================================================================

/**
 * State holder for intelligent mood suggestion based on text analysis.
 * This is a conceptual implementation showing how AI hints would work.
 */
class MoodSuggestionState {
    var suggestedMood by mutableStateOf<String?>(null)
        private set

    var confidence by mutableStateOf(0f)
        private set

    var isAnalyzing by mutableStateOf(false)
        private set

    fun analyzeText(text: String) {
        if (text.length < 20) {
            suggestedMood = null
            confidence = 0f
            return
        }

        isAnalyzing = true

        // Conceptual keyword-based analysis (in production, this would use ML)
        val lowercaseText = text.lowercase()

        val moodKeywords = mapOf(
            "happy" to listOf("joy", "happy", "excited", "great", "wonderful", "amazing", "love", "blessed"),
            "calm" to listOf("peaceful", "calm", "relaxed", "serene", "quiet", "content", "mindful"),
            "anxious" to listOf("worried", "anxious", "nervous", "stressed", "overwhelmed", "uncertain"),
            "sad" to listOf("sad", "down", "lonely", "miss", "difficult", "hard", "struggle"),
            "motivated" to listOf("motivated", "determined", "focused", "goal", "achieve", "progress"),
            "grateful" to listOf("grateful", "thankful", "appreciate", "blessed", "fortune")
        )

        var bestMood: String? = null
        var bestScore = 0

        moodKeywords.forEach { (mood, keywords) ->
            val score = keywords.count { lowercaseText.contains(it) }
            if (score > bestScore) {
                bestScore = score
                bestMood = mood
            }
        }

        suggestedMood = if (bestScore >= 2) bestMood else null
        confidence = (bestScore / 3f).coerceIn(0f, 1f)
        isAnalyzing = false
    }

    fun clearSuggestion() {
        suggestedMood = null
        confidence = 0f
    }
}

@Composable
fun rememberMoodSuggestionState(): MoodSuggestionState {
    return remember { MoodSuggestionState() }
}

/**
 * Subtle mood suggestion indicator that appears near mood selectors.
 * Non-intrusive hint based on journal text analysis.
 *
 * @param state MoodSuggestionState with analysis results
 * @param modifier Modifier for the indicator
 */
@Composable
fun MoodSuggestionHint(
    state: MoodSuggestionState,
    modifier: Modifier = Modifier
) {
    val suggestedMood = state.suggestedMood ?: return

    val animatedAlpha by animateFloatAsState(
        targetValue = state.confidence.coerceIn(0.4f, 0.8f),
        animationSpec = tween(500),
        label = "suggestion_alpha"
    )

    val pulseAlpha by rememberInfiniteTransition(label = "hint_pulse")
        .animateFloat(
            initialValue = 0.8f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = EaseInOutSine),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse"
        )

    Row(
        modifier = modifier.alpha(animatedAlpha * pulseAlpha),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Small indicator dot
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(JournalAccentGreen, CircleShape)
        )

        Text(
            text = "Feeling $suggestedMood?",
            style = MaterialTheme.typography.labelSmall,
            color = JournalAccentGreen.copy(alpha = 0.9f)
        )
    }
}

// =============================================================================
// PREMIUM TOUCH FEEDBACK - Ripple & Haptic
// =============================================================================

/**
 * Premium ripple effect that expands organically from touch point.
 */
@Composable
fun PremiumRippleEffect(
    modifier: Modifier = Modifier,
    color: Color = ProdyPrimary,
    onTap: (Offset) -> Unit = {}
) {
    var rippleCenter by remember { mutableStateOf<Offset?>(null) }
    var rippleProgress by remember { mutableStateOf(0f) }
    val scope = rememberCoroutineScope()

    val animatedProgress by animateFloatAsState(
        targetValue = rippleProgress,
        animationSpec = tween(400, easing = EaseOutCubic),
        label = "ripple_progress",
        finishedListener = {
            if (rippleProgress >= 1f) {
                rippleCenter = null
                rippleProgress = 0f
            }
        }
    )

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    rippleCenter = offset
                    rippleProgress = 1f
                    onTap(offset)
                }
            }
    ) {
        rippleCenter?.let { center ->
            Canvas(modifier = Modifier.fillMaxSize()) {
                val maxRadius = maxOf(size.width, size.height) * 1.5f
                val currentRadius = maxRadius * animatedProgress
                val alpha = (1f - animatedProgress) * 0.3f

                drawCircle(
                    color = color.copy(alpha = alpha),
                    radius = currentRadius,
                    center = center
                )
            }
        }
    }
}

// =============================================================================
// PARTICLE EFFECTS - Environmental Delight
// =============================================================================

/**
 * Floating particle system for ambient decoration.
 * Creates gentle, floating particles that drift upward.
 */
@Composable
fun FloatingParticles(
    modifier: Modifier = Modifier,
    particleCount: Int = 15,
    particleColor: Color = ProdyPrimary.copy(alpha = 0.3f)
) {
    val particles = remember {
        List(particleCount) {
            FloatingParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 4f + 2f,
                speed = Random.nextFloat() * 0.3f + 0.1f,
                swayAmount = Random.nextFloat() * 0.1f + 0.02f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "floating_particles")

    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particle_time"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val y = (particle.y - time * particle.speed).mod(1.2f) - 0.1f
            val sway = sin(y * PI * 4 + particle.x * PI * 2).toFloat() * particle.swayAmount
            val x = (particle.x + sway).coerceIn(0f, 1f)

            val alpha = when {
                y < 0f -> 0f
                y < 0.1f -> y * 10f
                y > 0.9f -> (1f - y) * 10f
                else -> 1f
            }

            drawCircle(
                color = particleColor.copy(alpha = particleColor.alpha * alpha),
                radius = particle.size,
                center = Offset(x * size.width, y * size.height)
            )
        }
    }
}

private data class FloatingParticle(
    val x: Float,
    val y: Float,
    val size: Float,
    val speed: Float,
    val swayAmount: Float
)

// =============================================================================
// LEVEL UP CELEBRATION - XP Milestone Effect
// =============================================================================

/**
 * State holder for level up celebration
 */
class LevelUpCelebrationState {
    var isPlaying by mutableStateOf(false)
        private set

    var newLevel by mutableStateOf(1)
        private set

    fun trigger(level: Int) {
        newLevel = level
        isPlaying = true
    }

    fun stop() {
        isPlaying = false
    }
}

@Composable
fun rememberLevelUpCelebrationState(): LevelUpCelebrationState {
    return remember { LevelUpCelebrationState() }
}

/**
 * Premium level up celebration with golden burst and level display.
 *
 * @param state LevelUpCelebrationState controlling the animation
 * @param onComplete Callback when celebration finishes
 */
@Composable
fun LevelUpCelebration(
    state: LevelUpCelebrationState,
    onComplete: () -> Unit = {}
) {
    if (!state.isPlaying) return

    var phase by remember { mutableStateOf(0) }

    LaunchedEffect(state.isPlaying) {
        if (state.isPlaying) {
            phase = 1  // Build up
            delay(300)
            phase = 2  // Burst
            delay(600)
            phase = 3  // Show level
            delay(1500)
            phase = 4  // Fade out
            delay(500)
            state.stop()
            onComplete()
        }
    }

    val overlayAlpha by animateFloatAsState(
        targetValue = when (phase) {
            1, 2 -> 0.8f
            3 -> 0.6f
            4 -> 0f
            else -> 0f
        },
        animationSpec = tween(300),
        label = "level_overlay"
    )

    val burstScale by animateFloatAsState(
        targetValue = when (phase) {
            1 -> 0.5f
            2, 3 -> 1.5f
            4 -> 2f
            else -> 0f
        },
        animationSpec = when (phase) {
            2 -> spring(dampingRatio = 0.5f, stiffness = 200f)
            else -> tween(400)
        },
        label = "burst_scale"
    )

    val levelScale by animateFloatAsState(
        targetValue = when (phase) {
            3 -> 1f
            else -> 0f
        },
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f),
        label = "level_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = overlayAlpha)),
        contentAlignment = Alignment.Center
    ) {
        // Golden burst
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val maxRadius = minOf(size.width, size.height) * 0.4f

            // Central golden glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = (1f - (phase - 2).coerceIn(0, 2) / 2f) * 0.9f),
                        LevelUpGlow.copy(alpha = (1f - (phase - 2).coerceIn(0, 2) / 2f) * 0.7f),
                        XpBarFill.copy(alpha = (1f - (phase - 2).coerceIn(0, 2) / 2f) * 0.4f),
                        Color.Transparent
                    ),
                    center = Offset(centerX, centerY),
                    radius = maxRadius * burstScale
                ),
                radius = maxRadius * burstScale,
                center = Offset(centerX, centerY)
            )

            // Expanding ring particles
            if (phase >= 2) {
                repeat(16) { i ->
                    val angle = (i * 22.5f) * (PI / 180f)
                    val distance = maxRadius * 0.8f * burstScale
                    val px = centerX + distance * cos(angle).toFloat()
                    val py = centerY + distance * sin(angle).toFloat()

                    drawCircle(
                        color = LevelUpGlow.copy(alpha = 0.8f * (4 - phase.coerceIn(2, 4)) / 2f),
                        radius = 6f,
                        center = Offset(px, py)
                    )
                }
            }
        }

        // Level text
        if (phase >= 3) {
            Column(
                modifier = Modifier.scale(levelScale),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "LEVEL UP!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = LevelUpGlow
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${state.newLevel}",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

// =============================================================================
// UTILITY FUNCTIONS
// =============================================================================

/**
 * Custom easing that mimics natural breathing rhythm
 */
private val EaseInOutSine = CubicBezierEasing(0.37f, 0f, 0.63f, 1f)

/**
 * Custom easing for organic motion
 */
private val EaseOutCubic = CubicBezierEasing(0.33f, 1f, 0.68f, 1f)
