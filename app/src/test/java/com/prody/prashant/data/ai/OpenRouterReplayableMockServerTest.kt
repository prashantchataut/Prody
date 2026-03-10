package com.prody.prashant.data.ai

import com.prody.prashant.data.ai.mock.ReplayableMockAiServer
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.mockwebserver.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Test

class OpenRouterReplayableMockServerTest {

    @Test
    fun `replays deterministic openrouter responses for integration flows`() {
        val server = ReplayableMockAiServer()
        try {
            server.start(
                mapOf(
                    "/api/v1/chat/completions" to listOf(
                        fixtureResponse("contracts/openrouter/chat-completions-2024-11/v1/success.json", 200),
                        fixtureResponse("contracts/openrouter/chat-completions-2024-11/v1/auth_failure.json", 401)
                    )
                )
            )

            val client = OkHttpClient()
            val payload = "{\"model\":\"${ProviderContractCatalog.OPENROUTER_MODEL}\",\"messages\":[]}".toRequestBody("application/json".toMediaType())

            val successRaw = client.newCall(
                Request.Builder().url(server.url("/api/v1/chat/completions")).post(payload).build()
            ).execute().body!!.string()
            val success = ProviderResponseParser.parseOpenRouterText(successRaw).getOrThrow()
            assertEquals("You showed up today. That matters.", success)

            val authRaw = client.newCall(
                Request.Builder().url(server.url("/api/v1/chat/completions")).post(payload).build()
            ).execute().body!!.string()
            val authFailure = ProviderResponseParser.parseOpenRouterText(authRaw)
            assertEquals(true, authFailure.isFailure)
        } finally {
            server.shutdown()
        }
    }

    private fun fixtureResponse(path: String, code: Int) =
        MockResponse().setResponseCode(code).setBody(fixture(path))

    private fun fixture(path: String): String =
        javaClass.classLoader!!.getResource(path)!!.readText()
}
