package com.prody.prashant.ui.screens.challenges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.dao.ChallengeDao
import com.prody.prashant.data.local.dao.UserDao
import com.prody.prashant.data.local.entity.ChallengeEntity
import com.prody.prashant.data.local.entity.ChallengeLeaderboardEntity
import com.prody.prashant.data.local.entity.ChallengeMilestoneEntity
import com.prody.prashant.domain.model.Challenge
import com.prody.prashant.domain.model.ChallengeDifficulty
import com.prody.prashant.domain.model.ChallengeMilestone
import com.prody.prashant.domain.model.ChallengeType
import com.prody.prashant.domain.model.DefaultChallenges
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

data class ChallengesUiState(
    val isLoading: Boolean = true,
    val featuredChallenge: Challenge? = null,
    val activeChallenges: List<Challenge> = emptyList(),
    val joinedChallenges: List<Challenge> = emptyList(),
    val completedChallenges: List<Challenge> = emptyList(),
    val selectedChallenge: Challenge? = null,
    val challengeLeaderboard: List<ChallengeLeaderboardEntry> = emptyList(),
    val totalChallengesJoined: Int = 0,
    val totalChallengesCompleted: Int = 0,
    val totalPointsFromChallenges: Int = 0,
    val showCelebration: Boolean = false,
    val celebrationMessage: String = "",
    val error: String? = null
)

data class ChallengeLeaderboardEntry(
    val odId: String,
    val displayName: String,
    val avatarId: String,
    val progress: Int,
    val rank: Int,
    val isCurrentUser: Boolean
)

@HiltViewModel
class ChallengesViewModel @Inject constructor(
    private val challengeDao: ChallengeDao,
    private val userDao: UserDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChallengesUiState())
    val uiState: StateFlow<ChallengesUiState> = _uiState.asStateFlow()

    init {
        loadChallenges()
        observeChallenges()
    }

    private fun loadChallenges() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Initialize default challenges if needed
            initializeDefaultChallenges()

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun observeChallenges() {
        viewModelScope.launch {
            // Observe featured challenge
            challengeDao.getFeaturedChallenge().collect { entity ->
                val featured = entity?.let { mapEntityToChallenge(it) }
                _uiState.update { it.copy(featuredChallenge = featured) }
            }
        }

        viewModelScope.launch {
            // Observe active challenges
            challengeDao.getActiveChallenges().collect { entities ->
                val challenges = entities.map { mapEntityToChallenge(it) }
                _uiState.update { it.copy(activeChallenges = challenges) }
            }
        }

        viewModelScope.launch {
            // Observe joined challenges
            challengeDao.getJoinedChallenges().collect { entities ->
                val challenges = entities.map { mapEntityToChallenge(it) }
                _uiState.update { it.copy(joinedChallenges = challenges) }
            }
        }

        viewModelScope.launch {
            // Observe completed challenges
            challengeDao.getCompletedChallenges().collect { entities ->
                val challenges = entities.map { mapEntityToChallenge(it) }
                _uiState.update { it.copy(completedChallenges = challenges) }
            }
        }

        viewModelScope.launch {
            // Observe statistics
            combine(
                challengeDao.getTotalChallengesJoined(),
                challengeDao.getTotalChallengesCompleted(),
                challengeDao.getTotalPointsFromChallenges()
            ) { joined, completed, points ->
                Triple(joined, completed, points ?: 0)
            }.collect { (joined, completed, points) ->
                _uiState.update {
                    it.copy(
                        totalChallengesJoined = joined,
                        totalChallengesCompleted = completed,
                        totalPointsFromChallenges = points
                    )
                }
            }
        }
    }

    private suspend fun initializeDefaultChallenges() {
        // Check if challenges already exist
        val existingChallenges = challengeDao.getActiveChallenges().first()
        if (existingChallenges.isEmpty()) {
            // Insert default challenges
            val allChallenges = DefaultChallenges.monthlyChallenges +
                    DefaultChallenges.weeklyChallenges +
                    DefaultChallenges.specialChallenges

            allChallenges.forEach { challenge ->
                val entity = mapChallengeToEntity(challenge)
                challengeDao.insertChallenge(entity)

                // Insert milestones
                challenge.milestones.forEach { milestone ->
                    val milestoneEntity = ChallengeMilestoneEntity(
                        id = milestone.id,
                        challengeId = milestone.challengeId,
                        title = milestone.title,
                        description = milestone.description,
                        targetProgress = milestone.targetProgress,
                        isPercentage = milestone.isPercentage,
                        isReached = milestone.isReached,
                        reachedAt = milestone.reachedAt,
                        rewardPoints = milestone.rewardPoints,
                        celebrationMessage = milestone.celebrationMessage,
                        orderIndex = challenge.milestones.indexOf(milestone)
                    )
                    challengeDao.insertMilestone(milestoneEntity)
                }
            }

            // Generate simulated community data
            generateSimulatedCommunityData()
        }
    }

    private suspend fun generateSimulatedCommunityData() {
        val challenges = challengeDao.getActiveChallenges().first()

        challenges.forEach { challenge ->
            // Simulate community progress
            val simulatedParticipants = Random.nextInt(500, 2000)
            val progressPercentage = Random.nextFloat() * 0.6f // 0-60% progress
            val simulatedProgress = (challenge.communityTarget * progressPercentage).toInt()

            challengeDao.updateCommunityProgress(
                challengeId = challenge.id,
                progress = simulatedProgress,
                participants = simulatedParticipants
            )

            // Generate simulated leaderboard
            val leaderboardEntries = generateSimulatedLeaderboard(challenge.id, challenge.targetCount)
            challengeDao.insertLeaderboardEntries(leaderboardEntries)
        }
    }

    private fun generateSimulatedLeaderboard(
        challengeId: String,
        targetCount: Int
    ): List<ChallengeLeaderboardEntity> {
        val names = listOf(
            "MindfulSeeker", "WisdomWanderer", "GrowthGuru", "ReflectionRider",
            "ThoughtTracker", "WordWizard", "JourneyJournal", "PeacePursuer",
            "ZenMaster", "StreakStar", "DailyDevoter", "InsightInquirer",
            "CalmCollector", "VocabVictor", "MotivatedMind", "FocusFinder",
            "SoulSearcher", "BalanceBuilder", "PathPioneer", "HarmonyHunter"
        )

        return names.mapIndexed { index, name ->
            ChallengeLeaderboardEntity(
                odId = "user_$index",
                challengeId = challengeId,
                displayName = name,
                avatarId = "avatar_${index % 10}",
                progress = (targetCount * (1f - index * 0.04f).coerceAtLeast(0.1f)).toInt() +
                        Random.nextInt(-5, 5).coerceAtLeast(0),
                rank = index + 1,
                isCurrentUser = false
            )
        }.sortedByDescending { it.progress }
            .mapIndexed { index, entry -> entry.copy(rank = index + 1) }
    }

    fun selectChallenge(challenge: Challenge) {
        viewModelScope.launch {
            _uiState.update { it.copy(selectedChallenge = challenge) }

            // Load leaderboard for selected challenge
            challengeDao.getChallengeLeaderboard(challenge.id, 20).collect { entries ->
                val leaderboard = entries.map {
                    ChallengeLeaderboardEntry(
                        odId = it.odId,
                        displayName = it.displayName,
                        avatarId = it.avatarId,
                        progress = it.progress,
                        rank = it.rank,
                        isCurrentUser = it.isCurrentUser
                    )
                }
                _uiState.update { it.copy(challengeLeaderboard = leaderboard) }
            }
        }
    }

    fun clearSelectedChallenge() {
        _uiState.update { it.copy(selectedChallenge = null, challengeLeaderboard = emptyList()) }
    }

    fun joinChallenge(challengeId: String) {
        viewModelScope.launch {
            challengeDao.joinChallenge(challengeId)

            // Add user to leaderboard
            val userProfile = userDao.getUserProfileSync()
            val leaderboardEntry = ChallengeLeaderboardEntity(
                odId = "current_user",
                challengeId = challengeId,
                displayName = userProfile?.displayName ?: "You",
                avatarId = userProfile?.avatarId ?: "default",
                progress = 0,
                rank = 0,
                isCurrentUser = true
            )
            challengeDao.insertLeaderboardEntry(leaderboardEntry)

            // Award points for joining
            userDao.addPoints(10)

            // Show celebration
            _uiState.update {
                it.copy(
                    showCelebration = true,
                    celebrationMessage = "Challenge Joined! +10 points"
                )
            }
        }
    }

    fun recordProgress(challengeId: String, progressAmount: Int = 1) {
        viewModelScope.launch {
            val challenge = challengeDao.getChallengeByIdSync(challengeId) ?: return@launch

            if (!challenge.isJoined) return@launch

            // Update user progress
            challengeDao.incrementUserProgress(challengeId, progressAmount)

            // Check if completed
            val newProgress = challenge.currentUserProgress + progressAmount
            if (newProgress >= challenge.targetCount && !challenge.isCompleted) {
                challengeDao.markChallengeCompleted(challengeId)

                // Award completion reward
                userDao.addPoints(challenge.rewardPoints)

                _uiState.update {
                    it.copy(
                        showCelebration = true,
                        celebrationMessage = "Challenge Completed! +${challenge.rewardPoints} points"
                    )
                }
            }

            // Update community progress simulation
            val updatedCommunityProgress = challenge.communityProgress + progressAmount
            challengeDao.updateCommunityProgress(
                challengeId,
                updatedCommunityProgress,
                challenge.totalParticipants
            )

            // Check milestones
            checkMilestones(challengeId, updatedCommunityProgress, challenge.communityTarget)
        }
    }

    private suspend fun checkMilestones(challengeId: String, currentProgress: Int, target: Int) {
        val milestones = challengeDao.getMilestonesForChallenge(challengeId).first()
        val progressPercentage = (currentProgress.toFloat() / target * 100).toInt()

        milestones.forEach { milestone ->
            if (!milestone.isReached) {
                val threshold = if (milestone.isPercentage) {
                    milestone.targetProgress
                } else {
                    (milestone.targetProgress.toFloat() / target * 100).toInt()
                }

                if (progressPercentage >= threshold) {
                    challengeDao.markMilestoneReached(milestone.id)

                    // Award milestone points
                    userDao.addPoints(milestone.rewardPoints)

                    _uiState.update {
                        it.copy(
                            showCelebration = true,
                            celebrationMessage = "${milestone.celebrationMessage}\n+${milestone.rewardPoints} points"
                        )
                    }
                }
            }
        }
    }

    fun dismissCelebration() {
        _uiState.update { it.copy(showCelebration = false, celebrationMessage = "") }
    }

    private fun mapEntityToChallenge(entity: ChallengeEntity): Challenge {
        return Challenge(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            type = ChallengeType.values().find { it.name.equals(entity.type, ignoreCase = true) }
                ?: ChallengeType.MIXED,
            difficulty = ChallengeDifficulty.values().find { it.name.equals(entity.difficulty, ignoreCase = true) }
                ?: ChallengeDifficulty.MEDIUM,
            startDate = entity.startDate,
            endDate = entity.endDate,
            targetCount = entity.targetCount,
            currentUserProgress = entity.currentUserProgress,
            isJoined = entity.isJoined,
            joinedAt = entity.joinedAt,
            totalParticipants = entity.totalParticipants,
            communityProgress = entity.communityProgress,
            communityTarget = entity.communityTarget,
            rewardPoints = entity.rewardPoints,
            rewardBadgeId = entity.rewardBadgeId,
            rewardTitle = entity.rewardTitle,
            isCompleted = entity.isCompleted,
            completedAt = entity.completedAt,
            isFeatured = entity.isFeatured
        )
    }

    private fun mapChallengeToEntity(challenge: Challenge): ChallengeEntity {
        return ChallengeEntity(
            id = challenge.id,
            title = challenge.title,
            description = challenge.description,
            type = challenge.type.name.lowercase(),
            startDate = challenge.startDate,
            endDate = challenge.endDate,
            targetCount = challenge.targetCount,
            currentUserProgress = challenge.currentUserProgress,
            isJoined = challenge.isJoined,
            joinedAt = challenge.joinedAt,
            totalParticipants = challenge.totalParticipants,
            communityProgress = challenge.communityProgress,
            communityTarget = challenge.communityTarget,
            rewardPoints = challenge.rewardPoints,
            rewardBadgeId = challenge.rewardBadgeId,
            rewardTitle = challenge.rewardTitle,
            difficulty = challenge.difficulty.name.lowercase(),
            isCompleted = challenge.isCompleted,
            completedAt = challenge.completedAt,
            isFeatured = challenge.isFeatured
        )
    }
}
