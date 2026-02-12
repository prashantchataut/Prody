package com.prody.prashant.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prody.prashant.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.*
import kotlin.random.Random

/**
 * Prody Magical Effects Library
 *
 * A premium collection of ethereal, magical UI/UX effects designed to make Prody
 * feel like a living, evolving companion. These effects create moments of delight
 * and reinforce the app's mindfulness journey theme.
 *
 * Design Philosophy:
 * - Subtlety is key: Effects should be felt, not overtly seen
 * - Performance first: All animations target 60fps+
 * - Organic feel: Animations mimic natural, breathing movements
 * - Purposeful: Each effect serves to enhance user engagement
 */

// =============================================================================
// AMBIENT BACKGROUND EFFECTS - Dynamic Thematic Adaptation
// =============================================================================

/**
 * Time-of-day aware ambient background with subtle, organic animations.
 * Creates a living, breathing backdrop that responds to context.
 *
 * @param modifier Modifier for the ambient layer
 * @param timeOfDay Current time context (Morning, Day, Evening, Night)
 * @param mood Optional user's dominant mood to influence ambiance
 * @param intensity How prominent the effect should be (0.0 to 1.0)
 */
@Composable
fun AmbientBackground(
    modifier: Modifier = Modifier,
    timeOfDay: EffectTimeOfDay = EffectTimeOfDay.Day,
    mood: AmbientMood? = null,
    intensity: Float = 0.3f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ambient_bg")

    // Slow, organic breathing animation
    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing"
    )

    // Gentle rotation for organic feel
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(60000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Shifting ambient glow position
    val glowOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_offset"
    )

    val ambientColors = remember(timeOfDay, mood) {
        getAmbientColors(timeOfDay, mood)
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val maxRadius = maxOf(size.width, size.height) * 0.8f

        // Primary ambient glow
        val glowX = centerX + (glowOffset - 0.5f) * size.width * 0.3f
        val glowY = centerY + sin(glowOffset * PI).toFloat() * size.height * 0.1f

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    ambientColors.primary.copy(alpha = intensity * 0.15f * breathingScale),
                    ambientColors.primary.copy(alpha = intensity * 0.05f),
                    Color.Transparent
                ),
                center = Offset(glowX, glowY),
                radius = maxRadius * breathingScale
            ),
            radius = maxRadius * breathingScale,
            center = Offset(glowX, glowY)
        )

        // Secondary ambient accent
        rotate(rotation, pivot = Offset(centerX, centerY)) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        ambientColors.secondary.copy(alpha = intensity * 0.08f),
                        Color.Transparent
                    ),
                    center = Offset(centerX + maxRadius * 0.3f, centerY - maxRadius * 0.2f),
                    radius = maxRadius * 0.5f
                ),
                radius = maxRadius * 0.5f,
                center = Offset(centerX + maxRadius * 0.3f, centerY - maxRadius * 0.2f)
            )
        }
    }
}

/**
 * Time of day for visual effects.
 *
 * Renamed from TimeOfDay to avoid collision with
 * com.prody.prashant.domain.model.TimeOfDay.
 */
enum class EffectTimeOfDay {
    Morning,  // 5am - 12pm: Soft rising light
    Day,      // 12pm - 5pm: Bright, energetic
    Evening,  // 5pm - 9pm: Warm, deep shimmer
    Night     // 9pm - 5am: Calm, serene
}

enum class AmbientMood {
    Calm,       // Serene, slow pulse
    Happy,      // Energized shimmer
    Reflective, // Deep, contemplative
    Motivated,  // Dynamic, vibrant
    Grateful    // Warm, glowing
}

private data class AmbientColors(
    val primary: Color,
    val secondary: Color
)

private fun getAmbientColors(timeOfDay: EffectTimeOfDay, mood: AmbientMood?): AmbientColors {
    // Base colors from time of day
    val baseColors = when (timeOfDay) {
        EffectTimeOfDay.Morning -> AmbientColors(
            primary = Color(0xFFFFE4B5),  // Soft sunrise gold
            secondary = Color(0xFFFFB6C1) // Gentle pink
        )
        EffectTimeOfDay.Day -> AmbientColors(
            primary = ProdyPrimary,
            secondary = ProdyTertiary
        )
        EffectTimeOfDay.Evening -> AmbientColors(
            primary = Color(0xFFE57373),  // Warm sunset
            secondary = Color(0xFFFFB74D) // Amber glow
        )
        EffectTimeOfDay.Night -> AmbientColors(
            primary = Color(0xFF5C6BC0),  // Deep indigo
            secondary = Color(0xFF7986CB) // Soft blue
        )
    }

    // Override with mood if provided
    return mood?.let {
        when (it) {
            AmbientMood.Calm -> AmbientColors(MoodCalm, Color(0xFF90CAF9))
            AmbientMood.Happy -> AmbientColors(MoodHappy, MoodExcited)
            AmbientMood.Reflective -> AmbientColors(MoodConfused, WisdomPerspective)
            AmbientMood.Motivated -> AmbientColors(MoodMotivated, MoodEnergetic)
            AmbientMood.Grateful -> AmbientColors(MoodGrateful, ProdyPrimary)
        }
    } ?: baseColors
}

// =============================================================================
// MOOD-DRIVEN BREATHING HALO - Avatar/Level Indicator Effect
// =============================================================================

/**
 * A breathing halo effect that surrounds an element and pulses based on mood.
 * Perfect for avatar frames or level indicators.
 *
 * @param modifier Modifier for the halo container
 * @param mood The mood driving the visual behavior
 * @param size Size of the halo area
 * @param content The content to wrap with the halo
 */
@Composable
fun MoodBreathingHalo(
    modifier: Modifier = Modifier,
    mood: AmbientMood = AmbientMood.Calm,
    size: Dp = 80.dp,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mood_halo")

    // Breathing speed varies by mood
    val breathingDuration = when (mood) {
        AmbientMood.Calm -> 3000
        AmbientMood.Happy -> 1500
        AmbientMood.Reflective -> 4000
        AmbientMood.Motivated -> 1200
        AmbientMood.Grateful -> 2500
    }

    val haloAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(breathingDuration, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "halo_alpha"
    )

    val haloScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(breathingDuration, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "halo_scale"
    )

    val haloColor = when (mood) {
        AmbientMood.Calm -> MoodCalm
        AmbientMood.Happy -> MoodHappy
        AmbientMood.Reflective -> MoodConfused
        AmbientMood.Motivated -> MoodMotivated
        AmbientMood.Grateful -> MoodGrateful
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Outer glow halo
        // Performance Optimization: Use graphicsLayer for both scale and alpha
        Box(
            modifier = Modifier
                .size(size)
                .graphicsLayer {
                    scaleX = haloScale
                    scaleY = haloScale
                    alpha = haloAlpha
                }
                .blur(12.dp)
                .background(haloColor, CircleShape)
        )

        // Inner subtle glow
        Box(
            modifier = Modifier
                .size(size * 0.9f)
                .graphicsLayer {
                    alpha = haloAlpha * 0.5f
                }
                .blur(6.dp)
                .background(haloColor.copy(alpha = 0.3f), CircleShape)
        )

        // Content
        content()
    }
}

// =============================================================================
// XP ENERGY PARTICLE FLOW - Gamification Delight
// =============================================================================

/**
 * State holder for XP particle flow animation
 */
class XpParticleFlowState {
    var isActive by mutableStateOf(false)
        private set

    var particleCount by mutableStateOf(0)
        private set

    var sourcePosition by mutableStateOf(Offset.Zero)
        private set

    var targetPosition by mutableStateOf(Offset.Zero)
        private set

    fun trigger(source: Offset, target: Offset, count: Int = 8) {
        sourcePosition = source
        targetPosition = target
        particleCount = count
        isActive = true
    }

    fun stop() {
        isActive = false
    }
}

@Composable
fun rememberXpParticleFlowState(): XpParticleFlowState {
    return remember { XpParticleFlowState() }
}

/**
 * XP Energy Particle Flow animation.
 * When triggered, vibrant green energy particles flow from source to target.
 *
 * @param state XpParticleFlowState controlling the animation
 * @param onComplete Callback when particles reach destination
 */
@Composable
fun XpParticleFlow(
    state: XpParticleFlowState,
    onComplete: () -> Unit = {}
) {
    if (!state.isActive) return

    val particles = remember(state.sourcePosition, state.targetPosition, state.particleCount) {
        List(state.particleCount) { index ->
            XpParticle(
                delay = index * 80,
                offset = Random.nextFloat() * 0.3f - 0.15f,
                size = Random.nextFloat() * 6f + 4f,
                speed = Random.nextFloat() * 0.2f + 0.9f
            )
        }
    }

    var allComplete by remember { mutableStateOf(false) }

    LaunchedEffect(state.isActive) {
        if (state.isActive) {
            delay(1500L)
            allComplete = true
            state.stop()
            onComplete()
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        if (!state.isActive) return@Canvas

        val source = state.sourcePosition
        val target = state.targetPosition
        val direction = target - source
        val distance = sqrt(direction.x.pow(2) + direction.y.pow(2))

        particles.forEach { particle ->
            // Calculate particle position along curved path
            val progress = ((System.currentTimeMillis() % 1500).toFloat() / 1500f * particle.speed)
                .coerceIn(0f, 1f)

            if (progress > 0f && progress < 1f) {
                // Bezier curve for organic flow
                val controlOffset = Offset(
                    direction.y * particle.offset * 0.5f,
                    -direction.x * particle.offset * 0.5f
                )
                val control = Offset(
                    source.x + direction.x * 0.5f + controlOffset.x,
                    source.y + direction.y * 0.5f + controlOffset.y
                )

                val t = progress
                val u = 1 - t
                val position = Offset(
                    u * u * source.x + 2 * u * t * control.x + t * t * target.x,
                    u * u * source.y + 2 * u * t * control.y + t * t * target.y
                )

                // Particle glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            XpBarFill.copy(alpha = 0.8f * (1f - progress)),
                            XpBarGlow.copy(alpha = 0.4f * (1f - progress)),
                            Color.Transparent
                        ),
                        center = position,
                        radius = particle.size * 3
                    ),
                    radius = particle.size * 3,
                    center = position
                )

                // Core particle
                drawCircle(
                    color = XpBarFill.copy(alpha = 1f - progress * 0.5f),
                    radius = particle.size,
                    center = position
                )
            }
        }
    }
}

private data class XpParticle(
    val delay: Int,
    val offset: Float,
    val size: Float,
    val speed: Float
)

// =============================================================================
// STREAK FLAME ANIMATION - Gamification Visual Momentum
// =============================================================================

/**
 * Animated streak flame that grows more intense with longer streaks.
 * The flame "breathes" and flickers organically.
 *
 * @param modifier Modifier for the flame container
 * @param streakDays Current streak count
 * @param size Size of the flame area
 */
@Composable
fun StreakFlame(
    modifier: Modifier = Modifier,
    streakDays: Int,
    size: Dp = 32.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "streak_flame")

    // Intensity based on streak length
    val intensity = when {
        streakDays >= 365 -> 1.0f  // Inferno
        streakDays >= 90 -> 0.85f  // Blazing
        streakDays >= 30 -> 0.7f   // Hot
        streakDays >= 7 -> 0.55f   // Warm
        else -> 0.4f               // Cold/Starting
    }

    // Flame colors based on intensity
    val flameColors = when {
        streakDays >= 365 -> listOf(StreakInferno, Color(0xFFFF00FF), Color(0xFFFFD700))
        streakDays >= 90 -> listOf(StreakBlazing, Color(0xFFFF4500), Color(0xFFFFD700))
        streakDays >= 30 -> listOf(StreakHot, StreakFire, Color(0xFFFFD700))
        streakDays >= 7 -> listOf(StreakWarm, Color(0xFFFFB74D), Color(0xFFFFF59D))
        else -> listOf(StreakCold, Color(0xFF64B5F6), Color(0xFFE3F2FD))
    }

    // Primary breathing animation
    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween((600 / intensity).toInt(), easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame_breathing"
    )

    // Flickering animation
    val flickerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween((200 / intensity).toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame_flicker"
    )

    // Swaying animation
    val sway by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween((800 / intensity).toInt(), easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flame_sway"
    )

    val density = LocalDensity.current
    val sizePx = with(density) { size.toPx() }

    Canvas(modifier = modifier.size(size)) {
        val centerX = this.size.width / 2
        val baseY = this.size.height * 0.9f

        // Outer glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    flameColors[0].copy(alpha = 0.4f * intensity * flickerAlpha),
                    Color.Transparent
                ),
                center = Offset(centerX, baseY - sizePx * 0.3f),
                radius = sizePx * 0.8f * breathingScale
            ),
            radius = sizePx * 0.8f * breathingScale,
            center = Offset(centerX, baseY - sizePx * 0.3f)
        )

        // Main flame body
        val flamePath = Path().apply {
            moveTo(centerX - sizePx * 0.2f, baseY)

            // Left curve
            cubicTo(
                centerX - sizePx * 0.25f, baseY - sizePx * 0.4f * breathingScale,
                centerX - sizePx * 0.1f + sway, baseY - sizePx * 0.7f * breathingScale,
                centerX + sway * 0.5f, baseY - sizePx * 0.9f * breathingScale
            )

            // Right curve
            cubicTo(
                centerX + sizePx * 0.1f + sway, baseY - sizePx * 0.7f * breathingScale,
                centerX + sizePx * 0.25f, baseY - sizePx * 0.4f * breathingScale,
                centerX + sizePx * 0.2f, baseY
            )

            close()
        }

        drawPath(
            path = flamePath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    flameColors[2].copy(alpha = flickerAlpha),
                    flameColors[1].copy(alpha = flickerAlpha * 0.9f),
                    flameColors[0].copy(alpha = flickerAlpha * 0.8f)
                ),
                startY = baseY - sizePx * 0.9f * breathingScale,
                endY = baseY
            )
        )

        // Inner bright core
        val corePath = Path().apply {
            moveTo(centerX - sizePx * 0.08f, baseY)

            cubicTo(
                centerX - sizePx * 0.1f, baseY - sizePx * 0.25f * breathingScale,
                centerX - sizePx * 0.05f + sway * 0.3f, baseY - sizePx * 0.45f * breathingScale,
                centerX + sway * 0.2f, baseY - sizePx * 0.55f * breathingScale
            )

            cubicTo(
                centerX + sizePx * 0.05f + sway * 0.3f, baseY - sizePx * 0.45f * breathingScale,
                centerX + sizePx * 0.1f, baseY - sizePx * 0.25f * breathingScale,
                centerX + sizePx * 0.08f, baseY
            )

            close()
        }

        drawPath(
            path = corePath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = flickerAlpha * 0.9f),
                    flameColors[2].copy(alpha = flickerAlpha)
                ),
                startY = baseY - sizePx * 0.55f * breathingScale,
                endY = baseY
            )
        )
    }
}

// =============================================================================
// ACHIEVEMENT CELEBRATION - Premium Full-Screen Reveal
// =============================================================================

/**
 * State holder for achievement celebration
 */
class AchievementRevealState {
    var isPlaying by mutableStateOf(false)
        private set

    var achievementName by mutableStateOf("")
        private set

    var achievementRarity by mutableStateOf(EffectAchievementRarity.Common)
        private set

    fun trigger(name: String, rarity: EffectAchievementRarity = EffectAchievementRarity.Common) {
        achievementName = name
        achievementRarity = rarity
        isPlaying = true
    }

    fun stop() {
        isPlaying = false
    }
}

/**
 * Rarity levels for visual effects in achievement celebrations.
 *
 * Renamed from AchievementRarity to avoid collision with
 * com.prody.prashant.domain.gamification.AchievementRarity and
 * com.prody.prashant.ui.theme.UiAchievementRarity.
 */
enum class EffectAchievementRarity {
    Common, Uncommon, Rare, Epic, Legendary, Mythic
}

@Composable
fun rememberAchievementRevealState(): AchievementRevealState {
    return remember { AchievementRevealState() }
}

/**
 * Premium full-screen achievement celebration.
 * A brief, elegant visual sequence that celebrates mastery.
 *
 * @param state AchievementRevealState controlling the animation
 * @param onComplete Callback when celebration finishes
 */
@Composable
fun AchievementRevealCelebration(
    state: AchievementRevealState,
    onComplete: () -> Unit = {}
) {
    if (!state.isPlaying) return

    val rarityColors = when (state.achievementRarity) {
        EffectAchievementRarity.Common -> listOf(RarityCommon, Color(0xFF90A4AE))
        EffectAchievementRarity.Uncommon -> listOf(RarityUncommon, Color(0xFF81C784))
        EffectAchievementRarity.Rare -> listOf(RarityRare, Color(0xFF64B5F6))
        EffectAchievementRarity.Epic -> listOf(RarityEpic, Color(0xFFCE93D8))
        EffectAchievementRarity.Legendary -> listOf(RarityLegendary, Color(0xFFF4D03F))
        EffectAchievementRarity.Mythic -> listOf(RarityMythic, Color(0xFFFFF59D), Color(0xFFFFD700))
    }

    var phase by remember { mutableStateOf(0) }

    LaunchedEffect(state.isPlaying) {
        if (state.isPlaying) {
            phase = 1  // Build up
            delay(400)
            phase = 2  // Reveal burst
            delay(800)
            phase = 3  // Particles disperse
            delay(1200)
            phase = 4  // Fade out
            delay(600)
            state.stop()
            onComplete()
        }
    }

    // Background overlay
    val overlayAlpha by animateFloatAsState(
        targetValue = when (phase) {
            1, 2, 3 -> 0.7f
            4 -> 0f
            else -> 0f
        },
        animationSpec = tween(400, easing = EaseInOutCubic),
        label = "overlay_alpha"
    )

    // Central burst scale
    val burstScale by animateFloatAsState(
        targetValue = when (phase) {
            1 -> 0.3f
            2 -> 1.5f
            3, 4 -> 2f
            else -> 0f
        },
        animationSpec = when (phase) {
            2 -> spring(dampingRatio = 0.4f, stiffness = 200f)
            else -> tween(400, easing = EaseOutCubic)
        },
        label = "burst_scale"
    )

    // Central glow alpha
    val glowAlpha by animateFloatAsState(
        targetValue = when (phase) {
            1 -> 0.3f
            2 -> 1f
            3 -> 0.6f
            4 -> 0f
            else -> 0f
        },
        animationSpec = tween(300),
        label = "glow_alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = overlayAlpha)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val maxRadius = minOf(size.width, size.height) * 0.4f

            // Central radial burst
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = glowAlpha * 0.8f),
                        rarityColors.first().copy(alpha = glowAlpha * 0.6f),
                        rarityColors.getOrElse(1) { rarityColors.first() }.copy(alpha = glowAlpha * 0.3f),
                        Color.Transparent
                    ),
                    center = Offset(centerX, centerY),
                    radius = maxRadius * burstScale
                ),
                radius = maxRadius * burstScale,
                center = Offset(centerX, centerY)
            )

            // Particle ring (phase 2-3)
            if (phase >= 2) {
                repeat(24) { i ->
                    val angle = (i * 15f) * (PI / 180f)
                    val particleProgress = ((phase - 2f) / 2f).coerceIn(0f, 1f)
                    val distance = maxRadius * 0.5f + maxRadius * 0.8f * particleProgress
                    val particleX = centerX + distance * cos(angle).toFloat()
                    val particleY = centerY + distance * sin(angle).toFloat()
                    val particleAlpha = (1f - particleProgress) * glowAlpha

                    drawCircle(
                        color = rarityColors.first().copy(alpha = particleAlpha),
                        radius = 8f * (1f - particleProgress * 0.5f),
                        center = Offset(particleX, particleY)
                    )
                }
            }

            // Inner shimmering ring
            if (phase >= 2) {
                drawCircle(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = glowAlpha * 0.6f),
                            Color.Transparent,
                            rarityColors.first().copy(alpha = glowAlpha * 0.4f),
                            Color.Transparent
                        ),
                        center = Offset(centerX, centerY)
                    ),
                    radius = maxRadius * burstScale * 0.3f,
                    center = Offset(centerX, centerY),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
                )
            }
        }
    }
}

// =============================================================================
// TIME CAPSULE SEALING ANIMATION
// =============================================================================

/**
 * State holder for time capsule sealing animation
 */
class TimeCapsuleSealState {
    var isSealing by mutableStateOf(false)
        private set

    var sealProgress by mutableStateOf(0f)
        private set

    fun startSealing() {
        isSealing = true
        sealProgress = 0f
    }

    fun updateProgress(progress: Float) {
        sealProgress = progress
    }

    fun complete() {
        isSealing = false
        sealProgress = 1f
    }
}

@Composable
fun rememberTimeCapsuleSealState(): TimeCapsuleSealState {
    return remember { TimeCapsuleSealState() }
}

/**
 * Time Capsule "Sealing" Animation.
 * Message visually folds/compresses into a glowing icon that travels away.
 *
 * @param state TimeCapsuleSealState controlling the animation
 * @param onComplete Callback when sealing finishes
 */
@Composable
fun TimeCapsuleSealAnimation(
    state: TimeCapsuleSealState,
    onComplete: () -> Unit = {}
) {
    if (!state.isSealing) return

    var phase by remember { mutableStateOf(0) }

    LaunchedEffect(state.isSealing) {
        if (state.isSealing) {
            phase = 1  // Folding
            repeat(20) { i ->
                state.updateProgress(i / 20f)
                delay(30)
            }
            phase = 2  // Compressing to icon
            delay(500)
            phase = 3  // Glowing and traveling
            delay(800)
            state.complete()
            onComplete()
        }
    }

    val foldScale by animateFloatAsState(
        targetValue = when (phase) {
            1 -> 0.6f
            2, 3 -> 0.1f
            else -> 1f
        },
        animationSpec = tween(500, easing = EaseInOutCubic),
        label = "fold_scale"
    )

    val glowIntensity by animateFloatAsState(
        targetValue = when (phase) {
            2 -> 0.8f
            3 -> 1f
            else -> 0.2f
        },
        animationSpec = tween(300),
        label = "glow_intensity"
    )

    val travelOffsetY by animateFloatAsState(
        targetValue = when (phase) {
            3 -> -500f
            else -> 0f
        },
        animationSpec = tween(800, easing = EaseInCubic),
        label = "travel_offset"
    )

    val travelOffsetX by animateFloatAsState(
        targetValue = when (phase) {
            3 -> 200f
            else -> 0f
        },
        animationSpec = tween(800, easing = EaseInOutCubic),
        label = "travel_offset_x"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(100.dp)
                .scale(foldScale)
                .graphicsLayer {
                    translationY = travelOffsetY
                    translationX = travelOffsetX
                }
        ) {
            val centerX = size.width / 2
            val centerY = size.height / 2

            // Glowing envelope/capsule shape
            drawRoundRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = glowIntensity),
                        TimeCapsuleAccent.copy(alpha = glowIntensity * 0.8f),
                        TimeCapsuleAccent.copy(alpha = glowIntensity * 0.4f),
                        Color.Transparent
                    ),
                    center = Offset(centerX, centerY),
                    radius = size.minDimension
                ),
                size = size,
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(size.minDimension * 0.2f)
            )

            // Inner bright core
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = glowIntensity),
                        TimeCapsuleAccent.copy(alpha = glowIntensity * 0.6f),
                        Color.Transparent
                    ),
                    center = Offset(centerX, centerY),
                    radius = size.minDimension * 0.3f
                ),
                radius = size.minDimension * 0.3f,
                center = Offset(centerX, centerY)
            )
        }
    }
}

/**
 * Time Capsule "Unsealing" Animation for delivered messages.
 *
 * @param isOpen Whether the message is being opened
 * @param onComplete Callback when unsealing finishes
 * @param content The revealed message content
 */
@Composable
fun TimeCapsuleUnsealAnimation(
    isOpen: Boolean,
    onComplete: () -> Unit = {},
    content: @Composable () -> Unit
) {
    var revealed by remember { mutableStateOf(false) }

    val revealProgress by animateFloatAsState(
        targetValue = if (isOpen) 1f else 0f,
        animationSpec = tween(800, easing = EaseOutCubic),
        label = "reveal_progress",
        finishedListener = { if (isOpen) revealed = true; onComplete() }
    )

    val glowAlpha by animateFloatAsState(
        targetValue = if (isOpen && !revealed) 0.8f else 0f,
        animationSpec = tween(400),
        label = "unseal_glow"
    )

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        // Glow effect during reveal
        if (glowAlpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .alpha(glowAlpha)
                    .blur(20.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                TimeCapsuleAccent.copy(alpha = 0.5f),
                                FutureMessageArrived.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
            )
        }

        // Content with reveal animation
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    scaleY = revealProgress
                    alpha = revealProgress
                    transformOrigin = TransformOrigin(0.5f, 0f)
                }
        ) {
            content()
        }
    }
}

// =============================================================================
// BOTTOM NAVIGATION BREATHING GLOW
// =============================================================================

/**
 * Breathing glow effect for bottom navigation active item.
 * Creates a subtle, alive feeling for the focused navigation element.
 *
 * @param modifier Modifier for the glow wrapper
 * @param isActive Whether this nav item is currently active
 * @param color The accent color for the glow
 * @param content The navigation item content
 */
@Composable
fun NavigationBreathingGlow(
    modifier: Modifier = Modifier,
    isActive: Boolean,
    color: Color = ProdyPrimary,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "nav_breathing")

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "nav_glow_alpha"
    )

    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "nav_glow_scale"
    )

    val activeAlpha by animateFloatAsState(
        targetValue = if (isActive) 1f else 0f,
        animationSpec = tween(300),
        label = "active_alpha"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Breathing glow behind active item
        // Performance Optimization: Use graphicsLayer to avoid recomposition during animation
        Box(
            modifier = Modifier
                .size(48.dp)
                .graphicsLayer {
                    val a = activeAlpha
                    if (a > 0f) {
                        alpha = glowAlpha * a
                        scaleX = glowScale
                        scaleY = glowScale
                    } else {
                        alpha = 0f
                    }
                }
                .blur(12.dp)
                .background(color, CircleShape)
        )

        content()
    }
}

// =============================================================================
// BUDDHA WISDOM REVEAL ANIMATION
// =============================================================================

/**
 * Elegant wisdom text reveal animation.
 * Text unfurls with a thoughtful, contemplative feeling.
 *
 * @param text The wisdom text to reveal
 * @param isVisible Whether to show the text
 * @param modifier Modifier for the text container
 * @param onRevealComplete Callback when reveal animation completes
 */
@Composable
fun WisdomTextReveal(
    text: String,
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    moodTint: Color? = null,
    onRevealComplete: () -> Unit = {}
) {
    val revealProgress by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(1200, easing = EaseOutCubic),
        label = "wisdom_reveal",
        finishedListener = { if (isVisible) onRevealComplete() }
    )

    val infiniteTransition = rememberInfiniteTransition(label = "wisdom_ambient")

    val ambientGlow by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ambient_glow"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Subtle mood-tinted background glow
        moodTint?.let { tint ->
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer {
                        alpha = ambientGlow * revealProgress
                    }
                    .blur(30.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                tint.copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
            )
        }

        // Text with character-by-character reveal effect simulation
        // Performance Optimization: Use graphicsLayer for both alpha and scale
        androidx.compose.material3.Text(
            text = text,
            modifier = Modifier
                .graphicsLayer {
                    alpha = revealProgress
                    // Subtle vertical unfolding effect
                    scaleY = 0.9f + (revealProgress * 0.1f)
                    transformOrigin = TransformOrigin(0.5f, 0f)
                },
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = revealProgress)
            )
        )
    }
}

// =============================================================================
// HELPER COMPOSABLES & UTILITIES
// =============================================================================

/**
 * Smooth easing function mimicking natural breathing
 */
private val EaseInOutSine = CubicBezierEasing(0.37f, 0f, 0.63f, 1f)

/**
 * Calculate current time of day for ambient effects
 */
fun getCurrentTimeOfDay(): EffectTimeOfDay {
    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    return when {
        hour in 5..11 -> EffectTimeOfDay.Morning
        hour in 12..16 -> EffectTimeOfDay.Day
        hour in 17..20 -> EffectTimeOfDay.Evening
        else -> EffectTimeOfDay.Night
    }
}

/**
 * Map mood string to AmbientMood enum
 */
fun mapMoodToAmbient(moodName: String): AmbientMood? {
    return when (moodName.lowercase()) {
        "calm", "peaceful", "serene" -> AmbientMood.Calm
        "happy", "joyful", "excited" -> AmbientMood.Happy
        "reflective", "thoughtful", "contemplative" -> AmbientMood.Reflective
        "motivated", "energetic", "determined" -> AmbientMood.Motivated
        "grateful", "thankful", "appreciative" -> AmbientMood.Grateful
        else -> null
    }
}
