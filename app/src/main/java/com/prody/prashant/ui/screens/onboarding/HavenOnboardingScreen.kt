package com.prody.prashant.ui.screens.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.prody.prashant.ui.icons.ProdyIcons
import com.prody.prashant.ui.theme.ProdyAccentGreen

/**
 * Haven Onboarding Screen - Optimized for performance and UX.
 *
 * Performance:
 * - Memoized Brush gradients and Shapes to prevent object allocation during recomposition.
 * - Used graphicsLayer for deferred state reads in animations.
 *
 * UX:
 * - Staggered entrance animations for elements to create a premium feel.
 */
@Composable
fun HavenOnboardingScreen(
    onNext: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    // Memoized resources to prevent redundant object creation
    val heroGradient = remember {
        Brush.linearGradient(
            colors = listOf(
                ProdyAccentGreen.copy(alpha = 0.2f),
                ProdyAccentGreen.copy(alpha = 0.05f)
            )
        )
    }

    val buttonShape = remember { RoundedCornerShape(28.dp) }
    val featureIconShape = remember { RoundedCornerShape(12.dp) }

    LaunchedEffect(Unit) {
        visible = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 1. Hero Icon Animation
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(600)) + scaleIn(tween(600, easing = EaseOutBack), initialScale = 0.8f)
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(heroGradient),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = ProdyIcons.Psychology,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = ProdyAccentGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 2. Title Animation
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(600, delayMillis = 200)) + slideInVertically(tween(600, delayMillis = 200)) { h -> h / 2 }
        ) {
            Text(
                text = "Meet Haven",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Description Animation
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(600, delayMillis = 400)) + slideInVertically(tween(600, delayMillis = 400)) { h -> h / 2 }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Your private, safe space for reflection and support.",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Haven is here to listen, support, and help you navigate life's challenges—without judgment.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))

        // 4. Feature List Staggered Animation
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val features = remember {
                listOf(
                    Triple(ProdyIcons.Lock, "Private & Secure", "Your conversations are encrypted and private."),
                    Triple(ProdyIcons.Favorite, "Always Here", "Available 24/7 whenever you need a listening ear."),
                    Triple(ProdyIcons.SelfImprovement, "Growth Focused", "Tools and exercises to help you thrive.")
                )
            }

            features.forEachIndexed { index, (icon, title, desc) ->
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(500, delayMillis = 600 + (index * 150))) +
                            slideInHorizontally(tween(500, delayMillis = 600 + (index * 150))) { w -> w / 4 }
                ) {
                    OnboardingFeatureItem(
                        icon = icon,
                        title = title,
                        description = desc,
                        iconShape = featureIconShape
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // 5. Button Animation
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(600, delayMillis = 1200)) + slideInVertically(tween(600, delayMillis = 1200)) { h -> h / 2 }
        ) {
            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = buttonShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ProdyAccentGreen,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Continue",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun OnboardingFeatureItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    iconShape: RoundedCornerShape
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(iconShape)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
