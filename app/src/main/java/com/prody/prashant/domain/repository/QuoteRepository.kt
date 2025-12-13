package com.prody.prashant.domain.repository

import com.prody.prashant.data.local.entity.QuoteEntity
import com.prody.prashant.domain.common.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for quote operations.
 * Provides abstraction layer between ViewModels and data sources.
 */
interface QuoteRepository {
    /**
     * Get all quotes.
     */
    fun getAllQuotes(): Flow<List<QuoteEntity>>

    /**
     * Get a quote by ID.
     */
    suspend fun getQuoteById(id: Long): Result<QuoteEntity>

    /**
     * Get quote of the day.
     */
    suspend fun getQuoteOfTheDay(): Result<QuoteEntity>

    /**
     * Get favorite quotes.
     */
    fun getFavoriteQuotes(): Flow<List<QuoteEntity>>

    /**
     * Get quotes by category.
     */
    fun getQuotesByCategory(category: String): Flow<List<QuoteEntity>>

    /**
     * Get quotes by author.
     */
    fun getQuotesByAuthor(author: String): Flow<List<QuoteEntity>>

    /**
     * Search quotes.
     */
    fun searchQuotes(query: String): Flow<List<QuoteEntity>>

    /**
     * Get all distinct categories.
     */
    fun getAllCategories(): Flow<List<String>>

    /**
     * Get all distinct authors.
     */
    fun getAllAuthors(): Flow<List<String>>

    /**
     * Insert a new quote.
     */
    suspend fun insertQuote(quote: QuoteEntity): Result<Long>

    /**
     * Insert multiple quotes.
     */
    suspend fun insertQuotes(quotes: List<QuoteEntity>): Result<Unit>

    /**
     * Update a quote.
     */
    suspend fun updateQuote(quote: QuoteEntity): Result<Unit>

    /**
     * Delete a quote.
     */
    suspend fun deleteQuote(quote: QuoteEntity): Result<Unit>

    /**
     * Update favorite status.
     */
    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean): Result<Unit>

    /**
     * Mark quote as shown as daily.
     */
    suspend fun markAsShownDaily(id: Long): Result<Unit>

    /**
     * Get random quote for sharing.
     */
    suspend fun getRandomQuote(): Result<QuoteEntity>

    /**
     * Get quotes with reflection prompts.
     */
    fun getQuotesWithReflectionPrompts(): Flow<List<QuoteEntity>>
}
