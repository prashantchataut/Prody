package com.prody.prashant.ui.screens.quotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.ai.BuddhaAiRepository
import com.prody.prashant.data.ai.BuddhaAiResult
import com.prody.prashant.data.ai.QuoteExplanationResult
import com.prody.prashant.data.local.dao.IdiomDao
import com.prody.prashant.data.local.dao.PhraseDao
import com.prody.prashant.data.local.dao.ProverbDao
import com.prody.prashant.data.local.dao.QuoteDao
import com.prody.prashant.data.local.entity.IdiomEntity
import com.prody.prashant.data.local.entity.PhraseEntity
import com.prody.prashant.data.local.entity.ProverbEntity
import com.prody.prashant.data.local.entity.QuoteEntity
import com.prody.prashant.data.onboarding.AiHint
import com.prody.prashant.data.onboarding.AiHintType
import com.prody.prashant.data.onboarding.AiOnboardingManager
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
    val isLoading: Boolean = true,
    val error: String? = null,
    val loadedCount: Int = 0,
    // AI-generated quote explanations (keyed by quote ID)
    val quoteExplanations: Map<Long, QuoteExplanationResult> = emptyMap(),
    val loadingExplanations: Set<Long> = emptySet(),
    // Failed explanation attempts (keyed by quote ID) - for showing retry option
    val failedExplanations: Set<Long> = emptySet(),
    // Onboarding hint for first quote explanation
    val showQuoteExplanationHint: Boolean = false
)

@HiltViewModel
class QuotesViewModel @Inject constructor(
    private val quoteDao: QuoteDao,
    private val proverbDao: ProverbDao,
    private val idiomDao: IdiomDao,
    private val phraseDao: PhraseDao,
    private val buddhaAiRepository: BuddhaAiRepository,
    private val aiOnboardingManager: AiOnboardingManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuotesUiState())
    val uiState: StateFlow<QuotesUiState> = _uiState.asStateFlow()

    init {
        loadAllContent()
        checkOnboarding()
    }

    private fun checkOnboarding() {
        viewModelScope.launch {
            aiOnboardingManager.shouldShowHint(AiHintType.FIRST_QUOTE_EXPLANATION).collect { shouldShow ->
                _uiState.update { state ->
                    state.copy(showQuoteExplanationHint = shouldShow)
                }
            }
        }
    }

    fun onQuoteExplanationHintDismiss() {
        viewModelScope.launch {
            aiOnboardingManager.markHintShown(AiHintType.FIRST_QUOTE_EXPLANATION)
            _uiState.update { it.copy(showQuoteExplanationHint = false) }
        }
    }

    fun getQuoteExplanationHint(): AiHint {
        return aiOnboardingManager.getHintContent(AiHintType.FIRST_QUOTE_EXPLANATION)
    }

    private fun loadAllContent() {
        viewModelScope.launch {
            // Load quotes
            launch {
                try {
                    quoteDao.getAllQuotes().collect { quotes ->
                        _uiState.update { state ->
                            val newCount = state.loadedCount + 1
                            state.copy(
                                quotes = quotes,
                                loadedCount = newCount,
                                isLoading = newCount < 4
                            )
                        }
                    }
                } catch (e: Exception) {
                    _uiState.update { it.copy(error = "Failed to load quotes") }
                }
            }

            // Load proverbs
            launch {
                try {
                    proverbDao.getAllProverbs().collect { proverbs ->
                        _uiState.update { state ->
                            val newCount = state.loadedCount + 1
                            state.copy(
                                proverbs = proverbs,
                                loadedCount = newCount,
                                isLoading = newCount < 4
                            )
                        }
                    }
                } catch (e: Exception) {
                    _uiState.update { it.copy(error = "Failed to load proverbs") }
                }
            }

            // Load idioms
            launch {
                try {
                    idiomDao.getAllIdioms().collect { idioms ->
                        _uiState.update { state ->
                            val newCount = state.loadedCount + 1
                            state.copy(
                                idioms = idioms,
                                loadedCount = newCount,
                                isLoading = newCount < 4
                            )
                        }
                    }
                } catch (e: Exception) {
                    _uiState.update { it.copy(error = "Failed to load idioms") }
                }
            }

            // Load phrases
            launch {
                try {
                    phraseDao.getAllPhrases().collect { phrases ->
                        _uiState.update { state ->
                            val newCount = state.loadedCount + 1
                            state.copy(
                                phrases = phrases,
                                loadedCount = newCount,
                                isLoading = newCount < 4
                            )
                        }
                    }
                } catch (e: Exception) {
                    _uiState.update { it.copy(error = "Failed to load phrases") }
                }
            }
        }
    }

    fun toggleQuoteFavorite(quote: QuoteEntity) {
        viewModelScope.launch {
            try {
                quoteDao.updateQuote(quote.copy(isFavorite = !quote.isFavorite))
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to update quote favorite") }
            }
        }
    }

    fun toggleProverbFavorite(proverb: ProverbEntity) {
        viewModelScope.launch {
            try {
                proverbDao.updateProverb(proverb.copy(isFavorite = !proverb.isFavorite))
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to update proverb favorite") }
            }
        }
    }

    fun toggleIdiomFavorite(idiom: IdiomEntity) {
        viewModelScope.launch {
            try {
                idiomDao.updateIdiom(idiom.copy(isFavorite = !idiom.isFavorite))
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to update idiom favorite") }
            }
        }
    }

    fun togglePhraseFavorite(phrase: PhraseEntity) {
        viewModelScope.launch {
            try {
                phraseDao.updatePhrase(phrase.copy(isFavorite = !phrase.isFavorite))
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to update phrase favorite") }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun retry() {
        _uiState.update { it.copy(isLoading = true, error = null, loadedCount = 0) }
        loadAllContent()
    }

    /**
     * Load AI-generated explanation for a quote.
     * Shows "Meaning" and "Try this today" suggestions.
     * Results are cached for 7 days.
     */
    fun loadQuoteExplanation(quote: QuoteEntity) {
        // Skip if already loaded or loading
        if (_uiState.value.quoteExplanations.containsKey(quote.id) ||
            _uiState.value.loadingExplanations.contains(quote.id)) {
            return
        }

        viewModelScope.launch {
            try {
                // Mark as loading, clear any previous failure state
                _uiState.update { state ->
                    state.copy(
                        loadingExplanations = state.loadingExplanations + quote.id,
                        failedExplanations = state.failedExplanations - quote.id
                    )
                }

                val result = buddhaAiRepository.getQuoteExplanation(
                    quote = quote.content,
                    author = quote.author
                )

                val explanation = result.getOrNull()
                if (explanation != null) {
                    _uiState.update { state ->
                        state.copy(
                            quoteExplanations = state.quoteExplanations + (quote.id to explanation),
                            loadingExplanations = state.loadingExplanations - quote.id,
                            failedExplanations = state.failedExplanations - quote.id
                        )
                    }
                } else {
                    // Mark as failed so UI can show retry option
                    _uiState.update { state ->
                        state.copy(
                            loadingExplanations = state.loadingExplanations - quote.id,
                            failedExplanations = state.failedExplanations + quote.id
                        )
                    }
                }
            } catch (e: Exception) {
                com.prody.prashant.util.AppLogger.e("QuotesViewModel", "Error loading quote explanation", e)
                // Mark as failed so UI can show retry option
                _uiState.update { state ->
                    state.copy(
                        loadingExplanations = state.loadingExplanations - quote.id,
                        failedExplanations = state.failedExplanations + quote.id
                    )
                }
            }
        }
    }

    /**
     * Check if explanation loading failed for a quote.
     */
    fun isExplanationFailed(quoteId: Long): Boolean {
        return _uiState.value.failedExplanations.contains(quoteId)
    }

    /**
     * Retry loading explanation for a quote that previously failed.
     */
    fun retryExplanation(quote: QuoteEntity) {
        // Clear failure state and retry
        _uiState.update { state ->
            state.copy(failedExplanations = state.failedExplanations - quote.id)
        }
        loadQuoteExplanation(quote)
    }

    /**
     * Get explanation for a quote if available.
     */
    fun getQuoteExplanation(quoteId: Long): QuoteExplanationResult? {
        return _uiState.value.quoteExplanations[quoteId]
    }

    /**
     * Check if explanation is currently loading for a quote.
     */
    fun isExplanationLoading(quoteId: Long): Boolean {
        return _uiState.value.loadingExplanations.contains(quoteId)
    }
}
