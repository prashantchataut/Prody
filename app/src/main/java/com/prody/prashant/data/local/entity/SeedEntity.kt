package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Seed Entity - Tracks the "Seed -> Bloom" mechanic.
 *
 * Every day, Prody surfaces ONE "Seed" item (word/proverb/quote/phrase) from the content library.
 *
 * Seed States:
 * - PLANTED: Shown to user, waiting for engagement
 * - GROWING: User engaged with it (viewed detail, saved to collection)
 * - BLOOMED: User applied it (used in journal entry or future message)
 *
 * Bloom triggers rewards:
 * - 15 Discipline XP
 * - 5 Tokens
 *
 * This makes wisdom actionable and creates a unique progression loop.
 */
@Entity(
    tableName = "daily_seeds",
    indices = [
        Index(value = ["date"], unique = true),
        Index(value = ["userId", "date"])
    ]
)
data class SeedEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "local",
    val date: Long, // Day timestamp (start of day)
    val seedType: String, // "word", "proverb", "quote", "phrase"
    val seedContent: String, // The actual word/phrase/quote
    val seedSource: String = "", // "vocabulary", "quotes", "proverbs", "ai"
    val sourceId: Long? = null, // Reference to source entity if applicable

    // Seed state: "planted", "growing", "bloomed"
    val state: String = "planted",

    // Legacy compatibility field
    val hasBloomedToday: Boolean = false,

    // Bloom tracking
    val bloomedAt: Long? = null,
    val bloomedIn: String? = null, // "journal", "future_message"
    val bloomedEntryId: Long? = null, // Reference to the entry where it bloomed

    // Reward tracking
    val rewardClaimed: Boolean = false,
    val rewardPoints: Int = 25, // Legacy - replaced by rewardXp
    val rewardXp: Int = 15, // Discipline XP for blooming
    val rewardTokens: Int = 5, // Bonus tokens for blooming

    // Content matching helpers (populated at creation)
    val variations: String = "", // JSON array of word variations for matching
    val keywords: String = "", // JSON array of keywords for proverbs
    val keyPhrase: String? = null, // Key phrase for quote matching

    val createdAt: Long = System.currentTimeMillis()
) {
    /**
     * Check if this seed is currently active (not yet bloomed)
     */
    fun isActive(): Boolean = state != "bloomed" && !hasBloomedToday

    /**
     * Check if this seed is in growing state
     */
    fun isGrowing(): Boolean = state == "growing"

    /**
     * Check if this seed has bloomed
     */
    fun hasBloomed(): Boolean = state == "bloomed" || hasBloomedToday

    /**
     * Check if this seed matches content (case-insensitive matching)
     */
    fun matchesContent(text: String): Boolean {
        val lowerText = text.lowercase()

        return when (seedType) {
            "word" -> {
                // Check main content and variations
                val mainWord = seedContent.lowercase()
                if (lowerText.contains(mainWord)) return true

                // Check variations if provided
                if (variations.isNotEmpty()) {
                    try {
                        val varList = variations.removeSurrounding("[", "]")
                            .split(",")
                            .map { it.trim().removeSurrounding("\"") }
                        return varList.any { lowerText.contains(it.lowercase()) }
                    } catch (_: Exception) {
                        // Fall back to basic matching
                    }
                }
                false
            }
            "quote" -> {
                // Check key phrase
                keyPhrase?.let { lowerText.contains(it.lowercase()) } ?: false
            }
            "proverb" -> {
                // Check keywords
                if (keywords.isNotEmpty()) {
                    try {
                        val keywordList = keywords.removeSurrounding("[", "]")
                            .split(",")
                            .map { it.trim().removeSurrounding("\"") }
                        return keywordList.any { lowerText.contains(it.lowercase()) }
                    } catch (_: Exception) {
                        // Fall back to basic matching
                    }
                }
                // Fallback: check if any meaningful words match
                val seedWords = seedContent.lowercase().split(" ", ",", ";")
                seedWords.any { word -> word.length >= 4 && lowerText.contains(word.trim()) }
            }
            "phrase" -> {
                lowerText.contains(seedContent.lowercase())
            }
            else -> {
                // Fallback for unknown types
                val seedWords = seedContent.lowercase().split(" ", ",", ";")
                seedWords.any { word -> word.length >= 3 && lowerText.contains(word.trim()) }
            }
        }
    }
}

/**
 * Summary of a user's Seed -> Bloom history
 */
data class SeedBloomSummary(
    val totalSeeds: Int = 0,
    val totalBloomed: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastBloomDate: Long? = null,
    val bloomRate: Float = 0f
) {
    companion object {
        fun calculate(seeds: List<SeedEntity>): SeedBloomSummary {
            if (seeds.isEmpty()) return SeedBloomSummary()

            val bloomed = seeds.filter { it.hasBloomedToday }
            val sortedBloomed = bloomed.sortedByDescending { it.date }

            // Calculate current streak
            var currentStreak = 0
            val today = java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.HOUR_OF_DAY, 0)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }.timeInMillis

            val dayMs = 24 * 60 * 60 * 1000L
            var checkDate = today

            for (seed in sortedBloomed) {
                if (seed.date == checkDate || seed.date == checkDate - dayMs) {
                    currentStreak++
                    checkDate = seed.date - dayMs
                } else {
                    break
                }
            }

            // Calculate longest streak (simplified)
            var longestStreak = currentStreak
            var tempStreak = 0
            var prevDate = 0L

            for (seed in bloomed.sortedBy { it.date }) {
                if (prevDate == 0L || seed.date - prevDate <= dayMs) {
                    tempStreak++
                    longestStreak = maxOf(longestStreak, tempStreak)
                } else {
                    tempStreak = 1
                }
                prevDate = seed.date
            }

            return SeedBloomSummary(
                totalSeeds = seeds.size,
                totalBloomed = bloomed.size,
                currentStreak = currentStreak,
                longestStreak = longestStreak,
                lastBloomDate = bloomed.maxByOrNull { it.bloomedAt ?: 0 }?.bloomedAt,
                bloomRate = if (seeds.isNotEmpty()) bloomed.size.toFloat() / seeds.size else 0f
            )
        }
    }
}
