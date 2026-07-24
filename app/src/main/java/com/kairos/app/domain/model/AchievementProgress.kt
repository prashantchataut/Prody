package com.kairos.app.domain.model

/**
 * UI-independent representation of a meaningful Kairos milestone.
 * Persistence details remain in the data layer.
 */
data class AchievementProgress(
    val id: String,
    val name: String,
    val description: String,
    val iconId: String,
    val category: String,
    val requirement: Int,
    val currentProgress: Int,
    val isUnlocked: Boolean,
    val unlockedAt: Long?,
    val rewardPoints: Int,
    val rarity: String,
    val celebrationMessage: String,
    val isHidden: Boolean,
    val isSecret: Boolean
) {
    val progressFraction: Float
        get() = if (isUnlocked) 1f else currentProgress.toFloat() / requirement.coerceAtLeast(1)

    val remaining: Int
        get() = (requirement - currentProgress).coerceAtLeast(0)
}
