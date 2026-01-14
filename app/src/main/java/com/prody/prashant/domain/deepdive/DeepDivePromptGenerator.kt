package com.prody.prashant.domain.deepdive

import com.prody.prashant.data.local.dao.DeepDiveDao
import com.prody.prashant.data.local.dao.JournalDao
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * Generates thoughtful, varied prompts for Deep Dive sessions.
 *
 * Features:
 * - Multiple variations per theme (5+ each)
 * - Personalization based on user history
 * - Seasonal/contextual variations
 * - Follow-up prompts based on previous responses
 */
@Singleton
class DeepDivePromptGenerator @Inject constructor(
    private val deepDiveDao: DeepDiveDao,
    private val journalDao: JournalDao
) {

    /**
     * Generate prompts for a specific theme and variation
     */
    fun generatePrompts(
        theme: DeepDiveTheme,
        variation: Int = 0,
        previousCompletions: Int = 0
    ): DeepDivePrompt {
        // Select variation, cycling through if user has done this theme before
        val selectedVariation = if (previousCompletions > 0) {
            (variation + previousCompletions) % 5
        } else {
            variation
        }

        return when (theme) {
            DeepDiveTheme.GRATITUDE -> generateGratitudePrompts(selectedVariation)
            DeepDiveTheme.GROWTH -> generateGrowthPrompts(selectedVariation)
            DeepDiveTheme.RELATIONSHIPS -> generateRelationshipsPrompts(selectedVariation)
            DeepDiveTheme.PURPOSE -> generatePurposePrompts(selectedVariation)
            DeepDiveTheme.FEAR -> generateFearPrompts(selectedVariation)
            DeepDiveTheme.JOY -> generateJoyPrompts(selectedVariation)
            DeepDiveTheme.FORGIVENESS -> generateForgivenessPrompts(selectedVariation)
            DeepDiveTheme.AMBITION -> generateAmbitionPrompts(selectedVariation)
        }
    }

    /**
     * Generate personalized prompts based on user's journal history
     */
    suspend fun generatePersonalizedPrompts(
        userId: String,
        theme: DeepDiveTheme
    ): DeepDivePrompt {
        val completedCount = deepDiveDao.getCompletedCountByTheme(userId, theme.id)
        val recentMoods = getRecentMoodTrend(userId)
        val hasLowMoodRecently = recentMoods.any { it.contains("sad", ignoreCase = true) ||
                                                     it.contains("anxious", ignoreCase = true) }

        // Adjust variation based on context
        val variation = when {
            completedCount == 0 && hasLowMoodRecently -> 0 // Gentle first-time prompts
            completedCount > 0 && hasLowMoodRecently -> 1 // Supportive returning prompts
            completedCount > 2 -> 2 + (completedCount % 3) // Deeper, varied prompts
            else -> Random.nextInt(5)
        }

        return generatePrompts(theme, variation, completedCount)
    }

    // ==================== GRATITUDE PROMPTS ====================

    private fun generateGratitudePrompts(variation: Int): DeepDivePrompt {
        return when (variation % 5) {
            0 -> DeepDivePrompt(
                theme = DeepDiveTheme.GRATITUDE,
                variation = 0,
                openingQuestion = "Take a moment to settle in. What small thing made you smile recently?",
                coreQuestions = listOf(
                    "Think of someone who helped shape who you are. What would you tell them if you could?",
                    "What challenge from your past are you now grateful for? How did it change you?",
                    "Look around your life right now. What do you have that past-you would be amazed by?"
                ),
                insightPrompt = "What pattern do you notice in what you're grateful for? What does this reveal about what matters most to you?",
                commitmentPrompt = "How might you carry this gratitude into tomorrow? What's one small way you could express it?"
            )
            1 -> DeepDivePrompt(
                theme = DeepDiveTheme.GRATITUDE,
                variation = 1,
                openingQuestion = "Close your eyes for a moment. What feeling of warmth or comfort comes to mind?",
                coreQuestions = listOf(
                    "What simple pleasure have you taken for granted lately? Why is it actually precious?",
                    "Think of a difficult moment when someone showed up for you. What did it mean?",
                    "What part of your body works hard for you every day? How can you thank it?"
                ),
                insightPrompt = "Looking at these reflections, what gift keeps appearing in your life, even in disguise?",
                commitmentPrompt = "Choose one person or thing you're grateful for. How will you honor that gratitude this week?"
            )
            2 -> DeepDivePrompt(
                theme = DeepDiveTheme.GRATITUDE,
                variation = 2,
                openingQuestion = "What made you feel seen, heard, or understood recently, even if just for a moment?",
                coreQuestions = listOf(
                    "What skill or ability do you possess that you didn't have five years ago? How has it enriched your life?",
                    "Think of a place that feels like home to you. What does it give you?",
                    "What lesson did you resist learning but are now glad you know?"
                ),
                insightPrompt = "What thread connects these moments of gratitude? What does it say about what nourishes you?",
                commitmentPrompt = "What practice could help you stay aware of these gifts? What's the smallest version you could try?"
            )
            3 -> DeepDivePrompt(
                theme = DeepDiveTheme.GRATITUDE,
                variation = 3,
                openingQuestion = "Breathe deeply. What opportunity exists in your life right now that you haven't fully appreciated?",
                coreQuestions = listOf(
                    "Who taught you something important without trying to teach you?",
                    "What ending or loss eventually led to something meaningful?",
                    "What do you have access to that millions of people would consider a luxury?"
                ),
                insightPrompt = "What surprising blessing have you discovered in these reflections?",
                commitmentPrompt = "How might your awareness of these blessings change how you move through tomorrow?"
            )
            else -> DeepDivePrompt(
                theme = DeepDiveTheme.GRATITUDE,
                variation = 4,
                openingQuestion = "What aspect of today would you miss if it were gone tomorrow?",
                coreQuestions = listOf(
                    "What quality in yourself are you finally learning to appreciate?",
                    "Think of someone who believed in you when you didn't believe in yourself. What did they see?",
                    "What 'ordinary' moment from this week actually held something sacred?"
                ),
                insightPrompt = "What form does abundance take in your life, beyond material things?",
                commitmentPrompt = "What daily reminder could help you remember this abundance? Where will you place it?"
            )
        }
    }

    // ==================== GROWTH PROMPTS ====================

    private fun generateGrowthPrompts(variation: Int): DeepDivePrompt {
        return when (variation % 5) {
            0 -> DeepDivePrompt(
                theme = DeepDiveTheme.GROWTH,
                variation = 0,
                openingQuestion = "Think back to who you were a year ago. What feels different about you now?",
                coreQuestions = listOf(
                    "What belief about yourself or the world have you outgrown?",
                    "What strength have you developed through necessity?",
                    "What would younger-you be most proud of about who you are today?"
                ),
                insightPrompt = "What's the most significant way you've evolved? What made that growth possible?",
                commitmentPrompt = "What's the next edge of growth calling to you? What small step could you take toward it?"
            )
            1 -> DeepDivePrompt(
                theme = DeepDiveTheme.GROWTH,
                variation = 1,
                openingQuestion = "When did you surprise yourself recently with your own capability?",
                coreQuestions = listOf(
                    "What mistake taught you more than any success could?",
                    "What fear have you made friends with instead of running from?",
                    "How has your definition of success or happiness shifted?"
                ),
                insightPrompt = "What pattern of transformation do you see in your journey? What's your growth style?",
                commitmentPrompt = "What aspect of yourself deserves more compassion as you continue growing?"
            )
            2 -> DeepDivePrompt(
                theme = DeepDiveTheme.GROWTH,
                variation = 2,
                openingQuestion = "What hard truth have you recently accepted about yourself or life?",
                coreQuestions = listOf(
                    "What part of your identity are you letting go of to make room for who you're becoming?",
                    "What conversation or experience catalyzed a shift in your perspective?",
                    "What can you do now with ease that once felt impossible?"
                ),
                insightPrompt = "What does your growth story reveal about your resilience?",
                commitmentPrompt = "What new behavior or habit would honor the person you're growing into?"
            )
            3 -> DeepDivePrompt(
                theme = DeepDiveTheme.GROWTH,
                variation = 3,
                openingQuestion = "What discomfort have you been sitting with lately? What might it be teaching you?",
                coreQuestions = listOf(
                    "What old coping mechanism are you ready to release?",
                    "How has your relationship with failure or imperfection evolved?",
                    "What parts of yourself are you finally integrating instead of hiding?"
                ),
                insightPrompt = "What wisdom have you earned through your growing pains?",
                commitmentPrompt = "What practice or support could nurture your continued transformation?"
            )
            else -> DeepDivePrompt(
                theme = DeepDiveTheme.GROWTH,
                variation = 4,
                openingQuestion = "What version of yourself have you been shedding? What's emerging in its place?",
                coreQuestions = listOf(
                    "What boundary have you learned to set that you couldn't before?",
                    "How has your inner voice become kinder or more truthful?",
                    "What dream or goal feels more possible now than ever before?"
                ),
                insightPrompt = "What does your growth trajectory tell you about where you're headed?",
                commitmentPrompt = "How will you celebrate how far you've come, even as you continue forward?"
            )
        }
    }

    // ==================== RELATIONSHIPS PROMPTS ====================

    private fun generateRelationshipsPrompts(variation: Int): DeepDivePrompt {
        return when (variation % 5) {
            0 -> DeepDivePrompt(
                theme = DeepDiveTheme.RELATIONSHIPS,
                variation = 0,
                openingQuestion = "Who comes to mind when you think about feeling truly understood?",
                coreQuestions = listOf(
                    "What quality do your closest relationships share? What does this say about what you value?",
                    "When have you felt most connected to someone? What made that moment special?",
                    "What pattern do you notice in the relationships that drain you versus those that energize you?"
                ),
                insightPrompt = "What have your relationships been teaching you about yourself?",
                commitmentPrompt = "What relationship deserves more of your attention and care? How will you show up differently?"
            )
            1 -> DeepDivePrompt(
                theme = DeepDiveTheme.RELATIONSHIPS,
                variation = 1,
                openingQuestion = "Think of someone you miss. What specifically do you miss about them?",
                coreQuestions = listOf(
                    "What do you need to say to someone but haven't yet? What's holding you back?",
                    "How has a difficult relationship actually helped you grow stronger boundaries?",
                    "Who accepts you exactly as you are, without trying to change you?"
                ),
                insightPrompt = "What does authentic connection mean to you? What are its essential ingredients?",
                commitmentPrompt = "What vulnerability could deepen a relationship that matters to you?"
            )
            2 -> DeepDivePrompt(
                theme = DeepDiveTheme.RELATIONSHIPS,
                variation = 2,
                openingQuestion = "When did someone's presence make everything feel more manageable?",
                coreQuestions = listOf(
                    "What relationship have you outgrown? What are you learning from letting it evolve or end?",
                    "How do you show love? How do you prefer to receive it?",
                    "What relationship taught you what you won't tolerate anymore?"
                ),
                insightPrompt = "What role do you tend to play in relationships? Is that role serving you?",
                commitmentPrompt = "What relationship pattern are you ready to break? What will you do instead?"
            )
            3 -> DeepDivePrompt(
                theme = DeepDiveTheme.RELATIONSHIPS,
                variation = 3,
                openingQuestion = "Who has surprised you by showing up in an unexpected way?",
                coreQuestions = listOf(
                    "What do you wish you could tell your family about who you really are?",
                    "Which relationship have you taken for granted? Why?",
                    "What did a past relationship teach you that you couldn't have learned any other way?"
                ),
                insightPrompt = "What does belonging mean to you? Where do you truly belong?",
                commitmentPrompt = "What gesture of connection could you offer someone this week, without expecting anything back?"
            )
            else -> DeepDivePrompt(
                theme = DeepDiveTheme.RELATIONSHIPS,
                variation = 4,
                openingQuestion = "What does your ideal friendship look and feel like?",
                coreQuestions = listOf(
                    "How has your relationship with yourself changed your relationships with others?",
                    "What do you appreciate about being alone? What does solitude give you?",
                    "Who brings out your best self? What do they do differently?"
                ),
                insightPrompt = "What balance are you seeking between connection and independence?",
                commitmentPrompt = "How will you nurture both your relationships and your relationship with yourself?"
            )
        }
    }

    // ==================== PURPOSE PROMPTS ====================

    private fun generatePurposePrompts(variation: Int): DeepDivePrompt {
        return when (variation % 5) {
            0 -> DeepDivePrompt(
                theme = DeepDiveTheme.PURPOSE,
                variation = 0,
                openingQuestion = "What activity makes you lose track of time? What is it about that experience?",
                coreQuestions = listOf(
                    "If you knew you couldn't fail, what would you dedicate your life to?",
                    "What problem in the world makes your heart ache? What draws you to it?",
                    "When do you feel most alive and aligned with yourself?"
                ),
                insightPrompt = "What thread connects the moments when you feel most purposeful?",
                commitmentPrompt = "What's one way you could align your daily life more with your sense of purpose?"
            )
            1 -> DeepDivePrompt(
                theme = DeepDiveTheme.PURPOSE,
                variation = 1,
                openingQuestion = "What impact do you want to have on the people around you?",
                coreQuestions = listOf(
                    "What gift or talent do you have that the world needs?",
                    "What legacy do you want to leave behind, big or small?",
                    "What would you do even if no one ever knew you did it?"
                ),
                insightPrompt = "What is your unique contribution? What can you offer that no one else can in quite the same way?",
                commitmentPrompt = "What small step could move you closer to living your purpose today?"
            )
            2 -> DeepDivePrompt(
                theme = DeepDiveTheme.PURPOSE,
                variation = 2,
                openingQuestion = "What were you doing the last time you felt deeply fulfilled?",
                coreQuestions = listOf(
                    "What injustice or suffering do you want to help heal?",
                    "What would you teach if you were asked to share wisdom?",
                    "What do people come to you for? What need do you naturally fulfill?"
                ),
                insightPrompt = "What meaning have you been creating in your life, whether you realized it or not?",
                commitmentPrompt = "What story about your purpose are you ready to rewrite?"
            )
            3 -> DeepDivePrompt(
                theme = DeepDiveTheme.PURPOSE,
                variation = 3,
                openingQuestion = "What makes you feel that your existence matters?",
                coreQuestions = listOf(
                    "If your purpose isn't your job, what is it? How does it express itself?",
                    "What future are you trying to help create?",
                    "What suffering have you experienced that could help others heal?"
                ),
                insightPrompt = "What is asking to be born through you? What wants to come into the world through your life?",
                commitmentPrompt = "What fear about living your purpose are you ready to face?"
            )
            else -> DeepDivePrompt(
                theme = DeepDiveTheme.PURPOSE,
                variation = 4,
                openingQuestion = "At the end of your life, what do you want to have mattered?",
                coreQuestions = listOf(
                    "What breaks your heart and fills it at the same time?",
                    "How has your sense of purpose evolved over time? Where is it taking you?",
                    "What would you do if you trusted that your purpose would provide for you?"
                ),
                insightPrompt = "What is your 'why' - the reason you're here that goes beyond success or survival?",
                commitmentPrompt = "What daily ritual could keep you connected to your purpose?"
            )
        }
    }

    // ==================== FEAR PROMPTS ====================

    private fun generateFearPrompts(variation: Int): DeepDivePrompt {
        return when (variation % 5) {
            0 -> DeepDivePrompt(
                theme = DeepDiveTheme.FEAR,
                variation = 0,
                openingQuestion = "What fear has been following you around lately, even if you've been ignoring it?",
                coreQuestions = listOf(
                    "What are you actually afraid will happen if this fear comes true?",
                    "When did you first learn to be afraid of this? What taught you this fear?",
                    "What might you gain if you no longer carried this fear?"
                ),
                insightPrompt = "What is this fear trying to protect you from? What would it look like to thank it and then choose courage anyway?",
                commitmentPrompt = "What's the smallest brave thing you could do to face this fear?"
            )
            1 -> DeepDivePrompt(
                theme = DeepDiveTheme.FEAR,
                variation = 1,
                openingQuestion = "What would you do if you weren't afraid? Really, what would you do?",
                coreQuestions = listOf(
                    "What fear have you already survived that once felt insurmountable?",
                    "What's the difference between healthy caution and limiting fear in your life?",
                    "Who would you be without this particular fear? How would you move through the world?"
                ),
                insightPrompt = "What pattern do your fears share? What are they all guarding against?",
                commitmentPrompt = "What support or resource could help you face this fear? Who could walk beside you?"
            )
            2 -> DeepDivePrompt(
                theme = DeepDiveTheme.FEAR,
                variation = 2,
                openingQuestion = "What opportunity have you avoided because of fear? What's been the cost?",
                coreQuestions = listOf(
                    "What's the worst that could realistically happen? Could you survive it?",
                    "What fear did you inherit from your family or culture? Is it even yours?",
                    "When have you been brave without realizing it? What made that possible?"
                ),
                insightPrompt = "What would courage look like for you right now, in this specific situation?",
                commitmentPrompt = "What phrase or image could you return to when this fear rises up?"
            )
            3 -> DeepDivePrompt(
                theme = DeepDiveTheme.FEAR,
                variation = 3,
                openingQuestion = "What are you afraid people will think, see, or know about you?",
                coreQuestions = listOf(
                    "What part of yourself are you hiding because you're afraid it's unacceptable?",
                    "What does your fear of failure cost you? What about your fear of success?",
                    "What would self-compassion say to your fear right now?"
                ),
                insightPrompt = "What truth lives on the other side of this fear?",
                commitmentPrompt = "What would it mean to befriend your fear rather than fight it? How might you start?"
            )
            else -> DeepDivePrompt(
                theme = DeepDiveTheme.FEAR,
                variation = 4,
                openingQuestion = "If your fear had a voice, what would it say? What is it trying to tell you?",
                coreQuestions = listOf(
                    "What fear keeps you playing small? What would expanding look like?",
                    "What story have you been telling yourself about what you can't do or can't be?",
                    "What would you attempt if you knew you were braver than you believe?"
                ),
                insightPrompt = "What is the invitation hidden within this fear? What growth is waiting?",
                commitmentPrompt = "What act of courage will you commit to, however small, as a way of honoring your brave heart?"
            )
        }
    }

    // ==================== JOY PROMPTS ====================

    private fun generateJoyPrompts(variation: Int): DeepDivePrompt {
        return when (variation % 5) {
            0 -> DeepDivePrompt(
                theme = DeepDiveTheme.JOY,
                variation = 0,
                openingQuestion = "When was the last time you felt genuinely delighted? What sparked it?",
                coreQuestions = listOf(
                    "What simple pleasure have you been denying yourself? Why?",
                    "What made you laugh until your stomach hurt? What was so funny?",
                    "What activity makes you feel like a kid again?"
                ),
                insightPrompt = "What pattern do you notice in what brings you joy? What does this reveal about your spirit?",
                commitmentPrompt = "How can you invite more of this joy into your daily life? What's one way you'll prioritize delight?"
            )
            1 -> DeepDivePrompt(
                theme = DeepDiveTheme.JOY,
                variation = 1,
                openingQuestion = "What small moment of beauty did you almost miss today?",
                coreQuestions = listOf(
                    "What hobby or interest lights you up but feels 'impractical'?",
                    "When do you feel most playful and free? What allows that?",
                    "What brings you quiet contentment versus exhilarating excitement? Do you need both?"
                ),
                insightPrompt = "What would a life rich in joy look like for you? Not just happiness - deep, resonant joy?",
                commitmentPrompt = "What permission do you need to give yourself to experience more joy?"
            )
            2 -> DeepDivePrompt(
                theme = DeepDiveTheme.JOY,
                variation = 2,
                openingQuestion = "What are you looking forward to? How does that anticipation feel in your body?",
                coreQuestions = listOf(
                    "What creative expression brings you joy? When did you last create something just for fun?",
                    "What sensory experience delights you - a taste, smell, sound, texture, or sight?",
                    "Who makes you laugh? What is it about them or your dynamic together?"
                ),
                insightPrompt = "What false belief about joy or pleasure are you ready to release?",
                commitmentPrompt = "What joyful ritual could you establish? Something that says 'I deserve delight'?"
            )
            3 -> DeepDivePrompt(
                theme = DeepDiveTheme.JOY,
                variation = 3,
                openingQuestion = "What experience makes you feel fully alive and present?",
                coreQuestions = listOf(
                    "What 'guilty pleasure' could you enjoy guilt-free? Why does it bring you joy?",
                    "What moment of spontaneous joy surprised you recently?",
                    "What would you do for the pure joy of it, even if you weren't 'good' at it?"
                ),
                insightPrompt = "Where has joy been hiding in your life, waiting for you to notice it?",
                commitmentPrompt = "What 'no' could create space for more 'yes' to joy?"
            )
            else -> DeepDivePrompt(
                theme = DeepDiveTheme.JOY,
                variation = 4,
                openingQuestion = "What does your joy look like? How does it sound, feel, taste?",
                coreQuestions = listOf(
                    "What childhood joy have you lost touch with? Could you reclaim it?",
                    "What brings you joy that others might not understand? Why is that okay?",
                    "What moment of connection filled you with warmth recently?"
                ),
                insightPrompt = "What is joy teaching you about what makes life worth living?",
                commitmentPrompt = "How will you protect and prioritize your joy, even when life gets hard?"
            )
        }
    }

    // ==================== FORGIVENESS PROMPTS ====================

    private fun generateForgivenessPrompts(variation: Int): DeepDivePrompt {
        return when (variation % 5) {
            0 -> DeepDivePrompt(
                theme = DeepDiveTheme.FORGIVENESS,
                variation = 0,
                openingQuestion = "What heaviness have you been carrying that you're tired of holding?",
                coreQuestions = listOf(
                    "Who hurt you in a way that still echoes? What specifically still stings?",
                    "What would letting go of this resentment give you? What might you lose?",
                    "What do you need to forgive yourself for? What judgment are you ready to release?"
                ),
                insightPrompt = "What would forgiveness mean in this situation? What does it NOT have to mean?",
                commitmentPrompt = "What small step toward release feels possible today? What would ease the burden?"
            )
            1 -> DeepDivePrompt(
                theme = DeepDiveTheme.FORGIVENESS,
                variation = 1,
                openingQuestion = "What old wound are you tired of revisiting? What keeps pulling you back to it?",
                coreQuestions = listOf(
                    "What pain are you protecting by not forgiving? What familiar identity would you lose?",
                    "How has holding onto this hurt changed you? What has it cost you?",
                    "What compassion could you extend to the person who hurt you, even if they don't deserve it?"
                ),
                insightPrompt = "What does your inability to forgive reveal about what you need or need to protect?",
                commitmentPrompt = "What would it mean to forgive without forgetting, to release without reconciling?"
            )
            2 -> DeepDivePrompt(
                theme = DeepDiveTheme.FORGIVENESS,
                variation = 2,
                openingQuestion = "What mistake of your own are you still punishing yourself for?",
                coreQuestions = listOf(
                    "What would you tell a friend who made the same mistake you did?",
                    "What were you doing the best you could with at that time? What didn't you know yet?",
                    "What would your older, wiser self say about this? What perspective could soften the blame?"
                ),
                insightPrompt = "What wisdom or strength did you gain from this experience? Could you thank the mistake?",
                commitmentPrompt = "What self-forgiveness practice could you try? What words do you need to hear yourself say?"
            )
            3 -> DeepDivePrompt(
                theme = DeepDiveTheme.FORGIVENESS,
                variation = 3,
                openingQuestion = "What grudge have you been feeding? What does keeping it alive give you?",
                coreQuestions = listOf(
                    "Who do you need to apologize to? What's stopping you?",
                    "What boundary could replace the wall of resentment you've built?",
                    "What would it mean to wish well for someone who wronged you, from a safe distance?"
                ),
                insightPrompt = "How is unforgiveness serving as protection? What other ways could you stay safe while still releasing?",
                commitmentPrompt = "What healing practice - a letter never sent, a ritual, a conversation - could support your forgiveness journey?"
            )
            else -> DeepDivePrompt(
                theme = DeepDiveTheme.FORGIVENESS,
                variation = 4,
                openingQuestion = "What apology are you still waiting for? What if it never comes?",
                coreQuestions = listOf(
                    "What part of you needs to be forgiven by you?",
                    "What would closure look like if it had to come from within, not from someone else?",
                    "How has not forgiving affected your other relationships? Your peace?"
                ),
                insightPrompt = "What freedom awaits you on the other side of forgiveness?",
                commitmentPrompt = "What symbolic act could represent your choice to release and heal? When will you do it?"
            )
        }
    }

    // ==================== AMBITION PROMPTS ====================

    private fun generateAmbitionPrompts(variation: Int): DeepDivePrompt {
        return when (variation % 5) {
            0 -> DeepDivePrompt(
                theme = DeepDiveTheme.AMBITION,
                variation = 0,
                openingQuestion = "If you could have any life five years from now, what would it look like?",
                coreQuestions = listOf(
                    "What dream have you been too afraid to say out loud? What makes it so powerful?",
                    "What would you attempt if you had unlimited resources and support?",
                    "What does success mean to you, in your own words, not society's?"
                ),
                insightPrompt = "What vision is calling to you? What's trying to become real through your efforts?",
                commitmentPrompt = "What's the very first step toward this dream? When will you take it?"
            )
            1 -> DeepDivePrompt(
                theme = DeepDiveTheme.AMBITION,
                variation = 1,
                openingQuestion = "What achievement would make you proud, even if no one else understood it?",
                coreQuestions = listOf(
                    "What ambitious goal have you given up on? What would it take to revive it?",
                    "What impact do you want to have in your field, community, or family?",
                    "What legacy project keeps whispering to you?"
                ),
                insightPrompt = "What would it mean to pursue ambition without sacrificing your values or well-being?",
                commitmentPrompt = "What limiting belief about your potential are you ready to question? What's one piece of evidence against it?"
            )
            2 -> DeepDivePrompt(
                theme = DeepDiveTheme.AMBITION,
                variation = 2,
                openingQuestion = "What does your ideal day look like in your dream life? Walk through it.",
                coreQuestions = listOf(
                    "What skill or knowledge are you hungry to master? What draws you to it?",
                    "What would you create, build, or bring into existence if you could?",
                    "How do you want to be remembered? What mark do you want to leave?"
                ),
                insightPrompt = "What's the difference between ambition and ego in your life? How do you distinguish between them?",
                commitmentPrompt = "What support system, habit, or resource do you need to cultivate to reach for this dream?"
            )
            3 -> DeepDivePrompt(
                theme = DeepDiveTheme.AMBITION,
                variation = 3,
                openingQuestion = "What mountain have you been standing at the base of, wondering if you can climb it?",
                coreQuestions = listOf(
                    "What would you do with your one wild and precious life if you took it seriously?",
                    "What opportunity would you regret not taking when you're 80 years old?",
                    "What does 'making it' actually mean to you? What would be enough?"
                ),
                insightPrompt = "What's the real reason behind this ambition? What need or desire is fueling it?",
                commitmentPrompt = "What's one bold move you could make this month toward your biggest goal?"
            )
            else -> DeepDivePrompt(
                theme = DeepDiveTheme.AMBITION,
                variation = 4,
                openingQuestion = "What would you do if you believed you were capable of extraordinary things?",
                coreQuestions = listOf(
                    "What impossible thing have you already accomplished? What made you doubt it was possible?",
                    "What change do you want to see in the world? How could you contribute to it?",
                    "What excites you so much that you'd work on it for free?"
                ),
                insightPrompt = "What would authentic ambition look like for you - aligned with your values and joy?",
                commitmentPrompt = "What permission do you need to give yourself to dream bigger? What are you waiting for?"
            )
        }
    }

    // ==================== HELPER METHODS ====================

    private suspend fun getRecentMoodTrend(userId: String): List<String> {
        return try {
            val recentEntries = journalDao.getRecentEntriesSync(5)
            recentEntries.map { it.mood }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Generate follow-up prompts based on previous deep dive responses
     */
    fun generateFollowUpPrompt(
        theme: DeepDiveTheme,
        previousInsight: String?
    ): String {
        if (previousInsight.isNullOrBlank()) {
            return "Welcome back to exploring ${theme.displayName}. What's evolved in your thinking since last time?"
        }

        return when (theme) {
            DeepDiveTheme.GRATITUDE -> "Last time you discovered: '$previousInsight'. How has this insight influenced your gratitude practice?"
            DeepDiveTheme.GROWTH -> "You previously realized: '$previousInsight'. What growth has unfolded since then?"
            DeepDiveTheme.RELATIONSHIPS -> "You noted: '$previousInsight'. How have your relationships shifted with this awareness?"
            DeepDiveTheme.PURPOSE -> "Your insight was: '$previousInsight'. How has your sense of purpose deepened?"
            DeepDiveTheme.FEAR -> "You discovered: '$previousInsight'. What courage have you found since facing this fear?"
            DeepDiveTheme.JOY -> "You learned: '$previousInsight'. What new sources of joy have you discovered?"
            DeepDiveTheme.FORGIVENESS -> "You realized: '$previousInsight'. What healing has taken place?"
            DeepDiveTheme.AMBITION -> "Your vision was: '$previousInsight'. What progress have you made toward this dream?"
        }
    }

    /**
     * Get a contextual opening based on time of day
     */
    fun getContextualOpening(): String {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        return when {
            hour < 12 -> "Good morning. Let's settle in for some deeper reflection."
            hour < 17 -> "Welcome. Take a moment to arrive here, leaving the day's concerns at the door."
            else -> "Good evening. As the day winds down, let's turn inward together."
        }
    }
}
