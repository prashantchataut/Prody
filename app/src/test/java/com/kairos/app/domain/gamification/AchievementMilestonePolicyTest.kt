package com.kairos.app.domain.gamification

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import com.kairos.app.domain.identity.KairosAchievements
import org.junit.Assert.assertTrue
import org.junit.Test

class AchievementMilestonePolicyTest {
    @Test
    fun `milestone definitions are internally valid`() {
        assertTrue(AchievementMilestonePolicy.validate().isEmpty())
    }

    @Test
    fun `every milestone points to a catalog achievement`() {
        val catalogIds = KairosAchievements.allAchievements.map { it.id }.toSet()
        AchievementMilestonePolicy.Evidence.entries.forEach { evidence ->
            AchievementMilestonePolicy.milestonesFor(evidence).forEach { milestone ->
                assertTrue(
                    "Missing achievement definition for ${milestone.achievementId}",
                    milestone.achievementId in catalogIds
                )
            }
        }
    }

    @Test
    fun `journal evidence reaches only completed milestones`() {
        val reached = AchievementMilestonePolicy.reached(
            AchievementMilestonePolicy.Evidence.JOURNAL_ENTRY,
            currentValue = 10
        )
        assertEquals(listOf("first_journal", "journal_5", "journal_10"), reached.map { it.achievementId })
    }

    @Test
    fun `next milestone advances predictably`() {
        val next = AchievementMilestonePolicy.next(
            AchievementMilestonePolicy.Evidence.FUTURE_LETTER_SENT,
            currentValue = 5
        )
        assertEquals("letter_10", next?.achievementId)
        assertNull(
            AchievementMilestonePolicy.next(
                AchievementMilestonePolicy.Evidence.FUTURE_LETTER_SENT,
                currentValue = 10
            )
        )
    }
}
