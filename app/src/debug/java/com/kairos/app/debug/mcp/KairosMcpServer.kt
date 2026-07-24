package com.kairos.app.debug.mcp

import android.content.Context
import android.util.Log

/**
 * The diagnostic server instance - Disabled due to dependency issues.
 */
object KairosMcpServer {
    private const val TAG = "KairosMcpServer"

    fun start(context: Context) {
        Log.d(TAG, "MCP Diagnostic Server is disabled")
    }

    fun stop() {
        // No-op
    }
}
