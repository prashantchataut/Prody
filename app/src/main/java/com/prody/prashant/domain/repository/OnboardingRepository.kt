package com.prody.prashant.domain.repository

import com.prody.prashant.domain.common.Result

data class OnboardingPreferences(
    val vocabularyDifficulty: Int,
    val wisdomCategories: Set<String>
)

interface OnboardingRepository {
    suspend fun completeSetup(preferences: OnboardingPreferences): Result<Unit>
}
