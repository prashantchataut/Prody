package com.kairos.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** Append-only behavioral signal used by the explainable recommendation engine. */
@Entity(
    tableName = "content_interactions",
    indices = [
        Index(value = ["userId", "createdAt"]),
        Index(value = ["userId", "contentType", "contentId"]),
        Index(value = ["userId", "category"])
    ]
)
data class ContentInteractionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val localDate: String,
    val contentType: String,
    val contentId: Long,
    val category: String,
    val sourceKey: String,
    val difficulty: Int? = null,
    val interactionType: String,
    val createdAt: Long
)
