package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quotes")
data class QuoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val content: String,
    val author: String,
    val source: String = "", // Book, speech, etc.
    val category: String = "wisdom", // wisdom, motivation, stoic, life, success, etc.
    val tags: String = "", // Comma-separated
    val isFavorite: Boolean = false,
    val shownAsDaily: Boolean = false,
    val shownAt: Long? = null,
    val reflectionPrompt: String = "" // A question to reflect on related to the quote
)

@Entity(tableName = "proverbs")
data class ProverbEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val content: String,
    val meaning: String,
    val origin: String = "", // Culture/language of origin
    val usage: String = "", // When to use this proverb
    val category: String = "wisdom",
    val isFavorite: Boolean = false,
    val shownAsDaily: Boolean = false,
    val shownAt: Long? = null
)

@Entity(tableName = "idioms")
data class IdiomEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val phrase: String,
    val meaning: String,
    val origin: String = "",
    val exampleSentence: String = "",
    val category: String = "general",
    val isFavorite: Boolean = false,
    val shownAsDaily: Boolean = false,
    val shownAt: Long? = null
)

@Entity(tableName = "phrases")
data class PhraseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val phrase: String,
    val meaning: String,
    val usage: String = "",
    val exampleSentence: String = "",
    val formality: String = "neutral", // formal, informal, neutral
    val category: String = "general",
    val isFavorite: Boolean = false,
    val shownAsDaily: Boolean = false,
    val shownAt: Long? = null
)
