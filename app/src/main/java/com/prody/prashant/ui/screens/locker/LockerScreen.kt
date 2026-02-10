package com.prody.prashant.ui.screens.locker

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.data.local.entity.EvidenceEntity
import com.prody.prashant.data.local.entity.EvidenceRarity
import com.prody.prashant.data.local.entity.EvidenceType
import com.prody.prashant.ui.theme.ProdyAccentGreen
import com.prody.prashant.ui.theme.isDarkTheme
import com.prody.prashant.util.SecureScreen
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

// =============================================================================
// DESIGN CONSTANTS
// =============================================================================

// Evidence Type Colors
private val ReceiptColor = Color(0xFFFFA726) // Orange
private val WitnessColor = Color(0xFF42A5F5) // Blue
private val ProphecyColor = Color(0xFFAB47BC) // Purple
private val BreakthroughColor = Color(0xFF66BB6A) // Green
private val StreakColor = Color(0xFFEF5350) // Red

// Rarity Colors
private val CommonColor = Color(0xFF9E9E9E)
private val RareColor = Color(0xFF42A5F5)
private val EpicColor = Color(0xFFAB47BC)
private val LegendaryColor = Color(0xFFFFD54F)

// =============================================================================
// MAIN LOCKER SCREEN
// =============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LockerScreen(
    viewModel: LockerViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val evidence by viewModel.evidence.collectAsStateWithLifecycle()

    // Security: Prevent screenshots and screen recordings of growth evidence and journal snippets
    SecureScreen()

    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val selectedEvidence by viewModel.selectedEvidence.collectAsStateWithLifecycle()
    val totalCount by viewModel.totalCount.collectAsStateWithLifecycle()
    val unviewedCount by viewModel.unviewedCount.collectAsStateWithLifecycle()
    val typeCounts by viewModel.typeCounts.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    val isDark = isDarkTheme()
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val backgroundColor = if (isDark) Color(0xFF0D1B19) else Color(0xFFF5F9F8)
    val cardColor = if (isDark) Color(0xFF1A2E2A) else Color(0xFFFFFFFF)

    Scaffold(
        containerColor = backgroundColor
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                LockerHeader(
                    totalCount = totalCount,
                    unviewedCount = unviewedCount,
                    isDark = isDark
                )
            }

            // Filter Chips
            item {
                FilterChipRow(
                    selectedFilter = selectedFilter,
                    onFilterSelected = { viewModel.setFilter(it) },
                    typeCounts = typeCounts,
                    isDark = isDark
                )
            }

            // Evidence Grid
            if (evidence.isEmpty() && !isLoading) {
                item {
                    EmptyLockerState(
                        filter = selectedFilter,
                        isDark = isDark
                    )
                }
            } else {
                itemsIndexed(
                    items = evidence,
                    key = { _, item -> item.id }
                ) { index, item ->
                    EvidenceCard(
                        evidence = item,
                        index = index,
                        isDark = isDark,
                        onClick = { viewModel.selectEvidence(item) },
                        onPinClick = { viewModel.togglePin(item) }
                    )
                }
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Evidence Detail Bottom Sheet
    selectedEvidence?.let { evidence ->
        ModalBottomSheet(
            onDismissRequest = { viewModel.clearSelection() },
            sheetState = sheetState,
            containerColor = cardColor
        ) {
            EvidenceDetailSheet(
                evidence = evidence,
                isDark = isDark,
                onDismiss = {
                    scope.launch {
                        sheetState.hide()
                        viewModel.clearSelection()
                    }
                },
                onPinClick = { viewModel.togglePin(evidence) }
            )
        }
    }
}

// =============================================================================
// HEADER
// =============================================================================

@Composable
private fun LockerHeader(
    totalCount: Int,
    unviewedCount: Int,
    isDark: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "THE LOCKER",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else Color(0xFF1A2E2A),
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Concrete evidence of your growth",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isDark) Color.White.copy(alpha = 0.6f) else Color(0xFF1A2E2A).copy(alpha = 0.6f)
                )
            }

            // Total count badge
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(ProdyAccentGreen, ProdyAccentGreen.copy(alpha = 0.7f))
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$totalCount",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        // Unviewed indicator
        AnimatedVisibility(
            visible = unviewedCount > 0,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            Row(
                modifier = Modifier.padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(ReceiptColor, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$unviewedCount new evidence to review",
                    style = MaterialTheme.typography.bodySmall,
                    color = ReceiptColor
                )
            }
        }
    }
}

// =============================================================================
// FILTER CHIPS
// =============================================================================

@Composable
private fun FilterChipRow(
    selectedFilter: EvidenceFilter,
    onFilterSelected: (EvidenceFilter) -> Unit,
    typeCounts: List<com.prody.prashant.data.local.entity.EvidenceTypeCount>,
    isDark: Boolean
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(EvidenceFilter.all) { filter ->
            val count = when (filter) {
                EvidenceFilter.All -> typeCounts.sumOf { it.count }
                EvidenceFilter.Receipt -> typeCounts.find { it.evidenceType == EvidenceType.RECEIPT }?.count ?: 0
                EvidenceFilter.Witness -> typeCounts.find { it.evidenceType == EvidenceType.WITNESS }?.count ?: 0
                EvidenceFilter.Prophecy -> typeCounts.find { it.evidenceType == EvidenceType.PROPHECY }?.count ?: 0
                EvidenceFilter.Breakthrough -> typeCounts.find { it.evidenceType == EvidenceType.BREAKTHROUGH }?.count ?: 0
                EvidenceFilter.Streak -> typeCounts.find { it.evidenceType == EvidenceType.STREAK }?.count ?: 0
                EvidenceFilter.Pinned -> 0 // Don't show count for pinned
            }

            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(filter.emoji)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(filter.displayName)
                        if (count > 0 && filter != EvidenceFilter.Pinned) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "($count)",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (selectedFilter == filter) Color.White else Color.Gray
                            )
                        }
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = getEvidenceTypeColor(filter),
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

// =============================================================================
// EVIDENCE CARD
// =============================================================================

@Composable
private fun EvidenceCard(
    evidence: EvidenceEntity,
    index: Int,
    isDark: Boolean,
    onClick: () -> Unit,
    onPinClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    val typeColor = getEvidenceTypeColorFromString(evidence.evidenceType)
    val rarityColor = getRarityColor(evidence.rarity)
    val cardColor = if (isDark) Color(0xFF1A2E2A) else Color.White

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .alpha(if (evidence.isViewed) 1f else 1f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (evidence.isViewed) 2.dp else 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Type Badge
                Row(
                    modifier = Modifier
                        .background(
                            color = typeColor.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = getEvidenceEmoji(evidence.evidenceType),
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = getEvidenceTypeName(evidence.evidenceType),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = typeColor
                    )
                }

                // Rarity + Pin
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Rarity badge
                    if (evidence.rarity != EvidenceRarity.COMMON) {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = rarityColor.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = rarityColor,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = evidence.rarity.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = rarityColor,
                                fontSize = 10.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    // Pin button
                    IconButton(
                        onClick = onPinClick,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (evidence.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                            contentDescription = if (evidence.isPinned) "Unpin" else "Pin",
                            tint = if (evidence.isPinned) typeColor else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // New indicator
                    if (!evidence.isViewed) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(ReceiptColor, CircleShape)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Content
            Text(
                text = evidence.content,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDark) Color.White else Color(0xFF1A2E2A),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            // Secondary content (for receipts: "Now" vs "Then")
            evidence.secondaryContent?.let { secondary ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = secondary,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isDark) Color.White.copy(alpha = 0.6f) else Color(0xFF1A2E2A).copy(alpha = 0.6f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Date
                Text(
                    text = formatDate(evidence.collectedAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )

                // Days apart for receipts
                evidence.daysApart?.let { days ->
                    Text(
                        text = "$days days between",
                        style = MaterialTheme.typography.labelSmall,
                        color = typeColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// =============================================================================
// EVIDENCE DETAIL SHEET
// =============================================================================

@Composable
private fun EvidenceDetailSheet(
    evidence: EvidenceEntity,
    isDark: Boolean,
    onDismiss: () -> Unit,
    onPinClick: () -> Unit
) {
    val typeColor = getEvidenceTypeColorFromString(evidence.evidenceType)
    val rarityColor = getRarityColor(evidence.rarity)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = getEvidenceEmoji(evidence.evidenceType),
                    fontSize = 32.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = getEvidenceTypeName(evidence.evidenceType),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = typeColor
                    )
                    if (evidence.rarity != EvidenceRarity.COMMON) {
                        Text(
                            text = "${evidence.rarity.uppercase()} EVIDENCE",
                            style = MaterialTheme.typography.labelSmall,
                            color = rarityColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close"
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // For Receipt type: Show "THEN" vs "NOW"
        if (evidence.evidenceType == EvidenceType.RECEIPT) {
            // THEN section
            Text(
                text = "THEN",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = evidence.content,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isDark) Color.White.copy(alpha = 0.8f) else Color(0xFF1A2E2A).copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // NOW section
            evidence.secondaryContent?.let { now ->
                Text(
                    text = "NOW",
                    style = MaterialTheme.typography.labelMedium,
                    color = typeColor,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = now,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isDark) Color.White else Color(0xFF1A2E2A)
                )
            }

            evidence.daysApart?.let { days ->
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = typeColor.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$days days between these thoughts",
                        style = MaterialTheme.typography.bodyMedium,
                        color = typeColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            // Standard content display for other types
            Text(
                text = evidence.content,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isDark) Color.White else Color(0xFF1A2E2A)
            )

            evidence.secondaryContent?.let { secondary ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = secondary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isDark) Color.White.copy(alpha = 0.7f) else Color(0xFF1A2E2A).copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Metadata
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Collected ${formatDate(evidence.collectedAt)}",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )

            // Pin toggle
            Surface(
                onClick = onPinClick,
                color = if (evidence.isPinned) typeColor.copy(alpha = 0.15f) else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (evidence.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                        contentDescription = null,
                        tint = if (evidence.isPinned) typeColor else Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (evidence.isPinned) "Pinned" else "Pin",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (evidence.isPinned) typeColor else Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// =============================================================================
// EMPTY STATE
// =============================================================================

@Composable
private fun EmptyLockerState(
    filter: EvidenceFilter,
    isDark: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = when (filter) {
                EvidenceFilter.All -> "📦"
                EvidenceFilter.Receipt -> "🧾"
                EvidenceFilter.Witness -> "👁️"
                EvidenceFilter.Prophecy -> "🔮"
                EvidenceFilter.Breakthrough -> "💡"
                EvidenceFilter.Streak -> "🔥"
                EvidenceFilter.Pinned -> "📌"
            },
            fontSize = 64.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = when (filter) {
                EvidenceFilter.All -> "Your locker is empty"
                EvidenceFilter.Receipt -> "No receipts yet"
                EvidenceFilter.Witness -> "No witnesses yet"
                EvidenceFilter.Prophecy -> "No prophecies yet"
                EvidenceFilter.Breakthrough -> "No breakthroughs yet"
                EvidenceFilter.Streak -> "No streak milestones yet"
                EvidenceFilter.Pinned -> "Nothing pinned yet"
            },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (isDark) Color.White else Color(0xFF1A2E2A)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = when (filter) {
                EvidenceFilter.All -> "Evidence drops when you grow"
                EvidenceFilter.Receipt -> "Mirror finds contradictions in your journal"
                EvidenceFilter.Witness -> "Haven follows up on things you mention"
                EvidenceFilter.Prophecy -> "Future Messages predict and verify"
                EvidenceFilter.Breakthrough -> "Journal insights get captured"
                EvidenceFilter.Streak -> "Keep your streaks going!"
                EvidenceFilter.Pinned -> "Pin your favorite evidence to find it easily"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

// =============================================================================
// HELPER FUNCTIONS
// =============================================================================

private fun getEvidenceTypeColor(filter: EvidenceFilter): Color {
    return when (filter) {
        EvidenceFilter.All -> ProdyAccentGreen
        EvidenceFilter.Receipt -> ReceiptColor
        EvidenceFilter.Witness -> WitnessColor
        EvidenceFilter.Prophecy -> ProphecyColor
        EvidenceFilter.Breakthrough -> BreakthroughColor
        EvidenceFilter.Streak -> StreakColor
        EvidenceFilter.Pinned -> Color.Gray
    }
}

private fun getEvidenceTypeColorFromString(type: String): Color {
    return when (type) {
        EvidenceType.RECEIPT -> ReceiptColor
        EvidenceType.WITNESS -> WitnessColor
        EvidenceType.PROPHECY -> ProphecyColor
        EvidenceType.BREAKTHROUGH -> BreakthroughColor
        EvidenceType.STREAK -> StreakColor
        else -> ProdyAccentGreen
    }
}

private fun getRarityColor(rarity: String): Color {
    return when (rarity) {
        EvidenceRarity.COMMON -> CommonColor
        EvidenceRarity.RARE -> RareColor
        EvidenceRarity.EPIC -> EpicColor
        EvidenceRarity.LEGENDARY -> LegendaryColor
        else -> CommonColor
    }
}

private fun getEvidenceEmoji(type: String): String {
    return when (type) {
        EvidenceType.RECEIPT -> "🧾"
        EvidenceType.WITNESS -> "👁️"
        EvidenceType.PROPHECY -> "🔮"
        EvidenceType.BREAKTHROUGH -> "💡"
        EvidenceType.STREAK -> "🔥"
        else -> "📦"
    }
}

private fun getEvidenceTypeName(type: String): String {
    return when (type) {
        EvidenceType.RECEIPT -> "Receipt"
        EvidenceType.WITNESS -> "Witness"
        EvidenceType.PROPHECY -> "Prophecy"
        EvidenceType.BREAKTHROUGH -> "Breakthrough"
        EvidenceType.STREAK -> "Streak"
        else -> "Evidence"
    }
}

private fun formatDate(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val days = TimeUnit.MILLISECONDS.toDays(diff)

    return when {
        days == 0L -> "Today"
        days == 1L -> "Yesterday"
        days < 7 -> "$days days ago"
        else -> SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(timestamp))
    }
}
