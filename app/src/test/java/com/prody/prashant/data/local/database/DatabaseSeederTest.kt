package com.prody.prashant.data.local.database

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DatabaseSeederTest {

    @Test
    fun `parses quote payload and enforces schema`() {
        val quotes = DatabaseSeeder.parseQuotes("""{"version":1,"items":[{"content":"c","author":"a","category":"g","tags":"t","reflectionPrompt":"r"}]}""")
        assertEquals(1, quotes.size)

        assertFailsWith<IllegalStateException> {
            DatabaseSeeder.parseQuotes("""{"version":1,"items":[{"content":"c"}]}""")
        }
    }

    @Test
    fun `integrity validation checks uniqueness and foreign consistency`() {
        val quotes = DatabaseSeeder.parseQuotes("""{"version":1,"items":[{"content":"Q1","author":"A","category":"g","tags":"t","reflectionPrompt":"r"}]}""")
        val vocabulary = DatabaseSeeder.parseVocabulary("""{"version":1,"items":[{"word":"clarity","definition":"d","pronunciation":"p","partOfSpeech":"noun","exampleSentence":"e","synonyms":"s","antonyms":"a","difficulty":1,"category":"mindset"}]}""")
        val idioms = DatabaseSeeder.parseIdioms("""{"version":1,"items":[{"phrase":"break the ice","meaning":"m","origin":"o","exampleSentence":"e","category":"social"}]}""")
        val achievements = DatabaseSeeder.parseAchievements("""{"version":1,"items":[{"id":"a1","name":"n","description":"d","iconId":"i","category":"c","requirement":1,"rewardType":"points","rewardValue":"100","rarity":"common","celebrationMessage":"msg","xpReward":50}]}""")
        val leaderboard = DatabaseSeeder.parseLeaderboard("""{"version":1,"items":[{"odId":"local","displayName":"me","totalPoints":1,"weeklyPoints":1,"currentStreak":1,"rank":1,"previousRank":1,"isCurrentUser":true}]}""")
        val user = DatabaseSeeder.parseUserSeed("""{"version":1,"profile":{"odUserId":"local","displayName":"me","titleId":"seeker"},"stats":{"userId":"local"}}""")
        val missions = DatabaseSeeder.parseMissions("""{"version":1,"date":1735689600000,"items":[{"missionId":"m1","missionType":"reflect","title":"t","description":"d","targetValue":1,"rewardXp":1,"rewardTokens":1}]}""")

        DatabaseSeeder.validateIntegrity(quotes, vocabulary, idioms, achievements, leaderboard, user, missions)

        val mismatchedUser = DatabaseSeeder.parseUserSeed("""{"version":1,"profile":{"odUserId":"different","displayName":"me","titleId":"seeker"},"stats":{"userId":"local"}}""")
        assertFailsWith<IllegalArgumentException> {
            DatabaseSeeder.validateIntegrity(quotes, vocabulary, idioms, achievements, leaderboard, mismatchedUser, missions)
        }
    }
}
