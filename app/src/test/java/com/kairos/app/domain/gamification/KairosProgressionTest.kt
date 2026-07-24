package com.kairos.app.domain.gamification

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class KairosProgressionTest {
    @Test
    fun repeatedActionsTaperAndThenStopForTheDay() {
        var day = GrowthDay()
        repeat(GrowthAction.DAILY_MOMENT_COMPLETED.dailyHardLimit) {
            day = KairosProgressionPolicy.award(GrowthAction.DAILY_MOMENT_COMPLETED, day).updatedDay
        }
        val extra = KairosProgressionPolicy.award(GrowthAction.DAILY_MOMENT_COMPLETED, day)
        assertEquals(0, extra.reward.points)
        assertTrue(extra.reward.reachedDailyLimit)
    }

    @Test
    fun balancedPracticeRewardsBreadthNotAppOpens() {
        var day = GrowthDay()
        day = KairosProgressionPolicy.award(GrowthAction.VOCABULARY_RECALLED, day).updatedDay
        day = KairosProgressionPolicy.award(GrowthAction.REFLECTION_COMPLETED, day).updatedDay
        assertEquals(8, KairosProgressionPolicy.balancedDayBonus(day))
    }

    @Test
    fun levelProgressIsStableAtBoundaries() {
        assertEquals(1, KairosProgressionPolicy.levelFor(119))
        assertEquals(2, KairosProgressionPolicy.levelFor(120))
        assertEquals(0f, KairosProgressionPolicy.progressWithinLevel(120), 0.0001f)
    }
}
