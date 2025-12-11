package com.prody.prashant.ui.screens.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.prody.prashant.R
import com.prody.prashant.ui.theme.*
import kotlinx.coroutines.launch

data class OnboardingPage(
    val icon: ImageVector,
    val titleResId: Int,
    val descriptionResId: Int,
    val backgroundColor: Color,
    val iconColor: Color
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val pages = listOf(
        OnboardingPage(
            icon = Icons.Filled.AutoAwesome,
            titleResId = R.string.onboarding_welcome_title,
            descriptionResId = R.string.onboarding_welcome_desc,
            backgroundColor = ProdyPrimary,
            iconColor = ProdyTertiary
        ),
        OnboardingPage(
            icon = Icons.Filled.Lightbulb,
            titleResId = R.string.onboarding_wisdom_title,
            descriptionResId = R.string.onboarding_wisdom_desc,
            backgroundColor = ProdyPrimaryVariant,
            iconColor = MoodMotivated
        ),
        OnboardingPage(
            icon = Icons.Filled.SelfImprovement,
            titleResId = R.string.onboarding_journal_title,
            descriptionResId = R.string.onboarding_journal_desc,
            backgroundColor = ProdyPrimary,
            iconColor = MoodCalm
        ),
        OnboardingPage(
            icon = Icons.Filled.Schedule,
            titleResId = R.string.onboarding_future_title,
            descriptionResId = R.string.onboarding_future_desc,
            backgroundColor = ProdyPrimaryVariant,
            iconColor = MoodExcited
        ),
        OnboardingPage(
            icon = Icons.AutoMirrored.Filled.TrendingUp,
            titleResId = R.string.onboarding_growth_title,
            descriptionResId = R.string.onboarding_growth_desc,
            backgroundColor = ProdyPrimary,
            iconColor = AchievementUnlocked
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()
    val isLastPage = pagerState.currentPage == pages.size - 1

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        pages[pagerState.currentPage].backgroundColor,
                        pages[pagerState.currentPage].backgroundColor.copy(alpha = 0.8f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            // Skip button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                AnimatedVisibility(
                    visible = !isLastPage,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    TextButton(
                        onClick = {
                            viewModel.completeOnboarding()
                            onComplete()
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.skip),
                            color = Color.White.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }

            // Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                OnboardingPageContent(
                    page = pages[page],
                    isCurrentPage = page == pagerState.currentPage
                )
            }

            // Bottom section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Page indicators
                Row(
                    modifier = Modifier.padding(bottom = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    pages.forEachIndexed { index, _ ->
                        PageIndicator(
                            isSelected = index == pagerState.currentPage
                        )
                    }
                }

                // Navigation buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button
                    AnimatedVisibility(
                        visible = pagerState.currentPage > 0,
                        enter = fadeIn() + slideInHorizontally(),
                        exit = fadeOut() + slideOutHorizontally()
                    ) {
                        OutlinedButton(
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.White
                            ),
                            border = BorderStroke(
                                width = 1.dp,
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color.White.copy(alpha = 0.5f), Color.White.copy(alpha = 0.5f))
                                )
                            ),
                            modifier = Modifier.height(56.dp)
                        ) {
                            Text(stringResource(R.string.back))
                        }
                    }

                    if (pagerState.currentPage == 0) {
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    // Next/Get Started button
                    Button(
                        onClick = {
                            if (isLastPage) {
                                viewModel.completeOnboarding()
                                onComplete()
                            } else {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = pages[pagerState.currentPage].backgroundColor
                        ),
                        modifier = Modifier
                            .height(56.dp)
                            .then(
                                if (isLastPage) Modifier.fillMaxWidth(0.7f)
                                else Modifier
                            ),
                        shape = ButtonShape
                    ) {
                        Text(
                            text = if (isLastPage) stringResource(R.string.get_started)
                            else stringResource(R.string.next),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (!isLastPage) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(
    page: OnboardingPage,
    isCurrentPage: Boolean
) {
    val animatedScale by animateFloatAsState(
        targetValue = if (isCurrentPage) 1f else 0.8f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "icon_animation")
    val iconScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "icon_pulse"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .scale(animatedScale),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon with animated background
        Box(
            modifier = Modifier
                .size(160.dp)
                .scale(iconScale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            page.iconColor.copy(alpha = 0.3f),
                            Color.White.copy(alpha = 0.1f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = page.icon,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Title
        Text(
            text = stringResource(page.titleResId),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text(
            text = stringResource(page.descriptionResId),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.9f),
            textAlign = TextAlign.Center,
            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.3
        )
    }
}

@Composable
private fun PageIndicator(
    isSelected: Boolean
) {
    val width by animateDpAsState(
        targetValue = if (isSelected) 32.dp else 8.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "indicator_width"
    )

    Box(
        modifier = Modifier
            .height(8.dp)
            .width(width)
            .clip(CircleShape)
            .background(
                if (isSelected) Color.White
                else Color.White.copy(alpha = 0.3f)
            )
    )
}
