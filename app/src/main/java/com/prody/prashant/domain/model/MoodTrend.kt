package com.prody.prashant.domain.model

/**
 * Unified enumeration for mood trends over time.
 *
 * This is the canonical definition used across the codebase.
 * Different features may use different subsets of these values.
 */
enum class MoodTrend(val displayName: String, val description: String) {
    IMPROVING("Improving", "Your mood has been getting better"),
    STABLE("Stable", "Your mood has been consistent"),
    DECLINING("Declining", "Your mood has been challenging lately"),
    VOLATILE("Variable", "Your mood has been fluctuating"),
    VARIABLE("Variable", "Your mood changes frequently"),
    FLUCTUATING("Fluctuating", "Highly variable mood patterns"),
    INSUFFICIENT_DATA("Not enough data", "Keep journaling to see patterns");

    companion object {
        fun fromString(value: String): MoodTrend {
            return entries.find { it.name.equals(value, ignoreCase = true) }
                ?: INSUFFICIENT_DATA
        }
    }
}
