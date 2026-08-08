package com.kairos.app.domain.recommendation

import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Personalization profile built from explicit feedback and behavioral signals.
 *
 * Everything here is pure and deterministic so the result can be unit-tested and,
 * more importantly, explained to the user. The profile feeds both the daily plan
 * ranker and the Learn-tab study queue.
 */
data class InteractionSignal(
    val interaction: ContentInteractionType,
    val category: String? = null,
    val sourceKey: String? = null,
    val difficulty: Int? = null,
    val createdAt: Long = 0L
)

data class PersonalizationProfile(
    val categoryAffinity: Map<String, Double>,
    val sourceAffinity: Map<String, Double>,
    val difficultyDelta: Int,
    /** 0..1 — how much signal exists to personalize on at all. */
    val confidence: Double
) {

    companion object {

        const val DAY_MILLIS = 24L * 60 * 60 * 1000L

        private const val SIGNAL_HALF_LIFE_DAYS = 21.0

        // Weight of each interaction type when building affinity. Positive signals
        // move toward the category/author; negative signals move away.
        private val SIGNAL_WEIGHTS = mapOf(
            ContentInteractionType.SAVED to 0.45,
            ContentInteractionType.MORE_LIKE_THIS to 0.55,
            ContentInteractionType.COMPLETED to 0.30,
            ContentInteractionType.USED_IN_JOURNAL to 0.35,
            ContentInteractionType.OPENED to 0.08,
            ContentInteractionType.IMPRESSION to 0.02,
            ContentInteractionType.UNSAVED to -0.35,
            ContentInteractionType.LESS_LIKE_THIS to -0.55,
            ContentInteractionType.DISMISSED to -0.25,
            ContentInteractionType.TOO_EASY to -0.10,
            ContentInteractionType.TOO_HARD to -0.10
        )

        /**
         * Build a profile from the user's chosen categories plus the recorded
         * interaction history. Older signals decay exponentially so taste changes
         * are actually reflected.
         */
        fun compute(
            preferredCategories: Map<String, Double>,
            interactions: List<InteractionSignal>,
            now: Long
        ): PersonalizationProfile {
            val categoryScores = preferredCategories.mapValues { it.value.coerceIn(0.0, 2.0) }.toMutableMap()
            val sourceScores = mutableMapOf<String, Double>()
            var difficultyDelta = 0.0

            var weightedSignalCount = 0.0
            var totalWeight = 0.0

            interactions.forEach { signal ->
                val ageDays = ((now - signal.createdAt).coerceAtLeast(0L)) / DAY_MILLIS.toDouble()
                val decay = exp(-ageDays / SIGNAL_HALF_LIFE_DAYS)
                val weight = (SIGNAL_WEIGHTS[signal.interaction] ?: 0.0) * decay
                if (weight == 0.0) return@forEach

                totalWeight += abs(weight)
                weightedSignalCount += 1

                if (signal.category != null) {
                    val key = signal.category.trim().lowercase()
                    if (key.isNotEmpty()) {
                        categoryScores[key] = (categoryScores[key] ?: 0.0) + weight
                    }
                }
                if (signal.sourceKey != null) {
                    val key = signal.sourceKey.trim().lowercase()
                    if (key.isNotEmpty()) {
                        sourceScores[key] = (sourceScores[key] ?: 0.0) + weight
                    }
                }
                when (signal.interaction) {
                    ContentInteractionType.TOO_EASY -> difficultyDelta -= 0.5 * decay
                    ContentInteractionType.TOO_HARD -> difficultyDelta += 0.5 * decay
                    else -> Unit
                }
            }

            return PersonalizationProfile(
                categoryAffinity = categoryScores
                    .mapValues { it.value.coerceIn(-2.0, 2.0) }
                    .filterValues { it != 0.0 },
                sourceAffinity = sourceScores
                    .mapValues { it.value.coerceIn(-2.0, 2.0) }
                    .filterValues { it != 0.0 },
                difficultyDelta = difficultyDelta.roundToInt().coerceIn(-2, 2),
                confidence = if (totalWeight == 0.0) 0.0 else min(1.0, weightedSignalCount / 40.0)
            )
        }
    }
}

/**
 * One item considered for the user's study queue.
 */
data class StudyCandidate(
    val contentId: Long,
    val category: String,
    val sourceKey: String = "",
    val difficulty: Int = 2,
    val isNew: Boolean = false,
    val isDueForReview: Boolean = false,
    val isMastered: Boolean = false,
    val lastSeenAt: Long? = null,
    val reviewDueAt: Long? = null,
    val quality: Double = 0.5
)

data class QueueAssignment(
    val contentId: Long,
    val reason: String,
    val score: Double
)

/**
 * Builds the Learn-tab study queue: due reviews first (spaced repetition is the
 * highest-leverage signal), then fresh new words matched to the user's profile,
 * then a diversity fill so the stream stays wide. Every assignment carries a
 * human-readable reason — no opaque "because you might like this".
 */
object PersonalizedStudyQueue {

    private const val OVERDUE_BOOST_DAY = 0.04

    fun build(
        candidates: List<StudyCandidate>,
        profile: PersonalizationProfile,
        now: Long,
        maxItems: Int = 10,
        newWordRatio: Float = 0.4f
    ): List<QueueAssignment> {
        if (candidates.isEmpty()) return emptyList()

        val scored = candidates.map { candidate ->
            val score = scoreCandidate(candidate, profile, now)
            QueueAssignment(
                contentId = candidate.contentId,
                reason = reasonFor(candidate, profile, now),
                score = score
            )
        }

        val dueReviews = scored.filter { candidate ->
            candidates.first { it.contentId == candidate.contentId }.isDueForReview
        }.sortedByDescending { it.score }

        val newItems = scored.filter { candidate ->
            val c = candidates.first { it.contentId == candidate.contentId }
            c.isNew && !c.isDueForReview
        }.sortedByDescending { it.score }

        val remainder = scored.filter { candidate ->
            val c = candidates.first { it.contentId == candidate.contentId }
            !c.isNew && !c.isDueForReview
        }.sortedByDescending { it.score }

        val maxNew = (maxItems * newWordRatio.coerceIn(0f, 1f)).toInt().coerceIn(1, maxItems)
        val result = LinkedHashMap<Long, QueueAssignment>()
        dueReviews.forEach { result.putIfAbsent(it.contentId, it) }
        newItems.take(maxNew).forEach { result.putIfAbsent(it.contentId, it) }
        remainder.forEach { result.putIfAbsent(it.contentId, it) }

        return result.values.take(maxItems).toList()
    }

    private fun scoreCandidate(
        candidate: StudyCandidate,
        profile: PersonalizationProfile,
        now: Long
    ): Double {
        var score = 0.0

        if (candidate.isDueForReview) {
            val overdueDays = candidate.reviewDueAt?.let { (now - it).coerceAtLeast(0L) / DAY_MILLIS } ?: 0L
            score += min(1.0, 0.85 + overdueDays * OVERDUE_BOOST_DAY)
        } else {
            // New items get a modest base so they are never starved out entirely.
            score += if (candidate.isNew) 0.45 else 0.15
        }

        val categoryKey = candidate.category.trim().lowercase()
        score += (profile.categoryAffinity[categoryKey] ?: 0.0).coerceIn(-2.0, 2.0) * 0.10

        val sourceKey = candidate.sourceKey.trim().lowercase()
        score += (profile.sourceAffinity[sourceKey] ?: 0.0).coerceIn(-2.0, 2.0) * 0.06

        val targetDifficulty = (2 + profile.difficultyDelta).coerceIn(1, 5)
        val difficultyFit = 1.0 - abs(candidate.difficulty - targetDifficulty).coerceAtMost(4) / 4.0
        score += difficultyFit * 0.10

        // Novelty: things seen recently rank lower, unseen items rank higher.
        if (candidate.lastSeenAt != null) {
            val daysSince = (now - candidate.lastSeenAt).coerceAtLeast(0L) / DAY_MILLIS
            score += when {
                daysSince < 2 -> -0.6
                daysSince < 7 -> -0.25
                daysSince < 14 -> 0.1
                else -> 0.3
            }
        } else {
            score += 0.25
        }

        score += candidate.quality.coerceIn(0.0, 1.0) * 0.08
        if (candidate.isMastered) score -= 0.3

        return score
    }

    private fun reasonFor(
        candidate: StudyCandidate,
        profile: PersonalizationProfile,
        now: Long
    ): String {
        val parts = mutableListOf<String>()
        if (candidate.isDueForReview) {
            val overdueDays = candidate.reviewDueAt?.let { (now - it).coerceAtLeast(0L) / DAY_MILLIS } ?: 0L
            parts += if (overdueDays <= 0) "Due for review today" else "Due for review · $overdueDays day${if (overdueDays == 1L) "" else "s"} overdue"
        } else if (candidate.isNew) {
            parts += "New word to learn"
        }

        val categoryKey = candidate.category.trim().lowercase()
        val categoryPull = profile.categoryAffinity[categoryKey] ?: 0.0
        if (categoryPull > 0.15) parts += "Matches your interest in ${candidate.category.trim().lowercase()}"

        val sourceKey = candidate.sourceKey.trim().lowercase()
        val sourcePull = profile.sourceAffinity[sourceKey] ?: 0.0
        if (sourcePull > 0.15) parts += "You engage with ${candidate.sourceKey.trim()}"

        val targetDifficulty = (2 + profile.difficultyDelta).coerceIn(1, 5)
        if (candidate.difficulty == targetDifficulty) parts += "Right level for you"
        if (candidate.lastSeenAt == null) parts += "Fresh to you"

        return when {
            parts.isEmpty() -> "A balanced pick for your next session"
            parts.size == 1 -> parts.first().replaceFirstChar { it.uppercase() }
            else -> parts.take(2).joinToString(" · ").replaceFirstChar { it.uppercase() }
        }
    }

    private const val DAY_MILLIS = 24L * 60 * 60 * 1000L
}
