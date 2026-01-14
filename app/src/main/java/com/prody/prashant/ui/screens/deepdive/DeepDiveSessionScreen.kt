package com.prody.prashant.ui.screens.deepdive
import com.prody.prashant.ui.icons.ProdyIcons

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.prody.prashant.domain.deepdive.*
import com.prody.prashant.ui.theme.*

/**
 * Deep Dive Session Screen - Guided deep reflection experience.
 *
 * Steps:
 * 1. Mood check (before)
 * 2. Opening reflection
 * 3. Core exploration
 * 4. Key insight
 * 5. Commitment
 * 6. Mood check (after)
 * 7. Celebration
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeepDiveSessionScreen(
    deepDiveId: Long,
    onNavigateBack: () -> Unit,
    onSessionComplete: () -> Unit,
    viewModel: DeepDiveViewModel = hiltViewModel()
) {
    val uiState by viewModel.sessionState.collectAsStateWithLifecycle()

    // Load session on start
    LaunchedEffect(deepDiveId) {
        viewModel.loadSession(deepDiveId)
    }

    val theme = uiState.session?.theme
    val themeColor = theme?.let { Color(it.colorDark) } ?: ProdyAccentGreen

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = theme?.displayName ?: "Deep Dive",
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = uiState.currentProgress.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = ProdyIcons.AutoMirrored.Filled.ArrowBack,
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
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = themeColor)
                    }
                }
                uiState.session != null -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Progress indicator
                        LinearProgressIndicator(
                            progress = { uiState.progressPercent },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp),
                            color = themeColor,
                            trackColor = themeColor.copy(alpha = 0.2f)
                        )

                        // Content based on current step
                        AnimatedContent(
                            targetState = uiState.currentProgress,
                            transitionSpec = {
                                fadeIn(tween(300)) togetherWith fadeOut(tween(300))
                            },
                            label = "step_content"
                        ) { progress ->
                            when (progress) {
                                DeepDiveProgress.NOT_STARTED -> MoodBeforeStep(
                                    theme = theme,
                                    themeColor = themeColor,
                                    selectedMood = uiState.moodBefore,
                                    onMoodSelected = viewModel::onMoodBeforeSelected,
                                    onStart = {
                                        viewModel.startSession(deepDiveId)
                                    }
                                )
                                DeepDiveProgress.OPENING -> OpeningStep(
                                    prompt = uiState.session?.prompts?.openingQuestion.orEmpty(),
                                    text = uiState.openingText,
                                    onTextChange = viewModel::onOpeningTextChanged,
                                    canAdvance = uiState.canAdvance,
                                    isSaving = uiState.isSaving,
                                    themeColor = themeColor,
                                    onNext = viewModel::saveOpeningAndAdvance
                                )
                                DeepDiveProgress.CORE -> CoreStep(
                                    prompts = uiState.session?.prompts?.coreQuestions.orEmpty(),
                                    text = uiState.coreText,
                                    onTextChange = viewModel::onCoreTextChanged,
                                    canAdvance = uiState.canAdvance,
                                    isSaving = uiState.isSaving,
                                    themeColor = themeColor,
                                    onBack = viewModel::goToPreviousStep,
                                    onNext = viewModel::saveCoreAndAdvance
                                )
                                DeepDiveProgress.INSIGHT -> InsightStep(
                                    prompt = uiState.session?.prompts?.insightPrompt.orEmpty(),
                                    text = uiState.insightText,
                                    onTextChange = viewModel::onInsightTextChanged,
                                    canAdvance = uiState.canAdvance,
                                    isSaving = uiState.isSaving,
                                    themeColor = themeColor,
                                    onBack = viewModel::goToPreviousStep,
                                    onNext = viewModel::saveInsightAndAdvance
                                )
                                DeepDiveProgress.COMMITMENT -> CommitmentStep(
                                    prompt = uiState.session?.prompts?.commitmentPrompt.orEmpty(),
                                    text = uiState.commitmentText,
                                    onTextChange = viewModel::onCommitmentTextChanged,
                                    canAdvance = uiState.canAdvance,
                                    isSaving = uiState.isSaving,
                                    themeColor = themeColor,
                                    onBack = viewModel::goToPreviousStep,
                                    onComplete = viewModel::saveCommitmentAndComplete
                                )
                                DeepDiveProgress.COMPLETED -> CompletedStep(
                                    theme = theme,
                                    themeColor = themeColor,
                                    moodBefore = uiState.moodBefore,
                                    moodAfter = uiState.moodAfter,
                                    keyInsight = uiState.insightText,
                                    commitment = uiState.commitmentText,
                                    onDone = {
                                        viewModel.resetSessionState()
                                        onSessionComplete()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Mood after dialog
    if (uiState.showMoodAfterDialog) {
        MoodAfterDialog(
            onMoodSelected = viewModel::onMoodAfterSelected,
            onSkip = viewModel::skipMoodAfter
        )
    }

    // Completion celebration
    if (uiState.showCompletionCelebration) {
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(2000)
            viewModel.dismissCompletionCelebration()
        }
    }

    // Error handling
    uiState.errorMessage?.let { error ->
        LaunchedEffect(error) {
            viewModel.dismissSessionError()
        }
    }
}

@Composable
private fun MoodBeforeStep(
    theme: DeepDiveTheme?,
    themeColor: Color,
    selectedMood: Int?,
    onMoodSelected: (Int) -> Unit,
    onStart: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Theme icon
        Text(text = theme?.icon ?: "🌊", fontSize = 64.sp)

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Before We Begin",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "How are you feeling right now?",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Mood selection
        MoodSelector(
            selectedMood = selectedMood,
            onMoodSelected = onMoodSelected,
            themeColor = themeColor
        )

        Spacer(modifier = Modifier.weight(1f))

        // Theme description
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = theme?.let { Color(it.colorLight) }
                    ?: MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Today's Theme: ${theme?.displayName.orEmpty()}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = theme?.description.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onStart,
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedMood != null,
            colors = ButtonDefaults.buttonColors(containerColor = themeColor)
        ) {
            Text("Begin Deep Dive", color = Color.White)
        }
    }
}

@Composable
private fun MoodSelector(
    selectedMood: Int?,
    onMoodSelected: (Int) -> Unit,
    themeColor: Color
) {
    val moods = listOf(
        1 to "😢",
        3 to "😔",
        5 to "😐",
        7 to "🙂",
        10 to "😊"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        moods.forEach { (value, emoji) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onMoodSelected(value) }
                    .background(
                        if (selectedMood == value) themeColor.copy(alpha = 0.2f)
                        else Color.Transparent
                    )
                    .padding(12.dp)
            ) {
                Text(text = emoji, fontSize = 32.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (selectedMood == value) themeColor
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun OpeningStep(
    prompt: String,
    text: String,
    onTextChange: (String) -> Unit,
    canAdvance: Boolean,
    isSaving: Boolean,
    themeColor: Color,
    onNext: () -> Unit
) {
    ReflectionStepContent(
        stepTitle = "Opening Reflection",
        stepIcon = "🌅",
        prompt = prompt,
        text = text,
        onTextChange = onTextChange,
        placeholder = "Take your time to reflect...",
        canAdvance = canAdvance,
        isSaving = isSaving,
        themeColor = themeColor,
        showBackButton = false,
        onBack = {},
        onNext = onNext,
        nextLabel = "Continue"
    )
}

@Composable
private fun CoreStep(
    prompts: List<String>,
    text: String,
    onTextChange: (String) -> Unit,
    canAdvance: Boolean,
    isSaving: Boolean,
    themeColor: Color,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "🌊", fontSize = 32.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Core Exploration",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Display all core prompts
        prompts.forEachIndexed { index, prompt ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = themeColor.copy(alpha = 0.1f)
                )
            ) {
                Row(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "${index + 1}",
                        style = MaterialTheme.typography.labelMedium,
                        color = themeColor,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = prompt,
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp),
            placeholder = {
                Text("Explore these questions deeply...")
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = themeColor,
                cursorColor = themeColor
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) {
                Text("Back")
            }
            Button(
                onClick = onNext,
                modifier = Modifier.weight(1f),
                enabled = canAdvance && !isSaving,
                colors = ButtonDefaults.buttonColors(containerColor = themeColor)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Text("Continue", color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun InsightStep(
    prompt: String,
    text: String,
    onTextChange: (String) -> Unit,
    canAdvance: Boolean,
    isSaving: Boolean,
    themeColor: Color,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    ReflectionStepContent(
        stepTitle = "Key Insight",
        stepIcon = "💡",
        prompt = prompt,
        text = text,
        onTextChange = onTextChange,
        placeholder = "What's the most important thing you've realized?",
        canAdvance = canAdvance,
        isSaving = isSaving,
        themeColor = themeColor,
        showBackButton = true,
        onBack = onBack,
        onNext = onNext,
        nextLabel = "Continue"
    )
}

@Composable
private fun CommitmentStep(
    prompt: String,
    text: String,
    onTextChange: (String) -> Unit,
    canAdvance: Boolean,
    isSaving: Boolean,
    themeColor: Color,
    onBack: () -> Unit,
    onComplete: () -> Unit
) {
    ReflectionStepContent(
        stepTitle = "Commitment",
        stepIcon = "🎯",
        prompt = prompt,
        text = text,
        onTextChange = onTextChange,
        placeholder = "What will you commit to doing differently?",
        canAdvance = canAdvance,
        isSaving = isSaving,
        themeColor = themeColor,
        showBackButton = true,
        onBack = onBack,
        onNext = onComplete,
        nextLabel = "Complete Deep Dive"
    )
}

@Composable
private fun ReflectionStepContent(
    stepTitle: String,
    stepIcon: String,
    prompt: String,
    text: String,
    onTextChange: (String) -> Unit,
    placeholder: String,
    canAdvance: Boolean,
    isSaving: Boolean,
    themeColor: Color,
    showBackButton: Boolean,
    onBack: () -> Unit,
    onNext: () -> Unit,
    nextLabel: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = stepIcon, fontSize = 32.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stepTitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = themeColor.copy(alpha = 0.1f)
            )
        ) {
            Text(
                text = prompt,
                style = MaterialTheme.typography.bodyLarge,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 150.dp),
            placeholder = { Text(placeholder) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = themeColor,
                cursorColor = themeColor
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (showBackButton) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Back")
                }
            }
            Button(
                onClick = onNext,
                modifier = Modifier.weight(1f),
                enabled = canAdvance && !isSaving,
                colors = ButtonDefaults.buttonColors(containerColor = themeColor)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Text(nextLabel, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun CompletedStep(
    theme: DeepDiveTheme?,
    themeColor: Color,
    moodBefore: Int?,
    moodAfter: Int?,
    keyInsight: String,
    commitment: String,
    onDone: () -> Unit
) {
    val moodChange = if (moodBefore != null && moodAfter != null) {
        moodAfter - moodBefore
    } else null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Celebration icon
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(ProdySuccess.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "✨", fontSize = 48.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Deep Dive Complete!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "You've explored ${theme?.displayName.orEmpty()} deeply",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Mood change
        moodChange?.let { change ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (change >= 0) ProdySuccess.copy(alpha = 0.1f)
                    else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mood Change",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = if (change >= 0) "+$change" else change.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (change >= 0) ProdySuccess else ProdyError
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Key insight
        if (keyInsight.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = themeColor.copy(alpha = 0.1f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = ProdyIcons.Lightbulb,
                            contentDescription = null,
                            tint = themeColor
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Your Key Insight",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = keyInsight,
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Commitment
        if (commitment.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = ProdyIcons.CheckCircle,
                            contentDescription = null,
                            tint = ProdySuccess
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Your Commitment",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = commitment,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onDone,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = themeColor)
        ) {
            Text("Done", color = Color.White)
        }
    }
}

@Composable
private fun MoodAfterDialog(
    onMoodSelected: (Int) -> Unit,
    onSkip: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onSkip,
        title = {
            Text(
                text = "How do you feel now?",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Has this session shifted how you feel?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(1 to "😢", 3 to "😔", 5 to "😐", 7 to "🙂", 10 to "😊").forEach { (value, emoji) ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onMoodSelected(value) }
                                .padding(8.dp)
                        ) {
                            Text(text = emoji, fontSize = 28.sp)
                            Text(
                                text = value.toString(),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onSkip) {
                Text("Skip")
            }
        }
    )
}
