package com.prody.prashant.data.repository

import com.prody.prashant.data.local.entity.MonthlyLetterEntity
import com.prody.prashant.domain.model.ActivitySummary
import com.prody.prashant.domain.model.BuddhaInsight
import com.prody.prashant.domain.model.LetterClosing
import com.prody.prashant.domain.model.LetterHighlight
import com.prody.prashant.domain.model.Milestones
import com.prody.prashant.domain.model.Milestone
import com.prody.prashant.domain.model.MonthComparison
import com.prody.prashant.domain.model.MonthlyLetter
import com.prody.prashant.domain.model.MoodJourney
import com.prody.prashant.domain.model.MoodTrend
import com.prody.prashant.domain.model.ThemeAnalysis
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.YearMonth

object MonthlyLetterMapper {

    fun toEntity(letter: MonthlyLetter): MonthlyLetterEntity {
        return MonthlyLetterEntity(
            id = letter.id,
            userId = letter.userId,
            month = letter.monthYear.monthValue,
            year = letter.monthYear.year,
            greeting = letter.greeting,
            entriesCount = letter.activitySummary.entriesCount,
            microEntriesCount = letter.activitySummary.microEntriesCount,
            totalWords = letter.activitySummary.totalWords,
            activeDays = letter.activitySummary.activeDays,
            averageWordsPerEntry = letter.activitySummary.averageWordsPerEntry,
            mostActiveWeek = letter.activitySummary.mostActiveWeek,
            topThemes = JSONArray(letter.themeAnalysis.topThemes).toString(),
            themesAnalysis = letter.themeAnalysis.narrative,
            recurringWords = JSONArray(letter.themeAnalysis.recurringWords).toString(),
            moodJourney = letter.moodJourney.toJson(),
            dominantMood = letter.moodJourney.dominantMood,
            moodAnalysis = letter.moodJourney.narrative,
            moodTrend = letter.moodJourney.trend.name.lowercase(),
            patternObservation = letter.buddhaInsight.observation,
            buddhaWisdom = letter.buddhaInsight.wisdom,
            achievedMilestones = JSONArray(letter.milestones.achieved.map { it.toJson() }).toString(),
            upcomingMilestones = JSONArray(letter.milestones.upcoming.map { it.toJson() }).toString(),
            streakInfo = letter.milestones.streakNote,
            entriesChangePercent = letter.comparison?.entriesChangePercent ?: 0,
            wordsChangePercent = letter.comparison?.wordsChangePercent ?: 0,
            comparisonNote = letter.comparison?.note,
            highlightEntryId = letter.highlight?.entryId,
            highlightQuote = letter.highlight?.quote,
            highlightReason = letter.highlight?.reason,
            closingMessage = letter.closing.message,
            encouragementNote = letter.closing.encouragement,
            generatedAt = letter.generatedAt,
            isRead = letter.isRead,
            readAt = letter.readAt,
            isFavorite = letter.isFavorite
        )
    }

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