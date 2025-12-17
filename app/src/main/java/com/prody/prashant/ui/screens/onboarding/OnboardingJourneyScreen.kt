package com.prody.prashant.ui.screens.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * OnboardingJourneyScreen - Premium 3-Screen Onboarding Flow
 *
 * A stoic, wisdom-focused onboarding experience with:
 * 1. "The Awakening" - Welcome with spinning lotus/spiral icon
 * 2. "The Path" - Gamification showcase with custom Canvas arc
 * 3. "The Contract" - Commitment with name input and seal animation
 *
 * Design System:
 * - Font: Playfair Display (Serif) for Headlines, Poppins (Sans) for body
 * - Background: #121212 (Dark) / #F5F2E9 (Light)
 * - Primary: #1A3C34 (Deep Green)
 * - Accent: #C5A059 (Muted Gold)
 * - Shapes: RoundedCornerShape(8.dp) for buttons
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingJourneyScreen(
    onComplete: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()
    val isDarkTheme = isSystemInDarkTheme()

    // Theme colors
    val backgroundColor = if (isDarkTheme) JourneyBackgroundDark else JourneyBackgroundLight

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .systemBarsPadding()
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            beyondViewportPageCount = 1
        ) { page ->
            when (page) {
                0 -> AwakeningScreen(
                    isDarkTheme = isDarkTheme,
                    onBeginJourney = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    }
                )
                1 -> PathScreen(
                    isDarkTheme = isDarkTheme,
                    onContinue = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(2)
                        }
                    }
                )
                2 -> ContractScreen(
                    isDarkTheme = isDarkTheme,
                    onSignAndEnter = onComplete
                )
            }
        }

        // Page indicators at bottom center (only visible on screens 1 and 2)
        if (pagerState.currentPage < 2) {
            JourneyPageIndicator(
                pageCount = 3,
                currentPage = pagerState.currentPage,
                isDarkTheme = isDarkTheme,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 120.dp)
            )
        }
    }
}

// =============================================================================
// SCREEN 1: THE AWAKENING (Welcome)
// =============================================================================

@Composable
private fun AwakeningScreen(
    isDarkTheme: Boolean,
    onBeginJourney: () -> Unit
) {
    val textPrimary = if (isDarkTheme) JourneyTextPrimaryDark else JourneyTextPrimaryLight
    val textSecondary = if (isDarkTheme) JourneyTextSecondaryDark else JourneyTextSecondaryLight

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.3f))

            // Spinning Lotus/Spiral Icon
            SpinningLotusIcon(
                modifier = Modifier.size(180.dp),
                color = JourneyAccent
            )

            Spacer(modifier = Modifier.weight(0.15f))

            // Headline - Playfair Display
            Text(
                text = "Evolve Your Mind.",
                style = TextStyle(
                    fontFamily = PlayfairFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    lineHeight = 40.sp
                ),
                color = textPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Subtext - Poppins
            Text(
                text = "Stoic wisdom meets modern psychology. Your journey to self-mastery begins now.",
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    lineHeight = 26.sp
                ),
                color = textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.weight(0.35f))
        }

        // Begin Journey Button - Fixed at bottom
        JourneyPrimaryButton(
            text = "Begin Journey",
            onClick = onBeginJourney,
            isDarkTheme = isDarkTheme,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 32.dp, vertical = 32.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun SpinningLotusIcon(
    modifier: Modifier = Modifier,
    color: Color
) {
    val infiniteTransition = rememberInfiniteTransition(label = "lotusRotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2 * 0.7f

        rotate(rotation, pivot = center) {
            // Draw lotus/spiral pattern
            val petalCount = 8
            val petalWidth = radius * 0.35f

            for (i in 0 until petalCount) {
                val angle = (i * 360f / petalCount) * (PI / 180f).toFloat()

                // Draw petal shape using arcs
                val petalPath = Path().apply {
                    val startX = center.x + (radius * 0.2f) * cos(angle)
                    val startY = center.y + (radius * 0.2f) * sin(angle)
                    val endX = center.x + radius * cos(angle)
                    val endY = center.y + radius * sin(angle)

                    moveTo(startX, startY)

                    // Control points for bezier curve to create petal shape
                    val controlAngle1 = angle + 0.4f
                    val controlAngle2 = angle - 0.4f
                    val controlRadius = radius * 0.7f

                    cubicTo(
                        center.x + controlRadius * cos(controlAngle1),
                        center.y + controlRadius * sin(controlAngle1),
                        center.x + controlRadius * cos(controlAngle2),
                        center.y + controlRadius * sin(controlAngle2),
                        endX,
                        endY
                    )

                    // Return path
                    cubicTo(
                        center.x + controlRadius * 0.8f * cos(controlAngle2),
                        center.y + controlRadius * 0.8f * sin(controlAngle2),
                        center.x + controlRadius * 0.8f * cos(controlAngle1),
                        center.y + controlRadius * 0.8f * sin(controlAngle1),
                        startX,
                        startY
                    )
                    close()
                }

                drawPath(
                    path = petalPath,
                    color = color.copy(alpha = 0.8f),
                    style = Stroke(width = 2.dp.toPx())
                )
            }

            // Center circle
            drawCircle(
                color = color,
                radius = radius * 0.15f,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )
        }
    }
}

// =============================================================================
// SCREEN 2: THE PATH (Gamification Showcase)
// =============================================================================

@Composable
private fun PathScreen(
    isDarkTheme: Boolean,
    onContinue: () -> Unit
) {
    val textPrimary = if (isDarkTheme) JourneyTextPrimaryDark else JourneyTextPrimaryLight
    val textSecondary = if (isDarkTheme) JourneyTextSecondaryDark else JourneyTextSecondaryLight

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Headline - Top
            Text(
                text = "Gamified Growth",
                style = TextStyle(
                    fontFamily = PlayfairFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    lineHeight = 36.sp
                ),
                color = textPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtext
            Text(
                text = "Earn XP for journaling, reading wisdom, and keeping promises.",
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                ),
                color = textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Top Half - The Arc with Trophy and Rank
            Box(
                modifier = Modifier
                    .size(240.dp),
                contentAlignment = Alignment.Center
            ) {
                // Custom Canvas Arc
                GamificationArc(
                    progress = 0f, // Starting at 0
                    isDarkTheme = isDarkTheme,
                    modifier = Modifier.fillMaxSize()
                )

                // Center Content - Trophy and Rank
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Trophy Icon
                    Icon(
                        imageVector = Icons.Filled.EmojiEvents,
                        contentDescription = "Trophy",
                        tint = JourneyAccent,
                        modifier = Modifier.size(48.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Rank: Seeker",
                        style = TextStyle(
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        ),
                        color = textPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Level 1 \u2022 0/100 XP",
                        style = TextStyle(
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp
                        ),
                        color = textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Bottom Half - Row of 3 Cards (Bento Box)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Card 1: Streak
                StatBentoCard(
                    icon = Icons.Filled.LocalFireDepartment,
                    iconTint = JourneyIconFire,
                    label = "Streak",
                    value = "0",
                    isLocked = false,
                    isDarkTheme = isDarkTheme,
                    modifier = Modifier.weight(1f)
                )

                // Card 2: Wisdom
                StatBentoCard(
                    icon = Icons.Filled.AutoStories,
                    iconTint = JourneyIconScroll,
                    label = "Wisdom",
                    value = "Daily",
                    isLocked = false,
                    isDarkTheme = isDarkTheme,
                    modifier = Modifier.weight(1f)
                )

                // Card 3: Locked
                StatBentoCard(
                    icon = Icons.Filled.Lock,
                    iconTint = JourneyIconLocked,
                    label = "Locked",
                    value = "???",
                    isLocked = true,
                    isDarkTheme = isDarkTheme,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        // Continue Button
        JourneyPrimaryButton(
            text = "Continue",
            onClick = onContinue,
            isDarkTheme = isDarkTheme,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 32.dp, vertical = 32.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun GamificationArc(
    progress: Float,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val arcBackground = if (isDarkTheme) JourneyArcBackgroundDark else JourneyArcBackground
    val arcFill = JourneyArcFill

    Canvas(modifier = modifier) {
        val strokeWidth = 12.dp.toPx()
        val radius = (size.minDimension - strokeWidth) / 2
        val center = Offset(size.width / 2, size.height / 2)

        // Arc angles: open at bottom (270 degree arc)
        val startAngle = 135f
        val sweepAngle = 270f

        // Background arc
        drawArc(
            color = arcBackground,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Progress arc
        if (progress > 0f) {
            val progressSweep = sweepAngle * progress.coerceIn(0f, 1f)
            drawArc(
                color = arcFill,
                startAngle = startAngle,
                sweepAngle = progressSweep,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
    }
}

@Composable
private fun StatBentoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    label: String,
    value: String,
    isLocked: Boolean,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val cardColor = when {
        isLocked && isDarkTheme -> JourneyStatCardLockedDark
        isLocked -> JourneyStatCardLockedLight
        isDarkTheme -> JourneyStatCardDark
        else -> JourneyStatCardLight
    }
    val borderColor = if (isDarkTheme) JourneyStatCardBorderDark else JourneyStatCardBorderLight
    val textPrimary = if (isDarkTheme) JourneyTextPrimaryDark else JourneyTextPrimaryLight
    val textSecondary = if (isDarkTheme) JourneyTextSecondaryDark else JourneyTextSecondaryLight
    val lockedTextColor = if (isDarkTheme) JourneyTextTertiaryDark else JourneyTextTertiaryLight

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(cardColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isLocked) lockedTextColor else iconTint,
            modifier = Modifier.size(28.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = value,
            style = TextStyle(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            color = if (isLocked) lockedTextColor else textPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label.uppercase(),
            style = TextStyle(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp,
                letterSpacing = 1.sp
            ),
            color = if (isLocked) lockedTextColor else textSecondary
        )
    }
}

// =============================================================================
// SCREEN 3: THE CONTRACT (Commitment)
// =============================================================================

@Composable
private fun ContractScreen(
    isDarkTheme: Boolean,
    onSignAndEnter: () -> Unit
) {
    val textPrimary = if (isDarkTheme) JourneyTextPrimaryDark else JourneyTextPrimaryLight
    val textSecondary = if (isDarkTheme) JourneyTextSecondaryDark else JourneyTextSecondaryLight
    val paperColor = if (isDarkTheme) JourneyContractPaperDark else JourneyContractPaperLight
    val inkColor = if (isDarkTheme) JourneyContractTextDark else JourneyContractTextLight
    val lineColor = if (isDarkTheme) JourneyContractLineDark else JourneyContractLineLight

    var userName by remember { mutableStateOf("") }
    var showSealAnimation by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Headline
            Text(
                text = "The Contract",
                style = TextStyle(
                    fontFamily = PlayfairFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    lineHeight = 36.sp
                ),
                color = textPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Paper-like card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(paperColor)
                    .padding(32.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Contract text
                    Text(
                        text = "I, ",
                        style = TextStyle(
                            fontFamily = PlayfairFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 18.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        ),
                        color = inkColor
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Name input with underline
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                    ) {
                        BasicTextField(
                            value = userName,
                            onValueChange = { userName = it },
                            textStyle = TextStyle(
                                fontFamily = PlayfairFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 20.sp,
                                color = inkColor,
                                textAlign = TextAlign.Center
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Placeholder
                        if (userName.isEmpty()) {
                            Text(
                                text = "[Your Name]",
                                style = TextStyle(
                                    fontFamily = PlayfairFamily,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 20.sp,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                ),
                                color = lineColor,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Underline
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .offset(y = 4.dp)
                                .height(1.dp)
                                .background(lineColor)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Promise text
                    Text(
                        text = "promise to dedicate 5 minutes a day to my future self.",
                        style = TextStyle(
                            fontFamily = PlayfairFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 18.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            lineHeight = 28.sp
                        ),
                        color = inkColor,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Seal/Checkmark area
                    AnimatedVisibility(
                        visible = showSealAnimation,
                        enter = fadeIn() + scaleIn(initialScale = 0.5f),
                        exit = fadeOut() + scaleOut()
                    ) {
                        WaxSealAnimation(
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        // Sign & Enter Button
        JourneyPrimaryButton(
            text = "Sign & Enter Prody",
            onClick = {
                coroutineScope.launch {
                    showSealAnimation = true
                    delay(1200) // Show seal animation for 1.2 seconds
                    onSignAndEnter()
                }
            },
            isDarkTheme = isDarkTheme,
            enabled = userName.isNotBlank(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 32.dp, vertical = 32.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun WaxSealAnimation(
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "sealScale"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Wax seal circle
        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(scale)
                .clip(CircleShape)
                .background(JourneySealGold),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Signed",
                tint = JourneyBackgroundLight,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

// =============================================================================
// SHARED COMPONENTS
// =============================================================================

@Composable
private fun JourneyPrimaryButton(
    text: String,
    onClick: () -> Unit,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val buttonColor = JourneyButtonPrimary
    val textColor = JourneyButtonTextOnPrimary
    val disabledButtonColor = if (isDarkTheme)
        JourneyButtonPrimary.copy(alpha = 0.3f)
    else
        JourneyButtonPrimary.copy(alpha = 0.4f)
    val disabledTextColor = textColor.copy(alpha = 0.5f)

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(8.dp), // Slightly boxy, premium feel
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            contentColor = textColor,
            disabledContainerColor = disabledButtonColor,
            disabledContentColor = disabledTextColor
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 2.dp
        )
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
        )
    }
}

@Composable
private fun JourneyPageIndicator(
    pageCount: Int,
    currentPage: Int,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val activeColor = JourneyIndicatorActive
    val inactiveColor = if (isDarkTheme) JourneyIndicatorInactiveDark else JourneyIndicatorInactiveLight

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        repeat(pageCount) { index ->
            val isActive = index == currentPage
            Box(
                modifier = Modifier
                    .size(
                        width = if (isActive) 24.dp else 8.dp,
                        height = 8.dp
                    )
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isActive) activeColor else inactiveColor)
            )
        }
    }
}
