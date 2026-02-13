package com.prody.prashant.data.local.database

import android.content.Context
import android.util.Log
import com.prody.prashant.data.local.entity.AchievementEntity
import com.prody.prashant.data.local.entity.DailyMissionEntity
import com.prody.prashant.data.local.entity.IdiomEntity
import com.prody.prashant.data.local.entity.LeaderboardEntryEntity
import com.prody.prashant.data.local.entity.PhraseEntity
import com.prody.prashant.data.local.entity.ProverbEntity
import com.prody.prashant.data.local.entity.QuoteEntity
import com.prody.prashant.data.local.entity.SeedVersionEntity
import com.prody.prashant.data.local.entity.UserProfileEntity
import com.prody.prashant.data.local.entity.UserStatsEntity
import com.prody.prashant.data.local.entity.VocabularyEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import java.security.MessageDigest

object DatabaseSeeder {

    private const val TAG = "DatabaseSeeder"
    private const val BATCH_SIZE = 25
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val json = Json { ignoreUnknownKeys = false }

    fun seedDatabase(context: Context, database: ProdyDatabase) {
        scope.launch {
            try {
                val tasks = seedTasks(context, database)
                tasks.forEach { task -> runSeedTask(task, database) }
                Log.d(TAG, "Database seeding complete")
            } catch (e: Exception) {
                Log.e(TAG, "Error seeding database", e)
            }
        }
    }

    private suspend fun runSeedTask(task: SeedTask, database: ProdyDatabase) {
        val checksum = checksum(task.payload)
        val seedVersionDao = database.seedVersionDao()
        val existing = seedVersionDao.getSeedVersion(task.domain, task.version)
        if (existing?.checksum == checksum) {
            Log.d(TAG, "Skipping ${task.domain} v${task.version}; checksum unchanged")
            return
        }

        task.apply(database)
        seedVersionDao.upsertSeedVersion(
            SeedVersionEntity(domain = task.domain, version = task.version, checksum = checksum)
        )
        Log.d(TAG, "Applied ${task.domain} v${task.version}")
    }

    private fun seedTasks(context: Context, database: ProdyDatabase): List<SeedTask> {
        val quotePayload = loadPayload(context, "seeds/quotes.v1.json")
        val proverbPayload = loadPayload(context, "seeds/proverbs.v1.json")
        val idiomPayload = loadPayload(context, "seeds/idioms.v1.json")
        val phrasePayload = loadPayload(context, "seeds/phrases.v1.json")
        val vocabularyPayload = loadPayload(context, "seeds/vocabulary.v1.json")
        val leaderboardPayload = loadPayload(context, "seeds/leaderboard.v1.json")
        val userPayload = loadPayload(context, "seeds/user.v1.json")
        val achievementPayload = loadPayload(context, "seeds/achievements.v1.json")
        val missionPayload = loadPayload(context, "seeds/missions.v1.json")

        val quotes = parseQuotes(quotePayload)
        val proverbs = parseProverbs(proverbPayload)
        val idioms = parseIdioms(idiomPayload)
        val phrases = parsePhrases(phrasePayload)
        val vocabulary = parseVocabulary(vocabularyPayload)
        val leaderboard = parseLeaderboard(leaderboardPayload)
        val user = parseUserSeed(userPayload)
        val achievements = parseAchievements(achievementPayload)
        val missions = parseMissions(missionPayload)

        validateIntegrity(quotes, vocabulary, idioms, achievements, leaderboard, user, missions)

        return listOf(
            SeedTask("quotes", 1, quotePayload) { db -> batchInsert(quotes) { db.quoteDao().insertQuotes(it) } },
            SeedTask("proverbs", 1, proverbPayload) { db -> batchInsert(proverbs) { db.proverbDao().insertProverbs(it) } },
            SeedTask("idioms", 1, idiomPayload) { db -> batchInsert(idioms) { db.idiomDao().insertIdioms(it) } },
            SeedTask("phrases", 1, phrasePayload) { db -> batchInsert(phrases) { db.phraseDao().insertPhrases(it) } },
            SeedTask("vocabulary", 1, vocabularyPayload) { db -> batchInsert(vocabulary) { db.vocabularyDao().insertWords(it) } },
            SeedTask("leaderboard", 1, leaderboardPayload) { db -> batchInsert(leaderboard) { db.userDao().insertLeaderboardEntries(it) } },
            SeedTask("user", 1, userPayload) { db ->
                db.userDao().insertUserProfile(user.profile)
                db.userDao().insertUserStats(user.stats)
            },
            SeedTask("achievements", 1, achievementPayload) { db -> batchInsert(achievements) { db.userDao().insertAchievements(it) } },
            SeedTask("missions", 1, missionPayload) { db ->
                val existing = db.missionDao().getMissionsForDateSync(missions.date)
                if (existing.isEmpty()) {
                    batchInsert(missions.items) { db.missionDao().insertMissions(it) }
                }
            }
        )
    }

    private suspend fun <T> batchInsert(data: List<T>, inserter: suspend (List<T>) -> Unit) {
        data.chunked(BATCH_SIZE).forEach { inserter(it) }
    }

    private fun loadPayload(context: Context, path: String): String =
        context.assets.open(path).bufferedReader().use { it.readText() }

    internal fun parseQuotes(payload: String): List<QuoteEntity> = parseItems(payload, "quotes") { item ->
        QuoteEntity(
            content = requiredString(item, "content"),
            author = requiredString(item, "author"),
            category = requiredString(item, "category"),
            tags = requiredString(item, "tags"),
            reflectionPrompt = requiredString(item, "reflectionPrompt")
        )
    }

    internal fun parseProverbs(payload: String): List<ProverbEntity> = parseItems(payload, "proverbs") { item ->
        ProverbEntity(
            content = requiredString(item, "content"),
            meaning = requiredString(item, "meaning"),
            origin = requiredString(item, "origin"),
            usage = requiredString(item, "usage"),
            category = requiredString(item, "category")
        )
    }

    internal fun parseIdioms(payload: String): List<IdiomEntity> = parseItems(payload, "idioms") { item ->
        IdiomEntity(
            phrase = requiredString(item, "phrase"),
            meaning = requiredString(item, "meaning"),
            origin = requiredString(item, "origin"),
            exampleSentence = requiredString(item, "exampleSentence"),
            category = requiredString(item, "category")
        )
    }

    internal fun parsePhrases(payload: String): List<PhraseEntity> = parseItems(payload, "phrases") { item ->
        PhraseEntity(
            phrase = requiredString(item, "phrase"),
            meaning = requiredString(item, "meaning"),
            usage = requiredString(item, "usage"),
            exampleSentence = requiredString(item, "exampleSentence"),
            formality = requiredString(item, "formality"),
            category = requiredString(item, "category")
        )
    }

    internal fun parseVocabulary(payload: String): List<VocabularyEntity> = parseItems(payload, "vocabulary") { item ->
        VocabularyEntity(
            word = requiredString(item, "word"),
            definition = requiredString(item, "definition"),
            pronunciation = requiredString(item, "pronunciation"),
            partOfSpeech = requiredString(item, "partOfSpeech"),
            exampleSentence = requiredString(item, "exampleSentence"),
            synonyms = requiredString(item, "synonyms"),
            antonyms = requiredString(item, "antonyms"),
            difficulty = requiredInt(item, "difficulty"),
            category = requiredString(item, "category")
        )
    }

    internal fun parseLeaderboard(payload: String): List<LeaderboardEntryEntity> = parseItems(payload, "leaderboard") { item ->
        LeaderboardEntryEntity(
            odId = requiredString(item, "odId"),
            displayName = requiredString(item, "displayName"),
            totalPoints = requiredInt(item, "totalPoints"),
            weeklyPoints = requiredInt(item, "weeklyPoints"),
            currentStreak = requiredInt(item, "currentStreak"),
            rank = requiredInt(item, "rank"),
            previousRank = requiredInt(item, "previousRank"),
            isCurrentUser = requiredBoolean(item, "isCurrentUser")
        )
    }

    internal fun parseAchievements(payload: String): List<AchievementEntity> = parseItems(payload, "achievements") { item ->
        AchievementEntity(
            id = requiredString(item, "id"),
            name = requiredString(item, "name"),
            description = requiredString(item, "description"),
            iconId = requiredString(item, "iconId"),
            category = requiredString(item, "category"),
            requirement = requiredInt(item, "requirement"),
            rewardType = requiredString(item, "rewardType"),
            rewardValue = requiredString(item, "rewardValue"),
            rarity = requiredString(item, "rarity"),
            celebrationMessage = requiredString(item, "celebrationMessage"),
            xpReward = requiredInt(item, "xpReward")
        )
    }

    internal fun parseUserSeed(payload: String): UserSeed {
        val root = parseRoot(payload, "user")
        val profile = root["profile"]?.jsonObject ?: error("Missing user.profile")
        val stats = root["stats"]?.jsonObject ?: error("Missing user.stats")

        return UserSeed(
            profile = UserProfileEntity(
                odUserId = requiredString(profile, "odUserId"),
                displayName = requiredString(profile, "displayName"),
                titleId = requiredString(profile, "titleId")
            ),
            stats = UserStatsEntity(userId = requiredString(stats, "userId"))
        )
    }

    internal fun parseMissions(payload: String): MissionSeed {
        val root = parseRoot(payload, "missions")
        val date = requiredLong(root, "date")
        val items = (root["items"] as? JsonArray ?: error("Missing missions.items array")).map { element ->
            val item = element.jsonObject
            DailyMissionEntity(
                missionId = requiredString(item, "missionId"),
                date = date,
                missionType = requiredString(item, "missionType"),
                title = requiredString(item, "title"),
                description = requiredString(item, "description"),
                targetValue = requiredInt(item, "targetValue"),
                rewardXp = requiredInt(item, "rewardXp"),
                rewardTokens = requiredInt(item, "rewardTokens")
            )
        }
        return MissionSeed(date, items)
    }

    internal fun validateIntegrity(
        quotes: List<QuoteEntity>,
        vocabulary: List<VocabularyEntity>,
        idioms: List<IdiomEntity>,
        achievements: List<AchievementEntity>,
        leaderboard: List<LeaderboardEntryEntity>,
        userSeed: UserSeed,
        missionSeed: MissionSeed
    ) {
        require(quotes.isNotEmpty()) { "quotes cannot be empty" }
        require(vocabulary.isNotEmpty()) { "vocabulary cannot be empty" }
        require(idioms.isNotEmpty()) { "idioms cannot be empty" }
        require(achievements.isNotEmpty()) { "achievements cannot be empty" }

        require(quotes.map { it.content }.toSet().size == quotes.size) { "duplicate quote content" }
        require(vocabulary.map { it.word.lowercase() }.toSet().size == vocabulary.size) { "duplicate vocabulary word" }
        require(idioms.map { it.phrase.lowercase() }.toSet().size == idioms.size) { "duplicate idiom phrase" }
        require(achievements.map { it.id }.toSet().size == achievements.size) { "duplicate achievement id" }

        require(leaderboard.count { it.isCurrentUser } == 1) { "leaderboard must contain exactly one current user" }
        require(leaderboard.first { it.isCurrentUser }.odId == userSeed.profile.odUserId) {
            "leaderboard current user must match user profile"
        }

        val allowedMissionTypes = setOf("reflect", "sharpen", "commit")
        require(missionSeed.items.map { it.missionId }.toSet().size == missionSeed.items.size) { "duplicate mission ids" }
        require(missionSeed.items.all { it.missionType in allowedMissionTypes }) { "invalid mission type" }
    }

    private fun parseRoot(payload: String, domain: String): JsonObject {
        val root = json.parseToJsonElement(payload).jsonObject
        require(requiredInt(root, "version") > 0) { "$domain version must be > 0" }
        return root
    }

    private fun <T> parseItems(payload: String, domain: String, parser: (JsonObject) -> T): List<T> {
        val root = parseRoot(payload, domain)
        val items = root["items"] as? JsonArray ?: error("Missing $domain.items array")
        return items.map { parser(it.jsonObject) }
    }

    private fun requiredString(obj: JsonObject, key: String): String =
        (obj[key] as? JsonPrimitive)?.contentOrNull?.takeIf { it.isNotBlank() }
            ?: error("Missing or blank '$key'")

    private fun requiredInt(obj: JsonObject, key: String): Int =
        (obj[key] as? JsonPrimitive)?.intOrNull ?: error("Missing int '$key'")

    private fun requiredLong(obj: JsonObject, key: String): Long =
        (obj[key] as? JsonPrimitive)?.contentOrNull?.toLongOrNull() ?: error("Missing long '$key'")

    private fun requiredBoolean(obj: JsonObject, key: String): Boolean =
        (obj[key] as? JsonPrimitive)?.contentOrNull?.toBooleanStrictOrNull() ?: error("Missing boolean '$key'")

    internal fun checksum(payload: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(payload.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }

    private data class SeedTask(
        val domain: String,
        val version: Int,
        val payload: String,
        val apply: suspend (ProdyDatabase) -> Unit
    )

    internal data class UserSeed(val profile: UserProfileEntity, val stats: UserStatsEntity)
    internal data class MissionSeed(val date: Long, val items: List<DailyMissionEntity>)
}
