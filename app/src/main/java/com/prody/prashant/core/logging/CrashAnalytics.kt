package com.prody.prashant.core.logging

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

interface CrashAnalytics {
    fun record(
        errorCode: ErrorCode,
        throwable: Throwable,
        metadata: Map<String, Any?> = emptyMap()
    )
}

@Singleton
class LogcatCrashAnalytics @Inject constructor(
    private val piiRedactor: PiiRedactor
) : CrashAnalytics {

    override fun record(errorCode: ErrorCode, throwable: Throwable, metadata: Map<String, Any?>) {
        val sanitized = piiRedactor.redactMetadata(metadata)
        Log.e(
            "CrashAnalytics",
            "code=${errorCode.code}, type=${throwable::class.java.simpleName}, metadata=$sanitized",
            throwable
        )
    }
}
