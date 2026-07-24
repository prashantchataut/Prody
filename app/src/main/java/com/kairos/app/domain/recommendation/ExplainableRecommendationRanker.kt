package com.kairos.app.domain.recommendation

import java.security.MessageDigest
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Deterministic, explainable recommendation ranker.
 *
 * This deliberately avoids random selection and opaque ML. Every score can be
 * inspected, reproduced, and explained to the user. A learned ranker can replace
 * this later only after reliable interaction data and offline evaluation exist.
 */
class ExplainableRecommendationRanker {

    fun rank(
        candidates: List<RecommendationCandidate>,
        context: RecommendationContext
    ): List<RankedCandidate> = candidates
        .map { candidate -> score(candidate, context) }
        .sortedWith(
            compareByDescending<RankedCandidate> { it.score }
                .thenBy { stableTieBreaker(it.candidate, context) }
        )

    private fun score(
        candidate: RecommendationCandidate,
        context: RecommendationContext
    ): RankedCandidate {
        val recentForItem = context.recentSelections
            .filter { it.type == candidate.type && it.contentId == candidate.id }
            .maxByOrNull { it.selectedAt }
        val recentForCategory = context.recentSelections
            .count { it.type == candidate.type && it.category.equals(candidate.category, true) }
        val recentForSource = context.recentSelections
            .count { it.type == candidate.type && it.sourceKey.equals(candidate.sourceKey, true) }

        val novelty = when {
            recentForItem == null -> 1.0
            context.now - recentForItem.selectedAt >= DAYS_30 -> 0.75
            context.now - recentForItem.selectedAt >= DAYS_14 -> 0.45
            context.now - recentForItem.selectedAt >= DAYS_7 -> 0.2
            else -> 0.0
        }

        val reviewUrgency = if (candidate.type == DailyContentType.VOCABULARY) {
            when {
                candidate.reviewDueAt == null || !candidate.isIntroduced -> 0.38
                candidate.reviewDueAt <= context.now -> {
                    val overdueDays = ((context.now - candidate.reviewDueAt) / DAYS_1).coerceAtLeast(0)
                    min(1.0, 0.75 + overdueDays * 0.04)
                }
                else -> 0.12
            }
        } else 0.0

        val categoryKey = candidate.category.normalized()
        val sourceKey = candidate.sourceKey.normalized()
        val affinity = normalizeSigned(
            (context.preferredCategories[categoryKey] ?: 0.0) +
                (context.categoryFeedback[categoryKey] ?: 0.0)
        )
        val sourceFeedback = normalizeSigned(context.sourceFeedback[sourceKey] ?: 0.0)
        val temporalFit = if (context.temporalCategories.any { it.equals(candidate.category, true) }) 1.0 else 0.35
        val quality = candidate.quality.coerceIn(0.0, 1.0)
        val diversity = (1.0 - min(1.0, recentForCategory * 0.18 + recentForSource * 0.12)).coerceAtLeast(0.0)
        val difficultyFit = candidate.difficulty?.let { difficulty ->
            (1.0 - abs(difficulty - context.targetDifficulty).coerceAtMost(4) / 4.0).coerceIn(0.0, 1.0)
        } ?: 0.5

        val repetitionPenalty = when {
            recentForItem == null -> 0.0
            context.now - recentForItem.selectedAt < DAYS_2 -> 0.8
            context.now - recentForItem.selectedAt < DAYS_7 -> 0.45
            context.now - recentForItem.selectedAt < DAYS_14 -> 0.2
            else -> 0.0
        }

        val breakdown = if (candidate.type == DailyContentType.VOCABULARY) {
            ScoreBreakdown(
                reviewUrgency = reviewUrgency * 0.35,
                affinity = affinity * 0.10,
                novelty = novelty * 0.15,
                temporalFit = temporalFit * 0.05,
                quality = quality * 0.10,
                diversity = diversity * 0.10,
                difficultyFit = difficultyFit * 0.10,
                feedback = sourceFeedback * 0.05,
                repetitionPenalty = repetitionPenalty
            )
        } else {
            ScoreBreakdown(
                affinity = affinity * 0.25,
                novelty = novelty * 0.25,
                temporalFit = temporalFit * 0.15,
                quality = quality * 0.15,
                diversity = diversity * 0.10,
                feedback = sourceFeedback * 0.10,
                repetitionPenalty = repetitionPenalty
            )
        }

        return RankedCandidate(
            candidate = candidate,
            score = breakdown.total,
            reason = explain(breakdown, candidate),
            breakdown = breakdown
        )
    }

    private fun explain(breakdown: ScoreBreakdown, candidate: RecommendationCandidate): String {
        val reasons = buildList {
            add(breakdown.reviewUrgency to "ready for review")
            add(breakdown.affinity to "matches your interests")
            add(breakdown.novelty to "feels fresh")
            add(breakdown.temporalFit to "fits today's rhythm")
            add(breakdown.quality to "has a strong explanation")
            add(breakdown.diversity to "adds variety")
            add(breakdown.difficultyFit to "matches your level")
            add(breakdown.feedback to "reflects your feedback")
        }.filter { it.first > 0.0 }
            .sortedByDescending { it.first }
            .take(2)
            .map { it.second }

        return when {
            reasons.isEmpty() -> "A balanced ${candidate.type.name.lowercase()} pick for today"
            reasons.size == 1 -> reasons.first().replaceFirstChar { it.uppercase() }
            else -> reasons.joinToString(separator = " and ").replaceFirstChar { it.uppercase() }
        }
    }

    private fun stableTieBreaker(
        candidate: RecommendationCandidate,
        context: RecommendationContext
    ): String {
        val input = "${context.userId}|${context.epochDay}|${candidate.type}|${candidate.id}"
        return MessageDigest.getInstance("SHA-256")
            .digest(input.toByteArray())
            .joinToString("") { byte -> "%02x".format(byte) }
    }

    private fun normalizeSigned(value: Double): Double = when {
        value > 0 -> min(1.0, value / 5.0)
        value < 0 -> max(-1.0, value / 5.0)
        else -> 0.0
    }

    private fun String.normalized(): String = trim().lowercase()

    private companion object {
        const val DAYS_1 = 24L * 60L * 60L * 1_000L
        const val DAYS_2 = 2L * DAYS_1
        const val DAYS_7 = 7L * DAYS_1
        const val DAYS_14 = 14L * DAYS_1
        const val DAYS_30 = 30L * DAYS_1
    }
}
