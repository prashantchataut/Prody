package com.prody.prashant.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.ui.theme.PoppinsFamily
import com.prody.prashant.ui.theme.isDarkTheme
import kotlin.math.*

/**
 * Growth Garden - Visual Stats Representation
 *
 * A beautiful, organic visualization of user progress:
 * - Journal entries = Leaves/flowers on the tree
 * - Streak days = Tree trunk growth
 * - Missed days = Withered leaves (recoverable through consistency)
 *
 * Design Philosophy:
 * - Organic, hand-drawn aesthetic
 * - Peaceful, zen-like animation
 * - Celebrates growth without being gamey
 * - Withered elements show recovery is possible
 */

/**
 * Main Growth Garden composable showing a growing tree
 * that represents the user's growth journey.
 *
 * @param journalEntries Total number of journal entries (affects leaf count)
 * @param currentStreak Current streak days (affects tree vitality)
 * @param longestStreak Longest streak achieved (reference point)
 * @param missedDays Recent missed days (affects withered leaves)
 * @param modifier Modifier for sizing and positioning
 */
@Composable
fun GrowthGarden(
    journalEntries: Int,
    currentStreak: Int,
    longestStreak: Int,
    missedDays: Int = 0,
    modifier: Modifier = Modifier
) {
    val isDarkMode = isDarkTheme()

    // Animation for gentle swaying
    val infiniteTransition = rememberInfiniteTransition(label = "garden_animation")

    val swayAngle by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sway"
    )

    val breathe by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe"
    )

    val sparkle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sparkle"
    )

    // Calculate tree properties based on stats
    val treeGrowthStage = calculateGrowthStage(journalEntries)
    val treeVitality = calculateVitality(currentStreak, longestStreak)
    val leafCount = (journalEntries.coerceAtMost(50)) // Cap at 50 for visual clarity
    val witheredCount = (missedDays.coerceAtMost(5)) // Max 5 withered leaves shown

    // Colors
    val trunkColor = if (isDarkMode) Color(0xFF5D4037) else Color(0xFF6D4C41)
    val leafColorHealthy = if (isDarkMode) Color(0xFF36F97F) else Color(0xFF2ECC71)
    val leafColorWithered = if (isDarkMode) Color(0xFF8B6914) else Color(0xFFA67C00)
    val flowerColor = if (isDarkMode) Color(0xFFFF69B4) else Color(0xFFE91E63)
    val backgroundColor = if (isDarkMode) Color(0xFF1A3331).copy(alpha = 0.3f) else Color(0xFFF5F8F7)
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        color = backgroundColor,
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Growth Garden",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = textPrimary
                )
                Text(
                    text = "${leafCount} leaves",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    color = textSecondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tree Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawGrowthTree(
                        growthStage = treeGrowthStage,
                        vitality = treeVitality,
                        leafCount = leafCount,
                        witheredCount = witheredCount,
                        swayAngle = swayAngle,
                        breatheScale = breathe,
                        sparklePhase = sparkle,
                        trunkColor = trunkColor,
                        leafColorHealthy = leafColorHealthy,
                        leafColorWithered = leafColorWithered,
                        flowerColor = flowerColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stats summary row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                GrowthGardenStat(
                    label = "Stage",
                    value = getGrowthStageName(treeGrowthStage),
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )
                GrowthGardenStat(
                    label = "Vitality",
                    value = "${(treeVitality * 100).toInt()}%",
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )
                GrowthGardenStat(
                    label = "Streak",
                    value = "${currentStreak}d",
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )
            }

            // Recovery message if there are withered leaves
            if (witheredCount > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Keep journaling to restore withered leaves",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp,
                    color = leafColorWithered.copy(alpha = 0.8f),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
private fun GrowthGardenStat(
    label: String,
    value: String,
    textPrimary: Color,
    textSecondary: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = textPrimary
        )
        Text(
            text = label,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 11.sp,
            color = textSecondary
        )
    }
}

/**
 * Draw the growth tree on canvas
 */
private fun DrawScope.drawGrowthTree(
    growthStage: Int,
    vitality: Float,
    leafCount: Int,
    witheredCount: Int,
    swayAngle: Float,
    breatheScale: Float,
    sparklePhase: Float,
    trunkColor: Color,
    leafColorHealthy: Color,
    leafColorWithered: Color,
    flowerColor: Color
) {
    val centerX = size.width / 2
    val groundY = size.height * 0.9f

    // Ground line
    drawLine(
        color = trunkColor.copy(alpha = 0.3f),
        start = Offset(0f, groundY),
        end = Offset(size.width, groundY),
        strokeWidth = 2f
    )

    // Tree trunk height based on growth stage
    val trunkHeight = size.height * (0.2f + growthStage * 0.1f).coerceAtMost(0.6f)
    val trunkWidth = 8f + growthStage * 2f

    // Draw trunk with slight curve
    val trunkPath = Path().apply {
        moveTo(centerX - trunkWidth / 2, groundY)
        cubicTo(
            centerX - trunkWidth / 2 + swayAngle, groundY - trunkHeight * 0.3f,
            centerX - trunkWidth / 3 + swayAngle * 1.5f, groundY - trunkHeight * 0.7f,
            centerX + swayAngle * 2, groundY - trunkHeight
        )
        lineTo(centerX + trunkWidth / 2 + swayAngle * 2, groundY - trunkHeight)
        cubicTo(
            centerX + trunkWidth / 3 + swayAngle * 1.5f, groundY - trunkHeight * 0.7f,
            centerX + trunkWidth / 2 + swayAngle, groundY - trunkHeight * 0.3f,
            centerX + trunkWidth / 2, groundY
        )
        close()
    }
    drawPath(path = trunkPath, color = trunkColor)

    // Branches based on growth stage
    if (growthStage >= 2) {
        drawBranch(
            startX = centerX + swayAngle * 1.5f,
            startY = groundY - trunkHeight * 0.6f,
            angle = -45f + swayAngle * 3,
            length = trunkHeight * 0.25f,
            thickness = trunkWidth * 0.4f,
            color = trunkColor
        )
    }
    if (growthStage >= 3) {
        drawBranch(
            startX = centerX + swayAngle * 1.8f,
            startY = groundY - trunkHeight * 0.75f,
            angle = 50f + swayAngle * 3,
            length = trunkHeight * 0.3f,
            thickness = trunkWidth * 0.35f,
            color = trunkColor
        )
    }
    if (growthStage >= 4) {
        drawBranch(
            startX = centerX + swayAngle * 2f,
            startY = groundY - trunkHeight * 0.9f,
            angle = -60f + swayAngle * 3,
            length = trunkHeight * 0.2f,
            thickness = trunkWidth * 0.3f,
            color = trunkColor
        )
    }

    // Crown/canopy area
    val crownCenterY = groundY - trunkHeight - size.height * 0.1f

    // Draw leaves based on count
    val actualLeafCount = leafCount.coerceAtMost(50)
    val leafPositions = generateLeafPositions(
        centerX = centerX + swayAngle * 2,
        centerY = crownCenterY,
        count = actualLeafCount,
        radius = size.height * 0.15f * breatheScale * (0.7f + growthStage * 0.1f)
    )

    // Draw withered leaves first (at the edges)
    val witheredPositions = leafPositions.takeLast(witheredCount)
    witheredPositions.forEach { pos ->
        drawLeaf(
            x = pos.x + swayAngle,
            y = pos.y,
            size = 8f,
            color = leafColorWithered,
            rotation = pos.x % 360f,
            isWithered = true
        )
    }

    // Draw healthy leaves
    val healthyPositions = leafPositions.dropLast(witheredCount)
    healthyPositions.forEachIndexed { index, pos ->
        val leafSize = 6f + (vitality * 4f)
        val leafAlpha = 0.7f + vitality * 0.3f
        drawLeaf(
            x = pos.x + swayAngle * (1f + index * 0.1f),
            y = pos.y,
            size = leafSize,
            color = leafColorHealthy.copy(alpha = leafAlpha),
            rotation = pos.x % 360f + swayAngle * 5,
            isWithered = false
        )
    }

    // Draw flowers for milestones (every 10 entries)
    val flowerCount = (leafCount / 10).coerceAtMost(5)
    repeat(flowerCount) { i ->
        val angle = (i * 72f + sparklePhase * 20f) * PI.toFloat() / 180f
        val radius = size.height * 0.12f * breatheScale
        val flowerX = centerX + swayAngle * 2 + cos(angle) * radius
        val flowerY = crownCenterY + sin(angle) * radius * 0.7f
        drawFlower(
            x = flowerX,
            y = flowerY,
            size = 10f + sparklePhase * 2f,
            color = flowerColor,
            sparkle = sparklePhase
        )
    }

    // Sparkle effect for high vitality
    if (vitality > 0.7f) {
        repeat(3) { i ->
            val sparkleX = centerX + swayAngle * 2 + cos(sparklePhase * 2 * PI.toFloat() + i * 2f) * size.height * 0.1f
            val sparkleY = crownCenterY + sin(sparklePhase * 2 * PI.toFloat() + i * 2f) * size.height * 0.08f
            val sparkleAlpha = sin(sparklePhase * PI.toFloat() + i) * 0.5f + 0.3f
            drawCircle(
                color = Color.White.copy(alpha = sparkleAlpha.coerceIn(0f, 1f) * vitality),
                radius = 2f,
                center = Offset(sparkleX, sparkleY)
            )
        }
    }
}

private fun DrawScope.drawBranch(
    startX: Float,
    startY: Float,
    angle: Float,
    length: Float,
    thickness: Float,
    color: Color
) {
    val radians = angle * PI.toFloat() / 180f
    val endX = startX + cos(radians) * length
    val endY = startY + sin(radians) * length

    drawLine(
        color = color,
        start = Offset(startX, startY),
        end = Offset(endX, endY),
        strokeWidth = thickness,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawLeaf(
    x: Float,
    y: Float,
    size: Float,
    color: Color,
    rotation: Float,
    isWithered: Boolean
) {
    rotate(rotation, Offset(x, y)) {
        if (isWithered) {
            // Withered leaf - droopy
            val path = Path().apply {
                moveTo(x, y - size / 2)
                quadraticBezierTo(x + size / 2, y + size / 4, x, y + size)
                quadraticBezierTo(x - size / 2, y + size / 4, x, y - size / 2)
                close()
            }
            drawPath(path = path, color = color.copy(alpha = 0.6f))
        } else {
            // Healthy leaf - upward pointing
            val path = Path().apply {
                moveTo(x, y - size)
                quadraticBezierTo(x + size / 2, y - size / 2, x, y + size / 2)
                quadraticBezierTo(x - size / 2, y - size / 2, x, y - size)
                close()
            }
            drawPath(path = path, color = color)
        }
    }
}

private fun DrawScope.drawFlower(
    x: Float,
    y: Float,
    size: Float,
    color: Color,
    sparkle: Float
) {
    val petalCount = 5
    repeat(petalCount) { i ->
        val angle = (i * 360f / petalCount + sparkle * 10f) * PI.toFloat() / 180f
        val petalX = x + cos(angle) * size / 2
        val petalY = y + sin(angle) * size / 2
        drawCircle(
            color = color.copy(alpha = 0.8f),
            radius = size / 3,
            center = Offset(petalX, petalY)
        )
    }
    // Center
    drawCircle(
        color = Color(0xFFFFD54F),
        radius = size / 4,
        center = Offset(x, y)
    )
}

private fun generateLeafPositions(
    centerX: Float,
    centerY: Float,
    count: Int,
    radius: Float
): List<Offset> {
    val positions = mutableListOf<Offset>()
    val goldenAngle = PI * (3 - sqrt(5f)) // Golden angle for natural distribution

    repeat(count) { i ->
        val r = radius * sqrt((i + 1).toFloat() / count)
        val theta = i * goldenAngle
        val x = centerX + r * cos(theta).toFloat()
        val y = centerY + r * sin(theta).toFloat() * 0.7f // Elliptical distribution
        positions.add(Offset(x, y))
    }
    return positions
}

private fun calculateGrowthStage(entries: Int): Int {
    return when {
        entries >= 100 -> 5
        entries >= 50 -> 4
        entries >= 20 -> 3
        entries >= 5 -> 2
        entries >= 1 -> 1
        else -> 0
    }
}

private fun calculateVitality(currentStreak: Int, longestStreak: Int): Float {
    if (longestStreak == 0) return if (currentStreak > 0) 0.5f else 0.2f
    val ratio = currentStreak.toFloat() / longestStreak.coerceAtLeast(1)
    return (ratio * 0.7f + 0.3f).coerceIn(0.2f, 1f)
}

private fun getGrowthStageName(stage: Int): String {
    return when (stage) {
        0 -> "Seed"
        1 -> "Sprout"
        2 -> "Sapling"
        3 -> "Young Tree"
        4 -> "Mature Tree"
        5 -> "Ancient Tree"
        else -> "Seed"
    }
}

/**
 * Compact version of Growth Garden for smaller spaces
 */
@Composable
fun GrowthGardenCompact(
    journalEntries: Int,
    currentStreak: Int,
    modifier: Modifier = Modifier
) {
    val isDarkMode = isDarkTheme()
    val leafColorHealthy = if (isDarkMode) Color(0xFF36F97F) else Color(0xFF2ECC71)
    val trunkColor = if (isDarkMode) Color(0xFF5D4037) else Color(0xFF6D4C41)

    val growthStage = calculateGrowthStage(journalEntries)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Mini tree icon
        Canvas(modifier = Modifier.size(32.dp)) {
            val centerX = size.width / 2
            val groundY = size.height * 0.9f
            val trunkHeight = size.height * 0.5f

            // Simple trunk
            drawLine(
                color = trunkColor,
                start = Offset(centerX, groundY),
                end = Offset(centerX, groundY - trunkHeight),
                strokeWidth = 3f,
                cap = StrokeCap.Round
            )

            // Simple crown
            drawCircle(
                color = leafColorHealthy,
                radius = size.width * 0.3f,
                center = Offset(centerX, groundY - trunkHeight - size.width * 0.2f)
            )
        }

        Column {
            Text(
                text = getGrowthStageName(growthStage),
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "$journalEntries entries",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
