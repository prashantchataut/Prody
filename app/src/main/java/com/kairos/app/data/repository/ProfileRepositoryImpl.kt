package com.kairos.app.data.repository

import com.kairos.app.data.local.dao.UserDao
import com.kairos.app.data.local.entity.AchievementEntity
import com.kairos.app.data.local.entity.PlayerSkillsEntity
import com.kairos.app.data.local.entity.UserProfileEntity
import com.kairos.app.domain.common.ErrorType
import com.kairos.app.domain.common.Result
import com.kairos.app.domain.common.runSuspendCatching
import com.kairos.app.domain.identity.KairosAchievements
import com.kairos.app.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : ProfileRepository {
    override fun observeProfile(): Flow<UserProfileEntity?> = userDao.getUserProfile()

    private val canonicalAchievementIds: Set<String> by lazy {
        KairosAchievements.allAchievements.mapTo(mutableSetOf()) { it.id }
    }

    override fun observeUnlockedAchievements(): Flow<List<AchievementEntity>> =
        userDao.getUnlockedAchievements().map { rows -> rows.filter { it.id in canonicalAchievementIds } }

    override fun observeLockedAchievements(): Flow<List<AchievementEntity>> =
        userDao.getLockedAchievements().map { rows -> rows.filter { it.id in canonicalAchievementIds } }

    override fun observePlayerSkills(): Flow<PlayerSkillsEntity?> = userDao.observePlayerSkills()

    override suspend fun getProfile(): Result<UserProfileEntity> =
        runSuspendCatching(ErrorType.DATABASE, "Kairos could not load your profile.") {
            userDao.getUserProfileSync() ?: throw NoSuchElementException("Local profile was not found")
        }

    override suspend fun getAllAchievements(): Result<List<AchievementEntity>> =
        runSuspendCatching(ErrorType.DATABASE, "Kairos could not load achievements.") {
            userDao.getAllAchievements().first().filter { it.id in canonicalAchievementIds }
        }

    override suspend fun updateBanner(bannerId: String): Result<Unit> =
        runSuspendCatching(ErrorType.DATABASE, "Kairos could not apply this banner.") {
            userDao.updateBanner(bannerId)
        }

    override suspend fun updateProfileFields(
        displayName: String?,
        bio: String?,
        avatarId: String?,
        titleId: String?,
        bannerId: String?
    ): Result<Unit> = runSuspendCatching(
        ErrorType.DATABASE,
        "Kairos could not save your profile."
    ) {
        userDao.updateProfileFields(
            displayName = displayName,
            bio = bio,
            avatarId = avatarId,
            titleId = titleId,
            bannerId = bannerId
        )
    }
}
