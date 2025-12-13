package com.prody.prashant.debug

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.pm.PackageInfoCompat
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Global Exception Handler for Prody
 * 
 * This handler catches all uncaught exceptions and redirects to a crash activity
 * that displays the full error information with options to copy or restart.
 */
class CrashHandler private constructor(
    private val applicationContext: Context
) : Thread.UncaughtExceptionHandler {

    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

    companion object {
        private const val TAG = "CrashHandler"
        const val EXTRA_CRASH_INFO = "crash_info"
        const val EXTRA_EXCEPTION_TYPE = "exception_type"
        const val EXTRA_EXCEPTION_MESSAGE = "exception_message"
        const val EXTRA_STACK_TRACE = "stack_trace"
        const val EXTRA_DEVICE_INFO = "device_info"
        const val EXTRA_TIMESTAMP = "timestamp"

        @Volatile
        private var instance: CrashHandler? = null

        fun initialize(context: Context) {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = CrashHandler(context.applicationContext)
                        Thread.setDefaultUncaughtExceptionHandler(instance)
                        Log.d(TAG, "CrashHandler initialized")
                    }
                }
            }
        }

        fun getInstance(): CrashHandler? = instance
    }

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        try {
            Log.e(TAG, "Uncaught exception in thread: ${thread.name}", throwable)
            
            // Collect crash information
            val crashInfo = buildCrashInfo(thread, throwable)
            
            // Launch crash activity
            launchCrashActivity(crashInfo)
            
            // Give some time for the crash activity to start
            Thread.sleep(500)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error handling crash", e)
        }
        
        // Kill the process
        android.os.Process.killProcess(android.os.Process.myPid())
        System.exit(1)
    }

    private fun buildCrashInfo(thread: Thread, throwable: Throwable): CrashInfo {
        val stackTraceWriter = StringWriter()
        throwable.printStackTrace(PrintWriter(stackTraceWriter))
        
        val rootCause = getRootCause(throwable)
        val rootCauseWriter = StringWriter()
        rootCause.printStackTrace(PrintWriter(rootCauseWriter))

        return CrashInfo(
            exceptionType = throwable::class.java.simpleName,
            exceptionMessage = throwable.message ?: "No message",
            fullStackTrace = stackTraceWriter.toString(),
            rootCauseType = rootCause::class.java.simpleName,
            rootCauseMessage = rootCause.message ?: "No message",
            rootCauseStackTrace = rootCauseWriter.toString(),
            threadName = thread.name,
            deviceInfo = buildDeviceInfo(),
            timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        )
    }

    private fun getRootCause(throwable: Throwable): Throwable {
        var cause: Throwable? = throwable
        while (cause?.cause != null && cause.cause != cause) {
            cause = cause.cause
        }
        return cause ?: throwable
    }

    private fun buildDeviceInfo(): String {
        return buildString {
            appendLine("═══ Device Information ═══")
            appendLine("App Version: ${getAppVersion()}")
            appendLine("Android Version: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
            appendLine("Device: ${Build.MANUFACTURER} ${Build.MODEL}")
            appendLine("Brand: ${Build.BRAND}")
            appendLine("Product: ${Build.PRODUCT}")
            appendLine("Hardware: ${Build.HARDWARE}")
            appendLine("Build ID: ${Build.ID}")
            appendLine("Build Type: ${Build.TYPE}")
            appendLine()
            appendLine("═══ Memory Information ═══")
            val runtime = Runtime.getRuntime()
            val maxMemory = runtime.maxMemory() / 1024 / 1024
            val totalMemory = runtime.totalMemory() / 1024 / 1024
            val freeMemory = runtime.freeMemory() / 1024 / 1024
            val usedMemory = totalMemory - freeMemory
            appendLine("Max Memory: ${maxMemory}MB")
            appendLine("Total Memory: ${totalMemory}MB")
            appendLine("Used Memory: ${usedMemory}MB")
            appendLine("Free Memory: ${freeMemory}MB")
        }
    }

    private fun getAppVersion(): String {
        return try {
            val packageInfo = applicationContext.packageManager
                .getPackageInfo(applicationContext.packageName, 0)
            val versionCode = PackageInfoCompat.getLongVersionCode(packageInfo)
            "${packageInfo.versionName} ($versionCode)"
        } catch (e: Exception) {
            "Unknown"
        }
    }

    private fun launchCrashActivity(crashInfo: CrashInfo) {
        val intent = Intent(applicationContext, CrashActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            putExtra(EXTRA_EXCEPTION_TYPE, crashInfo.exceptionType)
            putExtra(EXTRA_EXCEPTION_MESSAGE, crashInfo.exceptionMessage)
            putExtra(EXTRA_STACK_TRACE, crashInfo.fullStackTrace)
            putExtra(EXTRA_DEVICE_INFO, crashInfo.deviceInfo)
            putExtra(EXTRA_TIMESTAMP, crashInfo.timestamp)
            putExtra(EXTRA_CRASH_INFO, crashInfo.getFullReport())
        }
        applicationContext.startActivity(intent)
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
    val rootCauseStackTrace: String,
    val threadName: String,
    val deviceInfo: String,
    val timestamp: String
) {
    fun getFullReport(): String = buildString {
        appendLine("╔════════════════════════════════════════╗")
        appendLine("║           PRODY CRASH REPORT           ║")
        appendLine("╚════════════════════════════════════════╝")
        appendLine()
        appendLine("⏱ Timestamp: $timestamp")
        appendLine("🧵 Thread: $threadName")
        appendLine()
        appendLine("═══════════════════════════════════════")
        appendLine("🔴 EXCEPTION")
        appendLine("═══════════════════════════════════════")
        appendLine("Type: $exceptionType")
        appendLine("Message: $exceptionMessage")
        appendLine()
        appendLine("═══════════════════════════════════════")
        appendLine("🎯 ROOT CAUSE")
        appendLine("═══════════════════════════════════════")
        appendLine("Type: $rootCauseType")
        appendLine("Message: $rootCauseMessage")
        appendLine()
        appendLine("═══════════════════════════════════════")
        appendLine("📋 FULL STACK TRACE")
        appendLine("═══════════════════════════════════════")
        appendLine(fullStackTrace)
        appendLine()
        appendLine(deviceInfo)
    }
}
