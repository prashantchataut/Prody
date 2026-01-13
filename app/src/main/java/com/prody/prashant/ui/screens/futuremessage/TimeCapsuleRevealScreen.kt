package com.prody.prashant.ui.screens.futuremessage
import com.prody.prashant.ui.icons.ProdyIcons

import android.view.HapticFeedbackConstants
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

/**
 * Time Capsule Reveal Screen
 *
 * An immersive, emotional experience for opening messages from your past self.
 * Features:
 * - Beautiful envelope unfold animation
 * - Haptic feedback on reveal
 * - Premium typography and spacing
 * - Warm, encouraging prompts
 * - Actions: Reply, Favorite, Share
 *
 * This is a special moment - make it feel magical.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeCapsuleRevealScreen(
    messageId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToJournal: (String) -> Unit = {},
    viewModel: TimeCapsuleRevealViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val view = LocalView.current
    val scrollState = rememberScrollState()

    // Load message on first composition
    LaunchedEffect(messageId) {
        viewModel.loadMessage(messageId)
        delay(500) // Brief pause before starting reveal
        viewModel.reveal()
        // Haptic feedback on reveal
        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(ProdyIcons.Close, contentDescription = "Close")
                    }
                },
                actions = {
                    // Favorite button
                    IconButton(
                        onClick = {
                            viewModel.toggleFavorite()
                            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                        }
                    ) {
                        Icon(
                            imageVector = if (uiState.isFavorite) ProdyIcons.Favorite else ProdyIcons.FavoriteBorder,
                            contentDescription = if (uiState.isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = if (uiState.isFavorite) MaterialTheme.colorScheme.primary else LocalContentColor.current
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    )
                )
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    LoadingState()
                }
                uiState.error != null -> {
                    ErrorState(
                        error = uiState.error!!,
                        onRetry = { viewModel.retry(messageId) },
                        onBack = onNavigateBack
                    )
                }
                uiState.message != null -> {
                    RevealContent(
                        message = uiState.message!!,
                        isRevealed = uiState.hasBeenRevealed,
                        isFavorite = uiState.isFavorite,
                        hasReply = uiState.hasReply,
                        timeAgoText = uiState.timeAgoText,
                        onReply = { reflectionText ->
                            // Create journal entry as reply
                            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                                val journalId = viewModel.replyToPastSelf(reflectionText)
                                if (journalId != null) {
                                    // Optionally navigate to the journal entry
                                    view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                                }
                            }
                        },
                        onSaveToFavorites = {
                            viewModel.saveToFavorites()
                            view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                        },
                        scrollState = scrollState
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text(
                text = "Opening your time capsule...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorState(
    error: String,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = ProdyIcons.ErrorOutline,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                text = error,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onClick = onBack) {
                    Text("Go Back")
                }
                Button(onClick = onRetry) {
                    Text("Retry")
                }
            }
        }
    }
}

@Composable
private fun RevealContent(
    message: com.prody.prashant.data.local.entity.FutureMessageEntity,
    isRevealed: Boolean,
    isFavorite: Boolean,
    hasReply: Boolean,
    timeAgoText: String,
    onReply: (String) -> Unit,
    onSaveToFavorites: () -> Unit,
    scrollState: androidx.compose.foundation.ScrollState
) {
    var showReplyDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Envelope animation wrapper
        EnvelopeReveal(isRevealed = isRevealed) {
            // Header - Time capsule opened
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Time Capsule",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "From $timeAgoText ago",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatDate(message.createdAt),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Category badge
            CategoryBadge(category = message.category)

            Spacer(modifier = Modifier.height(24.dp))

            // Message title if exists
            if (message.title.isNotBlank()) {
                Text(
                    text = message.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Message content - the heart of the reveal
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.6f
                    ),
                    modifier = Modifier.padding(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Reflection prompt
            ReflectionPrompt(category = message.category)

            Spacer(modifier = Modifier.height(24.dp))

            // Action buttons
            ActionButtons(
                hasReply = hasReply,
                onReplyClick = { showReplyDialog = true },
                onSaveToFavorites = onSaveToFavorites,
                isFavorite = isFavorite
            )

            Spacer(modifier = Modifier.height(48.dp))
        }
    }

    // Reply dialog
    if (showReplyDialog) {
        ReplyDialog(
            onDismiss = { showReplyDialog = false },
            onConfirm = { reflectionText ->
                onReply(reflectionText)
                showReplyDialog = false
            }
        )
    }
}

@Composable
private fun EnvelopeReveal(
    isRevealed: Boolean,
    content: @Composable () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isRevealed) 1f else 0.92f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "envelope_scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isRevealed) 1f else 0f,
        animationSpec = tween(
            durationMillis = 600,
            easing = FastOutSlowInEasing
        ),
        label = "envelope_alpha"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .alpha(alpha)
    ) {
        content()
    }
}

@Composable
private fun CategoryBadge(category: String) {
    val (icon, label, color) = when (category) {
        "goal" -> Triple(ProdyIcons.Flag, "Goal", Color(0xFF4CAF50))
        "promise" -> Triple(ProdyIcons.Outlined.Handshake, "Promise", Color(0xFF2196F3))
        "motivation" -> Triple(ProdyIcons.LocalFireDepartment, "Motivation", Color(0xFFFF9800))
        "reminder" -> Triple(ProdyIcons.NotificationsActive, "Reminder", Color(0xFF9C27B0))
        else -> Triple(ProdyIcons.MailOutline, "Message", Color(0xFF607D8B))
    }

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = color,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ReflectionPrompt(category: String) {
    val prompt = when (category) {
        "goal" -> "How does this goal look from where you are now?"
        "promise" -> "Did you keep this promise to yourself?"
        "motivation" -> "Do these words still move you?"
        "reminder" -> "What memories does this bring back?"
        else -> "How does this land now?"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = ProdyIcons.Psychology,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = prompt,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ActionButtons(
    hasReply: Boolean,
    onReplyClick: () -> Unit,
    onSaveToFavorites: () -> Unit,
    isFavorite: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Reply to past self button
        Button(
            onClick = onReplyClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            Icon(
                imageVector = if (hasReply) ProdyIcons.CheckCircle else ProdyIcons.Edit,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (hasReply) "Reply Saved" else "Reply to Past Self",
                style = MaterialTheme.typography.labelLarge
            )
        }

        // Save to favorites (if not already favorited)
        if (!isFavorite) {
            OutlinedButton(
                onClick = onSaveToFavorites,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                Icon(
                    imageVector = ProdyIcons.BookmarkBorder,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Save to Favorites",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun ReplyDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var reflectionText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Reply to Your Past Self",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Take a moment to reflect. What would you say to who you were then?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = reflectionText,
                    onValueChange = { reflectionText = it },
                    label = { Text("Your reflection") },
                    placeholder = { Text("How do these words land now?...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    maxLines = 6,
                    textStyle = MaterialTheme.typography.bodyMedium
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(reflectionText) },
                enabled = reflectionText.isNotBlank()
            ) {
                Text("Save as Journal Entry")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun formatDate(timestamp: Long): String {
    val format = SimpleDateFormat("MMMM d, yyyy 'at' h:mm a", Locale.getDefault())
    return format.format(Date(timestamp))
}
