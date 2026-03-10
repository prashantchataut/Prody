package com.prody.prashant.data.ai

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class AiFallbackOrchestratorTest {

    private val geminiService: GeminiService = mockk(relaxed = true)
    private val openRouterService: OpenRouterService = mockk(relaxed = true)

    @Test
    fun `falls back to openrouter when gemini times out`() = runTest {
        io.mockk.every { geminiService.isConfigured() } returns true
        coEvery { geminiService.generateContent(any()) } throws SocketTimeoutException("timeout")
        io.mockk.every { openRouterService.isConfigured() } returns true
        coEvery { openRouterService.generateResponse(any(), any(), any()) } returns Result.success("fallback response")

        val orchestrator = AiFallbackOrchestrator(geminiService, openRouterService)
        val result = orchestrator.generate("prompt")

        assertEquals("fallback response", result)
    }

    @Test
    fun `retries then returns null on auth failure and network loss`() = runTest {
        io.mockk.every { geminiService.isConfigured() } returns true
        coEvery { geminiService.generateContent(any()) } returns GeminiResult.Error(
            IllegalStateException("auth failure"),
            "auth failure"
        )
        io.mockk.every { openRouterService.isConfigured() } returns true
        coEvery { openRouterService.generateResponse(any(), any(), any()) } returns Result.failure(UnknownHostException("offline"))

        val orchestrator = AiFallbackOrchestrator(geminiService, openRouterService)
        val result = orchestrator.generate("prompt", maxRetries = 2)

        assertNull(result)
    }

    @Test
    fun `sanitizes generic ai language when all providers only return generic text`() = runTest {
        io.mockk.every { geminiService.isConfigured() } returns true
        coEvery { geminiService.generateContent(any()) } returns GeminiResult.Success("As an AI, reflect daily with intention.")
        io.mockk.every { openRouterService.isConfigured() } returns false

        val orchestrator = AiFallbackOrchestrator(geminiService, openRouterService)
        val result = orchestrator.generate("prompt", maxRetries = 1)

        assertEquals("reflect daily with intention.", result)
    }
}
