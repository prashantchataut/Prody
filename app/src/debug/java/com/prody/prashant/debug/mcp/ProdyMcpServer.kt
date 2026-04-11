package com.prody.prashant.debug.mcp

import android.content.Context
import android.util.Log

/**
 * The diagnostic server instance - Disabled due to dependency issues.
 */
object ProdyMcpServer {
    private const val TAG = "ProdyMcpServer"

    fun start(context: Context) {
        Log.d(TAG, "MCP Diagnostic Server is disabled")
    }

    fun stop() {
        // No-op
    }
}
