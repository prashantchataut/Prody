package com.prody.prashant.domain.model

/**
 * Difficulty levels for challenges.
 *
 * Note: UI properties (icon, color) are provided separately via ChallengeUi extensions
 * in the ui.theme package to avoid Compose dependencies in the domain layer.
 * This prevents class initialization crashes when Challenge is accessed from
 * background threads or before Compose is initialized.
 */
enum class ChallengeDifficulty(
    val displayName: String,
    val pointMultiplier: Float
) {
    EASY("Easy", 1.0f),
    MEDIUM("Medium", 1.5f),
    HARD("Hard", 2.0f),
    EXTREME("Extreme", 3.0f)
}

/**
 * Types of challenges available in the app.
 *
 * Note: UI properties (icon, color) are provided separately via ChallengeUi extensions
 * in the ui.theme package to avoid Compose dependencies in the domain layer.
 */
enum class ChallengeType(
    val displayName: String,
    val description: String
) {
    JOURNALING(
        "Journaling",
        "Write journal entries consistently"
    ),
    VOCABULARY(
        "Vocabulary",
        "Learn new words and expand your vocabulary"
    ),
    STREAK(
        "Streak",
        "Maintain daily activity streaks"
    ),
    MEDITATION(
        "Meditation",
        "Practice mindfulness and meditation"
    ),
    REFLECTION(
        "Reflection",
        "Engage in deep reflection activities"
    ),
    MIXED(
        "Mixed",
        "Complete various activities"
    )
}

/**
 * Status of a challenge from the user's perspective.
 */
enum class ChallengeStatus {
    UPCOMING,    // Challenge hasn't started yet
    ACTIVE,      // Challenge is in progress
    JOINED,      // User has joined and is participating
    COMPLETED,   // User successfully completed the challenge
    ENDED,       // Challenge ended (user may or may not have completed)
    MISSED       // User joined but didn't complete before end
}

/**
 * Domain model for a community challenge.
 */
data class Challenge(
    val id: String,
    val title: String,
    val description: String,
    val type: ChallengeType,
    val difficulty: ChallengeDifficulty,
    val startDate: Long,
    val endDate: Long,
    val targetCount: Int,
    val currentUserProgress: Int = 0,
    val isJoined: Boolean = false,
    val joinedAt: Long? = null,
    val totalParticipants: Int = 0,
    val communityProgress: Int = 0,
    val communityTarget: Int = 0,
    val rewardPoints: Int = 0,
    val rewardBadgeId: String? = null,
    val rewardTitle: String? = null,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val isFeatured: Boolean = false,
    val milestones: List<ChallengeMilestone> = emptyList()
) {
    val progressPercentage: Float
        get() = if (targetCount > 0) (currentUserProgress.toFloat() / targetCount).coerceIn(0f, 1f) else 0f

    val communityProgressPercentage: Float
        get() = if (communityTarget > 0) (communityProgress.toFloat() / communityTarget).coerceIn(0f, 1f) else 0f

    val daysRemaining: Int
        get() {
            val now = System.currentTimeMillis()
            return if (endDate > now) {
                ((endDate - now) / (24 * 60 * 60 * 1000)).toInt()
            } else {
                0
            }
        }

    val totalDays: Int
        get() = ((endDate - startDate) / (24 * 60 * 60 * 1000)).toInt()

    val status: ChallengeStatus
        get() {
            val now = System.currentTimeMillis()
            return when {
                isCompleted -> ChallengeStatus.COMPLETED
                now < startDate -> ChallengeStatus.UPCOMING
                now > endDate && isJoined && !isCompleted -> ChallengeStatus.MISSED
                now > endDate -> ChallengeStatus.ENDED
                isJoined -> ChallengeStatus.JOINED
                else -> ChallengeStatus.ACTIVE
            }
        }

    val isActive: Boolean
        get() {
            val now = System.currentTimeMillis()
            return now in startDate..endDate
        }
}

/**
 * Domain model for challenge milestones (community achievements).
 */
data class ChallengeMilestone(
    val id: String,
    val challengeId: String,
    val title: String,
    val description: String,
    val targetProgress: Int,
    val isPercentage: Boolean = true,
    val isReached: Boolean = false,
    val reachedAt: Long? = null,
    val rewardPoints: Int = 0,
    val celebrationMessage: String = ""
) {
    val progressThreshold: Int
        get() = if (isPercentage) targetProgress else targetProgress
}

/**
 * Object containing default/sample challenges for the app.
 */
object DefaultChallenges {

    private val currentTime = System.currentTimeMillis()
    private val dayInMillis = 24 * 60 * 60 * 1000L

    val monthlyChallenges = listOf(
        Challenge(
            id = "30_days_journaling",
            title = "30 Days of Journaling",
            description = "Write a journal entry every day for 30 days. Document your thoughts, feelings, and growth journey.",
            type = ChallengeType.JOURNALING,
            difficulty = ChallengeDifficulty.MEDIUM,
            startDate = getStartOfMonth(),
            endDate = getEndOfMonth(),
            targetCount = 30,
            communityTarget = 10000,
            rewardPoints = 500,
            rewardBadgeId = "journal_master",
            rewardTitle = "Journal Master",
            isFeatured = true,
            milestones = listOf(
                ChallengeMilestone(
                    id = "30dj_m1",
                    challengeId = "30_days_journaling",
                    title = "Community Kickoff",
                    description = "1,000 total entries submitted",
                    targetProgress = 10,
                    isPercentage = true,
                    rewardPoints = 25,
                    celebrationMessage = "The community has started strong! 1,000 entries and counting!"
                ),
                ChallengeMilestone(
                    id = "30dj_m2",
                    challengeId = "30_days_journaling",
                    title = "Halfway Heroes",
                    description = "5,000 total entries submitted",
                    targetProgress = 50,
                    isPercentage = true,
                    rewardPoints = 50,
                    celebrationMessage = "Incredible progress! The community is halfway there!"
                ),
                ChallengeMilestone(
                    id = "30dj_m3",
                    challengeId = "30_days_journaling",
                    title = "Community Champions",
                    description = "10,000 total entries reached",
                    targetProgress = 100,
                    isPercentage = true,
                    rewardPoints = 100,
                    celebrationMessage = "LEGENDARY! The community reached 10,000 entries together!"
                )
            )
        ),
        Challenge(
            id = "vocabulary_century",
            title = "Vocabulary Century",
            description = "Learn 100 new words this month. Expand your vocabulary one word at a time.",
            type = ChallengeType.VOCABULARY,
            difficulty = ChallengeDifficulty.HARD,
            startDate = getStartOfMonth(),
            endDate = getEndOfMonth(),
            targetCount = 100,
            communityTarget = 50000,
            rewardPoints = 750,
            rewardBadgeId = "word_champion",
            rewardTitle = "Word Champion",
            milestones = listOf(
                ChallengeMilestone(
                    id = "vc_m1",
                    challengeId = "vocabulary_century",
                    title = "Words Unlocked",
                    description = "10,000 words learned community-wide",
                    targetProgress = 20,
                    isPercentage = true,
                    rewardPoints = 30,
                    celebrationMessage = "10,000 words learned! Knowledge is spreading!"
                ),
                ChallengeMilestone(
                    id = "vc_m2",
                    challengeId = "vocabulary_century",
                    title = "Lexicon Legion",
                    description = "25,000 words learned community-wide",
                    targetProgress = 50,
                    isPercentage = true,
                    rewardPoints = 60,
                    celebrationMessage = "25,000 words! The community speaks volumes!"
                ),
                ChallengeMilestone(
                    id = "vc_m3",
                    challengeId = "vocabulary_century",
                    title = "Dictionary Dominators",
                    description = "50,000 words mastered together",
                    targetProgress = 100,
                    isPercentage = true,
                    rewardPoints = 150,
                    celebrationMessage = "EXTRAORDINARY! 50,000 words learned by our community!"
                )
            )
        ),
        Challenge(
            id = "mindful_march",
            title = "Mindful Month",
            description = "Complete 20 meditation sessions this month. Cultivate inner peace and focus.",
            type = ChallengeType.MEDITATION,
            difficulty = ChallengeDifficulty.MEDIUM,
            startDate = getStartOfMonth(),
            endDate = getEndOfMonth(),
            targetCount = 20,
            communityTarget = 5000,
            rewardPoints = 400,
            rewardBadgeId = "zen_master",
            milestones = listOf(
                ChallengeMilestone(
                    id = "mm_m1",
                    challengeId = "mindful_march",
                    title = "Collective Calm",
                    description = "1,000 meditation sessions completed",
                    targetProgress = 20,
                    isPercentage = true,
                    rewardPoints = 20,
                    celebrationMessage = "1,000 moments of peace created together!"
                ),
                ChallengeMilestone(
                    id = "mm_m2",
                    challengeId = "mindful_march",
                    title = "Serenity Summit",
                    description = "5,000 meditation sessions achieved",
                    targetProgress = 100,
                    isPercentage = true,
                    rewardPoints = 75,
                    celebrationMessage = "5,000 sessions! The community radiates tranquility!"
                )
            )
        )
    )

    val weeklyChallenges = listOf(
        Challenge(
            id = "week_reflector",
            title = "Weekly Reflector",
            description = "Write 7 journal entries this week - one each day.",
            type = ChallengeType.JOURNALING,
            difficulty = ChallengeDifficulty.EASY,
            startDate = getStartOfWeek(),
            endDate = getEndOfWeek(),
            targetCount = 7,
            communityTarget = 2000,
            rewardPoints = 150,
            milestones = listOf(
                ChallengeMilestone(
                    id = "wr_m1",
                    challengeId = "week_reflector",
                    title = "Week Warriors",
                    description = "2,000 entries this week",
                    targetProgress = 100,
                    isPercentage = true,
                    rewardPoints = 25,
                    celebrationMessage = "Amazing week! 2,000 reflections shared!"
                )
            )
        ),
        Challenge(
            id = "word_sprint",
            title = "Word Sprint",
            description = "Learn 25 new vocabulary words in one week.",
            type = ChallengeType.VOCABULARY,
            difficulty = ChallengeDifficulty.MEDIUM,
            startDate = getStartOfWeek(),
            endDate = getEndOfWeek(),
            targetCount = 25,
            communityTarget = 5000,
            rewardPoints = 200
        ),
        Challenge(
            id = "streak_keeper",
            title = "Streak Keeper",
            description = "Maintain your streak for the entire week (7 days).",
            type = ChallengeType.STREAK,
            difficulty = ChallengeDifficulty.EASY,
            startDate = getStartOfWeek(),
            endDate = getEndOfWeek(),
            targetCount = 7,
            communityTarget = 1000,
            rewardPoints = 100
        )
    )

    val specialChallenges = listOf(
        Challenge(
            id = "new_year_new_me",
            title = "New Year, New Growth",
            description = "Complete 50 activities in the first month of the year. Start your transformation journey.",
            type = ChallengeType.MIXED,
            difficulty = ChallengeDifficulty.EXTREME,
            startDate = getStartOfYear(),
            endDate = getStartOfYear() + 31 * dayInMillis,
            targetCount = 50,
            communityTarget = 100000,
            rewardPoints = 1000,
            rewardBadgeId = "new_year_champion",
            rewardTitle = "New Year Champion",
            milestones = listOf(
                ChallengeMilestone(
                    id = "nynm_m1",
                    challengeId = "new_year_new_me",
                    title = "Fresh Start",
                    description = "25,000 activities completed",
                    targetProgress = 25,
                    isPercentage = true,
                    rewardPoints = 50,
                    celebrationMessage = "25,000 activities! The community starts strong!"
                ),
                ChallengeMilestone(
                    id = "nynm_m2",
                    challengeId = "new_year_new_me",
                    title = "Momentum Building",
                    description = "50,000 activities completed",
                    targetProgress = 50,
                    isPercentage = true,
                    rewardPoints = 100,
                    celebrationMessage = "Halfway there! 50,000 activities and growing!"
                ),
                ChallengeMilestone(
                    id = "nynm_m3",
                    challengeId = "new_year_new_me",
                    title = "Transformation Titans",
                    description = "100,000 activities achieved",
                    targetProgress = 100,
                    isPercentage = true,
                    rewardPoints = 250,
                    celebrationMessage = "PHENOMENAL! 100,000 activities completed together!"
                )
            )
        )
    )

    // Helper functions for date calculations
    private fun getStartOfMonth(): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.DAY_OF_MONTH, 1)
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getEndOfMonth(): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.DAY_OF_MONTH, calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH))
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 23)
        calendar.set(java.util.Calendar.MINUTE, 59)
        calendar.set(java.util.Calendar.SECOND, 59)
        calendar.set(java.util.Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }

    private fun getStartOfWeek(): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun getEndOfWeek(): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        calendar.add(java.util.Calendar.DAY_OF_WEEK, 6)
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 23)
        calendar.set(java.util.Calendar.MINUTE, 59)
        calendar.set(java.util.Calendar.SECOND, 59)
        calendar.set(java.util.Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }

    private fun getStartOfYear(): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.MONTH, java.util.Calendar.JANUARY)
        calendar.set(java.util.Calendar.DAY_OF_MONTH, 1)
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}
