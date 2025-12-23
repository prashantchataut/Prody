package com.prody.prashant.ui.screens.journal

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.prody.prashant.util.AccessibilityUtils
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.prody.prashant.R
import com.prody.prashant.domain.model.Mood
import com.prody.prashant.ui.components.AmbientBackground
import com.prody.prashant.ui.components.MoodSuggestionHint
import com.prody.prashant.ui.components.rememberMoodSuggestionState
import com.prody.prashant.ui.components.getCurrentTimeOfDay
import com.prody.prashant.ui.components.mapMoodToAmbient
import com.prody.prashant.ui.theme.*

/**
 * Journal New Entry Screen - Premium Minimalist Design
 *
 * A completely redesigned journal entry screen with:
 * - Clean, flat minimalist aesthetic (no shadows, gradients, or hi-fi elements)
 * - Full light/dark mode support with distinct color palettes
 * - Template cards for guided journaling (Gratitude, Reflection)
 * - Mood selection with horizontal scrolling chips
 * - Custom intensity slider with tick marks
 * - Rich text input with action icons and word count
 */
@Composable
fun NewJournalEntryScreen(
    onNavigateBack: () -> Unit,
    onEntrySaved: () -> Unit,
    viewModel: NewJournalEntryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Photo/Video picker launcher
    val mediaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val photoUris = uris.filter { uri ->
                context.contentResolver.getType(uri)?.startsWith("image/") == true
            }.map { it.toString() }
            val videoUris = uris.filter { uri ->
                context.contentResolver.getType(uri)?.startsWith("video/") == true
            }.map { it.toString() }

            if (photoUris.isNotEmpty()) viewModel.addPhotos(photoUris)
            if (videoUris.isNotEmpty()) viewModel.addVideos(videoUris)
        }
    }

    // Audio recording permission state
    var hasRecordingPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Audio permission launcher
    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasRecordingPermission = isGranted
        if (isGranted) {
            viewModel.startRecording()
        }
    }

    // Function to handle voice button click with permission check
    val handleVoiceClick: () -> Unit = {
        if (uiState.isRecording) {
            viewModel.stopRecording()
        } else {
            if (hasRecordingPermission) {
                viewModel.startRecording()
            } else {
                audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    // Mood suggestion state for AI-powered hints
    val moodSuggestionState = rememberMoodSuggestionState()

    // Determine if dark mode is active
    val isDarkTheme = LocalJournalThemeColors.current.isDark

    // Analyze content for mood suggestions when content changes
    LaunchedEffect(uiState.content) {
        if (uiState.content.length > 50 && uiState.selectedMood == null) {
            moodSuggestionState.analyzeText(uiState.content)
        } else {
            moodSuggestionState.clearSuggestion()
        }
    }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onEntrySaved()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                duration = SnackbarDuration.Short
            )
            viewModel.clearError()
        }
    }

    // Handle back navigation with unsaved changes check
    val handleBack: () -> Unit = {
        if (viewModel.handleBackNavigation()) {
            onNavigateBack()
        }
    }

    JournalTheme {
        val colors = LocalJournalThemeColors.current

        // Discard Changes Dialog
        if (uiState.showDiscardDialog) {
            DiscardChangesDialog(
                onDismiss = { viewModel.hideDiscardDialog() },
                onDiscard = {
                    viewModel.hideDiscardDialog()
                    onNavigateBack()
                },
                colors = colors
            )
        }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = colors.background,
            topBar = {
                JournalTopBar(
                    onBackClick = handleBack,
                    onSaveClick = { viewModel.saveEntry() },
                    isSaveEnabled = uiState.content.isNotBlank() && !uiState.isSaving,
                    isSaving = uiState.isSaving,
                    isGeneratingAi = uiState.isGeneratingAiResponse,
                    colors = colors
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Title Input Field
                TitleInputField(
                    title = uiState.title,
                    onTitleChanged = { viewModel.updateTitle(it) },
                    colors = colors
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Use Template Section
                UseTemplateSection(
                    onTemplateSelected = { viewModel.selectTemplate(it) },
                    colors = colors
                )

                Spacer(modifier = Modifier.height(24.dp))

                // How are you feeling? Section with mood suggestion hint
                Box {
                    MoodSelectionSection(
                        selectedMood = uiState.selectedMood,
                        onMoodSelected = { viewModel.updateMood(it) },
                        colors = colors
                    )

                    // Subtle AI-powered mood suggestion hint
                    MoodSuggestionHint(
                        state = moodSuggestionState,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(end = 20.dp, top = 4.dp)
                    )
                }

                // Main Input Field with media actions
                JournalInputField(
                    content = uiState.content,
                    wordCount = uiState.wordCount,
                    onContentChanged = { viewModel.updateContent(it) },
                    onMediaClick = { mediaPickerLauncher.launch("*/*") },
                    onVoiceClick = handleVoiceClick,
                    onListClick = {
                        val bulletPrefix = if (uiState.content.isEmpty() || uiState.content.endsWith("\n")) "• " else "\n• "
                        viewModel.updateContent(uiState.content + bulletPrefix)
                    },
                    isRecording = uiState.isRecording,
                    recordingTimeElapsed = uiState.recordingTimeElapsed,
                    colors = colors
                )

                // Attached Media Preview Section (NEW)
                if (uiState.attachedPhotos.isNotEmpty() || uiState.attachedVideos.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    AttachedMediaSection(
                        photos = uiState.attachedPhotos,
                        videos = uiState.attachedVideos,
                        onRemovePhoto = { viewModel.removePhoto(it) },
                        onRemoveVideo = { viewModel.removeVideo(it) },
                        colors = colors
                    )
                }

                // Voice Recording Preview Section (NEW)
                if (uiState.voiceRecordingUri != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    VoiceRecordingPreview(
                        duration = uiState.voiceRecordingDuration,
                        isPlaying = uiState.isPlayingVoice,
                        onPlayToggle = { viewModel.toggleVoicePlayback() },
                        onRemove = { viewModel.removeVoiceRecording() },
                        colors = colors
                    )
                }

                // Recording Indicator (NEW)
                if (uiState.isRecording) {
                    Spacer(modifier = Modifier.height(16.dp))
                    RecordingIndicator(
                        timeElapsed = uiState.recordingTimeElapsed,
                        onStop = { viewModel.stopRecording() },
                        onCancel = { viewModel.cancelRecording() },
                        colors = colors
                    )
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

// =============================================================================
// JOURNAL THEME COLORS
// =============================================================================

data class JournalThemeColors(
    val isDark: Boolean,
    val background: Color,
    val surface: Color,
    val primaryText: Color,
    val secondaryText: Color,
    val placeholderText: Color,
    val accent: Color,
    val sliderInactive: Color,
    val saveButtonBg: Color,
    val iconCircleBorder: Color,
    val cardCornerDetail: Color
)

val LocalJournalThemeColors = staticCompositionLocalOf {
    JournalThemeColors(
        isDark = false,
        background = JournalBackgroundLight,
        surface = JournalSurfaceLight,
        primaryText = JournalTextPrimaryLight,
        secondaryText = JournalTextSecondaryLight,
        placeholderText = JournalPlaceholderLight,
        accent = JournalAccentGreen,
        sliderInactive = JournalSliderInactiveLight,
        saveButtonBg = JournalSaveButtonBgLight,
        iconCircleBorder = JournalIconCircleBorderLight,
        cardCornerDetail = JournalCardCornerDetailLight
    )
}

@Composable
fun JournalTheme(
    content: @Composable () -> Unit
) {
    val isDark = !MaterialTheme.colorScheme.background.luminance().let { it > 0.5f }

    val colors = if (isDark) {
        JournalThemeColors(
            isDark = true,
            background = JournalBackgroundDark,
            surface = JournalSurfaceDark,
            primaryText = JournalTextPrimaryDark,
            secondaryText = JournalTextSecondaryDark,
            placeholderText = JournalPlaceholderDark,
            accent = JournalAccentGreen,
            sliderInactive = JournalSliderInactiveDark,
            saveButtonBg = JournalSaveButtonBgDark,
            iconCircleBorder = JournalIconCircleBorderDark,
            cardCornerDetail = JournalCardCornerDetailDark
        )
    } else {
        JournalThemeColors(
            isDark = false,
            background = JournalBackgroundLight,
            surface = JournalSurfaceLight,
            primaryText = JournalTextPrimaryLight,
            secondaryText = JournalTextSecondaryLight,
            placeholderText = JournalPlaceholderLight,
            accent = JournalAccentGreen,
            sliderInactive = JournalSliderInactiveLight,
            saveButtonBg = JournalSaveButtonBgLight,
            iconCircleBorder = JournalIconCircleBorderLight,
            cardCornerDetail = JournalCardCornerDetailLight
        )
    }

    CompositionLocalProvider(LocalJournalThemeColors provides colors) {
        content()
    }
}

// Helper function to calculate luminance
private fun Color.luminance(): Float {
    val r = red
    val g = green
    val b = blue
    return 0.2126f * r + 0.7152f * g + 0.0722f * b
}

// =============================================================================
// TOP BAR
// =============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun JournalTopBar(
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    isSaveEnabled: Boolean,
    isSaving: Boolean,
    isGeneratingAi: Boolean,
    colors: JournalThemeColors
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.new_entry),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.SemiBold
                ),
                color = colors.primaryText
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = colors.primaryText,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        actions = {
            // Save Button with pill shape
            Box(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(colors.saveButtonBg)
                    .clickable(
                        enabled = isSaveEnabled,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onSaveClick() }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isSaving) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            strokeWidth = 2.dp,
                            color = colors.accent
                        )
                        Text(
                            text = if (isGeneratingAi) "..." else "...",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = PoppinsFamily,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = colors.accent
                        )
                    }
                } else {
                    Text(
                        text = stringResource(R.string.save_entry),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = if (isSaveEnabled) colors.accent else colors.accent.copy(alpha = 0.5f)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = colors.background
        )
    )
}

// =============================================================================
// USE TEMPLATE SECTION
// =============================================================================

@Composable
private fun UseTemplateSection(
    onTemplateSelected: (JournalTemplate) -> Unit,
    colors: JournalThemeColors
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        // Section Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.GridView,
                contentDescription = null,
                tint = colors.accent,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Use Template",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.SemiBold
                ),
                color = colors.primaryText
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Template Cards - Horizontal Scroll
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                TemplateCard(
                    title = "Gratitude",
                    description = "Cultivate positivity by reflecting on what...",
                    icon = Icons.Filled.Favorite,
                    onClick = {
                        JournalTemplate.all.find { it.id == "gratitude" }?.let { onTemplateSelected(it) }
                    },
                    colors = colors
                )
            }
            item {
                TemplateCard(
                    title = "Reflection",
                    description = "Review your day with structured prompts.",
                    icon = Icons.Filled.Psychology,
                    onClick = {
                        JournalTemplate.all.find { it.id == "reflection" }?.let { onTemplateSelected(it) }
                    },
                    colors = colors
                )
            }
        }
    }
}

@Composable
private fun TemplateCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit,
    colors: JournalThemeColors
) {
    Box(
        modifier = Modifier
            .width(160.dp)
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surface)
            .clickable(onClick = onClick)
            .semantics {
                contentDescription = "$title template: $description"
                role = Role.Button
            }
    ) {
        // Corner decoration (subtle abstract shape)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(80.dp)
                .offset(x = 20.dp, y = (-20).dp)
        ) {
            // Large faded icon in corner
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.cardCornerDetail,
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.Center)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Icon Circle
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(colors.surface)
                    .border(1.dp, colors.iconCircleBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = colors.accent,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Title
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold
                ),
                color = colors.primaryText
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Description
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 18.sp
                ),
                color = colors.secondaryText,
                maxLines = 3
            )
        }
    }
}

// =============================================================================
// MOOD SELECTION SECTION
// =============================================================================

@Composable
private fun MoodSelectionSection(
    selectedMood: Mood,
    onMoodSelected: (Mood) -> Unit,
    colors: JournalThemeColors
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        // Section Header
        Text(
            text = "How are you feeling?",
            style = MaterialTheme.typography.titleSmall.copy(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold
            ),
            color = colors.primaryText
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Mood Buttons - Horizontal Scroll
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(getMoodList()) { mood ->
                MoodButton(
                    mood = mood,
                    isSelected = mood == selectedMood,
                    onClick = { onMoodSelected(mood) },
                    colors = colors
                )
            }
        }
    }
}

private fun getMoodList(): List<Mood> = listOf(
    Mood.HAPPY,
    Mood.CALM,
    Mood.ANXIOUS,
    Mood.SAD,
    Mood.MOTIVATED,
    Mood.GRATEFUL,
    Mood.CONFUSED,
    Mood.EXCITED
)

@Composable
private fun MoodButton(
    mood: Mood,
    isSelected: Boolean,
    onClick: () -> Unit,
    colors: JournalThemeColors
) {
    val backgroundColor = if (isSelected) colors.accent else colors.surface
    val contentColor = if (isSelected) Color.White else colors.primaryText

    Box(
        modifier = Modifier
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp)
            .semantics {
                contentDescription = AccessibilityUtils.moodDescription(mood.displayName, isSelected)
                role = Role.Button
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = mood.icon,
                contentDescription = null, // Parent has description
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = mood.displayName,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold
                ),
                color = contentColor
            )
        }
    }
}

// =============================================================================
// INTENSITY SECTION
// =============================================================================

@Composable
private fun IntensitySection(
    intensity: Int,
    onIntensityChanged: (Int) -> Unit,
    colors: JournalThemeColors
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        // Section Header with value
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Intensity",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.SemiBold
                ),
                color = colors.primaryText
            )

            Row {
                Text(
                    text = "$intensity",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Medium
                    ),
                    color = colors.accent
                )
                Text(
                    text = " / 10",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Medium
                    ),
                    color = colors.secondaryText
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Slider Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(colors.surface)
                .padding(16.dp)
        ) {
            Column {
                // Labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "MILD",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.sp
                        ),
                        color = colors.secondaryText
                    )
                    Text(
                        text = "INTENSE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.sp
                        ),
                        color = colors.secondaryText
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Custom Slider with tick marks
                CustomIntensitySlider(
                    value = intensity,
                    onValueChange = onIntensityChanged,
                    colors = colors
                )
            }
        }
    }
}

@Composable
private fun CustomIntensitySlider(
    value: Int,
    onValueChange: (Int) -> Unit,
    colors: JournalThemeColors
) {
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp)
    ) {
        val sliderWidth = constraints.maxWidth.toFloat()
        val thumbRadius = with(density) { 12.dp.toPx() }
        val trackHeight = with(density) { 4.dp.toPx() }
        val tickWidth = with(density) { 2.dp.toPx() }
        val tickHeight = with(density) { 8.dp.toPx() }

        // Calculate position based on value (1-10)
        val progress = (value - 1) / 9f
        val thumbX = thumbRadius + (sliderWidth - 2 * thumbRadius) * progress

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        change.consume()
                        val x = change.position.x.coerceIn(thumbRadius, sliderWidth - thumbRadius)
                        val newProgress = (x - thumbRadius) / (sliderWidth - 2 * thumbRadius)
                        val newValue = (newProgress * 9 + 1).toInt().coerceIn(1, 10)
                        onValueChange(newValue)
                    }
                }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { }
        ) {
            val centerY = size.height / 2

            // Draw tick marks
            for (i in 0..9) {
                val tickX = thumbRadius + (sliderWidth - 2 * thumbRadius) * (i / 9f)
                val tickProgress = i / 9f
                val tickColor = if (tickProgress <= progress) colors.accent else colors.sliderInactive

                drawLine(
                    color = tickColor,
                    start = Offset(tickX, centerY - tickHeight / 2),
                    end = Offset(tickX, centerY + tickHeight / 2),
                    strokeWidth = tickWidth
                )
            }

            // Draw inactive track (full width)
            drawLine(
                color = colors.sliderInactive,
                start = Offset(thumbRadius, centerY),
                end = Offset(sliderWidth - thumbRadius, centerY),
                strokeWidth = trackHeight
            )

            // Draw active track
            drawLine(
                color = colors.accent,
                start = Offset(thumbRadius, centerY),
                end = Offset(thumbX, centerY),
                strokeWidth = trackHeight
            )

            // Draw thumb - white fill with green border
            drawCircle(
                color = Color.White,
                radius = thumbRadius,
                center = Offset(thumbX, centerY)
            )
            drawCircle(
                color = colors.accent,
                radius = thumbRadius,
                center = Offset(thumbX, centerY),
                style = Stroke(width = with(density) { 3.dp.toPx() })
            )

            // Inner dot on thumb
            drawCircle(
                color = colors.accent,
                radius = with(density) { 4.dp.toPx() },
                center = Offset(thumbX, centerY)
            )
        }
    }
}

// =============================================================================
// TITLE INPUT FIELD (NEW)
// =============================================================================

@Composable
private fun TitleInputField(
    title: String,
    onTitleChanged: (String) -> Unit,
    colors: JournalThemeColors
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        BasicTextField(
            value = title,
            onValueChange = onTitleChanged,
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                lineHeight = 32.sp,
                color = colors.primaryText
            ),
            cursorBrush = SolidColor(colors.accent),
            singleLine = true,
            decorationBox = { innerTextField ->
                Box {
                    if (title.isEmpty()) {
                        Text(
                            text = "Entry Title (optional)",
                            style = TextStyle(
                                fontFamily = PoppinsFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                lineHeight = 32.sp,
                                color = colors.placeholderText
                            )
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}

// =============================================================================
// JOURNAL INPUT FIELD
// =============================================================================

@Composable
private fun JournalInputField(
    content: String,
    wordCount: Int,
    onContentChanged: (String) -> Unit,
    onMediaClick: () -> Unit,
    onVoiceClick: () -> Unit,
    onListClick: () -> Unit,
    isRecording: Boolean,
    recordingTimeElapsed: Long,
    colors: JournalThemeColors
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(colors.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Text Input
                BasicTextField(
                    value = content,
                    onValueChange = onContentChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp),
                    textStyle = TextStyle(
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        color = colors.primaryText
                    ),
                    cursorBrush = SolidColor(colors.accent),
                    decorationBox = { innerTextField ->
                        Box {
                            if (content.isEmpty()) {
                                Text(
                                    text = "What's on your mind?",
                                    style = TextStyle(
                                        fontFamily = PoppinsFamily,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 16.sp,
                                        lineHeight = 24.sp,
                                        color = colors.placeholderText
                                    )
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Row - Icons and Word Count
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Action Icons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        IconButton(
                            onClick = onMediaClick,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Image,
                                contentDescription = "Add photo/video",
                                tint = colors.primaryText,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        IconButton(
                            onClick = onVoiceClick,
                            modifier = Modifier.size(48.dp)
                        ) {
                            // Pulsating animation when recording
                            val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
                            val scale by infiniteTransition.animateFloat(
                                initialValue = 1f,
                                targetValue = if (isRecording) 1.2f else 1f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(500, easing = FastOutSlowInEasing),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "mic_scale"
                            )

                            Icon(
                                imageVector = if (isRecording) Icons.Filled.Stop else Icons.Filled.Mic,
                                contentDescription = if (isRecording) "Stop recording" else "Voice input",
                                tint = if (isRecording) colors.accent else colors.primaryText,
                                modifier = Modifier
                                    .size(24.dp)
                                    .scale(if (isRecording) scale else 1f)
                            )
                        }
                        IconButton(
                            onClick = onListClick,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.List,
                                contentDescription = "Add list",
                                tint = colors.primaryText,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    // Word Count
                    Text(
                        text = "$wordCount WORDS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.sp
                        ),
                        color = colors.placeholderText
                    )
                }
            }
        }
    }
}

// =============================================================================
// ATTACHED MEDIA SECTION
// =============================================================================

@Composable
private fun AttachedMediaSection(
    photos: List<String>,
    videos: List<String>,
    onRemovePhoto: (String) -> Unit,
    onRemoveVideo: (String) -> Unit,
    colors: JournalThemeColors
) {
    val context = LocalContext.current

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(photos) { photoUri ->
            MediaThumbnail(
                uri = photoUri,
                isVideo = false,
                onRemove = { onRemovePhoto(photoUri) },
                colors = colors
            )
        }
        items(videos) { videoUri ->
            MediaThumbnail(
                uri = videoUri,
                isVideo = true,
                onRemove = { onRemoveVideo(videoUri) },
                colors = colors
            )
        }
    }
}

@Composable
private fun MediaThumbnail(
    uri: String,
    isVideo: Boolean,
    onRemove: () -> Unit,
    colors: JournalThemeColors
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(uri)
                .crossfade(true)
                .build(),
            contentDescription = if (isVideo) "Video thumbnail" else "Photo thumbnail",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        if (isVideo) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(24.dp)
                .padding(2.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Remove",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

// =============================================================================
// VOICE RECORDING PREVIEW
// =============================================================================

@Composable
private fun VoiceRecordingPreview(
    duration: Long,
    isPlaying: Boolean,
    onPlayToggle: () -> Unit,
    onRemove: () -> Unit,
    colors: JournalThemeColors
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surface)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        IconButton(
            onClick = onPlayToggle,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(colors.accent)
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.height(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(20) { index ->
                    val height = (8 + (index * 7) % 16).dp
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height(height)
                            .clip(RoundedCornerShape(1.5.dp))
                            .background(colors.accent.copy(alpha = 0.6f))
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatDuration(duration),
                fontFamily = PoppinsFamily,
                fontSize = 12.sp,
                color = colors.secondaryText
            )
        }

        IconButton(
            onClick = onRemove,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Remove recording",
                tint = Color(0xFFEF5350),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// =============================================================================
// RECORDING INDICATOR
// =============================================================================

@Composable
private fun RecordingIndicator(
    timeElapsed: Long,
    onStop: () -> Unit,
    onCancel: () -> Unit,
    colors: JournalThemeColors
) {
    val infiniteTransition = rememberInfiniteTransition(label = "recording_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surface)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(Color(0xFFEF5350).copy(alpha = pulseAlpha))
        )

        Text(
            text = formatDuration(timeElapsed),
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            color = colors.primaryText,
            modifier = Modifier.weight(1f)
        )

        TextButton(onClick = onCancel) {
            Text(
                text = "Cancel",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Medium,
                color = colors.secondaryText
            )
        }

        TextButton(
            onClick = onStop
        ) {
            Text(
                text = "Stop",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold,
                color = colors.accent
            )
        }
    }
}

// =============================================================================
// DISCARD CHANGES DIALOG
// =============================================================================

@Composable
private fun DiscardChangesDialog(
    onDismiss: () -> Unit,
    onDiscard: () -> Unit,
    colors: JournalThemeColors
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.surface,
        title = {
            Text(
                text = "Discard Changes?",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold,
                color = colors.primaryText
            )
        },
        text = {
            Text(
                text = "You have unsaved changes. Are you sure you want to discard them?",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Normal,
                color = colors.secondaryText
            )
        },
        confirmButton = {
            TextButton(onClick = onDiscard) {
                Text(
                    text = "Discard",
                    color = Color(0xFFEF5350),
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Keep Editing",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    color = colors.accent
                )
            }
        }
    )
}

// =============================================================================
// HELPER FUNCTIONS
// =============================================================================

private fun formatDuration(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}

