package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.Index

/**
 * Immutable-per-day recommendation decision.
 *
 * The composite key guarantees that a user receives one stable item for each
 * content type and local date, even across process death and app restarts.
 */
@Entity(
    tableName = "daily_content_selections",
    primaryKeys = ["userId", "localDate", "contentType"],
    indices = [
        Index(value = ["userId", "selectedAt"]),
        Index(value = ["contentType", "contentId"])
    ]
)
data class DailyContentSelectionEntity(
    val userId: String,
    val localDate: String,
    val contentType: String,
    val contentId: Long,
    val category: String,
    val sourceKey: String,
    val algorithmVersion: Int,
    val score: Double,
    val reason: String,
    val selectedAt: Long,
    val impressedAt: Long? = null,
    val completedAt: Long? = null
)
