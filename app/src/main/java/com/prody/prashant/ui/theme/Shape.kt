package com.prody.prashant.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val ProdyShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

// Custom shapes for specific use cases
val CardShape = RoundedCornerShape(16.dp)
val BottomSheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
val ButtonShape = RoundedCornerShape(12.dp)
val ChipShape = RoundedCornerShape(8.dp)
val AvatarShape = RoundedCornerShape(50)
val BadgeShape = RoundedCornerShape(6.dp)
val DialogShape = RoundedCornerShape(28.dp)
val FloatingActionButtonShape = RoundedCornerShape(16.dp)
val SearchBarShape = RoundedCornerShape(28.dp)
val ProgressIndicatorShape = RoundedCornerShape(4.dp)
