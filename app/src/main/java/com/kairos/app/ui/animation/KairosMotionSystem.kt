package com.kairos.app.ui.animation

import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext

/**
 * Motion primitives used by the focused Kairos surfaces.
 *
 * Motion is intentionally short, non-bouncy, and able to collapse to fades when
 * the system animator scale is disabled. The app never makes reading wait for an
 * animation to finish.
 */
object KairosEasing {
    val EaseOutQuart = CubicBezierEasing(0.25f, 1f, 0.5f, 1f)
    val EaseInQuart = CubicBezierEasing(0.5f, 0f, 0.75f, 0f)
    val EaseOutExpo = CubicBezierEasing(0.16f, 1f, 0.3f, 1f)
}

object KairosDurations {
    const val Press = 110
    const val Micro = 160
    const val State = 240
    const val Page = 320
    const val Ambient = 1_400
}

@Composable
fun rememberKairosReducedMotion(): Boolean {
    val context = LocalContext.current
    return remember(context) {
        runCatching {
            Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1f
            ) == 0f
        }.getOrDefault(false)
    }
}

@Composable
fun KairosReveal(
    visible: Boolean,
    modifier: Modifier = Modifier,
    delayMillis: Int = 0,
    offsetFraction: Int = 10,
    content: @Composable () -> Unit
) {
    val reducedMotion = rememberKairosReducedMotion()
    val enter: EnterTransition = if (reducedMotion) {
        fadeIn(tween(KairosDurations.Micro, delayMillis = delayMillis))
    } else {
        fadeIn(
            tween(
                durationMillis = KairosDurations.State,
                delayMillis = delayMillis,
                easing = KairosEasing.EaseOutQuart
            )
        ) + slideInVertically(
            initialOffsetY = { it / offsetFraction.coerceAtLeast(1) },
            animationSpec = tween(
                durationMillis = KairosDurations.Page,
                delayMillis = delayMillis,
                easing = KairosEasing.EaseOutExpo
            )
        )
    }
    val exit: ExitTransition = if (reducedMotion) {
        fadeOut(tween(KairosDurations.Micro))
    } else {
        fadeOut(tween(KairosDurations.Micro, easing = KairosEasing.EaseInQuart)) +
            slideOutVertically(
                targetOffsetY = { it / 20 },
                animationSpec = tween(KairosDurations.Micro, easing = KairosEasing.EaseInQuart)
            )
    }

    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = enter,
        exit = exit
    ) {
        content()
    }
}

@Composable
fun rememberKairosPressScale(
    interactionSource: InteractionSource,
    pressedScale: Float = 0.975f
): Float {
    val pressed by interactionSource.collectIsPressedAsState()
    val reducedMotion = rememberKairosReducedMotion()
    return animateFloatAsState(
        targetValue = if (pressed && !reducedMotion) pressedScale else 1f,
        animationSpec = tween(KairosDurations.Press, easing = KairosEasing.EaseOutQuart),
        label = "kairos-press-scale"
    ).value
}

fun Modifier.kairosScale(scale: Float): Modifier = graphicsLayer {
    scaleX = scale
    scaleY = scale
}

@Composable
fun rememberOneShotVisibility(initiallyVisible: Boolean = false): MutableTransitionState<Boolean> =
    remember {
        MutableTransitionState(initiallyVisible).apply { targetState = true }
    }
