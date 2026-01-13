package com.prody.prashant.domain.model

import com.prody.prashant.data.local.entity.MonthlyLetterEntity
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
) {
    /**
     * Convert to entity for database storage
     */
    fun toEntity(): MonthlyLetterEntity {
        return MonthlyLetterEntity(
            id = id,
            userId = userId,
            month = monthYear.monthValue,
            year = monthYear.year,
            greeting = greeting,

            // Activity
            entriesCount = activitySummary.entriesCount,
            microEntriesCount = activitySummary.microEntriesCount,
            totalWords = activitySummary.totalWords,
            activeDays = activitySummary.activeDays,
            averageWordsPerEntry = activitySummary.averageWordsPerEntry,
            mostActiveWeek = activitySummary.mostActiveWeek,

            // Themes
            topThemes = JSONArray(themeAnalysis.topThemes).toString(),
            themesAnalysis = themeAnalysis.narrative,
            recurringWords = JSONArray(themeAnalysis.recurringWords).toString(),

            // Mood
            moodJourney = moodJourney.toJson(),
            dominantMood = moodJourney.dominantMood,
            moodAnalysis = moodJourney.narrative,
            moodTrend = moodJourney.trend.name.lowercase(),

            // Buddha
            patternObservation = buddhaInsight.observation,
            buddhaWisdom = buddhaInsight.wisdom,

            // Milestones
            achievedMilestones = JSONArray(milestones.achieved.map { it.toJson() }).toString(),
            upcomingMilestones = JSONArray(milestones.upcoming.map { it.toJson() }).toString(),
            streakInfo = milestones.streakNote,

            // Comparison
            entriesChangePercent = comparison?.entriesChangePercent ?: 0,
            wordsChangePercent = comparison?.wordsChangePercent ?: 0,
            comparisonNote = comparison?.note,

            // Highlight
            highlightEntryId = highlight?.entryId,
            highlightQuote = highlight?.quote,
            highlightReason = highlight?.reason,

            // Closing
            closingMessage = closing.message,
            encouragementNote = closing.encouragement,

            // Metadata
            generatedAt = generatedAt,
            isRead = isRead,
            readAt = readAt,
            isFavorite = isFavorite
        )
    }

    companion object {
        /**
         * Convert from entity to domain model
         */
        fun fromEntity(entity: MonthlyLetterEntity): MonthlyLetter {
            return MonthlyLetter(
                id = entity.id,
                userId = entity.userId,
                monthYear = YearMonth.of(entity.year, entity.month),
                greeting = entity.greeting,

                activitySummary = ActivitySummary(
                    entriesCount = entity.entriesCount,
                    microEntriesCount = entity.microEntriesCount,
                    totalWords = entity.totalWords,
                    activeDays = entity.activeDays,
                    averageWordsPerEntry = entity.averageWordsPerEntry,
                    mostActiveWeek = entity.mostActiveWeek
                ),

                themeAnalysis = ThemeAnalysis(
                    topThemes = parseJsonArray(entity.topThemes),
                    narrative = entity.themesAnalysis,
                    recurringWords = parseJsonArray(entity.recurringWords)
                ),

                moodJourney = MoodJourney.fromJson(
                    entity.moodJourney,
                    entity.dominantMood,
                    entity.moodAnalysis,
                    entity.moodTrend
                ),

                buddhaInsight = BuddhaInsight(
                    observation = entity.patternObservation,
                    wisdom = entity.buddhaWisdom
                ),

                milestones = Milestones(
                    achieved = parseMilestones(entity.achievedMilestones),
                    upcoming = parseMilestones(entity.upcomingMilestones),
                    streakNote = entity.streakInfo
                ),

                comparison = if (entity.entriesChangePercent != 0 || entity.wordsChangePercent != 0) {
                    MonthComparison(
                        entriesChangePercent = entity.entriesChangePercent,
                        wordsChangePercent = entity.wordsChangePercent,
                        note = entity.comparisonNote
                    )
                } else null,

                highlight = if (entity.highlightEntryId != null) {
                    LetterHighlight(
                        entryId = entity.highlightEntryId,
                        quote = entity.highlightQuote,
                        reason = entity.highlightReason
                    )
                } else null,

                closing = LetterClosing(
                    message = entity.closingMessage,
                    encouragement = entity.encouragementNote
                ),

                generatedAt = entity.generatedAt,
                isRead = entity.isRead,
                readAt = entity.readAt,
                isFavorite = entity.isFavorite
            )
        }

        private fun parseJsonArray(json: String): List<String> {
            return try {
                val array = JSONArray(json)
                List(array.length()) { array.getString(it) }
            } catch (e: Exception) {
                emptyList()
            }
        }

        private fun parseMilestones(json: String): List<Milestone> {
            return try {
                val array = JSONArray(json)
                List(array.length()) {
                    Milestone.fromJson(array.getJSONObject(it))
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}

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
                icon = json.optString("icon", null)
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
