package com.prody.prashant.ui.screens.haven
import com.prody.prashant.ui.icons.ProdyIcons

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.R
import com.prody.prashant.domain.haven.ExerciseType
import com.prody.prashant.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Haven Exercise Screen - Guided therapeutic exercises
 *
 * Features:
 * - Breathing exercises with animated visuals
 * - Grounding exercises with step-by-step guidance
 * - Thought record exercises with prompts
 * - Progress tracking and completion
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HavenExerciseScreen(
    exerciseType: ExerciseType,
    onNavigateBack: () -> Unit,
    onExerciseComplete: () -> Unit,
    viewModel: HavenViewModel = hiltViewModel()
) {
    val uiState by viewModel.exerciseState.collectAsStateWithLifecycle()

    // Start exercise when screen loads
    LaunchedEffect(exerciseType) {
        viewModel.startExercise(exerciseType)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = exerciseType.displayName,
                        fontWeight = FontWeight.SemiBold
                    )
                },
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
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = ProdyAccentGreen)
                    }
                }
                uiState.isCompleted -> {
                    ExerciseCompletionScreen(
                        exerciseType = exerciseType,
                        totalDuration = (uiState.elapsedSeconds * 1000L),
                        onDone = onExerciseComplete
                    )
                }
                else -> {
                    val exercise = uiState.exercise
                    val currentStep = uiState.currentStepIndex
                    val totalSteps = exercise?.steps?.size ?: 0
                    val currentInstruction = exercise?.steps?.getOrNull(currentStep)?.instruction ?: ""

                    when (exerciseType) {
                        ExerciseType.BOX_BREATHING,
                        ExerciseType.FOUR_SEVEN_EIGHT_BREATHING -> {
                            BreathingExerciseContent(
                                exerciseType = exerciseType,
                                currentStep = currentStep,
                                totalSteps = totalSteps,
                                instruction = currentInstruction,
                                onComplete = { viewModel.completeExercise() }
                            )
                        }
                        ExerciseType.GROUNDING_54321,
                        ExerciseType.BODY_SCAN -> {
                            GroundingExerciseContent(
                                exerciseType = exerciseType,
                                currentStep = currentStep,
                                totalSteps = totalSteps,
                                instruction = currentInstruction,
                                onNextStep = { viewModel.nextStep() },
                                onComplete = { viewModel.completeExercise() }
                            )
                        }
                        ExerciseType.THOUGHT_RECORD -> {
                            ThoughtExerciseContent(
                                exerciseType = exerciseType,
                                currentStep = currentStep,
                                totalSteps = totalSteps,
                                instruction = currentInstruction,
                                onNextStep = { viewModel.nextStep() },
                                onComplete = { viewModel.completeExercise() }
                            )
                        }
                        ExerciseType.PROGRESSIVE_RELAXATION -> {
                            RelaxationExerciseContent(
                                currentStep = currentStep,
                                totalSteps = totalSteps,
                                instruction = currentInstruction,
                                onNextStep = { viewModel.nextStep() },
                                onComplete = { viewModel.completeExercise() }
                            )
                        }
                        ExerciseType.EMOTION_WHEEL,
                        ExerciseType.GRATITUDE_MOMENT,
                        ExerciseType.LOVING_KINDNESS -> {
                            // Default fallback for exercises not yet implemented
                            GroundingExerciseContent(
                                exerciseType = exerciseType,
                                currentStep = currentStep,
                                totalSteps = totalSteps,
                                instruction = currentInstruction,
                                onNextStep = { viewModel.nextStep() },
                                onComplete = { viewModel.completeExercise() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BreathingExerciseContent(
    exerciseType: ExerciseType,
    currentStep: Int,
    totalSteps: Int,
    instruction: String,
    onComplete: () -> Unit
) {
    // Breathing pattern based on exercise type
    val (inhale, hold1, exhale, hold2) = when (exerciseType) {
        ExerciseType.BOX_BREATHING -> listOf(4000, 4000, 4000, 4000)
        ExerciseType.FOUR_SEVEN_EIGHT_BREATHING -> listOf(4000, 7000, 8000, 0)
        else -> listOf(4000, 4000, 4000, 4000)
    }

    val totalCycleDuration = inhale + hold1 + exhale + hold2
    val cyclesCompleted = remember { mutableIntStateOf(0) }
    val targetCycles = 4

    // Animation state
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val breathProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(totalCycleDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "breathProgress"
    )

    // Determine current phase
    val inhaleEnd = inhale.toFloat() / totalCycleDuration
    val hold1End = (inhale + hold1).toFloat() / totalCycleDuration
    val exhaleEnd = (inhale + hold1 + exhale).toFloat() / totalCycleDuration

    val (phase, phaseProgress) = when {
        breathProgress < inhaleEnd -> "Breathe In" to (breathProgress / inhaleEnd)
        breathProgress < hold1End -> "Hold" to ((breathProgress - inhaleEnd) / (hold1End - inhaleEnd))
        breathProgress < exhaleEnd -> "Breathe Out" to ((breathProgress - hold1End) / (exhaleEnd - hold1End))
        else -> "Hold" to ((breathProgress - exhaleEnd) / (1f - exhaleEnd))
    }

    // Circle scale based on phase
    val circleScale = when (phase) {
        "Breathe In" -> 0.6f + (phaseProgress * 0.4f)
        "Breathe Out" -> 1f - (phaseProgress * 0.4f)
        else -> if (breathProgress < hold1End) 1f else 0.6f
    }

    // Track cycles
    LaunchedEffect(Unit) {
        while (cyclesCompleted.intValue < targetCycles) {
            delay(totalCycleDuration.toLong())
            cyclesCompleted.intValue++
        }
        onComplete()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Progress indicator
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Cycle ${cyclesCompleted.intValue + 1} of $targetCycles",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { cyclesCompleted.intValue.toFloat() / targetCycles },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = ProdyAccentGreen,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }

        // Breathing circle
        Box(
            modifier = Modifier
                .size(280.dp)
                .scale(circleScale),
            contentAlignment = Alignment.Center
        ) {
            // Outer glow
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            ProdyAccentGreen.copy(alpha = 0.3f),
                            ProdyAccentGreen.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                )
            }

            // Main circle
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                ProdyAccentGreen.copy(alpha = 0.6f),
                                ProdyAccentGreen.copy(alpha = 0.3f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = phase,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // Instructions
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = when (exerciseType) {
                        ExerciseType.BOX_BREATHING -> "Box Breathing"
                        ExerciseType.FOUR_SEVEN_EIGHT_BREATHING -> "4-7-8 Technique"
                        else -> "Breathing Exercise"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = instruction.ifEmpty {
                        "Follow the circle. Breathe in as it expands, hold, then breathe out as it contracts."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun GroundingExerciseContent(
    exerciseType: ExerciseType,
    currentStep: Int,
    totalSteps: Int,
    instruction: String,
    onNextStep: () -> Unit,
    onComplete: () -> Unit
) {
    val steps = when (exerciseType) {
        ExerciseType.GROUNDING_54321 -> listOf(
            "5 things you can SEE" to "Look around and notice 5 things you can see right now. Observe their colors, shapes, and details.",
            "4 things you can TOUCH" to "Notice 4 things you can physically touch. Feel their texture, temperature, and weight.",
            "3 things you can HEAR" to "Listen carefully for 3 distinct sounds around you. They can be near or far.",
            "2 things you can SMELL" to "Identify 2 things you can smell. If needed, move closer to something nearby.",
            "1 thing you can TASTE" to "Notice 1 thing you can taste right now, even if it's just the taste in your mouth."
        )
        ExerciseType.BODY_SCAN -> listOf(
            "Feet & Ankles" to "Bring your attention to your feet. Notice any sensations - warmth, pressure, tingling.",
            "Legs" to "Move your awareness up to your calves and thighs. Notice any tension and let it soften.",
            "Hips & Lower Back" to "Focus on your hips and lower back. Breathe into any areas of tightness.",
            "Chest & Upper Back" to "Notice your chest rising and falling with each breath. Let your shoulders drop.",
            "Arms & Hands" to "Bring awareness to your arms and hands. Notice the weight of your hands resting.",
            "Neck & Head" to "Finally, focus on your neck and head. Relax your jaw and forehead."
        )
        else -> emptyList()
    }

    val actualStep = currentStep.coerceIn(0, steps.lastIndex)
    val (title, description) = steps.getOrElse(actualStep) { "Complete" to "Well done!" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Progress
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Step ${actualStep + 1} of ${steps.size}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { (actualStep + 1).toFloat() / steps.size },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = ProdyAccentGreen,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }

        // Main content
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Step icon/number
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(ProdyAccentGreen.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                if (exerciseType == ExerciseType.GROUNDING_54321) {
                    Text(
                        text = (5 - actualStep).toString(),
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = ProdyAccentGreen
                    )
                } else {
                    Icon(
                        imageVector = ProdyIcons.SelfImprovement,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = ProdyAccentGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (actualStep < steps.lastIndex) {
                Button(
                    onClick = onNextStep,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = ProdyAccentGreen)
                ) {
                    Text("Next Step", color = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = ProdyIcons.ArrowForward,
                        contentDescription = null,
                        tint = Color.Black
                    )
                }
            } else {
                Button(
                    onClick = onComplete,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = ProdyAccentGreen)
                ) {
                    Text("Complete", color = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = ProdyIcons.Check,
                        contentDescription = null,
                        tint = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
private fun ThoughtExerciseContent(
    exerciseType: ExerciseType,
    currentStep: Int,
    totalSteps: Int,
    instruction: String,
    onNextStep: () -> Unit,
    onComplete: () -> Unit
) {
    val steps = when (exerciseType) {
        ExerciseType.THOUGHT_RECORD -> listOf(
            "Situation" to "What happened? Describe the situation that triggered your feelings.",
            "Emotions" to "What emotions did you feel? Rate their intensity from 1-10.",
            "Automatic Thoughts" to "What thoughts went through your mind? Write them exactly as they occurred.",
            "Evidence For" to "What evidence supports this thought? Be specific and factual.",
            "Evidence Against" to "What evidence contradicts this thought? Consider alternative explanations.",
            "Balanced Thought" to "What's a more balanced way to think about this situation?"
        )
        else -> listOf(
            "Identify the Thought" to "What negative thought are you having right now? Write it down.",
            "Examine the Evidence" to "Is this thought based on facts or feelings? What's the actual evidence?",
            "Alternative Perspective" to "How might someone else view this situation? What would you tell a friend?",
            "Reframe" to "Create a more balanced, realistic thought to replace the negative one."
        )
    }

    val actualStep = currentStep.coerceIn(0, steps.lastIndex)
    val (title, description) = steps.getOrElse(actualStep) { "Complete" to "Well done!" }

    var userInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Progress
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Step ${actualStep + 1} of ${steps.size}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { (actualStep + 1).toFloat() / steps.size },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = ProdyAccentGreen,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Step title
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Input field
        OutlinedTextField(
            value = userInput,
            onValueChange = { userInput = it },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            placeholder = {
                Text(
                    "Write your thoughts here...",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ProdyAccentGreen,
                cursorColor = ProdyAccentGreen
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (actualStep < steps.lastIndex) {
                Button(
                    onClick = {
                        userInput = ""
                        onNextStep()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = ProdyAccentGreen)
                ) {
                    Text("Next Step", color = Color.Black)
                }
            } else {
                Button(
                    onClick = onComplete,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = ProdyAccentGreen)
                ) {
                    Text("Complete", color = Color.Black)
                }
            }
        }
    }
}

@Composable
private fun RelaxationExerciseContent(
    currentStep: Int,
    totalSteps: Int,
    instruction: String,
    onNextStep: () -> Unit,
    onComplete: () -> Unit
) {
    val steps = listOf(
        "Right Hand & Forearm" to "Make a fist with your right hand. Squeeze tightly for 5 seconds, then release and notice the relaxation.",
        "Right Upper Arm" to "Bend your elbow and tense your bicep. Hold for 5 seconds, then release.",
        "Left Hand & Forearm" to "Make a fist with your left hand. Squeeze tightly for 5 seconds, then release.",
        "Left Upper Arm" to "Bend your elbow and tense your bicep. Hold for 5 seconds, then release.",
        "Forehead" to "Raise your eyebrows as high as you can. Hold for 5 seconds, then release.",
        "Eyes & Cheeks" to "Squeeze your eyes shut tightly. Hold for 5 seconds, then release.",
        "Jaw" to "Clench your jaw tightly. Hold for 5 seconds, then release.",
        "Neck & Shoulders" to "Raise your shoulders toward your ears. Hold for 5 seconds, then release.",
        "Chest" to "Take a deep breath and hold it, tensing your chest. Hold for 5 seconds, then exhale and release.",
        "Stomach" to "Tighten your stomach muscles. Hold for 5 seconds, then release.",
        "Right Leg" to "Tense your right thigh, calf, and foot. Hold for 5 seconds, then release.",
        "Left Leg" to "Tense your left thigh, calf, and foot. Hold for 5 seconds, then release."
    )

    val actualStep = currentStep.coerceIn(0, steps.lastIndex)
    val (title, description) = steps.getOrElse(actualStep) { "Complete" to "Well done!" }

    // Timer for each step
    var timeRemaining by remember(actualStep) { mutableIntStateOf(15) }

    LaunchedEffect(actualStep) {
        timeRemaining = 15
        while (timeRemaining > 0) {
            delay(1000)
            timeRemaining--
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Progress
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Step ${actualStep + 1} of ${steps.size}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { (actualStep + 1).toFloat() / steps.size },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = ProdyAccentGreen,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }

        // Main content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.weight(1f)
        ) {
            // Timer circle
            Box(
                modifier = Modifier
                    .size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { timeRemaining / 15f },
                    modifier = Modifier.fillMaxSize(),
                    color = ProdyAccentGreen,
                    strokeWidth = 8.dp,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Text(
                    text = "${timeRemaining}s",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = ProdyAccentGreen
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // Navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (actualStep < steps.lastIndex) {
                Button(
                    onClick = onNextStep,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = ProdyAccentGreen),
                    enabled = timeRemaining == 0
                ) {
                    Text(
                        if (timeRemaining > 0) "Wait..." else "Next Step",
                        color = Color.Black
                    )
                }
            } else {
                Button(
                    onClick = onComplete,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = ProdyAccentGreen),
                    enabled = timeRemaining == 0
                ) {
                    Text("Complete", color = Color.Black)
                }
            }
        }
    }
}

@Composable
private fun ExerciseCompletionScreen(
    exerciseType: ExerciseType,
    totalDuration: Long,
    onDone: () -> Unit
) {
    val durationMinutes = (totalDuration / 60000).toInt()
    val durationSeconds = ((totalDuration % 60000) / 1000).toInt()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Success icon
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(ProdySuccess.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = ProdyIcons.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = ProdySuccess
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Exercise Complete!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Great work completing ${exerciseType.displayName}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Stats
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = exerciseType.icon,
                        contentDescription = null,
                        tint = ProdyAccentGreen,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Exercise",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (durationMinutes > 0) "${durationMinutes}m ${durationSeconds}s" else "${durationSeconds}s",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = ProdyAccentGreen
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Duration",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onDone,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = ProdyAccentGreen)
        ) {
            Text("Done", color = Color.Black)
        }
    }
}
