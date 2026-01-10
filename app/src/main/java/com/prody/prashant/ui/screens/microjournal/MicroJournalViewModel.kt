package com.prody.prashant.ui.screens.microjournal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.entity.MicroEntryEntity
import com.prody.prashant.domain.model.Mood
import com.prody.prashant.domain.repository.MicroEntryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Capture context for micro entries
 */
enum class CaptureContext(val value: String, val displayName: String) {
    QUICK_CAPTURE("quick_capture", "Quick Thought"),
    MORNING_RITUAL("morning_ritual", "Morning Ritual"),
    EVENING_RITUAL("evening_ritual", "Evening Ritual"),
    NOTIFICATION("notification", "Notification")
}

/**
 * UI State for Micro Journaling
 */
data class MicroJournalUiState(
    val isLoading: Boolean = true,
    val microEntries: List<MicroEntryEntity> = emptyList(),
    val todayEntries: List<MicroEntryEntity> = emptyList(),
    val unexpandedEntries: List<MicroEntryEntity> = emptyList(),
    val totalCount: Int = 0,
    val todayCount: Int = 0,
    val unexpandedCount: Int = 0,
    // Quick capture state
    val showQuickCapture: Boolean = false,
    val captureContent: String = "",
    val captureMood: Mood? = null,
    val captureContext: CaptureContext = CaptureContext.QUICK_CAPTURE,
    val isSaving: Boolean = false,
    val characterCount: Int = 0,
    val maxCharacters: Int = MicroEntryEntity.MAX_CONTENT_LENGTH,
    // Detail view
    val selectedEntry: MicroEntryEntity? = null,
    val showDetailSheet: Boolean = false,
    // Expansion
    val entryToExpand: MicroEntryEntity? = null,
    val showExpandConfirmation: Boolean = false,
    // Error handling
    val errorMessage: String? = null,
    val successMessage: String? = null,
    // Mood distribution stats
    val moodDistribution: Map<String, Int> = emptyMap()
)

@HiltViewModel
class MicroJournalViewModel @Inject constructor(
    private val microEntryRepository: MicroEntryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MicroJournalUiState())
    val uiState: StateFlow<MicroJournalUiState> = _uiState.asStateFlow()

    private val userId = "local"

    init {
        loadMicroEntries()
        loadTodayEntries()
        loadUnexpandedEntries()
        loadStatistics()
    }

    private fun loadMicroEntries() {
        viewModelScope.launch {
            microEntryRepository.getRecentMicroEntries(userId, 50)
                .catch { e ->
                    _uiState.update { it.copy(
                        isLoading = false,
                        errorMessage = "Failed to load micro entries"
                    )}
                }
                .collect { entries ->
                    _uiState.update { it.copy(
                        isLoading = false,
                        microEntries = entries,
                        totalCount = entries.size
                    )}
                }
        }
    }

    private fun loadTodayEntries() {
        viewModelScope.launch {
            microEntryRepository.getTodayMicroEntries(userId)
                .collect { entries ->
                    _uiState.update { it.copy(
                        todayEntries = entries,
                        todayCount = entries.size
                    )}
                }
        }
    }

    private fun loadUnexpandedEntries() {
        viewModelScope.launch {
            microEntryRepository.getUnexpandedMicroEntries(userId)
                .collect { entries ->
                    _uiState.update { it.copy(
                        unexpandedEntries = entries,
                        unexpandedCount = entries.size
                    )}
                }
        }
    }

    private fun loadStatistics() {
        viewModelScope.launch {
            try {
                val moodDist = microEntryRepository.getMoodDistribution(userId)
                _uiState.update { it.copy(moodDistribution = moodDist) }
            } catch (e: Exception) {
                // Non-critical, ignore
            }
        }
    }

    // ==================== QUICK CAPTURE ====================

    fun showQuickCapture(context: CaptureContext = CaptureContext.QUICK_CAPTURE) {
        _uiState.update { it.copy(
            showQuickCapture = true,
            captureContext = context,
            captureContent = "",
            captureMood = null,
            characterCount = 0
        )}
    }

    fun hideQuickCapture() {
        _uiState.update { it.copy(
            showQuickCapture = false,
            captureContent = "",
            captureMood = null,
            characterCount = 0
        )}
    }

    fun onCaptureContentChanged(content: String) {
        if (content.length <= _uiState.value.maxCharacters) {
            _uiState.update { it.copy(
                captureContent = content,
                characterCount = content.length
            )}
        }
    }

    fun onCaptureMoodSelected(mood: Mood?) {
        _uiState.update { it.copy(captureMood = mood) }
    }

    fun saveQuickCapture() {
        val state = _uiState.value
        if (state.captureContent.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val entry = MicroEntryEntity(
                userId = userId,
                content = state.captureContent.trim(),
                mood = state.captureMood?.name,
                moodIntensity = null,
                captureContext = state.captureContext.value,
                createdAt = System.currentTimeMillis()
            )

            microEntryRepository.createMicroEntry(entry)
                .onSuccess {
                    _uiState.update { it.copy(
                        isSaving = false,
                        showQuickCapture = false,
                        captureContent = "",
                        captureMood = null,
                        characterCount = 0,
                        successMessage = "Thought captured!"
                    )}
                }
                .onError { error ->
                    _uiState.update { it.copy(
                        isSaving = false,
                        errorMessage = error.userMessage
                    )}
                }
        }
    }

    // ==================== ENTRY DETAIL ====================

    fun onEntrySelected(entry: MicroEntryEntity) {
        _uiState.update { it.copy(
            selectedEntry = entry,
            showDetailSheet = true
        )}
    }

    fun dismissDetailSheet() {
        _uiState.update { it.copy(
            selectedEntry = null,
            showDetailSheet = false
        )}
    }

    fun deleteEntry(entry: MicroEntryEntity) {
        viewModelScope.launch {
            microEntryRepository.softDeleteMicroEntry(entry.id)
                .onSuccess {
                    _uiState.update { it.copy(
                        showDetailSheet = false,
                        selectedEntry = null,
                        successMessage = "Entry deleted"
                    )}
                }
                .onError { error ->
                    _uiState.update { it.copy(errorMessage = error.userMessage) }
                }
        }
    }

    // ==================== EXPANSION ====================

    fun showExpandConfirmation(entry: MicroEntryEntity) {
        _uiState.update { it.copy(
            entryToExpand = entry,
            showExpandConfirmation = true
        )}
    }

    fun dismissExpandConfirmation() {
        _uiState.update { it.copy(
            entryToExpand = null,
            showExpandConfirmation = false
        )}
    }

    /**
     * Returns the entry to expand and its pre-filled content.
     * The actual navigation and journal creation is handled by the UI.
     */
    fun getExpansionContent(): Pair<MicroEntryEntity, String>? {
        val entry = _uiState.value.entryToExpand ?: return null

        val prefilledContent = buildString {
            appendLine(entry.content)
            appendLine()
            appendLine("---")
            appendLine("*Expanded from a quick thought*")
        }

        return Pair(entry, prefilledContent)
    }

    /**
     * Called after successful expansion to mark the micro entry as expanded.
     */
    fun markAsExpanded(microEntryId: Long, journalEntryId: Long) {
        viewModelScope.launch {
            microEntryRepository.markAsExpanded(microEntryId, journalEntryId)
            _uiState.update { it.copy(
                showExpandConfirmation = false,
                entryToExpand = null,
                showDetailSheet = false,
                selectedEntry = null,
                successMessage = "Expanded to full journal entry"
            )}
        }
    }

    // ==================== ERROR HANDLING ====================

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun dismissSuccess() {
        _uiState.update { it.copy(successMessage = null) }
    }

    // ==================== REFRESH ====================

    fun refresh() {
        _uiState.update { it.copy(isLoading = true) }
        loadMicroEntries()
        loadTodayEntries()
        loadUnexpandedEntries()
        loadStatistics()
    }
}
