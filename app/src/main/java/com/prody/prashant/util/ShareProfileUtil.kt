package com.prody.prashant.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.LinearGradient
import android.graphics.Shader
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import kotlin.math.min

/**
 * Utility object for generating and sharing profile cards/banners.
 *
 * Features:
 * - Generate shareable profile images with stats and achievements
 * - Minimal Prody watermark (non-obtrusive)
 * - Support for various social media platforms
 * - Customizable themes matching app design system
 *
 * Usage:
 * ```kotlin
 * val shareUtil = ShareProfileUtil
 * shareUtil.shareProfile(
 *     context = context,
 *     userName = "John",
 *     title = "Scholar",
 *     level = 5,
 *     totalPoints = 1500,
 *     currentStreak = 30,
 *     wordsLearned = 150,
 *     journalEntries = 45,
 *     achievementsUnlocked = 12
 * )
 * ```
 */
object ShareProfileUtil {

    // Design system colors
    private val PRIMARY_COLOR = Color.parseColor("#2D5A3D") // Forest Green
    private val SECONDARY_COLOR = Color.parseColor("#D4C4A8") // Warm Sand
    private val TERTIARY_COLOR = Color.parseColor("#5B9A8B") // Soft Teal
    private val GOLD_COLOR = Color.parseColor("#FFD700") // Gold
    private val BACKGROUND_LIGHT = Color.parseColor("#FBFAF8") // Warm White
    private val BACKGROUND_DARK = Color.parseColor("#0F1210") // Deep Charcoal
    private val TEXT_PRIMARY_LIGHT = Color.parseColor("#1A1A1A")
    private val TEXT_PRIMARY_DARK = Color.parseColor("#F5F5F5")
    private val STREAK_FIRE = Color.parseColor("#FF6B35")

    // Card dimensions (optimized for social media)
    private const val CARD_WIDTH = 1080
    private const val CARD_HEIGHT = 1350 // 4:5 ratio for Instagram

    // Story dimensions (9:16 for Instagram/WhatsApp stories)
    private const val STORY_WIDTH = 1080
    private const val STORY_HEIGHT = 1920

    /**
     * Generates a shareable profile card image.
     *
     * @param context Application context
     * @param userName User's display name
     * @param title User's title/rank
     * @param level User's current level
     * @param totalPoints Total points earned
     * @param currentStreak Current streak in days
     * @param longestStreak Longest streak achieved
     * @param wordsLearned Number of words learned
     * @param journalEntries Number of journal entries
     * @param achievementsUnlocked Number of achievements unlocked
     * @param isDarkMode Whether to use dark mode styling
     * @return Bitmap of the generated profile card
     */
    fun generateProfileCard(
        context: Context,
        userName: String,
        title: String,
        level: Int,
        totalPoints: Int,
        currentStreak: Int,
        longestStreak: Int,
        wordsLearned: Int,
        journalEntries: Int,
        achievementsUnlocked: Int,
        isDarkMode: Boolean = false
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(CARD_WIDTH, CARD_HEIGHT, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val backgroundColor = if (isDarkMode) BACKGROUND_DARK else BACKGROUND_LIGHT
        val textColor = if (isDarkMode) TEXT_PRIMARY_DARK else TEXT_PRIMARY_LIGHT
        val textSecondaryColor = if (isDarkMode) Color.parseColor("#AAAAAA") else Color.parseColor("#666666")

        // Draw background
        canvas.drawColor(backgroundColor)

        // Draw header gradient
        val headerPaint = Paint().apply {
            shader = LinearGradient(
                0f, 0f, CARD_WIDTH.toFloat(), 400f,
                PRIMARY_COLOR, TERTIARY_COLOR,
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, CARD_WIDTH.toFloat(), 400f, headerPaint)

        // Draw decorative circles in header
        val circlePaint = Paint().apply {
            color = Color.WHITE
            alpha = 30
            isAntiAlias = true
        }
        canvas.drawCircle(100f, 100f, 150f, circlePaint)
        canvas.drawCircle(CARD_WIDTH - 100f, 300f, 100f, circlePaint)
        canvas.drawCircle(CARD_WIDTH / 2f, 50f, 80f, circlePaint)

        // Draw avatar circle
        val avatarPaint = Paint().apply {
            color = Color.WHITE
            alpha = 40
            isAntiAlias = true
        }
        canvas.drawCircle(CARD_WIDTH / 2f, 200f, 80f, avatarPaint)

        // Draw level badge
        val levelBadgePaint = Paint().apply {
            color = GOLD_COLOR
            isAntiAlias = true
        }
        canvas.drawCircle(CARD_WIDTH / 2f + 60f, 260f, 25f, levelBadgePaint)

        val levelTextPaint = Paint().apply {
            color = Color.WHITE
            textSize = 24f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText(level.toString(), CARD_WIDTH / 2f + 60f, 268f, levelTextPaint)

        // Draw user initials in avatar
        val initialsPaint = Paint().apply {
            color = Color.WHITE
            textSize = 48f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val initials = userName.split(" ").take(2).map { it.firstOrNull()?.uppercaseChar() ?: "" }.joinToString("")
        canvas.drawText(initials, CARD_WIDTH / 2f, 215f, initialsPaint)

        // Draw name
        val namePaint = Paint().apply {
            color = Color.WHITE
            textSize = 42f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText(userName, CARD_WIDTH / 2f, 340f, namePaint)

        // Draw title
        val titlePaint = Paint().apply {
            color = GOLD_COLOR
            textSize = 28f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }
        canvas.drawText(title, CARD_WIDTH / 2f, 380f, titlePaint)

        // Draw stats section
        val statsY = 500f
        val statCardWidth = 280f
        val statCardHeight = 120f
        val statCardMargin = 40f

        // Stats data
        val stats = listOf(
            Triple("Points", formatNumber(totalPoints), GOLD_COLOR),
            Triple("Words", formatNumber(wordsLearned), TERTIARY_COLOR),
            Triple("Entries", formatNumber(journalEntries), PRIMARY_COLOR),
            Triple("Badges", formatNumber(achievementsUnlocked), Color.parseColor("#9B59B6"))
        )

        // Draw 2x2 grid of stats
        stats.forEachIndexed { index, (label, value, color) ->
            val row = index / 2
            val col = index % 2
            val x = statCardMargin + col * (statCardWidth + 30f)
            val y = statsY + row * (statCardHeight + 20f)

            drawStatCard(canvas, x, y, statCardWidth, statCardHeight, label, value, color, backgroundColor, textColor, textSecondaryColor, isDarkMode)
        }

        // Draw streak section
        val streakY = statsY + 2 * (statCardHeight + 20f) + 40f
        drawStreakSection(canvas, streakY, currentStreak, longestStreak, textColor, textSecondaryColor, isDarkMode)

        // Draw motivational quote
        val quoteY = streakY + 180f
        drawMotivationalQuote(canvas, quoteY, textSecondaryColor)

        // Draw Prody watermark (minimal, non-obtrusive)
        drawWatermark(canvas, CARD_HEIGHT - 80f, isDarkMode)

        return bitmap
    }

    /**
     * Generates a story-format profile card (9:16 ratio).
     */
    fun generateProfileStory(
        context: Context,
        userName: String,
        title: String,
        level: Int,
        totalPoints: Int,
        currentStreak: Int,
        wordsLearned: Int,
        achievementsUnlocked: Int,
        isDarkMode: Boolean = false
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(STORY_WIDTH, STORY_HEIGHT, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val backgroundColor = if (isDarkMode) BACKGROUND_DARK else BACKGROUND_LIGHT
        val textColor = if (isDarkMode) TEXT_PRIMARY_DARK else TEXT_PRIMARY_LIGHT
        val textSecondaryColor = if (isDarkMode) Color.parseColor("#AAAAAA") else Color.parseColor("#666666")

        // Draw gradient background
        val bgPaint = Paint().apply {
            shader = LinearGradient(
                0f, 0f, STORY_WIDTH.toFloat(), STORY_HEIGHT.toFloat(),
                backgroundColor, if (isDarkMode) Color.parseColor("#1A2520") else Color.parseColor("#F0EDE8"),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, 0f, STORY_WIDTH.toFloat(), STORY_HEIGHT.toFloat(), bgPaint)

        // Draw decorative elements
        val decorPaint = Paint().apply {
            color = PRIMARY_COLOR
            alpha = 20
            isAntiAlias = true
        }
        canvas.drawCircle(-100f, 200f, 300f, decorPaint)
        canvas.drawCircle(STORY_WIDTH + 100f, STORY_HEIGHT - 200f, 300f, decorPaint)

        // Draw header card
        val headerPaint = Paint().apply {
            shader = LinearGradient(
                100f, 300f, STORY_WIDTH - 100f, 700f,
                PRIMARY_COLOR, TERTIARY_COLOR,
                Shader.TileMode.CLAMP
            )
            isAntiAlias = true
        }
        val headerRect = RectF(80f, 300f, STORY_WIDTH - 80f, 750f)
        canvas.drawRoundRect(headerRect, 40f, 40f, headerPaint)

        // Draw avatar
        val avatarPaint = Paint().apply {
            color = Color.WHITE
            alpha = 40
            isAntiAlias = true
        }
        canvas.drawCircle(STORY_WIDTH / 2f, 450f, 70f, avatarPaint)

        // Draw initials
        val initialsPaint = Paint().apply {
            color = Color.WHITE
            textSize = 42f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val initials = userName.split(" ").take(2).map { it.firstOrNull()?.uppercaseChar() ?: "" }.joinToString("")
        canvas.drawText(initials, STORY_WIDTH / 2f, 465f, initialsPaint)

        // Draw name and title
        val namePaint = Paint().apply {
            color = Color.WHITE
            textSize = 48f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText(userName, STORY_WIDTH / 2f, 580f, namePaint)

        val titleBadgePaint = Paint().apply {
            color = Color.WHITE
            alpha = 30
            isAntiAlias = true
        }
        val titleWidth = Paint().apply { textSize = 28f }.measureText(title) + 60f
        canvas.drawRoundRect(
            STORY_WIDTH / 2f - titleWidth / 2f, 610f,
            STORY_WIDTH / 2f + titleWidth / 2f, 660f,
            20f, 20f, titleBadgePaint
        )

        val titleTextPaint = Paint().apply {
            color = GOLD_COLOR
            textSize = 28f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("Lvl $level • $title", STORY_WIDTH / 2f, 645f, titleTextPaint)

        // Draw stats
        val statsStartY = 850f
        val statHeight = 100f
        val statMargin = 20f

        val storyStats = listOf(
            Triple("Total Points", formatNumber(totalPoints), GOLD_COLOR),
            Triple("Current Streak", "$currentStreak days", STREAK_FIRE),
            Triple("Words Learned", formatNumber(wordsLearned), TERTIARY_COLOR),
            Triple("Achievements", formatNumber(achievementsUnlocked), Color.parseColor("#9B59B6"))
        )

        storyStats.forEachIndexed { index, (label, value, color) ->
            val y = statsStartY + index * (statHeight + statMargin)
            drawStoryStatRow(canvas, y, label, value, color, backgroundColor, textColor, textSecondaryColor, isDarkMode)
        }

        // Draw motivational message
        val messageY = statsStartY + storyStats.size * (statHeight + statMargin) + 80f
        val messagePaint = Paint().apply {
            this.color = textSecondaryColor
            textSize = 28f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            isAntiAlias = true
        }
        canvas.drawText("Growing every day with Prody", STORY_WIDTH / 2f, messageY, messagePaint)

        // Draw watermark
        drawWatermark(canvas, STORY_HEIGHT - 120f, isDarkMode)

        return bitmap
    }

    /**
     * Shares the generated profile card via Android share sheet.
     */
    fun shareProfile(
        context: Context,
        userName: String,
        title: String,
        level: Int,
        totalPoints: Int,
        currentStreak: Int,
        longestStreak: Int,
        wordsLearned: Int,
        journalEntries: Int,
        achievementsUnlocked: Int,
        isDarkMode: Boolean = false,
        shareAsStory: Boolean = false
    ) {
        val bitmap = if (shareAsStory) {
            generateProfileStory(
                context, userName, title, level, totalPoints,
                currentStreak, wordsLearned, achievementsUnlocked, isDarkMode
            )
        } else {
            generateProfileCard(
                context, userName, title, level, totalPoints, currentStreak,
                longestStreak, wordsLearned, journalEntries, achievementsUnlocked, isDarkMode
            )
        }

        val uri = saveBitmapToCache(context, bitmap, "prody_profile_${System.currentTimeMillis()}.png")

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, "Check out my growth journey on Prody! $currentStreak day streak and counting.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(shareIntent, "Share your Prody profile"))
    }

    /**
     * Shares to a specific app (Instagram, WhatsApp, etc.).
     */
    fun shareToApp(
        context: Context,
        bitmap: Bitmap,
        packageName: String,
        message: String = ""
    ) {
        val uri = saveBitmapToCache(context, bitmap, "prody_share_${System.currentTimeMillis()}.png")

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            if (message.isNotEmpty()) {
                putExtra(Intent.EXTRA_TEXT, message)
            }
            setPackage(packageName)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        try {
            context.startActivity(shareIntent)
        } catch (e: Exception) {
            // Fallback to generic share if app not installed
            context.startActivity(Intent.createChooser(
                shareIntent.apply { setPackage(null) },
                "Share your Prody profile"
            ))
        }
    }

    // Helper functions

    private fun drawStatCard(
        canvas: Canvas,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        label: String,
        value: String,
        accentColor: Int,
        backgroundColor: Int,
        textColor: Int,
        textSecondaryColor: Int,
        isDarkMode: Boolean
    ) {
        // Card background
        val cardPaint = Paint().apply {
            color = if (isDarkMode) Color.parseColor("#1A2520") else Color.WHITE
            isAntiAlias = true
            setShadowLayer(10f, 0f, 4f, Color.argb(30, 0, 0, 0))
        }
        val rect = RectF(x, y, x + width, y + height)
        canvas.drawRoundRect(rect, 20f, 20f, cardPaint)

        // Accent line
        val accentPaint = Paint().apply {
            color = accentColor
            isAntiAlias = true
        }
        canvas.drawRoundRect(RectF(x, y, x + 8f, y + height), 4f, 4f, accentPaint)

        // Value text
        val valuePaint = Paint().apply {
            color = textColor
            textSize = 36f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText(value, x + 30f, y + 50f, valuePaint)

        // Label text
        val labelPaint = Paint().apply {
            color = textSecondaryColor
            textSize = 22f
            isAntiAlias = true
        }
        canvas.drawText(label, x + 30f, y + 85f, labelPaint)
    }

    private fun drawStreakSection(
        canvas: Canvas,
        y: Float,
        currentStreak: Int,
        longestStreak: Int,
        textColor: Int,
        textSecondaryColor: Int,
        isDarkMode: Boolean
    ) {
        // Section title
        val titlePaint = Paint().apply {
            color = textColor
            textSize = 28f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("Streak Progress", CARD_WIDTH / 2f, y, titlePaint)

        // Streak value with fire emoji representation
        val streakValuePaint = Paint().apply {
            color = STREAK_FIRE
            textSize = 72f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("$currentStreak", CARD_WIDTH / 2f, y + 80f, streakValuePaint)

        val daysPaint = Paint().apply {
            color = textSecondaryColor
            textSize = 24f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("day streak", CARD_WIDTH / 2f, y + 115f, daysPaint)

        // Longest streak
        if (longestStreak > currentStreak) {
            val bestPaint = Paint().apply {
                color = GOLD_COLOR
                textSize = 20f
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.drawText("Personal best: $longestStreak days", CARD_WIDTH / 2f, y + 150f, bestPaint)
        }
    }

    private fun drawMotivationalQuote(canvas: Canvas, y: Float, textColor: Int) {
        val quotes = listOf(
            "Growing every day.",
            "Building wisdom, one word at a time.",
            "The journey of self-improvement.",
            "Progress, not perfection."
        )
        val quote = quotes.random()

        val quotePaint = Paint().apply {
            color = textColor
            textSize = 24f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            isAntiAlias = true
        }
        canvas.drawText("\"$quote\"", CARD_WIDTH / 2f, y, quotePaint)
    }

    private fun drawStoryStatRow(
        canvas: Canvas,
        y: Float,
        label: String,
        value: String,
        accentColor: Int,
        backgroundColor: Int,
        textColor: Int,
        textSecondaryColor: Int,
        isDarkMode: Boolean
    ) {
        val margin = 80f
        val width = STORY_WIDTH - 2 * margin
        val height = 90f

        // Row background
        val rowPaint = Paint().apply {
            color = if (isDarkMode) Color.parseColor("#1A2520") else Color.WHITE
            isAntiAlias = true
            setShadowLayer(8f, 0f, 2f, Color.argb(20, 0, 0, 0))
        }
        canvas.drawRoundRect(RectF(margin, y, margin + width, y + height), 16f, 16f, rowPaint)

        // Accent dot
        val dotPaint = Paint().apply {
            color = accentColor
            isAntiAlias = true
        }
        canvas.drawCircle(margin + 35f, y + height / 2f, 10f, dotPaint)

        // Label
        val labelPaint = Paint().apply {
            color = textSecondaryColor
            textSize = 24f
            isAntiAlias = true
        }
        canvas.drawText(label, margin + 60f, y + height / 2f + 8f, labelPaint)

        // Value
        val valuePaint = Paint().apply {
            color = textColor
            textSize = 32f
            textAlign = Paint.Align.RIGHT
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText(value, margin + width - 30f, y + height / 2f + 10f, valuePaint)
    }

    private fun drawWatermark(canvas: Canvas, y: Float, isDarkMode: Boolean) {
        val watermarkPaint = Paint().apply {
            color = if (isDarkMode) Color.parseColor("#444444") else Color.parseColor("#CCCCCC")
            textSize = 18f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("Prody - Your Growth Companion", canvas.width / 2f, y, watermarkPaint)

        // Tiny logo placeholder (just text for now)
        val logoPaint = Paint().apply {
            color = PRIMARY_COLOR
            alpha = if (isDarkMode) 100 else 150
            textSize = 16f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("prody.app", canvas.width / 2f, y + 25f, logoPaint)
    }

    private fun saveBitmapToCache(context: Context, bitmap: Bitmap, fileName: String): Uri {
        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs()
        val file = File(cachePath, fileName)

        FileOutputStream(file).use { stream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        }

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    private fun formatNumber(number: Int): String {
        return when {
            number >= 1000000 -> String.format("%.1fM", number / 1000000.0)
            number >= 1000 -> String.format("%.1fK", number / 1000.0)
            else -> number.toString()
        }
    }

    /**
     * Package names for popular social media apps.
     */
    object SocialMediaPackages {
        const val INSTAGRAM = "com.instagram.android"
        const val WHATSAPP = "com.whatsapp"
        const val FACEBOOK = "com.facebook.katana"
        const val TWITTER = "com.twitter.android"
        const val TELEGRAM = "org.telegram.messenger"
        const val SNAPCHAT = "com.snapchat.android"
    }
}
