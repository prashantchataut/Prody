package com.prody.prashant.ui.components.identity
import com.prody.prashant.ui.icons.ProdyIcons

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.domain.identity.*
import com.prody.prashant.ui.theme.PoppinsFamily

/**
 * [CustomizeProfile] - Profile customization components
 *
 * A comprehensive set of Compose components for customizing user profile:
 * - Profile loadouts (saved looks)
 * - Cosmetic selection (banner, frame, title, accent)
 * - Badge pinboard management
 *
 * Design Philosophy:
 * - "Closet" metaphor: switch between saved looks
 * - Quick preview of each loadout
 * - One-tap switching between loadouts
 * - Easy access to customize individual elements
 */

// ============================================================================
// Colors
// ============================================================================

private object CustomizeColors {
    val BackgroundDark = Color(0xFF0D2826)
    val CardDark = Color(0xFF1A3331)
    val CardElevatedDark = Color(0xFF223D3A)
    val AccentGreen = Color(0xFF36F97F)
    val TextPrimaryDark = Color(0xFFFFFFFF)
    val TextSecondaryDark = Color(0xFFB8C5C3)

    val BackgroundLight = Color(0xFFF5F8F7)
    val CardLight = Color(0xFFFFFFFF)
    val CardElevatedLight = Color(0xFFF0F5F4)
    val AccentGreenLight = Color(0xFF2ECC71)
    val TextPrimaryLight = Color(0xFF1A2B23)
    val TextSecondaryLight = Color(0xFF5A6B63)

    val QuietGold = Color(0xFFD4AF37)
}

// ============================================================================
// Loadout Selector
// ============================================================================

/**
 * Data for displaying a loadout.
 */
data class LoadoutDisplayData(
    val id: String,
    val name: String,
    val bannerColors: List<Color>,
    val frameColor: Color,
    val titleName: String,
    val accentColor: Color,
    val pinnedBadgeCount: Int,
    val isActive: Boolean
)

/**
 * Horizontal loadout selector showing 2-3 saved looks.
 */
@Composable
fun LoadoutSelector(
    loadouts: List<LoadoutDisplayData>,
    onLoadoutSelected: (String) -> Unit,
    onAddLoadout: () -> Unit,
    onEditLoadout: (String) -> Unit,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier,
    maxLoadouts: Int = ProfileLoadout.MAX_LOADOUTS
) {
    Column(modifier = modifier) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Your Looks",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = if (isDarkMode) CustomizeColors.TextPrimaryDark
                        else CustomizeColors.TextPrimaryLight
            )

            if (loadouts.size < maxLoadouts) {
                TextButton(onClick = onAddLoadout) {
                    Icon(
                        imageVector = ProdyIcons.Add,
                        contentDescription = "Add Loadout",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Add Look",
                        fontFamily = PoppinsFamily,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Loadout Cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            loadouts.forEach { loadout ->
                LoadoutCard(
                    loadout = loadout,
                    onSelect = { onLoadoutSelected(loadout.id) },
                    onEdit = { onEditLoadout(loadout.id) },
                    isDarkMode = isDarkMode
                )
            }
        }
    }
}

/**
 * Individual loadout card showing preview.
 */
@Composable
private fun LoadoutCard(
    loadout: LoadoutDisplayData,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    isDarkMode: Boolean
) {
    val borderColor by animateColorAsState(
        if (loadout.isActive) {
            if (isDarkMode) CustomizeColors.AccentGreen else CustomizeColors.AccentGreenLight
        } else {
            Color.Transparent
        },
        animationSpec = tween(200),
        label = "border"
    )

    val borderWidth by animateDpAsState(
        targetValue = if (loadout.isActive) 2.dp else 0.dp,
        animationSpec = tween(200),
        label = "border_width"
    )

    Card(
        modifier = Modifier
            .width(140.dp)
            .border(
                width = borderWidth,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onSelect() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) CustomizeColors.CardDark
                            else CustomizeColors.CardLight
        )
    ) {
        Column {
            // Banner Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = loadout.bannerColors.ifEmpty {
                                listOf(Color.Gray, Color.DarkGray)
                            }
                        )
                    )
            ) {
                // Active indicator
                if (loadout.isActive) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(CustomizeColors.AccentGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = ProdyIcons.Check,
                            contentDescription = "Active",
                            tint = Color.Black,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                // Edit button
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .size(28.dp)
                ) {
                    Icon(
                        imageVector = ProdyIcons.Edit,
                        contentDescription = "Edit",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = loadout.name,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = if (isDarkMode) CustomizeColors.TextPrimaryDark
                            else CustomizeColors.TextPrimaryLight,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = loadout.titleName,
                    fontFamily = PoppinsFamily,
                    fontSize = 12.sp,
                    color = if (isDarkMode) CustomizeColors.TextSecondaryDark
                            else CustomizeColors.TextSecondaryLight,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Badge count & accent preview
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Accent color dot
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(loadout.accentColor)
                    )

                    // Badge count
                    if (loadout.pinnedBadgeCount > 0) {
                        Text(
                            text = "${loadout.pinnedBadgeCount} badges",
                            fontFamily = PoppinsFamily,
                            fontSize = 10.sp,
                            color = if (isDarkMode) CustomizeColors.TextSecondaryDark
                                    else CustomizeColors.TextSecondaryLight
                        )
                    }
                }
            }
        }
    }
}

// ============================================================================
// Customization Sections
// ============================================================================

/**
 * A customization section with label and tap action.
 */
@Composable
fun CustomizeSection(
    title: String,
    currentValue: String,
    icon: ImageVector,
    onClick: () -> Unit,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier,
    rarity: CosmeticRarity? = null,
    previewColors: List<Color>? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) CustomizeColors.CardDark
                            else CustomizeColors.CardLight
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon or Preview
            if (previewColors != null && previewColors.size >= 2) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            brush = Brush.linearGradient(previewColors)
                        )
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isDarkMode) CustomizeColors.CardElevatedDark
                            else CustomizeColors.CardElevatedLight
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isDarkMode) CustomizeColors.TextSecondaryDark
                               else CustomizeColors.TextSecondaryLight,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Text content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = if (isDarkMode) CustomizeColors.TextPrimaryDark
                                else CustomizeColors.TextPrimaryLight
                    )

                    rarity?.let {
                        RarityDot(rarity = it)
                    }
                }

                Text(
                    text = currentValue,
                    fontFamily = PoppinsFamily,
                    fontSize = 13.sp,
                    color = if (isDarkMode) CustomizeColors.TextSecondaryDark
                            else CustomizeColors.TextSecondaryLight,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Chevron
            Icon(
                imageVector = ProdyIcons.ChevronRight,
                contentDescription = "Change",
                tint = if (isDarkMode) CustomizeColors.TextSecondaryDark
                       else CustomizeColors.TextSecondaryLight,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

// ============================================================================
// Badge Pinboard Component
// ============================================================================

/**
 * Data for a pinned badge display.
 */
data class PinnedBadgeDisplay(
    val id: String,
    val name: String,
    val iconId: String,
    val rarity: CosmeticRarity,
    val isSpecial: Boolean = false
)

/**
 * The hero badge pinboard showing 3-5 pinned badges.
 */
@Composable
fun BadgePinboardView(
    pinnedBadges: List<PinnedBadgeDisplay>,
    onBadgeTap: (String) -> Unit,
    onEditPinboard: () -> Unit,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier,
    maxBadges: Int = BadgePinboard.MAX_PINNED_BADGES
) {
    Column(modifier = modifier) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pinned Badges",
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = if (isDarkMode) CustomizeColors.TextPrimaryDark
                            else CustomizeColors.TextPrimaryLight
                )

                Text(
                    text = "${pinnedBadges.size}/$maxBadges",
                    fontFamily = PoppinsFamily,
                    fontSize = 13.sp,
                    color = if (isDarkMode) CustomizeColors.TextSecondaryDark
                            else CustomizeColors.TextSecondaryLight
                )
            }

            TextButton(onClick = onEditPinboard) {
                Icon(
                    imageVector = ProdyIcons.Edit,
                    contentDescription = "Edit",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Edit",
                    fontFamily = PoppinsFamily,
                    fontSize = 14.sp
                )
            }
        }

        // Badges
        if (pinnedBadges.isEmpty()) {
            // Empty state
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clickable { onEditPinboard() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDarkMode) CustomizeColors.CardDark
                                    else CustomizeColors.CardLight
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = ProdyIcons.WorkspacePremium,
                        contentDescription = null,
                        tint = if (isDarkMode) CustomizeColors.TextSecondaryDark
                               else CustomizeColors.TextSecondaryLight,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "Pin your achievements",
                        fontFamily = PoppinsFamily,
                        fontSize = 14.sp,
                        color = if (isDarkMode) CustomizeColors.TextSecondaryDark
                                else CustomizeColors.TextSecondaryLight
                    )
                    Text(
                        text = "Tap to choose badges to showcase",
                        fontFamily = PoppinsFamily,
                        fontSize = 12.sp,
                        color = if (isDarkMode) CustomizeColors.TextSecondaryDark.copy(alpha = 0.7f)
                                else CustomizeColors.TextSecondaryLight.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            // Badge row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                pinnedBadges.forEach { badge ->
                    PinnedBadgeChip(
                        badge = badge,
                        onClick = { onBadgeTap(badge.id) },
                        isDarkMode = isDarkMode
                    )
                }

                // Add more slot (if room)
                if (pinnedBadges.size < maxBadges) {
                    AddBadgeSlot(
                        onClick = onEditPinboard,
                        isDarkMode = isDarkMode
                    )
                }
            }
        }
    }
}

/**
 * Individual pinned badge chip.
 */
@Composable
private fun PinnedBadgeChip(
    badge: PinnedBadgeDisplay,
    onClick: () -> Unit,
    isDarkMode: Boolean
) {
    val rarityColor = when (badge.rarity) {
        CosmeticRarity.COMMON -> GalleryColors.RarityCommon
        CosmeticRarity.RARE -> GalleryColors.RarityRare
        CosmeticRarity.EPIC -> GalleryColors.RarityEpic
        CosmeticRarity.LEGENDARY -> GalleryColors.RarityLegendary
    }

    Card(
        modifier = Modifier
            .width(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) CustomizeColors.CardDark
                            else CustomizeColors.CardLight
        ),
        border = if (badge.rarity >= CosmeticRarity.EPIC) {
            CardDefaults.outlinedCardBorder().copy(
                brush = Brush.linearGradient(
                    listOf(rarityColor, rarityColor.copy(alpha = 0.5f))
                )
            )
        } else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Badge icon placeholder
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(rarityColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (badge.isSpecial) ProdyIcons.Stars
                                 else ProdyIcons.EmojiEvents,
                    contentDescription = null,
                    tint = rarityColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = badge.name,
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                color = if (isDarkMode) CustomizeColors.TextPrimaryDark
                        else CustomizeColors.TextPrimaryLight,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Add badge slot button.
 */
@Composable
private fun AddBadgeSlot(
    onClick: () -> Unit,
    isDarkMode: Boolean
) {
    Card(
        modifier = Modifier
            .width(80.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) CustomizeColors.CardDark.copy(alpha = 0.5f)
                            else CustomizeColors.CardLight.copy(alpha = 0.5f)
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isDarkMode) CustomizeColors.CardElevatedDark
                        else CustomizeColors.CardElevatedLight
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = ProdyIcons.Add,
                    contentDescription = "Add",
                    tint = if (isDarkMode) CustomizeColors.TextSecondaryDark
                           else CustomizeColors.TextSecondaryLight,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = "Add",
                fontFamily = PoppinsFamily,
                fontSize = 11.sp,
                color = if (isDarkMode) CustomizeColors.TextSecondaryDark
                        else CustomizeColors.TextSecondaryLight
            )
        }
    }
}

// ============================================================================
// Full Customize Profile Screen Content
// ============================================================================

/**
 * Full customize profile content that combines all sections.
 */
@Composable
fun CustomizeProfileContent(
    loadouts: List<LoadoutDisplayData>,
    currentBannerName: String,
    currentBannerColors: List<Color>,
    currentBannerRarity: CosmeticRarity,
    currentFrameName: String,
    currentFrameRarity: CosmeticRarity,
    currentTitleName: String,
    currentTitleRarity: CosmeticRarity,
    currentAccentName: String,
    currentAccentColor: Color,
    pinnedBadges: List<PinnedBadgeDisplay>,
    onLoadoutSelected: (String) -> Unit,
    onAddLoadout: () -> Unit,
    onEditLoadout: (String) -> Unit,
    onBannerClick: () -> Unit,
    onFrameClick: () -> Unit,
    onTitleClick: () -> Unit,
    onAccentClick: () -> Unit,
    onBadgeTap: (String) -> Unit,
    onEditPinboard: () -> Unit,
    isDarkMode: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Loadout Selector
        LoadoutSelector(
            loadouts = loadouts,
            onLoadoutSelected = onLoadoutSelected,
            onAddLoadout = onAddLoadout,
            onEditLoadout = onEditLoadout,
            isDarkMode = isDarkMode
        )

        // Divider
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 20.dp),
            color = if (isDarkMode) Color.White.copy(alpha = 0.1f)
                    else Color.Black.copy(alpha = 0.1f)
        )

        // Cosmetics Sections
        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Customize",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = if (isDarkMode) CustomizeColors.TextPrimaryDark
                        else CustomizeColors.TextPrimaryLight
            )

            CustomizeSection(
                title = "Banner",
                currentValue = currentBannerName,
                icon = ProdyIcons.Wallpaper,
                onClick = onBannerClick,
                isDarkMode = isDarkMode,
                rarity = currentBannerRarity,
                previewColors = currentBannerColors
            )

            CustomizeSection(
                title = "Frame",
                currentValue = currentFrameName,
                icon = ProdyIcons.FilterFrames,
                onClick = onFrameClick,
                isDarkMode = isDarkMode,
                rarity = currentFrameRarity
            )

            CustomizeSection(
                title = "Title",
                currentValue = currentTitleName,
                icon = ProdyIcons.Badge,
                onClick = onTitleClick,
                isDarkMode = isDarkMode,
                rarity = currentTitleRarity
            )

            CustomizeSection(
                title = "Accent Color",
                currentValue = currentAccentName,
                icon = ProdyIcons.Palette,
                onClick = onAccentClick,
                isDarkMode = isDarkMode,
                previewColors = listOf(currentAccentColor, currentAccentColor.copy(alpha = 0.5f))
            )
        }

        // Divider
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 20.dp),
            color = if (isDarkMode) Color.White.copy(alpha = 0.1f)
                    else Color.Black.copy(alpha = 0.1f)
        )

        // Badge Pinboard
        BadgePinboardView(
            pinnedBadges = pinnedBadges,
            onBadgeTap = onBadgeTap,
            onEditPinboard = onEditPinboard,
            isDarkMode = isDarkMode
        )
    }
}

// ============================================================================
// Header with Preview
// ============================================================================

/**
 * Header showing live preview of current loadout.
 */
@Composable
fun CustomizePreviewHeader(
    displayName: String,
    titleName: String,
    bannerColors: List<Color>,
    frameColor: Color,
    accentColor: Color,
    isDarkMode: Boolean,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        // Banner background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = bannerColors.ifEmpty {
                            listOf(Color.Gray, Color.DarkGray)
                        }
                    )
                )
        )

        // Content overlay
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            // Back button
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(
                    imageVector = ProdyIcons.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            // Avatar and name
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar with frame
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .border(
                            width = 3.dp,
                            color = frameColor,
                            shape = CircleShape
                        )
                        .background(Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = ProdyIcons.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Name with accent
                Text(
                    text = displayName,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White
                )

                // Title
                Text(
                    text = titleName,
                    fontFamily = PoppinsFamily,
                    fontSize = 14.sp,
                    color = accentColor
                )
            }
        }
    }
}
