package com.prody.prashant.ui.icons

import androidx.compose.material.icons.Icons

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
    val ArrowBack: ImageVector get() = _customIcons.getOrPut("ArrowBack") {
        createIcon("ArrowBack") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(20f, 11f)
                horizontalLineTo(7.83f)
                lineToRelative(5.59f, -5.59f)
                lineTo(12f, 4f)
                lineToRelative(-8f, 8f)
                lineToRelative(8f, 8f)
                lineToRelative(1.41f, -1.41f)
                lineTo(7.83f, 13f)
                horizontalLineTo(20f)
                verticalLineToRelative(-2f)
                close()
            }
        }
    }

    val ArrowForward: ImageVector get() = _customIcons.getOrPut("ArrowForward") {
        createIcon("ArrowForward") {
             path(fill = SolidColor(Color.Black)) {
                moveTo(12f, 4f)
                lineToRelative(-1.41f, 1.41f)
                lineTo(16.17f, 11f)
                horizontalLineTo(4f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(12.17f)
                lineToRelative(-5.58f, 5.59f)
                lineTo(12f, 20f)
                lineToRelative(8f, -8f)
                close()
            }
        }
    }

    val ChevronRight: ImageVector get() = _customIcons.getOrPut("ChevronRight") {
         createIcon("ChevronRight") {
             path(fill = SolidColor(Color.Black)) {
                moveTo(10f, 6f)
                lineTo(8.59f, 7.41f)
                lineTo(13.17f, 12f)
                lineTo(8.59f, 16.59f)
                lineTo(10f, 18f)
                lineTo(16f, 12f)
                close()
            }
        }
    }

    val KeyboardArrowRight: ImageVector get() = ChevronRight

    val List: ImageVector get() = _customIcons.getOrPut("List") {
        createIcon("List") {
             path(fill = SolidColor(Color.Black)) {
                moveTo(3f, 13f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(-2f)
                horizontalLineTo(3f)
                verticalLineToRelative(2f)
                close()
                moveTo(3f, 17f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(-2f)
                horizontalLineTo(3f)
                verticalLineToRelative(2f)
                close()
                moveTo(3f, 9f)
                horizontalLineToRelative(2f)
                verticalLineTo(7f)
                horizontalLineTo(3f)
                verticalLineToRelative(2f)
                close()
                moveTo(7f, 13f)
                horizontalLineToRelative(14f)
                verticalLineToRelative(-2f)
                horizontalLineTo(7f)
                verticalLineToRelative(2f)
                close()
                moveTo(7f, 17f)
                horizontalLineToRelative(14f)
                verticalLineToRelative(-2f)
                horizontalLineTo(7f)
                verticalLineToRelative(2f)
                close()
                moveTo(7f, 9f)
                horizontalLineToRelative(14f)
                verticalLineTo(7f)
                horizontalLineTo(7f)
                verticalLineToRelative(2f)
                close()
            }
        }
    }

    val Send: ImageVector get() = _customIcons.getOrPut("Send") {
        createIcon("Send") {
             path(fill = SolidColor(Color.Black)) {
                moveTo(2.01f, 21f)
                lineTo(23f, 12f)
                lineTo(2.01f, 3f)
                lineTo(2f, 10f)
                lineToRelative(15f, 2f)
                lineToRelative(-15f, 2f)
                close()
            }
        }
    }
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
    val Mic: ImageVector = Icons.Filled.Mic
    val MicNone: ImageVector = Icons.Filled.MicNone
    val Spa: ImageVector = Icons.Filled.Spa
    val HealthAndSafety: ImageVector = Icons.Filled.HealthAndSafety
    
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
                    verticalLineToRelative(9f)
                    horizontalLineTo(3f)
                    verticalLineToRelative(7f)
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
                    horizontalLineToRelative(9f)
                    verticalLineToRelative(1f)
                    close()
                    moveTo(12f, 2f)
                    curveToRelative(8.1f, 2f, 5f, 5.1f, 5f, 9f)
                    curveToRelative(0f, 2.4f, 1.2f, 4.5f, 3f, 5.7f)
                    verticalLineToRelative(17f)
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
                    lineToRelative(23f, 5f)
                    lineToRelative(-2.75f, -1.25f)
                    lineToRelative(19f, 1f)
                    lineToRelative(-1.25f, 2.75f)
                    lineToRelative(15f, 5f)
                    lineToRelative(2.75f, 1.25f)
                    close()
                    moveTo(19f, 15f)
                    lineToRelative(-1.25f, 2.75f)
                    lineToRelative(15f, 19f)
                    lineToRelative(2.75f, 1.25f)
                    lineToRelative(19f, 23f)
                    lineToRelative(1.25f, -2.75f)
                    lineToRelative(23f, 19f)
                    lineToRelative(-2.75f, -1.25f)
                    close()
                    moveTo(11.5f, 9.5f)
                    lineTo(9f, 4f)
                    lineToRelative(6.5f, 9.5f)
                    lineToRelative(1f, 12f)
                    lineToRelative(6.5f, 14.5f)
                    lineToRelative(9f, 20f)
                    lineToRelative(11.5f, 14.5f)
                    lineToRelative(17f, 12f)
                    lineToRelative(11.5f, 9.5f)
                    close()
                }
            }
        }

    // === COMMON ALIASES (Prevent duplicate definitions) ===
    
    val Mail: ImageVector = Email
    val Lock: ImageVector = Icons.Filled.Lock
    val SendIcon: ImageVector get() = Send
    val ArrowRight: ImageVector get() = KeyboardArrowRight
    val ListIcon: ImageVector get() = List

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
                        verticalLineToRelative(3f)
                        curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
                        reflectiveCurveToRelative(-2f, 0.9f, -2f, 2f)
                        verticalLineToRelative(2f)
                        horizontalLineToRelative(7f)
                        verticalLineToRelative(3f)
                        curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
                        reflectiveCurveToRelative(-2f, 0.9f, -2f, 2f)
                        verticalLineToRelative(2f)
                        horizontalLineToRelative(5f)
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


    val Schedule: ImageVector by lazy {
        createIcon("Schedule") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(11.99f, 2f)
                curveTo(6.47f, 2f, 2f, 6.48f, 2f, 12f)
                reflectiveCurveToRelative(4.47f, 10f, 9.99f, 10f)
                curveTo(17.52f, 22f, 22f, 17.52f, 22f, 12f)
                reflectiveCurveTo(17.52f, 2f, 11.99f, 2f)
                close()
                moveTo(12f, 20f)
                curveToRelative(-4.42f, 0f, -8f, -3.58f, -8f, -8f)
                reflectiveCurveToRelative(3.58f, -8f, 8f, -8f)
                reflectiveCurveToRelative(8f, 3.58f, 8f, 8f)
                reflectiveCurveToRelative(-3.58f, 8f, -8f, 8f)
                close()
                moveTo(12.5f, 7f)
                horizontalLineTo(11f)
                verticalLineToRelative(6f)
                lineToRelative(5.25f, 3.15f)
                lineToRelative(0.75f, -1.23f)
                lineToRelative(-4.5f, -2.67f)
                close()
            }
        }
    }

    val LocalFireDepartment: ImageVector by lazy {
        createIcon("LocalFireDepartment") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(12f, 12.9f)
                lineToRelative(-2.13f, 2.09f)
                curveTo(9.31f, 15.55f, 9f, 16.28f, 9f, 17.06f)
                curveTo(9f, 18.68f, 10.35f, 20f, 12f, 20f)
                reflectiveCurveToRelative(3f, -1.32f, 3f, -2.94f)
                curveToRelative(0f, -0.78f, -0.31f, -1.52f, -0.87f, -2.07f)
                close()
                moveTo(16f, 6f)
                lineToRelative(-0.44f, 0.55f)
                curveTo(14.38f, 8.02f, 12f, 7.19f, 12f, 5.3f)
                verticalLineTo(2f)
                reflectiveCurveToRelative(-8f, 4f, -8f, 11f)
                curveToRelative(0f, 4.42f, 3.58f, 8f, 8f, 8f)
                reflectiveCurveToRelative(8f, -3.58f, 8f, -8f)
                curveToRelative(0f, -2.96f, -1.61f, -5.62f, -4f, -7f)
                close()
            }
        }
    }

    val EmojiEvents: ImageVector by lazy {
        createIcon("EmojiEvents") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(19f, 5f)
                horizontalLineToRelative(-2f)
                verticalLineTo(3f)
                horizontalLineTo(7f)
                verticalLineToRelative(2f)
                horizontalLineTo(5f)
                curveTo(3.9f, 5f, 3f, 5.9f, 3f, 7f)
                verticalLineToRelative(1f)
                curveToRelative(0f, 2.55f, 1.92f, 4.63f, 4.39f, 4.94f)
                curveToRelative(0.63f, 1.5f, 1.98f, 2.63f, 3.61f, 2.96f)
                verticalLineTo(19f)
                horizontalLineTo(7f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(10f)
                verticalLineToRelative(-2f)
                horizontalLineToRelative(-4f)
                verticalLineToRelative(-3.1f)
                curveToRelative(1.63f, -0.33f, 2.98f, -1.46f, 3.61f, -2.96f)
                curveTo(19.08f, 12.63f, 21f, 10.55f, 21f, 8f)
                verticalLineTo(7f)
                curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
                close()
                moveTo(5f, 8f)
                verticalLineTo(7f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(3.82f)
                curveTo(5.84f, 10.4f, 5f, 9.3f, 5f, 8f)
                close()
                moveTo(19f, 8f)
                curveToRelative(0f, 1.3f, -0.84f, 2.4f, -2f, 2.82f)
                verticalLineTo(7f)
                horizontalLineToRelative(2f)
                close()
            }
        }
    }

    val Psychology: ImageVector by lazy {
        createIcon("Psychology") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(13f, 8.57f)
                curveToRelative(-0.79f, 0f, -1.43f, 0.64f, -1.43f, 1.43f)
                reflectiveCurveToRelative(0.64f, 1.43f, 1.43f, 1.43f)
                reflectiveCurveToRelative(1.43f, -0.64f, 1.43f, -1.43f)
                reflectiveCurveTo(13.79f, 8.57f, 13f, 8.57f)
                close()
                moveTo(13f, 3f)
                curveTo(9.25f, 3f, 6.2f, 5.94f, 6.02f, 9.64f)
                lineTo(4.1f, 12.2f)
                curveToRelative(-0.25f, 0.33f, -0.01f, 0.8f, 0.4f, 0.8f)
                horizontalLineTo(6f)
                verticalLineToRelative(3f)
                curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
                horizontalLineToRelative(1f)
                verticalLineToRelative(3f)
                horizontalLineToRelative(7f)
                verticalLineToRelative(-4.68f)
                curveToRelative(2.36f, -1.12f, 4f, -3.53f, 4f, -6.32f)
                curveToRelative(0f, -3.87f, -3.13f, -7f, -7f, -7f)
                close()
                moveTo(16.23f, 13.83f)
                lineToRelative(-0.23f, 0.11f)
                verticalLineTo(18f)
                horizontalLineToRelative(-3f)
                verticalLineToRelative(-3f)
                horizontalLineTo(9f)
                verticalLineToRelative(-4f)
                horizontalLineTo(6.82f)
                lineToRelative(1.03f, -1.38f)
                lineToRelative(-0.02f, -0.6f)
                curveTo(7.93f, 6.73f, 10.21f, 5f, 13f, 5f)
                curveToRelative(2.76f, 0f, 5f, 2.24f, 5f, 5f)
                curveToRelative(0f, 2.01f, -1.19f, 3.74f, -2.77f, 4.83f)
                close()
            }
        }
    }

    val Chat: ImageVector by lazy {
        createIcon("Chat") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(20f, 2f)
                horizontalLineTo(4f)
                curveToRelative(-1.1f, 0f, -1.99f, 0.9f, -1.99f, 2f)
                lineTo(2f, 22f)
                lineToRelative(4f, -4f)
                horizontalLineToRelative(14f)
                curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
                verticalLineTo(4f)
                curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
                close()
                moveTo(6f, 9f)
                horizontalLineToRelative(12f)
                verticalLineToRelative(2f)
                horizontalLineTo(6f)
                close()
                moveTo(14f, 14f)
                horizontalLineTo(6f)
                verticalLineToRelative(-2f)
                horizontalLineToRelative(8f)
                close()
                moveTo(18f, 8f)
                horizontalLineTo(6f)
                verticalLineTo(6f)
                horizontalLineToRelative(12f)
                close()
            }
        }
    }

    val Notifications: ImageVector by lazy {
        createIcon("Notifications") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(12f, 22f)
                curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
                horizontalLineToRelative(-4f)
                curveToRelative(0f, 1.1f, 0.89f, 2f, 2f, 2f)
                close()
                moveTo(18f, 16f)
                verticalLineToRelative(-5f)
                curveToRelative(0f, -3.07f, -1.64f, -5.64f, -4.5f, -6.32f)
                verticalLineTo(4f)
                curveToRelative(0f, -0.83f, -0.67f, -1.5f, -1.5f, -1.5f)
                reflectiveCurveToRelative(-1.5f, 0.67f, -1.5f, 1.5f)
                verticalLineToRelative(0.68f)
                curveTo(7.63f, 5.36f, 6f, 7.92f, 6f, 11f)
                verticalLineToRelative(5f)
                lineToRelative(-2f, 2f)
                verticalLineToRelative(1f)
                horizontalLineToRelative(16f)
                verticalLineToRelative(-1f)
                close()
            }
        }
    }



    val Inbox: ImageVector by lazy {
        createIcon("Inbox") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(19f, 3f)
                horizontalLineTo(4.99f)
                curveToRelative(-1.1f, 0f, -1.98f, 0.9f, -1.98f, 2f)
                lineTo(3f, 19f)
                curveToRelative(0f, 1.1f, 0.89f, 2f, 1.99f, 2f)
                horizontalLineTo(19f)
                curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
                verticalLineTo(5f)
                curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
                close()
                moveTo(19f, 15f)
                horizontalLineToRelative(-4f)
                curveToRelative(0f, 1.66f, -1.35f, 3f, -3f, 3f)
                reflectiveCurveToRelative(-3f, -1.34f, -3f, -3f)
                horizontalLineTo(4.99f)
                verticalLineTo(5f)
                horizontalLineTo(19f)
                close()
            }
        }
    }



    val Sms: ImageVector by lazy {
        createIcon("Sms") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(20f, 2f)
                horizontalLineTo(4f)
                curveToRelative(-1.1f, 0f, -1.99f, 0.9f, -1.99f, 2f)
                lineTo(2f, 22f)
                lineToRelative(4f, -4f)
                horizontalLineToRelative(14f)
                curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
                verticalLineTo(4f)
                curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
                close()
                moveTo(9f, 11f)
                horizontalLineTo(7f)
                verticalLineTo(9f)
                horizontalLineToRelative(2f)
                close()
                moveTo(13f, 11f)
                horizontalLineToRelative(-2f)
                verticalLineTo(9f)
                horizontalLineToRelative(2f)
                close()
                moveTo(17f, 11f)
                horizontalLineToRelative(-2f)
                verticalLineTo(9f)
                horizontalLineToRelative(2f)
                close()
            }
        }
    }

    val Refresh: ImageVector by lazy {
        createIcon("Refresh") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(17.65f, 6.35f)
                curveTo(16.2f, 4.9f, 14.21f, 4f, 12f, 4f)
                curveToRelative(-4.42f, 0f, -7.99f, 3.58f, -7.99f, 8f)
                reflectiveCurveToRelative(3.57f, 8f, 7.99f, 8f)
                curveToRelative(3.73f, 0f, 6.84f, -2.55f, 7.73f, -6f)
                horizontalLineToRelative(-2.08f)
                curveToRelative(-0.82f, 2.33f, -3.04f, 4f, -5.65f, 4f)
                curveToRelative(-3.31f, 0f, -6f, -2.69f, -6f, -6f)
                reflectiveCurveToRelative(2.69f, -6f, 6f, -6f)
                curveToRelative(1.66f, 0f, 3.14f, 0.69f, 4.22f, 1.78f)
                lineTo(13f, 11f)
                horizontalLineToRelative(7f)
                verticalLineTo(4f)
                close()
            }
        }
    }



    val School: ImageVector by lazy {
        createIcon("School") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(5f, 13.18f)
                verticalLineToRelative(4f)
                lineTo(12f, 21f)
                lineToRelative(7f, -3.82f)
                verticalLineToRelative(-4f)
                lineTo(12f, 17f)
                close()
                moveTo(12f, 3f)
                lineTo(1f, 9f)
                lineToRelative(11f, 6f)
                lineToRelative(9f, -4.91f)
                verticalLineTo(17f)
                horizontalLineToRelative(2f)
                verticalLineTo(9f)
                close()
            }
        }
    }

    val Book: ImageVector by lazy {
        createIcon("Book") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(18f, 2f)
                horizontalLineTo(6f)
                curveToRelative(-1.1f, 0f, -2f, 0.9f, -2f, 2f)
                verticalLineToRelative(16f)
                curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
                horizontalLineToRelative(12f)
                curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
                verticalLineTo(4f)
                curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
                close()
                moveTo(6f, 4f)
                horizontalLineToRelative(5f)
                verticalLineToRelative(8f)
                lineToRelative(-2.5f, -1.5f)
                lineTo(6f, 12f)
                close()
            }
        }
    }

    val Stars: ImageVector by lazy {
        createIcon("Stars") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(11.99f, 2f)
                curveTo(6.47f, 2f, 2f, 6.48f, 2f, 12f)
                reflectiveCurveToRelative(4.47f, 10f, 9.99f, 10f)
                curveTo(17.52f, 22f, 22f, 17.52f, 22f, 12f)
                reflectiveCurveTo(17.52f, 2f, 11.99f, 2f)
                close()
                moveTo(16.23f, 18f)
                lineTo(12f, 15.45f)
                lineTo(7.77f, 18f)
                lineToRelative(1.12f, -4.81f)
                lineToRelative(-3.73f, -3.23f)
                lineToRelative(4.92f, -0.42f)
                lineTo(12f, 5f)
                lineToRelative(1.92f, 4.53f)
                lineToRelative(4.92f, 0.42f)
                lineToRelative(-3.73f, 3.23f)
                close()
            }
        }
    }

    val TrendingUp: ImageVector by lazy {
        createIcon("TrendingUp") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(16f, 6f)
                lineToRelative(2.29f, 2.29f)
                lineToRelative(-4.88f, 4.88f)
                lineToRelative(-4f, -4f)
                lineTo(2f, 16.59f)
                lineTo(3.41f, 18f)
                lineToRelative(6f, -6f)
                lineToRelative(4f, 4f)
                lineToRelative(6.3f, -6.29f)
                lineTo(22f, 12f)
                verticalLineTo(6f)
                close()
            }
        }
    }

    val TrendingDown: ImageVector by lazy {
        createIcon("TrendingDown") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(16f, 18f)
                lineToRelative(2.29f, -2.29f)
                lineToRelative(-4.88f, -4.88f)
                lineToRelative(-4f, 4f)
                lineTo(2f, 7.41f)
                lineTo(3.41f, 6f)
                lineToRelative(6f, 6f)
                lineToRelative(4f, -4f)
                lineToRelative(6.3f, 6.29f)
                lineTo(22f, 12f)
                verticalLineTo(18f)
                close()
            }
        }
    }

    val ExpandLess: ImageVector by lazy {
        createIcon("ExpandLess") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(12f, 8f)
                lineToRelative(-6f, 6f)
                lineToRelative(1.41f, 1.41f)
                lineTo(12f, 10.83f)
                lineToRelative(4.59f, 4.58f)
                lineTo(18f, 14f)
                close()
            }
        }
    }

    val ExpandMore: ImageVector by lazy {
        createIcon("ExpandMore") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(16.59f, 8.59f)
                lineTo(12f, 13.17f)
                lineTo(7.41f, 8.59f)
                lineTo(6f, 10f)
                lineToRelative(6f, 6f)
                lineToRelative(6f, -6f)
                close()
            }
        }
    }

    val MenuBook: ImageVector by lazy {
        createIcon("MenuBook") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(21f, 5f)
                curveToRelative(-1.11f, -0.35f, -2.33f, -0.5f, -3.5f, -0.5f)
                curveToRelative(-1.95f, 0f, -4.05f, 0.4f, -5.5f, 1.5f)
                curveToRelative(-1.45f, -1.1f, -3.55f, -1.5f, -5.5f, -1.5f)
                reflectiveCurveTo(2.45f, 4.9f, 1f, 6f)
                verticalLineToRelative(14.65f)
                curveToRelative(0f, 0.25f, 0.25f, 0.5f, 0.5f, 0.5f)
                curveToRelative(0.1f, 0f, 0.15f, -0.05f, 0.25f, -0.05f)
                curveTo(3.1f, 20.45f, 5.05f, 20f, 6.5f, 20f)
                curveToRelative(1.95f, 0f, 4.05f, 0.4f, 5.5f, 1.5f)
                curveToRelative(1.35f, -0.85f, 3.8f, -1.5f, 5.5f, -1.5f)
                curveToRelative(1.65f, 0f, 3.35f, 0.3f, 4.75f, 1.05f)
                curveToRelative(0.1f, 0.05f, 0.15f, 0.05f, 0.25f, 0.05f)
                curveToRelative(0.25f, 0f, 0.5f, -0.25f, 0.5f, -0.5f)
                verticalLineTo(6f)
                curveToRelative(-0.6f, -0.45f, -1.25f, -0.75f, -2f, -1f)
                close()
                moveTo(21f, 18.5f)
                curveToRelative(-1.1f, -0.35f, -2.3f, -0.5f, -3.5f, -0.5f)
                curveToRelative(-1.7f, 0f, -4.15f, 0.65f, -5.5f, 1.5f)
                verticalLineTo(8f)
                curveToRelative(1.35f, -0.85f, 3.8f, -1.5f, 5.5f, -1.5f)
                curveToRelative(1.2f, 0f, 2.4f, 0.15f, 3.5f, 0.5f)
                close()
            }
        }
    }

    val ErrorOutline: ImageVector by lazy {
        createIcon("ErrorOutline") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(11f, 15f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(-2f)
                close()
                moveTo(11f, 7f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(6f)
                horizontalLineToRelative(-2f)
                close()
                moveTo(11.99f, 2f)
                curveTo(6.47f, 2f, 2f, 6.48f, 2f, 12f)
                reflectiveCurveToRelative(4.47f, 10f, 9.99f, 10f)
                curveTo(17.52f, 22f, 22f, 17.52f, 22f, 12f)
                reflectiveCurveTo(17.52f, 2f, 11.99f, 2f)
                close()
                moveTo(12f, 20f)
                curveToRelative(-4.42f, 0f, -8f, -3.58f, -8f, -8f)
                reflectiveCurveToRelative(3.58f, -8f, 8f, -8f)
                reflectiveCurveToRelative(8f, 3.58f, 8f, 8f)
                reflectiveCurveToRelative(-3.58f, 8f, -8f, 8f)
                close()
            }
        }
    }

    val AutoStories: ImageVector by lazy {
        createIcon("AutoStories") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(22.47f, 5.2f)
                curveTo(22f, 4.96f, 21.51f, 4.76f, 21f, 4.59f)
                verticalLineTo(3.89f)
                curveToRelative(0f, -0.13f, -0.05f, -0.26f, -0.14f, -0.36f)
                curveToRelative(-0.1f, -0.11f, -0.23f, -0.17f, -0.37f, -0.17f)
                curveToRelative(-0.13f, 0f, -0.26f, 0.06f, -0.36f, 0.16f)
                curveToRelative(-0.09f, 0.1f, -0.14f, 0.23f, -0.14f, 0.37f)
                verticalLineToRelative(0.62f)
                curveToRelative(-0.72f, -0.2f, -1.47f, -0.35f, -2.23f, -0.46f)
                curveTo(16.59f, 3.89f, 15.4f, 3.75f, 14.2f, 3.68f)
                lineTo(13f, 5.28f)
                verticalLineTo(19f)
                horizontalLineToRelative(1f)
                lineToRelative(5f, -5f)
                verticalLineTo(4.78f)
                curveToRelative(0.36f, 0.08f, 0.71f, 0.18f, 1.05f, 0.3f)
                verticalLineToRelative(8.11f)
                lineToRelative(1.5f, 1.5f)
                verticalLineTo(5.62f)
                curveToRelative(0.32f, 0.16f, 0.63f, 0.34f, 0.92f, 0.55f)
                curveToRelative(0.11f, 0.08f, 0.25f, 0.11f, 0.38f, 0.08f)
                curveToRelative(0.14f, -0.03f, 0.25f, -0.11f, 0.33f, -0.22f)
                curveToRelative(0.07f, -0.11f, 0.11f, -0.25f, 0.08f, -0.38f)
                curveTo(22.73f, 5.52f, 22.62f, 5.33f, 22.47f, 5.2f)
                close()
                moveTo(10f, 4f)
                horizontalLineTo(4f)
                verticalLineToRelative(13f)
                lineToRelative(5f, 5f)
                horizontalLineToRelative(1f)
                close()
            }
        }
    }

    val FormatQuote: ImageVector by lazy {
        createIcon("FormatQuote") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(6f, 17f)
                horizontalLineToRelative(3f)
                lineToRelative(2f, -4f)
                verticalLineTo(7f)
                horizontalLineTo(5f)
                verticalLineToRelative(6f)
                horizontalLineToRelative(3f)
                close()
                moveTo(14f, 17f)
                horizontalLineToRelative(3f)
                lineToRelative(2f, -4f)
                verticalLineTo(7f)
                horizontalLineToRelative(-6f)
                verticalLineToRelative(6f)
                horizontalLineToRelative(3f)
                close()
            }
        }
    }

    val WbSunny: ImageVector by lazy {
        createIcon("WbSunny") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(6.76f, 4.84f)
                lineToRelative(-1.8f, -1.79f)
                lineToRelative(-1.41f, 1.41f)
                lineToRelative(1.79f, 1.79f)
                close()
                moveTo(4f, 10.5f)
                horizontalLineTo(1f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(3f)
                close()
                moveTo(13f, 0.55f)
                horizontalLineToRelative(-2f)
                verticalLineTo(3.5f)
                horizontalLineToRelative(2f)
                close()
                moveTo(20.45f, 4.46f)
                lineToRelative(-1.41f, -1.41f)
                lineToRelative(-1.79f, 1.79f)
                lineToRelative(1.41f, 1.41f)
                close()
                moveTo(17.24f, 18.16f)
                lineToRelative(1.79f, 1.8f)
                lineToRelative(1.41f, -1.41f)
                lineToRelative(-1.8f, -1.79f)
                close()
                moveTo(20f, 10.5f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(3f)
                verticalLineToRelative(-2f)
                close()
                moveTo(12f, 5.5f)
                curveToRelative(-3.31f, 0f, -6f, 2.69f, -6f, 6f)
                reflectiveCurveToRelative(2.69f, 6f, 6f, 6f)
                reflectiveCurveToRelative(6f, -2.69f, 6f, -6f)
                reflectiveCurveToRelative(-2.69f, -6f, -6f, -6f)
                close()
                moveTo(11f, 22.45f)
                horizontalLineToRelative(2f)
                verticalLineTo(19.5f)
                horizontalLineToRelative(-2f)
                close()
                moveTo(3.55f, 18.54f)
                lineToRelative(1.41f, 1.41f)
                lineToRelative(1.79f, -1.8f)
                lineToRelative(-1.41f, -1.41f)
                close()
            }
        }
    }

    val NightsStay: ImageVector by lazy {
        createIcon("NightsStay") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(11.1f, 12.08f)
                curveToRelative(-2.33f, -4.51f, -0.5f, -8.48f, 0.53f, -10.07f)
                curveTo(6.27f, 2.2f, 1.98f, 6.59f, 1.98f, 12f)
                curveToRelative(0f, 0.14f, 0.02f, 0.28f, 0.02f, 0.42f)
                curveTo(2.62f, 12.15f, 3.29f, 12f, 4f, 12f)
                curveToRelative(1.66f, 0f, 3.18f, 0.83f, 4.1f, 2.15f)
                curveTo(9.77f, 14.63f, 11f, 16.17f, 11f, 18f)
                curveToRelative(0f, 1.52f, -0.87f, 2.83f, -2.12f, 3.51f)
                curveToRelative(0.98f, 0.32f, 2.03f, 0.5f, 3.11f, 0.5f)
                curveToRelative(3.5f, 0f, 6.58f, -1.8f, 8.37f, -4.52f)
                curveToRelative(-2.36f, 0.23f, -6.98f, -0.97f, -9.26f, -5.41f)
                close()
                moveTo(7f, 16f)
                horizontalLineToRelative(-0.18f)
                curveTo(6.4f, 14.84f, 5.3f, 14f, 4f, 14f)
                curveToRelative(-1.66f, 0f, -3f, 1.34f, -3f, 3f)
                reflectiveCurveToRelative(1.34f, 3f, 3f, 3f)
                curveToRelative(0.62f, 0f, 2.49f, 0f, 3f, 0f)
                curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
                reflectiveCurveToRelative(-0.9f, -2f, -2f, -2f)
                close()
            }
        }
    }

    val DarkMode: ImageVector by lazy {
        createIcon("DarkMode") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(12f, 3f)
                curveToRelative(-4.97f, 0f, -9f, 4.03f, -9f, 9f)
                reflectiveCurveToRelative(4.03f, 9f, 9f, 9f)
                reflectiveCurveToRelative(9f, -4.03f, 9f, -9f)
                curveToRelative(0f, -0.46f, -0.04f, -0.92f, -0.1f, -1.36f)
                curveToRelative(-0.98f, 1.37f, -2.58f, 2.26f, -4.4f, 2.26f)
                curveToRelative(-2.98f, 0f, -5.4f, -2.42f, -5.4f, -5.4f)
                curveToRelative(0f, -1.81f, 0.89f, -3.42f, 2.26f, -4.4f)
                curveTo(12.92f, 3.04f, 12.46f, 3f, 12f, 3f)
                close()
            }
        }
    }

    val Bookmark: ImageVector by lazy {
        createIcon("Bookmark") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(17f, 3f)
                horizontalLineTo(7f)
                curveToRelative(-1.1f, 0f, -1.99f, 0.9f, -1.99f, 2f)
                lineTo(5f, 21f)
                lineToRelative(7f, -3f)
                lineToRelative(7f, 3f)
                verticalLineTo(5f)
                curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
                close()
            }
        }
    }

    val BookmarkBorder: ImageVector by lazy {
        createIcon("BookmarkBorder") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(17f, 3f)
                horizontalLineTo(7f)
                curveToRelative(-1.1f, 0f, -1.99f, 0.9f, -1.99f, 2f)
                lineTo(5f, 21f)
                lineToRelative(7f, -3f)
                lineToRelative(7f, 3f)
                verticalLineTo(5f)
                curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
                close()
                moveTo(17f, 18f)
                lineToRelative(-5f, -2.18f)
                lineTo(7f, 18f)
                verticalLineTo(5f)
                horizontalLineToRelative(10f)
                close()
            }
        }
    }

    val LocalFlorist: ImageVector by lazy {
        createIcon("LocalFlorist") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(12f, 22f)
                curveToRelative(4.97f, 0f, 9f, -4.03f, 9f, -9f)
                curveToRelative(-4.97f, 0f, -9f, 4.03f, -9f, 9f)
                close()
                moveTo(5.6f, 10.25f)
                curveToRelative(0f, 1.38f, 1.12f, 2.5f, 2.5f, 2.5f)
                curveToRelative(0.53f, 0f, 1.01f, -0.16f, 1.42f, -0.44f)
                lineToRelative(-0.02f, 0.19f)
                curveToRelative(0f, 1.38f, 1.12f, 2.5f, 2.5f, 2.5f)
                reflectiveCurveToRelative(2.5f, -1.12f, 2.5f, -2.5f)
                lineToRelative(-0.02f, -0.19f)
                curveToRelative(0.4f, 0.28f, 0.89f, 0.44f, 1.42f, 0.44f)
                curveToRelative(1.38f, 0f, 2.5f, -1.12f, 2.5f, -2.5f)
                curveToRelative(0f, -1f, -0.59f, -1.85f, -1.43f, -2.25f)
                curveToRelative(0.84f, -0.4f, 1.43f, -1.25f, 1.43f, -2.25f)
                curveToRelative(0f, -1.38f, -1.12f, -2.5f, -2.5f, -2.5f)
                curveToRelative(-0.53f, 0f, -1.01f, 0.16f, -1.42f, 0.44f)
                lineToRelative(0.02f, -0.19f)
                curveTo(14.5f, 2.12f, 13.38f, 1f, 12f, 1f)
                reflectiveCurveTo(9.5f, 2.12f, 9.5f, 3.5f)
                lineToRelative(0.02f, 0.19f)
                curveToRelative(-0.4f, -0.28f, -0.89f, -0.44f, -1.42f, -0.44f)
                curveToRelative(-1.38f, 0f, -2.5f, 1.12f, -2.5f, 2.5f)
                curveToRelative(0f, 1f, 0.59f, 1.85f, 1.43f, 2.25f)
                curveToRelative(-0.84f, 0.4f, -1.43f, 1.25f, -1.43f, 2.25f)
                close()
                moveTo(12f, 5.5f)
                curveToRelative(1.38f, 0f, 2.5f, 1.12f, 2.5f, 2.5f)
                reflectiveCurveToRelative(-1.12f, 2.5f, -2.5f, 2.5f)
                reflectiveCurveTo(9.5f, 9.38f, 9.5f, 8f)
                reflectiveCurveToRelative(1.12f, -2.5f, 2.5f, -2.5f)
                close()
                moveTo(3f, 13f)
                curveToRelative(0f, 4.97f, 4.03f, 9f, 9f, 9f)
                curveToRelative(0f, -4.97f, -4.03f, -9f, -9f, -9f)
                close()
            }
        }
    }

    val Bolt: ImageVector by lazy {
        createIcon("Bolt") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(11f, 21f)
                horizontalLineToRelative(-1f)
                lineToRelative(1f, -7f)
                horizontalLineTo(7.5f)
                curveToRelative(-0.58f, 0f, -0.57f, -0.32f, -0.38f, -0.66f)
                lineToRelative(0.07f, -0.12f)
                curveTo(8.48f, 10.94f, 10.42f, 7.54f, 13f, 3f)
                horizontalLineToRelative(1f)
                lineToRelative(-1f, 7f)
                horizontalLineToRelative(3.5f)
                curveToRelative(0.49f, 0f, 0.56f, 0.33f, 0.47f, 0.51f)
                lineToRelative(-0.07f, 0.15f)
                curveTo(12.96f, 17.55f, 11f, 21f, 11f, 21f)
                close()
            }
        }
    }

    val History: ImageVector by lazy {
        createIcon("History") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(13f, 3f)
                curveToRelative(-4.97f, 0f, -9f, 4.03f, -9f, 9f)
                horizontalLineTo(1f)
                lineToRelative(3.89f, 3.89f)
                lineToRelative(0.07f, 0.14f)
                lineTo(9f, 12f)
                horizontalLineTo(6f)
                curveToRelative(0f, -3.87f, 3.13f, -7f, 7f, -7f)
                reflectiveCurveToRelative(7f, 3.13f, 7f, 7f)
                reflectiveCurveToRelative(-3.13f, 7f, -7f, 7f)
                curveToRelative(-1.93f, 0f, -3.68f, -0.79f, -4.94f, -2.06f)
                lineToRelative(-1.42f, 1.42f)
                curveTo(8.27f, 19.99f, 10.51f, 21f, 13f, 21f)
                curveToRelative(4.97f, 0f, 9f, -4.03f, 9f, -9f)
                reflectiveCurveToRelative(-4.03f, -9f, -9f, -9f)
                close()
                moveTo(12f, 8f)
                verticalLineToRelative(5f)
                lineToRelative(4.28f, 2.54f)
                lineToRelative(0.72f, -1.21f)
                lineToRelative(-3.5f, -2.08f)
                verticalLineTo(8f)
                close()
            }
        }
    }

    val Celebration: ImageVector by lazy {
        createIcon("Celebration") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(2f, 22f)
                lineToRelative(14f, -5f)
                lineTo(7f, 8f)
                close()
                moveTo(12.35f, 16.18f)
                lineTo(6.82f, 12.65f)
                lineToRelative(6.32f, -2.35f)
                close()
                moveTo(14.53f, 12.53f)
                lineToRelative(0.59f, -4.43f)
                lineToRelative(4.35f, 1.95f)
                close()
                moveTo(16.5f, 5.5f)
                curveToRelative(-0.97f, 0f, -1.75f, -0.78f, -1.75f, -1.75f)
                reflectiveCurveTo(15.53f, 2f, 16.5f, 2f)
                reflectiveCurveToRelative(1.75f, 0.78f, 1.75f, 1.75f)
                reflectiveCurveTo(17.47f, 5.5f, 16.5f, 5.5f)
                close()
                moveTo(18.85f, 11.5f)
                lineToRelative(3.15f, -1f)
                lineToRelative(-1f, 3.15f)
                close()
            }
        }
    }



    val Pause: ImageVector by lazy {
        createIcon("Pause") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(6f, 19f)
                horizontalLineToRelative(4f)
                verticalLineTo(5f)
                horizontalLineTo(6f)
                close()
                moveTo(14f, 5f)
                verticalLineToRelative(14f)
                horizontalLineToRelative(4f)
                verticalLineTo(5f)
                close()
            }
        }
    }



    val Stop: ImageVector by lazy {
        createIcon("Stop") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(6f, 6f)
                horizontalLineToRelative(12f)
                verticalLineToRelative(12f)
                horizontalLineTo(6f)
                close()
            }
        }
    }

    val SkipNext: ImageVector by lazy {
        createIcon("SkipNext") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(6f, 18f)
                lineToRelative(8.5f, -6f)
                lineTo(6f, 6f)
                close()
                moveTo(16f, 6f)
                verticalLineToRelative(12f)
                horizontalLineToRelative(2f)
                verticalLineTo(6f)
                close()
            }
        }
    }

    val Verified: ImageVector by lazy {
        createIcon("Verified") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(23f, 12f)
                lineToRelative(-2.44f, -2.79f)
                lineToRelative(0.34f, -3.69f)
                lineToRelative(-3.61f, -0.82f)
                lineTo(15.4f, 1.5f)
                lineTo(12f, 2.96f)
                lineTo(8.6f, 1.5f)
                lineTo(6.71f, 4.69f)
                lineTo(3.1f, 5.5f)
                lineToRelative(0.34f, 3.7f)
                lineTo(1f, 12f)
                lineToRelative(2.44f, 2.79f)
                lineToRelative(-0.34f, 3.7f)
                lineToRelative(3.61f, 0.82f)
                lineTo(8.6f, 22.5f)
                lineTo(12f, 21.03f)
                lineToRelative(3.4f, 1.47f)
                lineToRelative(1.89f, -3.19f)
                lineToRelative(3.61f, -0.82f)
                lineToRelative(-0.34f, -3.69f)
                close()
                moveTo(10.09f, 16.72f)
                lineToRelative(-3.8f, -3.81f)
                lineToRelative(1.48f, -1.48f)
                lineToRelative(2.32f, 2.33f)
                lineToRelative(5.85f, -5.87f)
                lineToRelative(1.48f, 1.48f)
                close()
            }
        }
    }

    val Palette: ImageVector by lazy {
        createIcon("Palette") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(12f, 2f)
                curveTo(6.49f, 2f, 2f, 6.49f, 2f, 12f)
                reflectiveCurveToRelative(4.49f, 10f, 10f, 10f)
                curveToRelative(1.38f, 0f, 2.5f, -1.12f, 2.5f, -2.5f)
                curveToRelative(0f, -0.61f, -0.23f, -1.2f, -0.64f, -1.67f)
                curveToRelative(-0.08f, -0.1f, -0.13f, -0.21f, -0.13f, -0.33f)
                curveToRelative(0f, -0.28f, 0.22f, -0.5f, 0.5f, -0.5f)
                horizontalLineTo(16f)
                curveToRelative(3.31f, 0f, 6f, -2.69f, 6f, -6f)
                curveTo(22f, 6.04f, 17.51f, 2f, 12f, 2f)
                close()
                moveTo(6.5f, 13f)
                curveTo(5.67f, 13f, 5f, 12.33f, 5f, 11.5f)
                reflectiveCurveTo(5.67f, 10f, 6.5f, 10f)
                reflectiveCurveTo(8f, 10.67f, 8f, 11.5f)
                reflectiveCurveTo(7.33f, 13f, 6.5f, 13f)
                close()
                moveTo(9.5f, 9f)
                curveTo(8.67f, 9f, 8f, 8.33f, 8f, 7.5f)
                reflectiveCurveTo(8.67f, 6f, 9.5f, 6f)
                reflectiveCurveTo(11f, 6.67f, 11f, 7.5f)
                reflectiveCurveTo(10.33f, 9f, 9.5f, 9f)
                close()
                moveTo(14.5f, 9f)
                curveToRelative(-0.83f, 0f, -1.5f, -0.67f, -1.5f, -1.5f)
                reflectiveCurveTo(13.67f, 6f, 14.5f, 6f)
                reflectiveCurveTo(16f, 6.67f, 16f, 7.5f)
                reflectiveCurveTo(15.33f, 9f, 14.5f, 9f)
                close()
                moveTo(17.5f, 13f)
                curveToRelative(-0.83f, 0f, -1.5f, -0.67f, -1.5f, -1.5f)
                reflectiveCurveToRelative(0.67f, -1.5f, 1.5f, -1.5f)
                reflectiveCurveToRelative(1.5f, 0.67f, 1.5f, 1.5f)
                reflectiveCurveTo(18.33f, 13f, 17.5f, 13f)
                close()
            }
        }
    }

    val Timer: ImageVector by lazy {
        createIcon("Timer") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(15f, 1f)
                horizontalLineTo(9f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(6f)
                close()
                moveTo(11f, 14f)
                horizontalLineToRelative(2f)
                verticalLineTo(8f)
                horizontalLineToRelative(-2f)
                close()
                moveTo(19.03f, 7.39f)
                lineToRelative(1.42f, -1.42f)
                curveToRelative(-0.43f, -0.51f, -0.9f, -0.99f, -1.41f, -1.41f)
                lineToRelative(-1.42f, 1.42f)
                curveTo(16.07f, 4.74f, 14.12f, 4f, 12f, 4f)
                curveToRelative(-4.97f, 0f, -9f, 4.03f, -9f, 9f)
                reflectiveCurveToRelative(4.02f, 9f, 9f, 9f)
                reflectiveCurveToRelative(9f, -4.03f, 9f, -9f)
                curveToRelative(0f, -2.12f, -0.74f, -4.07f, -1.97f, -5.61f)
                close()
                moveTo(12f, 20f)
                curveToRelative(-3.87f, 0f, -7f, -3.13f, -7f, -7f)
                reflectiveCurveToRelative(3.13f, -7f, 7f, -7f)
                reflectiveCurveToRelative(7f, 3.13f, 7f, 7f)
                reflectiveCurveToRelative(-3.13f, 7f, -7f, 7f)
                close()
            }
        }
    }

    val ThumbUp: ImageVector by lazy {
        createIcon("ThumbUp") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(1f, 21f)
                horizontalLineToRelative(4f)
                verticalLineTo(9f)
                horizontalLineTo(1f)
                close()
                moveTo(23f, 10f)
                curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
                horizontalLineToRelative(-6.31f)
                lineToRelative(0.95f, -4.57f)
                lineToRelative(0.03f, -0.32f)
                curveToRelative(0f, -0.41f, -0.17f, -0.79f, -0.44f, -1.06f)
                lineTo(14.17f, 1f)
                lineTo(7.59f, 7.59f)
                curveTo(7.22f, 7.95f, 7f, 8.45f, 7f, 9f)
                verticalLineToRelative(10f)
                curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
                horizontalLineToRelative(9f)
                curveToRelative(0.83f, 0f, 1.54f, -0.5f, 1.84f, -1.22f)
                lineToRelative(3.02f, -7.05f)
                curveToRelative(0.09f, -0.23f, 0.14f, -0.47f, 0.14f, -0.73f)
                verticalLineToRelative(-2f)
                close()
            }
        }
    }

    val Error: ImageVector by lazy {
        createIcon("Error") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(12f, 2f)
                curveTo(6.48f, 2f, 2f, 6.48f, 2f, 12f)
                reflectiveCurveToRelative(4.48f, 10f, 10f, 10f)
                reflectiveCurveToRelative(10f, -4.48f, 10f, -10f)
                reflectiveCurveTo(17.52f, 2f, 12f, 2f)
                close()
                moveTo(13f, 17f)
                horizontalLineToRelative(-2f)
                verticalLineToRelative(-2f)
                horizontalLineToRelative(2f)
                close()
                moveTo(13f, 13f)
                horizontalLineToRelative(-2f)
                verticalLineTo(7f)
                horizontalLineToRelative(2f)
                close()
            }
        }
    }

    val Clear: ImageVector by lazy { Close }

    val SearchOff: ImageVector by lazy {
        createIcon("SearchOff") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(15.5f, 14f)
                horizontalLineToRelative(-0.79f)
                lineToRelative(-0.28f, -0.27f)
                curveToRelative(1.2f, -1.4f, 1.82f, -3.31f, 1.48f, -5.34f)
                curveToRelative(-0.47f, -2.78f, -2.79f, -5f, -5.59f, -5.34f)
                curveTo(5.26f, 2.52f, 1.52f, 6.26f, 2.05f, 11.32f)
                curveToRelative(0.34f, 2.8f, 2.56f, 5.12f, 5.34f, 5.59f)
                curveToRelative(2.03f, 0.34f, 3.94f, -0.28f, 5.34f, -1.48f)
                lineToRelative(0.27f, 0.28f)
                verticalLineToRelative(0.79f)
                lineToRelative(4.25f, 4.25f)
                curveToRelative(0.41f, 0.41f, 1.08f, 0.41f, 1.49f, 0f)
                curveToRelative(0.41f, -0.41f, 0.41f, -1.08f, 0f, -1.49f)
                close()
                moveTo(9.5f, 14f)
                curveTo(7.01f, 14f, 5f, 11.99f, 5f, 9.5f)
                reflectiveCurveTo(7.01f, 5f, 9.5f, 5f)
                reflectiveCurveTo(14f, 7.01f, 14f, 9.5f)
                reflectiveCurveTo(11.99f, 14f, 9.5f, 14f)
                close()
                moveTo(7.53f, 11.03f)
                lineToRelative(1.97f, -1.97f)
                lineTo(7.53f, 7.09f)
                lineToRelative(1.06f, -1.06f)
                lineToRelative(1.97f, 1.97f)
                lineToRelative(1.97f, -1.97f)
                lineToRelative(1.06f, 1.06f)
                lineToRelative(-1.97f, 1.97f)
                lineToRelative(1.97f, 1.97f)
                lineToRelative(-1.06f, 1.06f)
                lineToRelative(-1.97f, -1.97f)
                lineToRelative(-1.97f, 1.97f)
                close()
            }
        }
    }

    val Wallpaper: ImageVector by lazy {
        createIcon("Wallpaper") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(4f, 4f)
                horizontalLineToRelative(7f)
                verticalLineTo(2f)
                horizontalLineTo(4f)
                curveToRelative(-1.1f, 0f, -2f, 0.9f, -2f, 2f)
                verticalLineToRelative(7f)
                horizontalLineToRelative(2f)
                close()
                moveTo(10f, 10f)
                lineToRelative(-4f, 5f)
                horizontalLineToRelative(12f)
                lineToRelative(-3f, -4f)
                lineToRelative(-2.03f, 2.71f)
                close()
                moveTo(17f, 8.5f)
                curveToRelative(0f, -0.83f, -0.67f, -1.5f, -1.5f, -1.5f)
                reflectiveCurveTo(14f, 7.67f, 14f, 8.5f)
                reflectiveCurveToRelative(0.67f, 1.5f, 1.5f, 1.5f)
                reflectiveCurveTo(17f, 9.33f, 17f, 8.5f)
                close()
                moveTo(20f, 2f)
                horizontalLineToRelative(-7f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(7f)
                verticalLineToRelative(7f)
                horizontalLineToRelative(2f)
                verticalLineTo(4f)
                curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
                close()
                moveTo(20f, 20f)
                horizontalLineToRelative(-7f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(7f)
                curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
                verticalLineToRelative(-7f)
                horizontalLineToRelative(-2f)
                close()
                moveTo(4f, 13f)
                horizontalLineTo(2f)
                verticalLineToRelative(7f)
                curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
                horizontalLineToRelative(7f)
                verticalLineToRelative(-2f)
                horizontalLineTo(4f)
                close()
            }
        }
    }

    // Additional icons aliased from existing ones or simple shapes
    val HourglassEmpty: ImageVector by lazy {
        createIcon("HourglassEmpty") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(6f, 2f)
                verticalLineToRelative(6f)
                lineToRelative(4f, 4f)
                lineToRelative(-4f, 4f)
                verticalLineToRelative(6f)
                horizontalLineToRelative(12f)
                verticalLineToRelative(-6f)
                lineToRelative(-4f, -4f)
                lineToRelative(4f, -4f)
                verticalLineTo(2f)
                close()
                moveTo(16f, 16.5f)
                verticalLineTo(20f)
                horizontalLineTo(8f)
                verticalLineToRelative(-3.5f)
                lineToRelative(4f, -4f)
                close()
                moveTo(12f, 11.5f)
                lineToRelative(-4f, -4f)
                verticalLineTo(4f)
                horizontalLineToRelative(8f)
                verticalLineToRelative(3.5f)
                close()
            }
        }
    }

    val Create: ImageVector by lazy { Edit }
    val Image: ImageVector by lazy {
        createIcon("Image") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(21f, 19f)
                verticalLineTo(5f)
                curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
                horizontalLineTo(5f)
                curveToRelative(-1.1f, 0f, -2f, 0.9f, -2f, 2f)
                verticalLineToRelative(14f)
                curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
                horizontalLineToRelative(14f)
                curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
                close()
                moveTo(8.5f, 13.5f)
                lineToRelative(2.5f, 3.01f)
                lineTo(14.5f, 12f)
                lineToRelative(4.5f, 6f)
                horizontalLineTo(5f)
                close()
            }
        }
    }

    val FilterList: ImageVector by lazy {
        createIcon("FilterList") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(10f, 18f)
                horizontalLineToRelative(4f)
                verticalLineToRelative(-2f)
                horizontalLineToRelative(-4f)
                close()
                moveTo(3f, 6f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(18f)
                verticalLineTo(6f)
                close()
                moveTo(6f, 13f)
                horizontalLineToRelative(12f)
                verticalLineToRelative(-2f)
                horizontalLineTo(6f)
                close()
            }
        }
    }

    val Assignment: ImageVector by lazy {
        createIcon("Assignment") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(19f, 3f)
                horizontalLineToRelative(-4.18f)
                curveTo(14.4f, 1.84f, 13.3f, 1f, 12f, 1f)
                curveToRelative(-1.3f, 0f, -2.4f, 0.84f, -2.82f, 2f)
                horizontalLineTo(5f)
                curveToRelative(-1.1f, 0f, -2f, 0.9f, -2f, 2f)
                verticalLineToRelative(14f)
                curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
                horizontalLineToRelative(14f)
                curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
                verticalLineTo(5f)
                curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
                close()
                moveTo(12f, 3f)
                curveToRelative(0.55f, 0f, 1f, 0.45f, 1f, 1f)
                reflectiveCurveToRelative(-0.45f, 1f, -1f, 1f)
                reflectiveCurveToRelative(-1f, -0.45f, -1f, -1f)
                reflectiveCurveToRelative(0.45f, -1f, 1f, -1f)
                close()
                moveTo(14f, 17f)
                horizontalLineTo(7f)
                verticalLineToRelative(-2f)
                horizontalLineToRelative(7f)
                close()
                moveTo(17f, 13f)
                horizontalLineTo(7f)
                verticalLineToRelative(-2f)
                horizontalLineToRelative(10f)
                close()
                moveTo(17f, 9f)
                horizontalLineTo(7f)
                verticalLineTo(7f)
                horizontalLineToRelative(10f)
                close()
            }
        }
    }

    val BarChart: ImageVector by lazy {
        createIcon("BarChart") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(5f, 9.2f)
                horizontalLineToRelative(3f)
                verticalLineTo(19f)
                horizontalLineTo(5f)
                close()
                moveTo(10.6f, 5f)
                horizontalLineToRelative(2.8f)
                verticalLineToRelative(14f)
                horizontalLineToRelative(-2.8f)
                close()
                moveTo(16.2f, 13f)
                horizontalLineTo(19f)
                verticalLineToRelative(6f)
                horizontalLineToRelative(-2.8f)
                close()
            }
        }
    }

    val CalendarMonth: ImageVector by lazy {
        createIcon("CalendarMonth") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(19f, 4f)
                horizontalLineToRelative(-1f)
                verticalLineTo(2f)
                horizontalLineToRelative(-2f)
                verticalLineToRelative(2f)
                horizontalLineTo(8f)
                verticalLineTo(2f)
                horizontalLineTo(6f)
                verticalLineToRelative(2f)
                horizontalLineTo(5f)
                curveToRelative(-1.11f, 0f, -1.99f, 0.9f, -1.99f, 2f)
                lineTo(3f, 20f)
                curveToRelative(0f, 1.1f, 0.89f, 2f, 2f, 2f)
                horizontalLineToRelative(14f)
                curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
                verticalLineTo(6f)
                curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
                close()
                moveTo(19f, 20f)
                horizontalLineTo(5f)
                verticalLineTo(10f)
                horizontalLineToRelative(14f)
                close()
                moveTo(19f, 8f)
                horizontalLineTo(5f)
                verticalLineTo(6f)
                horizontalLineToRelative(14f)
                close()
            }
        }
    }

    val Code: ImageVector by lazy {
        createIcon("Code") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(9.4f, 16.6f)
                lineTo(4.8f, 12f)
                lineToRelative(4.6f, -4.6f)
                lineTo(8f, 6f)
                lineToRelative(-6f, 6f)
                lineToRelative(6f, 6f)
                close()
                moveTo(14.6f, 16.6f)
                lineToRelative(4.6f, -4.6f)
                lineToRelative(-4.6f, -4.6f)
                lineTo(16f, 6f)
                lineToRelative(6f, 6f)
                lineToRelative(-6f, 6f)
                close()
            }
        }
    }

    val Dashboard: ImageVector by lazy {
        createIcon("Dashboard") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(3f, 13f)
                horizontalLineToRelative(8f)
                verticalLineTo(3f)
                horizontalLineTo(3f)
                close()
                moveTo(3f, 21f)
                horizontalLineToRelative(8f)
                verticalLineToRelative(-6f)
                horizontalLineTo(3f)
                close()
                moveTo(13f, 21f)
                horizontalLineToRelative(8f)
                verticalLineTo(11f)
                horizontalLineToRelative(-8f)
                close()
                moveTo(13f, 3f)
                verticalLineToRelative(6f)
                horizontalLineToRelative(8f)
                verticalLineTo(3f)
                close()
            }
        }
    }

    val Explore: ImageVector by lazy {
        createIcon("Explore") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(12f, 10.9f)
                curveToRelative(-0.61f, 0f, -1.1f, 0.49f, -1.1f, 1.1f)
                reflectiveCurveToRelative(0.49f, 1.1f, 1.1f, 1.1f)
                curveToRelative(0.61f, 0f, 1.1f, -0.49f, 1.1f, -1.1f)
                reflectiveCurveToRelative(-0.49f, -1.1f, -1.1f, -1.1f)
                close()
                moveTo(12f, 2f)
                curveTo(6.48f, 2f, 2f, 6.48f, 2f, 12f)
                reflectiveCurveToRelative(4.48f, 10f, 10f, 10f)
                reflectiveCurveToRelative(10f, -4.48f, 10f, -10f)
                reflectiveCurveTo(17.52f, 2f, 12f, 2f)
                close()
                moveTo(14.19f, 14.19f)
                lineTo(6f, 18f)
                lineToRelative(3.81f, -8.19f)
                lineTo(18f, 6f)
                close()
            }
        }
    }

    val Forum: ImageVector by lazy {
        createIcon("Forum") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(21f, 6f)
                horizontalLineToRelative(-2f)
                verticalLineToRelative(9f)
                horizontalLineTo(6f)
                verticalLineToRelative(2f)
                curveToRelative(0f, 0.55f, 0.45f, 1f, 1f, 1f)
                horizontalLineToRelative(11f)
                lineToRelative(4f, 4f)
                verticalLineTo(7f)
                curveToRelative(0f, -0.55f, -0.45f, -1f, -1f, -1f)
                close()
                moveTo(17f, 12f)
                verticalLineTo(3f)
                curveToRelative(0f, -0.55f, -0.45f, -1f, -1f, -1f)
                horizontalLineTo(3f)
                curveToRelative(-0.55f, 0f, -1f, 0.45f, -1f, 1f)
                verticalLineToRelative(14f)
                lineToRelative(4f, -4f)
                horizontalLineToRelative(10f)
                curveToRelative(0.55f, 0f, 1f, -0.45f, 1f, -1f)
                close()
            }
        }
    }

    val Grass: ImageVector by lazy { LocalFlorist }
    val Eco: ImageVector by lazy { LocalFlorist }
    val Park: ImageVector by lazy { LocalFlorist }
    val Nature: ImageVector by lazy { LocalFlorist }

    val Shield: ImageVector by lazy {
        createIcon("Shield") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(12f, 1f)
                lineTo(3f, 5f)
                verticalLineToRelative(6f)
                curveToRelative(0f, 5.55f, 3.84f, 10.74f, 9f, 12f)
                curveToRelative(5.16f, -1.26f, 9f, -6.45f, 9f, -12f)
                verticalLineTo(5f)
                close()
            }
        }
    }

    val WorkspacePremium: ImageVector by lazy { EmojiEvents }
    val MilitaryTech: ImageVector by lazy { EmojiEvents }
    val Leaderboard: ImageVector by lazy { BarChart }
    val ShowChart: ImageVector by lazy { TrendingUp }
    val HistoryEdu: ImageVector by lazy { MenuBook }
    val EditNote: ImageVector by lazy { Edit }
    val TipsAndUpdates: ImageVector by lazy { Lightbulb }
    val MarkEmailRead: ImageVector by lazy { Mail }
    val MailOutline: ImageVector by lazy { Mail }
    val ChatBubble: ImageVector by lazy { Chat }
    val NotificationsActive: ImageVector by lazy { Notifications }
    val Whatshot: ImageVector by lazy { LocalFireDepartment }
    val RocketLaunch: ImageVector by lazy { LocalFireDepartment }
    val Rocket: ImageVector by lazy { LocalFireDepartment }

    val Fingerprint: ImageVector by lazy { Lock }
    val Security: ImageVector by lazy { Lock }
    val Policy: ImageVector by lazy { Shield }
    val VerifiedUser: ImageVector by lazy { Verified }
    val Groups: ImageVector by lazy { Person }
    val Diversity: ImageVector by lazy { Person }
    val Elderly: ImageVector by lazy { Person }
    val Visibility: ImageVector by lazy {
        createIcon("Visibility") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(12f, 4.5f)
                curveTo(7f, 4.5f, 2.73f, 7.61f, 1f, 12f)
                curveToRelative(1.73f, 4.39f, 6f, 7.5f, 11f, 7.5f)
                reflectiveCurveToRelative(9.27f, -3.11f, 11f, -7.5f)
                curveToRelative(-1.73f, -4.39f, -6f, -7.5f, -11f, -7.5f)
                close()
                moveTo(12f, 17f)
                curveToRelative(-2.76f, 0f, -5f, -2.24f, -5f, -5f)
                reflectiveCurveToRelative(2.24f, -5f, 5f, -5f)
                reflectiveCurveToRelative(5f, 2.24f, 5f, 5f)
                reflectiveCurveToRelative(-2.24f, 5f, -5f, 5f)
                close()
                moveTo(12f, 9f)
                curveToRelative(-1.66f, 0f, -3f, 1.34f, -3f, 3f)
                reflectiveCurveToRelative(1.34f, 3f, 3f, 3f)
                reflectiveCurveToRelative(3f, -1.34f, 3f, -3f)
                reflectiveCurveToRelative(-1.34f, -3f, -3f, -3f)
                close()
            }
        }
    }
    val FitnessCenter: ImageVector by lazy {
        createIcon("FitnessCenter") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(20.57f, 14.86f)
                lineTo(22f, 13.43f)
                lineTo(20.57f, 12f)
                lineTo(17f, 15.57f)
                lineTo(8.43f, 7f)
                lineTo(12f, 3.43f)
                lineTo(10.57f, 2f)
                lineTo(9.14f, 3.43f)
                lineTo(7.71f, 2f)
                lineTo(5.57f, 4.14f)
                lineTo(4.14f, 2.71f)
                lineTo(2.71f, 4.14f)
                lineToRelative(1.43f, 1.43f)
                lineTo(2f, 7.71f)
                lineToRelative(1.43f, 1.43f)
                lineTo(2f, 10.57f)
                lineTo(3.43f, 12f)
                lineTo(7f, 8.43f)
                lineTo(15.57f, 17f)
                lineTo(12f, 20.57f)
                lineTo(13.43f, 22f)
                lineToRelative(1.43f, -1.43f)
                lineTo(16.29f, 22f)
                lineToRelative(2.14f, -2.14f)
                lineToRelative(1.43f, 1.43f)
                lineToRelative(1.43f, -1.43f)
                lineToRelative(-1.43f, -1.43f)
                lineTo(22f, 16.29f)
                close()
            }
        }
    }
    val RecordVoiceOver: ImageVector by lazy { Mic }
    val Hearing: ImageVector by lazy { Mic }
    val SentimentSatisfied: ImageVector by lazy { EmojiEvents }
    val SentimentVerySatisfied: ImageVector by lazy { EmojiEvents }
    val SentimentDissatisfied: ImageVector by lazy { Warning }
    val Mood: ImageVector by lazy { EmojiEvents }
    val EmojiNature: ImageVector by lazy { LocalFlorist }
    val Terrain: ImageVector by lazy { LocalFlorist }
    val Landscape: ImageVector by lazy { LocalFlorist }
    val Water: ImageVector by lazy { LocalFlorist }
    val Grain: ImageVector by lazy { LocalFlorist }
    val Flare: ImageVector by lazy { WbSunny }
    val LightMode: ImageVector by lazy { WbSunny }
    val Nightlight: ImageVector by lazy { NightsStay }
    val WbTwilight: ImageVector by lazy { NightsStay }
    val BrightnessAuto: ImageVector by lazy { WbSunny }
    val Science: ImageVector by lazy { Psychology }
    val PsychologyAlt: ImageVector by lazy { Psychology }
    val AutoGraph: ImageVector by lazy { TrendingUp }
    val Flag: ImageVector by lazy {
        createIcon("Flag") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(14.4f, 6f)
                lineTo(14f, 4f)
                horizontalLineTo(5f)
                verticalLineToRelative(17f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(-7f)
                horizontalLineToRelative(5.6f)
                lineToRelative(0.4f, 2f)
                horizontalLineToRelative(7f)
                verticalLineTo(6f)
                close()
            }
        }
    }

    val Map: ImageVector by lazy { Place }
    val GridView: ImageVector by lazy { Dashboard }
    val Storage: ImageVector by lazy { Dashboard }
    val Memory: ImageVector by lazy { Dashboard }
    val Inventory: ImageVector by lazy { Dashboard }
    val CollectionsBookmark: ImageVector by lazy { Book }
    val PhotoLibrary: ImageVector by lazy { Image }
    val FilterFrames: ImageVector by lazy { Image }
    val FilterAlt: ImageVector by lazy { FilterList }
    val Tune: ImageVector by lazy { Settings }
    val Help: ImageVector by lazy { Info }
    val ExitToApp: ImageVector by lazy { ArrowForward }
    val ArrowUpward: ImageVector by lazy { ExpandLess }
    val ArrowDownward: ImageVector by lazy { ExpandMore }
    val ArrowDropDown: ImageVector by lazy { ExpandMore }
    val KeyboardArrowUp: ImageVector by lazy { ExpandLess }
    val KeyboardArrowDown: ImageVector by lazy { ExpandMore }
    val Remove: ImageVector by lazy { Close }
    val ContentCopy: ImageVector by lazy { Edit }
    val PlayCircle: ImageVector by lazy { PlayArrow }
    val PhoneAndroid: ImageVector by lazy { Phone }
    val Vibration: ImageVector by lazy { Phone }
    val Today: ImageVector by lazy { CalendarMonth }
    val Translate: ImageVector by lazy { MenuBook }
    val VisibilityOff: ImageVector by lazy { Close }
    val Moving: ImageVector by lazy { TrendingUp }
    val NewReleases: ImageVector by lazy { AutoAwesome }
    val CloudOff: ImageVector by lazy { Close }
    val Cloud: ImageVector by lazy {
        createIcon("Cloud") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(19.35f, 10.04f)
                curveTo(18.67f, 6.59f, 15.64f, 4f, 12f, 4f)
                curveToRelative(-2.79f, 0f, -5.2f, 1.47f, -6.57f, 3.66f)
                curveTo(2.35f, 8.03f, 0f, 10.6f, 0f, 13.75f)
                curveToRelative(0f, 3.45f, 2.8f, 6.25f, 6.25f, 6.25f)
                horizontalLineTo(19f)
                curveToRelative(2.76f, 0f, 5f, -2.24f, 5f, -5f)
                curveToRelative(0f, -2.64f, -2.05f, -4.78f, -4.65f, -4.96f)
                close()
            }
        }
    }
    val CloudDone: ImageVector by lazy { Cloud }
    val CloudSync: ImageVector by lazy { Cloud }
    val Cached: ImageVector by lazy { Refresh }
    val WifiOff: ImageVector by lazy { CloudOff }
    val Api: ImageVector by lazy { Code }
    val BugReport: ImageVector by lazy { ErrorOutline }
    val Construction: ImageVector by lazy { Settings }
    val Handyman: ImageVector by lazy { Settings }
    val Architecture: ImageVector by lazy { Settings }
    val Foundation: ImageVector by lazy { Home }
    val Badge: ImageVector by lazy { Verified }
    val Diamond: ImageVector by lazy { Star }
    val LocalOffer: ImageVector by lazy { Star }
    val HourglassBottom: ImageVector by lazy { HourglassEmpty }
    val Handshake: ImageVector by lazy { Person }

    val LockOpen: ImageVector by lazy {
        createIcon("LockOpen") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(12f, 17f)
                curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
                reflectiveCurveToRelative(-0.9f, -2f, -2f, -2f)
                reflectiveCurveToRelative(-2f, 0.9f, -2f, 2f)
                reflectiveCurveToRelative(0.9f, 2f, 2f, 2f)
                close()
                moveTo(18f, 8f)
                horizontalLineToRelative(-1f)
                verticalLineTo(6f)
                curveToRelative(0f, -2.76f, -2.24f, -5f, -5f, -5f)
                reflectiveCurveTo(7f, 3.24f, 7f, 6f)
                horizontalLineToRelative(1.9f)
                curveToRelative(0f, -1.71f, 1.39f, -3.1f, 3.1f, -3.1f)
                curveToRelative(1.71f, 0f, 3.1f, 1.39f, 3.1f, 3.1f)
                verticalLineToRelative(2f)
                horizontalLineTo(6f)
                curveToRelative(-1.1f, 0f, -2f, 0.9f, -2f, 2f)
                verticalLineToRelative(10f)
                curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
                horizontalLineToRelative(12f)
                curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
                verticalLineTo(10f)
                curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
                close()
                moveTo(18f, 20f)
                horizontalLineTo(6f)
                verticalLineTo(10f)
                horizontalLineToRelative(12f)
                close()
            }
        }
    }

    // Additional missing icons
    val NoteAlt: ImageVector by lazy {
        createIcon("NoteAlt") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(19f, 3f)
                horizontalLineTo(14.82f)
                curveTo(14.4f, 1.84f, 13.3f, 1f, 12f, 1f)
                curveToRelative(-1.3f, 0f, -2.4f, 0.84f, -2.82f, 2f)
                horizontalLineTo(5f)
                curveToRelative(-1.1f, 0f, -2f, 0.9f, -2f, 2f)
                verticalLineToRelative(14f)
                curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
                horizontalLineToRelative(14f)
                curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
                verticalLineTo(5f)
                curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
                close()
                moveTo(12f, 3f)
                curveToRelative(0.55f, 0f, 1f, 0.45f, 1f, 1f)
                reflectiveCurveToRelative(-0.45f, 1f, -1f, 1f)
                reflectiveCurveToRelative(-1f, -0.45f, -1f, -1f)
                reflectiveCurveToRelative(0.45f, -1f, 1f, -1f)
                close()
                moveTo(7f, 7f)
                horizontalLineToRelative(10f)
                verticalLineToRelative(2f)
                horizontalLineTo(7f)
                close()
                moveTo(7f, 11f)
                horizontalLineToRelative(10f)
                verticalLineToRelative(2f)
                horizontalLineTo(7f)
                close()
                moveTo(7f, 15f)
                horizontalLineToRelative(7f)
                verticalLineToRelative(2f)
                horizontalLineTo(7f)
                close()
            }
        }
    }

    val NoteAdd: ImageVector by lazy {
        createIcon("NoteAdd") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(14f, 2f)
                horizontalLineTo(6f)
                curveToRelative(-1.1f, 0f, -1.99f, 0.9f, -1.99f, 2f)
                lineTo(4f, 20f)
                curveToRelative(0f, 1.1f, 0.89f, 2f, 1.99f, 2f)
                horizontalLineTo(18f)
                curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
                verticalLineTo(8f)
                close()
                moveTo(16f, 16f)
                horizontalLineToRelative(-3f)
                verticalLineToRelative(3f)
                horizontalLineToRelative(-2f)
                verticalLineToRelative(-3f)
                horizontalLineTo(8f)
                verticalLineToRelative(-2f)
                horizontalLineToRelative(3f)
                verticalLineToRelative(-3f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(3f)
                horizontalLineToRelative(3f)
                close()
                moveTo(13f, 9f)
                verticalLineTo(3.5f)
                lineTo(18.5f, 9f)
                close()
            }
        }
    }

    val BookmarkRemove: ImageVector by lazy {
        createIcon("BookmarkRemove") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(17f, 3f)
                horizontalLineTo(7f)
                curveToRelative(-1.1f, 0f, -1.99f, 0.9f, -1.99f, 2f)
                lineTo(5f, 21f)
                lineToRelative(7f, -3f)
                lineToRelative(7f, 3f)
                verticalLineTo(5f)
                curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
                close()
                moveTo(15f, 11f)
                horizontalLineTo(9f)
                verticalLineTo(9f)
                horizontalLineToRelative(6f)
                close()
            }
        }
    }

    val Diversity3: ImageVector by lazy { Person }

    // Outlined versions - these are aliased to filled versions since we don't have the outlined library
    // In practice, the visual difference is minimal for most use cases
    object Outlined {
        val Home: ImageVector get() = ProdyIcons.Home
        val Book: ImageVector get() = ProdyIcons.Book
        val BarChart: ImageVector get() = ProdyIcons.BarChart
        val Person: ImageVector get() = ProdyIcons.Person
        val BugReport: ImageVector get() = ProdyIcons.BugReport
        val Shield: ImageVector get() = ProdyIcons.Shield
        val Info: ImageVector get() = ProdyIcons.Info
        val LocalFireDepartment: ImageVector get() = ProdyIcons.LocalFireDepartment
        val Edit: ImageVector get() = ProdyIcons.Edit
        val CheckCircle: ImageVector get() = ProdyIcons.CheckCircle
        val Warning: ImageVector get() = ProdyIcons.Warning
        val EmojiEvents: ImageVector get() = ProdyIcons.EmojiEvents
        val Groups: ImageVector get() = ProdyIcons.Groups
        val Schedule: ImageVector get() = ProdyIcons.Schedule
        val Flag: ImageVector get() = ProdyIcons.Flag
        val Bolt: ImageVector get() = ProdyIcons.Bolt
        val Handshake: ImageVector get() = ProdyIcons.Handshake
        val CalendarMonth: ImageVector get() = ProdyIcons.CalendarMonth
        val Settings: ImageVector get() = ProdyIcons.Settings
        val FormatQuote: ImageVector get() = ProdyIcons.FormatQuote
        val Psychology: ImageVector get() = ProdyIcons.Psychology
        val TrendingUp: ImageVector get() = ProdyIcons.TrendingUp
        val TextFormat: ImageVector get() = ProdyIcons.MenuBook
        val School: ImageVector get() = ProdyIcons.School
        val ChatBubble: ImageVector get() = ProdyIcons.ChatBubble
        val Email: ImageVector get() = ProdyIcons.Email
        val Lock: ImageVector get() = ProdyIcons.Lock
        val SearchOff: ImageVector get() = ProdyIcons.SearchOff
        val AutoStories: ImageVector get() = ProdyIcons.AutoStories
        val BookmarkBorder: ImageVector get() = ProdyIcons.BookmarkBorder
        val HelpOutline: ImageVector get() = ProdyIcons.Info
        val Search: ImageVector get() = ProdyIcons.Search
        val Star: ImageVector get() = ProdyIcons.Star
        val ArrowForward: ImageVector get() = ProdyIcons.ArrowForward
        val Send: ImageVector get() = ProdyIcons.Send
        val OpenInNew: ImageVector get() = ProdyIcons.ArrowForward
        val SelfImprovement: ImageVector get() = ProdyIcons.SelfImprovement
        val Refresh: ImageVector get() = ProdyIcons.Refresh
        val Park: ImageVector get() = ProdyIcons.Park
        val Spa: ImageVector get() = ProdyIcons.Spa
        val CameraAlt: ImageVector get() = ProdyIcons.Image
        val Stop: ImageVector get() = ProdyIcons.Stop
        val Mic: ImageVector get() = ProdyIcons.Mic
        val Pause: ImageVector get() = ProdyIcons.Pause
        val PlayArrow: ImageVector get() = ProdyIcons.PlayArrow
        val Visibility: ImageVector get() = ProdyIcons.Visibility
        val Close: ImageVector get() = ProdyIcons.Close
        val ChevronRight: ImageVector get() = ProdyIcons.ChevronRight
    }

    // Rounded versions - aliased to filled versions
    object Rounded {
        val Close: ImageVector get() = ProdyIcons.Close
        val AutoAwesome: ImageVector get() = ProdyIcons.AutoAwesome
        val Star: ImageVector get() = ProdyIcons.Star
        val Token: ImageVector get() = ProdyIcons.Star  // No direct Token icon, using Star
        val Favorite: ImageVector get() = ProdyIcons.Favorite
        val TrendingUp: ImageVector get() = ProdyIcons.TrendingUp
        val Book: ImageVector get() = ProdyIcons.Book
        val Send: ImageVector get() = ProdyIcons.Send
        val Mail: ImageVector get() = ProdyIcons.Mail
        val EmojiEvents: ImageVector get() = ProdyIcons.EmojiEvents
        val Psychology: ImageVector get() = ProdyIcons.Psychology
        val Stars: ImageVector get() = ProdyIcons.Stars
        val LocalFireDepartment: ImageVector get() = ProdyIcons.LocalFireDepartment
        val Celebration: ImageVector get() = ProdyIcons.Celebration
        val WbSunny: ImageVector get() = ProdyIcons.WbSunny
        val EditNote: ImageVector get() = ProdyIcons.EditNote
        val TextFields: ImageVector get() = ProdyIcons.MenuBook  // No direct TextFields, using MenuBook
        val CalendarMonth: ImageVector get() = ProdyIcons.CalendarMonth
        val Eco: ImageVector get() = ProdyIcons.Eco
        val School: ImageVector get() = ProdyIcons.School
        val Schedule: ImageVector get() = ProdyIcons.Schedule
    }

    // AutoMirrored icons - using direct references to non-mirrored versions
    object AutoMirrored {
        object Filled {
            val ArrowBack: ImageVector get() = ProdyIcons.ArrowBack
            val ArrowForward: ImageVector get() = ProdyIcons.ArrowForward
            val KeyboardArrowRight: ImageVector get() = ProdyIcons.ChevronRight
            val List: ImageVector get() = ProdyIcons.List
            val Send: ImageVector get() = ProdyIcons.Send
            // Extended library icons - aliased to non-mirrored versions
            val Chat: ImageVector get() = ProdyIcons.Chat
            val HelpOutline: ImageVector get() = ProdyIcons.HelpOutline
            val MenuBook: ImageVector get() = ProdyIcons.MenuBook
            val TrendingUp: ImageVector get() = ProdyIcons.TrendingUp
            val TrendingDown: ImageVector get() = ProdyIcons.TrendingDown
            val Undo: ImageVector get() = ProdyIcons.Refresh  // Using Refresh as Undo alternative
            val VolumeUp: ImageVector get() = ProdyIcons.Mic  // Using Mic as VolumeUp alternative
        }
        object Rounded {
            val ArrowForward: ImageVector get() = ProdyIcons.ArrowForward
        }
    }

    // The "let" is probably a typo - it's not a real icon
    val let: ImageVector by lazy { Info }

    // =========================================================================
    // Additional missing icons for UI screens
    // These are aliased to semantically similar existing icons
    // =========================================================================

    // Email/Communication icons
    val AlternateEmail: ImageVector by lazy { Email }

    // Analytics/Data icons
    val Analytics: ImageVector by lazy { BarChart }

    // Document/Content icons
    val Article: ImageVector by lazy { MenuBook }
    val StickyNote2: ImageVector by lazy { NoteAlt }
    val Summarize: ImageVector by lazy { MenuBook }

    // Calendar/Time icons
    val CalendarToday: ImageVector by lazy { CalendarMonth }

    // Shape/UI icons
    val Circle: ImageVector by lazy {
        createIcon("Circle") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(12f, 2f)
                curveTo(6.47f, 2f, 2f, 6.47f, 2f, 12f)
                reflectiveCurveToRelative(4.47f, 10f, 10f, 10f)
                reflectiveCurveToRelative(10f, -4.47f, 10f, -10f)
                reflectiveCurveTo(17.53f, 2f, 12f, 2f)
                close()
            }
        }
    }

    // Contact/People icons
    val Contacts: ImageVector by lazy { Person }




    // Inventory/Storage icons
    val Inventory2: ImageVector by lazy { Dashboard }

    // Loop/Repeat icons
    val Loop: ImageVector by lazy { Refresh }

    // Menu/Options icons
    val MoreHoriz: ImageVector by lazy {
        createIcon("MoreHoriz") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(6f, 10f)
                curveToRelative(-1.1f, 0f, -2f, 0.9f, -2f, 2f)
                reflectiveCurveToRelative(0.9f, 2f, 2f, 2f)
                reflectiveCurveToRelative(2f, -0.9f, 2f, -2f)
                reflectiveCurveToRelative(-0.9f, -2f, -2f, -2f)
                close()
                moveTo(18f, 10f)
                curveToRelative(-1.1f, 0f, -2f, 0.9f, -2f, 2f)
                reflectiveCurveToRelative(0.9f, 2f, 2f, 2f)
                reflectiveCurveToRelative(2f, -0.9f, 2f, -2f)
                reflectiveCurveToRelative(-0.9f, -2f, -2f, -2f)
                close()
                moveTo(12f, 10f)
                curveToRelative(-1.1f, 0f, -2f, 0.9f, -2f, 2f)
                reflectiveCurveToRelative(0.9f, 2f, 2f, 2f)
                reflectiveCurveToRelative(2f, -0.9f, 2f, -2f)
                reflectiveCurveToRelative(-0.9f, -2f, -2f, -2f)
                close()
            }
        }
    }

    // Expand/Fullscreen icons
    val OpenInFull: ImageVector by lazy {
        createIcon("OpenInFull") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(21f, 11f)
                verticalLineTo(3f)
                horizontalLineToRelative(-8f)
                lineToRelative(3.29f, 3.29f)
                lineToRelative(-10f, 10f)
                lineTo(3f, 13f)
                verticalLineToRelative(8f)
                horizontalLineToRelative(8f)
                lineToRelative(-3.29f, -3.29f)
                lineToRelative(10f, -10f)
                close()
            }
        }
    }

    // Quiz/Question icons
    val Quiz: ImageVector by lazy { Help }

    // Shuffle/Randomize icons
    val Shuffle: ImageVector by lazy {
        createIcon("Shuffle") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(10.59f, 9.17f)
                lineTo(5.41f, 4f)
                lineTo(4f, 5.41f)
                lineToRelative(5.17f, 5.17f)
                close()
                moveTo(14.5f, 4f)
                lineToRelative(2.04f, 2.04f)
                lineTo(4f, 18.59f)
                lineTo(5.41f, 20f)
                lineTo(17.96f, 7.46f)
                lineTo(20f, 9.5f)
                verticalLineTo(4f)
                close()
                moveTo(14.83f, 13.41f)
                lineToRelative(-1.41f, 1.41f)
                lineToRelative(3.13f, 3.13f)
                lineTo(14.5f, 20f)
                horizontalLineTo(20f)
                verticalLineToRelative(-5.5f)
                lineToRelative(-2.04f, 2.04f)
                close()
            }
        }
    }

    // Swap/Exchange icons
    val SwapHoriz: ImageVector by lazy {
        createIcon("SwapHoriz") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(6.99f, 11f)
                lineTo(3f, 15f)
                lineToRelative(3.99f, 4f)
                verticalLineToRelative(-3f)
                horizontalLineTo(14f)
                verticalLineToRelative(-2f)
                horizontalLineTo(6.99f)
                close()
                moveTo(21f, 9f)
                lineToRelative(-3.99f, -4f)
                verticalLineToRelative(3f)
                horizontalLineTo(10f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(7.01f)
                verticalLineToRelative(3f)
                close()
            }
        }
    }

    // Tag/Label icons
    val Tag: ImageVector by lazy { LocalOffer }

    // Tips/Help icons
    val Tips: ImageVector by lazy { Lightbulb }

    // Trend icons
    val TrendingFlat: ImageVector by lazy {
        createIcon("TrendingFlat") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(22f, 12f)
                lineToRelative(-4f, -4f)
                verticalLineToRelative(3f)
                horizontalLineTo(3f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(15f)
                verticalLineToRelative(3f)
                close()
            }
        }
    }

    // Additional icons for UI components
    val Token: ImageVector by lazy { Star }  // Token icon aliased to Star
    val TextFields: ImageVector by lazy {
        createIcon("TextFields") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(2.5f, 4f)
                verticalLineToRelative(3f)
                horizontalLineToRelative(5f)
                verticalLineToRelative(12f)
                horizontalLineToRelative(3f)
                verticalLineTo(7f)
                horizontalLineToRelative(5f)
                verticalLineTo(4f)
                close()
                moveTo(21.5f, 9f)
                horizontalLineToRelative(-9f)
                verticalLineToRelative(3f)
                horizontalLineToRelative(3f)
                verticalLineToRelative(7f)
                horizontalLineToRelative(3f)
                verticalLineToRelative(-7f)
                horizontalLineToRelative(3f)
                close()
            }
        }
    }
    val HelpOutline: ImageVector by lazy {
        createIcon("HelpOutline") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(11f, 18f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(-2f)
                horizontalLineToRelative(-2f)
                close()
                moveTo(12f, 2f)
                curveTo(6.48f, 2f, 2f, 6.48f, 2f, 12f)
                reflectiveCurveToRelative(4.48f, 10f, 10f, 10f)
                reflectiveCurveToRelative(10f, -4.48f, 10f, -10f)
                reflectiveCurveTo(17.52f, 2f, 12f, 2f)
                close()
                moveTo(12f, 20f)
                curveToRelative(-4.41f, 0f, -8f, -3.59f, -8f, -8f)
                reflectiveCurveToRelative(3.59f, -8f, 8f, -8f)
                reflectiveCurveToRelative(8f, 3.59f, 8f, 8f)
                reflectiveCurveToRelative(-3.59f, 8f, -8f, 8f)
                close()
                moveTo(12f, 6f)
                curveToRelative(-2.21f, 0f, -4f, 1.79f, -4f, 4f)
                horizontalLineToRelative(2f)
                curveToRelative(0f, -1.1f, 0.9f, -2f, 2f, -2f)
                reflectiveCurveToRelative(2f, 0.9f, 2f, 2f)
                curveToRelative(0f, 2f, -3f, 1.75f, -3f, 5f)
                horizontalLineToRelative(2f)
                curveToRelative(0f, -2.25f, 3f, -2.5f, 3f, -5f)
                curveToRelative(0f, -2.21f, -1.79f, -4f, -4f, -4f)
                close()
            }
        }
    }
    val OpenInNew: ImageVector by lazy {
        createIcon("OpenInNew") {
            path(fill = SolidColor(Color.Black)) {
                moveTo(19f, 19f)
                horizontalLineTo(5f)
                verticalLineTo(5f)
                horizontalLineToRelative(7f)
                verticalLineTo(3f)
                horizontalLineTo(5f)
                curveToRelative(-1.11f, 0f, -2f, 0.9f, -2f, 2f)
                verticalLineToRelative(14f)
                curveToRelative(0f, 1.1f, 0.89f, 2f, 2f, 2f)
                horizontalLineToRelative(14f)
                curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
                verticalLineToRelative(-7f)
                horizontalLineToRelative(-2f)
                close()
                moveTo(14f, 3f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(3.59f)
                lineToRelative(-9.83f, 9.83f)
                lineToRelative(1.41f, 1.41f)
                lineTo(19f, 6.41f)
                verticalLineTo(10f)
                horizontalLineToRelative(2f)
                verticalLineTo(3f)
                close()
            }
        }
    }

}
