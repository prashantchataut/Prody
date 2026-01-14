package com.prody.prashant.ui.screens.learning
import com.prody.prashant.ui.icons.ProdyIcons

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.R
import com.prody.prashant.domain.learning.*
import com.prody.prashant.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Lesson Screen - Individual lesson content display
 *
 * Adapts UI based on lesson type:
 * - Reading: Scrollable content with sections
 * - Reflection: Text input with prompts
 * - Exercise: Step-by-step guidance
 * - Journal Prompt: Writing prompt with guidance
 * - Meditation: Timer with guidance
 * - Quiz: Multiple choice questions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(
    pathId: String,
    lessonId: String,
    onNavigateBack: () -> Unit,
    onLessonComplete: () -> Unit,
    viewModel: LessonViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.lesson?.title ?: "Loading...",
                        fontWeight = FontWeight.SemiBold
                    )
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
                        CircularProgressIndicator(color = ProdyAccentGreen)
                    }
                }
                uiState.content != null -> {
                    when (val content = uiState.content) {
                        is LessonContent.Reading -> ReadingContent(
                            content = content,
                            onComplete = { viewModel.completeLesson() }
                        )
                        is LessonContent.Reflection -> ReflectionContent(
                            content = content,
                            text = uiState.reflectionText,
                            onTextChange = viewModel::onReflectionTextChanged,
                            isSaving = uiState.isSavingReflection,
                            onSave = { viewModel.saveReflection() }
                        )
                        is LessonContent.Exercise -> ExerciseContent(
                            content = content,
                            onComplete = { viewModel.completeLesson() }
                        )
                        is LessonContent.JournalPrompt -> JournalPromptContent(
                            content = content,
                            text = uiState.reflectionText,
                            onTextChange = viewModel::onReflectionTextChanged,
                            isSaving = uiState.isSavingReflection,
                            onSave = { viewModel.saveReflection() }
                        )
                        is LessonContent.Meditation -> MeditationContent(
                            content = content,
                            timeRemaining = uiState.meditationTimeRemaining,
                            isPlaying = uiState.meditationIsPlaying,
                            onStart = viewModel::startMeditation,
                            onPause = viewModel::pauseMeditation,
                            onResume = viewModel::resumeMeditation,
                            onTimeUpdate = viewModel::updateMeditationTime,
                            onComplete = { viewModel.completeLesson() }
                        )
                        is LessonContent.Quiz -> QuizContent(
                            content = content,
                            answers = uiState.quizAnswers,
                            onAnswerSelected = viewModel::onQuizAnswerSelected,
                            showResults = uiState.showQuizResults,
                            score = uiState.quizScore,
                            onSubmit = { viewModel.submitQuiz() }
                        )
                        null -> {}
                    }
                }
            }
        }
    }

    // Completion Dialog
    if (uiState.showCompletionDialog) {
        LessonCompleteDialog(
            lessonTitle = uiState.lesson?.title ?: "",
            onDismiss = {
                viewModel.dismissCompletionDialog()
                onLessonComplete()
            }
        )
    }

    // Error handling
    uiState.errorMessage?.let { error ->
        LaunchedEffect(error) {
            viewModel.dismissError()
        }
    }
}

@Composable
private fun ReadingContent(
    content: LessonContent.Reading,
    onComplete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        content.sections.forEach { section ->
            ContentSectionItem(section = section)
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Key Takeaways
        if (content.keyTakeaways.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = ProdyAccentGreen.copy(alpha = 0.1f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = ProdyIcons.Lightbulb,
                            contentDescription = null,
                            tint = ProdyAccentGreen
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Key Takeaways",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    content.keyTakeaways.forEach { takeaway ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text(text = "✓ ", color = ProdyAccentGreen)
                            Text(
                                text = takeaway,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onComplete,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = ProdyAccentGreen)
        ) {
            Text("I've Read This", color = Color.Black)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun ContentSectionItem(section: ContentSection) {
    Column {
        Text(
            text = section.heading,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = section.body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (section.bulletPoints.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            section.bulletPoints.forEach { point ->
                Row(modifier = Modifier.padding(start = 8.dp, top = 4.dp)) {
                    Text(text = "• ", color = ProdyAccentGreen)
                    Text(
                        text = point,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        section.quote?.let { quote ->
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "\"$quote\"",
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic
                    )
                    section.quoteAuthor?.let { author ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "— $author",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReflectionContent(
    content: LessonContent.Reflection,
    text: String,
    onTextChange: (String) -> Unit,
    isSaving: Boolean,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = content.prompt,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        content.context?.let { ctx ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = ctx,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Guiding questions
        if (content.guidingQuestions.isNotEmpty()) {
            Text(
                text = "Consider:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            content.guidingQuestions.forEach { question ->
                Text(
                    text = "• $question",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            placeholder = {
                Text("Write your reflection here...")
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ProdyAccentGreen,
                cursorColor = ProdyAccentGreen
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${text.split("\\s+".toRegex()).filter { it.isNotBlank() }.size} words",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            content.minWords?.let { min ->
                Text(
                    text = "Min: $min words",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSaving && text.isNotBlank(),
            colors = ButtonDefaults.buttonColors(containerColor = ProdyAccentGreen)
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.Black,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Save Reflection", color = Color.Black)
            }
        }
    }
}

@Composable
private fun ExerciseContent(
    content: LessonContent.Exercise,
    onComplete: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = content.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = content.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Progress
        LinearProgressIndicator(
            progress = { (currentStep + 1).toFloat() / content.steps.size },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = ProdyAccentGreen,
            trackColor = ProdyAccentGreen.copy(alpha = 0.2f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Current step
        val step = content.steps[currentStep]
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(ProdyAccentGreen.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = step.stepNumber.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = ProdyAccentGreen
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = step.instruction,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                step.duration?.let { duration ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$duration seconds",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                step.tip?.let { tip ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(ProdyAccentGreen.copy(alpha = 0.1f))
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = ProdyIcons.Tips,
                            contentDescription = null,
                            tint = ProdyAccentGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = tip,
                            style = MaterialTheme.typography.bodySmall,
                            color = ProdyAccentGreen
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (currentStep > 0) {
                OutlinedButton(
                    onClick = { currentStep-- },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Previous")
                }
            }
            Button(
                onClick = {
                    if (currentStep < content.steps.lastIndex) {
                        currentStep++
                    } else {
                        onComplete()
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = ProdyAccentGreen)
            ) {
                Text(
                    if (currentStep < content.steps.lastIndex) "Next Step" else "Complete",
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
private fun JournalPromptContent(
    content: LessonContent.JournalPrompt,
    text: String,
    onTextChange: (String) -> Unit,
    isSaving: Boolean,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = ProdyAccentGreen.copy(alpha = 0.1f)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Icon(
                    imageVector = ProdyIcons.Create,
                    contentDescription = null,
                    tint = ProdyAccentGreen
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = content.prompt,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = content.context,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (content.guidingQuestions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            content.guidingQuestions.forEach { question ->
                Text(
                    text = "• $question",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            placeholder = {
                Text("Start writing...")
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ProdyAccentGreen,
                cursorColor = ProdyAccentGreen
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Suggested: ${content.suggestedLength}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSaving && text.isNotBlank(),
            colors = ButtonDefaults.buttonColors(containerColor = ProdyAccentGreen)
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.Black,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Save Entry", color = Color.Black)
            }
        }
    }
}

@Composable
private fun MeditationContent(
    content: LessonContent.Meditation,
    timeRemaining: Int,
    isPlaying: Boolean,
    onStart: (Int) -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onTimeUpdate: (Int) -> Unit,
    onComplete: () -> Unit
) {
    var selectedDuration by remember { mutableIntStateOf(content.durationOptions.firstOrNull() ?: 5) }
    val isStarted = timeRemaining > 0 || isPlaying

    // Timer countdown
    LaunchedEffect(isPlaying, timeRemaining) {
        if (isPlaying && timeRemaining > 0) {
            delay(1000)
            onTimeUpdate(timeRemaining - 1)
        } else if (isPlaying && timeRemaining == 0) {
            onComplete()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = content.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = content.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (!isStarted) {
            // Duration selection
            Text(
                text = "Select Duration",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                content.durationOptions.forEach { duration ->
                    FilterChip(
                        selected = selectedDuration == duration,
                        onClick = { selectedDuration = duration },
                        label = { Text("$duration min") }
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        // Timer circle
        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = {
                    if (isStarted) {
                        timeRemaining.toFloat() / (selectedDuration * 60)
                    } else 1f
                },
                modifier = Modifier.fillMaxSize(),
                color = ProdyAccentGreen,
                strokeWidth = 8.dp,
                trackColor = ProdyAccentGreen.copy(alpha = 0.2f)
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val displayTime = if (isStarted) timeRemaining else selectedDuration * 60
                val minutes = displayTime / 60
                val seconds = displayTime % 60
                Text(
                    text = String.format("%02d:%02d", minutes, seconds),
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = ProdyAccentGreen
                )
                if (isPlaying) {
                    Text(
                        text = "Breathe...",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Guidance text
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Text(
                text = content.guidanceText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Control button
        if (!isStarted) {
            Button(
                onClick = { onStart(selectedDuration) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = ProdyAccentGreen)
            ) {
                Icon(ProdyIcons.PlayArrow, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Begin Meditation", color = Color.Black)
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = if (isPlaying) onPause else onResume,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        if (isPlaying) ProdyIcons.Pause else ProdyIcons.PlayArrow,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isPlaying) "Pause" else "Resume")
                }
                Button(
                    onClick = onComplete,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = ProdyAccentGreen)
                ) {
                    Text("End Early", color = Color.Black)
                }
            }
        }
    }
}

@Composable
private fun QuizContent(
    content: LessonContent.Quiz,
    answers: Map<String, Int>,
    onAnswerSelected: (String, Int) -> Unit,
    showResults: Boolean,
    score: Int,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = content.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = content.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        content.questions.forEachIndexed { index, question ->
            QuestionItem(
                questionNumber = index + 1,
                question = question,
                selectedAnswer = answers[question.id],
                onAnswerSelected = { onAnswerSelected(question.id, it) },
                showResult = showResults
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (showResults) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (score >= content.passingScore)
                        ProdySuccess.copy(alpha = 0.1f)
                    else
                        ProdyError.copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (score >= content.passingScore) "🎉 Great job!" else "Keep learning!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "You scored $score%",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (score >= content.passingScore) ProdySuccess else ProdyError
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Passing score: ${content.passingScore}%",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth(),
                enabled = answers.size == content.questions.size,
                colors = ButtonDefaults.buttonColors(containerColor = ProdyAccentGreen)
            ) {
                Text("Submit Quiz", color = Color.Black)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun QuestionItem(
    questionNumber: Int,
    question: LearningQuizQuestion,
    selectedAnswer: Int?,
    onAnswerSelected: (Int) -> Unit,
    showResult: Boolean
) {
    val isCorrect = selectedAnswer == question.correctAnswer

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                showResult && isCorrect -> ProdySuccess.copy(alpha = 0.1f)
                showResult && !isCorrect && selectedAnswer != null -> ProdyError.copy(alpha = 0.1f)
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Question $questionNumber",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = question.question,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(12.dp))

            question.options.forEachIndexed { index, option ->
                val isSelected = selectedAnswer == index
                val isCorrectOption = index == question.correctAnswer

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .selectable(
                            selected = isSelected,
                            enabled = !showResult,
                            onClick = { onAnswerSelected(index) },
                            role = Role.RadioButton
                        )
                        .background(
                            when {
                                showResult && isCorrectOption -> ProdySuccess.copy(alpha = 0.2f)
                                showResult && isSelected && !isCorrect -> ProdyError.copy(alpha = 0.2f)
                                isSelected -> ProdyAccentGreen.copy(alpha = 0.2f)
                                else -> Color.Transparent
                            }
                        )
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = null,
                        colors = RadioButtonDefaults.colors(
                            selectedColor = when {
                                showResult && isCorrectOption -> ProdySuccess
                                showResult && isSelected && !isCorrect -> ProdyError
                                else -> ProdyAccentGreen
                            }
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = option,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (showResult && !isCorrect && selectedAnswer != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = question.explanation,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}

@Composable
private fun LessonCompleteDialog(
    lessonTitle: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "✨", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Lesson Complete!",
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Text(
                text = "Great work completing \"$lessonTitle\"! Keep up the momentum.",
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = ProdyAccentGreen)
            ) {
                Text("Continue", color = Color.Black)
            }
        }
    )
}
