package com.prody.prashant.data.moderation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Content moderation result
 */
enum class ModerationResult {
    /** Content is safe */
    SAFE,
    /** Content may need review */
    FLAGGED,
    /** Content should be blocked */
    BLOCKED
}

/**
 * Categories for flagged content
 */
enum class FlagCategory {
    /** Potentially harmful to self */
    SELF_HARM,
    /** Violence or threats */
    VIOLENCE,
    /** Explicit adult content */
    EXPLICIT,
    /** Spam or promotional content */
    SPAM,
    /** Other inappropriate content */
    OTHER
}

/**
 * Report for inappropriate content
 */
data class ContentReport(
    val id: String = java.util.UUID.randomUUID().toString(),
    val contentType: String, // "journal", "ai_response", "future_message"
    val contentId: Long,
    val category: FlagCategory,
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: ReportStatus = ReportStatus.PENDING
)

enum class ReportStatus {
    PENDING,
    REVIEWED,
    RESOLVED,
    DISMISSED
}

/**
 * Moderation check result with details
 */
data class ModerationCheck(
    val result: ModerationResult,
    val flaggedCategories: Set<FlagCategory> = emptySet(),
    val confidence: Float = 1f,
    val message: String? = null
)

/**
 * ContentModerationManager handles content filtering and reporting.
 *
 * Features:
 * - Basic content filtering for journal entries
 * - AI-generated content moderation
 * - Report inappropriate content feature
 * - Privacy-respecting local-first approach
 *
 * Privacy Notes:
 * - All moderation is done locally
 * - No content is sent to external servers
 * - Reports are stored locally until user explicitly shares
 */
@Singleton
class ContentModerationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "ContentModeration"
        private const val SUPPORT_EMAIL = "support@prody.app"
        private const val MAX_STORED_REPORTS = 10
    }

    // Local storage for reports (would be in Room for production)
    private val reports = mutableListOf<ContentReport>()

    private val _pendingReportsCount = MutableStateFlow(0)
    val pendingReportsCount: StateFlow<Int> = _pendingReportsCount.asStateFlow()

    // Keywords for basic content filtering (expanded for production)
    private val selfHarmKeywords = setOf(
        "hurt myself", "end it all", "kill myself", "suicide",
        "self harm", "cut myself", "don't want to live"
    )

    private val supportiveResponses = mapOf(
        FlagCategory.SELF_HARM to "If you're struggling, please reach out to a mental health professional or crisis helpline. You're not alone, and help is available."
    )

    /**
     * Check content for moderation issues.
     * This is a basic implementation - production would use ML models.
     */
    fun checkContent(content: String): ModerationCheck {
        val lowerContent = content.lowercase()
        val flaggedCategories = mutableSetOf<FlagCategory>()

        // Check for self-harm indicators (prioritize user safety)
        if (selfHarmKeywords.any { lowerContent.contains(it) }) {
            flaggedCategories.add(FlagCategory.SELF_HARM)
        }

        return when {
            flaggedCategories.contains(FlagCategory.SELF_HARM) -> {
                ModerationCheck(
                    result = ModerationResult.FLAGGED,
                    flaggedCategories = flaggedCategories,
                    message = supportiveResponses[FlagCategory.SELF_HARM]
                )
            }
            flaggedCategories.isNotEmpty() -> {
                ModerationCheck(
                    result = ModerationResult.FLAGGED,
                    flaggedCategories = flaggedCategories
                )
            }
            else -> {
                ModerationCheck(result = ModerationResult.SAFE)
            }
        }
    }

    /**
     * Check AI-generated content for appropriateness
     */
    fun checkAiContent(content: String): ModerationCheck {
        // For AI content, we're more strict
        val basicCheck = checkContent(content)

        // Additional checks for AI-specific issues
        if (content.isBlank() || content.length < 10) {
            return ModerationCheck(
                result = ModerationResult.FLAGGED,
                message = "AI response seems incomplete. Please try again."
            )
        }

        return basicCheck
    }

    /**
     * Create a report for inappropriate content
     */
    fun createReport(
        contentType: String,
        contentId: Long,
        category: FlagCategory,
        description: String = ""
    ): ContentReport {
        val report = ContentReport(
            contentType = contentType,
            contentId = contentId,
            category = category,
            description = description
        )

        synchronized(reports) {
            reports.add(report)
            // Keep only recent reports
            while (reports.size > MAX_STORED_REPORTS) {
                reports.removeAt(0)
            }
        }

        updatePendingCount()
        Log.d(TAG, "Report created: ${report.id}")
        return report
    }

    /**
     * Get all pending reports
     */
    fun getPendingReports(): List<ContentReport> {
        return synchronized(reports) {
            reports.filter { it.status == ReportStatus.PENDING }.toList()
        }
    }

    /**
     * Update report status
     */
    fun updateReportStatus(reportId: String, status: ReportStatus) {
        synchronized(reports) {
            reports.find { it.id == reportId }?.let { report ->
                val index = reports.indexOf(report)
                reports[index] = report.copy(status = status)
            }
        }
        updatePendingCount()
    }

    /**
     * Open email to send report to support
     * Note: This does NOT automatically send the report content.
     * User must explicitly compose and send the email.
     */
    fun openReportEmail(report: ContentReport) {
        val subject = "Content Report - ${report.category.name}"
        val body = """
            Report ID: ${report.id}
            Content Type: ${report.contentType}
            Category: ${report.category.name}
            Description: ${report.description}

            [Please add any additional context here]

            ---
            Note: No personal journal content is included in this report.
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(SUPPORT_EMAIL))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(intent)
            Log.d(TAG, "Opened email for report: ${report.id}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open email client", e)
        }
    }

    /**
     * Clear all reports (for data cleanup)
     */
    fun clearAllReports() {
        synchronized(reports) {
            reports.clear()
        }
        updatePendingCount()
    }

    /**
     * Delete a specific report
     */
    fun deleteReport(reportId: String) {
        synchronized(reports) {
            reports.removeAll { it.id == reportId }
        }
        updatePendingCount()
    }

    private fun updatePendingCount() {
        _pendingReportsCount.value = synchronized(reports) {
            reports.count { it.status == ReportStatus.PENDING }
        }
    }

    /**
     * Get supportive message for a flagged category
     */
    fun getSupportiveMessage(category: FlagCategory): String? {
        return supportiveResponses[category]
    }

    /**
     * Crisis resources for self-harm detection
     */
    fun getCrisisResources(): List<ModerationCrisisResource> {
        return listOf(
            ModerationCrisisResource(
                name = "National Suicide Prevention Lifeline",
                phone = "988",
                description = "24/7 support for people in distress"
            ),
            ModerationCrisisResource(
                name = "Crisis Text Line",
                phone = "Text HOME to 741741",
                description = "Free 24/7 text support"
            ),
            ModerationCrisisResource(
                name = "International Association for Suicide Prevention",
                website = "https://www.iasp.info/resources/Crisis_Centres/",
                description = "Find a crisis center in your country"
            )
        )
    }
}

/**
 * Crisis resource information for content moderation.
 *
 * Renamed from CrisisResource to avoid collision with
 * com.prody.prashant.domain.haven.CrisisResource.
 */
data class ModerationCrisisResource(
    val name: String,
    val phone: String? = null,
    val website: String? = null,
    val description: String
)
