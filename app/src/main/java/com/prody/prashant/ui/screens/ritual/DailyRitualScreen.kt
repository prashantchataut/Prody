package com.prody.prashant.ui.screens.ritual
import com.prody.prashant.ui.icons.ProdyIcons

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.data.local.entity.SavedWisdomEntity
import com.prody.prashant.domain.model.Mood
import com.prody.prashant.ui.components.ProdyCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyRitualScreen(
    onNavigateBack: () -> Unit,
    onNavigateToJournal: (String) -> Unit,
    viewModel: DailyRitualViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Handle navigation
    LaunchedEffect(uiState.shouldNavigateToJournal) {
        if (uiState.shouldNavigateToJournal) {
            onNavigateToJournal(uiState.prefilledContent)
            viewModel.clearNavigation()
        }
    }

    // Progress calculation
    val totalSteps = if (uiState.ritualMode == RitualMode.MORNING && uiState.wisdomForMorning != null) 5 else 4
    val currentStepIndex = when (uiState.currentStep) {
        RitualStep.WELCOME -> 0
        RitualStep.WISDOM -> 1
        RitualStep.MOOD -> if (uiState.wisdomForMorning != null) 2 else 1
        RitualStep.INTENTION -> if (uiState.wisdomForMorning != null) 3 else 2
        RitualStep.REFLECTION -> if (uiState.wisdomForMorning != null) 4 else 3
        RitualStep.COMPLETE -> totalSteps
    }

    val progress by animateFloatAsState(
        targetValue = if (totalSteps > 0) currentStepIndex.toFloat() / totalSteps else 0f,
        animationSpec = tween(300),
        label = "progress"
    )

    Scaffold(
        topBar = {
            if (uiState.currentStep != RitualStep.WELCOME && uiState.currentStep != RitualStep.COMPLETE) {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = if (uiState.ritualMode == RitualMode.MORNING) "Morning Ritual" else "Evening Ritual",
                                style = MaterialTheme.typography.titleMedium
                            )
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = viewModel::previousStep) {
                            Icon(
                                imageVector = ProdyIcons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AnimatedContent(
                targetState = uiState.currentStep,
                transitionSpec = {
                    if (targetState.ordinal > initialState.ordinal) {
                        slideInHorizontally { it } + fadeIn() togetherWith
                                slideOutHorizontally { -it } + fadeOut()
                    } else {
                        slideInHorizontally { -it } + fadeIn() togetherWith
                                slideOutHorizontally { it } + fadeOut()
                    }
                },
                label = "step_transition"
            ) { step ->
                when (step) {
                    RitualStep.WELCOME -> WelcomeStep(
                        ritualMode = uiState.ritualMode,
                        isMorningCompleted = uiState.isMorningCompleted,
                        isEveningCompleted = uiState.isEveningCompleted,
                        currentStreak = uiState.currentStreak,
                        thisWeekRituals = uiState.thisWeekRituals,
                        onModeSelected = viewModel::setRitualMode,
                        onStart = viewModel::startRitual,
                        onClose = onNavigateBack
                    )

                    RitualStep.WISDOM -> WisdomStep(
                        wisdom = uiState.wisdomForMorning,
                        onNext = viewModel::nextStep
                    )

                    RitualStep.MOOD -> MoodStep(
                        selectedMood = uiState.selectedMood,
                        ritualMode = uiState.ritualMode,
                        onMoodSelected = viewModel::onMoodSelected,
                        onNext = viewModel::nextStep
                    )

                    RitualStep.INTENTION -> IntentionStep(
                        ritualMode = uiState.ritualMode,
                        intention = uiState.intention,
                        dayRating = uiState.dayRating,
                        currentPrompt = uiState.currentPrompt,
                        onIntentionChanged = viewModel::onIntentionChanged,
                        onDayRatingSelected = viewModel::onDayRatingSelected,
                        onNext = viewModel::nextStep
                    )

                    RitualStep.REFLECTION -> ReflectionStep(
                        ritualMode = uiState.ritualMode,
                        reflection = uiState.eveningReflection,
                        onReflectionChanged = viewModel::onEveningReflectionChanged,
                        isSaving = uiState.isSaving,
                        onComplete = viewModel::nextStep
                    )

                    RitualStep.COMPLETE -> CompleteStep(
                        ritualMode = uiState.ritualMode,
                        completionMessage = uiState.completionMessage,
                        currentStreak = uiState.currentStreak,
                        onExpandToJournal = viewModel::expandToJournal,
                        onClose = onNavigateBack
                    )
                }
            }
        }
    }

    // Error snackbar
    uiState.errorMessage?.let { message ->
        LaunchedEffect(message) {
            viewModel.dismissError()
        }
    }
}

@Composable
private fun WelcomeStep(
    ritualMode: RitualMode,
    isMorningCompleted: Boolean,
    isEveningCompleted: Boolean,
    currentStreak: Int,
    thisWeekRituals: Int,
    onModeSelected: (RitualMode) -> Unit,
    onStart: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Close button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = onClose) {
                Icon(ProdyIcons.Close, contentDescription = "Close")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Greeting
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        val greeting = when {
            hour < 12 -> "Good Morning"
            hour < 17 -> "Good Afternoon"
            else -> "Good Evening"
        }

        Text(
            text = greeting,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Take 60 seconds to center yourself",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Streak display
        if (currentStreak > 0) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = ProdyIcons.LocalFireDepartment,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$currentStreak day streak",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // This week progress
        Text(
            text = "$thisWeekRituals of 7 days this week",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Ritual mode selection
        Text(
            text = "Choose your ritual",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RitualModeCard(
                mode = RitualMode.MORNING,
                isSelected = ritualMode == RitualMode.MORNING,
                isCompleted = isMorningCompleted,
                icon = ProdyIcons.WbSunny,
                title = "Morning",
                subtitle = "Set your intention",
                onClick = { onModeSelected(RitualMode.MORNING) },
                modifier = Modifier.weight(1f)
            )

            RitualModeCard(
                mode = RitualMode.EVENING,
                isSelected = ritualMode == RitualMode.EVENING,
                isCompleted = isEveningCompleted,
                icon = ProdyIcons.NightsStay,
                title = "Evening",
                subtitle = "Reflect on your day",
                onClick = { onModeSelected(RitualMode.EVENING) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Start button
        val canStart = when (ritualMode) {
            RitualMode.MORNING -> !isMorningCompleted
            RitualMode.EVENING -> !isEveningCompleted
        }

        Button(
            onClick = onStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = canStart
        ) {
            if (canStart) {
                Text("Begin Ritual")
                Spacer(modifier = Modifier.width(8.dp))
                Icon(ProdyIcons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
            } else {
                Icon(ProdyIcons.CheckCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Already Completed")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun RitualModeCard(
    mode: RitualMode,
    isSelected: Boolean,
    isCompleted: Boolean,
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = when {
            isCompleted -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            isSelected -> MaterialTheme.colorScheme.primaryContainer
            else -> MaterialTheme.colorScheme.surface
        },
        border = if (isSelected && !isCompleted) {
            ButtonDefaults.outlinedButtonBorder(enabled = true)
        } else null,
        tonalElevation = if (isSelected) 4.dp else 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isCompleted) {
                Icon(
                    imageVector = ProdyIcons.CheckCircle,
                    contentDescription = "Completed",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = if (isCompleted) "Done" else subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun WisdomStep(
    wisdom: SavedWisdomEntity?,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = ProdyIcons.AutoAwesome,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(40.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Today's Wisdom",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        wisdom?.let {
            ProdyCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = it.content,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        fontStyle = FontStyle.Italic
                    )

                    it.author?.let { author ->
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "— $author",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Continue")
            Spacer(modifier = Modifier.width(8.dp))
            Icon(ProdyIcons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
        }
    }
}

@Composable
private fun MoodStep(
    selectedMood: Mood?,
    ritualMode: RitualMode,
    onMoodSelected: (Mood) -> Unit,
    onNext: () -> Unit
) {
    val moods = listOf(
        Mood.HAPPY, Mood.CALM, Mood.GRATEFUL, Mood.EXCITED, Mood.MOTIVATED,
        Mood.ANXIOUS, Mood.SAD, Mood.CONFUSED
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = when (ritualMode) {
                RitualMode.MORNING -> "How are you feeling this morning?"
                RitualMode.EVENING -> "How are you feeling right now?"
            },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Mood grid
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            moods.chunked(4).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    row.forEach { mood ->
                        MoodButton(
                            mood = mood,
                            isSelected = mood == selectedMood,
                            onClick = { onMoodSelected(mood) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // Fill remaining space
                    repeat(4 - row.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = selectedMood != null
        ) {
            Text("Continue")
            Spacer(modifier = Modifier.width(8.dp))
            Icon(ProdyIcons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
        }
    }
}

@Composable
private fun MoodButton(
    mood: Mood,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surface,
        tonalElevation = if (isSelected) 4.dp else 1.dp,
        border = if (isSelected) ButtonDefaults.outlinedButtonBorder(enabled = true) else null
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = mood.emoji,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = mood.displayName,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun IntentionStep(
    ritualMode: RitualMode,
    intention: String,
    dayRating: DayRating?,
    currentPrompt: String,
    onIntentionChanged: (String) -> Unit,
    onDayRatingSelected: (DayRating) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        when (ritualMode) {
            RitualMode.MORNING -> {
                Text(
                    text = "What's your intention for today?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = currentPrompt,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = intention,
                    onValueChange = onIntentionChanged,
                    placeholder = { Text("Today I want to...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 4
                )
            }

            RitualMode.EVENING -> {
                Text(
                    text = "How was your day?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(48.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DayRating.entries.forEach { rating ->
                        DayRatingButton(
                            rating = rating,
                            isSelected = rating == dayRating,
                            onClick = { onDayRatingSelected(rating) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = when (ritualMode) {
                RitualMode.MORNING -> true // Intention is optional
                RitualMode.EVENING -> dayRating != null
            }
        ) {
            Text("Continue")
            Spacer(modifier = Modifier.width(8.dp))
            Icon(ProdyIcons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
        }
    }
}

@Composable
private fun DayRatingButton(
    rating: DayRating,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surface,
        tonalElevation = if (isSelected) 4.dp else 1.dp,
        border = if (isSelected) ButtonDefaults.outlinedButtonBorder(enabled = true) else null
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = rating.emoji,
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = rating.displayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun ReflectionStep(
    ritualMode: RitualMode,
    reflection: String,
    onReflectionChanged: (String) -> Unit,
    isSaving: Boolean,
    onComplete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = when (ritualMode) {
                RitualMode.MORNING -> "Any thoughts before you start your day?"
                RitualMode.EVENING -> "Anything you want to capture from today?"
            },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "This is optional - skip if you're ready to finish",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = reflection,
            onValueChange = onReflectionChanged,
            placeholder = {
                Text(
                    when (ritualMode) {
                        RitualMode.MORNING -> "A quick thought, worry, or hope..."
                        RitualMode.EVENING -> "What happened? What did you learn?..."
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            maxLines = 6
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onComplete,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !isSaving
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Complete Ritual")
                Spacer(modifier = Modifier.width(8.dp))
                Icon(ProdyIcons.Check, contentDescription = null)
            }
        }
    }
}

@Composable
private fun CompleteStep(
    ritualMode: RitualMode,
    completionMessage: String,
    currentStreak: Int,
    onExpandToJournal: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Success animation/icon
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(96.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = ProdyIcons.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Well Done!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = completionMessage,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        if (currentStreak > 1) {
            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = ProdyIcons.LocalFireDepartment,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$currentStreak day streak!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Optional: expand to journal
        OutlinedButton(
            onClick = onExpandToJournal,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(ProdyIcons.Edit, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Write more in journal")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onClose,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Done")
        }
    }
}
