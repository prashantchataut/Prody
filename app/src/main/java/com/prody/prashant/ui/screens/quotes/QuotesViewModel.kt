package com.prody.prashant.ui.screens.quotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.dao.IdiomDao
import com.prody.prashant.data.local.dao.PhraseDao
import com.prody.prashant.data.local.dao.ProverbDao
import com.prody.prashant.data.local.dao.QuoteDao
import com.prody.prashant.data.local.entity.IdiomEntity
import com.prody.prashant.data.local.entity.PhraseEntity
import com.prody.prashant.data.local.entity.ProverbEntity
import com.prody.prashant.data.local.entity.QuoteEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuotesUiState(
    val quotes: List<QuoteEntity> = emptyList(),
    val proverbs: List<ProverbEntity> = emptyList(),
    val idioms: List<IdiomEntity> = emptyList(),
    val phrases: List<PhraseEntity> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class QuotesViewModel @Inject constructor(
    private val quoteDao: QuoteDao,
    private val proverbDao: ProverbDao,
    private val idiomDao: IdiomDao,
    private val phraseDao: PhraseDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuotesUiState())
    val uiState: StateFlow<QuotesUiState> = _uiState.asStateFlow()

    init {
        loadAllContent()
    }

    private fun loadAllContent() {
        viewModelScope.launch {
            // Load quotes
            launch {
                quoteDao.getAllQuotes().collect { quotes ->
                    _uiState.update { it.copy(quotes = quotes) }
                }
            }

            // Load proverbs
            launch {
                proverbDao.getAllProverbs().collect { proverbs ->
                    _uiState.update { it.copy(proverbs = proverbs) }
                }
            }

            // Load idioms
            launch {
                idiomDao.getAllIdioms().collect { idioms ->
                    _uiState.update { it.copy(idioms = idioms) }
                }
            }

            // Load phrases
            launch {
                phraseDao.getAllPhrases().collect { phrases ->
                    _uiState.update { it.copy(phrases = phrases, isLoading = false) }
                }
            }
        }
    }

    fun toggleQuoteFavorite(quote: QuoteEntity) {
        viewModelScope.launch {
            quoteDao.updateQuote(quote.copy(isFavorite = !quote.isFavorite))
        }
    }

    fun toggleProverbFavorite(proverb: ProverbEntity) {
        viewModelScope.launch {
            proverbDao.updateProverb(proverb.copy(isFavorite = !proverb.isFavorite))
        }
    }

    fun toggleIdiomFavorite(idiom: IdiomEntity) {
        viewModelScope.launch {
            idiomDao.updateIdiom(idiom.copy(isFavorite = !idiom.isFavorite))
        }
    }

    fun togglePhraseFavorite(phrase: PhraseEntity) {
        viewModelScope.launch {
            phraseDao.updatePhrase(phrase.copy(isFavorite = !phrase.isFavorite))
        }
    }
}
