package com.prody.prashant.ui.components.identity
import com.prody.prashant.ui.icons.ProdyIcons

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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.domain.identity.CosmeticRarity
import com.prody.prashant.ui.theme.PoppinsFamily

/**
 * [CosmeticsGallery] - Premium cosmetics gallery components
 *
 * A collection of Compose components for displaying cosmetics with
 * premium store UX - even though everything is free/earned.
 *
 * Design Philosophy:
 * - Every item feels premium
 * - Rarity badges add prestige without being childish
 * - Locked items show what's possible, not what's missing
 * - Subtle animations for legendary/epic items
 */

// ============================================================================
// Gallery Colors
// ============================================================================

object GalleryColors {
    // Backgrounds
    val BackgroundDark = Color(0xFF0D2826)
    val CardDark = Color(0xFF1A3331)
    val CardElevatedDark = Color(0xFF223D3A)

    val BackgroundLight = Color(0xFFF5F8F7)
    val CardLight = Color(0xFFFFFFFF)
    val CardElevatedLight = Color(0xFFF0F5F4)

    // Accent
    val AccentGreen = Color(0xFF36F97F)
    val AccentGreenLight = Color(0xFF2ECC71)

    // Text
    val TextPrimaryDark = Color(0xFFFFFFFF)
    val TextSecondaryDark = Color(0xFFB8C5C3)
    val TextPrimaryLight = Color(0xFF1A2B23)
    val TextSecondaryLight = Color(0xFF5A6B63)

    // Rarity colors
    val RarityCommon = Color(0xFF9E9E9E)
    val RarityRare = Color(0xFF42A5F5)
    val RarityEpic = Color(0xFF9C27B0)
    val RarityLegendary = Color(0xFFD4AF37)

    // States
    val LockedOverlay = Color(0x99000000)
    val SelectedBorder = Color(0xFF36F97F)
}

// ============================================================================
// Rarity Badge Component
// ============================================================================

/**
 * Displays a cosmetic rarity badge with appropriate styling.
 * Uses understated design - no glow spam or childish effects.
 */
@Composable
fun RarityBadge(
    rarity: CosmeticRarity,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true
) {
    val color = when (rarity) {
        CosmeticRarity.COMMON -> GalleryColors.RarityCommon
        CosmeticRarity.RARE -> GalleryColors.RarityRare
        CosmeticRarity.EPIC -> GalleryColors.RarityEpic
        CosmeticRarity.LEGENDARY -> GalleryColors.RarityLegendary
    }

    // Subtle shimmer for legendary items
    val shimmerAlpha by if (rarity == CosmeticRarity.LEGENDARY) {
        rememberInfiniteTransition(label = "shimmer").animateFloat(
            initialValue = 0.8f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(3000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "shimmer_alpha"
        )
    } else {
        remember { mutableFloatStateOf(1f) }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.15f * shimmerAlpha))
            .border(
                width = 1.dp,
                color = color.copy(alpha = 0.5f),
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        if (showLabel) {
            Text(
                text = rarity.displayName,
                fontFamily = PoppinsFamily,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = color,
                letterSpacing = 0.5.sp
            )
        }
    }
}

/**
 * Compact rarity indicator (just a colored dot).
 */
@Composable
fun RarityDot(
    rarity: CosmeticRarity,
    modifier: Modifier = Modifier,
    size: Int = 8
) {
    val color = when (rarity) {
        CosmeticRarity.COMMON -> GalleryColors.RarityCommon
        CosmeticRarity.RARE -> GalleryColors.RarityRare
        CosmeticRarity.EPIC -> GalleryColors.RarityEpic
        CosmeticRarity.LEGENDARY -> GalleryColors.RarityLegendary
    }

    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(color)
    )
}

// ============================================================================
// Category Filter Tabs
// ============================================================================

/**
 * Horizontal filter tabs for cosmetic categories.
 */
@Composable
fun <T : Enum<T>> CategoryFilterTabs(
    categories: Array<T>,
    selectedCategory: T,
    onCategorySelected: (T) -> Unit,
    categoryLabel: (T) -> String,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        categories.forEach { category ->
            FilterTab(
                label = categoryLabel(category),
                isSelected = category == selectedCategory,
                onClick = { onCategorySelected(category) },
                isDarkMode = isDarkMode
            )
        }
    }
}

@Composable
private fun FilterTab(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    isDarkMode: Boolean
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) {
            if (isDarkMode) GalleryColors.AccentGreen else GalleryColors.AccentGreenLight
        } else {
            if (isDarkMode) GalleryColors.CardDark else GalleryColors.CardLight
        },
        animationSpec = tween(200),
        label = "tab_bg"
    )

    val textColor by animateColorAsState(
        targetValue = if (isSelected) {
            Color.Black
        } else {
            if (isDarkMode) GalleryColors.TextSecondaryDark else GalleryColors.TextSecondaryLight
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
            text = label,
            fontFamily = PoppinsFamily,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            fontSize = 14.sp,
            color = textColor
        )
    }
}

// ============================================================================
// Gallery Item Cards
// ============================================================================

/**
 * Data class representing a cosmetic item for display.
 */
data class GalleryItem(
    val id: String,
    val name: String,
    val description: String,
    val rarity: CosmeticRarity,
    val isUnlocked: Boolean,
    val isSelected: Boolean,
    val unlockRequirement: String?,
    val gradientColors: List<Color>? = null,
    val iconVector: ImageVector? = null,
    val isSpecial: Boolean = false,
    val isAnimated: Boolean = false
)

/**
 * A gallery card for cosmetic items with rarity badge and lock state.
 */
@Composable
fun GalleryItemCard(
    item: GalleryItem,
    onClick: () -> Unit,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier,
    aspectRatio: Float = 1.5f
) {
    val scale by animateFloatAsState(
        targetValue = if (item.isSelected) 1.02f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    val borderWidth by animateDpAsState(
        targetValue = if (item.isSelected) 3.dp else 0.dp,
        animationSpec = tween(200),
        label = "border"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = borderWidth,
                color = GalleryColors.SelectedBorder,
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                brush = if (item.gradientColors != null && item.gradientColors.size >= 2) {
                    Brush.horizontalGradient(item.gradientColors)
                } else {
                    Brush.horizontalGradient(
                        listOf(
                            if (isDarkMode) GalleryColors.CardDark else GalleryColors.CardLight,
                            if (isDarkMode) GalleryColors.CardElevatedDark else GalleryColors.CardElevatedLight
                        )
                    )
                }
            )
            .clickable(enabled = item.isUnlocked) { onClick() }
    ) {
        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top row: Rarity badge + Special indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                RarityBadge(rarity = item.rarity)

                if (item.isSpecial) {
                    Icon(
                        imageVector = ProdyIcons.Stars,
                        contentDescription = "Special",
                        tint = GalleryColors.RarityLegendary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Bottom row: Name
            Column {
                Text(
                    text = item.name,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (item.description.isNotBlank()) {
                    Text(
                        text = item.description,
                        fontFamily = PoppinsFamily,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Locked Overlay
        if (!item.isUnlocked) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(GalleryColors.LockedOverlay),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = ProdyIcons.Lock,
                        contentDescription = "Locked",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    item.unlockRequirement?.let { req ->
                        Text(
                            text = req,
                            fontFamily = PoppinsFamily,
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }
        }

        // Selected Checkmark
        if (item.isSelected && item.isUnlocked) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(GalleryColors.AccentGreen),
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

// ============================================================================
// Gallery Grid
// ============================================================================

/**
 * A grid display of gallery items.
 */
@Composable
fun GalleryGrid(
    items: List<GalleryItem>,
    onItemClick: (String) -> Unit,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier,
    columns: Int = 2
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 16.dp,
            bottom = 120.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        items(items, key = { it.id }) { item ->
            GalleryItemCard(
                item = item,
                onClick = { onItemClick(item.id) },
                isDarkMode = isDarkMode
            )
        }
    }
}

// ============================================================================
// Horizontal Gallery Row (for preview/featured)
// ============================================================================

/**
 * Horizontal scrolling gallery for featured items.
 */
@Composable
fun FeaturedGalleryRow(
    title: String,
    items: List<GalleryItem>,
    onItemClick: (String) -> Unit,
    onSeeAllClick: () -> Unit,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = if (isDarkMode) GalleryColors.TextPrimaryDark
                        else GalleryColors.TextPrimaryLight
            )

            TextButton(onClick = onSeeAllClick) {
                Text(
                    text = "See All",
                    fontFamily = PoppinsFamily,
                    fontSize = 14.sp,
                    color = if (isDarkMode) GalleryColors.AccentGreen
                            else GalleryColors.AccentGreenLight
                )
            }
        }

        // Horizontal List
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items, key = { it.id }) { item ->
                GalleryItemCard(
                    item = item,
                    onClick = { onItemClick(item.id) },
                    isDarkMode = isDarkMode,
                    modifier = Modifier.width(160.dp),
                    aspectRatio = 1.2f
                )
            }
        }
    }
}

// ============================================================================
// Section Header
// ============================================================================

/**
 * Section header for gallery sections (Unlocked/Locked).
 */
@Composable
fun GallerySectionHeader(
    title: String,
    count: Int,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                tint = if (isDarkMode) GalleryColors.TextSecondaryDark
                       else GalleryColors.TextSecondaryLight,
                modifier = Modifier.size(18.dp)
            )
        }

        Text(
            text = title,
            fontFamily = PoppinsFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = if (isDarkMode) GalleryColors.TextSecondaryDark
                    else GalleryColors.TextSecondaryLight
        )

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(
                    if (isDarkMode) GalleryColors.CardDark
                    else GalleryColors.CardLight
                )
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = count.toString(),
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = if (isDarkMode) GalleryColors.TextSecondaryDark
                        else GalleryColors.TextSecondaryLight
            )
        }
    }
}

// ============================================================================
// Item Detail Modal
// ============================================================================

/**
 * Detail view for a cosmetic item.
 */
@Composable
fun GalleryItemDetail(
    item: GalleryItem,
    onSelect: () -> Unit,
    onDismiss: () -> Unit,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) GalleryColors.CardDark
                            else GalleryColors.CardLight
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = if (item.gradientColors != null && item.gradientColors.size >= 2) {
                            Brush.horizontalGradient(item.gradientColors)
                        } else {
                            Brush.horizontalGradient(
                                listOf(Color.Gray, Color.DarkGray)
                            )
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (item.iconVector != null) {
                    Icon(
                        imageVector = item.iconVector,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            // Name & Rarity
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = item.name,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = if (isDarkMode) GalleryColors.TextPrimaryDark
                            else GalleryColors.TextPrimaryLight
                )

                RarityBadge(rarity = item.rarity)
            }

            // Description
            Text(
                text = item.description,
                fontFamily = PoppinsFamily,
                fontSize = 14.sp,
                color = if (isDarkMode) GalleryColors.TextSecondaryDark
                        else GalleryColors.TextSecondaryLight,
                textAlign = TextAlign.Center
            )

            // Unlock Requirement (if locked)
            if (!item.isUnlocked && item.unlockRequirement != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isDarkMode) GalleryColors.CardElevatedDark
                            else GalleryColors.CardElevatedLight
                        )
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = ProdyIcons.Lock,
                        contentDescription = null,
                        tint = if (isDarkMode) GalleryColors.TextSecondaryDark
                               else GalleryColors.TextSecondaryLight,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = item.unlockRequirement,
                        fontFamily = PoppinsFamily,
                        fontSize = 14.sp,
                        color = if (isDarkMode) GalleryColors.TextSecondaryDark
                                else GalleryColors.TextSecondaryLight
                    )
                }
            }

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Close",
                        fontFamily = PoppinsFamily
                    )
                }

                Button(
                    onClick = onSelect,
                    modifier = Modifier.weight(1f),
                    enabled = item.isUnlocked,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDarkMode) GalleryColors.AccentGreen
                                        else GalleryColors.AccentGreenLight
                    )
                ) {
                    Text(
                        text = if (item.isSelected) "Selected" else "Select",
                        fontFamily = PoppinsFamily,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

// ============================================================================
// Empty State
// ============================================================================

/**
 * Empty state when no items match filter.
 */
@Composable
fun GalleryEmptyState(
    message: String,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = ProdyIcons.Inventory2,
            contentDescription = null,
            tint = if (isDarkMode) GalleryColors.TextSecondaryDark
                   else GalleryColors.TextSecondaryLight,
            modifier = Modifier.size(48.dp)
        )

        Text(
            text = message,
            fontFamily = PoppinsFamily,
            fontSize = 16.sp,
            color = if (isDarkMode) GalleryColors.TextSecondaryDark
                    else GalleryColors.TextSecondaryLight,
            textAlign = TextAlign.Center
        )
    }
}
