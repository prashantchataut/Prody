package com.prody.prashant.data.content

import java.util.Calendar

/**
 * Static vocabulary words for widget display when database is not accessible.
 * These are curated growth-oriented words that align with Prody's philosophy.
 */
object VocabularyContent {

    data class Word(
        val word: String,
        val partOfSpeech: String,
        val definition: String,
        val pronunciation: String = "",
        val example: String = ""
    )

    val allWords: List<Word> = listOf(
        Word(
            word = "Resilience",
            partOfSpeech = "noun",
            definition = "The capacity to recover quickly from difficulties; mental toughness",
            pronunciation = "ri-ZIL-yuhns",
            example = "Her resilience helped her bounce back after every setback."
        ),
        Word(
            word = "Serendipity",
            partOfSpeech = "noun",
            definition = "The occurrence of events by chance in a happy or beneficial way",
            pronunciation = "ser-uhn-DIP-i-tee",
            example = "Finding that book was pure serendipity."
        ),
        Word(
            word = "Perseverance",
            partOfSpeech = "noun",
            definition = "Persistence in doing something despite difficulty or delay in achieving success",
            pronunciation = "pur-suh-VEER-uhns",
            example = "His perseverance through failures eventually led to success."
        ),
        Word(
            word = "Equanimity",
            partOfSpeech = "noun",
            definition = "Mental calmness and composure, especially in a difficult situation",
            pronunciation = "ee-kwuh-NIM-i-tee",
            example = "She faced the challenge with remarkable equanimity."
        ),
        Word(
            word = "Tenacity",
            partOfSpeech = "noun",
            definition = "The quality of being very determined; persistence",
            pronunciation = "tuh-NAS-i-tee",
            example = "Her tenacity in pursuing her goals was inspiring."
        ),
        Word(
            word = "Introspection",
            partOfSpeech = "noun",
            definition = "The examination of one's own conscious thoughts and feelings",
            pronunciation = "in-truh-SPEK-shuhn",
            example = "Daily introspection helps you understand yourself better."
        ),
        Word(
            word = "Gratitude",
            partOfSpeech = "noun",
            definition = "The quality of being thankful; readiness to show appreciation",
            pronunciation = "GRAT-i-tood",
            example = "Practicing gratitude improves mental well-being."
        ),
        Word(
            word = "Mindfulness",
            partOfSpeech = "noun",
            definition = "A mental state achieved by focusing awareness on the present moment",
            pronunciation = "MYND-fuhl-nis",
            example = "Mindfulness meditation reduced her anxiety significantly."
        ),
        Word(
            word = "Authenticity",
            partOfSpeech = "noun",
            definition = "The quality of being genuine and true to one's own personality and values",
            pronunciation = "aw-then-TIS-i-tee",
            example = "His authenticity made people trust him immediately."
        ),
        Word(
            word = "Stoic",
            partOfSpeech = "adjective",
            definition = "Enduring pain and hardship without showing feelings or complaining",
            pronunciation = "STOH-ik",
            example = "He remained stoic despite the bad news."
        ),
        Word(
            word = "Ephemeral",
            partOfSpeech = "adjective",
            definition = "Lasting for a very short time; transient",
            pronunciation = "ih-FEM-er-uhl",
            example = "The ephemeral beauty of cherry blossoms reminds us to appreciate the moment."
        ),
        Word(
            word = "Sagacious",
            partOfSpeech = "adjective",
            definition = "Having keen mental discernment and good judgment; shrewd",
            pronunciation = "suh-GAY-shuhs",
            example = "Her sagacious advice helped guide the company through crisis."
        ),
        Word(
            word = "Eudaimonia",
            partOfSpeech = "noun",
            definition = "A Greek term for human flourishing or living well",
            pronunciation = "yoo-dye-MOH-nee-uh",
            example = "The Stoics believed eudaimonia comes from virtue, not pleasure."
        ),
        Word(
            word = "Perspicacious",
            partOfSpeech = "adjective",
            definition = "Having a ready insight into and understanding of things",
            pronunciation = "pur-spi-KAY-shuhs",
            example = "His perspicacious observations revealed the truth."
        ),
        Word(
            word = "Fortitude",
            partOfSpeech = "noun",
            definition = "Courage in pain or adversity; mental and emotional strength",
            pronunciation = "FOR-ti-tood",
            example = "She showed great fortitude during her recovery."
        ),
        Word(
            word = "Transcendence",
            partOfSpeech = "noun",
            definition = "Existence or experience beyond the normal or physical level",
            pronunciation = "tran-SEN-duhns",
            example = "Meditation can lead to moments of transcendence."
        ),
        Word(
            word = "Pragmatic",
            partOfSpeech = "adjective",
            definition = "Dealing with things sensibly and realistically",
            pronunciation = "prag-MAT-ik",
            example = "She took a pragmatic approach to solving the problem."
        ),
        Word(
            word = "Contentment",
            partOfSpeech = "noun",
            definition = "A state of happiness and satisfaction",
            pronunciation = "kuhn-TENT-muhnt",
            example = "True contentment comes from within, not from possessions."
        ),
        Word(
            word = "Efficacious",
            partOfSpeech = "adjective",
            definition = "Successful in producing a desired or intended result; effective",
            pronunciation = "ef-i-KAY-shuhs",
            example = "The new treatment proved highly efficacious."
        ),
        Word(
            word = "Metamorphosis",
            partOfSpeech = "noun",
            definition = "A change of form or nature into a completely different one",
            pronunciation = "met-uh-MOR-fuh-sis",
            example = "His personal metamorphosis inspired everyone around him."
        ),
        Word(
            word = "Aplomb",
            partOfSpeech = "noun",
            definition = "Self-confidence or assurance, especially in a demanding situation",
            pronunciation = "uh-PLOM",
            example = "She handled the crisis with remarkable aplomb."
        ),
        Word(
            word = "Catalyst",
            partOfSpeech = "noun",
            definition = "A person or thing that precipitates an event or change",
            pronunciation = "KAT-uh-list",
            example = "The book was a catalyst for her personal transformation."
        ),
        Word(
            word = "Indomitable",
            partOfSpeech = "adjective",
            definition = "Impossible to subdue or defeat; unconquerable",
            pronunciation = "in-DOM-i-tuh-buhl",
            example = "Her indomitable spirit kept her going through hardship."
        ),
        Word(
            word = "Luminous",
            partOfSpeech = "adjective",
            definition = "Full of or shedding light; bright or shining",
            pronunciation = "LOO-mi-nuhs",
            example = "She had a luminous presence that lit up every room."
        ),
        Word(
            word = "Quintessential",
            partOfSpeech = "adjective",
            definition = "Representing the most perfect example of a quality or class",
            pronunciation = "kwin-tuh-SEN-shuhl",
            example = "She is the quintessential leader we all aspire to be."
        ),
        Word(
            word = "Ineffable",
            partOfSpeech = "adjective",
            definition = "Too great or extreme to be expressed or described in words",
            pronunciation = "in-EF-uh-buhl",
            example = "The moment brought her ineffable joy."
        ),
        Word(
            word = "Diligent",
            partOfSpeech = "adjective",
            definition = "Having or showing care and conscientiousness in one's work",
            pronunciation = "DIL-i-juhnt",
            example = "Diligent practice is the key to mastery."
        ),
        Word(
            word = "Temperance",
            partOfSpeech = "noun",
            definition = "Moderation and self-restraint, especially in eating and drinking",
            pronunciation = "TEM-per-uhns",
            example = "The Stoics valued temperance as a cardinal virtue."
        ),
        Word(
            word = "Prudence",
            partOfSpeech = "noun",
            definition = "The quality of acting with care and thought for the future",
            pronunciation = "PROO-duhns",
            example = "Prudence in decision-making saves you from regret."
        ),
        Word(
            word = "Benevolent",
            partOfSpeech = "adjective",
            definition = "Well meaning and kindly; showing goodwill",
            pronunciation = "buh-NEV-uh-luhnt",
            example = "His benevolent nature made him beloved by all."
        )
    )

    /**
     * Get the word of the day based on day of year for consistency.
     */
    fun getWordOfTheDay(): Word {
        if (allWords.isEmpty()) return Word(
            word = "Resilience",
            partOfSpeech = "noun",
            definition = "The capacity to recover quickly from difficulties"
        )
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        return allWords[dayOfYear % allWords.size]
    }

    /**
     * Get a random word.
     */
    fun getRandomWord(): Word = allWords.random()
}
