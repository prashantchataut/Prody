package com.kairos.app.ui.screens.missions
import com.kairos.app.ui.icons.KairosIcons

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kairos.app.R
import com.kairos.app.data.local.entity.DailyMissionEntity
import com.kairos.app.data.local.entity.WeeklyTrialEntity
import com.kairos.app.ui.components.MissionBoard
import com.kairos.app.ui.theme.*

/**
 * Missions Screen - Full-page view of daily missions and weekly trials.
 *
 * Features:
 * - Today's 3 daily missions with progress tracking
 * - Weekly trial "boss challenge"
 * - Mission completion celebration
 * - Navigation to complete missions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToJournal: () -> Unit,
    onNavigateToVocabulary: () -> Unit,
    onNavigateToFutureMessage: () -> Unit,
    viewModel: MissionsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isRefreshing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mission Board",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = KairosIcons.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                viewModel.refresh()
                isRefreshing = false
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = KairosAccentGreen)
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Daily progress header
                        item(key = "daily_progress") {
                            DailyProgressCard(
                                completed = uiState.completedToday,
                                total = uiState.totalMissions,
                                allComplete = uiState.allMissionsComplete
                            )
                        }

                        // Today's missions
                        item(key = "missions_header") {
                            Text(
                                text = "Today's Missions",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        items(
                            items = uiState.todayMissions,
                            key = { mission -> "mission_${mission.id}" }
                        ) { mission ->
                            MissionDetailCard(
                                mission = mission,
                                onClick = {
                                    when (mission.missionType) {
                                        "reflect" -> onNavigateToJournal()
                                        "sharpen" -> onNavigateToVocabulary()
                                        "commit" -> onNavigateToFutureMessage()
                                    }
                                }
                            )
                        }

                        // Weekly trial section
                        uiState.weeklyTrial?.let { trial ->
                            item(key = "trial_header") {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Weekly Trial",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${viewModel.getDaysRemainingInWeek()} days left",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            item(key = "weekly_trial_${trial.id}") {
                                WeeklyTrialCard(trial = trial)
                            }
                        }

                        // Tips section
                        item(key = "tips_section") {
                            MissionTipsCard()
                        }

                        item(key = "bottom_spacer") {
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
        }
    }

    // Error handling
    uiState.errorMessage?.let { error ->
        LaunchedEffect(error) {
            viewModel.dismissError()
        }
    }
}

@Composable
private fun DailyProgressCard(
    completed: Int,
    total: Int,
    allComplete: Boolean
) {
    val animatedProgress by animateFloatAsState(
        targetValue = if (total > 0) completed.toFloat() / total else 0f,
        animationSpec = tween(500),
        label = "progress_animation"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (allComplete) {
                KairosSuccess.copy(alpha = 0.15f)
            } else {
                KairosAccentGreen.copy(alpha = 0.1f)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (allComplete) "All Complete!" else "Daily Progress",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (allComplete) KairosSuccess else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "$completed of $total missions",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier.size(64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.fillMaxSize(),
                        color = if (allComplete) KairosSuccess else KairosAccentGreen,
                        strokeWidth = 6.dp,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    if (allComplete) {
                        Icon(
                            imageVector = KairosIcons.Check,
                            contentDescription = null,
                            tint = KairosSuccess,
                            modifier = Modifier.size(28.dp)
                        )
                    } else {
                        Text(
                            text = "${(animatedProgress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = KairosAccentGreen
                        )
                    }
                }
            }

            if (allComplete) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "🎉", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "You've conquered today's challenges!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = KairosSuccess
                    )
                }
            }
        }
    }
}

@Composable
private fun MissionDetailCard(
    mission: DailyMissionEntity,
    onClick: () -> Unit
) {
    val progress by animateFloatAsState(
        targetValue = mission.progressPercent() / 100f,
        animationSpec = tween(500),
        label = "mission_progress"
    )

    val missionConfig = getMissionConfig(mission.missionType)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (mission.isCompleted) {
                KairosSuccess.copy(alpha = 0.1f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (mission.isCompleted) {
                                KairosSuccess.copy(alpha = 0.2f)
                            } else {
                                missionConfig.color.copy(alpha = 0.15f)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (mission.isCompleted) KairosIcons.Check else missionConfig.icon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = if (mission.isCompleted) KairosSuccess else missionConfig.color
                    )
                }

                // Content
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = missionConfig.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = missionConfig.color,
                            fontWeight = FontWeight.Bold
                        )
                        if (mission.difficulty == "hard") {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.errorContainer
                            ) {
                                Text(
                                    text = "HARD",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = mission.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Progress/Status
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${mission.currentProgress}/${mission.targetValue}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (mission.isCompleted) KairosSuccess else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "+${mission.rewardXp} XP",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (mission.isCompleted) KairosSuccess else missionConfig.color,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            if (!mission.isCompleted) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    FilledTonalButton(
                        onClick = onClick,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = missionConfig.color.copy(alpha = 0.15f)
                        )
                    ) {
                        Text(
                            text = "Start",
                            color = missionConfig.color
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = KairosIcons.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = missionConfig.color
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WeeklyTrialCard(trial: WeeklyTrialEntity) {
    val progress by animateFloatAsState(
        targetValue = trial.progressPercent() / 100f,
        animationSpec = tween(500),
        label = "trial_progress"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (trial.isCompleted) {
                KairosSuccess.copy(alpha = 0.15f)
            } else {
                MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f)
            }
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = KairosIcons.Star,
                            contentDescription = null,
                            tint = if (trial.isCompleted) KairosSuccess else MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "BOSS CHALLENGE",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (trial.isCompleted) KairosSuccess else MaterialTheme.colorScheme.tertiary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = trial.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = trial.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Reward badge
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(12.dp)
                ) {
                    Text(text = "🏆", fontSize = 24.sp)
                    Text(
                        text = "+${trial.rewardXp}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        text = "XP",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Progress",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${trial.currentProgress}/${trial.targetValue}",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (trial.isCompleted) KairosSuccess else MaterialTheme.colorScheme.tertiary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (trial.isCompleted) KairosSuccess else MaterialTheme.colorScheme.tertiary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            if (trial.isCompleted) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "✨", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Trial Conquered!",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = KairosSuccess
                    )
                }
            }
        }
    }
}

@Composable
private fun MissionTipsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = KairosIcons.Lightbulb,
                    contentDescription = null,
                    tint = KairosAccentGreen
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Mission Tips",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            listOf(
                "Missions auto-complete as you use the app",
                "New missions appear at midnight",
                "Complete all 3 for bonus XP",
                "Weekly trials reward rare items"
            ).forEach { tip ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "• ",
                        color = KairosAccentGreen
                    )
                    Text(
                        text = tip,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private data class MissionConfig(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val label: String
)

@Composable
private fun getMissionConfig(missionType: String): MissionConfig {
    return when (missionType.lowercase()) {
        "reflect" -> MissionConfig(
            icon = KairosIcons.Book,
            color = Color(0xFF7C4DFF), // Purple for Clarity
            label = "REFLECT"
        )
        "sharpen" -> MissionConfig(
            icon = KairosIcons.School,
            color = Color(0xFF00BCD4), // Cyan for Discipline
            label = "SHARPEN"
        )
        "commit" -> MissionConfig(
            icon = KairosIcons.Send,
            color = Color(0xFFFF9800), // Orange for Courage
            label = "COMMIT"
        )
        else -> MissionConfig(
            icon = KairosIcons.Assignment,
            color = KairosAccentGreen,
            label = "MISSION"
        )
    }
}
