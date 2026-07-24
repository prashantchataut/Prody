package com.kairos.app.domain.repository

import com.kairos.app.domain.model.AchievementProgress
import kotlinx.coroutines.flow.Flow

interface AchievementRepository {
    fun observeAchievements(): Flow<List<AchievementProgress>>
}
