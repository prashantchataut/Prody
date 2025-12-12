package com.prody.prashant.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.prody.prashant.ui.theme.CardShape

@Composable
fun ProdyCard(
    modifier: Modifier = Modifier,
    shape: Shape = CardShape,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    elevation: Dp = 1.dp,
    showBorder: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val borderModifier = if (showBorder) {
        Modifier.border(
            width = 1.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.12f),
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.06f)
                )
            ),
            shape = shape
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                clip = false,
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.06f),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            )
            .clip(shape)
            .then(borderModifier)
            .background(backgroundColor),
        content = content
    )
}
