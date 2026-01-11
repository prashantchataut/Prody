package com.prody.prashant.ui.screens.journal

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import com.prody.prashant.ui.theme.isDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.data.local.entity.JournalEntryEntity
import com.prody.prashant.domain.model.Mood
import com.prody.prashant.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Journal History Screen - Premium Phase 2 Redesign
 *
 * A completely redesigned screen with an innovative visual timeline that connects
 * journal entries chronologically. Features the new Prody design system with:
 *
 * Design Philosophy:
 * - Extreme minimalism, flat design - NO shadows, gradients, or hi-fi elements
 * - Deep teal dark (#0D2826), clean off-white light (#F0F4F3)
 * - Vibrant neon green accent (#36F97F) for interactive elements
 * - Poppins typography throughout
 * - 8dp grid spacing system
 * - Innovative timeline visual connecting entries with green-accented lines/dots
 *
 * Features:
 * - Chronological timeline with connected entry dots
 * - Section headers (This Week, Last Week, Older)
 * - Premium entry cards with mood/intensity display
 * - Filter/Sort bottom sheet
 * - Load more functionality
 * - Empty state with elegant design
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalHistoryScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    viewModel: JournalHistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDark = isDarkTheme()

    // Premium theme-aware colors using MaterialTheme
    val backgroundColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant
    val accentColor = MaterialTheme.colorScheme.primary
    val dividerColor = MaterialTheme.colorScheme.outlineVariant
    val timelineColor = accentColor.copy(alpha = 0.4f)

    var showFilterSheet by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            PremiumJournalHistoryTopBar(
                onNavigateBack = onNavigateBack,
                onFilterClick = { showFilterSheet = true },
                textColor = textPrimary,
                backgroundColor = backgroundColor,
                hasActiveFilters = uiState.hasActiveFilters,
                activeFilterCount = uiState.activeFilterCount
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = accentColor,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(40.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(backgroundColor),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp) // Timeline handles spacing
            ) {
                // This Week Section with Timeline
                if (uiState.thisWeekEntries.isNotEmpty()) {
                    item {
                        TimelineSectionHeader(
                            title = "This Week",
                            entryCount = uiState.thisWeekEntries.size,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            accentColor = accentColor
                        )
                    }
                    itemsIndexed(
                        items = uiState.thisWeekEntries,
                        key = { _, entry -> "thisweek_${entry.id}" }
                    ) { index, entry ->
                        TimelineEntryRow(
                            entry = entry,
                            onClick = { onNavigateToDetail(entry.id) },
                            isFirst = index == 0,
                            isLast = index == uiState.thisWeekEntries.lastIndex,
                            isDarkTheme = isDark,
                            accentColor = accentColor,
                            timelineColor = timelineColor,
                            surfaceColor = surfaceColor,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary
                        )
                    }
                }

                // Last Week Section with Timeline
                if (uiState.lastWeekEntries.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        TimelineSectionHeader(
                            title = "Last Week",
                            entryCount = uiState.lastWeekEntries.size,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            accentColor = accentColor
                        )
                    }
                    itemsIndexed(
                        items = uiState.lastWeekEntries,
                        key = { _, entry -> "lastweek_${entry.id}" }
                    ) { index, entry ->
                        TimelineEntryRow(
                            entry = entry,
                            onClick = { onNavigateToDetail(entry.id) },
                            isFirst = index == 0,
                            isLast = index == uiState.lastWeekEntries.lastIndex,
                            isDarkTheme = isDark,
                            accentColor = accentColor,
                            timelineColor = timelineColor,
                            surfaceColor = surfaceColor,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary
                        )
                    }
                }

                // Older Section with Timeline
                if (uiState.olderEntries.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        TimelineSectionHeader(
                            title = "Earlier",
                            entryCount = uiState.totalOlderCount,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            accentColor = accentColor
                        )
                    }
                    val displayedOlder = uiState.olderEntries.take(uiState.displayedOlderCount)
                    itemsIndexed(
                        items = displayedOlder,
                        key = { _, entry -> "older_${entry.id}" }
                    ) { index, entry ->
                        TimelineEntryRow(
                            entry = entry,
                            onClick = { onNavigateToDetail(entry.id) },
                            isFirst = index == 0,
                            isLast = index == displayedOlder.lastIndex && uiState.displayedOlderCount >= uiState.totalOlderCount,
                            isDarkTheme = isDark,
                            accentColor = accentColor,
                            timelineColor = timelineColor,
                            surfaceColor = surfaceColor,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary
                        )
                    }

                    // Load More Button
                    if (uiState.displayedOlderCount < uiState.totalOlderCount) {
                        item {
                            PremiumLoadMoreButton(
                                onClick = { viewModel.loadMoreOlderEntries() },
                                remainingCount = uiState.totalOlderCount - uiState.displayedOlderCount,
                                textColor = textPrimary,
                                accentColor = accentColor,
                                timelineColor = timelineColor
                            )
                        }
                    }
                }

                // Empty State
                if (uiState.thisWeekEntries.isEmpty() &&
                    uiState.lastWeekEntries.isEmpty() &&
                    uiState.olderEntries.isEmpty()
                ) {
                    item {
                        PremiumEmptyHistoryState(
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            hasActiveFilters = uiState.hasActiveFilters,
                            onClearFilters = { viewModel.clearAllFilters() }
                        )
                    }
                }

                // Bottom spacing
                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }

    // Filter/Sort Bottom Sheet
    if (showFilterSheet) {
        PremiumFilterSortBottomSheet(
            currentSortOrder = uiState.sortOrder,
            currentFilterMood = uiState.selectedFilterMood,
            currentBookmarkedOnly = uiState.showBookmarkedOnly,
            currentDateRangeFilter = uiState.dateRangeFilter,
            hasActiveFilters = uiState.hasActiveFilters,
            onSortOrderChange = { viewModel.setSortOrder(it) },
            onFilterMoodChange = { viewModel.setFilterMood(it) },
            onBookmarkedOnlyChange = { viewModel.setBookmarkedOnly(it) },
            onDateRangeFilterChange = { viewModel.setDateRangeFilter(it) },
            onClearAllFilters = { viewModel.clearAllFilters() },
            onDismiss = { showFilterSheet = false },
            isDarkTheme = isDark
        )
    }
}

/**
 * Premium Top Bar with minimal design
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PremiumJournalHistoryTopBar(
    onNavigateBack: () -> Unit,
    onFilterClick: () -> Unit,
    textColor: Color,
    backgroundColor: Color,
    hasActiveFilters: Boolean,
    activeFilterCount: Int,
    accentColor: Color = JournalHistoryAccent
) {
    TopAppBar(
        title = {
            Text(
                text = "Journal History",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = textColor
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .size(48.dp)
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Navigate back",
                    tint = textColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        actions = {
            Box {
                IconButton(
                    onClick = onFilterClick,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = if (hasActiveFilters) Icons.Filled.FilterAlt else Icons.Filled.Tune,
                        contentDescription = "Filter and sort",
                        tint = if (hasActiveFilters) JournalHistoryAccent else textColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                // Filter badge
                if (hasActiveFilters) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 6.dp, end = 6.dp)
                            .size(18.dp)
                            .clip(RoundedCornerShape(9.dp))
                            .background(JournalHistoryAccent),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$activeFilterCount",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = backgroundColor,
            titleContentColor = textColor,
            navigationIconContentColor = textColor,
            actionIconContentColor = accentColor
        )
    )
}

/**
 * Timeline Section Header with entry count badge
 */
@Composable
private fun TimelineSectionHeader(
    title: String,
    entryCount: Int,
    textPrimary: Color,
    textSecondary: Color,
    accentColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = textPrimary
        )

        // Entry count badge
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = accentColor.copy(alpha = 0.15f)
        ) {
            Text(
                text = "$entryCount entries",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = accentColor,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
    }
}

/**
 * Timeline Entry Row with connected dot and line visual
 */
@Composable
private fun TimelineEntryRow(
    entry: JournalEntryEntity,
    onClick: () -> Unit,
    isFirst: Boolean,
    isLast: Boolean,
    isDarkTheme: Boolean,
    accentColor: Color,
    timelineColor: Color,
    surfaceColor: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(150),
        label = "scale"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        verticalAlignment = Alignment.Top
    ) {
        // Timeline column (dot + line)
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(IntrinsicSize.Max),
            contentAlignment = Alignment.TopCenter
        ) {
            // Timeline vertical line
            Canvas(
                modifier = Modifier
                    .width(2.dp)
                    .fillMaxHeight()
            ) {
                val lineTop = if (isFirst) size.height / 2 else 0f
                val lineBottom = if (isLast) size.height / 2 else size.height

                drawLine(
                    color = timelineColor,
                    start = Offset(size.width / 2, lineTop),
                    end = Offset(size.width / 2, lineBottom),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            // Timeline dot - accent green for current entry
            Box(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(accentColor)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Entry Card
        PremiumEntryCard(
            entry = entry,
            onClick = onClick,
            onPressChange = { isPressed = it },
            surfaceColor = surfaceColor,
            textPrimary = textPrimary,
            textSecondary = textSecondary,
            accentColor = accentColor,
            isDarkTheme = isDarkTheme,
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 16.dp)
        )
    }
}

/**
 * Premium Entry Card with flat design
 */
@Composable
private fun PremiumEntryCard(
    entry: JournalEntryEntity,
    onClick: () -> Unit,
    onPressChange: (Boolean) -> Unit,
    surfaceColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    accentColor: Color,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("MMM d", Locale.getDefault()) }
    val dayOfWeekFormat = remember { SimpleDateFormat("EEEE", Locale.getDefault()) }
    val timeFormat = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) }
    val date = remember(entry.createdAt) { Date(entry.createdAt) }

    val mood = remember(entry.mood) { Mood.fromString(entry.mood) }
    val moodColor = getMoodColorPremium(mood)
    val moodIcon = getMoodIconPremium(mood)

    // Get entry type based on tags
    val entryType = remember(entry.tags) {
        when {
            entry.tags.contains("reflection", ignoreCase = true) -> "Reflection"
            entry.tags.contains("gratitude", ignoreCase = true) -> "Gratitude"
            entry.tags.contains("free", ignoreCase = true) -> "Free Write"
            entry.tags.contains("goal", ignoreCase = true) -> "Goals"
            else -> "Entry"
        }
    }

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        color = surfaceColor,
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 0.dp // Flat design - no elevation
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Top row: Date and mood
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Date display
                Column {
                    Text(
                        text = dateFormat.format(date),
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = textPrimary
                    )
                    Text(
                        text = "${dayOfWeekFormat.format(date)} · ${timeFormat.format(date)}",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 13.sp,
                        color = textSecondary
                    )
                }

                // Mood chip
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = moodColor.copy(alpha = 0.12f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = moodIcon,
                            contentDescription = null,
                            tint = moodColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = mood.displayName,
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            color = moodColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Content preview
            Text(
                text = entry.content,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = textSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom row: Entry type and intensity
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Entry type tag
                Text(
                    text = entryType.uppercase(),
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp,
                    color = textSecondary,
                    letterSpacing = 1.sp
                )

                // Intensity indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Intensity",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = textSecondary
                    )
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = accentColor, fontWeight = FontWeight.Bold)) {
                                append("${entry.moodIntensity}")
                            }
                            withStyle(SpanStyle(color = textSecondary)) {
                                append("/10")
                            }
                        },
                        fontFamily = PoppinsFamily,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

/**
 * Premium Load More Button with timeline continuation
 */
@Composable
private fun PremiumLoadMoreButton(
    onClick: () -> Unit,
    remainingCount: Int,
    textColor: Color,
    accentColor: Color,
    timelineColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Timeline continuation
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(80.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Canvas(
                modifier = Modifier
                    .width(2.dp)
                    .height(40.dp)
            ) {
                val pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                drawLine(
                    color = timelineColor,
                    start = Offset(size.width / 2, 0f),
                    end = Offset(size.width / 2, size.height),
                    strokeWidth = 2.dp.toPx(),
                    pathEffect = pathEffect
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Load More Button
        Surface(
            modifier = Modifier
                .weight(1f)
                .padding(top = 8.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = 1.5.dp,
                    color = accentColor.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable(onClick = onClick),
            color = Color.Transparent
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.ExpandMore,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Load $remainingCount More",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = accentColor
                )
            }
        }
    }
}

/**
 * Premium Empty State with elegant design
 */
@Composable
private fun PremiumEmptyHistoryState(
    textPrimary: Color,
    textSecondary: Color,
    hasActiveFilters: Boolean = false,
    onClearFilters: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (hasActiveFilters) Icons.Outlined.SearchOff else Icons.Outlined.AutoStories,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = textSecondary.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (hasActiveFilters) "No Matching Entries" else "No Journal Entries Yet",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            color = textPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (hasActiveFilters) "Try adjusting your filters to see more entries"
                   else "Start writing to build your journal history",
            style = MaterialTheme.typography.bodyMedium,
            color = textSecondary
        )
        if (hasActiveFilters) {
            Spacer(modifier = Modifier.height(20.dp))
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable(onClick = onClearFilters),
                color = JournalHistoryAccent.copy(alpha = 0.15f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = null,
                        tint = JournalHistoryAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Clear Filters",
                        style = MaterialTheme.typography.labelMedium,
                        color = JournalHistoryAccent,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/**
 * Premium Filter/Sort Bottom Sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PremiumFilterSortBottomSheet(
    currentSortOrder: SortOrder,
    currentFilterMood: String?,
    currentBookmarkedOnly: Boolean,
    currentDateRangeFilter: DateRangeFilter,
    hasActiveFilters: Boolean,
    onSortOrderChange: (SortOrder) -> Unit,
    onFilterMoodChange: (String?) -> Unit,
    onBookmarkedOnlyChange: (Boolean) -> Unit,
    onDateRangeFilterChange: (DateRangeFilter) -> Unit,
    onClearAllFilters: () -> Unit,
    onDismiss: () -> Unit,
    isDarkTheme: Boolean
) {
    val sheetBackground = if (isDarkTheme) JournalHistoryCardDark else JournalHistoryCardLight
    val textPrimary = if (isDarkTheme) JournalHistoryTextPrimaryDark else JournalHistoryTextPrimaryLight
    val textSecondary = if (isDarkTheme) JournalHistoryTextSecondaryDark else JournalHistoryTextSecondaryLight
    val dividerColor = if (isDarkTheme) JournalHistoryDividerDark.copy(alpha = 0.3f) else JournalHistoryDividerLight.copy(alpha = 0.3f)

    // Track expanded sections
    var sortExpanded by remember { mutableStateOf(false) }
    var moodExpanded by remember { mutableStateOf(false) }
    var dateRangeExpanded by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = sheetBackground,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .width(48.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(textSecondary.copy(alpha = 0.3f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp)
        ) {
            // Header with Clear All button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filter & Sort",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
                if (hasActiveFilters) {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onClearAllFilters() },
                        color = JournalHistoryAccent.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = null,
                                tint = JournalHistoryAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Clear All",
                                style = MaterialTheme.typography.labelSmall,
                                color = JournalHistoryAccent,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Bookmarked Only Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onBookmarkedOnlyChange(!currentBookmarkedOnly) }
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = if (currentBookmarkedOnly) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = null,
                        tint = if (currentBookmarkedOnly) JournalHistoryAccent else textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Bookmarked Only",
                        style = MaterialTheme.typography.bodyLarge,
                        color = textPrimary,
                        fontWeight = if (currentBookmarkedOnly) FontWeight.Medium else FontWeight.Normal
                    )
                }
                Switch(
                    checked = currentBookmarkedOnly,
                    onCheckedChange = { onBookmarkedOnlyChange(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = JournalHistoryAccent,
                        checkedTrackColor = JournalHistoryAccent.copy(alpha = 0.3f)
                    )
                )
            }

            HorizontalDivider(color = dividerColor, modifier = Modifier.padding(vertical = 8.dp))

            // Sort Section (Collapsible)
            FilterSectionHeader(
                title = "SORT BY",
                currentValue = when (currentSortOrder) {
                    SortOrder.NEWEST_FIRST -> "Newest First"
                    SortOrder.OLDEST_FIRST -> "Oldest First"
                    SortOrder.HIGHEST_INTENSITY -> "Highest Intensity"
                    SortOrder.LOWEST_INTENSITY -> "Lowest Intensity"
                },
                isExpanded = sortExpanded,
                onToggle = { sortExpanded = !sortExpanded },
                textPrimary = textPrimary,
                textSecondary = textSecondary
            )

            AnimatedVisibility(visible = sortExpanded) {
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    SortOrder.entries.forEach { sortOrder ->
                        FilterSortOption(
                            label = when (sortOrder) {
                                SortOrder.NEWEST_FIRST -> "Newest First"
                                SortOrder.OLDEST_FIRST -> "Oldest First"
                                SortOrder.HIGHEST_INTENSITY -> "Highest Intensity"
                                SortOrder.LOWEST_INTENSITY -> "Lowest Intensity"
                            },
                            isSelected = currentSortOrder == sortOrder,
                            onClick = {
                                onSortOrderChange(sortOrder)
                                sortExpanded = false
                            },
                            textPrimary = textPrimary,
                            accentColor = JournalHistoryAccent
                        )
                    }
                }
            }

            HorizontalDivider(color = dividerColor, modifier = Modifier.padding(vertical = 8.dp))

            // Date Range Section (Collapsible)
            FilterSectionHeader(
                title = "DATE RANGE",
                currentValue = when (currentDateRangeFilter) {
                    DateRangeFilter.ALL_TIME -> "All Time"
                    DateRangeFilter.THIS_WEEK -> "This Week"
                    DateRangeFilter.THIS_MONTH -> "This Month"
                    DateRangeFilter.LAST_3_MONTHS -> "Last 3 Months"
                    DateRangeFilter.THIS_YEAR -> "This Year"
                },
                isExpanded = dateRangeExpanded,
                onToggle = { dateRangeExpanded = !dateRangeExpanded },
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                isActive = currentDateRangeFilter != DateRangeFilter.ALL_TIME
            )

            AnimatedVisibility(visible = dateRangeExpanded) {
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    DateRangeFilter.entries.forEach { dateRange ->
                        FilterSortOption(
                            label = when (dateRange) {
                                DateRangeFilter.ALL_TIME -> "All Time"
                                DateRangeFilter.THIS_WEEK -> "This Week"
                                DateRangeFilter.THIS_MONTH -> "This Month"
                                DateRangeFilter.LAST_3_MONTHS -> "Last 3 Months"
                                DateRangeFilter.THIS_YEAR -> "This Year"
                            },
                            isSelected = currentDateRangeFilter == dateRange,
                            onClick = {
                                onDateRangeFilterChange(dateRange)
                                dateRangeExpanded = false
                            },
                            textPrimary = textPrimary,
                            accentColor = JournalHistoryAccent
                        )
                    }
                }
            }

            HorizontalDivider(color = dividerColor, modifier = Modifier.padding(vertical = 8.dp))

            // Mood Filter Section (Collapsible)
            FilterSectionHeader(
                title = "MOOD",
                currentValue = currentFilterMood?.let {
                    try {
                        Mood.valueOf(it).displayName
                    } catch (e: Exception) {
                        "All Moods"
                    }
                } ?: "All Moods",
                isExpanded = moodExpanded,
                onToggle = { moodExpanded = !moodExpanded },
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                isActive = currentFilterMood != null
            )

            AnimatedVisibility(visible = moodExpanded) {
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    // All moods option
                    FilterSortOption(
                        label = "All Moods",
                        isSelected = currentFilterMood == null,
                        onClick = {
                            onFilterMoodChange(null)
                            moodExpanded = false
                        },
                        textPrimary = textPrimary,
                        accentColor = JournalHistoryAccent
                    )

                    // Individual mood options
                    Mood.entries.forEach { mood ->
                        FilterSortOption(
                            label = mood.displayName,
                            isSelected = currentFilterMood?.equals(mood.name, ignoreCase = true) == true,
                            onClick = {
                                onFilterMoodChange(mood.name)
                                moodExpanded = false
                            },
                            textPrimary = textPrimary,
                            accentColor = getMoodColorForHistory(mood),
                            icon = getMoodIconForHistory(mood)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Apply button
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = JournalHistoryAccent,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Done",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

/**
 * Premium Filter Option Row
 */
@Composable
private fun FilterSectionHeader(
    title: String,
    currentValue: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    textPrimary: Color,
    textSecondary: Color,
    isActive: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onToggle)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = textSecondary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = currentValue,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isActive) JournalHistoryAccent else textPrimary,
                fontWeight = if (isActive) FontWeight.Medium else FontWeight.Normal
            )
        }
        Icon(
            imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
            contentDescription = if (isExpanded) "Collapse" else "Expand",
            tint = textSecondary,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun FilterSortOption(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    textPrimary: Color,
    accentColor: Color,
    icon: ImageVector? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) accentColor else textPrimary.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected) accentColor else textPrimary,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
            )
        }
        if (isSelected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Selected",
                tint = accentColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Mood color helper for history filter
 */
private fun getMoodColorForHistory(mood: Mood): Color = getMoodColorPremium(mood)

/**
 * Mood icon helper for history filter
 */
private fun getMoodIconForHistory(mood: Mood): ImageVector = getMoodIconPremium(mood)

/**
 * Premium mood color mapping - using canonical colors from design system
 */
private fun getMoodColorPremium(mood: Mood): Color {
    return when (mood) {
        Mood.HAPPY -> MoodHappy               // Sunshine gold
        Mood.EXCITED -> MoodExcited           // Vibrant coral
        Mood.CALM -> MoodCalm                 // Serene sky blue
        Mood.GRATEFUL -> MoodGrateful         // Soft sage
        Mood.ANXIOUS -> MoodAnxious           // Soft coral
        Mood.CONFUSED -> MoodConfused         // Soft lavender
        Mood.SAD -> MoodSad                   // Muted slate blue
        Mood.MOTIVATED -> MoodMotivated       // Energetic amber
        Mood.NOSTALGIC -> MoodNostalgic       // Warm sepia
    }
}

/**
 * Premium mood icon mapping
 */
private fun getMoodIconPremium(mood: Mood): ImageVector {
    return when (mood) {
        Mood.HAPPY -> Icons.Filled.SentimentVerySatisfied
        Mood.CALM -> Icons.Filled.SelfImprovement
        Mood.ANXIOUS -> Icons.Outlined.Psychology
        Mood.SAD -> Icons.Filled.SentimentDissatisfied
        Mood.MOTIVATED -> Icons.Filled.LocalFireDepartment
        Mood.GRATEFUL -> Icons.Filled.Favorite
        Mood.CONFUSED -> Icons.Outlined.HelpOutline
        Mood.EXCITED -> Icons.Filled.Celebration
        Mood.NOSTALGIC -> Icons.Filled.HistoryEdu
    }
}
