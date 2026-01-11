package com.prody.prashant.domain.intelligence

import com.prody.prashant.domain.model.TimeOfDay
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * ================================================================================================
 * TEMPORAL CONTENT ENGINE - Time-Aware Intelligence
 * ================================================================================================
 *
 * This engine makes Prody aware of time in a meaningful way. It's not just about showing
 * "Good morning!" - it's about understanding how different times affect people and
 * adjusting the entire experience accordingly.
 *
 * Temporal awareness includes:
 * - Time of day (morning energy vs evening reflection)
 * - Day of week (Monday motivation vs Friday wind-down)
 * - Season (cozy winter vs vibrant summer)
 * - Special days (holidays, user anniversaries)
 * - User's personal rhythms (when they typically journal)
 *
 * Key principles:
 * - Greetings should feel fresh, not repetitive
 * - Prompts should match the natural flow of the day
 * - Never use generic time-unaware content when temporal content makes sense
 */
@Singleton
class TemporalContentEngine @Inject constructor() {

    // =============================================================================================
    // PUBLIC API
    // =============================================================================================

    /**
     * Get a context-aware greeting for the current time and user state.
     */
    fun getGreeting(context: UserContext): TemporalGreeting {
        val timeOfDay = context.timeOfDay
        val dayOfWeek = context.dayOfWeek
        val isWeekend = context.isWeekend
        val firstName = context.firstName
        val specialDay = context.specialDay

        // Handle special days first
        if (specialDay != null) {
            return getSpecialDayGreeting(specialDay, firstName, context)
        }

        // Get base greeting for time of day
        val greetingText = getTimeBasedGreeting(timeOfDay, firstName, context)
        val subtitle = getContextualSubtitle(context)

        return TemporalGreeting(
            greeting = greetingText,
            subtitle = subtitle,
            timeOfDay = timeOfDay,
            mood = inferGreetingMood(context),
            shouldAnimate = context.daysSinceLastEntry > 0 || timeOfDay == TimeOfDay.MORNING
        )
    }

    /**
     * Get a journal prompt appropriate for the current time and user state.
     */
    fun getJournalPrompt(context: UserContext): TemporalPrompt {
        return when {
            context.isInFirstWeek -> getFirstWeekPrompt(context)
            context.isStruggling -> getSupportivePrompt(context)
            context.specialDay != null -> getSpecialDayPrompt(context.specialDay!!, context)
            else -> getTimeBasedPrompt(context)
        }
    }

    /**
     * Get all available prompts for the user to choose from.
     */
    fun getPromptOptions(context: UserContext): List<TemporalPrompt> {
        val prompts = mutableListOf<TemporalPrompt>()

        // Always include the primary prompt
        prompts.add(getJournalPrompt(context))

        // Add time-appropriate alternatives
        prompts.addAll(getAlternativePrompts(context))

        // Add freeform option
        prompts.add(
            TemporalPrompt(
                text = "Write freely",
                category = PromptCategory.FREEFORM,
                timeAppropriate = true,
                hint = "No prompt today - just let your thoughts flow",
                followUpQuestions = emptyList()
            )
        )

        return prompts.take(6) // Max 6 options
    }

    /**
     * Get the daily theme for visual and content adjustments.
     */
    fun getDailyTheme(context: UserContext): DailyTheme {
        // Special day themes take priority
        context.specialDay?.let { specialDay ->
            return getSpecialDayTheme(specialDay)
        }

        return when (context.dayOfWeek) {
            DayOfWeek.MONDAY -> DailyTheme(
                name = "Fresh Start",
                accentColorHex = "#4CAF50",
                promptStyle = PromptStyle.INTENTIONAL,
                wisdomCategory = WisdomCategory.ACTION
            )
            DayOfWeek.TUESDAY -> DailyTheme(
                name = "Building Momentum",
                accentColorHex = "#2196F3",
                promptStyle = PromptStyle.ENERGETIC,
                wisdomCategory = WisdomCategory.GROWTH
            )
            DayOfWeek.WEDNESDAY -> DailyTheme(
                name = "Midweek Clarity",
                accentColorHex = "#9C27B0",
                promptStyle = PromptStyle.REFLECTIVE,
                wisdomCategory = WisdomCategory.PERSPECTIVE
            )
            DayOfWeek.THURSDAY -> DailyTheme(
                name = "Gratitude Day",
                accentColorHex = "#FF9800",
                promptStyle = PromptStyle.GRATEFUL,
                wisdomCategory = WisdomCategory.GRATITUDE
            )
            DayOfWeek.FRIDAY -> DailyTheme(
                name = "Reflect & Release",
                accentColorHex = "#673AB7",
                promptStyle = PromptStyle.REFLECTIVE,
                wisdomCategory = WisdomCategory.PEACE
            )
            DayOfWeek.SATURDAY -> DailyTheme(
                name = "Weekend Exploration",
                accentColorHex = "#00BCD4",
                promptStyle = PromptStyle.GENTLE,
                wisdomCategory = WisdomCategory.INTROSPECTION
            )
            DayOfWeek.SUNDAY -> DailyTheme(
                name = "Prepare & Restore",
                accentColorHex = "#FFB300",
                promptStyle = PromptStyle.GENTLE,
                wisdomCategory = WisdomCategory.COURAGE
            )
        }
    }

    /**
     * Get seasonal content adjustments.
     */
    fun getSeasonalContext(): SeasonalContext {
        val season = Season.current()
        val month = LocalDate.now().month

        return SeasonalContext(
            season = season,
            mood = getSeasonalMood(season),
            themes = getSeasonalThemes(season, month),
            visualHints = getSeasonalVisualHints(season),
            wisdomFocus = getSeasonalWisdomFocus(season)
        )
    }

    /**
     * Check if there's a temporal event worth acknowledging.
     */
    fun getTemporalEvent(context: UserContext): TemporalEvent? {
        val today = LocalDate.now()
        val now = LocalDateTime.now()

        // Check for special days
        context.specialDay?.let { specialDay ->
            return TemporalEvent(
                type = getEventType(specialDay),
                title = specialDay.displayName,
                message = getSpecialDayMessage(specialDay, context),
                shouldCelebrate = specialDay.shouldCelebrate,
                suggestedAction = getSuggestedActionForSpecialDay(specialDay)
            )
        }

        // Check for New Year's resolution check-in (January)
        if (today.month == Month.JANUARY && today.dayOfMonth in 15..31) {
            return TemporalEvent(
                type = TemporalEventType.SEASONAL_REMINDER,
                title = "Resolution Check-In",
                message = "How are your New Year intentions going? This is a perfect time to reflect.",
                shouldCelebrate = false,
                suggestedAction = "Write about your progress"
            )
        }

        // Check for spring renewal (March 20-21 around equinox)
        if (today.month == Month.MARCH && today.dayOfMonth in 19..22) {
            return TemporalEvent(
                type = TemporalEventType.SEASONAL_TRANSITION,
                title = "Spring Equinox",
                message = "A time of renewal and balance. What new beginnings are calling to you?",
                shouldCelebrate = true,
                suggestedAction = "Reflect on fresh starts"
            )
        }

        // Check for summer solstice (June 20-21)
        if (today.month == Month.JUNE && today.dayOfMonth in 20..22) {
            return TemporalEvent(
                type = TemporalEventType.SEASONAL_TRANSITION,
                title = "Summer Solstice",
                message = "The longest day of the year. A beautiful time to celebrate light.",
                shouldCelebrate = true,
                suggestedAction = "Write about what brings you joy"
            )
        }

        // Check for fall reflection (September 22-23 around equinox)
        if (today.month == Month.SEPTEMBER && today.dayOfMonth in 21..24) {
            return TemporalEvent(
                type = TemporalEventType.SEASONAL_TRANSITION,
                title = "Autumn Equinox",
                message = "A time of balance and harvest. What have you cultivated this year?",
                shouldCelebrate = true,
                suggestedAction = "Reflect on your growth"
            )
        }

        // Check for winter solstice (December 21-22)
        if (today.month == Month.DECEMBER && today.dayOfMonth in 20..23) {
            return TemporalEvent(
                type = TemporalEventType.SEASONAL_TRANSITION,
                title = "Winter Solstice",
                message = "The shortest day, marking the return of longer days ahead. A time for inner reflection.",
                shouldCelebrate = true,
                suggestedAction = "Write about your inner world"
            )
        }

        // Check for end of year reflection
        if (today.month == Month.DECEMBER && today.dayOfMonth >= 28) {
            return TemporalEvent(
                type = TemporalEventType.YEAR_END,
                title = "Year's End Reflection",
                message = "The year is coming to a close. A powerful time to reflect on your journey.",
                shouldCelebrate = true,
                suggestedAction = "Reflect on the year"
            )
        }

        return null
    }

    /**
     * Get content appropriate for a user's evening wind-down.
     */
    fun getEveningReflectionContent(context: UserContext): EveningReflectionContent {
        val dayOfWeek = context.dayOfWeek
        val isWeekend = context.isWeekend

        val prompt = when {
            dayOfWeek == DayOfWeek.FRIDAY -> "The work week is done. What are you most proud of?"
            dayOfWeek == DayOfWeek.SUNDAY -> "A new week begins tomorrow. What intention will guide you?"
            isWeekend -> "How did you spend your day? What restored you?"
            else -> "Before you rest, what's one thing you're grateful for today?"
        }

        val gratitudePrompt = EVENING_GRATITUDE_PROMPTS.random()

        return EveningReflectionContent(
            greeting = getEveningGreeting(context),
            reflectionPrompt = prompt,
            gratitudePrompt = gratitudePrompt,
            windDownSuggestion = getWindDownSuggestion(context),
            tomorrowPreview = getTomorrowPreview(dayOfWeek)
        )
    }

    /**
     * Get content appropriate for morning motivation.
     */
    fun getMorningContent(context: UserContext): MorningContent {
        val dayOfWeek = context.dayOfWeek
        val isWeekend = context.isWeekend

        val intentionPrompt = when {
            dayOfWeek == DayOfWeek.MONDAY -> "What will make this week meaningful?"
            isWeekend -> "What will bring you joy today?"
            else -> "What's the one thing that matters most today?"
        }

        return MorningContent(
            greeting = getGreeting(context),
            intentionPrompt = intentionPrompt,
            energyBoost = getMorningEnergyBoost(context),
            dailyFocus = getDailyFocus(dayOfWeek)
        )
    }

    // =============================================================================================
    // PRIVATE HELPERS - GREETINGS
    // =============================================================================================

    private fun getTimeBasedGreeting(
        timeOfDay: TimeOfDay,
        firstName: String,
        context: UserContext
    ): String {
        val greetings = when (timeOfDay) {
            TimeOfDay.MORNING -> listOf(
                "Good morning, $firstName",
                "Rise and shine, $firstName",
                "A fresh day awaits, $firstName",
                "Morning, $firstName",
                "Hello, beautiful morning",
                "New day, new possibilities"
            )
            TimeOfDay.AFTERNOON -> listOf(
                "Good afternoon, $firstName",
                "Hey there, $firstName",
                "Afternoon, $firstName",
                "Hope your day is going well",
                "Checking in, $firstName",
                "Hello again, $firstName"
            )
            TimeOfDay.EVENING -> listOf(
                "Good evening, $firstName",
                "Evening, $firstName",
                "Winding down, $firstName?",
                "Time to reflect, $firstName",
                "The day is settling down",
                "Welcome to the quiet hours"
            )
            TimeOfDay.NIGHT -> listOf(
                "Still up, $firstName?",
                "Burning the midnight oil?",
                "The night is yours, $firstName",
                "Quiet thoughts at night",
                "Night owl, I see",
                "The world sleeps, but you reflect"
            )
            TimeOfDay.LATE_NIGHT -> listOf(
                "The late hours, $firstName",
                "Still going, $firstName?",
                "Deep into the night",
                "The world is quiet now",
                "Late night thoughts?",
                "In the stillness of night"
            )
        }

        // Add returning user variations
        if (context.daysSinceLastEntry > 3) {
            return when (context.userArchetype) {
                UserArchetype.RETURNING -> listOf(
                    "Welcome back, $firstName",
                    "Good to see you again",
                    "It's been a while, $firstName",
                    "We missed you, $firstName"
                ).random()
                else -> greetings.random()
            }
        }

        return greetings.random()
    }

    private fun getContextualSubtitle(context: UserContext): String {
        return when {
            context.daysSinceLastEntry == 0 -> listOf(
                "Back for more reflection?",
                "Another moment to capture",
                "Your journal awaits",
                "Ready to continue?"
            ).random()
            context.daysSinceLastEntry == 1 -> listOf(
                "Yesterday's thoughts led you here",
                "A new day, new insights",
                "Continuing your journey",
                "What's on your mind today?"
            ).random()
            context.daysSinceLastEntry in 2..7 -> listOf(
                "Ready to pick up where you left off?",
                "Your journal is here when you need it",
                "Time for some reflection",
                "What would you like to capture?"
            ).random()
            context.daysSinceLastEntry > 7 -> listOf(
                "No pressure, just possibilities",
                "Start wherever feels right",
                "Your space is ready",
                "Pick up when you're ready"
            ).random()
            else -> listOf(
                "What's on your mind?",
                "Ready to reflect?",
                "Let's capture this moment",
                "Your thoughts matter"
            ).random()
        }
    }

    private fun getSpecialDayGreeting(
        specialDay: SpecialDay,
        firstName: String,
        context: UserContext
    ): TemporalGreeting {
        val (greeting, subtitle) = when (specialDay) {
            is SpecialDay.NewYear -> Pair(
                "Happy New Year, $firstName!",
                "A whole year of possibilities awaits"
            )
            is SpecialDay.Birthday -> Pair(
                "Happy Birthday, $firstName!",
                "May this year bring you joy and growth"
            )
            is SpecialDay.ProdyAnniversary -> Pair(
                "Happy Anniversary, $firstName!",
                "It's been ${specialDay.yearsWithPrody} year${if (specialDay.yearsWithPrody > 1) "s" else ""} of growth together"
            )
            is SpecialDay.FirstEntryAnniversary -> Pair(
                "A special day, $firstName",
                "It's the anniversary of your first entry"
            )
            is SpecialDay.MentalHealthDay -> Pair(
                "World Mental Health Day",
                "A day to honor the importance of mental wellness"
            )
            is SpecialDay.GratitudeDay -> Pair(
                "Happy Gratitude Day, $firstName",
                "A day to count our blessings"
            )
            is SpecialDay.Custom -> Pair(
                specialDay.name,
                "A day worth marking"
            )
        }

        return TemporalGreeting(
            greeting = greeting,
            subtitle = subtitle,
            timeOfDay = context.timeOfDay,
            mood = GreetingMood.CELEBRATORY,
            shouldAnimate = true
        )
    }

    private fun getEveningGreeting(context: UserContext): String {
        val firstName = context.firstName
        return listOf(
            "Time to wind down, $firstName",
            "The day is settling, $firstName",
            "Evening reflection time",
            "Before you rest, $firstName",
            "A moment to pause"
        ).random()
    }

    private fun inferGreetingMood(context: UserContext): GreetingMood {
        return when {
            context.specialDay != null -> GreetingMood.CELEBRATORY
            context.isStruggling -> GreetingMood.GENTLE
            context.isThriving -> GreetingMood.ENERGETIC
            context.daysSinceLastEntry > 7 -> GreetingMood.WARM_WELCOME
            else -> GreetingMood.NEUTRAL
        }
    }

    // =============================================================================================
    // PRIVATE HELPERS - PROMPTS
    // =============================================================================================

    private fun getTimeBasedPrompt(context: UserContext): TemporalPrompt {
        val timeOfDay = context.timeOfDay
        val dayOfWeek = context.dayOfWeek
        val promptStyle = getDailyTheme(context).promptStyle

        val (text, category, hint) = when {
            // Morning prompts
            timeOfDay == TimeOfDay.MORNING && dayOfWeek == DayOfWeek.MONDAY -> Triple(
                "What intention will guide your week?",
                PromptCategory.INTENTION,
                "Monday is about setting the tone for the week ahead"
            )
            timeOfDay == TimeOfDay.MORNING -> Triple(
                MORNING_PROMPTS.random(),
                PromptCategory.INTENTION,
                "Mornings are great for setting intentions"
            )
            // Evening prompts
            timeOfDay == TimeOfDay.EVENING || timeOfDay == TimeOfDay.NIGHT -> Triple(
                EVENING_PROMPTS.random(),
                PromptCategory.REFLECTION,
                "Evenings invite reflection on the day"
            )
            // Day-specific prompts
            dayOfWeek == DayOfWeek.THURSDAY -> Triple(
                GRATITUDE_PROMPTS.random(),
                PromptCategory.GRATITUDE,
                "Thursday is for gratitude"
            )
            dayOfWeek == DayOfWeek.FRIDAY -> Triple(
                WEEK_END_PROMPTS.random(),
                PromptCategory.REFLECTION,
                "Friday is for looking back at the week"
            )
            // Weekend prompts
            context.isWeekend -> Triple(
                WEEKEND_PROMPTS.random(),
                PromptCategory.FREEFORM,
                "Weekends are for whatever feels right"
            )
            // Default afternoon
            else -> Triple(
                AFTERNOON_PROMPTS.random(),
                PromptCategory.CHECK_IN,
                "A moment to pause and check in"
            )
        }

        return TemporalPrompt(
            text = text,
            category = category,
            timeAppropriate = true,
            hint = hint,
            followUpQuestions = getFollowUpQuestions(category)
        )
    }

    private fun getFirstWeekPrompt(context: UserContext): TemporalPrompt {
        val dayNumber = context.daysWithPrody.coerceIn(1, 7)

        return when (dayNumber) {
            1 -> TemporalPrompt(
                text = "What brings you here today?",
                category = PromptCategory.INTRODUCTION,
                timeAppropriate = true,
                hint = "There's no right or wrong answer - just be yourself",
                followUpQuestions = listOf(
                    "What do you hope journaling will bring you?",
                    "What made today feel significant enough to write about?"
                )
            )
            2 -> TemporalPrompt(
                text = "What's one thing on your mind right now?",
                category = PromptCategory.CHECK_IN,
                timeAppropriate = true,
                hint = "Day 2! Your consistency is already showing",
                followUpQuestions = listOf(
                    "How does it feel to write for the second day?",
                    "What surprised you about yesterday's entry?"
                )
            )
            3 -> TemporalPrompt(
                text = "What's something you're grateful for today?",
                category = PromptCategory.GRATITUDE,
                timeAppropriate = true,
                hint = "Gratitude is a powerful practice",
                followUpQuestions = listOf(
                    "Why does this gratitude feel meaningful?",
                    "How can you carry this feeling with you?"
                )
            )
            4 -> TemporalPrompt(
                text = "What challenge are you working through?",
                category = PromptCategory.GROWTH,
                timeAppropriate = true,
                hint = "Writing about challenges helps us process them",
                followUpQuestions = listOf(
                    "What have you tried so far?",
                    "What support would help?"
                )
            )
            5 -> TemporalPrompt(
                text = "What would your future self thank you for?",
                category = PromptCategory.REFLECTION,
                timeAppropriate = true,
                hint = "Connect with your future self",
                followUpQuestions = listOf(
                    "What does that future look like?",
                    "What's one step you can take today?"
                )
            )
            6 -> TemporalPrompt(
                text = "What patterns do you notice in your week?",
                category = PromptCategory.REFLECTION,
                timeAppropriate = true,
                hint = "Almost a full week of entries!",
                followUpQuestions = listOf(
                    "What surprised you this week?",
                    "What would you do differently?"
                )
            )
            7 -> TemporalPrompt(
                text = "How has this first week of journaling felt?",
                category = PromptCategory.REFLECTION,
                timeAppropriate = true,
                hint = "Congratulations on a full week!",
                followUpQuestions = listOf(
                    "What did you learn about yourself?",
                    "What will you carry forward?"
                )
            )
            else -> getTimeBasedPrompt(context)
        }
    }

    private fun getSupportivePrompt(context: UserContext): TemporalPrompt {
        return TemporalPrompt(
            text = SUPPORTIVE_PROMPTS.random(),
            category = PromptCategory.SUPPORT,
            timeAppropriate = true,
            hint = "This space is here for whatever you need",
            followUpQuestions = listOf(
                "What would help right now?",
                "What do you need to hear?"
            )
        )
    }

    private fun getSpecialDayPrompt(specialDay: SpecialDay, context: UserContext): TemporalPrompt {
        return when (specialDay) {
            is SpecialDay.NewYear -> TemporalPrompt(
                text = "What word will define your year ahead?",
                category = PromptCategory.INTENTION,
                timeAppropriate = true,
                hint = "New Year, new possibilities",
                followUpQuestions = listOf(
                    "What will you leave behind from last year?",
                    "What do you want to invite into your life?"
                )
            )
            is SpecialDay.Birthday -> TemporalPrompt(
                text = "What wisdom would you share with your younger self?",
                category = PromptCategory.REFLECTION,
                timeAppropriate = true,
                hint = "Happy Birthday! A day for reflection and celebration",
                followUpQuestions = listOf(
                    "What are you most proud of?",
                    "What do you hope for this new year of life?"
                )
            )
            is SpecialDay.ProdyAnniversary -> TemporalPrompt(
                text = "Look back at your first entry. How have you grown?",
                category = PromptCategory.REFLECTION,
                timeAppropriate = true,
                hint = "Happy Prody Anniversary!",
                followUpQuestions = listOf(
                    "What surprised you about your journey?",
                    "What patterns do you notice?"
                )
            )
            is SpecialDay.MentalHealthDay -> TemporalPrompt(
                text = "How are you really doing?",
                category = PromptCategory.CHECK_IN,
                timeAppropriate = true,
                hint = "Today we honor mental health. How's yours?",
                followUpQuestions = listOf(
                    "What do you need more of?",
                    "What brings you peace?"
                )
            )
            is SpecialDay.GratitudeDay -> TemporalPrompt(
                text = "What fills your gratitude list today?",
                category = PromptCategory.GRATITUDE,
                timeAppropriate = true,
                hint = "A day dedicated to counting blessings",
                followUpQuestions = listOf(
                    "Who would you like to thank?",
                    "What unexpected blessing appeared recently?"
                )
            )
            else -> getTimeBasedPrompt(context)
        }
    }

    private fun getAlternativePrompts(context: UserContext): List<TemporalPrompt> {
        val alternatives = mutableListOf<TemporalPrompt>()

        // Add gratitude option
        alternatives.add(
            TemporalPrompt(
                text = GRATITUDE_PROMPTS.random(),
                category = PromptCategory.GRATITUDE,
                timeAppropriate = true,
                hint = "Gratitude changes perspective",
                followUpQuestions = listOf("Why does this feel meaningful?")
            )
        )

        // Add growth option
        alternatives.add(
            TemporalPrompt(
                text = GROWTH_PROMPTS.random(),
                category = PromptCategory.GROWTH,
                timeAppropriate = true,
                hint = "Reflect on your journey",
                followUpQuestions = listOf("What's the next step?")
            )
        )

        // Add creative option
        alternatives.add(
            TemporalPrompt(
                text = CREATIVE_PROMPTS.random(),
                category = PromptCategory.CREATIVE,
                timeAppropriate = true,
                hint = "Let your imagination play",
                followUpQuestions = emptyList()
            )
        )

        return alternatives
    }

    private fun getFollowUpQuestions(category: PromptCategory): List<String> {
        return when (category) {
            PromptCategory.GRATITUDE -> listOf(
                "Why does this feel meaningful?",
                "How will you carry this gratitude forward?"
            )
            PromptCategory.REFLECTION -> listOf(
                "What does this teach you?",
                "How do you feel about this?"
            )
            PromptCategory.INTENTION -> listOf(
                "What's the first step?",
                "What might get in the way?"
            )
            PromptCategory.GROWTH -> listOf(
                "What have you learned?",
                "What's the next step?"
            )
            else -> listOf(
                "How does this make you feel?",
                "What else comes to mind?"
            )
        }
    }

    // =============================================================================================
    // PRIVATE HELPERS - SEASONAL
    // =============================================================================================

    private fun getSeasonalMood(season: Season): SeasonalMood {
        return when (season) {
            Season.SPRING -> SeasonalMood.RENEWAL
            Season.SUMMER -> SeasonalMood.VITALITY
            Season.FALL -> SeasonalMood.REFLECTION
            Season.WINTER -> SeasonalMood.INTROSPECTION
        }
    }

    private fun getSeasonalThemes(season: Season, month: Month): List<String> {
        return when (season) {
            Season.SPRING -> listOf("new beginnings", "growth", "renewal", "fresh starts", "blossoming")
            Season.SUMMER -> listOf("adventure", "joy", "freedom", "connection", "warmth")
            Season.FALL -> listOf("harvest", "reflection", "letting go", "transformation", "gratitude")
            Season.WINTER -> listOf("rest", "introspection", "warmth within", "quiet strength", "patience")
        }
    }

    private fun getSeasonalVisualHints(season: Season): SeasonalVisualHints {
        return when (season) {
            Season.SPRING -> SeasonalVisualHints(
                primaryColorHex = "#81C784",
                accentColorHex = "#FFEB3B",
                iconTheme = "flower"
            )
            Season.SUMMER -> SeasonalVisualHints(
                primaryColorHex = "#FFD54F",
                accentColorHex = "#4FC3F7",
                iconTheme = "sun"
            )
            Season.FALL -> SeasonalVisualHints(
                primaryColorHex = "#FFB74D",
                accentColorHex = "#A1887F",
                iconTheme = "leaf"
            )
            Season.WINTER -> SeasonalVisualHints(
                primaryColorHex = "#90CAF9",
                accentColorHex = "#B0BEC5",
                iconTheme = "snowflake"
            )
        }
    }

    private fun getSeasonalWisdomFocus(season: Season): WisdomCategory {
        return when (season) {
            Season.SPRING -> WisdomCategory.GROWTH
            Season.SUMMER -> WisdomCategory.ACTION
            Season.FALL -> WisdomCategory.GRATITUDE
            Season.WINTER -> WisdomCategory.INTROSPECTION
        }
    }

    // =============================================================================================
    // PRIVATE HELPERS - SPECIAL DAYS
    // =============================================================================================

    private fun getSpecialDayTheme(specialDay: SpecialDay): DailyTheme {
        return when (specialDay) {
            is SpecialDay.NewYear -> DailyTheme(
                name = "New Beginnings",
                accentColorHex = "#FFD700",
                promptStyle = PromptStyle.INTENTIONAL,
                wisdomCategory = WisdomCategory.GROWTH
            )
            is SpecialDay.Birthday -> DailyTheme(
                name = "Celebration",
                accentColorHex = "#FF6B6B",
                promptStyle = PromptStyle.REFLECTIVE,
                wisdomCategory = WisdomCategory.PERSPECTIVE
            )
            is SpecialDay.ProdyAnniversary -> DailyTheme(
                name = "Anniversary",
                accentColorHex = "#9C27B0",
                promptStyle = PromptStyle.REFLECTIVE,
                wisdomCategory = WisdomCategory.GROWTH
            )
            is SpecialDay.MentalHealthDay -> DailyTheme(
                name = "Mental Health",
                accentColorHex = "#4CAF50",
                promptStyle = PromptStyle.GENTLE,
                wisdomCategory = WisdomCategory.PEACE
            )
            is SpecialDay.GratitudeDay -> DailyTheme(
                name = "Gratitude",
                accentColorHex = "#FF9800",
                promptStyle = PromptStyle.GRATEFUL,
                wisdomCategory = WisdomCategory.GRATITUDE
            )
            else -> getDailyTheme(UserContext.empty())
        }
    }

    private fun getEventType(specialDay: SpecialDay): TemporalEventType {
        return when (specialDay) {
            is SpecialDay.NewYear -> TemporalEventType.CALENDAR_EVENT
            is SpecialDay.Birthday -> TemporalEventType.PERSONAL_MILESTONE
            is SpecialDay.ProdyAnniversary -> TemporalEventType.APP_ANNIVERSARY
            is SpecialDay.FirstEntryAnniversary -> TemporalEventType.APP_ANNIVERSARY
            is SpecialDay.MentalHealthDay -> TemporalEventType.CALENDAR_EVENT
            is SpecialDay.GratitudeDay -> TemporalEventType.CALENDAR_EVENT
            is SpecialDay.Custom -> TemporalEventType.PERSONAL_MILESTONE
        }
    }

    private fun getSpecialDayMessage(specialDay: SpecialDay, context: UserContext): String {
        return when (specialDay) {
            is SpecialDay.NewYear -> "A whole new year awaits. What will you make of it?"
            is SpecialDay.Birthday -> "Another year wiser. Time to reflect on your journey."
            is SpecialDay.ProdyAnniversary -> "It's been ${specialDay.yearsWithPrody} year${if (specialDay.yearsWithPrody > 1) "s" else ""} since you started with Prody. Look how far you've come!"
            is SpecialDay.FirstEntryAnniversary -> "Today marks the anniversary of your very first entry."
            is SpecialDay.MentalHealthDay -> "A day to honor the importance of mental wellness."
            is SpecialDay.GratitudeDay -> "A perfect day to count your blessings."
            is SpecialDay.Custom -> "A day worth marking."
        }
    }

    private fun getSuggestedActionForSpecialDay(specialDay: SpecialDay): String {
        return when (specialDay) {
            is SpecialDay.NewYear -> "Set your intentions for the year"
            is SpecialDay.Birthday -> "Write a letter to yourself"
            is SpecialDay.ProdyAnniversary -> "Look back at your first entry"
            is SpecialDay.FirstEntryAnniversary -> "Revisit your first entry"
            is SpecialDay.MentalHealthDay -> "Check in with yourself"
            is SpecialDay.GratitudeDay -> "List what you're grateful for"
            else -> "Reflect on this moment"
        }
    }

    // =============================================================================================
    // PRIVATE HELPERS - EVENING/MORNING
    // =============================================================================================

    private fun getWindDownSuggestion(context: UserContext): String {
        return listOf(
            "Consider putting your phone away an hour before bed",
            "A warm drink might help you settle",
            "Tomorrow is a fresh start",
            "Let go of what you can't control tonight",
            "Your thoughts will still be there tomorrow"
        ).random()
    }

    private fun getTomorrowPreview(today: DayOfWeek): String {
        return when (today) {
            DayOfWeek.SUNDAY -> "Monday's theme: Fresh Start. Set your intention tonight."
            DayOfWeek.MONDAY -> "Tuesday is about building momentum."
            DayOfWeek.TUESDAY -> "Wednesday brings midweek clarity."
            DayOfWeek.WEDNESDAY -> "Thursday is gratitude day. Sleep with thanks."
            DayOfWeek.THURSDAY -> "Friday's for reflection. Almost there."
            DayOfWeek.FRIDAY -> "The weekend awaits. Rest well."
            DayOfWeek.SATURDAY -> "Tomorrow is for restoration. Enjoy the evening."
        }
    }

    private fun getMorningEnergyBoost(context: UserContext): String {
        return when {
            context.isStruggling -> "Start small. One step at a time."
            context.isThriving -> "Your energy is contagious. Share it with the world."
            context.currentStreak.reflectionStreak.current >= 7 -> "You're on a roll! Keep the momentum going."
            else -> listOf(
                "Today is full of possibilities",
                "Your morning mindset shapes your day",
                "Start with what matters most",
                "Small wins lead to big victories"
            ).random()
        }
    }

    private fun getDailyFocus(dayOfWeek: DayOfWeek): String {
        return when (dayOfWeek) {
            DayOfWeek.MONDAY -> "Focus: Setting intentions for the week"
            DayOfWeek.TUESDAY -> "Focus: Building on yesterday's progress"
            DayOfWeek.WEDNESDAY -> "Focus: Midweek check-in and adjustment"
            DayOfWeek.THURSDAY -> "Focus: Gratitude and appreciation"
            DayOfWeek.FRIDAY -> "Focus: Celebrating wins and learning"
            DayOfWeek.SATURDAY -> "Focus: Rest and restoration"
            DayOfWeek.SUNDAY -> "Focus: Reflection and preparation"
        }
    }

    // =============================================================================================
    // PROMPT CONSTANTS
    // =============================================================================================

    companion object {
        private val MORNING_PROMPTS = listOf(
            "What's one thing you want to accomplish today?",
            "What are you looking forward to?",
            "How do you want to feel at the end of today?",
            "What intention will guide your day?",
            "What's the most important thing today?",
            "If today were your only chance, what would you do?"
        )

        private val AFTERNOON_PROMPTS = listOf(
            "How is your day going so far?",
            "What's been on your mind today?",
            "What have you learned today?",
            "What's something you noticed today?",
            "How are you feeling right now?",
            "What would make the rest of today great?"
        )

        private val EVENING_PROMPTS = listOf(
            "What was the best part of your day?",
            "What are you grateful for today?",
            "What did you learn today?",
            "How did you grow today?",
            "What would you do differently tomorrow?",
            "What are you letting go of tonight?"
        )

        private val EVENING_GRATITUDE_PROMPTS = listOf(
            "Name three good things from today, no matter how small",
            "Who made your day a little brighter?",
            "What went right today?",
            "What's one thing you're thankful for tonight?"
        )

        private val GRATITUDE_PROMPTS = listOf(
            "What are you grateful for today?",
            "Who has made a difference in your life recently?",
            "What's something you often take for granted?",
            "What's a simple pleasure that brought you joy?",
            "What ability or skill are you thankful for?"
        )

        private val GROWTH_PROMPTS = listOf(
            "What challenge helped you grow recently?",
            "What's something you understand now that you didn't before?",
            "How have you surprised yourself lately?",
            "What would your past self think of where you are now?",
            "What's a belief you've let go of?"
        )

        private val CREATIVE_PROMPTS = listOf(
            "If your life were a book, what chapter are you in?",
            "What color describes your mood today, and why?",
            "If you could have coffee with anyone, who would it be?",
            "What would your ideal day look like?",
            "If you could tell the world one thing, what would it be?"
        )

        private val SUPPORTIVE_PROMPTS = listOf(
            "What do you need right now?",
            "What would help you feel better?",
            "What kind words would you offer a friend in your situation?",
            "What's weighing on you?",
            "It's okay to not be okay. What's going on?",
            "What would comfort you right now?"
        )

        private val WEEKEND_PROMPTS = listOf(
            "What's giving you energy this weekend?",
            "How are you recharging?",
            "What are you doing for yourself today?",
            "What brings you peace this weekend?",
            "How does freedom feel today?"
        )

        private val WEEK_END_PROMPTS = listOf(
            "What are you proud of this week?",
            "What did you learn this week?",
            "What will you leave behind this week?",
            "How did you grow this week?",
            "What's one word to describe your week?"
        )
    }
}

// ================================================================================================
// TEMPORAL CONTENT MODELS
// ================================================================================================

/**
 * A greeting with temporal context.
 */
data class TemporalGreeting(
    val greeting: String,
    val subtitle: String,
    val timeOfDay: TimeOfDay,
    val mood: GreetingMood,
    val shouldAnimate: Boolean
)

enum class GreetingMood {
    NEUTRAL,
    ENERGETIC,
    GENTLE,
    CELEBRATORY,
    WARM_WELCOME
}

/**
 * A journal prompt with temporal context.
 */
data class TemporalPrompt(
    val text: String,
    val category: PromptCategory,
    val timeAppropriate: Boolean,
    val hint: String?,
    val followUpQuestions: List<String>
)

enum class PromptCategory {
    GRATITUDE,
    REFLECTION,
    INTENTION,
    GROWTH,
    SUPPORT,
    CHECK_IN,
    CREATIVE,
    FREEFORM,
    INTRODUCTION
}

/**
 * Seasonal context for visual and content adjustments.
 */
data class SeasonalContext(
    val season: Season,
    val mood: SeasonalMood,
    val themes: List<String>,
    val visualHints: SeasonalVisualHints,
    val wisdomFocus: WisdomCategory
)

enum class SeasonalMood {
    RENEWAL,      // Spring
    VITALITY,     // Summer
    REFLECTION,   // Fall
    INTROSPECTION // Winter
}

data class SeasonalVisualHints(
    val primaryColorHex: String,
    val accentColorHex: String,
    val iconTheme: String
)

/**
 * A temporal event worth acknowledging.
 */
data class TemporalEvent(
    val type: TemporalEventType,
    val title: String,
    val message: String,
    val shouldCelebrate: Boolean,
    val suggestedAction: String
)

enum class TemporalEventType {
    CALENDAR_EVENT,
    SEASONAL_TRANSITION,
    SEASONAL_REMINDER,
    PERSONAL_MILESTONE,
    APP_ANNIVERSARY,
    YEAR_END
}

/**
 * Evening reflection content.
 */
data class EveningReflectionContent(
    val greeting: String,
    val reflectionPrompt: String,
    val gratitudePrompt: String,
    val windDownSuggestion: String,
    val tomorrowPreview: String
)

/**
 * Morning motivation content.
 */
data class MorningContent(
    val greeting: TemporalGreeting,
    val intentionPrompt: String,
    val energyBoost: String,
    val dailyFocus: String
)
