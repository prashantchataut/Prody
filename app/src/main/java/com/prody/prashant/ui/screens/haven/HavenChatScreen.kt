package com.prody.prashant.ui.screens.haven

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.R
import com.prody.prashant.domain.haven.*
import com.prody.prashant.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

/**
 * Haven Chat Screen - Therapeutic conversation interface
 *
 * Features:
 * - Chat-like message display
 * - Typing indicator
 * - Exercise suggestions
 * - Crisis resources when needed
 * - Session completion with mood tracking
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HavenChatScreen(
    sessionType: SessionType,
    sessionId: Long? = null,
    onNavigateBack: () -> Unit,
    onNavigateToExercise: (ExerciseType) -> Unit,
    onSessionComplete: () -> Unit,
    viewModel: HavenViewModel = hiltViewModel()
) {
    val uiState by viewModel.chatState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val focusManager = LocalFocusManager.current

    var messageInput by remember { mutableStateOf("") }
    var showMoodDialog by remember { mutableStateOf(false) }
    var showCompletionDialog by remember { mutableStateOf(false) }

    // Start or resume session
    LaunchedEffect(sessionId, sessionType) {
        if (sessionId != null) {
            viewModel.resumeSession(sessionId)
        } else {
            viewModel.startSession(sessionType)
        }
    }

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            delay(100)
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = sessionType.displayName,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (uiState.isTyping) {
                            Text(
                                text = "Haven is thinking...",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (!uiState.isCompleted && uiState.messages.isNotEmpty()) {
                            showCompletionDialog = true
                        } else {
                            onNavigateBack()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    if (!uiState.isCompleted) {
                        IconButton(onClick = { showCompletionDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "End Session"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        bottomBar = {
            if (!uiState.isCompleted) {
                ChatInputBar(
                    value = messageInput,
                    onValueChange = { messageInput = it },
                    onSend = {
                        if (messageInput.isNotBlank()) {
                            viewModel.sendMessage(messageInput.trim())
                            messageInput = ""
                            focusManager.clearFocus()
                        }
                    },
                    isLoading = uiState.isTyping,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.isLoading && uiState.messages.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ProdyAccentGreen)
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Crisis resources banner (if applicable)
                    AnimatedVisibility(visible = uiState.showCrisisResources) {
                        CrisisResourcesBanner(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    // Messages list
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = uiState.messages,
                            key = { message -> message.id }
                        ) { message ->
                            MessageBubble(
                                message = message,
                                onExerciseClick = { exerciseType ->
                                    onNavigateToExercise(exerciseType)
                                }
                            )
                        }

                        // Typing indicator
                        if (uiState.isTyping) {
                            item(key = "typing") {
                                TypingIndicator()
                            }
                        }

                        // Suggested exercise (if any)
                        uiState.suggestedExercise?.let { exercise ->
                            item(key = "suggested_exercise") {
                                SuggestedExerciseCard(
                                    exerciseType = exercise,
                                    onClick = { onNavigateToExercise(exercise) }
                                )
                            }
                        }

                        // Session summary (if completed)
                        uiState.summary?.let { summary ->
                            item(key = "summary") {
                                SessionSummaryCard(
                                    summary = summary,
                                    onDone = onSessionComplete
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Mood selection dialog for completion
    if (showMoodDialog) {
        MoodSelectionDialog(
            onMoodSelected = { mood ->
                viewModel.completeSession(mood)
                showMoodDialog = false
            },
            onDismiss = { showMoodDialog = false }
        )
    }

    // Completion confirmation dialog
    if (showCompletionDialog) {
        AlertDialog(
            onDismissRequest = { showCompletionDialog = false },
            title = { Text("End Session?") },
            text = { Text("Would you like to complete this session? You can rate how you're feeling now.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showCompletionDialog = false
                        showMoodDialog = true
                    }
                ) {
                    Text("End & Rate", color = ProdyAccentGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showCompletionDialog = false
                    onNavigateBack()
                }) {
                    Text("Just Leave")
                }
            }
        )
    }

    // Error handling
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // Show error then clear
            viewModel.clearChatError()
        }
    }
}

@Composable
private fun ChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = {
                    Text(
                        "Share what's on your mind...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(onSend = { onSend() }),
                singleLine = false,
                maxLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onSend,
                enabled = value.isNotBlank() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = ProdyAccentGreen
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (value.isNotBlank()) ProdyAccentGreen
                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(
    message: HavenMessage,
    onExerciseClick: (ExerciseType) -> Unit,
    modifier: Modifier = Modifier
) {
    val alignment = if (message.isUser) Alignment.End else Alignment.Start
    val backgroundColor = if (message.isUser) {
        ProdyAccentGreen
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = if (message.isUser) {
        Color.Black
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Card(
            modifier = Modifier.widthIn(max = 300.dp),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isUser) 16.dp else 4.dp,
                bottomEnd = if (message.isUser) 4.dp else 16.dp
            ),
            colors = CardDefaults.cardColors(containerColor = backgroundColor)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.content,
                    color = textColor,
                    style = MaterialTheme.typography.bodyMedium
                )

                // Technique badge
                message.techniqueUsed?.let { technique ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = textColor.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = technique.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = textColor.copy(alpha = 0.7f),
                            fontStyle = FontStyle.Italic
                        )
                    }
                }

                // Exercise suggestion button
                message.exerciseSuggested?.let { exercise ->
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { onExerciseClick(exercise) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (message.isUser) Color.Black else ProdyAccentGreen
                        )
                    ) {
                        Text("${exercise.icon} Try ${exercise.displayName}")
                    }
                }
            }
        }

        // Timestamp
        Text(
            text = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(message.timestamp)),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun TypingIndicator(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(3) { index ->
            val delay = index * 100
            var visible by remember { mutableStateOf(true) }

            LaunchedEffect(Unit) {
                while (true) {
                    delay(delay.toLong())
                    visible = !visible
                    delay(300)
                }
            }

            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        if (visible) ProdyAccentGreen.copy(alpha = 0.8f)
                        else ProdyAccentGreen.copy(alpha = 0.3f)
                    )
            )
        }
    }
}

@Composable
private fun CrisisResourcesBanner(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.HealthAndSafety,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Support is available",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Call 988 for immediate crisis support",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun SuggestedExerciseCard(
    exerciseType: ExerciseType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = ProdyAccentGreen.copy(alpha = 0.1f)
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(ProdyAccentGreen.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = exerciseType.icon, fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Try an Exercise",
                    style = MaterialTheme.typography.labelMedium,
                    color = ProdyAccentGreen
                )
                Text(
                    text = exerciseType.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${exerciseType.estimatedDuration / 60} minutes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Start",
                tint = ProdyAccentGreen
            )
        }
    }
}

@Composable
private fun SessionSummaryCard(
    summary: SessionSummary,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = ProdySuccess
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Session Complete",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryStatItem(
                    value = "${summary.duration / 60000}",
                    label = "Minutes"
                )
                SummaryStatItem(
                    value = summary.messageCount.toString(),
                    label = "Messages"
                )
                summary.moodChange?.let { change ->
                    SummaryStatItem(
                        value = if (change >= 0) "+$change" else change.toString(),
                        label = "Mood Change"
                    )
                }
            }

            // Techniques used
            if (summary.techniquesUsed.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Techniques Explored",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    summary.techniquesUsed.forEach { technique ->
                        AssistChip(
                            onClick = { },
                            label = { Text(technique.displayName, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }
            }

            // Key insights
            if (summary.keyInsights.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Key Insights",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                summary.keyInsights.forEach { insight ->
                    Text(
                        text = "• $insight",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onDone,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = ProdyAccentGreen)
            ) {
                Text("Done", color = Color.Black)
            }
        }
    }
}

@Composable
private fun rememberScrollState() = androidx.compose.foundation.rememberScrollState()

@Composable
private fun Row.horizontalScroll(state: androidx.compose.foundation.ScrollState): Modifier {
    return Modifier.horizontalScroll(state)
}

@Composable
private fun SummaryStatItem(
    value: String,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = ProdyAccentGreen
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun MoodSelectionDialog(
    onMoodSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "How are you feeling now?",
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
                    text = "Rate your mood from 1 (worst) to 10 (best)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf(1, 3, 5, 7, 10).forEach { mood ->
                        val emoji = when (mood) {
                            1 -> "😔"
                            3 -> "😕"
                            5 -> "😐"
                            7 -> "🙂"
                            10 -> "😊"
                            else -> "😐"
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onMoodSelected(mood) }
                                .padding(8.dp)
                        ) {
                            Text(text = emoji, fontSize = 28.sp)
                            Text(
                                text = mood.toString(),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onMoodSelected(5) }) {
                Text("Skip")
            }
        }
    )
}

@Composable
private fun Column.clickable(onClick: () -> Unit): Modifier {
    return Modifier.clickable(onClick = onClick)
}
