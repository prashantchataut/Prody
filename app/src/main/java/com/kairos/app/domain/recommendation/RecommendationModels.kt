package com.kairos.app.domain.recommendation

/** Content families that participate in the daily recommendation plan. */
enum class DailyContentType {
    VOCABULARY,
    QUOTE
}

/**
 * Explicit feedback and behavioral signals. The list is intentionally small and
 * product-facing so ranking remains understandable and auditable.
 */
enum class ContentInteractionType {
    IMPRESSION,
    OPENED,
    SAVED,
    UNSAVED,
    DISMISSED,
    COMPLETED,
    USED_IN_JOURNAL,
    MORE_LIKE_THIS,
    LESS_LIKE_THIS,
    TOO_EASY,
    TOO_HARD
}

data class RecommendationCandidate(
    val id: Long,
    val type: DailyContentType,
    val category: String,
    val sourceKey: String,
    val difficulty: Int? = null,
    val quality: Double = 0.5,
    val reviewDueAt: Long? = null,
    val isIntroduced: Boolean = false,
    val isMastered: Boolean = false
)

data class CandidateHistory(
    val selectedAt: Long,
    val contentId: Long,
    val type: DailyContentType,
    val category: String,
    val sourceKey: String
)

data class RecommendationContext(
    val userId: String,
    val epochDay: Long,
    val now: Long,
    val targetDifficulty: Int = 2,
    val preferredCategories: Map<String, Double> = emptyMap(),
    val recentSelections: List<CandidateHistory> = emptyList(),
    val categoryFeedback: Map<String, Double> = emptyMap(),
    val sourceFeedback: Map<String, Double> = emptyMap(),
    val temporalCategories: Set<String> = emptySet()
)

data class ScoreBreakdown(
    val reviewUrgency: Double = 0.0,
    val affinity: Double = 0.0,
    val novelty: Double = 0.0,
    val temporalFit: Double = 0.0,
    val quality: Double = 0.0,
    val diversity: Double = 0.0,
    val difficultyFit: Double = 0.0,
    val feedback: Double = 0.0,
    val repetitionPenalty: Double = 0.0
) {
    val total: Double
        get() = reviewUrgency + affinity + novelty + temporalFit + quality +
            diversity + difficultyFit + feedback - repetitionPenalty
}

data class RankedCandidate(
    val candidate: RecommendationCandidate,
    val score: Double,
    val reason: String,
    val breakdown: ScoreBreakdown
)
