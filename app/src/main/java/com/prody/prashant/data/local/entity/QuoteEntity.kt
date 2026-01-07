package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "quotes",
    indices = [
        androidx.room.Index(value = ["category"]),
        androidx.room.Index(value = ["author"]),
        androidx.room.Index(value = ["isFavorite"]),
        androidx.room.Index(value = ["shownAsDaily"])
    ]
)
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
