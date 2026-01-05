package com.prody.prashant.ui.screens.journal

import android.media.MediaPlayer
import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.prody.prashant.R
import com.prody.prashant.domain.model.Mood
import com.prody.prashant.ui.components.ContextualAiHint
import com.prody.prashant.ui.components.ProdyCard
import com.prody.prashant.ui.theme.*
import kotlinx.coroutines.delay
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import android.app.Activity
import android.view.WindowManager
import com.prody.prashant.util.AccessibilityUtils

@Composable
fun JournalDetailScreen(
    entryId: Long,
    onNavigateBack: () -> Unit,
    viewModel: JournalDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // Security: Prevent screenshots and screen recording of sensitive journal content.
    DisposableEffect(Unit) {
        val window = (context as? Activity)?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(entryId) {
        viewModel.loadEntry(entryId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Journal Entry") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.toggleBookmark() },
                        modifier = Modifier.size(48.dp) // Minimum touch target for accessibility
                    ) {
                        Icon(
                            imageVector = if (uiState.entry?.isBookmarked == true) Icons.Filled.Bookmark
                            else Icons.Filled.BookmarkBorder,
                            contentDescription = AccessibilityUtils.bookmarkDescription(uiState.entry?.isBookmarked == true),
                            tint = if (uiState.entry?.isBookmarked == true) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = { viewModel.showDeleteDialog() },
                        modifier = Modifier.size(48.dp) // Minimum touch target for accessibility
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = AccessibilityUtils.deleteDescription("journal entry"),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        val context = LocalContext.current

        // Loading state
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Loading entry...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            return@Scaffold
        }

        // Error state
        uiState.error?.let { error ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ErrorOutline,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    )
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = { viewModel.retry(entryId) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Retry")
                    }
                    TextButton(onClick = onNavigateBack) {
                        Text("Go Back")
                    }
                }
            }
            return@Scaffold
        }

        // Entry not found state
        if (uiState.entry == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.SearchOff,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "Entry not found",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "This journal entry may have been deleted.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = onNavigateBack,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Go Back")
                    }
                }
            }
            return@Scaffold
        }

        uiState.entry?.let { entry ->
            val mood = Mood.fromString(entry.mood)
            val dateFormat = remember { SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()) }
            val timeFormat = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) }

            // Parse media attachments
            val photos = remember(entry.attachedPhotos) { parseJsonArray(entry.attachedPhotos) }
            val videos = remember(entry.attachedVideos) { parseJsonArray(entry.attachedVideos) }
            val hasMedia = photos.isNotEmpty() || videos.isNotEmpty() || !entry.voiceRecordingUri.isNullOrEmpty()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header with mood and date
                ProdyCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    backgroundColor = mood.color.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(mood.color.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = mood.icon,
                                contentDescription = mood.displayName,
                                tint = mood.color,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = mood.displayName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = mood.color
                            )
                            Text(
                                text = dateFormat.format(Date(entry.createdAt)),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = timeFormat.format(Date(entry.createdAt)),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }

                        Surface(
                            shape = ChipShape,
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = "${entry.wordCount} words",
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Title (if present)
                if (entry.title.isNotBlank()) {
                    Text(
                        text = entry.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                // Voice Recording Playback (if present)
                entry.voiceRecordingUri?.let { voiceUri ->
                    if (voiceUri.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        VoiceRecordingPlayer(
                            voiceUri = voiceUri,
                            durationMs = entry.voiceRecordingDuration,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }

                // Media Gallery (Photos & Videos)
                if (photos.isNotEmpty() || videos.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    MediaGallerySection(
                        photos = photos,
                        videos = videos,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Journal content
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = if (entry.title.isNotBlank()) "Entry" else "Your Thoughts",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = entry.content,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.5
                    )
                }

                // AI Insights Section (emotion, themes, insight)
                if (entry.aiInsightGenerated && entry.aiInsight != null) {
                    Spacer(modifier = Modifier.height(24.dp))

                    AiInsightsCard(
                        emotionLabel = entry.aiEmotionLabel,
                        themes = entry.aiThemes?.split(",")?.filter { it.isNotBlank() } ?: emptyList(),
                        insight = entry.aiInsight,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                // Buddha's response
                entry.buddhaResponse?.let { response ->
                    Spacer(modifier = Modifier.height(24.dp))

                    // Show journal insight hint for first-time users
                    if (uiState.showJournalInsightHint) {
                        ContextualAiHint(
                            hint = viewModel.getJournalInsightHint(),
                            onDismiss = { viewModel.onJournalInsightHintDismiss() }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    ProdyCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .semantics {
                                contentDescription = AccessibilityUtils.buddhaResponseDescription(true)
                            },
                        backgroundColor = ProdyPrimary.copy(alpha = 0.08f)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.SelfImprovement,
                                    contentDescription = null, // Decorative, parent has description
                                    tint = ProdyPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = stringResource(R.string.buddha_response),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ProdyPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Parse and display Buddha's response with formatting
                            BuddhaResponseContent(response = response)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        // Delete confirmation dialog
        if (uiState.showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.hideDeleteDialog() },
                title = { Text("Delete Entry?") },
                text = { Text("This action cannot be undone. Are you sure you want to delete this journal entry?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteEntry()
                            onNavigateBack()
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.hideDeleteDialog() }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

// ============================================================================
// AI INSIGHTS CARD (Emotion, Themes, Insight)
// ============================================================================

@Composable
private fun AiInsightsCard(
    emotionLabel: String?,
    themes: List<String>,
    insight: String?,
    modifier: Modifier = Modifier
) {
    val insightColor = MoodCalm

    ProdyCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = insightColor.copy(alpha = 0.08f)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    tint = insightColor,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Buddha's Analysis",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = insightColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Emotion Label
            emotionLabel?.let { emotion ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Emotion:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = insightColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = emotion,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            color = insightColor,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Themes
            if (themes.isNotEmpty()) {
                Text(
                    text = "Themes:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    themes.take(4).forEach { theme ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = theme.trim(),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Insight
            insight?.let { insightText ->
                Text(
                    text = "Insight:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = insightText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.4
                )
            }

            // Generated by Buddha indicator
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Verified,
                    contentDescription = null,
                    tint = insightColor.copy(alpha = 0.6f),
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = "Generated by Buddha",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun BuddhaResponseContent(response: String) {
    val lines = response.split("\n")

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        lines.forEach { line ->
            when {
                line.startsWith("**") && line.endsWith("**") -> {
                    // Bold text (headers)
                    Text(
                        text = line.removeSurrounding("**"),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                line.startsWith("*") && line.endsWith("*") && !line.startsWith("**") -> {
                    // Italic text (wisdom quotes)
                    Text(
                        text = line.removeSurrounding("*"),
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        color = ProdyPrimary.copy(alpha = 0.9f)
                    )
                }
                line.isNotBlank() -> {
                    Text(
                        text = line,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.4
                    )
                }
            }
        }
    }
}

// ============================================================================
// VOICE RECORDING PLAYER
// ============================================================================

@Composable
private fun VoiceRecordingPlayer(
    voiceUri: String,
    durationMs: Long,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableFloatStateOf(0f) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    // Cleanup on dispose
    DisposableEffect(voiceUri) {
        onDispose {
            mediaPlayer?.apply {
                if (isPlaying) stop()
                release()
            }
        }
    }

    // Progress update while playing
    LaunchedEffect(isPlaying) {
        while (isPlaying && mediaPlayer != null) {
            try {
                val player = mediaPlayer
                if (player != null && player.isPlaying) {
                    currentPosition = player.currentPosition.toFloat() / player.duration.toFloat()
                }
            } catch (e: Exception) {
                isPlaying = false
            }
            delay(100)
        }
    }

    val accentColor by animateColorAsState(
        targetValue = if (isPlaying) MoodMotivated else MoodCalm,
        animationSpec = tween(300),
        label = "voice_accent"
    )

    ProdyCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = accentColor.copy(alpha = 0.1f)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Play/Pause button
                FilledIconButton(
                    onClick = {
                        if (isPlaying) {
                            mediaPlayer?.pause()
                            isPlaying = false
                        } else {
                            try {
                                if (mediaPlayer == null) {
                                    mediaPlayer = MediaPlayer().apply {
                                        setDataSource(context, Uri.parse(voiceUri))
                                        prepare()
                                        setOnCompletionListener {
                                            isPlaying = false
                                            currentPosition = 0f
                                        }
                                    }
                                }
                                mediaPlayer?.start()
                                isPlaying = true
                            } catch (e: Exception) {
                                android.util.Log.e("VoicePlayer", "Error playing voice", e)
                            }
                        }
                    },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = accentColor.copy(alpha = 0.2f),
                        contentColor = accentColor
                    ),
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Mic,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Voice Note",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Progress bar
                    LinearProgressIndicator(
                        progress = { currentPosition },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = accentColor,
                        trackColor = accentColor.copy(alpha = 0.2f)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Duration
                    Text(
                        text = formatDuration(
                            if (isPlaying && mediaPlayer != null) {
                                (currentPosition * durationMs).toLong()
                            } else {
                                durationMs
                            }
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// ============================================================================
// MEDIA GALLERY SECTION
// ============================================================================

@Composable
private fun MediaGallerySection(
    photos: List<String>,
    videos: List<String>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<String?>(null) }

    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.PhotoLibrary,
                contentDescription = null,
                tint = MoodExcited,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = "Attachments",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Surface(
                shape = CircleShape,
                color = MoodExcited.copy(alpha = 0.15f)
            ) {
                Text(
                    text = "${photos.size + videos.size}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = MoodExcited,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Photos
            items(photos) { photoUri ->
                MediaThumbnail(
                    uri = photoUri,
                    isVideo = false,
                    onClick = { selectedImageUri = photoUri }
                )
            }

            // Videos
            items(videos) { videoUri ->
                MediaThumbnail(
                    uri = videoUri,
                    isVideo = true,
                    onClick = {
                        try {
                            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                                setDataAndType(Uri.parse(videoUri), "video/*")
                                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            android.util.Log.e("JournalDetail", "No video player found", e)
                        }
                    }
                )
            }
        }
    }

    // Full-screen image viewer
    selectedImageUri?.let { uri ->
        FullScreenImageViewer(
            imageUri = uri,
            onDismiss = { selectedImageUri = null }
        )
    }
}

@Composable
private fun MediaThumbnail(
    uri: String,
    isVideo: Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(Uri.parse(uri))
                .crossfade(true)
                .build(),
            contentDescription = if (isVideo) "Video" else "Photo",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Video overlay
        if (isVideo) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayCircle,
                    contentDescription = "Play video",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        // Gradient overlay for aesthetics
        if (!isVideo) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.1f)
                            ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )
        }
    }
}

@Composable
private fun FullScreenImageViewer(
    imageUri: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(Uri.parse(imageUri))
                    .crossfade(true)
                    .build(),
                contentDescription = "Full screen image",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )

            // Close button
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .statusBarsPadding()
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

// ============================================================================
// HELPER FUNCTIONS
// ============================================================================

private fun parseJsonArray(jsonString: String): List<String> {
    if (jsonString.isBlank()) return emptyList()
    return try {
        val jsonArray = JSONArray(jsonString)
        (0 until jsonArray.length()).map { jsonArray.getString(it) }
    } catch (e: Exception) {
        emptyList()
    }
}

private fun formatDuration(durationMs: Long): String {
    val seconds = (durationMs / 1000) % 60
    val minutes = (durationMs / 1000) / 60
    return String.format("%d:%02d", minutes, seconds)
}
