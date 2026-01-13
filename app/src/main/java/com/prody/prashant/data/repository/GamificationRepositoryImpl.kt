package com.prody.prashant.data.repository

import com.prody.prashant.data.local.dao.SeedDao
import com.prody.prashant.data.local.dao.UserDao
import com.prody.prashant.data.local.entity.*
import com.prody.prashant.domain.common.ErrorType
import com.prody.prashant.domain.common.Result
import com.prody.prashant.domain.common.runSuspendCatching
import com.prody.prashant.domain.gamification.*
import com.prody.prashant.domain.repository.*
import com.prody.prashant.domain.summary.WeeklySummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of GamificationRepository using Room database.
 *
 * Handles all gamification operations with idempotency via ProcessedRewardEntity.
 */
@Singleton
class GamificationRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val seedDao: SeedDao
) : GamificationRepository {

    // =========================================================================
    // Skill Progression
    // =========================================================================

    override fun observePlayerSkills(): Flow<PlayerSkillsState> {
        return userDao.observePlayerSkills().map { entity ->
            entity?.toPlayerSkillsState() ?: PlayerSkillsState.empty()
        }
    }

    override suspend fun getPlayerSkillsSync(): PlayerSkillsState {
        return userDao.getPlayerSkillsSync()?.toPlayerSkillsState() ?: PlayerSkillsState.empty()
    }

    override suspend fun awardSkillXp(
        skill: Skill,
        amount: Int,
        rewardKey: String,
        respectDailyCap: Boolean
    ): Result<SkillXpAwardResult> = runSuspendCatching(ErrorType.DATABASE, "Failed to award XP") {
        // Check idempotency
        if (userDao.hasProcessedRewardKey(rewardKey)) {
            val currentState = getPlayerSkillsSync()
            val progress = currentState.getProgress(skill)
            return@runSuspendCatching SkillXpAwardResult(
                skill = skill,
                requestedAmount = amount,
                actualAmountAwarded = 0,
                newTotalXp = progress.totalPointsEarned,
                newLevel = progress.level,
                previousLevel = progress.level,
                didLevelUp = false,
                remainingDailyCap = getRemainingDailyCapacity(skill),
                wasCapped = false
            )
        }

        // Get current state
        val skills = userDao.getPlayerSkillsSync() ?: createInitialSkills()
        val previousLevel = Skill.calculateLevel(skills.getXpForSkill(skill))

        // Calculate actual amount (respecting daily cap if needed)
        val dailyXp = skills.getDailyXpForSkill(skill)
        val dailyCap = PointCalculator.getDailyCap(skill)
        val remainingCap = (dailyCap - dailyXp).coerceAtLeast(0)

        val actualAmount = if (respectDailyCap) {
            amount.coerceAtMost(remainingCap)
        } else {
            amount
        }

        if (actualAmount <= 0) {
            return@runSuspendCatching SkillXpAwardResult(
                skill = skill,
                requestedAmount = amount,
                actualAmountAwarded = 0,
                newTotalXp = skills.getXpForSkill(skill),
                newLevel = previousLevel,
                previousLevel = previousLevel,
                didLevelUp = false,
                remainingDailyCap = remainingCap,
                wasCapped = true
            )
        }

        // Award XP
        when (skill) {
            Skill.CLARITY -> {
                userDao.addClarityXp(actualAmount)
                userDao.addDailyClarityXp(actualAmount)
            }
            Skill.DISCIPLINE -> {
                userDao.addDisciplineXp(actualAmount)
                userDao.addDailyDisciplineXp(actualAmount)
            }
            Skill.COURAGE -> {
                userDao.addCourageXp(actualAmount)
                userDao.addDailyCourageXp(actualAmount)
            }
        }

        // Mark reward as processed
        userDao.markRewardKeyProcessed(rewardKey)

        // Get new state
        val newSkills = userDao.getPlayerSkillsSync() ?: createInitialSkills()
        val newTotalXp = newSkills.getXpForSkill(skill)
        val newLevel = Skill.calculateLevel(newTotalXp)

        SkillXpAwardResult(
            skill = skill,
            requestedAmount = amount,
            actualAmountAwarded = actualAmount,
            newTotalXp = newTotalXp,
            newLevel = newLevel,
            previousLevel = previousLevel,
            didLevelUp = newLevel > previousLevel,
            remainingDailyCap = (dailyCap - newSkills.getDailyXpForSkill(skill)).coerceAtLeast(0),
            wasCapped = actualAmount < amount
        )
    }

    override suspend fun getDailyXpForSkill(skill: Skill): Int {
        val skills = userDao.getPlayerSkillsSync() ?: return 0
        return skills.getDailyXpForSkill(skill)
    }

    override suspend fun getRemainingDailyCapacity(skill: Skill): Int {
        val dailyXp = getDailyXpForSkill(skill)
        val cap = PointCalculator.getDailyCap(skill)
        return (cap - dailyXp).coerceAtLeast(0)
    }

    override suspend fun getWeeklyXp(): WeeklyXpProgress {
        val skills = userDao.getPlayerSkillsSync() ?: return WeeklyXpProgress.empty()
        return WeeklyXpProgress(
            clarityXp = skills.weeklyClarityXp,
            disciplineXp = skills.weeklyDisciplineXp,
            courageXp = skills.weeklyCourageXp,
            weekStartDate = LeaderboardScoring.getWeekStartDate()
        )
    }

    override suspend fun resetDailyXp() {
        userDao.resetDailySkillXp()
    }

    override suspend fun resetWeeklyXp() {
        val skills = userDao.getPlayerSkillsSync() ?: return
        userDao.updatePlayerSkills(
            skills.copy(
                weeklyClarityXp = 0,
                weeklyDisciplineXp = 0,
                weeklyCourageXp = 0,
                weeklyResetDate = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    // =========================================================================
    // Streak System
    // =========================================================================

    override fun observeStreakState(): Flow<StreakData> {
        return userDao.observePlayerSkills().map { entity ->
            // For now, derive from user profile until we have dedicated streak entity
            val profile = userDao.getUserProfileSync()
            StreakData(
                currentStreak = profile?.currentStreak ?: 0,
                longestStreak = profile?.longestStreak ?: 0,
                lastActiveDate = LocalDate.now(), // Would come from streak_data table
                gracePeriodAvailable = true,
                lastGracePeriodUsed = null,
                freezeTokensEarned = 0,
                freezeTokensUsed = 0,
                freezesAvailable = StreakData.MAX_FREEZES_PER_MONTH,
                freezesUsedThisMonth = 0,
                lastFreezeResetMonth = LocalDate.now().monthValue,
                totalDaysActive = profile?.currentStreak ?: 0,
                totalStreaksStarted = if ((profile?.currentStreak ?: 0) > 0) 1 else 0,
                longestStreakDate = if ((profile?.longestStreak ?: 0) > 0) LocalDate.now() else null
            )
        }
    }

    override suspend fun getStreakStateSync(): StreakData {
        val profile = userDao.getUserProfileSync()
        return StreakData(
            currentStreak = profile?.currentStreak ?: 0,
            longestStreak = profile?.longestStreak ?: 0,
            lastActiveDate = LocalDate.now(),
            gracePeriodAvailable = true,
            lastGracePeriodUsed = null,
            freezeTokensEarned = 0,
            freezeTokensUsed = 0,
            freezesAvailable = StreakData.MAX_FREEZES_PER_MONTH,
            freezesUsedThisMonth = 0,
            lastFreezeResetMonth = LocalDate.now().monthValue,
            totalDaysActive = profile?.currentStreak ?: 0,
            totalStreaksStarted = if ((profile?.currentStreak ?: 0) > 0) 1 else 0,
            longestStreakDate = if ((profile?.longestStreak ?: 0) > 0) LocalDate.now() else null
        )
    }

    override suspend fun recordDailyActivity(activityType: ActivityType): Result<StreakUpdateResult> =
        runSuspendCatching(ErrorType.DATABASE, "Failed to record activity") {
            val profile = userDao.getUserProfileSync() ?: throw IllegalStateException("No user profile")
            val today = LocalDate.now()
            val lastActive = LocalDate.ofEpochDay(profile.lastActiveDate / (24 * 60 * 60 * 1000))

            val status = StreakCalculator.calculateStreakStatus(lastActive, today)

            when (status) {
                StreakStatus.SAME_DAY -> {
                    val currentTier = StreakTier.forStreak(profile.currentStreak)
                    StreakUpdateResult.Maintained(
                        streak = profile.currentStreak,
                        tier = currentTier
                    )
                }
                StreakStatus.CONSECUTIVE -> {
                    val newStreak = profile.currentStreak + 1
                    val isNewLongest = newStreak > profile.longestStreak
                    userDao.updateStreak(newStreak)
                    userDao.updateLastActiveDate(System.currentTimeMillis())
                    val milestone = StreakCalculator.getMilestoneReached(profile.currentStreak, newStreak)
                    val previousTier = StreakTier.forStreak(profile.currentStreak)
                    val newTier = StreakTier.forStreak(newStreak)
                    StreakUpdateResult.Incremented(
                        newStreak = newStreak,
                        previousStreak = profile.currentStreak,
                        isNewLongest = isNewLongest,
                        milestoneReached = milestone,
                        newTier = newTier,
                        previousTier = previousTier,
                        tierChanged = newTier != previousTier
                    )
                }
                StreakStatus.CAN_FREEZE -> {
                    // Streak broken but can use freeze
                    StreakUpdateResult.Broken(
                        previousStreak = profile.currentStreak,
                        newStreak = 1,
                        daysMissed = 1,
                        canUseGracePeriod = false,
                        canUseFreezeToken = true,
                        freezeTokensAvailable = 1
                    )
                }
                StreakStatus.BROKEN -> {
                    // Streak broken, too late for freeze
                    userDao.updateStreak(1)
                    userDao.updateLastActiveDate(System.currentTimeMillis())
                    StreakUpdateResult.Broken(
                        previousStreak = profile.currentStreak,
                        newStreak = 1,
                        daysMissed = 2,
                        canUseGracePeriod = false,
                        canUseFreezeToken = false,
                        freezeTokensAvailable = 0
                    )
                }
            }
        }

    override suspend fun useMindfulBreak(): Result<MindfulBreakResult> =
        runSuspendCatching(ErrorType.DATABASE, "Failed to use Mindful Break") {
            // Simplified implementation - would need dedicated streak_data table
            MindfulBreakResult.NoFreezesAvailable
        }

    override suspend fun getActivityHistory(startDate: LocalDate, endDate: LocalDate): List<DailyActivity> {
        // Would query daily_activity table
        return emptyList()
    }

    override suspend fun resetMonthlyFreezes() {
        // Would update streak_data table
    }

    // =========================================================================
    // Achievements
    // =========================================================================

    override fun observeAllAchievements(): Flow<List<AchievementProgress>> {
        return userDao.getAllAchievements().map { entities ->
            entities.map { entity ->
                val achievement = Achievements.findById(entity.id)
                AchievementProgress(
                    achievement = achievement ?: createUnknownAchievement(entity),
                    currentProgress = entity.currentProgress,
                    isUnlocked = entity.isUnlocked,
                    unlockedAt = entity.unlockedAt,
                    progressPercent = if (entity.requirement > 0) {
                        entity.currentProgress.toFloat() / entity.requirement
                    } else 0f
                )
            }
        }
    }

    override fun observeUnlockedAchievements(): Flow<List<AchievementProgress>> {
        return userDao.getUnlockedAchievements().map { entities ->
            entities.map { entity ->
                val achievement = Achievements.findById(entity.id)
                AchievementProgress(
                    achievement = achievement ?: createUnknownAchievement(entity),
                    currentProgress = entity.currentProgress,
                    isUnlocked = true,
                    unlockedAt = entity.unlockedAt,
                    progressPercent = 1f
                )
            }
        }
    }

    override suspend fun getUnlockedAchievementIds(): Set<String> {
        return userDao.getUnlockedAchievements().first().map { it.id }.toSet()
    }

    override suspend fun updateAchievementProgress(achievementId: String, progress: Int): Result<Boolean> =
        runSuspendCatching(ErrorType.DATABASE, "Failed to update achievement") {
            val entity = userDao.getAchievementById(achievementId) ?: return@runSuspendCatching false
            if (entity.isUnlocked) return@runSuspendCatching false

            userDao.updateAchievementProgress(achievementId, progress)

            // Check if achievement should unlock
            if (progress >= entity.requirement) {
                userDao.unlockAchievement(achievementId)
                true
            } else {
                false
            }
        }

    override suspend fun checkAndUpdateAchievements(): List<Achievement> {
        // Would check all achievement requirements against current stats
        return emptyList()
    }

    // =========================================================================
    // Seed -> Bloom
    // =========================================================================

    override fun observeTodaysSeed(): Flow<DailySeed?> {
        val todayStart = LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        return seedDao.observeSeedByDate(todayStart).map { entity ->
            entity?.toDailySeed()
        }
    }

    override suspend fun getTodaysSeedSync(): DailySeed? {
        val todayStart = LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        return seedDao.getSeedByDate(todayStart)?.toDailySeed()
    }

    override suspend fun getOrCreateTodaysSeed(): Result<DailySeed> =
        runSuspendCatching(ErrorType.DATABASE, "Failed to get/create seed") {
            val todayStart = LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

            val existing = seedDao.getSeedByDate(todayStart)
            if (existing != null) {
                return@runSuspendCatching existing.toDailySeed()
            }

            // Create new seed - would select from vocabulary/quotes/proverbs
            val newSeed = SeedEntity(
                date = todayStart,
                seedType = "word",
                seedContent = "wisdom", // Would be randomly selected
                seedSource = "vocabulary",
                state = "planted"
            )
            val id = seedDao.insertSeed(newSeed)
            seedDao.getSeedById(id)?.toDailySeed()
                ?: throw IllegalStateException("Failed to create seed")
        }

    override suspend fun updateSeedState(seedId: Long, newState: SeedState): Result<Unit> =
        runSuspendCatching(ErrorType.DATABASE, "Failed to update seed state") {
            seedDao.updateSeedState(seedId, newState.name.lowercase())
        }

    override suspend fun attemptBloom(
        content: String,
        bloomedIn: String,
        entryId: Long
    ): Result<BloomAttemptResult> = runSuspendCatching(ErrorType.DATABASE, "Failed to attempt bloom") {
        val seed = getTodaysSeedSync() ?: return@runSuspendCatching BloomAttemptResult.NoSeedAvailable

        if (seed.hasBloomed) {
            return@runSuspendCatching BloomAttemptResult.AlreadyBloomed
        }

        // Check if content matches seed
        val todayStart = LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        val seedEntity = seedDao.getSeedByDate(todayStart)
            ?: return@runSuspendCatching BloomAttemptResult.NoSeedAvailable

        if (!seedEntity.matchesContent(content)) {
            return@runSuspendCatching BloomAttemptResult.NoMatch
        }

        // Bloom the seed!
        seedDao.bloomSeed(
            seedId = seedEntity.id,
            bloomedAt = System.currentTimeMillis(),
            bloomedIn = bloomedIn,
            bloomedEntryId = entryId
        )

        BloomAttemptResult.Success(
            seedContent = seed.content,
            pointsAwarded = PointCalculator.Discipline.BLOOM_SEED,
            tokensAwarded = PointCalculator.Tokens.BLOOM_SEED,
            newBloomStreak = 1, // Would calculate actual bloom streak
            isStreakMilestone = false
        )
    }

    override suspend fun getBloomSummary(): BloomSummary {
        val seeds = seedDao.getAllSeedsSync()
        val bloomedSeeds = seeds.filter { it.hasBloomedToday }
        return BloomSummary(
            totalSeeds = seeds.size,
            totalBloomed = bloomedSeeds.size,
            currentStreak = 0, // Would calculate
            longestStreak = 0, // Would calculate
            lastBloomDate = bloomedSeeds.maxByOrNull { it.bloomedAt ?: 0 }?.bloomedAt,
            bloomRate = if (seeds.isNotEmpty()) bloomedSeeds.size.toFloat() / seeds.size else 0f,
            thisMonthBlooms = 0 // Would calculate
        )
    }

    // =========================================================================
    // Rank System
    // =========================================================================

    override fun observeRankState(): Flow<RankState> {
        return observePlayerSkills().map { skills ->
            RankState.fromSkillLevels(
                clarityLevel = skills.clarity.level,
                disciplineLevel = skills.discipline.level,
                courageLevel = skills.courage.level
            )
        }
    }

    override suspend fun getCurrentRank(): RankState {
        val skills = getPlayerSkillsSync()
        return RankState.fromSkillLevels(
            clarityLevel = skills.clarity.level,
            disciplineLevel = skills.discipline.level,
            courageLevel = skills.courage.level
        )
    }

    override suspend fun checkForRankUp(previousState: RankState): RankUpdateResult {
        val currentState = getCurrentRank()
        return if (currentState.currentRank != previousState.currentRank) {
            RankUpdateResult.RankUp(
                previousRank = previousState.currentRank,
                newRank = currentState.currentRank,
                message = RankMessages.advancementMessage(currentState.currentRank)
            )
        } else {
            RankUpdateResult.NoChange(currentState.currentRank)
        }
    }

    // =========================================================================
    // Leaderboard
    // =========================================================================

    override fun observeWeeklyLeaderboard(): Flow<LeaderboardState> {
        return userDao.getWeeklyLeaderboard().map { entries ->
            LeaderboardState(
                entries = entries.map { it.toLeaderboardEntry() },
                currentUserEntry = entries.find { it.isCurrentUser }?.toLeaderboardEntry(),
                currentUserRank = entries.indexOfFirst { it.isCurrentUser }.let { if (it >= 0) it + 1 else null },
                weekStartDate = LeaderboardScoring.getWeekStartDate(),
                weekEndDate = LeaderboardScoring.getWeekEndDate(),
                daysRemaining = LeaderboardScoring.daysRemainingInWeek(),
                totalParticipants = entries.size,
                lastUpdatedAt = System.currentTimeMillis()
            )
        }
    }

    override suspend fun getCurrentUserLeaderboardEntry(): LeaderboardEntry? {
        return userDao.getCurrentUserRank().first()?.toLeaderboardEntry()
    }

    override suspend fun updateLeaderboardScore(): Result<Unit> =
        runSuspendCatching(ErrorType.DATABASE, "Failed to update leaderboard") {
            // Would calculate weekly score and update leaderboard entry
        }

    override suspend fun getWeeklySummary(weekStartDate: LocalDate): WeeklySummary? {
        // Would retrieve summary for completed week
        return null
    }

    // =========================================================================
    // Tokens
    // =========================================================================

    override fun observeTokenBalance(): Flow<Int> {
        return userDao.observePlayerSkills().map { it?.tokens ?: 0 }
    }

    override suspend fun getTokenBalance(): Int {
        return userDao.getPlayerSkillsSync()?.tokens ?: 0
    }

    override suspend fun awardTokens(amount: Int, rewardKey: String): Result<Int> =
        runSuspendCatching(ErrorType.DATABASE, "Failed to award tokens") {
            if (userDao.hasProcessedRewardKey(rewardKey)) {
                return@runSuspendCatching getTokenBalance()
            }

            val skills = userDao.getPlayerSkillsSync() ?: createInitialSkills()
            val newBalance = skills.tokens + amount
            userDao.updatePlayerSkills(skills.copy(tokens = newBalance, updatedAt = System.currentTimeMillis()))
            userDao.markRewardKeyProcessed(rewardKey)
            newBalance
        }

    override suspend fun spendTokens(amount: Int, reason: String): Result<Int> =
        runSuspendCatching(ErrorType.DATABASE, "Failed to spend tokens") {
            val skills = userDao.getPlayerSkillsSync() ?: throw IllegalStateException("No player skills")
            if (skills.tokens < amount) {
                throw IllegalArgumentException("Insufficient tokens")
            }
            val newBalance = skills.tokens - amount
            userDao.updatePlayerSkills(skills.copy(tokens = newBalance, updatedAt = System.currentTimeMillis()))
            newBalance
        }

    // =========================================================================
    // Utility
    // =========================================================================

    override suspend fun hasProcessedReward(rewardKey: String): Boolean {
        return userDao.hasProcessedRewardKey(rewardKey)
    }

    override suspend fun markRewardProcessed(rewardKey: String) {
        userDao.markRewardKeyProcessed(rewardKey)
    }

    override suspend fun initializeForNewUser() {
        if (userDao.getPlayerSkillsSync() == null) {
            createInitialSkills()
        }
    }

    override suspend fun performDailyResetChecks() {
        val skills = userDao.getPlayerSkillsSync() ?: return
        val today = LocalDate.now()
        val lastResetDate = LocalDate.ofEpochDay(skills.dailyResetDate / (24 * 60 * 60 * 1000))

        // Check daily reset
        if (today != lastResetDate) {
            resetDailyXp()
        }

        // Check weekly reset (Monday)
        val lastWeeklyReset = LocalDate.ofEpochDay(skills.weeklyResetDate / (24 * 60 * 60 * 1000))
        val thisWeekMonday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        if (lastWeeklyReset.isBefore(thisWeekMonday)) {
            resetWeeklyXp()
        }
    }

    // =========================================================================
    // Private Helpers
    // =========================================================================

    private suspend fun createInitialSkills(): PlayerSkillsEntity {
        val initial = PlayerSkillsEntity(
            id = 1,
            userId = "local",
            dailyResetDate = System.currentTimeMillis(),
            weeklyResetDate = System.currentTimeMillis(),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        userDao.insertPlayerSkills(initial)
        return initial
    }

    private fun PlayerSkillsEntity.toPlayerSkillsState(): PlayerSkillsState {
        return PlayerSkillsState(
            clarity = SkillProgress.fromTotalXp(Skill.CLARITY, clarityXp),
            discipline = SkillProgress.fromTotalXp(Skill.DISCIPLINE, disciplineXp),
            courage = SkillProgress.fromTotalXp(Skill.COURAGE, courageXp),
            combinedLevel = Skill.calculateLevel(clarityXp) +
                    Skill.calculateLevel(disciplineXp) +
                    Skill.calculateLevel(courageXp),
            tokens = tokens,
            freezeTokensFromPerks = perkFreezeTokens - perkFreezeTokensUsed
        )
    }

    private fun PlayerSkillsEntity.getXpForSkill(skill: Skill): Int = when (skill) {
        Skill.CLARITY -> clarityXp
        Skill.DISCIPLINE -> disciplineXp
        Skill.COURAGE -> courageXp
    }

    private fun PlayerSkillsEntity.getDailyXpForSkill(skill: Skill): Int = when (skill) {
        Skill.CLARITY -> dailyClarityXp
        Skill.DISCIPLINE -> dailyDisciplineXp
        Skill.COURAGE -> dailyCourageXp
    }

    private fun SeedEntity.toDailySeed(): DailySeed {
        return DailySeed(
            id = id,
            date = date,
            type = SeedType.entries.find { it.name.equals(seedType, ignoreCase = true) } ?: SeedType.WORD,
            content = seedContent,
            source = seedSource,
            sourceId = sourceId,
            state = SeedState.entries.find { it.name.equals(state, ignoreCase = true) } ?: SeedState.PLANTED,
            bloomedAt = bloomedAt,
            bloomedIn = bloomedIn,
            variations = parseJsonArray(variations),
            keywords = parseJsonArray(keywords),
            keyPhrase = keyPhrase
        )
    }

    private fun parseJsonArray(json: String): List<String> {
        if (json.isBlank()) return emptyList()
        return try {
            json.removeSurrounding("[", "]")
                .split(",")
                .map { it.trim().removeSurrounding("\"") }
                .filter { it.isNotBlank() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun LeaderboardEntryEntity.toLeaderboardEntry(): LeaderboardEntry {
        return LeaderboardEntry(
            odId = odId,
            displayName = displayName,
            avatarId = avatarId,
            titleId = titleId,
            bannerId = bannerId,
            frameId = frameId,
            weeklyScore = weeklyPoints,
            totalScore = totalPoints,
            rank = rank,
            previousRank = previousRank,
            currentStreak = currentStreak,
            clarityLevel = 1, // Would need to store/calculate
            disciplineLevel = 1,
            courageLevel = 1,
            isCurrentUser = isCurrentUser,
            isDevBadgeHolder = isDevBadgeHolder,
            isBetaTester = isBetaTester,
            isFounder = isFounder,
            lastActiveAt = lastActiveAt
        )
    }

    private fun createUnknownAchievement(entity: AchievementEntity): Achievement {
        return Achievement(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            category = AchievementCategory.entries.find { it.name.equals(entity.category, ignoreCase = true) }
                ?: AchievementCategory.JOURNEY,
            rarity = AchievementRarity.entries.find { it.name.equals(entity.rarity, ignoreCase = true) }
                ?: AchievementRarity.COMMON,
            iconName = entity.iconId,
            requirement = AchievementRequirement.Count(entity.requirement),
            celebrationMessage = entity.celebrationMessage,
            xpReward = entity.xpReward
        )
    }
}
