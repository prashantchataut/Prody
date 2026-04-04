package com.prody.prashant.ui.screens.social
import com.prody.prashant.ui.icons.ProdyIcons

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.prody.prashant.domain.social.*
import com.prody.prashant.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Social Accountability Circles - UI Components
 *
 * Reusable Compose components for the social feature.
 */

// =========================================================================
// CIRCLE CARD
// =========================================================================

@Composable
fun CircleCard(
    circle: Circle,
    recentActivity: List<CircleUpdate> = emptyList(),
    activeChallenges: List<CircleChallenge> = emptyList(),
    unreadNotifications: Int = 0,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = circle.colorTheme.backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = circle.iconEmoji,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = circle.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = circle.colorTheme.textColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${circle.memberCount} member${if (circle.memberCount != 1) "s" else ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = circle.colorTheme.textColor.copy(alpha = 0.7f)
                        )
                    }
                }

                if (unreadNotifications > 0) {
                    Badge(
                        containerColor = ProdyAccentGreen,
                        contentColor = Color.Black
                    ) {
                        Text(text = unreadNotifications.toString())
                    }
                }
            }

            if (circle.description != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = circle.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = circle.colorTheme.textColor.copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Recent activity preview
            if (recentActivity.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = circle.colorTheme.textColor.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Recent Activity",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = circle.colorTheme.textColor.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                recentActivity.take(2).forEach { update ->
                    Text(
                        text = "${update.type.emoji} ${update.content}",
                        style = MaterialTheme.typography.bodySmall,
                        color = circle.colorTheme.textColor.copy(alpha = 0.9f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Active challenges
            if (activeChallenges.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = ProdyIcons.Star,
                        contentDescription = null,
                        tint = ProdyAccentGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${activeChallenges.size} active challenge${if (activeChallenges.size != 1) "s" else ""}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = circle.colorTheme.textColor
                    )
                }
            }
        }
    }
}

// =========================================================================
// MEMBER AVATAR
// =========================================================================

@Composable
fun MemberAvatar(
    member: CircleMember,
    showStreak: Boolean = true,
    size: Int = 48,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.then(
            if (onClick != null) {
                Modifier.clickable(onClick = onClick)
            } else Modifier
        ),
        contentAlignment = Alignment.Center
    ) {
        // Avatar circle
        Box(
            modifier = Modifier
                .size(size.dp)
                .clip(CircleShape)
                .background(member.role.getColor())
                .border(2.dp, if (member.isOnFire) StreakColor else Color.Transparent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = member.displayName.firstOrNull()?.uppercase() ?: "?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Streak badge
        if (showStreak && member.currentStreak > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 4.dp, y = 4.dp)
                    .size((size * 0.4).dp)
                    .clip(CircleShape)
                    .background(StreakColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🔥",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(2.dp)
                )
            }
        }
    }
}

private fun MemberRole.getColor(): Color {
    return when (this) {
        MemberRole.OWNER -> LeaderboardGold
        MemberRole.ADMIN -> LeaderboardSilver
        MemberRole.MEMBER -> ProdyAccentGreen
    }
}

// =========================================================================
// ACTIVITY FEED ITEM
// =========================================================================

@Composable
fun ActivityFeedItem(
    update: CircleUpdate,
    currentUserId: String,
    onReact: (String) -> Unit,
    onMemberClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Header with member info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clickable(onClick = onMemberClick)
                ) {
                    MemberAvatar(
                        member = update.member,
                        showStreak = true,
                        size = 36
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = update.member.displayName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = formatTimestamp(update.timestamp),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }

                Text(
                    text = update.type.emoji,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Content
            Text(
                text = update.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Reactions
            if (update.hasReactions) {
                Spacer(modifier = Modifier.height(8.dp))
                ReactionBar(
                    reactions = update.reactions,
                    currentUserId = currentUserId,
                    onReact = onReact
                )
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("👍", "🎉", "💪", "🔥", "❤️").forEach { emoji ->
                        Text(
                            text = emoji,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .clickable { onReact(emoji) }
                                .padding(4.dp)
                        )
                    }
                }
            }
        }
    }
}

// =========================================================================
// REACTION BAR
// =========================================================================

@Composable
fun ReactionBar(
    reactions: Map<String, List<String>>,
    currentUserId: String,
    onReact: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        reactions.forEach { (emoji, userIds) ->
            val hasUserReacted = userIds.contains(currentUserId)

            Surface(
                modifier = Modifier.clickable { onReact(emoji) },
                shape = RoundedCornerShape(16.dp),
                color = if (hasUserReacted) ProdyAccentGreen.copy(alpha = 0.2f)
                else MaterialTheme.colorScheme.surfaceVariant,
                border = if (hasUserReacted) androidx.compose.foundation.BorderStroke(1.dp, ProdyAccentGreen)
                else null
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = emoji,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = userIds.size.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = if (hasUserReacted) FontWeight.Bold else FontWeight.Normal,
                        color = if (hasUserReacted) ProdyAccentGreen
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// =========================================================================
// NUDGE BUTTON
// =========================================================================

@Composable
fun NudgeButton(
    nudgeType: NudgeType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = nudgeType.color.copy(alpha = 0.2f),
            contentColor = nudgeType.color
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text = nudgeType.emoji, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = nudgeType.name.replace("_", " ").lowercase()
                .replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

// =========================================================================
// CHALLENGE PROGRESS CARD
// =========================================================================

@Composable
fun ChallengeProgressCard(
    challenge: CircleChallenge,
    userProgress: Int,
    userProgressPercent: Float,
    isParticipating: Boolean,
    hasCompleted: Boolean,
    onJoin: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (hasCompleted) ProdySuccess.copy(alpha = 0.1f)
            else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${challenge.targetType.emoji} ${challenge.title}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = challenge.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isParticipating) {
                // Progress bar
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Your Progress",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$userProgress / ${challenge.targetValue} ${challenge.targetType.unit}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (hasCompleted) ProdySuccess else MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { userProgressPercent },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (hasCompleted) ProdySuccess else ProdyAccentGreen,
                    )
                }

                if (hasCompleted) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = ProdyIcons.CheckCircle,
                            contentDescription = null,
                            tint = ProdySuccess,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Completed!",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = ProdySuccess
                        )
                    }
                }
            } else {
                Button(
                    onClick = onJoin,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Join Challenge")
                }
            }

            // Time remaining
            if (!challenge.isEnded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "⏱️ ${challenge.daysRemaining} day${if (challenge.daysRemaining != 1) "s" else ""} remaining",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Participants
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "👥 ${challenge.participantCount} participant${if (challenge.participantCount != 1) "s" else ""}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// =========================================================================
// INVITE CODE DISPLAY
// =========================================================================

@Composable
fun InviteCodeDisplay(
    inviteCode: String,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = ProdyAccentGreen.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Invite Code",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = ProdyAccentGreen.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(2.dp, ProdyAccentGreen)
            ) {
                Text(
                    text = inviteCode,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = ProdyAccentGreen,
                    letterSpacing = androidx.compose.ui.unit.TextUnit(8f, androidx.compose.ui.unit.TextUnitType.Sp),
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCopy,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = ProdyIcons.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Copy")
                }
                Button(
                    onClick = onShare,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = ProdyIcons.Share,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Share")
                }
            }
        }
    }
}

// =========================================================================
// HELPER FUNCTIONS
// =========================================================================

private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60_000 -> "Just now"
        diff < 3600_000 -> "${diff / 60_000}m ago"
        diff < 86400_000 -> "${diff / 3600_000}h ago"
        diff < 604800_000 -> "${diff / 86400_000}d ago"
        else -> {
            val sdf = SimpleDateFormat("MMM d", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }
}
