package com.prody.prashant.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager class for Text-to-Speech functionality.
 * Provides offline pronunciation for vocabulary words and quote reading.
 */
@Singleton
class TextToSpeechManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var textToSpeech: TextToSpeech? = null

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _speechRate = MutableStateFlow(1.0f)
    val speechRate: StateFlow<Float> = _speechRate.asStateFlow()

    private val _pitch = MutableStateFlow(1.0f)
    val pitch: StateFlow<Float> = _pitch.asStateFlow()

    init {
        // Initialize TTS safely - wrapped in try-catch to prevent app crash
        // on devices with TTS issues or missing TTS engines
        try {
            initialize()
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Failed to initialize TextToSpeech", e)
            _isInitialized.value = false
        }
    }

    private fun initialize() {
        try {
            textToSpeech = TextToSpeech(context) { status ->
                try {
                    if (status == TextToSpeech.SUCCESS) {
                        val result = textToSpeech?.setLanguage(Locale.US)
                        _isInitialized.value = result != TextToSpeech.LANG_MISSING_DATA &&
                                result != TextToSpeech.LANG_NOT_SUPPORTED

                        textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                            override fun onStart(utteranceId: String?) {
                                _isSpeaking.value = true
                            }

                            override fun onDone(utteranceId: String?) {
                                _isSpeaking.value = false
                            }

                            @Deprecated("Deprecated in Java")
                            override fun onError(utteranceId: String?) {
                                _isSpeaking.value = false
                            }

                            override fun onError(utteranceId: String?, errorCode: Int) {
                                _isSpeaking.value = false
                            }
                        })
                    } else {
                        _isInitialized.value = false
                        android.util.Log.w(TAG, "TextToSpeech initialization failed with status: $status")
                    }
                } catch (e: Exception) {
                    android.util.Log.e(TAG, "Error during TTS initialization callback", e)
                    _isInitialized.value = false
                }
            }
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Failed to create TextToSpeech instance", e)
            _isInitialized.value = false
        }
    }

    companion object {
        private const val TAG = "TextToSpeechManager"
    }

    /**
     * Speak the given text.
     * @param text The text to speak
     * @param utteranceId Optional unique identifier for this speech request
     */
    fun speak(text: String, utteranceId: String = "prody_utterance") {
        if (_isInitialized.value && text.isNotBlank()) {
            textToSpeech?.setSpeechRate(_speechRate.value)
            textToSpeech?.setPitch(_pitch.value)
            textToSpeech?.speak(
                text,
                TextToSpeech.QUEUE_FLUSH,
                null,
                utteranceId
            )
        }
    }

    /**
     * Speak the given text, adding it to the queue instead of replacing current speech.
     */
    fun speakQueued(text: String, utteranceId: String = "prody_utterance_${System.currentTimeMillis()}") {
        if (_isInitialized.value && text.isNotBlank()) {
            textToSpeech?.setSpeechRate(_speechRate.value)
            textToSpeech?.setPitch(_pitch.value)
            textToSpeech?.speak(
                text,
                TextToSpeech.QUEUE_ADD,
                null,
                utteranceId
            )
        }
    }

    /**
     * Stop any ongoing speech.
     */
    fun stop() {
        textToSpeech?.stop()
        _isSpeaking.value = false
    }

    /**
     * Set the speech rate.
     * @param rate Speech rate (0.5 = slow, 1.0 = normal, 2.0 = fast)
     */
    fun setSpeechRate(rate: Float) {
        _speechRate.value = rate.coerceIn(0.25f, 4.0f)
    }

    /**
     * Set the pitch.
     * @param pitch Voice pitch (0.5 = low, 1.0 = normal, 2.0 = high)
     */
    fun setPitch(pitch: Float) {
        _pitch.value = pitch.coerceIn(0.5f, 2.0f)
    }

    /**
     * Set the language for TTS.
     */
    fun setLanguage(locale: Locale): Boolean {
        return textToSpeech?.setLanguage(locale)?.let { result ->
            result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
        } ?: false
    }

    /**
     * Check if a specific locale is available.
     */
    fun isLanguageAvailable(locale: Locale): Boolean {
        return textToSpeech?.isLanguageAvailable(locale)?.let { result ->
            result >= TextToSpeech.LANG_AVAILABLE
        } ?: false
    }

    /**
     * Release TTS resources. Call this when the app is being destroyed.
     */
    fun shutdown() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
        _isInitialized.value = false
        _isSpeaking.value = false
    }

    /**
     * Reinitialize TTS if it was previously shut down.
     */
    fun reinitialize() {
        if (textToSpeech == null) {
            initialize()
        }
    }
}
