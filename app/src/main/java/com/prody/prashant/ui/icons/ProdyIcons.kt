package com.prody.prashant.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.unit.dp

/**
 * Optimized Icon System for Prody App
 *
 * This object provides efficient access to Material Design icons and custom icons.
 */
object ProdyIcons {
    
    // === CORE MATERIAL ICONS (Pre-loaded) ===
    
    val Add: ImageVector = Icons.Filled.Add
    val ArrowBack: ImageVector = Icons.AutoMirrored.Filled.ArrowBack
    val ArrowForward: ImageVector = Icons.AutoMirrored.Filled.ArrowForward
    val ChevronRight: ImageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight
    val KeyboardArrowRight: ImageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight
    val List: ImageVector = Icons.AutoMirrored.Filled.List
    val Send: ImageVector = Icons.AutoMirrored.Filled.Send
    
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
    val GridView: ImageVector = Icons.Filled.GridView
    val Image: ImageVector = Icons.Filled.Image
    val Stop: ImageVector = Icons.Filled.Stop
    val Pause: ImageVector = Icons.Filled.Pause
    
    // === NESTED OBJECTS FOR STYLE VARIANTS ===
    
    object Outlined {
        val Info: ImageVector = Icons.Outlined.Info
        val LocalFireDepartment: ImageVector = Icons.Outlined.LocalFireDepartment
        val Edit: ImageVector = Icons.Outlined.Edit
        val CheckCircle: ImageVector = Icons.Outlined.CheckCircle
        val Shield: ImageVector = Icons.Outlined.Shield
        val Warning: ImageVector = Icons.Outlined.Warning
        val Book: ImageVector = Icons.Outlined.Book
        val Send: ImageVector = Icons.AutoMirrored.Outlined.Send
        val FormatQuote: ImageVector = Icons.Outlined.FormatQuote
        val Psychology: ImageVector = Icons.Outlined.Psychology
        val Lightbulb: ImageVector = Icons.Outlined.Lightbulb
        val ArrowForward: ImageVector = Icons.AutoMirrored.Outlined.ArrowForward
        val Notifications: ImageVector = Icons.Outlined.Notifications
        val Search: ImageVector = Icons.Outlined.Search
        val OpenInNew: ImageVector = Icons.AutoMirrored.Outlined.OpenInNew
        val Refresh: ImageVector = Icons.Outlined.Refresh
        val SelfImprovement: ImageVector = Icons.Outlined.SelfImprovement
        val School: ImageVector = Icons.Outlined.School
        val ChatBubble: ImageVector = Icons.Outlined.ChatBubble
        val TextFormat: ImageVector = Icons.AutoMirrored.Outlined.TextFormat
        val AutoAwesome: ImageVector = Icons.Outlined.AutoAwesome
        val Leaderboard: ImageVector = Icons.Outlined.Leaderboard
        val Star: ImageVector = Icons.Outlined.Star
        val Email: ImageVector = Icons.Outlined.Email
        val Lock: ImageVector = Icons.Outlined.Lock
        val TrendingUp: ImageVector = Icons.AutoMirrored.Outlined.TrendingUp
        val MenuBook: ImageVector = Icons.AutoMirrored.Outlined.MenuBook
        val Image: ImageVector = Icons.Outlined.Image
        val Bolt: ImageVector = Icons.Outlined.Bolt
        val EmojiEvents: ImageVector = Icons.Outlined.EmojiEvents
        val Handshake: ImageVector = Icons.Outlined.Handshake
        val Schedule: ImageVector = Icons.Outlined.DateRange
        val Flag: ImageVector = Icons.Outlined.Flag
        val Groups: ImageVector = Icons.Outlined.Person
        val MoreHoriz: ImageVector = Icons.Outlined.MoreVert
        val StickyNote2: ImageVector = Icons.Outlined.Edit
        val Article: ImageVector = Icons.AutoMirrored.Outlined.Article
        val TextFields: ImageVector = Icons.AutoMirrored.Outlined.TextFormat
        val CalendarToday: ImageVector = Icons.Outlined.DateRange
        val TrendingFlat: ImageVector = Icons.AutoMirrored.Outlined.ArrowForward
        val Tag: ImageVector = Icons.Outlined.Label
        val Circle: ImageVector = Icons.Outlined.Lens
        val Summarize: ImageVector = Icons.Outlined.Assessment
        val Loop: ImageVector = Icons.Outlined.Refresh
        val CameraAlt: ImageVector = Icons.Outlined.CameraAlt
        val Stop: ImageVector = Icons.Outlined.Stop
        val Mic: ImageVector = Icons.Outlined.Mic
        val Pause: ImageVector = Icons.Outlined.Pause
        val PlayArrow: ImageVector = Icons.Outlined.PlayArrow
        val SearchOff: ImageVector = Icons.Outlined.SearchOff
        val AutoStories: ImageVector = Icons.AutoMirrored.Outlined.AutoStories
        val BookmarkBorder: ImageVector = Icons.Outlined.BookmarkBorder
        val HelpOutline: ImageVector = Icons.AutoMirrored.Outlined.Help
        val Tips: ImageVector = Icons.Outlined.Lightbulb
        val Quiz: ImageVector = Icons.AutoMirrored.Outlined.Help
        val OpenInFull: ImageVector = Icons.AutoMirrored.Outlined.OpenInNew
        val Settings: ImageVector = Icons.Outlined.Settings
        val CalendarMonth: ImageVector = Icons.Outlined.DateRange
        val NoteAlt: ImageVector = Icons.Outlined.Edit
        val NoteAdd: ImageVector = Icons.Outlined.NoteAdd
        val BookmarkRemove: ImageVector = Icons.Outlined.BookmarkBorder
        val Diversity3: ImageVector = Icons.Outlined.Person
        val AlternateEmail: ImageVector = Icons.Outlined.Email
        val Contacts: ImageVector = Icons.Outlined.Person
    }

    object Rounded {
        val Check: ImageVector = Icons.Rounded.Check
        val Close: ImageVector = Icons.Rounded.Close
        val Info: ImageVector = Icons.Rounded.Info
        val Warning: ImageVector = Icons.Rounded.Warning
        val Person: ImageVector = Icons.Rounded.Person
        val Settings: ImageVector = Icons.Rounded.Settings
        val Star: ImageVector = Icons.Rounded.Star
        val Add: ImageVector = Icons.Rounded.Add
        val Edit: ImageVector = Icons.Rounded.Edit
        val Delete: ImageVector = Icons.Rounded.Delete
        val AutoAwesome: ImageVector = Icons.Rounded.AutoAwesome
        val TrendingUp: ImageVector = Icons.AutoMirrored.Rounded.TrendingUp
        val Celebration: ImageVector = Icons.Rounded.Celebration
        val Favorite: ImageVector = Icons.Rounded.Favorite
        val Token: ImageVector = Icons.Rounded.Star // Fallback
        val Book: ImageVector = Icons.Rounded.Book
        val Send: ImageVector = Icons.AutoMirrored.Rounded.Send
        val Mail: ImageVector = Icons.Rounded.Email
        val EmojiEvents: ImageVector = Icons.Rounded.EmojiEvents
        val Psychology: ImageVector = Icons.Rounded.Psychology
        val Stars: ImageVector = Icons.Rounded.Stars
        val LocalFireDepartment: ImageVector = Icons.Rounded.LocalFireDepartment
        val EditNote: ImageVector = Icons.Rounded.Edit
        val CalendarMonth: ImageVector = Icons.Rounded.DateRange
    }

    // === CUSTOM ICONS (Lazy-loaded on demand) ===
    
    private val _customIcons = mutableMapOf<String, ImageVector>()

    // === SENTIMENT ICONS for Mood Selection ===
    val SentimentVeryDissatisfied: ImageVector
        get() = _customIcons.getOrPut("SentimentVeryDissatisfied") {
            createIcon("SentimentVeryDissatisfied") {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(12f, 2f)
                    curveTo(6.48f, 2f, 2f, 6.48f, 2f, 12f)
                    reflectiveCurveTo(6.48f, 22f, 12f, 22f)
                    reflectiveCurveTo(22f, 17.52f, 22f, 12f)
                    reflectiveCurveTo(17.52f, 2f, 12f, 2f)
                    close()
                    moveTo(12f, 20f)
                    curveToRelative(-4.42f, 0f, -8f, -3.58f, -8f, -8f)
                    reflectiveCurveTo(7.58f, 4f, 12f, 4f)
                    reflectiveCurveTo(20f, 7.58f, 20f, 12f)
                    reflectiveCurveTo(16.42f, 20f, 12f, 20f)
                    close()
                    moveTo(8.5f, 8f)
                    curveTo(7.67f, 8f, 7f, 8.67f, 7f, 9.5f)
                    reflectiveCurveTo(7.67f, 11f, 8.5f, 11f)
                    reflectiveCurveTo(10f, 10.33f, 10f, 9.5f)
                    reflectiveCurveTo(9.33f, 8f, 8.5f, 8f)
                    close()
                    moveTo(15.5f, 8f)
                    curveTo(14.67f, 8f, 14f, 8.67f, 14f, 9.5f)
                    reflectiveCurveTo(14.67f, 11f, 15.5f, 11f)
                    reflectiveCurveTo(17f, 10.33f, 17f, 9.5f)
                    reflectiveCurveTo(16.33f, 8f, 15.5f, 8f)
                    close()
                    moveTo(12f, 14f)
                    curveToRelative(-2.33f, 0f, -4.32f, 1.45f, -5.12f, 3.5f)
                    horizontalLineToRelative(1.67f)
                    curveToRelative(0.69f, -1.19f, 1.97f, -2f, 3.45f, -2f)
                    reflectiveCurveTo(14.64f, 16.31f, 15.33f, 17.5f)
                    horizontalLineToRelative(1.67f)
                    curveTo(16.2f, 15.45f, 14.22f, 14f, 12f, 14f)
                    close()
                }
            }
        }

    val SentimentDissatisfied: ImageVector
        get() = _customIcons.getOrPut("SentimentDissatisfied") {
            createIcon("SentimentDissatisfied") {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(15.5f, 9.5f)
                    moveToRelative(-1.5f, 0f)
                    arcToRelative(1.5f, 1.5f, 0f, isMoreThanHalf = true, isPositiveArc = true, 3f, 0f)
                    arcToRelative(1.5f, 1.5f, 0f, isMoreThanHalf = true, isPositiveArc = true, -3f, 0f)
                    moveTo(8.5f, 9.5f)
                    moveToRelative(-1.5f, 0f)
                    arcToRelative(1.5f, 1.5f, 0f, isMoreThanHalf = true, isPositiveArc = true, 3f, 0f)
                    arcToRelative(1.5f, 1.5f, 0f, isMoreThanHalf = true, isPositiveArc = true, -3f, 0f)
                    moveTo(12f, 14f)
                    curveToRelative(-2f, 0f, -3.7f, 1.2f, -4.5f, 3f)
                    horizontalLineToRelative(9f)
                    curveToRelative(-0.8f, -1.8f, -2.5f, -3f, -4.5f, -3f)
                    close()
                    moveTo(12f, 2f)
                    curveTo(6.5f, 2f, 2f, 6.5f, 2f, 12f)
                    reflectiveCurveTo(6.5f, 22f, 12f, 22f)
                    reflectiveCurveTo(22f, 17.5f, 22f, 12f)
                    reflectiveCurveTo(17.5f, 2f, 12f, 2f)
                    close()
                    moveTo(12f, 20f)
                    curveToRelative(-4.4f, 0f, -8f, -3.6f, -8f, -8f)
                    reflectiveCurveTo(7.6f, 4f, 12f, 4f)
                    reflectiveCurveTo(20f, 7.6f, 20f, 12f)
                    reflectiveCurveTo(16.4f, 20f, 12f, 20f)
                    close()
                }
            }
        }

    val SentimentNeutral: ImageVector
        get() = _customIcons.getOrPut("SentimentNeutral") {
            createIcon("SentimentNeutral") {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(9f, 14f)
                    horizontalLineToRelative(6f)
                    verticalLineToRelative(1.5f)
                    horizontalLineTo(9f)
                    verticalLineTo(14f)
                    close()
                    moveTo(15.5f, 9.5f)
                    moveToRelative(-1.5f, 0f)
                    arcToRelative(1.5f, 1.5f, 0f, isMoreThanHalf = true, isPositiveArc = true, 3f, 0f)
                    arcToRelative(1.5f, 1.5f, 0f, isMoreThanHalf = true, isPositiveArc = true, -3f, 0f)
                    moveTo(8.5f, 9.5f)
                    moveToRelative(-1.5f, 0f)
                    arcToRelative(1.5f, 1.5f, 0f, isMoreThanHalf = true, isPositiveArc = true, 3f, 0f)
                    arcToRelative(1.5f, 1.5f, 0f, isMoreThanHalf = true, isPositiveArc = true, -3f, 0f)
                    moveTo(12f, 2f)
                    curveTo(6.5f, 2f, 2f, 6.5f, 2f, 12f)
                    reflectiveCurveTo(6.5f, 22f, 12f, 22f)
                    reflectiveCurveTo(22f, 17.5f, 22f, 12f)
                    reflectiveCurveTo(17.5f, 2f, 12f, 2f)
                    close()
                    moveTo(12f, 20f)
                    curveToRelative(-4.4f, 0f, -8f, -3.6f, -8f, -8f)
                    reflectiveCurveTo(7.6f, 4f, 12f, 4f)
                    reflectiveCurveTo(20f, 7.6f, 20f, 12f)
                    reflectiveCurveTo(16.4f, 20f, 12f, 20f)
                    close()
                }
            }
        }

    val SentimentSatisfied: ImageVector
        get() = _customIcons.getOrPut("SentimentSatisfied") {
            createIcon("SentimentSatisfied") {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(15.5f, 9.5f)
                    moveToRelative(-1.5f, 0f)
                    arcToRelative(1.5f, 1.5f, 0f, isMoreThanHalf = true, isPositiveArc = true, 3f, 0f)
                    arcToRelative(1.5f, 1.5f, 0f, isMoreThanHalf = true, isPositiveArc = true, -3f, 0f)
                    moveTo(8.5f, 9.5f)
                    moveToRelative(-1.5f, 0f)
                    arcToRelative(1.5f, 1.5f, 0f, isMoreThanHalf = true, isPositiveArc = true, 3f, 0f)
                    arcToRelative(1.5f, 1.5f, 0f, isMoreThanHalf = true, isPositiveArc = true, -3f, 0f)
                    moveTo(12f, 16f)
                    curveToRelative(1.5f, 0f, 2.7f, -0.8f, 3.5f, -2f)
                    horizontalLineToRelative(-7f)
                    curveToRelative(0.8f, 1.2f, 2f, 2f, 3.5f, 2f)
                    close()
                    moveTo(12f, 2f)
                    curveTo(6.5f, 2f, 2f, 6.5f, 2f, 12f)
                    reflectiveCurveTo(6.5f, 22f, 12f, 22f)
                    reflectiveCurveTo(22f, 17.5f, 22f, 12f)
                    reflectiveCurveTo(17.5f, 2f, 12f, 2f)
                    close()
                    moveTo(12f, 20f)
                    curveToRelative(-4.4f, 0f, -8f, -3.6f, -8f, -8f)
                    reflectiveCurveTo(7.6f, 4f, 12f, 4f)
                    reflectiveCurveTo(20f, 7.6f, 20f, 12f)
                    reflectiveCurveTo(16.4f, 20f, 12f, 20f)
                    close()
                }
            }
        }

    val SentimentVerySatisfied: ImageVector
        get() = _customIcons.getOrPut("SentimentVerySatisfied") {
            createIcon("SentimentVerySatisfied") {
                path(fill = SolidColor(Color.Black)) {
                    moveTo(12f, 2f)
                    curveTo(6.48f, 2f, 2f, 6.48f, 2f, 12f)
                    reflectiveCurveTo(6.48f, 22f, 12f, 22f)
                    reflectiveCurveTo(22f, 17.52f, 22f, 12f)
                    reflectiveCurveTo(17.52f, 2f, 12f, 2f)
                    close()
                    moveTo(12f, 20f)
                    curveToRelative(-4.42f, 0f, -8f, -3.58f, -8f, -8f)
                    reflectiveCurveTo(7.58f, 4f, 12f, 4f)
                    reflectiveCurveTo(20f, 7.58f, 20f, 12f)
                    reflectiveCurveTo(16.42f, 20f, 12f, 20f)
                    close()
                    moveTo(13f, 9.94f)
                    lineTo(14.06f, 11f)
                    lineTo(15.12f, 9.94f)
                    lineTo(16.18f, 11f)
                    lineTo(17.24f, 9.94f)
                    lineTo(15.12f, 7.82f)
                    lineTo(13f, 9.94f)
                    close()
                    moveTo(8.88f, 9.94f)
                    lineTo(9.94f, 11f)
                    lineTo(11f, 9.94f)
                    lineTo(8.88f, 7.82f)
                    lineTo(6.76f, 9.94f)
                    lineTo(7.82f, 11f)
                    lineTo(8.88f, 9.94f)
                    close()
                    moveTo(12f, 17.5f)
                    curveToRelative(2.33f, 0f, 4.31f, -1.46f, 5.11f, -3.5f)
                    horizontalLineTo(6.89f)
                    curveTo(7.69f, 16.04f, 9.67f, 17.5f, 12f, 17.5f)
                    close()
                }
            }
        }
    
    val CheckCircle: ImageVector get() = Icons.Filled.CheckCircle
    val SelfImprovement: ImageVector get() = Icons.Filled.SelfImprovement
    val Lightbulb: ImageVector get() = Icons.Filled.Lightbulb
    val AutoAwesome: ImageVector get() = Icons.Filled.AutoAwesome

    // === COMMON ALIASES ===
    
    val Mail: ImageVector = Email
    val Lock: ImageVector = Icons.Filled.Lock
    val SendIcon: ImageVector = Send
    val ArrowRight: ImageVector = KeyboardArrowRight
    val ListIcon: ImageVector = List

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

    object Gamification {
        val Trophy: ImageVector = Icons.Filled.EmojiEvents
    }

    // === UTILITY METHODS ===

    private fun createIcon(name: String, pathData: ImageVector.Builder.() -> Unit): ImageVector {
        return ImageVector.Builder(
            name = name,
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply(pathData).build()
    }

    fun clearIconCache() { _customIcons.clear() }
    
    fun getIconMemoryInfo(): String = "Icons loaded"
    fun preloadCommonIcons() {}

    val Schedule: ImageVector = Icons.Filled.DateRange
    val LocalFireDepartment: ImageVector = Icons.Filled.LocalFireDepartment
    val EmojiEvents: ImageVector = Icons.Filled.EmojiEvents
    val Psychology: ImageVector = Icons.Filled.Psychology
    val Chat: ImageVector = Icons.Filled.Chat
    val Notifications: ImageVector = Icons.Filled.Notifications
    val Inbox: ImageVector = Icons.Filled.Inbox
    val Sms: ImageVector = Icons.Filled.Email
    val Refresh: ImageVector = Icons.Filled.Refresh
    val School: ImageVector = Icons.Filled.School
    val Book: ImageVector = Icons.Filled.Book
    val Stars: ImageVector = Icons.Filled.Stars
    val TrendingUp: ImageVector = Icons.AutoMirrored.Filled.TrendingUp
    val TrendingDown: ImageVector = Icons.AutoMirrored.Filled.TrendingDown
    val ExpandLess: ImageVector = Icons.Filled.ExpandLess
    val ExpandMore: ImageVector = Icons.Filled.ExpandMore
    val MenuBook: ImageVector = Icons.AutoMirrored.Filled.MenuBook
    val ErrorOutline: ImageVector = Icons.Filled.ErrorOutline
    val AutoStories: ImageVector = Icons.AutoMirrored.Filled.AutoStories
    val FormatQuote: ImageVector = Icons.Filled.FormatQuote
    val WbSunny: ImageVector = Icons.Filled.WbSunny
    val NightsStay: ImageVector = Icons.Filled.NightsStay
    val DarkMode: ImageVector = Icons.Filled.DarkMode
    val Bookmark: ImageVector = Icons.Filled.Bookmark
    val BookmarkBorder: ImageVector = Icons.Filled.BookmarkBorder
    val LocalFlorist: ImageVector = Icons.Filled.LocalFlorist
    val Bolt: ImageVector = Icons.Filled.Bolt
    val History: ImageVector = Icons.Filled.History
    val Celebration: ImageVector = Icons.Filled.Celebration
    val Pause: ImageVector = Icons.Filled.Pause
    val Stop: ImageVector = Icons.Filled.Stop
    val SkipNext: ImageVector = Icons.Filled.SkipNext
    val Verified: ImageVector = Icons.Filled.Verified
    val Palette: ImageVector = Icons.Filled.Palette
    val Timer: ImageVector = Icons.Filled.Timer
    val ThumbUp: ImageVector = Icons.Filled.ThumbUp
    val Error: ImageVector = Icons.Filled.Error
    val Clear: ImageVector = Close
    val SearchOff: ImageVector = Icons.Filled.SearchOff
    val Wallpaper: ImageVector = Icons.Filled.Wallpaper
    val HourglassEmpty: ImageVector = Icons.Filled.HourglassEmpty
    val Create: ImageVector = Edit
    val Image: ImageVector = Icons.Filled.Image
    val FilterList: ImageVector = Icons.Filled.List
    val Assignment: ImageVector = Icons.AutoMirrored.Filled.Assignment
    val BarChart: ImageVector = Icons.AutoMirrored.Filled.List // Fallback
    val CalendarMonth: ImageVector = Icons.Filled.DateRange
    val Code: ImageVector = Icons.Filled.Code
    val Dashboard: ImageVector = Icons.Filled.Home // Fallback
    val Explore: ImageVector = Icons.Filled.Place
    val Forum: ImageVector = Icons.Filled.Email
    val Grass: ImageVector = LocalFlorist
    val Eco: ImageVector = LocalFlorist
    val Park: ImageVector = LocalFlorist
    val Nature: ImageVector = LocalFlorist
    val Shield: ImageVector = Icons.Filled.Info // Fallback if Shield missing
    val WorkspacePremium: ImageVector = EmojiEvents
    val MilitaryTech: ImageVector = EmojiEvents
    val Leaderboard: ImageVector = Icons.AutoMirrored.Filled.List // Fallback
    val ShowChart: ImageVector = TrendingUp
    val HistoryEdu: ImageVector = MenuBook
    val EditNote: ImageVector = Edit
    val TipsAndUpdates: ImageVector = Lightbulb
    val MarkEmailRead: ImageVector = Mail
    val MailOutline: ImageVector = Mail
    val ChatBubble: ImageVector = Chat
    val NotificationsActive: ImageVector = Notifications
    val Whatshot: ImageVector = LocalFireDepartment
    val RocketLaunch: ImageVector = LocalFireDepartment
    val Rocket: ImageVector = LocalFireDepartment
    val Fingerprint: ImageVector = Lock
    val Security: ImageVector = Lock
    val Policy: ImageVector = Shield
    val VerifiedUser: ImageVector = Verified
    val Groups: ImageVector = Person
    val Diversity: ImageVector = Person
    val Elderly: ImageVector = Person
    val Visibility: ImageVector = Icons.Filled.Info // Fallback
    val FitnessCenter: ImageVector = Icons.Filled.Info // Fallback
    val RecordVoiceOver: ImageVector = Mic
    val Hearing: ImageVector = Mic
    val SentimentSatisfied: ImageVector = Icons.Filled.Face
    val SentimentVerySatisfied: ImageVector = Icons.Filled.Face
    val SentimentDissatisfied: ImageVector = Icons.Filled.Warning
    val Mood: ImageVector = EmojiEvents
    val EmojiNature: ImageVector = LocalFlorist
    val Terrain: ImageVector = LocalFlorist
    val Landscape: ImageVector = LocalFlorist
    val Water: ImageVector = LocalFlorist
    val Grain: ImageVector = LocalFlorist
    val Flare: ImageVector = WbSunny
    val LightMode: ImageVector = WbSunny
    val Nightlight: ImageVector = NightsStay
    val WbTwilight: ImageVector = NightsStay
    val BrightnessAuto: ImageVector = WbSunny
    val Science: ImageVector = Psychology
    val PsychologyAlt: ImageVector = Psychology
    val AutoGraph: ImageVector = TrendingUp
    val Flag: ImageVector = Icons.Filled.Flag
    val Map: ImageVector = Place
    val GridView: ImageVector = Dashboard
    val Storage: ImageVector = Dashboard
    val Memory: ImageVector = Dashboard
    val Inventory: ImageVector = Dashboard
    val CollectionsBookmark: ImageVector = Book
    val PhotoLibrary: ImageVector = Image
    val FilterFrames: ImageVector = Image
    val FilterAlt: ImageVector = FilterList
    val Tune: ImageVector = Settings
    val Help: ImageVector = Info
    val ExitToApp: ImageVector = ArrowForward
    val ArrowUpward: ImageVector = ExpandLess
    val ArrowDownward: ImageVector = ExpandMore
    val ArrowDropDown: ImageVector = ExpandMore
    val KeyboardArrowUp: ImageVector = ExpandLess
    val KeyboardArrowDown: ImageVector = ExpandMore
    val Remove: ImageVector = Close
    val ContentCopy: ImageVector = Edit
    val PlayCircle: ImageVector = PlayArrow
    val PhoneAndroid: ImageVector = Phone
    val Vibration: ImageVector = Phone
    val Today: ImageVector = CalendarMonth
    val Translate: ImageVector = MenuBook
    val VisibilityOff: ImageVector = Close
    val Moving: ImageVector = TrendingUp
    val NewReleases: ImageVector = AutoAwesome
    val CloudOff: ImageVector = Close
    val Cloud: ImageVector = Icons.Filled.Info // Fallback
    val CloudDone: ImageVector = Cloud
    val CloudSync: ImageVector = Cloud
    val Cached: ImageVector = Refresh
    val WifiOff: ImageVector = CloudOff
    val Api: ImageVector = Code
    val BugReport: ImageVector = ErrorOutline
    val Construction: ImageVector = Settings
    val Handyman: ImageVector = Settings
    val Architecture: ImageVector = Settings
    val Foundation: ImageVector = Home
    val Badge: ImageVector = Verified
    val Diamond: ImageVector = Star
    val LocalOffer: ImageVector = Star
    val HourglassBottom: ImageVector = HourglassEmpty
    val Handshake: ImageVector = Person
    val LockOpen: ImageVector = Icons.Filled.Lock // Fallback
}
