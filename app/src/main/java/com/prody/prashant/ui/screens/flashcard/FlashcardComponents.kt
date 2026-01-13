package com.prody.prashant.ui.screens.flashcard
import com.prody.prashant.ui.icons.ProdyIcons

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.prody.prashant.data.local.entity.VocabularyEntity
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Data class representing the state of a flashcard swipe.
 */
data class SwipeState(
    val direction: SwipeDirection = SwipeDirection.NONE,
    val progress: Float = 0f // 0 to 1, representing how far the swipe has progressed
)

enum class SwipeDirection {
    NONE, LEFT, RIGHT, UP
}

/**
 * A swipeable flashcard stack component with Tinder-style interactions.
 */
@Composable
fun FlashcardStack(
    cards: List<VocabularyEntity>,
    currentIndex: Int,
    onSwipeLeft: (VocabularyEntity) -> Unit,
    onSwipeRight: (VocabularyEntity) -> Unit,
    onSwipeUp: (VocabularyEntity) -> Unit,
    onSpeak: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (cards.isEmpty() || currentIndex >= cards.size) {
        EmptyFlashcardState(modifier = modifier)
        return
    }

    val currentCard = cards[currentIndex]

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        // Show next card behind (preview)
        if (currentIndex + 1 < cards.size) {
            val nextCard = cards[currentIndex + 1]
            Flashcard(
                word = nextCard,
                isFlipped = false,
                onFlip = {},
                onSpeak = {},
                swipeState = SwipeState(),
                modifier = Modifier
                    .scale(0.9f)
                    .graphicsLayer { alpha = 0.5f }
            )
        }

        // Show current card on top
        SwipeableFlashcard(
            word = currentCard,
            onSwipeLeft = { onSwipeLeft(currentCard) },
            onSwipeRight = { onSwipeRight(currentCard) },
            onSwipeUp = { onSwipeUp(currentCard) },
            onSpeak = { onSpeak(currentCard.word) }
        )
    }
}

/**
 * A swipeable flashcard stack that tracks when the user flips the card to see the answer.
 * This enables showing more detailed review options after the user has seen the definition.
 */
@Composable
fun FlashcardStackWithFlipTracking(
    cards: List<VocabularyEntity>,
    currentIndex: Int,
    onSwipeLeft: (VocabularyEntity) -> Unit,
    onSwipeRight: (VocabularyEntity) -> Unit,
    onSwipeUp: (VocabularyEntity) -> Unit,
    onSpeak: (String) -> Unit,
    onFlip: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (cards.isEmpty() || currentIndex >= cards.size) {
        EmptyFlashcardState(modifier = modifier)
        return
    }

    val currentCard = cards[currentIndex]

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        // Show next card behind (preview)
        if (currentIndex + 1 < cards.size) {
            val nextCard = cards[currentIndex + 1]
            Flashcard(
                word = nextCard,
                isFlipped = false,
                onFlip = {},
                onSpeak = {},
                swipeState = SwipeState(),
                modifier = Modifier
                    .scale(0.9f)
                    .graphicsLayer { alpha = 0.5f }
            )
        }

        // Show current card on top with flip tracking
        SwipeableFlashcardWithFlipTracking(
            word = currentCard,
            onSwipeLeft = { onSwipeLeft(currentCard) },
            onSwipeRight = { onSwipeRight(currentCard) },
            onSwipeUp = { onSwipeUp(currentCard) },
            onSpeak = { onSpeak(currentCard.word) },
            onFlip = onFlip
        )
    }
}

/**
 * Swipeable flashcard with gesture detection and flip tracking callback.
 */
@Composable
fun SwipeableFlashcardWithFlipTracking(
    word: VocabularyEntity,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    onSwipeUp: () -> Unit,
    onSpeak: () -> Unit,
    onFlip: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFlipped by remember { mutableStateOf(false) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var swipeState by remember { mutableStateOf(SwipeState()) }

    val scope = rememberCoroutineScope()
    val animatedOffsetX = remember { Animatable(0f) }
    val animatedOffsetY = remember { Animatable(0f) }

    val swipeThreshold = 150f

    LaunchedEffect(offsetX, offsetY) {
        val direction = when {
            abs(offsetY) > abs(offsetX) && offsetY < -swipeThreshold * 0.3f -> SwipeDirection.UP
            offsetX < -swipeThreshold * 0.3f -> SwipeDirection.LEFT
            offsetX > swipeThreshold * 0.3f -> SwipeDirection.RIGHT
            else -> SwipeDirection.NONE
        }
        val progress = when (direction) {
            SwipeDirection.UP -> (abs(offsetY) / swipeThreshold).coerceIn(0f, 1f)
            SwipeDirection.LEFT, SwipeDirection.RIGHT -> (abs(offsetX) / swipeThreshold).coerceIn(0f, 1f)
            SwipeDirection.NONE -> 0f
        }
        swipeState = SwipeState(direction, progress)
    }

    Flashcard(
        word = word,
        isFlipped = isFlipped,
        onFlip = {
            isFlipped = !isFlipped
            if (!isFlipped) {
                // Card was just flipped to back (showing answer)
            } else {
                // Card is now showing front
            }
            // Always notify parent when card is flipped to show answer (back)
            if (isFlipped) {
                // isFlipped = true means showing back (answer)
            }
            // Actually, we flip on tap, so when isFlipped becomes true, user sees the answer
            // Notify on first flip to back
            onFlip()
        },
        onSpeak = onSpeak,
        swipeState = swipeState,
        modifier = modifier
            .offset {
                IntOffset(
                    animatedOffsetX.value.roundToInt() + offsetX.roundToInt(),
                    animatedOffsetY.value.roundToInt() + offsetY.roundToInt()
                )
            }
            .rotate(offsetX / 20f)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        when {
                            // Swipe up (skip)
                            offsetY < -swipeThreshold -> {
                                scope.launch {
                                    animatedOffsetY.animateTo(
                                        -1000f,
                                        spring(stiffness = Spring.StiffnessLow)
                                    )
                                    onSwipeUp()
                                    animatedOffsetY.snapTo(0f)
                                    animatedOffsetX.snapTo(0f)
                                    isFlipped = false
                                }
                            }
                            // Swipe left (don't know)
                            offsetX < -swipeThreshold -> {
                                scope.launch {
                                    animatedOffsetX.animateTo(
                                        -1000f,
                                        spring(stiffness = Spring.StiffnessLow)
                                    )
                                    onSwipeLeft()
                                    animatedOffsetX.snapTo(0f)
                                    animatedOffsetY.snapTo(0f)
                                    isFlipped = false
                                }
                            }
                            // Swipe right (know)
                            offsetX > swipeThreshold -> {
                                scope.launch {
                                    animatedOffsetX.animateTo(
                                        1000f,
                                        spring(stiffness = Spring.StiffnessLow)
                                    )
                                    onSwipeRight()
                                    animatedOffsetX.snapTo(0f)
                                    animatedOffsetY.snapTo(0f)
                                    isFlipped = false
                                }
                            }
                            // Return to center
                            else -> {
                                scope.launch {
                                    animatedOffsetX.animateTo(0f, spring())
                                    animatedOffsetY.animateTo(0f, spring())
                                }
                            }
                        }
                        offsetX = 0f
                        offsetY = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                )
            }
    )
}

/**
 * A single flashcard with flip animation.
 */
@Composable
fun Flashcard(
    word: VocabularyEntity,
    isFlipped: Boolean,
    onFlip: () -> Unit,
    onSpeak: () -> Unit,
    swipeState: SwipeState,
    modifier: Modifier = Modifier
) {
    val rotation = remember { Animatable(0f) }

    LaunchedEffect(isFlipped) {
        rotation.animateTo(
            targetValue = if (isFlipped) 180f else 0f,
            animationSpec = tween(durationMillis = 300)
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(400.dp)
            .graphicsLayer {
                rotationY = rotation.value
                cameraDistance = 12f * density
            },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = when {
                swipeState.direction == SwipeDirection.LEFT && swipeState.progress > 0.2f ->
                    Color(0xFFFFEBEE).copy(alpha = 0.5f + swipeState.progress * 0.5f)
                swipeState.direction == SwipeDirection.RIGHT && swipeState.progress > 0.2f ->
                    Color(0xFFE8F5E9).copy(alpha = 0.5f + swipeState.progress * 0.5f)
                swipeState.direction == SwipeDirection.UP && swipeState.progress > 0.2f ->
                    Color(0xFFFFF8E1).copy(alpha = 0.5f + swipeState.progress * 0.5f)
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        onClick = onFlip
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (rotation.value <= 90f) {
                // Front of card
                FlashcardFront(
                    word = word,
                    onSpeak = onSpeak,
                    swipeState = swipeState
                )
            } else {
                // Back of card (mirrored)
                Box(
                    modifier = Modifier.graphicsLayer { rotationY = 180f }
                ) {
                    FlashcardBack(word = word)
                }
            }
        }
    }
}

@Composable
private fun FlashcardFront(
    word: VocabularyEntity,
    onSpeak: () -> Unit,
    swipeState: SwipeState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Swipe indicator icons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left swipe indicator (Don't Know)
            Icon(
                imageVector = ProdyIcons.Close,
                contentDescription = "Don't Know",
                tint = Color.Red.copy(
                    alpha = if (swipeState.direction == SwipeDirection.LEFT)
                        swipeState.progress.coerceIn(0f, 1f) else 0.2f
                ),
                modifier = Modifier.size(32.dp)
            )

            // Up swipe indicator (Skip)
            Icon(
                imageVector = ProdyIcons.SkipNext,
                contentDescription = "Skip",
                tint = Color(0xFFFFA000).copy(
                    alpha = if (swipeState.direction == SwipeDirection.UP)
                        swipeState.progress.coerceIn(0f, 1f) else 0.2f
                ),
                modifier = Modifier.size(32.dp)
            )

            // Right swipe indicator (Know)
            Icon(
                imageVector = ProdyIcons.Check,
                contentDescription = "Know",
                tint = Color(0xFF4CAF50).copy(
                    alpha = if (swipeState.direction == SwipeDirection.RIGHT)
                        swipeState.progress.coerceIn(0f, 1f) else 0.2f
                ),
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Word
        Text(
            text = word.word,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Pronunciation
        if (word.pronunciation.isNotBlank()) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = word.pronunciation,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                IconButton(onClick = onSpeak) {
                    Icon(
                        imageVector = ProdyIcons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Pronounce",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Part of speech
        if (word.partOfSpeech.isNotBlank()) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = word.partOfSpeech,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Hint text
        Text(
            text = "Tap to see definition",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FlashcardBack(word: VocabularyEntity) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Word (smaller on back)
        Text(
            text = word.word,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Definition
        Text(
            text = word.definition,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (word.exampleSentence.isNotBlank()) {
            Spacer(modifier = Modifier.height(24.dp))

            // Example sentence
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = "\"${word.exampleSentence}\"",
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (word.synonyms.isNotBlank()) {
            Spacer(modifier = Modifier.height(16.dp))

            // Synonyms
            Text(
                text = "Synonyms: ${word.synonyms}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Swipeable flashcard with gesture detection.
 */
@Composable
fun SwipeableFlashcard(
    word: VocabularyEntity,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    onSwipeUp: () -> Unit,
    onSpeak: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFlipped by remember { mutableStateOf(false) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var swipeState by remember { mutableStateOf(SwipeState()) }

    val scope = rememberCoroutineScope()
    val animatedOffsetX = remember { Animatable(0f) }
    val animatedOffsetY = remember { Animatable(0f) }

    val swipeThreshold = 150f

    LaunchedEffect(offsetX, offsetY) {
        val direction = when {
            abs(offsetY) > abs(offsetX) && offsetY < -swipeThreshold * 0.3f -> SwipeDirection.UP
            offsetX < -swipeThreshold * 0.3f -> SwipeDirection.LEFT
            offsetX > swipeThreshold * 0.3f -> SwipeDirection.RIGHT
            else -> SwipeDirection.NONE
        }
        val progress = when (direction) {
            SwipeDirection.UP -> (abs(offsetY) / swipeThreshold).coerceIn(0f, 1f)
            SwipeDirection.LEFT, SwipeDirection.RIGHT -> (abs(offsetX) / swipeThreshold).coerceIn(0f, 1f)
            SwipeDirection.NONE -> 0f
        }
        swipeState = SwipeState(direction, progress)
    }

    Flashcard(
        word = word,
        isFlipped = isFlipped,
        onFlip = { isFlipped = !isFlipped },
        onSpeak = onSpeak,
        swipeState = swipeState,
        modifier = modifier
            .offset {
                IntOffset(
                    animatedOffsetX.value.roundToInt() + offsetX.roundToInt(),
                    animatedOffsetY.value.roundToInt() + offsetY.roundToInt()
                )
            }
            .rotate(offsetX / 20f)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        when {
                            // Swipe up (skip)
                            offsetY < -swipeThreshold -> {
                                scope.launch {
                                    animatedOffsetY.animateTo(
                                        -1000f,
                                        spring(stiffness = Spring.StiffnessLow)
                                    )
                                    onSwipeUp()
                                    animatedOffsetY.snapTo(0f)
                                    animatedOffsetX.snapTo(0f)
                                    isFlipped = false
                                }
                            }
                            // Swipe left (don't know)
                            offsetX < -swipeThreshold -> {
                                scope.launch {
                                    animatedOffsetX.animateTo(
                                        -1000f,
                                        spring(stiffness = Spring.StiffnessLow)
                                    )
                                    onSwipeLeft()
                                    animatedOffsetX.snapTo(0f)
                                    animatedOffsetY.snapTo(0f)
                                    isFlipped = false
                                }
                            }
                            // Swipe right (know)
                            offsetX > swipeThreshold -> {
                                scope.launch {
                                    animatedOffsetX.animateTo(
                                        1000f,
                                        spring(stiffness = Spring.StiffnessLow)
                                    )
                                    onSwipeRight()
                                    animatedOffsetX.snapTo(0f)
                                    animatedOffsetY.snapTo(0f)
                                    isFlipped = false
                                }
                            }
                            // Return to center
                            else -> {
                                scope.launch {
                                    animatedOffsetX.animateTo(0f, spring())
                                    animatedOffsetY.animateTo(0f, spring())
                                }
                            }
                        }
                        offsetX = 0f
                        offsetY = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                )
            }
    )
}

/**
 * Control buttons for flashcard actions.
 *
 * When showDetailedOptions is true (card is flipped), shows 4-tier response options:
 * - Again (Don't Know): Failed to recall, short interval
 * - Hard: Recalled with difficulty
 * - Good: Recalled correctly
 * - Easy (Perfect): Recalled effortlessly, longer interval
 *
 * When showDetailedOptions is false, shows simple 3-button controls.
 */
@Composable
fun FlashcardControls(
    onDontKnow: () -> Unit,
    onSkip: () -> Unit,
    onKnow: () -> Unit,
    onHard: (() -> Unit)? = null,
    onPerfect: (() -> Unit)? = null,
    showDetailedOptions: Boolean = false,
    modifier: Modifier = Modifier
) {
    if (showDetailedOptions && onHard != null && onPerfect != null) {
        // 4-tier Anki-style response buttons for better spaced repetition
        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Again (Don't Know) - Red
                ResponseButton(
                    label = "Again",
                    sublabel = "<1m",
                    onClick = onDontKnow,
                    containerColor = Color(0xFFFFCDD2),
                    contentColor = Color(0xFFD32F2F),
                    size = 56
                )

                // Hard - Orange
                ResponseButton(
                    label = "Hard",
                    sublabel = "<10m",
                    onClick = onHard,
                    containerColor = Color(0xFFFFE0B2),
                    contentColor = Color(0xFFE65100),
                    size = 56
                )

                // Good (Know) - Light Green
                ResponseButton(
                    label = "Good",
                    sublabel = "~1d",
                    onClick = onKnow,
                    containerColor = Color(0xFFC8E6C9),
                    contentColor = Color(0xFF388E3C),
                    size = 56
                )

                // Easy (Perfect) - Blue
                ResponseButton(
                    label = "Easy",
                    sublabel = "~4d",
                    onClick = onPerfect,
                    containerColor = Color(0xFFBBDEFB),
                    contentColor = Color(0xFF1565C0),
                    size = 56
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Skip option still available
            Text(
                text = "Swipe up to skip",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    } else {
        // Simple 3-button controls (front of card)
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Don't Know button
            FilledIconButton(
                onClick = onDontKnow,
                modifier = Modifier.size(64.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color(0xFFFFCDD2),
                    contentColor = Color(0xFFD32F2F)
                )
            ) {
                Icon(
                    imageVector = ProdyIcons.Close,
                    contentDescription = "Don't Know",
                    modifier = Modifier.size(32.dp)
                )
            }

            // Skip button
            FilledIconButton(
                onClick = onSkip,
                modifier = Modifier.size(48.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color(0xFFFFE0B2),
                    contentColor = Color(0xFFF57C00)
                )
            ) {
                Icon(
                    imageVector = ProdyIcons.SkipNext,
                    contentDescription = "Skip",
                    modifier = Modifier.size(24.dp)
                )
            }

            // Know button
            FilledIconButton(
                onClick = onKnow,
                modifier = Modifier.size(64.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color(0xFFC8E6C9),
                    contentColor = Color(0xFF388E3C)
                )
            ) {
                Icon(
                    imageVector = ProdyIcons.Check,
                    contentDescription = "Know",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

/**
 * Response button with label and sublabel for interval hint.
 */
@Composable
private fun ResponseButton(
    label: String,
    sublabel: String,
    onClick: () -> Unit,
    containerColor: Color,
    contentColor: Color,
    size: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FilledIconButton(
            onClick = onClick,
            modifier = Modifier.size(size.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = containerColor,
                contentColor = contentColor
            )
        ) {
            Text(
                text = label.first().toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = contentColor
        )
        Text(
            text = sublabel,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}

/**
 * Progress indicator for flashcard session.
 */
@Composable
fun FlashcardProgress(
    current: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Card ${current + 1} of $total",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${((current + 1).toFloat() / total * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { (current + 1).toFloat() / total },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
        )
    }
}

/**
 * Session statistics summary.
 */
@Composable
fun SessionStats(
    known: Int,
    unknown: Int,
    skipped: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(
            label = "Known",
            count = known,
            color = Color(0xFF4CAF50)
        )
        StatItem(
            label = "Learning",
            count = unknown,
            color = Color(0xFFF44336)
        )
        StatItem(
            label = "Skipped",
            count = skipped,
            color = Color(0xFFFF9800)
        )
    }
}

@Composable
private fun StatItem(
    label: String,
    count: Int,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Empty state when no cards are available.
 */
@Composable
fun EmptyFlashcardState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = ProdyIcons.Refresh,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No cards to review",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Great job! Check back later for more words to practice.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
