package com.prody.prashant.domain.vocabulary

import com.prody.prashant.data.local.entity.VocabularyEntity
import java.time.Instant
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of VocabularyDetector that provides smart word detection.
 *
 * Features:
 * - Case-insensitive matching by default
 * - Handles different word forms (plurals, verb conjugations, etc.)
 * - Ensures word boundary matching (no partial matches)
 * - Extracts complete sentences for context
 */
@Singleton
class VocabularyDetectorImpl @Inject constructor(
    private val config: DetectionConfig = DetectionConfig()
) : VocabularyDetector {

    override fun detectLearnedWords(
        content: String,
        learnedWords: List<VocabularyEntity>
    ): List<WordUsage> {
        if (content.isBlank() || learnedWords.isEmpty()) {
            return emptyList()
        }

        val usages = mutableListOf<WordUsage>()
        val processedContent = if (config.caseSensitive) content else content.lowercase(Locale.getDefault())

        for (word in learnedWords) {
            if (word.word.length < config.minWordLength) continue

            val baseWord = if (config.caseSensitive) word.word else word.word.lowercase(Locale.getDefault())
            val variations = if (config.matchWordForms) {
                generateWordForms(baseWord)
            } else {
                listOf(baseWord)
            }

            // Find all occurrences of this word and its variations
            for (variation in variations) {
                val regex = createWordBoundaryRegex(variation)
                val matches = regex.findAll(processedContent)

                for (match in matches) {
                    val position = match.range
                    val matchedForm = content.substring(position.first, position.last + 1)
                    val sentence = extractSentence(content, position)

                    // Check if we already detected this exact position
                    val alreadyDetected = usages.any { it.position == position }
                    if (!alreadyDetected) {
                        usages.add(
                            WordUsage(
                                word = word,
                                usedIn = sentence.trim(),
                                position = position,
                                detectedAt = Instant.now(),
                                matchedForm = matchedForm
                            )
                        )
                    }
                }
            }
        }

        return usages.sortedBy { it.position.first }
    }

    override fun extractSentence(content: String, position: IntRange): String {
        if (content.isEmpty() || position.first >= content.length) {
            return ""
        }

        // Sentence delimiters
        val sentenceEnders = setOf('.', '!', '?', '\n')

        // Find the start of the sentence (after the previous sentence ender)
        var start = position.first
        while (start > 0) {
            if (sentenceEnders.contains(content[start - 1])) {
                break
            }
            start--
        }

        // Find the end of the sentence (at the next sentence ender)
        var end = position.last
        while (end < content.length - 1) {
            if (sentenceEnders.contains(content[end])) {
                end++ // Include the punctuation
                break
            }
            end++
        }

        // Handle case where we're at the end without punctuation
        if (end >= content.length - 1) {
            end = content.length
        }

        return content.substring(start, end).trim()
    }

    /**
     * Creates a regex pattern that matches the word with proper word boundaries.
     */
    private fun createWordBoundaryRegex(word: String): Regex {
        val escapedWord = Regex.escape(word)
        // \b matches word boundaries (between word and non-word characters)
        return Regex("\\b$escapedWord\\b", RegexOption.IGNORE_CASE)
    }

    /**
     * Generates common word forms for better matching.
     * Handles:
     * - Plurals (add 's', 'es')
     * - Verb forms (add 'ing', 'ed', 's')
     * - Comparative/superlative (add 'er', 'est')
     */
    private fun generateWordForms(baseWord: String): List<String> {
        val forms = mutableListOf(baseWord)

        // Skip very short words to avoid false matches
        if (baseWord.length <= 2) return forms

        // Plurals and verb conjugations
        forms.add("${baseWord}s")
        forms.add("${baseWord}es")
        forms.add("${baseWord}ing")
        forms.add("${baseWord}ed")
        forms.add("${baseWord}er")
        forms.add("${baseWord}est")

        // Handle words ending in 'e' (e.g., write -> writing)
        if (baseWord.endsWith('e')) {
            val stem = baseWord.dropLast(1)
            forms.add("${stem}ing")
            forms.add("${stem}ed")
            forms.add("${stem}er")
        }

        // Handle words ending in 'y' (e.g., happy -> happier, study -> studied)
        if (baseWord.endsWith('y') && baseWord.length > 2) {
            val stem = baseWord.dropLast(1)
            forms.add("${stem}ies") // study -> studies
            forms.add("${stem}ied") // study -> studied
            forms.add("${stem}ier") // happy -> happier
            forms.add("${stem}iest") // happy -> happiest
        }

        // Handle consonant doubling (e.g., run -> running, big -> bigger)
        if (baseWord.length >= 3) {
            val lastChar = baseWord.last()
            val secondLastChar = baseWord[baseWord.length - 2]
            if (isConsonant(lastChar) && isVowel(secondLastChar)) {
                forms.add("${baseWord}${lastChar}ing") // run -> running
                forms.add("${baseWord}${lastChar}ed") // plan -> planned
                forms.add("${baseWord}${lastChar}er") // big -> bigger
                forms.add("${baseWord}${lastChar}est") // big -> biggest
            }
        }

        // Handle irregular plurals and forms (basic set)
        when (baseWord) {
            "be" -> forms.addAll(listOf("am", "is", "are", "was", "were", "been", "being"))
            "have" -> forms.addAll(listOf("has", "had", "having"))
            "do" -> forms.addAll(listOf("does", "did", "done", "doing"))
            "go" -> forms.addAll(listOf("goes", "went", "gone", "going"))
            "take" -> forms.addAll(listOf("takes", "took", "taken", "taking"))
            "make" -> forms.addAll(listOf("makes", "made", "making"))
            "think" -> forms.addAll(listOf("thinks", "thought", "thinking"))
            "see" -> forms.addAll(listOf("sees", "saw", "seen", "seeing"))
            "come" -> forms.addAll(listOf("comes", "came", "coming"))
            "get" -> forms.addAll(listOf("gets", "got", "gotten", "getting"))
            "say" -> forms.addAll(listOf("says", "said", "saying"))
            "give" -> forms.addAll(listOf("gives", "gave", "given", "giving"))
            "find" -> forms.addAll(listOf("finds", "found", "finding"))
            "tell" -> forms.addAll(listOf("tells", "told", "telling"))
            "become" -> forms.addAll(listOf("becomes", "became", "becoming"))
            "leave" -> forms.addAll(listOf("leaves", "left", "leaving"))
            "feel" -> forms.addAll(listOf("feels", "felt", "feeling"))
            "bring" -> forms.addAll(listOf("brings", "brought", "bringing"))
            "begin" -> forms.addAll(listOf("begins", "began", "begun", "beginning"))
            "keep" -> forms.addAll(listOf("keeps", "kept", "keeping"))
            "hold" -> forms.addAll(listOf("holds", "held", "holding"))
            "write" -> forms.addAll(listOf("writes", "wrote", "written", "writing"))
            "stand" -> forms.addAll(listOf("stands", "stood", "standing"))
            "hear" -> forms.addAll(listOf("hears", "heard", "hearing"))
            "let" -> forms.addAll(listOf("lets", "letting"))
            "mean" -> forms.addAll(listOf("means", "meant", "meaning"))
            "set" -> forms.addAll(listOf("sets", "setting"))
            "meet" -> forms.addAll(listOf("meets", "met", "meeting"))
            "run" -> forms.addAll(listOf("runs", "ran", "running"))
            "pay" -> forms.addAll(listOf("pays", "paid", "paying"))
            "sit" -> forms.addAll(listOf("sits", "sat", "sitting"))
            "speak" -> forms.addAll(listOf("speaks", "spoke", "spoken", "speaking"))
            "lie" -> forms.addAll(listOf("lies", "lay", "lain", "lying"))
            "lead" -> forms.addAll(listOf("leads", "led", "leading"))
            "read" -> forms.addAll(listOf("reads", "reading"))
            "grow" -> forms.addAll(listOf("grows", "grew", "grown", "growing"))
            "lose" -> forms.addAll(listOf("loses", "lost", "losing"))
            "fall" -> forms.addAll(listOf("falls", "fell", "fallen", "falling"))
            "send" -> forms.addAll(listOf("sends", "sent", "sending"))
            "build" -> forms.addAll(listOf("builds", "built", "building"))
            "understand" -> forms.addAll(listOf("understands", "understood", "understanding"))
            "draw" -> forms.addAll(listOf("draws", "drew", "drawn", "drawing"))
            "break" -> forms.addAll(listOf("breaks", "broke", "broken", "breaking"))
            "spend" -> forms.addAll(listOf("spends", "spent", "spending"))
            "cut" -> forms.addAll(listOf("cuts", "cutting"))
            "rise" -> forms.addAll(listOf("rises", "rose", "risen", "rising"))
            "drive" -> forms.addAll(listOf("drives", "drove", "driven", "driving"))
            "buy" -> forms.addAll(listOf("buys", "bought", "buying"))
            "wear" -> forms.addAll(listOf("wears", "wore", "worn", "wearing"))
            "choose" -> forms.addAll(listOf("chooses", "chose", "chosen", "choosing"))
            "seek" -> forms.addAll(listOf("seeks", "sought", "seeking"))
            "throw" -> forms.addAll(listOf("throws", "threw", "thrown", "throwing"))
            "catch" -> forms.addAll(listOf("catches", "caught", "catching"))
            "deal" -> forms.addAll(listOf("deals", "dealt", "dealing"))
            "win" -> forms.addAll(listOf("wins", "won", "winning"))
            "forget" -> forms.addAll(listOf("forgets", "forgot", "forgotten", "forgetting"))
            "sing" -> forms.addAll(listOf("sings", "sang", "sung", "singing"))
            "hang" -> forms.addAll(listOf("hangs", "hung", "hanging"))
            "strike" -> forms.addAll(listOf("strikes", "struck", "striking"))
            "swim" -> forms.addAll(listOf("swims", "swam", "swum", "swimming"))
            "teach" -> forms.addAll(listOf("teaches", "taught", "teaching"))
        }

        return forms.distinct()
    }

    private fun isVowel(char: Char): Boolean {
        return char.lowercaseChar() in setOf('a', 'e', 'i', 'o', 'u')
    }

    private fun isConsonant(char: Char): Boolean {
        return char.isLetter() && !isVowel(char)
    }
}
