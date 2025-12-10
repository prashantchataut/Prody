package com.prody.prashant.data.local.dao

import androidx.room.*
import com.prody.prashant.data.local.entity.IdiomEntity
import com.prody.prashant.data.local.entity.PhraseEntity
import com.prody.prashant.data.local.entity.ProverbEntity
import com.prody.prashant.data.local.entity.QuoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuoteDao {

    @Query("SELECT * FROM quotes ORDER BY id ASC")
    fun getAllQuotes(): Flow<List<QuoteEntity>>

    @Query("SELECT * FROM quotes WHERE id = :id")
    suspend fun getQuoteById(id: Long): QuoteEntity?

    @Query("SELECT * FROM quotes WHERE shownAsDaily = 0 ORDER BY RANDOM() LIMIT 1")
    suspend fun getQuoteOfTheDay(): QuoteEntity?

    @Query("SELECT * FROM quotes WHERE isFavorite = 1 ORDER BY id DESC")
    fun getFavoriteQuotes(): Flow<List<QuoteEntity>>

    @Query("SELECT * FROM quotes WHERE category = :category ORDER BY RANDOM()")
    fun getQuotesByCategory(category: String): Flow<List<QuoteEntity>>

    @Query("SELECT * FROM quotes WHERE author = :author ORDER BY id ASC")
    fun getQuotesByAuthor(author: String): Flow<List<QuoteEntity>>

    @Query("SELECT * FROM quotes WHERE content LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%'")
    fun searchQuotes(query: String): Flow<List<QuoteEntity>>

    @Query("SELECT DISTINCT author FROM quotes ORDER BY author ASC")
    fun getAllAuthors(): Flow<List<String>>

    @Query("SELECT DISTINCT category FROM quotes")
    fun getAllCategories(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuote(quote: QuoteEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuotes(quotes: List<QuoteEntity>)

    @Update
    suspend fun updateQuote(quote: QuoteEntity)

    @Query("UPDATE quotes SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean)

    @Query("UPDATE quotes SET shownAsDaily = 1, shownAt = :shownAt WHERE id = :id")
    suspend fun markAsShownDaily(id: Long, shownAt: Long = System.currentTimeMillis())

    @Delete
    suspend fun deleteQuote(quote: QuoteEntity)
}

@Dao
interface ProverbDao {

    @Query("SELECT * FROM proverbs ORDER BY id ASC")
    fun getAllProverbs(): Flow<List<ProverbEntity>>

    @Query("SELECT * FROM proverbs WHERE id = :id")
    suspend fun getProverbById(id: Long): ProverbEntity?

    @Query("SELECT * FROM proverbs WHERE shownAsDaily = 0 ORDER BY RANDOM() LIMIT 1")
    suspend fun getProverbOfTheDay(): ProverbEntity?

    @Query("SELECT * FROM proverbs WHERE isFavorite = 1 ORDER BY id DESC")
    fun getFavoriteProverbs(): Flow<List<ProverbEntity>>

    @Query("SELECT * FROM proverbs WHERE category = :category ORDER BY RANDOM()")
    fun getProverbsByCategory(category: String): Flow<List<ProverbEntity>>

    @Query("SELECT * FROM proverbs WHERE origin = :origin ORDER BY id ASC")
    fun getProverbsByOrigin(origin: String): Flow<List<ProverbEntity>>

    @Query("SELECT * FROM proverbs WHERE content LIKE '%' || :query || '%' OR meaning LIKE '%' || :query || '%'")
    fun searchProverbs(query: String): Flow<List<ProverbEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProverb(proverb: ProverbEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProverbs(proverbs: List<ProverbEntity>)

    @Update
    suspend fun updateProverb(proverb: ProverbEntity)

    @Query("UPDATE proverbs SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean)

    @Query("UPDATE proverbs SET shownAsDaily = 1, shownAt = :shownAt WHERE id = :id")
    suspend fun markAsShownDaily(id: Long, shownAt: Long = System.currentTimeMillis())

    @Delete
    suspend fun deleteProverb(proverb: ProverbEntity)
}

@Dao
interface IdiomDao {

    @Query("SELECT * FROM idioms ORDER BY phrase ASC")
    fun getAllIdioms(): Flow<List<IdiomEntity>>

    @Query("SELECT * FROM idioms WHERE id = :id")
    suspend fun getIdiomById(id: Long): IdiomEntity?

    @Query("SELECT * FROM idioms WHERE shownAsDaily = 0 ORDER BY RANDOM() LIMIT 1")
    suspend fun getIdiomOfTheDay(): IdiomEntity?

    @Query("SELECT * FROM idioms WHERE isFavorite = 1 ORDER BY phrase ASC")
    fun getFavoriteIdioms(): Flow<List<IdiomEntity>>

    @Query("SELECT * FROM idioms WHERE category = :category ORDER BY phrase ASC")
    fun getIdiomsByCategory(category: String): Flow<List<IdiomEntity>>

    @Query("SELECT * FROM idioms WHERE phrase LIKE '%' || :query || '%' OR meaning LIKE '%' || :query || '%'")
    fun searchIdioms(query: String): Flow<List<IdiomEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIdiom(idiom: IdiomEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIdioms(idioms: List<IdiomEntity>)

    @Update
    suspend fun updateIdiom(idiom: IdiomEntity)

    @Query("UPDATE idioms SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean)

    @Query("UPDATE idioms SET shownAsDaily = 1, shownAt = :shownAt WHERE id = :id")
    suspend fun markAsShownDaily(id: Long, shownAt: Long = System.currentTimeMillis())

    @Delete
    suspend fun deleteIdiom(idiom: IdiomEntity)
}

@Dao
interface PhraseDao {

    @Query("SELECT * FROM phrases ORDER BY phrase ASC")
    fun getAllPhrases(): Flow<List<PhraseEntity>>

    @Query("SELECT * FROM phrases WHERE id = :id")
    suspend fun getPhraseById(id: Long): PhraseEntity?

    @Query("SELECT * FROM phrases WHERE shownAsDaily = 0 ORDER BY RANDOM() LIMIT 1")
    suspend fun getPhraseOfTheDay(): PhraseEntity?

    @Query("SELECT * FROM phrases WHERE isFavorite = 1 ORDER BY phrase ASC")
    fun getFavoritePhrases(): Flow<List<PhraseEntity>>

    @Query("SELECT * FROM phrases WHERE category = :category ORDER BY phrase ASC")
    fun getPhrasesByCategory(category: String): Flow<List<PhraseEntity>>

    @Query("SELECT * FROM phrases WHERE formality = :formality ORDER BY phrase ASC")
    fun getPhrasesByFormality(formality: String): Flow<List<PhraseEntity>>

    @Query("SELECT * FROM phrases WHERE phrase LIKE '%' || :query || '%' OR meaning LIKE '%' || :query || '%'")
    fun searchPhrases(query: String): Flow<List<PhraseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhrase(phrase: PhraseEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhrases(phrases: List<PhraseEntity>)

    @Update
    suspend fun updatePhrase(phrase: PhraseEntity)

    @Query("UPDATE phrases SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean)

    @Query("UPDATE phrases SET shownAsDaily = 1, shownAt = :shownAt WHERE id = :id")
    suspend fun markAsShownDaily(id: Long, shownAt: Long = System.currentTimeMillis())

    @Delete
    suspend fun deletePhrase(phrase: PhraseEntity)
}
