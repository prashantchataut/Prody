package com.prody.prashant.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.EmojiEmotions
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Handshake
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.Square
import androidx.compose.material.icons.outlined.Terrain
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material.icons.outlined.WavingHand
import androidx.compose.material.icons.outlined.Waves
import androidx.compose.ui.graphics.vector.ImageVector
import com.prody.prashant.domain.haven.ExerciseType
import com.prody.prashant.domain.haven.SessionType

val SessionType.icon: ImageVector
    get() = when (this) {
        SessionType.CHECK_IN -> Icons.Outlined.WavingHand
        SessionType.ANXIETY -> Icons.Outlined.Waves
        SessionType.STRESS -> Icons.Outlined.Terrain
        SessionType.SADNESS -> Icons.Outlined.Favorite
        SessionType.ANGER -> Icons.Outlined.LocalFireDepartment
        SessionType.GENERAL -> Icons.Outlined.Chat
        SessionType.CRISIS_SUPPORT -> Icons.Outlined.Handshake
    }

val ExerciseType.icon: ImageVector
    get() = when (this) {
        ExerciseType.BOX_BREATHING -> Icons.Outlined.Square
        ExerciseType.FOUR_SEVEN_EIGHT_BREATHING -> Icons.Outlined.Air
        ExerciseType.GROUNDING_54321 -> Icons.Outlined.Public
        ExerciseType.BODY_SCAN -> Icons.Outlined.SelfImprovement
        ExerciseType.THOUGHT_RECORD -> Icons.Filled.EditNote
        ExerciseType.EMOTION_WHEEL -> Icons.Outlined.EmojiEmotions
        ExerciseType.GRATITUDE_MOMENT -> Icons.Outlined.VolunteerActivism
        ExerciseType.PROGRESSIVE_RELAXATION -> Icons.Outlined.FitnessCenter
        ExerciseType.LOVING_KINDNESS -> Icons.Outlined.Favorite
    }