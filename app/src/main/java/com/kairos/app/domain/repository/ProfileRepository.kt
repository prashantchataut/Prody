package com.kairos.app.domain.repository

import com.kairos.app.data.local.entity.AchievementEntity
import com.kairos.app.data.local.entity.PlayerSkillsEntity
import com.kairos.app.data.local.entity.UserProfileEntity
import com.kairos.app.domain.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Local-first boundary for profile identity, progress presentation, and cosmetics.
 *
 * Profile screens should not coordinate Room DAOs directly. Unlock decisions are
 * still owned by the gamification domain; this repository only exposes durable
 * profile state and applies explicit user customization choices.
 */
interface ProfileRepository {
    fun observeProfile(): Flow<UserProfileEntity?>
    fun observeUnlockedAchievements(): Flow<List<AchievementEntity>>
    fun observeLockedAchievements(): Flow<List<AchievementEntity>>
    fun observePlayerSkills(): Flow<PlayerSkillsEntity?>

    suspend fun getProfile(): Result<UserProfileEntity>
    suspend fun getAllAchievements(): Result<List<AchievementEntity>>
    suspend fun updateBanner(bannerId: String): Result<Unit>
    suspend fun updateProfileFields(
        displayName: String? = null,
        bio: String? = null,
        avatarId: String? = null,
        titleId: String? = null,
        bannerId: String? = null
    ): Result<Unit>
}
