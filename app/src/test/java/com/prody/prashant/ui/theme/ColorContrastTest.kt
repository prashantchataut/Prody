package com.prody.prashant.ui.theme

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertTrue
import org.junit.Test

class ColorContrastTest {

    @Test
    fun `light theme text pairs meet wcag aa contrast`() {
        assertContrast("onPrimary/primary", ProdyTextOnPrimaryLight, ProdyPrimary, 4.5)
        assertContrast("onBackground/background", ProdyTextPrimaryLight, ProdyBackgroundLight, 4.5)
        assertContrast("onSurface/surface", ProdyTextPrimaryLight, ProdySurfaceLight, 4.5)
        assertContrast("secondary/onSecondary", ProdyOnSecondary, ProdySecondary, 4.5)
    }

    @Test
    fun `dark theme text pairs meet wcag aa contrast`() {
        assertContrast("onPrimary/primary dark", ProdyTextOnPrimaryDark, ProdyPrimaryDark, 4.5)
        assertContrast("onBackground/background dark", ProdyTextPrimaryDark, ProdyBackgroundDark, 4.5)
        assertContrast("onSurface/surface dark", ProdyTextPrimaryDark, ProdySurfaceDark, 4.5)
        assertContrast("secondary/onSecondary dark", ProdyOnSecondaryDark, ProdySecondaryDark, 4.5)
    }

    private fun assertContrast(label: String, foreground: Color, background: Color, minimum: Double) {
        val ratio = contrastRatio(foreground, background)
        assertTrue("$label contrast was $ratio but expected >= $minimum", ratio >= minimum)
    }

    private fun contrastRatio(foreground: Color, background: Color): Double {
        val l1 = foreground.luminance().toDouble()
        val l2 = background.luminance().toDouble()
        val lighter = maxOf(l1, l2)
        val darker = minOf(l1, l2)
        return (lighter + 0.05) / (darker + 0.05)
    }
}
