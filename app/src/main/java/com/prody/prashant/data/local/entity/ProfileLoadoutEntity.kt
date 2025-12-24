package com.prody.prashant.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.prody.prashant.domain.identity.ProfileLoadout

/**
 * [ProfileLoadoutEntity] - Database entity for Profile Loadouts
 *
 * Stores user's saved profile "looks" that can be switched instantly.
 * Each loadout contains banner, frame, title, accent color, and pinned badges.
 */
@Entity(
    tableName = "profile_loadouts",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["isActive"]),
        Index(value = ["userId", "isActive"])
    ]
)
data class ProfileLoadoutEntity(
    @PrimaryKey
    val id: String,

    /** User ID for multi-user support */
    val userId: String = "local",

    /** User-defined name for this loadout */
    val name: String,

    /** Selected banner ID from ProdyBanners */
    val bannerId: String,

    /** Selected frame ID from ProdyFrames */
    val frameId: String,

    /** Selected title ID from ProdyTitles */
    val titleId: String,

    /** Selected accent color ID from AccentColors */
    val accentColorId: String,

    /** Comma-separated list of pinned badge/achievement IDs */
    val pinnedBadgeIds: String = "",

    /** Whether this is the currently active loadout */
    val isActive: Boolean = false,

    /** Timestamp when created */
    val createdAt: Long = System.currentTimeMillis(),

    /** Timestamp when last modified */
    val updatedAt: Long = System.currentTimeMillis(),

    /** Display order in loadout list */
    val sortOrder: Int = 0
) {
    /**
     * Converts to domain model.
     */
    fun toDomain(): ProfileLoadout.Loadout {
        return ProfileLoadout.Loadout(
            id = id,
            name = name,
            bannerId = bannerId,
            frameId = frameId,
            titleId = titleId,
            accentColorId = accentColorId,
            pinnedBadgeIds = if (pinnedBadgeIds.isBlank()) {
                emptyList()
            } else {
                pinnedBadgeIds.split(",").filter { it.isNotBlank() }
            },
            isActive = isActive,
            createdAt = createdAt,
            updatedAt = updatedAt,
            sortOrder = sortOrder
        )
    }

    companion object {
        /**
         * Creates entity from domain model.
         */
        fun fromDomain(
            loadout: ProfileLoadout.Loadout,
            userId: String = "local"
        ): ProfileLoadoutEntity {
            return ProfileLoadoutEntity(
                id = loadout.id,
                userId = userId,
                name = loadout.name,
                bannerId = loadout.bannerId,
                frameId = loadout.frameId,
                titleId = loadout.titleId,
                accentColorId = loadout.accentColorId,
                pinnedBadgeIds = loadout.pinnedBadgeIds.joinToString(","),
                isActive = loadout.isActive,
                createdAt = loadout.createdAt,
                updatedAt = loadout.updatedAt,
                sortOrder = loadout.sortOrder
            )
        }
    }
}

/**
 * [PinnedBadgeEntity] - Entity for badge pinboard system
 *
 * Tracks which badges are pinned and their display order.
 * This is separate from loadouts for the "master" pinboard.
 */
@Entity(
    tableName = "pinned_badges",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["userId", "sortOrder"])
    ]
)
data class PinnedBadgeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** User ID for multi-user support */
    val userId: String = "local",

    /** Achievement/Badge ID */
    val badgeId: String,

    /** Display order in pinboard (lower = first) */
    val sortOrder: Int,

    /** When this badge was pinned */
    val pinnedAt: Long = System.currentTimeMillis()
)
