package com.kairos.app.data.content

import com.kairos.app.domain.vocabulary.DerivedWord
import com.kairos.app.domain.vocabulary.WordFamilyExpander
import com.kairos.app.domain.vocabulary.WordFamilySpec

/**
 * Expanded curated vocabulary catalog.
 *
 * The original seed catalog was ~47 words — enough for a few weeks of daily words.
 * This expansion adds curated roots plus their genuine derived families, which
 * multiplies the catalog several times over with real, correct English. It feeds
 * both fresh installs (via [com.kairos.app.data.local.database.DatabaseSeeder]) and
 * existing installs (via CatalogExpansionManager, which inserts only missing rows).
 */
object ExpandedVocabularyContent {

    val specs: List<WordFamilySpec> = listOf(

        // ---- Self-improvement -------------------------------------------------
        WordFamilySpec(
            word = "Discipline", partOfSpeech = "noun",
            definition = "The practice of training oneself to act in accordance with rules or a chosen code of behavior",
            pronunciation = "DIS-uh-plin",
            exampleSentence = "Daily discipline mattered more than occasional bursts of motivation.",
            synonyms = "self-control, regimen, order", antonyms = "indulgence, chaos",
            difficulty = 2, category = "self-improvement",
            derived = listOf(
                DerivedWord("Disciplined", "adjective",
                    "Showing a controlled form of behavior based on training and self-restraint",
                    "DIS-uh-plind",
                    "She was disciplined about protecting her mornings for quiet study.",
                    "self-controlled, steady", "undisciplined, erratic"),
                DerivedWord("Self-Discipline", "noun",
                    "The ability to control one's feelings and overcome weaknesses; the ability to pursue what one thinks is right despite temptations to abandon it",
                    "self-DIS-uh-plin",
                    "Self-discipline turned a vague wish into a finished book.",
                    "willpower, resolve", "weakness, vacillation", difficultyOffset = 1)
            )
        ),
        WordFamilySpec(
            word = "Consistency", partOfSpeech = "noun",
            definition = "The quality of always behaving or performing in the same way over time",
            pronunciation = "kuhn-SIS-tuhn-see",
            exampleSentence = "Consistency is what separates a habit from a one-off effort.",
            synonyms = "steadiness, uniformity", antonyms = "inconsistency, fluctuation",
            difficulty = 2, category = "self-improvement",
            derived = listOf(
                DerivedWord("Consistent", "adjective",
                    "Acting or done in the same way over time, especially so as to be fair or accurate",
                    "kuhn-SIS-tuhnt",
                    "A consistent evening routine made sleep noticeably easier.",
                    "steady, dependable", "erratic, variable"),
                DerivedWord("Consistently", "adverb",
                    "In a way that does not change and continues at the same rate",
                    "kuhn-SIS-tuhnt-lee",
                    "She consistently reviewed five words before breakfast.",
                    "steadily, regularly", "sporadically, rarely", difficultyOffset = -1)
            )
        ),
        WordFamilySpec(
            word = "Deliberate", partOfSpeech = "adjective",
            definition = "Done consciously and intentionally; slow and careful in decision-making",
            pronunciation = "di-LIB-er-uht",
            exampleSentence = "A deliberate pause before answering gave her time to choose the honest words.",
            synonyms = "intentional, considered", antonyms = "hasty, impulsive",
            difficulty = 3, category = "self-improvement",
            derived = listOf(
                DerivedWord("Deliberately", "adverb",
                    "In a way that is done on purpose; slowly and carefully",
                    "di-LIB-er-uht-lee",
                    "He deliberately left his phone in another room while he wrote.",
                    "intentionally, purposefully", "accidentally, impulsively", difficultyOffset = -1),
                DerivedWord("Deliberation", "noun",
                    "Long and careful consideration or discussion",
                    "di-lib-uh-RAY-shuhn",
                    "After much deliberation, she chose the simpler plan.",
                    "consideration, reflection", "haste, recklessness", difficultyOffset = 1)
            )
        ),
        WordFamilySpec(
            word = "Habitual", partOfSpeech = "adjective",
            definition = "Done constantly or as a fixed habit; automatic from repetition",
            pronunciation = "huh-BICH-oo-uhl",
            exampleSentence = "Her habitual morning walk became the anchor of her day.",
            synonyms = "customary, routine", antonyms = "occasional, rare",
            difficulty = 3, category = "self-improvement",
            derived = listOf(
                DerivedWord("Habit", "noun",
                    "A settled or regular tendency or practice, especially one that is hard to give up",
                    "HAB-it",
                    "Reading one page before bed turned into a lifelong habit.",
                    "routine, custom", "exception, one-off", difficultyOffset = -1)
            )
        ),
        WordFamilySpec(
            word = "Persist", partOfSpeech = "verb",
            definition = "To continue firmly in an opinion or course of action despite difficulty or opposition",
            pronunciation = "pur-SIST",
            exampleSentence = "She chose to persist even when progress was invisible.",
            synonyms = "continue, persevere", antonyms = "quit, relent",
            difficulty = 2, category = "self-improvement",
            derived = listOf(
                DerivedWord("Persistence", "noun",
                    "The fact of continuing in an opinion or course of action in spite of difficulty",
                    "pur-SIS-tuhns",
                    "Persistence turned repeated small efforts into a visible skill.",
                    "tenacity, determination", "giving up, surrender", difficultyOffset = 1),
                DerivedWord("Persistent", "adjective",
                    "Continuing firmly or obstinately in a course of action in spite of difficulty",
                    "pur-SIS-tuhnt",
                    "A persistent learner reviews even on busy days.",
                    "determined, dogged", "half-hearted, sporadic", difficultyOffset = 1)
            )
        ),

        // ---- Communication ----------------------------------------------------
        WordFamilySpec(
            word = "Articulate", partOfSpeech = "adjective",
            definition = "Able to express thoughts and feelings clearly and effectively in speech or writing",
            pronunciation = "ahr-TIK-yuh-luht",
            exampleSentence = "Her articulate explanation made the idea easy to act on.",
            synonyms = "eloquent, fluent", antonyms = "inarticulate, unclear",
            difficulty = 3, category = "communication",
            derived = listOf(
                DerivedWord("Articulately", "adverb",
                    "In a clear, fluent, and effective way",
                    "ahr-TIK-yuh-luht-lee",
                    "He articulated his needs articulately instead of sulking.",
                    "clearly, fluently", "muddledly, vaguely", difficultyOffset = -1)
            )
        ),
        WordFamilySpec(
            word = "Concise", partOfSpeech = "adjective",
            definition = "Giving a lot of information clearly and in a few words",
            pronunciation = "kuhn-SISE",
            exampleSentence = "A concise answer respected everyone's time.",
            synonyms = "brief, succinct", antonyms = "verbose, wordy",
            difficulty = 2, category = "communication",
            derived = listOf(
                DerivedWord("Conciseness", "noun",
                    "The quality of expressing something in few words",
                    "kuhn-SISE-nuhs",
                    "Conciseness made his writing far easier to edit.",
                    "brevity, economy", "verbosity, length", difficultyOffset = 1)
            )
        ),
        WordFamilySpec(
            word = "Nuance", partOfSpeech = "noun",
            definition = "A subtle distinction or variation; a delicate degree of difference in meaning or feeling",
            pronunciation = "NOO-ahns",
            exampleSentence = "The apology carried a nuance that showed genuine understanding.",
            synonyms = "subtlety, shade", antonyms = "bluntness, crudeness",
            difficulty = 3, category = "communication",
            derived = listOf(
                DerivedWord("Nuanced", "adjective",
                    "Characterized by subtle shades of meaning or feeling",
                    "NOO-ahnst",
                    "A nuanced view of the problem avoided easy answers.",
                    "subtle, sophisticated", "simplistic, reductive", difficultyOffset = 1)
            )
        ),
        WordFamilySpec(
            word = "Eloquent", partOfSpeech = "adjective",
            definition = "Fluent or persuasive in speaking or writing; clearly expressing emotions or ideas",
            pronunciation = "EL-uh-kwuhnt",
            exampleSentence = "His eloquent tribute moved the whole room.",
            synonyms = "articulate, expressive", antonyms = "inarticulate, halting",
            difficulty = 3, category = "communication",
            derived = listOf(
                DerivedWord("Eloquence", "noun",
                    "Fluent or persuasive speaking or writing",
                    "EL-uh-kwuhns",
                    "Her eloquence came from preparation, not magic.",
                    "fluency, expressiveness", "awkwardness, dullness", difficultyOffset = 1)
            )
        ),
        WordFamilySpec(
            word = "Clarity", partOfSpeech = "noun",
            definition = "The quality of being clear, coherent, and intelligible",
            pronunciation = "KLAR-i-tee",
            exampleSentence = "Writing the letter gave her clarity about what she wanted.",
            synonyms = "lucidity, precision", antonyms = "confusion, vagueness",
            difficulty = 2, category = "communication",
            derived = listOf(
                DerivedWord("Clarify", "verb",
                    "To make a statement or situation less confused and more comprehensible",
                    "KLAR-uh-fye",
                    "Could you clarify what you mean by 'soon'?",
                    "explain, illuminate", "obscure, confuse", difficultyOffset = -1)
            )
        ),

        // ---- Emotion & character ---------------------------------------------
        WordFamilySpec(
            word = "Compassion", partOfSpeech = "noun",
            definition = "Sympathetic pity and concern for the sufferings or misfortunes of others",
            pronunciation = "kuhm-PASH-uhn",
            exampleSentence = "Compassion for her past self loosened the grip of regret.",
            synonyms = "empathy, kindness", antonyms = "indifference, cruelty",
            difficulty = 2, category = "emotion",
            derived = listOf(
                DerivedWord("Compassionate", "adjective",
                    "Feeling or showing sympathy and concern for others",
                    "kuhm-PASH-uh-nuht",
                    "A compassionate listener asks before advising.",
                    "caring, tender", "cold, callous", difficultyOffset = 1)
            )
        ),
        WordFamilySpec(
            word = "Resolve", partOfSpeech = "noun",
            definition = "Firm determination to do something",
            pronunciation = "ri-ZOLV",
            exampleSentence = "Her resolve strengthened with every honest reflection.",
            synonyms = "determination, conviction", antonyms = "indecision, weakness",
            difficulty = 3, category = "emotion",
            derived = listOf(
                DerivedWord("Resolved", "adjective",
                    "Firmly determined to do something",
                    "ri-ZOLVD",
                    "He was resolved to finish the conversation rather than avoid it.",
                    "determined, committed", "wavering, unsure", difficultyOffset = -1)
            )
        ),
        WordFamilySpec(
            word = "Fortitude", partOfSpeech = "noun",
            definition = "Courage in pain or adversity; mental and emotional strength",
            pronunciation = "FOR-tuh-tood",
            exampleSentence = "She faced the setback with quiet fortitude.",
            synonyms = "courage, endurance", antonyms = "timidity, weakness",
            difficulty = 4, category = "emotion",
            derived = listOf()
        ),
        WordFamilySpec(
            word = "Serenity", partOfSpeech = "noun",
            definition = "The state of being calm, peaceful, and untroubled",
            pronunciation = "suh-REN-i-tee",
            exampleSentence = "Morning silence brought a rare serenity.",
            synonyms = "calm, tranquility", antonyms = "agitation, turmoil",
            difficulty = 3, category = "emotion",
            derived = listOf(
                DerivedWord("Serene", "adjective",
                    "Calm, peaceful, and untroubled",
                    "suh-REEN",
                    "Her serene voice steadied the whole team.",
                    "tranquil, composed", "agitated, frantic", difficultyOffset = -1)
            )
        ),
        WordFamilySpec(
            word = "Humility", partOfSpeech = "noun",
            definition = "A modest view of one's own importance; the quality of being humble",
            pronunciation = "hyoo-MIL-i-tee",
            exampleSentence = "Humility let her ask for help before burning out.",
            synonyms = "modesty, meekness", antonyms = "arrogance, pride",
            difficulty = 3, category = "emotion",
            derived = listOf(
                DerivedWord("Humble", "adjective",
                    "Having or showing a modest or low estimate of one's own importance",
                    "HUHM-buhl",
                    "A humble attitude made feedback easier to hear.",
                    "modest, unassuming", "proud, boastful", difficultyOffset = -1)
            )
        ),
        WordFamilySpec(
            word = "Integrity", partOfSpeech = "noun",
            definition = "The quality of being honest and having strong moral principles; moral uprightness",
            pronunciation = "in-TEG-ruh-tee",
            exampleSentence = "Integrity meant telling the truth even when it cost her.",
            synonyms = "honesty, uprightness", antonyms = "dishonesty, corruption",
            difficulty = 3, category = "emotion",
            derived = listOf(
                DerivedWord("Integrous", "adjective",
                    "Having or showing strong moral principles",
                    "in-TEG-ruhs",
                    "An integrous choice rarely needs explaining later.",
                    "honest, principled", "corrupt, deceitful", difficultyOffset = 1)
            )
        ),
        WordFamilySpec(
            word = "Temperance", partOfSpeech = "noun",
            definition = "Restraint or moderation, especially in avoiding extremes of emotion or action",
            pronunciation = "TEM-per-uhns",
            exampleSentence = "Temperance kept her response measured during the argument.",
            synonyms = "moderation, restraint", antonyms = "excess, indulgence",
            difficulty = 4, category = "emotion",
            derived = listOf(
                DerivedWord("Temperate", "adjective",
                    "Showing moderation or self-restraint; mild in character",
                    "TEM-per-uht",
                    "A temperate reply de-escalated the disagreement.",
                    "moderate, balanced", "extreme, immoderate", difficultyOffset = -1)
            )
        ),

        // ---- Mindfulness ------------------------------------------------------
        WordFamilySpec(
            word = "Equanimity", partOfSpeech = "noun",
            definition = "Mental calmness and composure, especially in a difficult situation",
            pronunciation = "ee-kwuh-NIM-i-tee",
            exampleSentence = "She received both praise and criticism with equanimity.",
            synonyms = "composure, poise", antonyms = "anxiety, agitation",
            difficulty = 4, category = "mindfulness",
            derived = listOf()
        ),
        WordFamilySpec(
            word = "Contemplation", partOfSpeech = "noun",
            definition = "Deep reflective thought; the action of looking thoughtfully at something for a long time",
            pronunciation = "kon-tuhm-PLAY-shuhn",
            exampleSentence = "A short contemplation before replying changed the whole tone of the email.",
            synonyms = "reflection, meditation", antonyms = "impulse, rashness",
            difficulty = 3, category = "mindfulness",
            derived = listOf(
                DerivedWord("Contemplate", "verb",
                    "To look thoughtfully for a long time at; to think about deeply",
                    "KON-tuhm-playt",
                    "She contemplated her reasons before making the call.",
                    "ponder, consider", "ignore, dismiss", difficultyOffset = -1),
                DerivedWord("Contemplative", "adjective",
                    "Expressing or involving prolonged thought",
                    "kuhn-TEM-pluh-tiv",
                    "The contemplative walk cleared her head.",
                    "thoughtful, reflective", "thoughtless, impulsive", difficultyOffset = 1)
            )
        ),
        WordFamilySpec(
            word = "Present", partOfSpeech = "adjective",
            definition = "Existing or occurring now; fully attentive to the current moment",
            pronunciation = "PREZ-uhnt",
            exampleSentence = "Being present at dinner beat scrolling through it.",
            synonyms = "attentive, current", antonyms = "absent, distracted",
            difficulty = 1, category = "mindfulness",
            derived = listOf(
                DerivedWord("Presence", "noun",
                    "The state of being fully engaged with what is happening now",
                    "PREZ-uhns",
                    "Her calm presence steadied the meeting.",
                    "attentiveness, awareness", "absence, detachment", difficultyOffset = 1),
                DerivedWord("Presentness", "noun",
                    "The quality of being fully in the current moment",
                    "PREZ-uhnt-nuhs",
                    "Presentness is a skill you can practice at any meal.",
                    "mindfulness, attention", "distraction, daydreaming", difficultyOffset = 2)
            )
        ),
        WordFamilySpec(
            word = "Receptive", partOfSpeech = "adjective",
            definition = "Willing to consider or accept new suggestions and ideas; open and responsive",
            pronunciation = "ri-SEP-tiv",
            exampleSentence = "A receptive mindset turned criticism into a gift.",
            synonyms = "open, amenable", antonyms = "resistant, closed",
            difficulty = 3, category = "mindfulness",
            derived = listOf(
                DerivedWord("Receptiveness", "noun",
                    "Willingness to consider or accept new ideas",
                    "ri-SEP-tiv-nuhs",
                    "Receptiveness to feedback accelerated her growth.",
                    "openness, flexibility", "stubbornness, rigidity", difficultyOffset = 1)
            )
        ),
        WordFamilySpec(
            word = "Intention", partOfSpeech = "noun",
            definition = "A thing intended; an aim or plan, especially one that shapes present attention",
            pronunciation = "in-TEN-shuhn",
            exampleSentence = "Setting an intention made the morning feel chosen rather than chaotic.",
            synonyms = "aim, purpose", antonyms = "accident, chance",
            difficulty = 2, category = "mindfulness",
            derived = listOf(
                DerivedWord("Intentional", "adjective",
                    "Done on purpose; deliberate",
                    "in-TEN-shuh-nuhl",
                    "An intentional pause before eating changed her relationship with food.",
                    "deliberate, purposeful", "accidental, automatic", difficultyOffset = 1),
                DerivedWord("Intentionally", "adverb",
                    "In a deliberate or purposeful way",
                    "in-TEN-shuh-nuh-lee",
                    "He intentionally scheduled nothing after nine.",
                    "deliberately, on purpose", "unintentionally, accidentally", difficultyOffset = -1)
            )
        ),
        WordFamilySpec(
            word = "Detachment", partOfSpeech = "noun",
            definition = "The state of being objective or aloof; emotional distance that allows clear judgment",
            pronunciation = "di-TACH-muhnt",
            exampleSentence = "A little detachment let her see the argument from both sides.",
            synonyms = "objectivity, distance", antonyms = "involvement, attachment",
            difficulty = 4, category = "mindfulness",
            derived = listOf(
                DerivedWord("Detached", "adjective",
                    "Separate or disconnected; not emotionally involved",
                    "di-TACHT",
                    "He stayed detached enough to notice his own reactions.",
                    "objective, neutral", "involved, biased", difficultyOffset = -1)
            )
        ),

        // ---- Character & growth ----------------------------------------------
        WordFamilySpec(
            word = "Resilient", partOfSpeech = "adjective",
            definition = "Able to withstand or recover quickly from difficult conditions",
            pronunciation = "ri-ZIL-yuhnt",
            exampleSentence = "Resilient people treat setbacks as data, not verdicts.",
            synonyms = "tough, adaptable", antonyms = "fragile, brittle",
            difficulty = 2, category = "self-improvement",
            derived = listOf(
                DerivedWord("Resilience", "noun",
                    "The capacity to recover quickly from difficulties",
                    "ri-ZIL-yuhns",
                    "Resilience grew from many small recoveries.",
                    "toughness, buoyancy", "fragility, vulnerability", difficultyOffset = 1)
            )
        ),
        WordFamilySpec(
            word = "Courageous", partOfSpeech = "adjective",
            definition = "Not deterred by danger or pain; brave",
            pronunciation = "kuh-RAY-juhs",
            exampleSentence = "It was courageous to admit the mistake out loud.",
            synonyms = "brave, valiant", antonyms = "cowardly, timid",
            difficulty = 2, category = "emotion",
            derived = listOf(
                DerivedWord("Courage", "noun",
                    "The ability to do something that frightens one; strength in the face of pain or grief",
                    "KUR-ij",
                    "Courage is acting despite fear, not without it.",
                    "bravery, nerve", "fearfulness, timidity", difficultyOffset = -1)
            )
        ),
        WordFamilySpec(
            word = "Accountable", partOfSpeech = "adjective",
            definition = "Required or expected to justify actions or decisions; responsible",
            pronunciation = "uh-KOUNT-uh-buhl",
            exampleSentence = "She kept a simple log to stay accountable to her own goals.",
            synonyms = "responsible, answerable", antonyms = "irresponsible, exempt",
            difficulty = 3, category = "self-improvement",
            derived = listOf(
                DerivedWord("Accountability", "noun",
                    "The fact or condition of being responsible for one's actions",
                    "uh-koun-tuh-BIL-i-tee",
                    "Accountability without shame made the habit stick.",
                    "responsibility, answerability", "irresponsibility, evasion", difficultyOffset = 1)
            )
        ),
        WordFamilySpec(
            word = "Diligent", partOfSpeech = "adjective",
            definition = "Having or showing care and conscientiousness in one's work or duties",
            pronunciation = "DIL-i-juhnt",
            exampleSentence = "Diligent note-taking paid off at the end of the month.",
            synonyms = "industrious, meticulous", antonyms = "lazy, careless",
            difficulty = 3, category = "self-improvement",
            derived = listOf(
                DerivedWord("Diligence", "noun",
                    "Careful and persistent work or effort",
                    "DIL-i-juhns",
                    "Diligence, not talent, explained her progress.",
                    "industry, assiduity", "negligence, sloth", difficultyOffset = 1),
                DerivedWord("Diligently", "adverb",
                    "In a way that shows care and conscientiousness",
                    "DIL-i-juhnt-lee",
                    "He practiced diligently for twenty minutes a day.",
                    "carefully, industriously", "carelessly, lazily", difficultyOffset = -1)
            )
        ),
        WordFamilySpec(
            word = "Prudent", partOfSpeech = "adjective",
            definition = "Acting with or showing care and thought for the future",
            pronunciation = "PROO-duhnt",
            exampleSentence = "A prudent plan leaves room for the unexpected.",
            synonyms = "wise, sensible", antonyms = "reckless, imprudent",
            difficulty = 3, category = "self-improvement",
            derived = listOf(
                DerivedWord("Prudence", "noun",
                    "The quality of being careful and sensible in practical affairs",
                    "PROO-duhns",
                    "Prudence suggested a small first step rather than a leap.",
                    "wisdom, caution", "rashness, folly", difficultyOffset = 1)
            )
        ),
        WordFamilySpec(
            word = "Tenacious", partOfSpeech = "adjective",
            definition = "Tending to keep a firm hold of something; clinging or persistent",
            pronunciation = "tuh-NAY-shuhs",
            exampleSentence = "Her tenacious focus on the essay finally broke through the block.",
            synonyms = "persistent, determined", antonyms = "weak-willed, irresolute",
            difficulty = 4, category = "self-improvement",
            derived = listOf(
                DerivedWord("Tenacity", "noun",
                    "The quality or fact of being very determined; persistence",
                    "tuh-NAS-i-tee",
                    "Tenacity kept her returning to the hard problem.",
                    "determination, grit", "apathy, surrender", difficultyOffset = 1)
            )
        ),

        // ---- Creativity & learning -------------------------------------------
        WordFamilySpec(
            word = "Curious", partOfSpeech = "adjective",
            definition = "Eager to know or learn something",
            pronunciation = "KYOOR-ee-uhs",
            exampleSentence = "A curious question opened a door the lecture had missed.",
            synonyms = "inquisitive, interested", antonyms = "indifferent, apathetic",
            difficulty = 1, category = "learning",
            derived = listOf(
                DerivedWord("Curiosity", "noun",
                    "A strong desire to know or learn something",
                    "kyoor-ee-OS-i-tee",
                    "Curiosity made the long study session feel short.",
                    "inquisitiveness, wonder", "boredom, disinterest", difficultyOffset = 1)
            )
        ),
        WordFamilySpec(
            word = "Inquisitive", partOfSpeech = "adjective",
            definition = "Curious or inquiring; eager for knowledge",
            pronunciation = "in-KWIZ-i-tiv",
            exampleSentence = "Her inquisitive reading habits built a wide mental library.",
            synonyms = "curious, probing", antonyms = "incurious, uninterested",
            difficulty = 4, category = "learning",
            derived = listOf()
        ),
        WordFamilySpec(
            word = "Synthesize", partOfSpeech = "verb",
            definition = "To combine a number of things into a coherent whole",
            pronunciation = "SIN-thuh-syze",
            exampleSentence = "The essay synthesized three separate ideas into one argument.",
            synonyms = "combine, integrate", antonyms = "separate, split",
            difficulty = 4, category = "learning",
            derived = listOf(
                DerivedWord("Synthesis", "noun",
                    "The combination of components to form a connected whole",
                    "SIN-thuh-sis",
                    "Synthesis turned scattered notes into an insight.",
                    "combination, integration", "analysis, division", difficultyOffset = 1),
                DerivedWord("Synthetic", "adjective",
                    "Made by combining parts; (of a statement) not purely logical but drawing on experience",
                    "sin-THET-ik",
                    "Her synthetic method combined reading, writing, and conversation.",
                    "composite, integrative", "analytic, fragmented", difficultyOffset = 1)
            )
        ),
        WordFamilySpec(
            word = "Inquire", partOfSpeech = "verb",
            definition = "To ask for information; to investigate",
            pronunciation = "in-KWIRE",
            exampleSentence = "She inquired about the reasoning behind the rule.",
            synonyms = "ask, investigate", antonyms = "assume, ignore",
            difficulty = 2, category = "learning",
            derived = listOf(
                DerivedWord("Inquiry", "noun",
                    "An act of asking for information; an investigation",
                    "in-KWIRE-ee",
                    "A good inquiry reveals more than a quick answer.",
                    "question, investigation", "assumption, conclusion", difficultyOffset = 1)
            )
        ),
        WordFamilySpec(
            word = "Absorb", partOfSpeech = "verb",
            definition = "To take in or soak up; to engage wholly the attention of",
            pronunciation = "uhb-ZORB",
            exampleSentence = "She took a walk to absorb the chapter before discussing it.",
            synonyms = "soak up, assimilate", antonyms = "expel, reject",
            difficulty = 2, category = "learning",
            derived = listOf(
                DerivedWord("Absorbed", "adjective",
                    "Deeply engaged or engrossed",
                    "uhb-ZORBD",
                    "He was so absorbed in the problem that he missed his stop.",
                    "engrossed, immersed", "distracted, inattentive", difficultyOffset = -1),
                DerivedWord("Absorption", "noun",
                    "The process of taking something in; deep mental engagement",
                    "uhb-ZORP-shuhn",
                    "Absorption in the task made the hour feel like a minute.",
                    "immersion, engagement", "distraction, detachment", difficultyOffset = 1)
            )
        ),

        // ---- Academic & business ---------------------------------------------
        WordFamilySpec(
            word = "Pragmatic", partOfSpeech = "adjective",
            definition = "Dealing with things sensibly and realistically, based on practical considerations",
            pronunciation = "prag-MAT-ik",
            exampleSentence = "A pragmatic plan accepted the constraint and worked within it.",
            synonyms = "practical, realistic", antonyms = "idealistic, impractical",
            difficulty = 3, category = "business",
            derived = listOf(
                DerivedWord("Pragmatism", "noun",
                    "An approach that assesses truth or meaning in terms of practical consequences",
                    "PRAG-muh-tiz-uhm",
                    "Pragmatism kept the project alive through the budget cut.",
                    "practicality, realism", "idealism, theory", difficultyOffset = 1)
            )
        ),
        WordFamilySpec(
            word = "Meticulous", partOfSpeech = "adjective",
            definition = "Showing great attention to detail; very careful and precise",
            pronunciation = "muh-TIK-yuh-luhs",
            exampleSentence = "Meticulous revision transformed the rough draft.",
            synonyms = "careful, thorough", antonyms = "careless, sloppy",
            difficulty = 4, category = "business",
            derived = listOf(
                DerivedWord("Meticulously", "adverb",
                    "In a way that shows great attention to detail",
                    "muh-TIK-yuh-luhs-lee",
                    "She meticulously checked each source before citing it.",
                    "carefully, precisely", "carelessly, roughly", difficultyOffset = -1)
            )
        ),
        WordFamilySpec(
            word = "Streamline", partOfSpeech = "verb",
            definition = "To make an organization or process more efficient by simplifying or removing unnecessary steps",
            pronunciation = "STREEM-lyne",
            exampleSentence = "They streamlined the review so decisions took half the time.",
            synonyms = "simplify, rationalize", antonyms = "complicate, expand",
            difficulty = 3, category = "business",
            derived = listOf(
                DerivedWord("Streamlined", "adjective",
                    "Made more efficient by simplification",
                    "STREEM-lynd",
                    "The streamlined morning routine saved twenty minutes.",
                    "efficient, lean", "cumbersome, bloated", difficultyOffset = -1)
            )
        ),
        WordFamilySpec(
            word = "Allocate", partOfSpeech = "verb",
            definition = "To distribute resources or duties for a particular purpose",
            pronunciation = "AL-uh-kayt",
            exampleSentence = "She allocated thirty minutes each morning to deep work.",
            synonyms = "assign, apportion", antonyms = "withhold, hoard",
            difficulty = 3, category = "business",
            derived = listOf(
                DerivedWord("Allocation", "noun",
                    "The action or process of distributing resources",
                    "al-uh-KAY-shuhn",
                    "A deliberate allocation of attention changed his output.",
                    "distribution, assignment", "misuse, waste", difficultyOffset = 1)
            )
        ),
        WordFamilySpec(
            word = "Coherent", partOfSpeech = "adjective",
            definition = "Logical and consistent; able to speak clearly and logically",
            pronunciation = "koh-HEER-uhnt",
            exampleSentence = "A coherent structure made the argument easy to follow.",
            synonyms = "logical, consistent", antonyms = "incoherent, muddled",
            difficulty = 3, category = "academic",
            derived = listOf(
                DerivedWord("Coherence", "noun",
                    "The quality of being logical and consistent",
                    "koh-HEER-uhns",
                    "Coherence came from outlining before writing.",
                    "consistency, logic", "confusion, disorder", difficultyOffset = 1),
                DerivedWord("Coherently", "adverb",
                    "In a logical and consistent manner",
                    "koh-HEER-uhnt-lee",
                    "He explained the failure coherently and without blame.",
                    "clearly, logically", "incoherently, confusingly", difficultyOffset = -1)
            )
        ),
        WordFamilySpec(
            word = "Empirical", partOfSpeech = "adjective",
            definition = "Based on, concerned with, or verifiable by observation or experience rather than theory",
            pronunciation = "em-PIR-i-kuhl",
            exampleSentence = "Empirical evidence from her own log showed the habit worked.",
            synonyms = "observational, factual", antonyms = "theoretical, speculative",
            difficulty = 4, category = "academic",
            derived = listOf(
                DerivedWord("Empirically", "adverb",
                    "By means of observation or experiment",
                    "em-PIR-ik-lee",
                    "He tested the technique empirically for two weeks.",
                    "experimentally, observably", "theoretically, speculatively", difficultyOffset = -1)
            )
        ),
        WordFamilySpec(
            word = "Proficiency", partOfSpeech = "noun",
            definition = "A high degree of competence or skill in something",
            pronunciation = "pruh-FISH-uhn-see",
            exampleSentence = "Proficiency came from deliberate, repeated practice.",
            synonyms = "skill, mastery", antonyms = "incompetence, clumsiness",
            difficulty = 3, category = "learning",
            derived = listOf(
                DerivedWord("Proficient", "adjective",
                    "Competent or skilled in doing or using something",
                    "pruh-FISH-uhnt",
                    "She became proficient at reading before breakfast.",
                    "skilled, adept", "unskilled, inept", difficultyOffset = -1)
            )
        ),

        // ---- Vocabulary of reflection ----------------------------------------
        WordFamilySpec(
            word = "Retrospect", partOfSpeech = "noun",
            definition = "A survey or review of a past course of events or period of time",
            pronunciation = "RE-truh-spekt",
            exampleSentence = "In retrospect, the detour was the best part of the trip.",
            synonyms = "hindsight, review", antonyms = "prospect, foresight",
            difficulty = 3, category = "reflection",
            derived = listOf(
                DerivedWord("Retrospective", "adjective",
                    "Looking back on or dealing with past events or situations",
                    "re-truh-SPEK-tiv",
                    "A retrospective glance showed how far she had come.",
                    "backward-looking, reflective", "forward-looking, prospective", difficultyOffset = 1)
            )
        ),
        WordFamilySpec(
            word = "Introspection", partOfSpeech = "noun",
            definition = "The examination of one's own conscious thoughts and feelings",
            pronunciation = "in-truh-SPEK-shuhn",
            exampleSentence = "Brief introspection before answering kept the apology honest.",
            synonyms = "self-examination, reflection", antonyms = "extrospection, oblivion",
            difficulty = 4, category = "reflection",
            derived = listOf(
                DerivedWord("Introspective", "adjective",
                    "Characterized by or given to examining one's own thoughts and feelings",
                    "in-truh-SPEK-tiv",
                    "The introspective entry noted her own contradictions kindly.",
                    "self-reflective, thoughtful", "unreflective, shallow", difficultyOffset = 1)
            )
        ),
        WordFamilySpec(
            word = "Discern", partOfSpeech = "verb",
            definition = "To perceive or recognize something; to distinguish with difficulty",
            pronunciation = "di-SURN",
            exampleSentence = "She learned to discern useful feedback from noise.",
            synonyms = "perceive, distinguish", antonyms = "confuse, overlook",
            difficulty = 4, category = "reflection",
            derived = listOf(
                DerivedWord("Discernment", "noun",
                    "The ability to judge well; keen insight",
                    "di-SURN-muhnt",
                    "Discernment helped her choose what deserved her attention.",
                    "judgment, insight", "obtuseness, blindness", difficultyOffset = 1)
            )
        ),
        WordFamilySpec(
            word = "Distill", partOfSpeech = "verb",
            definition = "To extract the essential meaning or nature of something",
            pronunciation = "di-STIL",
            exampleSentence = "A long conversation can be distilled into one sentence.",
            synonyms = "extract, condense", antonyms = "dilute, expand",
            difficulty = 4, category = "reflection",
            derived = listOf(
                DerivedWord("Distillation", "noun",
                    "The extraction of the essential meaning or nature of something",
                    "dis-tuh-LAY-shuhn",
                    "The evening's distillation: 'I need fewer inputs, not more.'",
                    "essence, condensation", "dilution, sprawl", difficultyOffset = 1)
            )
        )
    )

    /** Flattened rows for seeding and expansion (root + derived members). */
    val allWords: List<com.kairos.app.data.local.entity.VocabularyEntity> by lazy {
        WordFamilyExpander.expand(specs)
    }
}
