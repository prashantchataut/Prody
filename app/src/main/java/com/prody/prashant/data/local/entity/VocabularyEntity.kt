package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vocabulary")
data class VocabularyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val word: String,
    val definition: String,
    val pronunciation: String = "",
    val partOfSpeech: String = "", // noun, verb, adjective, etc.
    val exampleSentence: String = "",
    val synonyms: String = "", // Comma-separated
    val antonyms: String = "", // Comma-separated
    val origin: String = "",
    val difficulty: Int = 1, // 1-5 scale
    val category: String = "general", // academic, business, literary, etc.
    val isLearned: Boolean = false,
    val learnedAt: Long? = null,
    val reviewCount: Int = 0,
    val lastReviewedAt: Long? = null,
    val nextReviewAt: Long? = null, // For spaced repetition
    val masteryLevel: Int = 0, // 0-100
    val isFavorite: Boolean = false,
    val shownAsDaily: Boolean = false,
    val shownAt: Long? = null
)
