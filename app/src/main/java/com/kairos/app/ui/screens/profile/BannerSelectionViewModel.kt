package com.kairos.app.ui.screens.profile

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kairos.app.domain.repository.ProfileRepository
import com.kairos.app.domain.gamification.KairosProgressionPolicy
import com.kairos.app.domain.identity.CosmeticRarity
import com.kairos.app.domain.identity.KairosBanners
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** A presentation model backed by the canonical Kairos banner catalogue. */
data class BannerOption(
    val id: String,
    val name: String,
    val description: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val patternType: KairosBanners.PatternType,
    val rarity: CosmeticRarity,
    val isAnimated: Boolean,
    val isLocked: Boolean = false,
    val unlockRequirement: String? = null,
    val category: BannerCategory = BannerCategory.ESSENTIAL
)

enum class BannerCategory(val displayName: String) {
    ESSENTIAL("Essential"),
    JOURNEY("Journey"),
    ACHIEVEMENTS("Earned"),
    RARE("Rare")
}

data class BannerSelectionUiState(
    val currentBannerId: String = "default_dawn",
    val selectedBannerId: String = "default_dawn",
    val banners: List<BannerOption> = emptyList(),
    val selectedCategory: BannerCategory = BannerCategory.ESSENTIAL,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
) {
    val hasChanges: Boolean get() = currentBannerId != selectedBannerId
    val filteredBanners: List<BannerOption>
        get() = banners.filter { it.category == selectedCategory }
}

@HiltViewModel
class BannerSelectionViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BannerSelectionUiState())
    val uiState: StateFlow<BannerSelectionUiState> = _uiState.asStateFlow()

    init {
        loadBannerOptions()
    }

    private fun loadBannerOptions() {
        viewModelScope.launch {
            runCatching {
                val profile = profileRepository.getProfile().getOrNull()
                val achievements = profileRepository.getAllAchievements().getOrDefault(emptyList())
                val unlockedAchievementIds = achievements
                    .asSequence()
                    .filter { it.isUnlocked }
                    .map { it.id }
                    .toSet()
                val points = profile?.totalPoints ?: 0
                val currentLevel = KairosProgressionPolicy.levelFor(points)
                val joinedAt = profile?.joinedAt ?: System.currentTimeMillis()
                val daysOnApp = TimeUnit.MILLISECONDS.toDays(
                    (System.currentTimeMillis() - joinedAt).coerceAtLeast(0L)
                ).toInt()

                val options = KairosBanners.allBanners.map { banner ->
                    val colors = banner.gradientColors.map(::Color)
                    val unlocked = banner.isUnlockedFor(
                        currentLevel = currentLevel,
                        daysOnApp = daysOnApp,
                        unlockedAchievementIds = unlockedAchievementIds,
                        isDevBadgeHolder = profile?.isDevBadgeHolder == true,
                        isFounder = profile?.isFounder == true,
                        isBetaTester = profile?.isBetaTester == true
                    )
                    BannerOption(
                        id = banner.id,
                        name = banner.name,
                        description = banner.description,
                        primaryColor = colors.firstOrNull() ?: Color(0xFF262936),
                        secondaryColor = colors.getOrElse(1) { colors.firstOrNull() ?: Color(0xFF50556A) },
                        patternType = banner.patternType,
                        rarity = banner.rarity,
                        isAnimated = banner.hasAnimation,
                        isLocked = !unlocked,
                        unlockRequirement = banner.unlockRequirement.takeUnless { unlocked },
                        category = when {
                            banner.isSpecial || banner.rarity.sortOrder >= CosmeticRarity.EPIC.sortOrder -> BannerCategory.RARE
                            banner.requiredAchievementId != null -> BannerCategory.ACHIEVEMENTS
                            banner.isDefault -> BannerCategory.ESSENTIAL
                            else -> BannerCategory.JOURNEY
                        }
                    )
                }

                val persistedId = profile?.bannerId
                val safeCurrent = persistedId
                    ?.takeIf { id -> options.any { it.id == id && !it.isLocked } }
                    ?: options.firstOrNull { !it.isLocked }?.id
                    ?: "default_dawn"

                Triple(options, safeCurrent, profile)
            }.onSuccess { (options, currentId, _) ->
                _uiState.update {
                    it.copy(
                        banners = options,
                        currentBannerId = currentId,
                        selectedBannerId = currentId,
                        selectedCategory = options.firstOrNull { banner -> banner.id == currentId }?.category
                            ?: BannerCategory.ESSENTIAL,
                        isLoading = false,
                        error = null
                    )
                }
            }.onFailure {
                _uiState.update { state ->
                    state.copy(isLoading = false, error = "Banners could not be loaded.")
                }
            }
        }
    }

    fun selectCategory(category: BannerCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun selectBanner(bannerId: String) {
        val banner = _uiState.value.banners.find { it.id == bannerId } ?: return
        if (!banner.isLocked) _uiState.update { it.copy(selectedBannerId = bannerId) }
    }

    fun saveBanner() {
        viewModelScope.launch {
            val state = _uiState.value
            if (!state.hasChanges) {
                _uiState.update { it.copy(isSaved = true) }
                return@launch
            }
            _uiState.update { it.copy(isSaving = true, error = null) }
            profileRepository.updateBanner(state.selectedBannerId)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            currentBannerId = state.selectedBannerId,
                            isSaving = false,
                            isSaved = true
                        )
                    }
                }
                .onError { error ->
                    _uiState.update { it.copy(isSaving = false, error = error.userMessage) }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
