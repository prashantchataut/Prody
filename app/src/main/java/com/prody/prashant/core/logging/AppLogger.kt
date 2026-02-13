package com.prody.prashant.core.logging

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

interface AppLogger {
    fun d(tag: String, message: String, metadata: Map<String, Any?> = emptyMap())
    fun i(tag: String, message: String, metadata: Map<String, Any?> = emptyMap())
    fun w(tag: String, message: String, throwable: Throwable? = null, metadata: Map<String, Any?> = emptyMap())
    fun e(
        tag: String,
        message: String,
        throwable: Throwable? = null,
        errorCode: ErrorCode = ErrorCode.UNKNOWN,
        metadata: Map<String, Any?> = emptyMap(),
        reportToCrashAnalytics: Boolean = false
    )
}

@Singleton
class StructuredAppLogger @Inject constructor(
    private val piiRedactor: PiiRedactor,
    private val crashAnalytics: CrashAnalytics
) : AppLogger {

    override fun d(tag: String, message: String, metadata: Map<String, Any?>) {
        Log.d(tag, format(message, metadata))
    }

    override fun i(tag: String, message: String, metadata: Map<String, Any?>) {
        Log.i(tag, format(message, metadata))
    }

    override fun w(tag: String, message: String, throwable: Throwable?, metadata: Map<String, Any?>) {
        Log.w(tag, format(message, metadata), throwable)
    }

    override fun e(
        tag: String,
        message: String,
        throwable: Throwable?,
        errorCode: ErrorCode,
        metadata: Map<String, Any?>,
        reportToCrashAnalytics: Boolean
    ) {
        Log.e(tag, "${format(message, metadata)} errorCode=${errorCode.code}", throwable)
        if (reportToCrashAnalytics && throwable != null) {
            crashAnalytics.record(errorCode, throwable, metadata)
        }
    }

    private fun format(message: String, metadata: Map<String, Any?>): String {
        val redactedMessage = piiRedactor.redactMessage(message)
        val redactedMetadata = piiRedactor.redactMetadata(metadata)
        return "$redactedMessage metadata=$redactedMetadata"
    }
}
