package com.prody.prashant.domain.repository

import com.prody.prashant.domain.common.Result
import com.prody.prashant.domain.social.*
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Social Accountability Circles.
 *
 * Handles all operations related to circles, members, updates, nudges,
 * challenges, and privacy settings.
 */
interface SocialRepository {

    // =========================================================================
    // CIRCLES
    // =========================================================================

    /**
     * Get all circles for a user.
     */
    fun observeUserCircles(userId: String): Flow<List<Circle>>

    /**
     * Get a specific circle by ID.
     */
    suspend fun getCircleById(circleId: String): Result<Circle>

    /**
     * Observe a specific circle.
     */
    fun observeCircle(circleId: String): Flow<Circle?>

    /**
     * Create a new circle.
     */
    suspend fun createCircle(
        userId: String,
        displayName: String,
        name: String,
        description: String?,
        colorTheme: CircleTheme,
        iconEmoji: String,
        maxMembers: Int = 10
    ): Result<Circle>

    /**
     * Join a circle using invite code.
     */
    suspend fun joinCircle(
        userId: String,
        displayName: String,
        inviteCode: String
    ): Result<Circle>

    /**
     * Leave a circle.
     */
    suspend fun leaveCircle(userId: String, circleId: String): Result<Unit>

    /**
     * Update circle details (owner/admin only).
     */
    suspend fun updateCircle(
        circleId: String,
        name: String?,
        description: String?,
        colorTheme: CircleTheme?,
        iconEmoji: String?
    ): Result<Unit>

    /**
     * Delete a circle (owner only).
     */
    suspend fun deleteCircle(circleId: String): Result<Unit>

    /**
     * Regenerate invite code for a circle.
     */
    suspend fun regenerateInviteCode(circleId: String): Result<String>

    // =========================================================================
    // MEMBERS
    // =========================================================================

    /**
     * Get all members in a circle.
     */
    fun observeCircleMembers(circleId: String): Flow<List<CircleMember>>

    /**
     * Get member details.
     */
    suspend fun getCircleMember(circleId: String, userId: String): Result<CircleMember>

    /**
     * Update member role (owner/admin only).
     */
    suspend fun updateMemberRole(
        circleId: String,
        userId: String,
        newRole: MemberRole
    ): Result<Unit>

    /**
     * Remove a member from circle (owner/admin only).
     */
    suspend fun removeMember(
        circleId: String,
        userId: String,
        removedBy: String
    ): Result<Unit>

    /**
     * Update member stats (automatic).
     */
    suspend fun updateMemberStats(
        userId: String,
        circleId: String,
        currentStreak: Int?,
        totalEntries: Int?
    ): Result<Unit>

    // =========================================================================
    // UPDATES (Activity Feed)
    // =========================================================================

    /**
     * Observe circle updates.
     */
    fun observeCircleUpdates(circleId: String, limit: Int = 50): Flow<List<CircleUpdate>>

    /**
     * Get circle updates (paginated).
     */
    suspend fun getCircleUpdates(
        circleId: String,
        limit: Int = 50,
        offset: Int = 0
    ): Result<List<CircleUpdate>>

    /**
     * Post a manual encouragement update.
     */
    suspend fun postEncouragementUpdate(
        userId: String,
        displayName: String,
        circleId: String,
        message: String
    ): Result<CircleUpdate>

    /**
     * React to an update.
     */
    suspend fun reactToUpdate(
        updateId: Long,
        userId: String,
        emoji: String
    ): Result<Unit>

    /**
     * Remove reaction from an update.
     */
    suspend fun removeReaction(
        updateId: Long,
        userId: String
    ): Result<Unit>

    /**
     * Delete an update (owner only).
     */
    suspend fun deleteUpdate(updateId: Long, userId: String): Result<Unit>

    // =========================================================================
    // NUDGES
    // =========================================================================

    /**
     * Observe unread nudges for a user.
     */
    fun observeUnreadNudges(userId: String): Flow<List<Nudge>>

    /**
     * Get nudge count.
     */
    fun observeUnreadNudgeCount(userId: String): Flow<Int>

    /**
     * Send a nudge to a circle member.
     */
    suspend fun sendNudge(
        fromUserId: String,
        fromDisplayName: String,
        toUserId: String,
        circleId: String,
        nudgeType: NudgeType,
        message: String? = null
    ): Result<Nudge>

    /**
     * Mark nudge as read.
     */
    suspend fun markNudgeAsRead(nudgeId: Long): Result<Unit>

    /**
     * Mark all nudges as read.
     */
    suspend fun markAllNudgesAsRead(userId: String): Result<Unit>

    // =========================================================================
    // CHALLENGES
    // =========================================================================

    /**
     * Observe active challenges in a circle.
     */
    fun observeActiveChallenges(circleId: String): Flow<List<CircleChallenge>>

    /**
     * Get challenge details.
     */
    suspend fun getChallengeById(challengeId: String): Result<CircleChallenge>

    /**
     * Observe a specific challenge.
     */
    fun observeChallenge(challengeId: String): Flow<CircleChallenge?>

    /**
     * Create a new challenge.
     */
    suspend fun createChallenge(
        userId: String,
        circleId: String,
        title: String,
        description: String,
        startDate: Long,
        endDate: Long,
        targetType: ChallengeTargetType,
        targetValue: Int
    ): Result<CircleChallenge>

    /**
     * Join a challenge.
     */
    suspend fun joinChallenge(
        userId: String,
        challengeId: String
    ): Result<Unit>

    /**
     * Leave a challenge.
     */
    suspend fun leaveChallenge(
        userId: String,
        challengeId: String
    ): Result<Unit>

    /**
     * Update challenge progress.
     */
    suspend fun updateChallengeProgress(
        userId: String,
        challengeId: String,
        progress: Int
    ): Result<Unit>

    /**
     * Get challenge leaderboard.
     */
    suspend fun getChallengeLeaderboard(challengeId: String): Result<ChallengeLeaderboard>

    /**
     * End a challenge (creator only).
     */
    suspend fun endChallenge(challengeId: String): Result<Unit>

    // =========================================================================
    // PRIVACY
    // =========================================================================

    /**
     * Get privacy settings for a circle.
     */
    suspend fun getPrivacySettings(userId: String, circleId: String): Result<PrivacySettings>

    /**
     * Observe global privacy settings.
     */
    fun observeGlobalPrivacySettings(userId: String): Flow<PrivacySettings>

    /**
     * Update global privacy settings.
     */
    suspend fun updateGlobalPrivacySettings(
        userId: String,
        settings: PrivacySettings
    ): Result<Unit>

    /**
     * Update circle-specific privacy settings.
     */
    suspend fun updateCirclePrivacySettings(
        userId: String,
        circleId: String,
        settings: PrivacySettings
    ): Result<Unit>

    // =========================================================================
    // NOTIFICATIONS
    // =========================================================================

    /**
     * Observe unread notifications.
     */
    fun observeUnreadNotifications(userId: String): Flow<List<CircleNotification>>

    /**
     * Get notification count.
     */
    fun observeUnreadNotificationCount(userId: String): Flow<Int>

    /**
     * Mark notification as read.
     */
    suspend fun markNotificationAsRead(notificationId: Long): Result<Unit>

    /**
     * Mark all notifications as read.
     */
    suspend fun markAllNotificationsAsRead(userId: String): Result<Unit>

    // =========================================================================
    // STATISTICS & UTILITIES
    // =========================================================================

    /**
     * Get circle summary with recent activity.
     */
    suspend fun getCircleSummary(circleId: String): Result<CircleSummary>

    /**
     * Get member stats with privacy filtering.
     */
    suspend fun getMemberStats(
        userId: String,
        circleId: String
    ): Result<MemberStats>

    /**
     * Validate invite code.
     */
    suspend fun validateInviteCode(inviteCode: String): Result<Circle>

    /**
     * Search circles (for future social discovery feature).
     */
    suspend fun searchCircles(query: String): Result<List<Circle>>

    // =========================================================================
    // MAINTENANCE
    // =========================================================================

    /**
     * Cleanup old data.
     */
    suspend fun cleanupOldData(): Result<Unit>

    /**
     * Sync member stats across all circles.
     */
    suspend fun syncMemberStats(userId: String): Result<Unit>
}
