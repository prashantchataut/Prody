package com.prody.prashant.data.repository

import com.prody.prashant.data.local.dao.SocialDao
import com.prody.prashant.data.local.entity.*
import com.prody.prashant.domain.common.ErrorType
import com.prody.prashant.domain.common.Result
import com.prody.prashant.domain.repository.SocialRepository
import com.prody.prashant.domain.social.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import javax.inject.Inject
import kotlin.random.Random

/**
 * Implementation of SocialRepository.
 *
 * Handles all social accountability circle operations with privacy-first approach.
 */
class SocialRepositoryImpl @Inject constructor(
    private val socialDao: SocialDao,
    private val privacyManager: SocialPrivacyManager,
    private val updateGenerator: CircleUpdateGenerator
) : SocialRepository {

    companion object {
        private const val INVITE_CODE_LENGTH = 6
        private val INVITE_CODE_CHARS = ('A'..'Z') + ('0'..'9')
    }

    // =========================================================================
    // CIRCLES
    // =========================================================================

    override fun observeUserCircles(userId: String): Flow<List<Circle>> {
        return socialDao.observeUserCircles(userId).map { entities ->
            entities.mapNotNull { entity ->
                try {
                    entity.toDomain(userId, socialDao)
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    override suspend fun getCircleById(circleId: String): Result<Circle> {
        return try {
            val entity = socialDao.getCircleById(circleId)
            if (entity != null) {
                Result.Success(entity.toDomain("", socialDao))
            } else {
                Result.error(Exception("Circle not found"), "Circle not found", ErrorType.NOT_FOUND)
            }
        } catch (e: Exception) {
            Result.error(e, "Failed to get circle: ${e.message}", ErrorType.DATABASE)
        }
    }

    override fun observeCircle(circleId: String): Flow<Circle?> {
        return socialDao.observeCircle(circleId).map { entity ->
            entity?.toDomain("", socialDao)
        }
    }

    override suspend fun createCircle(
        userId: String,
        displayName: String,
        name: String,
        description: String?,
        colorTheme: CircleTheme,
        iconEmoji: String,
        maxMembers: Int
    ): Result<Circle> {
        return try {
            val circleId = UUID.randomUUID().toString()
            val inviteCode = generateInviteCode()

            val circle = CircleEntity(
                id = circleId,
                name = name,
                description = description,
                createdBy = userId,
                inviteCode = inviteCode,
                colorTheme = colorTheme.name,
                iconEmoji = iconEmoji,
                memberCount = 1,
                maxMembers = maxMembers
            )

            socialDao.insertCircle(circle)

            // Add creator as owner
            val memberId = "${circleId}_$userId"
            val member = CircleMemberEntity(
                id = memberId,
                circleId = circleId,
                userId = userId,
                displayName = displayName,
                role = MemberRole.OWNER.name
            )

            socialDao.insertMember(member)

            Result.Success(circle.toDomain(userId, socialDao))
        } catch (e: Exception) {
            Result.error(e, "Failed to create circle: ${e.message}", ErrorType.DATABASE)
        }
    }

    override suspend fun joinCircle(
        userId: String,
        displayName: String,
        inviteCode: String
    ): Result<Circle> {
        return try {
            val circle = socialDao.getCircleByInviteCode(inviteCode)
                ?: return Result.error(Exception("Invalid invite code"), "Invalid invite code", ErrorType.VALIDATION)

            if (!circle.isActive) {
                return Result.error(Exception("This circle is no longer active"), "This circle is no longer active", ErrorType.VALIDATION)
            }

            val memberCount = socialDao.getActiveMemberCount(circle.id)
            if (memberCount >= circle.maxMembers) {
                return Result.error(Exception("Circle is full"), "Circle is full", ErrorType.VALIDATION)
            }

            // Check if already a member
            val existingMember = socialDao.getCircleMember(circle.id, userId)
            if (existingMember != null && existingMember.isActive) {
                return Result.error(Exception("You're already in this circle"), "You're already in this circle", ErrorType.VALIDATION)
            }

            val memberId = "${circle.id}_$userId"
            val member = CircleMemberEntity(
                id = memberId,
                circleId = circle.id,
                userId = userId,
                displayName = displayName,
                role = MemberRole.MEMBER.name
            )

            socialDao.insertMember(member)
            socialDao.updateMemberCount(circle.id, memberCount + 1)

            // Create notification for circle members
            notifyCircleMembers(
                circle.id,
                userId,
                NotificationType.NEW_MEMBER,
                "New member joined",
                "$displayName joined ${circle.name}"
            )

            Result.Success(circle.toDomain(userId, socialDao))
        } catch (e: Exception) {
            Result.error(e, "Failed to join circle: ${e.message}", ErrorType.DATABASE)
        }
    }

    override suspend fun leaveCircle(userId: String, circleId: String): Result<Unit> {
        return try {
            socialDao.leaveCircle(userId, circleId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.error(e, "Failed to leave circle: ${e.message}", ErrorType.DATABASE)
        }
    }

    override suspend fun updateCircle(
        circleId: String,
        name: String?,
        description: String?,
        colorTheme: CircleTheme?,
        iconEmoji: String?
    ): Result<Unit> {
        return try {
            val circle = socialDao.getCircleById(circleId)
                ?: return Result.error(Exception("Circle not found"), "Circle not found", ErrorType.NOT_FOUND)

            val updated = circle.copy(
                name = name ?: circle.name,
                description = description ?: circle.description,
                colorTheme = colorTheme?.name ?: circle.colorTheme,
                iconEmoji = iconEmoji ?: circle.iconEmoji,
                updatedAt = System.currentTimeMillis()
            )

            socialDao.updateCircle(updated)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.error(e, "Failed to update circle: ${e.message}", ErrorType.DATABASE)
        }
    }

    override suspend fun deleteCircle(circleId: String): Result<Unit> {
        return try {
            socialDao.deleteCircleCompletely(circleId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.error(e, "Failed to delete circle: ${e.message}", ErrorType.DATABASE)
        }
    }

    override suspend fun regenerateInviteCode(circleId: String): Result<String> {
        return try {
            val circle = socialDao.getCircleById(circleId)
                ?: return Result.error(Exception("Circle not found"), "Circle not found", ErrorType.NOT_FOUND)

            val newCode = generateInviteCode()
            socialDao.updateCircle(circle.copy(inviteCode = newCode))
            Result.Success(newCode)
        } catch (e: Exception) {
            Result.error(e, "Failed to regenerate invite code: ${e.message}", ErrorType.DATABASE)
        }
    }

    // =========================================================================
    // MEMBERS
    // =========================================================================

    override fun observeCircleMembers(circleId: String): Flow<List<CircleMember>> {
        return socialDao.observeCircleMembers(circleId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getCircleMember(circleId: String, userId: String): Result<CircleMember> {
        return try {
            val entity = socialDao.getCircleMember(circleId, userId)
            if (entity != null) {
                Result.Success(entity.toDomain())
            } else {
                Result.error(Exception("Member not found"), "Member not found", ErrorType.NOT_FOUND)
            }
        } catch (e: Exception) {
            Result.error(e, "Failed to get member: ${e.message}", ErrorType.DATABASE)
        }
    }

    override suspend fun updateMemberRole(
        circleId: String,
        userId: String,
        newRole: MemberRole
    ): Result<Unit> {
        return try {
            val member = socialDao.getCircleMember(circleId, userId)
                ?: return Result.error(Exception("Member not found"), "Member not found", ErrorType.NOT_FOUND)

            socialDao.updateMember(member.copy(role = newRole.name))
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.error(e, "Failed to update role: ${e.message}", ErrorType.DATABASE)
        }
    }

    override suspend fun removeMember(
        circleId: String,
        userId: String,
        removedBy: String
    ): Result<Unit> {
        return try {
            socialDao.removeMember(userId, circleId)
            val count = socialDao.getActiveMemberCount(circleId)
            socialDao.updateMemberCount(circleId, count)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.error(e, "Failed to remove member: ${e.message}", ErrorType.DATABASE)
        }
    }

    override suspend fun updateMemberStats(
        userId: String,
        circleId: String,
        currentStreak: Int?,
        totalEntries: Int?
    ): Result<Unit> {
        return try {
            currentStreak?.let {
                socialDao.updateMemberStreak(userId, circleId, it)
            }
            totalEntries?.let {
                socialDao.updateMemberEntryCount(userId, circleId, it)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.error(e, "Failed to update member stats: ${e.message}", ErrorType.DATABASE)
        }
    }

    // =========================================================================
    // UPDATES
    // =========================================================================

    override fun observeCircleUpdates(circleId: String, limit: Int): Flow<List<CircleUpdate>> {
        return socialDao.observeCircleUpdates(circleId, limit).map { entities ->
            entities.mapNotNull { entity ->
                try {
                    entity.toDomain(socialDao)
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    override suspend fun getCircleUpdates(
        circleId: String,
        limit: Int,
        offset: Int
    ): Result<List<CircleUpdate>> {
        return try {
            val entities = socialDao.getCircleUpdates(circleId, limit, offset)
            val updates = entities.mapNotNull { entity ->
                try {
                    entity.toDomain(socialDao)
                } catch (e: Exception) {
                    null
                }
            }
            Result.Success(updates)
        } catch (e: Exception) {
            Result.error(e, "Failed to get updates: ${e.message}", ErrorType.DATABASE)
        }
    }

    override suspend fun postEncouragementUpdate(
        userId: String,
        displayName: String,
        circleId: String,
        message: String
    ): Result<CircleUpdate> {
        return try {
            if (!privacyManager.isContentSafe(message)) {
                return Result.error(Exception("Message contains sensitive content"), "Message contains sensitive content", ErrorType.VALIDATION)
            }

            updateGenerator.generateEncouragementUpdate(userId, displayName, circleId, message)

            val updates = socialDao.getCircleUpdates(circleId, limit = 1)
            val update = updates.firstOrNull()?.toDomain(socialDao)
                ?: return Result.error(Exception("Failed to retrieve posted update"), "Failed to retrieve posted update", ErrorType.UNKNOWN)

            Result.Success(update)
        } catch (e: Exception) {
            Result.error(e, "Failed to post update: ${e.message}", ErrorType.DATABASE)
        }
    }

    override suspend fun reactToUpdate(updateId: Long, userId: String, emoji: String): Result<Unit> {
        return try {
            val update = socialDao.getUpdateById(updateId)
                ?: return Result.error(Exception("Update not found"), "Update not found", ErrorType.NOT_FOUND)

            val reactions = JSONObject(update.reactionsJson)
            val usersList = reactions.optJSONArray(emoji) ?: JSONArray()

            // Add user if not already reacted
            val userIds = mutableListOf<String>()
            for (i in 0 until usersList.length()) {
                userIds.add(usersList.getString(i))
            }

            if (!userIds.contains(userId)) {
                userIds.add(userId)
                val newUsersList = JSONArray(userIds)
                reactions.put(emoji, newUsersList)

                val reactionCount = reactions.keys().asSequence().sumOf {
                    reactions.getJSONArray(it).length()
                }

                socialDao.updateReactions(updateId, reactions.toString(), reactionCount)
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.error(e, "Failed to react: ${e.message}", ErrorType.DATABASE)
        }
    }

    override suspend fun removeReaction(updateId: Long, userId: String): Result<Unit> {
        return try {
            val update = socialDao.getUpdateById(updateId)
                ?: return Result.error(Exception("Update not found"), "Update not found", ErrorType.NOT_FOUND)

            val reactions = JSONObject(update.reactionsJson)
            val keys = reactions.keys()

            keys.forEach { emoji ->
                val usersList = reactions.getJSONArray(emoji)
                val newList = JSONArray()
                for (i in 0 until usersList.length()) {
                    val id = usersList.getString(i)
                    if (id != userId) {
                        newList.put(id)
                    }
                }
                reactions.put(emoji, newList)
            }

            val reactionCount = reactions.keys().asSequence().sumOf {
                reactions.getJSONArray(it).length()
            }

            socialDao.updateReactions(updateId, reactions.toString(), reactionCount)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.error(e, "Failed to remove reaction: ${e.message}", ErrorType.DATABASE)
        }
    }

    override suspend fun deleteUpdate(updateId: Long, userId: String): Result<Unit> {
        return try {
            val update = socialDao.getUpdateById(updateId)
                ?: return Result.error(Exception("Update not found"), "Update not found", ErrorType.NOT_FOUND)

            if (update.userId != userId) {
                return Result.error(Exception("You can only delete your own updates"), "You can only delete your own updates", ErrorType.PERMISSION)
            }

            socialDao.deleteUpdate(update)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.error(e, "Failed to delete update: ${e.message}", ErrorType.DATABASE)
        }
    }

    // =========================================================================
    // NUDGES
    // =========================================================================

    override fun observeUnreadNudges(userId: String): Flow<List<Nudge>> {
        return socialDao.observeUnreadNudges(userId).map { entities ->
            entities.mapNotNull { entity ->
                try {
                    entity.toDomain(socialDao)
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    override fun observeUnreadNudgeCount(userId: String): Flow<Int> {
        return socialDao.observeUnreadNudgeCount(userId)
    }

    override suspend fun sendNudge(
        fromUserId: String,
        fromDisplayName: String,
        toUserId: String,
        circleId: String,
        nudgeType: NudgeType,
        message: String?
    ): Result<Nudge> {
        return try {
            // Check privacy settings
            val settings = privacyManager.getPrivacySettings(toUserId, circleId)
            if (!settings.allowNudgesFromMembers) {
                return Result.error(Exception("This member has disabled nudges"), "This member has disabled nudges", ErrorType.PERMISSION)
            }

            val nudge = CircleNudgeEntity(
                circleId = circleId,
                fromUserId = fromUserId,
                fromDisplayName = fromDisplayName,
                toUserId = toUserId,
                nudgeType = nudgeType.name,
                message = message
            )

            val id = socialDao.insertNudge(nudge)

            // Create notification
            socialDao.insertNotification(
                CircleNotificationEntity(
                    userId = toUserId,
                    circleId = circleId,
                    notificationType = NotificationType.NUDGE.name,
                    title = "Nudge from $fromDisplayName",
                    message = message ?: nudgeType.defaultMessage,
                    actionType = NotificationAction.RESPOND_NUDGE.name,
                    actionData = JSONObject().apply {
                        put("nudgeId", id)
                    }.toString()
                )
            )

            val savedNudge = socialDao.getUserNudges(toUserId, 1).firstOrNull()
                ?: return Result.error(Exception("Failed to save nudge"), "Failed to save nudge", ErrorType.DATABASE)
            Result.Success(savedNudge.toDomain(socialDao))
        } catch (e: Exception) {
            Result.error(e, "Failed to send nudge: ${e.message}", ErrorType.DATABASE)
        }
    }

    override suspend fun markNudgeAsRead(nudgeId: Long): Result<Unit> {
        return try {
            socialDao.markNudgeAsRead(nudgeId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.error(e, "Failed to mark nudge as read: ${e.message}", ErrorType.DATABASE)
        }
    }

    override suspend fun markAllNudgesAsRead(userId: String): Result<Unit> {
        return try {
            socialDao.markAllNudgesAsRead(userId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.error(e, "Failed to mark all nudges as read: ${e.message}", ErrorType.DATABASE)
        }
    }

    // =========================================================================
    // CHALLENGES (Continued in next section due to length)
    // =========================================================================

    override fun observeActiveChallenges(circleId: String): Flow<List<CircleChallenge>> {
        return socialDao.observeActiveChallenges(circleId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getChallengeById(challengeId: String): Result<CircleChallenge> {
        return try {
            val entity = socialDao.getChallengeById(challengeId)
            if (entity != null) {
                Result.Success(entity.toDomain())
            } else {
                Result.error(Exception("Challenge not found"), "Challenge not found", ErrorType.NOT_FOUND)
            }
        } catch (e: Exception) {
            Result.error(e, "Failed to get challenge: ${e.message}", ErrorType.DATABASE)
        }
    }

    override fun observeChallenge(challengeId: String): Flow<CircleChallenge?> {
        return socialDao.observeChallenge(challengeId).map { it?.toDomain() }
    }

    override suspend fun createChallenge(
        userId: String,
        circleId: String,
        title: String,
        description: String,
        startDate: Long,
        endDate: Long,
        targetType: ChallengeTargetType,
        targetValue: Int
    ): Result<CircleChallenge> {
        return try {
            val challengeId = UUID.randomUUID().toString()
            val challenge = CircleChallengeEntity(
                id = challengeId,
                circleId = circleId,
                title = title,
                description = description,
                startDate = startDate,
                endDate = endDate,
                targetType = targetType.name,
                targetValue = targetValue,
                createdBy = userId,
                participantsJson = JSONArray(listOf(userId)).toString(),
                progressJson = JSONObject().apply { put(userId, 0) }.toString()
            )

            socialDao.insertChallenge(challenge)

            // Notify circle members
            notifyCircleMembers(
                circleId,
                userId,
                NotificationType.CHALLENGE_UPDATE,
                "New Challenge",
                "A new challenge '$title' has been created!"
            )

            Result.Success(challenge.toDomain())
        } catch (e: Exception) {
            Result.error(e, "Failed to create challenge: ${e.message}", ErrorType.DATABASE)
        }
    }

    override suspend fun joinChallenge(userId: String, challengeId: String): Result<Unit> {
        return try {
            val challenge = socialDao.getChallengeById(challengeId)
                ?: return Result.error(Exception("Challenge not found"), "Challenge not found", ErrorType.NOT_FOUND)

            val participants = JSONArray(challenge.participantsJson)
            val participantsList = mutableListOf<String>()
            for (i in 0 until participants.length()) {
                participantsList.add(participants.getString(i))
            }

            if (!participantsList.contains(userId)) {
                participantsList.add(userId)
                socialDao.updateChallengeParticipants(
                    challengeId,
                    JSONArray(participantsList).toString()
                )

                // Initialize progress
                val progress = JSONObject(challenge.progressJson)
                progress.put(userId, 0)
                socialDao.updateChallengeProgress(challengeId, progress.toString())
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.error(e, "Failed to join challenge: ${e.message}", ErrorType.DATABASE)
        }
    }

    override suspend fun leaveChallenge(userId: String, challengeId: String): Result<Unit> {
        return try {
            val challenge = socialDao.getChallengeById(challengeId)
                ?: return Result.error(Exception("Challenge not found"), "Challenge not found", ErrorType.NOT_FOUND)

            val participants = JSONArray(challenge.participantsJson)
            val newList = JSONArray()
            for (i in 0 until participants.length()) {
                val id = participants.getString(i)
                if (id != userId) {
                    newList.put(id)
                }
            }

            socialDao.updateChallengeParticipants(challengeId, newList.toString())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.error(e, "Failed to leave challenge: ${e.message}", ErrorType.DATABASE)
        }
    }

    override suspend fun updateChallengeProgress(
        userId: String,
        challengeId: String,
        progress: Int
    ): Result<Unit> {
        return try {
            val challenge = socialDao.getChallengeById(challengeId)
                ?: return Result.error(Exception("Challenge not found"), "Challenge not found", ErrorType.NOT_FOUND)

            val progressObj = JSONObject(challenge.progressJson)
            progressObj.put(userId, progress)
            socialDao.updateChallengeProgress(challengeId, progressObj.toString())

            // Check if completed
            if (progress >= challenge.targetValue) {
                val completedBy = JSONArray(challenge.completedByJson)
                val completedList = mutableListOf<String>()
                for (i in 0 until completedBy.length()) {
                    completedList.add(completedBy.getString(i))
                }
                if (!completedList.contains(userId)) {
                    completedList.add(userId)
                    socialDao.updateChallengeCompletions(
                        challengeId,
                        JSONArray(completedList).toString()
                    )
                }
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.error(e, "Failed to update progress: ${e.message}", ErrorType.DATABASE)
        }
    }

    override suspend fun getChallengeLeaderboard(challengeId: String): Result<ChallengeLeaderboard> {
        return try {
            val challenge = socialDao.getChallengeById(challengeId)
                ?: return Result.error(Exception("Challenge not found"), "Challenge not found", ErrorType.NOT_FOUND)

            val progressObj = JSONObject(challenge.progressJson)
            val rankings = mutableListOf<ChallengeRanking>()

            val participants = JSONArray(challenge.participantsJson)
            for (i in 0 until participants.length()) {
                val userId = participants.getString(i)
                val progress = progressObj.optInt(userId, 0)
                val member = socialDao.getCircleMember(challenge.circleId, userId)

                if (member != null) {
                    rankings.add(
                        ChallengeRanking(
                            rank = 0, // Will be set after sorting
                            member = member.toDomain(),
                            progress = progress,
                            progressPercent = progress.toFloat() / challenge.targetValue.toFloat(),
                            isCompleted = progress >= challenge.targetValue
                        )
                    )
                }
            }

            // Sort by progress descending
            rankings.sortByDescending { it.progress }
            rankings.forEachIndexed { index, ranking ->
                rankings[index] = ranking.copy(rank = index + 1)
            }

            Result.Success(
                ChallengeLeaderboard(
                    challenge = challenge.toDomain(),
                    rankings = rankings
                )
            )
        } catch (e: Exception) {
            Result.error(e, "Failed to get leaderboard: ${e.message}", ErrorType.DATABASE)
        }
    }

    override suspend fun endChallenge(challengeId: String): Result<Unit> {
        return try {
            socialDao.deactivateChallenge(challengeId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.error(e, "Failed to end challenge: ${e.message}", ErrorType.DATABASE)
        }
    }

    // =========================================================================
    // PRIVACY
    // =========================================================================

    override suspend fun getPrivacySettings(userId: String, circleId: String): Result<PrivacySettings> {
        return try {
            val settings = privacyManager.getPrivacySettings(userId, circleId)
            Result.Success(settings)
        } catch (e: Exception) {
            Result.error(e, "Failed to get privacy settings: ${e.message}", ErrorType.DATABASE)
        }
    }

    override fun observeGlobalPrivacySettings(userId: String): Flow<PrivacySettings> {
        return privacyManager.observeGlobalPrivacySettings(userId)
    }

    override suspend fun updateGlobalPrivacySettings(
        userId: String,
        settings: PrivacySettings
    ): Result<Unit> {
        return try {
            privacyManager.updateGlobalPrivacySettings(userId, settings)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.error(e, "Failed to update privacy settings: ${e.message}", ErrorType.DATABASE)
        }
    }

    override suspend fun updateCirclePrivacySettings(
        userId: String,
        circleId: String,
        settings: PrivacySettings
    ): Result<Unit> {
        return try {
            privacyManager.updateCirclePrivacySettings(userId, circleId, settings)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.error(e, "Failed to update circle privacy settings: ${e.message}", ErrorType.DATABASE)
        }
    }

    // =========================================================================
    // NOTIFICATIONS
    // =========================================================================

    override fun observeUnreadNotifications(userId: String): Flow<List<CircleNotification>> {
        return socialDao.observeUnreadNotifications(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeUnreadNotificationCount(userId: String): Flow<Int> {
        return socialDao.observeUnreadNotificationCount(userId)
    }

    override suspend fun markNotificationAsRead(notificationId: Long): Result<Unit> {
        return try {
            socialDao.markNotificationAsRead(notificationId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.error(e, "Failed to mark notification as read: ${e.message}", ErrorType.DATABASE)
        }
    }

    override suspend fun markAllNotificationsAsRead(userId: String): Result<Unit> {
        return try {
            socialDao.markAllNotificationsAsRead(userId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.error(e, "Failed to mark all notifications as read: ${e.message}", ErrorType.DATABASE)
        }
    }

    // =========================================================================
    // UTILITIES
    // =========================================================================

    override suspend fun getCircleSummary(circleId: String): Result<CircleSummary> {
        return try {
            val circle = socialDao.getCircleById(circleId)
                ?: return Result.error(Exception("Circle not found"), "Circle not found", ErrorType.NOT_FOUND)

            val recentUpdates = socialDao.getCircleUpdates(circleId, limit = 10)
                .mapNotNull { it.toDomain(socialDao) }

            val activeChallenges = socialDao.getCircleChallenges(circleId, limit = 5)
                .map { it.toDomain() }

            val summary = CircleSummary(
                circle = circle.toDomain("", socialDao),
                unreadNotifications = 0, // Would need to calculate per user
                recentActivity = recentUpdates,
                activeChallenges = activeChallenges.filter { it.isActive }
            )

            Result.Success(summary)
        } catch (e: Exception) {
            Result.error(e, "Failed to get circle summary: ${e.message}", ErrorType.DATABASE)
        }
    }

    override suspend fun getMemberStats(userId: String, circleId: String): Result<MemberStats> {
        return try {
            val cache = socialDao.getMemberStatsCache(userId, circleId)
            if (cache != null) {
                Result.Success(cache.toDomain())
            } else {
                Result.error(Exception("Stats not found"), "Stats not found", ErrorType.NOT_FOUND)
            }
        } catch (e: Exception) {
            Result.error(e, "Failed to get member stats: ${e.message}", ErrorType.DATABASE)
        }
    }

    override suspend fun validateInviteCode(inviteCode: String): Result<Circle> {
        return try {
            val circle = socialDao.getCircleByInviteCode(inviteCode)
            if (circle != null && circle.isActive) {
                Result.Success(circle.toDomain("", socialDao))
            } else {
                Result.error(Exception("Invalid or inactive invite code"), "Invalid or inactive invite code", ErrorType.VALIDATION)
            }
        } catch (e: Exception) {
            Result.error(e, "Failed to validate invite code: ${e.message}", ErrorType.DATABASE)
        }
    }

    override suspend fun searchCircles(query: String): Result<List<Circle>> {
        // Future feature: public circle discovery
        return Result.Success(emptyList())
    }

    override suspend fun cleanupOldData(): Result<Unit> {
        return try {
            val thirtyDaysAgo = System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000L)
            socialDao.cleanupOldUpdates(thirtyDaysAgo)
            socialDao.cleanupOldNudges(thirtyDaysAgo)
            socialDao.cleanupOldNotifications(thirtyDaysAgo)
            socialDao.cleanupOldStatsCache(thirtyDaysAgo)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.error(e, "Failed to cleanup: ${e.message}", ErrorType.DATABASE)
        }
    }

    override suspend fun syncMemberStats(userId: String): Result<Unit> {
        return try {
            val circles = socialDao.getUserCircles(userId)
            circles.forEach { circle ->
                socialDao.updateMemberLastActive(userId, circle.id)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.error(e, "Failed to sync stats: ${e.message}", ErrorType.DATABASE)
        }
    }

    // =========================================================================
    // PRIVATE HELPERS
    // =========================================================================

    private fun generateInviteCode(): String {
        return (1..INVITE_CODE_LENGTH)
            .map { INVITE_CODE_CHARS.random() }
            .joinToString("")
    }

    private suspend fun notifyCircleMembers(
        circleId: String,
        excludeUserId: String,
        type: NotificationType,
        title: String,
        message: String
    ) {
        try {
            val members = socialDao.getCircleMembers(circleId)
            members.filter { it.userId != excludeUserId && it.isActive }.forEach { member ->
                socialDao.insertNotification(
                    CircleNotificationEntity(
                        userId = member.userId,
                        circleId = circleId,
                        notificationType = type.name,
                        title = title,
                        message = message,
                        actionType = NotificationAction.OPEN_CIRCLE.name,
                        actionData = JSONObject().apply {
                            put("circleId", circleId)
                        }.toString()
                    )
                )
            }
        } catch (e: Exception) {
            // Log but don't fail the operation
        }
    }
}

// =========================================================================
// EXTENSION FUNCTIONS for Entity -> Domain conversion
// =========================================================================

private suspend fun CircleEntity.toDomain(currentUserId: String, dao: SocialDao): Circle {
    val members = dao.getCircleMembers(id).map { it.toDomain() }
    val yourMember = members.find { it.userId == currentUserId }

    return Circle(
        id = id,
        name = name,
        description = description,
        members = members,
        inviteCode = inviteCode,
        colorTheme = CircleTheme.fromString(colorTheme),
        iconEmoji = iconEmoji,
        yourRole = yourMember?.role ?: MemberRole.MEMBER,
        memberCount = memberCount,
        lastActivityAt = lastActivityAt,
        createdAt = createdAt,
        isActive = isActive,
        allowNudges = allowNudges,
        allowChallenges = allowChallenges,
        maxMembers = maxMembers
    )
}

private fun CircleMemberEntity.toDomain(): CircleMember {
    return CircleMember(
        id = id,
        userId = userId,
        displayName = displayName,
        avatarUrl = avatarUrl,
        currentStreak = currentStreak,
        totalEntries = totalEntries,
        lastActiveAt = lastActiveAt,
        joinedAt = joinedAt,
        role = MemberRole.valueOf(role),
        isActive = isActive
    )
}

private suspend fun CircleUpdateEntity.toDomain(dao: SocialDao): CircleUpdate {
    val member = dao.getCircleMember(circleId, userId)?.toDomain()
        ?: throw IllegalStateException("Member not found for update")

    val reactions = mutableMapOf<String, List<String>>()
    val reactionsObj = JSONObject(reactionsJson)
    reactionsObj.keys().forEach { emoji ->
        val usersList = mutableListOf<String>()
        val array = reactionsObj.getJSONArray(emoji)
        for (i in 0 until array.length()) {
            usersList.add(array.getString(i))
        }
        reactions[emoji] = usersList
    }

    val metadata = metadata?.let { JSONObject(it) }?.let { obj ->
        val map = mutableMapOf<String, Any>()
        obj.keys().forEach { key ->
            map[key] = obj.get(key)
        }
        map
    }

    return CircleUpdate(
        id = id,
        circleId = circleId,
        member = member,
        type = UpdateType.fromString(updateType),
        content = content,
        timestamp = createdAt,
        reactions = reactions,
        metadata = metadata
    )
}

private suspend fun CircleNudgeEntity.toDomain(dao: SocialDao): Nudge {
    val fromMember = dao.getCircleMember(circleId, fromUserId)?.toDomain()
        ?: throw IllegalStateException("From member not found")

    return Nudge(
        id = id,
        circleId = circleId,
        from = fromMember,
        toUserId = toUserId,
        type = NudgeType.fromString(nudgeType),
        message = message,
        timestamp = createdAt,
        isRead = isRead,
        respondedAt = respondedAt
    )
}

private fun CircleChallengeEntity.toDomain(): CircleChallenge {
    val participants = mutableListOf<String>()
    val participantsArray = JSONArray(participantsJson)
    for (i in 0 until participantsArray.length()) {
        participants.add(participantsArray.getString(i))
    }

    val progress = mutableMapOf<String, Int>()
    val progressObj = JSONObject(progressJson)
    progressObj.keys().forEach { userId ->
        progress[userId] = progressObj.getInt(userId)
    }

    val completedBy = mutableListOf<String>()
    val completedArray = JSONArray(completedByJson)
    for (i in 0 until completedArray.length()) {
        completedBy.add(completedArray.getString(i))
    }

    return CircleChallenge(
        id = id,
        circleId = circleId,
        title = title,
        description = description,
        startDate = startDate,
        endDate = endDate,
        targetType = ChallengeTargetType.fromString(targetType),
        targetValue = targetValue,
        createdBy = createdBy,
        createdAt = createdAt,
        participants = participants,
        progress = progress,
        completedBy = completedBy,
        isActive = isActive
    )
}

private fun CircleNotificationEntity.toDomain(): CircleNotification {
    val actionData = actionData?.let { JSONObject(it) }?.let { obj ->
        val map = mutableMapOf<String, String>()
        obj.keys().forEach { key ->
            map[key] = obj.getString(key)
        }
        map
    }

    return CircleNotification(
        id = id,
        circleId = circleId,
        type = NotificationType.fromString(notificationType),
        title = title,
        message = message,
        actionType = NotificationAction.fromString(actionType),
        actionData = actionData,
        isRead = isRead,
        createdAt = createdAt
    )
}

private fun CircleMemberStatsCacheEntity.toDomain(): MemberStats {
    return MemberStats(
        userId = userId,
        currentStreak = currentStreak,
        longestStreak = longestStreak,
        totalEntries = totalEntries,
        totalWords = totalWords,
        meditationMinutes = meditationMinutes,
        lastActiveAt = lastActiveAt
    )
}
