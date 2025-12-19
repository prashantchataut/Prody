package com.prody.prashant.ui.screens.futuremessage

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    var showDatePicker by remember { mutableStateOf(false) }
    val isDarkTheme = isSystemInDarkTheme()

    // Magical sealing animation state
    val sealState = rememberTimeCapsuleSealState()

    // Theme-aware colors
    val backgroundColor = if (isDarkTheme) TimeCapsuleBackgroundDark else TimeCapsuleBackgroundLight
    val titleTextColor = if (isDarkTheme) TimeCapsuleTitleTextDark else TimeCapsuleTitleTextLight
    val discardTextColor = if (isDarkTheme) TimeCapsuleDiscardTextDark else TimeCapsuleDiscardTextLight
    val placeholderColor = if (isDarkTheme) TimeCapsulePlaceholderDark else TimeCapsulePlaceholderLight
    val activeTextColor = if (isDarkTheme) TimeCapsuleActiveTextDark else TimeCapsuleActiveTextLight
    val multimediaIconColor = if (isDarkTheme) TimeCapsuleMultimediaIconDark else TimeCapsuleMultimediaIconLight
    val attachTextColor = if (isDarkTheme) TimeCapsuleAttachTextDark else TimeCapsuleAttachTextLight
    val dividerColor = if (isDarkTheme) TimeCapsuleDividerDark else TimeCapsuleDividerLight
    val sectionTitleColor = if (isDarkTheme) TimeCapsuleSectionTitleDark else TimeCapsuleSectionTitleLight
    val inactiveTagBgColor = if (isDarkTheme) TimeCapsuleInactiveTagBgDark else TimeCapsuleInactiveTagBgLight
    val inactiveTagTextColor = if (isDarkTheme) TimeCapsuleInactiveTagTextDark else TimeCapsuleInactiveTagTextLight
    val buttonTextColor = if (isDarkTheme) TimeCapsuleButtonTextDark else TimeCapsuleButtonTextLight

    // Handle saved state with magical animation
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            // Small delay for the animation to complete
            delay(100)
            onMessageSaved()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp) // Space for bottom button
        ) {
            // Top Header Bar
            TimeCapsuleTopBar(
                titleTextColor = titleTextColor,
                discardTextColor = discardTextColor,
                onBackClick = onNavigateBack,
                onDiscardClick = onNavigateBack
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Capsule Title Input
            TimeCapsuleTitleInput(
                value = uiState.title,
                onValueChange = { viewModel.updateTitle(it) },
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
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Multimedia Attachment Row
            MultimediaAttachmentRow(
                iconColor = multimediaIconColor,
                attachTextColor = attachTextColor,
                dividerColor = dividerColor,
                backgroundColor = inactiveTagBgColor,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // TIME TRAVEL Section
            TimeTravelSection(
                sectionTitleColor = sectionTitleColor,
                selectedPreset = uiState.selectedPreset,
                onPresetSelected = { viewModel.selectDatePreset(it) },
                inactiveTagBgColor = inactiveTagBgColor,
                inactiveTagTextColor = inactiveTagTextColor,
                onCustomDateClick = { showDatePicker = true },
                modifier = Modifier.padding(start = 24.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // ESSENCE Section
            EssenceSection(
                sectionTitleColor = sectionTitleColor,
                selectedCategory = uiState.selectedCategory,
                onCategorySelected = { viewModel.updateCategory(it) },
                inactiveTagBgColor = inactiveTagBgColor,
                inactiveTagTextColor = inactiveTagTextColor,
                modifier = Modifier.padding(start = 24.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))
        }

        // Seal & Schedule Button - Fixed at bottom
        SealAndScheduleButton(
            onClick = {
                // Trigger the magical sealing animation
                sealState.startSealing()
                viewModel.saveMessage()
            },
            enabled = uiState.canSave && !uiState.isSaving,
            isLoading = uiState.isSaving,
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
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = uiState.deliveryDate
            )

            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let {
                                viewModel.selectCustomDate(it)
                            }
                            showDatePicker = false
                        }
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
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
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp),
        textStyle = TextStyle(
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = activeTextColor,
            lineHeight = 24.sp
        ),
        cursorBrush = SolidColor(TimeCapsuleAccent),
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
    modifier: Modifier = Modifier
) {
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
                .clickable { /* TODO: Open camera/gallery */ },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.CameraAlt,
                contentDescription = "Attach photo",
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }

        // Microphone Icon Button
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(backgroundColor)
                .clickable { /* TODO: Open voice recorder */ },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Mic,
                contentDescription = "Record voice",
                tint = iconColor,
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

        // Attach memories text
        Text(
            text = stringResource(R.string.attach_memories),
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = attachTextColor
        )
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
                imageVector = Icons.Filled.RocketLaunch,
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
                imageVector = Icons.Filled.LocalOffer,
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
                    imageVector = Icons.Filled.Lock,
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
