package com.prody.prashant.ui.screens.haven
import com.prody.prashant.ui.icons.ProdyIcons

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext as ComposeLocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.prody.prashant.R
import com.prody.prashant.domain.haven.*
import com.prody.prashant.ui.theme.*
import com.prody.prashant.util.SecureScreen
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
    val isDark = isSystemInDarkTheme()

    var messageInput by remember { mutableStateOf("") }
    var showMoodDialog by remember { mutableStateOf(false) }
    var showCompletionDialog by remember { mutableStateOf(false) }
    var showSoftMenu by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Security: Prevent screenshots and screen recordings while in a therapeutic chat
    SecureScreen()

    // Haven Theme Colors
    val havenBackground = if (isDark) HavenBackgroundDark else HavenBackgroundLight
    val havenText = if (isDark) HavenTextDark else HavenTextLight

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
                            style = HavenMessageStyle.copy(fontWeight = FontWeight.Bold),
                            color = havenText
                        )
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
                            imageVector = ProdyIcons.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = havenText
                        )
                    }
                },
                actions = {
                    // Soft Menu Button (The "Dot" or subtle icon)
                    Box {
                        IconButton(onClick = { showSoftMenu = true }) {
                            Icon(
                                imageVector = ProdyIcons.MoreVert, // Or a custom "Soft Dot" icon
                                contentDescription = "Haven Menu",
                                tint = havenText
                            )
                        }
                        
                        DropdownMenu(
                            expanded = showSoftMenu,
                            onDismissRequest = { showSoftMenu = false },
                            modifier = Modifier.background(if(isDark) HavenUserBubbleDark else HavenUserBubbleLight)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Breathe", fontFamily = PoppinsFamily) },
                                onClick = { 
                                    showSoftMenu = false
                                    onNavigateToExercise(ExerciseType.BOX_BREATHING) 
                                },
                                leadingIcon = { Icon(ProdyIcons.Spa, null) }
                            )
                            DropdownMenuItem(
                                text = { Text("People who can help", fontFamily = PoppinsFamily) },
                                onClick = { 
                                    showSoftMenu = false
                                    // In a real app, navigate to resources. For now, trigger crisis banner.
                                    viewModel.showCrisisResources() 
                                },
                                leadingIcon = { Icon(ProdyIcons.HealthAndSafety, null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Reflect & End", fontFamily = PoppinsFamily) },
                                onClick = { 
                                    showSoftMenu = false
                                    showCompletionDialog = true
                                },
                                leadingIcon = { Icon(ProdyIcons.CheckCircle, null) }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = havenText,
                    actionIconContentColor = havenText,
                    navigationIconContentColor = havenText
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
                        .padding(16.dp), // Increased padding
                    isDark = isDark
                )
            }
        },
        containerColor = havenBackground
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
                    CircularProgressIndicator(color = HavenBubbleLight) 
                }
            } else if (uiState.messages.isEmpty()) {
                // Empty State / Welcome
                Box(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Start a conversation",
                            style = MaterialTheme.typography.titleMedium,
                            color = havenText.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Haven is here to listen. Say hello!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = havenText.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Persistent Error Banner (if error exists)
                    if (uiState.error != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = uiState.error ?: "Unknown error",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

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
                        verticalArrangement = Arrangement.spacedBy(16.dp) // More breathing room
                    ) {
                        items(
                            items = uiState.messages,
                            key = { message -> message.id }
                        ) { message ->
                            MessageBubble(
                                message = message,
                                onExerciseClick = { exerciseType ->
                                    onNavigateToExercise(exerciseType)
                                },
                                isDark = isDark
                            )
                        }

                        // Typing indicator (Subtle Glow)
                        if (uiState.isTyping) {
                            item(key = "typing") {
                                TypingIndicator(isDark = isDark)
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

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun ChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    isDark: Boolean = false
) {
    val containerColor = if (isDark) HavenUserBubbleDark else HavenUserBubbleLight
    val contentColor = if (isDark) HavenTextDark else HavenTextLight
    val context = LocalContext.current
    
    // Microphone permission state using Accompanist
    val microphonePermissionState = rememberPermissionState(
        android.Manifest.permission.RECORD_AUDIO
    )
    
    // Speech Recognizer State
    var isListening by remember { mutableStateOf(false) }
    var showPermissionRationale by remember { mutableStateOf(false) }
    val speechRecognizer = remember { android.speech.SpeechRecognizer.createSpeechRecognizer(context) }
    val intent = remember {
        android.content.Intent(android.speech.RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE_MODEL, android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            try {
                speechRecognizer.destroy()
            } catch (e: Exception) {
                // Ignore destruction errors
            }
        }
    }
    
    // Permission rationale dialog
    if (showPermissionRationale) {
        AlertDialog(
            onDismissRequest = { showPermissionRationale = false },
            title = { Text("Microphone Permission Needed") },
            text = { Text("Haven needs microphone access to transcribe your voice into text. This helps you express yourself more naturally.") },
            confirmButton = {
                TextButton(onClick = {
                    showPermissionRationale = false
                    microphonePermissionState.launchPermissionRequest()
                }) {
                    Text("Grant Permission", color = ProdyAccentGreen)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionRationale = false }) {
                    Text("Not Now")
                }
            }
        )
    }

    Surface(
        modifier = modifier.imePadding(), // Fix keyboard overlap
        shape = RoundedCornerShape(24.dp),
        color = containerColor,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Microphone Button (Real-time Dictation)
            IconButton(
                onClick = { 
                    if (isListening) {
                        try {
                            speechRecognizer.stopListening()
                        } catch(e: Exception) {
                            // Ignore stop errors
                        }
                        isListening = false
                    } else {
                        // Check permission using Accompanist
                        when {
                            microphonePermissionState.status.isGranted -> {
                                // Permission granted, start listening
                                try {
                                    isListening = true
                                    speechRecognizer.setRecognitionListener(object : android.speech.RecognitionListener {
                                        override fun onReadyForSpeech(params: android.os.Bundle?) {}
                                        override fun onBeginningOfSpeech() {}
                                        override fun onRmsChanged(rmsdB: Float) {}
                                        override fun onBufferReceived(buffer: ByteArray?) {}
                                        override fun onEndOfSpeech() { isListening = false }
                                        override fun onError(error: Int) { 
                                            isListening = false 
                                            val errorMsg = when (error) {
                                                android.speech.SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                                                android.speech.SpeechRecognizer.ERROR_CLIENT -> "Client error"
                                                android.speech.SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Permission denied"
                                                android.speech.SpeechRecognizer.ERROR_NETWORK -> "Network error"
                                                android.speech.SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                                                android.speech.SpeechRecognizer.ERROR_NO_MATCH -> "No speech detected"
                                                android.speech.SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer busy"
                                                android.speech.SpeechRecognizer.ERROR_SERVER -> "Server error"
                                                android.speech.SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Speech timeout"
                                                else -> "Voice error"
                                            }
                                            if (error != android.speech.SpeechRecognizer.ERROR_NO_MATCH) {
                                                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                        override fun onResults(results: android.os.Bundle?) {
                                            val matches = results?.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION)
                                            if (!matches.isNullOrEmpty()) {
                                                onValueChange(value + (if (value.isNotEmpty()) " " else "") + matches[0])
                                            }
                                            isListening = false
                                        }
                                        override fun onPartialResults(partialResults: android.os.Bundle?) {}
                                        override fun onEvent(eventType: Int, params: android.os.Bundle?) {}
                                    })
                                    speechRecognizer.startListening(intent)
                                } catch (e: Exception) {
                                    isListening = false
                                    Toast.makeText(context, "Voice error: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                            microphonePermissionState.status.shouldShowRationale -> {
                                // Show rationale dialog
                                showPermissionRationale = true
                            }
                            else -> {
                                // Request permission directly
                                microphonePermissionState.launchPermissionRequest()
                            }
                        }
                    }
                }
            ) {
                // Pulse effect if listening
                val micColor = if(isListening) HavenAccentRose else contentColor.copy(alpha = 0.6f)
                val infiniteTransition = rememberInfiniteTransition(label = "RecPulse")
                val scale by if(isListening) infiniteTransition.animateFloat(
                    initialValue = 1f, targetValue = 1.2f,
                    animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse), label = "RecScale"
                ) else remember { mutableStateOf(1f) }

                Icon(
                    imageVector = if(isListening) ProdyIcons.Mic else ProdyIcons.MicNone,
                    contentDescription = "Dictate",
                    tint = micColor,
                    modifier = Modifier.scale(scale)
                )
            }

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = {
                    Text(
                        if(isListening) "Listening..." else "Share your thoughts...",
                        color = contentColor.copy(alpha = 0.5f),
                        style = HavenInputStyle
                    )
                },
                modifier = Modifier.weight(1f),
                textStyle = HavenInputStyle.copy(color = contentColor),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(onSend = { onSend() }),
                singleLine = false,
                maxLines = 4,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = HavenBubbleLight
                )
            )

            Spacer(modifier = Modifier.width(4.dp))

            IconButton(
                onClick = onSend,
                enabled = value.isNotBlank() && !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = HavenBubbleLight
                    )
                } else {
                    Icon(
                        imageVector = ProdyIcons.Send,
                        contentDescription = "Send",
                        tint = if (value.isNotBlank()) HavenBubbleLight
                        else contentColor.copy(alpha = 0.3f)
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
    modifier: Modifier = Modifier,
    isDark: Boolean = false
) {
    val alignment = if (message.isUser) Alignment.End else Alignment.Start
    
    // Haven Aesthetic logic
    val backgroundColor = if (message.isUser) {
        if (isDark) HavenUserBubbleDark else HavenUserBubbleLight
    } else {
        if (isDark) HavenBubbleDark else HavenBubbleLight
    }
    
    val textColor = if (message.isUser) {
        if (isDark) HavenTextDark else HavenTextLight
    } else {
        // Haven's text needs to be readable on the blush/rose background
        if (isDark) Color(0xFFF0EAE2) else Color(0xFF2D2424)
    }

    val textStyle = if (message.isUser) HavenInputStyle else HavenMessageStyle
    var showRecallPopup by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Card(
            modifier = Modifier.widthIn(max = 300.dp),
            shape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
                bottomStart = if (message.isUser) 20.dp else 4.dp,
                bottomEnd = if (message.isUser) 4.dp else 20.dp
            ),
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Flat design
        ) {
            Column(modifier = Modifier.padding(16.dp)) { // More padding for "letter" feel
                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        text = message.content,
                        color = textColor,
                        style = textStyle,
                        modifier = Modifier.weight(1f)
                    )

                    // Recall Icon
                    if (message.recalledMessage != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = ProdyIcons.History,
                            contentDescription = "Recall past memory",
                            tint = HavenAccentGold,
                            modifier = Modifier
                                .size(18.dp)
                                .clickable { showRecallPopup = !showRecallPopup }
                        )
                    }
                }

                // Recall Content Popup
                AnimatedVisibility(visible = showRecallPopup) {
                    Card(
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = textColor.copy(alpha = 0.05f)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, textColor.copy(alpha = 0.1f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "What you said before:",
                                style = MaterialTheme.typography.labelSmall,
                                color = textColor.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = message.recalledMessage ?: "",
                                style = HavenMessageStyle.copy(fontSize = 14.sp),
                                color = textColor.copy(alpha = 0.8f),
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }
                }

                // Technique badge
                message.techniqueUsed?.let { technique ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = ProdyIcons.Psychology,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = textColor.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = technique.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = textColor.copy(alpha = 0.6f),
                            fontStyle = FontStyle.Italic
                        )
                    }
                }

                // Exercise suggestion button
                message.exerciseSuggested?.let { exercise ->
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { onExerciseClick(exercise) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = textColor
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, textColor.copy(alpha = 0.3f))
                    ) {
                        Icon(
                            imageVector = exercise.icon,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Try ${exercise.displayName}", fontFamily = PoppinsFamily)
                    }
                }
            }
        }

        // Timestamp
        Text(
            text = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(message.timestamp)),
            style = MaterialTheme.typography.labelSmall,
            color = (if(isDark) HavenTextDark else HavenTextLight).copy(alpha = 0.4f),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun TypingIndicator(
    modifier: Modifier = Modifier,
    isDark: Boolean = false
) {
    val glowColor = if (isDark) HavenBubbleDark else HavenBubbleLight
    
    Box(
        modifier = modifier
            .padding(8.dp)
            .height(40.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        // Slow, calming color swell (Anti-AI loading)
        val infiniteTransition = rememberInfiniteTransition(label = "Swell")
        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.1f,
            targetValue = 0.4f,
            animationSpec = infiniteRepeatable(
                animation = tween(3000, easing = LinearOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "SwellAlpha"
        )
        
        val scale by infiniteTransition.animateFloat(
            initialValue = 0.8f,
            targetValue = 1.5f,
            animationSpec = infiniteRepeatable(
                animation = tween(3000, easing = LinearOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "SwellScale"
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .scale(scale)
                    .background(glowColor.copy(alpha = alpha), CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Haven is listening...",
                style = HavenMessageStyle.copy(
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic,
                    color = (if(isDark) HavenTextDark else HavenTextLight).copy(alpha = 0.6f)
                )
            )
        }
    }
}

@Composable
private fun CrisisResourcesBanner(
    modifier: Modifier = Modifier
) {
    // Use a warm, supportive color (Rose/Gold) instead of alarming red
    val containerColor = Color(0xFFFCE4EC) // Very light pink
    val iconColor = Color(0xFFC2185B) // Rose red
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = ProdyIcons.Favorite, // Heart instead of warning sign
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "You are not alone", // Warmer message
                    style = HavenMessageStyle.copy(fontWeight = FontWeight.Bold, fontSize = 16.sp),
                    color = Color(0xFF4A0F22)
                )
                Text(
                    text = "We can connect you with people who care.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF4A0F22).copy(alpha = 0.8f)
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
                Icon(
                    imageVector = exerciseType.icon,
                    contentDescription = null,
                    tint = ProdyAccentGreen,
                    modifier = Modifier.size(24.dp)
                )
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
                imageVector = ProdyIcons.PlayArrow,
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
                imageVector = ProdyIcons.CheckCircle,
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
                        val icon = when (mood) {
                            1 -> ProdyIcons.SentimentVeryDissatisfied
                            3 -> ProdyIcons.SentimentDissatisfied
                            5 -> ProdyIcons.SentimentNeutral
                            7 -> ProdyIcons.SentimentSatisfied
                            10 -> ProdyIcons.SentimentVerySatisfied
                            else -> ProdyIcons.SentimentNeutral
                        }
                        val iconColor = when (mood) {
                            1 -> Color(0xFF5E7CE2) // Blue/sad
                            3 -> Color(0xFF9B7BF7) // Purple/concerned
                            5 -> Color(0xFFE2A14A) // Amber/neutral
                            7 -> Color(0xFF7B61FF) // Violet/content
                            10 -> ProdyAccentGreen // Green/happy
                            else -> Color.Gray
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable(onClick = { onMoodSelected(mood) })
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = "Mood $mood",
                                tint = iconColor,
                                modifier = Modifier.size(32.dp)
                            )
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

