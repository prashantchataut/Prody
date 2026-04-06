package com.prody.prashant.architecture

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class PackageDependencyRulesTest {

    private val sourceRoot = File("src/main/java/com/prody/prashant")

    @Test
    fun `domain layer does not depend on data or ui layers`() {
        val legacyViolations = listOf(
            "analytics/MoodAnalyticsEngine.kt",
            "collaborative/CollaborativeMessageScheduler.kt",
            "collaborative/CollaborativeModels.kt",
            "collaborative/MessageDeliveryService.kt",
            "deepdive/DeepDiveModels.kt",
            "deepdive/DeepDivePromptGenerator.kt",
            "deepdive/DeepDiveScheduler.kt",
            "games/VocabularyGame.kt",
            "gamification/ContextBloomService.kt",
            "gamification/GameSessionManager.kt",
            "gamification/GameSkillSystem.kt",
            "gamification/GamificationService.kt",
            "gamification/MissionSystem.kt",
            "gamification/RankSystem.kt",
            "gamification/WisdomQuestEngine.kt",
            "gamification/WisdomShield.kt",
            "haven/WitnessModeManager.kt",
            "intelligence/FirstWeekJourneyManager.kt",
            "intelligence/MemoryEngine.kt",
            "intelligence/NotificationIntelligence.kt",
            "intelligence/PatternAnalysisEngine.kt",
            "intelligence/UserContextEngine.kt",
            "learning/PathContentProvider.kt",
            "learning/PathRecommender.kt",
            "learning/SpacedRepetitionEngine.kt",
            "letter/MonthlyLetterGenerator.kt",
            "model/MonthlyLetter.kt",
            "progress/ActiveProgressService.kt",
            "progress/SeedBloomService.kt",
            "recommendation/ContentRecommendationEngine.kt",
            "repository/DailyRitualRepository.kt",
            "repository/DeepDiveRepository.kt",
            "repository/EvidenceRepository.kt",
            "repository/FutureMessageReplyRepository.kt",
            "repository/JournalRepository.kt",
            "repository/LearningPathRepository.kt",
            "repository/MicroEntryRepository.kt",
            "repository/VocabularyRepository.kt",
            "repository/WeeklyDigestRepository.kt",
            "repository/WisdomCollectionRepository.kt",
            "repository/YearlyWrappedRepository.kt",
            "ritual/EveningReflectionEngine.kt",
            "ritual/MorningIntentionEngine.kt",
            "social/CircleUpdateGenerator.kt",
            "social/SocialModels.kt",
            "social/SocialPrivacyManager.kt",
            "streak/DualStreakManager.kt",
            "summary/BuddhaWeeklyInsightGenerator.kt",
            "summary/WeeklySummaryEngine.kt",
            "summary/WeeklySummaryEngineImpl.kt",
            "summary/WeeklySummaryScheduler.kt",
            "vocabulary/VocabularyCelebrationService.kt",
            "vocabulary/VocabularyDetector.kt",
            "vocabulary/VocabularyDetectorImpl.kt",
            "vocabulary/VocabularySuggestionEngine.kt",
            "wellbeing/QuietModeDetector.kt",
            "wellbeing/QuietModeExtensions.kt",
            "wellbeing/QuietModeManager.kt",
            "wrapped/YearlyWrappedGenerator.kt"
        ).map { it.replace("/", File.separator) }

        val violations = kotlinFilesIn("domain")
            .filter { file ->
                legacyViolations.none { file.path.endsWith(it) }
            }
            .flatMap { file ->
                forbiddenImports(file, listOf("com.prody.prashant.data", "com.prody.prashant.ui"))
            }

        assertTrue("Domain layer has NEW forbidden imports:\n${violations.joinToString("\n")}", violations.isEmpty())
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
