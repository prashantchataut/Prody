package com.prody.prashant.ui.screens.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.ui.icons.ProdyIcons
import com.prody.prashant.ui.theme.ProdyAccentGreen

/**
 * Performance Optimization: Immutable data class ensures Compose compiler
 * can skip recomposition when parameters haven't changed.
 */
@Immutable
private data class HavenFeature(
    val icon: ImageVector,
    val title: String,
    val description: String
)

@Composable
fun HavenOnboardingScreen(
    onNext: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    // Micro-UX: Staggered entrance animation
    LaunchedEffect(Unit) {
        visible = true
    }

    // Performance Optimization: Memoize brush and list to prevent allocation during recomposition
    val heroBrush = remember {
        Brush.linearGradient(
            colors = listOf(
                ProdyAccentGreen.copy(alpha = 0.2f),
                ProdyAccentGreen.copy(alpha = 0.05f)
            )
        )
    }

    val features = remember {
        listOf(
            HavenFeature(
                icon = ProdyIcons.Lock,
                title = "Private & Secure",
                description = "Your conversations are encrypted and private."
            ),
            HavenFeature(
                icon = ProdyIcons.Favorite,
                title = "Always Here",
                description = "Available 24/7 whenever you need a listening ear."
            ),
            HavenFeature(
                icon = ProdyIcons.SelfImprovement,
                title = "Growth Focused",
                description = "Tools and exercises to help you thrive."
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
            visible = visible,
            enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { -40 }
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(heroBrush),
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

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(600, 100)) + slideInVertically(tween(600, 100)) { 20 }
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
            visible = visible,
            enter = fadeIn(tween(600, 200)) + slideInVertically(tween(600, 200)) { 20 }
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

        // Feature list
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            features.forEachIndexed { index, feature ->
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(600, 400 + (index * 100))) +
                            slideInVertically(tween(600, 400 + (index * 100))) { 20 }
                ) {
                    OnboardingFeatureItem(
                        icon = feature.icon,
                        title = feature.title,
                        description = feature.description
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
