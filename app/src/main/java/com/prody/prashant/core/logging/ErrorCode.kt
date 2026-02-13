package com.prody.prashant.core.logging

/**
 * Stable error codes for monitoring and analytics dashboards.
 */
enum class ErrorCode(val code: String) {
    DEEP_DIVE_NOTIFICATION_SCHEDULE_FAILED("DD-001"),
    DEEP_DIVE_NOTIFICATION_CANCEL_FAILED("DD-002"),
    CRASH_HANDLER_INITIALIZATION_FAILED("CR-001"),
    CRASH_HANDLER_ACTIVITY_LAUNCH_FAILED("CR-002"),
    SECURITY_EVENT("SEC-001"),
    UNKNOWN("GEN-000")
}
