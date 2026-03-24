package com.prody.prashant.ui.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.ui.icons.ProdyIcons
import com.prody.prashant.ui.theme.ProdyAccentGreen

private val EaseOutQuart = CubicBezierEasing(0.25f, 1f, 0.5f, 1f)

@Composable
fun HavenOnboardingScreen(
    onNext: () -> Unit
) {
    // Staggered entrance animation state
    val visible = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible.value = true }

    val iconBackgroundBrush = remember {
        Brush.linearGradient(
            colors = listOf(
                ProdyAccentGreen.copy(alpha = 0.2f),
                ProdyAccentGreen.copy(alpha = 0.05f)
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon / Hero Image
        AnimatedVisibility(
            visible = visible.value,
            enter = fadeIn(tween(600)) + scaleIn(tween(600, easing = EaseOutQuart), initialScale = 0.8f)
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(iconBackgroundBrush),
                contentAlignment = Alignment.Center
            ) {
            Icon(
                imageVector = ProdyIcons.Psychology, // Or a similar relevant icon
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = ProdyAccentGreen
            )
        }

        }

        Spacer(modifier = Modifier.height(32.dp))

        AnimatedVisibility(
            visible = visible.value,
            enter = fadeIn(tween(600, delayMillis = 100)) + slideInVertically(tween(600, delayMillis = 100)) { it / 2 }
        ) {
            Text(
                text = "Meet Haven",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(
            visible = visible.value,
            enter = fadeIn(tween(600, delayMillis = 200)) + slideInVertically(tween(600, delayMillis = 200)) { it / 2 }
        ) {
            Text(
                text = "Your private, safe space for reflection and support.",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        AnimatedVisibility(
            visible = visible.value,
            enter = fadeIn(tween(600, delayMillis = 300)) + slideInVertically(tween(600, delayMillis = 300)) { it / 2 }
        ) {
            Text(
                text = "Haven is here to listen, support, and help you navigate life's challenges—without judgment.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Feature list
        val features = remember {
            listOf(
                Triple(ProdyIcons.Lock, "Private & Secure", "Your conversations are encrypted and private."),
                Triple(ProdyIcons.Favorite, "Always Here", "Available 24/7 whenever you need a listening ear."),
                Triple(ProdyIcons.SelfImprovement, "Growth Focused", "Tools and exercises to help you thrive.")
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            features.forEachIndexed { index, feature ->
                AnimatedVisibility(
                    visible = visible.value,
                    enter = fadeIn(tween(600, delayMillis = 400 + (index * 100))) + slideInHorizontally(tween(600, delayMillis = 400 + (index * 100))) { it / 4 }
                ) {
                    OnboardingFeatureItem(
                        icon = feature.first,
                        title = feature.second,
                        description = feature.third
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f)) // Push button to bottom
        
        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
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
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun OnboardingFeatureItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
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
