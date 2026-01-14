package com.prody.prashant.ui.components
import com.prody.prashant.ui.icons.ProdyIcons

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prody.prashant.data.local.entity.VocabularyEntity
import com.prody.prashant.domain.vocabulary.VocabularySuggestion
import com.prody.prashant.ui.theme.PoppinsFamily

/**
 * Subtle vocabulary suggestion chip that appears while writing.
 * Shows relevant words that could enhance the writing.
 */
@Composable
fun VocabularySuggestionSection(
    suggestions: List<VocabularySuggestion>,
    onSuggestionClick: (VocabularyEntity) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = suggestions.isNotEmpty(),
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = ProdyIcons.Lightbulb,
                        contentDescription = null,
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Vocabulary Suggestions",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = ProdyIcons.Close,
                        contentDescription = "Dismiss",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Suggestion chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(suggestions) { suggestion ->
                    VocabularySuggestionChip(
                        suggestion = suggestion,
                        onClick = { onSuggestionClick(suggestion.word) }
                    )
                }
            }
        }
    }
}

/**
 * Individual suggestion chip.
 */
@Composable
private fun VocabularySuggestionChip(
    suggestion: VocabularySuggestion,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFFFC107).copy(alpha = 0.1f),
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .width(140.dp)
                .padding(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = suggestion.word.word,
                    fontFamily = PoppinsFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = suggestion.word.partOfSpeech.ifEmpty { "word" },
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = suggestion.word.definition.take(40) + if (suggestion.word.definition.length > 40) "..." else "",
                fontFamily = PoppinsFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 11.sp,
                lineHeight = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
        }
    }
}

/**
 * Celebration badge shown when a word is used in context.
 */
@Composable
fun VocabularyCelebrationBadge(
    word: String,
    bonusPoints: Int,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onDismiss),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF4CAF50),
            tonalElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "🎉",
                        fontSize = 20.sp
                    )

                    Column {
                        Text(
                            text = "You used \"$word\" in context!",
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = Color.White
                        )

                        Text(
                            text = "+$bonusPoints Discipline",
                            fontFamily = PoppinsFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = ProdyIcons.Close,
                        contentDescription = "Dismiss",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * Inline celebration shown in the content after saving.
 */
@Composable
fun InlineVocabularyCelebrations(
    celebrations: List<Pair<String, Int>>, // word to bonus points
    modifier: Modifier = Modifier
) {
    if (celebrations.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        celebrations.forEach { (word, points) ->
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF4CAF50).copy(alpha = 0.15f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "✓",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF4CAF50)
                    )

                    Text(
                        text = "You used \"$word\" +$points Discipline",
                        fontFamily = PoppinsFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
