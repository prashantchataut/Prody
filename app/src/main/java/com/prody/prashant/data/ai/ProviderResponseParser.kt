package com.prody.prashant.data.ai

import java.io.IOException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

/**
 * Contract-pinned parsing helpers for provider JSON response bodies.
 */
object ProviderResponseParser {

    private val parser = Json { ignoreUnknownKeys = true }

    fun parseOpenRouterText(rawBody: String): Result<String> = runCatching {
        val root = parser.parseToJsonElement(rawBody).jsonObject
        val errorMessage = extractErrorMessage(root)
        if (errorMessage != null) throw IOException(errorMessage)

        val choices = root["choices"]?.jsonArray.orEmpty()
        val content = choices.firstNotNullOfOrNull { choice ->
            choice.jsonObject["message"]
                ?.jsonObject
                ?.get("content")
                ?.jsonPrimitive
                ?.contentOrNull
                ?.trim()
                ?.takeIf { it.isNotBlank() }
        }

        content ?: throw IOException("OpenRouter response missing non-empty choices[0].message.content")
    }

    fun parseGeminiText(rawBody: String): Result<String> = runCatching {
        val root = parser.parseToJsonElement(rawBody).jsonObject
        val errorMessage = extractErrorMessage(root)
        if (errorMessage != null) throw IOException(errorMessage)

        val candidates = root["candidates"]?.jsonArray.orEmpty()
        val text = candidates.firstNotNullOfOrNull { candidate ->
            candidate.jsonObject["content"]
                ?.jsonObject
                ?.get("parts")
                ?.jsonArray
                ?.firstNotNullOfOrNull { part ->
                    part.jsonObject["text"]
                        ?.jsonPrimitive
                        ?.contentOrNull
                        ?.trim()
                        ?.takeIf { it.isNotBlank() }
                }
        }

        text ?: throw IOException("Gemini response missing non-empty candidates[].content.parts[].text")
    }

    private fun extractErrorMessage(root: JsonObject): String? {
        val error = root["error"] as? JsonObject ?: return null
        val message = error["message"]?.jsonPrimitive?.contentOrNull
        val code = error["code"]?.jsonPrimitive?.contentOrNull
        return when {
            !message.isNullOrBlank() -> message
            !code.isNullOrBlank() -> "Provider error: $code"
            else -> "Provider returned an error response"
        }
    }

    private fun JsonElement?.orEmpty(): List<JsonElement> = this?.jsonArray ?: emptyList()
}
