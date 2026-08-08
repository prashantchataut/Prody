package com.kairos.app.domain.vocabulary

import com.kairos.app.data.local.entity.VocabularyEntity

/**
 * Deterministic expansion of curated word-family specs into full catalog rows.
 *
 * A vocabulary app that relies on a fixed list runs out of fresh material in a few
 * weeks. This expander multiplies each curated root word into its genuine derived
 * family (noun / verb / adjective / adverb forms) so the catalog grows several times
 * over — using only real, correct English words. Derived rows are marked with
 * `origin = "kairos:family:<root>"` so they can be filtered or debugged later.
 *
 * The expansion is pure and stable: the same spec always yields the same rows, which
 * keeps the daily recommendation plan deterministic and explainable.
 */

/** A genuine derived form of a root word, ready to become its own catalog row. */
data class DerivedWord(
    val word: String,
    val partOfSpeech: String,
    val definition: String,
    val pronunciation: String = "",
    val exampleSentence: String = "",
    val synonyms: String = "",
    val antonyms: String = "",
    /** Difficulty is relative to the root; the expander clamps to the 1..5 scale. */
    val difficultyOffset: Int = 0
)

/**
 * A curated root word plus its verified family. Definitions must be real and
 * unambiguous; this is teaching material, not filler.
 */
data class WordFamilySpec(
    val word: String,
    val partOfSpeech: String,
    val definition: String,
    val pronunciation: String = "",
    val exampleSentence: String = "",
    val synonyms: String = "",
    val antonyms: String = "",
    val origin: String = "",
    val difficulty: Int = 2,
    val category: String = "general",
    val derived: List<DerivedWord> = emptyList()
) {

    fun toRootEntity(): VocabularyEntity = VocabularyEntity(
        word = word,
        definition = definition,
        pronunciation = pronunciation,
        partOfSpeech = partOfSpeech,
        exampleSentence = exampleSentence,
        synonyms = synonyms,
        antonyms = antonyms,
        origin = origin,
        difficulty = difficulty.coerceIn(WordFamilyExpander.MIN_DIFFICULTY, WordFamilyExpander.MAX_DIFFICULTY),
        category = category
    )

    /** The root row followed by one row per derived family member. */
    fun expand(): List<VocabularyEntity> {
        val root = toRootEntity()
        if (derived.isEmpty()) return listOf(root)
        return buildList {
            add(root)
            derived.forEach { member ->
                add(
                    VocabularyEntity(
                        word = member.word,
                        definition = member.definition,
                        pronunciation = member.pronunciation,
                        partOfSpeech = member.partOfSpeech,
                        exampleSentence = member.exampleSentence,
                        synonyms = member.synonyms,
                        antonyms = member.antonyms,
                        origin = "kairos:family:$word",
                        difficulty = (difficulty + member.difficultyOffset).coerceIn(WordFamilyExpander.MIN_DIFFICULTY, WordFamilyExpander.MAX_DIFFICULTY),
                        category = category
                    )
                )
            }
        }
    }
}

object WordFamilyExpander {

    const val MIN_DIFFICULTY = 1
    const val MAX_DIFFICULTY = 5
    const val FAMILY_ORIGIN_PREFIX = "kairos:family:"

    /** Expand many specs into a flat list of catalog rows. */
    fun expand(specs: List<WordFamilySpec>): List<VocabularyEntity> =
        specs.flatMap { it.expand() }

    /** True when a row was produced by the family expander rather than curated directly. */
    fun isDerived(entity: VocabularyEntity): Boolean =
        entity.origin.startsWith(FAMILY_ORIGIN_PREFIX)

    /** The root word that a derived row expands, or null when the row is curated. */
    fun rootWordOf(entity: VocabularyEntity): String? =
        if (isDerived(entity)) entity.origin.removePrefix(FAMILY_ORIGIN_PREFIX) else null
}
