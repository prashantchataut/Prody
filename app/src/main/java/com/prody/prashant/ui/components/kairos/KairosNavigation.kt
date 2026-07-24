package com.prody.prashant.ui.components.kairos

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.prody.prashant.ui.animation.KairosDurations
import com.prody.prashant.ui.animation.KairosEasing
import com.prody.prashant.ui.animation.rememberKairosReducedMotion
import com.prody.prashant.ui.navigation.BottomNavItem
import com.prody.prashant.ui.theme.KairosMotion
import com.prody.prashant.ui.theme.KairosRadius

@Composable
fun KairosBottomNavigation(
    items: List<BottomNavItem>,
    selectedRoute: String?,
    onSelect: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = 12.dp, end = 12.dp, bottom = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        KairosGlassSurface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(KairosRadius.navigation),
            strong = true,
            elevation = 10.dp,
            contentPadding = PaddingValues(5.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
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

@Composable
fun KairosNavigationRail(
    items: List<BottomNavItem>,
    selectedRoute: String?,
    onSelect: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(104.dp)
            .fillMaxHeight()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        KairosGlassSurface(
            modifier = Modifier.fillMaxHeight(),
            shape = RoundedCornerShape(KairosRadius.navigation),
            strong = true,
            elevation = 10.dp,
            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
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

@Composable
private fun KairosNavigationItem(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    vertical: Boolean = false
) {
    val container by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.surface else Color.Transparent,
        animationSpec = tween(KairosMotion.quick),
        label = "navigation-container"
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(KairosMotion.quick),
        label = "navigation-content"
    )

    val reducedMotion = rememberKairosReducedMotion()
    val iconScale by animateFloatAsState(
        targetValue = if (selected && !reducedMotion) 1.08f else 1f,
        animationSpec = tween(KairosDurations.State, easing = KairosEasing.EaseOutQuart),
        label = "navigation-icon-scale"
    )

    Surface(
        onClick = onClick,
        modifier = modifier
            .semantics {
                this.selected = selected
                role = Role.Tab
            },
        shape = RoundedCornerShape(if (vertical) 20.dp else 18.dp),
        color = container,
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
                    modifier = Modifier
                        .size(21.dp)
                        .graphicsLayer { scaleX = iconScale; scaleY = iconScale }
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
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(
                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                    contentDescription = stringResource(item.contentDescriptionResId),
                    modifier = Modifier
                        .size(21.dp)
                        .graphicsLayer { scaleX = iconScale; scaleY = iconScale }
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
