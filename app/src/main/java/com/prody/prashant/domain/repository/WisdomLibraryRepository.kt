package com.prody.prashant.domain.repository

import com.prody.prashant.data.local.entity.IdiomEntity
import com.prody.prashant.data.local.entity.PhraseEntity
import com.prody.prashant.data.local.entity.ProverbEntity
import com.prody.prashant.data.local.entity.QuoteEntity
import com.prody.prashant.domain.common.Result
import kotlinx.coroutines.flow.Flow

data class WisdomLibraryContent(
    val quotes: List<QuoteEntity>,
    val proverbs: List<ProverbEntity>,
    val idioms: List<IdiomEntity>,
    val phrases: List<PhraseEntity>
)

/**
 * Single boundary for the read-only language and wisdom catalog.
 * UI state holders no longer coordinate four Room DAOs independently.
 */
interface WisdomLibraryRepository {
    fun observeLibrary(): Flow<WisdomLibraryContent>

    suspend fun setQuoteFavorite(item: QuoteEntity, favorite: Boolean): Result<Unit>
    suspend fun setProverbFavorite(item: ProverbEntity, favorite: Boolean): Result<Unit>
    suspend fun setIdiomFavorite(item: IdiomEntity, favorite: Boolean): Result<Unit>
    suspend fun setPhraseFavorite(item: PhraseEntity, favorite: Boolean): Result<Unit>
}
