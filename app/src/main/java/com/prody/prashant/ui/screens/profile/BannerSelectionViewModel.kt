package com.prody.prashant.ui.screens.profile

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.dao.UserDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Banner option for profile customization.
 */
data class BannerOption(
    val id: String,
    val name: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val isLocked: Boolean = false,
    val unlockRequirement: String? = null,
    val category: BannerCategory = BannerCategory.NATURE
)

enum class BannerCategory(val displayName: String) {
    NATURE("Nature"),
    GRADIENT("Gradients"),
    PREMIUM("Premium"),
    ACHIEVEMENTS("Achievements")
}

data class BannerSelectionUiState(
    val currentBannerId: String = "default_dawn",
    val selectedBannerId: String = "default_dawn",
    val banners: List<BannerOption> = emptyList(),
    val selectedCategory: BannerCategory = BannerCategory.NATURE,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
) {
    val hasChanges: Boolean
        get() = currentBannerId != selectedBannerId

    val filteredBanners: List<BannerOption>
        get() = banners.filter { it.category == selectedCategory }
}

@HiltViewModel
class BannerSelectionViewModel @Inject constructor(
    private val userDao: UserDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(BannerSelectionUiState())
    val uiState: StateFlow<BannerSelectionUiState> = _uiState.asStateFlow()

    companion object {
        private const val TAG = "BannerSelectionViewModel"
    }

    init {
        loadBannerOptions()
        loadCurrentBanner()
    }

    private fun loadCurrentBanner() {
        viewModelScope.launch {
            try {
                userDao.getUserProfile().collect { profile ->
                    profile?.let {
                        _uiState.update { state ->
                            state.copy(
                                currentBannerId = it.bannerId,
                                selectedBannerId = it.bannerId,
                                isLoading = false
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                com.prody.prashant.util.AppLogger.e(TAG, "Error loading current banner", e)
                _uiState.update { it.copy(isLoading = false, error = "Failed to load banner") }
            }
        }
    }

    private fun loadBannerOptions() {
        viewModelScope.launch {
            try {
                val profile = userDao.getUserProfileSync()
                val totalPoints = profile?.totalPoints ?: 0
                val longestStreak = profile?.longestStreak ?: 0
                val journalEntries = profile?.journalEntriesCount ?: 0

                val banners = listOf(
                    // Nature Banners
                    BannerOption(
                        id = "default_dawn",
                        name = "Dawn",
                        primaryColor = Color(0xFFFFB347),
                        secondaryColor = Color(0xFFFF7043),
                        category = BannerCategory.NATURE
                    ),
                    BannerOption(
                        id = "forest_mist",
                        name = "Forest Mist",
                        primaryColor = Color(0xFF2E7D32),
                        secondaryColor = Color(0xFF1B5E20),
                        category = BannerCategory.NATURE
                    ),
                    BannerOption(
                        id = "ocean_depths",
                        name = "Ocean Depths",
                        primaryColor = Color(0xFF0288D1),
                        secondaryColor = Color(0xFF01579B),
                        category = BannerCategory.NATURE
                    ),
                    BannerOption(
                        id = "mountain_peak",
                        name = "Mountain Peak",
                        primaryColor = Color(0xFF546E7A),
                        secondaryColor = Color(0xFF37474F),
                        category = BannerCategory.NATURE
                    ),
                    BannerOption(
                        id = "autumn_leaves",
                        name = "Autumn Leaves",
                        primaryColor = Color(0xFFE65100),
                        secondaryColor = Color(0xFFBF360C),
                        category = BannerCategory.NATURE
                    ),
                    BannerOption(
                        id = "spring_bloom",
                        name = "Spring Bloom",
                        primaryColor = Color(0xFFEC407A),
                        secondaryColor = Color(0xFFAB47BC),
                        category = BannerCategory.NATURE
                    ),

                    // Gradient Banners
                    BannerOption(
                        id = "neon_green",
                        name = "Neon Green",
                        primaryColor = Color(0xFF36F97F),
                        secondaryColor = Color(0xFF00C853),
                        category = BannerCategory.GRADIENT
                    ),
                    BannerOption(
                        id = "electric_purple",
                        name = "Electric Purple",
                        primaryColor = Color(0xFF9C27B0),
                        secondaryColor = Color(0xFF673AB7),
                        category = BannerCategory.GRADIENT
                    ),
                    BannerOption(
                        id = "sunset_glow",
                        name = "Sunset Glow",
                        primaryColor = Color(0xFFFF6F00),
                        secondaryColor = Color(0xFFE91E63),
                        category = BannerCategory.GRADIENT
                    ),
                    BannerOption(
                        id = "midnight_blue",
                        name = "Midnight Blue",
                        primaryColor = Color(0xFF1A237E),
                        secondaryColor = Color(0xFF0D47A1),
                        category = BannerCategory.GRADIENT
                    ),
                    BannerOption(
                        id = "aurora",
                        name = "Aurora",
                        primaryColor = Color(0xFF00BCD4),
                        secondaryColor = Color(0xFF8BC34A),
                        category = BannerCategory.GRADIENT
                    ),
                    BannerOption(
                        id = "rose_gold",
                        name = "Rose Gold",
                        primaryColor = Color(0xFFFFAB91),
                        secondaryColor = Color(0xFFFF8A80),
                        category = BannerCategory.GRADIENT
                    ),

                    // Premium Banners (Locked based on level)
                    BannerOption(
                        id = "golden_hour",
                        name = "Golden Hour",
                        primaryColor = Color(0xFFFFD700),
                        secondaryColor = Color(0xFFFFA000),
                        isLocked = totalPoints < 500,
                        unlockRequirement = "Reach 500 XP",
                        category = BannerCategory.PREMIUM
                    ),
                    BannerOption(
                        id = "cosmic_night",
                        name = "Cosmic Night",
                        primaryColor = Color(0xFF311B92),
                        secondaryColor = Color(0xFF1A237E),
                        isLocked = totalPoints < 1500,
                        unlockRequirement = "Reach 1500 XP",
                        category = BannerCategory.PREMIUM
                    ),
                    BannerOption(
                        id = "emerald_dream",
                        name = "Emerald Dream",
                        primaryColor = Color(0xFF00695C),
                        secondaryColor = Color(0xFF004D40),
                        isLocked = totalPoints < 3500,
                        unlockRequirement = "Reach 3500 XP",
                        category = BannerCategory.PREMIUM
                    ),
                    BannerOption(
                        id = "diamond_shine",
                        name = "Diamond Shine",
                        primaryColor = Color(0xFFE0E0E0),
                        secondaryColor = Color(0xFF90CAF9),
                        isLocked = totalPoints < 7500,
                        unlockRequirement = "Reach 7500 XP",
                        category = BannerCategory.PREMIUM
                    ),

                    // Achievement Banners
                    BannerOption(
                        id = "streak_fire",
                        name = "Streak Fire",
                        primaryColor = Color(0xFFFF5722),
                        secondaryColor = Color(0xFFFF9800),
                        isLocked = longestStreak < 7,
                        unlockRequirement = "7-day streak",
                        category = BannerCategory.ACHIEVEMENTS
                    ),
                    BannerOption(
                        id = "streak_inferno",
                        name = "Streak Inferno",
                        primaryColor = Color(0xFFFF1744),
                        secondaryColor = Color(0xFFFF5722),
                        isLocked = longestStreak < 30,
                        unlockRequirement = "30-day streak",
                        category = BannerCategory.ACHIEVEMENTS
                    ),
                    BannerOption(
                        id = "journal_master",
                        name = "Journal Master",
                        primaryColor = Color(0xFF3F51B5),
                        secondaryColor = Color(0xFF2196F3),
                        isLocked = journalEntries < 50,
                        unlockRequirement = "50 journal entries",
                        category = BannerCategory.ACHIEVEMENTS
                    ),
                    BannerOption(
                        id = "legendary",
                        name = "Legendary",
                        primaryColor = Color(0xFFD4AF37),
                        secondaryColor = Color(0xFFFFC107),
                        isLocked = totalPoints < 10000,
                        unlockRequirement = "Reach Legend status",
                        category = BannerCategory.ACHIEVEMENTS
                    )
                )

                _uiState.update { it.copy(banners = banners) }
            } catch (e: Exception) {
                com.prody.prashant.util.AppLogger.e(TAG, "Error loading banner options", e)
            }
        }
    }

    fun selectCategory(category: BannerCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun selectBanner(bannerId: String) {
        val banner = _uiState.value.banners.find { it.id == bannerId }
        if (banner != null && !banner.isLocked) {
            _uiState.update { it.copy(selectedBannerId = bannerId) }
        }
    }

    fun saveBanner() {
        viewModelScope.launch {
            val state = _uiState.value

            if (!state.hasChanges) {
                _uiState.update { it.copy(isSaved = true) }
                return@launch
            }

            _uiState.update { it.copy(isSaving = true, error = null) }

            try {
                userDao.updateBanner(state.selectedBannerId)

                _uiState.update {
                    it.copy(
                        isSaving = false,
                        isSaved = true,
                        currentBannerId = state.selectedBannerId
                    )
                }
            } catch (e: Exception) {
                com.prody.prashant.util.AppLogger.e(TAG, "Error saving banner", e)
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        error = "Failed to save banner"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
