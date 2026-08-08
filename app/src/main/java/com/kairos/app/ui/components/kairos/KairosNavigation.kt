package com.kairos.app.ui.components.kairos

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kairos.app.ui.navigation.BottomNavItem
import com.kairos.app.ui.theme.KairosMotion
import com.kairos.app.ui.theme.KairosTheme

private val NavCapsuleShape = RoundedCornerShape(28.dp)
private val NavItemShape = RoundedCornerShape(16.dp)
private val RailShape = RoundedCornerShape(24.dp)

/**
 * Liquid-glass floating navigation capsule.
 *
 * A translucent warm fill with a bright top edge, hairline border, soft tinted
 * shadow, and a gentle top sheen. The selected destination sits on a soft
 * accent-wash pill with its filled icon. Translucency carries the glass effect
 * on every API level (real backdrop blur would need platform-specific hacks).
 */
@Composable
fun KairosBottomNavigation(
    items: List<BottomNavItem>,
    selectedRoute: String?,
    onSelect: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val glass = KairosTheme.liquidGlass
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        LiquidGlassCapsule(
            shape = NavCapsuleShape,
            fill = glass.fill,
            fillDeep = glass.fillDeep,
            border = glass.border,
            highlight = glass.highlight,
            sheen = glass.sheen,
            shadow = glass.shadow,
            elevation = 16.dp,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 640.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val selected = selectedRoute == item.destinationRoute
                    KairosNavigationItem(
                        item = item,
                        selected = selected,
                        onClick = { onSelect(item) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/**
 * Liquid-glass navigation rail for expanded widths (tablets).
 */
@Composable
fun KairosNavigationRail(
    items: List<BottomNavItem>,
    selectedRoute: String?,
    onSelect: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val glass = KairosTheme.liquidGlass
    Box(
        modifier = modifier
            .width(112.dp)
            .fillMaxHeight()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 10.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        LiquidGlassCapsule(
            shape = RailShape,
            fill = glass.fill,
            fillDeep = glass.fillDeep,
            border = glass.border,
            highlight = glass.highlight,
            sheen = glass.sheen,
            shadow = glass.shadow,
            elevation = 14.dp,
            modifier = Modifier
                .fillMaxHeight()
                .width(92.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 14.dp, horizontal = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                items.forEach { item ->
                    val selected = selectedRoute == item.destinationRoute
                    KairosNavigationItem(
                        item = item,
                        selected = selected,
                        onClick = { onSelect(item) },
                        vertical = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

/**
 * Shared glass capsule: shadow, translucent vertical fill, hairline border with
 * a bright top edge, and a specular top sheen.
 */
@Composable
private fun LiquidGlassCapsule(
    shape: RoundedCornerShape,
    fill: Color,
    fillDeep: Color,
    border: Color,
    highlight: Color,
    sheen: Color,
    shadow: Color,
    elevation: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = shadow,
                spotColor = shadow
            )
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(fill, fillDeep),
                    startY = 0f,
                    endY = 1200f
                )
            )
            .border(
                BorderStroke(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(highlight, border)
                    )
                ),
                shape = shape
            )
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(shape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(sheen, Color.Transparent),
                        startY = 0f,
                        endY = 220f
                    )
                )
        )
        content()
    }
}

@Composable
private fun KairosNavigationItem(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    vertical: Boolean = false
) {
    val scheme = MaterialTheme.colorScheme
    val pillColor by animateColorAsState(
        targetValue = if (selected) scheme.primaryContainer.copy(alpha = 0.92f) else Color.Transparent,
        animationSpec = tween(KairosMotion.state),
        label = "navigation-pill"
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) scheme.onPrimaryContainer else scheme.onSurfaceVariant,
        animationSpec = tween(KairosMotion.quick),
        label = "navigation-content"
    )

    Surface(
        onClick = onClick,
        modifier = modifier
            .semantics {
                this.selected = selected
                role = Role.Tab
            },
        shape = NavItemShape,
        color = pillColor,
        contentColor = contentColor
    ) {
        if (vertical) {
            Column(
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                    contentDescription = null,
                    modifier = Modifier.size(21.dp)
                )
                Text(
                    text = stringResource(item.labelResId),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                    maxLines = 1
                )
            }
        } else {
            Column(
                modifier = Modifier.padding(vertical = 7.dp, horizontal = 2.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(
                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                    contentDescription = stringResource(item.contentDescriptionResId),
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = stringResource(item.labelResId),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                    maxLines = 1
                )
            }
        }
    }
}
