package com.prody.prashant.domain.repository

import com.prody.prashant.data.local.entity.FutureMessageReplyEntity
import com.prody.prashant.domain.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Future Message Reply operations.
 *
 * This enables the enhanced "Future Self Conversation" feature where
 * users can reply to messages from their past self, creating
 * time capsule chains and meaningful dialogues across time.
 */
interface FutureMessageReplyRepository {

    // ==================== RETRIEVAL ====================

    /**
     * Get all replies.
     */
    fun getAllReplies(userId: String = "local"): Flow<List<FutureMessageReplyEntity>>

    /**
     * Get reply by ID.
     */
    suspend fun getReplyById(id: Long): Result<FutureMessageReplyEntity>

    /**
     * Observe reply by ID.
     */
    fun observeReplyById(id: Long): Flow<FutureMessageReplyEntity?>

    /**
     * Get reply for a specific original message.
     */
    suspend fun getReplyForMessage(messageId: Long): Result<FutureMessageReplyEntity?>

    /**
     * Observe reply for a specific original message.
     */
    fun observeReplyForMessage(messageId: Long): Flow<FutureMessageReplyEntity?>

    /**
     * Check if a message has been replied to.
     */
    suspend fun hasReplyForMessage(messageId: Long): Boolean

    // ==================== TIME CAPSULE CHAIN ====================

    /**
     * Get the full conversation chain for a message.
     * Includes original message and all replies that created chains.
     */
    fun getConversationChain(userId: String = "local", messageId: Long): Flow<List<FutureMessageReplyEntity>>

    /**
     * Get replies that have created chained messages.
     */
    fun getRepliesWithChains(userId: String = "local"): Flow<List<FutureMessageReplyEntity>>

    /**
     * Link a chained message to a reply.
     */
    suspend fun setChainedMessage(replyId: Long, chainedMessageId: Long): Result<Unit>

    // ==================== CREATE/UPDATE ====================

    /**
     * Create a new reply to a future message.
     */
    suspend fun createReply(reply: FutureMessageReplyEntity): Result<Long>

    /**
     * Update an existing reply.
     */
    suspend fun updateReply(reply: FutureMessageReplyEntity): Result<Unit>

    // ==================== JOURNAL INTEGRATION ====================

    /**
     * Mark a reply as saved to journal.
     */
    suspend fun markSavedAsJournal(replyId: Long, journalId: Long): Result<Unit>

    /**
     * Get replies that were saved as journal entries.
     */
    fun getRepliesSavedAsJournal(userId: String = "local"): Flow<List<FutureMessageReplyEntity>>

    // ==================== DELETION ====================

    /**
     * Soft delete a reply.
     */
    suspend fun softDeleteReply(id: Long): Result<Unit>

    /**
     * Permanently delete a reply.
     */
    suspend fun deleteReply(id: Long): Result<Unit>

    // ==================== STATISTICS ====================

    /**
     * Get total reply count.
     */
    fun getReplyCount(userId: String = "local"): Flow<Int>

    /**
     * Get count of chains started.
     */
    suspend fun getChainsStartedCount(userId: String = "local"): Int

    /**
     * Get reaction mood distribution.
     */
    suspend fun getReactionMoodDistribution(userId: String = "local"): Map<String, Int>

    // ==================== CLEANUP ====================

    /**
     * Purge soft-deleted replies.
     */
    suspend fun purgeSoftDeleted(): Int
}
