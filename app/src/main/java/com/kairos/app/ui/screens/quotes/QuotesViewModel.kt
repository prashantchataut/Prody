package com.kairos.app.ui.screens.quotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kairos.app.data.ai.BuddhaAiRepository
import com.kairos.app.data.ai.QuoteExplanationResult
import com.kairos.app.data.local.entity.IdiomEntity
import com.kairos.app.data.local.entity.PhraseEntity
import com.kairos.app.data.local.entity.ProverbEntity
import com.kairos.app.data.local.entity.QuoteEntity
import com.kairos.app.data.onboarding.AiHint
import com.kairos.app.data.onboarding.AiHintType
import com.kairos.app.data.onboarding.AiOnboardingManager
import com.kairos.app.domain.repository.WisdomLibraryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.Job
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
    private val wisdomLibraryRepository: WisdomLibraryRepository,
    private val buddhaAiRepository: BuddhaAiRepository,
    private val aiOnboardingManager: AiOnboardingManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuotesUiState())
    private var libraryJob: Job? = null
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
        libraryJob?.cancel()
        libraryJob = viewModelScope.launch {
            wisdomLibraryRepository.observeLibrary()
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "The library could not be loaded."
                        )
                    }
                }
                .collect { library ->
                    _uiState.update {
                        it.copy(
                            quotes = library.quotes,
                            proverbs = library.proverbs,
                            idioms = library.idioms,
                            phrases = library.phrases,
                            isLoading = false,
                            error = null,
                            loadedCount = 4
                        )
                    }
                }
        }
    }

    fun toggleQuoteFavorite(quote: QuoteEntity) {
        viewModelScope.launch {
            wisdomLibraryRepository.setQuoteFavorite(quote, !quote.isFavorite)
                .onError { error -> _uiState.update { it.copy(error = error.userMessage) } }
        }
    }

    fun toggleProverbFavorite(proverb: ProverbEntity) {
        viewModelScope.launch {
            wisdomLibraryRepository.setProverbFavorite(proverb, !proverb.isFavorite)
                .onError { error -> _uiState.update { it.copy(error = error.userMessage) } }
        }
    }

    fun toggleIdiomFavorite(idiom: IdiomEntity) {
        viewModelScope.launch {
            wisdomLibraryRepository.setIdiomFavorite(idiom, !idiom.isFavorite)
                .onError { error -> _uiState.update { it.copy(error = error.userMessage) } }
        }
    }

    fun togglePhraseFavorite(phrase: PhraseEntity) {
        viewModelScope.launch {
            wisdomLibraryRepository.setPhraseFavorite(phrase, !phrase.isFavorite)
                .onError { error -> _uiState.update { it.copy(error = error.userMessage) } }
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
                android.util.Log.e("QuotesViewModel", "Error loading quote explanation", e)
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
