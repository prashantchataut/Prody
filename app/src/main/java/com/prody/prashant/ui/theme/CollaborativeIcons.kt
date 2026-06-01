package com.prody.prashant.ui.theme

import androidx.compose.ui.graphics.Color
import com.prody.prashant.domain.collaborative.CardDesign
import com.prody.prashant.domain.collaborative.CardTheme

val CardTheme.primaryColor: Color
    get() = Color(getPrimaryColorArgb())

val CardTheme.secondaryColor: Color
    get() = Color(getSecondaryColorArgb())

val CardTheme.textColorColor: Color
    get() = Color(getTextColorArgb())

val CardDesign.backgroundColor: Color
    get() = Color(getBackgroundColorArgb())

val CardDesign.accentColor: Color
    get() = Color(getAccentColorArgb())

val CardDesign.textColor: Color
    get() = Color(getTextColorArgb())

fun CardDesign.customBackgroundColorCompose(): Color? = customBackgroundColorArgb?.let { Color(it) }