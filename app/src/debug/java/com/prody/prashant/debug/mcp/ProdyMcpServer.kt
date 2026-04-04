package com.prody.prashant.debug.mcp

import android.content.Context
import android.util.Log
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.routing.*
import io.ktor.server.sse.*
import io.modelcontextprotocol.kotlin.sdk.server.ktor.mcp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * The diagnostic server instance that runs locally on the device/emulator.
 */
object ProdyMcpServer {
    private const val TAG = "ProdyMcpServer"
    private const val PORT = 8080
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var engine: EmbeddedServer<CIOApplicationEngine, CIOApplicationEngine.Configuration>? = null

    fun start(context: Context) {
        if (engine != null) {
            return
        }

        val provider = ProdyMcpProvider(context)
        
        scope.launch {
            try {
                engine = embeddedServer(CIO, port = PORT) {
                    install(SSE)
                    routing {
                        mcp("/mcp", provider.server)
                    }
                }.start(wait = false)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start MCP server", e)
            }
        }
    }

    fun stop() {
        engine?.stop(1000, 2000)
        engine = null
    }
}
