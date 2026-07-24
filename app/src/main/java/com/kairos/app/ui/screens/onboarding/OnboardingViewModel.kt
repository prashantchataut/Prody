package com.kairos.app.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kairos.app.domain.common.Result
import com.kairos.app.domain.repository.OnboardingPreferences
import com.kairos.app.domain.repository.OnboardingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface OnboardingCompletionState {
    data object Idle : OnboardingCompletionState
    data object Saving : OnboardingCompletionState
    data object Completed : OnboardingCompletionState
    data class Error(val message: String) : OnboardingCompletionState
}

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingRepository: OnboardingRepository
) : ViewModel() {

    private val _completionState = MutableStateFlow<OnboardingCompletionState>(OnboardingCompletionState.Idle)
    val completionState: StateFlow<OnboardingCompletionState> = _completionState.asStateFlow()

    fun completeOnboarding(
        vocabularyDifficulty: Int,
        wisdomCategories: Set<String>
    ) {
        if (_completionState.value is OnboardingCompletionState.Saving) return

        viewModelScope.launch {
            _completionState.value = OnboardingCompletionState.Saving
            when (
                val result = onboardingRepository.completeSetup(
                    OnboardingPreferences(
                        vocabularyDifficulty = vocabularyDifficulty,
                        wisdomCategories = wisdomCategories
                    )
                )
            ) {
                is Result.Success -> _completionState.value = OnboardingCompletionState.Completed
                is Result.Error -> _completionState.value = OnboardingCompletionState.Error(
                    result.userMessage.ifBlank {
                        "Kairos could not finish local setup. Your choices are safe; try once more."
                    }
                )
                is Result.Loading -> Unit
            }
        }
    }

    fun clearError() {
        if (_completionState.value is OnboardingCompletionState.Error) {
            _completionState.value = OnboardingCompletionState.Idle
        }
    }
}
