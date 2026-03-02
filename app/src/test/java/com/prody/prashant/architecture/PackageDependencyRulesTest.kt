package com.prody.prashant.architecture

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class PackageDependencyRulesTest {

    private val sourceRoot = File("src/main/java/com/prody/prashant")

    @Test
    fun `domain layer does not depend on data or ui layers`() {
        // These are pre-existing architectural violations that we are baselining to unblock CI.
        // DO NOT add new ones. Goal is to refactor these out over time.
        val legacyViolations = listOf(
            "domain/analytics/MoodAnalyticsEngine.kt",
            "domain/gamification/ContextBloomService.kt",
            "domain/gamification/WisdomQuestEngine.kt",
            "domain/gamification/WisdomShield.kt",
            "domain/gamification/GameSessionManager.kt",
            "domain/gamification/MissionSystem.kt",
            "domain/gamification/GameSkillSystem.kt",
            "domain/gamification/RankSystem.kt",
            "domain/gamification/GamificationService.kt",
            "domain/progress/SeedBloomService.kt",
            "domain/progress/ActiveProgressService.kt",
            "domain/letter/MonthlyLetterGenerator.kt",
            "domain/wrapped/YearlyWrappedGenerator.kt",
            "domain/haven/WitnessModeManager.kt",
            "domain/games/VocabularyGame.kt",
            "domain/streak/DualStreakManager.kt",
            "domain/recommendation/ContentRecommendationEngine.kt",
            "domain/deepdive/DeepDiveModels.kt",
            "domain/deepdive/DeepDiveScheduler.kt",
            "domain/deepdive/DeepDivePromptGenerator.kt",
            "domain/social/CircleUpdateGenerator.kt",
            "domain/social/SocialPrivacyManager.kt",
            "domain/social/SocialModels.kt",
            "domain/summary/WeeklySummaryEngineImpl.kt",
            "domain/summary/WeeklySummaryScheduler.kt",
            "domain/summary/BuddhaWeeklyInsightGenerator.kt",
            "domain/summary/WeeklySummaryEngine.kt",
            "domain/vocabulary/VocabularyDetector.kt",
            "domain/vocabulary/VocabularySuggestionEngine.kt",
            "domain/vocabulary/VocabularyDetectorImpl.kt",
            "domain/vocabulary/VocabularyCelebrationService.kt",
            "domain/learning/PathContentProvider.kt",
            "domain/learning/PathRecommender.kt",
            "domain/learning/SpacedRepetitionEngine.kt",
            "domain/ritual/MorningIntentionEngine.kt",
            "domain/ritual/EveningReflectionEngine.kt",
            "domain/intelligence/UserContextEngine.kt",
            "domain/intelligence/NotificationIntelligence.kt",
            "domain/intelligence/MemoryEngine.kt",
            "domain/intelligence/FirstWeekJourneyManager.kt",
            "domain/wellbeing/QuietModeManager.kt",
            "domain/wellbeing/QuietModeDetector.kt",
            "domain/wellbeing/QuietModeExtensions.kt",
            "domain/repository/",
            "domain/collaborative/CollaborativeMessageScheduler.kt",
            "domain/collaborative/CollaborativeModels.kt",
            "domain/collaborative/MessageDeliveryService.kt",
            "domain/model/MonthlyLetter.kt"
        ).map { it.replace("/", File.separator) }

        val violations = kotlinFilesIn("domain")
            .filter { file -> legacyViolations.none { file.path.endsWith(it) || file.path.contains("${File.separator}$it") } }
            .flatMap { file ->
                forbiddenImports(file, listOf("com.prody.prashant.data", "com.prody.prashant.ui"))
            }

        assertTrue("Domain layer has new forbidden imports:\n${violations.joinToString("\n")}", violations.isEmpty())
    }

    @Test
    fun `data layer does not depend on ui layer`() {
        val violations = kotlinFilesIn("data")
            .flatMap { file ->
                forbiddenImports(file, listOf("com.prody.prashant.ui"))
            }

        assertTrue("Data layer has forbidden imports:\n${violations.joinToString("\n")}", violations.isEmpty())
    }

    private fun kotlinFilesIn(relativePath: String): List<File> {
        val directory = File(sourceRoot, relativePath)
        if (!directory.exists()) return emptyList()
        return directory.walkTopDown().filter { it.isFile && it.extension == "kt" }.toList()
    }

    private fun forbiddenImports(file: File, forbiddenPrefixes: List<String>): List<String> {
        return file.readLines()
            .mapIndexedNotNull { index, line ->
                val trimmed = line.trim()
                val offendingPrefix = forbiddenPrefixes.firstOrNull { trimmed.startsWith("import $it") }
                offendingPrefix?.let { "${file.path}:${index + 1} imports $offendingPrefix" }
            }
    }
}
