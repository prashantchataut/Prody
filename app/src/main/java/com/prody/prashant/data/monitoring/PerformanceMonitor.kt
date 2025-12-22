package com.prody.prashant.data.monitoring

import android.content.Context
import android.os.Build
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Performance metric types
 */
enum class MetricType {
    JOURNAL_SAVE,
    JOURNAL_LOAD,
    AI_RESPONSE,
    SCREEN_LOAD,
    DATABASE_QUERY,
    NETWORK_REQUEST,
    ENCRYPTION,
    BACKUP_EXPORT,
    BACKUP_IMPORT
}

/**
 * Single performance measurement
 */
data class PerformanceMetric(
    val type: MetricType,
    val durationMs: Long,
    val timestamp: Long = System.currentTimeMillis(),
    val success: Boolean = true,
    val metadata: Map<String, String> = emptyMap()
)

/**
 * Aggregated metrics for a specific operation type
 */
data class AggregatedMetrics(
    val type: MetricType,
    val count: Int = 0,
    val totalDurationMs: Long = 0,
    val minDurationMs: Long = Long.MAX_VALUE,
    val maxDurationMs: Long = 0,
    val successRate: Float = 1f,
    val avgDurationMs: Long = 0
) {
    companion object {
        fun fromMetrics(type: MetricType, metrics: List<PerformanceMetric>): AggregatedMetrics {
            if (metrics.isEmpty()) return AggregatedMetrics(type)

            val successCount = metrics.count { it.success }
            val totalDuration = metrics.sumOf { it.durationMs }

            return AggregatedMetrics(
                type = type,
                count = metrics.size,
                totalDurationMs = totalDuration,
                minDurationMs = metrics.minOf { it.durationMs },
                maxDurationMs = metrics.maxOf { it.durationMs },
                successRate = successCount.toFloat() / metrics.size,
                avgDurationMs = totalDuration / metrics.size
            )
        }
    }
}

/**
 * Performance baseline targets
 */
object PerformanceBaselines {
    const val JOURNAL_SAVE_TARGET_MS = 300L
    const val AI_RESPONSE_TARGET_MS = 2000L
    const val SCREEN_LOAD_TARGET_MS = 500L
    const val DATABASE_QUERY_TARGET_MS = 100L
    const val ENCRYPTION_TARGET_MS = 50L
}

/**
 * Error tracking data
 */
data class ErrorReport(
    val id: String = java.util.UUID.randomUUID().toString(),
    val message: String,
    val stackTrace: String? = null,
    val context: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val deviceInfo: Map<String, String> = emptyMap()
)

/**
 * Overall performance report
 */
data class PerformanceReport(
    val timestamp: Long = System.currentTimeMillis(),
    val metrics: Map<MetricType, AggregatedMetrics> = emptyMap(),
    val errorCount: Int = 0,
    val baselineViolations: List<String> = emptyList()
)

/**
 * PerformanceMonitor tracks app performance metrics for optimization.
 *
 * Privacy-First Design:
 * - NO personal data collection
 * - NO user identification
 * - NO content analysis
 * - Only anonymous, aggregate timing data
 *
 * Usage:
 * ```
 * val tracker = performanceMonitor.startTracking(MetricType.JOURNAL_SAVE)
 * // ... perform operation ...
 * tracker.stop() // or tracker.fail(exception)
 * ```
 */
@Singleton
class PerformanceMonitor @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "PerformanceMonitor"
        private const val MAX_METRICS_PER_TYPE = 100
        private const val MAX_ERRORS = 50
    }

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    // Metrics storage - in-memory only, cleared on app restart
    private val metricsStore = ConcurrentHashMap<MetricType, MutableList<PerformanceMetric>>()
    private val errorStore = mutableListOf<ErrorReport>()

    private val _currentReport = MutableStateFlow(PerformanceReport())
    val currentReport: StateFlow<PerformanceReport> = _currentReport.asStateFlow()

    /**
     * Device info for error reports (anonymized)
     */
    private val deviceInfo: Map<String, String> by lazy {
        mapOf(
            "sdk_version" to Build.VERSION.SDK_INT.toString(),
            "device_model" to "${Build.MANUFACTURER} ${Build.MODEL}",
            "app_version" to getAppVersion()
        )
    }

    /**
     * Start tracking a performance metric.
     * Returns a tracker that must be stopped when the operation completes.
     */
    fun startTracking(type: MetricType, metadata: Map<String, String> = emptyMap()): PerformanceTracker {
        return PerformanceTracker(type, metadata, this::recordMetric)
    }

    /**
     * Track an operation with automatic timing
     */
    suspend fun <T> track(
        type: MetricType,
        metadata: Map<String, String> = emptyMap(),
        block: suspend () -> T
    ): T {
        val tracker = startTracking(type, metadata)
        return try {
            val result = block()
            tracker.stop()
            result
        } catch (e: Exception) {
            tracker.fail(e)
            throw e
        }
    }

    /**
     * Track a synchronous operation with automatic timing
     */
    fun <T> trackSync(
        type: MetricType,
        metadata: Map<String, String> = emptyMap(),
        block: () -> T
    ): T {
        val tracker = startTracking(type, metadata)
        return try {
            val result = block()
            tracker.stop()
            result
        } catch (e: Exception) {
            tracker.fail(e)
            throw e
        }
    }

    /**
     * Record a metric directly
     */
    internal fun recordMetric(metric: PerformanceMetric) {
        val metrics = metricsStore.getOrPut(metric.type) { mutableListOf() }

        synchronized(metrics) {
            metrics.add(metric)
            // Keep only recent metrics
            while (metrics.size > MAX_METRICS_PER_TYPE) {
                metrics.removeAt(0)
            }
        }

        checkBaseline(metric)
        updateReport()

        Log.d(TAG, "Recorded ${metric.type}: ${metric.durationMs}ms, success=${metric.success}")
    }

    /**
     * Record an error (without personal data)
     */
    fun recordError(
        message: String,
        exception: Throwable? = null,
        context: String? = null
    ) {
        val sanitizedMessage = sanitizeErrorMessage(message)
        val sanitizedStackTrace = exception?.let { sanitizeStackTrace(it) }

        val error = ErrorReport(
            message = sanitizedMessage,
            stackTrace = sanitizedStackTrace,
            context = context,
            deviceInfo = deviceInfo
        )

        synchronized(errorStore) {
            errorStore.add(error)
            while (errorStore.size > MAX_ERRORS) {
                errorStore.removeAt(0)
            }
        }

        updateReport()
        Log.e(TAG, "Error recorded: $sanitizedMessage")
    }

    /**
     * Get aggregated metrics for a specific type
     */
    fun getAggregatedMetrics(type: MetricType): AggregatedMetrics {
        val metrics = metricsStore[type] ?: return AggregatedMetrics(type)
        return AggregatedMetrics.fromMetrics(type, metrics.toList())
    }

    /**
     * Get all aggregated metrics
     */
    fun getAllAggregatedMetrics(): Map<MetricType, AggregatedMetrics> {
        return MetricType.entries.associateWith { getAggregatedMetrics(it) }
    }

    /**
     * Check if a metric violates baseline targets
     */
    private fun checkBaseline(metric: PerformanceMetric) {
        val target = when (metric.type) {
            MetricType.JOURNAL_SAVE -> PerformanceBaselines.JOURNAL_SAVE_TARGET_MS
            MetricType.AI_RESPONSE -> PerformanceBaselines.AI_RESPONSE_TARGET_MS
            MetricType.SCREEN_LOAD -> PerformanceBaselines.SCREEN_LOAD_TARGET_MS
            MetricType.DATABASE_QUERY -> PerformanceBaselines.DATABASE_QUERY_TARGET_MS
            MetricType.ENCRYPTION -> PerformanceBaselines.ENCRYPTION_TARGET_MS
            else -> null
        }

        if (target != null && metric.durationMs > target) {
            Log.w(TAG, "Baseline violation: ${metric.type} took ${metric.durationMs}ms (target: ${target}ms)")
        }
    }

    /**
     * Update the performance report
     */
    private fun updateReport() {
        scope.launch {
            val violations = mutableListOf<String>()
            val aggregated = getAllAggregatedMetrics()

            // Check for baseline violations
            aggregated.forEach { (type, metrics) ->
                val target = when (type) {
                    MetricType.JOURNAL_SAVE -> PerformanceBaselines.JOURNAL_SAVE_TARGET_MS
                    MetricType.AI_RESPONSE -> PerformanceBaselines.AI_RESPONSE_TARGET_MS
                    MetricType.SCREEN_LOAD -> PerformanceBaselines.SCREEN_LOAD_TARGET_MS
                    else -> null
                }
                if (target != null && metrics.avgDurationMs > target) {
                    violations.add("${type.name}: avg ${metrics.avgDurationMs}ms > target ${target}ms")
                }
            }

            _currentReport.value = PerformanceReport(
                metrics = aggregated,
                errorCount = errorStore.size,
                baselineViolations = violations
            )
        }
    }

    /**
     * Get recent errors for debugging
     */
    fun getRecentErrors(): List<ErrorReport> {
        return synchronized(errorStore) {
            errorStore.toList()
        }
    }

    /**
     * Clear all metrics and errors
     */
    fun clearAll() {
        metricsStore.clear()
        synchronized(errorStore) {
            errorStore.clear()
        }
        updateReport()
    }

    /**
     * Remove personal data from error messages
     */
    private fun sanitizeErrorMessage(message: String): String {
        // Remove potential email addresses
        var sanitized = message.replace(Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"), "[EMAIL]")
        // Remove potential phone numbers
        sanitized = sanitized.replace(Regex("\\+?\\d{10,15}"), "[PHONE]")
        // Remove potential file paths with usernames
        sanitized = sanitized.replace(Regex("/Users/[^/]+/"), "/Users/[USER]/")
        sanitized = sanitized.replace(Regex("/home/[^/]+/"), "/home/[USER]/")
        return sanitized
    }

    /**
     * Sanitize stack trace to remove personal data
     */
    private fun sanitizeStackTrace(throwable: Throwable): String {
        val stackTrace = throwable.stackTraceToString()
        return sanitizeErrorMessage(stackTrace)
    }

    private fun getAppVersion(): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "unknown"
        } catch (e: Exception) {
            "unknown"
        }
    }
}

/**
 * Helper class for tracking operation timing
 */
class PerformanceTracker(
    private val type: MetricType,
    private val metadata: Map<String, String>,
    private val onComplete: (PerformanceMetric) -> Unit
) {
    private val startTime = System.currentTimeMillis()
    private var completed = false

    /**
     * Stop tracking and record successful completion
     */
    fun stop(): Long {
        if (completed) return 0
        completed = true

        val duration = System.currentTimeMillis() - startTime
        onComplete(
            PerformanceMetric(
                type = type,
                durationMs = duration,
                success = true,
                metadata = metadata
            )
        )
        return duration
    }

    /**
     * Stop tracking and record failure
     */
    fun fail(exception: Throwable? = null): Long {
        if (completed) return 0
        completed = true

        val duration = System.currentTimeMillis() - startTime
        onComplete(
            PerformanceMetric(
                type = type,
                durationMs = duration,
                success = false,
                metadata = metadata + mapOf("error" to (exception?.message ?: "unknown"))
            )
        )
        return duration
    }
}
