package com.prody.prashant.core.logging

/**
 * Redacts PII-sensitive values before they reach logs or crash analytics.
 */
class PiiRedactor {

    private val sensitiveKeyTokens = setOf(
        "journal", "therapy", "user", "userid", "user_id", "message", "content", "text"
    )

    fun redactMessage(message: String): String {
        var sanitized = message
        sanitized = sanitized.replace(Regex("(?i)(userId|user_id)\\s*[:=]\\s*[^,\\s]+"), "$1=<redacted>")
        sanitized = sanitized.replace(Regex("(?i)(journal(text|_text)?|therapy(content)?|content|text)\\s*[:=]\\s*[^,]+"), "$1=<redacted>")
        return sanitized
    }

    fun redactMetadata(metadata: Map<String, Any?>): Map<String, Any?> {
        return metadata.mapValues { (key, value) ->
            if (sensitiveKeyTokens.any { token -> key.lowercase().contains(token) }) {
                "<redacted>"
            } else {
                value
            }
        }
    }
}
