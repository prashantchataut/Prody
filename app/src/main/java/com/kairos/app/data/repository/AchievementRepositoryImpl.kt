package com.kairos.app.data.repository

import com.kairos.app.data.local.dao.UserDao
import com.kairos.app.data.local.entity.AchievementEntity
import com.kairos.app.domain.identity.KairosAchievements
import com.kairos.app.domain.model.AchievementProgress
import com.kairos.app.domain.repository.AchievementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AchievementRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : AchievementRepository {
    private val canonicalAchievementIds: Set<String> by lazy {
        KairosAchievements.allAchievements.mapTo(mutableSetOf()) { it.id }
    }

    override fun observeAchievements(): Flow<List<AchievementProgress>> =
        userDao.getAllAchievements().map { achievements ->
            achievements
                .filter { it.id in canonicalAchievementIds }
                .filterNot { it.isSecret && !it.isUnlocked }
                .map(AchievementEntity::toDomain)
        }
}

private fun AchievementEntity.toDomain(): AchievementProgress = AchievementProgress(
    id = id,
    name = name,
    description = description,
    iconId = iconId,
    category = category,
    requirement = requirement,
    currentProgress = currentProgress.coerceIn(0, requirement.coerceAtLeast(1)),
    isUnlocked = isUnlocked,
    unlockedAt = unlockedAt,
    rewardPoints = rewardValue.toIntOrNull() ?: xpReward,
    rarity = rarity,
    celebrationMessage = celebrationMessage,
    isHidden = isHidden,
    isSecret = isSecret
)
