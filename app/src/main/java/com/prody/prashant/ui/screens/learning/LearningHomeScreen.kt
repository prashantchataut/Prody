package com.prody.prashant.ui.screens.learning

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.R
import com.prody.prashant.domain.learning.*
import com.prody.prashant.ui.theme.*

/**
 * Learning Home Screen - Main dashboard for learning paths
 *
 * Features:
 * - Active path progress cards
 * - AI-powered recommendations
 * - Path selection
 * - Learning statistics
 * - Badge showcase
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningHomeScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPath: (String) -> Unit,
    onNavigateToLesson: (String, String) -> Unit,
    viewModel: LearningHomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Learning Paths",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        floatingActionButton = {
            if (uiState.activePaths.size < 3) {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.showPathSelection() },
                    containerColor = ProdyAccentGreen,
                    contentColor = Color.Black
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Start Path")
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.isLoading && uiState.activePaths.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ProdyAccentGreen)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Learning Stats Header
                    item(key = "stats") {
                        uiState.learningStats?.let { stats ->
                            LearningStatsCard(stats = stats)
                        }
                    }

                    // Active Paths Section
                    if (uiState.activePaths.isNotEmpty()) {
                        item(key = "active_header") {
                            SectionHeader(
                                title = "Your Active Paths",
                                subtitle = "${uiState.activePaths.size} path${if (uiState.activePaths.size > 1) "s" else ""} in progress"
                            )
                        }

                        items(
                            items = uiState.activePaths,
                            key = { path -> "active_${path.id}" }
                        ) { path ->
                            ActivePathCard(
                                path = path,
                                onClick = { onNavigateToPath(path.id) },
                                onContinue = {
                                    path.progress.currentLessonId?.let { lessonId ->
                                        onNavigateToLesson(path.id, lessonId)
                                    }
                                }
                            )
                        }
                    }

                    // Recommendations Section
                    if (uiState.recommendations.isNotEmpty()) {
                        item(key = "recommendations_header") {
                            SectionHeader(
                                title = "Recommended For You",
                                subtitle = "Based on your journal entries"
                            )
                        }

                        item(key = "recommendations_row") {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(
                                    items = uiState.recommendations,
                                    key = { rec -> "rec_${rec.id}" }
                                ) { recommendation ->
                                    RecommendationCard(
                                        recommendation = recommendation,
                                        onClick = { viewModel.onRecommendationClicked(recommendation) }
                                    )
                                }
                            }
                        }
                    }

                    // Badges Section
                    if (uiState.displayedBadges.isNotEmpty()) {
                        item(key = "badges_header") {
                            SectionHeader(
                                title = "Recent Badges",
                                subtitle = "Your achievements"
                            )
                        }

                        item(key = "badges_row") {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(
                                    items = uiState.displayedBadges,
                                    key = { badge -> "badge_${badge.id}" }
                                ) { badge ->
                                    BadgeCard(badge = badge)
                                }
                            }
                        }
                    }

                    // Completed Paths Section
                    if (uiState.completedPaths.isNotEmpty()) {
                        item(key = "completed_header") {
                            SectionHeader(
                                title = "Completed Paths",
                                subtitle = "${uiState.completedPaths.size} path${if (uiState.completedPaths.size > 1) "s" else ""} completed"
                            )
                        }

                        items(
                            items = uiState.completedPaths,
                            key = { path -> "completed_${path.id}" }
                        ) { path ->
                            CompletedPathCard(
                                path = path,
                                onClick = { onNavigateToPath(path.id) }
                            )
                        }
                    }

                    // Empty State
                    if (uiState.activePaths.isEmpty() && uiState.completedPaths.isEmpty()) {
                        item(key = "empty_state") {
                            EmptyLearningState(
                                onStartPath = { viewModel.showPathSelection() }
                            )
                        }
                    }

                    item(key = "bottom_spacer") {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    // Path Selection Bottom Sheet
    if (uiState.showPathSelectionSheet) {
        PathSelectionSheet(
            pathTypes = uiState.allPathTypes,
            activePaths = uiState.activePaths,
            onPathSelected = { viewModel.onPathSelected(it) },
            onDismiss = { viewModel.hidePathSelection() }
        )
    }

    // Recommendation Dialog
    uiState.showRecommendationDialog?.let { recommendation ->
        RecommendationDialog(
            recommendation = recommendation,
            onAccept = { viewModel.acceptRecommendation(recommendation.id) },
            onDismiss = { viewModel.dismissRecommendation(recommendation.id) }
        )
    }

    // Error Snackbar
    uiState.errorMessage?.let { error ->
        LaunchedEffect(error) {
            viewModel.dismissError()
        }
    }
}

@Composable
private fun LearningStatsCard(
    stats: LearningStats,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = ProdyAccentGreen.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(value = stats.totalPathsCompleted.toString(), label = "Paths")
            StatItem(value = stats.totalLessonsCompleted.toString(), label = "Lessons")
            StatItem(value = "${stats.totalMinutesLearned}m", label = "Learning")
            StatItem(value = stats.totalBadges.toString(), label = "Badges")
        }
    }
}

@Composable
private fun StatItem(value: String, label: String) {
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
private fun SectionHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ActivePathCard(
    path: LearningPath,
    onClick: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pathColor = Color(android.graphics.Color.parseColor(path.type.color))

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(pathColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = path.type.icon, fontSize = 24.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = path.type.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${path.progress.completedLessons}/${path.progress.totalLessons} lessons",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { path.progress.percentage / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = pathColor,
                trackColor = pathColor.copy(alpha = 0.2f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${path.progress.percentage.toInt()}% complete",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(
                    onClick = onContinue,
                    colors = ButtonDefaults.buttonColors(containerColor = pathColor),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        "Continue",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun RecommendationCard(
    recommendation: PathRecommendation,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pathColor = Color(android.graphics.Color.parseColor(recommendation.pathType.color))

    Card(
        modifier = modifier
            .width(200.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = pathColor.copy(alpha = 0.1f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = recommendation.pathType.icon, fontSize = 28.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = pathColor,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = recommendation.pathType.displayName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = recommendation.reason,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${(recommendation.confidence * 100).toInt()}% match",
                style = MaterialTheme.typography.labelSmall,
                color = pathColor
            )
        }
    }
}

@Composable
private fun BadgeCard(
    badge: PathBadge,
    modifier: Modifier = Modifier
) {
    val rarityColor = Color(android.graphics.Color.parseColor(badge.rarity.color))

    Card(
        modifier = modifier.width(120.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = rarityColor.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = badge.badgeIcon, fontSize = 32.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = badge.badgeName,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = badge.rarity.displayName,
                style = MaterialTheme.typography.labelSmall,
                color = rarityColor
            )
        }
    }
}

@Composable
private fun CompletedPathCard(
    path: LearningPath,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pathColor = Color(android.graphics.Color.parseColor(path.type.color))

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(pathColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = path.type.icon, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = path.type.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${path.progress.totalLessons} lessons completed",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Completed",
                tint = ProdySuccess
            )
        }
    }
}

@Composable
private fun EmptyLearningState(
    onStartPath: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "🎓", fontSize = 64.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Start Your Learning Journey",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Choose a path that resonates with you and begin transforming your mindset",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onStartPath,
            colors = ButtonDefaults.buttonColors(containerColor = ProdyAccentGreen)
        ) {
            Text("Choose a Path", color = Color.Black)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PathSelectionSheet(
    pathTypes: List<PathType>,
    activePaths: List<LearningPath>,
    onPathSelected: (PathType) -> Unit,
    onDismiss: () -> Unit
) {
    val activePathTypes = activePaths.map { it.type }.toSet()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Choose a Learning Path",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Select up to 3 paths to work on simultaneously",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = pathTypes,
                    key = { pathType -> "path_type_${pathType.id}" }
                ) { pathType ->
                    val isActive = pathType in activePathTypes
                    PathTypeItem(
                        pathType = pathType,
                        isActive = isActive,
                        onClick = { if (!isActive) onPathSelected(pathType) }
                    )
                }
                item(key = "sheet_bottom_spacer") {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun PathTypeItem(
    pathType: PathType,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pathColor = Color(android.graphics.Color.parseColor(pathType.color))

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = !isActive, onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else
                pathColor.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = pathType.icon, fontSize = 32.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pathType.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isActive) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = pathType.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text(
                        text = "${pathType.estimatedMinutes} min",
                        style = MaterialTheme.typography.labelSmall,
                        color = pathColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = pathType.difficultyLevel.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (isActive) {
                Text(
                    text = "Active",
                    style = MaterialTheme.typography.labelSmall,
                    color = ProdyAccentGreen
                )
            }
        }
    }
}

@Composable
private fun RecommendationDialog(
    recommendation: PathRecommendation,
    onAccept: () -> Unit,
    onDismiss: () -> Unit
) {
    val pathColor = Color(android.graphics.Color.parseColor(recommendation.pathType.color))

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = recommendation.pathType.icon, fontSize = 28.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = recommendation.pathType.displayName)
            }
        },
        text = {
            Column {
                Text(
                    text = recommendation.reason,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "This path was recommended based on:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                recommendation.basedOn.take(3).forEach { reason ->
                    Text(
                        text = "• $reason",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onAccept,
                colors = ButtonDefaults.buttonColors(containerColor = pathColor)
            ) {
                Text("Start Path", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Maybe Later")
            }
        }
    )
}
