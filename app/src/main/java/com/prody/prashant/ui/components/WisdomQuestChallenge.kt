package com.prody.prashant.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.domain.gamification.WisdomQuestEngine
import com.prody.prashant.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin

/**
 * WisdomQuestChallenge - Active Recall Mini-Challenge UI
 *
 * Replaces the boring "Mark as Learned +XP" button with engaging micro-challenges.
 * Users must complete a cognitive task to earn XP, creating psychological investment.
 *
 * Challenge Types:
 * - UNSCRAMBLE: Rearrange scrambled letters with timer
 * - MULTIPLE_CHOICE: Select correct definition from 4 options
 * - CONTEXT_FIT: Type the word that fits the sentence context
 *
 * Design: Premium, animated, with instant feedback and celebration effects.
 */

@Composable
fun WisdomQuestChallenge(
    challenge: WisdomQuestEngine.WisdomChallenge,
    currentStreak: Int,
    dailyFocus: WisdomQuestEngine.DailyFocus?,
    onAnswerSubmit: (String) -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    var userAnswer by remember { mutableStateOf("") }
    var showHint by remember { mutableStateOf(false) }
    var timeRemaining by remember { mutableLongStateOf(challenge.timeLimitMs) }

    // Timer for timed challenges
    LaunchedEffect(challenge) {
        if (challenge.timeLimitMs > 0) {
            while (timeRemaining > 0) {
                delay(100)
                timeRemaining -= 100
            }
            // Auto-submit on timeout
            onAnswerSubmit(userAnswer)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(ProdyTokens.Radius.lg))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Challenge header with streak indicator
        ChallengeHeader(
            challengeType = challenge.type,
            currentStreak = currentStreak,
            dailyFocus = dailyFocus,
            isFocusMatch = dailyFocus != null &&
                challenge.word.category.lowercase() in listOf(
                    dailyFocus.name.lowercase(),
                    "general", "academic", "literary"
                )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Timer bar for timed challenges
        if (challenge.timeLimitMs > 0) {
            TimerBar(
                timeRemaining = timeRemaining,
                totalTime = challenge.timeLimitMs
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Challenge content based on type
        when (challenge.type) {
            WisdomQuestEngine.ChallengeType.UNSCRAMBLE -> {
                UnscrambleChallenge(
                    scrambledWord = challenge.scrambledWord ?: "",
                    definition = challenge.word.definition,
                    userAnswer = userAnswer,
                    onAnswerChange = { userAnswer = it },
                    onSubmit = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onAnswerSubmit(userAnswer)
                    }
                )
            }
            WisdomQuestEngine.ChallengeType.MULTIPLE_CHOICE -> {
                MultipleChoiceChallenge(
                    word = challenge.word.word,
                    options = challenge.options ?: emptyList(),
                    onOptionSelected = { selectedOption ->
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onAnswerSubmit(selectedOption)
                    }
                )
            }
            WisdomQuestEngine.ChallengeType.CONTEXT_FIT -> {
                ContextFitChallenge(
                    contextSentence = challenge.contextSentence ?: "",
                    userAnswer = userAnswer,
                    onAnswerChange = { userAnswer = it },
                    onSubmit = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onAnswerSubmit(userAnswer)
                    },
                    showHint = showHint,
                    hint = challenge.word.definition
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Hint button (for context fit)
            if (challenge.type == WisdomQuestEngine.ChallengeType.CONTEXT_FIT) {
                TextButton(
                    onClick = { showHint = !showHint }
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = "Hint",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (showHint) "Hide Hint" else "Show Hint")
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

            // Skip button
            TextButton(
                onClick = onSkip,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text("Skip")
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Skip",
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun ChallengeHeader(
    challengeType: WisdomQuestEngine.ChallengeType,
    currentStreak: Int,
    dailyFocus: WisdomQuestEngine.DailyFocus?,
    isFocusMatch: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Challenge type badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(ProdyTokens.Radius.full))
                .background(ProdyAccentGreen.copy(alpha = 0.15f))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Icon(
                imageVector = when (challengeType) {
                    WisdomQuestEngine.ChallengeType.UNSCRAMBLE -> Icons.Default.Shuffle
                    WisdomQuestEngine.ChallengeType.MULTIPLE_CHOICE -> Icons.Default.Quiz
                    WisdomQuestEngine.ChallengeType.CONTEXT_FIT -> Icons.Default.TextFields
                },
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = ProdyAccentGreen
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = when (challengeType) {
                    WisdomQuestEngine.ChallengeType.UNSCRAMBLE -> "Unscramble"
                    WisdomQuestEngine.ChallengeType.MULTIPLE_CHOICE -> "Definition"
                    WisdomQuestEngine.ChallengeType.CONTEXT_FIT -> "Context"
                },
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = ProdyAccentGreen
            )
        }

        // Streak and focus indicators
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Daily focus bonus indicator
            if (isFocusMatch && dailyFocus != null) {
                Surface(
                    shape = RoundedCornerShape(ProdyTokens.Radius.full),
                    color = LeaderboardGold.copy(alpha = 0.2f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "2x XP",
                            modifier = Modifier.size(14.dp),
                            tint = LeaderboardGold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "2x",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = LeaderboardGold
                        )
                    }
                }
            }

            // Streak counter
            if (currentStreak > 0) {
                Surface(
                    shape = RoundedCornerShape(ProdyTokens.Radius.full),
                    color = StreakFire.copy(alpha = 0.2f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Streak",
                            modifier = Modifier.size(14.dp),
                            tint = StreakFire
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$currentStreak",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = StreakFire
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TimerBar(
    timeRemaining: Long,
    totalTime: Long
) {
    val progress = (timeRemaining.toFloat() / totalTime.toFloat()).coerceIn(0f, 1f)
    val timerColor = when {
        progress > 0.5f -> ProdyAccentGreen
        progress > 0.25f -> ProdyWarning
        else -> ProdyError
    }

    // Pulsing animation when low time
    val infiniteTransition = rememberInfiniteTransition(label = "timer_pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (progress < 0.25f) 1.1f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(300),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Time Remaining",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${(timeRemaining / 1000)}s",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = timerColor,
                modifier = Modifier.scale(pulse)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = timerColor,
            trackColor = timerColor.copy(alpha = 0.2f)
        )
    }
}

@Composable
private fun UnscrambleChallenge(
    scrambledWord: String,
    definition: String,
    userAnswer: String,
    onAnswerChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Scrambled letters display
        Text(
            text = "Unscramble this word:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Animated scrambled letters
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            scrambledWord.forEachIndexed { index, char ->
                ScrambledLetterBox(
                    letter = char,
                    index = index
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Definition hint
        Text(
            text = "Meaning: \"$definition\"",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Answer input
        BasicTextField(
            value = userAnswer,
            onValueChange = onAnswerChange,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            textStyle = MaterialTheme.typography.titleLarge.copy(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { onSubmit() }
            ),
            cursorBrush = SolidColor(ProdyAccentGreen),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(ProdyTokens.Radius.md))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(
                            width = 2.dp,
                            color = if (userAnswer.isNotEmpty()) ProdyAccentGreen else MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(ProdyTokens.Radius.md)
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (userAnswer.isEmpty()) {
                        Text(
                            text = "Type your answer...",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                    innerTextField()
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Submit button
        Button(
            onClick = onSubmit,
            enabled = userAnswer.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = ProdyAccentGreen,
                contentColor = Color.Black
            )
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Submit Answer",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ScrambledLetterBox(
    letter: Char,
    index: Int
) {
    val infiniteTransition = rememberInfiniteTransition(label = "letter_$index")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000 + index * 100, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )

    val verticalOffset = sin(offset * 2 * PI.toFloat()) * 3

    Box(
        modifier = Modifier
            .padding(horizontal = 2.dp)
            .offset(y = verticalOffset.dp)
            .size(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        ProdyAccentGreen.copy(alpha = 0.3f),
                        ProdyAccentGreen.copy(alpha = 0.15f)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = ProdyAccentGreen.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = letter.toString(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun MultipleChoiceChallenge(
    word: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    var selectedOption by remember { mutableStateOf<String?>(null) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "What does this word mean?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Word display
        Text(
            text = word,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = ProdyAccentGreen
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Options
        options.forEachIndexed { index, option ->
            val isSelected = selectedOption == option

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        selectedOption = option
                        onOptionSelected(option)
                    },
                shape = RoundedCornerShape(ProdyTokens.Radius.md),
                color = if (isSelected) ProdyAccentGreen.copy(alpha = 0.2f)
                       else MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) ProdyAccentGreen
                           else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Option letter
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) ProdyAccentGreen
                                else MaterialTheme.colorScheme.surfaceVariant
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = ('A' + index).toString(),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.Black
                                   else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = option,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ContextFitChallenge(
    contextSentence: String,
    userAnswer: String,
    onAnswerChange: (String) -> Unit,
    onSubmit: () -> Unit,
    showHint: Boolean,
    hint: String
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Fill in the blank:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Context sentence with blank
        Text(
            text = contextSentence,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 28.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        // Hint section
        AnimatedVisibility(
            visible = showHint,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                shape = RoundedCornerShape(ProdyTokens.Radius.sm),
                color = ProdyInfo.copy(alpha = 0.1f)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = ProdyInfo
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Hint: $hint",
                        style = MaterialTheme.typography.bodySmall,
                        color = ProdyInfo
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Answer input
        BasicTextField(
            value = userAnswer,
            onValueChange = onAnswerChange,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            textStyle = MaterialTheme.typography.titleMedium.copy(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { onSubmit() }
            ),
            cursorBrush = SolidColor(ProdyAccentGreen),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(ProdyTokens.Radius.md))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(
                            width = 2.dp,
                            color = if (userAnswer.isNotEmpty()) ProdyAccentGreen else MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(ProdyTokens.Radius.md)
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (userAnswer.isEmpty()) {
                        Text(
                            text = "Type the missing word...",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                    innerTextField()
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Submit button
        Button(
            onClick = onSubmit,
            enabled = userAnswer.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = ProdyAccentGreen,
                contentColor = Color.Black
            )
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Submit Answer",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Result display after completing a Wisdom Quest challenge.
 * Shows XP breakdown with celebration animation for correct answers.
 */
@Composable
fun WisdomQuestResult(
    result: WisdomQuestEngine.QuestResult,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(result.isCorrect) {
        if (result.isCorrect) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    // Entry animation
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 300f
        ),
        label = "result_scale"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(ProdyTokens.Radius.lg))
            .background(
                if (result.isCorrect) ProdyAccentGreen.copy(alpha = 0.15f)
                else ProdyError.copy(alpha = 0.1f)
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Result icon
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(
                    if (result.isCorrect) ProdyAccentGreen.copy(alpha = 0.2f)
                    else ProdyError.copy(alpha = 0.2f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (result.isCorrect) Icons.Default.CheckCircle
                             else Icons.Default.Close,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = if (result.isCorrect) ProdyAccentGreen else ProdyError
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Result title
        Text(
            text = if (result.isCorrect) "Wisdom Earned!" else "Keep Learning",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = if (result.isCorrect) ProdyAccentGreen else ProdyError
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Message
        Text(
            text = result.message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        // XP breakdown for correct answers
        if (result.isCorrect) {
            Spacer(modifier = Modifier.height(20.dp))

            // XP breakdown card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(ProdyTokens.Radius.md),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    XpBreakdownRow(
                        label = "Base XP",
                        value = "+${result.baseXp}",
                        color = ProdyAccentGreen
                    )

                    if (result.streakBonus > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        XpBreakdownRow(
                            label = "Streak Bonus (${result.newStreak}x)",
                            value = "+${result.streakBonus}",
                            color = StreakFire,
                            icon = Icons.Default.LocalFireDepartment
                        )
                    }

                    if (result.dailyFocusBonus > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        XpBreakdownRow(
                            label = "Daily Focus 2x",
                            value = "×2",
                            color = LeaderboardGold,
                            icon = Icons.Default.AutoAwesome
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )

                    // Total
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total XP",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "+${result.totalXp}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = ProdyAccentGreen
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Continue button
        Button(
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (result.isCorrect) ProdyAccentGreen else MaterialTheme.colorScheme.primary,
                contentColor = if (result.isCorrect) Color.Black else MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = if (result.isCorrect) "Continue Learning" else "Try Another",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun XpBreakdownRow(
    label: String,
    value: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = color
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

/**
 * Daily Focus selector for choosing today's focus category.
 * Appears when user hasn't set their daily focus yet.
 */
@Composable
fun DailyFocusSelector(
    onFocusSelected: (WisdomQuestEngine.DailyFocus) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(ProdyTokens.Radius.lg))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        LeaderboardGold.copy(alpha = 0.15f),
                        LeaderboardGold.copy(alpha = 0.05f)
                    )
                )
            )
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            tint = LeaderboardGold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Set Your Daily Focus",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Earn 2x XP on words matching your focus!",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Focus options grid
        val focusOptions = WisdomQuestEngine.DailyFocus.entries

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            focusOptions.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowItems.forEach { focus ->
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    onFocusSelected(focus)
                                },
                            shape = RoundedCornerShape(ProdyTokens.Radius.md),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = focus.displayName,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = focus.description,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
