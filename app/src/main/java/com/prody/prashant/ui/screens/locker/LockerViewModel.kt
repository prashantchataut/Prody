package com.prody.prashant.ui.screens.locker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.dao.EvidenceRarityCount
import com.prody.prashant.data.local.dao.EvidenceTypeCount
import com.prody.prashant.data.local.entity.EvidenceEntity
import com.prody.prashant.data.local.entity.EvidenceType
import com.prody.prashant.domain.repository.EvidenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for THE LOCKER screen.
 *
 * Manages evidence display, filtering, and statistics for the
 * Evidence Locker feature that replaces traditional XP/plants.
 */
@HiltViewModel
class LockerViewModel @Inject constructor(
    private val evidenceRepository: EvidenceRepository
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow<EvidenceFilter>(EvidenceFilter.All)
    val selectedFilter: StateFlow<EvidenceFilter> = _selectedFilter.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedEvidence = MutableStateFlow<EvidenceEntity?>(null)
    val selectedEvidence: StateFlow<EvidenceEntity?> = _selectedEvidence.asStateFlow()

    private val _typeCounts = MutableStateFlow<List<EvidenceTypeCount>>(emptyList())
    val typeCounts: StateFlow<List<EvidenceTypeCount>> = _typeCounts.asStateFlow()

    private val _rarityCounts = MutableStateFlow<List<EvidenceRarityCount>>(emptyList())
    val rarityCounts: StateFlow<List<EvidenceRarityCount>> = _rarityCounts.asStateFlow()

    // All evidence flow
    private val allEvidence = evidenceRepository.getAllEvidence()

    // Filtered evidence based on selected filter
    val evidence: StateFlow<List<EvidenceEntity>> = combine(
        allEvidence,
        _selectedFilter
    ) { all, filter ->
        when (filter) {
            EvidenceFilter.All -> all
            EvidenceFilter.Receipt -> all.filter { it.evidenceType == EvidenceType.RECEIPT }
            EvidenceFilter.Witness -> all.filter { it.evidenceType == EvidenceType.WITNESS }
            EvidenceFilter.Prophecy -> all.filter { it.evidenceType == EvidenceType.PROPHECY }
            EvidenceFilter.Breakthrough -> all.filter { it.evidenceType == EvidenceType.BREAKTHROUGH }
            EvidenceFilter.Streak -> all.filter { it.evidenceType == EvidenceType.STREAK }
            EvidenceFilter.Pinned -> all.filter { it.isPinned }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Total count
    val totalCount = evidenceRepository.getTotalEvidenceCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Unviewed count for badge
    val unviewedCount = evidenceRepository.getUnviewedEvidenceCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        loadStatistics()
    }

    private fun loadStatistics() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _typeCounts.value = evidenceRepository.getEvidenceCountByType()
                _rarityCounts.value = evidenceRepository.getRarityDistribution()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setFilter(filter: EvidenceFilter) {
        _selectedFilter.value = filter
    }

    fun selectEvidence(evidence: EvidenceEntity) {
        _selectedEvidence.value = evidence
        // Mark as viewed
        viewModelScope.launch {
            if (!evidence.isViewed) {
                evidenceRepository.markAsViewed(evidence.id)
            }
        }
    }

    fun clearSelection() {
        _selectedEvidence.value = null
    }

    fun togglePin(evidence: EvidenceEntity) {
        viewModelScope.launch {
            evidenceRepository.setPinned(evidence.id, !evidence.isPinned)
        }
    }

    fun refresh() {
        loadStatistics()
    }
}

/**
 * Filter options for the evidence list.
 */
sealed class EvidenceFilter(val displayName: String, val emoji: String) {
    data object All : EvidenceFilter("All", "📦")
    data object Receipt : EvidenceFilter("Receipts", "🧾")
    data object Witness : EvidenceFilter("Witnesses", "👁️")
    data object Prophecy : EvidenceFilter("Prophecies", "🔮")
    data object Breakthrough : EvidenceFilter("Breakthroughs", "💡")
    data object Streak : EvidenceFilter("Streaks", "🔥")
    data object Pinned : EvidenceFilter("Pinned", "📌")

    companion object {
        val all = listOf(All, Receipt, Witness, Prophecy, Breakthrough, Streak, Pinned)
    }
}
