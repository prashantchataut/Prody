package com.prody.prashant.data.repository

import com.prody.prashant.data.local.dao.FutureMessageReplyDao
import com.prody.prashant.data.local.entity.FutureMessageReplyEntity
import com.prody.prashant.domain.common.ErrorType
import com.prody.prashant.domain.common.Result
import com.prody.prashant.domain.common.runSuspendCatching
import com.prody.prashant.domain.repository.FutureMessageReplyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of FutureMessageReplyRepository using Room database.
 */
@Singleton
class FutureMessageReplyRepositoryImpl @Inject constructor(
    private val futureMessageReplyDao: FutureMessageReplyDao
) : FutureMessageReplyRepository {

    // ==================== RETRIEVAL ====================

    override fun getAllReplies(userId: String): Flow<List<FutureMessageReplyEntity>> {
        return futureMessageReplyDao.getRepliesByUser(userId)
    }

    override suspend fun getReplyById(id: Long): Result<FutureMessageReplyEntity> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to load reply") {
            futureMessageReplyDao.getReplyById(id)
                ?: throw NoSuchElementException("Reply not found")
        }
    }

    override fun observeReplyById(id: Long): Flow<FutureMessageReplyEntity?> {
        return futureMessageReplyDao.observeReplyById(id)
    }

    override suspend fun getReplyForMessage(messageId: Long): Result<FutureMessageReplyEntity?> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to get reply for message") {
            futureMessageReplyDao.getReplyForMessage(messageId)
        }
    }

    override fun observeReplyForMessage(messageId: Long): Flow<FutureMessageReplyEntity?> {
        return futureMessageReplyDao.observeReplyForMessage(messageId)
    }

    override suspend fun hasReplyForMessage(messageId: Long): Boolean {
        return futureMessageReplyDao.hasReplyForMessage(messageId)
    }

    // ==================== TIME CAPSULE CHAIN ====================

    override fun getConversationChain(userId: String, messageId: Long): Flow<List<FutureMessageReplyEntity>> {
        return futureMessageReplyDao.getConversationChain(userId, messageId)
    }

    override fun getRepliesWithChains(userId: String): Flow<List<FutureMessageReplyEntity>> {
        return futureMessageReplyDao.getRepliesWithChains(userId)
    }

    override suspend fun setChainedMessage(replyId: Long, chainedMessageId: Long): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to set chained message") {
            futureMessageReplyDao.setChainedMessage(replyId, chainedMessageId)
        }
    }

    // ==================== CREATE/UPDATE ====================

    override suspend fun createReply(reply: FutureMessageReplyEntity): Result<Long> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to create reply") {
            futureMessageReplyDao.insertReply(reply)
        }
    }

    override suspend fun updateReply(reply: FutureMessageReplyEntity): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to update reply") {
            futureMessageReplyDao.updateReply(reply)
        }
    }

    // ==================== JOURNAL INTEGRATION ====================

    override suspend fun markSavedAsJournal(replyId: Long, journalId: Long): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to mark as saved to journal") {
            futureMessageReplyDao.setSavedAsJournal(replyId, journalId)
        }
    }

    override fun getRepliesSavedAsJournal(userId: String): Flow<List<FutureMessageReplyEntity>> {
        return futureMessageReplyDao.getRepliesSavedAsJournal(userId)
    }

    // ==================== DELETION ====================

    override suspend fun softDeleteReply(id: Long): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to delete reply") {
            futureMessageReplyDao.softDeleteReply(id)
        }
    }

    override suspend fun deleteReply(id: Long): Result<Unit> {
        return runSuspendCatching(ErrorType.DATABASE, "Failed to delete reply") {
            futureMessageReplyDao.deleteReplyById(id)
        }
    }

    // ==================== STATISTICS ====================

    override fun getReplyCount(userId: String): Flow<Int> {
        return futureMessageReplyDao.getReplyCount(userId)
    }

    override suspend fun getChainsStartedCount(userId: String): Int {
        return futureMessageReplyDao.getChainsStartedCount(userId)
    }

    override suspend fun getReactionMoodDistribution(userId: String): Map<String, Int> {
        val moodCounts = futureMessageReplyDao.getReactionMoodDistribution(userId)
        return moodCounts.associate { (it.mood ?: "UNKNOWN") to it.count }
    }

    // ==================== CLEANUP ====================

    override suspend fun purgeSoftDeleted(): Int {
        return futureMessageReplyDao.purgeSoftDeleted()
    }
}
