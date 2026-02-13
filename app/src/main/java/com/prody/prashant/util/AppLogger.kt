package com.prody.prashant.util

import android.util.Log

/**
 * Centralized logging utility with optional redaction helpers for sensitive content.
 */
object AppLogger {
    fun v(tag: String, message: String) = Log.v(tag, message)

    fun d(tag: String, message: String) = Log.d(tag, message)

    fun i(tag: String, message: String) = Log.i(tag, message)

    fun w(tag: String, message: String) = Log.w(tag, message)

    fun w(tag: String, message: String, throwable: Throwable) = Log.w(tag, message, throwable)

    fun e(tag: String, message: String) = Log.e(tag, message)

    fun e(tag: String, message: String, throwable: Throwable) = Log.e(tag, message, throwable)

    fun wtf(tag: String, message: String) = Log.wtf(tag, message)

    fun wtf(tag: String, message: String, throwable: Throwable) = Log.wtf(tag, message, throwable)

    fun redactJournalText(text: String?, previewLength: Int = 48): String =
        redactSensitiveText("journal", text, previewLength)

    fun redactTherapyText(text: String?, previewLength: Int = 48): String =
        redactSensitiveText("therapy", text, previewLength)

    private fun redactSensitiveText(label: String, text: String?, previewLength: Int): String {
        if (text.isNullOrBlank()) return "$label=<empty>"

        val normalized = text.trim().replace(Regex("\\s+"), " ")
        val safeLength = previewLength.coerceAtLeast(0)
        val preview = normalized.take(safeLength)
        val suffix = if (normalized.length > safeLength) "…" else ""

        return "$label=<redacted len=${normalized.length} preview=\"$preview$suffix\">"
    }
}
