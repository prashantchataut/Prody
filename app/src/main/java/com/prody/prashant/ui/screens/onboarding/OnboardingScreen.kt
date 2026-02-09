package com.prody.prashant.ui.screens.onboarding

import com.prody.prashant.ui.icons.ProdyIcons
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.prody.prashant.ui.theme.*
import kotlinx.coroutines.launch

// =============================================================================
// PRODY ONBOARDING - REVAMPED 2026
// =============================================================================

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState(pageCount = { 8 })
    val coroutineScope = rememberCoroutineScope()
    // Determine theme for background only if needed, but we mostly use specific colors
    // We will use the ProdyTheme colors.
    
    // Gradient Background: White to #F5F5F5 for light mode
    val gradientColors = if (!isSystemInDarkTheme()) {
        listOf(Color.White, Color(0xFFF5F5F5))
    } else {
        listOf(ProdyBackgroundDark, ProdyBackgroundDark) // Keep dark mode simple
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors))
            .systemBarsPadding()
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = false // Force navigation via buttons
        ) { page ->
            when (page) {
                0 -> WelcomeScreen(
                    onNext = { coroutineScope.launch { pagerState.animateScrollToPage(1) } },
                    onLogin = { coroutineScope.launch { pagerState.animateScrollToPage(7) } }
                )
                1 -> HavenOnboardingScreen(
                    onNext = { coroutineScope.launch { pagerState.animateScrollToPage(2) } }
                )
                2 -> JournalingScreen(
                    onNext = { coroutineScope.launch { pagerState.animateScrollToPage(3) } },
                    onSkip = { coroutineScope.launch { pagerState.animateScrollToPage(7) } }
                )
                3 -> GamificationLeaderboardScreen(
                    onNext = { coroutineScope.launch { pagerState.animateScrollToPage(4) } },
                    onSkip = { coroutineScope.launch { pagerState.animateScrollToPage(7) } }
                )
                4 -> GamificationXpScreen(
                    onNext = { coroutineScope.launch { pagerState.animateScrollToPage(5) } }
                )
                5 -> DailyWisdomScreen(
                    onNext = { coroutineScope.launch { pagerState.animateScrollToPage(6) } },
                    onSkip = { coroutineScope.launch { pagerState.animateScrollToPage(7) } }
                )
                6 -> InsightsScreen(
                    onNext = { coroutineScope.launch { pagerState.animateScrollToPage(7) } },
                    onSkip = { coroutineScope.launch { pagerState.animateScrollToPage(7) } }
                )
                7 -> LoginSignupScreen(
                    onLogin = {
                        viewModel.completeOnboarding()
                        onComplete()
                    },
                    onCreateAccount = {
                        viewModel.completeOnboarding()
                        onComplete()
                    },
                    onGoogleLogin = {
                        viewModel.completeOnboarding()
                        onComplete()
                    }
                )
            }
        }
    }
}

// =============================================================================
// SCREEN 1: WELCOME SCREEN
// =============================================================================

@Composable
private fun WelcomeScreen(
    onNext: () -> Unit,
    onLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo positioned at 25% from top
        Spacer(modifier = Modifier.fillMaxHeight(0.25f))

        ProdyLogo(modifier = Modifier.size(100.dp))

        Spacer(modifier = Modifier.height(32.dp))

        // Brand name "Prody" - Poppins SemiBold 32sp
        Text(
            text = "Prody",
            style = TextStyle(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 32.sp
            ),
            color = ProdyTextPrimaryLight // Always dark for contrast on light gradient
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tagline "Your mindful companion" - Poppins Regular 16sp
        Text(
            text = "Your mindful companion",
            style = TextStyle(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp
            ),
            color = ProdyTextSecondaryLight
        )

        Spacer(modifier = Modifier.weight(1f))

        // Progress Indicator
        ProdyProgressIndicator(currentPage = 0, totalPages = 8)

        Spacer(modifier = Modifier.height(48.dp))

        // Primary CTA Button: 56dp height, 16dp corner radius
        PrimaryButton(
            text = "Get Started",
            onClick = onNext,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Secondary link
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onLogin() }
        ) {
            Text(
                text = "Already have an account? ",
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                ),
                color = ProdyTextSecondaryLight
            )
            Text(
                text = "Log In",
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    textDecoration = TextDecoration.Underline
                ),
                color = ProdyForestGreen
            )
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
}

// =============================================================================
// FEATURE SCREENS
// =============================================================================

@Composable
private fun JournalingScreen(onNext: () -> Unit, onSkip: () -> Unit) {
    FeatureScreenLayout(
        title = "Reflect Daily",
        description = "Capture your thoughts and feelings in a safe, private space designed for clarity.",
        currentPage = 2,
        totalPages = 8,
        onNext = onNext,
        onSkip = onSkip
    ) {
        StandardFeatureCard {
            Icon(Icons.Outlined.Book, contentDescription = null, tint = ProdyForestGreen, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Journaling", style = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, fontSize = 20.sp))
            Text("Clear your mind.", style = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp), color = ProdyTextSecondaryLight)
        }
    }
}

@Composable
private fun GamificationLeaderboardScreen(onNext: () -> Unit, onSkip: () -> Unit) {
    FeatureScreenLayout(
        title = "Grow Together",
        description = "Join a community of mindful individuals. Track your progress and celebrate milestones.",
        currentPage = 3,
        totalPages = 8,
        onNext = onNext,
        onSkip = onSkip
    ) {
        StandardFeatureCard {
            Icon(Icons.Outlined.Leaderboard, contentDescription = null, tint = ProdyForestGreen, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Community", style = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, fontSize = 20.sp))
            Text("Inspire and be inspired.", style = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp), color = ProdyTextSecondaryLight)
        }
    }
}

@Composable
private fun GamificationXpScreen(onNext: () -> Unit) {
    FeatureScreenLayout(
        title = "Level Up",
        description = "Earn XP for every mindful action. Visualize your personal growth journey.",
        currentPage = 4,
        totalPages = 8,
        onNext = onNext,
        onSkip = onNext // Skip acts as next here
    ) {
        StandardFeatureCard {
            Icon(Icons.Outlined.Star, contentDescription = null, tint = ProdyWarmAmber, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Achievements", style = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, fontSize = 20.sp))
            Text("Celebrate your wins.", style = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp), color = ProdyTextSecondaryLight)
        }
    }
}

@Composable
private fun DailyWisdomScreen(onNext: () -> Unit, onSkip: () -> Unit) {
    FeatureScreenLayout(
        title = "Daily Wisdom",
        description = "Receive curated quotes, proverbs, and words to expand your perspective.",
        currentPage = 5,
        totalPages = 8,
        onNext = onNext,
        onSkip = onSkip
    ) {
        StandardFeatureCard {
            Icon(Icons.Outlined.Lightbulb, contentDescription = null, tint = ProdyForestGreen, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Insights", style = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, fontSize = 20.sp))
            Text("Wisdom for your day.", style = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp), color = ProdyTextSecondaryLight)
        }
    }
}

@Composable
private fun InsightsScreen(onNext: () -> Unit, onSkip: () -> Unit) {
    FeatureScreenLayout(
        title = "Personalized For You",
        description = "Prody learns from your entries to provide tailored guidance and feedback.",
        currentPage = 6,
        totalPages = 8,
        onNext = onNext,
        onSkip = onSkip
    ) {
        StandardFeatureCard {
            Icon(Icons.Outlined.AutoAwesome, contentDescription = null, tint = ProdyForestGreen, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("AI Companion", style = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.SemiBold, fontSize = 20.sp))
            Text("Understanding you better.", style = TextStyle(fontFamily = PoppinsFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp), color = ProdyTextSecondaryLight)
        }
    }
}


@Composable
private fun FeatureScreenLayout(
    title: String,
    description: String,
    currentPage: Int,
    totalPages: Int,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header with Skip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Skip",
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                ),
                color = ProdyTextSecondaryLight,
                modifier = Modifier.clickable { onSkip() }
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Main Content (Card)
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            content()
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Text Content
        Text(
            text = title,
            style = TextStyle(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp // Header text 24-32sp
            ),
            color = ProdyTextPrimaryLight,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = description,
            style = TextStyle(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 21.sp // 1.5 line height
            ),
            color = ProdyTextSecondaryLight,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ProdyProgressIndicator(currentPage = currentPage, totalPages = totalPages)

            FloatingActionButton(
                onClick = onNext,
                containerColor = ProdyForestGreen,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun StandardFeatureCard(
    content: @Composable ColumnScope.() -> Unit
) {
    // Card Design: 12dp corner radius, 8dp elevation with proper shadow
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.8f)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = Color(0x1A000000), // Soft shadow
                ambientColor = Color(0x1A000000)
            ),
        shape = RoundedCornerShape(12.dp),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            content()
        }
    }
}

// =============================================================================
// LOGIN / SIGNUP SCREEN
// =============================================================================

@Composable
private fun LoginSignupScreen(
    onLogin: () -> Unit,
    onCreateAccount: () -> Unit,
    onGoogleLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    val imeVisible = WindowInsets.ime.getBottom(density) > 0

    // Auto-scroll when keyboard appears
    LaunchedEffect(imeVisible) {
        if (imeVisible) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .imePadding() // Add IME padding to handle keyboard
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(64.dp))

        ProdyLogo(modifier = Modifier.size(80.dp))

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Welcome Back",
            style = TextStyle(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 28.sp
            ),
            color = ProdyTextPrimaryLight
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Email Input
        ProdyInputField(
            value = email,
            onValueChange = { email = it },
            label = "Email Address",
            placeholder = "Enter your email"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Input
        ProdyInputField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            placeholder = "Enter your password",
            isPassword = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Forgot Password
        Text(
            text = "Forgot Password?",
            style = TextStyle(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
            ),
            color = ProdyTextSecondaryLight,
            modifier = Modifier
                .align(Alignment.End)
                .clickable { }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Login Button
        PrimaryButton(
            text = "Log In",
            onClick = onLogin,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Divider
        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = ProdyOutlineLight)
            Text(
                text = "OR",
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp
                ),
                color = ProdyTextSecondaryLight,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = ProdyOutlineLight)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Social Login - Google Button
        // White background with black text, 24dp logo, 1dp border
        OutlinedButton(
            onClick = onGoogleLogin,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Google Logo Placeholder (Colored 'G' or similar)
                // Using a simple Canvas for G logo rep
                Canvas(modifier = Modifier.size(24.dp)) {
                     // Simple representation of Google colors
                     drawCircle(Color(0xFF4285F4), radius = 10.dp.toPx(), center = center)
                     drawCircle(Color.White, radius = 6.dp.toPx(), center = center)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Continue with Google",
                    style = TextStyle(
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Sign Up Link
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onCreateAccount() }
        ) {
            Text(
                text = "Don't have an account? ",
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                ),
                color = ProdyTextSecondaryLight
            )
            Text(
                text = "Sign Up",
                style = TextStyle(
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                ),
                color = ProdyForestGreen
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// =============================================================================
// SHARED COMPONENTS
// =============================================================================

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ProdyForestGreen,
            contentColor = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        )
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                letterSpacing = 0.5.sp
            )
        )
    }
}

@Composable
fun ProdyInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    isPassword: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            ),
            color = ProdyTextPrimaryLight,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ProdyForestGreen,
                unfocusedBorderColor = ProdyOutlineLight,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            placeholder = {
                Text(
                    text = placeholder,
                    style = TextStyle(
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp
                    ),
                    color = ProdyTextTertiaryLight
                )
            },
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            singleLine = true
        )
    }
}

@Composable
fun ProdyLogo(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .border(1.dp, ProdyOutlineLight, RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = ProdyIcons.Spa, // Using Spa icon as leaf/seedling
            contentDescription = null,
            tint = ProdyForestGreen,
            modifier = Modifier.fillMaxSize(0.6f)
        )
    }
}

@Composable
fun ProdyProgressIndicator(
    currentPage: Int,
    totalPages: Int,
    modifier: Modifier = Modifier
) {
    // Performance Optimization: Use a single Canvas to draw all indicator dots.
    // This avoids multiple Box allocations and layout passes when animating widths.
    val activeColor = ProdyForestGreen
    val inactiveColor = ProdyOutlineLight

    val animatedPageProgress = animateFloatAsState(
        targetValue = currentPage.toFloat(),
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "page_progress"
    )

    Canvas(
        modifier = modifier
            .height(4.dp)
            .width((totalPages * 8 + (totalPages - 1) * 4 + 16).dp)
    ) {
        val pageProgress = animatedPageProgress.value
        val spacing = 4.dp.toPx()
        val minWidth = 8.dp.toPx()
        val maxWidth = 24.dp.toPx()
        val height = 4.dp.toPx()

        var currentX = 0f
        repeat(totalPages) { index ->
            val distance = kotlin.math.abs(index - pageProgress)
            val activeFraction = (1f - distance).coerceIn(0f, 1f)

            val dotWidth = minWidth + (maxWidth - minWidth) * activeFraction

            // Manual color interpolation for precision
            val dotColor = Color(
                red = inactiveColor.red + (activeColor.red - inactiveColor.red) * activeFraction,
                green = inactiveColor.green + (activeColor.green - inactiveColor.green) * activeFraction,
                blue = inactiveColor.blue + (activeColor.blue - inactiveColor.blue) * activeFraction,
                alpha = inactiveColor.alpha + (activeColor.alpha - inactiveColor.alpha) * activeFraction
            )
            
            drawRoundRect(
                color = dotColor,
                topLeft = Offset(currentX, 0f),
                size = Size(dotWidth, height),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(height / 2)
            )
            currentX += dotWidth + spacing
        }
    }
}