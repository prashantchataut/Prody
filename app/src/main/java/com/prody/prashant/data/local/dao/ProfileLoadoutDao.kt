package com.prody.prashant.data.local.dao

import androidx.room.*
import com.prody.prashant.data.local.entity.PinnedBadgeEntity
import com.prody.prashant.data.local.entity.ProfileLoadoutEntity
import kotlinx.coroutines.flow.Flow

/**
 * [ProfileLoadoutDao] - Data Access Object for Profile Loadouts
 *
 * Handles all database operations for:
 * - Profile loadouts (saved cosmetic configurations)
 * - Pinned badges (badge pinboard system)
 */
@Dao
interface ProfileLoadoutDao {

    // =========================================================================
    // Profile Loadouts
    // =========================================================================

    /**
     * Gets all loadouts for a user, ordered by sort order.
     */
    @Query("SELECT * FROM profile_loadouts WHERE userId = :userId ORDER BY sortOrder ASC")
    fun getLoadoutsForUser(userId: String = "local"): Flow<List<ProfileLoadoutEntity>>

    /**
     * Gets all loadouts synchronously.
     */
    @Query("SELECT * FROM profile_loadouts WHERE userId = :userId ORDER BY sortOrder ASC")
    suspend fun getLoadoutsForUserSync(userId: String = "local"): List<ProfileLoadoutEntity>

    /**
     * Gets the currently active loadout for a user.
     */
    @Query("SELECT * FROM profile_loadouts WHERE userId = :userId AND isActive = 1 LIMIT 1")
    fun getActiveLoadout(userId: String = "local"): Flow<ProfileLoadoutEntity?>

    /**
     * Gets the active loadout synchronously.
     */
    @Query("SELECT * FROM profile_loadouts WHERE userId = :userId AND isActive = 1 LIMIT 1")
    suspend fun getActiveLoadoutSync(userId: String = "local"): ProfileLoadoutEntity?

    /**
     * Gets a specific loadout by ID.
     */
    @Query("SELECT * FROM profile_loadouts WHERE id = :loadoutId")
    suspend fun getLoadoutById(loadoutId: String): ProfileLoadoutEntity?

    /**
     * Gets a loadout by ID as Flow.
     */
    @Query("SELECT * FROM profile_loadouts WHERE id = :loadoutId")
    fun observeLoadoutById(loadoutId: String): Flow<ProfileLoadoutEntity?>

    /**
     * Gets the count of loadouts for a user.
     */
    @Query("SELECT COUNT(*) FROM profile_loadouts WHERE userId = :userId")
    suspend fun getLoadoutCount(userId: String = "local"): Int

    /**
     * Gets the count as Flow.
     */
    @Query("SELECT COUNT(*) FROM profile_loadouts WHERE userId = :userId")
    fun observeLoadoutCount(userId: String = "local"): Flow<Int>

    /**
     * Inserts a new loadout.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoadout(loadout: ProfileLoadoutEntity)

    /**
     * Updates an existing loadout.
     */
    @Update
    suspend fun updateLoadout(loadout: ProfileLoadoutEntity)

    /**
     * Deletes a loadout.
     */
    @Delete
    suspend fun deleteLoadout(loadout: ProfileLoadoutEntity)

    /**
     * Deletes a loadout by ID.
     */
    @Query("DELETE FROM profile_loadouts WHERE id = :loadoutId")
    suspend fun deleteLoadoutById(loadoutId: String)

    /**
     * Sets a loadout as active, deactivating all others for the user.
     */
    @Transaction
    suspend fun setActiveLoadout(loadoutId: String, userId: String = "local") {
        deactivateAllLoadouts(userId)
        activateLoadout(loadoutId)
    }

    /**
     * Deactivates all loadouts for a user.
     */
    @Query("UPDATE profile_loadouts SET isActive = 0 WHERE userId = :userId")
    suspend fun deactivateAllLoadouts(userId: String = "local")

    /**
     * Activates a specific loadout.
     */
    @Query("UPDATE profile_loadouts SET isActive = 1 WHERE id = :loadoutId")
    suspend fun activateLoadout(loadoutId: String)

    /**
     * Updates the banner for a loadout.
     */
    @Query("UPDATE profile_loadouts SET bannerId = :bannerId, updatedAt = :updatedAt WHERE id = :loadoutId")
    suspend fun updateBanner(loadoutId: String, bannerId: String, updatedAt: Long = System.currentTimeMillis())

    /**
     * Updates the frame for a loadout.
     */
    @Query("UPDATE profile_loadouts SET frameId = :frameId, updatedAt = :updatedAt WHERE id = :loadoutId")
    suspend fun updateFrame(loadoutId: String, frameId: String, updatedAt: Long = System.currentTimeMillis())

    /**
     * Updates the title for a loadout.
     */
    @Query("UPDATE profile_loadouts SET titleId = :titleId, updatedAt = :updatedAt WHERE id = :loadoutId")
    suspend fun updateTitle(loadoutId: String, titleId: String, updatedAt: Long = System.currentTimeMillis())

    /**
     * Updates the accent color for a loadout.
     */
    @Query("UPDATE profile_loadouts SET accentColorId = :accentColorId, updatedAt = :updatedAt WHERE id = :loadoutId")
    suspend fun updateAccentColor(loadoutId: String, accentColorId: String, updatedAt: Long = System.currentTimeMillis())

    /**
     * Updates pinned badges for a loadout.
     */
    @Query("UPDATE profile_loadouts SET pinnedBadgeIds = :pinnedBadgeIds, updatedAt = :updatedAt WHERE id = :loadoutId")
    suspend fun updatePinnedBadges(loadoutId: String, pinnedBadgeIds: String, updatedAt: Long = System.currentTimeMillis())

    /**
     * Updates loadout name.
     */
    @Query("UPDATE profile_loadouts SET name = :name, updatedAt = :updatedAt WHERE id = :loadoutId")
    suspend fun updateLoadoutName(loadoutId: String, name: String, updatedAt: Long = System.currentTimeMillis())

    /**
     * Clears all loadouts for a user.
     */
    @Query("DELETE FROM profile_loadouts WHERE userId = :userId")
    suspend fun clearLoadoutsForUser(userId: String = "local")

    // =========================================================================
    // Pinned Badges (Master Pinboard - separate from loadouts)
    // =========================================================================

    /**
     * Gets all pinned badges for a user, ordered by sort order.
     */
    @Query("SELECT * FROM pinned_badges WHERE userId = :userId ORDER BY sortOrder ASC")
    fun getPinnedBadges(userId: String = "local"): Flow<List<PinnedBadgeEntity>>

    /**
     * Gets pinned badges synchronously.
     */
    @Query("SELECT * FROM pinned_badges WHERE userId = :userId ORDER BY sortOrder ASC")
    suspend fun getPinnedBadgesSync(userId: String = "local"): List<PinnedBadgeEntity>

    /**
     * Gets the count of pinned badges.
     */
    @Query("SELECT COUNT(*) FROM pinned_badges WHERE userId = :userId")
    suspend fun getPinnedBadgeCount(userId: String = "local"): Int

    /**
     * Checks if a badge is pinned.
     */
    @Query("SELECT COUNT(*) FROM pinned_badges WHERE userId = :userId AND badgeId = :badgeId")
    suspend fun isBadgePinned(userId: String = "local", badgeId: String): Int

    /**
     * Inserts a pinned badge.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPinnedBadge(pinnedBadge: PinnedBadgeEntity)

    /**
     * Deletes a pinned badge by badge ID.
     */
    @Query("DELETE FROM pinned_badges WHERE userId = :userId AND badgeId = :badgeId")
    suspend fun unpinBadge(userId: String = "local", badgeId: String)

    /**
     * Updates the sort order of a pinned badge.
     */
    @Query("UPDATE pinned_badges SET sortOrder = :sortOrder WHERE userId = :userId AND badgeId = :badgeId")
    suspend fun updatePinnedBadgeOrder(userId: String = "local", badgeId: String, sortOrder: Int)

    /**
     * Clears all pinned badges for a user.
     */
    @Query("DELETE FROM pinned_badges WHERE userId = :userId")
    suspend fun clearPinnedBadges(userId: String = "local")

    /**
     * Gets the maximum sort order for pinned badges.
     */
    @Query("SELECT MAX(sortOrder) FROM pinned_badges WHERE userId = :userId")
    suspend fun getMaxPinnedBadgeSortOrder(userId: String = "local"): Int?
}
