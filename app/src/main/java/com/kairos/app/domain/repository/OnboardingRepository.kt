package com.kairos.app.domain.repository

import com.kairos.app.domain.common.Result

data class OnboardingPreferences(
    val vocabularyDifficulty: Int,
    val wisdomCategories: Set<String>,
    val wordCategories: Set<String> = wisdomCategories,
    val practiceSessionSize: Int = 5
)

interface OnboardingRepository {
    suspend fun completeSetup(preferences: OnboardingPreferences): Result<Unit>
}
