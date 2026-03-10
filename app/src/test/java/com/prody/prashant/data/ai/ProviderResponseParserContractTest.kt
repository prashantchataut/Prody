package com.prody.prashant.data.ai

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProviderResponseParserContractTest {

    @Test
    fun `openrouter success contract parses content`() {
        assertEquals("openai/gpt-3.5-turbo", ProviderContractCatalog.OPENROUTER_MODEL)
        assertEquals("v1", ProviderContractCatalog.OPENROUTER_CONTRACT_VERSION)
        val raw = fixture("contracts/openrouter/chat-completions-2024-11/v1/success.json")
        val result = ProviderResponseParser.parseOpenRouterText(raw)
        assertTrue(result.isSuccess)
        assertEquals("You showed up today. That matters.", result.getOrNull())
    }

    @Test
    fun `openrouter malformed and partial contracts fail cleanly`() {
        val malformed = ProviderResponseParser.parseOpenRouterText(
            fixture("contracts/openrouter/chat-completions-2024-11/v1/malformed.json")
        )
        val partial = ProviderResponseParser.parseOpenRouterText(
            fixture("contracts/openrouter/chat-completions-2024-11/v1/empty_choices.json")
        )
        assertTrue(malformed.isFailure)
        assertTrue(partial.exceptionOrNull()?.message?.contains("missing", ignoreCase = true) == true)
    }

    @Test
    fun `gemini success and failure contracts are pinned`() {
        assertEquals("gemini-1.5-flash", ProviderContractCatalog.GEMINI_MODEL)
        assertEquals("v1", ProviderContractCatalog.GEMINI_CONTRACT_VERSION)

        val success = ProviderResponseParser.parseGeminiText(
            fixture("contracts/gemini/2024-10/v1/success.json")
        )
        val partial = ProviderResponseParser.parseGeminiText(
            fixture("contracts/gemini/2024-10/v1/partial_missing_parts.json")
        )
        val networkError = ProviderResponseParser.parseGeminiText(
            fixture("contracts/gemini/2024-10/v1/network_failure.json")
        )

        assertTrue(success.isSuccess)
        assertTrue(partial.isFailure)
        assertTrue(networkError.exceptionOrNull()?.message?.contains("Connection reset") == true)
    }

    private fun fixture(path: String): String =
        javaClass.classLoader!!.getResource(path)!!.readText()
}
