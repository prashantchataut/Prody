package com.prody.prashant.ui.screens.meditation

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prody.prashant.data.local.dao.QuoteDao
import com.prody.prashant.data.local.dao.UserDao
import com.prody.prashant.data.local.entity.QuoteEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MeditationUiState(
    val selectedDuration: Int = 5, // minutes
    val remainingTime: Long = 5 * 60 * 1000L, // milliseconds
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val isCompleted: Boolean = false,
    val openingWisdom: String? = null,
    val closingWisdom: String? = null,
    val sessionsCompleted: Int = 0,
    val totalMeditationTime: Long = 0, // in seconds
    val showDurationPicker: Boolean = false
)

@HiltViewModel
class MeditationTimerViewModel @Inject constructor(
    private val quoteDao: QuoteDao,
    private val userDao: UserDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(MeditationUiState())
    val uiState: StateFlow<MeditationUiState> = _uiState.asStateFlow()

    private var countDownTimer: CountDownTimer? = null
    private var mediaPlayer: MediaPlayer? = null

    companion object {
        val DURATION_OPTIONS = listOf(1, 3, 5, 10, 15, 20, 30) // minutes
    }

    init {
        loadInitialWisdom()
    }

    private fun loadInitialWisdom() {
        viewModelScope.launch {
            val quotes = try {
                quoteDao.getAllQuotes().first()
            } catch (e: Exception) {
                emptyList()
            }

            if (quotes.isNotEmpty()) {
                val randomQuote = quotes.random()
                _uiState.update { it.copy(openingWisdom = randomQuote.content) }
            } else {
                _uiState.update {
                    it.copy(openingWisdom = "Take a moment to breathe deeply and be present.")
                }
            }
        }
    }

    fun setDuration(minutes: Int) {
        if (!_uiState.value.isRunning) {
            _uiState.update {
                it.copy(
                    selectedDuration = minutes,
                    remainingTime = minutes * 60 * 1000L,
                    showDurationPicker = false
                )
            }
        }
    }

    fun toggleDurationPicker() {
        if (!_uiState.value.isRunning) {
            _uiState.update { it.copy(showDurationPicker = !it.showDurationPicker) }
        }
    }

    fun startMeditation() {
        if (_uiState.value.isRunning && !_uiState.value.isPaused) return

        if (_uiState.value.isPaused) {
            // Resume from paused state
            resumeTimer()
        } else {
            // Fresh start
            loadInitialWisdom()
            startTimer(_uiState.value.remainingTime)
        }

        _uiState.update {
            it.copy(
                isRunning = true,
                isPaused = false,
                isCompleted = false,
                closingWisdom = null
            )
        }
    }

    fun pauseMeditation() {
        countDownTimer?.cancel()
        _uiState.update { it.copy(isPaused = true) }
    }

    fun stopMeditation() {
        countDownTimer?.cancel()
        val duration = _uiState.value.selectedDuration
        _uiState.update {
            it.copy(
                isRunning = false,
                isPaused = false,
                remainingTime = duration * 60 * 1000L
            )
        }
    }

    private fun startTimer(duration: Long) {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _uiState.update { it.copy(remainingTime = millisUntilFinished) }
            }

            override fun onFinish() {
                completeMeditation()
            }
        }.start()
    }

    private fun resumeTimer() {
        startTimer(_uiState.value.remainingTime)
    }

    private fun completeMeditation() {
        viewModelScope.launch {
            // Load closing wisdom
            val quotes = try {
                quoteDao.getAllQuotes().first()
            } catch (e: Exception) {
                emptyList()
            }

            val closingQuote = if (quotes.isNotEmpty()) {
                quotes.random().content
            } else {
                "Well done. Carry this peace with you."
            }

            // Update user stats
            val sessionMinutes = _uiState.value.selectedDuration
            try {
                userDao.addPoints(sessionMinutes * 10) // 10 points per minute
            } catch (e: Exception) {
                // Handle error silently
            }

            _uiState.update {
                it.copy(
                    isRunning = false,
                    isPaused = false,
                    isCompleted = true,
                    remainingTime = 0,
                    closingWisdom = closingQuote,
                    sessionsCompleted = it.sessionsCompleted + 1,
                    totalMeditationTime = it.totalMeditationTime + (sessionMinutes * 60)
                )
            }
        }
    }

    fun resetSession() {
        val duration = _uiState.value.selectedDuration
        _uiState.update {
            it.copy(
                isRunning = false,
                isPaused = false,
                isCompleted = false,
                remainingTime = duration * 60 * 1000L,
                closingWisdom = null
            )
        }
        loadInitialWisdom()
    }

    fun formatTime(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%02d:%02d".format(minutes, seconds)
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
        mediaPlayer?.release()
    }
}
