package com.prody.prashant.data.ai

import android.util.Log

/**
 * Encapsulates provider fallback and retry behavior for deterministic testing.
 */
private const val DEFAULT_SYSTEM_PROMPT = "You are Buddha, a wise mentor in Prody. Never mention being an AI."

class AiFallbackOrchestrator(
    private val geminiService: GeminiService,
    private val openRouterService: OpenRouterService,
    private val logTag: String = "AiFallbackOrchestrator"
) {

    suspend fun generate(prompt: String, maxRetries: Int = 2): String? {
        var attempts = 0
        var lastResponse: String? = null

        while (attempts < maxRetries) {
            attempts++
            val enhancedPrompt = if (attempts > 1) buildRetryPrompt(prompt, attempts) else "$DEFAULT_SYSTEM_PROMPT\n\n$prompt"

            if (geminiService.isConfigured()) {
                try {
                    val result = geminiService.generateContent(enhancedPrompt)
                    if (result is GeminiResult.Success && result.data.isNotBlank()) {
                        lastResponse = result.data
                        if (!containsGenericAiLanguage(lastResponse)) return lastResponse
                    }
                } catch (e: Exception) {
                    Log.w(logTag, "Gemini failed on attempt $attempts", e)
                }
            }

            if (openRouterService.isConfigured()) {
                try {
                    val result = openRouterService.generateResponse(
                        prompt = enhancedPrompt,
                        systemPrompt = DEFAULT_SYSTEM_PROMPT
                    )
                    result.onSuccess { response ->
                        lastResponse = response
                        if (!containsGenericAiLanguage(response)) return response
                    }
                } catch (e: Exception) {
                    Log.w(logTag, "OpenRouter failed on attempt $attempts", e)
                }
            }
        }

        return lastResponse?.let(::sanitizeGenericAiLanguage)?.takeIf { it.isNotBlank() }
    }

    private fun buildRetryPrompt(originalPrompt: String, attemptNumber: Int): String {
        val reinforcement = when (attemptNumber) {
            2 -> "IMPORTANT REMINDER: You are Buddha. Never mention being an AI."
            else -> "CRITICAL: Stay in character as Buddha."
        }
        return "$DEFAULT_SYSTEM_PROMPT\n\n$reinforcement\n\n$originalPrompt"
    }

    private fun containsGenericAiLanguage(response: String): Boolean {
        val lower = response.lowercase()
        return listOf("as an ai", "as a language model", "as a chatbot", "i was trained on").any(lower::contains)
    }

    private fun sanitizeGenericAiLanguage(response: String): String {
        return response
            .replace(Regex("(?i)as an ai[^.]*\\.\\s*"), "")
            .replace(Regex("(?i)as a language model[^.]*\\.\\s*"), "")
            .replace(Regex("\\s+"), " ")
            .trim()
    }
}
