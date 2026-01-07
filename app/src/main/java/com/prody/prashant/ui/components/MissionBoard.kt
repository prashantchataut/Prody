package com.prody.prashant.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.prody.prashant.data.local.entity.DailyMissionEntity
import com.prody.prashant.data.local.entity.WeeklyTrialEntity
import com.prody.prashant.domain.gamification.MissionType

/**
 * Mission Board - Displays the 3 daily missions in a compact, game-like format.
 *
 * Design principles:
 * - Compact: Fits in a card on the home/profile screen
 * - Clear progress: Shows exactly what's needed vs done
 * - No spam: Progress updates are visual, not toast-based
 * - Actionable: Tapping a mission could navigate to that mode
 */
@Composable
fun MissionBoard(
    missions: List<DailyMissionEntity>,
    weeklyTrial: WeeklyTrialEntity? = null,
    onMissionClick: ((DailyMissionEntity) -> Unit)? = null,
    onTrialClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daily Missions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                // Completion indicator
                val completedCount = missions.count { it.isCompleted }
                Text(
                    text = "$completedCount/${missions.size}",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (completedCount == missions.size) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            // Mission items
            missions.forEach { mission ->
                MissionItem(
                    mission = mission,
                    onClick = { onMissionClick?.invoke(mission) }
                )
            }

            // Weekly trial (if present)
            if (weeklyTrial != null) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                WeeklyTrialItem(
                    trial = weeklyTrial,
                    onClick = onTrialClick
                )
            }
        }
    }
}

@Composable
private fun MissionItem(
    mission: DailyMissionEntity,
    onClick: (() -> Unit)?
) {
    val progress by animateFloatAsState(
        targetValue = mission.progressPercent() / 100f,
        animationSpec = tween(500),
        label = "mission_progress"
    )

    val missionConfig = getMissionConfig(mission.missionType)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Icon with completion state
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (mission.isCompleted) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        missionConfig.color.copy(alpha = 0.1f)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (mission.isCompleted) Icons.Filled.Check else missionConfig.icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = if (mission.isCompleted) {
                    MaterialTheme.colorScheme.primary
                } else {
                    missionConfig.color
                }
            )
        }

        // Mission details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = mission.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = if (mission.isCompleted) {
                    MaterialTheme.colorScheme.primary
                } else {
                    missionConfig.color
                },
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }

        // Progress text
        Text(
            text = "${mission.currentProgress}/${mission.targetValue}",
            style = MaterialTheme.typography.labelMedium,
            color = if (mission.isCompleted) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

@Composable
private fun WeeklyTrialItem(
    trial: WeeklyTrialEntity,
    onClick: (() -> Unit)?
) {
    val progress by animateFloatAsState(
        targetValue = trial.progressPercent() / 100f,
        animationSpec = tween(500),
        label = "trial_progress"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
            )
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Trial icon (boss-like)
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (trial.isCompleted) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (trial.isCompleted) Icons.Filled.Check else Icons.Filled.Star,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = if (trial.isCompleted) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.tertiary
                }
            )
        }

        // Trial details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Weekly Trial",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            Text(
                text = trial.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = if (trial.isCompleted) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.tertiary
                },
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }

        // Progress text
        Text(
            text = "${trial.currentProgress}/${trial.targetValue}",
            style = MaterialTheme.typography.labelMedium,
            color = if (trial.isCompleted) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

/**
 * Compact version of mission board for smaller spaces (e.g., home screen widget).
 */
@Composable
fun CompactMissionBoard(
    missions: List<DailyMissionEntity>,
    onExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
    val completedCount = missions.count { it.isCompleted }
    val totalCount = missions.size

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onExpand),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Assignment,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Daily Missions",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mini progress dots
                missions.forEach { mission ->
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (mission.isCompleted) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.outlineVariant
                                }
                            )
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "$completedCount/$totalCount",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = "View missions",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private data class MissionConfig(
    val icon: ImageVector,
    val color: Color
)

@Composable
private fun getMissionConfig(missionType: String): MissionConfig {
    return when (missionType.lowercase()) {
        "reflect" -> MissionConfig(
            icon = Icons.Filled.Book,
            color = Color(0xFF7C4DFF) // Purple for Clarity
        )
        "sharpen" -> MissionConfig(
            icon = Icons.Filled.School,
            color = Color(0xFF00BCD4) // Cyan for Discipline
        )
        "commit" -> MissionConfig(
            icon = Icons.Filled.Send,
            color = Color(0xFFFF9800) // Orange for Courage
        )
        else -> MissionConfig(
            icon = Icons.Filled.Assignment,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
