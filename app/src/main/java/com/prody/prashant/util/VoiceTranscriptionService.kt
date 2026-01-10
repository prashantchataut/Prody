package com.prody.prashant.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Voice Transcription Service - Converts speech to text using Android's SpeechRecognizer.
 *
 * Features:
 * - Real-time speech-to-text transcription
 * - Partial results for live feedback
 * - Multiple language support
 * - Error handling with descriptive messages
 * - State management for UI integration
 *
 * Usage:
 * 1. Check if speech recognition is available: isAvailable()
 * 2. Start listening: startListening() or transcribe() flow
 * 3. Observe state via isListening, transcription, error flows
 * 4. Stop with stopListening() or let it auto-stop on silence
 */
@Singleton
class VoiceTranscriptionService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "VoiceTranscription"
    }

    private var speechRecognizer: SpeechRecognizer? = null

    // State flows for UI observation
    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _transcription = MutableStateFlow("")
    val transcription: StateFlow<String> = _transcription.asStateFlow()

    private val _partialTranscription = MutableStateFlow("")
    val partialTranscription: StateFlow<String> = _partialTranscription.asStateFlow()

    private val _error = MutableStateFlow<TranscriptionError?>(null)
    val error: StateFlow<TranscriptionError?> = _error.asStateFlow()

    private val _soundLevel = MutableStateFlow(0f)
    val soundLevel: StateFlow<Float> = _soundLevel.asStateFlow()

    /**
     * Checks if speech recognition is available on this device.
     */
    fun isAvailable(): Boolean {
        return SpeechRecognizer.isRecognitionAvailable(context)
    }

    /**
     * Starts listening for speech and transcribing.
     * Results will be available via the state flows.
     *
     * @param language Language code (e.g., "en-US"). Defaults to device locale.
     */
    fun startListening(language: String? = null) {
        if (!isAvailable()) {
            _error.value = TranscriptionError.NotAvailable
            return
        }

        if (_isListening.value) {
            Log.w(TAG, "Already listening")
            return
        }

        try {
            // Create speech recognizer on main looper
            speechRecognizer?.destroy()
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(createRecognitionListener())
            }

            val intent = createRecognizerIntent(language)
            speechRecognizer?.startListening(intent)

            _isListening.value = true
            _transcription.value = ""
            _partialTranscription.value = ""
            _error.value = null

            Log.d(TAG, "Started listening")

        } catch (e: Exception) {
            Log.e(TAG, "Failed to start listening", e)
            _error.value = TranscriptionError.Unknown(e.message ?: "Failed to start")
            _isListening.value = false
        }
    }

    /**
     * Stops listening for speech.
     */
    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
            _isListening.value = false
            Log.d(TAG, "Stopped listening")
        } catch (e: Exception) {
            Log.w(TAG, "Error stopping listener", e)
        }
    }

    /**
     * Cancels the current recognition session.
     */
    fun cancel() {
        try {
            speechRecognizer?.cancel()
            _isListening.value = false
            _partialTranscription.value = ""
            Log.d(TAG, "Cancelled recognition")
        } catch (e: Exception) {
            Log.w(TAG, "Error cancelling", e)
        }
    }

    /**
     * Releases all resources. Call when done using the service.
     */
    fun release() {
        try {
            speechRecognizer?.destroy()
            speechRecognizer = null
            _isListening.value = false
            Log.d(TAG, "Released resources")
        } catch (e: Exception) {
            Log.w(TAG, "Error releasing", e)
        }
    }

    /**
     * Clears the error state.
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Returns a Flow that emits transcription results.
     * Useful for one-shot transcription with coroutines.
     *
     * @param language Language code (e.g., "en-US"). Defaults to device locale.
     * @return Flow emitting TranscriptionResult
     */
    fun transcribe(language: String? = null): Flow<TranscriptionResult> = callbackFlow {
        if (!isAvailable()) {
            send(TranscriptionResult.Error(TranscriptionError.NotAvailable))
            close()
            return@callbackFlow
        }

        val recognizer = SpeechRecognizer.createSpeechRecognizer(context)

        recognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                trySend(TranscriptionResult.Ready)
            }

            override fun onBeginningOfSpeech() {
                trySend(TranscriptionResult.Started)
            }

            override fun onRmsChanged(rmsdB: Float) {
                // Normalize to 0-1 range (typical range is -2 to 10)
                val normalized = ((rmsdB + 2) / 12f).coerceIn(0f, 1f)
                trySend(TranscriptionResult.SoundLevel(normalized))
            }

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                trySend(TranscriptionResult.Ended)
            }

            override fun onError(errorCode: Int) {
                val error = mapErrorCode(errorCode)
                trySend(TranscriptionResult.Error(error))
                close()
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull() ?: ""
                trySend(TranscriptionResult.Final(text))
                close()
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull() ?: ""
                if (text.isNotEmpty()) {
                    trySend(TranscriptionResult.Partial(text))
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        val intent = createRecognizerIntent(language)
        recognizer.startListening(intent)

        awaitClose {
            recognizer.cancel()
            recognizer.destroy()
        }
    }

    private fun createRecognizerIntent(language: String?): Intent {
        return Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, language ?: Locale.getDefault().toString())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            // Enable offline recognition if available
            putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, false)
        }
    }

    private fun createRecognitionListener(): RecognitionListener {
        return object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d(TAG, "Ready for speech")
            }

            override fun onBeginningOfSpeech() {
                Log.d(TAG, "Speech started")
            }

            override fun onRmsChanged(rmsdB: Float) {
                // Normalize to 0-1 range
                val normalized = ((rmsdB + 2) / 12f).coerceIn(0f, 1f)
                _soundLevel.value = normalized
            }

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                Log.d(TAG, "Speech ended")
            }

            override fun onError(errorCode: Int) {
                val error = mapErrorCode(errorCode)
                Log.e(TAG, "Recognition error: $error")
                _error.value = error
                _isListening.value = false
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull() ?: ""
                _transcription.value = text
                _partialTranscription.value = ""
                _isListening.value = false
                Log.d(TAG, "Final result: $text")
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull() ?: ""
                if (text.isNotEmpty()) {
                    _partialTranscription.value = text
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
    }

    private fun mapErrorCode(errorCode: Int): TranscriptionError {
        return when (errorCode) {
            SpeechRecognizer.ERROR_AUDIO -> TranscriptionError.AudioError
            SpeechRecognizer.ERROR_CLIENT -> TranscriptionError.ClientError
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> TranscriptionError.PermissionDenied
            SpeechRecognizer.ERROR_NETWORK -> TranscriptionError.NetworkError
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> TranscriptionError.NetworkTimeout
            SpeechRecognizer.ERROR_NO_MATCH -> TranscriptionError.NoMatch
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> TranscriptionError.RecognizerBusy
            SpeechRecognizer.ERROR_SERVER -> TranscriptionError.ServerError
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> TranscriptionError.SpeechTimeout
            else -> TranscriptionError.Unknown("Error code: $errorCode")
        }
    }
}

/**
 * Transcription errors with user-friendly messages.
 */
sealed class TranscriptionError(val message: String) {
    data object NotAvailable : TranscriptionError("Speech recognition is not available on this device")
    data object PermissionDenied : TranscriptionError("Microphone permission is required for voice input")
    data object NetworkError : TranscriptionError("Network error. Please check your connection")
    data object NetworkTimeout : TranscriptionError("Network request timed out")
    data object NoMatch : TranscriptionError("Could not understand. Please try again")
    data object RecognizerBusy : TranscriptionError("Speech recognizer is busy. Please wait")
    data object ServerError : TranscriptionError("Server error. Please try again later")
    data object SpeechTimeout : TranscriptionError("No speech detected. Please try again")
    data object AudioError : TranscriptionError("Audio recording error")
    data object ClientError : TranscriptionError("Voice recognition error")
    data class Unknown(val details: String) : TranscriptionError("An error occurred: $details")
}

/**
 * Result types for the transcription flow.
 */
sealed class TranscriptionResult {
    /** Ready to receive speech */
    data object Ready : TranscriptionResult()

    /** Speech has started */
    data object Started : TranscriptionResult()

    /** Speech has ended, processing... */
    data object Ended : TranscriptionResult()

    /** Partial transcription (live feedback) */
    data class Partial(val text: String) : TranscriptionResult()

    /** Final transcription result */
    data class Final(val text: String) : TranscriptionResult()

    /** Sound level for visualization (0-1) */
    data class SoundLevel(val level: Float) : TranscriptionResult()

    /** Error occurred */
    data class Error(val error: TranscriptionError) : TranscriptionResult()
}
