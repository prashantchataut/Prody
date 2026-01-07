package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "phrases",
    indices = [
        androidx.room.Index(value = ["category"]),
        androidx.room.Index(value = ["isFavorite"]),
        androidx.room.Index(value = ["shownAsDaily"])
    ]
)
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