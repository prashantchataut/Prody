package com.prody.prashant.domain.model

import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.YearMonth

/**
 * Domain model for Monthly Growth Letter.
 *
 * Represents a personalized monthly letter that summarizes the user's journaling journey
 * with a warm, personal tone - like a caring friend who's been watching their progress.
 */
data class MonthlyLetter(
    val id: Long = 0,
    val userId: String = "local",
    val monthYear: YearMonth,
    val greeting: String,

    // Activity summary
    val activitySummary: ActivitySummary,

    // Theme analysis
    val themeAnalysis: ThemeAnalysis,

    // Mood journey
    val moodJourney: MoodJourney,

    // Buddha's insight
    val buddhaInsight: BuddhaInsight,

    // Milestones
    val milestones: Milestones,

    // Comparison
    val comparison: MonthComparison?,

    // Highlights
    val highlight: LetterHighlight?,

    // Closing
    val closing: LetterClosing,

    // Metadata
    val generatedAt: Long,
    val isRead: Boolean = false,
    val readAt: Long? = null,
    val isFavorite: Boolean = false
)

/**
 * Activity summary for the month
 */
data class ActivitySummary(
    val entriesCount: Int,
    val microEntriesCount: Int,
    val totalWords: Int,
    val activeDays: Int,
    val averageWordsPerEntry: Int,
    val mostActiveWeek: String?
) {
    val totalEntries: Int
        get() = entriesCount + microEntriesCount

    val hasActivity: Boolean
        get() = totalEntries > 0
}

/**
 * Theme analysis from AI tags and content
 */
data class ThemeAnalysis(
    val topThemes: List<String>,
    val narrative: String,
    val recurringWords: List<String>
)

/**
 * Mood journey through the month
 */
data class MoodJourney(
    val dataPoints: List<MoodDataPoint>,
    val dominantMood: String?,
    val narrative: String,
    val trend: MoodTrend
) {
    fun toJson(): String {
        val array = JSONArray()
        dataPoints.forEach { point ->
            array.put(JSONObject().apply {
                put("date", point.date.toString())
                put("mood", point.mood)
                put("intensity", point.intensity)
            })
        }
        return array.toString()
    }

    companion object {
        fun fromJson(json: String, dominantMood: String?, narrative: String, trendStr: String): MoodJourney {
            val dataPoints = try {
                val array = JSONArray(json)
                List(array.length()) {
                    val obj = array.getJSONObject(it)
                    MoodDataPoint(
                        date = LocalDate.parse(obj.getString("date")),
                        mood = obj.getString("mood"),
                        intensity = obj.getInt("intensity")
                    )
                }
            } catch (e: Exception) {
                emptyList()
            }

            val trend = MoodTrend.fromString(trendStr)

            return MoodJourney(dataPoints, dominantMood, narrative, trend)
        }
    }
}

data class MoodDataPoint(
    val date: LocalDate,
    val mood: String,
    val intensity: Int
)

/**
 * Buddha's personalized insight
 */
data class BuddhaInsight(
    val observation: String,
    val wisdom: String?
)

/**
 * Milestones achieved and upcoming
 */
data class Milestones(
    val achieved: List<Milestone>,
    val upcoming: List<Milestone>,
    val streakNote: String?
)

data class Milestone(
    val title: String,
    val description: String,
    val progress: Int = 100, // 0-100, 100 means achieved
    val icon: String? = null
) {
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("title", title)
            put("description", description)
            put("progress", progress)
            icon?.let { put("icon", it) }
        }
    }

    companion object {
        fun fromJson(json: JSONObject): Milestone {
            return Milestone(
                title = json.getString("title"),
                description = json.getString("description"),
                progress = json.optInt("progress", 100),
                icon = if (json.has("icon") && !json.isNull("icon")) json.getString("icon") else null
            )
        }
    }
}

/**
 * Month-over-month comparison
 */
data class MonthComparison(
    val entriesChangePercent: Int,
    val wordsChangePercent: Int,
    val note: String?
)

/**
 * Highlighted entry from the month
 */
data class LetterHighlight(
    val entryId: Long,
    val quote: String?,
    val reason: String?
)

/**
 * Closing message
 */
data class LetterClosing(
    val message: String,
    val encouragement: String?
)
