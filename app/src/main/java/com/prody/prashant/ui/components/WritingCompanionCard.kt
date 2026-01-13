package com.prody.prashant.ui.components
import com.prody.prashant.ui.icons.ProdyIcons

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.prody.prashant.data.ai.WritingCompanionService
import com.prody.prashant.domain.model.Mood

/**
 * Writing Companion Card Component
 *
 * A non-intrusive, dismissable card that provides contextual writing suggestions
 * to help users when they're stuck or need inspiration while journaling.
 *
 * Features:
 * - Shows contextual prompts based on mood, time of day, and content
 * - Completely dismissable - never intrusive
 * - Appears when user seems stuck (empty content or short pause)
 * - Can be manually triggered or hidden
 */
@Composable
fun WritingCompanionCard(
    suggestion: String,
    suggestionType: SuggestionType,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onUseSuggestion: (String) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
    ) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f),
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (suggestionType) {
                                SuggestionType.STARTING_PROMPT -> ProdyIcons.Lightbulb
                                SuggestionType.CONTINUATION -> ProdyIcons.ArrowForward
                                SuggestionType.STUCK_HELP -> ProdyIcons.HelpOutline
                            },
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (suggestionType) {
                                SuggestionType.STARTING_PROMPT -> "Start here"
                                SuggestionType.CONTINUATION -> "Keep going"
                                SuggestionType.STUCK_HELP -> "Need a nudge?"
                            },
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = ProdyIcons.Close,
                            contentDescription = "Dismiss",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = suggestion,
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        onClick = onRefresh,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.tertiary
                        )
                    ) {
                        Icon(
                            imageVector = ProdyIcons.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Another", style = MaterialTheme.typography.labelMedium)
                    }

                    if (suggestionType == SuggestionType.STARTING_PROMPT) {
                        FilledTonalButton(
                            onClick = { onUseSuggestion(suggestion) },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
                                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        ) {
                            Text("Use this", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Type of writing suggestion
 */
enum class SuggestionType {
    STARTING_PROMPT,    // When journal is empty
    CONTINUATION,       // When user has written a bit but paused
    STUCK_HELP          // When user seems stuck (idle for a while)
}

/**
 * State holder for Writing Companion
 */
data class WritingCompanionState(
    val isVisible: Boolean = false,
    val currentSuggestion: String = "",
    val suggestionType: SuggestionType = SuggestionType.STARTING_PROMPT,
    val isDismissedForSession: Boolean = false,
    val lastContentLength: Int = 0,
    val lastActivityTime: Long = System.currentTimeMillis()
)

/**
 * Writing Companion Controller
 *
 * Manages the logic for when and what suggestions to show.
 */
class WritingCompanionController(
    private val writingCompanionService: WritingCompanionService
) {
    private var state = WritingCompanionState()
    private var onStateChange: ((WritingCompanionState) -> Unit)? = null

    fun setStateChangeListener(listener: (WritingCompanionState) -> Unit) {
        onStateChange = listener
    }

    /**
     * Called when journal content changes
     */
    fun onContentChanged(
        content: String,
        mood: Mood? = null,
        recentThemes: List<String> = emptyList()
    ) {
        if (state.isDismissedForSession) return

        val contentLength = content.length
        val wasEmpty = state.lastContentLength == 0
        val isNowNotEmpty = contentLength > 0

        state = state.copy(
            lastContentLength = contentLength,
            lastActivityTime = System.currentTimeMillis()
        )

        // If content went from empty to having content, hide the starting prompt
        if (wasEmpty && isNowNotEmpty && state.suggestionType == SuggestionType.STARTING_PROMPT) {
            state = state.copy(isVisible = false)
            onStateChange?.invoke(state)
        }
    }

    /**
     * Show a starting prompt when journal is opened with empty content
     */
    fun showStartingPrompt(
        mood: Mood? = null,
        recentThemes: List<String> = emptyList()
    ) {
        if (state.isDismissedForSession) return

        val prompt = writingCompanionService.getStartingPrompt(
            mood = mood,
            recentThemes = recentThemes
        )

        state = state.copy(
            isVisible = true,
            currentSuggestion = prompt,
            suggestionType = SuggestionType.STARTING_PROMPT
        )
        onStateChange?.invoke(state)
    }

    /**
     * Check if user needs a continuation suggestion
     * Called after a pause in writing
     */
    fun checkForContinuation(
        content: String,
        mood: Mood? = null
    ) {
        if (state.isDismissedForSession || content.isBlank()) return

        val suggestion = writingCompanionService.getContinuationSuggestion(
            currentContent = content,
            mood = mood
        )

        if (suggestion != null) {
            state = state.copy(
                isVisible = true,
                currentSuggestion = suggestion,
                suggestionType = SuggestionType.CONTINUATION
            )
            onStateChange?.invoke(state)
        }
    }

    /**
     * Show stuck help prompt when user has been idle
     */
    fun showStuckHelp(
        content: String,
        mood: Mood? = null,
        recentThemes: List<String> = emptyList()
    ) {
        if (state.isDismissedForSession) return

        val prompt = writingCompanionService.getStuckHelpPrompt(
            currentContent = content,
            mood = mood,
            recentThemes = recentThemes
        )

        state = state.copy(
            isVisible = true,
            currentSuggestion = prompt,
            suggestionType = SuggestionType.STUCK_HELP
        )
        onStateChange?.invoke(state)
    }

    /**
     * Get a new suggestion of the same type
     */
    fun refreshSuggestion(
        content: String = "",
        mood: Mood? = null,
        recentThemes: List<String> = emptyList()
    ) {
        val newSuggestion = when (state.suggestionType) {
            SuggestionType.STARTING_PROMPT -> writingCompanionService.getStartingPrompt(
                mood = mood,
                recentThemes = recentThemes
            )
            SuggestionType.CONTINUATION -> writingCompanionService.getContinuationSuggestion(
                currentContent = content,
                mood = mood
            ) ?: writingCompanionService.getStuckHelpPrompt(content, mood, recentThemes)
            SuggestionType.STUCK_HELP -> writingCompanionService.getStuckHelpPrompt(
                currentContent = content,
                mood = mood,
                recentThemes = recentThemes
            )
        }

        state = state.copy(currentSuggestion = newSuggestion)
        onStateChange?.invoke(state)
    }

    /**
     * Dismiss the current suggestion
     */
    fun dismiss() {
        state = state.copy(isVisible = false)
        onStateChange?.invoke(state)
    }

    /**
     * Dismiss all suggestions for this session
     */
    fun dismissForSession() {
        state = state.copy(
            isVisible = false,
            isDismissedForSession = true
        )
        onStateChange?.invoke(state)
    }

    /**
     * Reset for a new session
     */
    fun reset() {
        state = WritingCompanionState()
        onStateChange?.invoke(state)
    }

    fun getCurrentState(): WritingCompanionState = state
}

/**
 * Minimal floating prompt button - shows when companion is hidden
 * Allows user to manually request help
 */
@Composable
fun WritingCompanionButton(
    onClick: () -> Unit,
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        FilledIconButton(
            onClick = onClick,
            modifier = modifier,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.8f),
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            )
        ) {
            Icon(
                imageVector = ProdyIcons.Lightbulb,
                contentDescription = "Writing help",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
