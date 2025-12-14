package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

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