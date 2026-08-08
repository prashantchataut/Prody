package com.kairos.app.data.local.database

import android.util.Log
import com.kairos.app.data.content.ExpandedQuoteContent
import com.kairos.app.data.content.ExpandedVocabularyContent
import com.kairos.app.data.local.dao.QuoteDao
import com.kairos.app.data.local.dao.VocabularyDao
import com.kairos.app.data.local.entity.QuoteEntity
import com.kairos.app.data.local.entity.VocabularyEntity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Idempotent catalog expansion for installs that predate the expanded content.
 *
 * Fresh installs receive the expanded catalog through [DatabaseSeeder]. Existing
 * installs must not be force-seeded (rows are already referenced by daily plans,
 * favorites, and learning progress), so this manager inserts only rows that are
 * genuinely missing — words matched by normalized name, quotes by normalized text.
 * Running it repeatedly is safe; there is no version marker to drift out of sync.
 */
@Singleton
class CatalogExpansionManager @Inject constructor(
    private val vocabularyDao: VocabularyDao,
    private val quoteDao: QuoteDao
) {

    companion object {
        private const val TAG = "CatalogExpansion"
        private const val BATCH_SIZE = 60
    }

    /** Expand the local catalog with any missing curated content. Call off the startup path. */
    suspend fun expandIfNeeded() {
        try {
            insertMissingVocabulary()
            insertMissingQuotes()
        } catch (e: Exception) {
            // Expansion is a quality improvement, never a launch blocker.
            Log.e(TAG, "Catalog expansion failed", e)
        }
    }

    private suspend fun insertMissingVocabulary() {
        val existing = vocabularyDao.getAllVocabularySync()
            .map { normalizeWord(it.word) }
            .toHashSet()

        val missing = ExpandedVocabularyContent.allWords
            .filter { normalizeWord(it.word) !in existing }
            .distinctBy { normalizeWord(it.word) }

        if (missing.isEmpty()) return
        Log.i(TAG, "Inserting ${missing.size} missing vocabulary rows")

        missing.chunked(BATCH_SIZE).forEach { batch ->
            vocabularyDao.insertWords(batch.map { it.copy(isLearned = false) })
        }
    }

    private suspend fun insertMissingQuotes() {
        val existing = quoteDao.getAllQuotesSync()
            .map { normalizeQuote(it.content) }
            .toHashSet()

        val missing = ExpandedQuoteContent.quotes
            .filter { normalizeQuote(it.content) !in existing }
            .distinctBy { normalizeQuote(it.content) }

        if (missing.isEmpty()) return
        Log.i(TAG, "Inserting ${missing.size} missing quotes")

        missing.chunked(BATCH_SIZE).forEach { batch ->
            quoteDao.insertQuotes(batch.map { cleanQuote(it) })
        }
    }

    private fun cleanQuote(quote: QuoteEntity): QuoteEntity = quote.copy(
        content = quote.content.trim(),
        author = quote.author.trim(),
        category = quote.category.trim().ifEmpty { "wisdom" },
        tags = quote.tags.trim()
    )

    private fun normalizeWord(word: String): String =
        word.trim().lowercase().replace(Regex("\\s+"), " ")

    private fun normalizeQuote(content: String): String =
        content.trim().lowercase().replace(Regex("\\s+"), " ")
}
