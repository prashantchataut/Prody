package com.prody.prashant.util

import com.prody.prashant.domain.model.Mood
import kotlin.random.Random

/**
 * Buddha - The Stoic AI Mentor
 *
 * Buddha provides stoic wisdom, reflective responses, and mentorship
 * to guide users on their self-improvement journey. Buddha is not
 * monotonous but has personality - thoughtful, wise, occasionally
 * using metaphors and timeless wisdom.
 */
object BuddhaWisdom {

    private val openingPhrases = listOf(
        "I hear you, seeker.",
        "Your words carry weight.",
        "This is worth reflecting upon.",
        "Let us examine this together.",
        "There is wisdom in your thoughts.",
        "The path becomes clearer as we walk.",
        "Consider this, dear traveler.",
        "In stillness, we find clarity.",
        "Your journey speaks volumes.",
        "Let me share something with you."
    )

    private val closingWisdom = listOf(
        "Remember: the obstacle is the way.",
        "What stands in the way becomes the way.",
        "This too shall pass, but growth remains.",
        "In the midst of chaos, there is also opportunity.",
        "The best time to plant a tree was yesterday. The second best time is now.",
        "You have power over your mind, not outside events.",
        "The happiness of your life depends upon the quality of your thoughts.",
        "Waste no more time arguing about what a good person should be. Be one.",
        "Very little is needed to make a happy life; it is all within yourself.",
        "He who fears death will never do anything worth of a living man.",
        "The soul becomes dyed with the color of its thoughts.",
        "First say to yourself what you would be; then do what you have to do.",
        "No person has the power to have everything they want, but it is in their power not to want what they don't have.",
        "Difficulties strengthen the mind, as labor does the body.",
        "It is not the man who has too little, but the man who craves more, that is poor."
    )

    private val moodResponses = mapOf(
        Mood.HAPPY to listOf(
            "Joy is not a destination but a manner of traveling. Embrace this moment fully.",
            "Your happiness today is the seed of tomorrow's strength. Nurture it wisely.",
            "A joyful heart sees the world as it truly is - full of possibility.",
            "This contentment you feel - remember it. It is your natural state, often obscured but always present.",
            "Happiness, like all things, is temporary. Not to diminish it, but to savor it more deeply."
        ),
        Mood.CALM to listOf(
            "In calmness lies your greatest power. The still waters reflect most clearly.",
            "You have found what many seek their entire lives. Stay here a while.",
            "Peace is not the absence of storms, but the tranquility within them. You know this now.",
            "This serenity is your true nature revealing itself. It was always there.",
            "A calm mind is a fortress that no external chaos can breach."
        ),
        Mood.ANXIOUS to listOf(
            "Anxiety often speaks of futures that may never arrive. What is real is this moment.",
            "The mind creates mountains where there are molehills. What is truly within your control right now?",
            "Breathe. You have survived every difficult day so far. You will survive this too.",
            "Worry is like paying a debt you may never owe. What can you actually do in this moment?",
            "The anxious mind races ahead. Bring it back. This moment is all we truly have."
        ),
        Mood.SAD to listOf(
            "Sadness is not weakness; it is the heart's way of processing depth. Feel it fully.",
            "Even the mightiest rivers have their low seasons. This is natural, not permanent.",
            "In sadness, we often discover what truly matters to us. There is wisdom here.",
            "The night is darkest before dawn. You are closer to light than you realize.",
            "Allow yourself to grieve what needs grieving. Then, when ready, rise again."
        ),
        Mood.MOTIVATED to listOf(
            "Channel this fire wisely. Energy without direction is chaos; with direction, it moves mountains.",
            "Motivation is the spark, discipline is the flame. Let one ignite the other.",
            "This drive you feel - it is the universe speaking through you. Listen and act.",
            "A motivated mind is unstoppable, but remember: sustainable pace wins the race.",
            "Use this energy not just to do more, but to become more. Growth over mere activity."
        ),
        Mood.GRATEFUL to listOf(
            "Gratitude transforms what we have into enough. You see clearly today.",
            "The grateful heart attracts abundance. You are planting seeds of future joy.",
            "To appreciate is to elevate. Your perspective today is a gift to yourself.",
            "Gratitude is the highest form of thought. It returns us to our natural state.",
            "What you focus on expands. By choosing gratitude, you choose growth."
        ),
        Mood.CONFUSED to listOf(
            "Confusion is often the doorway to deeper understanding. Stay curious, not frustrated.",
            "The wise person admits confusion; the fool pretends certainty. You are wise today.",
            "In not knowing, we create space for learning. This uncertainty is fertile ground.",
            "Clarity comes not from forcing answers, but from sitting patiently with questions.",
            "Sometimes the path reveals itself only one step at a time. Trust the next step."
        ),
        Mood.EXCITED to listOf(
            "Excitement is life announcing its possibilities. Receive this gift with both hands.",
            "Channel this energy into action before it fades. What will you create?",
            "The excited mind sees potential everywhere. Use this vision wisely.",
            "This enthusiasm is contagious - to yourself most of all. Let it fuel your discipline.",
            "Excitement and anxiety are cousins. You've chosen the wiser relative today."
        )
    )

    private val reflectionPrompts = listOf(
        "What would your future self thank you for today?",
        "If this challenge were a teacher, what lesson would it be offering?",
        "What is one small thing you can do right now to honor yourself?",
        "When have you faced something similar and emerged stronger?",
        "What would you tell a dear friend in this same situation?",
        "Is this something you can control, influence, or must accept?",
        "What story are you telling yourself about this? Is it true?",
        "How will this matter in five years? In one year? In one month?",
        "What strength of yours is being called upon right now?",
        "If you acted from your best self, what would you do next?"
    )

    private val encouragements = listOf(
        "You are exactly where you need to be.",
        "The fact that you're reflecting shows remarkable self-awareness.",
        "Your commitment to growth is admirable.",
        "Every master was once a disaster. Keep going.",
        "The journey of a thousand miles continues with your next step.",
        "You are stronger than you know.",
        "Progress, not perfection, is the goal.",
        "Your potential is limitless.",
        "Today's struggles are tomorrow's strengths.",
        "You are writing your story. Make it a good one."
    )

    /**
     * Generates a personalized Buddha response based on journal content and mood.
     * This creates meaningful, stoic-inspired reflections for the user.
     */
    fun generateResponse(content: String, mood: Mood, wordCount: Int): String {
        val response = StringBuilder()

        // Opening
        response.append(openingPhrases.random())
        response.append("\n\n")

        // Mood-specific wisdom
        val moodWisdom = moodResponses[mood]?.random()
            ?: "Your feelings are valid and temporary, like clouds passing through the sky."
        response.append(moodWisdom)
        response.append("\n\n")

        // Content analysis and reflection
        val contentAnalysis = analyzeContent(content, wordCount)
        response.append(contentAnalysis)
        response.append("\n\n")

        // Reflection prompt
        response.append("**Reflect on this:** ")
        response.append(reflectionPrompts.random())
        response.append("\n\n")

        // Closing wisdom
        response.append("*")
        response.append(closingWisdom.random())
        response.append("*")

        return response.toString()
    }

    private fun analyzeContent(content: String, wordCount: Int): String {
        val lowerContent = content.lowercase()

        // Detect themes and provide relevant wisdom
        return when {
            containsAny(lowerContent, listOf("work", "job", "career", "boss", "office", "project")) ->
                "Work consumes much of our lives, yet it is not life itself. The Stoics remind us: we cannot control outcomes, only our efforts. Pour your excellence into your craft, but detach from results. Your worth is not measured by promotions or praise, but by the person you become through honest labor."

            containsAny(lowerContent, listOf("relationship", "friend", "family", "love", "partner", "lonely")) ->
                "Relationships are mirrors - they reflect back what we bring to them. Marcus Aurelius wrote to expect that people will disappoint us, for they are human as we are human. Love fully, but remember: we can only control our own actions, our own kindness. Be the person you wish others would be."

            containsAny(lowerContent, listOf("fear", "scared", "afraid", "worry", "nervous", "anxious")) ->
                "Fear often speaks loudest about things that may never happen. Seneca advised us to practice misfortune - imagine the worst, and you'll find it rarely arrives. And if it does? You've already rehearsed. What you fear has less power than you think. Your resilience is greater than any challenge."

            containsAny(lowerContent, listOf("fail", "failure", "mistake", "wrong", "messed", "regret")) ->
                "Failure is not the opposite of success; it is part of success. Every stumble teaches what cannot be learned from triumph. The obstacle has become your way forward. What wisdom has this difficulty offered? How has it carved you into something stronger?"

            containsAny(lowerContent, listOf("goal", "dream", "future", "plan", "achieve", "want")) ->
                "Goals are stars by which we navigate, but the journey itself is the destination. Focus not on distant shores but on rowing well today. What can you do *now*, in this moment, that serves your vision? Small actions, consistently taken, move mountains."

            containsAny(lowerContent, listOf("tired", "exhausted", "burnout", "overwhelmed", "stressed")) ->
                "Rest is not a reward for productivity; it is essential fuel for it. Even the mightiest warriors knew when to sheathe their swords. There is no virtue in running yourself empty. What can you release? What can you delegate? What can you simply let go?"

            containsAny(lowerContent, listOf("grateful", "thankful", "appreciate", "blessed", "lucky")) ->
                "You've discovered a profound truth: gratitude is the foundation of contentment. Epictetus, a former slave, found freedom through appreciation of what he had, not longing for what he lacked. This perspective you've cultivated is worth more than gold."

            containsAny(lowerContent, listOf("change", "different", "new", "transform", "grow")) ->
                "Change is the only constant - the Stoics knew this well. You cannot step in the same river twice, for it is not the same river and you are not the same person. Embrace this flux. The person you are becoming awaits on the other side of your courage."

            wordCount > 200 ->
                "You've poured much onto this page - a sign of a mind actively processing and seeking clarity. This practice of externalization, of putting thoughts into words, is ancient wisdom. What patterns do you notice in your own words? What truth emerges?"

            wordCount < 50 ->
                "Sometimes the deepest truths require few words. In brevity, there is often essence. But I wonder - is there more beneath the surface? What would you say if no one would ever read it? Let your pen flow without judgment."

            else ->
                "Your reflection shows someone engaged with their own growth. This is the first and most important step. The unexamined life, Socrates warned, is not worth living. By examining, you are truly living."
        }
    }

    private fun containsAny(text: String, keywords: List<String>): Boolean {
        return keywords.any { text.contains(it) }
    }

    /**
     * Gets a random encouragement for notifications or quick inspiration.
     */
    fun getRandomEncouragement(): String {
        return encouragements.random()
    }

    /**
     * Gets a reflection prompt for the day.
     */
    fun getDailyReflectionPrompt(): String {
        return reflectionPrompts.random()
    }

    /**
     * Generates a summary insight from multiple journal entries.
     */
    fun generateWeeklySummary(entriesCount: Int, dominantMood: Mood?, streakDays: Int): String {
        val summary = StringBuilder()

        summary.append("**Your Week in Reflection**\n\n")

        if (entriesCount > 0) {
            summary.append("You've journaled $entriesCount time${if (entriesCount > 1) "s" else ""} this week. ")
            summary.append("Each entry is a step toward self-understanding.\n\n")
        } else {
            summary.append("Your journal awaits your thoughts. Even a single entry can spark insight.\n\n")
        }

        dominantMood?.let {
            summary.append("Your prevailing mood has been **${it.displayName}**. ")
            summary.append("${getMoodSummaryInsight(it)}\n\n")
        }

        when {
            streakDays >= 7 -> summary.append("Your $streakDays-day streak shows remarkable commitment. Consistency is the compound interest of self-improvement.")
            streakDays >= 3 -> summary.append("A $streakDays-day streak is building. Small disciplines lead to great achievements.")
            streakDays > 0 -> summary.append("Your streak begins at $streakDays day${if (streakDays > 1) "s" else ""}. Every journey starts with a single step.")
            else -> summary.append("Today is perfect for beginning anew. The best time to start was yesterday; the second best is now.")
        }

        return summary.toString()
    }

    private fun getMoodSummaryInsight(mood: Mood): String {
        return when (mood) {
            Mood.HAPPY -> "Joy has been your companion - may you share it generously."
            Mood.CALM -> "Serenity has been your state - a precious and powerful condition."
            Mood.ANXIOUS -> "You've faced uncertainty with courage by continuing to reflect."
            Mood.SAD -> "You've honored your feelings by acknowledging them."
            Mood.MOTIVATED -> "Your drive has been strong - channel it into lasting change."
            Mood.GRATEFUL -> "Appreciation has colored your week - the wisest lens."
            Mood.CONFUSED -> "You've sat with uncertainty rather than fleeing it - that takes strength."
            Mood.EXCITED -> "Enthusiasm has fueled your days - harness it for growth."
        }
    }
}
