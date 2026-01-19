package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Evidence Entity - THE LOCKER
 *
 * Evidence are moments of truth/growth collected throughout the user's journey.
 * Instead of abstract "XP" or "plants", these are concrete, meaningful moments:
 *
 * Types of Evidence:
 * - RECEIPT: A contradiction/pattern found by The Mirror
 * - WITNESS: A follow-up completed by Haven (user confirmed outcome)
 * - PROPHECY: A Future Message prediction that was verified
 * - BREAKTHROUGH: A significant insight from journaling
 * - STREAK: A streak milestone reached
 *
 * Evidence creates a "Locker" of growth artifacts the user can revisit.
 * Each piece of evidence is a small text snippet - the brutal truth.
 */
@Entity(
    tableName = "evidence",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["evidenceType"]),
        Index(value = ["collectedAt"]),
        Index(value = ["userId", "evidenceType"]),
        Index(value = ["sourceId", "sourceType"])
    ]
)
data class EvidenceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // User identification
    val userId: String = "local",

    // Type of evidence (determines icon, styling, and grouping)
    val evidenceType: String, // receipt, witness, prophecy, breakthrough, streak

    // The actual evidence content - the brutal truth
    val content: String,

    // Optional secondary content (for context)
    val secondaryContent: String? = null,

    // Source tracking (what generated this evidence)
    val sourceType: String? = null, // journal, haven, future_message, mirror, streak
    val sourceId: Long? = null, // ID of the source entity

    // For receipts: the comparison details
    val thenDate: Long? = null,
    val nowDate: Long? = null,
    val daysApart: Int? = null,

    // For witness: the outcome
    val witnessOutcome: String? = null, // success, failed, partial

    // For prophecy: was the prediction accurate?
    val predictionAccurate: Boolean? = null,

    // Rarity/significance level
    val rarity: String = "common", // common, rare, epic, legendary

    // Has the user viewed this evidence?
    val isViewed: Boolean = false,
    val viewedAt: Long? = null,

    // Is this evidence "pinned" in the locker?
    val isPinned: Boolean = false,

    // Metadata
    val collectedAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),

    // Sync metadata
    val syncStatus: String = "pending",
    val isDeleted: Boolean = false
)

/**
 * Evidence types
 */
object EvidenceType {
    const val RECEIPT = "receipt"       // Mirror found a contradiction/pattern
    const val WITNESS = "witness"       // Haven follow-up completed
    const val PROPHECY = "prophecy"     // Future Message prediction verified
    const val BREAKTHROUGH = "breakthrough" // Significant journal insight
    const val STREAK = "streak"         // Streak milestone reached
}

/**
 * Evidence rarity levels
 */
object EvidenceRarity {
    const val COMMON = "common"         // Standard evidence
    const val RARE = "rare"             // Uncommon patterns
    const val EPIC = "epic"             // Significant breakthroughs
    const val LEGENDARY = "legendary"   // Major life moments
}

/**
 * Evidence source types
 */
object EvidenceSource {
    const val JOURNAL = "journal"
    const val HAVEN = "haven"
    const val FUTURE_MESSAGE = "future_message"
    const val MIRROR = "mirror"
    const val STREAK = "streak"
}
