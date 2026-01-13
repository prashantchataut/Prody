package com.prody.prashant.ui.screens.futuremessage
import com.prody.prashant.ui.icons.ProdyIcons

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import com.prody.prashant.ui.theme.isDarkTheme
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.prody.prashant.R
import com.prody.prashant.ui.components.TimeCapsuleSealAnimation
import com.prody.prashant.ui.components.rememberTimeCapsuleSealState
import com.prody.prashant.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Write to Time Capsule Screen - Complete Redesign
 *
 * Design Philosophy:
 * - Minimalism & Cleanliness: Flat, modern, compact, extremely clean
 * - No shadows, gradients, skeuomorphism, or unnecessary translucency
 * - Professional & Premium Feel with intentional design
 * - Typography: Exclusively Poppins font family
 * - Color Accents: Vibrant neon green (#36F97F) for interactive elements
 * - Accessibility: 48dp minimum touch targets, WCAG AA contrast compliance
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteMessageScreen(
    onNavigateBack: () -> Unit,
    onMessageSaved: () -> Unit,
    viewModel: WriteMessageViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDark = isDarkTheme()
    val context = LocalContext.current

    // Media picker launcher
    val mediaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val photos = uris.filter { uri ->
                context.contentResolver.getType(uri)?.startsWith("image/") == true
            }.map { it.toString() }
            val videos = uris.filter { uri ->
                context.contentResolver.getType(uri)?.startsWith("video/") == true
            }.map { it.toString() }

            if (photos.isNotEmpty()) viewModel.addPhotos(photos)
            if (videos.isNotEmpty()) viewModel.addVideos(videos)
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

    // Handle back navigation with unsaved changes check
    val handleBack: () -> Unit = {
        if (viewModel.handleBackNavigation()) {
            onNavigateBack()
        }
    }

    // Focus management for keyboard navigation
    val contentFocusRequester = remember { FocusRequester() }

    // Magical sealing animation state
    val sealState = rememberTimeCapsuleSealState()

    // Theme-aware colors
    val backgroundColor = if (isDark) TimeCapsuleBackgroundDark else TimeCapsuleBackgroundLight
    val titleTextColor = if (isDark) TimeCapsuleTitleTextDark else TimeCapsuleTitleTextLight
    val discardTextColor = if (isDark) TimeCapsuleDiscardTextDark else TimeCapsuleDiscardTextLight
    val placeholderColor = if (isDark) TimeCapsulePlaceholderDark else TimeCapsulePlaceholderLight
    val activeTextColor = if (isDark) TimeCapsuleActiveTextDark else TimeCapsuleActiveTextLight
    val multimediaIconColor = if (isDark) TimeCapsuleMultimediaIconDark else TimeCapsuleMultimediaIconLight
    val attachTextColor = if (isDark) TimeCapsuleAttachTextDark else TimeCapsuleAttachTextLight
    val dividerColor = if (isDark) TimeCapsuleDividerDark else TimeCapsuleDividerLight
    val sectionTitleColor = if (isDark) TimeCapsuleSectionTitleDark else TimeCapsuleSectionTitleLight
    val inactiveTagBgColor = if (isDark) TimeCapsuleInactiveTagBgDark else TimeCapsuleInactiveTagBgLight
    val inactiveTagTextColor = if (isDark) TimeCapsuleInactiveTagTextDark else TimeCapsuleInactiveTagTextLight
    val buttonTextColor = if (isDark) TimeCapsuleButtonTextDark else TimeCapsuleButtonTextLight

    // Handle saved state with magical animation
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            // Small delay for the animation to complete
            delay(100)
            onMessageSaved()
        }
    }

    // Discard changes confirmation dialog
    if (uiState.showDiscardDialog) {
        DiscardChangesDialog(
            onDismiss = { viewModel.hideDiscardDialog() },
            onConfirm = {
                viewModel.hideDiscardDialog()
                onNavigateBack()
            }
        )
    }

    // Error snackbar
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            kotlinx.coroutines.delay(3000)
            viewModel.clearError()
        }
    }

    val density = LocalDensity.current
    val scrollState = rememberScrollState()

    // Auto-scroll when keyboard appears
    val imeVisible = WindowInsets.ime.getBottom(density) > 0
    LaunchedEffect(imeVisible) {
        if (imeVisible) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .imePadding() // Critical: Add IME padding to push content above keyboard
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 100.dp) // Space for bottom button
        ) {
            // Top Header Bar
            TimeCapsuleTopBar(
                titleTextColor = titleTextColor,
                discardTextColor = discardTextColor,
                onBackClick = handleBack,
                onDiscardClick = handleBack
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Capsule Title Input
            TimeCapsuleTitleInput(
                value = uiState.title,
                onValueChange = { viewModel.updateTitle(it) },
                onNext = { contentFocusRequester.requestFocus() },
                placeholderColor = placeholderColor,
                activeTextColor = activeTextColor,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Message Body Input
            TimeCapsuleMessageInput(
                value = uiState.content,
                onValueChange = { viewModel.updateContent(it) },
                placeholderColor = placeholderColor,
                activeTextColor = activeTextColor,
                focusRequester = contentFocusRequester,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Multimedia Attachment Row
            MultimediaAttachmentRow(
                iconColor = multimediaIconColor,
                attachTextColor = attachTextColor,
                dividerColor = dividerColor,
                backgroundColor = inactiveTagBgColor,
                isRecording = uiState.isRecording,
                recordingTimeElapsed = uiState.recordingTimeElapsed,
                onMediaClick = {
                    mediaPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                    )
                },
                onVoiceClick = handleVoiceClick,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            // Attached Media Preview
            if (uiState.attachedPhotos.isNotEmpty() || uiState.attachedVideos.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                AttachedMediaSection(
                    photos = uiState.attachedPhotos,
                    videos = uiState.attachedVideos,
                    onRemovePhoto = { viewModel.removePhoto(it) },
                    onRemoveVideo = { viewModel.removeVideo(it) },
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }

            // Voice Recording Preview
            if (uiState.voiceRecordingUri != null) {
                Spacer(modifier = Modifier.height(16.dp))
                VoiceRecordingPreview(
                    duration = uiState.voiceRecordingDuration,
                    isPlaying = uiState.isPlayingVoice,
                    onPlayPauseClick = { viewModel.toggleVoicePlayback() },
                    onRemoveClick = { viewModel.removeVoiceRecording() },
                    backgroundColor = inactiveTagBgColor,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // TIME TRAVEL Section
            TimeTravelSection(
                sectionTitleColor = sectionTitleColor,
                selectedPreset = uiState.selectedPreset,
                onPresetSelected = { viewModel.selectDatePreset(it) },
                inactiveTagBgColor = inactiveTagBgColor,
                inactiveTagTextColor = inactiveTagTextColor,
                onCustomDateClick = { viewModel.showDatePicker() },
                modifier = Modifier.padding(start = 24.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ESSENCE Section - Multi-select categories
            EssenceSectionMultiSelect(
                sectionTitleColor = sectionTitleColor,
                selectedCategories = uiState.selectedCategories,
                onCategoryToggle = { viewModel.toggleCategory(it) },
                inactiveTagBgColor = inactiveTagBgColor,
                inactiveTagTextColor = inactiveTagTextColor,
                modifier = Modifier.padding(start = 24.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))
        }

        // Error message
        AnimatedVisibility(
            visible = uiState.error != null,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 80.dp)
        ) {
            uiState.error?.let { error ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp),
                        fontFamily = PoppinsFamily,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Seal & Schedule Button - Fixed at bottom
        SealAndScheduleButton(
            onClick = { viewModel.saveMessage() },
            enabled = !uiState.isSaving && uiState.content.isNotBlank(),
            isLoading = uiState.isSaving && !uiState.showSealingAnimation,
            buttonTextColor = buttonTextColor,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 32.dp)
        )

        // Magical Time Capsule Sealing Animation overlay
        TimeCapsuleSealAnimation(
            state = sealState,
            onComplete = { /* Animation completed, message is being saved */ }
        )

        // Date Picker Dialog
        if (uiState.showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = uiState.deliveryDate
            )

            DatePickerDialog(
                onDismissRequest = { viewModel.hideDatePicker() },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let {
                                viewModel.selectCustomDate(it)
                            }
                        }
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.hideDatePicker() }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        // Sealing Animation Overlay
        AnimatedVisibility(
            visible = uiState.showSealingAnimation,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300)),
            modifier = Modifier.fillMaxSize()
        ) {
            SealingAnimationOverlay(
                backgroundColor = backgroundColor
            )
        }
    }
}

/**
 * Top Header Bar with back arrow, centered title, and discard text
 */
@Composable
private fun TimeCapsuleTopBar(
    titleTextColor: Color,
    discardTextColor: Color,
    onBackClick: () -> Unit,
    onDiscardClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back Arrow
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = ProdyIcons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back),
                tint = titleTextColor,
                modifier = Modifier.size(24.dp)
            )
        }

        // Centered Title
        Text(
            text = stringResource(R.string.write_to_time_capsule),
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            color = titleTextColor
        )

        // Discard Text
        TextButton(
            onClick = onDiscardClick,
            modifier = Modifier.defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
        ) {
            Text(
                text = stringResource(R.string.discard),
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = discardTextColor
            )
        }
    }
}

/**
 * Large Capsule Title Input with placeholder
 */
@Composable
private fun TimeCapsuleTitleInput(
    value: String,
    onValueChange: (String) -> Unit,
    onNext: () -> Unit,
    placeholderColor: Color,
    activeTextColor: Color,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        textStyle = TextStyle(
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 28.sp,
            color = activeTextColor
        ),
        singleLine = true,
        cursorBrush = SolidColor(TimeCapsuleAccent),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { onNext() }
        ),
        decorationBox = { innerTextField ->
            Box {
                if (value.isEmpty()) {
                    Text(
                        text = stringResource(R.string.capsule_title_placeholder),
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 28.sp,
                        color = placeholderColor
                    )
                }
                innerTextField()
            }
        }
    )
}

/**
 * Multi-line Message Body Input with placeholder
 */
@Composable
private fun TimeCapsuleMessageInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholderColor: Color,
    activeTextColor: Color,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp)
            .focusRequester(focusRequester),
        textStyle = TextStyle(
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = activeTextColor,
            lineHeight = 24.sp
        ),
        cursorBrush = SolidColor(TimeCapsuleAccent),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            imeAction = ImeAction.Default // Multi-line, default behavior
        ),
        decorationBox = { innerTextField ->
            Box {
                if (value.isEmpty()) {
                    Text(
                        text = stringResource(R.string.capsule_message_placeholder),
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = placeholderColor,
                        lineHeight = 24.sp
                    )
                }
                innerTextField()
            }
        }
    )
}

/**
 * Multimedia Attachment Row with camera, mic icons and "Attach memories" text
 */
@Composable
private fun MultimediaAttachmentRow(
    iconColor: Color,
    attachTextColor: Color,
    dividerColor: Color,
    backgroundColor: Color,
    isRecording: Boolean,
    recordingTimeElapsed: Long,
    onMediaClick: () -> Unit,
    onVoiceClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Recording pulse animation
    val infiniteTransition = rememberInfiniteTransition(label = "recording")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Camera Icon Button
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(backgroundColor)
                .clickable(enabled = !isRecording) { onMediaClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = ProdyIcons.Outlined.CameraAlt,
                contentDescription = "Attach photo",
                tint = if (isRecording) iconColor.copy(alpha = 0.3f) else iconColor,
                modifier = Modifier.size(24.dp)
            )
        }

        // Microphone Icon Button
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isRecording) TimeCapsuleAccent.copy(alpha = pulseAlpha * 0.3f) else backgroundColor)
                .clickable { onVoiceClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isRecording) ProdyIcons.Outlined.Stop else ProdyIcons.Outlined.Mic,
                contentDescription = if (isRecording) "Stop recording" else "Record voice",
                tint = if (isRecording) TimeCapsuleAccent else iconColor,
                modifier = Modifier.size(24.dp)
            )
        }

        // Vertical Divider
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(20.dp)
                .background(dividerColor)
        )

        // Attach memories text or recording indicator
        if (isRecording) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(TimeCapsuleAccent.copy(alpha = pulseAlpha))
                )
                Text(
                    text = formatDuration(recordingTimeElapsed),
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = TimeCapsuleAccent
                )
            }
        } else {
            Text(
                text = stringResource(R.string.attach_memories),
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = attachTextColor
            )
        }
    }
}

/**
 * Attached Media Section showing photo/video thumbnails
 */
@Composable
private fun AttachedMediaSection(
    photos: List<String>,
    videos: List<String>,
    onRemovePhoto: (String) -> Unit,
    onRemoveVideo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(photos) { photoUri ->
            MediaThumbnail(
                uri = photoUri,
                isVideo = false,
                onRemove = { onRemovePhoto(photoUri) }
            )
        }
        items(videos) { videoUri ->
            MediaThumbnail(
                uri = videoUri,
                isVideo = true,
                onRemove = { onRemoveVideo(videoUri) }
            )
        }
    }
}

/**
 * Media thumbnail with remove button
 */
@Composable
private fun MediaThumbnail(
    uri: String,
    isVideo: Boolean,
    onRemove: () -> Unit
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

        // Video indicator
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
                    imageVector = ProdyIcons.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Remove button
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
                    imageVector = ProdyIcons.Close,
                    contentDescription = "Remove",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

/**
 * Voice Recording Preview with play/pause and remove
 */
@Composable
private fun VoiceRecordingPreview(
    duration: Long,
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    onRemoveClick: () -> Unit,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Play/Pause button
        IconButton(
            onClick = onPlayPauseClick,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(TimeCapsuleAccent)
        ) {
            Icon(
                imageVector = if (isPlaying) ProdyIcons.Outlined.Pause else ProdyIcons.Outlined.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        // Waveform placeholder and duration
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Waveform visualization placeholder
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
                            .background(TimeCapsuleAccent.copy(alpha = 0.6f))
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatDuration(duration),
                fontFamily = PoppinsFamily,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Remove button
        IconButton(
            onClick = onRemoveClick,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = ProdyIcons.Delete,
                contentDescription = "Remove recording",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * TIME TRAVEL Section with rocket icon and time selection tags
 */
@Composable
private fun TimeTravelSection(
    sectionTitleColor: Color,
    selectedPreset: DatePreset,
    onPresetSelected: (DatePreset) -> Unit,
    inactiveTagBgColor: Color,
    inactiveTagTextColor: Color,
    onCustomDateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Section Header with Rocket Icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = ProdyIcons.RocketLaunch,
                contentDescription = null,
                tint = TimeCapsuleAccent,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = stringResource(R.string.time_travel),
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                letterSpacing = 1.sp,
                color = sectionTitleColor
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Horizontal Scrolling Time Tags
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TimeTag(
                text = stringResource(R.string.one_week),
                isSelected = selectedPreset == DatePreset.ONE_WEEK,
                onClick = { onPresetSelected(DatePreset.ONE_WEEK) },
                inactiveTagBgColor = inactiveTagBgColor,
                inactiveTagTextColor = inactiveTagTextColor
            )
            TimeTag(
                text = stringResource(R.string.one_month),
                isSelected = selectedPreset == DatePreset.ONE_MONTH,
                onClick = { onPresetSelected(DatePreset.ONE_MONTH) },
                inactiveTagBgColor = inactiveTagBgColor,
                inactiveTagTextColor = inactiveTagTextColor
            )
            TimeTag(
                text = stringResource(R.string.six_months),
                isSelected = selectedPreset == DatePreset.SIX_MONTHS,
                onClick = { onPresetSelected(DatePreset.SIX_MONTHS) },
                inactiveTagBgColor = inactiveTagBgColor,
                inactiveTagTextColor = inactiveTagTextColor
            )
            TimeTag(
                text = stringResource(R.string.one_year),
                isSelected = selectedPreset == DatePreset.ONE_YEAR,
                onClick = { onPresetSelected(DatePreset.ONE_YEAR) },
                inactiveTagBgColor = inactiveTagBgColor,
                inactiveTagTextColor = inactiveTagTextColor
            )
            TimeTag(
                text = "Custom",
                isSelected = selectedPreset == DatePreset.CUSTOM,
                onClick = onCustomDateClick,
                inactiveTagBgColor = inactiveTagBgColor,
                inactiveTagTextColor = inactiveTagTextColor
            )
            // Right padding spacer
            Spacer(modifier = Modifier.width(24.dp))
        }
    }
}

/**
 * ESSENCE Section with tag icon and category selection tags
 */
@Composable
private fun EssenceSection(
    sectionTitleColor: Color,
    selectedCategory: MessageCategory,
    onCategorySelected: (MessageCategory) -> Unit,
    inactiveTagBgColor: Color,
    inactiveTagTextColor: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Section Header with Tag Icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = ProdyIcons.LocalOffer,
                contentDescription = null,
                tint = TimeCapsuleAccent,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = stringResource(R.string.essence),
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                letterSpacing = 1.sp,
                color = sectionTitleColor
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Horizontal Scrolling Category Tags
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CategoryTag(
                text = stringResource(R.string.goal),
                isSelected = selectedCategory == MessageCategory.GOAL,
                onClick = { onCategorySelected(MessageCategory.GOAL) },
                inactiveTagBgColor = inactiveTagBgColor,
                inactiveTagTextColor = inactiveTagTextColor
            )
            CategoryTag(
                text = stringResource(R.string.motivation),
                isSelected = selectedCategory == MessageCategory.MOTIVATION,
                onClick = { onCategorySelected(MessageCategory.MOTIVATION) },
                inactiveTagBgColor = inactiveTagBgColor,
                inactiveTagTextColor = inactiveTagTextColor
            )
            CategoryTag(
                text = stringResource(R.string.promise),
                isSelected = selectedCategory == MessageCategory.PROMISE,
                onClick = { onCategorySelected(MessageCategory.PROMISE) },
                inactiveTagBgColor = inactiveTagBgColor,
                inactiveTagTextColor = inactiveTagTextColor
            )
            CategoryTag(
                text = stringResource(R.string.prediction),
                isSelected = selectedCategory == MessageCategory.GENERAL,
                onClick = { onCategorySelected(MessageCategory.GENERAL) },
                inactiveTagBgColor = inactiveTagBgColor,
                inactiveTagTextColor = inactiveTagTextColor
            )
            // Right padding spacer
            Spacer(modifier = Modifier.width(24.dp))
        }
    }
}

/**
 * Reusable Time Tag component with animated selection state
 */
@Composable
private fun TimeTag(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    inactiveTagBgColor: Color,
    inactiveTagTextColor: Color
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) TimeCapsuleAccent else inactiveTagBgColor,
        animationSpec = tween(200),
        label = "time_tag_bg"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else inactiveTagTextColor,
        animationSpec = tween(200),
        label = "time_tag_text"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .defaultMinSize(minWidth = 80.dp, minHeight = 48.dp)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = textColor
        )
    }
}

/**
 * Reusable Category Tag component with animated selection state
 */
@Composable
private fun CategoryTag(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    inactiveTagBgColor: Color,
    inactiveTagTextColor: Color
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) TimeCapsuleAccent else inactiveTagBgColor,
        animationSpec = tween(200),
        label = "category_tag_bg"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else inactiveTagTextColor,
        animationSpec = tween(200),
        label = "category_tag_text"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .defaultMinSize(minWidth = 80.dp, minHeight = 48.dp)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = textColor
        )
    }
}

/**
 * Seal & Schedule Button - Full width with lock icon
 */
@Composable
private fun SealAndScheduleButton(
    onClick: () -> Unit,
    enabled: Boolean,
    isLoading: Boolean,
    buttonTextColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(55.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(
                if (enabled) TimeCapsuleAccent else TimeCapsuleAccent.copy(alpha = 0.5f)
            )
            .clickable(enabled = enabled && !isLoading) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = buttonTextColor,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = ProdyIcons.Lock,
                    contentDescription = null,
                    tint = buttonTextColor,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = stringResource(R.string.seal_and_schedule),
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = buttonTextColor
                )
            }
        }
    }
}

enum class DatePreset {
    ONE_WEEK, ONE_MONTH, SIX_MONTHS, ONE_YEAR, CUSTOM
}

enum class MessageCategory(val displayName: String) {
    GENERAL("General"),
    GOAL("Goal"),
    PROMISE("Promise"),
    MOTIVATION("Motivation")
}

/**
 * ESSENCE Section with multi-select category tags
 */
@Composable
private fun EssenceSectionMultiSelect(
    sectionTitleColor: Color,
    selectedCategories: Set<MessageCategory>,
    onCategoryToggle: (MessageCategory) -> Unit,
    inactiveTagBgColor: Color,
    inactiveTagTextColor: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Section Header with Tag Icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = ProdyIcons.LocalOffer,
                contentDescription = null,
                tint = TimeCapsuleAccent,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = stringResource(R.string.essence),
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                letterSpacing = 1.sp,
                color = sectionTitleColor
            )
            // Multi-select indicator
            Text(
                text = "(select multiple)",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 10.sp,
                color = sectionTitleColor.copy(alpha = 0.6f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Horizontal Scrolling Category Tags with multi-select
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MultiSelectCategoryTag(
                text = stringResource(R.string.goal),
                isSelected = selectedCategories.contains(MessageCategory.GOAL),
                onClick = { onCategoryToggle(MessageCategory.GOAL) },
                inactiveTagBgColor = inactiveTagBgColor,
                inactiveTagTextColor = inactiveTagTextColor
            )
            MultiSelectCategoryTag(
                text = stringResource(R.string.motivation),
                isSelected = selectedCategories.contains(MessageCategory.MOTIVATION),
                onClick = { onCategoryToggle(MessageCategory.MOTIVATION) },
                inactiveTagBgColor = inactiveTagBgColor,
                inactiveTagTextColor = inactiveTagTextColor
            )
            MultiSelectCategoryTag(
                text = stringResource(R.string.promise),
                isSelected = selectedCategories.contains(MessageCategory.PROMISE),
                onClick = { onCategoryToggle(MessageCategory.PROMISE) },
                inactiveTagBgColor = inactiveTagBgColor,
                inactiveTagTextColor = inactiveTagTextColor
            )
            MultiSelectCategoryTag(
                text = stringResource(R.string.prediction),
                isSelected = selectedCategories.contains(MessageCategory.GENERAL),
                onClick = { onCategoryToggle(MessageCategory.GENERAL) },
                inactiveTagBgColor = inactiveTagBgColor,
                inactiveTagTextColor = inactiveTagTextColor
            )
            // Right padding spacer
            Spacer(modifier = Modifier.width(24.dp))
        }
    }
}

/**
 * Multi-select Category Tag with checkmark indicator
 */
@Composable
private fun MultiSelectCategoryTag(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    inactiveTagBgColor: Color,
    inactiveTagTextColor: Color
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) TimeCapsuleAccent else inactiveTagBgColor,
        animationSpec = tween(200),
        label = "multi_category_tag_bg"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else inactiveTagTextColor,
        animationSpec = tween(200),
        label = "multi_category_tag_text"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .defaultMinSize(minWidth = 80.dp, minHeight = 48.dp)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            AnimatedVisibility(
                visible = isSelected,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                Icon(
                    imageVector = ProdyIcons.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
            Text(
                text = text,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = textColor
            )
        }
    }
}

/**
 * Sealing Animation Overlay - Shows lock sealing animation
 */
@Composable
private fun SealingAnimationOverlay(
    backgroundColor: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sealing")

    // Pulsing lock scale
    val lockScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lock_scale"
    )

    // Rotating particles
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Glow alpha
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor.copy(alpha = 0.95f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Animated lock icon with glow effect
            Box(
                contentAlignment = Alignment.Center
            ) {
                // Outer glow
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .scale(lockScale * 1.2f)
                        .alpha(glowAlpha)
                        .clip(CircleShape)
                        .background(TimeCapsuleAccent.copy(alpha = 0.2f))
                )

                // Inner glow
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .scale(lockScale)
                        .clip(CircleShape)
                        .background(TimeCapsuleAccent.copy(alpha = 0.3f))
                )

                // Lock icon
                Icon(
                    imageVector = ProdyIcons.Lock,
                    contentDescription = "Sealing",
                    tint = TimeCapsuleAccent,
                    modifier = Modifier
                        .size(48.dp)
                        .scale(lockScale)
                )
            }

            // Text
            Text(
                text = "Sealing your time capsule...",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                color = TimeCapsuleAccent,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Your message is being locked away\nfor your future self",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Discard Changes Confirmation Dialog
 */
@Composable
private fun DiscardChangesDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Discard Changes?",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Text(
                text = "You have unsaved changes. Are you sure you want to discard them?",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Normal
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = "Discard",
                    color = MaterialTheme.colorScheme.error,
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
                    fontWeight = FontWeight.Medium
                )
            }
        }
    )
}

/**
 * Format duration in milliseconds to mm:ss
 */
private fun formatDuration(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%d:%02d", minutes, seconds)
}
