package com.kairos.app.domain.repository

import com.kairos.app.data.local.entity.FutureMessageEntity
import com.kairos.app.domain.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Local-first boundary for sealed future messages.
 *
 * UI code must not coordinate Room state directly. This repository keeps
 * delivery, reveal, favorites, and reply linkage consistent across screens.
 */
interface FutureMessageRepository {
    fun observeDeliveredMessages(): Flow<List<FutureMessageEntity>>
    fun observePendingMessages(): Flow<List<FutureMessageEntity>>
    fun observeUnreadCount(): Flow<Int>

    suspend fun getMessage(id: Long): Result<FutureMessageEntity>
    suspend fun getMessagesReadyForDelivery(now: Long): Result<List<FutureMessageEntity>>
    suspend fun createMessage(message: FutureMessageEntity): Result<Long>
    suspend fun markDelivered(id: Long, deliveredAt: Long = System.currentTimeMillis()): Result<Unit>
    suspend fun markRead(id: Long): Result<Unit>
    suspend fun markRevealed(id: Long, readAt: Long = System.currentTimeMillis()): Result<Unit>
    suspend fun setFavorite(id: Long, isFavorite: Boolean): Result<Unit>
    suspend fun linkReplyJournal(id: Long, journalId: Long): Result<Unit>
}
