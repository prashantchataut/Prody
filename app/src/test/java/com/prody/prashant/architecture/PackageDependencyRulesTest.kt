package com.prody.prashant.architecture

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class PackageDependencyRulesTest {

    private val sourceRoot = File("src/main/java/com/prody/prashant")

    @Test
    fun `domain layer does not depend on data or ui layers`() {
        val legacyViolations = listOf(
            "com/prody/prashant/domain/summary/WeeklySummaryEngineImpl.kt",
            "com/prody/prashant/domain/summary/WeeklySummaryScheduler.kt",
            "com/prody/prashant/domain/summary/BuddhaWeeklyInsightGenerator.kt",
            "com/prody/prashant/domain/summary/WeeklySummaryEngine.kt",
            "com/prody/prashant/domain/vocabulary/VocabularyDetector.kt",
            "com/prody/prashant/domain/vocabulary/VocabularySuggestionEngine.kt",
            "com/prody/prashant/domain/vocabulary/VocabularyDetectorImpl.kt",
            "com/prody/prashant/domain/vocabulary/VocabularyCelebrationService.kt",
            "com/prody/prashant/domain/learning/PathContentProvider.kt",
            "com/prody/prashant/domain/learning/PathRecommender.kt",
            "com/prody/prashant/domain/learning/SpacedRepetitionEngine.kt",
            "com/prody/prashant/domain/ritual/MorningIntentionEngine.kt",
            "com/prody/prashant/domain/ritual/EveningReflectionEngine.kt",
            "com/prody/prashant/domain/intelligence/UserContextEngine.kt",
            "com/prody/prashant/domain/intelligence/NotificationIntelligence.kt",
            "com/prody/prashant/domain/intelligence/MemoryEngine.kt",
            "com/prody/prashant/domain/intelligence/FirstWeekJourneyManager.kt",
            "com/prody/prashant/domain/wellbeing/QuietModeManager.kt",
            "com/prody/prashant/domain/wellbeing/QuietModeDetector.kt",
            "com/prody/prashant/domain/wellbeing/QuietModeExtensions.kt",
            "com/prody/prashant/domain/repository/DeepDiveRepository.kt",
            "com/prody/prashant/domain/repository/YearlyWrappedRepository.kt",
            "com/prody/prashant/domain/repository/VocabularyRepository.kt",
            "com/prody/prashant/domain/repository/EvidenceRepository.kt",
            "com/prody/prashant/domain/repository/LearningPathRepository.kt",
            "com/prody/prashant/domain/repository/JournalRepository.kt",
            "com/prody/prashant/domain/repository/WeeklyDigestRepository.kt",
            "com/prody/prashant/domain/repository/FutureMessageReplyRepository.kt",
            "com/prody/prashant/domain/repository/DailyRitualRepository.kt",
            "com/prody/prashant/domain/repository/WisdomCollectionRepository.kt",
            "com/prody/prashant/domain/repository/MicroEntryRepository.kt",
            "com/prody/prashant/domain/collaborative/CollaborativeMessageScheduler.kt",
            "com/prody/prashant/domain/collaborative/CollaborativeModels.kt",
            "com/prody/prashant/domain/collaborative/MessageDeliveryService.kt",
            "com/prody/prashant/domain/model/MonthlyLetter.kt",
            "com/prody/prashant/domain/gamification/RankSystem.kt",
            "com/prody/prashant/domain/social/SocialModels.kt",
            "com/prody/prashant/domain/social/CircleUpdateGenerator.kt",
            "com/prody/prashant/domain/social/SocialPrivacyManager.kt",
            "com/prody/prashant/domain/deepdive/DeepDiveModels.kt",
            "com/prody/prashant/domain/deepdive/DeepDiveScheduler.kt",
            "com/prody/prashant/domain/deepdive/DeepDivePromptGenerator.kt",
            "com/prody/prashant/domain/recommendation/ContentRecommendationEngine.kt",
            "com/prody/prashant/domain/analytics/MoodAnalyticsEngine.kt",
            "com/prody/prashant/domain/gamification/ContextBloomService.kt",
            "com/prody/prashant/domain/gamification/WisdomQuestEngine.kt",
            "com/prody/prashant/domain/gamification/WisdomShield.kt",
            "com/prody/prashant/domain/gamification/GameSessionManager.kt",
            "com/prody/prashant/domain/gamification/MissionSystem.kt",
            "com/prody/prashant/domain/gamification/GameSkillSystem.kt",
            "com/prody/prashant/domain/gamification/GamificationService.kt",
            "com/prody/prashant/domain/progress/SeedBloomService.kt",
            "com/prody/prashant/domain/progress/ActiveProgressService.kt",
            "com/prody/prashant/domain/letter/MonthlyLetterGenerator.kt",
            "com/prody/prashant/domain/wrapped/YearlyWrappedGenerator.kt",
            "com/prody/prashant/domain/haven/WitnessModeManager.kt",
            "com/prody/prashant/domain/games/VocabularyGame.kt",
            "com/prody/prashant/domain/streak/DualStreakManager.kt"
        )

        val violations = kotlinFilesIn("domain")
            .flatMap { file ->
                val normalizedPath = file.path.replace("\\", "/")
                val isLegacy = legacyViolations.any { normalizedPath.endsWith(it) }
                if (isLegacy) {
                    emptyList()
                } else {
                    val forbidden = forbiddenImports(file, listOf("com.prody.prashant.data", "com.prody.prashant.ui"))
                    if (forbidden.isNotEmpty()) {
                        println("Found violation in $normalizedPath")
                    }
                    forbidden
                }
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
