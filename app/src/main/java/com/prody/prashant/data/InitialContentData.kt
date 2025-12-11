package com.prody.prashant.data

import com.prody.prashant.data.local.entity.*

/**
 * Initial content data for the app. This provides a rich starting experience
 * without requiring internet connectivity or backend services.
 */
object InitialContentData {

    val vocabularyWords = listOf(
        VocabularyEntity(
            word = "Serendipity",
            definition = "The occurrence of events by chance in a happy or beneficial way",
            pronunciation = "ser-uhn-DIP-i-tee",
            partOfSpeech = "noun",
            exampleSentence = "Finding that old book at the garage sale was pure serendipity.",
            synonyms = "chance, fortune, luck, fate",
            antonyms = "misfortune, design",
            origin = "Coined by Horace Walpole in 1754, from the Persian fairy tale 'The Three Princes of Serendip'",
            difficulty = 3,
            category = "literary"
        ),
        VocabularyEntity(
            word = "Ephemeral",
            definition = "Lasting for a very short time; transient",
            pronunciation = "ih-FEM-er-uhl",
            partOfSpeech = "adjective",
            exampleSentence = "The ephemeral beauty of cherry blossoms reminds us to appreciate the present moment.",
            synonyms = "fleeting, transient, momentary, brief",
            antonyms = "permanent, lasting, enduring",
            origin = "From Greek 'ephemeros' meaning 'lasting only a day'",
            difficulty = 3,
            category = "literary"
        ),
        VocabularyEntity(
            word = "Resilience",
            definition = "The capacity to recover quickly from difficulties; toughness",
            pronunciation = "ri-ZIL-yuhns",
            partOfSpeech = "noun",
            exampleSentence = "Her resilience in the face of adversity inspired everyone around her.",
            synonyms = "toughness, flexibility, adaptability, strength",
            antonyms = "fragility, weakness, rigidity",
            origin = "From Latin 'resilire' meaning 'to spring back'",
            difficulty = 2,
            category = "personal_growth"
        ),
        VocabularyEntity(
            word = "Equanimity",
            definition = "Mental calmness and composure, especially in difficult situations",
            pronunciation = "ee-kwuh-NIM-i-tee",
            partOfSpeech = "noun",
            exampleSentence = "The monk faced both praise and criticism with remarkable equanimity.",
            synonyms = "composure, calmness, serenity, poise",
            antonyms = "anxiety, agitation, distress",
            origin = "From Latin 'aequanimitas', from 'aequus' (equal) + 'animus' (mind)",
            difficulty = 4,
            category = "stoic"
        ),
        VocabularyEntity(
            word = "Perspicacious",
            definition = "Having a ready insight into things; shrewd and discerning",
            pronunciation = "pur-spi-KAY-shuhs",
            partOfSpeech = "adjective",
            exampleSentence = "Her perspicacious analysis of the market trends saved the company millions.",
            synonyms = "astute, shrewd, perceptive, insightful",
            antonyms = "obtuse, dense, unperceptive",
            origin = "From Latin 'perspicax' meaning 'sharp-sighted'",
            difficulty = 5,
            category = "academic"
        ),
        VocabularyEntity(
            word = "Sonder",
            definition = "The realization that each passerby has a life as vivid and complex as your own",
            pronunciation = "SON-der",
            partOfSpeech = "noun",
            exampleSentence = "Sitting in the busy cafe, she experienced a profound sense of sonder.",
            synonyms = "empathy, awareness, realization",
            antonyms = "self-absorption, obliviousness",
            origin = "Neologism coined in 2012 by John Koenig for The Dictionary of Obscure Sorrows",
            difficulty = 3,
            category = "philosophical"
        ),
        VocabularyEntity(
            word = "Mellifluous",
            definition = "Sweet-sounding; pleasant to hear",
            pronunciation = "muh-LIF-loo-uhs",
            partOfSpeech = "adjective",
            exampleSentence = "The singer's mellifluous voice captivated the entire audience.",
            synonyms = "dulcet, harmonious, melodious, honeyed",
            antonyms = "harsh, grating, discordant",
            origin = "From Latin 'mel' (honey) + 'fluere' (to flow)",
            difficulty = 4,
            category = "literary"
        ),
        VocabularyEntity(
            word = "Eudaimonia",
            definition = "A state of flourishing; human flourishing or prosperity",
            pronunciation = "yoo-dy-MOH-nee-uh",
            partOfSpeech = "noun",
            exampleSentence = "The philosopher argued that eudaimonia, not pleasure, should be our ultimate goal.",
            synonyms = "flourishing, well-being, fulfillment",
            antonyms = "misery, suffering, languishing",
            origin = "Ancient Greek concept central to Aristotelian ethics",
            difficulty = 5,
            category = "philosophical"
        ),
        VocabularyEntity(
            word = "Laconic",
            definition = "Using very few words; brief and to the point",
            pronunciation = "luh-KON-ik",
            partOfSpeech = "adjective",
            exampleSentence = "His laconic response of 'If.' became legendary in history.",
            synonyms = "concise, terse, succinct, pithy",
            antonyms = "verbose, wordy, loquacious",
            origin = "From Laconia, the region of Greece around Sparta, whose people were known for brevity",
            difficulty = 3,
            category = "academic"
        ),
        VocabularyEntity(
            word = "Sanguine",
            definition = "Optimistic, especially in difficult situations; cheerfully confident",
            pronunciation = "SANG-gwin",
            partOfSpeech = "adjective",
            exampleSentence = "Despite the setbacks, she remained sanguine about the project's success.",
            synonyms = "optimistic, hopeful, confident, positive",
            antonyms = "pessimistic, gloomy, despairing",
            origin = "From Latin 'sanguineus' meaning 'blood-red', related to the medieval belief in humors",
            difficulty = 3,
            category = "personal_growth"
        ),
        VocabularyEntity(
            word = "Efficacious",
            definition = "Successful in producing a desired result; effective",
            pronunciation = "ef-i-KAY-shuhs",
            partOfSpeech = "adjective",
            exampleSentence = "The new treatment proved highly efficacious in clinical trials.",
            synonyms = "effective, productive, successful, potent",
            antonyms = "ineffective, useless, futile",
            origin = "From Latin 'efficax' meaning 'powerful, effectual'",
            difficulty = 4,
            category = "academic"
        ),
        VocabularyEntity(
            word = "Magnanimous",
            definition = "Generous or forgiving, especially toward a rival or less powerful person",
            pronunciation = "mag-NAN-uh-muhs",
            partOfSpeech = "adjective",
            exampleSentence = "The magnanimous leader pardoned his defeated opponents.",
            synonyms = "generous, noble, benevolent, charitable",
            antonyms = "petty, mean-spirited, vindictive",
            origin = "From Latin 'magnus' (great) + 'animus' (soul)",
            difficulty = 3,
            category = "stoic"
        ),
        VocabularyEntity(
            word = "Prosaic",
            definition = "Lacking poetic beauty; commonplace, unromantic",
            pronunciation = "proh-ZAY-ik",
            partOfSpeech = "adjective",
            exampleSentence = "He described their adventure in prosaic terms that failed to capture its magic.",
            synonyms = "mundane, ordinary, pedestrian, banal",
            antonyms = "poetic, imaginative, romantic",
            origin = "From Latin 'prosa' meaning 'straightforward discourse'",
            difficulty = 3,
            category = "literary"
        ),
        VocabularyEntity(
            word = "Zeitgeist",
            definition = "The defining spirit or mood of a particular period of history",
            pronunciation = "ZITE-gyst",
            partOfSpeech = "noun",
            exampleSentence = "The music of the 1960s perfectly captured the zeitgeist of social revolution.",
            synonyms = "spirit of the age, ethos, mood, atmosphere",
            antonyms = "N/A",
            origin = "German: 'Zeit' (time) + 'Geist' (spirit)",
            difficulty = 3,
            category = "philosophical"
        ),
        VocabularyEntity(
            word = "Ineffable",
            definition = "Too great or extreme to be expressed in words",
            pronunciation = "in-EF-uh-buhl",
            partOfSpeech = "adjective",
            exampleSentence = "The beauty of the sunset was simply ineffable.",
            synonyms = "indescribable, inexpressible, unspeakable",
            antonyms = "describable, expressible",
            origin = "From Latin 'ineffabilis' meaning 'unutterable'",
            difficulty = 4,
            category = "literary"
        )
    )

    val quotes = listOf(
        QuoteEntity(
            content = "The obstacle is the way.",
            author = "Marcus Aurelius",
            source = "Meditations",
            category = "stoic",
            tags = "adversity,growth,wisdom",
            reflectionPrompt = "What obstacle in your life might actually be pointing you toward growth?"
        ),
        QuoteEntity(
            content = "We suffer more often in imagination than in reality.",
            author = "Seneca",
            source = "Letters from a Stoic",
            category = "stoic",
            tags = "anxiety,mindset,perspective",
            reflectionPrompt = "What fears are you experiencing that may never actually come to pass?"
        ),
        QuoteEntity(
            content = "The only way to do great work is to love what you do.",
            author = "Steve Jobs",
            source = "Stanford Commencement Speech, 2005",
            category = "motivation",
            tags = "work,passion,success",
            reflectionPrompt = "How can you bring more of what you love into your daily work?"
        ),
        QuoteEntity(
            content = "It is not that we have a short time to live, but that we waste a lot of it.",
            author = "Seneca",
            source = "On the Shortness of Life",
            category = "stoic",
            tags = "time,life,wisdom",
            reflectionPrompt = "Where in your life might you be wasting time on things that don't matter?"
        ),
        QuoteEntity(
            content = "The unexamined life is not worth living.",
            author = "Socrates",
            source = "Plato's Apology",
            category = "philosophical",
            tags = "reflection,wisdom,life",
            reflectionPrompt = "When was the last time you deeply examined your beliefs and choices?"
        ),
        QuoteEntity(
            content = "What you get by achieving your goals is not as important as what you become by achieving your goals.",
            author = "Zig Ziglar",
            source = "See You at the Top",
            category = "motivation",
            tags = "goals,growth,success",
            reflectionPrompt = "How is the pursuit of your current goal changing you as a person?"
        ),
        QuoteEntity(
            content = "He who has a why to live can bear almost any how.",
            author = "Friedrich Nietzsche",
            source = "Twilight of the Idols",
            category = "philosophical",
            tags = "purpose,meaning,resilience",
            reflectionPrompt = "What is your 'why' that helps you persevere through challenges?"
        ),
        QuoteEntity(
            content = "The best time to plant a tree was 20 years ago. The second best time is now.",
            author = "Chinese Proverb",
            source = "Traditional",
            category = "wisdom",
            tags = "action,time,beginning",
            reflectionPrompt = "What have you been postponing that you could start today?"
        ),
        QuoteEntity(
            content = "Waste no more time arguing about what a good man should be. Be one.",
            author = "Marcus Aurelius",
            source = "Meditations",
            category = "stoic",
            tags = "action,character,virtue",
            reflectionPrompt = "What action could you take today that aligns with your values?"
        ),
        QuoteEntity(
            content = "The mind is everything. What you think you become.",
            author = "Buddha",
            source = "Dhammapada",
            category = "wisdom",
            tags = "mindset,thoughts,transformation",
            reflectionPrompt = "What thoughts have been shaping your reality lately?"
        ),
        QuoteEntity(
            content = "Excellence is not a singular act, but a habit. You are what you repeatedly do.",
            author = "Shaquille O'Neal (paraphrasing Aristotle)",
            source = "Interview",
            category = "motivation",
            tags = "habits,excellence,discipline",
            reflectionPrompt = "What habit, if developed, would transform your life?"
        ),
        QuoteEntity(
            content = "In the middle of difficulty lies opportunity.",
            author = "Albert Einstein",
            source = "Attributed",
            category = "wisdom",
            tags = "adversity,opportunity,perspective",
            reflectionPrompt = "What opportunity might be hidden in your current challenge?"
        ),
        QuoteEntity(
            content = "The happiness of your life depends upon the quality of your thoughts.",
            author = "Marcus Aurelius",
            source = "Meditations",
            category = "stoic",
            tags = "happiness,thoughts,mindset",
            reflectionPrompt = "How would you rate the quality of your thoughts today?"
        ),
        QuoteEntity(
            content = "It does not matter how slowly you go as long as you do not stop.",
            author = "Confucius",
            source = "Analects",
            category = "wisdom",
            tags = "persistence,progress,patience",
            reflectionPrompt = "Where in your life do you need to trust the process and keep moving forward?"
        ),
        QuoteEntity(
            content = "We are what we repeatedly do. Excellence, then, is not an act, but a habit.",
            author = "Will Durant (summarizing Aristotle)",
            source = "The Story of Philosophy",
            category = "philosophical",
            tags = "habits,excellence,character",
            reflectionPrompt = "What does your daily routine say about who you're becoming?"
        )
    )

    val proverbs = listOf(
        ProverbEntity(
            content = "A journey of a thousand miles begins with a single step.",
            meaning = "Even the longest and most difficult ventures have a starting point; something that begins with one small step.",
            origin = "Chinese (Lao Tzu)",
            usage = "Use when encouraging someone to start a daunting task",
            category = "wisdom"
        ),
        ProverbEntity(
            content = "The pen is mightier than the sword.",
            meaning = "Writing and communication can be more effective than direct violence or military force.",
            origin = "English (Edward Bulwer-Lytton, 1839)",
            usage = "Use when emphasizing the power of ideas and communication",
            category = "wisdom"
        ),
        ProverbEntity(
            content = "Fall seven times, stand up eight.",
            meaning = "No matter how many times you fail, keep getting back up. Resilience is key.",
            origin = "Japanese",
            usage = "Use when encouraging persistence after failures",
            category = "resilience"
        ),
        ProverbEntity(
            content = "The nail that sticks out gets hammered down.",
            meaning = "Those who stand out or are different often face pressure to conform.",
            origin = "Japanese",
            usage = "Use when discussing conformity vs. individuality",
            category = "philosophical"
        ),
        ProverbEntity(
            content = "A smooth sea never made a skilled sailor.",
            meaning = "Difficulties and challenges are necessary for developing strength and expertise.",
            origin = "English",
            usage = "Use when encouraging someone facing difficulties",
            category = "resilience"
        ),
        ProverbEntity(
            content = "The bamboo that bends is stronger than the oak that resists.",
            meaning = "Flexibility and adaptability often prove more durable than rigid resistance.",
            origin = "Japanese",
            usage = "Use when advising adaptability over stubbornness",
            category = "wisdom"
        ),
        ProverbEntity(
            content = "What you seek is seeking you.",
            meaning = "When you pursue something with genuine intention, the universe tends to meet you halfway.",
            origin = "Persian (Rumi)",
            usage = "Use when encouraging someone to pursue their true desires",
            category = "motivation"
        ),
        ProverbEntity(
            content = "A bird in the hand is worth two in the bush.",
            meaning = "It's better to hold onto something you have than to risk losing it by trying to get something better.",
            origin = "English (Medieval)",
            usage = "Use when advising caution over speculation",
            category = "wisdom"
        ),
        ProverbEntity(
            content = "He who chases two rabbits catches neither.",
            meaning = "If you try to do two things at once, you won't succeed at either.",
            origin = "Roman",
            usage = "Use when advising focus over multitasking",
            category = "focus"
        ),
        ProverbEntity(
            content = "The best revenge is a life well lived.",
            meaning = "Rather than seeking vengeance, the greatest response to those who wrong you is to live happily and successfully.",
            origin = "Spanish (attributed to George Herbert)",
            usage = "Use when discussing how to respond to injustice",
            category = "stoic"
        )
    )

    val idioms = listOf(
        IdiomEntity(
            phrase = "Burning the midnight oil",
            meaning = "Working late into the night",
            origin = "From the days before electric lighting when oil lamps were used",
            exampleSentence = "She's been burning the midnight oil to finish her thesis.",
            category = "work"
        ),
        IdiomEntity(
            phrase = "The elephant in the room",
            meaning = "An obvious problem or issue that people avoid discussing",
            origin = "American English, 20th century",
            exampleSentence = "Let's address the elephant in the room - our budget is completely unrealistic.",
            category = "communication"
        ),
        IdiomEntity(
            phrase = "Bite the bullet",
            meaning = "To endure a painful or difficult situation with courage",
            origin = "From the practice of having patients bite a bullet during surgery before anesthesia",
            exampleSentence = "I need to bite the bullet and have that difficult conversation with my manager.",
            category = "courage"
        ),
        IdiomEntity(
            phrase = "Break the ice",
            meaning = "To initiate social interaction and reduce tension or awkwardness",
            origin = "From the practice of sending small ships to break ice for larger vessels",
            exampleSentence = "His joke helped break the ice at the networking event.",
            category = "social"
        ),
        IdiomEntity(
            phrase = "Cut to the chase",
            meaning = "Get to the point; skip the unnecessary details",
            origin = "From early films where action scenes would cut directly to chase sequences",
            exampleSentence = "Let's cut to the chase - are you interested in the proposal or not?",
            category = "communication"
        ),
        IdiomEntity(
            phrase = "Devil's advocate",
            meaning = "Someone who argues against a cause for the sake of argument",
            origin = "From the Catholic Church's practice of appointing someone to argue against canonization",
            exampleSentence = "Let me play devil's advocate here - what if our assumptions are wrong?",
            category = "debate"
        ),
        IdiomEntity(
            phrase = "Hit the ground running",
            meaning = "To start something and proceed quickly without delay",
            origin = "Military parachute training terminology",
            exampleSentence = "We need someone who can hit the ground running on day one.",
            category = "work"
        ),
        IdiomEntity(
            phrase = "The ball is in your court",
            meaning = "It's your turn to take action or make a decision",
            origin = "From tennis, where the player whose side the ball is on must play it",
            exampleSentence = "I've made my offer - the ball is in your court now.",
            category = "decision"
        )
    )

    val phrases = listOf(
        PhraseEntity(
            phrase = "At the end of the day",
            meaning = "Ultimately; when all is considered",
            usage = "Used to introduce a final assessment or conclusion",
            exampleSentence = "At the end of the day, what matters most is that you tried your best.",
            formality = "neutral",
            category = "conclusion"
        ),
        PhraseEntity(
            phrase = "To be on the same page",
            meaning = "To have the same understanding or agree on something",
            usage = "Used in professional and personal contexts to confirm alignment",
            exampleSentence = "Let's make sure we're all on the same page before the meeting.",
            formality = "neutral",
            category = "agreement"
        ),
        PhraseEntity(
            phrase = "To touch base",
            meaning = "To briefly make contact or check in with someone",
            usage = "Common in business contexts for quick communication",
            exampleSentence = "I just wanted to touch base about the project timeline.",
            formality = "informal",
            category = "communication"
        ),
        PhraseEntity(
            phrase = "For what it's worth",
            meaning = "Introducing an opinion that may or may not be helpful",
            usage = "Used to offer perspective while acknowledging it may not be wanted",
            exampleSentence = "For what it's worth, I think you made the right decision.",
            formality = "neutral",
            category = "opinion"
        ),
        PhraseEntity(
            phrase = "To think outside the box",
            meaning = "To think creatively and unconventionally",
            usage = "Often used when encouraging innovative problem-solving",
            exampleSentence = "We need to think outside the box to solve this challenge.",
            formality = "neutral",
            category = "creativity"
        ),
        PhraseEntity(
            phrase = "With all due respect",
            meaning = "Polite preface before expressing disagreement",
            usage = "Used to soften a potentially offensive statement",
            exampleSentence = "With all due respect, I believe there's a better approach.",
            formality = "formal",
            category = "disagreement"
        ),
        PhraseEntity(
            phrase = "To play it by ear",
            meaning = "To improvise and see how things develop",
            usage = "Used when there's no fixed plan",
            exampleSentence = "Let's play it by ear and adjust as needed.",
            formality = "informal",
            category = "planning"
        ),
        PhraseEntity(
            phrase = "In a nutshell",
            meaning = "In summary; briefly stated",
            usage = "Used to provide a concise summary",
            exampleSentence = "In a nutshell, we need to reduce costs and increase efficiency.",
            formality = "neutral",
            category = "summary"
        )
    )
}
