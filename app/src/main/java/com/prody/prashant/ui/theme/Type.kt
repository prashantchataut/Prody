package com.prody.prashant.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.prody.prashant.R

/**
 * Prody Design System - Typography
 *
 * A refined typography system using Poppins with carefully calibrated weights,
 * sizes, and letter spacing for optimal readability and visual hierarchy.
 *
 * Design Principles:
 * - Generous line heights for comfortable reading
 * - Subtle letter spacing for elegance
 * - Clear weight distinctions for visual hierarchy
 * - Consistent optical sizing across all text styles
 */

// Primary font family - Poppins
val PoppinsFamily = FontFamily(
    Font(R.font.poppins_thin, FontWeight.Thin),
    Font(R.font.poppins_extralight, FontWeight.ExtraLight),
    Font(R.font.poppins_light, FontWeight.Light),
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_bold, FontWeight.Bold),
    Font(R.font.poppins_extrabold, FontWeight.ExtraBold),
    Font(R.font.poppins_black, FontWeight.Black)
)

/**
 * Main Typography configuration following Material Design 3 guidelines
 * with custom refinements for the Prody design language.
 */
val ProdyTypography = Typography(
    // ==========================================================================
    // DISPLAY STYLES - For large, impactful text (splash screens, hero sections)
    // ==========================================================================
    displayLarge = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 52.sp,
        lineHeight = 60.sp,
        letterSpacing = (-0.5).sp  // Tighter tracking for large display
    ),
    displayMedium = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 42.sp,
        lineHeight = 50.sp,
        letterSpacing = (-0.25).sp
    ),
    displaySmall = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 34.sp,
        lineHeight = 42.sp,
        letterSpacing = 0.sp
    ),

    // ==========================================================================
    // HEADLINE STYLES - For section headers and prominent text
    // ==========================================================================
    headlineLarge = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 30.sp,
        lineHeight = 38.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 26.sp,
        lineHeight = 34.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 30.sp,
        letterSpacing = 0.sp
    ),

    // ==========================================================================
    // TITLE STYLES - For card titles, list items, and UI element headers
    // ==========================================================================
    titleLarge = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp
    ),
    titleSmall = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),

    // ==========================================================================
    // BODY STYLES - For main content and readable text blocks
    // ==========================================================================
    bodyLarge = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 26.sp,  // More generous for readability
        letterSpacing = 0.3.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.2.sp
    ),
    bodySmall = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.3.sp
    ),

    // ==========================================================================
    // LABEL STYLES - For buttons, chips, tabs, and small UI elements
    // ==========================================================================
    labelLarge = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelSmall = TextStyle(
        fontFamily = PoppinsFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.5.sp
    )
)

// =============================================================================
// EXTENDED TYPOGRAPHY - Custom text styles for special use cases
// =============================================================================

/**
 * Quote style - For displaying wisdom quotes and inspirational text
 */
val QuoteTextStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Light,
    fontSize = 18.sp,
    lineHeight = 28.sp,
    letterSpacing = 0.2.sp
)

/**
 * Number display - For stats, counters, and numerical highlights
 */
val NumberDisplayStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 32.sp,
    lineHeight = 40.sp,
    letterSpacing = (-0.5).sp
)

/**
 * Caption style - For timestamps, metadata, and secondary information
 */
val CaptionTextStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 11.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.4.sp
)

/**
 * Button text - Emphasized for interactive elements
 */
val ButtonTextStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.5.sp
)

/**
 * Overline style - For category labels and small headers
 */
val OverlineTextStyle = TextStyle(
    fontFamily = PoppinsFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 10.sp,
    lineHeight = 14.sp,
    letterSpacing = 1.5.sp  // Wide tracking for small caps effect
)
