package com.prody.prashant.ui.screens.meditation
import com.prody.prashant.ui.icons.ProdyIcons

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.R
import com.prody.prashant.ui.components.ProdyCard
import com.prody.prashant.ui.theme.*

@Composable
fun MeditationTimerScreen(
    onNavigateBack: () -> Unit,
    viewModel: MeditationTimerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Breathing animation
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val breathingScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meditation") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = ProdyIcons.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MoodCalm.copy(alpha = 0.05f),
                            ProdyPrimary.copy(alpha = 0.08f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.padding(24.dp)
            ) {
                // Opening wisdom or completion message
                AnimatedVisibility(
                    visible = !uiState.isRunning || uiState.isCompleted,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    WisdomCard(
                        wisdom = if (uiState.isCompleted) {
                            uiState.closingWisdom ?: "Session complete"
                        } else {
                            uiState.openingWisdom ?: "Prepare to be present"
                        },
                        isCompletion = uiState.isCompleted
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Timer circle
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(260.dp)
                        .scale(if (uiState.isRunning && !uiState.isPaused) breathingScale else 1f)
                ) {
                    // Outer glow ring
                    if (uiState.isRunning && !uiState.isPaused) {
                        Box(
                            modifier = Modifier
                                .size(260.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            MoodCalm.copy(alpha = 0.3f),
                                            MoodCalm.copy(alpha = 0.1f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )
                    }

                    // Main circle
                    Box(
                        modifier = Modifier
                            .size(220.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        MoodCalm.copy(alpha = 0.15f),
                                        ProdyPrimary.copy(alpha = 0.1f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Timer display
                            Text(
                                text = viewModel.formatTime(uiState.remainingTime),
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontSize = 56.sp,
                                    fontWeight = FontWeight.Light,
                                    letterSpacing = 4.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            if (uiState.isRunning) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (uiState.isPaused) "Paused" else "Breathe...",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontStyle = FontStyle.Italic
                                )
                            }
                        }
                    }
                }

                // Duration selector (only when not running)
                AnimatedVisibility(
                    visible = !uiState.isRunning,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    DurationSelector(
                        selectedDuration = uiState.selectedDuration,
                        showPicker = uiState.showDurationPicker,
                        onTogglePicker = { viewModel.toggleDurationPicker() },
                        onDurationSelected = { viewModel.setDuration(it) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Control buttons
                ControlButtons(
                    isRunning = uiState.isRunning,
                    isPaused = uiState.isPaused,
                    isCompleted = uiState.isCompleted,
                    onStart = { viewModel.startMeditation() },
                    onPause = { viewModel.pauseMeditation() },
                    onStop = { viewModel.stopMeditation() },
                    onReset = { viewModel.resetSession() }
                )

                // Stats (when not running)
                AnimatedVisibility(
                    visible = !uiState.isRunning && uiState.sessionsCompleted > 0,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    SessionStats(
                        sessionsCompleted = uiState.sessionsCompleted,
                        totalMinutes = (uiState.totalMeditationTime / 60).toInt()
                    )
                }
            }
        }
    }
}

@Composable
private fun WisdomCard(
    wisdom: String,
    isCompletion: Boolean
) {
    ProdyCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = if (isCompletion) {
            AchievementUnlocked.copy(alpha = 0.1f)
        } else {
            MoodCalm.copy(alpha = 0.1f)
        }
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = if (isCompletion) ProdyIcons.CheckCircle else ProdyIcons.SelfImprovement,
                contentDescription = null,
                tint = if (isCompletion) AchievementUnlocked else MoodCalm,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isCompletion) "Well Done" else "Today's Wisdom",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = if (isCompletion) AchievementUnlocked else MoodCalm
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "\"$wisdom\"",
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
private fun DurationSelector(
    selectedDuration: Int,
    showPicker: Boolean,
    onTogglePicker: () -> Unit,
    onDurationSelected: (Int) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Current selection button
        Surface(
            shape = CardShape,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.clickable(onClick = onTogglePicker)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = ProdyIcons.Timer,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "$selectedDuration min",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = if (showPicker) ProdyIcons.ExpandLess else ProdyIcons.ExpandMore,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Duration options
        AnimatedVisibility(
            visible = showPicker,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            LazyRow(
                modifier = Modifier.padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(MeditationTimerViewModel.DURATION_OPTIONS) { duration ->
                    FilterChip(
                        selected = duration == selectedDuration,
                        onClick = { onDurationSelected(duration) },
                        label = { Text("$duration min") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MoodCalm.copy(alpha = 0.2f),
                            selectedLabelColor = MoodCalm
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun ControlButtons(
    isRunning: Boolean,
    isPaused: Boolean,
    isCompleted: Boolean,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit,
    onReset: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isCompleted) {
            // Reset button after completion
            Button(
                onClick = onReset,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MoodCalm
                ),
                modifier = Modifier.height(56.dp)
            ) {
                Icon(
                    imageVector = ProdyIcons.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("New Session", fontWeight = FontWeight.SemiBold)
            }
        } else if (isRunning) {
            // Pause/Resume and Stop buttons
            OutlinedButton(
                onClick = onStop,
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    imageVector = ProdyIcons.Stop,
                    contentDescription = "Stop",
                    modifier = Modifier.size(24.dp)
                )
            }

            Button(
                onClick = if (isPaused) onStart else onPause,
                modifier = Modifier.size(72.dp),
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MoodCalm
                )
            ) {
                Icon(
                    imageVector = if (isPaused) ProdyIcons.PlayArrow else ProdyIcons.Pause,
                    contentDescription = if (isPaused) "Resume" else "Pause",
                    modifier = Modifier.size(32.dp)
                )
            }
        } else {
            // Start button
            Button(
                onClick = onStart,
                modifier = Modifier
                    .height(56.dp)
                    .width(160.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MoodCalm
                )
            ) {
                Icon(
                    imageVector = ProdyIcons.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Begin", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
            }
        }
    }
}

@Composable
private fun SessionStats(
    sessionsCompleted: Int,
    totalMinutes: Int
) {
    ProdyCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                value = sessionsCompleted.toString(),
                label = "Sessions Today"
            )
            StatItem(
                value = "$totalMinutes",
                label = "Minutes Total"
            )
        }
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MoodCalm
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
