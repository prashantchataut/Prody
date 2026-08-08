package com.kairos.app.ui.browse

import com.kairos.app.data.local.entity.JournalEntryEntity
import com.kairos.app.data.local.entity.VocabularyEntity

/**
 * Pure browse policies kept outside ViewModels for deterministic tests and reuse.
 *
 * The primary vocabulary signals are *saved* (favorited) and *studied* (reviewed
 * at least once). "New" means never studied; "Saved" means the user liked the word.
 */
fun filterVocabulary(
    words: List<VocabularyEntity>,
    favoritesOnly: Boolean,
    filterKey: String,
    query: String,
    nowMillis: Long = System.currentTimeMillis()
): List<VocabularyEntity> {
    val normalizedQuery = query.trim()
    return words.asSequence()
        .filter { !favoritesOnly || it.isFavorite }
        .filter {
            when (filterKey) {
                "new" -> !it.isFavorite && it.reviewCount == 0 && it.masteryLevel == 0
                "saved" -> it.isFavorite
                else -> true
            }
        }
        .filter {
            normalizedQuery.isBlank() ||
                it.word.contains(normalizedQuery, ignoreCase = true) ||
                it.definition.contains(normalizedQuery, ignoreCase = true) ||
                it.category.contains(normalizedQuery, ignoreCase = true)
        }
        .sortedWith(
            compareByDescending<VocabularyEntity> { it.nextReviewAt?.let { due -> due <= nowMillis } == true }
                .thenByDescending { it.isFavorite }
                .thenBy { it.word.lowercase() }
        )
        .toList()
}

fun filterJournalEntries(
    entries: List<JournalEntryEntity>,
    bookmarkedOnly: Boolean,
    query: String
): List<JournalEntryEntity> {
    val normalizedQuery = query.trim()
    return entries.asSequence()
        .filterNot { it.isDeleted }
        .filter { !bookmarkedOnly || it.isBookmarked }
        .filter {
            normalizedQuery.isBlank() ||
                it.title.contains(normalizedQuery, ignoreCase = true) ||
                it.content.contains(normalizedQuery, ignoreCase = true) ||
                it.tags.contains(normalizedQuery, ignoreCase = true)
        }
        .sortedByDescending { it.createdAt }
        .toList()
}
