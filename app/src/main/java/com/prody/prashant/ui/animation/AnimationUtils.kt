package com.prody.prashant.ui.animation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInCubic
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import com.prody.prashant.ui.theme.ProdyTokens
import kotlinx.coroutines.delay

/**
 * Prody Design System - Animation Utilities
 *
 * A comprehensive collection of animations designed for a premium,
 * calming user experience. All animations target 60fps performance.
 *
 * Design Philosophy:
 * - Smooth and natural feeling
 * - Not distracting or overwhelming
 * - Purposeful - animations should guide attention
 * - Accessible - respects reduced motion preferences
 */

// =============================================================================
// STANDARD ENTER/EXIT ANIMATIONS
// =============================================================================

/**
 * Standard animation transitions for Prody app.
 * Use these for consistent motion language across the app.
 */
object ProdyAnimations {

    // ----- Fade Transitions -----

    val fadeIn: EnterTransition = fadeIn(
        animationSpec = tween(ProdyTokens.Animation.normal)
    )

    val fadeOut: ExitTransition = fadeOut(
        animationSpec = tween(ProdyTokens.Animation.normal)
    )

    // ----- Slide From Bottom -----

    val slideInFromBottom: EnterTransition = slideInVertically(
        initialOffsetY = { it / 4 },
        animationSpec = tween(ProdyTokens.Animation.normal, easing = EaseOutCubic)
    ) + fadeIn(animationSpec = tween(ProdyTokens.Animation.normal))

    val slideOutToBottom: ExitTransition = slideOutVertically(
        targetOffsetY = { it / 4 },
        animationSpec = tween(ProdyTokens.Animation.normal, easing = EaseInCubic)
    ) + fadeOut(animationSpec = tween(ProdyTokens.Animation.normal))

    // ----- Slide From Top -----

    val slideInFromTop: EnterTransition = slideInVertically(
        initialOffsetY = { -it / 4 },
        animationSpec = tween(ProdyTokens.Animation.normal, easing = EaseOutCubic)
    ) + fadeIn(animationSpec = tween(ProdyTokens.Animation.normal))

    val slideOutToTop: ExitTransition = slideOutVertically(
        targetOffsetY = { -it / 4 },
        animationSpec = tween(ProdyTokens.Animation.normal, easing = EaseInCubic)
    ) + fadeOut(animationSpec = tween(ProdyTokens.Animation.normal))

    // ----- Slide From Right -----

    val slideInFromRight: EnterTransition = slideInHorizontally(
        initialOffsetX = { it },
        animationSpec = tween(ProdyTokens.Animation.normal, easing = EaseOutCubic)
    ) + fadeIn(animationSpec = tween(ProdyTokens.Animation.normal))

    val slideOutToLeft: ExitTransition = slideOutHorizontally(
        targetOffsetX = { -it },
        animationSpec = tween(ProdyTokens.Animation.normal, easing = EaseInCubic)
    ) + fadeOut(animationSpec = tween(ProdyTokens.Animation.normal))

    // ----- Slide From Left -----

    val slideInFromLeft: EnterTransition = slideInHorizontally(
        initialOffsetX = { -it },
        animationSpec = tween(ProdyTokens.Animation.normal, easing = EaseOutCubic)
    ) + fadeIn(animationSpec = tween(ProdyTokens.Animation.normal))

    val slideOutToRight: ExitTransition = slideOutHorizontally(
        targetOffsetX = { it },
        animationSpec = tween(ProdyTokens.Animation.normal, easing = EaseInCubic)
    ) + fadeOut(animationSpec = tween(ProdyTokens.Animation.normal))

    // ----- Scale Transitions -----

    val scaleIn: EnterTransition = scaleIn(
        initialScale = 0.9f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    ) + fadeIn(animationSpec = tween(ProdyTokens.Animation.normal))

    val scaleOut: ExitTransition = scaleOut(
        targetScale = 0.9f,
        animationSpec = tween(ProdyTokens.Animation.fast)
    ) + fadeOut(animationSpec = tween(ProdyTokens.Animation.fast))

    // ----- Expand/Collapse -----

    val expandIn: EnterTransition = scaleIn(
        initialScale = 0.8f,
        animationSpec = tween(ProdyTokens.Animation.normal, easing = EaseOutCubic)
    ) + fadeIn(animationSpec = tween(ProdyTokens.Animation.normal))

    val collapseOut: ExitTransition = scaleOut(
        targetScale = 0.8f,
        animationSpec = tween(ProdyTokens.Animation.fast, easing = EaseInCubic)
    ) + fadeOut(animationSpec = tween(ProdyTokens.Animation.fast))

    // ----- Pop Transition (for dialogs/modals) -----

    val popIn: EnterTransition = scaleIn(
        initialScale = 0.7f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        )
    ) + fadeIn(animationSpec = tween(ProdyTokens.Animation.fast))

    val popOut: ExitTransition = scaleOut(
        targetScale = 0.7f,
        animationSpec = tween(ProdyTokens.Animation.fast, easing = EaseInCubic)
    ) + fadeOut(animationSpec = tween(ProdyTokens.Animation.fast))
}

// =============================================================================
// STAGGERED ANIMATION UTILITIES
// =============================================================================

/**
 * Calculate delay for staggered list animations.
 *
 * @param index Index of the item in the list
 * @param baseDelay Base delay in milliseconds per item
 * @return Calculated delay in milliseconds
 */
fun staggeredDelay(index: Int, baseDelay: Int = ProdyTokens.Animation.listStagger): Int {
    return index * baseDelay
}

/**
 * Generate staggered animation states for a list of items.
 *
 * @param itemCount Number of items in the list
 * @param delayPerItem Delay between each item's animation
 * @return List of animation states (0f to 1f)
 */
@Composable
fun staggeredAnimationStates(
    itemCount: Int,
    delayPerItem: Int = ProdyTokens.Animation.listStagger
): List<State<Float>> {
    return (0 until itemCount).map { index ->
        val animatable = remember { Animatable(0f) }
        LaunchedEffect(Unit) {
            delay(index * delayPerItem.toLong())
            animatable.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        animatable.asState()
    }
}

// =============================================================================
// MODIFIER EXTENSIONS
// =============================================================================

/**
 * Modifier extension for shimmer loading effect.
 * Use on placeholder elements during loading states.
 */
fun Modifier.shimmerEffect(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer_alpha"
    )

    graphicsLayer { this.alpha = alpha }
}

/**
 * Modifier extension for breathing/pulsing effect.
 * Use sparingly for ambient, calming animations.
 *
 * @param minScale Minimum scale during animation
 * @param maxScale Maximum scale during animation
 * @param duration Full cycle duration in milliseconds
 */
fun Modifier.breathingEffect(
    minScale: Float = 0.98f,
    maxScale: Float = 1.02f,
    duration: Int = ProdyTokens.Animation.glowPulse
): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "breathing")
    val scale by transition.animateFloat(
        initialValue = minScale,
        targetValue = maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing_scale"
    )

    scale(scale)
}

/**
 * Modifier extension for gentle floating effect.
 * Creates a subtle vertical float animation.
 *
 * @param amplitude Maximum float distance in pixels
 * @param duration Full cycle duration in milliseconds
 */
fun Modifier.floatingEffect(
    amplitude: Float = 8f,
    duration: Int = 3000
): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "floating")
    val offset by transition.animateFloat(
        initialValue = 0f,
        targetValue = amplitude,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floating_offset"
    )

    graphicsLayer { translationY = offset }
}

/**
 * Modifier extension for rotation effect.
 * Creates continuous rotation animation.
 *
 * @param duration Full rotation duration in milliseconds
 * @param clockwise Direction of rotation
 */
fun Modifier.rotatingEffect(
    duration: Int = 2000,
    clockwise: Boolean = true
): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "rotating")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (clockwise) 360f else -360f,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    graphicsLayer { rotationZ = rotation }
}

/**
 * Modifier extension for fade pulse effect.
 * Creates alpha pulsing animation.
 *
 * @param minAlpha Minimum alpha value
 * @param maxAlpha Maximum alpha value
 * @param duration Full cycle duration in milliseconds
 */
fun Modifier.fadePulseEffect(
    minAlpha: Float = 0.6f,
    maxAlpha: Float = 1f,
    duration: Int = 1500
): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "fade_pulse")
    val alpha by transition.animateFloat(
        initialValue = minAlpha,
        targetValue = maxAlpha,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fade_pulse_alpha"
    )

    graphicsLayer { this.alpha = alpha }
}

// =============================================================================
// ANIMATION SPECS
// =============================================================================

/**
 * Pre-configured animation specs for common use cases.
 */
object ProdyAnimationSpecs {

    /**
     * Spring animation for bouncy, playful interactions.
     */
    val bouncy = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )

    /**
     * Spring animation for snappy, responsive interactions.
     */
    val snappy = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )

    /**
     * Spring animation for gentle, smooth transitions.
     */
    val gentle = spring<Float>(
        dampingRatio = Spring.DampingRatioHighBouncy,
        stiffness = Spring.StiffnessVeryLow
    )

    /**
     * Tween animation for micro-interactions.
     */
    val microInteraction = tween<Float>(
        durationMillis = ProdyTokens.Animation.fast,
        easing = EaseOutCubic
    )

    /**
     * Tween animation for standard transitions.
     */
    val standardTransition = tween<Float>(
        durationMillis = ProdyTokens.Animation.normal,
        easing = EaseInOutCubic
    )

    /**
     * Tween animation for emphasis/celebration.
     */
    val emphasis = tween<Float>(
        durationMillis = ProdyTokens.Animation.slow,
        easing = EaseOutCubic
    )
}

// =============================================================================
// CELEBRATION ANIMATION HELPERS
// =============================================================================

/**
 * Configuration for celebration animations.
 */
object CelebrationAnimations {

    /**
     * Duration for achievement unlock animation
     */
    const val achievementRevealDuration = ProdyTokens.Animation.achievementReveal

    /**
     * Duration for celebration burst effect
     */
    const val celebrationBurstDuration = ProdyTokens.Animation.celebrationBurst

    /**
     * Number of particles for confetti
     */
    const val confettiParticleCount = 50

    /**
     * Duration for streak milestone celebration
     */
    const val streakMilestoneDuration = 2000
}
