package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.Fts4

/**
 * FTS4 virtual table for full-text journal search.
 *
 * Enables fast, tokenized search across journal content, titles, tags,
 * and AI-generated themes. Replaces slow LIKE queries with MATCH-based
 * search that supports stemming, prefix matching, and phrase queries.
 *
 * The FTS table is automatically kept in sync via Room's content sync
 * mechanism — when journal_entries is modified, this table is updated.
 */
@Fts4(contentEntity = JournalEntryEntity::class)
@Entity(tableName = "journal_entries_fts")
data class JournalEntryFtsEntity(
    val title: String = "",
    val content: String = "",
    val tags: String = "",
    val aiThemes: String? = null
)