package com.prody.prashant.util

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Production-grade audio recorder manager for voice notes.
 * Handles recording, playback, and file management.
 *
 * Features:
 * - High-quality audio recording (AAC, 128kbps)
 * - Real-time recording duration tracking
 * - Playback with progress tracking
 * - Automatic file cleanup
 * - Error handling and recovery
 */
@Singleton
class AudioRecorderManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "AudioRecorderManager"
        private const val RECORDING_DIR = "voice_recordings"
        private const val FILE_EXTENSION = ".m4a"
        private const val AUDIO_BIT_RATE = 128000
        private const val AUDIO_SAMPLE_RATE = 44100
        private const val AUDIO_CHANNELS = 1
        private const val TIMER_UPDATE_INTERVAL = 100L // ms
        private const val MAX_RECORDING_DURATION = 5 * 60 * 1000L // 5 minutes max
    }

    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var currentRecordingFile: File? = null
    private var timerJob: Job? = null
    private var playbackJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    // Recording state
    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _recordingDuration = MutableStateFlow(0L)
    val recordingDuration: StateFlow<Long> = _recordingDuration.asStateFlow()

    private val _recordingAmplitude = MutableStateFlow(0)
    val recordingAmplitude: StateFlow<Int> = _recordingAmplitude.asStateFlow()

    // Playback state
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _playbackProgress = MutableStateFlow(0f)
    val playbackProgress: StateFlow<Float> = _playbackProgress.asStateFlow()

    private val _playbackDuration = MutableStateFlow(0L)
    val playbackDuration: StateFlow<Long> = _playbackDuration.asStateFlow()

    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * Starts audio recording to a new file.
     * @return The URI of the recording file, or null if failed
     */
    fun startRecording(): Uri? {
        if (_isRecording.value) {
            Log.w(TAG, "Recording already in progress")
            return null
        }

        try {
            // Create recording directory
            val recordingDir = File(context.filesDir, RECORDING_DIR)
            if (!recordingDir.exists()) {
                recordingDir.mkdirs()
            }

            // Create unique file name
            val fileName = "voice_${System.currentTimeMillis()}$FILE_EXTENSION"
            currentRecordingFile = File(recordingDir, fileName)

            // Initialize MediaRecorder
            mediaRecorder = createMediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(AUDIO_BIT_RATE)
                setAudioSamplingRate(AUDIO_SAMPLE_RATE)
                setAudioChannels(AUDIO_CHANNELS)
                setMaxDuration(MAX_RECORDING_DURATION.toInt())
                setOutputFile(currentRecordingFile?.absolutePath)

                setOnInfoListener { _, what, _ ->
                    if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                        Log.d(TAG, "Max recording duration reached")
                        stopRecording()
                    }
                }

                setOnErrorListener { _, what, extra ->
                    Log.e(TAG, "MediaRecorder error: what=$what, extra=$extra")
                    _error.value = "Recording error occurred"
                    stopRecording()
                }

                prepare()
                start()
            }

            _isRecording.value = true
            _recordingDuration.value = 0L
            _error.value = null

            // Start timer for duration tracking
            startRecordingTimer()

            val uri = getUriForFile(currentRecordingFile!!)
            Log.d(TAG, "Recording started: ${currentRecordingFile?.absolutePath}")
            return uri

        } catch (e: IOException) {
            Log.e(TAG, "Failed to start recording: IOException", e)
            _error.value = "Failed to start recording: Storage error"
            cleanupRecording()
            return null
        } catch (e: SecurityException) {
            Log.e(TAG, "Failed to start recording: SecurityException", e)
            _error.value = "Microphone permission required"
            cleanupRecording()
            return null
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start recording", e)
            _error.value = "Failed to start recording"
            cleanupRecording()
            return null
        }
    }

    /**
     * Stops the current recording.
     * @return Pair of (file URI, duration in milliseconds), or null if no recording
     */
    fun stopRecording(): Pair<Uri, Long>? {
        if (!_isRecording.value) {
            Log.w(TAG, "No recording in progress")
            return null
        }

        timerJob?.cancel()
        timerJob = null

        val duration = _recordingDuration.value
        val file = currentRecordingFile

        try {
            mediaRecorder?.apply {
                stop()
                reset()
                release()
            }
            mediaRecorder = null

            _isRecording.value = false
            _recordingAmplitude.value = 0

            if (file != null && file.exists() && file.length() > 0) {
                val uri = getUriForFile(file)
                Log.d(TAG, "Recording stopped: duration=${duration}ms, file=${file.absolutePath}")
                return Pair(uri, duration)
            } else {
                Log.w(TAG, "Recording file is empty or doesn't exist")
                _error.value = "Recording failed - file empty"
                return null
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error stopping recording", e)
            _error.value = "Error saving recording"
            cleanupRecording()
            return null
        }
    }

    /**
     * Cancels the current recording and deletes the file.
     */
    fun cancelRecording() {
        timerJob?.cancel()
        timerJob = null

        try {
            mediaRecorder?.apply {
                stop()
                reset()
                release()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error stopping recorder during cancel", e)
        }
        mediaRecorder = null

        // Delete the partial recording file
        currentRecordingFile?.delete()
        currentRecordingFile = null

        _isRecording.value = false
        _recordingDuration.value = 0L
        _recordingAmplitude.value = 0

        Log.d(TAG, "Recording cancelled")
    }

    /**
     * Starts playback of an audio file.
     * @param uri The URI of the audio file to play
     */
    fun startPlayback(uri: Uri) {
        if (_isPlaying.value) {
            stopPlayback()
        }

        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, uri)
                prepare()

                setOnCompletionListener {
                    stopPlayback()
                }

                setOnErrorListener { _, what, extra ->
                    Log.e(TAG, "MediaPlayer error: what=$what, extra=$extra")
                    _error.value = "Playback error"
                    stopPlayback()
                    true
                }

                start()
            }

            _isPlaying.value = true
            _playbackDuration.value = mediaPlayer?.duration?.toLong() ?: 0L
            _playbackProgress.value = 0f
            _error.value = null

            // Start playback progress tracking
            startPlaybackTimer()

            Log.d(TAG, "Playback started: $uri")

        } catch (e: Exception) {
            Log.e(TAG, "Failed to start playback", e)
            _error.value = "Failed to play audio"
            stopPlayback()
        }
    }

    /**
     * Stops the current playback.
     */
    fun stopPlayback() {
        playbackJob?.cancel()
        playbackJob = null

        try {
            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                reset()
                release()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error stopping playback", e)
        }
        mediaPlayer = null

        _isPlaying.value = false
        _playbackProgress.value = 0f

        Log.d(TAG, "Playback stopped")
    }

    /**
     * Toggles playback pause/resume.
     */
    fun togglePlaybackPause() {
        mediaPlayer?.let { player ->
            if (player.isPlaying) {
                player.pause()
                playbackJob?.cancel()
                _isPlaying.value = false
            } else {
                player.start()
                startPlaybackTimer()
                _isPlaying.value = true
            }
        }
    }

    /**
     * Seeks playback to a specific position.
     * @param progress Progress value from 0f to 1f
     */
    fun seekTo(progress: Float) {
        mediaPlayer?.let { player ->
            val position = (player.duration * progress.coerceIn(0f, 1f)).toInt()
            player.seekTo(position)
            _playbackProgress.value = progress
        }
    }

    /**
     * Deletes an audio recording file.
     * @param uri The URI of the file to delete
     */
    fun deleteRecording(uri: Uri): Boolean {
        return try {
            val path = uri.path
            if (path != null) {
                val file = File(path)
                if (file.exists()) {
                    file.delete()
                } else {
                    // Try to resolve from content URI
                    context.contentResolver.delete(uri, null, null)
                    true
                }
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete recording: $uri", e)
            false
        }
    }

    /**
     * Gets the duration of an audio file in milliseconds.
     */
    fun getAudioDuration(uri: Uri): Long {
        var player: MediaPlayer? = null
        return try {
            player = MediaPlayer()
            player.setDataSource(context, uri)
            player.prepare()
            player.duration.toLong()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get audio duration", e)
            0L
        } finally {
            player?.release()
        }
    }

    /**
     * Clears the error state.
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Releases all resources. Call when done using the manager.
     */
    fun release() {
        cancelRecording()
        stopPlayback()
    }

    // Private helpers

    @Suppress("DEPRECATION")
    private fun createMediaRecorder(): MediaRecorder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            MediaRecorder()
        }
    }

    private fun getUriForFile(file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    private fun startRecordingTimer() {
        timerJob = scope.launch {
            val startTime = System.currentTimeMillis()
            while (isActive && _isRecording.value) {
                _recordingDuration.value = System.currentTimeMillis() - startTime

                // Update amplitude for visualizer
                try {
                    mediaRecorder?.maxAmplitude?.let { amp ->
                        _recordingAmplitude.value = amp
                    }
                } catch (e: Exception) {
                    // Ignore amplitude errors
                }

                // Check max duration
                if (_recordingDuration.value >= MAX_RECORDING_DURATION) {
                    stopRecording()
                    break
                }

                delay(TIMER_UPDATE_INTERVAL)
            }
        }
    }

    private fun startPlaybackTimer() {
        playbackJob = scope.launch {
            while (isActive && _isPlaying.value) {
                mediaPlayer?.let { player ->
                    if (player.isPlaying) {
                        val current = player.currentPosition
                        val total = player.duration
                        if (total > 0) {
                            _playbackProgress.value = current.toFloat() / total
                        }
                    }
                }
                delay(TIMER_UPDATE_INTERVAL)
            }
        }
    }

    private fun cleanupRecording() {
        timerJob?.cancel()
        timerJob = null

        try {
            mediaRecorder?.release()
        } catch (e: Exception) {
            // Ignore
        }
        mediaRecorder = null

        currentRecordingFile?.delete()
        currentRecordingFile = null

        _isRecording.value = false
        _recordingDuration.value = 0L
        _recordingAmplitude.value = 0
    }
}
