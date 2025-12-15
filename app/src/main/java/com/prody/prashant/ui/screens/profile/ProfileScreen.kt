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
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prody.prashant.R
import com.prody.prashant.data.local.entity.AchievementEntity
import com.prody.prashant.ui.components.ProdyCard
import com.prody.prashant.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Profile Screen - User Profile & Achievement Showcase
 *
 * A comprehensive profile view featuring:
 * - Animated header with avatar, streak, and points
 * - Level progress with visual indicator
 * - Statistics cards with animated counters
 * - Achievement showcase with filtering
 * - Journey milestones tracking
 * - Motivational growth quotes
 *
 * UI/UX Features:
 * - Staggered entrance animations for visual delight
 * - Animated glow effects for achievements
 * - Progress rings and bars with smooth animations
 * - Category filtering with animated state changes
 * - Proper accessibility with content descriptions
 *
 * Design Principles:
 * - Celebratory visual language for achievements
 * - Clear hierarchy with card-based sections
 * - Color-coded rarity system for badges
 * - Generous touch targets (min 48dp)
 */

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ProfileScreen(
    onNavigateToSettings: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Entry animations
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    // Achievement category filter
    var selectedCategory by remember { mutableStateOf<AchievementCategory?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Animated Profile Header
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(600)) + slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(600, easing = EaseOutCubic)
                )
            ) {
                ProfileHeader(
                    displayName = uiState.displayName,
                    title = uiState.title,
                    totalPoints = uiState.totalPoints,
                    currentStreak = uiState.currentStreak,
                    longestStreak = uiState.longestStreak,
                    daysOnPrody = uiState.daysOnPrody,
                    onSettingsClick = onNavigateToSettings
                )
            }
        }

        // Level progress section
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(600, delayMillis = 150)) + slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = tween(600, delayMillis = 150, easing = EaseOutCubic)
                )
            ) {
                LevelProgressSection(
                    totalPoints = uiState.totalPoints,
                    title = uiState.title
                )
            }
        }

        // Animated stats cards
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(600, delayMillis = 200)) + slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = tween(600, delayMillis = 200, easing = EaseOutCubic)
                )
            ) {
                ProfileStatsSection(
                    wordsLearned = uiState.wordsLearned,
                    journalEntries = uiState.journalEntries,
                    achievementsUnlocked = uiState.achievementsUnlocked,
                    daysOnPrody = uiState.daysOnPrody
                )
            }
        }

        // Achievements section header
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(600, delayMillis = 300))
            ) {
                AchievementsSectionHeader(
                    unlockedCount = uiState.unlockedAchievements.size,
                    totalCount = uiState.unlockedAchievements.size + uiState.lockedAchievements.size
                )
            }
        }

        // Achievement category filter
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(600, delayMillis = 350))
            ) {
                AchievementCategoryFilter(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it }
                )
            }
        }

        // Unlocked achievements showcase
        val filteredUnlocked = if (selectedCategory != null) {
            uiState.unlockedAchievements.filter {
                val achievement = Achievements.getAchievementById(it.id)
                achievement?.category == selectedCategory
            }
        } else {
            uiState.unlockedAchievements
        }

        if (filteredUnlocked.isNotEmpty()) {
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(600, delayMillis = 400))
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Verified,
                                contentDescription = null,
                                tint = AchievementUnlocked,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "${stringResource(R.string.unlocked)} (${filteredUnlocked.size})",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = AchievementUnlocked
                            )
                        }
                    }
                }
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(filteredUnlocked) { index, achievement ->
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = fadeIn(tween(300, delayMillis = 450 + index * 50)) +
                                    slideInHorizontally(
                                        initialOffsetX = { it },
                                        animationSpec = tween(400, delayMillis = 450 + index * 50)
                                    )
                        ) {
                            EnhancedAchievementCard(
                                achievement = achievement,
                                isUnlocked = true
                            )
                        }
                    }
                }
            }
        }

        // Locked achievements (in progress)
        val filteredLocked = if (selectedCategory != null) {
            uiState.lockedAchievements.filter {
                val achievement = Achievements.getAchievementById(it.id)
                achievement?.category == selectedCategory
            }
        } else {
            uiState.lockedAchievements
        }

        if (filteredLocked.isNotEmpty()) {
            item {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(600, delayMillis = 500))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = null,
                            tint = AchievementLocked,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "${stringResource(R.string.locked)} (${filteredLocked.size})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium,
                            color = AchievementLocked
                        )
                    }
                }
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(filteredLocked.take(8)) { index, achievement ->
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = fadeIn(tween(300, delayMillis = 550 + index * 50)) +
                                    slideInHorizontally(
                                        initialOffsetX = { it },
                                        animationSpec = tween(400, delayMillis = 550 + index * 50)
                                    )
                        ) {
                            EnhancedAchievementCard(
                                achievement = achievement,
                                isUnlocked = false
                            )
                        }
                    }
                }
            }
        }

        // Journey milestone card
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(600, delayMillis = 700))
            ) {
                JourneyMilestoneCard(
                    daysOnPrody = uiState.daysOnPrody,
                    totalPoints = uiState.totalPoints,
                    achievementsUnlocked = uiState.achievementsUnlocked
                )
            }
        }

        // Motivational growth journey hint
        item {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(600, delayMillis = 800))
            ) {
                GrowthJourneyCard()
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    displayName: String,
    title: String,
    totalPoints: Int,
    currentStreak: Int,
    longestStreak: Int,
    daysOnPrody: Int,
    onSettingsClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "header_animation")
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
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.75f)
                    )
                )
            )
    ) {
        // Animated background
        ProfileHeaderBackground()

        // Settings button
        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = stringResource(R.string.settings),
                tint = Color.White
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Animated Avatar with glow
            Box(
                contentAlignment = Alignment.Center
            ) {
                // Outer glow
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .blur(20.dp)
                        .alpha(glowAlpha)
                        .background(GoldTier, CircleShape)
                )

                // Avatar ring with progress
                AvatarProgressRing(
                    progress = (totalPoints % 1000) / 1000f,
                    modifier = Modifier.size(110.dp)
                )

                // Avatar
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.3f),
                                    Color.White.copy(alpha = 0.1f)
                                )
                            )
                        )
                        .border(3.dp, GoldTier.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        modifier = Modifier.size(50.dp),
                        tint = Color.White
                    )
                }

                // Level badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(GoldTier)
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = getLevelFromPoints(totalPoints).toString(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Name and title
            Text(
                text = displayName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.WorkspacePremium,
                    contentDescription = null,
                    tint = GoldTier,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    color = GoldTier,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Stats row with animations
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedHeaderStat(
                    value = totalPoints,
                    label = "Points",
                    icon = Icons.Filled.Stars,
                    iconColor = GoldTier
                )

                AnimatedStreakDisplay(
                    currentStreak = currentStreak,
                    longestStreak = longestStreak
                )

                AnimatedHeaderStat(
                    value = daysOnPrody,
                    label = "Days",
                    icon = Icons.Filled.CalendarMonth,
                    iconColor = MoodCalm
                )
            }
        }
    }
}

@Composable
private fun ProfileHeaderBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "bg_animation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(40000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Canvas(modifier = Modifier.fillMaxSize().alpha(0.2f)) {
        val center = Offset(size.width / 2, size.height / 2)

        // Orbiting circles
        for (i in 0 until 4) {
            val angle = (rotation + i * 90f) * PI / 180
            val radius = minOf(size.width, size.height) * 0.35f
            val x = center.x + radius * cos(angle).toFloat()
            val y = center.y + radius * sin(angle).toFloat()
            drawCircle(
                color = Color.White.copy(alpha = 0.15f - i * 0.03f),
                radius = 40f - i * 8f,
                center = Offset(x, y)
            )
        }

        // Central decorative rings
        drawCircle(
            color = Color.White.copy(alpha = 0.05f),
            radius = minOf(size.width, size.height) * 0.3f,
            center = center,
            style = Stroke(width = 2f)
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.08f),
            radius = minOf(size.width, size.height) * 0.4f,
            center = center,
            style = Stroke(width = 1f)
        )
    }
}

@Composable
private fun AvatarProgressRing(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1500, easing = FastOutSlowInEasing),
        label = "avatar_progress"
    )

    Canvas(modifier = modifier) {
        val strokeWidth = 4.dp.toPx()
        val radius = (minOf(size.width, size.height) - strokeWidth) / 2

        // Background ring
        drawCircle(
            color = Color.White.copy(alpha = 0.2f),
            radius = radius,
            style = Stroke(width = strokeWidth)
        )

        // Progress arc
        drawArc(
            color = GoldTier,
            startAngle = -90f,
            sweepAngle = animatedProgress * 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
            size = Size(size.width - strokeWidth, size.height - strokeWidth)
        )
    }
}

@Composable
private fun AnimatedHeaderStat(
    value: Int,
    label: String,
    icon: ImageVector,
    iconColor: Color
) {
    val animatedValue by animateIntAsState(
        targetValue = value,
        animationSpec = tween(1500, easing = FastOutSlowInEasing),
        label = "stat_value"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = formatNumber(animatedValue),
            style = MaterialTheme.typography.titleLarge,
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
private fun AnimatedStreakDisplay(
    currentStreak: Int,
    longestStreak: Int
) {
    val infiniteTransition = rememberInfiniteTransition(label = "streak")
    val fireScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fire_scale"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(contentAlignment = Alignment.Center) {
        // Fire glow
        if (currentStreak > 0) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .scale(fireScale)
                    .blur(15.dp)
                    .alpha(glowAlpha)
                    .background(StreakFire, CircleShape)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (currentStreak > 0) {
                            Brush.horizontalGradient(
                                colors = listOf(
                                    StreakFire.copy(alpha = 0.6f),
                                    StreakGlow.copy(alpha = 0.4f)
                                )
                            )
                        } else {
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Gray.copy(alpha = 0.3f),
                                    Color.Gray.copy(alpha = 0.2f)
                                )
                            )
                        }
                    )
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.LocalFireDepartment,
                    contentDescription = null,
                    tint = if (currentStreak > 0) StreakFire else Color.Gray,
                    modifier = Modifier
                        .size(28.dp)
                        .scale(if (currentStreak > 0) fireScale else 1f)
                )
                Text(
                    text = currentStreak.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Streak",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.8f)
            )

            if (longestStreak > currentStreak) {
                Text(
                    text = "Best: $longestStreak",
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldTier.copy(alpha = 0.8f),
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun LevelProgressSection(
    totalPoints: Int,
    title: String
) {
    val level = getLevelFromPoints(totalPoints)
    val currentLevelPoints = getLevelThreshold(level)
    val nextLevelPoints = getLevelThreshold(level + 1)
    val progress = if (nextLevelPoints > currentLevelPoints) {
        (totalPoints - currentLevelPoints).toFloat() / (nextLevelPoints - currentLevelPoints)
    } else {
        1f
    }

    var isAnimated by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(300)
        isAnimated = true
    }

    val animatedProgress by animateFloatAsState(
        targetValue = if (isAnimated) progress else 0f,
        animationSpec = tween(1500, easing = FastOutSlowInEasing),
        label = "level_progress"
    )

    ProdyCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Level Progress",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Lvl $level",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress bar with gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MoodMotivated,
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
                    text = "${totalPoints - currentLevelPoints} XP",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${nextLevelPoints - currentLevelPoints} XP to next level",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Next: ${getNextTitle(title)}",
                style = MaterialTheme.typography.labelMedium,
                color = GoldTier,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ProfileStatsSection(
    wordsLearned: Int,
    journalEntries: Int,
    achievementsUnlocked: Int,
    daysOnPrody: Int
) {
    val stats = listOf(
        ProfileStat("Words", wordsLearned, Icons.Outlined.School, MoodMotivated),
        ProfileStat("Entries", journalEntries, Icons.Outlined.Book, MoodCalm),
        ProfileStat("Badges", achievementsUnlocked, Icons.Outlined.EmojiEvents, GoldTier),
        ProfileStat("Days", daysOnPrody, Icons.Outlined.CalendarMonth, MoodGrateful)
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(stats.size) { index ->
            ProfileStatCard(
                stat = stats[index],
                delayMillis = index * 100
            )
        }
    }
}

private data class ProfileStat(
    val label: String,
    val value: Int,
    val icon: ImageVector,
    val color: Color
)

@Composable
private fun ProfileStatCard(
    stat: ProfileStat,
    delayMillis: Int
) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(delayMillis.toLong())
        isVisible = true
    }

    val animatedValue by animateIntAsState(
        targetValue = if (isVisible) stat.value else 0,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "stat_value"
    )

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    ProdyCard(
        modifier = Modifier
            .width(95.dp)
            .scale(scale),
        backgroundColor = stat.color.copy(alpha = 0.1f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(stat.color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = stat.icon,
                    contentDescription = null,
                    tint = stat.color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = formatNumber(animatedValue),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = stat.color
            )
            Text(
                text = stat.label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AchievementsSectionHeader(
    unlockedCount: Int,
    totalCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.EmojiEvents,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = stringResource(R.string.achievements),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "$unlockedCount / $totalCount",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun AchievementCategoryFilter(
    selectedCategory: AchievementCategory?,
    onCategorySelected: (AchievementCategory?) -> Unit
) {
    val categories = listOf(
        null to "All",
        AchievementCategory.STREAK to "Streak",
        AchievementCategory.LEARNING to "Learning",
        AchievementCategory.JOURNAL to "Journal",
        AchievementCategory.SOCIAL to "Social",
        AchievementCategory.SPECIAL to "Special"
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { (category, label) ->
            val isSelected = selectedCategory == category
            val backgroundColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant,
                animationSpec = tween(200),
                label = "filter_bg"
            )
            val textColor by animateColorAsState(
                targetValue = if (isSelected) Color.White
                else MaterialTheme.colorScheme.onSurfaceVariant,
                animationSpec = tween(200),
                label = "filter_text"
            )

            Surface(
                modifier = Modifier.clickable { onCategorySelected(category) },
                color = backgroundColor,
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = textColor,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun EnhancedAchievementCard(
    achievement: AchievementEntity,
    isUnlocked: Boolean
) {
    val rarity = try {
        AchievementRarity.valueOf(achievement.rarity.uppercase())
    } catch (e: Exception) {
        AchievementRarity.COMMON
    }

    val achievementData = Achievements.getAchievementById(achievement.id)

    val infiniteTransition = rememberInfiniteTransition(label = "achievement_glow")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isUnlocked && rarity.ordinal >= AchievementRarity.RARE.ordinal) 1.05f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    ProdyCard(
        modifier = Modifier
            .width(150.dp)
            .scale(glowScale)
            .clickable { },
        backgroundColor = if (isUnlocked)
            rarity.color.copy(alpha = 0.12f)
        else
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        elevation = if (isUnlocked) 4.dp else 2.dp
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon with animated background
            Box(contentAlignment = Alignment.Center) {
                if (isUnlocked && rarity.ordinal >= AchievementRarity.RARE.ordinal) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .blur(12.dp)
                            .alpha(0.4f)
                            .background(rarity.color, CircleShape)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(
                            if (isUnlocked) {
                                Brush.radialGradient(
                                    colors = listOf(
                                        rarity.color.copy(alpha = 0.3f),
                                        rarity.color.copy(alpha = 0.1f)
                                    )
                                )
                            } else {
                                Brush.radialGradient(
                                    colors = listOf(
                                        AchievementLocked.copy(alpha = 0.2f),
                                        AchievementLocked.copy(alpha = 0.1f)
                                    )
                                )
                            }
                        )
                        .then(
                            if (isUnlocked) {
                                Modifier.border(2.dp, rarity.color.copy(alpha = 0.5f), CircleShape)
                            } else {
                                Modifier
                            }
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
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Progress bar for locked achievements
            if (!isUnlocked && achievement.requirement > 0) {
                val progress = achievement.currentProgress.toFloat() / achievement.requirement

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress.coerceIn(0f, 1f))
                            .fillMaxHeight()
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

            // Rarity badge
            Surface(
                color = if (isUnlocked) rarity.color.copy(alpha = 0.15f)
                else AchievementLocked.copy(alpha = 0.1f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = rarity.displayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isUnlocked) rarity.color else AchievementLocked,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }
        }
    }
}

/**
 * Journey Milestones Card - Premium Achievement-Style Display
 *
 * Features:
 * - Achievement-style milestone cards instead of radio buttons
 * - Completed milestones: Golden accent, checkmark, subtle glow
 * - Locked milestones: Dimmed with padlock, progress indicator
 * - In-progress milestones: Animated progress ring
 * - Proper visual hierarchy and state management
 */
@Composable
private fun JourneyMilestoneCard(
    daysOnPrody: Int,
    totalPoints: Int,
    achievementsUnlocked: Int
) {
    // Define milestones with name, target, current progress, and achievement status
    data class Milestone(
        val name: String,
        val description: String,
        val target: Int,
        val current: Int,
        val icon: ImageVector,
        val color: Color,
        val xpReward: Int
    )

    val milestones = listOf(
        Milestone(
            name = "First Week",
            description = "Active for 7 days",
            target = 7,
            current = daysOnPrody.coerceAtMost(7),
            icon = Icons.Filled.CalendarMonth,
            color = MoodCalm,
            xpReward = 50
        ),
        Milestone(
            name = "Century",
            description = "Earn 100 points",
            target = 100,
            current = totalPoints.coerceAtMost(100),
            icon = Icons.Filled.Stars,
            color = GoldTier,
            xpReward = 25
        ),
        Milestone(
            name = "First Badge",
            description = "Unlock an achievement",
            target = 1,
            current = achievementsUnlocked.coerceAtMost(1),
            icon = Icons.Filled.EmojiEvents,
            color = MoodExcited,
            xpReward = 30
        ),
        Milestone(
            name = "Monthly",
            description = "Active for 30 days",
            target = 30,
            current = daysOnPrody.coerceAtMost(30),
            icon = Icons.Filled.Event,
            color = MoodMotivated,
            xpReward = 150
        ),
        Milestone(
            name = "Half K",
            description = "Earn 500 points",
            target = 500,
            current = totalPoints.coerceAtMost(500),
            icon = Icons.Filled.Bolt,
            color = MoodGrateful,
            xpReward = 75
        ),
        Milestone(
            name = "Collector",
            description = "Unlock 5 badges",
            target = 5,
            current = achievementsUnlocked.coerceAtMost(5),
            icon = Icons.Filled.Diamond,
            color = SilverTier,
            xpReward = 100
        )
    )

    ProdyCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Section header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Flag,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Journey Milestones",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Track your growth path",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Completion counter
                val completedCount = milestones.count { it.current >= it.target }
                Surface(
                    color = if (completedCount == milestones.size)
                        GoldTier.copy(alpha = 0.2f)
                    else
                        MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "$completedCount / ${milestones.size}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (completedCount == milestones.size)
                            GoldTier
                        else
                            MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Milestone grid - 2 columns
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                milestones.chunked(2).forEach { rowMilestones ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowMilestones.forEach { milestone ->
                            MilestoneCard(
                                name = milestone.name,
                                description = milestone.description,
                                target = milestone.target,
                                current = milestone.current,
                                icon = milestone.icon,
                                color = milestone.color,
                                xpReward = milestone.xpReward,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        // Fill remaining space if odd number
                        if (rowMilestones.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

/**
 * Individual Milestone Card - Achievement-Style Display
 *
 * States:
 * - COMPLETED: Golden glow, checkmark icon, celebration styling
 * - IN_PROGRESS: Animated progress ring, percentage display
 * - LOCKED: Dimmed, padlock icon overlay
 */

@Composable
private fun MilestoneCard(
    name: String,
    description: String,
    target: Int,
    current: Int,
    icon: ImageVector,
    color: Color,
    xpReward: Int,
    modifier: Modifier = Modifier
) {
    val isCompleted = current >= target
    val progress = (current.toFloat() / target).coerceIn(0f, 1f)
    val isInProgress = !isCompleted && current > 0

    val infiniteTransition = rememberInfiniteTransition(label = "milestone_anim")

    // Glow animation for completed milestones
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = if (isCompleted) 0.3f else 0f,
        targetValue = if (isCompleted) 0.6f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    // Progress ring animation for in-progress milestones
    val progressRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isInProgress) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress_rotation"
    )

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "progress"
    )

    val scale by animateFloatAsState(
        targetValue = if (isCompleted) 1f else 0.98f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Box(modifier = modifier.scale(scale)) {
        // Card background
        ProdyCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = when {
                isCompleted -> color.copy(alpha = 0.12f)
                isInProgress -> MaterialTheme.colorScheme.surface
                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            },
            elevation = if (isCompleted) 4.dp else 2.dp
        ) {
            Box {
                // Glow effect for completed
                if (isCompleted) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(60.dp)
                            .offset(x = 15.dp, y = (-15).dp)
                            .blur(20.dp)
                            .alpha(glowAlpha)
                            .background(color, CircleShape)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Icon with status indicator
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(56.dp)
                    ) {
                        // Progress ring background
                        if (isInProgress || isCompleted) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val strokeWidth = 3.dp.toPx()
                                val radius = (size.minDimension - strokeWidth) / 2

                                // Background ring
                                drawCircle(
                                    color = color.copy(alpha = 0.2f),
                                    radius = radius,
                                    style = Stroke(width = strokeWidth)
                                )

                                // Progress arc
                                drawArc(
                                    color = color,
                                    startAngle = -90f,
                                    sweepAngle = animatedProgress * 360f,
                                    useCenter = false,
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                                    topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                                    size = Size(size.width - strokeWidth, size.height - strokeWidth)
                                )
                            }
                        }

                        // Icon background
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isCompleted -> Brush.radialGradient(
                                            colors = listOf(
                                                color.copy(alpha = 0.3f),
                                                color.copy(alpha = 0.15f)
                                            )
                                        )
                                        else -> Brush.radialGradient(
                                            colors = listOf(
                                                AchievementLocked.copy(alpha = 0.15f),
                                                AchievementLocked.copy(alpha = 0.08f)
                                            )
                                        )
                                    }
                                )
                                .then(
                                    if (isCompleted) {
                                        Modifier.border(2.dp, color.copy(alpha = 0.5f), CircleShape)
                                    } else {
                                        Modifier.border(
                                            1.dp,
                                            MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                            CircleShape
                                        )
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCompleted) {
                                // Checkmark overlay for completed
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Completed",
                                    tint = color,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else if (!isInProgress) {
                                // Lock icon for locked milestones
                                Icon(
                                    imageVector = Icons.Filled.Lock,
                                    contentDescription = "Locked",
                                    tint = AchievementLocked,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                // Original icon for in-progress
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = color,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        // Completed badge
                        if (isCompleted) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(AchievementUnlocked)
                                    .border(1.5.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Milestone name
                    Text(
                        text = name,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isCompleted) FontWeight.Bold else FontWeight.SemiBold,
                        color = when {
                            isCompleted -> color
                            isInProgress -> MaterialTheme.colorScheme.onSurface
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Description
                    Text(
                        text = description,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            alpha = if (isCompleted) 0.9f else 0.7f
                        ),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 10.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Progress indicator or completion badge
                    if (isCompleted) {
                        // XP reward badge
                        Surface(
                            color = GoldTier.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Stars,
                                    contentDescription = null,
                                    tint = GoldTier,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "+$xpReward XP",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GoldTier,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    } else {
                        // Progress text
                        Text(
                            text = "$current / $target",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isInProgress) color else AchievementLocked,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GrowthJourneyCard() {
    val quotes = remember {
        listOf(
            "Every step forward is progress.",
            "Your growth journey is unique and beautiful.",
            "Small daily improvements lead to stunning results.",
            "The path to wisdom begins with self-reflection.",
            "You're building something remarkable."
        )
    }
    // Use randomOrNull with fallback for defensive programming
    val quote = remember { quotes.randomOrNull() ?: "Every step forward is progress." }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        ProdyPrimary.copy(alpha = 0.1f),
                        ProdyTertiary.copy(alpha = 0.1f)
                    )
                )
            )
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.FormatQuote,
                contentDescription = null,
                tint = ProdyPrimary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = quote,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
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

private fun getLevelFromPoints(points: Int): Int {
    return when {
        points >= 10000 -> 10
        points >= 7500 -> 9
        points >= 5000 -> 8
        points >= 3500 -> 7
        points >= 2500 -> 6
        points >= 1500 -> 5
        points >= 1000 -> 4
        points >= 500 -> 3
        points >= 200 -> 2
        else -> 1
    }
}

private fun getLevelThreshold(level: Int): Int {
    return when (level) {
        1 -> 0
        2 -> 200
        3 -> 500
        4 -> 1000
        5 -> 1500
        6 -> 2500
        7 -> 3500
        8 -> 5000
        9 -> 7500
        10 -> 10000
        else -> 15000
    }
}

private fun getNextTitle(currentTitle: String): String {
    val titles = listOf("Newcomer", "Apprentice", "Scholar", "Sage", "Master", "Grandmaster", "Legend")
    val currentIndex = titles.indexOf(currentTitle)
    return if (currentIndex >= 0 && currentIndex < titles.size - 1) {
        titles[currentIndex + 1]
    } else {
        "Ascended"
    }
}
