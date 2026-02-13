package com.prody.prashant.domain.error

import com.prody.prashant.domain.common.ErrorType

enum class ErrorTaxonomy {
    RECOVERABLE,
    USER_ACTIONABLE,
    SECURITY_CRITICAL
}

enum class UiErrorHandling {
    INLINE_RETRY,
    ACTION_DIALOG,
    BLOCKING_SECURITY_SCREEN
}

data class ErrorHandlingPolicy(
    val taxonomy: ErrorTaxonomy,
    val uiHandling: UiErrorHandling,
    val shouldReportToCrashAnalytics: Boolean
)

fun ErrorType.toHandlingPolicy(): ErrorHandlingPolicy = when (this) {
    ErrorType.NETWORK,
    ErrorType.AI_SERVICE,
    ErrorType.TIMEOUT,
    ErrorType.RATE_LIMIT -> ErrorHandlingPolicy(
        taxonomy = ErrorTaxonomy.RECOVERABLE,
        uiHandling = UiErrorHandling.INLINE_RETRY,
        shouldReportToCrashAnalytics = false
    )

    ErrorType.VALIDATION,
    ErrorType.PERMISSION,
    ErrorType.NOT_FOUND,
    ErrorType.AUTHENTICATION,
    ErrorType.DATABASE -> ErrorHandlingPolicy(
        taxonomy = ErrorTaxonomy.USER_ACTIONABLE,
        uiHandling = UiErrorHandling.ACTION_DIALOG,
        shouldReportToCrashAnalytics = false
    )

    ErrorType.UNKNOWN -> ErrorHandlingPolicy(
        taxonomy = ErrorTaxonomy.SECURITY_CRITICAL,
        uiHandling = UiErrorHandling.BLOCKING_SECURITY_SCREEN,
        shouldReportToCrashAnalytics = true
    )
}
