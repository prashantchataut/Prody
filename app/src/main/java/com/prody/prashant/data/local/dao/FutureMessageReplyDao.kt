package com.prody.prashant.data.local.dao

import androidx.room.*
import com.prody.prashant.data.local.entity.FutureMessageReplyEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for FutureMessageReply operations.
 *
 * Handles all database operations for the enhanced Future Self Conversation feature,
 * including replies to past messages and time capsule chains.
 */
@Dao
interface FutureMessageReplyDao {

    // ==================== BASIC CRUD ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReply(reply: FutureMessageReplyEntity): Long

    @Update
    suspend fun updateReply(reply: FutureMessageReplyEntity)

    @Delete
    suspend fun deleteReply(reply: FutureMessageReplyEntity)

    @Query("DELETE FROM future_message_replies WHERE id = :id")
    suspend fun deleteReplyById(id: Long)

    @Query("UPDATE future_message_replies SET isDeleted = 1 WHERE id = :id")
    suspend fun softDeleteReply(id: Long)

    // ==================== RETRIEVAL QUERIES ====================

    @Query("SELECT * FROM future_message_replies WHERE isDeleted = 0 ORDER BY repliedAt DESC")
    fun getAllReplies(): Flow<List<FutureMessageReplyEntity>>

    @Query("SELECT * FROM future_message_replies WHERE userId = :userId AND isDeleted = 0 ORDER BY repliedAt DESC")
    fun getRepliesByUser(userId: String): Flow<List<FutureMessageReplyEntity>>

    @Query("SELECT * FROM future_message_replies WHERE id = :id AND isDeleted = 0")
    suspend fun getReplyById(id: Long): FutureMessageReplyEntity?

    @Query("SELECT * FROM future_message_replies WHERE id = :id")
    fun observeReplyById(id: Long): Flow<FutureMessageReplyEntity?>

    // ==================== REPLIES FOR SPECIFIC MESSAGE ====================

    /**
     * Get the reply for a specific original message
     */
    @Query("SELECT * FROM future_message_replies WHERE originalMessageId = :messageId AND isDeleted = 0 LIMIT 1")
    suspend fun getReplyForMessage(messageId: Long): FutureMessageReplyEntity?

    @Query("SELECT * FROM future_message_replies WHERE originalMessageId = :messageId AND isDeleted = 0")
    fun observeReplyForMessage(messageId: Long): Flow<FutureMessageReplyEntity?>

    /**
     * Check if a message has been replied to
     */
    @Query("SELECT COUNT(*) FROM future_message_replies WHERE originalMessageId = :messageId AND isDeleted = 0")
    suspend fun getReplyCountForMessage(messageId: Long): Int

    suspend fun hasReplyForMessage(messageId: Long): Boolean {
        return getReplyCountForMessage(messageId) > 0
    }

    // ==================== TIME CAPSULE CHAIN ====================

    /**
     * Get the full conversation chain for a message
     * This includes the original message and all replies that created chains
     */
    @Query("""
        SELECT * FROM future_message_replies
        WHERE userId = :userId
        AND (originalMessageId = :messageId OR chainedMessageId IS NOT NULL)
        AND isDeleted = 0
        ORDER BY repliedAt ASC
    """)
    fun getConversationChain(userId: String, messageId: Long): Flow<List<FutureMessageReplyEntity>>

    /**
     * Get replies that have chained messages (started new conversations)
     */
    @Query("""
        SELECT * FROM future_message_replies
        WHERE userId = :userId
        AND chainedMessageId IS NOT NULL
        AND isDeleted = 0
        ORDER BY repliedAt DESC
    """)
    fun getRepliesWithChains(userId: String): Flow<List<FutureMessageReplyEntity>>

    /**
     * Link a chained message to a reply
     */
    @Query("UPDATE future_message_replies SET chainedMessageId = :chainedMessageId WHERE id = :replyId")
    suspend fun setChainedMessage(replyId: Long, chainedMessageId: Long)

    // ==================== JOURNAL INTEGRATION ====================

    /**
     * Mark a reply as saved to journal
     */
    @Query("UPDATE future_message_replies SET savedAsJournalId = :journalId WHERE id = :replyId")
    suspend fun setSavedAsJournal(replyId: Long, journalId: Long)

    /**
     * Get replies that were saved as journal entries
     */
    @Query("""
        SELECT * FROM future_message_replies
        WHERE userId = :userId
        AND savedAsJournalId IS NOT NULL
        AND isDeleted = 0
        ORDER BY repliedAt DESC
    """)
    fun getRepliesSavedAsJournal(userId: String): Flow<List<FutureMessageReplyEntity>>

    // ==================== STATISTICS ====================

    /**
     * Get total reply count
     */
    @Query("SELECT COUNT(*) FROM future_message_replies WHERE userId = :userId AND isDeleted = 0")
    fun getReplyCount(userId: String): Flow<Int>

    /**
     * Get count of chains started (replies that created new future messages)
     */
    @Query("SELECT COUNT(*) FROM future_message_replies WHERE userId = :userId AND chainedMessageId IS NOT NULL AND isDeleted = 0")
    suspend fun getChainsStartedCount(userId: String): Int

    /**
     * Get mood distribution from reactions
     */
    @Query("""
        SELECT reactionMood as mood, COUNT(*) as count
        FROM future_message_replies
        WHERE userId = :userId
        AND reactionMood IS NOT NULL
        AND isDeleted = 0
        GROUP BY reactionMood
    """)
    suspend fun getReactionMoodDistribution(userId: String): List<ReplyMoodCount>

    // ==================== SYNC ====================

    @Query("SELECT * FROM future_message_replies WHERE syncStatus = 'pending' AND isDeleted = 0")
    suspend fun getPendingSyncReplies(): List<FutureMessageReplyEntity>

    @Query("UPDATE future_message_replies SET syncStatus = :status, lastSyncedAt = :syncTime WHERE id = :id")
    suspend fun updateSyncStatus(id: Long, status: String, syncTime: Long = System.currentTimeMillis())

    // ==================== CLEANUP ====================

    @Query("DELETE FROM future_message_replies WHERE isDeleted = 1")
    suspend fun purgeSoftDeleted(): Int

    @Query("SELECT * FROM future_message_replies WHERE isDeleted = 1")
    suspend fun getSoftDeletedReplies(): List<FutureMessageReplyEntity>

    // ==================== BACKUP ====================

    @Query("SELECT * FROM future_message_replies ORDER BY repliedAt DESC")
    suspend fun getAllRepliesSync(): List<FutureMessageReplyEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReplies(replies: List<FutureMessageReplyEntity>)
}

/**
 * Data class for reaction mood distribution
 */
data class ReplyMoodCount(
    val mood: String?,
    val count: Int
)
