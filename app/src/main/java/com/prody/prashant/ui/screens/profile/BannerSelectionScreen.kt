package com.prody.prashant.ui.screens.profile
import com.prody.prashant.ui.icons.ProdyIcons

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import com.prody.prashant.ui.theme.isDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import com.prody.prashant.R
import com.prody.prashant.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Banner Selection Screen
 *
 * Allows users to select and customize their profile banner.
 * Features category tabs and locked/unlocked banner states.
 */

// Design System Colors for Banner Selection
private object BannerColors {
    // Dark Mode
    val BackgroundDark = Color(0xFF0D2826)
    val CardBackgroundDark = Color(0xFF1A3331)
    val CardBackgroundElevatedDark = Color(0xFF223D3A)
    val AccentGreen = Color(0xFF36F97F)
    val TextPrimaryDark = Color(0xFFFFFFFF)
    val TextSecondaryDark = Color(0xFFB8C5C3)
    val TextTertiaryDark = Color(0xFF6B7F7C)
    val BorderDark = Color(0xFF2A4744)

    // Light Mode
    val BackgroundLight = Color(0xFFF5F8F7)
    val CardBackgroundLight = Color(0xFFFFFFFF)
    val CardBackgroundElevatedLight = Color(0xFFF0F5F4)
    val AccentGreenLight = Color(0xFF2ECC71)
    val TextPrimaryLight = Color(0xFF1A2B23)
    val TextSecondaryLight = Color(0xFF5A6B63)
    val TextTertiaryLight = Color(0xFF8A9B93)
    val BorderLight = Color(0xFFE0E8E4)

    // Locked state
    val LockedOverlay = Color(0x99000000)
    val LockedIcon = Color(0xFF6B7F7C)
}

@Composable
fun BannerSelectionScreen(
    onNavigateBack: () -> Unit,
    viewModel: BannerSelectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDarkMode = isDarkTheme()

    // Handle save success
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            delay(300)
            onNavigateBack()
        }
    }

    // Error snackbar
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            delay(3000)
            viewModel.clearError()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (isDarkMode) BannerColors.BackgroundDark
                else BannerColors.BackgroundLight
            )
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = if (isDarkMode) BannerColors.AccentGreen
                        else BannerColors.AccentGreenLight
            )
        } else {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                BannerSelectionHeader(
                    onBackClick = onNavigateBack,
                    isDarkMode = isDarkMode
                )

                // Preview Banner
                BannerPreview(
                    banner = uiState.banners.find { it.id == uiState.selectedBannerId },
                    isDarkMode = isDarkMode
                )

                // Category Tabs
                CategoryTabs(
                    selectedCategory = uiState.selectedCategory,
                    onCategorySelected = { viewModel.selectCategory(it) },
                    isDarkMode = isDarkMode
                )

                // Banner Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        top = 16.dp,
                        bottom = 120.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(uiState.filteredBanners) { banner ->
                        BannerOptionCard(
                            banner = banner,
                            isSelected = banner.id == uiState.selectedBannerId,
                            onClick = { viewModel.selectBanner(banner.id) },
                            isDarkMode = isDarkMode
                        )
                    }
                }
            }

            // Error message
            AnimatedVisibility(
                visible = uiState.error != null,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 80.dp)
            ) {
                uiState.error?.let { error ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        modifier = Modifier.padding(horizontal = 24.dp)
                    ) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(16.dp),
                            fontFamily = PoppinsFamily,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Apply Button
            ApplyBannerButton(
                onClick = { viewModel.saveBanner() },
                enabled = !uiState.isSaving && uiState.hasChanges,
                isLoading = uiState.isSaving,
                isDarkMode = isDarkMode,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            )
        }
    }
}

@Composable
private fun BannerSelectionHeader(
    onBackClick: () -> Unit,
    isDarkMode: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = ProdyIcons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back),
                tint = if (isDarkMode) BannerColors.TextPrimaryDark
                       else BannerColors.TextPrimaryLight,
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            text = "Profile Banner",
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = if (isDarkMode) BannerColors.TextPrimaryDark
                    else BannerColors.TextPrimaryLight
        )

        // Spacer for alignment
        Spacer(modifier = Modifier.size(48.dp))
    }
}

@Composable
private fun BannerPreview(
    banner: BannerOption?,
    isDarkMode: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .height(120.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        banner?.primaryColor ?: Color.Gray,
                        banner?.secondaryColor ?: Color.DarkGray
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Preview",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
            Text(
                text = banner?.name ?: "Select Banner",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White
            )
        }
    }
}

@Composable
private fun CategoryTabs(
    selectedCategory: BannerCategory,
    onCategorySelected: (BannerCategory) -> Unit,
    isDarkMode: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        BannerCategory.entries.forEach { category ->
            CategoryTab(
                category = category,
                isSelected = category == selectedCategory,
                onClick = { onCategorySelected(category) },
                isDarkMode = isDarkMode
            )
        }
    }
}

@Composable
private fun CategoryTab(
    category: BannerCategory,
    isSelected: Boolean,
    onClick: () -> Unit,
    isDarkMode: Boolean
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            if (isDarkMode) BannerColors.AccentGreen
            else BannerColors.AccentGreenLight
        } else {
            if (isDarkMode) BannerColors.CardBackgroundDark
            else BannerColors.CardBackgroundLight
        },
        animationSpec = tween(200),
        label = "tab_bg"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) {
            Color.Black
        } else {
            if (isDarkMode) BannerColors.TextSecondaryDark
            else BannerColors.TextSecondaryLight
        },
        animationSpec = tween(200),
        label = "tab_text"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = category.displayName,
            fontFamily = PoppinsFamily,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            fontSize = 14.sp,
            color = textColor
        )
    }
}

@Composable
private fun BannerOptionCard(
    banner: BannerOption,
    isSelected: Boolean,
    onClick: () -> Unit,
    isDarkMode: Boolean
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "banner_scale"
    )

    val borderWidth by animateDpAsState(
        targetValue = if (isSelected) 3.dp else 0.dp,
        animationSpec = tween(200),
        label = "banner_border"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.5f)
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = borderWidth,
                color = if (isDarkMode) BannerColors.AccentGreen
                        else BannerColors.AccentGreenLight,
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(banner.primaryColor, banner.secondaryColor)
                )
            )
            .clickable(enabled = !banner.isLocked) { onClick() }
    ) {
        // Banner Name
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.4f))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = banner.name,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Locked Overlay
        if (banner.isLocked) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BannerColors.LockedOverlay),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = ProdyIcons.Lock,
                        contentDescription = "Locked",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    banner.unlockRequirement?.let { requirement ->
                        Text(
                            text = requirement,
                            fontFamily = PoppinsFamily,
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Selected Checkmark
        if (isSelected && !banner.isLocked) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        if (isDarkMode) BannerColors.AccentGreen
                        else BannerColors.AccentGreenLight
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = ProdyIcons.Check,
                    contentDescription = "Selected",
                    tint = Color.Black,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun ApplyBannerButton(
    onClick: () -> Unit,
    enabled: Boolean,
    isLoading: Boolean,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(55.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(
                if (enabled) {
                    if (isDarkMode) BannerColors.AccentGreen
                    else BannerColors.AccentGreenLight
                } else {
                    if (isDarkMode) BannerColors.AccentGreen.copy(alpha = 0.3f)
                    else BannerColors.AccentGreenLight.copy(alpha = 0.3f)
                }
            )
            .clickable(enabled = enabled && !isLoading) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.Black,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = ProdyIcons.Wallpaper,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Apply Banner",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }
        }
    }
}
