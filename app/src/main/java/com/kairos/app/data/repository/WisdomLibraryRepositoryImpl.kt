package com.kairos.app.data.repository

import com.kairos.app.data.local.dao.IdiomDao
import com.kairos.app.data.local.dao.PhraseDao
import com.kairos.app.data.local.dao.ProverbDao
import com.kairos.app.data.local.dao.QuoteDao
import com.kairos.app.data.local.entity.IdiomEntity
import com.kairos.app.data.local.entity.PhraseEntity
import com.kairos.app.data.local.entity.ProverbEntity
import com.kairos.app.data.local.entity.QuoteEntity
import com.kairos.app.domain.common.ErrorType
import com.kairos.app.domain.common.Result
import com.kairos.app.domain.common.runSuspendCatching
import com.kairos.app.domain.repository.WisdomLibraryContent
import com.kairos.app.domain.repository.WisdomLibraryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WisdomLibraryRepositoryImpl @Inject constructor(
    private val quoteDao: QuoteDao,
    private val proverbDao: ProverbDao,
    private val idiomDao: IdiomDao,
    private val phraseDao: PhraseDao
) : WisdomLibraryRepository {

    override fun observeLibrary(): Flow<WisdomLibraryContent> = combine(
        quoteDao.getAllQuotes(),
        proverbDao.getAllProverbs(),
        idiomDao.getAllIdioms(),
        phraseDao.getAllPhrases()
    ) { quotes, proverbs, idioms, phrases ->
        WisdomLibraryContent(
            quotes = quotes,
            proverbs = proverbs,
            idioms = idioms,
            phrases = phrases
        )
    }

    override suspend fun setQuoteFavorite(item: QuoteEntity, favorite: Boolean): Result<Unit> =
        persistFavorite("quote") { quoteDao.updateFavoriteStatus(item.id, favorite) }

    override suspend fun setProverbFavorite(item: ProverbEntity, favorite: Boolean): Result<Unit> =
        persistFavorite("proverb") { proverbDao.updateFavoriteStatus(item.id, favorite) }

    override suspend fun setIdiomFavorite(item: IdiomEntity, favorite: Boolean): Result<Unit> =
        persistFavorite("idiom") { idiomDao.updateFavoriteStatus(item.id, favorite) }

    override suspend fun setPhraseFavorite(item: PhraseEntity, favorite: Boolean): Result<Unit> =
        persistFavorite("phrase") { phraseDao.updateFavoriteStatus(item.id, favorite) }

    private suspend fun persistFavorite(
        contentType: String,
        update: suspend () -> Unit
    ): Result<Unit> = runSuspendCatching(
        errorType = ErrorType.DATABASE,
        errorMessage = "Could not update the $contentType."
    ) {
        update()
    }
}
