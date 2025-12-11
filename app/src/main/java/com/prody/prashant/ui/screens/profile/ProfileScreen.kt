package com.prody.prashant.ui.screens.profile

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.R
import com.prody.prashant.data.local.entity.AchievementEntity
import com.prody.prashant.domain.model.AchievementRarity
import com.prody.prashant.domain.model.Achievements
import com.prody.prashant.ui.components.*
import com.prody.prashant.ui.theme.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ProfileScreen(
    onNavigateToSettings: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Animated Profile header
        item {
            AnimatedProfileHeader(
                displayName = uiState.displayName,
                title = uiState.title,
                totalPoints = uiState.totalPoints,
                currentStreak = uiState.currentStreak,
                longestStreak = uiState.longestStreak,
                onSettingsClick = onNavigateToSettings
            )
        }

        // Animated Stats summary
        item {
            AnimatedProfileStats(
                wordsLearned = uiState.wordsLearned,
                journalEntries = uiState.journalEntries,
                achievementsUnlocked = uiState.achievementsUnlocked,
                daysOnPrody = uiState.daysOnPrody
            )
        }

        // Level progress section
        item {
            LevelProgressSection(
                totalPoints = uiState.totalPoints,
                title = uiState.title
            )
        }

        // Achievements section header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = null,
                        tint = GoldTier,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = stringResource(R.string.achievements),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = "${uiState.unlockedAchievements.size}/${uiState.unlockedAchievements.size + uiState.lockedAchievements.size}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Unlocked achievements
        if (uiState.unlockedAchievements.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = AchievementUnlocked,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Unlocked (${uiState.unlockedAchievements.size})",
                        style = MaterialTheme.typography.labelLarge,
                        color = AchievementUnlocked,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(uiState.unlockedAchievements) { index, achievement ->
                        AnimatedAchievementCard(
                            achievement = achievement,
                            isUnlocked = true,
                            animationDelay = index * 80
                        )
                    }
                }
            }
        }

        // Locked achievements (in progress)
        if (uiState.lockedAchievements.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = null,
                        tint = AchievementLocked,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "In Progress (${uiState.lockedAchievements.size})",
                        style = MaterialTheme.typography.labelLarge,
                        color = AchievementLocked,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(uiState.lockedAchievements.take(5)) { index, achievement ->
                        AnimatedAchievementCard(
                            achievement = achievement,
                            isUnlocked = false,
                            animationDelay = index * 80
                        )
                    }
                }
            }
        }

        // Growth journey card with animation
        item {
            AnimatedGrowthJourneyCard()
        }

        // Inspirational quote footer
        item {
            InspirationalQuoteFooter()
        }
    }
}

@Composable
private fun AnimatedProfileHeader(
    displayName: String,
    title: String,
    totalPoints: Int,
    currentStreak: Int,
    longestStreak: Int,
    onSettingsClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "header_animation")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(30000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring_rotation"
    )

    val avatarPulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "avatar_pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                        ProdyPrimaryVariant
                    )
                )
            )
    ) {
        // Animated background elements
        FloatingParticlesBackground(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp),
            particleCount = 25,
            particleColor = Color.White.copy(alpha = 0.08f)
        )

        // Geometric pattern overlay
        GeometricPatternBackground(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp),
            patternColor = Color.White.copy(alpha = 0.03f)
        )

        // Settings button
        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(8.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.15f)
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = stringResource(R.string.settings),
                    tint = Color.White,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 20.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Animated avatar with decorative rings
            Box(
                modifier = Modifier.size(130.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer rotating ring
                Canvas(
                    modifier = Modifier
                        .size(130.dp)
                        .rotate(rotation)
                ) {
                    val strokeWidth = 2.dp.toPx()
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                GoldTier.copy(alpha = 0.8f),
                                GoldTier.copy(alpha = 0.2f),
                                GoldTier.copy(alpha = 0.8f)
                            )
                        ),
                        startAngle = 0f,
                        sweepAngle = 270f,
                        useCenter = false,
                        style = Stroke(strokeWidth)
                    )
                }

                // Inner glowing ring
                Canvas(
                    modifier = Modifier
                        .size(115.dp)
                        .rotate(-rotation * 0.5f)
                ) {
                    val strokeWidth = 1.5.dp.toPx()
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.5f),
                                Color.White.copy(alpha = 0.1f),
                                Color.White.copy(alpha = 0.5f)
                            )
                        ),
                        startAngle = 0f,
                        sweepAngle = 180f,
                        useCenter = false,
                        style = Stroke(strokeWidth)
                    )
                }

                // Avatar container
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .scale(avatarPulse)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.3f),
                                    Color.White.copy(alpha = 0.15f)
                                )
                            )
                        )
                        .border(3.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        modifier = Modifier.size(50.dp),
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Name with animation
            Text(
                text = displayName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // Animated title badge
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = GoldTier.copy(alpha = 0.2f),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.WorkspacePremium,
                        contentDescription = null,
                        tint = GoldTier,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium,
                        color = GoldTier,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Stats row with animated values
            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                AnimatedHeaderStat(
                    value = totalPoints,
                    label = "Points",
                    icon = Icons.Filled.Stars,
                    iconColor = GoldTier
                )

                // Streak badge in center
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    StreakBadge(streakDays = currentStreak)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Streak",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                AnimatedHeaderStat(
                    value = longestStreak,
                    label = "Best",
                    icon = Icons.Filled.EmojiEvents,
                    iconColor = GoldTier
                )
            }
        }
    }
}

@Composable
private fun AnimatedHeaderStat(
    value: Int,
    label: String,
    icon: ImageVector,
    iconColor: Color
) {
    val animatedValue = AnimatedCounter(targetValue = value)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = animatedValue.toString(),
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
private fun AnimatedProfileStats(
    wordsLearned: Int,
    journalEntries: Int,
    achievementsUnlocked: Int,
    daysOnPrody: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        AnimatedProfileStatItem(
            value = wordsLearned,
            label = "Words",
            icon = Icons.Filled.School,
            color = MoodMotivated,
            animationDelay = 0
        )
        AnimatedProfileStatItem(
            value = journalEntries,
            label = "Entries",
            icon = Icons.Filled.Book,
            color = MoodCalm,
            animationDelay = 100
        )
        AnimatedProfileStatItem(
            value = achievementsUnlocked,
            label = "Badges",
            icon = Icons.Filled.MilitaryTech,
            color = GoldTier,
            animationDelay = 200
        )
        AnimatedProfileStatItem(
            value = daysOnPrody,
            label = "Days",
            icon = Icons.Filled.CalendarMonth,
            color = MoodGrateful,
            animationDelay = 300
        )
    }
}

@Composable
private fun AnimatedProfileStatItem(
    value: Int,
    label: String,
    icon: ImageVector,
    color: Color,
    animationDelay: Int
) {
    var isVisible by remember { mutableStateOf(false) }
    val animatedValue = AnimatedCounter(targetValue = if (isVisible) value else 0)

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(animationDelay.toLong())
        isVisible = true
    }

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "stat_scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.scale(scale)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = animatedValue.toString(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun LevelProgressSection(
    totalPoints: Int,
    title: String
) {
    val levels = listOf(
        "Newcomer" to 0,
        "Explorer" to 100,
        "Learner" to 500,
        "Scholar" to 1000,
        "Sage" to 2500,
        "Master" to 5000,
        "Enlightened" to 10000,
        "Legend" to 25000
    )

    val currentLevelIndex = levels.indexOfLast { totalPoints >= it.second }.coerceAtLeast(0)
    val currentLevel = levels[currentLevelIndex]
    val nextLevel = levels.getOrNull(currentLevelIndex + 1)

    val progress = if (nextLevel != null) {
        val pointsInLevel = totalPoints - currentLevel.second
        val levelRange = nextLevel.second - currentLevel.second
        (pointsInLevel.toFloat() / levelRange).coerceIn(0f, 1f)
    } else {
        1f
    }

    ProdyCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Your Level",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                if (nextLevel != null) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Next Level",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = nextLevel.first,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = GoldTier
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Animated progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                val animatedProgress by animateFloatAsState(
                    targetValue = progress,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "level_progress"
                )

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    GoldTier
                                )
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${totalPoints} pts",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                if (nextLevel != null) {
                    Text(
                        text = "${nextLevel.second - totalPoints} pts to go",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimatedAchievementCard(
    achievement: AchievementEntity,
    isUnlocked: Boolean,
    animationDelay: Int = 0
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(animationDelay.toLong())
        isVisible = true
    }

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "achievement_scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(300),
        label = "achievement_alpha"
    )

    val rarity = try {
        AchievementRarity.valueOf(achievement.rarity.uppercase())
    } catch (e: Exception) {
        AchievementRarity.COMMON
    }

    val achievementData = Achievements.getAchievementById(achievement.id)

    val infiniteTransition = rememberInfiniteTransition(label = "achievement_animation")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    ProdyCard(
        modifier = Modifier
            .width(150.dp)
            .scale(scale)
            .alpha(alpha)
            .clickable { },
        backgroundColor = if (isUnlocked)
            rarity.color.copy(alpha = 0.12f)
        else
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon with glow effect for unlocked
            Box(
                modifier = Modifier.size(60.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isUnlocked) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .blur(12.dp)
                            .alpha(glowAlpha)
                            .clip(CircleShape)
                            .background(rarity.color)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(
                            if (isUnlocked) rarity.color.copy(alpha = 0.2f)
                            else AchievementLocked.copy(alpha = 0.15f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = achievementData?.icon ?: Icons.Filled.EmojiEvents,
                        contentDescription = null,
                        tint = if (isUnlocked) rarity.color else AchievementLocked,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = achievement.name,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (isUnlocked) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 2
            )

            if (!isUnlocked && achievement.requirement > 0) {
                Spacer(modifier = Modifier.height(8.dp))

                val progress = (achievement.currentProgress.toFloat() / achievement.requirement).coerceIn(0f, 1f)
                val animatedProgress by animateFloatAsState(
                    targetValue = if (isVisible) progress else 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "progress"
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedProgress)
                            .clip(RoundedCornerShape(3.dp))
                            .background(rarity.color)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${achievement.currentProgress}/${achievement.requirement}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Rarity badge
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = rarity.color.copy(alpha = 0.15f)
            ) {
                Text(
                    text = rarity.displayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = rarity.color,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun AnimatedGrowthJourneyCard() {
    var isExpanded by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "growth_animation")
    val iconRotation by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "icon_rotation"
    )

    ProdyCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { isExpanded = !isExpanded },
        backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .rotate(iconRotation)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Timeline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Your Growth Journey",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Every step forward matters",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Divider(
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    val milestones = listOf(
                        "Learn 100 words" to Icons.Filled.School,
                        "Write 50 journal entries" to Icons.Filled.Book,
                        "Maintain a 30-day streak" to Icons.Filled.LocalFireDepartment,
                        "Send 10 future messages" to Icons.Filled.Schedule
                    )

                    milestones.forEach { (milestone, icon) ->
                        Row(
                            modifier = Modifier.padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = milestone,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InspirationalQuoteFooter() {
    val quotes = listOf(
        "The journey of a thousand miles begins with a single step." to "Lao Tzu",
        "What lies behind us and what lies before us are tiny matters compared to what lies within us." to "Ralph Waldo Emerson",
        "The only way to do great work is to love what you do." to "Steve Jobs",
        "Believe you can and you're halfway there." to "Theodore Roosevelt"
    )
    val (quote, author) = remember { quotes.random() }

    ProdyCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        backgroundColor = ProdyPrimary.copy(alpha = 0.08f)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.FormatQuote,
                contentDescription = null,
                tint = ProdyPrimary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = quote,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "— $author",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
