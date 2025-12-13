package com.prody.prashant.domain.model

/**
 * Centralized celebration and notification messages.
 * All messages are elegant, literary, and contain NO EMOJIS.
 */
object CelebrationMessages {

    // ===== Achievement Messages =====

    fun achievementUnlocked(achievementName: String, celebrationMessage: String): String {
        return "Achievement Unlocked: $achievementName\n$celebrationMessage"
    }

    fun achievementProgress(achievementName: String, current: Int, required: Int): String {
        val percentage = ((current.toFloat() / required) * 100).toInt()
        return "$achievementName: $current of $required ($percentage% complete)"
    }

    // ===== Rank Messages =====

    fun rankAdvancement(newRank: String, description: String): String {
        return "You have become: $newRank\n$description"
    }

    fun rankProgress(currentRank: String, nextRank: String, pointsNeeded: Int): String {
        return "$pointsNeeded points until $nextRank"
    }

    // ===== Streak Messages =====

    fun streakMilestone(days: Int): String {
        return when {
            days == 1 -> "The first day of many. Welcome to the path."
            days == 3 -> "Three days of presence. The kindling has caught."
            days == 7 -> "Seven days without breaking. A week of dedication."
            days == 14 -> "A fortnight of commitment. The habit takes form."
            days == 21 -> "Twenty-one days. They say habits are forged in this time."
            days == 30 -> "One moon's cycle complete. The practice is becoming part of you."
            days == 60 -> "Two months of presence. This is no longer effort-it is identity."
            days == 90 -> "A full season of growth. Transformation is evident."
            days == 180 -> "Half a year. You have walked farther than most ever will."
            days == 365 -> "One complete orbit around the sun, present each day. This is mastery."
            days % 100 == 0 -> "$days days of presence. A remarkable journey continues."
            days % 50 == 0 -> "$days days. The path stretches behind you, long and true."
            else -> "$days days and counting. Keep walking."
        }
    }

    fun streakLost(previousStreak: Int): String {
        return when {
            previousStreak < 7 -> "The streak has ended at $previousStreak days. Begin again-every master has stumbled."
            previousStreak < 30 -> "A $previousStreak-day streak concludes. What matters now is the next step."
            else -> "A $previousStreak-day journey pauses. Such dedication is not erased by absence."
        }
    }

    // ===== Banner Messages =====

    fun bannerUnlocked(bannerName: String): String {
        return "New banner available: $bannerName"
    }

    fun bannerSelected(bannerName: String): String {
        return "Profile banner updated to: $bannerName"
    }

    // ===== Level Messages =====

    fun levelUp(newLevel: Int): String {
        return when (newLevel) {
            2 -> "Level 2 attained. The foundation is laid."
            3 -> "Level 3 reached. You are finding your rhythm."
            4 -> "Level 4 achieved. Consistency bears fruit."
            5 -> "Level 5 unlocked. You stand at the midpoint, looking back at growth."
            6 -> "Level 6 attained. Few walk this far."
            7 -> "Level 7 reached. The path grows clearer with each step."
            8 -> "Level 8 achieved. Wisdom accumulates."
            9 -> "Level 9 unlocked. The summit draws near."
            10 -> "Level 10 attained. You have reached the pinnacle-yet the journey continues."
            else -> "Level $newLevel attained. Onward."
        }
    }

    // ===== Daily Greetings =====

    fun dailyGreeting(displayName: String, hour: Int): String {
        return when (hour) {
            in 5..8 -> "The early hours find you here, $displayName. The day is ripe with possibility."
            in 9..11 -> "Good morning, $displayName. What wisdom will you gather today?"
            in 12..14 -> "Midday greetings, $displayName. A moment of reflection amidst the day."
            in 15..17 -> "Good afternoon, $displayName. The day matures."
            in 18..20 -> "Evening approaches, $displayName. Time for contemplation."
            in 21..23 -> "The quiet hours welcome you, $displayName. Reflection deepens in stillness."
            else -> "The night holds its own wisdom, $displayName. Welcome."
        }
    }

    // ===== Journal Messages =====

    fun journalMilestone(entries: Int): String {
        return when (entries) {
            1 -> "Your first journal entry. The practice of self-examination begins."
            10 -> "Ten entries written. A conversation with yourself grows richer."
            25 -> "Twenty-five reflections. Patterns begin to emerge."
            50 -> "Fifty entries. Half a hundred glimpses into your own depths."
            100 -> "One hundred journal entries. A true memoir of the soul."
            else -> if (entries % 50 == 0) {
                "$entries entries written. The chronicle continues."
            } else {
                "Entry $entries recorded."
            }
        }
    }

    // ===== Word Learning Messages =====

    fun wordLearned(word: String): String {
        return "A new word joins your vocabulary: $word"
    }

    fun wordMilestone(count: Int): String {
        return when (count) {
            1 -> "Your first word. A vocabulary is a garden-you have planted the first seed."
            10 -> "Ten words learned. The garden begins to take shape."
            25 -> "Twenty-five words. Language grows within you."
            50 -> "Fifty words. A craftsman's toolkit begins to form."
            100 -> "One hundred words. A lexicon worthy of respect."
            250 -> "Two hundred fifty words. Expression knows fewer limits."
            500 -> "Five hundred words. You have become a keeper of language."
            else -> if (count % 100 == 0) {
                "$count words learned. The vocabulary expands."
            } else {
                "Words learned: $count"
            }
        }
    }

    // ===== Buddha/AI Conversation Messages =====

    fun buddhaConversation(count: Int): String {
        return when (count) {
            1 -> "Your first dialogue with Buddha. The conversation begins."
            10 -> "Ten exchanges with wisdom. You are learning to ask."
            50 -> "Fifty conversations. The sage within awakens."
            100 -> "One hundred dialogues. You have become a true student of life."
            else -> if (count % 25 == 0) {
                "$count conversations with Buddha. The dialogue deepens."
            } else {
                "Conversation $count recorded."
            }
        }
    }

    // ===== Future Letter Messages =====

    fun futureLetterSent(deliveryDays: Int): String {
        return "Your words now travel $deliveryDays days into the future. May they arrive with meaning."
    }

    fun futureLetterReceived(): String {
        return "A message from your past self has arrived. Take time to listen."
    }

    // ===== Error Messages =====

    fun errorGeneric(): String {
        return "Something unexpected occurred. Please try again."
    }

    fun errorNetwork(): String {
        return "Unable to connect. Please check your connection and try again."
    }

    fun errorSaving(): String {
        return "Unable to save your progress. Please try again."
    }

    // ===== Empty States =====

    fun emptyJournal(): String {
        return "Your journal awaits its first entry. Begin the practice of self-reflection."
    }

    fun emptyAchievements(): String {
        return "Achievements are earned through presence and practice. Your journey begins now."
    }

    fun emptyWords(): String {
        return "Words of wisdom are gathered one by one. Your vocabulary journey starts today."
    }

    fun emptyLetters(): String {
        return "Write to your future self. Your words will travel through time."
    }

    // ===== Points Messages =====

    fun pointsEarned(points: Int, reason: String): String {
        return "+$points points for $reason"
    }

    fun pointsMilestone(total: Int): String {
        return when {
            total >= 10000 -> "Ten thousand points accumulated. A legend in the making."
            total >= 5000 -> "Five thousand points. Your dedication speaks volumes."
            total >= 1000 -> "One thousand points achieved. The journey bears fruit."
            total >= 500 -> "Five hundred points. Progress becomes visible."
            total >= 100 -> "One hundred points. The first milestone reached."
            else -> "Points accumulated: $total"
        }
    }

    // ===== Avatar Messages =====

    fun avatarUnlocked(avatarName: String): String {
        return "New avatar available: $avatarName"
    }

    fun avatarSelected(avatarName: String): String {
        return "Profile avatar updated to: $avatarName"
    }

    // ===== Title Messages =====

    fun titleUnlocked(titleName: String): String {
        return "New title earned: $titleName"
    }

    fun titleSelected(titleName: String): String {
        return "You now carry the title: $titleName"
    }
}
