package com.prody.prashant.data.repository

import com.prody.prashant.data.local.dao.FutureMessageDao
import com.prody.prashant.data.local.dao.JournalDao
import com.prody.prashant.data.local.dao.QuoteDao
import com.prody.prashant.data.local.dao.VocabularyDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Sealed class representing different types of search results.
 */
sealed class SearchResult {
    abstract val id: Long
    abstract val title: String
    abstract val subtitle: String
    abstract val timestamp: Long
    abstract val category: SearchCategory

    data class JournalResult(
        override val id: Long,
        override val title: String,
        override val subtitle: String,
        override val timestamp: Long,
        val mood: String?,
        val tags: String?
    ) : SearchResult() {
        override val category = SearchCategory.JOURNAL
    }

    data class QuoteResult(
        override val id: Long,
        override val title: String, // The quote content
        override val subtitle: String, // The author
        override val timestamp: Long,
        val quoteCategory: String?
    ) : SearchResult() {
        override val category = SearchCategory.QUOTES
    }

    data class VocabularyResult(
        override val id: Long,
        override val title: String, // The word
        override val subtitle: String, // The definition
        override val timestamp: Long,
        val partOfSpeech: String?,
        val isLearned: Boolean
    ) : SearchResult() {
        override val category = SearchCategory.VOCABULARY
    }

    data class FutureMessageResult(
        override val id: Long,
        override val title: String,
        override val subtitle: String,
        override val timestamp: Long,
        val deliveryDate: Long,
        val isDelivered: Boolean
    ) : SearchResult() {
        override val category = SearchCategory.FUTURE_MESSAGES
    }
}

enum class SearchCategory(val displayName: String) {
    ALL("All"),
    JOURNAL("Journal"),
    QUOTES("Quotes"),
    VOCABULARY("Vocabulary"),
    FUTURE_MESSAGES("Time Capsule")
}

/**
 * Repository for unified search across all content types.
 */
@Singleton
class SearchRepository @Inject constructor(
    private val journalDao: JournalDao,
    private val quoteDao: QuoteDao,
    private val vocabularyDao: VocabularyDao,
    private val futureMessageDao: FutureMessageDao
) {
    /**
     * Search across all content types.
     * @param query The search query string
     * @param category Optional category to filter results
     * @return Flow of combined search results
     */
    fun search(
        query: String,
        category: SearchCategory = SearchCategory.ALL
    ): Flow<List<SearchResult>> {
        if (query.isBlank()) {
            return flowOf(emptyList())
        }

        val trimmedQuery = query.trim()

        return when (category) {
            SearchCategory.ALL -> searchAll(trimmedQuery)
            SearchCategory.JOURNAL -> searchJournal(trimmedQuery)
            SearchCategory.QUOTES -> searchQuotes(trimmedQuery)
            SearchCategory.VOCABULARY -> searchVocabulary(trimmedQuery)
            SearchCategory.FUTURE_MESSAGES -> searchFutureMessages(trimmedQuery)
        }
    }

    private fun searchAll(query: String): Flow<List<SearchResult>> {
        return combine(
            searchJournal(query),
            searchQuotes(query),
            searchVocabulary(query),
            searchFutureMessages(query)
        ) { journal, quotes, vocabulary, futureMessages ->
            (journal + quotes + vocabulary + futureMessages)
                .sortedByDescending { it.timestamp }
        }
    }

    private fun searchJournal(query: String): Flow<List<SearchResult>> {
        return journalDao.searchEntries(query).map { entries ->
            entries.map { entry ->
                SearchResult.JournalResult(
                    id = entry.id,
                    title = entry.title.ifBlank {
                        entry.content.take(50) + if (entry.content.length > 50) "..." else ""
                    },
                    subtitle = entry.content.take(100) + if (entry.content.length > 100) "..." else "",
                    timestamp = entry.createdAt,
                    mood = entry.mood,
                    tags = entry.tags
                )
            }
        }
    }

    private fun searchQuotes(query: String): Flow<List<SearchResult>> {
        return quoteDao.searchQuotes(query).map { quotes ->
            quotes.map { quote ->
                SearchResult.QuoteResult(
                    id = quote.id,
                    title = quote.content.take(80) + if (quote.content.length > 80) "..." else "",
                    subtitle = quote.author,
                    timestamp = quote.shownAt ?: 0L,
                    quoteCategory = quote.category
                )
            }
        }
    }

    private fun searchVocabulary(query: String): Flow<List<SearchResult>> {
        return vocabularyDao.searchVocabulary(query).map { words ->
            words.map { word ->
                SearchResult.VocabularyResult(
                    id = word.id,
                    title = word.word,
                    subtitle = word.definition.take(100) + if (word.definition.length > 100) "..." else "",
                    timestamp = word.learnedAt ?: word.shownAt ?: 0L,
                    partOfSpeech = word.partOfSpeech,
                    isLearned = word.isLearned
                )
            }
        }
    }

    private fun searchFutureMessages(query: String): Flow<List<SearchResult>> {
        return futureMessageDao.searchMessages(query).map { messages ->
            messages.map { message ->
                SearchResult.FutureMessageResult(
                    id = message.id,
                    title = message.title.ifBlank {
                        message.content.take(50) + if (message.content.length > 50) "..." else ""
                    },
                    subtitle = message.content.take(100) + if (message.content.length > 100) "..." else "",
                    timestamp = message.createdAt,
                    deliveryDate = message.deliveryDate,
                    isDelivered = message.isDelivered
                )
            }
        }
    }

    /**
     * Get recent search suggestions (top content from each category).
     */
    fun getRecentContent(): Flow<List<SearchResult>> {
        return combine(
            journalDao.getRecentEntries(3).map { entries ->
                entries.map { entry ->
                    SearchResult.JournalResult(
                        id = entry.id,
                        title = entry.title.ifBlank {
                            entry.content.take(50) + if (entry.content.length > 50) "..." else ""
                        },
                        subtitle = entry.content.take(100),
                        timestamp = entry.createdAt,
                        mood = entry.mood,
                        tags = entry.tags
                    )
                }
            },
            quoteDao.getFavoriteQuotes().map { quotes ->
                quotes.take(3).map { quote ->
                    SearchResult.QuoteResult(
                        id = quote.id,
                        title = quote.content.take(80),
                        subtitle = quote.author,
                        timestamp = quote.shownAt ?: 0L,
                        quoteCategory = quote.category
                    )
                }
            },
            vocabularyDao.getLearnedWords().map { words ->
                words.take(3).map { word ->
                    SearchResult.VocabularyResult(
                        id = word.id,
                        title = word.word,
                        subtitle = word.definition.take(100),
                        timestamp = word.learnedAt ?: 0L,
                        partOfSpeech = word.partOfSpeech,
                        isLearned = word.isLearned
                    )
                }
            }
        ) { journal, quotes, vocabulary ->
            (journal + quotes + vocabulary).sortedByDescending { it.timestamp }
        }
    }
}
