package com.prody.prashant.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * Optimized Icon System for Prody App
 * 
 * This object provides efficient access to Material Design icons and custom icons
 * without excessive lazy initialization that can cause memory pressure.
 * 
 * Key optimizations:
 * - Pre-loaded core icons for immediate access
 * - Lazy initialization only for complex custom icons
 * - Reused icon definitions to reduce memory footprint
 * - Proper resource cleanup and management
 */
object ProdyIcons {
    
    // === CORE MATERIAL ICONS (Pre-loaded) ===
    // These are immediately available without lazy initialization
    
    val Add: ImageVector = Icons.Filled.Add
    val ArrowBack: ImageVector = Icons.AutoMirrored.Filled.ArrowBack
    val ArrowForward: ImageVector = Icons.AutoMirrored.Filled.ArrowForward
    val Check: ImageVector = Icons.Filled.Check
    val Close: ImageVector = Icons.Filled.Close
    val Delete: ImageVector = Icons.Filled.Delete
    val Done: ImageVector = Icons.Filled.Done
    val Edit: ImageVector = Icons.Filled.Edit
    val Email: ImageVector = Icons.Filled.Email
    val Favorite: ImageVector = Icons.Filled.Favorite
    val FavoriteBorder: ImageVector = Icons.Filled.FavoriteBorder
    val Home: ImageVector = Icons.Filled.Home
    val Info: ImageVector = Icons.Filled.Info
    val Menu: ImageVector = Icons.Filled.Menu
    val MoreVert: ImageVector = Icons.Filled.MoreVert
    val Person: ImageVector = Icons.Filled.Person
    val Phone: ImageVector = Icons.Filled.Phone
    val Place: ImageVector = Icons.Filled.Place
    val PlayArrow: ImageVector = Icons.Filled.PlayArrow
    val Search: ImageVector = Icons.Filled.Search
    val Settings: ImageVector = Icons.Filled.Settings
    val Share: ImageVector = Icons.Filled.Share
    val Star: ImageVector = Icons.Filled.Star
    val Warning: ImageVector = Icons.Filled.Warning
    
    // === CUSTOM ICONS (Lazy-loaded on demand) ===
    // Only initialized when first accessed
    
    private val _customIcons = mutableMapOf<String, ImageVector>()
    
    /**
     * Get CheckCircle icon - creates once and caches
     */
    val CheckCircle: ImageVector
        get() = _customIcons.getOrPut("CheckCircle") {
            createIcon("CheckCircle") {
                path(fill = SolidColor(Color.Black), pathFillType = PathFillType.EvenOdd) {
                    moveTo(12f, 2f)
                    curveTo(6.48f, 2f, 2f, 6.48f, 2f, 12f)
                    reflectiveCurveToRelative(4.48f, 10f, 10f, 10f)
                    reflectiveCurveToRelative(10f, -4.48f, 10f, -10f)
                    reflectiveCurveToRelative(4.48f, 2f, 12f, 2f)
                    close()
                    moveTo(10f, 17f)
                    lineToRelative(-5f, -5f)
                    lineToRelative(1.41f, -1.41f)
                    lineTo(10f, 14.17f)
                    lineToRelative(7.59f, -7.59f)
                    lineTo(19f, 8f)
                    lineToRelative(-9f, 9f)
                    close()
                }
            }
        }

    /**
     * Get SelfImprovement icon - creates once and caches
     */
    val SelfImprovement: ImageVector
        get() = _customIcons.getOrPut("SelfImprovement") {
            createIcon("SelfImprovement") {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(12f, 2f)
                    curveTo(13.1f, 2f, 14f, 2.9f, 14f, 4f)
                    reflectiveCurveToRelative(-0.9f, 2f, -2f, 2f)
                    reflectiveCurveToRelative(-2f, -0.9f, -2f, -2f)
                    reflectiveCurveToRelative(0.9f, -2f, 2f, -2f)
                    close()
                    moveTo(21f, 9f)
                    horizontalLineToRelative(-6f)
                    verticalLineToRelative(13f)
                    horizontalLineToRelative(-2f)
                    verticalLineToRelative(-6f)
                    horizontalLineToRelative(-2f)
                    verticalLineToRelative(6f)
                    horizontalLineTo(9f)
                    verticalLineTo(9f)
                    horizontalLineTo(3f)
                    verticalLineTo(7f)
                    horizontalLineToRelative(18f)
                    verticalLineToRelative(2f)
                    close()
                }
            }
        }

    /**
     * Get Lightbulb icon - creates once and caches
     */
    val Lightbulb: ImageVector
        get() = _customIcons.getOrPut("Lightbulb") {
            createIcon("Lightbulb") {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(9f, 21f)
                    curveToRelative(0f, 0.5f, 0.4f, 1f, 1f, 1f)
                    horizontalLineToRelative(4f)
                    curveToRelative(0.6f, 0f, 1f, -0.5f, 1f, -1f)
                    verticalLineToRelative(-1f)
                    horizontalLineTo(9f)
                    verticalLineToRelative(1f)
                    close()
                    moveTo(12f, 2f)
                    curveTo(8.1f, 2f, 5f, 5.1f, 5f, 9f)
                    curveToRelative(0f, 2.4f, 1.2f, 4.5f, 3f, 5.7f)
                    verticalLineTo(17f)
                    curveToRelative(0f, 0.5f, 0.4f, 1f, 1f, 1f)
                    horizontalLineToRelative(6f)
                    curveToRelative(0.6f, 0f, 1f, -0.5f, 1f, -1f)
                    verticalLineToRelative(-2.3f)
                    curveToRelative(1.8f, -1.3f, 3f, -3.4f, 3f, -5.7f)
                    curveToRelative(0f, -3.9f, -3.1f, -7f, -7f, -7f)
                    close()
                }
            }
        }

    /**
     * Get AutoAwesome icon - creates once and caches
     */
    val AutoAwesome: ImageVector
        get() = _customIcons.getOrPut("AutoAwesome") {
            createIcon("AutoAwesome") {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(19f, 9f)
                    lineToRelative(1.25f, -2.75f)
                    lineTo(23f, 5f)
                    lineToRelative(-2.75f, -1.25f)
                    lineTo(19f, 1f)
                    lineToRelative(-1.25f, 2.75f)
                    lineTo(15f, 5f)
                    lineToRelative(2.75f, 1.25f)
                    close()
                    moveTo(19f, 15f)
                    lineToRelative(-1.25f, 2.75f)
                    lineTo(15f, 19f)
                    lineToRelative(2.75f, 1.25f)
                    lineTo(19f, 23f)
                    lineToRelative(1.25f, -2.75f)
                    lineTo(23f, 19f)
                    lineToRelative(-2.75f, -1.25f)
                    close()
                    moveTo(11.5f, 9.5f)
                    lineTo(9f, 4f)
                    lineTo(6.5f, 9.5f)
                    lineTo(1f, 12f)
                    lineTo(6.5f, 14.5f)
                    lineTo(9f, 20f)
                    lineTo(11.5f, 14.5f)
                    lineTo(17f, 12f)
                    lineTo(11.5f, 9.5f)
                    close()
                }
            }
        }

    // === COMMON ALIASES (Prevent duplicate definitions) ===
    
    val Mail: ImageVector = Email
    val Lock: ImageVector = Icons.Filled.Lock
    val SendIcon: ImageVector = Icons.AutoMirrored.Filled.Send
    val ArrowRight: ImageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight
    val ListIcon: ImageVector = Icons.AutoMirrored.Filled.List

    // === FREQUENTLY USED ICONS (Optimized access) ===
    
    object Common {
        val Add = ProdyIcons.Add
        val Check = ProdyIcons.Check
        val Close = ProdyIcons.Close
        val Home = ProdyIcons.Home
        val Menu = ProdyIcons.Menu
        val Person = ProdyIcons.Person
        val Search = ProdyIcons.Search
        val Settings = ProdyIcons.Settings
        val Star = ProdyIcons.Star
    }

    // === GAMIFICATION ICONS (Grouped for efficiency) ===
    
    object Gamification {
        val Trophy: ImageVector
            get() = _customIcons.getOrPut("Trophy") {
                createIcon("Trophy") {
                    path(fill = SolidColor(Color.Black)) {
                        moveTo(19f, 5f)
                        horizontalLineToRelative(-2f)
                        verticalLineTo(3f)
                        curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
                        reflectiveCurveToRelative(-2f, 0.9f, -2f, 2f)
                        verticalLineToRelative(2f)
                        horizontalLineTo(7f)
                        verticalLineTo(3f)
                        curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
                        reflectiveCurveToRelative(-2f, 0.9f, -2f, 2f)
                        verticalLineToRelative(2f)
                        horizontalLineTo(5f)
                        curveToRelative(-1.1f, 0f, -2f, 0.9f, -2f, 2f)
                        verticalLineToRelative(1f)
                        curveToRelative(0f, 2.6f, 1.8f, 4.8f, 4.2f, 5.4f)
                        curveToRelative(-0.4f, 0.8f, -0.7f, 1.7f, -0.7f, 2.6f)
                        verticalLineToRelative(5f)
                        horizontalLineToRelative(12f)
                        verticalLineToRelative(-5f)
                        curveToRelative(0f, -0.9f, -0.3f, -1.8f, -0.7f, -2.6f)
                        curveToRelative(2.4f, -0.6f, 4.2f, -2.8f, 4.2f, -5.4f)
                        verticalLineToRelative(-1f)
                        curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
                    }
                }
            }
    }

    // === UTILITY METHODS ===

    /**
     * Helper function to create a standard icon with consistent sizing
     */
    private fun createIcon(name: String, pathData: Builder.() -> Unit): ImageVector {
        return ImageVector.Builder(
            name = name,
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply(pathData).build()
    }

    /**
     * Clear icon cache (useful for testing or memory management)
     */
    fun clearIconCache() {
        _customIcons.clear()
    }

    /**
     * Get memory usage statistics for icons
     */
    fun getIconMemoryInfo(): String {
        return "Core Icons: ${Common::class.java.fields.size}, " +
                "Custom Icons Cached: ${_customIcons.size}, " +
                "Total Memory: ~${(_customIcons.size * 1024)} bytes"
    }

    /**
     * Pre-load commonly used icons at app startup
     */
    fun preloadCommonIcons() {
        // Pre-load most frequently used custom icons
        listOf(
            CheckCircle, SelfImprovement, Lightbulb, AutoAwesome
        ).forEach { _ -> }
    }
}