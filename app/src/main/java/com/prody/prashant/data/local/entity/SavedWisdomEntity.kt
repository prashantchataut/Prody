package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity for saved wisdom items (quotes, words, proverbs, phrases)
 * that users want to remember and revisit.
 *
 * Part of the Wisdom Collection feature that allows users to bookmark
 * any piece of wisdom that resonates with them for later review
 * and intelligent resurfacing.
 */
@Entity(
    tableName = "saved_wisdom",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["type"]),
        Index(value = ["savedAt"]),
        Index(value = ["userId", "type"]),
        Index(value = ["sourceId", "type"], unique = true)
    ]
)
data class SavedWisdomEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // User authentication - prepared for multi-user support
    val userId: String = "local",

    // Type of wisdom: QUOTE, WORD, PROVERB, PHRASE, IDIOM
    val type: String,

    // The actual wisdom content
    val content: String,

    // Author/source (for quotes), or definition (for words)
    val author: String? = null,

    // Secondary content (e.g., etymology for words, meaning for proverbs)
    val secondaryContent: String? = null,

    // Reference to original entity (quoteId, wordId, etc.)
    val sourceId: Long? = null,

    // Tags for filtering and smart resurfacing
    val tags: String = "", // Comma-separated

    // Theme/category for contextual resurfacing
    val theme: String? = null,

    // Timestamps
    val savedAt: Long = System.currentTimeMillis(),

    // Smart resurfacing tracking
    val lastShownAt: Long? = null,
    val timesShown: Int = 0,

    // User engagement tracking
    val timesViewed: Int = 0,
    val lastViewedAt: Long? = null,

    // Personal note from user about why this resonates
    val userNote: String? = null,

    // Sync metadata
    val syncStatus: String = "pending",
    val lastSyncedAt: Long? = null,
    val isDeleted: Boolean = false
) {
    companion object {
        const val TYPE_QUOTE = "QUOTE"
        const val TYPE_WORD = "WORD"
        const val TYPE_PROVERB = "PROVERB"
        const val TYPE_PHRASE = "PHRASE"
        const val TYPE_IDIOM = "IDIOM"
        const val TYPE_BUDDHA_WISDOM = "BUDDHA_WISDOM"
    }
}
