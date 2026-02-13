package com.prody.prashant.debug

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Global Exception Handler for Prody
 *
 * CRITICAL: This handler MUST be initialized as early as possible in attachBaseContext().
 * It catches ALL uncaught exceptions and redirects to CrashActivity.
 *
 * Key Design Principles:
 * - Zero dependencies on Hilt or any DI framework
 * - Minimal dependencies to prevent initialization failures
 * - Catches exceptions from ALL threads
 * - Handles TransactionTooLargeException by truncating data
 * - Runs CrashActivity in a separate process for isolation
 */
class CrashHandler private constructor(
    private val applicationContext: Context
) : Thread.UncaughtExceptionHandler {

    private val defaultHandler: Thread.UncaughtExceptionHandler? = Thread.getDefaultUncaughtExceptionHandler()

    companion object {
        private const val TAG = "CrashHandler"
        private const val CRASH_PROCESS_SUFFIX = ":crash"

        // Intent extras - kept short to minimize data size
        const val EXTRA_EXCEPTION_TYPE = "ex_type"
        const val EXTRA_EXCEPTION_MESSAGE = "ex_msg"
        const val EXTRA_STACK_TRACE = "stack"
        const val EXTRA_DEVICE_INFO = "device"
        const val EXTRA_TIMESTAMP = "time"
        const val EXTRA_FULL_REPORT = "report"
        const val EXTRA_THREAD_NAME = "thread"
        const val EXTRA_ROOT_CAUSE_TYPE = "root_type"
        const val EXTRA_ROOT_CAUSE_MSG = "root_msg"

        // Size limits to prevent TransactionTooLargeException
        private const val MAX_MESSAGE_LENGTH = 2000
        private const val MAX_STACK_TRACE_LENGTH = 50_000
        private const val MAX_REPORT_LENGTH = 100_000

        @Volatile
        private var instance: CrashHandler? = null

        @Volatile
        private var isInitialized = false

        /**
         * Initialize the crash handler. Can be called multiple times safely.
         * Should be called in attachBaseContext() for earliest possible initialization.
         *
         * CRITICAL: This method is designed to be called during attachBaseContext(),
         * which is the earliest possible point in the Application lifecycle. At this
         * stage, the Application object is not fully initialized, so we must be
         * extremely careful about what methods we call.
         */
        @JvmStatic
        fun initialize(context: Context) {
            if (isInitialized) {
                // Already initialized, no need to log constantly
                return
            }

            synchronized(this) {
                if (isInitialized) return

                try {
                    // CRITICAL FIX: During attachBaseContext(), calling context.applicationContext
                    // can be problematic on some devices/OS versions because the Application
                    // object is not fully initialized yet. Instead, we should:
                    // 1. If the context IS an Application, use it directly
                    // 2. Otherwise, try to get applicationContext, but fall back to the context itself
                    val safeContext: Context = when {
                        context is Application -> context
                        else -> try {
                            context.applicationContext ?: context
                        } catch (e: Exception) {
                            // If applicationContext throws, use the context directly
                            com.prody.prashant.util.AppLogger.w(TAG, "Failed to get applicationContext, using context directly", e)
                            context
                        }
                    }

                    val handler = CrashHandler(safeContext)
                    Thread.setDefaultUncaughtExceptionHandler(handler)
                    instance = handler
                    isInitialized = true
                    com.prody.prashant.util.AppLogger.i(TAG, "CrashHandler initialized successfully")
                } catch (e: Exception) {
                    com.prody.prashant.util.AppLogger.e(TAG, "Failed to initialize CrashHandler", e)
                }
            }
        }

        @JvmStatic
        fun isInitialized(): Boolean = isInitialized
        
        /**
         * Checks if the current process is the crash reporting process.
         *
         * This method is designed to be safe during early initialization phases
         * and will never throw or cause crashes. If process detection fails for
         * any reason, it conservatively returns false (assumes main process).
         */
        fun isCrashProcess(context: Context): Boolean {
            return try {
                // Method 1 (Android P+): Use Application.getProcessName() - most reliable
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val processName = try {
                        Application.getProcessName()
                    } catch (e: Exception) {
                        null
                    }
                    if (processName != null) {
                        return processName.endsWith(CRASH_PROCESS_SUFFIX)
                    }
                }

                // Method 2: Try to get process name from ActivityManager
                try {
                    val pid = android.os.Process.myPid()
                    val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as? android.app.ActivityManager
                    val processInfo = manager?.runningAppProcesses?.firstOrNull { it.pid == pid }
                    if (processInfo?.processName?.endsWith(CRASH_PROCESS_SUFFIX) == true) {
                        return true
                    }
                } catch (e: Exception) {
                    // ActivityManager access failed, continue to next method
                    com.prody.prashant.util.AppLogger.w(TAG, "Failed to get process name from ActivityManager", e)
                }

                // Method 3: Fallback - check /proc/self/cmdline
                try {
                    val cmdline = java.io.File("/proc/self/cmdline").readText()
                        .trim()
                        .replace("\u0000", "") // Remove null terminators
                    if (cmdline.endsWith(CRASH_PROCESS_SUFFIX)) {
                        return true
                    }
                } catch (e: Exception) {
                    // /proc access failed
                    com.prody.prashant.util.AppLogger.w(TAG, "Failed to read /proc/self/cmdline", e)
                }

                // Default: assume we're in main process
                false
            } catch (e: Exception) {
                // Outer catch-all - never let this method crash
                com.prody.prashant.util.AppLogger.e(TAG, "Unexpected error checking crash process", e)
                false
            }
        }
    }

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        // SAFETY CHECK: If we are already in the crash process, DO NOT launch CrashActivity again.
        // This prevents infinite crash loops if the CrashActivity itself fails.
        if (isCrashProcess(applicationContext)) {
             com.prody.prashant.util.AppLogger.e(TAG, "CRASH IN CRASH PROCESS! Creating infinite loop detected. Terminating.", throwable)
             // Log to system log so at least adb logcat catches it
             defaultHandler?.uncaughtException(thread, throwable)
             android.os.Process.killProcess(android.os.Process.myPid())
             System.exit(10)
             return
        }

        com.prody.prashant.util.AppLogger.e(TAG, "═══════════════════════════════════════════════════════════════")
        com.prody.prashant.util.AppLogger.e(TAG, "UNCAUGHT EXCEPTION in thread: ${thread.name}")
        com.prody.prashant.util.AppLogger.e(TAG, "═══════════════════════════════════════════════════════════════")
        com.prody.prashant.util.AppLogger.e(TAG, "Exception:", throwable)

        try {
            // Build crash information
            val crashInfo = buildCrashInfo(thread, throwable)

            // Launch crash activity in separate process
            launchCrashActivity(crashInfo)

            // Give time for crash activity to start
            try {
                Thread.sleep(800)
            } catch (e: InterruptedException) {
                // Ignore
            }

        } catch (e: Exception) {
            com.prody.prashant.util.AppLogger.e(TAG, "Error handling crash - falling back to default handler", e)
            // If we fail to handle the crash, let the default handler take over
            defaultHandler?.uncaughtException(thread, throwable)
        } finally {
            // Always kill the process to ensure clean state
            android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(1)
        }
    }

    private fun buildCrashInfo(thread: Thread, throwable: Throwable): CrashInfo {
        val stackTraceWriter = StringWriter()
        throwable.printStackTrace(PrintWriter(stackTraceWriter))

        val rootCause = getRootCause(throwable)

        return CrashInfo(
            exceptionType = throwable::class.java.simpleName,
            exceptionMessage = throwable.message ?: "No message available",
            fullStackTrace = stackTraceWriter.toString(),
            rootCauseType = rootCause::class.java.simpleName,
            rootCauseMessage = rootCause.message ?: "No message available",
            threadName = thread.name,
            deviceInfo = buildDeviceInfo(),
            timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US).format(Date())
        )
    }

private fun getRootCause(throwable: Throwable): Throwable {
        var cause: Throwable = throwable
        var depth = 0
        val maxDepth = 20 // Prevent infinite loops

        while (cause.cause != null && cause.cause != cause && depth < maxDepth) {
            cause = cause.cause ?: break // Safe null check with fallback
            depth++
        }
        return cause
    }

    private fun buildDeviceInfo(): String {
        return try {
            buildString {
                appendLine("═══ Device Information ═══")
                appendLine("App Version: ${getAppVersion()}")
                appendLine("Android: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
                appendLine("Device: ${Build.MANUFACTURER} ${Build.MODEL}")
                appendLine("Brand: ${Build.BRAND}")
                appendLine("Product: ${Build.PRODUCT}")
                appendLine("Hardware: ${Build.HARDWARE}")
                appendLine("Build ID: ${Build.ID}")
                appendLine()
                appendLine("═══ Memory Information ═══")
                val runtime = Runtime.getRuntime()
                val maxMem = runtime.maxMemory() / 1024 / 1024
                val totalMem = runtime.totalMemory() / 1024 / 1024
                val freeMem = runtime.freeMemory() / 1024 / 1024
                appendLine("Max: ${maxMem}MB | Total: ${totalMem}MB | Free: ${freeMem}MB")
            }
        } catch (e: Exception) {
            "Device info unavailable: ${e.message}"
        }
    }

    private fun getAppVersion(): String {
        return try {
            val packageInfo = applicationContext.packageManager
                .getPackageInfo(applicationContext.packageName, 0)
            val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode.toLong()
            }
            "${packageInfo.versionName} ($versionCode)"
        } catch (e: Exception) {
            "Unknown"
        }
    }

    private fun launchCrashActivity(crashInfo: CrashInfo) {
        try {
            val intent = Intent(applicationContext, CrashActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)

                // Truncate data to prevent TransactionTooLargeException
                putExtra(EXTRA_EXCEPTION_TYPE, crashInfo.exceptionType)
                putExtra(EXTRA_EXCEPTION_MESSAGE, crashInfo.exceptionMessage.take(MAX_MESSAGE_LENGTH))
                putExtra(EXTRA_STACK_TRACE, crashInfo.fullStackTrace.take(MAX_STACK_TRACE_LENGTH))
                putExtra(EXTRA_DEVICE_INFO, crashInfo.deviceInfo)
                putExtra(EXTRA_TIMESTAMP, crashInfo.timestamp)
                putExtra(EXTRA_THREAD_NAME, crashInfo.threadName)
                putExtra(EXTRA_ROOT_CAUSE_TYPE, crashInfo.rootCauseType)
                putExtra(EXTRA_ROOT_CAUSE_MSG, crashInfo.rootCauseMessage.take(MAX_MESSAGE_LENGTH))
                putExtra(EXTRA_FULL_REPORT, crashInfo.getFullReport().take(MAX_REPORT_LENGTH))
            }

            applicationContext.startActivity(intent)
            com.prody.prashant.util.AppLogger.i(TAG, "CrashActivity launched successfully")

        } catch (e: Exception) {
            com.prody.prashant.util.AppLogger.e(TAG, "Failed to launch CrashActivity", e)
        }
    }
}

/**
 * Data class containing all crash information
 */
data class CrashInfo(
    val exceptionType: String,
    val exceptionMessage: String,
    val fullStackTrace: String,
    val rootCauseType: String,
    val rootCauseMessage: String,
    val threadName: String,
    val deviceInfo: String,
    val timestamp: String
) {
    fun getFullReport(): String = buildString {
        appendLine("╔════════════════════════════════════════════════════════════╗")
        appendLine("║              PRODY CRASH REPORT                            ║")
        appendLine("╚════════════════════════════════════════════════════════════╝")
        appendLine()
        appendLine("Timestamp: $timestamp")
        appendLine("Thread: $threadName")
        appendLine()
        appendLine("════════════════════════════════════════════════════════════")
        appendLine("EXCEPTION")
        appendLine("════════════════════════════════════════════════════════════")
        appendLine("Type: $exceptionType")
        appendLine("Message: $exceptionMessage")
        appendLine()
        appendLine("════════════════════════════════════════════════════════════")
        appendLine("ROOT CAUSE")
        appendLine("════════════════════════════════════════════════════════════")
        appendLine("Type: $rootCauseType")
        appendLine("Message: $rootCauseMessage")
        appendLine()
        appendLine("════════════════════════════════════════════════════════════")
        appendLine("FULL STACK TRACE")
        appendLine("════════════════════════════════════════════════════════════")
        appendLine(fullStackTrace)
        appendLine()
        appendLine(deviceInfo)
    }
}
