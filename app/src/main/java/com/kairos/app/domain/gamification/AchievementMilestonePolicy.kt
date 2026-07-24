package com.kairos.app.domain.gamification

/**
 * Canonical, testable milestone map for count-based Kairos achievements.
 *
 * Keeping IDs and thresholds in one pure Kotlin policy prevents the database,
 * reward service, and achievement UI from silently drifting apart.
 */
object AchievementMilestonePolicy {
    data class Milestone(val achievementId: String, val requirement: Int)

    enum class Evidence {
        JOURNAL_ENTRY,
        WORD_LEARNED,
        QUOTE_REFLECTION,
        FUTURE_LETTER_SENT,
        FUTURE_LETTER_RECEIVED,
        GUIDE_CONVERSATION,
        ACTIVE_DAY
    }

    private val byEvidence: Map<Evidence, List<Milestone>> = mapOf(
        Evidence.JOURNAL_ENTRY to milestones(
            "first_journal" to 1,
            "journal_5" to 5,
            "journal_10" to 10,
            "journal_25" to 25,
            "journal_30" to 30,
            "journal_50" to 50,
            "journal_100" to 100,
            "journal_365" to 365
        ),
        Evidence.WORD_LEARNED to milestones(
            "first_word" to 1,
            "word_collector_10" to 10,
            "word_collector_25" to 25,
            "word_collector_50" to 50,
            "word_collector_100" to 100,
            "word_collector_250" to 250,
            "word_collector_500" to 500,
            "word_collector_1000" to 1000
        ),
        Evidence.QUOTE_REFLECTION to milestones(
            "quote_reader_10" to 10,
            "quote_devotee" to 50,
            "quote_collector_100" to 100
        ),
        Evidence.FUTURE_LETTER_SENT to milestones(
            "letter_first" to 1,
            "letter_3" to 3,
            "letter_5" to 5,
            "letter_10" to 10
        ),
        Evidence.FUTURE_LETTER_RECEIVED to milestones(
            "letter_received" to 1,
            "letter_received_5" to 5
        ),
        Evidence.GUIDE_CONVERSATION to milestones(
            "buddha_first" to 1,
            "buddha_5" to 5,
            "buddha_10" to 10,
            "buddha_25" to 25,
            "buddha_50" to 50,
            "buddha_100" to 100,
            "buddha_250" to 250
        ),
        Evidence.ACTIVE_DAY to milestones(
            "streak_3" to 3,
            "streak_7" to 7,
            "streak_14" to 14,
            "streak_21" to 21,
            "streak_30" to 30,
            "streak_60" to 60,
            "streak_90" to 90,
            "streak_180" to 180,
            "streak_365" to 365
        )
    )

    fun milestonesFor(evidence: Evidence): List<Milestone> = byEvidence.getValue(evidence)

    fun reached(evidence: Evidence, currentValue: Int): List<Milestone> =
        milestonesFor(evidence).filter { currentValue >= it.requirement }

    fun next(evidence: Evidence, currentValue: Int): Milestone? =
        milestonesFor(evidence).firstOrNull { currentValue < it.requirement }

    fun validate(): List<String> = buildList {
        byEvidence.forEach { (evidence, milestones) ->
            if (milestones.isEmpty()) add("$evidence has no milestones")
            val duplicateIds = milestones.groupingBy { it.achievementId }
                .eachCount().filterValues { it > 1 }.keys
            duplicateIds.forEach { add("$evidence repeats achievement id $it") }
            milestones.zipWithNext().forEach { (left, right) ->
                if (right.requirement <= left.requirement) {
                    add("$evidence requirements are not strictly increasing at ${right.achievementId}")
                }
            }
            milestones.filter { it.requirement <= 0 }
                .forEach { add("${it.achievementId} has a non-positive requirement") }
        }
    }

    private fun milestones(vararg pairs: Pair<String, Int>): List<Milestone> =
        pairs.map { (id, requirement) -> Milestone(id, requirement) }
}
