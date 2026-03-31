package com.prody.prashant.debug.mcp

import android.content.Context
import android.util.Log
import com.prody.prashant.data.local.database.ProdyDatabase
import io.modelcontextprotocol.kotlin.sdk.Implementation
import io.modelcontextprotocol.kotlin.sdk.ServerCapabilities
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.ResourceCapabilities
import io.modelcontextprotocol.kotlin.sdk.ToolCapabilities
import io.modelcontextprotocol.kotlin.sdk.Resource
import io.modelcontextprotocol.kotlin.sdk.Tool
import io.modelcontextprotocol.kotlin.sdk.TextContent
import io.modelcontextprotocol.kotlin.sdk.EmbeddedResource
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * Provider for the Prody MCP Server.
 * Exposes app data and tools to the AI assistant for diagnostics and verification.
 */
class ProdyMcpProvider(private val context: Context) {

    private val database: ProdyDatabase by lazy {
        // Since this is debug only, we can access the database directly or via Hilt if available.
        // For simplicity in this standalone server, we'll assume the database is accessible.
        // In a real Hilt app, we would use an EntryPoint.
        com.prody.prashant.data.local.database.ProdyDatabase.getInstance(context)
    }

    val server = Server(
        serverInfo = Implementation(
            name = "Prody-Diagnostic-Server",
            version = "1.0.0"
        ),
        capabilities = ServerCapabilities(
            resources = ResourceCapabilities(subscribe = true),
            tools = ToolCapabilities()
        )
    ) {
        // List Resources
        listResources {
            listOf(
                Resource(
                    uri = "user://profile",
                    name = "User Profile",
                    description = "Core profile data including name, streak, and total points",
                    mimeType = "application/json"
                ),
                Resource(
                    uri = "journal://recent",
                    name = "Recent Journal Entries",
                    description = "The last 5 journal entries for pattern verification",
                    mimeType = "application/json"
                )
            )
        }

        // Read Resource
        readResource { request ->
            when (request.uri) {
                "user://profile" -> {
                    val profile = database.userDao().getUserProfileSync()
                    listOf(
                        TextContent(
                            text = Json.encodeToString(com.prody.prashant.data.local.entity.UserProfileEntity.serializer(), profile ?: throw Exception("Profile not found"))
                        )
                    )
                }
                "journal://recent" -> {
                    val entries = database.journalDao().getRecentEntriesSync(5)
                    listOf(
                        TextContent(
                            text = Json.encodeToString(kotlinx.serialization.builtins.ListSerializer(com.prody.prashant.data.local.entity.JournalEntryEntity.serializer()), entries)
                        )
                    )
                }
                else -> throw Exception("Unknown resource: ${request.uri}")
            }
        }

        // List Tools
        listTools {
            listOf(
                Tool(
                    name = "get_diagnostics",
                    description = "Get basic app health and build diagnostics",
                    inputSchema = JsonObject(emptyMap())
                )
            )
        }

        // Call Tool
        callTool { request ->
            when (request.name) {
                "get_diagnostics" -> {
                    val userCount = 1 // Simplified
                    val journalCount = database.journalDao().getEntryCount().firstOrNull() ?: 0
                    
                    val diagnostics = JsonObject(mapOf(
                        "version" to JsonPrimitive("1.0.0-RC"),
                        "database_health" to JsonPrimitive("OK"),
                        "total_journal_entries" to JsonPrimitive(journalCount),
                        "environment" to JsonPrimitive("Debug")
                    ))
                    
                    listOf(TextContent(text = diagnostics.toString()))
                }
                else -> throw Exception("Unknown tool: ${request.name}")
            }
        }
    }
}
