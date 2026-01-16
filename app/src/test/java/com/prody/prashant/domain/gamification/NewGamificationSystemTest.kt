package com.prody.prashant.domain.gamification

import org.junit.Assert.*
import org.junit.Test

/**
 * Comprehensive unit tests for the new gamification system.
 *
 * Tests all core mechanics:
 * - Skill progression (10-level system)
 * - Point calculations for all actions
 * - Daily caps and anti-exploit measures
 * - Streak system with Mindful Breaks
 * - Rank progression based on combined skill levels
 * - Achievement requirements
 * - Seed → Bloom mechanics
 * - Leaderboard scoring
 */
class NewGamificationSystemTest {

    // =========================================================================
    // SKILL LEVEL CALCULATION TESTS
    // =========================================================================

    @Test
    fun `Skill calculateLevel - 0 XP returns level 1`() {
        assertEquals(1, Skill.calculateLevel(0))
    }

    @Test
    fun `Skill calculateLevel - 49 XP returns level 1`() {
        assertEquals(1, Skill.calculateLevel(49))
    }

    @Test
    fun `Skill calculateLevel - 50 XP returns level 2`() {
        assertEquals(2, Skill.calculateLevel(50))
    }

    @Test
    fun `Skill calculateLevel - 119 XP returns level 2`() {
        assertEquals(2, Skill.calculateLevel(119))
    }

    @Test
    fun `Skill calculateLevel - 120 XP returns level 3`() {
        assertEquals(3, Skill.calculateLevel(120))
    }

    @Test
    fun `Skill calculateLevel - level thresholds are correct`() {
        // Level 1: 0 XP
        assertEquals(1, Skill.calculateLevel(0))
        // Level 2: 50 XP
        assertEquals(2, Skill.calculateLevel(50))
        // Level 3: 120 XP
        assertEquals(3, Skill.calculateLevel(120))
        // Level 4: 220 XP
        assertEquals(4, Skill.calculateLevel(220))
        // Level 5: 360 XP
        assertEquals(5, Skill.calculateLevel(360))
        // Level 6: 550 XP
        assertEquals(6, Skill.calculateLevel(550))
        // Level 7: 800 XP
        assertEquals(7, Skill.calculateLevel(800))
        // Level 8: 1150 XP
        assertEquals(8, Skill.calculateLevel(1150))
        // Level 9: 1600 XP
        assertEquals(9, Skill.calculateLevel(1600))
        // Level 10: 2200 XP (Mastery)
        assertEquals(10, Skill.calculateLevel(2200))
    }

    @Test
    fun `Skill calculateLevel - max level is 10`() {
        assertEquals(10, Skill.calculateLevel(5000))
        assertEquals(10, Skill.calculateLevel(10000))
    }

    @Test
    fun `Skill calculateLevelProgress - at threshold is 0 percent`() {
        // At exactly level 2 threshold (50 XP)
        val progress = Skill.calculateLevelProgress(50)
        assertEquals(0f, progress, 0.01f)
    }

    @Test
    fun `Skill calculateLevelProgress - halfway through level`() {
        // Level 2 is 50-119 (70 XP range), at 85 XP is halfway
        val progress = Skill.calculateLevelProgress(85)
        assertEquals(0.5f, progress, 0.01f)
    }

    @Test
    fun `Skill calculateLevelProgress - at max level returns 1`() {
        assertEquals(1f, Skill.calculateLevelProgress(2200), 0.01f)
        assertEquals(1f, Skill.calculateLevelProgress(5000), 0.01f)
    }

    @Test
    fun `Skill getXpUntilNextLevel - returns correct remaining XP`() {
        // At 30 XP (level 1), need 50-30=20 to reach level 2
        assertEquals(20, Skill.getXpUntilNextLevel(30))

        // At 100 XP (level 2), need 120-100=20 to reach level 3
        assertEquals(20, Skill.getXpUntilNextLevel(100))

        // At max level, returns 0
        assertEquals(0, Skill.getXpUntilNextLevel(2200))
    }

    // =========================================================================
    // POINT CALCULATOR TESTS - CLARITY (Journal)
    // =========================================================================

    @Test
    fun `PointCalculator Clarity - base journal entry is 10 XP`() {
        val points = PointCalculator.Clarity.calculateJournalPoints(
            wordCount = 50,
            triggeredInsight = false,
            includesReflection = false
        )
        assertEquals(10, points)
    }

    @Test
    fun `PointCalculator Clarity - 100+ words adds 5 XP bonus`() {
        val points = PointCalculator.Clarity.calculateJournalPoints(
            wordCount = 150,
            triggeredInsight = false,
            includesReflection = false
        )
        assertEquals(15, points) // 10 base + 5 word bonus
    }

    @Test
    fun `PointCalculator Clarity - 300+ words adds 10 XP bonus`() {
        val points = PointCalculator.Clarity.calculateJournalPoints(
            wordCount = 350,
            triggeredInsight = false,
            includesReflection = false
        )
        assertEquals(20, points) // 10 base + 10 word bonus
    }

    @Test
    fun `PointCalculator Clarity - insight adds 5 XP bonus`() {
        val points = PointCalculator.Clarity.calculateJournalPoints(
            wordCount = 50,
            triggeredInsight = true,
            includesReflection = false
        )
        assertEquals(15, points) // 10 base + 5 insight
    }

    @Test
    fun `PointCalculator Clarity - reflection adds 3 XP bonus`() {
        val points = PointCalculator.Clarity.calculateJournalPoints(
            wordCount = 50,
            triggeredInsight = false,
            includesReflection = true
        )
        assertEquals(13, points) // 10 base + 3 reflection
    }

    @Test
    fun `PointCalculator Clarity - all bonuses stack`() {
        val points = PointCalculator.Clarity.calculateJournalPoints(
            wordCount = 350,
            triggeredInsight = true,
            includesReflection = true
        )
        assertEquals(28, points) // 10 base + 10 words + 5 insight + 3 reflection
    }

    @Test
    fun `PointCalculator Clarity - micro entry is 3 XP`() {
        val points = PointCalculator.Clarity.calculateJournalPoints(
            wordCount = 20,
            triggeredInsight = false,
            includesReflection = false,
            isMicroEntry = true
        )
        assertEquals(3, points)
    }

    @Test
    fun `PointCalculator Clarity - detectReflection finds questions`() {
        assertTrue(PointCalculator.Clarity.detectReflection("What am I feeling today?"))
        assertTrue(PointCalculator.Clarity.detectReflection("Why did that happen?"))
    }

    @Test
    fun `PointCalculator Clarity - detectReflection finds introspection`() {
        assertTrue(PointCalculator.Clarity.detectReflection("I wonder about the future"))
        assertTrue(PointCalculator.Clarity.detectReflection("I feel grateful today"))
        assertTrue(PointCalculator.Clarity.detectReflection("I notice my thoughts"))
    }

    @Test
    fun `PointCalculator Clarity - detectReflection returns false for plain text`() {
        assertFalse(PointCalculator.Clarity.detectReflection("Today was a good day."))
        assertFalse(PointCalculator.Clarity.detectReflection("The weather is nice."))
    }

    // =========================================================================
    // POINT CALCULATOR TESTS - DISCIPLINE (Flashcards)
    // =========================================================================

    @Test
    fun `PointCalculator Discipline - flashcard points calculated correctly`() {
        val points = PointCalculator.Discipline.calculateFlashcardPoints(
            cardsReviewed = 10,
            accuracy = 0.5f
        )
        // 10 cards * 2 XP = 20 XP, no accuracy bonus at 50%
        assertEquals(20, points)
    }

    @Test
    fun `PointCalculator Discipline - 80+ percent accuracy adds 10 XP`() {
        val points = PointCalculator.Discipline.calculateFlashcardPoints(
            cardsReviewed = 10,
            accuracy = 0.85f
        )
        // 10 cards * 2 XP = 20 XP + 10 bonus = 30 XP
        assertEquals(30, points)
    }

    @Test
    fun `PointCalculator Discipline - 60-79 percent accuracy adds 5 XP`() {
        val points = PointCalculator.Discipline.calculateFlashcardPoints(
            cardsReviewed = 10,
            accuracy = 0.70f
        )
        // 10 cards * 2 XP = 20 XP + 5 bonus = 25 XP
        assertEquals(25, points)
    }

    @Test
    fun `PointCalculator Discipline - bloom seed is 15 XP`() {
        assertEquals(15, PointCalculator.Discipline.BLOOM_SEED)
    }

    // =========================================================================
    // POINT CALCULATOR TESTS - COURAGE (Future Messages)
    // =========================================================================

    @Test
    fun `PointCalculator Courage - base future message is 10 XP`() {
        val points = PointCalculator.Courage.calculateFutureMessagePoints(daysUntilDelivery = 7)
        assertEquals(10, points)
    }

    @Test
    fun `PointCalculator Courage - 30+ days adds 5 XP bonus`() {
        val points = PointCalculator.Courage.calculateFutureMessagePoints(daysUntilDelivery = 45)
        assertEquals(15, points) // 10 base + 5 bonus
    }

    @Test
    fun `PointCalculator Courage - 180+ days adds 10 XP bonus`() {
        val points = PointCalculator.Courage.calculateFutureMessagePoints(daysUntilDelivery = 200)
        assertEquals(20, points) // 10 base + 10 bonus
    }

    @Test
    fun `PointCalculator Courage - open delivered message is 15 XP`() {
        assertEquals(15, PointCalculator.Courage.OPEN_DELIVERED_MESSAGE)
    }

    @Test
    fun `PointCalculator Courage - reply to past self is 20 XP`() {
        assertEquals(20, PointCalculator.Courage.REPLY_TO_PAST_SELF)
    }

    // =========================================================================
    // DAILY CAPS TESTS
    // =========================================================================

    @Test
    fun `PointCalculator DailyCaps - Clarity cap is 150`() {
        assertEquals(150, PointCalculator.DailyCaps.CLARITY)
    }

    @Test
    fun `PointCalculator DailyCaps - Discipline cap is 150`() {
        assertEquals(150, PointCalculator.DailyCaps.DISCIPLINE)
    }

    @Test
    fun `PointCalculator DailyCaps - Courage cap is 100`() {
        assertEquals(100, PointCalculator.DailyCaps.COURAGE)
    }

    @Test
    fun `PointCalculator DailyCaps - applyDailyCap caps correctly`() {
        // Already at 140/150, trying to add 20
        val actual = PointCalculator.DailyCaps.applyDailyCap(
            skill = Skill.CLARITY,
            pointsToAdd = 20,
            currentDailyPoints = 140
        )
        assertEquals(10, actual) // Only 10 remaining
    }

    @Test
    fun `PointCalculator DailyCaps - returns 0 when cap reached`() {
        val actual = PointCalculator.DailyCaps.applyDailyCap(
            skill = Skill.CLARITY,
            pointsToAdd = 20,
            currentDailyPoints = 150
        )
        assertEquals(0, actual)
    }

    @Test
    fun `PointCalculator DailyCaps - no cap when under limit`() {
        val actual = PointCalculator.DailyCaps.applyDailyCap(
            skill = Skill.CLARITY,
            pointsToAdd = 20,
            currentDailyPoints = 50
        )
        assertEquals(20, actual) // Full amount
    }

    // =========================================================================
    // STREAK TESTS
    // =========================================================================

    @Test
    fun `StreakData - milestones are 7, 14, 30, 60, 100, 365`() {
        assertEquals(listOf(7, 14, 30, 60, 100, 365), StreakData.MILESTONES)
    }

    @Test
    fun `StreakData - max freezes is 2`() {
        assertEquals(2, StreakData.MAX_FREEZES_PER_MONTH)
    }

    @Test
    fun `StreakData - canUseMindfulBreak when freezes available`() {
        val streak = StreakData.initial().copy(freezesAvailable = 1)
        assertTrue(streak.canUseMindfulBreak)
    }

    @Test
    fun `StreakData - cannot use MindfulBreak when no freezes`() {
        val streak = StreakData.initial().copy(freezesAvailable = 0)
        assertFalse(streak.canUseMindfulBreak)
    }

    @Test
    fun `StreakData - isAtMilestone returns true for milestones`() {
        val streak7 = StreakData.initial().copy(currentStreak = 7)
        assertTrue(streak7.isAtMilestone)

        val streak30 = StreakData.initial().copy(currentStreak = 30)
        assertTrue(streak30.isAtMilestone)

        val streak100 = StreakData.initial().copy(currentStreak = 100)
        assertTrue(streak100.isAtMilestone)
    }

    @Test
    fun `StreakData - isAtMilestone returns false for non-milestones`() {
        val streak5 = StreakData.initial().copy(currentStreak = 5)
        assertFalse(streak5.isAtMilestone)

        val streak10 = StreakData.initial().copy(currentStreak = 10)
        assertFalse(streak10.isAtMilestone)
    }

    @Test
    fun `StreakData - nextMilestone calculated correctly`() {
        val streak3 = StreakData.initial().copy(currentStreak = 3)
        assertEquals(7, streak3.nextMilestone)

        val streak10 = StreakData.initial().copy(currentStreak = 10)
        assertEquals(14, streak10.nextMilestone)

        val streak50 = StreakData.initial().copy(currentStreak = 50)
        assertEquals(60, streak50.nextMilestone)
    }

    @Test
    fun `StreakCalculator - SAME_DAY when 0 days since last active`() {
        val today = java.time.LocalDate.now()
        val status = StreakCalculator.calculateStreakStatus(today, today)
        assertEquals(StreakStatus.SAME_DAY, status)
    }

    @Test
    fun `StreakCalculator - CONSECUTIVE when 1 day since last active`() {
        val yesterday = java.time.LocalDate.now().minusDays(1)
        val today = java.time.LocalDate.now()
        val status = StreakCalculator.calculateStreakStatus(yesterday, today)
        assertEquals(StreakStatus.CONSECUTIVE, status)
    }

    @Test
    fun `StreakCalculator - CAN_FREEZE when 2 days since last active`() {
        val twoDaysAgo = java.time.LocalDate.now().minusDays(2)
        val today = java.time.LocalDate.now()
        val status = StreakCalculator.calculateStreakStatus(twoDaysAgo, today)
        assertEquals(StreakStatus.CAN_FREEZE, status)
    }

    @Test
    fun `StreakCalculator - BROKEN when 3+ days since last active`() {
        val threeDaysAgo = java.time.LocalDate.now().minusDays(3)
        val today = java.time.LocalDate.now()
        val status = StreakCalculator.calculateStreakStatus(threeDaysAgo, today)
        assertEquals(StreakStatus.BROKEN, status)
    }

    @Test
    fun `StreakCalculator - getMilestoneReached returns milestone when crossed`() {
        val milestone = StreakCalculator.getMilestoneReached(6, 7)
        assertEquals(7, milestone)

        val milestone30 = StreakCalculator.getMilestoneReached(29, 31)
        assertEquals(30, milestone30)
    }

    @Test
    fun `StreakCalculator - getMilestoneReached returns null when no milestone crossed`() {
        val milestone = StreakCalculator.getMilestoneReached(5, 6)
        assertNull(milestone)
    }

    // =========================================================================
    // RANK SYSTEM TESTS
    // =========================================================================

    @Test
    fun `Rank - Seeker at combined level 0-4`() {
        assertEquals(Rank.SEEKER, Rank.fromCombinedLevel(0))
        assertEquals(Rank.SEEKER, Rank.fromCombinedLevel(4))
    }

    @Test
    fun `Rank - Learner at combined level 5-7`() {
        assertEquals(Rank.LEARNER, Rank.fromCombinedLevel(5))
        assertEquals(Rank.LEARNER, Rank.fromCombinedLevel(7))
    }

    @Test
    fun `Rank - Initiate at combined level 8-10`() {
        assertEquals(Rank.INITIATE, Rank.fromCombinedLevel(8))
        assertEquals(Rank.INITIATE, Rank.fromCombinedLevel(10))
    }

    @Test
    fun `Rank - Student at combined level 11-13`() {
        assertEquals(Rank.STUDENT, Rank.fromCombinedLevel(11))
        assertEquals(Rank.STUDENT, Rank.fromCombinedLevel(13))
    }

    @Test
    fun `Rank - Practitioner at combined level 14-16`() {
        assertEquals(Rank.PRACTITIONER, Rank.fromCombinedLevel(14))
        assertEquals(Rank.PRACTITIONER, Rank.fromCombinedLevel(16))
    }

    @Test
    fun `Rank - Contemplative at combined level 17-19`() {
        assertEquals(Rank.CONTEMPLATIVE, Rank.fromCombinedLevel(17))
        assertEquals(Rank.CONTEMPLATIVE, Rank.fromCombinedLevel(19))
    }

    @Test
    fun `Rank - Philosopher at combined level 20-23`() {
        assertEquals(Rank.PHILOSOPHER, Rank.fromCombinedLevel(20))
        assertEquals(Rank.PHILOSOPHER, Rank.fromCombinedLevel(23))
    }

    @Test
    fun `Rank - Sage at combined level 24-26`() {
        assertEquals(Rank.SAGE, Rank.fromCombinedLevel(24))
        assertEquals(Rank.SAGE, Rank.fromCombinedLevel(26))
    }

    @Test
    fun `Rank - Luminary at combined level 27-29`() {
        assertEquals(Rank.LUMINARY, Rank.fromCombinedLevel(27))
        assertEquals(Rank.LUMINARY, Rank.fromCombinedLevel(29))
    }

    @Test
    fun `Rank - Awakened at combined level 30`() {
        assertEquals(Rank.AWAKENED, Rank.fromCombinedLevel(30))
    }

    @Test
    fun `RankState - calculates from skill levels correctly`() {
        val state = RankState.fromSkillLevels(
            clarityLevel = 5,
            disciplineLevel = 4,
            courageLevel = 5
        )
        assertEquals(14, state.combinedLevel) // 5+4+5
        assertEquals(Rank.PRACTITIONER, state.currentRank) // Level 14-16
    }

    @Test
    fun `RankState - max combined level is 30`() {
        val state = RankState.fromSkillLevels(
            clarityLevel = 10,
            disciplineLevel = 10,
            courageLevel = 10
        )
        assertEquals(30, state.combinedLevel)
        assertEquals(Rank.AWAKENED, state.currentRank)
    }

    // =========================================================================
    // LEADERBOARD SCORING TESTS
    // =========================================================================

    @Test
    fun `LeaderboardScoring - courage has 1_2x multiplier`() {
        assertEquals(1.2f, LeaderboardScoring.COURAGE_MULTIPLIER)
    }

    @Test
    fun `LeaderboardScoring - calculateWeeklyScore with no streak`() {
        val score = LeaderboardScoring.calculateWeeklyScore(
            clarityXpThisWeek = 100,
            disciplineXpThisWeek = 100,
            courageXpThisWeek = 100,
            currentStreak = 0
        )
        // 100*1.0 + 100*1.0 + 100*1.2 + 0 = 320
        assertEquals(320, score)
    }

    @Test
    fun `LeaderboardScoring - calculateWeeklyScore with 3 day streak bonus`() {
        val score = LeaderboardScoring.calculateWeeklyScore(
            clarityXpThisWeek = 100,
            disciplineXpThisWeek = 100,
            courageXpThisWeek = 100,
            currentStreak = 3
        )
        // 100*1.0 + 100*1.0 + 100*1.2 + 10 = 330
        assertEquals(330, score)
    }

    @Test
    fun `LeaderboardScoring - calculateWeeklyScore with 7 day streak bonus`() {
        val score = LeaderboardScoring.calculateWeeklyScore(
            clarityXpThisWeek = 100,
            disciplineXpThisWeek = 100,
            courageXpThisWeek = 100,
            currentStreak = 7
        )
        // 100*1.0 + 100*1.0 + 100*1.2 + 25 = 345
        assertEquals(345, score)
    }

    @Test
    fun `LeaderboardScoring - courage XP has higher weight`() {
        val scoreCourage = LeaderboardScoring.calculateWeeklyScore(
            clarityXpThisWeek = 0,
            disciplineXpThisWeek = 0,
            courageXpThisWeek = 100,
            currentStreak = 0
        )
        // 0 + 0 + 100*1.2 = 120

        val scoreClarity = LeaderboardScoring.calculateWeeklyScore(
            clarityXpThisWeek = 100,
            disciplineXpThisWeek = 0,
            courageXpThisWeek = 0,
            currentStreak = 0
        )
        // 100*1.0 + 0 + 0 = 100

        assertTrue(scoreCourage > scoreClarity)
    }

    @Test
    fun `LeaderboardScoring - calculateStreakBonus returns correct bonus`() {
        assertEquals(0, LeaderboardScoring.calculateStreakBonus(0))
        assertEquals(0, LeaderboardScoring.calculateStreakBonus(2))
        assertEquals(10, LeaderboardScoring.calculateStreakBonus(3))
        assertEquals(10, LeaderboardScoring.calculateStreakBonus(6))
        assertEquals(25, LeaderboardScoring.calculateStreakBonus(7))
        assertEquals(25, LeaderboardScoring.calculateStreakBonus(13))
        assertEquals(50, LeaderboardScoring.calculateStreakBonus(14))
        assertEquals(50, LeaderboardScoring.calculateStreakBonus(29))
        assertEquals(100, LeaderboardScoring.calculateStreakBonus(30))
    }

    // =========================================================================
    // SEED BLOOM TESTS
    // =========================================================================

    @Test
    fun `DailySeed matchesContent - finds word in text`() {
        val seed = createTestSeed(
            type = SeedType.WORD,
            content = "wisdom",
            variations = listOf("wisdom", "wisdoms", "wise")
        )
        assertTrue(seed.matchesContent("Today I gained some wisdom about life."))
        assertTrue(seed.matchesContent("A wise person once said..."))
    }

    @Test
    fun `DailySeed matchesContent - case insensitive`() {
        val seed = createTestSeed(
            type = SeedType.WORD,
            content = "Courage",
            variations = listOf("courage")
        )
        assertTrue(seed.matchesContent("I showed COURAGE today"))
        assertTrue(seed.matchesContent("courage is important"))
    }

    @Test
    fun `DailySeed matchesContent - returns false when not found`() {
        val seed = createTestSeed(
            type = SeedType.WORD,
            content = "wisdom",
            variations = listOf("wisdom")
        )
        assertFalse(seed.matchesContent("Today was a good day"))
    }

    @Test
    fun `DailySeed generateWordVariations - creates common forms`() {
        val variations = DailySeed.generateWordVariations("hope")
        assertTrue(variations.contains("hope"))
        assertTrue(variations.contains("hopes"))
        assertTrue(variations.contains("hoped"))
        assertTrue(variations.contains("hoping"))
    }

    @Test
    fun `SeedState - progression is PLANTED to GROWING to BLOOMED`() {
        assertEquals(0, SeedState.PLANTED.ordinal)
        assertEquals(1, SeedState.GROWING.ordinal)
        assertEquals(2, SeedState.BLOOMED.ordinal)
    }

    // =========================================================================
    // ACHIEVEMENT TESTS
    // =========================================================================

    @Test
    fun `Achievements - First Words achievement exists`() {
        val achievement = Achievements.findById("first_words")
        assertNotNull(achievement)
        assertEquals("First Words", achievement?.name)
        assertEquals(AchievementCategory.REFLECTION, achievement?.category)
    }

    @Test
    fun `Achievements - all categories have achievements`() {
        AchievementCategory.entries.forEach { category ->
            val achievements = Achievements.getByCategory(category)
            assertTrue("Category $category should have achievements", achievements.isNotEmpty())
        }
    }

    @Test
    fun `Achievement requirement - Count type tracks progress`() {
        val req = AchievementRequirement.Count(10)
        val achievement = Achievement(
            id = "test",
            name = "Test Achievement",
            description = "Test",
            category = AchievementCategory.REFLECTION,
            rarity = AchievementRarity.COMMON,
            iconName = "ic_test",
            requirement = req
        )
        assertEquals(0.5f, achievement.calculateProgress(5), 0.01f)
        assertTrue(achievement.isRequirementMet(10))
        assertFalse(achievement.isRequirementMet(9))
    }

    @Test
    fun `Achievement requirement - Streak type checks value`() {
        val req = AchievementRequirement.Streak(30)
        val achievement = Achievement(
            id = "test",
            name = "Test Achievement",
            description = "Test",
            category = AchievementCategory.TIME,
            rarity = AchievementRarity.RARE,
            iconName = "ic_test",
            requirement = req
        )
        assertTrue(achievement.isRequirementMet(30))
        assertTrue(achievement.isRequirementMet(40))
        assertFalse(achievement.isRequirementMet(29))
    }

    @Test
    fun `AchievementRarity - ordering is Common to Mythic`() {
        assertTrue(AchievementRarity.COMMON.ordinal < AchievementRarity.MYTHIC.ordinal)
        assertEquals(listOf(
            AchievementRarity.COMMON,
            AchievementRarity.UNCOMMON,
            AchievementRarity.RARE,
            AchievementRarity.EPIC,
            AchievementRarity.LEGENDARY,
            AchievementRarity.MYTHIC
        ), AchievementRarity.entries)
    }

    // =========================================================================
    // SKILL PROGRESS TESTS
    // =========================================================================

    @Test
    fun `SkillProgress fromTotalXp - creates correct progress`() {
        val progress = SkillProgress.fromTotalXp(Skill.CLARITY, 100)
        assertEquals(Skill.CLARITY, progress.skill)
        assertEquals(2, progress.level) // 100 XP is level 2
        assertEquals(50, progress.currentPoints) // 100 - 50 (level 2 threshold)
        assertEquals(70, progress.pointsForNextLevel) // 120 - 50
        assertEquals(100, progress.totalPointsEarned)
        assertFalse(progress.isMastered)
    }

    @Test
    fun `SkillProgress fromTotalXp - mastery at level 10`() {
        val progress = SkillProgress.fromTotalXp(Skill.DISCIPLINE, 2200)
        assertEquals(10, progress.level)
        assertTrue(progress.isMastered)
        assertEquals(1f, progress.progressPercent, 0.01f)
    }

    @Test
    fun `PlayerSkillsState - combinedLevel is sum of skill levels`() {
        val state = PlayerSkillsState(
            clarity = SkillProgress.fromTotalXp(Skill.CLARITY, 50), // Level 2
            discipline = SkillProgress.fromTotalXp(Skill.DISCIPLINE, 120), // Level 3
            courage = SkillProgress.fromTotalXp(Skill.COURAGE, 220), // Level 4
            combinedLevel = 9, // 2+3+4
            tokens = 100,
            freezeTokensFromPerks = 1
        )
        assertEquals(2 + 3 + 4, state.combinedLevel)
    }

    @Test
    fun `PlayerSkillsState - hasMastery when any skill is level 10`() {
        val state = PlayerSkillsState(
            clarity = SkillProgress.fromTotalXp(Skill.CLARITY, 2200), // Mastered
            discipline = SkillProgress.fromTotalXp(Skill.DISCIPLINE, 100),
            courage = SkillProgress.fromTotalXp(Skill.COURAGE, 50),
            combinedLevel = 13,
            tokens = 0,
            freezeTokensFromPerks = 1
        )
        assertTrue(state.hasMastery)
        assertEquals(1, state.masteryCount)
    }

    @Test
    fun `PlayerSkillsState - isFullyMastered when all skills at 10`() {
        val state = PlayerSkillsState(
            clarity = SkillProgress.fromTotalXp(Skill.CLARITY, 2200),
            discipline = SkillProgress.fromTotalXp(Skill.DISCIPLINE, 2200),
            courage = SkillProgress.fromTotalXp(Skill.COURAGE, 2200),
            combinedLevel = 30,
            tokens = 0,
            freezeTokensFromPerks = 2
        )
        assertTrue(state.isFullyMastered)
        assertEquals(3, state.masteryCount)
    }

    // =========================================================================
    // TOKEN REWARDS TESTS
    // =========================================================================

    @Test
    fun `PointCalculator Tokens - streak milestones award correct tokens`() {
        assertEquals(10, PointCalculator.Tokens.getStreakMilestoneTokens(7))
        assertEquals(25, PointCalculator.Tokens.getStreakMilestoneTokens(30))
        assertEquals(50, PointCalculator.Tokens.getStreakMilestoneTokens(100))
        assertEquals(100, PointCalculator.Tokens.getStreakMilestoneTokens(365))
        assertNull(PointCalculator.Tokens.getStreakMilestoneTokens(5))
    }

    @Test
    fun `PointCalculator LevelUpRewards - calculated correctly`() {
        // Level 2: 10 + (2 * 5) = 20 tokens
        assertEquals(20, PointCalculator.LevelUpRewards.calculateTokenReward(2))

        // Level 5: 10 + (5 * 5) = 35 tokens
        assertEquals(35, PointCalculator.LevelUpRewards.calculateTokenReward(5))

        // Level 10 (mastery): 10 + (10 * 5) + 50 = 110 tokens
        assertEquals(110, PointCalculator.LevelUpRewards.calculateTokenReward(10))
    }

    // =========================================================================
    // STREAK MULTIPLIER TESTS
    // =========================================================================

    @Test
    fun `PointCalculator Streak - multiplier at 0 days is 1x`() {
        assertEquals(1.0f, PointCalculator.Streak.getMultiplier(0))
    }

    @Test
    fun `PointCalculator Streak - multiplier at 7 days is 1_1x`() {
        assertEquals(1.1f, PointCalculator.Streak.getMultiplier(7))
    }

    @Test
    fun `PointCalculator Streak - multiplier at 30 days is 1_25x`() {
        assertEquals(1.25f, PointCalculator.Streak.getMultiplier(30))
    }

    @Test
    fun `PointCalculator Streak - multiplier at 100 days is 1_5x`() {
        assertEquals(1.5f, PointCalculator.Streak.getMultiplier(100))
    }

    @Test
    fun `PointCalculator Streak - applyStreakBonus calculates correctly`() {
        // 100 points with 7-day streak (1.1x) = 110
        assertEquals(110, PointCalculator.Streak.applyStreakBonus(100, 7))

        // 100 points with 30-day streak (1.25x) = 125
        assertEquals(125, PointCalculator.Streak.applyStreakBonus(100, 30))

        // 100 points with 100-day streak (1.5x) = 150
        assertEquals(150, PointCalculator.Streak.applyStreakBonus(100, 100))
    }

    // =========================================================================
    // HELPER METHODS
    // =========================================================================

    private fun createTestSeed(
        type: SeedType = SeedType.WORD,
        content: String = "test",
        variations: List<String> = emptyList(),
        keywords: List<String> = emptyList(),
        keyPhrase: String? = null
    ): DailySeed {
        return DailySeed(
            id = 1,
            date = System.currentTimeMillis(),
            type = type,
            content = content,
            source = "test",
            sourceId = null,
            state = SeedState.PLANTED,
            bloomedAt = null,
            bloomedIn = null,
            variations = variations,
            keywords = keywords,
            keyPhrase = keyPhrase
        )
    }
}
