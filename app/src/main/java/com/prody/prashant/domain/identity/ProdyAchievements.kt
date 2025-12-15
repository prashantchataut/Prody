package com.prody.prashant.domain.identity

/**
 * [ProdyAchievements] - Comprehensive achievement system for Prody
 *
 * Defines meaningful milestones on the path of personal growth. Each achievement
 * represents a genuine accomplishment in the user's journey, designed to inspire
 * rather than trivialize their progress.
 *
 * Achievements are organized into categories aligned with Prody's core pillars:
 * - Wisdom: Learning words, quotes, and proverbs
 * - Reflection: Journaling and self-examination
 * - Consistency: Maintaining streaks and habits
 * - Presence: Engaging with Buddha (AI guide) and mindfulness
 * - Temporal: Future-self letters and time awareness
 * - Mastery: Special achievements for exceptional dedication
 *
 * Example usage:
 * ```
 * val achievement = ProdyAchievements.findById("first_word")
 * val wisdomAchievements = ProdyAchievements.getByCategory(Category.WISDOM)
 * ```
 */
object ProdyAchievements {

    /**
     * Achievement categories aligned with Prody's core pillars of personal growth.
     *
     * @property id Unique identifier for persistence
     * @property displayName User-facing category name
     * @property description Explanation of what this category encompasses
     * @property iconName Material icon name for visual representation
     * @property primaryColorHex Primary hex color for gradient (start)
     * @property secondaryColorHex Secondary hex color for gradient (end)
     */
    enum class Category(
        val id: String,
        val displayName: String,
        val description: String,
        val iconName: String,
        val primaryColorHex: Long,
        val secondaryColorHex: Long
    ) {
        WISDOM(
            id = "wisdom",
            displayName = "Wisdom",
            description = "Achievements from learning words, quotes, and proverbs",
            iconName = "auto_stories",
            primaryColorHex = 0xFF6B5CE7,
            secondaryColorHex = 0xFF9B8AFF
        ),
        REFLECTION(
            id = "reflection",
            displayName = "Reflection",
            description = "Achievements from journaling and self-examination",
            iconName = "psychology",
            primaryColorHex = 0xFF3AAFA9,
            secondaryColorHex = 0xFF6FD5CE
        ),
        CONSISTENCY(
            id = "consistency",
            displayName = "Consistency",
            description = "Achievements from maintaining streaks and habits",
            iconName = "local_fire_department",
            primaryColorHex = 0xFFFF6B6B,
            secondaryColorHex = 0xFFFFAB76
        ),
        PRESENCE(
            id = "presence",
            displayName = "Presence",
            description = "Achievements from engaging with Buddha and mindfulness",
            iconName = "self_improvement",
            primaryColorHex = 0xFF4ECDC4,
            secondaryColorHex = 0xFF44B09E
        ),
        TEMPORAL(
            id = "temporal",
            displayName = "Temporal",
            description = "Achievements from future-self letters and time awareness",
            iconName = "schedule",
            primaryColorHex = 0xFF667EEA,
            secondaryColorHex = 0xFF764BA2
        ),
        MASTERY(
            id = "mastery",
            displayName = "Mastery",
            description = "Special achievements for exceptional dedication",
            iconName = "workspace_premium",
            primaryColorHex = 0xFFD4AF37,
            secondaryColorHex = 0xFFF4D03F
        ),
        SOCIAL(
            id = "social",
            displayName = "Social",
            description = "Achievements from sharing and connecting with others",
            iconName = "group",
            primaryColorHex = 0xFFE91E63,
            secondaryColorHex = 0xFFF48FB1
        ),
        EXPLORER(
            id = "explorer",
            displayName = "Explorer",
            description = "Achievements from exploring all Prody features",
            iconName = "explore",
            primaryColorHex = 0xFF00BCD4,
            secondaryColorHex = 0xFF4DD0E1
        );

        companion object {
            fun fromId(id: String): Category? = entries.find { it.id == id }
        }
    }

    /**
     * Achievement rarity levels with visual distinction.
     * Rarity determines the glow intensity and color scheme of achievement badges.
     *
     * @property id Unique identifier for persistence
     * @property displayName User-facing rarity name
     * @property glowIntensity Visual glow effect intensity (0.0 to 1.0)
     * @property primaryColorHex Primary display color
     * @property secondaryColorHex Secondary/accent color
     */
    enum class Rarity(
        val id: String,
        val displayName: String,
        val glowIntensity: Float,
        val primaryColorHex: Long,
        val secondaryColorHex: Long
    ) {
        COMMON(
            id = "common",
            displayName = "Common",
            glowIntensity = 0f,
            primaryColorHex = 0xFF78909C,
            secondaryColorHex = 0xFF90A4AE
        ),
        UNCOMMON(
            id = "uncommon",
            displayName = "Uncommon",
            glowIntensity = 0.2f,
            primaryColorHex = 0xFF4CAF50,
            secondaryColorHex = 0xFF81C784
        ),
        RARE(
            id = "rare",
            displayName = "Rare",
            glowIntensity = 0.4f,
            primaryColorHex = 0xFF2196F3,
            secondaryColorHex = 0xFF64B5F6
        ),
        EPIC(
            id = "epic",
            displayName = "Epic",
            glowIntensity = 0.6f,
            primaryColorHex = 0xFF9C27B0,
            secondaryColorHex = 0xFFBA68C8
        ),
        LEGENDARY(
            id = "legendary",
            displayName = "Legendary",
            glowIntensity = 0.85f,
            primaryColorHex = 0xFFD4AF37,
            secondaryColorHex = 0xFFF4D03F
        );

        companion object {
            fun fromId(id: String): Rarity? = entries.find { it.id == id }
        }
    }

    /**
     * Complete achievement definition.
     *
     * @property id Unique identifier for persistence and lookup
     * @property name Achievement name displayed to users
     * @property description Philosophical or poetic description
     * @property category The category this achievement belongs to
     * @property rarity The rarity level determining visual treatment
     * @property requirement Numeric threshold to unlock this achievement
     * @property requirementDescription Human-readable requirement explanation
     * @property iconName Material icon name for visual representation
     * @property celebrationMessage Elegant message shown when unlocked
     * @property rewardPoints Points awarded for unlocking this achievement
     */
    data class Achievement(
        val id: String,
        val name: String,
        val description: String,
        val category: Category,
        val rarity: Rarity,
        val requirement: Int,
        val requirementDescription: String,
        val iconName: String,
        val celebrationMessage: String,
        val rewardPoints: Int
    )

    /**
     * Complete list of all achievements in Prody.
     * Organized by category for maintainability.
     */
    val allAchievements: List<Achievement> = buildList {
        // ===== WISDOM CATEGORY =====
        add(
            Achievement(
                id = "first_word",
                name = "First Light",
                description = "Every journey begins with a single step - or a single word",
                category = Category.WISDOM,
                rarity = Rarity.COMMON,
                requirement = 1,
                requirementDescription = "Learn your first word",
                iconName = "lightbulb",
                celebrationMessage = "The first word is a doorway. Many more await.",
                rewardPoints = 25
            )
        )
        add(
            Achievement(
                id = "word_collector_10",
                name = "Gathering Words",
                description = "A vocabulary is a garden - you are planting seeds",
                category = Category.WISDOM,
                rarity = Rarity.COMMON,
                requirement = 10,
                requirementDescription = "Learn 10 words",
                iconName = "eco",
                celebrationMessage = "Ten words now live within you, ready to bloom in thought and speech.",
                rewardPoints = 50
            )
        )
        add(
            Achievement(
                id = "word_collector_25",
                name = "Language Apprentice",
                description = "Words become tools in the hands of the dedicated",
                category = Category.WISDOM,
                rarity = Rarity.UNCOMMON,
                requirement = 25,
                requirementDescription = "Learn 25 words",
                iconName = "construction",
                celebrationMessage = "Twenty-five words - the foundation of eloquence is being laid.",
                rewardPoints = 100
            )
        )
        add(
            Achievement(
                id = "word_collector_50",
                name = "Wordsmith",
                description = "Words are tools, and you are learning your craft",
                category = Category.WISDOM,
                rarity = Rarity.UNCOMMON,
                requirement = 50,
                requirementDescription = "Learn 50 words",
                iconName = "handyman",
                celebrationMessage = "Fifty words - a craftsman's toolkit begins to take shape.",
                rewardPoints = 200
            )
        )
        add(
            Achievement(
                id = "word_collector_100",
                name = "Lexicon Keeper",
                description = "A hundred words is a language unto itself",
                category = Category.WISDOM,
                rarity = Rarity.RARE,
                requirement = 100,
                requirementDescription = "Learn 100 words",
                iconName = "menu_book",
                celebrationMessage = "One hundred words now color your world with new meaning.",
                rewardPoints = 500
            )
        )
        add(
            Achievement(
                id = "word_collector_250",
                name = "Vocabulary Scholar",
                description = "Your collection of words rivals that of the well-read",
                category = Category.WISDOM,
                rarity = Rarity.RARE,
                requirement = 250,
                requirementDescription = "Learn 250 words",
                iconName = "school",
                celebrationMessage = "Two hundred fifty words - you speak with the precision of a scholar.",
                rewardPoints = 1000
            )
        )
        add(
            Achievement(
                id = "word_collector_500",
                name = "Logophile",
                description = "A true lover of words, their histories, their music",
                category = Category.WISDOM,
                rarity = Rarity.EPIC,
                requirement = 500,
                requirementDescription = "Learn 500 words",
                iconName = "favorite",
                celebrationMessage = "Five hundred words - you have become a keeper of language.",
                rewardPoints = 2500
            )
        )
        add(
            Achievement(
                id = "word_collector_1000",
                name = "Master of Words",
                description = "A thousand words form a symphony of expression",
                category = Category.WISDOM,
                rarity = Rarity.LEGENDARY,
                requirement = 1000,
                requirementDescription = "Learn 1000 words",
                iconName = "stars",
                celebrationMessage = "One thousand words - your mastery of language is truly remarkable.",
                rewardPoints = 5000
            )
        )
        add(
            Achievement(
                id = "quote_reader_10",
                name = "Wisdom Seeker",
                description = "The words of sages past light your way",
                category = Category.WISDOM,
                rarity = Rarity.COMMON,
                requirement = 10,
                requirementDescription = "Read 10 quotes",
                iconName = "format_quote",
                celebrationMessage = "Ten quotes read - the voices of wisdom begin to speak.",
                rewardPoints = 30
            )
        )
        add(
            Achievement(
                id = "quote_devotee",
                name = "Quote Devotee",
                description = "Wisdom of the ages, collected and cherished",
                category = Category.WISDOM,
                rarity = Rarity.UNCOMMON,
                requirement = 50,
                requirementDescription = "Read 50 quotes",
                iconName = "auto_stories",
                celebrationMessage = "The voices of sages past now echo in your thoughts.",
                rewardPoints = 150
            )
        )
        add(
            Achievement(
                id = "quote_collector_100",
                name = "Keeper of Quotations",
                description = "A treasury of wisdom lines your mind",
                category = Category.WISDOM,
                rarity = Rarity.RARE,
                requirement = 100,
                requirementDescription = "Read 100 quotes",
                iconName = "collections_bookmark",
                celebrationMessage = "One hundred quotes - a library of wisdom within you.",
                rewardPoints = 400
            )
        )
        add(
            Achievement(
                id = "proverb_explorer_10",
                name = "Proverb Explorer",
                description = "Ancient wisdom distilled into timeless truth",
                category = Category.WISDOM,
                rarity = Rarity.COMMON,
                requirement = 10,
                requirementDescription = "Explore 10 proverbs",
                iconName = "explore",
                celebrationMessage = "Ten proverbs explored - ancestral wisdom speaks to you.",
                rewardPoints = 40
            )
        )
        add(
            Achievement(
                id = "proverb_keeper",
                name = "Keeper of Proverbs",
                description = "Ancient wisdom distilled into lasting truth",
                category = Category.WISDOM,
                rarity = Rarity.RARE,
                requirement = 30,
                requirementDescription = "Explore 30 proverbs",
                iconName = "history_edu",
                celebrationMessage = "The wisdom of generations now walks beside you.",
                rewardPoints = 300
            )
        )

        // ===== REFLECTION CATEGORY =====
        add(
            Achievement(
                id = "first_journal",
                name = "First Reflection",
                description = "The examined life begins with a single honest word",
                category = Category.REFLECTION,
                rarity = Rarity.COMMON,
                requirement = 1,
                requirementDescription = "Write your first journal entry",
                iconName = "edit_note",
                celebrationMessage = "You have begun the sacred practice of self-examination.",
                rewardPoints = 25
            )
        )
        add(
            Achievement(
                id = "journal_5",
                name = "Emerging Voice",
                description = "Your inner voice grows clearer with each entry",
                category = Category.REFLECTION,
                rarity = Rarity.COMMON,
                requirement = 5,
                requirementDescription = "Write 5 journal entries",
                iconName = "record_voice_over",
                celebrationMessage = "Five entries - your authentic voice emerges.",
                rewardPoints = 50
            )
        )
        add(
            Achievement(
                id = "journal_10",
                name = "Inner Dialogue",
                description = "A conversation with yourself, growing richer each day",
                category = Category.REFLECTION,
                rarity = Rarity.UNCOMMON,
                requirement = 10,
                requirementDescription = "Write 10 journal entries",
                iconName = "chat",
                celebrationMessage = "Ten conversations with your deeper self - the dialogue deepens.",
                rewardPoints = 150
            )
        )
        add(
            Achievement(
                id = "journal_25",
                name = "Faithful Chronicler",
                description = "Your story unfolds one entry at a time",
                category = Category.REFLECTION,
                rarity = Rarity.UNCOMMON,
                requirement = 25,
                requirementDescription = "Write 25 journal entries",
                iconName = "history",
                celebrationMessage = "Twenty-five entries - you are faithful to your own story.",
                rewardPoints = 250
            )
        )
        add(
            Achievement(
                id = "journal_30",
                name = "Chronicle Keeper",
                description = "Your story is being written, one entry at a time",
                category = Category.REFLECTION,
                rarity = Rarity.RARE,
                requirement = 30,
                requirementDescription = "Write 30 journal entries",
                iconName = "auto_stories",
                celebrationMessage = "Thirty chapters of your inner life now preserved in words.",
                rewardPoints = 500
            )
        )
        add(
            Achievement(
                id = "journal_50",
                name = "Reflective Soul",
                description = "Fifty windows into your evolving self",
                category = Category.REFLECTION,
                rarity = Rarity.RARE,
                requirement = 50,
                requirementDescription = "Write 50 journal entries",
                iconName = "psychology",
                celebrationMessage = "Fifty reflections - your soul has found its mirror.",
                rewardPoints = 750
            )
        )
        add(
            Achievement(
                id = "journal_100",
                name = "Memoir of the Soul",
                description = "A hundred glimpses into the depths of your being",
                category = Category.REFLECTION,
                rarity = Rarity.EPIC,
                requirement = 100,
                requirementDescription = "Write 100 journal entries",
                iconName = "book",
                celebrationMessage = "One hundred reflections - a true memoir of the soul.",
                rewardPoints = 1000
            )
        )
        add(
            Achievement(
                id = "journal_365",
                name = "Year of Introspection",
                description = "A full year of looking inward",
                category = Category.REFLECTION,
                rarity = Rarity.LEGENDARY,
                requirement = 365,
                requirementDescription = "Write 365 journal entries",
                iconName = "auto_awesome",
                celebrationMessage = "Three hundred sixty-five entries - a year of knowing yourself.",
                rewardPoints = 5000
            )
        )
        add(
            Achievement(
                id = "mood_tracker_5",
                name = "Emotional Observer",
                description = "Noticing the weather of your inner world",
                category = Category.REFLECTION,
                rarity = Rarity.COMMON,
                requirement = 5,
                requirementDescription = "Track your mood 5 times",
                iconName = "mood",
                celebrationMessage = "Five moods observed - awareness begins with noticing.",
                rewardPoints = 25
            )
        )
        add(
            Achievement(
                id = "mood_tracker",
                name = "Emotional Cartographer",
                description = "Mapping the terrain of your inner landscape",
                category = Category.REFLECTION,
                rarity = Rarity.UNCOMMON,
                requirement = 20,
                requirementDescription = "Track your mood 20 times",
                iconName = "map",
                celebrationMessage = "You are learning to read the weather of your own heart.",
                rewardPoints = 100
            )
        )
        add(
            Achievement(
                id = "mood_range",
                name = "Emotional Range",
                description = "You have touched all the colors of feeling",
                category = Category.REFLECTION,
                rarity = Rarity.RARE,
                requirement = 8,
                requirementDescription = "Experience all mood types",
                iconName = "palette",
                celebrationMessage = "The full spectrum of emotion is yours to understand.",
                rewardPoints = 400
            )
        )

        // ===== CONSISTENCY CATEGORY =====
        add(
            Achievement(
                id = "streak_3",
                name = "Kindling",
                description = "A spark becomes a flame with patient tending",
                category = Category.CONSISTENCY,
                rarity = Rarity.COMMON,
                requirement = 3,
                requirementDescription = "Maintain a 3-day streak",
                iconName = "whatshot",
                celebrationMessage = "Three days - the kindling has caught. Keep tending the flame.",
                rewardPoints = 50
            )
        )
        add(
            Achievement(
                id = "streak_7",
                name = "Steady Flame",
                description = "A week of presence - the habit takes root",
                category = Category.CONSISTENCY,
                rarity = Rarity.UNCOMMON,
                requirement = 7,
                requirementDescription = "Maintain a 7-day streak",
                iconName = "local_fire_department",
                celebrationMessage = "Seven days without breaking - you are building something real.",
                rewardPoints = 150
            )
        )
        add(
            Achievement(
                id = "streak_14",
                name = "Fortnight's Dedication",
                description = "Two weeks of unwavering commitment",
                category = Category.CONSISTENCY,
                rarity = Rarity.RARE,
                requirement = 14,
                requirementDescription = "Maintain a 14-day streak",
                iconName = "bolt",
                celebrationMessage = "Fourteen days of presence - the practice is becoming part of you.",
                rewardPoints = 300
            )
        )
        add(
            Achievement(
                id = "streak_21",
                name = "Habit Formed",
                description = "Twenty-one days - the threshold of transformation",
                category = Category.CONSISTENCY,
                rarity = Rarity.RARE,
                requirement = 21,
                requirementDescription = "Maintain a 21-day streak",
                iconName = "psychology_alt",
                celebrationMessage = "Twenty-one days - science says this is when habits form. You are proof.",
                rewardPoints = 400
            )
        )
        add(
            Achievement(
                id = "streak_30",
                name = "Moon Cycle",
                description = "A complete lunar cycle of daily practice",
                category = Category.CONSISTENCY,
                rarity = Rarity.RARE,
                requirement = 30,
                requirementDescription = "Maintain a 30-day streak",
                iconName = "nightlight",
                celebrationMessage = "One moon's passage of unbroken dedication. The habit is forged.",
                rewardPoints = 500
            )
        )
        add(
            Achievement(
                id = "streak_60",
                name = "Two Moons",
                description = "Two months of unwavering presence",
                category = Category.CONSISTENCY,
                rarity = Rarity.EPIC,
                requirement = 60,
                requirementDescription = "Maintain a 60-day streak",
                iconName = "dark_mode",
                celebrationMessage = "Sixty days - two moons have witnessed your dedication.",
                rewardPoints = 800
            )
        )
        add(
            Achievement(
                id = "streak_90",
                name = "Season of Growth",
                description = "Three months - a season of transformation",
                category = Category.CONSISTENCY,
                rarity = Rarity.EPIC,
                requirement = 90,
                requirementDescription = "Maintain a 90-day streak",
                iconName = "park",
                celebrationMessage = "A full season of growth. You have transformed.",
                rewardPoints = 1500
            )
        )
        add(
            Achievement(
                id = "streak_180",
                name = "Half Year's Journey",
                description = "Six months of daily dedication",
                category = Category.CONSISTENCY,
                rarity = Rarity.EPIC,
                requirement = 180,
                requirementDescription = "Maintain a 180-day streak",
                iconName = "landscape",
                celebrationMessage = "One hundred eighty days - half a year of presence. Remarkable.",
                rewardPoints = 3000
            )
        )
        add(
            Achievement(
                id = "streak_365",
                name = "Year of Presence",
                description = "365 days of showing up for yourself",
                category = Category.CONSISTENCY,
                rarity = Rarity.LEGENDARY,
                requirement = 365,
                requirementDescription = "Maintain a 365-day streak",
                iconName = "stars",
                celebrationMessage = "One complete orbit around the sun, present each day. This is mastery.",
                rewardPoints = 5000
            )
        )

        // ===== PRESENCE CATEGORY =====
        add(
            Achievement(
                id = "buddha_first",
                name = "First Counsel",
                description = "You sought wisdom, and wisdom answered",
                category = Category.PRESENCE,
                rarity = Rarity.COMMON,
                requirement = 1,
                requirementDescription = "Have your first conversation with Buddha",
                iconName = "forum",
                celebrationMessage = "The dialogue has begun. Buddha awaits your questions.",
                rewardPoints = 25
            )
        )
        add(
            Achievement(
                id = "buddha_5",
                name = "Curious Mind",
                description = "Questions are the beginning of wisdom",
                category = Category.PRESENCE,
                rarity = Rarity.COMMON,
                requirement = 5,
                requirementDescription = "Have 5 conversations with Buddha",
                iconName = "help_outline",
                celebrationMessage = "Five conversations - your curiosity leads you deeper.",
                rewardPoints = 50
            )
        )
        add(
            Achievement(
                id = "buddha_10",
                name = "Seeking Mind",
                description = "One who asks is one who grows",
                category = Category.PRESENCE,
                rarity = Rarity.UNCOMMON,
                requirement = 10,
                requirementDescription = "Have 10 conversations with Buddha",
                iconName = "psychology_alt",
                celebrationMessage = "Ten conversations - you are learning to ask the right questions.",
                rewardPoints = 150
            )
        )
        add(
            Achievement(
                id = "buddha_25",
                name = "Faithful Student",
                description = "Returning to the teacher is itself a form of wisdom",
                category = Category.PRESENCE,
                rarity = Rarity.UNCOMMON,
                requirement = 25,
                requirementDescription = "Have 25 conversations with Buddha",
                iconName = "school",
                celebrationMessage = "Twenty-five returns to wisdom - your patience bears fruit.",
                rewardPoints = 250
            )
        )
        add(
            Achievement(
                id = "buddha_50",
                name = "Dialogue of Wisdom",
                description = "A deepening friendship with your inner sage",
                category = Category.PRESENCE,
                rarity = Rarity.RARE,
                requirement = 50,
                requirementDescription = "Have 50 conversations with Buddha",
                iconName = "diversity_3",
                celebrationMessage = "Fifty dialogues with wisdom - the sage within awakens.",
                rewardPoints = 500
            )
        )
        add(
            Achievement(
                id = "buddha_100",
                name = "The Eternal Student",
                description = "Wisdom is found in the asking, not just the answers",
                category = Category.PRESENCE,
                rarity = Rarity.EPIC,
                requirement = 100,
                requirementDescription = "Have 100 conversations with Buddha",
                iconName = "self_improvement",
                celebrationMessage = "One hundred exchanges with Buddha. You have become a true student of life.",
                rewardPoints = 1000
            )
        )
        add(
            Achievement(
                id = "buddha_250",
                name = "Wisdom Companion",
                description = "Buddha has become a trusted companion on your path",
                category = Category.PRESENCE,
                rarity = Rarity.LEGENDARY,
                requirement = 250,
                requirementDescription = "Have 250 conversations with Buddha",
                iconName = "handshake",
                celebrationMessage = "Two hundred fifty conversations - wisdom walks beside you always.",
                rewardPoints = 2500
            )
        )

        // ===== TEMPORAL CATEGORY =====
        add(
            Achievement(
                id = "letter_first",
                name = "Message in a Bottle",
                description = "A letter cast into the river of time",
                category = Category.TEMPORAL,
                rarity = Rarity.UNCOMMON,
                requirement = 1,
                requirementDescription = "Write your first future-self letter",
                iconName = "mail",
                celebrationMessage = "Your words now travel toward a future you. May they arrive with meaning.",
                rewardPoints = 50
            )
        )
        add(
            Achievement(
                id = "letter_3",
                name = "Time Correspondent",
                description = "Building a bridge across the days",
                category = Category.TEMPORAL,
                rarity = Rarity.UNCOMMON,
                requirement = 3,
                requirementDescription = "Write 3 future-self letters",
                iconName = "send",
                celebrationMessage = "Three letters sent forward - you are in conversation with your future.",
                rewardPoints = 100
            )
        )
        add(
            Achievement(
                id = "letter_5",
                name = "Time Weaver",
                description = "Connecting present intention to future realization",
                category = Category.TEMPORAL,
                rarity = Rarity.RARE,
                requirement = 5,
                requirementDescription = "Write 5 future-self letters",
                iconName = "hourglass_empty",
                celebrationMessage = "Five letters to your future self - you are weaving threads across time.",
                rewardPoints = 200
            )
        )
        add(
            Achievement(
                id = "letter_10",
                name = "Temporal Architect",
                description = "You build your future with words in the present",
                category = Category.TEMPORAL,
                rarity = Rarity.EPIC,
                requirement = 10,
                requirementDescription = "Write 10 future-self letters",
                iconName = "architecture",
                celebrationMessage = "Ten letters - you architect your future with intention.",
                rewardPoints = 500
            )
        )
        add(
            Achievement(
                id = "letter_received",
                name = "Echo from the Past",
                description = "Your past self has something to tell you",
                category = Category.TEMPORAL,
                rarity = Rarity.RARE,
                requirement = 1,
                requirementDescription = "Receive your first future-self letter",
                iconName = "mark_email_read",
                celebrationMessage = "A message from who you were. Listen carefully.",
                rewardPoints = 300
            )
        )
        add(
            Achievement(
                id = "letter_received_5",
                name = "Time Listener",
                description = "The past speaks, and you listen",
                category = Category.TEMPORAL,
                rarity = Rarity.EPIC,
                requirement = 5,
                requirementDescription = "Receive 5 future-self letters",
                iconName = "hearing",
                celebrationMessage = "Five messages from your past selves - the conversation across time deepens.",
                rewardPoints = 750
            )
        )

        // ===== MASTERY CATEGORY =====
        add(
            Achievement(
                id = "early_bird",
                name = "Dawn Seeker",
                description = "Those who rise with the sun often find themselves ahead",
                category = Category.MASTERY,
                rarity = Rarity.UNCOMMON,
                requirement = 7,
                requirementDescription = "Use Prody before 7 AM for 7 days",
                iconName = "wb_twilight",
                celebrationMessage = "Seven dawns greeted with intention. The early hours hold power.",
                rewardPoints = 200
            )
        )
        add(
            Achievement(
                id = "night_owl",
                name = "Evening Contemplative",
                description = "The quiet hours of night invite reflection",
                category = Category.MASTERY,
                rarity = Rarity.UNCOMMON,
                requirement = 7,
                requirementDescription = "Use Prody after 10 PM for 7 days",
                iconName = "nights_stay",
                celebrationMessage = "Seven nights of quiet contemplation. The darkness holds its own wisdom.",
                rewardPoints = 200
            )
        )
        add(
            Achievement(
                id = "completionist_daily",
                name = "The Complete Day",
                description = "Every feature touched, every opportunity seized",
                category = Category.MASTERY,
                rarity = Rarity.RARE,
                requirement = 1,
                requirementDescription = "Use all Prody features in a single day",
                iconName = "check_circle",
                celebrationMessage = "A day fully lived within Prody. This is presence.",
                rewardPoints = 300
            )
        )
        add(
            Achievement(
                id = "completionist_weekly",
                name = "Week of Wholeness",
                description = "Seven complete days of full engagement",
                category = Category.MASTERY,
                rarity = Rarity.EPIC,
                requirement = 7,
                requirementDescription = "Complete all features for 7 consecutive days",
                iconName = "verified",
                celebrationMessage = "Seven complete days - you embrace the full journey.",
                rewardPoints = 1000
            )
        )
        add(
            Achievement(
                id = "level_3",
                name = "Ascending",
                description = "The first steps of ascent are behind you",
                category = Category.MASTERY,
                rarity = Rarity.COMMON,
                requirement = 3,
                requirementDescription = "Reach Level 3",
                iconName = "moving",
                celebrationMessage = "Level 3 - you are ascending. The view begins to widen.",
                rewardPoints = 50
            )
        )
        add(
            Achievement(
                id = "level_5",
                name = "Rising",
                description = "The path of growth leads ever upward",
                category = Category.MASTERY,
                rarity = Rarity.UNCOMMON,
                requirement = 5,
                requirementDescription = "Reach Level 5",
                iconName = "trending_up",
                celebrationMessage = "Level 5 - you are rising. The view grows clearer with each step.",
                rewardPoints = 150
            )
        )
        add(
            Achievement(
                id = "level_10",
                name = "The Ascent",
                description = "Ten levels of dedicated growth",
                category = Category.MASTERY,
                rarity = Rarity.EPIC,
                requirement = 10,
                requirementDescription = "Reach Level 10",
                iconName = "landscape",
                celebrationMessage = "Ten levels climbed. You stand now where few have stood.",
                rewardPoints = 1000
            )
        )
        add(
            Achievement(
                id = "level_20",
                name = "Summit Reached",
                description = "Twenty levels of mastery and dedication",
                category = Category.MASTERY,
                rarity = Rarity.LEGENDARY,
                requirement = 20,
                requirementDescription = "Reach Level 20",
                iconName = "terrain",
                celebrationMessage = "Level 20 - the summit reveals itself. Your dedication is extraordinary.",
                rewardPoints = 5000
            )
        )
        add(
            Achievement(
                id = "review_master_10",
                name = "Review Initiate",
                description = "Spaced repetition strengthens the mind",
                category = Category.MASTERY,
                rarity = Rarity.COMMON,
                requirement = 10,
                requirementDescription = "Complete 10 review sessions",
                iconName = "refresh",
                celebrationMessage = "Ten reviews - memory grows stronger through practice.",
                rewardPoints = 50
            )
        )
        add(
            Achievement(
                id = "review_master_50",
                name = "Memory Keeper",
                description = "What is reviewed is remembered",
                category = Category.MASTERY,
                rarity = Rarity.RARE,
                requirement = 50,
                requirementDescription = "Complete 50 review sessions",
                iconName = "memory",
                celebrationMessage = "Fifty reviews - your mind retains what you have chosen to keep.",
                rewardPoints = 300
            )
        )
        add(
            Achievement(
                id = "founder",
                name = "Foundation Stone",
                description = "Present from the earliest days",
                category = Category.MASTERY,
                rarity = Rarity.LEGENDARY,
                requirement = 1,
                requirementDescription = "Use Prody within its first month of existence",
                iconName = "foundation",
                celebrationMessage = "You were here at the beginning. This journey, we share.",
                rewardPoints = 1000
            )
        )

        // ===== SOCIAL CATEGORY =====
        add(
            Achievement(
                id = "first_share",
                name = "Sharing is Caring",
                description = "Your journey inspires others",
                category = Category.SOCIAL,
                rarity = Rarity.COMMON,
                requirement = 1,
                requirementDescription = "Share your profile for the first time",
                iconName = "share",
                celebrationMessage = "You've shared your growth journey. May it inspire others.",
                rewardPoints = 50
            )
        )
        add(
            Achievement(
                id = "social_butterfly",
                name = "Social Butterfly",
                description = "Spreading inspiration far and wide",
                category = Category.SOCIAL,
                rarity = Rarity.UNCOMMON,
                requirement = 5,
                requirementDescription = "Share your achievements 5 times",
                iconName = "flutter_dash",
                celebrationMessage = "Five shares - your journey touches many hearts.",
                rewardPoints = 100
            )
        )
        add(
            Achievement(
                id = "influencer",
                name = "Growth Influencer",
                description = "Your dedication inspires a community",
                category = Category.SOCIAL,
                rarity = Rarity.RARE,
                requirement = 15,
                requirementDescription = "Share your achievements 15 times",
                iconName = "campaign",
                celebrationMessage = "Fifteen moments shared - you are building a community of growth.",
                rewardPoints = 300
            )
        )
        add(
            Achievement(
                id = "leaderboard_top3",
                name = "Podium Finisher",
                description = "Among the most dedicated seekers",
                category = Category.SOCIAL,
                rarity = Rarity.EPIC,
                requirement = 1,
                requirementDescription = "Reach the top 3 on the leaderboard",
                iconName = "emoji_events",
                celebrationMessage = "You stand among the greats. Timi ta babal raixau yr!",
                rewardPoints = 500
            )
        )
        add(
            Achievement(
                id = "leaderboard_champion",
                name = "Champion of Growth",
                description = "The pinnacle of dedication and consistency",
                category = Category.SOCIAL,
                rarity = Rarity.LEGENDARY,
                requirement = 1,
                requirementDescription = "Reach #1 on the leaderboard",
                iconName = "military_tech",
                celebrationMessage = "You are the champion. Your dedication is unmatched!",
                rewardPoints = 1000
            )
        )

        // ===== EXPLORER CATEGORY =====
        add(
            Achievement(
                id = "first_word_learned",
                name = "Word Explorer",
                description = "The journey of a thousand words begins with one",
                category = Category.EXPLORER,
                rarity = Rarity.COMMON,
                requirement = 1,
                requirementDescription = "View your first word of the day",
                iconName = "search",
                celebrationMessage = "You've discovered your first word. Many more await!",
                rewardPoints = 25
            )
        )
        add(
            Achievement(
                id = "quote_explorer",
                name = "Quote Explorer",
                description = "Wisdom from the ages, discovered",
                category = Category.EXPLORER,
                rarity = Rarity.COMMON,
                requirement = 1,
                requirementDescription = "Read your first daily quote",
                iconName = "format_quote",
                celebrationMessage = "You've discovered the wisdom section. Explore freely!",
                rewardPoints = 25
            )
        )
        add(
            Achievement(
                id = "meditation_beginner",
                name = "Mindful Moment",
                description = "The first step into stillness",
                category = Category.EXPLORER,
                rarity = Rarity.COMMON,
                requirement = 1,
                requirementDescription = "Complete your first meditation session",
                iconName = "self_improvement",
                celebrationMessage = "You've taken your first mindful breath. Peace awaits.",
                rewardPoints = 50
            )
        )
        add(
            Achievement(
                id = "meditation_regular",
                name = "Regular Meditator",
                description = "Finding peace through consistent practice",
                category = Category.EXPLORER,
                rarity = Rarity.RARE,
                requirement = 10,
                requirementDescription = "Complete 10 meditation sessions",
                iconName = "spa",
                celebrationMessage = "Ten sessions of stillness. The calm within grows.",
                rewardPoints = 200
            )
        )
        add(
            Achievement(
                id = "challenge_accepted",
                name = "Challenge Accepted",
                description = "Growth comes from stepping outside comfort zones",
                category = Category.EXPLORER,
                rarity = Rarity.UNCOMMON,
                requirement = 1,
                requirementDescription = "Join your first challenge",
                iconName = "flag",
                celebrationMessage = "You've accepted the challenge. Rise to it!",
                rewardPoints = 75
            )
        )
        add(
            Achievement(
                id = "challenge_victor",
                name = "Challenge Victor",
                description = "Challenges conquered, character forged",
                category = Category.EXPLORER,
                rarity = Rarity.RARE,
                requirement = 5,
                requirementDescription = "Complete 5 challenges",
                iconName = "star",
                celebrationMessage = "Five challenges completed. You are unstoppable!",
                rewardPoints = 300
            )
        )
        add(
            Achievement(
                id = "feature_master",
                name = "Feature Master",
                description = "Explored every corner of Prody",
                category = Category.EXPLORER,
                rarity = Rarity.EPIC,
                requirement = 1,
                requirementDescription = "Use all major Prody features at least once",
                iconName = "apps",
                celebrationMessage = "You've explored all of Prody. You are a true master!",
                rewardPoints = 500
            )
        )
        add(
            Achievement(
                id = "comeback_kid",
                name = "Comeback Kid",
                description = "Falling down is not failure, staying down is",
                category = Category.EXPLORER,
                rarity = Rarity.UNCOMMON,
                requirement = 1,
                requirementDescription = "Return after a 7+ day absence",
                iconName = "refresh",
                celebrationMessage = "Welcome back! Every return is a victory.",
                rewardPoints = 100
            )
        )
        add(
            Achievement(
                id = "night_explorer",
                name = "Night Explorer",
                description = "The quiet hours hold their own wisdom",
                category = Category.EXPLORER,
                rarity = Rarity.UNCOMMON,
                requirement = 5,
                requirementDescription = "Use Prody between midnight and 4 AM, 5 times",
                iconName = "bedtime",
                celebrationMessage = "Five midnight sessions. You find wisdom in the quiet hours.",
                rewardPoints = 150
            )
        )
        add(
            Achievement(
                id = "weekend_warrior",
                name = "Weekend Warrior",
                description = "Growth doesn't take weekends off",
                category = Category.EXPLORER,
                rarity = Rarity.RARE,
                requirement = 8,
                requirementDescription = "Use Prody on 8 consecutive weekend days",
                iconName = "weekend",
                celebrationMessage = "Eight weekends of dedication. You never stop growing!",
                rewardPoints = 250
            )
        )
    }

    /**
     * Finds an achievement by its unique identifier.
     *
     * @param id The achievement's unique identifier
     * @return The matching achievement, or null if not found
     */
    fun findById(id: String): Achievement? = allAchievements.find { it.id == id }

    /**
     * Gets all achievements in a specific category.
     *
     * @param category The category to filter by
     * @return List of achievements in the specified category
     */
    fun getByCategory(category: Category): List<Achievement> =
        allAchievements.filter { it.category == category }

    /**
     * Gets all achievements of a specific rarity.
     *
     * @param rarity The rarity to filter by
     * @return List of achievements with the specified rarity
     */
    fun getByRarity(rarity: Rarity): List<Achievement> =
        allAchievements.filter { it.rarity == rarity }

    /**
     * Gets the total number of achievements.
     */
    val totalCount: Int get() = allAchievements.size

    /**
     * Gets achievements grouped by category.
     */
    val groupedByCategory: Map<Category, List<Achievement>>
        get() = allAchievements.groupBy { it.category }

    /**
     * Calculates total possible reward points from all achievements.
     */
    val totalPossiblePoints: Int
        get() = allAchievements.sumOf { it.rewardPoints }
}
