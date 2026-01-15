package com.prody.prashant.ui.screens.challenges
import com.prody.prashant.ui.icons.ProdyIcons

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.prody.prashant.util.AccessibilityUtils
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.domain.model.Challenge
import com.prody.prashant.domain.model.ChallengeDifficulty
import com.prody.prashant.domain.model.ChallengeStatus
import com.prody.prashant.domain.model.ChallengeType
import com.prody.prashant.ui.components.ConfettiAnimation
import com.prody.prashant.ui.components.ProdyCard
import com.prody.prashant.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ChallengesScreen(
    onNavigateBack: () -> Unit,
    viewModel: ChallengesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Header
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(600)) + slideInVertically(
                        initialOffsetY = { -it },
                        animationSpec = tween(600, easing = EaseOutCubic)
                    )
                ) {
                    ChallengesHeader(
                        totalJoined = uiState.totalChallengesJoined,
                        totalCompleted = uiState.totalChallengesCompleted,
                        totalPoints = uiState.totalPointsFromChallenges,
                        onBackClick = onNavigateBack
                    )
                }
            }

            // Featured Challenge
            uiState.featuredChallenge?.let { featured ->
                item {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(500, delayMillis = 200)) + slideInVertically(
                            initialOffsetY = { it / 3 },
                            animationSpec = tween(500, delayMillis = 200, easing = EaseOutCubic)
                        )
                    ) {
                        FeaturedChallengeCard(
                            challenge = featured,
                            onJoin = { viewModel.joinChallenge(featured.id) },
                            onClick = { viewModel.selectChallenge(featured) }
                        )
                    }
                }
            }

            // Your Active Challenges
            if (uiState.joinedChallenges.isNotEmpty()) {
                item {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(500, delayMillis = 300))
                    ) {
                        SectionHeader(
                            title = "Your Active Challenges",
                            subtitle = "${uiState.joinedChallenges.size} in progress",
                            icon = ProdyIcons.LocalFireDepartment
                        )
                    }
                }

                item {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(500, delayMillis = 350)) + slideInHorizontally(
                            initialOffsetX = { it / 2 },
                            animationSpec = tween(500, delayMillis = 350)
                        )
                    ) {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = uiState.joinedChallenges,
                                key = { it.id }
                            ) { challenge ->
                                ActiveChallengeCard(
                                    challenge = challenge,
                                    onClick = { viewModel.selectChallenge(challenge) }
                                )
                            }
                        }
                    }
                }
            }

            // Available Challenges
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(500, delayMillis = 400))
                ) {
                    SectionHeader(
                        title = "Available Challenges",
                        subtitle = "Join and compete with the community",
                        icon = ProdyIcons.Outlined.EmojiEvents
                    )
                }
            }

            val availableChallenges = uiState.activeChallenges.filter { !it.isJoined }
            itemsIndexed(
                items = availableChallenges,
                key = { _, challenge -> challenge.id }
            ) { index, challenge ->
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(400, delayMillis = 450 + index * 50)) + slideInVertically(
                        initialOffsetY = { it / 3 },
                        animationSpec = tween(400, delayMillis = 450 + index * 50)
                    )
                ) {
                    ChallengeListItem(
                        challenge = challenge,
                        onJoin = { viewModel.joinChallenge(challenge.id) },
                        onClick = { viewModel.selectChallenge(challenge) }
                    )
                }
            }

            // Completed Challenges
            if (uiState.completedChallenges.isNotEmpty()) {
                item {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(500, delayMillis = 600))
                    ) {
                        SectionHeader(
                            title = "Completed Challenges",
                            subtitle = "${uiState.completedChallenges.size} conquered",
                            icon = ProdyIcons.CheckCircle
                        )
                    }
                }

                items(
                    items = uiState.completedChallenges,
                    key = { it.id }
                ) { challenge ->
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(400, delayMillis = 650))
                    ) {
                        CompletedChallengeCard(challenge = challenge)
                    }
                }
            }

            // Empty state
            if (uiState.activeChallenges.isEmpty() && !uiState.isLoading) {
                item {
                    EmptyChallengesState()
                }
            }
        }

        // Loading indicator
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Celebration overlay
        if (uiState.showCelebration) {
            CelebrationOverlay(
                message = uiState.celebrationMessage,
                onDismiss = { viewModel.dismissCelebration() }
            )
        }

        // Challenge detail bottom sheet
        uiState.selectedChallenge?.let { challenge ->
            ChallengeDetailBottomSheet(
                challenge = challenge,
                leaderboard = uiState.challengeLeaderboard,
                onDismiss = { viewModel.clearSelectedChallenge() },
                onJoin = { viewModel.joinChallenge(challenge.id) }
            )
        }
    }
}

@Composable
private fun ChallengesHeader(
    totalJoined: Int,
    totalCompleted: Int,
    totalPoints: Int,
    onBackClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "header")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MoodExcited,
                        MoodExcited.copy(alpha = 0.9f),
                        MoodExcited.copy(alpha = 0.75f)
                    )
                )
            )
    ) {
        // Animated background
        ChallengesBackgroundAnimation()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            // Top bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = ProdyIcons.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = "Community Challenges",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.width(48.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                HeaderStatItem(
                    value = totalJoined.toString(),
                    label = "Joined",
                    icon = ProdyIcons.Outlined.Groups,
                    glowAlpha = glowAlpha
                )
                HeaderStatItem(
                    value = totalCompleted.toString(),
                    label = "Completed",
                    icon = ProdyIcons.EmojiEvents,
                    glowAlpha = glowAlpha
                )
                HeaderStatItem(
                    value = formatNumber(totalPoints),
                    label = "Points",
                    icon = ProdyIcons.Stars,
                    glowAlpha = glowAlpha
                )
            }
        }
    }
}

@Composable
private fun ChallengesBackgroundAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "bg")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(25000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Canvas(modifier = Modifier.fillMaxSize().alpha(0.25f)) {
        val center = Offset(size.width * 0.8f, size.height * 0.4f)

        for (i in 0 until 5) {
            val angle = (rotation + i * 72f) * PI / 180
            val radius = minOf(size.width, size.height) * 0.3f
            val x = center.x + radius * cos(angle).toFloat()
            val y = center.y + radius * sin(angle).toFloat()
            drawCircle(
                color = Color.White.copy(alpha = 0.15f - i * 0.02f),
                radius = 20f - i * 3f,
                center = Offset(x, y)
            )
        }

        drawCircle(
            color = Color.White.copy(alpha = 0.08f),
            radius = size.height * 0.5f,
            center = Offset(size.width * 0.2f, size.height * 0.8f)
        )
    }
}

@Composable
private fun HeaderStatItem(
    value: String,
    label: String,
    icon: ImageVector,
    glowAlpha: Float
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .blur(12.dp)
                    .alpha(glowAlpha)
                    .background(Color.White, CircleShape)
            )
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun FeaturedChallengeCard(
    challenge: Challenge,
    onJoin: () -> Unit,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "featured")
    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        ProdyCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .semantics {
                    contentDescription = "Featured challenge: ${challenge.title}. ${challenge.description}. ${challenge.daysRemaining} days remaining, ${challenge.rewardPoints} points reward"
                },
            backgroundColor = challenge.type.color.copy(alpha = 0.15f)
        ) {
            Box {
                // Glow effect
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(100.dp)
                        .offset(x = 30.dp, y = (-30).dp)
                        .blur(40.dp)
                        .alpha(shimmer * 0.5f)
                        .background(challenge.type.color, CircleShape)
                )

                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Featured badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(GoldTier.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = ProdyIcons.Star,
                                        contentDescription = null,
                                        tint = GoldTier,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "FEATURED",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldTier
                                    )
                                }
                            }

                            DifficultyBadge(difficulty = challenge.difficulty)
                        }

                        // Type icon
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(challenge.type.color.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = challenge.type.icon,
                                contentDescription = null,
                                tint = challenge.type.color,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = challenge.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = challenge.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Community progress
                    CommunityProgressSection(
                        currentProgress = challenge.communityProgress,
                        targetProgress = challenge.communityTarget,
                        participants = challenge.totalParticipants,
                        color = challenge.type.color
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Bottom row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Time remaining
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = ProdyIcons.Outlined.Schedule,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${challenge.daysRemaining} days left",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Reward
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = ProdyIcons.Stars,
                                contentDescription = null,
                                tint = GoldTier,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "+${challenge.rewardPoints}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = GoldTier
                            )
                        }

                        // Join button
                        if (!challenge.isJoined) {
                            Button(
                                onClick = onJoin,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = challenge.type.color
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = ProdyIcons.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Join Challenge")
                            }
                        } else {
                            // Progress indicator
                            UserProgressBadge(
                                progress = challenge.currentUserProgress,
                                target = challenge.targetCount,
                                color = challenge.type.color
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CommunityProgressSection(
    currentProgress: Int,
    targetProgress: Int,
    participants: Int,
    color: Color
) {
    val progress = (currentProgress.toFloat() / targetProgress).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1500, easing = FastOutSlowInEasing),
        label = "community_progress"
    )

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = ProdyIcons.Outlined.Groups,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "${formatNumber(participants)} participants",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "${formatNumber(currentProgress)} / ${formatNumber(targetProgress)}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = color
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                color,
                                color.copy(alpha = 0.8f)
                            )
                        )
                    )
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Community Progress: ${(animatedProgress * 100).toInt()}%",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ActiveChallengeCard(
    challenge: Challenge,
    onClick: () -> Unit
) {
    val progress = challenge.progressPercentage
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "progress"
    )

    ProdyCard(
        modifier = Modifier
            .width(280.dp)
            .clickable(onClick = onClick),
        backgroundColor = challenge.type.color.copy(alpha = 0.1f)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(challenge.type.color.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = challenge.type.icon,
                        contentDescription = null,
                        tint = challenge.type.color,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Circular progress
                Box(
                    modifier = Modifier.size(50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.fillMaxSize(),
                        color = challenge.type.color,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        strokeWidth = 4.dp,
                        strokeCap = StrokeCap.Round
                    )
                    Text(
                        text = "${(animatedProgress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = challenge.type.color
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = challenge.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${challenge.currentUserProgress}/${challenge.targetCount} completed",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${challenge.daysRemaining} days left",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (challenge.daysRemaining <= 3) MoodAnxious else MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        imageVector = ProdyIcons.Stars,
                        contentDescription = null,
                        tint = GoldTier,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "+${challenge.rewardPoints}",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldTier,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun ChallengeListItem(
    challenge: Challenge,
    onJoin: () -> Unit,
    onClick: () -> Unit
) {
    ProdyCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(challenge.type.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = challenge.type.icon,
                    contentDescription = null,
                    tint = challenge.type.color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Content
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = challenge.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    DifficultyBadge(difficulty = challenge.difficulty, small = true)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = challenge.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = ProdyIcons.Outlined.Groups,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = formatNumber(challenge.totalParticipants),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = ProdyIcons.Outlined.Schedule,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${challenge.daysRemaining}d",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = ProdyIcons.Stars,
                            contentDescription = null,
                            tint = GoldTier,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "+${challenge.rewardPoints}",
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldTier,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Join button
            if (!challenge.isJoined) {
                FilledTonalButton(
                    onClick = onJoin,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Join",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            } else {
                Icon(
                    imageVector = ProdyIcons.CheckCircle,
                    contentDescription = "Joined",
                    tint = AchievementUnlocked,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun CompletedChallengeCard(challenge: Challenge) {
    ProdyCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        backgroundColor = AchievementUnlocked.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Trophy icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(AchievementUnlocked.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = ProdyIcons.EmojiEvents,
                    contentDescription = null,
                    tint = AchievementUnlocked,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = challenge.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )

                challenge.completedAt?.let { completedAt ->
                    Text(
                        text = "Completed ${formatDate(completedAt)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Reward earned
            Surface(
                color = GoldTier.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = ProdyIcons.Stars,
                        contentDescription = null,
                        tint = GoldTier,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "+${challenge.rewardPoints}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = GoldTier
                    )
                }
            }
        }
    }
}

@Composable
private fun DifficultyBadge(
    difficulty: ChallengeDifficulty,
    small: Boolean = false
) {
    Surface(
        color = difficulty.color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(if (small) 4.dp else 6.dp)
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = if (small) 6.dp else 8.dp,
                vertical = if (small) 2.dp else 4.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Icon(
                imageVector = difficulty.icon,
                contentDescription = null,
                tint = difficulty.color,
                modifier = Modifier.size(if (small) 10.dp else 12.dp)
            )
            Text(
                text = difficulty.displayName,
                style = if (small) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = difficulty.color,
                fontSize = if (small) 9.sp else 10.sp
            )
        }
    }
}

@Composable
private fun UserProgressBadge(
    progress: Int,
    target: Int,
    color: Color
) {
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = ProdyIcons.TrendingUp,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "$progress/$target",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EmptyChallengesState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "empty")
        val scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = EaseInOutCubic),
                repeatMode = RepeatMode.Reverse
            ),
            label = "scale"
        )

        Icon(
            imageVector = ProdyIcons.Outlined.EmojiEvents,
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .scale(scale),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No Challenges Yet",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "New challenges are coming soon!\nCheck back later for exciting community events.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChallengeDetailBottomSheet(
    challenge: Challenge,
    leaderboard: List<ChallengeLeaderboardEntry>,
    onDismiss: () -> Unit,
    onJoin: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            contentPadding = PaddingValues(horizontal = 20.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(challenge.type.color.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = challenge.type.icon,
                                contentDescription = null,
                                tint = challenge.type.color,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Column {
                            Text(
                                text = challenge.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                DifficultyBadge(difficulty = challenge.difficulty)
                                Text(
                                    text = challenge.type.displayName,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = challenge.type.color
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Description
            item {
                Text(
                    text = challenge.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Stats grid
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DetailStatCard(
                        label = "Target",
                        value = challenge.targetCount.toString(),
                        icon = ProdyIcons.Outlined.Flag,
                        color = challenge.type.color,
                        modifier = Modifier.weight(1f)
                    )
                    DetailStatCard(
                        label = "Days Left",
                        value = challenge.daysRemaining.toString(),
                        icon = ProdyIcons.Outlined.Schedule,
                        color = if (challenge.daysRemaining <= 3) MoodAnxious else MoodMotivated,
                        modifier = Modifier.weight(1f)
                    )
                    DetailStatCard(
                        label = "Reward",
                        value = "+${challenge.rewardPoints}",
                        icon = ProdyIcons.Stars,
                        color = GoldTier,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Community progress
            item {
                Text(
                    text = "Community Progress",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(12.dp))

                CommunityProgressSection(
                    currentProgress = challenge.communityProgress,
                    targetProgress = challenge.communityTarget,
                    participants = challenge.totalParticipants,
                    color = challenge.type.color
                )

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Leaderboard
            if (leaderboard.isNotEmpty()) {
                item {
                    Text(
                        text = "Leaderboard",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                itemsIndexed(
                    items = leaderboard.take(10),
                    key = { _, entry -> entry.odId }
                ) { index, entry ->
                    LeaderboardItemRow(entry = entry, rank = index + 1)
                    if (index < leaderboard.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            // Action button
            item {
                if (!challenge.isJoined) {
                    Button(
                        onClick = {
                            onJoin()
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = challenge.type.color
                        ),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        Icon(
                            imageVector = ProdyIcons.Add,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Join This Challenge",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else if (!challenge.isCompleted) {
                    // Your progress
                    Surface(
                        color = challenge.type.color.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Your Progress",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${challenge.currentUserProgress}/${challenge.targetCount}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = challenge.type.color
                                )
                            }

                            CircularProgressIndicator(
                                progress = { challenge.progressPercentage },
                                modifier = Modifier.size(60.dp),
                                color = challenge.type.color,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                strokeWidth = 6.dp,
                                strokeCap = StrokeCap.Round
                            )
                        }
                    }
                } else {
                    // Completed badge
                    Surface(
                        color = AchievementUnlocked.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = ProdyIcons.EmojiEvents,
                                contentDescription = null,
                                tint = AchievementUnlocked,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Challenge Completed!",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AchievementUnlocked
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailStatCard(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun LeaderboardItemRow(
    entry: ChallengeLeaderboardEntry,
    rank: Int
) {
    val rankColor = when (rank) {
        1 -> GoldTier
        2 -> SilverTier
        3 -> BronzeTier
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rank
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    if (rank <= 3) rankColor.copy(alpha = 0.15f)
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            if (rank <= 3) {
                Icon(
                    imageVector = when (rank) {
                        1 -> ProdyIcons.EmojiEvents
                        2 -> ProdyIcons.WorkspacePremium
                        else -> ProdyIcons.MilitaryTech
                    },
                    contentDescription = null,
                    tint = rankColor,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Text(
                    text = rank.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = rankColor
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Name
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = entry.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (entry.isCurrentUser) FontWeight.Bold else FontWeight.Normal
                )
                if (entry.isCurrentUser) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "You",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Progress
        Text(
            text = entry.progress.toString(),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = if (entry.isCurrentUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun CelebrationOverlay(
    message: String,
    onDismiss: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
        delay(3000)
        onDismiss()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        // Confetti
        ConfettiAnimation(
            modifier = Modifier.fillMaxSize(),
            isPlaying = visible
        )

        // Message card
        AnimatedVisibility(
            visible = visible,
            enter = scaleIn(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(24.dp),
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val infiniteTransition = rememberInfiniteTransition(label = "celebration")
                    val scale by infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 1.2f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(500, easing = EaseInOutCubic),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "icon_scale"
                    )

                    Icon(
                        imageVector = ProdyIcons.Celebration,
                        contentDescription = null,
                        tint = GoldTier,
                        modifier = Modifier
                            .size(64.dp)
                            .scale(scale)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = message,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

// Helper functions
private fun formatNumber(number: Int): String {
    return when {
        number >= 1000000 -> String.format("%.1fM", number / 1000000.0)
        number >= 1000 -> String.format("%.1fK", number / 1000.0)
        else -> number.toString()
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM d", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
