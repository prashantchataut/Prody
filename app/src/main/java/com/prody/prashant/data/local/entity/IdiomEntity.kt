package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "idioms",
    indices = [
        androidx.room.Index(value = ["category"]),
        androidx.room.Index(value = ["isFavorite"]),
        androidx.room.Index(value = ["shownAsDaily"])
    ]
)
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