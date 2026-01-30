package com.prody.prashant.ui.screens.journal
import com.prody.prashant.ui.icons.ProdyIcons

import android.app.Activity
import android.view.WindowManager
import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.prody.prashant.util.AccessibilityUtils
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.prody.prashant.R
import com.prody.prashant.domain.model.Mood
import com.prody.prashant.domain.validation.ContentValidation
import com.prody.prashant.domain.validation.ContentValidator
import com.prody.prashant.ui.components.AmbientBackground
import com.prody.prashant.ui.components.MoodSuggestionHint
import com.prody.prashant.ui.components.SessionResultCard
import com.prody.prashant.ui.components.rememberMoodSuggestionState
import com.prody.prashant.ui.components.getCurrentTimeOfDay
import com.prody.prashant.ui.components.mapMoodToAmbient
import com.prody.prashant.ui.theme.*

/**
 * Journal New Entry Screen - Premium Minimalist Design
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

    // Security: Prevent screenshots and screen recordings while writing a private journal entry
    DisposableEffect(Unit) {
        val window = (context as? Activity)?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

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

    // Function to handle voice button click
    val handleVoiceClick: () -> Unit = {
        if (uiState.isTranscribing) {
            viewModel.stopTranscription()
        } else if (uiState.isRecording) {
            viewModel.stopRecording()
        } else {
            if (hasRecordingPermission) {
                if (uiState.transcriptionAvailable) {
                    viewModel.startTranscription()
                } else {
                    viewModel.startRecording()
                }
            } else {
                audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }

    // Mood suggestion state
    val moodSuggestionState = rememberMoodSuggestionState()

    // Analyze content for mood suggestions
    LaunchedEffect(uiState.content) {
        if (uiState.content.length > 50) {
            moodSuggestionState.analyzeText(uiState.content)
        } else {
            moodSuggestionState.clearSuggestion()
        }
    }

    // Navigate away after save
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved && !uiState.showSessionResult) {
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

    val handleBack: () -> Unit = {
        if (viewModel.handleBackNavigation()) {
            onNavigateBack()
        }
    }

    val contentFocusRequester = remember { FocusRequester() }

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

        // Session Result Card
        if (uiState.showSessionResult && uiState.sessionResult != null) {
            SessionResultCard(
                sessionResult = uiState.sessionResult!!,
                onDismiss = {
                    viewModel.dismissSessionResult()
                    onEntrySaved()
                }
            )
        }

        // Transcription Choice Dialog
        if (uiState.showTranscriptionChoice) {
            TranscriptionChoiceDialog(
                onChoiceSelected = { viewModel.onTranscriptionChoiceSelected(it) },
                onDismiss = { viewModel.onTranscriptionChoiceSelected(TranscriptionChoice.NEVER) },
                colors = colors
            )
        }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = colors.background,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
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
            val scrollState = rememberScrollState()
            val Density = LocalDensity.current 
            val imeVisible = WindowInsets.ime.getBottom(Density) > 0
            LaunchedEffect(imeVisible) {
                if (imeVisible) {
                    scrollState.animateScrollTo(scrollState.maxValue)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .imePadding()
                    .verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                TitleInputField(
                    title = uiState.title,
                    onTitleChanged = { viewModel.updateTitle(it) },
                    onNext = { contentFocusRequester.requestFocus() },
                    colors = colors
                )

                Spacer(modifier = Modifier.height(16.dp))

                UseTemplateSection(
                    onTemplateSelected = { viewModel.selectTemplate(it) },
                    colors = colors
                )

                Spacer(modifier = Modifier.height(24.dp))

                Box {
                    MoodSelectionSection(
                        selectedMood = uiState.selectedMood,
                        onMoodSelected = { viewModel.updateMood(it) },
                        colors = colors
                    )

                    MoodSuggestionHint(
                        state = moodSuggestionState,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(end = 20.dp, top = 4.dp)
                    )
                }

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
                    contentValidation = uiState.contentValidation,
                    completionProgress = uiState.contentCompletionProgress,
                    colors = colors,
                    focusRequester = contentFocusRequester
                )

                ContentValidationHint(
                    validation = uiState.contentValidation,
                    validationHint = uiState.validationHint,
                    completionProgress = uiState.contentCompletionProgress,
                    colors = colors
                )

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

                if (uiState.isRecording) {
                    Spacer(modifier = Modifier.height(16.dp))
                    RecordingIndicator(
                        timeElapsed = uiState.recordingTimeElapsed,
                        onStop = { viewModel.stopRecording() },
                        onCancel = { viewModel.cancelRecording() },
                        colors = colors
                    )
                }

                if (uiState.isTranscribing) {
                    Spacer(modifier = Modifier.height(16.dp))
                    TranscriptionIndicator(
                        partialText = uiState.transcriptionPartial,
                        soundLevel = uiState.transcriptionSoundLevel,
                        onStop = { viewModel.stopTranscription() },
                        onCancel = { viewModel.cancelTranscription() },
                        colors = colors
                    )
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

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
fun JournalTheme(content: @Composable () -> Unit) {
    val isDark = !MaterialTheme.colorScheme.background.luminance().let { it > 0.5f }
    val colors = if (isDark) {
        JournalThemeColors(true, JournalBackgroundDark, JournalSurfaceDark, JournalTextPrimaryDark, JournalTextSecondaryDark, JournalPlaceholderDark, JournalAccentGreen, JournalSliderInactiveDark, JournalSaveButtonBgDark, JournalIconCircleBorderDark, JournalCardCornerDetailDark)
    } else {
        JournalThemeColors(false, JournalBackgroundLight, JournalSurfaceLight, JournalTextPrimaryLight, JournalTextSecondaryLight, JournalPlaceholderLight, JournalAccentGreen, JournalSliderInactiveLight, JournalSaveButtonBgLight, JournalIconCircleBorderLight, JournalCardCornerDetailLight)
    }
    CompositionLocalProvider(LocalJournalThemeColors provides colors) { content() }
}

private fun Color.luminance(): Float = 0.2126f * red + 0.7152f * green + 0.0722f * blue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun JournalTopBar(onBackClick: () -> Unit, onSaveClick: () -> Unit, isSaveEnabled: Boolean, isSaving: Boolean, isGeneratingAi: Boolean, colors: JournalThemeColors) {
    CenterAlignedTopAppBar(
        title = { Text(text = stringResource(R.string.new_entry), style = MaterialTheme.typography.titleMedium.copy(fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold), color = colors.primaryText) },
        navigationIcon = { IconButton(onClick = onBackClick) { Icon(imageVector = ProdyIcons.ArrowBack, contentDescription = stringResource(R.string.back), tint = colors.primaryText) } },
        actions = {
            Box(modifier = Modifier.padding(end = 8.dp).clip(RoundedCornerShape(20.dp)).background(colors.saveButtonBg).clickable(enabled = isSaveEnabled) { onSaveClick() }.padding(horizontal = 16.dp, vertical = 8.dp), contentAlignment = Alignment.Center) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = colors.accent)
                } else {
                    Text(text = stringResource(R.string.save_entry), style = MaterialTheme.typography.labelMedium.copy(fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold), color = if (isSaveEnabled) colors.accent else colors.accent.copy(alpha = 0.5f))
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = colors.background)
    )
}

@Composable
private fun UseTemplateSection(onTemplateSelected: (JournalTemplate) -> Unit, colors: JournalThemeColors) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(imageVector = ProdyIcons.GridView, contentDescription = null, tint = colors.accent, modifier = Modifier.size(20.dp))
            Text(text = "Use Template", style = MaterialTheme.typography.titleSmall.copy(fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold), color = colors.primaryText)
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            item { TemplateCard("Gratitude", "Cultivate positivity...", ProdyIcons.Favorite, { JournalTemplate.all.find { it.id == "gratitude" }?.let { onTemplateSelected(it) } }, colors) }
            item { TemplateCard("Reflection", "Review your day...", ProdyIcons.Psychology, { JournalTemplate.all.find { it.id == "reflection" }?.let { onTemplateSelected(it) } }, colors) }
        }
    }
}

@Composable
private fun TemplateCard(title: String, description: String, icon: ImageVector, onClick: () -> Unit, colors: JournalThemeColors) {
    Box(modifier = Modifier.width(160.dp).height(180.dp).clip(RoundedCornerShape(16.dp)).background(colors.surface).clickable(onClick = onClick)) {
        Icon(imageVector = icon, contentDescription = null, tint = colors.cardCornerDetail, modifier = Modifier.size(80.dp).align(Alignment.TopEnd).offset(20.dp, (-20).dp))
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(colors.surface).border(1.dp, colors.iconCircleBorder, CircleShape), contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = colors.accent, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = title, style = MaterialTheme.typography.titleSmall.copy(fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold), color = colors.primaryText)
            Text(text = description, style = MaterialTheme.typography.bodySmall.copy(fontFamily = PoppinsFamily, lineHeight = 18.sp), color = colors.secondaryText, maxLines = 3)
        }
    }
}

@Composable
private fun MoodSelectionSection(selectedMood: Mood, onMoodSelected: (Mood) -> Unit, colors: JournalThemeColors) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(text = "How are you feeling?", style = MaterialTheme.typography.titleSmall.copy(fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold), color = colors.primaryText)
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(listOf(Mood.HAPPY, Mood.CALM, Mood.ANXIOUS, Mood.SAD, Mood.MOTIVATED, Mood.GRATEFUL, Mood.CONFUSED, Mood.EXCITED)) { mood ->
                MoodButton(mood, mood == selectedMood, { onMoodSelected(mood) }, colors)
            }
        }
    }
}

@Composable
private fun MoodButton(mood: Mood, isSelected: Boolean, onClick: () -> Unit, colors: JournalThemeColors) {
    Box(modifier = Modifier.height(48.dp).clip(RoundedCornerShape(24.dp)).background(if (isSelected) colors.accent else colors.surface).clickable(onClick = onClick).padding(horizontal = 16.dp), contentAlignment = Alignment.Center) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(imageVector = mood.icon, contentDescription = null, tint = if (isSelected) Color.White else colors.primaryText, modifier = Modifier.size(20.dp))
            Text(text = mood.displayName, style = MaterialTheme.typography.labelLarge.copy(fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold), color = if (isSelected) Color.White else colors.primaryText)
        }
    }
}

@Composable
private fun TitleInputField(title: String, onTitleChanged: (String) -> Unit, onNext: () -> Unit, colors: JournalThemeColors) {
    BasicTextField(value = title, onValueChange = onTitleChanged, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), textStyle = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = colors.primaryText), cursorBrush = SolidColor(colors.accent), singleLine = true, decorationBox = { inner -> Box { if (title.isEmpty()) Text("Entry Title (optional)", style = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = colors.placeholderText)) ; inner() } })
}

@Composable
private fun JournalInputField(content: String, wordCount: Int, onContentChanged: (String) -> Unit, onMediaClick: () -> Unit, onVoiceClick: () -> Unit, onListClick: () -> Unit, isRecording: Boolean, recordingTimeElapsed: Long, contentValidation: ContentValidation, completionProgress: Float, colors: JournalThemeColors, focusRequester: FocusRequester) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Box(modifier = Modifier.fillMaxWidth().heightIn(min = 200.dp).clip(RoundedCornerShape(16.dp)).background(colors.surface).padding(16.dp)) {
            Column {
                BasicTextField(value = content, onValueChange = onContentChanged, modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp).focusRequester(focusRequester), textStyle = TextStyle(fontFamily = PoppinsFamily, fontSize = 16.sp, color = colors.primaryText), cursorBrush = SolidColor(colors.accent), decorationBox = { inner -> Box { if (content.isEmpty()) Text("What's on your mind?", style = TextStyle(fontFamily = PoppinsFamily, fontSize = 16.sp, color = colors.placeholderText)) ; inner() } })
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        IconButton(onClick = onMediaClick) { Icon(imageVector = ProdyIcons.Image, contentDescription = null, tint = colors.primaryText) }
                        IconButton(onClick = onVoiceClick) { Icon(imageVector = if (isRecording) ProdyIcons.Stop else ProdyIcons.Mic, contentDescription = null, tint = if (isRecording) colors.accent else colors.primaryText) }
                        IconButton(onClick = onListClick) { Icon(imageVector = Icons.Filled.Menu, contentDescription = null, tint = colors.primaryText) }
                    }
                    Text(text = "$wordCount WORDS", style = MaterialTheme.typography.labelSmall, color = colors.secondaryText)
                }
            }
        }
    }
}

@Composable
private fun ContentValidationHint(validation: ContentValidation, validationHint: String?, completionProgress: Float, colors: JournalThemeColors) {
    if (validationHint != null && validation !is ContentValidation.Valid && validation !is ContentValidation.Empty) {
        Text(text = validationHint, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodySmall, color = colors.secondaryText)
    }
}

@Composable
private fun AttachedMediaSection(photos: List<String>, videos: List<String>, onRemovePhoto: (String) -> Unit, onRemoveVideo: (String) -> Unit, colors: JournalThemeColors) {
    LazyRow(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(photos) { photo -> MediaThumbnail(photo, false, { onRemovePhoto(photo) }, colors) }
        items(videos) { video -> MediaThumbnail(video, true, { onRemoveVideo(video) }, colors) }
    }
}

@Composable
private fun MediaThumbnail(uri: String, isVideo: Boolean, onRemove: () -> Unit, colors: JournalThemeColors) {
    Box(modifier = Modifier.size(72.dp).clip(RoundedCornerShape(12.dp))) {
        AsyncImage(model = uri, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        IconButton(onClick = onRemove, modifier = Modifier.align(Alignment.TopEnd).size(24.dp)) { Icon(imageVector = ProdyIcons.Close, contentDescription = null, tint = Color.White) }
    }
}

@Composable
private fun VoiceRecordingPreview(duration: Long, isPlaying: Boolean, onPlayToggle: () -> Unit, onRemove: () -> Unit, colors: JournalThemeColors) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).clip(RoundedCornerShape(12.dp)).background(colors.surface).padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        IconButton(onClick = onPlayToggle, modifier = Modifier.size(40.dp).clip(CircleShape).background(colors.accent)) { Icon(imageVector = if (isPlaying) ProdyIcons.Pause else ProdyIcons.PlayArrow, contentDescription = null, tint = Color.White) }
        Text(text = formatDuration(duration), modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall, color = colors.secondaryText)
        IconButton(onClick = onRemove) { Icon(imageVector = ProdyIcons.Delete, contentDescription = null, tint = Color.Red) }
    }
}

@Composable
private fun RecordingIndicator(timeElapsed: Long, onStop: () -> Unit, onCancel: () -> Unit, colors: JournalThemeColors) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).clip(RoundedCornerShape(12.dp)).background(colors.surface).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(Color.Red))
        Text(text = formatDuration(timeElapsed), modifier = Modifier.weight(1f).padding(start = 12.dp), color = colors.primaryText)
        TextButton(onClick = onCancel) { Text("Cancel", color = colors.secondaryText) }
        TextButton(onClick = onStop) { Text("Stop", color = colors.accent) }
    }
}

@Composable
private fun TranscriptionIndicator(partialText: String, soundLevel: Float, onStop: () -> Unit, onCancel: () -> Unit, colors: JournalThemeColors) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).clip(RoundedCornerShape(12.dp)).background(colors.surface).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = ProdyIcons.Mic, contentDescription = null, tint = colors.accent)
            Text(text = "Listening...", modifier = Modifier.padding(start = 12.dp), color = colors.accent)
        }
        if (partialText.isNotEmpty()) Text(text = partialText, modifier = Modifier.padding(top = 8.dp), style = MaterialTheme.typography.bodySmall, color = colors.secondaryText)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onCancel) { Text("Cancel", color = colors.secondaryText) }
            TextButton(onClick = onStop) { Text("Done", color = colors.accent) }
        }
    }
}

@Composable
private fun DiscardChangesDialog(onDismiss: () -> Unit, onDiscard: () -> Unit, colors: JournalThemeColors) {
    AlertDialog(onDismissRequest = onDismiss, containerColor = colors.surface, title = { Text("Discard Changes?") }, text = { Text("You have unsaved changes.") }, confirmButton = { TextButton(onClick = onDiscard) { Text("Discard", color = Color.Red) } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Keep Editing") } })
}

@Composable
private fun TranscriptionChoiceDialog(onChoiceSelected: (TranscriptionChoice) -> Unit, onDismiss: () -> Unit, colors: JournalThemeColors) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.surface,
        title = { Text("Voice Recording Completed", fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, color = colors.primaryText) },
        text = { Text("Would you like to transcribe this recording into text?", fontFamily = PoppinsFamily, color = colors.secondaryText) },
        confirmButton = {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { onChoiceSelected(TranscriptionChoice.NOW) }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = colors.accent), shape = RoundedCornerShape(12.dp)) { Text("Transcribe Now") }
                OutlinedButton(onClick = { onChoiceSelected(TranscriptionChoice.LATER) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, colors.accent)) { Text("Transcribe Later", color = colors.accent) }
                TextButton(onClick = { onChoiceSelected(TranscriptionChoice.NEVER) }, modifier = Modifier.fillMaxWidth()) { Text("Not Now", color = colors.secondaryText) }
            }
        }
    )
}

private fun formatDuration(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}
